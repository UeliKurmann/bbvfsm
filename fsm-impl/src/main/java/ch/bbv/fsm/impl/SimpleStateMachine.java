package ch.bbv.fsm.impl;

import ch.bbv.fsm.StateMachine;

/**
 * Simple state machine (used together with
 * {@link SimpleStateMachineDefinition}.
 * 
 * @param <S> the type of the states
 * @param <E> the type of the events
 */
public class SimpleStateMachine<S extends Enum<?>, E extends Enum<?>> extends AbstractStateMachine<SimpleStateMachine<S, E>, S, E> {

	protected SimpleStateMachine(final StateMachine<S, E> driver) {
		super(driver);
	}
}
