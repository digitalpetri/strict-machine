package com.digitalpetri.strictmachine.dsl;

public interface TransitionAction<S, E> {

    /**
     * Execute the {@link Action}s backing this TransitionAction.
     *
     * @param context the {@link ActionContext}.
     */
    void execute(ActionContext<S, E> context);

    /**
     * Test whether this TransitionAction is applicable to the transition criteria.
     *
     * @param from  the state transitioned from.
     * @param to    the state transitioned to.
     * @param event the event that caused the transition.
     * @return {@code true} if this transition action is applicable to the transition criteria.
     */
    boolean matches(S from, S to, E event);

}
