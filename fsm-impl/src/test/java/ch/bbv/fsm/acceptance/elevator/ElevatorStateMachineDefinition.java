package ch.bbv.fsm.acceptance.elevator;

import ch.bbv.fsm.HistoryType;
import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.acceptance.elevator.ElevatorStateMachineDefinition.Event;
import ch.bbv.fsm.acceptance.elevator.ElevatorStateMachineDefinition.State;
import ch.bbv.fsm.action.FsmAction0;
import ch.bbv.fsm.action.FsmAction1;
import ch.bbv.fsm.guard.Function;
import ch.bbv.fsm.impl.AbstractStateMachineDefinition;

public class ElevatorStateMachineDefinition extends AbstractStateMachineDefinition<ElevatorStateMachine, State, Event> {

	/**
	 * Elevator states.
	 */
	enum State {
		/** Elevator has an Error. */
		Error,

		/** Elevator is healthy, i.e. no error */
		Healthy,

		/** The elevator is moving (either up or down). */
		Moving,

		/** The elevator is moving up. */
		MovingUp,

		/** The elevator is moving down. */
		MovingDown,

		/** The elevator is standing on a floor. */
		OnFloor,

		/** The door is closed while standing still. */
		DoorClosed,

		/** The door is open while standing still. */
		DoorOpen
	}

	/**
	 * Elevator Events.
	 */
	enum Event {
		/** An error occurred. */
		ErrorOccured,

		/** Reset after error. */
		Reset,

		/** Open the door. */
		OpenDoor,

		/** Close the door. */
		CloseDoor,

		/** Move elevator up. */
		GoUp,

		/** Move elevator down. */
		GoDown,

		/** Stop the elevator. */
		Stop
	}

	/**
	 * Announces the floor.
	 */
	FsmAction1<ElevatorStateMachine, State, Event, String> announceFloorAction = (fsm, x) -> System.out.println("announceFloor: 1" + x);

	/**
	 * Announces that the elevator is overloaded.
	 */
	FsmAction0<ElevatorStateMachine, State, Event> announceOverloadAction = fsm -> System.out.println("announceOverload...");

	/**
	 * Checks whether the elevator is overloaded.
	 */
	Function<ElevatorStateMachine, State, Event, Object[], Boolean> overloadFunction = (fsm, argmuments) -> true;

	public ElevatorStateMachineDefinition() {
		super(State.Healthy);
		define();
	}

	private void define() {
		defineHierarchyOn(State.Healthy, State.OnFloor, HistoryType.DEEP, State.OnFloor, State.Moving);
		defineHierarchyOn(State.Moving, State.MovingUp, HistoryType.SHALLOW, State.MovingUp, State.MovingDown);
		defineHierarchyOn(State.OnFloor, State.DoorClosed, HistoryType.NONE, State.DoorClosed, State.DoorOpen);

		in(State.Healthy).on(Event.ErrorOccured).goTo(State.Error);
		in(State.Error).on(Event.Reset).goTo(State.Healthy);
		in(State.OnFloor).executeOnEntry(announceFloorAction, "xxx")//
				.on(Event.CloseDoor).goTo(State.DoorClosed)//
				.on(Event.OpenDoor).goTo(State.DoorOpen)//
				.on(Event.GoUp).goTo(State.MovingUp).onlyIf(overloadFunction)//
				.on(Event.GoUp).execute(announceOverloadAction).on(Event.GoDown).goTo(State.MovingDown).onlyIf(overloadFunction);

		in(State.Moving).on(Event.Stop).goTo(State.OnFloor);
	}

	@Override
	protected ElevatorStateMachine createStateMachine(final StateMachine<State, Event> driver) {
		return new ElevatorStateMachine(driver);
	}
}
