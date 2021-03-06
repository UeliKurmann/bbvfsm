/*******************************************************************************
 *  Copyright 2010, 2011 bbv Software Services AG, Ueli Kurmann
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * Contributors:
 *     bbv Software Services AG (http://www.bbv.ch), Ueli Kurmann
 *******************************************************************************/
package ch.bbv.fsm.impl.internal.driver;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.impl.internal.statemachine.state.StateContext;
import ch.bbv.fsm.impl.internal.statemachine.transition.TransitionContext;

/**
 * Notifier interface.
 * 
 * @author Ueli Kurmann  
 * 
 * @param <SM>
 *            the type of state machine
 * @param <S>
 *            the type of the states.
 * @param <E>
 *            the type of the events.
 */
public interface Notifier<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>> {

	/**
	 * Called when an exception was thrown.
	 * 
	 * @param stateContext
	 *            the context.
	 * @param exception
	 *            the exception.
	 */
	void onExceptionThrown(StateContext<SM, S, E> stateContext, Exception exception);

	/**
	 * Called when an exception was thrown in a transition.
	 * 
	 * @param context
	 *            the transition context.
	 * @param exception
	 *            the exception.
	 */
	void onExceptionThrown(TransitionContext<SM, S, E> context, Exception exception);

	/**
	 * Called before a transition is executed.
	 * 
	 * @param context
	 *            the context.
	 */
	void onTransitionBegin(StateContext<SM, S, E> context);
}
