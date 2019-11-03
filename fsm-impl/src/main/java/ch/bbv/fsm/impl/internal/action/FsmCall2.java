package ch.bbv.fsm.impl.internal.action;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.action.FsmAction2;

/**
 * {@link FsmCall} implementation with two parameters.
 *
 * @param <SM> the state machine.
 * @param <S>  the type of the states.
 * @param <E>  the type of the events.
 * @param <P1> the type of the first parameter.
 * @param <P2> the type of the second parameter.
 */
public class FsmCall2<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>, P1, P2> implements FsmCall<SM, S, E> {
	private final FsmAction2<SM, S, E, P1, P2> action;
	private final P1 p1;
	private final P2 p2;

	/**
	 * Constructor.
	 * 
	 * @param action the action.
	 * @param p1     the first parameter.
	 * @param p2     the second parameter.
	 */
	public FsmCall2(final FsmAction2<SM, S, E, P1, P2> action, final P1 p1, final P2 p2) {
		this.action = action;
		this.p1 = p1;
		this.p2 = p2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execOn(final SM fsm) {
		action.exec(fsm, p1, p2);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execOn(final SM fsm, final Object... args) {
		action.exec(fsm, (P1) args[0], (P2) args[1]);
	}
}
