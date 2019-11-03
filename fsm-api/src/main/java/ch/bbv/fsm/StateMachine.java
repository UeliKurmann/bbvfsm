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
 *     bbv Software Services AG (http://www.bbv.ch)
 *******************************************************************************/
package ch.bbv.fsm;

import ch.bbv.fsm.memento.StateMachineMemento;

/**
 * Interface for all finite state machines.
 * 
 * @param <S>
 *            the state enumeration type.
 * @param <E>
 *            the event enumeration type.
 */
public interface StateMachine<S extends Enum<?>, E extends Enum<?>> {

	/**
	 * The state of this state machine.
	 * 
	 */
	enum LiveCycle {
		Created, Running, Terminated
	}

	/**
	 * Fires the specified event. Events are queued if they can not be processed immediately. 
	 * 
	 * @param eventId
	 *            the event
	 * @param eventArguments
	 *            the event arguments
	 */
	void fire(E eventId, Object... eventArguments);

	/**
	 * Fires the specified priority event. The event will be handled before any
	 * already queued event.
	 * 
	 * @param eventId
	 *            the event.
	 * @param eventArguments
	 *            the event arguments.
	 */
	void firePriority(E eventId, Object... eventArguments);

	/**
	 * Returns the live cycles state of this state machine.
	 */
	LiveCycle getStatus();

	/**
	 * Returns the number of queued events.
	 * 
	 * @return the number of queued events.
	 */
	int numberOfQueuedEvents();

	/**
	 * Returns <code>true</code> if the state machine is running and all events
	 * are processed.
	 */
	boolean isIdle();

	/**
	 * Starts the state machine. Events will be processed. If the state machine
	 * is not started then the events will be queued until the state machine is
	 * started. Already queued events are processed. If there is an entry action
	 * defined on the initial state, this entry action will be executed.
	 */
	void start();

	/**
	 * Terminates the state machine. The state machine can not be used any
	 * longer.
	 */
	void terminate();

	/**
	 * Returns the current state.
	 */
	S getCurrentState();

	/**
	 * Reactivates the state machine with all its states and its history.
	 * Implementor may override this for restoring additional state information.
	 * 
	 * @param stateMachineMemento
	 *            the memento where the state is stored to
	 */
	void activate(StateMachineMemento<S, E> stateMachineMemento);

	/**
	 * Passivates the state machine and store its current state to the
	 * {@link StateMachineMemento}. Implementor may override this for storing
	 * additional state information.
	 * 
	 * @param stateMachineMemento
	 *            the memento where the state is restored from
	 */
	void passivate(StateMachineMemento<S, E> stateMachineMemento);
}
