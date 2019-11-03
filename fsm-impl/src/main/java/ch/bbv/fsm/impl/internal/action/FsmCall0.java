package ch.bbv.fsm.impl.internal.action;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.action.FsmAction0;

/**
 * {@link FsmCall} implementation with zero parameters.
 *
 * @param <SM> the type of the state machine.
 * @param <S>  the type of the states.
 * @param <E>  the type of the events.
 */
public class FsmCall0<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>> implements FsmCall<SM, S, E> {
	
	private final FsmAction0<SM, S, E> action;

	/**
	 * Constructor.
	 * 
	 * @param action the action.
	 */
	public FsmCall0(final FsmAction0<SM, S, E> action) {
		this.action = action;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execOn(final SM fsm) {
		action.exec(fsm);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execOn(final SM fsm, final Object... args) {
		action.exec(fsm);
	}
}
