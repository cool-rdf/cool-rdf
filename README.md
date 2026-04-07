# Cool RDF

[![build](https://github.com/cool-rdf/cool-rdf/actions/workflows/pull-request.yml/badge.svg)](https://github.com/cool-rdf/cool-rdf/actions/workflows/pull-request.yml)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cool.rdf/cool-rdf-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cool.rdf/cool-rdf-parent)

Cool RDF (formerly known as owl-cli and turtle-formatter) is a set of high-level Java libraries
and a command line tool, called `cool`, for working with [RDF](https://en.wikipedia.org/wiki/Resource_Description_Framework)
documents and [OWL](https://en.wikipedia.org/wiki/Web_Ontology_Language) ontologies. Currently you can:

* generate a diagram for an OWL ontology with the [diagram command](https://rdf.cool/cool-rdf/cool-cli.html#diagram-command),
* read any RDF document in [RDF/Turtle](https://www.w3.org/TR/turtle/),
[RDF/XML](https://www.w3.org/TR/rdf-syntax-grammar/),
[RDF N-Triples](https://www.w3.org/TR/n-triples/) or
[N3](https://www.w3.org/TeamSubmission/n3/) format and write it in configurable, pretty-printed
RDF/Turtle or one of the other formats using the [write command](https://rdf.cool/cool-rdf/cool-cli.html#write-command),
* perform OWL DL reasoning on an input ontology using the [infer command](https://rdf.cool/cool-rdf/cool-cli.html#infer-command).

## More information

Installation instructions and the usage guide can be found at [https://rdf.cool/cool-rdf/](https://rdf.cool/cool-rdf/).

If you are interested in how Cool RDF relates to `owl-cli` or `turtle-formatter`, please see
[Legacy Projects](https://rdf.cool/cool-rdf/release-notes.adoc#legacy-projects).
