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
package ch.bbv.fsm;

import ch.bbv.fsm.impl.SimpleStateMachine;
import ch.bbv.fsm.impl.SimpleStateMachineDefinition;
import ch.bbv.fsm.impl.StatesAndEvents.Events;
import ch.bbv.fsm.impl.StatesAndEvents.States;

/**
 * Base for state machine test fixtures.
 */
public class ActiveStateMachineDriverTest extends BaseStateMachineTest {

	@Override
	protected SimpleStateMachine<States, Events> createTestee(final SimpleStateMachineDefinition<States, Events> definition,
			final States states) {
		return definition.createActiveStateMachine("Testee", states);
	}
}
