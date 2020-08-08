package ch.bbv.fsm.events.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for Event Handler.
 * 
 * @author Ueli Kurmann
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface OnTransitionDeclined {

}
