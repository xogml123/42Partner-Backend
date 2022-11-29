package partner42.moduleapi.aop;

import javax.persistence.OptimisticLockException;
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
    public static final Integer RETRY_MAX_COUNT = 3;
    public static final Integer RETRY_WAIT_TIME = 100;

    /**
     * RETRY_MAX_COUNT만큼 반복하여 OptimisticLockException이 발생 하면 retry
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("partner42.moduleapi.aop.PointCut.allPublicArticleService()")
    public Object doOneMoreRetryTransactionIfOptimisticLockExceptionThrow(
        ProceedingJoinPoint joinPoint) throws Throwable {
        Exception exceptionHolder = null;
        for (int retryCount = 0; retryCount <= RETRY_MAX_COUNT; retryCount++) {
            try {
                log.info("[RETRY_COUNT]: {}", retryCount);
                return joinPoint.proceed();
            } catch (OptimisticLockException e) {
                log.error("OptimisticLockException 발생");
                exceptionHolder = e;
                //RETRY_WAIT_TIME ms 쉬고 다시 시도
                Thread.sleep(RETRY_WAIT_TIME);
            }
        }
        //3번 retry했음에도 exception이 발생하면 exception을 throw
        throw exceptionHolder;
    }

}
