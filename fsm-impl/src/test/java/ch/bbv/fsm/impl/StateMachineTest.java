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

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.bbv.fsm.HistoryType;
import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.impl.StatesAndEvents.Events;
import ch.bbv.fsm.impl.StatesAndEvents.States;

/**
 * Tests state machine initialization and state switching.
 */
public class StateMachineTest {

	private class StateMachineTestStateMachine extends AbstractStateMachine<StateMachineTestStateMachine, States, Events> {

		private final List<Record> records = new ArrayList<>();

		StateMachineTestStateMachine(final StateMachine<States, Events> driver) {
			super(driver);
		}

		void checkNoRemainingRecords() {
			if (this.records.size() == 0) {
				return;
			}

			final StringBuilder sb = new StringBuilder("there are additional records:");
			for (final Record record : this.records) {
				sb.append(NEWLINE);
				sb.append(record.getClass().getName()).append('-').append(record.getState());
			}

			Assertions.fail(sb.toString());
		}

		/**
		 * Checks that the first record in the list of records is of type T and involves
		 * the specified state.
		 */
		void checkRecords(final States state, final Class<?> c) {
			final Record record = this.records.get(0);

			Assertions.assertThat(record).as("expected record missing: %s on %s.", c.getName(), state).isNotNull();
			Assertions.assertThat(record.getClass()).as(record.getMessage()).isSameAs(c);

			this.records.remove(0);
		}

		void addEntryRecord(final States s) {
			records.add(new EntryRecord(s));
		}

		void addExitRecord(final States s) {
			records.add(new ExitRecord(s));
		}

		/**
		 * Clears the records.
		 */
		void clearRecords() {
			this.records.clear();
		}

	}

	private class StateMachineTestStateMachineDefinition
			extends AbstractStateMachineDefinition<StateMachineTestStateMachine, States, Events> {

		StateMachineTestStateMachineDefinition() {
			super(States.A);
		}

		@Override
		protected StateMachineTestStateMachine createStateMachine(final StateMachine<States, Events> driver) {
			return new StateMachineTestStateMachine(driver);
		}

	}

	/**
	 * Record of a state entry.
	 */
	public static class EntryRecord extends Record {

		public EntryRecord(final States state) {
			setState(state);
		}

		/**
		 * Gets the message.
		 */
		@Override
		public String getMessage() {
			return "InternalState " + this.getState() + " not entered.";
		}
	}

	/**
	 * Record of a state exit.
	 */
	public static class ExitRecord extends Record {
		public ExitRecord(final States s) {
			setState(s);
		}

		/**
		 * Gets the message.
		 */
		@Override
		public String getMessage() {
			return "InternalState " + this.getState() + " not exited.";
		}
	}

	/**
	 * A record of something that happened.
	 */
	public abstract static class Record {
		private States state;

		/**
		 * Gets the message.
		 */
		public abstract String getMessage();

		/**
		 * Gets or sets the state.
		 */
		public States getState() {
			return this.state;
		}

		public void setState(final States state) {
			this.state = state;
		}
	}

	private static final Object NEWLINE = System.getProperty("line.separator");

	/**
	 * The list of recorded actions.
	 */
	// private List<Record> records;

	private StateMachineTestStateMachineDefinition stateMachineDefinition;

	/**
	 * When a transition between two states at the top level then the exit action of
	 * the source state is executed, then the action is performed and the entry
	 * action of the target state is executed. Finally, the current state is the
	 * target state.
	 */
	@Test
	public void executeTransition() {
		final StateMachineTestStateMachine testee = stateMachineDefinition
				.createPassiveStateMachine("executeTransitionBetweenStatesOnDifferentLevelsDownwards", States.E);
		testee.start();

		testee.clearRecords();

		testee.fire(Events.A);

		Assertions.assertThat(testee.getCurrentState()).isEqualTo(States.A);

		testee.checkRecords(States.E, ExitRecord.class);
		testee.checkRecords(States.A, EntryRecord.class);
		testee.checkNoRemainingRecords();
	}

	/**
	 * When a transition between two states in different super states on different
	 * levels is executed then all states from the source up to the common
	 * super-state are exited and all states down to the target state are entered.
	 * In this case the target state is lower than the source state.
	 */
	@Test
	public void executeTransitionBetweenStatesOnDifferentLevelsDownwards() {
		final StateMachineTestStateMachine testee = stateMachineDefinition
				.createPassiveStateMachine("executeTransitionBetweenStatesOnDifferentLevelsDownwards", States.B2);
		testee.start();

		testee.clearRecords();

		testee.fire(Events.C1b);

		Assertions.assertThat(testee.getCurrentState()).isEqualTo(States.C1b);

		testee.checkRecords(States.B2, ExitRecord.class);
		testee.checkRecords(States.B, ExitRecord.class);
		testee.checkRecords(States.C, EntryRecord.class);
		testee.checkRecords(States.C1, EntryRecord.class);
		testee.checkRecords(States.C1b, EntryRecord.class);
		testee.checkNoRemainingRecords();
	}

	/**
	 * When a transition between two states in different super states on different
	 * levels is executed then all states from the source up to the common
	 * super-state are exited and all states down to the target state are entered.
	 * In this case the target state is higher than the source state.
	 */
	@Test
	public void executeTransitionBetweenStatesOnDifferentLevelsUpwards() {
		final StateMachineTestStateMachine testee = stateMachineDefinition
				.createPassiveStateMachine("executeTransitionBetweenStatesOnDifferentLevelsDownwards", States.D1b);
		testee.start();

		testee.clearRecords();

		testee.fire(Events.B1);

		Assertions.assertThat(testee.getCurrentState()).isEqualTo(States.B1);

		testee.checkRecords(States.D1b, ExitRecord.class);
		testee.checkRecords(States.D1, ExitRecord.class);
		testee.checkRecords(States.D, ExitRecord.class);
		testee.checkRecords(States.B, EntryRecord.class);
		testee.checkRecords(States.B1, EntryRecord.class);
		testee.checkNoRemainingRecords();
	}

	/**
	 * When a transition between two states with the same super state is executed
	 * then the exit action of source state, the transition action and the entry
	 * action of the target state are executed.
	 */
	@Test
	public void executeTransitionBetweenStatesWithSameSuperState() {
		final StateMachineTestStateMachine testee = stateMachineDefinition
				.createPassiveStateMachine("executeTransitionBetweenStatesOnDifferentLevelsDownwards", States.B1);
		testee.start();

		testee.clearRecords();

		testee.fire(Events.B2);

		Assertions.assertThat(testee.getCurrentState()).isEqualTo(States.B2);

		testee.checkRecords(States.B1, ExitRecord.class);
		testee.checkRecords(States.B2, EntryRecord.class);
		testee.checkNoRemainingRecords();
	}

	/**
	 * The state hierarchy is recursively walked up until a state can handle the
	 * event.
	 */
	@Test
	public void executeTransitionHandledBySuperState() {
		final StateMachineTestStateMachine testee = stateMachineDefinition
				.createPassiveStateMachine("executeTransitionBetweenStatesOnDifferentLevelsDownwards", States.C1b);
		testee.start();

		testee.clearRecords();

		testee.fire(Events.A);

		Assertions.assertThat(testee.getCurrentState()).isEqualTo(States.A);

		testee.checkRecords(States.C1b, ExitRecord.class);
		testee.checkRecords(States.C1, ExitRecord.class);
		testee.checkRecords(States.C, ExitRecord.class);
		testee.checkRecords(States.A, EntryRecord.class);
		testee.checkNoRemainingRecords();
	}

	/**
	 * When a transition targets a super-state with {@link HistoryType#DEEP} then
	 * the last active sub-state is entered recursively down to the most nested
	 * state.
	 */
	@Test
	public void executeTransitionWithHistoryTypeDeep() {
		final StateMachineTestStateMachine testee = stateMachineDefinition
				.createPassiveStateMachine("executeTransitionBetweenStatesOnDifferentLevelsDownwards", States.D1b);
		testee.start();
		testee.fire(Events.A);

		testee.clearRecords();

		testee.fire(Events.D);

		Assertions.assertThat(testee.getCurrentState()).isEqualTo(States.D1b);

		testee.checkRecords(States.A, ExitRecord.class);
		testee.checkRecords(States.D, EntryRecord.class);
		testee.checkRecords(States.D1, EntryRecord.class);
		testee.checkRecords(States.D1b, EntryRecord.class);
		testee.checkNoRemainingRecords();
	}

	/**
	 * When a transition targets a super-state with {@link HistoryType#NONE} then
	 * the initial sub-state is entered whatever sub.state was last active.
	 */
	@Test
	public void executeTransitionWithHistoryTypeNone() {
		final StateMachineTestStateMachine testee = stateMachineDefinition
				.createPassiveStateMachine("executeTransitionBetweenStatesOnDifferentLevelsDownwards", States.B2);
		testee.start();
		testee.fire(Events.A);

		testee.clearRecords();

		testee.fire(Events.B);

		testee.checkRecords(States.A, ExitRecord.class);
		testee.checkRecords(States.B, EntryRecord.class);
		testee.checkRecords(States.B1, EntryRecord.class);
		testee.checkNoRemainingRecords();
	}

	/**
	 * When a transition targets a super-state with {@link HistoryType#SHALLOW} then
	 * the last active sub-state is entered and the initial-state of the entered
	 * sub-state is entered (no recursive history).
	 */
	@Test
	public void executeTransitionWithHistoryTypeShallow() {
		final StateMachineTestStateMachine testee = stateMachineDefinition
				.createPassiveStateMachine("executeTransitionBetweenStatesOnDifferentLevelsDownwards", States.C1b);
		testee.start();
		testee.fire(Events.A);

		testee.clearRecords();

		testee.fire(Events.C);

		Assertions.assertThat(testee.getCurrentState()).isEqualTo(States.C1a);

		testee.checkRecords(States.A, ExitRecord.class);
		testee.checkRecords(States.C, EntryRecord.class);
		testee.checkRecords(States.C1, EntryRecord.class);
		testee.checkRecords(States.C1a, EntryRecord.class);
		testee.checkNoRemainingRecords();
	}

	/**
	 * When a transition targets a super-state then the initial-state of this
	 * super-state is entered recursively down to the most nested state. No history
	 * here!
	 */
	@Test
	public void executeTransitionWithInitialSubState() {
		final StateMachineTestStateMachine testee = stateMachineDefinition
				.createPassiveStateMachine("executeTransitionBetweenStatesOnDifferentLevelsDownwards", States.A);
		testee.start();

		testee.clearRecords();

		testee.fire(Events.B);

		Assertions.assertThat(testee.getCurrentState()).isEqualTo(States.B1);

		testee.checkRecords(States.A, ExitRecord.class);
		testee.checkRecords(States.B, EntryRecord.class);
		testee.checkRecords(States.B1, EntryRecord.class);
		testee.checkNoRemainingRecords();
	}

	/**
	 * When the state machine is initializes to a state with sub-states then the
	 * hierarchy is recursively traversed to the most nested state along the chain
	 * of initial states.
	 */
	@Test
	public void initializeStateWithSubStates() {
		final StateMachineTestStateMachine testee = stateMachineDefinition
				.createPassiveStateMachine("executeTransitionBetweenStatesOnDifferentLevelsDownwards", States.D);
		testee.start();

		Assertions.assertThat(testee.getCurrentState()).isEqualTo(States.D1a);

		testee.checkRecords(States.D, EntryRecord.class);
		testee.checkRecords(States.D1, EntryRecord.class);
		testee.checkRecords(States.D1a, EntryRecord.class);
		testee.checkNoRemainingRecords();
	}

	/**
	 * After initialization the state machine is in the initial state and the
	 * initial state is entered. All states up in the hierarchy of the initial state
	 * are entered, too.
	 */
	@Test
	public void initializeToNestedState() {
		final StateMachineTestStateMachine testee = stateMachineDefinition
				.createPassiveStateMachine("executeTransitionBetweenStatesOnDifferentLevelsDownwards", States.D1b);
		testee.start();

		Assertions.assertThat(testee.getCurrentState()).isEqualTo(States.D1b);

		testee.checkRecords(States.D, EntryRecord.class);
		testee.checkRecords(States.D1, EntryRecord.class);
		testee.checkRecords(States.D1b, EntryRecord.class);
		testee.checkNoRemainingRecords();
	}

	/**
	 * After initialization the state machine is in the initial state and the
	 * initial state is entered.
	 */
	@Test
	public void initializeToTopLevelState() {
		final StateMachineTestStateMachine testee = stateMachineDefinition
				.createPassiveStateMachine("executeTransitionBetweenStatesOnDifferentLevelsDownwards", States.A);
		testee.start();

		Assertions.assertThat(testee.getCurrentState()).isEqualTo(States.A);

		testee.checkRecords(States.A, EntryRecord.class);
		testee.checkNoRemainingRecords();
	}

	/**
	 * Internal transitions do not trigger any exit or entry actions.
	 */
	@Test
	public void internalTransition() {
		final StateMachineTestStateMachine testee = stateMachineDefinition
				.createPassiveStateMachine("executeTransitionBetweenStatesOnDifferentLevelsDownwards", States.A);
		testee.start();

		testee.clearRecords();

		testee.fire(Events.A);

		Assertions.assertThat(testee.getCurrentState()).isEqualTo(States.A);
	}

	/**
	 * Initializes a test.
	 */
	@BeforeEach
	public void setUp() {

		// this.records = Lists.newArrayList();

		stateMachineDefinition = new StateMachineTestStateMachineDefinition();

		stateMachineDefinition.defineHierarchyOn(States.B, States.B1, HistoryType.NONE, States.B1, States.B2);
		stateMachineDefinition.defineHierarchyOn(States.C, States.C2, HistoryType.SHALLOW, States.C1, States.C2);
		stateMachineDefinition.defineHierarchyOn(States.C1, States.C1a, HistoryType.SHALLOW, States.C1a, States.C1b);
		stateMachineDefinition.defineHierarchyOn(States.D, States.D1, HistoryType.DEEP, States.D1, States.D2);
		stateMachineDefinition.defineHierarchyOn(States.D1, States.D1a, HistoryType.DEEP, States.D1a, States.D1b);

		stateMachineDefinition.in(States.A).executeOnEntry(sm -> sm.addEntryRecord(States.A))
				.executeOnExit(sm -> sm.addExitRecord(States.A)).on(Events.B).goTo(States.B).on(Events.C).goTo(States.C).on(Events.D)
				.goTo(States.D).on(Events.A);

		stateMachineDefinition.in(States.B).executeOnEntry(sm -> sm.addEntryRecord(States.B))
				.executeOnExit(sm -> sm.addExitRecord(States.B));

		stateMachineDefinition.in(States.B1).executeOnEntry(sm -> sm.addEntryRecord(States.B1))
				.executeOnExit(sm -> sm.addExitRecord(States.B1)).on(Events.B2).goTo(States.B2);

		stateMachineDefinition.in(States.B2).executeOnEntry(sm -> sm.addEntryRecord(States.B2))
				.executeOnExit(sm -> sm.addExitRecord(States.B2)).on(Events.A).goTo(States.A).on(Events.C1b).goTo(States.C1b);

		stateMachineDefinition.in(States.C).executeOnEntry(sm -> sm.addEntryRecord(States.C))
				.executeOnExit(sm -> sm.addExitRecord(States.C)).on(Events.A).goTo(States.A);

		stateMachineDefinition.in(States.C1).executeOnEntry(sm -> sm.addEntryRecord(States.C1))
				.executeOnExit(sm -> sm.addExitRecord(States.C1));

		stateMachineDefinition.in(States.C2).executeOnEntry(sm -> sm.addEntryRecord(States.C2))
				.executeOnExit(sm -> sm.addExitRecord(States.C2));

		stateMachineDefinition.in(States.C1a).executeOnEntry(sm -> sm.addEntryRecord(States.C1a))
				.executeOnExit(sm -> sm.addExitRecord(States.C1a));

		stateMachineDefinition.in(States.C1b).executeOnEntry(sm -> sm.addEntryRecord(States.C1b))
				.executeOnExit(sm -> sm.addExitRecord(States.C1b)).on(Events.A).goTo(States.A);

		stateMachineDefinition.in(States.D).executeOnEntry(sm -> sm.addEntryRecord(States.D))
				.executeOnExit(sm -> sm.addExitRecord(States.D));

		stateMachineDefinition.in(States.D1).executeOnEntry(sm -> sm.addEntryRecord(States.D1))
				.executeOnExit(sm -> sm.addExitRecord(States.D1));

		stateMachineDefinition.in(States.D1a).executeOnEntry(sm -> sm.addEntryRecord(States.D1a))
				.executeOnExit(sm -> sm.addExitRecord(States.D1a));

		stateMachineDefinition.in(States.D1b).executeOnEntry(sm -> sm.addEntryRecord(States.D1b))
				.executeOnExit(sm -> sm.addExitRecord(States.D1b)).on(Events.A).goTo(States.A).on(Events.B1).goTo(States.B1);

		stateMachineDefinition.in(States.E).executeOnEntry(sm -> sm.addEntryRecord(States.E))
				.executeOnExit(sm -> sm.addExitRecord(States.E)).on(Events.A).goTo(States.A);

	}
}
