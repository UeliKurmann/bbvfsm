package ch.bbv.fsm.impl.builder;

public interface BuilderContext<S extends Enum<?>, E extends Enum<?>, C> {

	BuilderFactory<S, E, C> context(C c);

}
