Maven XML Formatter Plugin
==========================
This Maven plugin automatically formats XML files.

This plugin ensures all your XML files look and feel the same, with consistent
indentation and whitespace conversion. It will avoid modifying files if they
are already in the correct format. You can select to use tabs or four spaces
for indentation.

We have tested this plugin extensively with large projects using many Maven POMs
and Simple Binary Encoding schemas. Just use a comment block or [CDATA](https://en.wikipedia.org/wiki/CDATA) if you'd like to preserve specific
formatting (eg legal messages etc).

Usage
-----
You do not need to clone this GitHub repository to use the fork. You can use it immediately by making two simple changes to your project's pom.xml.

First, declare this project as a plugin repository:

```XML
    <pluginRepositories>
        <pluginRepository>
            <id>maven-xml-formatter-plugin</id>
            <name>Maven2 XML Formatter Plugin repository</name>
            <url>https://raw.github.com/benalexau/xml-formatter/master/releases/</url>
        </pluginRepository>
    </pluginRepositories>
```

Second, enable the plugin. Example configuration settings are shown below which will cause reformatting any time the "verify"
[lifecycle phase](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#Lifecycle_Reference) is run. It also shows the various properties you can customize.

```XML
    <build>
        <plugins>
            <plugin>
                <groupId>org.technicalsoftwareconfigurationmanagement.maven-plugin</groupId>
                <artifactId>tscm-maven-plugin</artifactId>
                <version>2.1.1</version>
                <configuration>
                    <useTabs>false</useTabs>
                    <includes>
                        <include>**/*.xml</include>
                    </includes>
                    <excludes>
                        <exclude>**/target/**</exclude>
                    </excludes>
                </configuration>
                <executions>
                    <execution>
                        <phase>verify</phase>
                        <goals>
                            <goal>xmlFormatter</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

If you would prefer to only run XML formatting on request, you can omit the
``executions`` element above and instead run ``mvn tscm:xmlFormatter``.

History
-------
Originally this project was on Google Code, but it hasn't been maintained since
2011. A patch was submitted upstream back in 2011, but no response was ever
received. With Google Code presently in read-only mode and winding up its
service, this GitHub project is happy to accept improvements to the plugin.

For archival purposes independent of Google Code, a Way Back Machine snapshot of
the original Google Code project can be [viewed here](https://web.archive.org/web/20141229160234/https://code.google.com/p/xml-formatter/). It is noted the displayed license is
[Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
and therefore the ASLv2 license file been added to the source repository here
for licensing clarity.
