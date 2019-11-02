package ch.bbv.fsm.impl;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.impl.internal.statemachine.state.StateDictionary;
import ch.bbv.fsm.model.StateMachineModel;

/**
 * Implementation of the definition of the finite state machine.
 * 
 * @param <S>        the type of the states.
 * @param <E>        the type of the events.
 * @param <TStateMachine> the type of the state machine
 */
public class SimpleStateMachineModel<TStateMachine extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>>
		implements StateMachineModel<TStateMachine, S, E> {

	/**
	 * The dictionary of all states.
	 */
	private final StateDictionary<TStateMachine, S, E> states;

	private final S initialState;

	/**
	 * @param states       the states to use.
	 * @param initialState the initial InternalState.
	 */
	public SimpleStateMachineModel(final StateDictionary<TStateMachine, S, E> states, final S initialState) {

		this.states = new StateDictionary<>();
		this.initialState = initialState;
	}

	/**
	 * @return The dictionary of all states.
	 */
	protected StateDictionary<TStateMachine, S, E> getStates() {
		return states;
	}

	@Override
	public final S getInitialState() {
		return this.initialState;
	}

}
