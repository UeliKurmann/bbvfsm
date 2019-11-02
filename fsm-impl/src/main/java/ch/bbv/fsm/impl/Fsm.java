package ch.bbv.fsm.impl;

import java.util.function.Consumer;

import ch.bbv.fsm.StateMachineFactory;

public class Fsm {
	
	public static <S extends Enum<?>, E extends Enum<?>> StateMachineFactory<SimpleStateMachine<S, E>, S, E> create(S initialState,
			Consumer<SimpleStateMachineDefinition<S, E>> definition) {
		SimpleStateMachineDefinition<S, E> result = new SimpleStateMachineDefinition<S, E>(initialState);
		definition.accept(result);
		return result;
	}

}
