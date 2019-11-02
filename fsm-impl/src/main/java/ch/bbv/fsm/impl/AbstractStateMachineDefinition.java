package ch.bbv.fsm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
 * @param <S>  the type of the states.
 * @param <E>  the type of the events.
 * @param <SM> the type of the state machine
 */
public abstract class AbstractStateMachineDefinition<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>>
		implements StateMachineDefinition<SM, S, E> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractStateMachineDefinition.class);

	private final List<StateMachineEventHandler<SM, S, E>> eventHandler;

	private final SimpleStateMachineModel<SM, S, E> model;

	/**
	 * Initializes the state machine.
	 * 
	 * @param name         the name of the state machine used in the logs.
	 * @param initialState the initial state to use
	 */
	public AbstractStateMachineDefinition(final S initialState) {
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

		final InternalState<SM, S, E> superState = this.model.getStates().getState(superStateId);
		superState.setHistoryType(historyType);

		for (final S subStateId : subStateIds) {
			final InternalState<SM, S, E> subState = this.model.getStates().getState(subStateId);
			subState.setSuperState(superState);
			superState.addSubState(subState);
		}

		superState.setInitialState(this.model.getStates().getState(initialSubStateId));
	}

	@Override
	public EntryActionSyntax<SM, S, E> in(final S state) {
		final InternalState<SM, S, E> newState = this.model.getStates().getState(state);
		return new StateBuilder<>(newState, this.model.getStates());
	}

	@Override
	public void addEventHandler(final StateMachineEventHandler<SM, S, E> handler) {
		LOG.debug("Add EventHandler: {}", handler.getClass());
		this.eventHandler.add(Objects.requireNonNull(handler));
	}

	private StateDictionary<SM, S, E> getStates() {
		return this.model.getStates();
	}

	@Override
	public SM createActiveStateMachine(final String name, final S initialState) {
		final ActiveStateMachineDriver<SM, S, E> activeStateMachine = new ActiveStateMachineDriver<>();
		final SM stateMachine = createStateMachine(activeStateMachine);
		activeStateMachine.initialize(stateMachine, name, getStates(), initialState, eventHandler);
		return stateMachine;
	}

	@Override
	public SM createActiveStateMachine(final String name) {
		final ActiveStateMachineDriver<SM, S, E> activeStateMachine = new ActiveStateMachineDriver<>();
		final SM stateMachine = createStateMachine(activeStateMachine);
		activeStateMachine.initialize(stateMachine, name, getStates(), getInitialState(), eventHandler);
		return stateMachine;
	}

	@Override
	public SM createPassiveStateMachine(final String name, final S initialState) {
		final PassiveStateMachineDriver<SM, S, E> passiveStateMachine = new PassiveStateMachineDriver<>();
		final SM stateMachine = createStateMachine(passiveStateMachine);
		passiveStateMachine.initialize(stateMachine, name, getStates(), initialState, eventHandler);
		return stateMachine;
	}

	@Override
	public SM createPassiveStateMachine(final String name) {
		final PassiveStateMachineDriver<SM, S, E> passiveStateMachine = new PassiveStateMachineDriver<>();
		final SM stateMachine = createStateMachine(passiveStateMachine);
		passiveStateMachine.initialize(stateMachine, name, getStates(), getInitialState(), eventHandler);
		return stateMachine;
	}

	protected abstract SM createStateMachine(StateMachine<S, E> driver);
}
