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
package ch.bbv.fsm.impl.internal.dsl;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.action.FsmAction0;
import ch.bbv.fsm.action.FsmAction1;
import ch.bbv.fsm.action.FsmAction2;
import ch.bbv.fsm.dsl.EntryActionSyntax;
import ch.bbv.fsm.dsl.EventActionSyntax;
import ch.bbv.fsm.dsl.EventSyntax;
import ch.bbv.fsm.dsl.ExecuteSyntax;
import ch.bbv.fsm.dsl.ExitActionSyntax;
import ch.bbv.fsm.dsl.GotoSyntax;
import ch.bbv.fsm.guard.Function;
import ch.bbv.fsm.impl.internal.action.FsmCall0;
import ch.bbv.fsm.impl.internal.action.FsmCall1;
import ch.bbv.fsm.impl.internal.action.FsmCall2;
import ch.bbv.fsm.impl.internal.statemachine.state.InternalState;
import ch.bbv.fsm.impl.internal.statemachine.state.StateDictionary;
import ch.bbv.fsm.impl.internal.statemachine.transition.Transition;

/**
 * InternalStateImpl Builder.
 *
 * @author Ueli Kurmann .
 *
 * @param <TStateMachine> the type of InternalStateImpl machine
 * @param <TState>        the type of the states.
 * @param <TEvent>        the type of the events.
 */
public class StateBuilder<TStateMachine extends StateMachine<TState, TEvent>, TState extends Enum<?>, TEvent extends Enum<?>>
		implements EntryActionSyntax<TStateMachine, TState, TEvent>, EventActionSyntax<TStateMachine, TState, TEvent>,
		ExecuteSyntax<TStateMachine, TState, TEvent>, GotoSyntax<TStateMachine, TState, TEvent> {

	private final InternalState<TStateMachine, TState, TEvent> InternalStateImpl;
	private final StateDictionary<TStateMachine, TState, TEvent> stateDictionary;
	private Transition<TStateMachine, TState, TEvent> currentTransition;

	/**
	 * Creates a new instance.
	 *
	 * @param state           the InternalStateImpl
	 * @param stateDictionary the InternalStateImpl dictionary
	 */
	public StateBuilder(final InternalState<TStateMachine, TState, TEvent> state,
			final StateDictionary<TStateMachine, TState, TEvent> stateDictionary) {
		this.InternalStateImpl = state;
		this.stateDictionary = stateDictionary;
	}

	@Override
	public ExecuteSyntax<TStateMachine, TState, TEvent> execute(final FsmAction0<TStateMachine, TState, TEvent> action) {
		this.currentTransition.getActions().add(new FsmCall0<TStateMachine, TState, TEvent>(action));
		return this;
	}

	@Override
	public <T> ExecuteSyntax<TStateMachine, TState, TEvent> execute(final FsmAction1<TStateMachine, TState, TEvent, T> action) {
		this.currentTransition.getActions().add(new FsmCall1<TStateMachine, TState, TEvent, T>(action, null));
		return this;
	}

	@Override
	public <T1, T2> ExecuteSyntax<TStateMachine, TState, TEvent> execute(final FsmAction2<TStateMachine, TState, TEvent, T1, T2> action) {
		this.currentTransition.getActions().add(new FsmCall2<TStateMachine, TState, TEvent, T1, T2>(action, null, null));
		return this;
	}

	@Override
	public ExitActionSyntax<TStateMachine, TState, TEvent> executeOnEntry(final FsmAction0<TStateMachine, TState, TEvent> action) {
		this.InternalStateImpl.setEntryAction(new FsmCall0<TStateMachine, TState, TEvent>(action));
		return this;
	}

	@Override
	public EventSyntax<TStateMachine, TState, TEvent> executeOnExit(final FsmAction0<TStateMachine, TState, TEvent> actionClass) {
		this.InternalStateImpl.setExitAction(new FsmCall0<TStateMachine, TState, TEvent>(actionClass));
		return this;
	}

	@Override
	public ExecuteSyntax<TStateMachine, TState, TEvent> goTo(final TState target) {
		this.currentTransition.setTarget(this.stateDictionary.getState(target));
		return this;
	}

	@Override
	public EventActionSyntax<TStateMachine, TState, TEvent> on(final TEvent eventId) {
		this.currentTransition = new Transition<>();
		this.InternalStateImpl.getTransitions().add(eventId, this.currentTransition);
		return this;
	}

	@Override
	public EventSyntax<TStateMachine, TState, TEvent> onlyIf(final Function<TStateMachine, TState, TEvent, Object[], Boolean> guard) {
		this.currentTransition.setGuard(guard);
		return this;
	}
}
