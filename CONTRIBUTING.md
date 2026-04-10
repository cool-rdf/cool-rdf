# Contributing to Cool RDF

Contributions are welcome!

# General process

1. Check that there is an issue that describes the fix or feature you'd like to
   contribute. There should not already be somebody assigned to it and there
   should not already be a PR (open/draft) for it.
2. Fork the repository on GitHub.
3. Create a new branch for your changes starting from the main branch.
4. Make your changes to the code.
5. Make sure you include tests.
6. Make sure the test suite passes after your changes: you can run `mvn verify` to run tests locally.
7. Make sure your code adheres to the code style using the `mvn spotless:check`
   and `mvn checkstyle:check` commands (see next section for more details).
8. Commit your changes into the branch you created.
9. Push your changes to your branch in your forked repository.
10. Use GitHub to submit a pull request (PR) for your contribution. Be sure to
   mention the issue the PR refers to in the format "Fixes #xyz".

# Code style

There are several ways you can check that your contribution adheres to the
project's code style:

1. If you use the Eclipse IDE, you can import
   [coolrdf-eclipse-code-style.xml](.development/coolrdf-eclipse-code-style.xml)
   to as a code style
2. If you use IntelliJ IDE, you can either import
   [coolrdf-intellij-code-style.xml](.development/coolrdf-intellij-code-style.xml)
   (which can be considered an _approximation_ of the code style), or you can
   install the [Adapter for Eclipse Code
   Formatter](https://plugins.jetbrains.com/plugin/6546-adapter-for-eclipse-code-formatter)
   plugin and import the Eclipse code style file
   [coolrdf-eclipse-code-style.xml](.development/coolrdf-eclipse-code-style.xml)
3. Install a plugin for
   [Checkstyle](https://checkstyle.sourceforge.io/) for your preferred IDE and
   import the [coolrdf-checkstyle.xml](.development/coolrdf-checkstyle.xml) style file. Note that the rules checked
   by checkstyle are _additional_ to the code style mentioned above.
4. Alternatively to 1. or 2., run `mvn spotless:check`. You can also run `mvn
   spotless:apply` to automatically fix code formatting.
5. Alternatively to 3., run `mvn checkstyle:check`. Unfortunately, checkstyle
   findings need to fixed manually.
