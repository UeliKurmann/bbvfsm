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
import ch.bbv.fsm.impl.internal.driver.Notifier;
import ch.bbv.fsm.impl.internal.driver.PassiveStateMachineDriver;
import ch.bbv.fsm.impl.internal.dsl.StateBuilder;
import ch.bbv.fsm.impl.internal.statemachine.events.ExceptionEventArgsImpl;
import ch.bbv.fsm.impl.internal.statemachine.events.TransitionEventArgsImpl;
import ch.bbv.fsm.impl.internal.statemachine.events.TransitionExceptionEventArgsImpl;
import ch.bbv.fsm.impl.internal.statemachine.state.InternalState;
import ch.bbv.fsm.impl.internal.statemachine.state.StateContext;
import ch.bbv.fsm.impl.internal.statemachine.state.StateDictionary;
import ch.bbv.fsm.impl.internal.statemachine.transition.TransitionContext;
import ch.bbv.fsm.model.StateMachineModel;

/**
 * Implementation of the definition of the finite state machine.
 * 
 * @param <S>             the type of the states.
 * @param <E>             the type of the events.
 * @param <TStateMachine> the type of the state machine
 */
public abstract class AbstractStateMachineDefinition<TStateMachine extends AbstractStateMachine<TStateMachine, S, E>, S extends Enum<?>, E extends Enum<?>>
		implements StateMachineDefinition<TStateMachine, S, E>, Notifier<TStateMachine, S, E> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractStateMachineDefinition.class);

	/**
	 * Name of this state machine used in log messages.
	 */
	private String name;

	private final List<StateMachineEventHandler<TStateMachine, S, E>> eventHandler;

	private final SimpleStateMachineModel<TStateMachine, S, E> simpleStateMachineModel;

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
		this.simpleStateMachineModel = new SimpleStateMachineModel<>(new StateDictionary<>(), initialState);
		this.eventHandler = new ArrayList<>();
	}

	@Override
	public final S getInitialState() {
		return this.simpleStateMachineModel.getInitialState();
	}

	@Override
	public StateMachineModel<TStateMachine, S, E> getModel() {

		return this.simpleStateMachineModel;
	}

	@Override
	public void defineHierarchyOn(final S superStateId, final S initialSubStateId, final HistoryType historyType,
			@SuppressWarnings("unchecked") final S... subStateIds) {
		final InternalState<TStateMachine, S, E> superState = this.simpleStateMachineModel.getStates().getState(superStateId);
		superState.setHistoryType(historyType);

		for (final S subStateId : subStateIds) {
			final InternalState<TStateMachine, S, E> subState = this.simpleStateMachineModel.getStates().getState(subStateId);
			subState.setSuperState(superState);
			superState.addSubState(subState);
		}

		superState.setInitialState(this.simpleStateMachineModel.getStates().getState(initialSubStateId));
	}

	@Override
	public EntryActionSyntax<TStateMachine, S, E> in(final S state) {
		final InternalState<TStateMachine, S, E> newState = this.simpleStateMachineModel.getStates().getState(state);
		return new StateBuilder<>(newState, this.simpleStateMachineModel.getStates());
	}

	@Override
	public void addEventHandler(final StateMachineEventHandler<TStateMachine, S, E> handler) {
		this.eventHandler.add(handler);
	}

	@Override
	public void removeEventHandler(final StateMachineEventHandler<TStateMachine, S, E> handler) {
		this.eventHandler.remove(handler);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public TStateMachine createActiveStateMachine(final String name, final S initialState) {
		final ActiveStateMachineDriver<TStateMachine, S, E> activeStateMachine = new ActiveStateMachineDriver<>();
		final TStateMachine stateMachine = createStateMachine(activeStateMachine);
		activeStateMachine.initialize(stateMachine, name, this.simpleStateMachineModel.getStates(), initialState, eventHandler);
		return stateMachine;
	}

	@Override
	public TStateMachine createActiveStateMachine(final String name) {
		final ActiveStateMachineDriver<TStateMachine, S, E> activeStateMachine = new ActiveStateMachineDriver<>();
		final TStateMachine stateMachine = createStateMachine(activeStateMachine);
		activeStateMachine.initialize(stateMachine, name, this.simpleStateMachineModel.getStates(),
				this.simpleStateMachineModel.getInitialState(), eventHandler);
		return stateMachine;
	}

	@Override
	public TStateMachine createPassiveStateMachine(final String name, final S initialState) {
		final PassiveStateMachineDriver<TStateMachine, S, E> passiveStateMachine = new PassiveStateMachineDriver<>();
		final TStateMachine stateMachine = createStateMachine(passiveStateMachine);
		passiveStateMachine.initialize(stateMachine, name, this.simpleStateMachineModel.getStates(), initialState, eventHandler);
		return stateMachine;
	}

	@Override
	public TStateMachine createPassiveStateMachine(final String name) {
		final PassiveStateMachineDriver<TStateMachine, S, E> passiveStateMachine = new PassiveStateMachineDriver<>();
		final TStateMachine stateMachine = createStateMachine(passiveStateMachine);
		passiveStateMachine.initialize(stateMachine, name, this.simpleStateMachineModel.getStates(),
				this.simpleStateMachineModel.getInitialState(), eventHandler);
		return stateMachine;
	}

	@Override
	public void onExceptionThrown(final StateContext<TStateMachine, S, E> stateContext, final Exception exception) {
		try {
			for (final StateMachineEventHandler<TStateMachine, S, E> handler : this.eventHandler) {
				handler.onExceptionThrown(new ExceptionEventArgsImpl<>(stateContext, exception));
			}
		} catch (final Exception e) {
			LOG.error("Exception during event handler.", e);
		}

	}

	@Override
	public void onExceptionThrown(final TransitionContext<TStateMachine, S, E> transitionContext, final Exception exception) {
		try {
			for (final StateMachineEventHandler<TStateMachine, S, E> handler : this.eventHandler) {
				handler.onTransitionThrowsException(new TransitionExceptionEventArgsImpl<>(transitionContext, exception));
			}
		} catch (final Exception e) {
			LOG.error("Exception during event handler.", e);
		}

	}

	@Override
	public void onTransitionBegin(final StateContext<TStateMachine, S, E> transitionContext) {
		try {
			for (final StateMachineEventHandler<TStateMachine, S, E> handler : this.eventHandler) {
				handler.onTransitionBegin(new TransitionEventArgsImpl<>(transitionContext));
			}
		} catch (final Exception e) {
			onExceptionThrown(transitionContext, e);
		}
	}

	protected abstract TStateMachine createStateMachine(StateMachine<S, E> driver);
}
