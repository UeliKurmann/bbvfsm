package ch.bbv.fsm.acceptance.radio;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.bbv.fsm.HistoryType;
import ch.bbv.fsm.acceptance.radio.RadioStateMachineDefinion.Event;

public class RadioAcceptanceTest {

	@Test
	public void radioWhenSimplingTurnOnAndOffThenPlayFM() {
		final RadioStateMachineDefinion radioStateMachineDefinion = new RadioStateMachineDefinion();

		final RadioStateMachine radioStateMachine = radioStateMachineDefinion.createPassiveStateMachine("radioWhenSimplingTurnOnAndOffThenPlayFM");

		radioStateMachine.start();

		radioStateMachine.fire(Event.TogglePower);
		radioStateMachine.fire(Event.TogglePower);

		radioStateMachine.terminate();

		Assertions.assertThat(radioStateMachine.consumeLog()).isEqualTo("entryOff.exitOff.OffToOn.entryOn.entryFM.exitFM.exitOn.OnToOff.entryOff.exitOff");
	}

	@Test
	public void radioWhenSetAMAutoTuningAndHistoryNoneThenPlayMustBeRestored() {
		final RadioStateMachineDefinion radioStateMachineDefinion = new RadioStateMachineDefinion();

		final RadioStateMachine radioStateMachine = radioStateMachineDefinion.createPassiveStateMachine("radioWhenSimplingTurnOnAndOffThenPlayFM");

		radioStateMachine.start();

		radioStateMachine.fire(Event.TogglePower);
		final String turnOnLog = radioStateMachine.consumeLog();

		radioStateMachine.fire(Event.ToggleMode);
		final String toggleMode = radioStateMachine.consumeLog();

		radioStateMachine.fire(Event.StationLost);
		final String autoTuning = radioStateMachine.consumeLog();

		radioStateMachine.fire(Event.TogglePower);
		final String powerOff = radioStateMachine.consumeLog();

		radioStateMachine.fire(Event.TogglePower);
		final String turnOnAgainLog = radioStateMachine.consumeLog();

		Assertions.assertThat(turnOnLog).isEqualTo("entryOff.exitOff.OffToOn.entryOn.entryFM");
		Assertions.assertThat(toggleMode).isEqualTo("exitFM.FMtoAM.entryAM.entryPlay");
		Assertions.assertThat(autoTuning).isEqualTo("exitPlay.PlayToAutoTune.entryAutoTune");
		Assertions.assertThat(powerOff).isEqualTo("exitAutoTune.exitAM.exitOn.OnToOff.entryOff");
		Assertions.assertThat(turnOnAgainLog).isEqualTo("exitOff.OffToOn.entryOn.entryAM.entryPlay");
	}

	@Test
	public void radioWhenSetAMAutoTuningAndHistoryShallowOnAMThenAutoTuneMustBeRestored() {
		final RadioStateMachineDefinion radioStateMachineDefinion = new RadioStateMachineDefinion(HistoryType.DEEP, HistoryType.SHALLOW);

		final RadioStateMachine radioStateMachine = radioStateMachineDefinion.createPassiveStateMachine("radioWhenSimplingTurnOnAndOffThenPlayFM");

		radioStateMachine.start();

		radioStateMachine.fire(Event.TogglePower);
		final String turnOnLog = radioStateMachine.consumeLog();

		radioStateMachine.fire(Event.ToggleMode);
		final String toggleMode = radioStateMachine.consumeLog();

		radioStateMachine.fire(Event.StationLost);
		final String autoTuning = radioStateMachine.consumeLog();

		radioStateMachine.fire(Event.TogglePower);
		final String powerOff = radioStateMachine.consumeLog();

		radioStateMachine.fire(Event.TogglePower);
		final String turnOnAgainLog = radioStateMachine.consumeLog();

		Assertions.assertThat(turnOnLog).isEqualTo("entryOff.exitOff.OffToOn.entryOn.entryFM");
		Assertions.assertThat(toggleMode).isEqualTo("exitFM.FMtoAM.entryAM.entryPlay");
		Assertions.assertThat(autoTuning).isEqualTo("exitPlay.PlayToAutoTune.entryAutoTune");
		Assertions.assertThat(powerOff).isEqualTo("exitAutoTune.exitAM.exitOn.OnToOff.entryOff");
		Assertions.assertThat(turnOnAgainLog).isEqualTo("exitOff.OffToOn.entryOn.entryAM.entryAutoTune");
	}

	@Test
	public void radioWhenSetAMAutoTuningAndHistoryShallowOnOnThenAutoTuneMustBeRestored() {
		final RadioStateMachineDefinion radioStateMachineDefinion = new RadioStateMachineDefinion(HistoryType.SHALLOW, HistoryType.SHALLOW);

		final RadioStateMachine radioStateMachine = radioStateMachineDefinion.createPassiveStateMachine("radioWhenSimplingTurnOnAndOffThenPlayFM");

		radioStateMachine.start();

		radioStateMachine.fire(Event.TogglePower);
		final String turnOnLog = radioStateMachine.consumeLog();

		radioStateMachine.fire(Event.ToggleMode);
		final String toggleMode = radioStateMachine.consumeLog();

		radioStateMachine.fire(Event.StationLost);
		final String autoTuning = radioStateMachine.consumeLog();

		radioStateMachine.fire(Event.TogglePower);
		final String powerOff = radioStateMachine.consumeLog();

		radioStateMachine.fire(Event.TogglePower);
		final String turnOnAgainLog = radioStateMachine.consumeLog();

		Assertions.assertThat(turnOnLog).isEqualTo("entryOff.exitOff.OffToOn.entryOn.entryFM");
		Assertions.assertThat(toggleMode).isEqualTo("exitFM.FMtoAM.entryAM.entryPlay");
		Assertions.assertThat(autoTuning).isEqualTo("exitPlay.PlayToAutoTune.entryAutoTune");
		Assertions.assertThat(powerOff).isEqualTo("exitAutoTune.exitAM.exitOn.OnToOff.entryOff");
		Assertions.assertThat(turnOnAgainLog).isEqualTo("exitOff.OffToOn.entryOn.entryAM.entryPlay");
	}

	@Test
	public void radioWhenMaintenanceThenStateMustBeMaintenance() {
		final RadioStateMachineDefinion radioStateMachineDefinion = new RadioStateMachineDefinion();

		final RadioStateMachine radioStateMachine = radioStateMachineDefinion.createPassiveStateMachine("radioWhenSimplingTurnOnAndOffThenPlayFM");

		radioStateMachine.start();

		radioStateMachine.setMaintenance(true);
		radioStateMachine.fire(Event.TogglePower);
		final String turnOnLog = radioStateMachine.consumeLog();

		Assertions.assertThat(turnOnLog).isEqualTo("entryOff.exitOff.OffToMaintenance.entryMaintenance");
	}

	@Test
	public void parallelUsageMustNotInfluenceEachOther() {
		final RadioStateMachineDefinion radioStateMachineDefinion = new RadioStateMachineDefinion();

		final RadioStateMachine radioStateMachine1 = radioStateMachineDefinion.createPassiveStateMachine("fsm1");
		final RadioStateMachine radioStateMachine2 = radioStateMachineDefinion.createPassiveStateMachine("fsm2");

		radioStateMachine1.start();
		radioStateMachine2.start();

		radioStateMachine1.setMaintenance(true);
		radioStateMachine1.fire(Event.TogglePower);
		final String turnOnLog1 = radioStateMachine1.consumeLog();

		radioStateMachine2.fire(Event.TogglePower);
		final String turnOnLog2 = radioStateMachine2.consumeLog();

		Assertions.assertThat(turnOnLog1).isEqualTo("entryOff.exitOff.OffToMaintenance.entryMaintenance");
		Assertions.assertThat(turnOnLog2).isEqualTo("entryOff.exitOff.OffToOn.entryOn.entryFM");
	}

}
