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
package ch.bbv.fsm.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.bbv.fsm.StateMachine;
import ch.bbv.fsm.events.StateMachineEventHandlerAdapter;
import ch.bbv.fsm.events.TransitionEvent;
import ch.bbv.fsm.impl.StatesAndEvents.Events;
import ch.bbv.fsm.impl.StatesAndEvents.States;

public class TransitionsTest {

  private class TransitionTestStateMachine extends AbstractStateMachine<TransitionTestStateMachine, States, Events> {

    private Object[] arguments1;
    private Object[] arguments2;

    private boolean executed;

    TransitionTestStateMachine(final StateMachine<States, Events> driver) {
      super(driver);
    }

    void setArgument1(final Object[] arguments1) {
      this.arguments1 = arguments1;
    }

    void setArgument2(final Object[] arguments) {
      arguments2 = arguments;
    }

    Object[] getArguments1() {
      return arguments1;
    }

    Object[] getArguments2() {
      return arguments2;
    }

    boolean isExecuted() {
      return executed;
    }

    void setExecuted(final boolean executed) {
      this.executed = executed;
    }

  }

  private class TransitionTestStateMachineDefinition extends AbstractStateMachineDefinition<TransitionTestStateMachine, States, Events> {

    TransitionTestStateMachineDefinition() {
      super(States.A);
      in(States.A).on(Events.A).execute((fsm) -> {
        fsm.setExecuted(true);
      });
      in(States.A).on(Events.B).goTo(States.B).execute(TransitionTestStateMachine::setArgument1).execute(TransitionTestStateMachine::setArgument2);
    }

    @Override
    protected TransitionTestStateMachine createStateMachine(final StateMachine<States, Events> driver) {
      return new TransitionTestStateMachine(driver);
    }

  }



  private class Handler extends StateMachineEventHandlerAdapter<SimpleStateMachine<States, Events>, States, Events> {

    @Override
    public void onTransitionDeclined(final TransitionEvent<SimpleStateMachine<States, Events>, States, Events> arg) {
      TransitionsTest.this.declined = true;

    }
  }

  private boolean declined = false;

  /**
   * Actions on transitions are performed and the event arguments are passed.
   */
  @Test
  public void executeActions() {

    final TransitionTestStateMachine transitionTestStateMachine =
        new TransitionTestStateMachineDefinition().createPassiveStateMachine("executeActions");

    transitionTestStateMachine.start();

    final Object[] eventArguments = new Object[] {1, 2, 3, "test"};
    transitionTestStateMachine.fire(Events.B, new Object[] {eventArguments});

    Assertions.assertThat(transitionTestStateMachine.getArguments1()).isEqualTo(eventArguments);
    Assertions.assertThat(transitionTestStateMachine.getArguments2()).isEqualTo(eventArguments);
  }

  /**
   * Internal transitions can be executed (internal transition = transition that remains in the same
   * state and does not execute exit and entry actions.
   */
  @Test
  public void internalTransition() {

    final TransitionTestStateMachineDefinition stateMachineDefinition = new TransitionTestStateMachineDefinition();
    stateMachineDefinition.in(States.A).on(Events.A).execute((fsm) -> {
      fsm.setExecuted(true);
    });
    final TransitionTestStateMachine fsm = stateMachineDefinition.createPassiveStateMachine("transitionTest", States.A);
    fsm.start();
    fsm.fire(Events.A);

    Assertions.assertThat(fsm.isExecuted()).isTrue();
    Assertions.assertThat(fsm.getCurrentState()).isEqualTo(States.A);
  }

  /**
   * When no transition for the fired event can be found in the entire hierarchy up from the current
   * state then.
   */
  @Test
  public void missingTransition() {
    final SimpleStateMachineDefinition<States, Events> stateMachineDefinition = new SimpleStateMachineDefinition<>(States.A);
    stateMachineDefinition.in(States.A).on(Events.B).goTo(States.B);

    stateMachineDefinition.addEventHandler(new Handler());
    final StateMachine<States, Events> fsm = stateMachineDefinition.createPassiveStateMachine("transitionTest", States.A);
    fsm.start();

    fsm.fire(Events.C);
    Assertions.assertThat(this.declined).isTrue();
    Assertions.assertThat(fsm.getCurrentState()).isEqualTo(States.A);
  }
}
