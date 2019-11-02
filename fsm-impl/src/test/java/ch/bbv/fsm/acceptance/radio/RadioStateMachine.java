package ch.bbv.fsm.acceptance.radio;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.acceptance.radio.RadioStateMachineDefinion.Event;
import ch.bbv.fsm.acceptance.radio.RadioStateMachineDefinion.State;
import ch.bbv.fsm.impl.AbstractStateMachine;

public class RadioStateMachine extends
		AbstractStateMachine<RadioStateMachine, State, Event> {

	private StringBuilder log = new StringBuilder();
	private boolean maintenance = false;

	public RadioStateMachine(final StateMachine<State, Event> driver) {
		super(driver);
	}

	public RadioStateMachine() {
		super(null);
	}

	public boolean isUserMode() {
		return !maintenance;
	}

	public boolean isMaintenanceMode() {
		return maintenance;
	}

	public void logOffEntry() {
		addOptionalDot();
		log.append("entryOff");
	}

	public void logOnEntry() {
		addOptionalDot();
		log.append("entryOn");
	}

	public void logFMEntry() {
		addOptionalDot();
		log.append("entryFM");
	}

	public void logTransitionFromOffToOn() {
		addOptionalDot();
		log.append("OffToOn");
	}

	public void logOffExit() {
		addOptionalDot();
		log.append("exitOff");
	}

	public void logTransitionFromOnToOff() {
		addOptionalDot();
		log.append("OnToOff");
	}

	public void logOnExit() {
		addOptionalDot();
		log.append("exitOn");
	}

	public void logFMExit() {
		addOptionalDot();
		log.append("exitFM");
	}

	public void logTransitionFromFMToAM() {
		addOptionalDot();
		log.append("FMtoAM");
	}

	public void logTransitionFromAMToFM() {
		addOptionalDot();
		log.append("AMtoFM");
	}

	public void logAMEntry() {
		addOptionalDot();
		log.append("entryAM");
	}

	public void logAMExit() {
		addOptionalDot();
		log.append("exitAM");
	}

	public void logTransitionOffToMaintenance() {
		addOptionalDot();
		log.append("OffToMaintenance");
	}

	public void logTransitionFromMaintenanceToOff() {
		addOptionalDot();
		log.append("MaintenanceToOff");
	}

	public void logMaintenanceEntry() {
		addOptionalDot();
		log.append("entryMaintenance");
	}

	public void logMaintenanceExit() {
		addOptionalDot();
		log.append("exitMaintenance");
	}

	public void logTransitionFromPlayToAutoTune() {
		addOptionalDot();
		log.append("PlayToAutoTune");
	}

	public void logPlayEntry() {
		addOptionalDot();
		log.append("entryPlay");
	}

	public void logPlayExit() {
		addOptionalDot();
		log.append("exitPlay");
	}

	public void logTransitionFromAutoTuneToPlay() {
		addOptionalDot();
		log.append("AutoTuneToPlay");
	}

	public void logAutoTuneEntry() {
		addOptionalDot();
		log.append("entryAutoTune");
	}

	public void logAutoTuneExit() {
		addOptionalDot();
		log.append("exitAutoTune");
	}

	public String consumeLog() {
		final String result = log.toString();
		log = new StringBuilder();
		return result;
	}

	private void addOptionalDot() {
		if (log.length() > 0) {
			log.append('.');
		}
	}

	public void setMaintenance(final boolean maintenance) {
		this.maintenance = maintenance;
	}
}
