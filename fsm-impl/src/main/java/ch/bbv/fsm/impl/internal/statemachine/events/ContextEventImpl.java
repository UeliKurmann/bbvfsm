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
package ch.bbv.fsm.impl.internal.statemachine.events;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.events.ContextEvent;
import ch.bbv.fsm.impl.internal.statemachine.state.StateContext;

/**
 * See {@link ContextEvent}.
 * 
 * @param <S>             the state enumeration
 * @param <E>             the event enumeration
 * @param <TStateMachine> the type of the state machine
 */
public class ContextEventImpl<TStateMachine extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>>
		implements ContextEvent<TStateMachine, S, E> {

	/**
	 * The context.
	 */
	private final StateContext<TStateMachine, S, E> stateContext;

	/**
	 * Initializes a new instance.
	 * 
	 * @param stateContext the state context.
	 */
	public ContextEventImpl(final StateContext<TStateMachine, S, E> stateContext) {
		this.stateContext = stateContext;
	}

	/**
	 * Returns the state context.
	 * 
	 * @return the state context.
	 */
	public StateContext<TStateMachine, S, E> getStateContext() {
		return this.stateContext;
	}

	@Override
	public TStateMachine getSource() {
		return this.stateContext.getStateMachine();
	}
}
