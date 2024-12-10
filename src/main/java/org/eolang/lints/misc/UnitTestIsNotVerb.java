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
package org.eolang.lints.misc;

import com.jcabi.xml.XML;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.eolang.lints.Defect;
import org.eolang.lints.Lint;
import org.eolang.lints.Severity;

/**
 * Lint that checks test object name is a verb in singular.
 *
 * @since 0.0.22
 */
public final class UnitTestIsNotVerb implements Lint<XML> {

    /**
     * The pattern to split kebab case.
     */
    private static final Pattern KEBAB = Pattern.compile("-");

    /**
     * NLP pipeline.
     */
    private final StanfordCoreNLP pipeline;

    /**
     * Ctor.
     * @param props Pipeline properties
     */
    public UnitTestIsNotVerb(final Properties props) {
        this(new StanfordCoreNLP(props));
    }

    /**
     * Ctor.
     */
    public UnitTestIsNotVerb() {
        this(defaults());
    }

    /**
     * Primary ctor.
     * @param pipe NLP pipeline
     */
    public UnitTestIsNotVerb(final StanfordCoreNLP pipe) {
        this.pipeline = pipe;
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        for (final XML object : xmir.nodes("/program[metas/meta[head='tests']]/objects/o[@name]")) {
            final String name = object.xpath("@name").get(0);
            final CoreDocument doc = new CoreDocument(
                Stream
                    .concat(
                        Stream.of("It"),
                        Arrays.stream(UnitTestIsNotVerb.KEBAB.split(name))
                    ).map(s -> s.toLowerCase(Locale.ROOT))
                    .collect(Collectors.joining(" "))
            );
            this.pipeline.annotate(doc);
            if (
                !"VBZ".equals(
                    doc.tokens().get(1).get(CoreAnnotations.PartOfSpeechAnnotation.class)
                )
            ) {
                defects.add(
                    new Defect.Default(
                        "unit-test-is-not-verb",
                        Severity.WARNING,
                        xmir.xpath("/program/@name").stream().findFirst().orElse("unknown"),
                        Integer.parseInt(object.xpath("@line").get(0)),
                        String.format(
                            "Test object name: \"%s\" doesn't start with verb in singular form",
                            name
                        )
                    )
                );
            }
        }
        return defects;
    }

    @Override
    public String motive() throws Exception {
        return new TextOf(
            new ResourceOf(
                "org/eolang/motives/misc/test-object-is-not-verb-in-singular.md"
            )
        ).asString();
    }

    /**
     * Prestructor for default properties.
     * @return Properties.
     */
    private static Properties defaults() {
        final Properties props = new Properties();
        props.setProperty("annotators", "tokenize,pos");
        return props;
    }
}
