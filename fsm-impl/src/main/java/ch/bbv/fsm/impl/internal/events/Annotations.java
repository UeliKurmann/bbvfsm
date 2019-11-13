package ch.bbv.fsm.impl.internal.events;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ch.bbv.fsm.events.ExceptionEvent;
import ch.bbv.fsm.events.TransitionCompletedEvent;
import ch.bbv.fsm.events.TransitionEvent;
import ch.bbv.fsm.events.TransitionExceptionEvent;
import ch.bbv.fsm.events.annotation.OnExceptionThrown;
import ch.bbv.fsm.events.annotation.OnTransitionBegin;
import ch.bbv.fsm.events.annotation.OnTransitionCompleted;
import ch.bbv.fsm.events.annotation.OnTransitionDeclined;
import ch.bbv.fsm.events.annotation.OnTransitionThrowsException;

public class Annotations {
	
	private static Map<Class<? extends Annotation>, Class<?>> typeMapping = new HashMap<>();
	static {
		typeMapping.put(OnTransitionBegin.class, TransitionEvent.class);
		typeMapping.put(OnExceptionThrown.class, ExceptionEvent.class);
		typeMapping.put(OnTransitionCompleted.class, TransitionCompletedEvent.class);
		typeMapping.put(OnTransitionDeclined.class, TransitionEvent.class);
		typeMapping.put(OnTransitionThrowsException.class, TransitionExceptionEvent.class);
	}

	public static <A extends Annotation> Optional<Method> find(Class<A> annotation, Class<?> clazz) {
		for (Method method : clazz.getDeclaredMethods()) {
			A annotations = method.getAnnotation(annotation);
			if (annotations != null) {
				Class<?>[] types = method.getParameterTypes();
				if (types.length == 1 && types[0].isAssignableFrom(typeMapping.get(annotation))) {
					return Optional.of(method);
				}
			}
		}
		return Optional.empty();
	}

}
