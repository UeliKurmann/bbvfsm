package ch.bbv.fsm.impl.internal.action;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.action.FsmAction1;

/**
 * {@link FsmCall} implementation with one parameter. 
 *
 * @param <SM> the type of the state machine.
 * @param <S> the type of the states.
 * @param <E> the type of the events.
 * @param <P1> the type of the first parameter.
 */
public class FsmCall1<SM extends StateMachine<S, E>, S extends Enum<?>, E extends Enum<?>, P1> implements
    FsmCall<SM, S, E> {
  private final FsmAction1<SM, S, E, P1> action;
  private final P1 p1;

  /**
   * The Constructor.
   * @param action the action.
   * @param p1 the first parameter.
   */
  public FsmCall1(final FsmAction1<SM, S, E, P1> action, final P1 p1) {
    this.action = action;
    this.p1 = p1;
  }

  /**
   * 
   *{@inheritDoc}
   */
  @Override
  public void execOn(final SM fsm) {
    action.exec(fsm, p1);
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void execOn(final SM fsm, final Object... args) {
    action.exec(fsm, (P1) args[0]);
  }
}
