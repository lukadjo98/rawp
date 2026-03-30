package dev.lukadjo.rawp.impl.openapi;

import io.swagger.v3.oas.models.media.Schema;

import java.util.Map;

public interface OpenApiSpecSchemaResolver {

    Map<String, Schema> getAllSchemas();

    Schema resolveSchema(Class<?> classType);

}
