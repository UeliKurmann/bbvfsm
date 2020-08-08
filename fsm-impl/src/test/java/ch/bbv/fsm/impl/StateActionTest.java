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
package ch.bbv.fsm.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.action.FsmAction0;
import ch.bbv.fsm.impl.StatesAndEvents.Events;
import ch.bbv.fsm.impl.StatesAndEvents.States;

public class StateActionTest {

	private class StateActionTestStateMachine extends AbstractStateMachine<StateActionTestStateMachine, States, Events> {

		private Boolean entered;

		private Object[] arguments;

		StateActionTestStateMachine(final StateMachine<States, Events> driver) {
			super(driver);
		}

		Boolean getEntered() {
			return entered;
		}

		void setEntered(final Boolean entered) {
			this.entered = entered;
		}

		Object[] getArguments() {
			return arguments;
		}

		void setArguments(final Object... arguments) {
			this.arguments = arguments;
		}

	}

	private class StateActionTestStateMachineDefinition
			extends AbstractStateMachineDefinition<StateActionTestStateMachine, States, Events> {

		StateActionTestStateMachineDefinition() {
			super(States.A);
		}

		@Override
		protected StateActionTestStateMachine createStateMachine(final StateMachine<States, Events> driver) {
			return new StateActionTestStateMachine(driver);
		}

	}

	public static class ActionClass implements FsmAction0<StateActionTestStateMachine, States, Events> {

		@Override
		public void exec(final StateActionTestStateMachine fsm) {
			fsm.setEntered(true);
		}
	}


	/**
	 * Entry actions are executed when a state is entered.
	 */
	@Test
	public void entryAction() {

		final StateActionTestStateMachineDefinition stateMachineDefinition = new StateActionTestStateMachineDefinition();

		stateMachineDefinition.in(States.A).executeOnEntry(new ActionClass());

		final StateActionTestStateMachine fsm = stateMachineDefinition.createPassiveStateMachine("entryAction", States.A);
		fsm.start();

		Assertions.assertThat(fsm.getEntered()).isTrue();
	}

	/**
	 * Exit actions are executed when a state is left.
	 */
	@Test
	public void exitAction() {

		final StateActionTestStateMachineDefinition stateMachineDefinition = new StateActionTestStateMachineDefinition();

		stateMachineDefinition.in(States.A).executeOnExit(new ActionClass()).on(Events.B).goTo(States.B);

		final StateActionTestStateMachine fsm = stateMachineDefinition.createPassiveStateMachine("exitAction", States.A);
		fsm.start();
		fsm.fire(Events.B);

		Assertions.assertThat(fsm.getEntered()).isTrue();
	}

	/**
	 * Entry actions can be parametrized.
	 */
	@Test
	public void parameterizedEntryAction() {

		final StateActionTestStateMachineDefinition stateMachineDefinition = new StateActionTestStateMachineDefinition();

		stateMachineDefinition.in(States.A).executeOnEntry(sm -> sm.setArguments(Integer.valueOf(3)));

		final StateActionTestStateMachine fsm = stateMachineDefinition.createPassiveStateMachine("parameterizedEntryAction", States.A);

		fsm.start();

		Assertions.assertThat(fsm.getArguments()[0]).isEqualTo(3);
	}

	/**
	 * Exit actions can be parametrized.
	 */
	@Test
	public void parametrizedExitAction() {
		final StateActionTestStateMachineDefinition stateMachineDefinition = new StateActionTestStateMachineDefinition();

		stateMachineDefinition.in(States.A).executeOnExit(sm -> sm.setArguments(Integer.valueOf(3))).on(Events.B).goTo(States.B);

		final StateActionTestStateMachine fsm = stateMachineDefinition.createPassiveStateMachine("exitAction", States.A);
		fsm.start();
		fsm.fire(Events.B);

		Assertions.assertThat(fsm.getArguments()[0]).isEqualTo(3);
	}
}
