/*******************************************************************************
 * Copyright 2010, 2011 bbv Software Services AG, Ueli Kurmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * Contributors: bbv Software Services AG (http://www.bbv.ch), Ueli Kurmann
 *******************************************************************************/
package ch.bbv.fsm.dsl;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.action.FsmAction0;
import ch.bbv.fsm.action.FsmAction1;
import ch.bbv.fsm.action.FsmAction2;

/**
 * Possibilities to execute an action.
 *
 * @param <SM> the type of state machine
 * @param <S>  the type of the states.
 * @param <E>  the type of the events.
 */
public interface ExecuteSyntax<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>> extends GuardSyntax<SM, S, E> {

	/**
	 * Defines the actions to execute on a transition.
	 *
	 * @param action The action class.
	 */
	ExecuteSyntax<SM, S, E> execute(FsmAction0<SM, S, E> action);

	/**
	 * Defines the actions to execute on a transition.
	 * 
	 * @param action the action.
	 * @param <T>    the type of the one and only parameter.
	 * 
	 * @return
	 */
	<T> ExecuteSyntax<SM, S, E> execute(FsmAction1<SM, S, E, T> action);

	/**
	 * Defines the actions to execute on a transition.
	 * 
	 * @param action the action.
	 * @param <T1>   the type of the first parameter.
	 * @param <T2>   the type of the second parameter.
	 * @return
	 */
	<T1, T2> ExecuteSyntax<SM, S, E> execute(FsmAction2<SM, S, E, T1, T2> action);

}
