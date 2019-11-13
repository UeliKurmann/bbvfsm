package ch.bbv.fsm.impl;

import ch.bbv.fsm.StateMachine;

/**
 * A simple state machine's definition. This is used if no special type should
 * be created.
 * 
 * @param <S> the type of the states
 * @param <E> the type of the events
 */
public class SimpleStateMachineDefinitionWithContext<S extends Enum<?>, E extends Enum<?>, C>
		extends AbstractStateMachineDefinition<SimpleStateMachineWithContext<S, E, C>, S, E> {

	/**
	 * Constructor.
	 * 
	 * @param initialState the default inital state to use
	 */
	public SimpleStateMachineDefinitionWithContext(final S initialState) {
		super(initialState);
	}

	@Override
	protected SimpleStateMachineWithContext<S, E, C> createStateMachine(final StateMachine<S, E> driver) {
		return new SimpleStateMachineWithContext<>(driver);
	}
}
