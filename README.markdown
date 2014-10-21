Maven XML Formatter Plugin
==========================

This is a fork of the original Maven [xml-formatter](http://code.google.com/p/xml-formatter/) plugin. This fork ensures the plugin will not overwrite original files unless an actual change has occurred.

You do not need to clone this GitHub repository to use the fork. You can use it immediately by making two simple changes to your project's pom.xml.

First, declare this project as a plugin repository:

<pre>
&lt;pluginRepositories&gt;
  &lt;pluginRepository&gt;
    &lt;id&gt;maven-xml-formatter-plugin&lt;/id&gt;
    &lt;name&gt;Maven2 XML Formatter Plugin repository&lt;/name&gt;
    &lt;url&gt;https://raw.github.com/benalexau/xml-formatter/master/releases/&lt;/url&gt;
  &lt;/pluginRepository&gt;
&lt;/pluginRepositories&gt;
</pre>

Second, enable the plugin. Example configuration settings are shown below which will cause reformatting any time a test is run, but of course you can attach it to any lifecycle phase. Please refer to the [original documentation](http://code.google.com/p/xml-formatter/wiki/Documentation) for more configuration options.

<pre>
&lt;plugin&gt;
    &lt;groupId&gt;org.technicalsoftwareconfigurationmanagement.maven-plugin&lt;/groupId&gt;
    &lt;artifactId&gt;tscm-maven-plugin&lt;/artifactId&gt;
    &lt;version&gt;2.1.0.20111230154050&lt;/version&gt;
    &lt;configuration&gt;
        &lt;includes&gt;
            &lt;include&gt;**/*.xml&lt;/include&gt;
        &lt;/includes&gt;
        &lt;excludes&gt;
            &lt;exclude&gt;**/target/**&lt;/exclude&gt;
            &lt;exclude&gt;checkstyle-config.xml&lt;/exclude&gt;
            &lt;exclude&gt;**/OSGI-INF/**&lt;/exclude&gt;
            &lt;exclude&gt;**/.idea/**&lt;/exclude&gt;
        &lt;/excludes&gt;
    &lt;/configuration&gt;
    &lt;executions&gt;
        &lt;execution&gt;
            &lt;phase&gt;test&lt;/phase&gt;
            &lt;goals&gt;
                &lt;goal&gt;xmlFormatter&lt;/goal&gt;
            &lt;/goals&gt;
        &lt;/execution&gt;
    &lt;/executions&gt;
&lt;/plugin&gt;
</pre>

[Ticket 2](http://code.google.com/p/xml-formatter/issues/detail?id=2) has been logged against the [original project](http://code.google.com/p/xml-formatter/) so they can incorporate these changes. As such, please check the ticket for updates before using this fork.

