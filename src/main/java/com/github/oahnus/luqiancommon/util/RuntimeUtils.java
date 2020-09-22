package com.github.oahnus.luqiancommon.util;

/**
 * Created by oahnus on 2020-04-12
 * 10:32.
 */
public class RuntimeUtils {
    // 默认线程池数量
    public static final int DEFAULT_POOL_SIZE = 8;
    // IO密集型默认阻塞系数
    public static final float IO_INTENSIVE_DEFAULT_BLOCK_FACTOR = 0.9f;

    /**
     * 获取cpu核心数
     * @return 核心数
     */
    public static int cpuProcessorSize() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * cpu密集型应用推荐线程池大小
     * @return 线程池size
     */
    public static int cpuIntensiveBestPoolSize() {
        return cpuProcessorSize() + 1;
    }

    /**
     * io密集型应用推荐线程池大小
     * @param blockFactor 阻塞系数 [0, 1)  推荐0.8~0.9
     * @return 线程池size
     */
    public static int ioIntensiveBestPoolSize(float blockFactor) {
        int processors = cpuProcessorSize();
        if (blockFactor < 0 || blockFactor >= 1) {
            throw new RuntimeException("Block Factor Value Must Between 0 And 1");
        }
        return (int)(processors / (1 - blockFactor));
    }

    /**
     * io密集型应用推荐线程池大小
     * @return 线程池size
     */
    public static int ioIntensiveBestPoolSize() {
        return ioIntensiveBestPoolSize(IO_INTENSIVE_DEFAULT_BLOCK_FACTOR);
    }
}
