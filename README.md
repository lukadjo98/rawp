# rawp
Java annotation driven library that eliminates HTTP exposure bolerplate. 
Developer declares service as a plain Java interface, and the library automatically binds it to HTTP endpoint at runtime.

## requirements

- jdk 21

## maven

~~~xml
	<dependency>
    	<groupId>dev.lukadjo</groupId>
        <artifactId>rawp-all</artifactId>
        <version>0.0.1</version>
	</dependency>

...
    <repositories>
        <repository>
            <id>github</id>
            <url>https://maven.pkg.github.com/lukadjo98/rawp</url>
        </repository>
    </repositories>
~~~

## usage

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
            api = "demo",						//<--HERE
            type = RawpMethodType.GET
    ) 
    int getRandomNumber();

}

~~~

### http exposure example

~~~http
curl http://<app_host>:<app_port>/path/random -H "api:demo"
~~~

## disclaimer

This tool is part of my internal platform for accelerating the journey from idea to realization. 
It's designed for building simple prototypes (note that advanced features like HTTP interceptors and rate limiting are not supported).

I also use it as a personal knowledge hub, since it's a convenient way to store and revisit boilerplate I'd otherwise forget

## roadmap
- OAuth 2.0 out of the box
- OpenAPI spec out of the box **[added in release v0.0.1]**
- Customizable http exposure conventions **[added in release v0.0.2]**

## changelog
### [release v0.0.2]

#### added
- Customizable http exposure conventions
	-	editing rawp-mapping.groovy  (rawp-impl module) affects the way http request is mapped to rawp request


### [release v0.0.1]

#### added 
-	OpenAPI spec exposure out of the box
~~~bash
wget http://<app_host>:<app_port>/swagger/my-api.yaml
~~~
