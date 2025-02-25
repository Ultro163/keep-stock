package com.example.keepstock.scheduling;

import com.example.keepstock.model.Product;
import com.example.keepstock.repository.ProductRepository;
import com.example.keepstock.util.annotations.MeasureExecutionTime;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@Profile("default")
@ConditionalOnExpression(value = "'${app.scheduling.mode}' == 'optimized'")
@RequiredArgsConstructor
public class OptimizedProductPriceScheduler {
    private final ProductRepository productRepository;

    private static final String LOG_FILE_PATH = "result_updated_prices.log";
    private static final String LOCK_QUERY = "LOCK TABLE products IN ACCESS EXCLUSIVE MODE";

    @PersistenceContext
    private EntityManager entityManager;
    @Value("${app.scheduling.priceIncreasePercentage}")
    private BigDecimal priceIncreasePercentage;
    @Value("${app.scheduling.batchSize:100000}")
    private int batchSize;
    @Value("${app.scheduling.exclusiveLock}")
    private boolean exclusiveLock;

    @MeasureExecutionTime
    @Transactional
    @Scheduled(fixedRateString = "${app.scheduling.period}")
    public void updateProductPrices() {
        log.info("🚀 Оптимизированный Scheduler начал работу.");
        int totalUpdated = 0;
        List<Product> products;
        Pageable pageable = (PageRequest.of(0, batchSize, Sort.by("id")));

        createLogFileIfNotExists();

        Session session = entityManager.unwrap(Session.class);
        session.setJdbcBatchSize(batchSize);
        if (exclusiveLock) {
            entityManager.createNativeQuery(LOCK_QUERY).executeUpdate();
            log.info("🔒 Таблица products заблокирована на время обновления.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            AtomicInteger rowNumber = new AtomicInteger(1);
            do {
                products = productRepository.findAllFromOptimized(pageable).getContent();

                if (!products.isEmpty()) {
                    products.forEach(product -> {
                        product.setPrice(calculatePrice(product.getPrice(), priceIncreasePercentage));
                        try {
                            String logEntry = String.format(
                                    "%d,%s,%s,%s,%s,%s,%.2f,%d,%s,%s%n",
                                    rowNumber.getAndIncrement(),
                                    product.getId(),
                                    product.getName(),
                                    product.getArticle(),
                                    product.getDescription(),
                                    product.getCategory().getId(),
                                    product.getPrice(),
                                    product.getQuantity(),
                                    product.getCreatedAt(),
                                    product.getLastQuantityUpdate()
                            );
                            writer.write(logEntry);
                        } catch (IOException e) {
                            throw new UncheckedIOException("Ошибка записи в лог обновленных цен", e);
                        }
                    });

                    entityManager.flush();
                    entityManager.clear();
                    totalUpdated += products.size();
                    log.info("✅ Обновлено и сохранено {} продуктов", totalUpdated);
                }

                pageable = pageable.next();
            } while (!products.isEmpty());
            log.info("Scheduler закончил работу. Цены обновлены на {}%! ", priceIncreasePercentage);
        } catch (IOException e) {
            log.error("Ошибка записи в файл лога обновленных цен: ", e);
        }
    }

    private void createLogFileIfNotExists() {
        try {
            Path path = Paths.get(LOG_FILE_PATH);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            log.error("Не удалось создать лог-файл: ", e);
        }
    }

    private BigDecimal calculatePrice(BigDecimal oldPrice, BigDecimal percentage) {
        BigDecimal multiplier = BigDecimal.ONE.add(percentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return oldPrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }
}