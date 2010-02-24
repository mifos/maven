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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Translates strings into a "pseudo"-locale. Translatable strings will be munged to visually stand out, allowing easier
 * identification of untranslatable strings.
 * 
 * @goal pseudolocalize
 * @phase package
 */
public class MifosPseudolocaleMojo extends AbstractMojo {
    /**
     * Directory where output files will be written.
     * 
     * @parameter default-value="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Directory where input files will be read.
     * 
     * @parameter
     * @required
     */
    private File inputDirectory;

    /**
     * Pattern for input filenames. Only files matching this pattern will be filtered.
     * 
     * @parameter default-value="^[A-Za-z]+\.properties$"
     * @required
     */
    private String inputFilenamePattern;

    /**
     * Pseudolocale name.
     * 
     * @parameter default-value="is_IS"
     * @required
     */
    private String pseudolocale = "is_IS";

    protected String pseudolocalize(String input) {
        StringBuffer buffer = new StringBuffer("@@@");
        buffer.append(input);
        buffer.append("^^^");
        return buffer.toString();
    }
    
    protected String createOutputFilename(String inputFile) {
        return inputFile.replaceAll("(\\.properties)$", "_" + pseudolocale + "$1");
    }

    public void execute() throws MojoExecutionException {
        try {
            getLog().info(
                    "pseudo-localizing files matching " + inputFilenamePattern + " in "
                            + inputDirectory.getCanonicalPath() + " to " + outputDirectory.getCanonicalPath());
        } catch (IOException e) {
            throw new MojoExecutionException("error during logging: " + e);
        }
        getLog().info("pseudo-locale is " + pseudolocale);

        if (!outputDirectory.exists())
            outputDirectory.mkdirs();

        String[] files = inputDirectory.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                boolean result = name.matches(inputFilenamePattern);
                getLog().debug("matching " + name + " in " + dir + " ==> " + result);
                return result;
            }
        });

        if (null == files)
            files = new String[] {};

        for (String inputFile : files) {
            SortedProperties defaultProps = new SortedProperties();
            FileInputStream in = null;
            FileOutputStream out = null;
            try {
                in = new FileInputStream(inputDirectory + File.separator + inputFile);
                defaultProps.load(in);

                for (Entry<Object, Object> entry : defaultProps.entrySet()) {
                    String key = (String) entry.getKey();
                    String value = (String) entry.getValue();
                    getLog().debug(key + "=" + value);
                    defaultProps.setProperty(key, pseudolocalize(value));
                }

                String outputFilename = outputDirectory + File.separator + createOutputFilename(inputFile);
                getLog().debug("writing to " + outputFilename);
                out = new FileOutputStream(outputFilename);
                defaultProps.store(out, "---Auto Generated Properties---");
            } catch (IOException e) {
                throw new MojoExecutionException("error during execution: ", e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
                if (in != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        }
    }
}

/**
 * Sorted properties file. This implementation requires that store() internally calls keys().
 */
class SortedProperties extends Properties {

    private static final long serialVersionUID = 5657650728102821923L;

    /**
     * To be compatible with version control systems, we need to sort properties before storing them to disk. Otherwise
     * each change may lead to problems by diff against previous version - because Property entries are randomly
     * distributed (it's a map).
     * 
     * @param keySet
     *            non null set instance to sort
     * @return non null list which contains all given keys, sorted lexicographically. The list may be empty if given set
     *         was empty
     */
    @Override
    @SuppressWarnings("unchecked")
    public Enumeration keys() {
        ArrayList list = new ArrayList(keySet());
        Collections.sort(list);
        return Collections.enumeration(list);
    }

}
