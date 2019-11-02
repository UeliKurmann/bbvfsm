package ch.bbv.fsm.impl;

import java.util.function.Consumer;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.StateMachineFactory;

/**
 * A simple state machine's definition. This is used if no special type should
 * be created.
 * 
 * @param <S> the type of the states
 * @param <E> the type of the events
 */
public class SimpleStateMachineDefinition<S extends Enum<?>, E extends Enum<?>>
		extends AbstractStateMachineDefinition<SimpleStateMachine<S, E>, S, E> {

	/**
	 * Constructor.
	 * 
	 * @param initialState the default inital state to use
	 */
	public SimpleStateMachineDefinition(final S initialState) {
		super(initialState);
	}

	@Override
	protected SimpleStateMachine<S, E> createStateMachine(final StateMachine<S, E> driver) {
		return new SimpleStateMachine<>(driver);
	}

	public static <S extends Enum<?>, E extends Enum<?>> StateMachineFactory<SimpleStateMachine<S, E>, S, E> create(S initialState,
			Consumer<SimpleStateMachineDefinition<S, E>> definition) {
		SimpleStateMachineDefinition<S, E> result = new SimpleStateMachineDefinition<S, E>(initialState);
		definition.accept(result);
		return result;
	}

}
