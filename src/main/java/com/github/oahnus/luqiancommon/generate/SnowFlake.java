package com.github.oahnus.luqiancommon.generate;

import java.util.stream.Stream;

/**
 * Created by oahnus on 2019/10/22
 * 15:12.
 */
public class SnowFlake {
    private static int SIGN_BITS = 1;
    private static int TIMESTAMP_BITS = 41;
    private static int DATA_CENTER_ID_BITS = 5;
    private static int WORKER_ID_BITS = 5;
    private static int SEQUENCE_BITS = 12;
    private static int TOTAL_BITS = SIGN_BITS + TIMESTAMP_BITS + DATA_CENTER_ID_BITS + WORKER_ID_BITS + SEQUENCE_BITS;

    private static long START_TIMESTAMP = 0;

    private static int TIMESTAMP_SHIFT_LEFT_BIT = DATA_CENTER_ID_BITS + WORKER_ID_BITS + SEQUENCE_BITS;
    private static int DATA_CENTER_SHIFT_LEFT_BIT = WORKER_ID_BITS + SEQUENCE_BITS;
    private static int WORKER_ID_SHIFT_LEFT_BIT = SEQUENCE_BITS;

    private static final int MAX_SEQUENCE = ~(-1 << SEQUENCE_BITS);

    private long sequence = 0;
    private long lastTimestamp = 0;

    private int dataCenterId;
    private int workerId;

    public SnowFlake(int dataCenterId, int workerId) {
        this(dataCenterId, workerId, 0);
    }

    public SnowFlake(int dataCenterId, int workerId, long startTimeStamp) {
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
        START_TIMESTAMP = startTimeStamp;
    }

    public String format(long id) {
        long timestamp = id >>> TIMESTAMP_SHIFT_LEFT_BIT;
        long dataCenterId = (id << (SIGN_BITS + TIMESTAMP_BITS)) >>> (TOTAL_BITS - DATA_CENTER_ID_BITS);
        long workerId = (id << (SIGN_BITS + TIMESTAMP_BITS + DATA_CENTER_ID_BITS)) >>> (TOTAL_BITS - WORKER_ID_BITS);
        long sequence = (id << (TOTAL_BITS - SEQUENCE_BITS)) >>> (TOTAL_BITS - SEQUENCE_BITS);

        return String.format("{timestamp: %s, dataCenterId: %s, workerId: %s, sequence: %s}",
                timestamp, dataCenterId, workerId, sequence);
    }

    public synchronized long generateId() {
        long nowTimestamp = System.currentTimeMillis();
        if (nowTimestamp < lastTimestamp) {
            throw new RuntimeException("时钟被拨回, 无法生成id");
        }

        if (nowTimestamp != lastTimestamp) {
            sequence = 0;
            lastTimestamp = nowTimestamp;
        }

        sequence = (sequence + 1) & MAX_SEQUENCE;
        if (sequence == 0) {
            nowTimestamp = System.currentTimeMillis();
            while (nowTimestamp < lastTimestamp) {
                nowTimestamp = System.currentTimeMillis();
            }
            lastTimestamp = nowTimestamp;
        }

        return (nowTimestamp - START_TIMESTAMP) << (TIMESTAMP_SHIFT_LEFT_BIT) |
                dataCenterId << (DATA_CENTER_SHIFT_LEFT_BIT) |
                workerId << (WORKER_ID_SHIFT_LEFT_BIT) |
                sequence;
    }

    public static void main(String... args) {
        SnowFlake snowFlake = new SnowFlake(1, 2);
        Stream.iterate(0, n -> n + 1).limit(100000).parallel().forEach(n -> {
            long id = snowFlake.generateId();
            System.out.println(id + "\t" + snowFlake.format(id));
        });
    }
}
