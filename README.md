# rawp
Java annotation driven library that eliminates HTTP exposure bolerplate. 
Developer declares service as a plain Java interface, and the library automatically binds it to HTTP endpoint at runtime.

## requirements

- jdk 21


## usage

- pom.xml: 
~~~xml
        <dependency>
            <groupId>dev.lukadjo</groupId>
            <artifactId>rawp-all</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
~~~

- main:
~~~java
@SpringBootApplication
@Import({RawpConfig.class}) //<--HERE
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
~~~

- service:
~~~java

@RawpComponent //<--HERE
public interface MyService {

    @RawpMethod(
            name = "path/random",
            api = "demo",
            type = RawpMethodType.GET
    ) //<--HERE
    int getRandomNumber();

}

~~~

## exposure spec

~~~http
curl http://<app_host>:<app_port>/path/random -H "api:demo"
~~~
