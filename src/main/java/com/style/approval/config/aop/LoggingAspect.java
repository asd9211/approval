package com.style.approval.config.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Slf4j
@Component
public class LoggingAspect {

    @Pointcut("execution(public * com.style.approval.*.service..*(..))")
    private void serviceTarget() { }

    @Around("serviceTarget()")
    public Object serviceStartAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();
        String className = target.getClass().getName();

        StopWatch sw = new StopWatch();
        sw.start();
        Object result = joinPoint.proceed(); // 비즈니스 로직 (메인 로직)
        sw.stop();
        log.info("=================================================");
        log.info(">>>>>>>>> AOP LOGGING >>>>>>>>>>");
        log.info("소요시간: {} ms", sw.getLastTaskTimeMillis());
        log.info("Class  ===> {}", className);
        log.info("Method ===> {}", joinPoint.getSignature().getName() + "()  END!!!");
        loggingParameters(joinPoint);
        log.info(">>>>>>>>>> AOP LOGGING >>>>>>>>>>");
        log.info("=================================================");

        return result;
    }

    private void loggingParameters(final ProceedingJoinPoint joinPoint) {

        try {
            final String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
            final Object[] arguments = joinPoint.getArgs();
            for (int i = 0; i < parameterNames.length; i++) {
                String paramName = parameterNames[i];
                String paramValue = arguments[i].toString();
                String param = paramName + " : " + paramValue;
                log.info("Parameter ===> {}", param);
            }
        } catch (Exception e) {
            log.error("Aspect error!!! : {} ", e.getMessage());
        }
    }
}
