/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.lints;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import org.cactoos.iterable.Mapped;

/**
 * Scope of analysis.
 *
 * @since 0.0.1
 */
public final class Scope {

    /**
     * Lints to use.
     */
    private static final Iterable<Lint> LINTS = new Mapped<>(
        LintByXsl::new,
        Arrays.asList(
            "critical/duplicate-names",
            "critical/not-empty-atoms"
        )
    );

    /**
     * Directory with XMIR files.
     */
    private final Path dir;

    /**
     * Ctor.
     * @param path The directory with XMIR files
     */
    public Scope(final Path path) {
        this.dir = path;
    }

    /**
     * Find defects possible defects in XMIR files.
     * @return All defects found
     */
    public Collection<Defect> defects() throws IOException {
        final Objects objects = new ObjectsInDir(this.dir);
        final Collection<Defect> messages = new LinkedList<>();
        for (final Lint lint : Scope.LINTS) {
            for (final String rel : objects.rels()) {
                messages.addAll(lint.violations(objects, rel));
            }
        }
        return messages;
    }

}