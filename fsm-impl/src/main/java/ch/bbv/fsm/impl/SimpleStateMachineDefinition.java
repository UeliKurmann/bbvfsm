package ch.bbv.fsm.impl;

import ch.bbv.fsm.StateMachine;

/**
 * A simple state machine's definition. This is used if no special type should be created.
 * 
 * @param <S>
 *            the type of the states
 * @param <E>
 *            the type of the events
 */
public class SimpleStateMachineDefinition<S extends Enum<?>, E extends Enum<?>> extends
		AbstractStateMachineDefinition<SimpleStateMachine<S, E>, S, E> {

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            the name of the definition
	 * @param initialState
	 *            the default inital state to use
	 */
	public SimpleStateMachineDefinition(final String name, final S initialState) {
		super(name, initialState);
	}

	@Override
	protected SimpleStateMachine<S, E> createStateMachine(final StateMachine<S, E> driver) {
		return new SimpleStateMachine<>(driver);
	}

}
