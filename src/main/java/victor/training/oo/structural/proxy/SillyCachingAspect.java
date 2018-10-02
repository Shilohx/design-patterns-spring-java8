package victor.training.oo.structural.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jooq.lambda.Unchecked;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
//@Aspect
public class SillyCachingAspect {
	
	@Around("execution(* ExpensiveOps.*(..))")
	public Object logAround(ProceedingJoinPoint point) throws Throwable {
		log.debug("(intercepted)");
		return cache.computeIfAbsent(getCacheKey(point.getSignature().getName(), point.getArgs()),
				Unchecked.function(k -> point.proceed()));
	}
	
	private Map<List<?>, Object> cache = new HashMap<>(); 
	private List<Object> getCacheKey(String methodName, Object... args) {
		List<Object> list = new ArrayList<>();
		list.add(methodName);
		list.addAll(Arrays.asList(args));
		return list;
	}
}