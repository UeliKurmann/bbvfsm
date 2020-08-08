package ch.bbv.fsm.acceptance.radio;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.bbv.fsm.acceptance.radio.RadioStateMachineDefinion.Event;
import ch.bbv.fsm.acceptance.radio.RadioStateMachineDefinion.State;
import ch.bbv.fsm.memento.StateMachineMemento;

public class RadioPassivateAndActivateAcceptanceTest {

	public class RadioStateMachineMemento extends StateMachineMemento<RadioStateMachineDefinion.State, RadioStateMachineDefinion.Event> {

	}

	@Test
	public void radioWhenUseHistoryThenHistoryMustBeWrittenToMemento() {
		final RadioStateMachineDefinion radioStateMachineDefinion = new RadioStateMachineDefinion();

		final RadioStateMachine radioStateMachine = radioStateMachineDefinion
				.createPassiveStateMachine("radioWhenSimplingTurnOnAndOffThenPlayFM");

		radioStateMachine.start();

		radioStateMachine.fire(Event.TogglePower);
		radioStateMachine.fire(Event.ToggleMode);
		radioStateMachine.fire(Event.TogglePower);

		final StateMachineMemento<State, Event> memento = new RadioStateMachineMemento();
		radioStateMachine.passivate(memento);

		final Map<State, State> expectedHistory = new HashMap<>();
		expectedHistory.put(State.On, State.AM);
		Assertions.assertThat(memento.getCurrentState()).isEqualTo(State.Off);
		Assertions.assertThat(memento.getSavedHistoryStates()).isEqualTo(expectedHistory);
	}

	@Test
	public void radioWhenActiveUsingMemementoThenMustBeRestored() {
		final RadioStateMachineDefinion radioStateMachineDefinion = new RadioStateMachineDefinion();

		final RadioStateMachine radioStateMachine = radioStateMachineDefinion
				.createPassiveStateMachine("radioWhenSimplingTurnOnAndOffThenPlayFM");

		final StateMachineMemento<State, Event> memento = new RadioStateMachineMemento();
		memento.setCurrentState(State.Off);
		memento.putHistoryState(State.On, State.FM);

		radioStateMachine.activate(memento);

		radioStateMachine.fire(Event.TogglePower);
		radioStateMachine.fire(Event.TogglePower);

		radioStateMachine.terminate();

		Assertions.assertThat(radioStateMachine.consumeLog()).isEqualTo("exitOff.OffToOn.entryOn.entryFM.exitFM.exitOn.OnToOff.entryOff.exitOff");
	}

}
