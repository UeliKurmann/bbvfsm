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

/**
 * Event Handler described by annotations.
 * 
 * @author Ueli Kurmann
 *
 * @param <S> the state
 * @param <E> the events
 * @param <O> the object
 */
public class AnnotationEventHandler<S extends Enum<?>, E extends Enum<?>, O extends Object>
		implements StateMachineEventHandler<SimpleStateMachineWithContext<S, E, O>, S, E> {

	private static final Logger LOG = LoggerFactory.getLogger(AnnotationEventHandler.class);

	@Override
	public void onExceptionThrown(final ExceptionEvent<SimpleStateMachineWithContext<S, E, O>, S, E> arg) {
		callMethod(OnExceptionThrown.class, arg);
	}

	@Override
	public void onTransitionBegin(final TransitionEvent<SimpleStateMachineWithContext<S, E, O>, S, E> arg) {
		callMethod(OnTransitionBegin.class, arg);
	}

	@Override
	public void onTransitionCompleted(final TransitionCompletedEvent<SimpleStateMachineWithContext<S, E, O>, S, E> arg) {
		callMethod(OnTransitionCompleted.class, arg);
	}

	@Override
	public void onTransitionDeclined(final TransitionEvent<SimpleStateMachineWithContext<S, E, O>, S, E> arg) {
		callMethod(OnTransitionDeclined.class, arg);

	}

	@Override
	public void onTransitionThrowsException(final TransitionExceptionEvent<SimpleStateMachineWithContext<S, E, O>, S, E> arg) {
		callMethod(OnTransitionThrowsException.class, arg);
	}

	private <A extends Annotation> void callMethod(final Class<A> annotation,
			final ContextEvent<SimpleStateMachineWithContext<S, E, O>, S, E> arg) {
		final O o = arg.getSource().get();
		Annotations.find(annotation, o.getClass()).ifPresent(method -> {
			try {
				method.invoke(o, arg);
			} catch (final Exception e) {
				LOG.error("Error executing " + annotation.getName(), e);
			}
		});
	}

}
