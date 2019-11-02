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
package ch.bbv.fsm;

import ch.bbv.fsm.dsl.EntryActionSyntax;
import ch.bbv.fsm.events.StateMachineEventHandler;

/**
 * Defines the interface of a state machine.
 * 
 * @author Ueli Kurmann
 * @param <S>             the enumeration type of the states.
 * @param <E>             the enumeration type of the events.
 * @param <TStateMachine> the type of state machine
 */
public interface StateMachineDefinition<TStateMachine extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>> {

	/**
	 * Returns the state machine's default initial state. This state will be used as
	 * initial state if the state machine is created using
	 * {@link #createActiveStateMachine(String)} or
	 * {@link #createPassiveStateMachine(String)}.
	 */
	S getInitialState();

	/**
	 * Defines behavior of a state.
	 * 
	 * @param state the state
	 */
	EntryActionSyntax<TStateMachine, S, E> in(S state);

	/**
	 * Defines a state hierarchy.
	 * 
	 * @param superStateId      the super state id.
	 * @param initialSubStateId the initial sub state id.
	 * @param historyType       type of history.
	 * @param subStateIds       the sub state id's.
	 */
	void defineHierarchyOn(S superStateId, S initialSubStateId, HistoryType historyType, @SuppressWarnings("unchecked") S... subStateIds);

	/**
	 * Returns the name of this state machine.
	 */
	String getName();

	/**
	 * Adds an event handler.
	 * 
	 * @param handler the event handler
	 */
	void addEventHandler(StateMachineEventHandler<TStateMachine, S, E> handler);


	/**
	 * Creates an active state-machine from this definition.
	 * 
	 * @param name         the state machine's name
	 * @param initialState The state to which the state machine is initialized.
	 */
	TStateMachine createActiveStateMachine(String name, S initialState);

	/**
	 * Creates an active state-machine this definition with the default initial
	 * state.
	 * 
	 * @param name the state machine's name
	 */
	TStateMachine createActiveStateMachine(String name);

	/**
	 * Creates a passive state-machine from definition.
	 * 
	 * @param name         the state machine's name
	 * @param initialState The state to which the state machine is initialized.
	 */
	TStateMachine createPassiveStateMachine(String name, S initialState);

	/**
	 * Creates an passive state-machine from this definition with the default
	 * initial state.
	 * 
	 * @param name the state machine's name
	 */
	TStateMachine createPassiveStateMachine(String name);

}
