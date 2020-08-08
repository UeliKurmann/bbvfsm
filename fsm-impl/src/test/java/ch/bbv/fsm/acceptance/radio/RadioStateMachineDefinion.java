package ch.bbv.fsm.acceptance.radio;

import ch.bbv.fsm.HistoryType;
import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.acceptance.radio.RadioStateMachineDefinion.Event;
import ch.bbv.fsm.acceptance.radio.RadioStateMachineDefinion.State;
import ch.bbv.fsm.impl.AbstractStateMachineDefinition;

public class RadioStateMachineDefinion extends AbstractStateMachineDefinition<RadioStateMachine, State, Event> {

	public enum State {
		Off, // Radio is turned off
		On, // Radio is turned on
		Maintenance, // Selfcheck radio
		// 'On' sub-states
		FM, // Playing on FM
		AM, // Playing on AM
		// 'AM' sub-states
		Play, // Play the current frequency
		AutoTune, // Search for station

	}

	public enum Event {
		TogglePower, // Switch on an off
		ToggleMode, // Switch between AM and FM
		StationLost, // Not station on this AM frequency
		StationFound // Found a station's frequency
	}

	private final HistoryType historyTypeForOn;
	private final HistoryType historyTypeForAM;

	public RadioStateMachineDefinion() {
		this(HistoryType.DEEP, HistoryType.NONE);
	}

	public RadioStateMachineDefinion(final HistoryType historyTypeForOn, final HistoryType historyTypeForAM) {
		super(State.Off);
		this.historyTypeForOn = historyTypeForOn;
		this.historyTypeForAM = historyTypeForAM;
		define();
	}

	@Override
	protected RadioStateMachine createStateMachine(final StateMachine<State, Event> driver) {
		return new RadioStateMachine(driver);
	}

	private void define() {

		in(State.Off).on(Event.TogglePower).goTo(State.On).execute(RadioStateMachine::logTransitionFromOffToOn)
				.onlyIf((sm, p) -> sm.isUserMode());
		in(State.Off).executeOnEntry(RadioStateMachine::logOffEntry);
		in(State.Off).executeOnExit(RadioStateMachine::logOffExit);

		in(State.On).on(Event.TogglePower).goTo(State.Off).execute(RadioStateMachine::logTransitionFromOnToOff);
		in(State.On).executeOnEntry(RadioStateMachine::logOnEntry);
		in(State.On).executeOnExit(RadioStateMachine::logOnExit);

		in(State.Off).on(Event.TogglePower).goTo(State.Maintenance).execute(RadioStateMachine::logTransitionOffToMaintenance)
				.onlyIf((sm, p) -> sm.isMaintenanceMode());

		in(State.Maintenance).on(Event.TogglePower).goTo(State.Off).execute(RadioStateMachine::logTransitionFromMaintenanceToOff);
		in(State.Maintenance).executeOnEntry(RadioStateMachine::logMaintenanceEntry);
		in(State.Maintenance).executeOnExit(RadioStateMachine::logMaintenanceExit);

		defineOnBla();
		defineAM();
	}

	private void defineOnBla() {
		defineHierarchyOn(State.On, State.FM, historyTypeForOn, State.FM, State.AM);

		in(State.FM).on(Event.ToggleMode).goTo(State.AM).execute(RadioStateMachine::logTransitionFromFMToAM);
		in(State.FM).executeOnEntry(RadioStateMachine::logFMEntry);
		in(State.FM).executeOnExit(RadioStateMachine::logFMExit);

		in(State.AM).on(Event.ToggleMode).goTo(State.FM).execute(RadioStateMachine::logTransitionFromAMToFM);
		in(State.AM).executeOnEntry(RadioStateMachine::logAMEntry);
		in(State.AM).executeOnExit(RadioStateMachine::logAMExit);

		
	}

	private void defineAM() {
		defineHierarchyOn(State.AM, State.Play, historyTypeForAM, State.Play, State.AutoTune);

		in(State.Play).on(Event.StationLost).goTo(State.AutoTune).execute(RadioStateMachine::logTransitionFromPlayToAutoTune);
		in(State.Play).executeOnEntry(RadioStateMachine::logPlayEntry);
		in(State.Play).executeOnExit(RadioStateMachine::logPlayExit);

		in(State.AutoTune).on(Event.StationFound).goTo(State.Play).execute(RadioStateMachine::logTransitionFromAutoTuneToPlay);
		in(State.AutoTune).executeOnEntry(RadioStateMachine::logAutoTuneEntry);
		in(State.AutoTune).executeOnExit(RadioStateMachine::logAutoTuneExit);
	}
}
