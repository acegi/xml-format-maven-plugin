Maven Java Formatter Plugin for Eclipse 3.7.1 SR1
=================================================

This is an Eclipse 3.7.1 Service Release 1-based fork of the original [maven-java-formatter-plugin](http://code.google.com/p/maven-java-formatter-plugin/). This fork will be of interest to people who prefer the option of code formatting both within Eclipse and also via Maven builds. This plugin ensures consistent formatting results between these two environments, plus can consume source code formatting configuration files exported from newer Eclipse versions.

You do not need to clone this GitHub repository to use the fork. You can use it immediately by making two simple changes to your project's pom.xml.

First, declare this project as a plugin repository:

<pre>
&lt;pluginRepositories&gt;
  &lt;pluginRepository&gt;
    &lt;id&gt;maven-java-formatter-plugin&lt;/id&gt;
    &lt;name&gt;Maven2 Java Formatter Plugin repository&lt;/name&gt;
    &lt;url&gt;https://raw.github.com/benalexau/maven-java-formatter-plugin/master/releases/&lt;/url&gt
  &lt;/pluginRepository&gt;
&lt;/pluginRepositories&gt;
</pre>

Second, enable the plugin. Please refer to the [original documentation](http://maven-java-formatter-plugin.googlecode.com/svn/site/0.3.1/usage.html) for more configuration options.

<pre>
&lt;plugin&gt;
  &lt;groupId&gt;com.googlecode.maven-java-formatter-plugin&lt;/groupId&gt;
  &lt;artifactId&gt;maven-java-formatter-plugin&lt;/artifactId&gt;
  &lt;version&gt;0.4.0.e371sr1&lt;/version&gt;
  &lt;configuration&gt;
    &lt;configFile&gt;../eclipse-formatter-config.xml&lt;/configFile&gt;
  &lt;/configuration&gt;
  &lt;executions&gt;
    &lt;execution&gt;
      &lt;goals&gt;
        &lt;goal&gt;format&lt;/goal&gt;
      &lt;/goals&gt;
    &lt;/execution&gt;
  &lt;/executions&gt;
&lt;/plugin&gt;
</pre>

[Ticket 18](http://code.google.com/p/maven-java-formatter-plugin/issues/detail?id=18&thanks=18&ts=1324185588) has been logged against the [original project](http://code.google.com/p/maven-java-formatter-plugin/) so they can incorporate these changes. As such, please check the ticket for updates before using this fork.

#### XML File Formatting

If you're interested in Java code formatting and Maven, you may also be interested in XML file formatting. The above plugin repository also includes a build of the [XML Formatter](http://code.google.com/p/xml-formatter) Maven plugin. To use this build, add the following additional plugin to your pom.xml and then refer to the [plugin documentation](http://code.google.com/p/xml-formatter/wiki/Documentation) (of course check the original project doesn't have a more recent release available):

<pre>
&lt;plugin&gt;
  &lt;groupId&gt;org.technicalsoftwareconfigurationmanagement.maven-plugin&lt;/groupId&gt;
  &lt;artifactId&gt;tscm-maven-plugin&lt;/artifactId&gt;
  &lt;version&gt;2.1.0.20111218164324&lt;/version&gt;
&lt;/plugin&gt;
</pre>

