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
package ch.bbv.fsm.impl.internal.statemachine.transition;

import java.util.List;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.impl.internal.statemachine.state.InternalState;

/**
 * The implementation of {@link TransitionResult}.
 *
 * @param <TStateMachine> the type of state machine
 * @param <S>             the type of the states
 * @param <E>             the type of the events
 */
public class TransitionResult<TStateMachine extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>> {

	/**
	 * This value represents that no transition was fired.
	 */
	private final boolean fired;
	private final InternalState<TStateMachine, S, E> newState;
	private final List<? extends Throwable> exceptions;

	/**
	 * Initializes a new instance of the TransitionResultImpl class.
	 *
	 * @param fired      true the transition was fired.
	 * @param newState   the new state
	 * @param exceptions the exceptions
	 */
	public TransitionResult(final boolean fired, final InternalState<TStateMachine, S, E> newState,
			final List<? extends Throwable> exceptions) {
		this.fired = fired;
		this.newState = newState;
		this.exceptions = exceptions;
	}

	/**
	 * Returns the list of exceptions.
	 *
	 * @return the list of exceptions.
	 */
	public List<? extends Throwable> getExceptions() {
		return this.exceptions;
	}

	/**
	 * Returns the new state.
	 * 
	 * @return the new state
	 */
	public InternalState<TStateMachine, S, E> getNewState() {
		return this.newState;
	}

	public boolean isFired() {
		return this.fired;
	}

	/**
	 * Creates a not fired result.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static TransitionResult getNotFired() {
		return new TransitionResult(false, null, null);

	}
}
