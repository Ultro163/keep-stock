package com.example.keepstock.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class TransactionExecutionTimeAspect {

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object measureTransactionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            log.error("Ошибка при выполнении транзакции в методе {}: {}", joinPoint.getSignature(), e.getMessage(), e);
            throw e;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                long endTime = System.currentTimeMillis() - start;
                long minutes = TimeUnit.MILLISECONDS.toMinutes(endTime);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(endTime) % 60;
                long millis = endTime % 1000;

                String formattedTime = String.format("%d мин %d сек %d мс", minutes, seconds, millis);
                log.info("Транзакция в методе {} завершена за {}", joinPoint.getSignature(), formattedTime);
            }
        });
        return result;
    }
}