/*
 * Copyright 2016-2020 Talsma ICT
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
package nl.talsmasoftware.context;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static nl.talsmasoftware.context.Priorities.inheritedOne;
import static nl.talsmasoftware.context.Priorities.minus3;
import static nl.talsmasoftware.context.Priorities.minus5;
import static nl.talsmasoftware.context.Priorities.noPriority;
import static nl.talsmasoftware.context.Priorities.two;
import static nl.talsmasoftware.context.Priorities.zero;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class PriorityComparatorTest {

    @Test
    public void testPrioritize() {
        List<Object> objects = asList(two, minus5, noPriority, zero, minus3, inheritedOne, zero, two);
        Collections.sort(objects, PriorityComparator.INSTANCE);
        assertThat(objects, contains(zero, zero, inheritedOne, two, two, noPriority, minus3, minus5));
    }

}
