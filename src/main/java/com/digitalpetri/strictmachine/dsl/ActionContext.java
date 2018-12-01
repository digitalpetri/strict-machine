package com.digitalpetri.strictmachine.dsl;

import com.digitalpetri.strictmachine.FsmContext;

/**
 * The context in which a {@link Action} is being executed.
 * <p>
 * Provides access to the transition criteria: the from state, to state, and event that triggered the transition.
 *
 * @param <S> state type
 * @param <E> event type
 */
public interface ActionContext<S, E> extends FsmContext<S, E> {

    /**
     * Get the state being transitioned from.
     *
     * @return the state being transitioned from.
     */
    S from();

    /**
     * Get the state transitioned to.
     *
     * @return the state transitioned to.
     */
    S to();

    /**
     * Get the event that caused the transition.
     *
     * @return the event that caused the transition.
     */
    E event();

}
