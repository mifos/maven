/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.maven.mifos_pseudoloc_gen;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;

public class MifosPseudolocaleMojoTest {
    MifosPseudolocaleMojo mojo = new MifosPseudolocaleMojo();

    @Test
    public void testPseudolocalize() throws Exception {
        assertThat(mojo.pseudolocalize("foo"), is("@@@foo^^^"));
    }
    
    @Test
    public void testCreateOutputFilename() throws Exception {
        assertThat(mojo.createOutputFilename("foo.properties"), is("foo_is_IS.properties"));
    }
}
