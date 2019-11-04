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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import ch.bbv.fsm.impl.Fsm;
import ch.bbv.fsm.impl.SimpleStateMachine;

public class SimpleStateMachineTest {

	public enum Events {
		TO_B, TO_C, TO_D, TO_E, TO_F
	}

	public enum States {
		A, B, C, D, E, F;
	}

	@Test
	public void test1() {
		StringBuilder sb = new StringBuilder();
		SimpleStateMachine<States, Events> sm = Fsm.<States, Events>create(States.A, def -> {
			def.in(States.A).executeOnEntry(s -> sb.append("onEntryA."))//
					.executeOnExit(s -> sb.append("onExitA."))//
					.on(Events.TO_B).goTo(States.B).execute(s -> sb.append("inTransitionToB."));
			def.in(States.B).executeOnEntry(s -> sb.append("onEntryB"));
		}).createPassiveStateMachine("StateMachine-1");
		sm.start();
		sm.fire(Events.TO_B, 1);

		
		assertThat(sb.toString(), is(equalTo("onEntryA.onExitA.inTransitionToB.onEntryB")));
	}

	@Test
	public void test2() {
		StringBuilder sb = new StringBuilder();
		SimpleStateMachine<States, Events> sm = Fsm.<States, Events>create(States.A, def -> {
			def.in(States.A).on(Events.TO_B).goTo(States.B).execute(s -> sb.append("inTransitionToB")).onlyIf((fsm, param)->true);
			def.in(States.A).on(Events.TO_B).goTo(States.C).execute(s -> sb.append("inTransitionToC"));
			
		}).createPassiveStateMachine("StateMachine-1");
		sm.start();
		sm.fire(Events.TO_B, 1);

		
		assertThat(sb.toString(), is(equalTo("inTransitionToB")));
	}
}
