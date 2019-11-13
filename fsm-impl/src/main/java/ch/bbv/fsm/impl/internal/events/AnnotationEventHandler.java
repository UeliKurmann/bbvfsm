package ch.bbv.fsm.impl.internal.events;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bbv.fsm.events.ContextEvent;
import ch.bbv.fsm.events.ExceptionEvent;
import ch.bbv.fsm.events.StateMachineEventHandler;
import ch.bbv.fsm.events.TransitionCompletedEvent;
import ch.bbv.fsm.events.TransitionEvent;
import ch.bbv.fsm.events.TransitionExceptionEvent;
import ch.bbv.fsm.events.annotation.OnExceptionThrown;
import ch.bbv.fsm.events.annotation.OnTransitionBegin;
import ch.bbv.fsm.events.annotation.OnTransitionCompleted;
import ch.bbv.fsm.events.annotation.OnTransitionDeclined;
import ch.bbv.fsm.events.annotation.OnTransitionThrowsException;
import ch.bbv.fsm.impl.SimpleStateMachineWithContext;

public class AnnotationEventHandler<S extends Enum<?>, E extends Enum<?>, O extends Object>
		implements StateMachineEventHandler<SimpleStateMachineWithContext<S, E, O>, S, E> {

	private static final Logger LOG = LoggerFactory.getLogger(AnnotationEventHandler.class);

	@Override
	public void onExceptionThrown(ExceptionEvent<SimpleStateMachineWithContext<S, E, O>, S, E> arg) {
		callMethod(OnExceptionThrown.class, arg);
	}

	@Override
	public void onTransitionBegin(TransitionEvent<SimpleStateMachineWithContext<S, E, O>, S, E> arg) {
		callMethod(OnTransitionBegin.class, arg);
	}

	@Override
	public void onTransitionCompleted(TransitionCompletedEvent<SimpleStateMachineWithContext<S, E, O>, S, E> arg) {
		callMethod(OnTransitionCompleted.class, arg);
	}

	@Override
	public void onTransitionDeclined(TransitionEvent<SimpleStateMachineWithContext<S, E, O>, S, E> arg) {
		callMethod(OnTransitionDeclined.class, arg);

	}

	@Override
	public void onTransitionThrowsException(TransitionExceptionEvent<SimpleStateMachineWithContext<S, E, O>, S, E> arg) {
		callMethod(OnTransitionThrowsException.class, arg);
	}

	private <A extends Annotation> void callMethod(Class<A> annotation, ContextEvent<SimpleStateMachineWithContext<S, E, O>, S, E> arg) {
		O o = arg.getSource().get();
		Annotations.find(annotation, o.getClass()).ifPresent(method -> {
			try {
				method.invoke(o, arg);
			} catch (Exception e) {
				LOG.error("Error executing " + annotation.getName(), e);
			}
		});
	}

}
