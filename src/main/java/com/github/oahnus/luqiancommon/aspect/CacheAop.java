package com.github.oahnus.luqiancommon.aspect;

import com.github.oahnus.luqiancommon.annotations.Cache;
import com.github.oahnus.luqiancommon.annotations.CacheClear;
import com.github.oahnus.luqiancommon.annotations.CachePut;
import com.github.oahnus.luqiancommon.config.condition.RedissonCondition;
import com.github.oahnus.luqiancommon.exceptions.CacheException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Created by oahnus on 2019/9/21
 * 23:37.
 */
@Slf4j
@Component
@Aspect
@Conditional(RedissonCondition.class)
@Order(2)
public class CacheAop {
    private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
    private ExpressionParser parser = new SpelExpressionParser();

    @Autowired
    RedissonClient redissonClient;

    @Pointcut("@annotation(com.github.oahnus.luqiancommon.annotations.Cache)")
    public void cache() {}

    @Pointcut("@annotation(com.github.oahnus.luqiancommon.annotations.CacheClear)")
    public void clearCache() {}

    @Pointcut("@annotation(com.github.oahnus.luqiancommon.annotations.CachePut)")
    public void putCache() {}

    /**
     * 更新缓存
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("putCache()")
    public Object putCacheAround(final ProceedingJoinPoint pjp) throws Throwable {
        Object resultVal = pjp.proceed();

        Method method = getMethod(pjp);

        CachePut cachePut = method.getAnnotation(CachePut.class);

        String keyOrSpel = cachePut.key();
        long expire = cachePut.expire();
        TimeUnit unit = cachePut.unit();

        Object[] args = pjp.getArgs();
        String key = getSpelVal(keyOrSpel, method, pjp.getTarget(), args);
        if (StringUtils.isEmpty(key)) {
            return defaultKey(method, args);
        }

        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(resultVal, expire, unit);
        return resultVal;
    }

    /**
     * 清除缓存
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("clearCache()")
    public Object clearCacheAround(ProceedingJoinPoint pjp) throws Throwable {
        Object resultVal = pjp.proceed();
        Method method = getMethod(pjp);

        CacheClear cache = method.getAnnotation(CacheClear.class);

        String keyOrSPEL = cache.key();

        Object[] args = pjp.getArgs();
        String key = getSpelVal(keyOrSPEL, method, pjp.getTarget(), args);
        if (StringUtils.isEmpty(key)) {
            key = defaultKey(method, args);
        }

        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.delete();
        return resultVal;
    }

    /**
     * 创建缓存
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("cache()")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        Method method = getMethod(pjp);

        Cache cache = method.getAnnotation(Cache.class);

        String keyOrSPEL = cache.key();
        long expire = cache.expire();
        TimeUnit unit = cache.unit();

        Object[] args = pjp.getArgs();
        String key;
        // 如果包含# 作为SPEL表达式处理
        if (keyOrSPEL.contains("#")) {
            key = getSpelVal(keyOrSPEL, method, pjp.getTarget(), args);
        }
        // 普通字符串常量做key
        else {
            key = keyOrSPEL;
        }

        if (StringUtils.isEmpty(key)) {
            key = defaultKey(method, args);
        }

        RBucket<Object> bucket = redissonClient.getBucket(key);
        Object resultVal;

        resultVal = bucket.get();
        if (resultVal == null) {
            resultVal = pjp.proceed();
            bucket.set(resultVal);
            bucket.expire(expire, unit);
        }
        return resultVal;
    }

    private Method getMethod(final ProceedingJoinPoint pjp) {
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

    /**
     * 解析SPEL表达式
     * @param spel 表达式字符串
     * @param method 被调用方法
     * @param target 被调用方法所在对象
     * @param methodArgs 被调用方法参数
     * @return
     */
    private String getSpelVal(String spel, Method method, Object target, Object[] methodArgs) {
        EvaluationContext ec = new StandardEvaluationContext();
        // 填充SPEL解析上下文
        String[] parameterNames = discoverer.getParameterNames(method);
        if (parameterNames != null) {
            int idx = 0;
            for (String name : parameterNames) {
                ec.setVariable(name, methodArgs[idx++]);
            }
        }
        ec.setVariable("method", method);
        ec.setVariable("args", methodArgs);
        ec.setVariable("methodName", method.getName());
        ec.setVariable("target", target);
        ec.setVariable("targetClass", target.getClass());

        try {
            Expression expression = parser.parseExpression(spel);

            Object value = expression.getValue(ec);
            if (value == null) return null;
            return value.toString();
        } catch (Exception e) {
            log.error("Cache Key SPEL Parse Fialed With Error: {}",e.getMessage());
            return defaultKey(method, methodArgs);
        }
    }

    /**
     * 默认cache key
     * @param method 被调用方法
     * @param args 被调用方法参数
     * @return
     */
    private String defaultKey(Method method, Object[] args) {
        StringBuilder key = new StringBuilder(method.getName());
        key.append("#");
        for (Object arg : args) {
            key.append(arg.toString());
        }
        return key.toString();
    }
}
