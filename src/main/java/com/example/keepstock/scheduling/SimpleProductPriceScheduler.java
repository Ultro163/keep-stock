package com.example.keepstock.scheduling;

import com.example.keepstock.model.Product;
import com.example.keepstock.repository.ProductRepository;
import com.example.keepstock.util.annotations.MeasureExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Component
@Profile("default")
@ConditionalOnExpression(value = "'${app.scheduling.mode}' == 'simple'")
@RequiredArgsConstructor
public class SimpleProductPriceScheduler {
    private final ProductRepository productRepository;
    @Value("${app.scheduling.priceIncreasePercentage}")
    private BigDecimal priceIncreasePercentage;

    @MeasureExecutionTime
    @Transactional
    @Scheduled(fixedRateString = "${app.scheduling.period}")
    public void updateProductPrices() {
        log.info("Scheduler начал работу.");
        List<Product> products = productRepository.findAllFromScheduling();

        for (Product product : products) {
            BigDecimal newPrice = calculatePrice(product.getPrice(), priceIncreasePercentage);
            product.setPrice(newPrice);
        }

        productRepository.saveAll(products);

        log.info("Scheduler закончил работу. Цены обновлены на {}%! ", priceIncreasePercentage);
    }

    private BigDecimal calculatePrice(BigDecimal oldPrice, BigDecimal percentage) {
        BigDecimal multiplier = BigDecimal.ONE.add(percentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return oldPrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }
}