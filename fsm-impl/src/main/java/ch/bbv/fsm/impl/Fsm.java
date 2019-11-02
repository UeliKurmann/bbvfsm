package ch.bbv.fsm.impl;

import java.util.function.Consumer;

import ch.bbv.fsm.StateMachineFactory;

public final class Fsm {
	
	private Fsm() {
		// static helper class
	}
	
	/**
	 * Creates a {@link StateMachineFactory} with an internal {@link SimpleStateMachineDefinition} instance. 
	 * @param <S>
	 * @param <E>
	 * @param initialState the initial state for the state machine
	 * @param definition a consumer that accepts a {@link SimpleStateMachineDefinition} to configure the state machine.
	 * @return the {@link StateMachineFactory}
	 */
	public static <S extends Enum<?>, E extends Enum<?>> StateMachineFactory<SimpleStateMachine<S, E>, S, E> create(S initialState,
			Consumer<SimpleStateMachineDefinition<S, E>> definition) {
		SimpleStateMachineDefinition<S, E> result = new SimpleStateMachineDefinition<S, E>(initialState);
		definition.accept(result);
		return result;
	}

}
