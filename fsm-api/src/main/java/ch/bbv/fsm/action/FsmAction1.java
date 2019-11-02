package ch.bbv.fsm.action;

import ch.bbv.fsm.StateMachine;

/**
 * A functional interface that can execute actions with one parameter.
 *
 * @param <SM>
 * @param <S>
 * @param <E>
 * @param <P1>
 */
@FunctionalInterface
public interface FsmAction1<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>, P1> {

	/**
	 * The functional interface method.
	 * 
	 * @param fsm The instance of the SM on which the method will be called.
	 * @param p1  The first parameter to the referenced method.
	 */
	void exec(SM fsm, P1 p1);
}