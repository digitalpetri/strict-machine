package com.digitalpetri.strictmachine.dsl;

@FunctionalInterface
public interface Action<S, E> {

    /**
     * Execute this action.
     *
     * @param context the {@link ActionContext}.
     */
    void execute(ActionContext<S, E> context);

}
