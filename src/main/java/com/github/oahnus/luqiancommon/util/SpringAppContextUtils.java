package com.github.oahnus.luqiancommon.util;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by oahnus on 2019/9/27
 * 10:24.
 */
@Component
public class SpringAppContextUtils implements ApplicationContextAware {
    private static ApplicationContext context;

    public static String getBeanName(Class clazz) {
        String clazzName = clazz.getSimpleName();
        return Character.toLowerCase(clazzName.charAt(0)) + clazzName.substring(1);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> beanClazz) {
        return context.getBean(beanClazz);
    }

    public static ApplicationContext getContext() {
        return context;
    }
}
