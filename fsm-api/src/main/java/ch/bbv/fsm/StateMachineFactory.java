package ch.bbv.fsm;

/**
 * State Machine Factory.
 * @author Ueli Kurmann
 *
 * @param <SM> the state machine
 * @param <S> the enum states
 * @param <E> the enum events
 */
public interface StateMachineFactory<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>> {
	
	/**
	 * Creates an active state-machine from this definition.
	 * 
	 * @param name         the state machine's name
	 * @param initialState The state to which the state machine is initialized.
	 */
	SM createActiveStateMachine(String name, S initialState);

	/**
	 * Creates an active state-machine this definition with the default initial
	 * state.
	 * 
	 * @param name the state machine's name
	 */
	SM createActiveStateMachine(String name);

	/**
	 * Creates a passive state-machine from definition.
	 * 
	 * @param name         the state machine's name
	 * @param initialState The state to which the state machine is initialized.
	 */
	SM createPassiveStateMachine(String name, S initialState);

	/**
	 * Creates an passive state-machine from this definition with the default
	 * initial state.
	 * 
	 * @param name the state machine's name
	 */
	SM createPassiveStateMachine(String name);

}
