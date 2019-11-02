package ch.bbv.fsm.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bbv.fsm.HistoryType;
import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.StateMachineDefinition;
import ch.bbv.fsm.dsl.EntryActionSyntax;
import ch.bbv.fsm.events.StateMachineEventHandler;
import ch.bbv.fsm.impl.internal.driver.ActiveStateMachineDriver;
import ch.bbv.fsm.impl.internal.driver.PassiveStateMachineDriver;
import ch.bbv.fsm.impl.internal.dsl.StateBuilder;
import ch.bbv.fsm.impl.internal.statemachine.state.InternalState;
import ch.bbv.fsm.impl.internal.statemachine.state.StateDictionary;

/**
 * Implementation of the definition of the finite state machine.
 * 
 * @param <S>   the type of the states.
 * @param <E>   the type of the events.
 * @param <FSM> the type of the state machine
 */
public abstract class AbstractStateMachineDefinition<FSM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>>
		implements StateMachineDefinition<FSM, S, E> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractStateMachineDefinition.class);

	/**
	 * Name of this state machine used in log messages.
	 */
	private String name;

	private final List<StateMachineEventHandler<FSM, S, E>> eventHandler;

	private final SimpleStateMachineModel<FSM, S, E> model;

	/**
	 * Initializes the passive state machine.
	 * 
	 * @param initialState the initial state to use
	 */
	public AbstractStateMachineDefinition(final S initialState) {
		this(AbstractStateMachineDefinition.class.getSimpleName(), initialState);
	}

	/**
	 * Initializes the state machine.
	 * 
	 * @param name         the name of the state machine used in the logs.
	 * @param initialState the initial state to use
	 */
	public AbstractStateMachineDefinition(final String name, final S initialState) {
		this.name = name;
		this.model = new SimpleStateMachineModel<>(new StateDictionary<>(), initialState);
		this.eventHandler = new ArrayList<>();
	}

	@Override
	public final S getInitialState() {
		return this.model.getInitialState();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void defineHierarchyOn(final S superStateId, final S initialSubStateId, final HistoryType historyType, final S... subStateIds) {

		final InternalState<FSM, S, E> superState = this.model.getStates().getState(superStateId);
		superState.setHistoryType(historyType);

		for (final S subStateId : subStateIds) {
			final InternalState<FSM, S, E> subState = this.model.getStates().getState(subStateId);
			subState.setSuperState(superState);
			superState.addSubState(subState);
		}

		superState.setInitialState(this.model.getStates().getState(initialSubStateId));
	}

	@Override
	public EntryActionSyntax<FSM, S, E> in(final S state) {
		final InternalState<FSM, S, E> newState = this.model.getStates().getState(state);
		return new StateBuilder<>(newState, this.model.getStates());
	}

	@Override
	public void addEventHandler(final StateMachineEventHandler<FSM, S, E> handler) {
		this.eventHandler.add(handler);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public FSM createActiveStateMachine(final String name, final S initialState) {
		final ActiveStateMachineDriver<FSM, S, E> activeStateMachine = new ActiveStateMachineDriver<>();
		final FSM stateMachine = createStateMachine(activeStateMachine);
		activeStateMachine.initialize(stateMachine, name, this.model.getStates(), initialState, eventHandler);
		return stateMachine;
	}

	@Override
	public FSM createActiveStateMachine(final String name) {
		final ActiveStateMachineDriver<FSM, S, E> activeStateMachine = new ActiveStateMachineDriver<>();
		final FSM stateMachine = createStateMachine(activeStateMachine);
		activeStateMachine.initialize(stateMachine, name, this.model.getStates(), getInitialState(), eventHandler);
		return stateMachine;
	}

	@Override
	public FSM createPassiveStateMachine(final String name, final S initialState) {
		final PassiveStateMachineDriver<FSM, S, E> passiveStateMachine = new PassiveStateMachineDriver<>();
		final FSM stateMachine = createStateMachine(passiveStateMachine);
		passiveStateMachine.initialize(stateMachine, name, this.model.getStates(), initialState, eventHandler);
		return stateMachine;
	}

	@Override
	public FSM createPassiveStateMachine(final String name) {
		final PassiveStateMachineDriver<FSM, S, E> passiveStateMachine = new PassiveStateMachineDriver<>();
		final FSM stateMachine = createStateMachine(passiveStateMachine);
		passiveStateMachine.initialize(stateMachine, name, this.model.getStates(), getInitialState(), eventHandler);
		return stateMachine;
	}

	protected abstract FSM createStateMachine(StateMachine<S, E> driver);
}
