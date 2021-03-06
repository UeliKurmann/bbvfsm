/*******************************************************************************
 * Copyright 2010, 2011 bbv Software Services AG, Mario Martinez
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
 * Contributors: bbv Software Services AG (http://www.bbv.ch), Mario Martinez
 *******************************************************************************/

package ch.bbv.fsm.acceptance.function.definition;

import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.action.FsmAction0;
import ch.bbv.fsm.guard.Guard;
import ch.bbv.fsm.impl.AbstractStateMachine;
import ch.bbv.fsm.impl.AbstractStateMachineDefinition;
import ch.bbv.fsm.impl.StatesAndEvents.Events;
import ch.bbv.fsm.impl.StatesAndEvents.States;

/**
 * @author Mario Martinez
 *
 */
public class FunctionDefinitionTest {

	private class FunctionDefinitionTestStateMachine extends AbstractStateMachine<FunctionDefinitionTestStateMachine, States, Events> {

		private final StringBuffer log = new StringBuffer();

		private final List<Guard<FunctionDefinitionTestStateMachine, States, Events, Object[], Boolean>> callingActions = new LinkedList<>();

		String consumeLog() {
			return log.toString();
		}

		void log(final String msg) {
			this.log.append(msg);
		}

		void addCallingAction(final Guard<FunctionDefinitionTestStateMachine, States, Events, Object[], Boolean> callingAction) {
			callingActions.add(callingAction);
		}

		FunctionDefinitionTestStateMachine(final StateMachine<States, Events> driver) {
			super(driver);
		}

	}

	private class FunctionDefinitionTestStateMachineDefinition
			extends AbstractStateMachineDefinition<FunctionDefinitionTestStateMachine, States, Events> {

		FunctionDefinitionTestStateMachineDefinition(final String name, final States initialState) {

			super(States.A);
		}

		@Override
		protected FunctionDefinitionTestStateMachine createStateMachine(final StateMachine<States, Events> driver) {
			return new FunctionDefinitionTestStateMachine(driver);
		}

	}

	public static class DoNothing implements FsmAction0<FunctionDefinitionTestStateMachine, States, Events> {

		@Override
		public void exec(final FunctionDefinitionTestStateMachine stateMachine) {
		}
	}

	public static class WriteLogFunction implements Guard<FunctionDefinitionTestStateMachine, States, Events, Object[], Boolean> {

		@Override
		public boolean execute(final FunctionDefinitionTestStateMachine stateMachine, final Object... parameter) {

			stateMachine.addCallingAction(this);
			stateMachine.log("execute(): FunctionDefinitionTest.WriteLogFunction.class");

			return Boolean.TRUE;
		}

	}

	@Test
	public void functionWhenFunctionToExceuteThenExecuteOK() {

		final FunctionDefinitionTestStateMachineDefinition stateMachineDefinition = new FunctionDefinitionTestStateMachineDefinition(
				"simpleSM", States.A);
		final FunctionDefinitionTestStateMachine stateMachine = stateMachineDefinition.createPassiveStateMachine("simpleSM", States.A);

		stateMachineDefinition.in(States.A).on(Events.A).goTo(States.B).execute(new FunctionDefinitionTest.DoNothing())
				.onlyIf(new FunctionDefinitionTest.WriteLogFunction());

		stateMachine.start();
		stateMachine.fire(Events.A);
		Assertions.assertThat(stateMachine.consumeLog()).isEqualTo("execute(): FunctionDefinitionTest.WriteLogFunction.class");
	}

}
