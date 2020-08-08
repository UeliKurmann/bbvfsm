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
package ch.bbv.fsm.impl.internal.statemachine.transition;

import java.util.ArrayList;
import java.util.List;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.impl.internal.statemachine.state.InternalState;

/**
 * Mapping between a internalState and its transitions.
 *
 * @author Ueli Kurmann
 *
 * @param <SM> the type of internalState machine
 * @param <S>  the type of the states
 * @param <E>  the type of the events
 */
public class TransitionDictionary<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>> {

	/**
	 * The internalState this transitions belong to.
	 */
	private final InternalState<SM, S, E> internalState;

	private final Multimap<E, Transition<SM, S, E>> transitions;

	/**
	 * Creates a new instance.
	 *
	 * @param state the internalState this transitions belong to.
	 */
	public TransitionDictionary(final InternalState<SM, S, E> state) {
		this.internalState = state;
		this.transitions = new Multimap<>();
	}

	/**
	 * Adds a transition to an event.
	 *
	 * @param eventId    the event id
	 * @param transition the transition
	 */
	public void add(final E eventId, final Transition<SM, S, E> transition) {
		transition.setSource(this.internalState);
		this.transitions.putOne(eventId, transition);
	}

	/**
	 * Returns a list of transitions for the given event.
	 *
	 * @param eventId the event id
	 * @return a list of transitions
	 */
	public List<Transition<SM, S, E>> getTransitions(final E eventId) {
		return new ArrayList<>(this.transitions.get(eventId));
	}

}
