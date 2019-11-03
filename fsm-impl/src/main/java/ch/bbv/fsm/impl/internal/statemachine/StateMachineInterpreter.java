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
package ch.bbv.fsm.impl.internal.statemachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.events.StateMachineEventHandler;
import ch.bbv.fsm.impl.internal.driver.Notifier;
import ch.bbv.fsm.impl.internal.statemachine.events.ExceptionEventImpl;
import ch.bbv.fsm.impl.internal.statemachine.events.TransitionCompletedEventImpl;
import ch.bbv.fsm.impl.internal.statemachine.events.TransitionEventImpl;
import ch.bbv.fsm.impl.internal.statemachine.events.TransitionExceptionEventImpl;
import ch.bbv.fsm.impl.internal.statemachine.state.InternalState;
import ch.bbv.fsm.impl.internal.statemachine.state.StateContext;
import ch.bbv.fsm.impl.internal.statemachine.state.StateDictionary;
import ch.bbv.fsm.impl.internal.statemachine.transition.TransitionContext;
import ch.bbv.fsm.impl.internal.statemachine.transition.TransitionResult;
import ch.bbv.fsm.memento.StateMachineMemento;

/**
 * InternalStateImpl Machine Implementation.
 * 
 * @author Ueli Kurmann
 * 
 * @param <TStateMachine> the type of state machine
 * @param <S>        the type of the states
 * @param <E>        the type of the events
 */
public class StateMachineInterpreter<TStateMachine extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>>
		implements Notifier<TStateMachine, S, E> {

	private static final Logger LOG = LoggerFactory.getLogger(StateMachineInterpreter.class);

	/**
	 * Name of this state machine used in log messages.
	 */
	private final String name;

	private final TStateMachine stateMachine;

	/**
	 * The current state.
	 */
	private InternalState<TStateMachine, S, E> currentState;

	private final Map<InternalState<TStateMachine, S, E>, InternalState<TStateMachine, S, E>> superToSubState = new HashMap<>();

	private final S initialStateId;

	/**
	 * The dictionary of all states.
	 */
	private final StateDictionary<TStateMachine, S, E> states;

	private final List<StateMachineEventHandler<TStateMachine, S, E>> eventHandler;

	/**
	 * Initializes a new instance of the StateMachineImpl<TState,TEvent> class.
	 * 
	 * @param stateMachine the custom's state machine
	 * @param name         The name of this state machine used in log messages.
	 * @param states       the states
	 * @param initialState the initial state
	 */
	public StateMachineInterpreter(final TStateMachine stateMachine, final String name,
			final StateDictionary<TStateMachine, S, E> states, final S initialState) {
		this.name = name;
		this.states = states;
		this.stateMachine = stateMachine;
		this.initialStateId = initialState;
		this.eventHandler = new ArrayList<>();
	}

	/**
	 * Fires the specified event.
	 * 
	 * @param eventId the event id.
	 */
	public void fire(final E eventId) {
		this.fire(eventId, null);
	}

	/**
	 * Fires the specified event.
	 * 
	 * @param eventId        the event id.
	 * @param eventArguments the event arguments.
	 */
	public void fire(final E eventId, final Object[] eventArguments) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Fire event {} on state machine {} with current state {} and event arguments {}.",
					new Object[] { eventId, this.getName(), this.getCurrentStateId(), eventArguments });
		}

		final TransitionContext<TStateMachine, S, E> context = new TransitionContext<>(stateMachine, getCurrentState(), eventId,
				eventArguments, this, this);
		final TransitionResult<TStateMachine, S, E> result = this.currentState.fire(context);

		if (!result.isFired()) {
			LOG.warn("No transition possible. Current state: {}, fired event: {}", getCurrentStateId(), eventArguments);
			this.onTransitionDeclined(context);
			return;
		}

		this.setCurrentState(result.getNewState());

		LOG.debug("Statemachine \"{}\" performed {}.", this, context.getRecords());

		this.onTransitionCompleted(context);
	}

	/**
	 * Returns the current state.
	 * 
	 * @return the current state.
	 */
	private InternalState<TStateMachine, S, E> getCurrentState() {
		return this.currentState;
	}

	/**
	 * Gets the id of the current state.
	 * 
	 * @return The id of the current state.
	 */
	public S getCurrentStateId() {
		if (this.getCurrentState() != null) {
			return this.getCurrentState().getId();
		} else {
			return null;
		}
	}

	/**
	 * Gets the name of this instance.
	 * 
	 * @return The name of this instance.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Initializes the state machine by setting the specified initial state.
	 * 
	 * @param initialState the initial state
	 * @param stateContext the state context
	 */
	private void initialize(final InternalState<TStateMachine, S, E> initialState,
			final StateContext<TStateMachine, S, E> stateContext) {
		if (initialState == null) {
			throw new IllegalArgumentException("initialState; The initial state must not be null.");
		}

		final StateMachineInitializer<TStateMachine, S, E> initializer = new StateMachineInitializer<>(initialState,
				stateContext);
		this.setCurrentState(initializer.enterInitialState());
	}

	/**
	 * Initializes the state machine.
	 */
	public void initialize() {
		LOG.info("Statemachine \"{}\" initializes to state {}.", this, initialStateId);
		final StateContext<TStateMachine, S, E> stateContext = new StateContext<>(stateMachine, null, this, this);
		this.initialize(this.states.getState(initialStateId), stateContext);
		LOG.info("Statemachine \"{}\" performed {}.", this, stateContext.getRecords());
	}

	/**
	 * Terminates the state machine.
	 */
	public void terminate() {
		final StateContext<TStateMachine, S, E> stateContext = new StateContext<>(stateMachine, null, this, this);
		InternalState<TStateMachine, S, E> o = getCurrentState();
		while (o != null) {
			o.exit(stateContext);
			o = o.getSuperState();
		}
	}

	/**
	 * Adds an event handler.
	 * 
	 * @param handler the event handler.
	 */
	public void addEventHandler(final StateMachineEventHandler<TStateMachine, S, E> handler) {
		this.eventHandler.add(handler);
	}

	@Override
	public void onExceptionThrown(final StateContext<TStateMachine, S, E> stateContext, final Exception exception) {
		for (final StateMachineEventHandler<TStateMachine, S, E> handler : this.eventHandler) {
			handler.onExceptionThrown(new ExceptionEventImpl<>(stateContext, exception));
		}
	}

	@Override
	public void onExceptionThrown(final TransitionContext<TStateMachine, S, E> transitionContext, final Exception exception) {
		for (final StateMachineEventHandler<TStateMachine, S, E> handler : this.eventHandler) {
			handler.onTransitionThrowsException(new TransitionExceptionEventImpl<>(transitionContext, exception));
		}
	}

	@Override
	public void onTransitionBegin(final StateContext<TStateMachine, S, E> transitionContext) {
		try {
			for (final StateMachineEventHandler<TStateMachine, S, E> handler : this.eventHandler) {
				handler.onTransitionBegin(new TransitionEventImpl<>(transitionContext));
			}
		} catch (final Exception e) {
			onExceptionThrown(transitionContext, e);
		}
	}

	/**
	 * Fires a transition completed event.
	 * 
	 * @param transitionContext the transition context
	 */
	protected void onTransitionCompleted(final StateContext<TStateMachine, S, E> transitionContext) {
		try {
			for (final StateMachineEventHandler<TStateMachine, S, E> handler : this.eventHandler) {
				handler.onTransitionCompleted(new TransitionCompletedEventImpl<>(this.getCurrentStateId(), transitionContext));
			}
		} catch (final Exception e) {
			onExceptionThrown(transitionContext, e);
		}
	}

	/**
	 * Fires the transaction declined event.
	 * 
	 * @param transitionContext the transition context.
	 */
	protected void onTransitionDeclined(final StateContext<TStateMachine, S, E> transitionContext) {
		try {
			for (final StateMachineEventHandler<TStateMachine, S, E> handler : this.eventHandler) {
				handler.onTransitionDeclined(new TransitionEventImpl<>(transitionContext));
			}
		} catch (final Exception e) {
			onExceptionThrown(transitionContext, e);
		}

	}

	/**
	 * Sets the current state.
	 * 
	 * @param state the current state.
	 */
	private void setCurrentState(final InternalState<TStateMachine, S, E> state) {
		LOG.info("Statemachine \"{}\" switched to state {}.", this.getName(), state.getId());
		this.currentState = state;
	}

	/**
	 * Returns the last active substate for the given composite state.
	 * 
	 * @param superState the super state
	 */
	public InternalState<TStateMachine, S, E> getLastActiveSubState(
			final InternalState<TStateMachine, S, E> superState) {
		return superToSubState.get(superState);
	}

	/**
	 * Sets the last active sub state for the given composite state.
	 * 
	 * @param superState the super state
	 * @param subState   the last active sub state
	 */
	public void setLastActiveSubState(final InternalState<TStateMachine, S, E> superState,
			final InternalState<TStateMachine, S, E> subState) {
		superToSubState.put(superState, subState);
	}

	@Override
	public String toString() {
		return this.name;
	}

	/**
	 * Activates the interpreter.
	 * 
	 * @param memento the memento to use
	 */
	public void activate(final StateMachineMemento<S, E> memento) {
		currentState = states.getState(memento.getCurrentState());
		for (final Map.Entry<S, S> e : memento.getSavedHistoryStates().entrySet()) {
			superToSubState.put(states.getState(e.getKey()), states.getState(e.getValue()));
		}
	}

	/**
	 * Deactivates the interpreter.
	 * 
	 * @param memento the memento to use
	 */
	public void passivate(final StateMachineMemento<S, E> memento) {
		memento.setCurrentState(getCurrentStateId());
		for (final Map.Entry<InternalState<TStateMachine, S, E>, InternalState<TStateMachine, S, E>> e : superToSubState
				.entrySet()) {
			memento.putHistoryState(e.getKey().getId(), e.getValue().getId());
		}
	}
}
