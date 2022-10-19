/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.common.function;

import org.wildfly.common.Assert;

/**
 * A two-argument object and integer consumer which can throw an exception.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@FunctionalInterface
public interface ExceptionObjIntConsumer<T, E extends Exception> {
    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first argument
     * @param value the second argument
     * @throws E if an exception occurs
     */
    void accept(T t, int value) throws E;

    default ExceptionObjIntConsumer<T, E> andThen(ExceptionObjIntConsumer<? super T, ? extends E> after) {
        Assert.checkNotNullParam("after", after);
        return (t, v) -> {
            accept(t, v);
            after.accept(t, v);
        };
    }

    default ExceptionObjIntConsumer<T, E> compose(ExceptionObjIntConsumer<? super T, ? extends E> before) {
        Assert.checkNotNullParam("before", before);
        return (t, v) -> {
            before.accept(t, v);
            accept(t, v);
        };
    }

    default ExceptionObjIntConsumer<T, E> andThen(ExceptionObjLongConsumer<? super T, ? extends E> after) {
        Assert.checkNotNullParam("after", after);
        return (t, v) -> {
            accept(t, v);
            after.accept(t, v);
        };
    }

    default ExceptionObjIntConsumer<T, E> compose(ExceptionObjLongConsumer<? super T, ? extends E> before) {
        Assert.checkNotNullParam("before", before);
        return (t, v) -> {
            before.accept(t, v);
            accept(t, v);
        };
    }
}
