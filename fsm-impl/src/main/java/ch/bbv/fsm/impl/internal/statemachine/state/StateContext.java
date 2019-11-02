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
package ch.bbv.fsm.impl.internal.statemachine.state;

import java.util.ArrayList;
import java.util.List;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.impl.internal.driver.Notifier;
import ch.bbv.fsm.impl.internal.statemachine.StateMachineInterpreter;

/**
 * InternalState Context.
 * 
 * @author Ueli Kurmann
 * 
 * @param <SM> the type of state machine
 * @param <S>   the type of the states
 * @param <E>   the type of the events
 */
public class StateContext<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>> {

	/**
	 * A record of a state exit or entry. Used to log the way taken by transitions
	 * and initialization.
	 */
	public class Record {
		private S stateId;
		private RecordType recordType;

		/**
		 * Creates a new instance.
		 * 
		 * @param stateId    the state id.
		 * @param recordType the record type.
		 */
		public Record(final S stateId, final RecordType recordType) {
			this.stateId = stateId;
			this.recordType = recordType;
		}

		/**
		 * Returns the record type.
		 * 
		 * @return the record type.
		 */
		public RecordType getRecordType() {
			return this.recordType;
		}

		/**
		 * Returns the state id.
		 * 
		 * @return the state id.
		 */
		public S getStateId() {
			return this.stateId;
		}

		@Override
		public String toString() {
			return getRecordType() + " " + getStateId();
		}
	}

	/**
	 * Specifies the type of the record.
	 */
	public enum RecordType {

		/**
		 * A state was entered.
		 */
		Enter,

		/**
		 * A state was exited.
		 */
		Exit
	}

	private final InternalState<SM, S, E> sourceState;

	/**
	 * The exceptions that occurred during performing an operation.
	 */
	private final List<Exception> exceptions;

	/**
	 * The list of records (state exits, entries).
	 */
	private final List<Record> records;

	private final StateMachineInterpreter<SM, S, E> stateMachineInterpreter;

	private final Notifier<SM, S, E> notifier;

	private final SM stateMachine;

	/**
	 * Creates a new instance.
	 * 
	 * @param stateMachine     the custom state machine
	 * @param sourceState      the source state of the transition.
	 * @param stateMachineImpl the state machine
	 * @param notifier         the notifier
	 */
	public StateContext(final SM stateMachine, final InternalState<SM, S, E> sourceState,
			final StateMachineInterpreter<SM, S, E> stateMachineImpl, final Notifier<SM, S, E> notifier) {
		this.sourceState = sourceState;
		this.stateMachineInterpreter = stateMachineImpl;
		this.notifier = notifier;
		this.stateMachine = stateMachine;
		this.exceptions = new ArrayList<>();
		this.records = new ArrayList<>();
	}

	/**
	 * Adds a record.
	 * 
	 * @param stateId    the state id.
	 * @param recordType the record type.
	 */
	public void addRecord(final S stateId, final RecordType recordType) {
		this.records.add(new Record(stateId, recordType));
	}

	/**
	 * Returns the occured exceptions during the transition.
	 * 
	 * @return the occured exceptions during the transition.
	 */
	public List<Exception> getExceptions() {
		return this.exceptions;
	}

	/**
	 * Returns all records in string representation.
	 * 
	 * @return all records in string representation.
	 */
	public String getRecords() {
		final StringBuilder result = new StringBuilder();

		for (final Record record : this.records) {
			result.append(String.format(" -> %s", record));
		}

		return result.toString();
	}

	/**
	 * Returns the source state of the transition.
	 * 
	 * @return the source state of the transition.
	 */
	public InternalState<SM, S, E> getState() {
		return this.sourceState;
	}

	/**
	 * Returns the state machine's implementation.
	 */
	public StateMachineInterpreter<SM, S, E> getStateMachineInterpreter() {
		return stateMachineInterpreter;
	}

	/**
	 * Returns the notifier.
	 */
	public Notifier<SM, S, E> getNotifier() {
		return notifier;
	}

	/**
	 * Returns the last active sub state for the given composite state.
	 * 
	 * @param superState the super state
	 */
	public InternalState<SM, S, E> getLastActiveSubState(final InternalState<SM, S, E> superState) {
		InternalState<SM, S, E> result = null;
		if (superState != null) {
			result = stateMachineInterpreter.getLastActiveSubState(superState);
			if (result == null) {
				result = superState.getInitialState();
			}
		}
		return result;
	}

	/**
	 * Sets the last active sub state for the given composite state.
	 * 
	 * @param superState the super state
	 * @param subState   the last active sub state
	 */
	public void setLastActiveSubState(final InternalState<SM, S, E> superState, final InternalState<SM, S, E> subState) {
		stateMachineInterpreter.setLastActiveSubState(superState, subState);
	}

	/**
	 * Returns the custom's state machine.
	 */
	public SM getStateMachine() {
		return stateMachine;
	}

}
