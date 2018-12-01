package com.digitalpetri.strictmachine.dsl;

import java.util.function.Predicate;

import com.digitalpetri.strictmachine.FsmContext;

class PredicatedTransition<S, E> implements Transition<S, E> {

    private volatile Predicate<FsmContext<S, E>> guard = ctx -> true;

    private final Predicate<S> from;
    private final Predicate<E> via;
    private final S target;

    PredicatedTransition(Predicate<S> from, Predicate<E> via, S target) {
        this.from = from;
        this.via = via;
        this.target = target;
    }

    @Override
    public S target() {
        return target;
    }

    @Override
    public boolean matches(FsmContext<S, E> ctx, S state, E event) {
        return from.test(state) && via.test(event) && guard.test(ctx);
    }

    Predicate<FsmContext<S, E>> getGuard() {
        return guard;
    }

    Predicate<S> getFrom() {
        return from;
    }

    Predicate<E> getVia() {
        return via;
    }

    S getTarget() {
        return target;
    }

    void setGuard(Predicate<FsmContext<S, E>> guard) {
        this.guard = guard;
    }

}
