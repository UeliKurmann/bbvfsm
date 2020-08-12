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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.bbv.fsm.StateMachineFactory;
import ch.bbv.fsm.impl.SimpleStateMachine;
import ch.bbv.fsm.impl.StatemachineBuilder;

/**
 * Example: Tennis Scorer.
 *
 * @author Ueli Kurmann
 *
 */
public class Tennis {

	public enum Events {
		A_Scores, B_Scores
	}

	public enum States {
		_0_0, _0_15, _0_30, _0_40, _15_15, _15_30, _15_40, _30_30, _30_40, _15_0, _30_0, _30_15, _40_0, _40_15, _40_30, _A_GAME, _B_GAME, _DEUCE, _A_ADV,
		_B_ADV;
	}

	private StateMachineFactory<SimpleStateMachine<States, Events>, States, Events> factory;

	@BeforeEach
	public void setup() {
		this.factory = StatemachineBuilder.create(States._0_0, def -> {
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

		final States initialState = testee.getCurrentState();

		testee.fire(Events.A_Scores);
		final States score1 = testee.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score2 = testee.getCurrentState();

		testee.fire(Events.A_Scores);
		final States score3 = testee.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score4 = testee.getCurrentState();

		testee.fire(Events.A_Scores);
		final States score5 = testee.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score6 = testee.getCurrentState();

		testee.terminate();

		Assertions.assertThat(States._0_0).isEqualTo(initialState);
		Assertions.assertThat(States._15_0).isEqualTo(score1);
		Assertions.assertThat(States._15_15).isEqualTo(score2);
		Assertions.assertThat(States._30_15).isEqualTo(score3);
		Assertions.assertThat(States._30_30).isEqualTo(score4);
		Assertions.assertThat(States._40_30).isEqualTo(score5);
		Assertions.assertThat(States._DEUCE).isEqualTo(score6);
	}

	@Test
	public void scoreWhenIn0to0AScoresrTimesThenAWins() {
		final SimpleStateMachine<States, Events> testee = factory.createPassiveStateMachine("Tennis-1");
		testee.start();

		final States initialState = testee.getCurrentState();

		testee.fire(Events.A_Scores);
		final States score1 = testee.getCurrentState();

		testee.fire(Events.A_Scores);
		final States score2 = testee.getCurrentState();

		testee.fire(Events.A_Scores);
		final States score3 = testee.getCurrentState();

		testee.fire(Events.A_Scores);
		final States score4 = testee.getCurrentState();

		testee.terminate();
		Assertions.assertThat(States._0_0).isEqualTo(initialState);
		Assertions.assertThat(States._15_0).isEqualTo(score1);
		Assertions.assertThat(States._30_0).isEqualTo(score2);
		Assertions.assertThat(States._40_0).isEqualTo(score3);
		Assertions.assertThat(States._A_GAME).isEqualTo(score4);
	}

	@Test
	public void scoreWhenIn0to0BScoresrTimesThenBWins() {
		final SimpleStateMachine<States, Events> testee = factory.createPassiveStateMachine("Tennis-1", States._0_0);
		testee.start();

		final States initialState = testee.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score1 = testee.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score2 = testee.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score3 = testee.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score4 = testee.getCurrentState();

		Assertions.assertThat(States._0_0).isEqualTo(initialState);
		Assertions.assertThat(States._0_15).isEqualTo(score1);
		Assertions.assertThat(States._0_30).isEqualTo(score2);
		Assertions.assertThat(States._0_40).isEqualTo(score3);
		Assertions.assertThat(States._B_GAME).isEqualTo(score4);
	}

	@Test
	public void testScorerWhenDeuceAndAScores2TimesThenAWins() {
		final SimpleStateMachine<States, Events> testee = factory.createPassiveStateMachine("Tennis-1", States._DEUCE);
		testee.start();

		testee.fire(Events.A_Scores);
		final States score1 = testee.getCurrentState();

		testee.fire(Events.A_Scores);
		final States score2 = testee.getCurrentState();

		testee.terminate();

		Assertions.assertThat(States._A_ADV).isEqualTo(score1);
		Assertions.assertThat(States._A_GAME).isEqualTo(score2);
	}

	@Test
	public void testScorerWhenDeuceAndBfollowedByAScoresThenDeuce() {
		final SimpleStateMachine<States, Events> testee = factory.createPassiveStateMachine("Tennis-1", States._DEUCE);
		testee.start();

		testee.fire(Events.A_Scores);
		final States score1 = testee.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score2 = testee.getCurrentState();

		testee.terminate();

		Assertions.assertThat(States._A_ADV).isEqualTo(score1);
		Assertions.assertThat(States._DEUCE).isEqualTo(score2);
	}

	@Test
	public void testScorerWhenDeuceAndBScores2TimesThenBWins() {
		final SimpleStateMachine<States, Events> testee = factory.createPassiveStateMachine("Tennis-1", States._DEUCE);
		testee.start();

		testee.fire(Events.B_Scores);
		final States score1 = testee.getCurrentState();

		testee.fire(Events.B_Scores);
		final States score2 = testee.getCurrentState();

		testee.terminate();

		Assertions.assertThat(States._B_ADV).isEqualTo(score1);
		Assertions.assertThat(States._B_GAME).isEqualTo(score2);
	}

}
