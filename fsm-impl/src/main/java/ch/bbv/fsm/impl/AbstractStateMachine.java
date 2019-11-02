package ch.bbv.fsm.impl;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.memento.StateMachineMemento;

/**
 * Base class for finite state machine implementations.
 * 
 * @param <S>   the type of the states.
 * @param <E>   the type of the events.
 * @param <SM> the type of state machine
 */
public abstract class AbstractStateMachine<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>>
		implements StateMachine<S, E> {

	private final StateMachine<S, E> driver;

	/**
	 * Create a state machine.
	 * 
	 * @param driver the executor of the state event machine.
	 */
	protected AbstractStateMachine(final StateMachine<S, E> driver) {
		this.driver = driver;
	}

	@Override
	public final void fire(final E eventId, final Object... eventArguments) {
		driver.fire(eventId, eventArguments);
	}

	@Override
	public final void firePriority(final E eventId, final Object... eventArguments) {
		driver.firePriority(eventId, eventArguments);
	}

	@Override
	public final int numberOfQueuedEvents() {
		return driver.numberOfQueuedEvents();
	}

	@Override
	public final void start() {
		driver.start();
	}

	@Override
	public final void terminate() {
		driver.terminate();
	}

	@Override
	public boolean isIdle() {
		return driver.isIdle();
	}

	@Override
	public final S getCurrentState() {
		return driver.getCurrentState();
	}

	@Override
	public LiveCycle getRunningState() {
		return driver.getRunningState();
	}

	@Override
	public void passivate(final StateMachineMemento<S, E> memento) {
		driver.passivate(memento);
	}

	@Override
	public void activate(final StateMachineMemento<S, E> memento) {
		driver.activate(memento);
	}
}