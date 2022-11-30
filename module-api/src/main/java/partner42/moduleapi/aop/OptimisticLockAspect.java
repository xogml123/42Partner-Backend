package partner42.moduleapi.aop;

import javax.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @Transactional annotation보다 먼저 실행되어야함.
 * Transactional annotation Default Order가 Integer.Max값임
 * 바로 앞에 실행되어야 하므로 Order를 Integer.Max -1로 지정
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@Aspect
public class OptimisticLockAspect {

    @Value("${retry.count}")
    public Integer retryMaxCount;
    @Value("${retry.sleep}")
    public Integer retryInterval;

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
        for (int retryCount = 0; retryCount <= retryMaxCount; retryCount++) {
            try {
                log.info("[RETRY_COUNT]: {}", retryCount);
                return joinPoint.proceed();
            } catch (OptimisticLockException e) {
                log.error("OptimisticLockException 발생");
                exceptionHolder = e;
                //RETRY_WAIT_TIME ms 쉬고 다시 시도
                //for loop에서 sleep busy waiting이 된다는 경고가 뜸 무슨의미일지 찾아보자.
                //interval을 주는 다른 방법이 있을지 찾아 봐야함.
                Thread.sleep(retryInterval);
            }
        }
        //3번 retry했음에도 실패하는 경우.
        throw exceptionHolder;
    }

}
