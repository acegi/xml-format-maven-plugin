This [Maven](https://maven.apache.org/) plugin automatically formats all XML
files in your project. It will skip changing an XML file if it is already in the
correct format.

Internally it uses [Dom4j](https://dom4j.github.io/)'s advanced formatter,
allowing you to easily control [many settings](xml-format-mojo.html) such as the
indentation level, padding, encoding, XML declaration line, newline characters,
encoding and so on. You can use a comment block or
[CDATA](https://en.wikipedia.org/wiki/CDATA) if you'd like to preserve specific
formatting (eg legal messages).

This plugin has been tested with large projects that use many XML files for Maven
POMs and
[Simple Binary Encoding](https://github.com/real-logic/simple-binary-encoding)
schemas. The plugin also uses a test suite to verify the correct formatting of
complex XML files (see
[test XML](https://github.com/acegi/xml-format-maven-plugin/tree/master/src/test/resources)).

Ready to format? Take a look at the [usage instructions](usage.html).
