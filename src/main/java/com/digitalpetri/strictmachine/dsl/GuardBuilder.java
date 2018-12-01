package com.digitalpetri.strictmachine.dsl;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import com.digitalpetri.strictmachine.FsmContext;

public class GuardBuilder<S, E> extends ActionBuilder<S, E> {

    private final PredicatedTransition<S, E> transition;

    GuardBuilder(
        PredicatedTransition<S, E> transition,
        List<TransitionAction<S, E>> transitionActions) {

        super(
            transition.getFrom(),
            s -> Objects.equals(s, transition.getTarget()),
            transition.getVia(),
            transitionActions
        );

        this.transition = transition;
    }

    public ActionBuilder<S, E> guardedBy(Predicate<FsmContext<S, E>> guard) {
        transition.setGuard(guard);

        return this;
    }

}
