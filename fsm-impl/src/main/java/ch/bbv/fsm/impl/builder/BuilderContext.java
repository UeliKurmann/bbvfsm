package ch.bbv.fsm.impl.builder;

/**
 * Context Builder interface.
 * 
 * @author Ueli Kurmann
 *
 * @param <S> the states
 * @param <E> the events
 * @param <C> the context
 */
public interface BuilderContext<S extends Enum<?>, E extends Enum<?>, C> {

	BuilderFactory<S, E, C> context(C c);

}
