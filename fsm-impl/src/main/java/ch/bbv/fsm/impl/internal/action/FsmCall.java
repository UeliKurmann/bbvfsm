package ch.bbv.fsm.impl.internal.action;

import ch.bbv.fsm.StateMachine;

/**
 * Defines the API of the Call implementations.
 *
 * @param <SM> the state machine.
 * @param <S>  the type of the states.
 * @param <E>  the type of the events.
 */
public interface FsmCall<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>> {

	/**
	 * The execute method without parameters.
	 * 
	 * @param fsm the state machine.
	 */
	void execOn(SM fsm);

	/**
	 * The execute method.
	 * 
	 * @param fsm  the state machine.
	 * @param args the parameters.
	 */
	void execOn(SM fsm, Object... args);
}
