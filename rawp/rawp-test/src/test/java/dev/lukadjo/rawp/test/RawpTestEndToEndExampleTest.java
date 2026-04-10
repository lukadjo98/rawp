package dev.lukadjo.rawp.test;

import dev.lukadjo.rawp.api.RawpComponent;
import dev.lukadjo.rawp.api.RawpMethod;
import dev.lukadjo.rawp.api.RawpMethodType;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

public class RawpTestEndToEndExampleTest {

    @Test
    void testExampleSOAP() {

        RawpTestContext ctx = RawpTestContext.builder()
                .groovyScript("classpath:soap.groovy")
                .component(new ExampleServiceImpl())
                .build();

        ctx.request()
                .body("<Envelope><Body><a>1</a><b>5</b></Body></Envelope>")
                .path("/math-api")
                .header("soapaction", "sum")
                .method("POST")
                .execute()
                .assertOk()
                .assertResponseBody(6);

    }


    @Test
    void testExampleJSON() {

        RawpTestContext ctx = RawpTestContext.builder()
                .groovyScript("classpath:rest.groovy")
                .component(new ExampleServiceImpl())
                .build();

        ctx.request()
                .body("{ \"a\": 1, \"b\":5 }")
                .path("/math-api/sum")
                .method("POST")
                .execute()
                .assertOk()
                .assertResponseBody(6);

    }


    @RawpComponent
    public interface ExampleService {

        @RawpMethod(
                name = "sum",
                api = "math-api",
                type = RawpMethodType.POST
        )
        Integer sum(int a, int b);

    }

    @Component
    public static class ExampleServiceImpl implements ExampleService {

        @Override
        public Integer sum(int a, int b) {
            return a + b;
        }
    }

}
