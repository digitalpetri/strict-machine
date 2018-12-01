package com.digitalpetri.strictmachine.dsl;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ActionFromBuilder<S extends Enum<S>, E> {

    private final Predicate<S> fromFilter;
    private final List<TransitionAction<S, E>> transitionActions;

    ActionFromBuilder(Predicate<S> fromFilter, List<TransitionAction<S, E>> transitionActions) {
        this.fromFilter = fromFilter;
        this.transitionActions = transitionActions;
    }

    public ViaBuilder<S, E> to(S state) {
        return to(s -> Objects.equals(s, state));
    }

    public ViaBuilder<S, E> to(Predicate<S> toFilter) {
        return new ViaBuilder<>(
            fromFilter,
            toFilter,
            transitionActions
        );
    }

    public ViaBuilder<S, E> toAny() {
        return to(s -> true);
    }

}
