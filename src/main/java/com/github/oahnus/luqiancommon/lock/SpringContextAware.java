package com.github.oahnus.luqiancommon.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by oahnus on 2019/9/30
 * 10:59.
 */
@Slf4j
@Component
public class SpringContextAware implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LockSpiLoader.setApplicationContext(applicationContext);
    }
}
