#To build this project use
```shell
    mvn install
```

#PreInstall
```shell
features:install webconsole
features:install camel-jetty
features:install camel-jsonpath
features:install camel-xmljson
features:install spring-orm/3.2.11.RELEASE_1
features:install spring-test/3.2.11.RELEASE_1
features:install spring-tx/3.2.11.RELEASE_1
features:install spring-aspects/3.2.11.RELEASE_1
features:install spring-web-portlet/3.2.11.RELEASE_1
features:install hibernate/4.2.15.Final

```
##3rd party bundles
***这些bundles需要独立手动安装***
>c3p0
```shell
osgi:install -s file:C:\\Users\\Chaos\\Downloads\\org.apache.servicemix.bundles.c3p0-0.9.1.2_1.jar
```
>aspectj
```shell
osgi:install -s file:C:\\Users\\Chaos\\Downloads\\org.apache.servicemix.bundles.aspectj-1.7.4_1.jar
```
>mysql-connector-java-5.1.31
```shell
osgi:install -s file:C:\\Users\\Chaos\\Downloads\\mysql-connector-java-5.1.31.jar
```
`需要将这个jar包拷贝至{servicemix_home}/lib/下`

>RabbitMQ client
osgi:install -s file:C:\\Users\\Chaos\\Downloads\\amqp-client-3.5.4.jar
```

[Optional]use activiti
```shell
features:install activiti
```

#Our Component install sequence
1. common
```shell
osgi:install -s mvn:com.ai.sboss/common/0.0.1
```
2. then install other components
```shell
osgi:install -s mvn:com.ai.sboss/{component_name}/0.0.1
```

#Note for Arrangement model
if classnotfound, should use `dynamic-import bundleid` to import class to servicemix highest classloader

CHANGE LOG: