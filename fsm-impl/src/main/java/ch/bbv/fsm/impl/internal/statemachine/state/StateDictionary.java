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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ch.bbv.fsm.StateMachine;

/**
 * The mapping between state id's and the corresponding state instance.
 * 
 * @author Ueli Kurmann
 * 
 * @param <SM> the type of state machine
 * @param <S>        the type of the states
 * @param <E>        the type of the events
 */
public class StateDictionary<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>> {

	private final ConcurrentMap<S, InternalState<SM, S, E>> dictionary;

	/**
	 * Creates a new instance of the state dictionary.
	 */
	public StateDictionary() {
		this.dictionary = new ConcurrentHashMap<>();
	}

	/**
	 * Returns the state instance by it's id.
	 * 
	 * @param stateId the state id.
	 * @return the state instance.
	 */
	public InternalState<SM, S, E> getState(final S stateId) {
		if (!this.dictionary.containsKey(stateId)) {
			this.dictionary.putIfAbsent(stateId, new InternalState<>(stateId));
		}

		return this.dictionary.get(stateId);
	}

}
