package worldgo.common.viewmodel.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import worldgo.common.viewmodel.aop.entity.AspectUtil;
import worldgo.common.viewmodel.aop.entity.StopWatch;

@Aspect
public class TraceAspect {

    /**
     * Create a log message.
     *
     * @param methodName     A string with the method name.
     * @param methodDuration Duration of the method in milliseconds.
     * @return A string representing message.
     */
    private static String buildLogMessage(String methodName, long methodDuration) {
        StringBuilder message = new StringBuilder();
        message.append(methodName);
        message.append("()");
        message.append(" take ");
        message.append("[");
        message.append(methodDuration);
        message.append("ms");
        message.append("]");

        return message.toString();
    }

    @Around("execution(!synthetic * *(..)) && onTraceMethod()")
    public Object doTraceMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        return traceMethod(joinPoint);
    }

    @Pointcut("@within(worldgo.common.viewmodel.aop.anno.Trace)||@annotation(worldgo.common.viewmodel.aop.anno.Trace)")
    public void onTraceMethod() {
    }

    private Object traceMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();
        AspectUtil.print(joinPoint, buildLogMessage(methodName, stopWatch.getTotalTimeMillis()));
        return result;
    }
}
