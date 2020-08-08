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
import ch.bbv.fsm.events.ExceptionEvent;
import ch.bbv.fsm.events.StateMachineEventHandlerAdapter;
import ch.bbv.fsm.events.TransitionEvent;
import ch.bbv.fsm.events.TransitionExceptionEvent;
import ch.bbv.fsm.guard.Guard;
import ch.bbv.fsm.impl.StatesAndEvents.Events;
import ch.bbv.fsm.impl.StatesAndEvents.States;

public class ExceptionCasesTest {

	private class Handler extends StateMachineEventHandlerAdapter<ExceptionCasesTestStateMachine, States, Events> {

		@Override
		public void onExceptionThrown(final ExceptionEvent<ExceptionCasesTestStateMachine, States, Events> eventArgs) {
			if (eventArgs != null) {
				ExceptionCasesTest.this.recordedException = eventArgs.getException();
			}

		}

		@Override
		public void onTransitionDeclined(final TransitionEvent<ExceptionCasesTestStateMachine, States, Events> arg) {
			ExceptionCasesTest.this.transitionDeclined = true;

		}

		@Override
		public void onTransitionThrowsException(final TransitionExceptionEvent<ExceptionCasesTestStateMachine, States, Events> eventArgs) {

			ExceptionCasesTest.this.recordedStateId = eventArgs.getStateId();
			ExceptionCasesTest.this.recordedEventId = eventArgs.getEventId();
			ExceptionCasesTest.this.recordedEventArguments = eventArgs.getEventArguments();
			ExceptionCasesTest.this.recordedException = eventArgs.getException();

		}

	}

	private class ExceptionCasesTestStateMachine extends AbstractStateMachine<ExceptionCasesTestStateMachine, States, Events> {

		private final RuntimeException e = new RuntimeException();

		RuntimeException getException() {
			return e;
		}

		ExceptionCasesTestStateMachine(final StateMachine<States, Events> driver) {
			super(driver);
		}

	}

	private class ExceptionCasesTestStateMachineDefinition
			extends AbstractStateMachineDefinition<ExceptionCasesTestStateMachine, States, Events> {

		ExceptionCasesTestStateMachineDefinition(final String name, final States initialState) {
			super(States.A);
		}

		@Override
		protected ExceptionCasesTestStateMachine createStateMachine(final StateMachine<States, Events> driver) {
			return new ExceptionCasesTestStateMachine(driver);
		}

	}

	public static class ThrowExceptionAction implements FsmAction0<ExceptionCasesTestStateMachine, States, Events> {

		private static RuntimeException exception = new RuntimeException();

		@Override
		public void exec(final ExceptionCasesTestStateMachine stateMachine) {

			throw exception;
		}

		/**
		 * @return the exception
		 */
		static RuntimeException getException() {
			return exception;
		}

	}

	/**
	 * The state that was provided in the
	 * {@link ch.bbv.fsm.events.StateMachineEventHandler#onExceptionThrown(ExceptionEvent)}
	 * event.
	 */
	private States recordedStateId;

	/**
	 * The event that was provided in the
	 * {@link ch.bbv.fsm.events.StateMachineEventHandler#onExceptionThrown(ExceptionEvent)}
	 * event.
	 */
	private Events recordedEventId;

	/**
	 * The event arguments that was provided in the
	 * {@link ch.bbv.fsm.events.StateMachineEventHandler#onExceptionThrown(ExceptionEvent)}
	 * event.
	 */
	private Object[] recordedEventArguments;

	/**
	 * The exception that was provided in the
	 * {@link ch.bbv.fsm.events.StateMachineEventHandler#onExceptionThrown(ExceptionEvent)}
	 * event.
	 */
	private Exception recordedException;

	private boolean transitionDeclined;

	private final ThrowExceptionAction throwException = new ThrowExceptionAction();

	public static class ThrowExceptionFunction implements Guard<ExceptionCasesTestStateMachine, States, Events, Object[], Boolean> {

		@Override
		public boolean execute(final ExceptionCasesTestStateMachine stateMachine, final Object... parameter) {
			throw stateMachine.getException();
		}
	}

	/**
	 * Asserts that the correct exception was notified.
	 */
	private void assertException(final States expectedStateId, final Events expectedEventId, final Object[] expectedEventArguments,
			final Exception expectedException) {
		
		Assertions.assertThat(this.recordedStateId).isEqualTo(expectedStateId);
		Assertions.assertThat(this.recordedEventId).isEqualTo(expectedEventId);
		Assertions.assertThat(this.recordedEventArguments).isEqualTo(expectedEventArguments);
		Assertions.assertThat(this.recordedException).isEqualTo(expectedException);
	}

	/**
	 * When a transition throws an exception then the exception is catched and the
	 * {@link ch.bbv.fsm.events.StateMachineEventHandler#onExceptionThrown(ExceptionEvent)}
	 * event is fired. The transition is executed and the state machine is in the
	 * target state.
	 */
	@Test
	public void exceptionThrowingAction() {

		final Object[] eventArguments = new Object[] { 1, 2, "test" };

		final ExceptionCasesTestStateMachineDefinition def = new ExceptionCasesTestStateMachineDefinition("exceptionThrowingAction",
				States.A);

		def.in(States.A).on(Events.B).goTo(States.B).execute(throwException);
		def.addEventHandler(new Handler());
		final StateMachine<States, Events> testee = def.createPassiveStateMachine("testee", States.A);
		testee.start();
		testee.fire(Events.B, eventArguments);

		this.assertException(States.A, Events.B, eventArguments, ThrowExceptionAction.getException());
		
		Assertions.assertThat(testee.getCurrentState()).isEqualTo(States.B);
	}

	/**
	 * When an exception is thrown in an entry action then it is notified and the
	 * state is entered anyway.
	 */
	@Test
	public void exceptionThrowingEntryAction() {

		final Object[] eventArguments = new Object[] { 1, 2, "test" };

		final ExceptionCasesTestStateMachineDefinition def = new ExceptionCasesTestStateMachineDefinition("exceptionThrowingEntryAction",
				States.A);
		def.addEventHandler(new Handler());
		def.in(States.A).on(Events.B).goTo(States.B);

		def.in(States.B).executeOnEntry(throwException);

		final StateMachine<States, Events> testee = def.createPassiveStateMachine("testee", States.A);
		testee.start();
		testee.fire(Events.B, eventArguments);
		
		Assertions.assertThat(testee.getCurrentState()).isEqualTo(States.B);
		Assertions.assertThat(this.recordedException).isEqualTo(ThrowExceptionAction.getException());
	}

	/**
	 * When an exception is thrown in an entry action then it is notified and the
	 * state is entered anyway.
	 */
	@Test
	public void exceptionThrowingExitAction() {

		final Object[] eventArguments = new Object[] { 1, 2, "test" };

		final ExceptionCasesTestStateMachineDefinition def = new ExceptionCasesTestStateMachineDefinition("exceptionThrowingExitAction",
				States.A);
		def.addEventHandler(new Handler());
		def.in(States.A).executeOnExit(throwException).on(Events.B).goTo(States.B);

		final StateMachine<States, Events> testee = def.createPassiveStateMachine("testee", States.A);
		testee.start();
		testee.fire(Events.B, eventArguments);

		Assertions.assertThat(testee.getCurrentState()).isEqualTo(States.B);
		Assertions.assertThat(this.recordedException).isEqualTo(ThrowExceptionAction.getException());
	}

	/**
	 * When a guard throws an exception then it is catched and the
	 * {@link ch.bbv.fsm.events.StateMachineEventHandler#onExceptionThrown(ExceptionEvent)}
	 * event is fired. The transition is not executed and if there is no other
	 * transition then the state machine remains in the same state.
	 */
	@Test
	public void exceptionThrowingGuard() {
		final Object[] eventArguments = new Object[] { 1, 2, "test" };

		final ExceptionCasesTestStateMachineDefinition def = new ExceptionCasesTestStateMachineDefinition("exceptionThrowingGuard",
				States.A);
		def.in(States.A).on(Events.B).goTo(States.B).onlyIf(new ExceptionCasesTest.ThrowExceptionFunction());

		def.addEventHandler(new Handler());

		final ExceptionCasesTestStateMachine testee = def.createPassiveStateMachine("testee", States.A);
		testee.start();
		testee.fire(Events.B, eventArguments);

		this.assertException(States.A, Events.B, eventArguments, testee.getException());
		
		Assertions.assertThat(testee.getCurrentState()).isEqualTo(States.A);
		Assertions.assertThat(this.transitionDeclined).isTrue();
	}
}
