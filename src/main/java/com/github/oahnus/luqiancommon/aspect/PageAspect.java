package com.github.oahnus.luqiancommon.aspect;

import com.github.oahnus.luqiancommon.annotations.Page;
import com.github.oahnus.luqiancommon.dto.PageableParams;
import com.github.oahnus.luqiancommon.util.ReflectUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by oahnus on 2019/9/25
 * 22:52.
 */
@Slf4j
@Component
@Aspect
public class PageAspect {

    @Pointcut("@annotation(com.github.oahnus.luqiancommon.annotations.Page)")
    public void pointCut() {}

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ReflectUtils.getMethod(pjp);

        Page page = method.getAnnotation(Page.class);
        PageableParams params = null;
        Object[] args = pjp.getArgs();

        boolean enablePageable = false;
        for (Object arg : args) {
            if (PageableParams.class.isAssignableFrom(arg.getClass())) {
                params = (PageableParams) arg;
                break;
            }
        }
        Class<?> returnType = method.getReturnType();
        if (returnType.equals(Object.class)) {
            enablePageable = true;
        }

        Object resultVal = null;
        if (enablePageable && params != null) {
            PageHelper.startPage(params.getPageNum(), params.getPageSize());
            resultVal = pjp.proceed();
            return new PageInfo<>((List)resultVal);
        } else {
            resultVal = pjp.proceed();
        }
        return resultVal;
    }
}
