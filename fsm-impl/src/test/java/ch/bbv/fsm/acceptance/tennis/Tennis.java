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
package ch.bbv.fsm.acceptance.tennis;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.bbv.fsm.StateMachineFactory;
import ch.bbv.fsm.events.StateMachineEventHandlerAdapter;
import ch.bbv.fsm.events.TransitionCompletedEvent;
import ch.bbv.fsm.impl.StatemachineBuilder;
import ch.bbv.fsm.impl.SimpleStateMachine;

/**
 * Example: Tennis Scorer.
 *
 * @author Ueli Kurmann
 *
 */
public class Tennis {

	private static class Handler extends StateMachineEventHandlerAdapter<SimpleStateMachine<States, Events>, States, Events> {

		States currentState = States._0_0;

		@Override
		public void onTransitionCompleted(TransitionCompletedEvent<SimpleStateMachine<States, Events>, States, Events> arg) {
			currentState = arg.getNewStateId();
		}

		public States getCurrentState() {
			return currentState;
		}
	}

	public enum Events {
		A_Scores, B_Scores
	}

	public enum States {
		_0_0, _0_15, _0_30, _0_40, _15_15, _15_30, _15_40, _30_30, _30_40, _15_0, _30_0, _30_15, _40_0, _40_15, _40_30, _A_GAME, _B_GAME,
		_DEUCE, _A_ADV, _B_ADV;
	}

	private StateMachineFactory<SimpleStateMachine<States, Events>, States, Events> factory;
	private Handler handler;

	@Before
	public void setup() {
		this.factory = StatemachineBuilder.create(States._0_0, def -> {
			this.handler = new Handler();
			def.addEventHandler(handler);
			def.in(States._0_0).on(Events.A_Scores).goTo(States._15_0);
			def.in(States._0_0).on(Events.B_Scores).goTo(States._0_15);

			def.in(States._0_15).on(Events.A_Scores).goTo(States._15_15);
			def.in(States._0_15).on(Events.B_Scores).goTo(States._0_30);

			def.in(States._0_30).on(Events.A_Scores).goTo(States._15_30);
			def.in(States._0_30).on(Events.B_Scores).goTo(States._0_40);

			def.in(States._0_40).on(Events.A_Scores).goTo(States._15_40);
			def.in(States._0_40).on(Events.B_Scores).goTo(States._B_GAME);

			def.in(States._15_0).on(Events.A_Scores).goTo(States._30_0);
			def.in(States._15_0).on(Events.B_Scores).goTo(States._15_15);

			def.in(States._15_15).on(Events.A_Scores).goTo(States._30_15);
			def.in(States._15_15).on(Events.B_Scores).goTo(States._15_30);

			def.in(States._15_30).on(Events.A_Scores).goTo(States._30_30);
			def.in(States._15_30).on(Events.B_Scores).goTo(States._15_40);

			def.in(States._15_40).on(Events.A_Scores).goTo(States._30_40);
			def.in(States._15_40).on(Events.B_Scores).goTo(States._B_GAME);

			def.in(States._30_0).on(Events.A_Scores).goTo(States._40_0);
			def.in(States._30_0).on(Events.B_Scores).goTo(States._30_15);

			def.in(States._30_15).on(Events.A_Scores).goTo(States._40_15);
			def.in(States._30_15).on(Events.B_Scores).goTo(States._30_30);

			def.in(States._30_30).on(Events.A_Scores).goTo(States._40_30);
			def.in(States._30_30).on(Events.B_Scores).goTo(States._30_40);

			def.in(States._30_40).on(Events.A_Scores).goTo(States._DEUCE);
			def.in(States._30_40).on(Events.B_Scores).goTo(States._B_GAME);

			def.in(States._40_0).on(Events.A_Scores).goTo(States._A_GAME);
			def.in(States._40_0).on(Events.B_Scores).goTo(States._40_15);

			def.in(States._40_15).on(Events.A_Scores).goTo(States._A_GAME);
			def.in(States._40_15).on(Events.B_Scores).goTo(States._40_30);

			def.in(States._40_30).on(Events.A_Scores).goTo(States._A_GAME);
			def.in(States._40_30).on(Events.B_Scores).goTo(States._DEUCE);

			def.in(States._DEUCE).on(Events.A_Scores).goTo(States._A_ADV);
			def.in(States._DEUCE).on(Events.B_Scores).goTo(States._B_ADV);

			def.in(States._A_ADV).on(Events.A_Scores).goTo(States._A_GAME);
			def.in(States._A_ADV).on(Events.B_Scores).goTo(States._DEUCE);

			def.in(States._B_ADV).on(Events.A_Scores).goTo(States._DEUCE);
			def.in(States._B_ADV).on(Events.B_Scores).goTo(States._B_GAME);
		});
	}

	@Test
	public void scoreWhenIn0to0AandBScores3TimesSwitchingThenDeuce() {
		final SimpleStateMachine<States, Events> testee = factory.createPassiveStateMachine("Tennis-0");
		testee.start();

		final States initialState = handler.getCurrentState();

		testee.fire(Events.A_Scores);
		final States score1 = handler.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score2 = handler.getCurrentState();

		testee.fire(Events.A_Scores);
		final States score3 = handler.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score4 = handler.getCurrentState();

		testee.fire(Events.A_Scores);
		final States score5 = handler.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score6 = handler.getCurrentState();

		testee.terminate();

		Assert.assertEquals(States._0_0, initialState);
		Assert.assertEquals(States._15_0, score1);
		Assert.assertEquals(States._15_15, score2);
		Assert.assertEquals(States._30_15, score3);
		Assert.assertEquals(States._30_30, score4);
		Assert.assertEquals(States._40_30, score5);
		Assert.assertEquals(States._DEUCE, score6);
	}

	@Test
	public void scoreWhenIn0to0AScoresrTimesThenAWins() {
		final SimpleStateMachine<States, Events> testee = factory.createPassiveStateMachine("Tennis-1");
		testee.start();

		final States initialState = handler.getCurrentState();

		testee.fire(Events.A_Scores);
		final States score1 = handler.getCurrentState();

		testee.fire(Events.A_Scores);
		final States score2 = handler.getCurrentState();

		testee.fire(Events.A_Scores);
		final States score3 = handler.getCurrentState();

		testee.fire(Events.A_Scores);
		final States score4 = handler.getCurrentState();

		testee.terminate();

		Assert.assertEquals(States._0_0, initialState);
		Assert.assertEquals(States._15_0, score1);
		Assert.assertEquals(States._30_0, score2);
		Assert.assertEquals(States._40_0, score3);
		Assert.assertEquals(States._A_GAME, score4);
	}

	@Test
	public void scoreWhenIn0to0BScoresrTimesThenBWins() {
		final SimpleStateMachine<States, Events> testee = factory.createPassiveStateMachine("Tennis-1", States._0_0);
		testee.start();

		final States initialState = handler.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score1 = handler.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score2 = handler.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score3 = handler.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score4 = handler.getCurrentState();

		Assert.assertEquals(States._0_0, initialState);
		Assert.assertEquals(States._0_15, score1);
		Assert.assertEquals(States._0_30, score2);
		Assert.assertEquals(States._0_40, score3);
		Assert.assertEquals(States._B_GAME, score4);
	}

	@Test
	public void testScorerWhenDeuceAndAScores2TimesThenAWins() {
		final SimpleStateMachine<States, Events> testee = factory.createPassiveStateMachine("Tennis-1", States._DEUCE);
		testee.start();

		testee.fire(Events.A_Scores);
		final States score1 = handler.getCurrentState();

		testee.fire(Events.A_Scores);
		final States score2 = testee.getCurrentState();

		testee.terminate();

		Assert.assertEquals(States._A_ADV, score1);
		Assert.assertEquals(States._A_GAME, score2);
	}

	@Test
	public void testScorerWhenDeuceAndBfollowedByAScoresThenDeuce() {
		final SimpleStateMachine<States, Events> testee = factory.createPassiveStateMachine("Tennis-1", States._DEUCE);
		testee.start();

		testee.fire(Events.A_Scores);
		final States score1 = handler.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score2 = handler.getCurrentState();

		testee.terminate();

		Assert.assertEquals(States._A_ADV, score1);
		Assert.assertEquals(States._DEUCE, score2);
	}

	@Test
	public void testScorerWhenDeuceAndBScores2TimesThenBWins() {
		final SimpleStateMachine<States, Events> testee = factory.createPassiveStateMachine("Tennis-1", States._DEUCE);
		testee.start();

		testee.fire(Events.B_Scores);
		final States score1 = handler.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score2 = handler.getCurrentState();

		testee.terminate();

		Assert.assertEquals(States._B_ADV, score1);
		Assert.assertEquals(States._B_GAME, score2);
	}

}
