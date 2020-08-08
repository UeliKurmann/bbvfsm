package ch.bbv.fsm.impl;

import java.util.function.Consumer;

import ch.bbv.fsm.StateMachineFactory;
import ch.bbv.fsm.impl.builder.BuilderContext;
import ch.bbv.fsm.impl.builder.BuilderFactory;
import ch.bbv.fsm.impl.internal.events.AnnotationEventHandler;

/**
 * A Statemachine Builder.
 *
 * @author Ueli Kurmann
 *
 * @param <C> the context
 * @param <S> the states
 * @param <E> the events
 */
public final class StatemachineBuilder<C, S extends Enum<?>, E extends Enum<?>>
		implements BuilderContext<S, E, C>, BuilderFactory<S, E, C> {

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
	public static <S extends Enum<?>, E extends Enum<?>, C> BuilderContext<S, E, C> createWithContext(final S initialState,
			final Consumer<SimpleStateMachineDefinitionWithContext<S, E, C>> definition) {
		final SimpleStateMachineDefinitionWithContext<S, E, C> result = new SimpleStateMachineDefinitionWithContext<S, E, C>(initialState);
		definition.accept(result);
		result.addEventHandler(new AnnotationEventHandler<S, E, C>());

		final StatemachineBuilder<C, S, E> builder = new StatemachineBuilder<C, S, E>();
		builder.definition = result;
		return builder;
	}

	public static <S extends Enum<?>, E extends Enum<?>> StateMachineFactory<SimpleStateMachine<S, E>, S, E> create(final S initialState,
			final Consumer<SimpleStateMachineDefinition<S, E>> definition) {
		final SimpleStateMachineDefinition<S, E> result = new SimpleStateMachineDefinition<S, E>(initialState);
		definition.accept(result);
		return result;
	}

	@Override
	public BuilderFactory<S, E, C> context(final C c) {
		this.c = c;
		return this;
	}

	@Override
	public SimpleStateMachineWithContext<S, E, C> buildPassive(final String name) {
		final SimpleStateMachineWithContext<S, E, C> sm = this.definition.createPassiveStateMachine(name);
		sm.set(this.c);
		return sm;
	}

	@Override
	public SimpleStateMachineWithContext<S, E, C> buildActive(final String name) {
		final SimpleStateMachineWithContext<S, E, C> sm = this.definition.createActiveStateMachine(name);
		sm.set(this.c);
		return sm;
	}

}
