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
package ch.bbv.fsm.events;

import ch.bbv.fsm.StateMachine;

/**
 * The exception event argument.
 * 
 * @author Ueli Kurmann
 * @param <S>   the type of the states.
 * @param <E>   the type of the events.
 * @param <SM> the type of the state machine
 */
public interface ExceptionEvent<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>> extends ContextEvent<SM, S, E> {

	/**
	 * Returns the exception.
	 * 
	 * @return the exception.
	 */
	Exception getException();

}