package com.digitalpetri.strictmachine.dsl;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ViaBuilder<S, E> {

    private final Predicate<S> fromFilter;
    private final Predicate<S> toFilter;
    private final List<TransitionAction<S, E>> transitionActions;

    ViaBuilder(
        Predicate<S> fromFilter,
        Predicate<S> toFilter,
        List<TransitionAction<S, E>> transitionActions) {

        this.fromFilter = fromFilter;
        this.toFilter = toFilter;
        this.transitionActions = transitionActions;
    }

    public ActionBuilder<S, E> via(E event) {
        return new ActionBuilder<>(
            fromFilter,
            toFilter,
            e -> Objects.equals(e, event),
            transitionActions
        );
    }

    public ActionBuilder<S, E> via(Class<? extends E> eventClass) {
        return new ActionBuilder<>(
            fromFilter,
            toFilter,
            e -> Objects.equals(e.getClass(), eventClass),
            transitionActions
        );
    }

    public ActionBuilder<S, E> via(Predicate<E> eventFilter) {
        return new ActionBuilder<>(
            fromFilter,
            toFilter,
            eventFilter,
            transitionActions
        );
    }

    public ActionBuilder<S, E> viaAny() {
        return new ActionBuilder<>(
            fromFilter,
            toFilter,
            e -> true,
            transitionActions
        );
    }

}
