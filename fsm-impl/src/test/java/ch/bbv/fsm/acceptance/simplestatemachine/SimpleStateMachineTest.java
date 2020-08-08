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
package ch.bbv.fsm.acceptance.simplestatemachine;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.bbv.fsm.events.TransitionEvent;
import ch.bbv.fsm.events.annotation.OnTransitionBegin;
import ch.bbv.fsm.events.annotation.OnTransitionCompleted;
import ch.bbv.fsm.impl.SimpleStateMachineWithContext;
import ch.bbv.fsm.impl.StatemachineBuilder;
import ch.bbv.fsm.impl.internal.events.Annotations;

public class SimpleStateMachineTest {

	public enum Events {
		TO_B, TO_C, TO_D, TO_E, TO_F
	}

	public enum States {
		A, B, C, D, E, F;
	}

	@Test
	public void test1() {
		final StringBuilder sb = new StringBuilder();
		final SimpleStateMachineWithContext<States, Events, Object> sm = StatemachineBuilder
				.<States, Events, Object>createWithContext(States.A, def -> {
					def.in(States.A).executeOnEntry(s -> sb.append("onEntryA."))//
							.executeOnExit(s -> sb.append("onExitA."))//
							.on(Events.TO_B).goTo(States.B).execute(s -> sb.append("inTransitionToB."));
					def.in(States.B).executeOnEntry(s -> sb.append("onEntryB"));
				}).context(new X()).buildPassive("StateMachine-1");
		sm.start();
		sm.fire(Events.TO_B, 1);

		Assertions.assertThat(sb.toString()).isEqualTo("onEntryA.onExitA.inTransitionToB.onEntryB");
	}

	@Test
	public void test2() {
		final StringBuilder sb = new StringBuilder();
		final SimpleStateMachineWithContext<States, Events, Object> sm = StatemachineBuilder
				.<States, Events, Object>createWithContext(States.A, def -> {
					def.in(States.A).on(Events.TO_B).goTo(States.B).execute(s -> sb.append("inTransitionToB")).onlyIf((fsm, param) -> true);
					def.in(States.A).on(Events.TO_B).goTo(States.C).execute(s -> sb.append("inTransitionToC"));

				}).context(new X()).buildPassive("StateMachine-1");
		sm.start();
		sm.fire(Events.TO_B, 1);

		Assertions.assertThat(sb.toString()).isEqualTo("inTransitionToB");
	}

	@Test
	public void test3() {
		final StringBuilder sb = new StringBuilder();
		final SimpleStateMachineWithContext<States, Events, Object> sm = StatemachineBuilder
				.<States, Events, Object>createWithContext(States.A, def -> {
					def.in(States.A).on(Events.TO_B).goTo(States.B).execute((s, p1) -> sb.append(p1));

				}).context(new X()).buildPassive("StateMachine-1");
		sm.start();
		sm.fire(Events.TO_B, 5);

		Assertions.assertThat(sb.toString()).isEqualTo("5");
	}

	@Test
	public void test4() {
		final StringBuilder sb = new StringBuilder();
		final SimpleStateMachineWithContext<States, Events, Object> sm = StatemachineBuilder
				.<States, Events, Object>createWithContext(States.A, def -> {
					def.in(States.A).on(Events.TO_B).goTo(States.B).execute((s, p1, p2) -> sb.append(p1 + "," + p2));

				}).context(new X()).buildPassive("StateMachine-1");
		sm.start();
		sm.fire(Events.TO_B, 4, "lambda");

		Assertions.assertThat(sb.toString()).isEqualTo("4,lambda");
	}

	@Test
	public void test5() {
		final StringBuilder sb = new StringBuilder();
		final SimpleStateMachineWithContext<States, Events, X> sm = StatemachineBuilder
				.<States, Events, X>createWithContext(States.A, def -> {
					def.in(States.A).on(Events.TO_B).goTo(States.B).execute((s, p1, p2) -> sb.append(p1 + "," + p2));
				}).context(new X()).buildPassive("StateMachine-1");
		sm.start();
		sm.fire(Events.TO_B, 4, "lambda");

		Assertions.assertThat(sb.toString()).isEqualTo("4,lambda");
		Assertions.assertThat(sm.get().toString()).isEqualTo("<@OnTransitionBegin>-><@OnTransitionBegin>");
	}

	@Test
	public void locateMethodWithAnnotation() {
		final Optional<Method> method = Annotations.find(OnTransitionBegin.class, X.class);
		method.orElseThrow(() -> new IllegalStateException());
	}

	public static class X {

		private final List<String> records = new ArrayList<>();

		@OnTransitionBegin
		public void m1(final TransitionEvent<SimpleStateMachineWithContext<States, Events, X>, States, Events> event) {
			records.add("<@OnTransitionBegin>");
		}

		@OnTransitionCompleted
		public void m2(final TransitionEvent<SimpleStateMachineWithContext<States, Events, X>, States, Events> event) {
			records.add("<@OnTransitionBegin>");
		}

		@Override
		public String toString() {
			return String.join("->", records);
		}

	}

}
