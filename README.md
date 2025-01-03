# Collection of Checkers for EO Programs

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/objectionary/lints/actions/workflows/mvn.yml/badge.svg)](https://github.com/objectionary/lints/actions/workflows/mvn.yml)
[![PDD status](http://www.0pdd.com/svg?name=objectionary/lints)](http://www.0pdd.com/p?name=objectionary/lints)
[![Maven Central](https://img.shields.io/maven-central/v/org.eolang/lints.svg)](https://maven-badges.herokuapp.com/maven-central/org.eolang/lints)
[![Javadoc](http://www.javadoc.io/badge/org.eolang/lints.svg)](http://www.javadoc.io/doc/org.eolang/lints)
[![Hits-of-Code](https://hitsofcode.com/github/objectionary/lints)](https://hitsofcode.com/view/github/objectionary/lints)
[![codecov](https://codecov.io/gh/objectionary/lints/graph/badge.svg?token=EdyMcrEuxc)](https://codecov.io/gh/objectionary/lints)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/objectionary/lints/blob/master/LICENSE.txt)

This Java package is a collection of "lints" (aka "checkers") for
[XMIR] (an intermediate representation of a
[EO] program). This is not about static analysis or code
formatting. This is about best practices and readiness of code
for successful compilation and execution.

We use this package as a dependency in the
[EO-to-Java compiler][EO]:

```xml
<dependency>
  <groupId>org.eolang</groupId>
  <artifactId>lints</artifactId>
  <version>0.0.29</version>
</dependency>
```

You can also use it in order to validate the validity
of [XMIR] documents your software may generate:

```java
import com.jcabi.xml.StrictXML;
import org.eolang.lints.Program;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class Foo {
  @Test
  void testValidProgram() {
    Assertions.assertTrue(
      new Program(
        new StrictXML("<program> your XMIR goes here </program>")
      ).defects().isEmpty()
    );
  }
}
```

Then, you can run a whole-program analysis of XMIR files
in your project, using the `Programs` class (there is a
different set of lints to be executed here!):

```java
import java.nio.file.Paths;
import org.eolang.lints.Programs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class Foo {
    @Test
    void testSetOfPrograms() {
        Assertions.assertTrue(
            new Programs(
                Paths.get("xmir-files") // directory with XMIR files
            ).defects().isEmpty()
        );
    }
}
```

It is possible to disable any particular linter in a program,
with the help of the `+unlint` meta.

## Design of This Library

The library is designed as a set of lints, each of which
is a separate class implementing the `Lint` interface.
Each lint is responsible for checking one particular aspect
of the [XMIR] document. The `Program` class is responsible for  
running all lints and collecting defects for a single XMIR file.
The `Programs` class is responsible for running all lints and
collecting defects for a set of XMIR files. All in all,
there are only four classes and interfaces that are supposed to
be exposed to a user of the library:

* `Program` - checker of a single [XMIR]
* `Programs` - checker of a set of [XMIR]
* `Defect` - a single defect discovered
* `Severity` - a severity of a defect

There are also a few classes that implement `Iterable<Lint>`:
`PkMono`, `PkWpa`, and `PkByXsl`.
They are supposed to be used only by the `Program` and `Programs`,
and are not supposed to be exposed to the user of the library.
They are responsible for providing a set of lints to be executed,
building them from the information in classpath.

## Benchmark

Here is the result of linting XMIRs:

<!-- benchmark_begin -->
```text
Input: com/sun/jna/Pointer.class
Size of .class: 22Kb (22Kb bytes)
Size of .xmir after disassemble: 1Mb (1Mb bytes, 29630 lines)
Lint time: 1min (86794 ms)

empty-object (74994 ms)
name-outside-of-abstract-object (5635 ms)
broken-alias-first (1129 ms)
unsorted-metas (819 ms)
alias-too-long (316 ms)
incorrect-bytes-format (275 ms)
duplicate-names (164 ms)
line-is-absent (141 ms)
unknown-name (120 ms)
duplicate-aliases (120 ms)
self-naming (88 ms)
object-has-data (73 ms)
decorated-formation (45 ms)
atom-and-base (45 ms)
sparse-decoration (43 ms)
duplicate-metas (43 ms)
many-free-attributes (40 ms)
atom-in-atom (36 ms)
incorrect-package (35 ms)
schema-is-absent (34 ms)
noname-attribute (34 ms)
mandatory-version (32 ms)
mandatory-home (32 ms)
atom-without-rt (31 ms)
object-line-out-of-listing (30 ms)
atom-with-data (29 ms)
not-empty-atom (28 ms)
comment-is-too-wide (28 ms)
same-line-names (27 ms)
unused-alias (26 ms)
rt-without-atoms (26 ms)
atom-with-phi (26 ms)
alias-without-tail (26 ms)
unknown-rt (25 ms)
broken-ref (25 ms)
broken-alias-second (25 ms)
incorrect-node-rt-location (24 ms)
error-line-out-of-listing (21 ms)
unique-metas (20 ms)
prohibited-package (20 ms)
incorrect-rt-parts (20 ms)
incorrect-jvm-rt-location (20 ms)
comment-without-dot (20 ms)
unit-test-without-phi (19 ms)
incorrect-version (19 ms)
incorrect-architect (19 ms)
comment-not-capitalized (19 ms)
unit-test-is-not-verb (18 ms)
object-does-not-match-filename (18 ms)
incorrect-home (18 ms)
comment-too-short (18 ms)
package-without-tail (17 ms)
mandatory-package (17 ms)
incorrect-test-object-name (17 ms)
zero-version (16 ms)
unknown-metas (16 ms)
meta-line-out-of-listing (16 ms)
ascii-only (15 ms)
package-contains-multiple-parts (14 ms)
global-noname (13 ms)
```

The results were calculated in [this GHA job][benchmark-gha]
on 2025-01-03 at 16:13,
on Linux with 4 CPUs.
<!-- benchmark_end -->

## How to Contribute

Fork repository, make changes, then send us
a [pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).
We will review your changes and apply them to the `master` branch shortly,
provided they don't violate our quality standards. To avoid frustration,
before sending us your pull request please run full Maven build:

```bash
mvn clean install -Pqulice
```

You will need [Maven 3.3+](https://maven.apache.org) and Java 11+ installed.

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
[EO]: https://www.eolang.org
[benchmark-gha]: https://github.com/objectionary/lints/actions/runs/12600510080
