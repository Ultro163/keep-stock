package com.example.keepstock.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.example.keepstock.service.order.*.*(..))")
    public Object logOrderService(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, "OrderService");
    }

    @Around("execution(* com.example.keepstock.service.category.*.*(..))")
    public Object logCustomerService(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, "CategoryService");
    }

    @Around("execution(* com.example.keepstock.service.product.*.*(..))")
    public Object logProductService(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, "ProductService");
    }

    private Object logExecution(ProceedingJoinPoint joinPoint, String serviceName) throws Throwable {
        String operation = defineOperation(joinPoint.getSignature().getName());
        log.info("[{}] {} началось. Аргументы: {}", serviceName, operation, joinPoint.getArgs());

        try {
            Object result = joinPoint.proceed();
            log.info("[{}] {} успешно завершено. Результат: {}", serviceName, operation, result);
            return result;
        } catch (Exception ex) {
            log.error("[{}] Ошибка при {}. Исключение: {}", serviceName, operation, ex.getMessage(), ex);
            throw ex;
        }
    }


    private String defineOperation(String methodName) {
        if (methodName.startsWith("save")) return "Сохранение";
        if (methodName.startsWith("update")) return "Обновление";
        if (methodName.startsWith("delete")) return "Удаление";
        if (methodName.startsWith("get")) return "Получение данных";
        return "Вызов метода " + methodName;
    }
}