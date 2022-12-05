package partner42.modulebatch.utils;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkerWithCountDownLatch extends Thread {

    private CountDownLatch latch;
    private Runnable runnable;

    public WorkerWithCountDownLatch(String name, CountDownLatch latch, Runnable actualWork ) {
        this.latch = latch;
        setName(name);
        this.runnable = actualWork;
    }

    @Override
    public void run() {
        try {
            log.info("{} created, blocked by the latch...", getName());
            latch.await();
            log.info("{} starts at: {}", getName(), Instant.now());
            this.runnable.run();
        } catch (InterruptedException e) {
            // handle exception
        }
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }
}