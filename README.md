# rawp
Java annotation driven library that eliminates HTTP exposure bolerplate. 
Developer declares service as a plain Java interface, and the library automatically binds it to HTTP endpoint at runtime.

## requirements

- jdk 21


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
            api = "demo",
            type = RawpMethodType.GET
    ) //<--HERE
    int getRandomNumber();

}

~~~

### http exposure spec

~~~http
curl http://<app_host>:<app_port>/path/random -H "api:demo"
~~~

## disclaimer

This tool is part of my internal platform for accelerating the journey from idea to realization. 
It's designed for building simple prototypes (note that advanced features like HTTP interceptors and rate limiting are not supported).

I also use it as a personal knowledge hub, since it's a convenient way to store and revisit boilerplate I'd otherwise forget

## roadmap
- OAuth 2.0 out of the box
- OpenAPI spec out of the box
- Customizable http exposure conventions

## changelog

