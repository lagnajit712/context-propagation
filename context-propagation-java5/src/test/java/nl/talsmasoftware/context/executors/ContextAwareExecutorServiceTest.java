/*
 * Copyright 2016-2019 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.talsmasoftware.context.executors;

import nl.talsmasoftware.context.Context;
import nl.talsmasoftware.context.ContextManagers;
import nl.talsmasoftware.context.DummyContextManager;
import nl.talsmasoftware.context.ThrowingContextManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;

public class ContextAwareExecutorServiceTest {
    private static DummyContextManager dummyContextManager = new DummyContextManager();
    private static ThrowingContextManager throwingContextManager = new ThrowingContextManager();

    private static Callable<String> getDummyContext = new Callable<String>() {
        public String call() {
            Context<String> active = dummyContextManager.getActiveContext();
            return active == null ? null : active.getValue();
        }
    };

    private ContextAwareExecutorService executor;

    @Before
    public void setupExecutor() {
        executor = new ContextAwareExecutorService(Executors.newCachedThreadPool());
    }

    @After
    public void tearDownExecutor() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        executor = null;
    }

    @Before
    @After
    public void clearActiveContexts() {
        ContextManagers.clearActiveContexts();
    }

    @Test
    public void testNoContext() throws ExecutionException, InterruptedException {
        Future<String> dummy = executor.submit(getDummyContext);
        assertThat(dummy.get(), is(nullValue()));
    }

    @Test
    public void testContext() throws ExecutionException, InterruptedException {
        dummyContextManager.initializeNewContext("The quick brown fox jumps over the lazy dog");
        Future<String> dummy = executor.submit(getDummyContext);
        dummyContextManager.initializeNewContext("god yzal eht revo spmuj xof nworb kciuq ehT");
        assertThat(dummyContextManager.getActiveContext().getValue(), is("god yzal eht revo spmuj xof nworb kciuq ehT"));
        assertThat(dummy.get(), is("The quick brown fox jumps over the lazy dog"));
    }

    @Test
    public void testCloseException() throws ExecutionException, InterruptedException {
        throwingContextManager.initializeNewContext("The quick brown fox jumps over the lazy dog");
        ThrowingContextManager.onClose = new IllegalStateException("Sometimes we stare so long at a door that is closing " +
                "that we see too late the one that is open. --Alexander Graham Bell");
        Future<String> dummy = executor.submit(getDummyContext);

        try {
            dummy.get();
            fail("Exception expected");
        } catch (ExecutionException expected) {
            assertThat(expected.getCause().getMessage(), containsString("a door that is closing"));
        }
    }
}
