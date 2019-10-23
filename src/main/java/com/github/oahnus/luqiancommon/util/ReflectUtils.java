package com.github.oahnus.luqiancommon.util;

import com.github.oahnus.luqiancommon.exceptions.CacheException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Created by oahnus on 2019/10/18
 * 9:45.
 */
public class ReflectUtils {
    public static Method getMethod(final ProceedingJoinPoint pjp) {
        final MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();

        if (method.getDeclaringClass().isInterface()) {
            try {
                method = pjp.getTarget().getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                throw new CacheException(e.getMessage());
            }
        }
        return method;
    }
}
