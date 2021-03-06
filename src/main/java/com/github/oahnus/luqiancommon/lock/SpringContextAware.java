package com.github.oahnus.luqiancommon.lock;

import com.github.oahnus.luqiancommon.config.props.LuqianProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created by oahnus on 2019/9/30
 * 10:59.
 */
@Slf4j
@Component
@Order(3)
@ConditionalOnBean(LuqianProperties.class)
public class SpringContextAware implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LockSpiLoader.setApplicationContext(applicationContext);
    }
}
