package com.github.oahnus.luqiancommon.aspect;

import com.alibaba.fastjson.JSON;
import com.github.oahnus.luqiancommon.annotations.ApiLock;
import com.github.oahnus.luqiancommon.enums.ApiLockStrategy;
import com.github.oahnus.luqiancommon.util.ReflectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by oahnus on 2020-06-11
 */
@Aspect
@Component
public class ApiLockAspect {
    private Map<String, String> tokenMap = new ConcurrentHashMap<>();
    private ThreadLocal<AtomicInteger> threadLocal = new ThreadLocal<>();

    @Pointcut("@annotation(com.github.oahnus.luqiancommon.annotations.ApiLock)")
    public void pointCut() {}

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return pjp.proceed();
        }

        String reqToken = calSig(request, pjp);

        try {
            boolean acquired = acquire(reqToken);
            if (!acquired) {
                Method handleMethod = ReflectUtils.getMethod(pjp);
                ApiLock apiLock = handleMethod.getAnnotation(ApiLock.class);

                ApiLockStrategy strategy = apiLock.strategy();
                switch (strategy) {
                    case FORBIDDEN:
                        response("请求过于频繁");
                        return null;
                    case WAIT:
                        int maxWait = apiLock.maxWait();
                        int c = 0;
                        int max = maxWait / 100;
                        while (true) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                response("请求超时");
                                return null;
                            }

                            if (acquire(reqToken)) {
                                break;
                            }
                            c ++;
                            if (c >= max) {
                                response("请求超时");
                                return null;
                            }
                        }
                }
            }
            Object retVal = pjp.proceed();
            release(reqToken);
            return retVal;
        } catch (Exception e) {
            release(reqToken);
            throw e;
        }
    }

    private String calSig(HttpServletRequest request, ProceedingJoinPoint pjp) {
        String url = request.getRequestURI();
        String method = request.getMethod();
        String remoteHost = request.getRemoteHost();
        Object[] args = pjp.getArgs();
        Object[] serialArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
                serialArgs[i] = "";
                continue;
            }
            serialArgs[i] = arg;
        }
        String req = remoteHost + url + method + JSON.toJSONString(serialArgs);

        return hash(req);
    }

    private String hash(String str) {
        return str == null ? "NULL" : String.valueOf(str.hashCode());
    }

    public HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest();
    }

    private boolean acquire(String reqToken) {
        AtomicInteger count = threadLocal.get();
        if (count != null) {
            count.getAndIncrement();
            return true;
        }
        String s = tokenMap.putIfAbsent(reqToken, "");
        if (s == null) {
            threadLocal.set(new AtomicInteger());
            return true;
        }
        return false;
    }

    private void release(String reqToken) {
        AtomicInteger count = threadLocal.get();
        if (count != null) {
            System.out.println("release " + count.get());
        }
        if (count != null && count.get() > 0) {
            count.getAndDecrement();
        } else {
            threadLocal.remove();
            tokenMap.remove(reqToken);
        }
    }

    public void response(String msg) throws IOException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = attributes.getResponse();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        try {
            writer.println(msg);
        } catch (Exception e) {

        } finally {
            writer.close();
        }
    }
}
