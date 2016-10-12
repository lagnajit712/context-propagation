/*
 * Copyright (C) 2016 Talsma ICT
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *          http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package nl.talsmasoftware.concurrency.context.function;

import nl.talsmasoftware.concurrency.context.Context;
import nl.talsmasoftware.concurrency.context.ContextSnapshot;

import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

/**
 * A wrapper for {@link Predicate} that {@link ContextSnapshot#reactivate() reactivates a context snapshot} before
 * calling a delegate.
 *
 * @author Sjoerd Talsma
 */
public class PredicateWithContext<T> implements Predicate<T> {
    private static final Logger LOGGER = Logger.getLogger(PredicateWithContext.class.getName());

    private final ContextSnapshot snapshot;
    private final Predicate<T> delegate;

    public PredicateWithContext(ContextSnapshot snapshot, Predicate<T> delegate) {
        this.snapshot = requireNonNull(snapshot, "No context snapshot provided to PredicateWithContext.");
        this.delegate = requireNonNull(delegate, "No delegate provided to PredicateWithContext.");
    }

    @Override
    public boolean test(T t) {
        try (Context<Void> context = snapshot.reactivate()) {
            LOGGER.log(Level.FINEST, "Delegating test method with {0} to {1}.", new Object[]{context, delegate});
            return delegate.test(t);
        }
    }

    @Override
    public Predicate<T> and(Predicate<? super T> other) {
        requireNonNull(other, "Cannot combine predicate with 'and' <null>.");
        return (t) -> {
            try (Context<Void> context = snapshot.reactivate()) {
                LOGGER.log(Level.FINEST, "Delegating 'and' method with {0} to {1}.", new Object[]{context, delegate});
                return delegate.test(t) && other.test(t);
            }
        };
    }

    @Override
    public Predicate<T> or(Predicate<? super T> other) {
        requireNonNull(other, "Cannot combine predicate with 'or' <null>.");
        return (t) -> {
            try (Context<Void> context = snapshot.reactivate()) {
                LOGGER.log(Level.FINEST, "Delegating 'or' method with {0} to {1}.", new Object[]{context, delegate});
                return delegate.test(t) || other.test(t);
            }
        };
    }

}
