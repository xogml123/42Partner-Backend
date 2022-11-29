package partner42.moduleapi.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

/**
 * @Transactional annotation보다 먼저 실행되어야함.
 * Transactional annotation Default Order가 Integer.Max값임
 * 이를 설정에서 100으로 조정해주고 OptimisticLockAspect의 Order를 99로 설정해주면.
 * Transactional annotation바로 바깥 범위에서 실행될 수 있음.
 */
@Slf4j
@Order(99)
@Aspect
public class OptimisticLockAspect {

    @Around("partner42.moduleapi.aop.PointCut.allPublicArticleService()")
    public Object doOneMoreRetryTransactionIfOptimisticLockExceptionThrow(
        ProceedingJoinPoint joinPoint) throws Throwable {
        try{
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("OptimisticLockException 발생");
            return joinPoint.proceed();
        } finally {
            log.info("{}", joinPoint.getSignature());
        }
    }
}
