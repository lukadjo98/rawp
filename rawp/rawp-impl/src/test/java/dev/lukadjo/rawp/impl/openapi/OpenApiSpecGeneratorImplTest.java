package dev.lukadjo.rawp.impl.openapi;

import dev.lukadjo.rawp.api.RawpComponent;
import dev.lukadjo.rawp.api.RawpMethod;
import dev.lukadjo.rawp.api.RawpMethodType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(OpenApiSpecGeneratorImplTest.MyServiceImpl.class)
public class OpenApiSpecGeneratorImplTest {

    @Autowired
    OpenApiSpecGenerator openApiSpecGenerator;

    //@Test
    void testSpec() {
        String result = openApiSpecGenerator.generateOpenApiSpec("myApi");
        Assertions.assertEquals("", result);
    }

    @RawpComponent
    public interface MyService {

        @RawpMethod(
                api = "myApi",
                type = RawpMethodType.POST,
                name = "printString"
        )
        void printString(String text);

    }

    @TestComponent
    public static class MyServiceImpl implements MyService {


        @Override
        public void printString(String text) {

        }
    }

}
