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

/**
 * Entry Action Syntax.
 * 
 * @author Ueli Kurmann 
 * 
 * @param <TStateMachine> the type of the state machine
 * @param <S> the type of the states.
 * @param <E> the type of the events.
 */
public interface EntryActionSyntax<TStateMachine extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>>
    extends ExitActionSyntax<TStateMachine, S, E>,
    EventSyntax<TStateMachine, S, E> {

  /**
   * Defines an entry action.
   * 
   * @param action the {@link FsmAction0} to be executed.
   * @return the ExitActionSyntax
   */
  ExitActionSyntax<TStateMachine, S, E> executeOnEntry(
      FsmAction0<TStateMachine, S, E> action);

  /**
   * Defines an entry action.
   * 
   * @param <T> The return type of the action.
   * @param action The {@link FsmAction1} to be executed.
   * @param parameter (necessary?)
   * @return the ExitActionSyntax
   */
  <T> ExitActionSyntax<TStateMachine, S, E> executeOnEntry(
		  FsmAction1<TStateMachine, S, E, T> action, T parameter);

}
