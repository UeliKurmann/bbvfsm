/*******************************************************************************
Ø *  Copyright 2010, 2011 bbv Software Services AG, Ueli Kurmann
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
package ch.bbv.fsm.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.events.StateMachineEventHandlerAdapter;
import ch.bbv.fsm.events.TransitionEvent;
import ch.bbv.fsm.guard.Guard;
import ch.bbv.fsm.impl.StatesAndEvents.Events;
import ch.bbv.fsm.impl.StatesAndEvents.States;

public class GuardTest {

	private class GuardTestStateMachine extends AbstractStateMachine<GuardTestStateMachine, States, Events> {

		private final Object[][] eventArguments = new Object[1][];

		GuardTestStateMachine(final StateMachine<States, Events> driver) {
			super(driver);
		}

		void setEventArguments(final int position, final Object[] eventArguments) {
			this.eventArguments[position] = eventArguments;
		}

		Object[][] getEventArguments() {
			return eventArguments;
		}

	}

	private class GuardTestStateMachineDefinition extends AbstractStateMachineDefinition<GuardTestStateMachine, States, Events> {

		GuardTestStateMachineDefinition(final String name, final States initialState) {
			super(States.A);
		}

		@Override
		protected GuardTestStateMachine createStateMachine(final StateMachine<States, Events> driver) {
			return new GuardTestStateMachine(driver);
		}

	}

	private class Handler extends StateMachineEventHandlerAdapter<GuardTestStateMachine, States, Events> {

		@Override
		public void onTransitionDeclined(final TransitionEvent<GuardTestStateMachine, States, Events> arg) {
			GuardTest.this.transitionDeclined = true;
		}
	}

	public static class FunctionTrue implements Guard<GuardTestStateMachine, States, Events, Object[], Boolean> {

		@Override
		public boolean execute(final GuardTestStateMachine stateMachine, final Object... parameter) {
			return true;
		}
	}

	public static class FunctionFalse implements Guard<GuardTestStateMachine, States, Events, Object[], Boolean> {

		@Override
		public boolean execute(final GuardTestStateMachine stateMachine, final Object... parameter) {
			return false;
		}
	}

	public static class FunctionTrueWithArgs implements Guard<GuardTestStateMachine, States, Events, Object[], Boolean> {

		@Override
		public boolean execute(final GuardTestStateMachine stateMachine, final Object... parameter) {

			stateMachine.setEventArguments(0, parameter);
			return true;
		}

	}

	private boolean transitionDeclined = false;

	/**
	 * When all guards deny the execution of its transition then ???
	 */
	@Test
	public void allGuardsReturnFalse() {

		final GuardTestStateMachineDefinition stateMachineDefinition = new GuardTestStateMachineDefinition("allGuardsReturnFalse",
				States.A);

		stateMachineDefinition.addEventHandler(new Handler());

		stateMachineDefinition.in(States.A).on(Events.A).goTo(States.B).onlyIf(new GuardTest.FunctionFalse()).on(Events.A).goTo(States.C)
				.onlyIf(new GuardTest.FunctionFalse());

		final StateMachine<States, Events> fsm = stateMachineDefinition.createPassiveStateMachine("allGuardsReturnFalse", States.A);
		fsm.start();

		fsm.fire(Events.A);
		
		Assertions.assertThat(fsm.getCurrentState()).isEqualTo(States.A);
		Assertions.assertThat(this.transitionDeclined).isTrue();
	}

	/**
	 * The event arguments are passed to the guard.
	 */
	@Test
	public void eventArgumentsArePassedToTheGuard() {

		final Object[] originalEventArguments = new Object[] { 1, 2, "test" };

		final GuardTestStateMachineDefinition stateMachineDefinition = new GuardTestStateMachineDefinition(
				"eventArgumentsArePassedToTheGuard", States.A);

		stateMachineDefinition.in(States.A).on(Events.A).goTo(States.B).onlyIf(new GuardTest.FunctionTrueWithArgs());

		final GuardTestStateMachine fsm = stateMachineDefinition.createPassiveStateMachine("allGuardsReturnFalse", States.A);
		fsm.start();

		fsm.fire(Events.A, originalEventArguments);
		
		Assertions.assertThat(fsm.getEventArguments()[0]).isSameAs(originalEventArguments);
	}

	/**
	 * Only the transition with a guard returning true is executed and the event
	 * arguments are passed to the guard.
	 */
	@Test
	public void transitionWithGuardReturningTrueIsExecuted() {

		final GuardTestStateMachineDefinition stateMachineDefinition = new GuardTestStateMachineDefinition(
				"transitionWithGuardReturningTrueIsExecuted", States.A);

		stateMachineDefinition.in(States.A).on(Events.A).goTo(States.B).onlyIf(new GuardTest.FunctionFalse()).on(Events.A).goTo(States.C)
				.onlyIf(new GuardTest.FunctionTrue());

		final StateMachine<States, Events> fsm = stateMachineDefinition.createPassiveStateMachine("allGuardsReturnFalse", States.A);
		fsm.start();

		fsm.fire(Events.A);

		Assertions.assertThat(fsm.getCurrentState()).isEqualTo(States.C);
	}
}
