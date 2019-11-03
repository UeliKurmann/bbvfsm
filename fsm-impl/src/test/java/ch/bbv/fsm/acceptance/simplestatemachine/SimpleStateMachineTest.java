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

import org.junit.Before;
import org.junit.Test;

import ch.bbv.fsm.StateMachineFactory;
import ch.bbv.fsm.impl.Fsm;
import ch.bbv.fsm.impl.SimpleStateMachine;

/**
 * Example: Tennis Scorer.
 *
 * @author Ueli Kurmann
 *
 */
public class SimpleStateMachineTest {

	public enum Events {
		TO_B, TO_C, TO_D, TO_E, TO_F
	}

	public enum States {
		A, B, C, D, E, F;
	}

	private StateMachineFactory<SimpleStateMachine<States, Events>, States, Events> factory;

	@Before
	public void setup() {
		this.factory = Fsm.create(States.A, def -> {
			//def.in(States.A).executeOnEntry()
		});
	}

	@Test
	public void scoreWhenIn0to0AandBScores3TimesSwitchingThenDeuce() {
	}

}
