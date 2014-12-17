/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Lerch - initial API and implementation.
 */
package cc.recommenders.utils.parser;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

import cc.recommenders.assertions.Throws;
import cc.recommenders.utils.Version;

public class MavenVersionParser implements IVersionParser {

    @Override
    public boolean canParse(final String version) {
        try {
            parse(version);
            return true;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public Version parse(final String version) throws IllegalArgumentException {
        int major = 0;
        int minor = 0;
        int micro = 0;
        String qualifier = "";
        try {
            final StringTokenizer tokenizer = new StringTokenizer(version, ".-", true);
            major = parseInt(tokenizer);
            if (tokenizer.hasMoreTokens()) {
                consumeDelimiter(tokenizer, ".");
                minor = parseInt(tokenizer);
                if (tokenizer.hasMoreTokens()) {
                    consumeDelimiter(tokenizer, ".");
                    micro = parseInt(tokenizer);
                    if (tokenizer.hasMoreTokens()) {
                        consumeDelimiter(tokenizer, "-", ".");
                        qualifier = parseString(tokenizer);
                        // everything that follows after the third separator is treated as being part of the qualifier
                        while (tokenizer.hasMoreElements()) {
                            qualifier += tokenizer.nextToken();
                        }
                    }
                }
            }
        } catch (final NoSuchElementException e) {
            Throws.throwIllegalArgumentException("couldn't convert string into version: '%s'", version);
        }
        return Version.create(major, minor, micro, qualifier);
    }

    private static String parseString(final StringTokenizer st) {
        return st.nextToken();
    }

    private static int parseInt(final StringTokenizer st) {
        return Integer.parseInt(st.nextToken());
    }

    private static void consumeDelimiter(final StringTokenizer st, final String... allowedDelimiters) {
        final String delimiter = st.nextToken();
        for (final String allowedDelimiter : allowedDelimiters) {
            if (delimiter.equals(allowedDelimiter)) {
                return;
            }
        }
        Throws.throwIllegalArgumentException("Unexpected delimiter '%s'; Expected delimiters '%s'", delimiter,
                StringUtils.join(allowedDelimiters, "','"));
    }
}
