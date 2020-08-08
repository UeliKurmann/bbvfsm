package ch.bbv.fsm.impl.builder;

import ch.bbv.fsm.impl.SimpleStateMachineWithContext;

/**
 * A Builder Factory.
 *
 * @author Ueli Kurmann
 *
 * @param <S> the states
 * @param <E> the events
 * @param <C> the context
 */
public interface BuilderFactory<S extends Enum<?>, E extends Enum<?>, C> {

	SimpleStateMachineWithContext<S, E, C> buildPassive(String name);

	SimpleStateMachineWithContext<S, E, C> buildActive(String name);

}