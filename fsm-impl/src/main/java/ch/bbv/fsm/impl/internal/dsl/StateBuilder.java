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
import ch.bbv.fsm.guard.Guard;
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
 * @param <SM> the type of InternalStateImpl machine
 * @param <S>        the type of the states.
 * @param <E>        the type of the events.
 */
public class StateBuilder<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>>
		implements EntryActionSyntax<SM, S, E>, EventActionSyntax<SM, S, E>,
		ExecuteSyntax<SM, S, E>, GotoSyntax<SM, S, E> {

	private final InternalState<SM, S, E> internalState;
	private final StateDictionary<SM, S, E> stateDictionary;
	private Transition<SM, S, E> currentTransition;

	/**
	 * Creates a new instance.
	 *
	 * @param state           the InternalStateImpl
	 * @param stateDictionary the InternalStateImpl dictionary
	 */
	public StateBuilder(final InternalState<SM, S, E> state,
			final StateDictionary<SM, S, E> stateDictionary) {
		this.internalState = state;
		this.stateDictionary = stateDictionary;
	}

	@Override
	public ExecuteSyntax<SM, S, E> execute(final FsmAction0<SM, S, E> action) {
		this.currentTransition.getActions().add(new FsmCall0<SM, S, E>(action));
		return this;
	}

	@Override
	public <T> ExecuteSyntax<SM, S, E> execute(final FsmAction1<SM, S, E, T> action) {
		this.currentTransition.getActions().add(new FsmCall1<SM, S, E, T>(action, null));
		return this;
	}

	@Override
	public <T1, T2> ExecuteSyntax<SM, S, E> execute(final FsmAction2<SM, S, E, T1, T2> action) {
		this.currentTransition.getActions().add(new FsmCall2<SM, S, E, T1, T2>(action, null, null));
		return this;
	}

	@Override
	public ExitActionSyntax<SM, S, E> executeOnEntry(final FsmAction0<SM, S, E> action) {
		this.internalState.setEntryAction(new FsmCall0<SM, S, E>(action));
		return this;
	}

	@Override
	public EventSyntax<SM, S, E> executeOnExit(final FsmAction0<SM, S, E> actionClass) {
		this.internalState.setExitAction(new FsmCall0<SM, S, E>(actionClass));
		return this;
	}

	@Override
	public ExecuteSyntax<SM, S, E> goTo(final S target) {
		this.currentTransition.setTarget(this.stateDictionary.getState(target));
		return this;
	}

	@Override
	public EventActionSyntax<SM, S, E> on(final E eventId) {
		this.currentTransition = new Transition<>();
		this.internalState.getTransitions().add(eventId, this.currentTransition);
		return this;
	}

	@Override
	public EventSyntax<SM, S, E> onlyIf(final Guard<SM, S, E, Object[], Boolean> guard) {
		this.currentTransition.setGuard(guard);
		return this;
	}
}
