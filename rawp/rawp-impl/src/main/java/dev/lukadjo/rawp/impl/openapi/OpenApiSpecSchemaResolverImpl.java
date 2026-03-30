package dev.lukadjo.rawp.impl.openapi;

import dev.lukadjo.rawp.impl.model.RawpEndpoint;
import dev.lukadjo.rawp.impl.registry.RawpEndpointRegistry;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OpenApiSpecSchemaResolverImpl implements OpenApiSpecSchemaResolver {

    private final Map<String, Schema> typeToSchemaMap;

    public OpenApiSpecSchemaResolverImpl(RawpEndpointRegistry rawpEndpointRegistry) {

        typeToSchemaMap = new HashMap<>();
        List<String> allApis = rawpEndpointRegistry.getAllApis();
        for (String api : allApis) {
            List<RawpEndpoint> apiEndpoints = rawpEndpointRegistry.getAllApiEndpoints(api);
            for (RawpEndpoint rawpEndpoint : apiEndpoints) {

                Method method = rawpEndpoint.getMethod();
                for (Parameter parameter : method.getParameters()) {
                    scanClassType(parameter.getType());
                }

            }
        }

    }

    private void scanClassType(Class<?> classType) {


        if (!Arrays.asList(
                void.class, Void.class, String.class,
                int.class, Integer.class, long.class, Long.class,
                boolean.class, Boolean.class
        ).contains(classType)) {
            typeToSchemaMap.put(classType.getTypeName(), buildSchema(classType));
        }
    }

    private Schema buildSchema(Class<?> classType) {
        if (classType == void.class || classType == Void.class) {
            return new Schema<>().type("object");
        } else if (classType == String.class) {
            return new Schema<>().type("string");
        } else if (classType == int.class || classType == Integer.class || classType == long.class || classType == Long.class) {
            return new Schema<>().type("integer");
        } else if (classType == boolean.class || classType == Boolean.class) {
            return new Schema<>().type("boolean");
        }
        Schema schema = new ObjectSchema();
        for (Field field : classType.getDeclaredFields()) {
            schema.addProperty(field.getName(), buildSchema(field.getType()));
        }
        return schema;
    }


    @Override
    public Map<String, Schema> getAllSchemas() {
        return typeToSchemaMap;
    }

    @Override
    public Schema resolveSchema(Class<?> classType) {
        if (classType == void.class || classType == Void.class) {
            return new Schema<>().type("object");
        } else if (classType == String.class) {
            return new Schema<>().type("string");
        } else if (classType == int.class || classType == Integer.class || classType == long.class || classType == Long.class) {
            return new Schema<>().type("integer");
        } else if (classType == boolean.class || classType == Boolean.class) {
            return new Schema<>().type("boolean");
        }
        return new Schema().$ref("#/components/schemas/" + classType.getTypeName());
    }


}
