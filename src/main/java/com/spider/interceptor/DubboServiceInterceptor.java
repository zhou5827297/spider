package com.spider.interceptor;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * 拦截dubboservice的异常
 * 对于向监控中心推送数据的接口，失败继续往后执行，不影响业务功能
 */

@Aspect
@Component
public class DubboServiceInterceptor {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Pointcut("execution(* com.zhoukai.service.monitor..*(..))")
    private void anyMethod() {//定义一个切入点

    }

//    @Before("anyMethod()")
//    public void doAccessCheck(JoinPoint joinPoint) {
//        System.out.println("前置通知");
//    }
//
//    @AfterReturning("anyMethod()")
//    public void doAfter() {
//        System.out.println("后置通知");
//    }
//
//    @After("anyMethod()")
//    public void after() {
//        System.out.println("最终通知");
//    }
//
//    @AfterThrowing(throwing = "throwable", pointcut = "anyMethod()")
//    public void doAfterThrow(Throwable throwable) {
//        System.out.println(throwable.getMessage());
//        System.out.println("例外通知");
//    }

    @Around("anyMethod()")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        Object object = 1;
        try {
            object = pjp.proceed();
        } catch (Exception e) {
            LOG.error("execute dubbo exception, message detail:[{}],", e.getMessage());
        }
        return object;
    }
}
