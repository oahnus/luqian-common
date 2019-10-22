package com.github.oahnus.luqiancommon.generate;

import java.util.stream.Stream;

/**
 * Created by oahnus on 2019/10/22
 * 15:12.
 */
public class SnowFlake {
    private static final int DATA_CENTER_ID_BIT = 5;
    private static final int WORKER_ID_BIT = 5;
    private static final int SEQUENCE_BIT = 12;

    private static final int TIMESTAMP_SHIFT_LEFT_BIT = DATA_CENTER_ID_BIT + WORKER_ID_BIT + SEQUENCE_BIT;
    private static final int DATA_CENTER_SHIFT_LEFT_BIT = WORKER_ID_BIT + SEQUENCE_BIT;
    private static final int WORKER_ID_SHIFT_LEFT_BIT = SEQUENCE_BIT;

    private static final int MAX_SEQUENCE = ~(-1 << SEQUENCE_BIT);

    private long sequence = 0;
    private long lastTimestamp = 0;

    private int dataCenterId;
    private int workerId;

    public SnowFlake(int dataCenterId, int workerId) {
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    public synchronized long generateId() {
        long nowTimestamp = System.currentTimeMillis();
        if (nowTimestamp != lastTimestamp) {
            sequence = 0;
            lastTimestamp = nowTimestamp;
        }

        long seq = sequence++;
        if (MAX_SEQUENCE == seq) {
            seq = 0;
            nowTimestamp = System.currentTimeMillis();
            lastTimestamp = nowTimestamp;
        }

        return nowTimestamp << (TIMESTAMP_SHIFT_LEFT_BIT) |
                dataCenterId << (DATA_CENTER_SHIFT_LEFT_BIT) |
                workerId << (WORKER_ID_SHIFT_LEFT_BIT) |
                seq;
    }

    public static void main(String... args) {
        SnowFlake snowFlake = new SnowFlake(1, 2);
        Stream.iterate(0, n -> n + 1).limit(100000).forEach(n -> {
            long id = snowFlake.generateId();
            System.out.println(id);
        });
    }
}
