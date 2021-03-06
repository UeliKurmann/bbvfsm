package ch.bbv.fsm.impl.internal.driver;

import java.util.List;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.events.StateMachineEventHandler;
import ch.bbv.fsm.impl.internal.statemachine.StateMachineInterpreter;
import ch.bbv.fsm.impl.internal.statemachine.state.StateDictionary;
import ch.bbv.fsm.memento.StateMachineMemento;

/**
 * Base implementation for all state machine drivers.
 * 
 * @param <SM> the type of state machine
 * @param <S>  the enumeration type of the states.
 * @param <E>  the enumeration type of the events.
 */
abstract class AbstractStateMachineDriver<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>>
		implements StateMachine<S, E> {

	private LiveCycle liveCycle = LiveCycle.Created;

	/**
	 * The internal state machine.
	 */
	private StateMachineInterpreter<SM, S, E> stateMachineInterpreter;

	AbstractStateMachineDriver() {
	}

	/**
	 * Initializes the state machine.
	 * 
	 * @param stateMachine the custom state machine
	 * @param name         the name of the state machine used in the logs.
	 * @param states       the states
	 */
	public void initialize(final SM stateMachine, final String name, final StateDictionary<SM, S, E> states, final S initialState,
			final List<StateMachineEventHandler<SM, S, E>> eventHandlers) {
		this.stateMachineInterpreter = new StateMachineInterpreter<>(stateMachine, name, states, initialState);
		eventHandlers.forEach(stateMachineInterpreter::addEventHandler);
	}

	@Override
	public LiveCycle getStatus() {
		return liveCycle;
	}

	@Override
	public void start() {
		if (LiveCycle.Created != getStatus()) {
			throw new IllegalStateException("Starting the statemachine is not allowed in this state. State is " + getStatus().name());
		}
		liveCycle = LiveCycle.Running;
		stateMachineInterpreter.initialize();
	}

	@Override
	public void terminate() {
		stateMachineInterpreter.terminate();
		liveCycle = LiveCycle.Terminated;
	}

	@Override
	public S getCurrentState() {
		return stateMachineInterpreter.getCurrentStateId();
	}

	/**
	 * Fires the event on the state machine.
	 * 
	 * @param e the event to be fired on the state machine.
	 */
	void fireEventOnStateMachine(final EventHolder<E> e) {
		stateMachineInterpreter.fire(e.getEventId(), e.getEventArguments());
	}

	@Override
	public void activate(final StateMachineMemento<S, E> stateMachineMemento) {
		stateMachineInterpreter.activate(stateMachineMemento);
		liveCycle = LiveCycle.Running;
	}

	@Override
	public void passivate(final StateMachineMemento<S, E> stateMachineMemento) {
		liveCycle = LiveCycle.Terminated;
		stateMachineInterpreter.passivate(stateMachineMemento);
	}

}
