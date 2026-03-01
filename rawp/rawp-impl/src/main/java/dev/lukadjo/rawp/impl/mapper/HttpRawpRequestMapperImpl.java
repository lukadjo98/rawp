package dev.lukadjo.rawp.impl.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lukadjo.rawp.api.RawpMethodType;
import dev.lukadjo.rawp.impl.model.HttpRequest;
import dev.lukadjo.rawp.impl.model.RawpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Component
public class HttpRawpRequestMapperImpl implements HttpRawpRequestMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public HttpRawpRequestMappingResult mapHttpRequestToRawpRequest(HttpRequest httpRequest) {

        RawpRequest rawpRequest = new RawpRequest();

        String methodPath = httpRequest.getPath();
        if (!StringUtils.hasText(methodPath) || methodPath.length() < 2) {
            return HttpRawpRequestMappingResult.builder()
                    .rawpRequest(null)
                    .status(HttpRawpRequestMappingResultStatus.INVALID_PATH)
                    .statusMessage("Method path not found.")
                    .build();
        } else {
            rawpRequest.setMethodName(methodPath.substring(1));
        }

        List<String> apiHeaders = retrieveApiHeader(httpRequest);
        if (apiHeaders == null || apiHeaders.isEmpty()) {
            return HttpRawpRequestMappingResult.builder()
                    .rawpRequest(null)
                    .status(HttpRawpRequestMappingResultStatus.INVALID_HEADERS)
                    .statusMessage("Api header not found.")
                    .build();
        } else {
            rawpRequest.setApi(apiHeaders.getFirst());
        }

        rawpRequest.setMethodType(retrieveMethodType(httpRequest));


        Map<String, Object> rawpArgs = retrieveArgs(httpRequest);
        if (rawpArgs == null) {
            return HttpRawpRequestMappingResult.builder()
                    .rawpRequest(null)
                    .status(HttpRawpRequestMappingResultStatus.INVALID_BODY)
                    .statusMessage("Http request body not valid")
                    .build();
        } else {
            rawpRequest.setArgs(rawpArgs);
        }


        return HttpRawpRequestMappingResult.builder()
                .rawpRequest(rawpRequest)
                .status(HttpRawpRequestMappingResultStatus.SUCCESS)
                .statusMessage(null)
                .build();
    }

    private Map<String, Object> retrieveArgs(HttpRequest httpRequest) {

        try {
            return objectMapper.readValue(httpRequest.getBody(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return null;
        }

    }

    private static RawpMethodType retrieveMethodType(HttpRequest httpRequest) {
        return switch (httpRequest.getHttpMethod()) {
            case "GET", "get" -> RawpMethodType.GET;
            case "POST", "post" -> RawpMethodType.POST;
            default ->
                    throw new IllegalArgumentException("Rawp method type not defined for http method: " + httpRequest.getHttpMethod());
        };
    }

    private static List<String> retrieveApiHeader(HttpRequest httpRequest) {
        List<String> apiHeaders = httpRequest.getHeaders().get("api");
        return apiHeaders;
    }
}
