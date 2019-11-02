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
package ch.bbv.fsm.impl.internal.statemachine;

import java.util.Stack;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.impl.internal.statemachine.state.InternalState;
import ch.bbv.fsm.impl.internal.statemachine.state.StateContext;

/**
 * InternalStateImpl Machine Initializer.
 * 
 * @author Ueli Kurmann
 * 
 * @param <TStateMachine> the type of state machine
 * @param <S>             the type of the states
 * @param <E>             the type of the events
 */
class StateMachineInitializer<TStateMachine extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>> {

	private final InternalState<TStateMachine, S, E> initialState;

	private final StateContext<TStateMachine, S, E> stateContext;

	/**
	 * Initializes a new instance.
	 * 
	 * @param initialState the initial state.
	 * @param stateContext the state context.
	 */
	StateMachineInitializer(final InternalState<TStateMachine, S, E> initialState, final StateContext<TStateMachine, S, E> stateContext) {
		this.initialState = initialState;
		this.stateContext = stateContext;
	}

	/**
	 * Enters the initial state by entering all states further up in the hierarchy.
	 * 
	 * @return The entered state. The initial state or a sub state of the initial
	 *         state.
	 */
	InternalState<TStateMachine, S, E> enterInitialState() {
		final Stack<InternalState<TStateMachine, S, E>> stack = this.traverseUpTheStateHierarchy();
		this.traverseDownTheStateHierarchyAndEnterStates(stack);
		return this.initialState.enterByHistory(this.stateContext);
	}

	/**
	 * Traverses down the state hierarchy and enter all states along.
	 * 
	 * @param stack The stack containing the state hierarchy.
	 */
	private void traverseDownTheStateHierarchyAndEnterStates(final Stack<InternalState<TStateMachine, S, E>> stack) {
		InternalState<TStateMachine, S, E> state;
		while (stack.size() > 0) {
			state = stack.pop();
			state.entry(this.stateContext);
		}
	}

	/**
	 * Traverses up the state hierarchy and build the stack of states.
	 * 
	 * @return The stack containing all states up the state hierarchy.
	 */
	private Stack<InternalState<TStateMachine, S, E>> traverseUpTheStateHierarchy() {
		final Stack<InternalState<TStateMachine, S, E>> stack = new Stack<>();

		InternalState<TStateMachine, S, E> state = this.initialState;
		while (state != null) {
			stack.push(state);
			state = state.getSuperState();
		}

		return stack;
	}
}
