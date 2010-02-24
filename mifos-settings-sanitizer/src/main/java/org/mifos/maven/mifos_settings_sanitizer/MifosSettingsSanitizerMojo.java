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

package org.mifos.maven.mifos_settings_sanitizer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Goal which filters a Java .properties file in a specific way useful as part
 * of the process of creating a Mifos release package.
 * <ol>
 * <li>Commented and blank lines are left as-is</li>
 * <li>lines containing any "active" lines are commented out</li>
 * <li>lines starting with a "!" are removed</li>
 * </ol>
 * This is used to filter and "sanitize" our application-wide configuration
 * file.
 * 
 * @goal sanitize
 * @phase package
 */
public class MifosSettingsSanitizerMojo extends AbstractMojo {
    // TODO: allow user to specify end-of-line character(?)
    private static final String EOL = "\n";

    /**
     * Directory where output file will be written.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Input file(name).
     * 
     * @parameter
     * @required
     */
    private File inputFile;

    /**
     * Output file(name).
     * 
     * @parameter
     * @required
     */
    private File outputFile;

    public String sanitizeLine(String line) {
        String output = null;
        if (line.matches("^\\s*#.*") || line.matches("^\\s*$")) {
            output = line + EOL;
            // don't write anything if the line begins with "!" (this
            // skips the warning for the default file)
        } else if (!line.matches("^\\s*(!).*")) {
            output = "#" + line + EOL;
        }
        return output;
    }

    public void execute() throws MojoExecutionException {
        getLog().debug("sanitizing " + inputFile + " to " + outputFile);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        BufferedReader input = null;
        BufferedWriter output = null;

        // TODO: properly deal with default file encoding(s)
        try {
            input = new BufferedReader(new FileReader(inputFile));
            output = new BufferedWriter(new FileWriter(outputFile));
            String line = input.readLine();
            while (line != null) {
                String sanitized = sanitizeLine(line);
                if (null != sanitized) {
                    output.write(sanitized);
                }
                line = input.readLine();
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error: ", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        getLog().debug("sanitization complete.");
    }
}
