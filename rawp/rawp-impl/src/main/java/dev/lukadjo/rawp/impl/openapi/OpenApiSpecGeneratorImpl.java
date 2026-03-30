package dev.lukadjo.rawp.impl.openapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import dev.lukadjo.rawp.api.RawpMethodType;
import dev.lukadjo.rawp.impl.model.RawpEndpoint;
import dev.lukadjo.rawp.impl.registry.RawpEndpointRegistry;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Component
public class OpenApiSpecGeneratorImpl implements OpenApiSpecGenerator {

    private final YAMLMapper yamlMapper = YAMLMapper.builder()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .addMixIn(MediaType.class, MediaTypeMixin.class)
            .build();
    private final OpenApiSpecSchemaResolver openApiSpecSchemaResolver;
    private final RawpEndpointRegistry rawpEndpointRegistry;

    public OpenApiSpecGeneratorImpl(OpenApiSpecSchemaResolver openApiSpecSchemaResolver, RawpEndpointRegistry rawpEndpointRegistry) {
        this.openApiSpecSchemaResolver = openApiSpecSchemaResolver;
        this.rawpEndpointRegistry = rawpEndpointRegistry;
    }

    abstract class MediaTypeMixin {
        @JsonIgnore
        abstract String getExampleSetFlag();       // skip by getter
    }

    @Override
    public String generateOpenApiSpec(String api) {


        OpenAPI openAPI = new OpenAPI();
        openAPI.setSpecVersion(SpecVersion.V31);
        openAPI.setInfo(generateInfoSegment(api));
        openAPI.setServers(generateServersSegment());
        openAPI.setPaths(generatePathsSegment(api));
        openAPI.setComponents(generateComponents(api));
        try {
            return yamlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(openAPI);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error while serializing spec to yaml", e);
        }
    }

    private Components generateComponents(String api) {
        Components components = new Components();
        components.setSchemas(openApiSpecSchemaResolver.getAllSchemas());
        return components;
    }

    private Paths generatePathsSegment(String api) {

        Paths paths = new Paths();
        List<RawpEndpoint> apiEndpoints = rawpEndpointRegistry.getAllApiEndpoints(api);
        Assert.notNull(apiEndpoints, "Api: " + api + " not registered. Registered apis: " + rawpEndpointRegistry.getAllApis());
        for (RawpEndpoint rawpEndpoint : rawpEndpointRegistry.getAllApiEndpoints(api)) {
            String name = rawpEndpoint.getMethodName();
            PathItem pathItem = new PathItem();
            Operation operation = new Operation();
            operation.setSummary(name);

            List<io.swagger.v3.oas.models.parameters.Parameter> params = new ArrayList<>();

            io.swagger.v3.oas.models.parameters.Parameter headerParam = new io.swagger.v3.oas.models.parameters.Parameter();
            headerParam.setName("api");
            headerParam.setIn("header");
            headerParam.setDescription("api name");
            headerParam.setRequired(true);
            headerParam.setSchema(new Schema<>().type("string"));
            params.add(headerParam);
            if (rawpEndpoint.getMethodType().equals(RawpMethodType.GET)) {
                pathItem.setGet(operation);
            }
            if (rawpEndpoint.getMethodType().equals(RawpMethodType.POST)) {
                RequestBody requestBody = new RequestBody();
                Content content = new Content();
                MediaType mediaType = new MediaType();
                mediaType.setSchema(getParamsSchema(rawpEndpoint.getMethod()));
                content.addMediaType("application/json", mediaType);
                requestBody.setContent(content);
                requestBody.setRequired(true);
                requestBody.setDescription("Request body");
                pathItem.setPost(operation);
                operation.setRequestBody(requestBody);
            }

            operation.setParameters(params);

            ApiResponse response = new ApiResponse();
            response.setDescription("Success");

            ApiResponses apiResponses = new ApiResponses();
            apiResponses.addApiResponse("200", response);
            operation.setResponses(apiResponses);
            paths.addPathItem("/" + name, pathItem);
        }
        return paths;

    }

    private Schema<?> getParamsSchema(Method method) {
        Schema<?> result = new Schema<>();

        result.setType("object");
        Map<String, Schema> properties = new HashMap<>();
        for (Parameter parameter : method.getParameters()) {
            properties.put(parameter.getName(), openApiSpecSchemaResolver.resolveSchema(parameter.getType()));
        }

        result.setProperties(properties);
        return result;

    }

    private List<Server> generateServersSegment() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        return Collections.singletonList(server);
    }

    private Info generateInfoSegment(String api) {
        Info info = new Info();
        info.setTitle(api);
        info.setVersion("3.0.1");
        return info;
    }
}
