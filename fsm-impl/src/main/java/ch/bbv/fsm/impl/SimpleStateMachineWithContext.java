package ch.bbv.fsm.impl;

import ch.bbv.fsm.StateMachine;

/**
 * Simple state machine (used together with
 * {@link SimpleStateMachineDefinition}.
 *
 * @param <S> the type of the states
 * @param <E> the type of the events
 * @param <C> The Context
 */
public class SimpleStateMachineWithContext<S extends Enum<?>, E extends Enum<?>, C>
		extends AbstractStateMachine<SimpleStateMachineWithContext<S, E, C>, S, E> {

	private C context;

	protected SimpleStateMachineWithContext(final StateMachine<S, E> driver) {
		super(driver);

	}

	void set(final C context) {
		this.context = context;
	}

	public C get() {
		return context;
	}
}
