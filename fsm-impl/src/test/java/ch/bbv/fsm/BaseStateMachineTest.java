/*******************************************************************************
 *  C
import ch.bbv.fsm.impl.SimpleStateMachineDefinition;
opyright 2010, 2011 bbv Software Services AG, Ueli Kurmann
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

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.bbv.fsm.StateMachine.LiveCycle;
import ch.bbv.fsm.events.ExceptionEvent;
import ch.bbv.fsm.events.StateMachineEventHandlerAdapter;
import ch.bbv.fsm.events.TransitionCompletedEvent;
import ch.bbv.fsm.events.TransitionEvent;
import ch.bbv.fsm.impl.SimpleStateMachine;
import ch.bbv.fsm.impl.SimpleStateMachineDefinition;
import ch.bbv.fsm.impl.StatesAndEvents.Events;
import ch.bbv.fsm.impl.StatesAndEvents.States;

/**
 * Base for state machine test fixtures.
 */
public abstract class BaseStateMachineTest {

	private class Handler extends StateMachineEventHandlerAdapter<SimpleStateMachine<States, Events>, States, Events> {

		@Override
		public void onExceptionThrown(final ExceptionEvent<SimpleStateMachine<States, Events>, States, Events> arg) {
			BaseStateMachineTest.this.exceptions.add(arg.getException());
		}

		@Override
		public void onTransitionBegin(final TransitionEvent<SimpleStateMachine<States, Events>, States, Events> args) {
			BaseStateMachineTest.this.transitionBeginMessages.add(args);
		}

		@Override
		public void onTransitionCompleted(final TransitionCompletedEvent<SimpleStateMachine<States, Events>, States, Events> arg) {
			BaseStateMachineTest.this.transitionCompletedMessages.add(arg);
		}

		@Override
		public void onTransitionDeclined(final TransitionEvent<SimpleStateMachine<States, Events>, States, Events> arg) {
			BaseStateMachineTest.this.transitionDeclinedMessages.add(arg);
		}

	}

	/**
	 * The state machine under test.
	 */
	private SimpleStateMachine<States, Events> testee;

	/**
	 * Gets the exceptions that were notified.
	 */
	private List<Exception> exceptions;

	/**
	 * Gets the begin transition messages that were notified.
	 */
	private List<TransitionEvent<SimpleStateMachine<States, Events>, States, Events>> transitionBeginMessages;

	/**
	 * Gets the transition completed messages that were notified.
	 */
	private List<TransitionCompletedEvent<SimpleStateMachine<States, Events>, States, Events>> transitionCompletedMessages;

	/**
	 * Gets the transition declined messages that were notified.
	 */
	private List<TransitionEvent<SimpleStateMachine<States, Events>, States, Events>> transitionDeclinedMessages;

	/**
	 * Checks the begin transition message.
	 */
	protected void checkBeginTransitionMessage(final States origin, final Events eventId, final Object[] eventArguments) {
		Assertions.assertThat(this.transitionBeginMessages)//
				.as("wrong number of begin transition messages.").hasSize(1);
		Assertions.assertThat(this.transitionBeginMessages.get(0).getStateId())//
				.as("wrong state in transition begin message.").isEqualTo(origin);
		Assertions.assertThat(this.transitionBeginMessages.get(0).getEventId())//
				.as("wrong event in transition begin message.").isEqualTo(eventId);
		Assertions.assertThat(this.transitionBeginMessages.get(0).getEventArguments())//
				.as("wrong event arguments in transition begin message.").isEqualTo(eventArguments);
	}

	/**
	 * Checks the no declined transition message occurred.
	 */
	protected void checkNoDeclinedTransitionMessage() {
		Assertions.assertThat(this.transitionDeclinedMessages).isEmpty();
	}

	/**
	 * Checks the no exception message occurred.
	 */
	protected void checkNoExceptionMessage() {
		Assertions.assertThat(this.exceptions).isEmpty();
	}

	/**
	 * Checks the transition completed message.
	 */
	protected void checkTransitionCompletedMessage(final Object[] eventArguments, final States origin, final Events eventId,
			final States newState) {
		
		Assertions.assertThat(this.transitionCompletedMessages).hasSize(1);
		
		Assertions.assertThat(this.transitionCompletedMessages.get(0).getStateId()).isEqualTo(origin);
		Assertions.assertThat(this.transitionCompletedMessages.get(0).getEventId()).isEqualTo(eventId);
		if (eventArguments != null) {
			Assertions.assertThat(this.transitionCompletedMessages.get(0).getEventArguments()).isEqualTo(eventArguments);
		}

		Assertions.assertThat(this.transitionCompletedMessages.get(0).getNewStateId()).isEqualTo(newState);
	}

	protected abstract SimpleStateMachine<States, Events> createTestee(SimpleStateMachineDefinition<States, Events> definition,
			States initialState);

	protected void initTestee(final SimpleStateMachineDefinition<States, Events> definition) {
		definition.addEventHandler(new Handler());
		testee = createTestee(definition, States.A);
		testee.start();
	}

	/**
	 * An event can be fired onto the state machine and all notifications are
	 * signaled.
	 */
	@Test
	public void fireEvent() {
		final SimpleStateMachineDefinition<States, Events> definition = new SimpleStateMachineDefinition<>(States.A);

		definition.defineHierarchyOn(States.B, States.B1, HistoryType.NONE, States.B1, States.B2);
		definition.defineHierarchyOn(States.C, States.C2, HistoryType.SHALLOW, States.C1, States.C2);
		definition.defineHierarchyOn(States.C1, States.C1a, HistoryType.SHALLOW, States.C1a, States.C1b);
		definition.defineHierarchyOn(States.D, States.D1, HistoryType.DEEP, States.D1, States.D2);
		definition.defineHierarchyOn(States.D1, States.D1a, HistoryType.DEEP, States.D1a, States.D1b);

		definition.in(States.A).on(Events.B).goTo(States.B);

		final Object[] eventArguments = new Object[] { 1, 2, "test" };

		initTestee(definition);

		testee.fire(Events.B, eventArguments[0], eventArguments[1], eventArguments[2]);

		waitUntilAllEventsAreProcessed();

		this.checkBeginTransitionMessage(States.A, Events.B, eventArguments);
		this.checkTransitionCompletedMessage(eventArguments, States.A, Events.B, States.B1);
		this.checkNoExceptionMessage();
		this.checkNoDeclinedTransitionMessage();
	}

	/**
	 * With FirePriority, an event can be added to the front of the queued events.
	 */
	@Test
	public void priorityFire() {
		final int transitions = 3;

		final SimpleStateMachineDefinition<States, Events> definition = new SimpleStateMachineDefinition<>(States.A);

		definition.in(States.A).on(Events.B).goTo(States.B).execute((fsm) -> {
			fsm.fire(Events.D);
			fsm.firePriority(Events.C);
		});

		definition.in(States.B).on(Events.C).goTo(States.C);

		definition.in(States.C).on(Events.D).goTo(States.D);

		initTestee(definition);

		testee.fire(Events.B);

		waitUntilAllEventsAreProcessed();

		Assertions.assertThat(this.transitionCompletedMessages).hasSize(transitions);
		this.checkNoDeclinedTransitionMessage();
		this.checkNoExceptionMessage();
	}

	@Test
	public void startTwice() {
		final SimpleStateMachineDefinition<States, Events> definition = new SimpleStateMachineDefinition<>(States.A);
		initTestee(definition);
		Assertions.assertThatIllegalStateException().isThrownBy(()->this.testee.start());
	}

	/**
	 * When the state machine is stopped then no events are processed. All events
	 * enqueued are processed when state machine is started.
	 */
	@Test
	public void stopAndStart() {

		final SimpleStateMachineDefinition<States, Events> definition = new SimpleStateMachineDefinition<>(States.A);

		definition.in(States.A).on(Events.B).goTo(States.B);

		definition.in(States.B).on(Events.C).goTo(States.C);

		initTestee(definition);

		this.testee.terminate();

		Assertions.assertThat(this.testee.getStatus()).isNotEqualTo(LiveCycle.Running);

		this.testee.fire(Events.B);
		this.testee.fire(Events.C);

		Assertions.assertThat(this.transitionBeginMessages).isEmpty();
		Assertions.assertThatIllegalStateException().isThrownBy(() -> this.testee.start());
	}

	/**
	 * Initializes a test.
	 */
	@BeforeEach
	public void setup() {
		this.exceptions = new ArrayList<>();
		this.transitionBeginMessages = new ArrayList<>();
		this.transitionCompletedMessages = new ArrayList<>();
		this.transitionDeclinedMessages = new ArrayList<>();
	}

	/**
	 * Tears down a test.
	 */
	@AfterEach
	public void tearDown() {
		this.testee.terminate();
	}

	private void waitUntilAllEventsAreProcessed() {
		while (!this.testee.isIdle()) {
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
