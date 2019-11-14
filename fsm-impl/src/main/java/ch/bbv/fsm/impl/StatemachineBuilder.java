package ch.bbv.fsm.impl;

import java.util.function.Consumer;

import ch.bbv.fsm.StateMachineFactory;
import ch.bbv.fsm.impl.builder.BuilderFactory;
import ch.bbv.fsm.impl.builder.BuilderContext;
import ch.bbv.fsm.impl.internal.events.AnnotationEventHandler;

public final class StatemachineBuilder<C, S extends Enum<?>, E extends Enum<?>> implements BuilderContext<S, E, C>, BuilderFactory<S, E, C> {

	private C c;
	private SimpleStateMachineDefinitionWithContext<S, E, C> definition;

	private StatemachineBuilder() {
		// static helper class
	}

	/**
	 * Creates a {@link StateMachineFactory} with an internal
	 * {@link SimpleStateMachineDefinition} instance.
	 * 
	 * @param <S>
	 * @param <E>
	 * @param initialState the initial state for the state machine
	 * @param definition   a consumer that accepts a
	 *                     {@link SimpleStateMachineDefinition} to configure the
	 *                     state machine.
	 * @return the {@link StateMachineFactory}
	 */
	public static <S extends Enum<?>, E extends Enum<?>, C> BuilderContext<S, E, C> createWithContext(S initialState,
			Consumer<SimpleStateMachineDefinitionWithContext<S, E, C>> definition) {
		SimpleStateMachineDefinitionWithContext<S, E, C> result = new SimpleStateMachineDefinitionWithContext<S, E, C>(initialState);
		definition.accept(result);
		result.addEventHandler(new AnnotationEventHandler<S, E, C>());

		StatemachineBuilder<C, S, E> builder = new StatemachineBuilder<C, S, E>();
		builder.definition = result;
		return builder;
	}

	public static <S extends Enum<?>, E extends Enum<?>> StateMachineFactory<SimpleStateMachine<S, E>, S, E> create(S initialState,
			Consumer<SimpleStateMachineDefinition<S, E>> definition) {
		SimpleStateMachineDefinition<S, E> result = new SimpleStateMachineDefinition<S, E>(initialState);
		definition.accept(result);
		return result;
	}

	@Override
	public BuilderFactory<S, E, C> context(C c) {
		this.c = c;
		return this;
	}

	@Override
	public SimpleStateMachineWithContext<S, E, C> buildPassive(String name) {
		SimpleStateMachineWithContext<S, E, C> sm = this.definition.createPassiveStateMachine(name);
		sm.set(this.c);
		return sm;
	}

	@Override
	public SimpleStateMachineWithContext<S, E, C> buildActive(String name) {
		SimpleStateMachineWithContext<S, E, C> sm = this.definition.createActiveStateMachine(name);
		sm.set(this.c);
		return sm;
	}

}
