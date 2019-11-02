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
package ch.bbv.fsm.impl.internal.statemachine.state;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bbv.fsm.HistoryType;
import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.impl.internal.action.FsmCall;
import ch.bbv.fsm.impl.internal.statemachine.state.StateContext.RecordType;
import ch.bbv.fsm.impl.internal.statemachine.transition.Transition;
import ch.bbv.fsm.impl.internal.statemachine.transition.TransitionContext;
import ch.bbv.fsm.impl.internal.statemachine.transition.TransitionDictionary;
import ch.bbv.fsm.impl.internal.statemachine.transition.TransitionResult;
import ch.bbv.fsm.model.State;

/**
 * Implementation of the state.
 *
 * @author Ueli Kurmann
 *
 * @param <SM> the type of state machine
 * @param <S>  the type of the states
 * @param <E>  the type of the events
 */
public class InternalState<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>> implements State<SM, S, E> {

	private static final Logger LOG = LoggerFactory.getLogger(InternalState.class);

	/**
	 * The level of this state within the state hierarchy [1..maxLevel].
	 */
	private int level;

	private final List<State<SM, S, E>> subStates;

	/**
	 * The super-state of this state. Null for states with <code>level</code> equal
	 * to 1.
	 */
	private InternalState<SM, S, E> superState;

	/**
	 * Collection of transitions that start in this .
	 * (Transition<TState,TEvent>.getSource() is equal to this state)
	 */
	private final TransitionDictionary<SM, S, E> transitions;

	/**
	 * The initial sub-state of this state.
	 */
	private InternalState<SM, S, E> initialState;

	/**
	 * The HistoryType of this state.
	 */
	private HistoryType historyType = HistoryType.NONE;

	/**
	 * The unique state id.
	 */
	private final S id;

	/**
	 * The entry action.
	 */
	private FsmCall<SM, S, E> entryAction;

	/**
	 * The exit action.
	 */
	private FsmCall<SM, S, E> exitAction;

	/**
	 * Initializes a new instance of the state.
	 *
	 * @param id the unique id of the state.
	 */
	public InternalState(final S id) {
		this.id = id;
		this.level = 1;

		this.subStates = new ArrayList<>();
		this.transitions = new TransitionDictionary<>(this);
	}

	/**
	 * Adds a sub state.
	 *
	 * @param state a sub state.
	 */
	public void addSubState(final InternalState<SM, S, E> state) {
		this.subStates.add(state);

	}

	/**
	 * Throws an exception if the new initial state is not a sub-state of this
	 * instance.
	 *
	 * @param value
	 */
	private void checkInitialStateIsASubState(final InternalState<SM, S, E> value) {
		if (value.getSuperState() != this) {
			throw new IllegalArgumentException(String.format(
					"InternalState {0} cannot be the initial state of super state {1} because it is not a direct sub-state.", value, this));
		}
	}

	/**
	 * Throws an exception if the new initial state is this instance.
	 *
	 * @param newInitialState the new initial state.
	 */
	private void checkInitialStateIsNotThisInstance(final InternalState<SM, S, E> newInitialState) {
		if (this == newInitialState) {
			throw new IllegalArgumentException(String.format("InternalState {0} cannot be the initial sub-state to itself.", this));
		}
	}

	/**
	 * Throws an exception if the new super state is this instance.
	 *
	 * @param newSuperState the super state.
	 */
	private void checkSuperStateIsNotThisInstance(final InternalState<SM, S, E> newSuperState) {
		if (this == newSuperState) {
			throw new IllegalArgumentException(String.format("State {0} cannot be its own super-state.", this));
		}
	}

	/**
	 * Enters this state by its history depending on its <code>HistoryType</code>.
	 * The <code>Entry</code> method has to be called already.
	 *
	 * @param stateContext the state context.
	 * @return the active state. (depends on this states <code>HistoryType</code>)
	 */
	public InternalState<SM, S, E> enterByHistory(final StateContext<SM, S, E> stateContext) {

		InternalState<SM, S, E> result = this;

		switch (this.historyType) {
		case NONE:
			result = enterHistoryNone(stateContext);
			break;
		case SHALLOW:
			result = enterHistoryShallow(stateContext);
			break;
		case DEEP:
			result = enterHistoryDeep(stateContext);
			break;
		default:
			throw new IllegalArgumentException("Unknown HistoryType : " + historyType);
		}

		return result;
	}

	/**
	 * Enters this state is deep mode: mode if there is one.
	 *
	 * @param stateContext the event context.
	 * @return the active state.
	 */
	public InternalState<SM, S, E> enterDeep(final StateContext<SM, S, E> stateContext) {
		this.entry(stateContext);
		final InternalState<SM, S, E> lastActiveState = stateContext.getLastActiveSubState(this);
		return lastActiveState == null ? this : lastActiveState.enterDeep(stateContext);
	}

	/**
	 * Enters this instance with history type = deep.
	 *
	 * @param stateContext the state context.
	 * @return the state
	 */
	private InternalState<SM, S, E> enterHistoryDeep(final StateContext<SM, S, E> stateContext) {
		final InternalState<SM, S, E> lastActiveState = stateContext.getLastActiveSubState(this);
		return lastActiveState != null ? lastActiveState.enterDeep(stateContext) : this;
	}

	/**
	 * Enters with history type = none.
	 *
	 * @param stateContext state context
	 * @return the entered state.
	 */
	private InternalState<SM, S, E> enterHistoryNone(final StateContext<SM, S, E> stateContext) {
		return this.initialState != null ? this.getInitialState().enterShallow(stateContext) : this;
	}

	/**
	 * Enters this instance with history type = shallow.
	 *
	 * @param stateContext state context
	 * @return the entered state
	 */
	private InternalState<SM, S, E> enterHistoryShallow(final StateContext<SM, S, E> stateContext) {
		final InternalState<SM, S, E> lastActiveState = stateContext.getLastActiveSubState(this);
		return lastActiveState != null ? lastActiveState.enterShallow(stateContext) : this;
	}

	/**
	 * Enters this state is shallow mode: The entry action is executed and the
	 * initial state is entered in shallow mode if there is one.
	 *
	 * @param stateContext the event context.
	 * @return the active state.
	 */

	public InternalState<SM, S, E> enterShallow(final StateContext<SM, S, E> stateContext) {
		this.entry(stateContext);
		return this.initialState == null ? this : this.initialState.enterShallow(stateContext);
	}

	/**
	 * Enters this state.
	 *
	 * @param stateContext the state context.
	 */

	public void entry(final StateContext<SM, S, E> stateContext) {
		stateContext.addRecord(this.getId(), RecordType.Enter);
		if (this.entryAction != null) {
			try {
				LOG.debug("Execute EntryAction: {}",this.entryAction);
				this.entryAction.execOn(stateContext.getStateMachine());
			} catch (final Exception e) {
				handleException(e, stateContext);
			}
		}

	}

	/**
	 * Exits this state.
	 *
	 * @param stateContext the state context.
	 */

	public void exit(final StateContext<SM, S, E> stateContext) {
		stateContext.addRecord(this.getId(), StateContext.RecordType.Exit);
		if (this.exitAction != null) {
			try {
				LOG.debug("Execute ExitAction: {}",this.exitAction);
				this.exitAction.execOn(stateContext.getStateMachine());
			} catch (final Exception e) {
				handleException(e, stateContext);
			}
		}
		this.setThisStateAsLastStateOfSuperState(stateContext);
	}

	private void setThisStateAsLastStateOfSuperState(final StateContext<SM, S, E> stateContext) {
		if (superState != null && !HistoryType.NONE.equals(superState.getHistoryType())) {
			stateContext.setLastActiveSubState(superState, this);
		}
	}

	/**
	 * Fires the specified event id on this state.
	 *
	 * @param context the event context.
	 * @return the result of the transition.
	 */

	public TransitionResult<SM, S, E> fire(final TransitionContext<SM, S, E> context) {
		@SuppressWarnings("unchecked")
		TransitionResult<SM, S, E> result = TransitionResult.getNotFired();

		final List<Transition<SM, S, E>> transitionsForEvent = this.transitions.getTransitions(context.getEventId());
		if (transitionsForEvent != null) {
			for (final Transition<SM, S, E> transition : transitionsForEvent) {
				result = transition.fire(context);
				if (result.isFired()) {
					return result;
				}
			}
		}
		LOG.info("No transition available in this state ({}).", this.getId());

		if (this.getSuperState() != null) {
			LOG.info("Fire the same event on the super state.");
			result = this.getSuperState().fire(context);
		}

		return result;
	}

	/**
	 * Returns the entry action.
	 *
	 * @return the entry action.
	 */

	public FsmCall<SM, S, E> getEntryAction() {
		return this.entryAction;
	}

	/**
	 * Gets the exit action.
	 *
	 * @return the exit action.
	 */

	public FsmCall<SM, S, E> getExitAction() {
		return this.exitAction;
	}

	/**
	 * Returns the history type of this state.
	 *
	 * @return the history type of this state.
	 */

	public HistoryType getHistoryType() {
		return this.historyType;
	}

	public S getId() {
		return this.id;
	}

	/**
	 * Returns the initial sub-state.
	 *
	 * @return the initial sub-state or Null if this state has no sub-states.
	 */

	public InternalState<SM, S, E> getInitialState() {
		return this.initialState;
	}

	public int getLevel() {
		return this.level;
	}

	/**
	 * Returns the sub-states.
	 *
	 * @return the sub-states.
	 */

	public List<State<SM, S, E>> getSubStates() {
		return new ArrayList<>(this.subStates);
	}

	/**
	 * Returns the super-state. Null if this is a root state.
	 *
	 * @return the super-state.
	 */

	public InternalState<SM, S, E> getSuperState() {
		return this.superState;
	}

	/**
	 * Returns the transitions.
	 *
	 * @return the transitions.
	 */

	public TransitionDictionary<SM, S, E> getTransitions() {
		return this.transitions;
	}

	/**
	 * Handles the specific exception.
	 *
	 * @param exception    the exception
	 * @param stateContext the state context.
	 */
	private void handleException(final Exception exception, final StateContext<SM, S, E> stateContext) {
		stateContext.getExceptions().add(exception);
		stateContext.getNotifier().onExceptionThrown(stateContext, exception);
	}

	/**
	 * Sets the entry action.
	 *
	 * @param <T>
	 * @param action the entry action.
	 */

	public void setEntryAction(final FsmCall<SM, S, E> action) {
		this.entryAction = action;

	}

	/**
	 * Set the exit action.
	 * 
	 * @param action the action
	 */

	public void setExitAction(final FsmCall<SM, S, E> action) {
		this.exitAction = action;

	}

	/**
	 * Sets the history type of this state.
	 *
	 * @param historyType the history type of this state.
	 */

	public void setHistoryType(final HistoryType historyType) {
		this.historyType = historyType;

	}

	/**
	 * Sets the initial level depending on the level of the super state of this
	 * instance.
	 */
	private void setInitialLevel() {
		this.setLevel(this.superState != null ? this.superState.getLevel() + 1 : 1);
	}

	/**
	 * Sets the initial sub-state.
	 *
	 * @param initialState the initial sub-state.
	 */

	public void setInitialState(final InternalState<SM, S, E> initialState) {
		this.checkInitialStateIsNotThisInstance(initialState);
		this.checkInitialStateIsASubState(initialState);
		this.initialState = initialState;
	}

	public void setLevel(final int level) {
		this.level = level;
		this.setLevelOfSubStates();
	}

	/**
	 * Sets the level of all sub states.
	 */
	private void setLevelOfSubStates() {
		for (final State<SM, S, E> state : this.getSubStates()) {
			state.setLevel(this.level + 1);
		}
	}

	/**
	 * Sets the super-state.
	 *
	 * @param superState the super-state.
	 */

	public void setSuperState(final InternalState<SM, S, E> superState) {
		this.checkSuperStateIsNotThisInstance(superState);
		this.superState = superState;
		this.setInitialLevel();

	}

	public String toString() {
		return this.id.toString();
	}

	public boolean hasParent() {
		return this.superState != null;
	}

	public State<SM, S, E> getParent() {
		return this.superState;
	}

	public boolean hasChildren() {
		return this.subStates != null && !this.subStates.isEmpty();
	}

	public List<State<SM, S, E>> getChildren() {
		return this.getSubStates();
	}
}
