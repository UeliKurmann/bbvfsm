package ch.bbv.fsm.action;

import ch.bbv.fsm.StateMachine;

/**
 * A functional interface that can execute actions without a parameter. 
 *
 * @param <SM>
 * @param <S>
 * @param <E>
 */
@FunctionalInterface
public interface FsmAction0<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>> {

	/**
	 * The functional interface method.
	 * 
	 * @param fsm
	 *            The instance of the SM on which the method will be called.
	 */
	void exec(SM fsm);
}