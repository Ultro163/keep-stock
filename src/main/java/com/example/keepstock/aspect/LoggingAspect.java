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

    @Around("execution(* com.example.keepstock.service.*ServiceImpl.*(..))")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String operation = defineOperation(joinPoint.getSignature().getName());
        String serviceName = joinPoint.getTarget().getClass().getSimpleName();

        log.info("{} началось в сервисе {}. Аргументы: {}",
                operation, serviceName, joinPoint.getArgs());

        try {
            Object result = joinPoint.proceed();
            log.info("{} успешно завершено в сервисе {}. Результат: {}",
                    operation, serviceName, result);
            return result;
        } catch (Exception ex) {
            log.error("Ошибка при {} в сервисе {}. Исключение: {}",
                    operation, serviceName, ex.getMessage(), ex);
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