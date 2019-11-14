package ch.bbv.fsm.impl.builder;

import ch.bbv.fsm.impl.SimpleStateMachineWithContext;

public interface BuilderFactory<S extends Enum<?>, E extends Enum<?>, C> {

	SimpleStateMachineWithContext<S, E, C> buildPassive(String name);

	SimpleStateMachineWithContext<S, E, C> buildActive(String name);

}