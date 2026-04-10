# rawp
Lightweight Java library that exposes annotate service methods as HTTP endpoints (that way removing all of the HTTP exposure boilerplate).

The mapping between incoming HTTP requests and your methods is defined by a Groovy script you provide, making rawp protocol-agnostic. 

The same service can be exposed over REST, SOAP, or any custom convention without touching the service code.
Developer declares service as a plain Java interface, and the library automatically binds it to HTTP endpoint at runtime.

## How it works
1. You annotate your service interface with *@RawpComponent* and mark the methods you want to expose with *@RawpMethod*.
2. You write a Groovy script that translates an incoming http-request into a *RawpRequest*. rawp executes this script for every request.
3. rawp matches the resolved *RawpRequest* to the annotated method and invokes it via reflection, returning the result as the HTTP response.

The groovy script is the only place where protocol conventions live. Switching from REST to SOAP is a matter of swapping the script.


## requirements

- Java 21
- Spring Boot (or a Spring application context)

## maven

Add the GitHub Packages repository to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/lukadjo98/rawp</url>
    </repository>
</repositories>
```

Then add a single dependency:

```xml
<dependency>
    <groupId>dev.lukadjo</groupId>
    <artifactId>rawp-all</artifactId>
    <version>1.0.0</version>
</dependency>
```

> GitHub Packages requires authentication even for public packages. Add your credentials to `~/.m2/settings.xml`:
> ```xml
> <servers>
>     <server>
>         <id>github</id>
>         <username>YOUR_GITHUB_USERNAME</username>
>         <password>YOUR_GITHUB_TOKEN</password>
>     </server>
> </servers>
> ```
> The token needs the `read:packages` scope.


## Getting started

### 1. Annotate your service

Place `@RawpComponent` on the interface and `@RawpMethod` on each method you want to expose:

```java
import dev.lukadjo.rawp.api.RawpComponent;
import dev.lukadjo.rawp.api.RawpMethod;
import dev.lukadjo.rawp.api.RawpMethodType;

@RawpComponent
public interface MathService {

    @RawpMethod(name = "sum", api = "math-api", type = RawpMethodType.POST)
    Integer sum(int a, int b);
}
```

The implementation must be a Spring-managed bean:

```java
import org.springframework.stereotype.Component;

@Component
public class MathServiceImpl implements MathService {

    @Override
    public Integer sum(int a, int b) {
        return a + b;
    }
}
```

### 2. Write a mapping script

Create a Groovy script that reads the incoming HTTP request and produces a `RawpRequest`. The script has three bindings available:

| Binding | Type | Description |
|---|---|---|
| `httpRequest` | `HttpRequest` | The incoming request (path, httpMethod, headers, body) |
| `jsonMapper` | `ObjectMapper` | Jackson JSON mapper |
| `xmlMapper` | `XmlMapper` | Jackson XML mapper |

The script must return a `RawpRequest` instance.


#### REST example (`src/main/resources/rest-mapping.groovy`)

Conventions: path is `/{api}/{methodName}`, body is JSON.

```groovy
import dev.lukadjo.rawp.api.RawpMethodType
import dev.lukadjo.rawp.impl.model.RawpRequest

def parts = httpRequest.path.replaceFirst("^/", "").split("/", 2)
def api = parts[0]
def methodName = parts[1]
def methodType = 'POST'.equalsIgnoreCase(httpRequest.httpMethod) ? RawpMethodType.POST : RawpMethodType.GET
def args = [:]

if (methodType == RawpMethodType.POST && httpRequest.body) {
    args = jsonMapper.readValue(httpRequest.body, Map.class)
}

def r = new RawpRequest()
r.api = api
r.methodName = methodName
r.methodType = methodType
r.args = args
r
```
A `POST /math-api/sum` with body `{"a": 1, "b": 5}` will invoke `MathService.sum(1, 5)`.

#### SOAP example (`src/main/resources/soap-mapping.groovy`)

Conventions: path is `/{api}`, method name comes from the `soapaction` header, body is a SOAP XML envelope.

```groovy
import dev.lukadjo.rawp.api.RawpMethodType
import dev.lukadjo.rawp.impl.model.RawpRequest

def api = (httpRequest.path?.length() > 1) ? httpRequest.path.substring(1) : null
def methodName = httpRequest.headers['soapaction']?.get(0)
def methodType = 'POST'.equalsIgnoreCase(httpRequest.httpMethod) ? RawpMethodType.POST : RawpMethodType.GET
def args = [:]

if (methodType == RawpMethodType.POST && httpRequest.body) {
    def envelope = xmlMapper.readValue(httpRequest.body, Map.class)
    args = envelope?.Body
}

def r = new RawpRequest()
r.api = api
r.methodName = methodName
r.methodType = methodType
r.args = args
r
```

A `POST /math-api` with header `soapaction: sum` and body `<Envelope><Body><a>1</a><b>5</b></Body></Envelope>` will invoke `MathService.sum(1, 5)`.


### 3. Configure the script path

Set the `rawp.mapping.script.path` property in your `application.properties` (or `application.yml`).

```properties
rawp.mapping.script.path=rest-mapping.groovy
```

## Testing

Add `rawp-test` as a test-scoped dependency:

```xml
<dependency>
    <groupId>dev.lukadjo</groupId>
    <artifactId>rawp-test</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

`RawpTestContext` lets you test the full pipeline — script execution, endpoint matching, and method invocation — without starting an HTTP server.

```java
import dev.lukadjo.rawp.test.RawpTestContext;
import org.junit.jupiter.api.Test;

class MathServiceTest {

    @Test
    void sum_rest() {
        RawpTestContext ctx = RawpTestContext.builder()
                .groovyScript("classpath:rawp-mapping.groovy")
                .component(new MathServiceImpl())
                .build();

        ctx.request()
                .method("POST")
                .path("/math-api/sum")
                .body("{\"a\": 1, \"b\": 5}")
                .execute()
                .assertOk()
                .assertResponseBody(6);
    }

    @Test
    void sum_soap() {
        RawpTestContext ctx = RawpTestContext.builder()
                .groovyScript("classpath:rawp-mapping-soap.groovy")
                .component(new MathServiceImpl())
                .build();

        ctx.request()
                .method("POST")
                .path("/math-api")
                .header("soapaction", "sum")
                .body("<Envelope><Body><a>1</a><b>5</b></Body></Envelope>")
                .execute()
                .assertOk()
                .assertResponseBody(6);
    }
}
```
component() accepts multiple instances. All @RawpComponent beans among them are discovered automatically by the registry.

## disclaimer

This tool is part of my internal platform for accelerating the journey from idea to realization. 
It's designed for building simple prototypes (note that advanced features like HTTP interceptors and rate limiting are not supported).

I also use it as a personal knowledge hub, since it's a convenient way to store and revisit boilerplate I'd otherwise forget

## roadmap
- OAuth 2.0 out of the box

## changelog

**[v1.0.0-release]**

### added
- protocol-agnostic java service http exposure
- module for testing
