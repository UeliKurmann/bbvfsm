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
package ch.bbv.fsm.dsl;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.action.FsmAction0;

/**
 * Possibilities to execute an action on exit.
 * 
 * @param <SM> the type of the state machine
 * @param <S>  the type of the states.
 * @param <E>  the type of the events.
 */
public interface ExitActionSyntax<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>> extends EventSyntax<SM, S, E> {

	/**
	 * Defines an exit action.
	 * 
	 * @param actionClass the {@link FsmAction0} Class
	 * @return the EventSyntax
	 */
	EventSyntax<SM, S, E> executeOnExit(FsmAction0<SM, S, E> actionClass);


}
