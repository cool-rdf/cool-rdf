# turtle-formatter

[![build](https://github.com/atextor/turtle-formatter/actions/workflows/build.yml/badge.svg)](https://github.com/atextor/turtle-formatter/actions/workflows/build.yml) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.atextor/turtle-formatter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.atextor/turtle-formatter) [![codecov](https://codecov.io/gh/atextor/turtle-formatter/branch/main/graph/badge.svg?token=X2YFDI4Z4W)](https://codecov.io/gh/atextor/turtle-formatter) [![License: Apache 2.0](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

**turtle-formatter** is a Java library for pretty printing
[RDF/Turtle](https://www.w3.org/TR/turtle/) documents in a _configurable_ and _reproducible_ way.

It takes as input a formatting style and an [Apache Jena](https://jena.apache.org) Model and
produces as output a pretty-printed RDF/Turtle document.

Starting from version 1.2.0, turtle-formatter is licensed under Apache 2.0. The
current version is 1.2.16.

**Current Status**: The library is feature-complete.

## Why?

### Reproducible Formatting

Every RDF library comes with its own serializers, for example an Apache Jena Model can be written
[in multiple ways](https://jena.apache.org/documentation/io/rdf-output.html), the easiest being
calling the write method on a model itself: `model.write(System.out, "TURTLE")`. However, due to the
nature of RDF, outgoing edges of a node in the graph have no order. When serializing a model, there
are multiple valid ways to do so. For example, the following two models are identical:

<table>
<tr>
<td>

```turtle
@prefix : <http://example.com/> .

:test
  :blorb "blorb" ;
  :floop "floop" .
```

</td>
<td>

```turtle
@prefix : <http://example.com/> .

:test
  :floop "floop" ;
  :blorb "blorb" .
```

</td>
</tr>
</table>

Therefore, when a model is serialized, one of many different (valid) serializations could be the
result. This is a problem when different versions of a model file are compared, for example when
used as artifacts in a git repository. Additionally, serialized files are often formatted in one
style hardcoded in the respective library. So while Apache Jena and for example
[libraptor2](http://librdf.org/raptor/) both write valid RDF/Turtle, the files are formatted
differently. You would not want the code of a project formatted differently in different files,
would you?
**turtle-formatter** addresses these problems by taking care of serialization order and providing a
way to customize the formatting style.

### Nice and Configurable Formatting

Most serializers, while creating valid RDF/Turtle, create _ugly_ formatting. Obviously, what is ugly
and what isn't is highly subjective, so this should be configurable. **turtle-formatter** addresses
this by making the formatting style configurable, e.g. how alignment should be done, where extra
spaces should be inserted and even if indendation is using tabs or spaces. A default style is
provided that reflects sane settings (i.e., the author's opinion). An RDF document formatted using
the default style could look like this:

```turtle
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> . ①
@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix : <http://example.com/relations#> .

:Male a owl:Class ; ②
  owl:disjointWith :Female ; ③
  owl:equivalentClass [ ④
    a owl:Restriction ;
    owl:hasSelf true ; ⑤
    owl:onProperty :isMale ;
  ] ;
  rdfs:subClassOf :Person .

:hasBrother a owl:ObjectProperty ;
  owl:propertyChainAxiom ( :hasSibling :isMale ) ; ⑥
  rdfs:range :Male .

:hasUncle a owl:ObjectProperty, owl:IrreflexiveProperty ; ⑦
  owl:propertyChainAxiom ( :hasParent :hasSibling :hasHusband ) ; ⑦
  owl:propertyChainAxiom ( :hasParent :hasBrother ) ;
  rdfs:range :Male .
```

* ① Prefixes are sorted by common, then custom. They are _not_ aligned on the colon because that
  looks bad when one prefix string is much longer than the others.
* ② `rdf:type` is always written as `a`. It is always the first predicate and written in the same
  line as the subject.
* ③ Indentation is done using a fixed size, like in any other format or language. Predicates are not
  aligned to subjects with an arbitrary length.
* ④ Anonymous nodes are written using the `[ ]` notation whenever possible.
* ⑤ Literal shortcuts are used where possible (e.g. no `"true"^^xsd:boolean`).
* ⑥ RDF Lists are always written using the `( )` notation, no blank node IDs or
  `rdf:next`/`rdf:first` seen here.
* ⑦ The same predicates on the same subjects are repeated rather than using the `,` notation,
  because especially when the objects are longer (nested anonymous nodes), it is difficult to
  understand. The exception to this rule is for different `rdf:type`s.

## Usage

### Usage as a CLI (command line interface)

turtle-formatter itself is only a library and thus intended to be used programmatically, which is
explained in the following sections. However, in the sibling project
[owl-cli](https://github.com/atextor/owl-cli), turtle-formatter is used and can be called using a
command line interface to pretty-print any OWL or RDF document. See owl-cli's [Getting
Started](https://atextor.de/owl-cli/main/snapshot/index.html) to get the tool and the [write command
documentation](https://atextor.de/owl-cli/main/snapshot/usage.html#write-command) to see which
command line switches are available to adjust the formatting.

### Usage as a library

Add the following dependency to your Maven `pom.xml`:
```xml
<dependency>
  <groupId>de.atextor</groupId>
  <artifactId>turtle-formatter</artifactId>
  <version>1.2.16</version>
</dependency>
```

Gradle/Groovy: `implementation 'de.atextor:turtle-formatter:1.2.16'`

Gradle/Kotlin: `implementation("de.atextor:turtle-formatter:1.2.16")`

### Calling the formatter

```java
import java.io.FileInputStream;
import de.atextor.turtle.formatter.FormattingStyle;
import de.atextor.turtle.formatter.TurtleFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

// ...

// Determine formatting style
FormattingStyle style = FormattingStyle.DEFAULT;
TurtleFormatter formatter = new TurtleFormatter(style);
// Build or load a Jena Model.
// Use the style's base URI for loading the model.
Model model = ModelFactory.createDefaultModel();
model.read(new FileInputStream("data.ttl"), style.emptyRdfBase, "TURTLE");
// Either create a string...
String prettyPrintedModel = formatter.apply(model);
// ...or write directly to an OutputStream
formatter.accept(model, System.out);
```

### Customizing the style

Instead of passing `FormattingStyle.DEFAULT`, you can create a custom `FormattingStyle` object.

```java
FormattingStyle style = FormattingStyle.builder(). ... .build();
```

The following options can be set on the FormattingStyle builder:

<table>
<tr>
<td>Option</td>
<td>Description</td>
<td>Default</td>
</tr>

<tr>
<td>

`emptyRdfBase`

</td>
<td>Set the URI that should be left out in formatting. If you don't care about
this, don't change it and use the FormattingStyle's emptyRdfBase field as the
base URI when loading/creating the model that will be formatted, see
<a href="#calling-the-formatter">calling the formatter</a>.

</td>
<td>urn:turtleformatter:internal</td>
</tr>

<tr>
<td>

`alignPrefixes`

</td>
<td>Boolean. Example:

```turtle
# true
@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix example: <http://example.com/> .

# false
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix example: <http://example.com/> .
```

</td>
<td>false</td>
</tr>

<tr>
<td>

`alignPredicates`
`firstPredicate`-
`InNewLine`

</td>
<td>
Boolean. Example:

```turtle
# firstPredicateInNewLine false
# alignPredicates true
:test a rdf:Resource ;
      :blorb "blorb" ;
      :floop "floop" .

# firstPredicateInNewLine false
# alignPredicates false
:test a rdf:Resource ;
  :blorb "blorb" ;
  :floop "floop" .

# firstPredicateInNewLine true
# alignPredicates does not matter
:test
  a rdf:Resource ;
  :blorb "blorb" ;
  :floop "floop" .
```

</td>
<td>false (for both)</td>
</tr>

<tr>
<td>

`alignObjects`

</td>
<td>

Boolean. Example:
```turtle
# alignObjects true
:test
  a           rdf:Resource ;
  :blorb      "blorb" ;
  :floopfloop "floopfloop" .

# alignObjects false
:test
  a rdf:Resource ;
  :blorb "blorb" ;
  :floopfloop "floopfloop" .
```

</td>
<td>
false
</td>
</tr>

<tr>
<td>

`charset`\*

</td>
<td>

One of `LATIN1`, `UTF_16_BE`, `UTF_16_LE`, `UTF_8`, `UTF_8_BOM`

</td>
<td>

`UTF_8`

</td>
</tr>
<tr>
<td>

`doubleFormat`

</td>
<td>

A [NumberFormat](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/NumberFormat.html) that describes how `xsd:double` literals are formatted if `enableDoubleFormatting` is `true`.

</td>
<td>

`0.####E0`

</td>
</tr>
<tr>
<td>

`enableDoubleFormatting`

</td>
<td>

Enables formatting of `xsd:double` values (see `doubleFormat` option)

</td>
<td>

`false`

</td>
</tr>

<tr>
<td>

`endOfLine`\*

</td>
<td>

One of `LF`, `CR`, `CRLF`. If unsure, please see [Newline](https://en.wikipedia.org/wiki/Newline)

</td>
<td>

`LF`

</td>
</tr>
<tr>
<td>

`indentStyle`\*

</td>
<td>

`SPACE` or `TAB`. Note that when choosing `TAB`, `alignPredicates` and `alignObjects` are
automatically treated as `false`.

</td>
<td>

`SPACE`

</td>
</tr>
<tr>
<td>

`quoteStyle`

</td>
<td>

`ALWAYS_SINGLE_QUOTES`, `TRIPLE_QUOTES_FOR_MULTILINE` or `ALWAYS_TRIPLE_QUOTES`.
Determines which quotes should be used for literals. Triple-quoted strings can
contain literal quotes and line breaks.

</td>
<td>

`TRIPLE_QUOTES_FOR_MULTILINE`

</td>
</tr>
<tr>
<td>

`indentSize`\*

</td>
<td>

Integer. When using `indentStyle` `SPACE`, defines the indentation size.

</td>
<td>
2
</td>
</tr>

<tr>
<td>

`insertFinalNewLine`\*

</td>
<td>
Boolean. Determines whether there is a line break after the last line
</td>
<td>
true
</td>
</tr>

<tr>
<td>

`useAForRdfType`

</td>
<td>

Boolean. Determines whether `rdf:type` is written as `a` or as `rdf:type`.

</td>
<td>
true
</td>
</tr>

<tr>
<td>

`keepUnusedPrefixes`

</td>
<td>

Boolean. If `true`, keeps prefixes that are not part of any statement.

</td>
<td>
false
</td>
</tr>

<tr>
<td>

`useCommaByDefault`

</td>
<td>

Boolean. Determines whether to use commas for identical predicates. Example:
```turtle
# useCommaByDefault false
:test a rdf:Resource ;
  :blorb "someBlorb" ;
  :blorb "anotherBlorb" .

# useCommaByDefault true
:test a rdf:Resource ;
  :blorb "someBlorb", "anotherBlorb" .
```

</td>
<td>
false
</td>
</tr>

<tr>
<td>

`commaForPredicate`

</td>
<td>

A set of predicates that, when used multiple times, are separated by commas, even when
`useCommaByDefault` is `false`. Example:

```turtle
# useCommaByDefault false, commaForPredicate contains
# 'rdf:type', firstPredicateInNewLine true
:test a ex:something, owl:NamedIndividual ;
  :blorb "someBlorb" ;
  :blorb "anotherBlorb" .

# useCommaByDefault false, commaForPredicate is empty,
# firstPredicateInNewLine false
:test
  a ex:something ;
  a owl:NamedIndividual ;
  :blorb "someBlorb" ;
  :blorb "anotherBlorb" .
```

</td>
<td>

Set.of(`rdf:type`)

</td>
</tr>

<tr>
<td>

`noCommaForPredicate`

</td>
<td>

Analogous to `commaForPredicate`: A set of predicates that, when used multiple times, are _not_
separated by commas, even when `useCommaByDefault` is `true`.

</td>
<td>
Empty
</td>
</tr>

<tr>
<td>

`prefixOrder`

</td>
<td>

A list of namespace prefixes that defines the order of `@prefix` directives. Namespaces from the
list always appear first (in this order), every other prefix will appear afterwards,
lexicographically sorted. Example:

```turtle
# prefixOrder contains "rdf" and "owl" (in this order), so
# they will appear in this order at the top (when the model
# contains them!), followed by all other namespaces
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix example: <http://example.com/> .
```

</td>
<td>

List.of(`rdf` `rdfs` `xsd` `owl`)

</td>
</tr>

<tr>
<td>

`subjectOrder`

</td>
<td>

A list of resources that determines the order in which subjects appear. For a subject `s` there must
exist a statement `s rdf:type t` in the model and an entry for `t` in the `subjectOrder` list for
the element to be considered in the ordering, i.e., when `subjectOrder` contains `:Foo` and `:Bar`
in that order, the pretty-printed model will show first all `:Foo`s, then all `:Bar`s, then
everything else lexicographically sorted.

</td>
<td>

List.of(`rdfs:Class` `owl:Ontology` `owl:Class` `rdf:Property` `owl:ObjectProperty`
`owl:DatatypeProperty` `owl:AnnotationProperty` `owl:NamedIndividual` `owl:AllDifferent`
`owl:Axiom`)

</td>
</tr>

<tr>
<td>

`predicateOrder`

</td>
<td>

A list of properties that determine the order in which predicates appear for a subject. First all
properties that are in the list are shown in that order, then everything else lexicographically
sorted. For example, when `predicateOrder` contains `:z`, `:y`, `:x` in that order and the subject
has statements for the properties `:a`, `:x` and `:z`:

```turtle
:test
  :z "z" ;
  :x "x" ;
  :a "a" .
```

</td>
<td>

List.of(`rdf:type` `rdfs:label` `rdfs:comment` `dcterms:description`)

</td>
</tr>

<tr>
<td>

`objectOrder`

</td>
<td>

A list of RDFNodes (i.e. resources or literals) that determine the order in which objects appear for
a predicate, when there are multiple statements with the same subject and the same predicate. First
all objects that are in the list are shown in that order, then everything else lexicographically
sorted. For example, when `objectOrder` contains `:Foo` and `:Bar` in that order:

```turtle
:test a :Foo, :Bar .
```

</td>
<td>

List.of(`owl:NamedIndividual` `owl:ObjectProperty` `owl:DatatypeProperty` `owl:AnnotationProperty` `owl:FunctionalProperty` `owl:InverseFunctionalProperty` `owl:TransitiveProperty` `owl:SymmetricProperty` `owl:AsymmetricProperty` `owl:ReflexiveProperty` `owl:IrreflexiveProperty`)

</td>
</tr>

<tr>
<td>

`anonymousNode`-
`IdGenerator`

</td>
<td>

A `BiFunction` that takes a resource (blank node) and an integer (counter) and determines the name
for a blank node in the formatted output, if it needs to be locally named. Consider the following
model:

```turtle
:test :foo _:b0 .
:test2 :bar _:b0 .
```

There is no way to serialize this model in RDF/Turtle while using the inline blank node syntax `[ ]`
for the anonymous node `_:b0`. If, as in this example, the node in question already has a label, the label is re-used.
Otherwise, the anonymousNodeIdGenerator is used to generate it.

</td>
<td>

`(r, i) -> "gen" + i`

</td>
</tr>

<tr>
<td>

{`after`,`before`}
{`Opening`, `Closing`}
{`Parenthesis`, `SquareBrackets`},

{`after`,`before`}
{`Comma`, `Dot`, `Semicolon` }

</td>
<td>

`NEWLINE`, `NOTHING` or `SPACE`. Various options for formatting gaps and line breaks. It is not
recommended to change those, as the default style represents the commonly accepted best practices
for formatting turtle already.

</td>
<td>

Varied

</td>
</tr>

<tr>
<td>

`wrapListItems`

</td>
<td>

`ALWAYS`, `NEVER` or `FOR_LONG_LINES`. Controls how line breaks are added after
elements in RDF lists.

</td>
<td>

`FOR_LONG_LINES`

</td>
</tr>

</table>

\* Adapted from [EditorConfig](https://editorconfig.org/#file-format-details)

## Release Notes
* Unreleased
  * Replace Platform-dependent line terminators in multiline strings with unix style newlines (`\n`, `\r`, and `\r\n` -> `\n`) 
* 1.2.16:
  * Bugfix: Empty RDF lists are formatted as empty set of parenthesis again
* 1.2.15:
  * Bugfix: RDF list nodes containing other properties than `rdf:rest` and
    `rdf:first` are formatted correctly
* 1.2.14:
  * Bugfix: xsd:double numbers are correctly typed even when lexically
    equivalent to decimals
* 1.2.13: 
  * Feature: Skip double formatting
* 1.2.12:
  * Bugfix: Handle RDF lists that start with a non-anonymous node
  * Bugfix: Handle blank node cycles
  * Bugfix: Ensure constant blank node ordering
  * Bugfix: Set Locale for NumberFormat to US
  * Change default `subjectOrder` to show `rdfs:Class` after `owl:Ontology`
* 1.2.11:
  * Bugfix: `rdf:type` is not printed as `a` when used as an object
  * Update all dependencies, including Apache Jena to 4.10.0
* 1.2.10:
  * Configured endOfLine style is honored in prefix formatting
* 1.2.9:
  * The dummy base URI is now configurable in the formatting style. Its default
    value was changed (to `urn:turtleformatter:internal`) to make it a valid URI.
* 1.2.8:
  * Bugfix: Quotes that are the last character in a triple-quoted string are
    escaped correctly
  * New style switch: `FormattingStyle.quoteStyle`
* 1.2.7:
  * Bugfix: URIs and local names are formatted using Jena RIOT; no invalid local
    names are printed any longer
* 1.2.6:
  * Fix typo in FormattingStyle property (`indentPredicates`)
  * Fix alignment of repeated identical predicates
* 1.2.5:
  * Dashes, underscores and full stops in the name part of local names are not
    escaped any more. Technically not a bug fix since both is valid, but it's
    nicer to read
* 1.2.4:
  * Bugfix: Dashes in prefixes of local names are not escaped any more
* 1.2.3:
  * Bugfix: Special characters in local names (curies) and literals are properly escaped
* 1.2.2:
  * Enable writing URIs with an empty base: use `TurtleFormatter.EMPTY_BASE` as
    value for "base" when reading a model using Jena's `model.read()`
  * Update build to Java 17
* 1.2.1:
  * Improve formatting for blank nodes nested in lists
  * Use triple quotes for literals containing line breaks
  * Use Jena's mechanisms for escaping special characters in literals
* 1.2.0:
  * Add `wrapListItems` configuration option
  * Change license from LGPL 3.0 to Apache 2.0
* 1.1.1:
  * Make fields of `FormattingStyle` public, so that `DEFAULT` config is readable
* 1.1.0:
  * Bugfix: Subjects with a `rdf:type` not in `subjectOrder` are rendered correctly
  * Adjust default `subjectOrder` and `predicateOrder`
  * Add new option `keepUnusedPrefixes` and by default render only used prefixes
* 1.0.1: Fix POM so that dependency can be used as jar
* 1.0.0: First version

## Contact

**turtle-formatter** is developed by Andreas Textor <<mail@atextor.de>>.
