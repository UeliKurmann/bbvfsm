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
package ch.bbv.fsm.impl.internal.statemachine.transition;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.guard.Guard;
import ch.bbv.fsm.impl.internal.action.FsmCall;
import ch.bbv.fsm.impl.internal.statemachine.state.InternalState;
import ch.bbv.fsm.impl.internal.statemachine.state.StateContext;

/**
 * The implementation of a transition.
 *
 * @param <SM> the type of state machine
 * @param <S>             the type of the states
 * @param <E>             the type of the events
 */
public class Transition<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>> {

	private static final Logger LOG = LoggerFactory.getLogger(Transition.class);

	/**
	 * The actions that are executed when this transition is fired.
	 */
	private final List<FsmCall<SM, S, E>> actions;

	private InternalState<SM, S, E> source;

	private InternalState<SM, S, E> target;

	private Guard<SM, S, E, Object[], Boolean> guard;

	/**
	 * Creates a new instance.
	 *
	 * @param notifier
	 */
	public Transition() {
		this.actions = new ArrayList<>();
	}

	/**
	 * Recursively traverses the state hierarchy, exiting states along the way,
	 * performing the action, and entering states to the target.
	 * <hr>
	 * There exist the following transition scenarios:
	 * <ul>
	 * <li>0. there is no target state (internal transition) --> handled outside
	 * this method.</li>
	 * <li>1. The source and target state are the same (self transition) --> perform
	 * the transition directly: Exit source state, perform transition actions and
	 * enter target state</li>
	 * <li>2. The target state is a direct or indirect sub-state of the source state
	 * --> perform the transition actions, then traverse the hierarchy from the
	 * source state down to the target state, entering each state along the way. No
	 * state is exited.
	 * <li>3. The source state is a sub-state of the target state --> traverse the
	 * hierarchy from the source up to the target, exiting each state along the way.
	 * Then perform transition actions. Finally enter the target state.</li>
	 * <li>4. The source and target state share the same super-state</li>
	 * <li>5. All other scenarios:
	 * <ul>
	 * <li>a. The source and target states reside at the same level in the hierarchy
	 * but do not share the same direct super-state</li>
	 * <li>b. The source state is lower in the hierarchy than the target state</li>
	 * <li>c. The target state is lower in the hierarchy than the source state</li>
	 * </ul>
	 * </ul>
	 *
	 * @param source         the source state
	 * @param target         the target state
	 * @param eventArguments the event arguments
	 * @param context        the state context
	 */
	private void fire(final InternalState<SM, S, E> source, final InternalState<SM, S, E> target,
			final Object[] eventArguments, final TransitionContext<SM, S, E> context) {
		if (source == this.getTarget()) {
			// Handles 1.
			// Handles 3. after traversing from the source to the target.
			source.exit(context);
			this.performActions(eventArguments, context);
			this.getTarget().entry(context);
		} else if (source == target) {
			// Handles 2. after traversing from the target to the source.
			this.performActions(eventArguments, context);
		} else if (source.getSuperState() == target.getSuperState()) {
			// // Handles 4.
			// // Handles 5a. after traversing the hierarchy until a common
			// ancestor if found.
			source.exit(context);
			this.performActions(eventArguments, context);
			target.entry(context);
		} else {
			// traverses the hierarchy until one of the above scenarios is met.

			// Handles 3.
			// Handles 5b.
			if (source.getLevel() > target.getLevel()) {
				source.exit(context);
				this.fire(source.getSuperState(), target, eventArguments, context);
			} else if (source.getLevel() < target.getLevel()) {
				// Handles 2.
				// Handles 5c.
				this.fire(source, target.getSuperState(), eventArguments, context);
				target.entry(context);
			} else {
				// Handles 5a.
				source.exit(context);
				this.fire(source.getSuperState(), target.getSuperState(), eventArguments, context);
				target.entry(context);
			}
		}
	}

	/**
	 * Fires the transition.
	 * 
	 * @param context the event context.
	 * @return The result of the transition.
	 */
	public TransitionResult<SM, S, E> fire(final TransitionContext<SM, S, E> context) {
		LOG.debug("Start transition1 {}", this);
		if (!this.shouldFire(context.getEventArguments(), context)) {
			LOG.debug("Start transition2 {}", this);
			@SuppressWarnings("unchecked")
			final TransitionResult<SM, S, E> result = TransitionResult.getNotFired();
			return result;
		}
		LOG.debug("Start transition3 {}", this);

		context.getNotifier().onTransitionBegin(context);

		InternalState<SM, S, E> newState = context.getState();

		if (!this.isInternalTransition()) {
			this.unwindSubStates(context.getState(), context);

			this.fire(this.getSource(), this.getTarget(), context.getEventArguments(), context);

			newState = this.getTarget().enterByHistory(context);
		} else {
			this.performActions(context.getEventArguments(), context);
		}

		return new TransitionResult<>(true, newState, context.getExceptions());
	}

	/**
	 * Gets the actions of this transition.
	 * 
	 * @return the actions of this transition.
	 */
	public List<FsmCall<SM, S, E>> getActions() {
		return this.actions;
	}

	/**
	 * Gets or sets the guard of this transition.
	 * 
	 * @return the guard.
	 */
	public Guard<SM, S, E, Object[], Boolean> getGuard() {
		return this.guard;
	}

	/**
	 * Returns the source state of the transition.
	 * 
	 * @return the source state of the transition.
	 */
	public InternalState<SM, S, E> getSource() {
		return this.source;
	}

	/**
	 * Gets the target state of the transition.
	 * 
	 * @return the target state of the transition.
	 */
	public InternalState<SM, S, E> getTarget() {
		return this.target;
	}

	/**
	 * Handles an exception thrown during performing the transition or guard
	 * evaluation.
	 *
	 * @param exception the exception
	 * @param context   the transition context
	 */
	private void handleException(final Exception exception, final TransitionContext<SM, S, E> context) {
		context.getExceptions().add(exception);
		context.getNotifier().onExceptionThrown(context, exception);
	}

	/**
	 * Gets a value indicating whether this is an internal transition. true =
	 * internal.
	 */
	private boolean isInternalTransition() {
		return this.target == null;
	}

	/**
	 * Performs the actions of this transition.
	 *
	 * @param eventArguments the event arguments
	 * @param context        the transition context
	 */
	private void performActions(final Object[] eventArguments, final TransitionContext<SM, S, E> context) {
		for (final FsmCall<SM, S, E> action : this.getActions()) {
			try {
				action.execOn(context.getStateMachine(), eventArguments);
			} catch (final Exception exception) {
				LOG.error("Exception in action of transition {}: {}", this, exception);
				this.handleException(exception, context);
			}
		}
	}

	/**
	 * Sets the guard function.
	 * 
	 * @param guard the guard function.
	 */
	public void setGuard(final Guard<SM, S, E, Object[], Boolean> guard) {
		this.guard = guard;
	}

	/**
	 * Sets the source state of the transition.
	 * 
	 * @param sourceState the source state of the transition.
	 */
	public void setSource(final InternalState<SM, S, E> source) {
		this.source = source;
	}

	/**
	 * Sets the target state.
	 * 
	 * @param target the target state.
	 */
	public void setTarget(final InternalState<SM, S, E> target) {
		this.target = target;
	}

	/**
	 * Returns true if the transition should fire.
	 *
	 * @param eventArguments the event arguments
	 * @param context        the context
	 * @return true if the transition should fire
	 */
	private boolean shouldFire(final Object[] eventArguments, final TransitionContext<SM, S, E> context) {
		try {
			boolean result = true;
			if (this.getGuard() != null) {
				result = this.getGuard().execute(context.getStateMachine(), eventArguments);
				LOG.debug("Checked guard: {} for {}, result is " + result, getGuard(), this);
			}
			return result;
		} catch (final Exception exception) {
			LOG.error("Exception in guard of transition {}: {}", this, exception);
			this.handleException(exception, context);
			return false;
		}
	}

	@Override
	public String toString() {
		return String.format("Transition from state %s to state %s.", this.getSource(), this.getTarget());
	}

	/**
	 * Exits all sub-states up the hierarchy up to the origin state.
	 *
	 * @param origin       the origin state
	 * @param stateContext the state context
	 */
	private void unwindSubStates(final InternalState<SM, S, E> origin, final StateContext<SM, S, E> stateContext) {
		for (InternalState<SM, S, E> o = origin; o != this.getSource(); o = o.getSuperState()) {
			o.exit(stateContext);
		}
	}
}
