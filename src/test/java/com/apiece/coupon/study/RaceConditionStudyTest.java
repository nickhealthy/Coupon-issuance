package com.apiece.coupon.study;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import static org.assertj.core.api.Assertions.*;

public class RaceConditionStudyTest {
    private static final int THREADS = 100;
    private static final int TASKS = 10_000;
    private static final int INITIAL_STOCK = 100;
    private static final int REQUESTS = 1_000;
    private static final long GAP_NANOS = 1_000_000; // 1ms

    private int count = 0;
    private int stock = INITIAL_STOCK;
    private final AtomicInteger issued = new AtomicInteger();
    private final AtomicInteger atomicCount = new AtomicInteger();

    private final Object lock = new Object();
    private final AtomicInteger atomicStock = new AtomicInteger(INITIAL_STOCK);

    // 문제 1: read-modify-write
    @Disabled
    @Test
    void givenSharedCounter_whenIncrementConcurrently_thenSomeUpdatesAreLost() throws InterruptedException {
        runConcurrently(TASKS, () -> count++);
        assertThat(count).isEqualTo(TASKS);
    }

    // 해결 1- 1: 원자 연산
    @Test
    void givenAtomicCounter_whenIncrementConcurrently_thenNoUpdateLost() throws InterruptedException {
        runConcurrently(TASKS, atomicCount::incrementAndGet);
        assertThat(atomicCount.get()).isEqualTo(TASKS);
    }

    // 문제2: check-then-act (v0의 isSoldOut() -> 발급과 같은 구조)
    @Disabled
    @Test
    void issueConcurrently() throws InterruptedException {
        runConcurrently(REQUESTS, () -> {
            if (stock > 0) { // check
                LockSupport.parkNanos(GAP_NANOS); // 확인과 발급 사이의 틈
                stock--; // act
                issued.incrementAndGet(); // 발급 수는 정확히 카운트
            }
        });
        assertThat(issued.get()).isLessThanOrEqualTo(INITIAL_STOCK);
    }

    // 해결 1: 상호 배제 — 확인~행동을 한 번에 한 스레드만 (≈ 비관적 락)
    @Test
    void givenLock_whenIssueConcurrently_thenNotOverIssued() throws InterruptedException {
        runConcurrently(REQUESTS, () -> {
            synchronized (lock) {
                if (stock > 0) {                         // check
                    LockSupport.parkNanos(GAP_NANOS);    // 틈이 있어도 다른 스레드는 밖에서 대기
                    stock--;                             // act
                    issued.incrementAndGet();
                }
            }
        });
        assertThat(issued.get()).isEqualTo(INITIAL_STOCK);
        assertThat(stock).isZero();
    }

    // 해결 2: 원자 연산 — 확인과 차감을 한 동작으로 (≈ 조건부 UPDATE, Redis Lua)
    @Test
    void givenAtomicCheckAndDecrement_whenIssueConcurrently_thenNotOverIssued() throws InterruptedException {
        runConcurrently(REQUESTS, () -> {
            int before = atomicStock.getAndUpdate(s -> s > 0 ? s - 1 : s); // 0보다 크면 1 차감, 아니면 그대로
            if (before > 0) {                            // 차감에 성공한 스레드만 발급
                LockSupport.parkNanos(GAP_NANOS);        // 이후 작업이 오래 걸려도 결과는 이미 확정됨
                issued.incrementAndGet();
            }
        });
        assertThat(issued.get()).isEqualTo(INITIAL_STOCK);
        assertThat(atomicStock.get()).isZero();
    }

    // 작업을 스레드 풀에서 동시에 실행하고 모두 끝날 때까지 대기
    private void runConcurrently(int times, Runnable task) throws InterruptedException {
        try (ExecutorService poll = Executors.newFixedThreadPool(THREADS)) {
            CountDownLatch done = new CountDownLatch(times);
            for (int i = 0; i < times; i++) {
                poll.submit(() -> {
                    task.run();
                    done.countDown();
                });
            }
            done.await();
            poll.shutdown();
        }
    }

}
