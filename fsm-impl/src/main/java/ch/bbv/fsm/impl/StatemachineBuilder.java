package ch.bbv.fsm.impl;

import java.util.function.Consumer;

import ch.bbv.fsm.StateMachineFactory;
import ch.bbv.fsm.impl.internal.events.AnnotationEventHandler;

public final class StatemachineBuilder {

	private StatemachineBuilder() {
		// static helper class
	}

	/**
	 * Creates a {@link StateMachineFactory} with an internal
	 * {@link SimpleStateMachineDefinition} instance.
	 * 
	 * @param <S>
	 * @param <E>
	 * @param initialState the initial state for the state machine
	 * @param definition   a consumer that accepts a
	 *                     {@link SimpleStateMachineDefinition} to configure the
	 *                     state machine.
	 * @return the {@link StateMachineFactory}
	 */
	public static <S extends Enum<?>, E extends Enum<?>, C> StateMachineFactory<SimpleStateMachineWithContext<S, E, C>, S, E> createWithContext(
			S initialState, Consumer<SimpleStateMachineDefinitionWithContext<S, E, C>> definition) {
		SimpleStateMachineDefinitionWithContext<S, E, C> result = new SimpleStateMachineDefinitionWithContext<S, E, C>(initialState);
		definition.accept(result);
		result.addEventHandler(new AnnotationEventHandler<S, E, C>());
		return result;
	}

	public static <S extends Enum<?>, E extends Enum<?>> StateMachineFactory<SimpleStateMachine<S, E>, S, E> create(S initialState,
			Consumer<SimpleStateMachineDefinition<S, E>> definition) {
		SimpleStateMachineDefinition<S, E> result = new SimpleStateMachineDefinition<S, E>(initialState);
		definition.accept(result);
		return result;
	}

}
