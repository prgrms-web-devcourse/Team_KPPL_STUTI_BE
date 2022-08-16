package prgrms.project.stuti.global.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class ExecutionTimer {

	@Pointcut("@annotation(prgrms.project.stuti.global.aop.ExecutionTimeLogger)")
	private void measureExecutionTime() {
	}

	@Around("measureExecutionTime()")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		Object response = joinPoint.proceed();
		Method method = getMethod(joinPoint);

		log.info("실행 메소드: {}, 총 실행시간: {}ms", method.getName(), getTotalMillis(start));

		return response;
	}

	private Method getMethod(ProceedingJoinPoint joinPoint) {
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();

		return methodSignature.getMethod();
	}

	private long getTotalMillis(long start) {
		return System.currentTimeMillis() - start;
	}
}
