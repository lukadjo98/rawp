package dev.lukadjo.rawp.test;

import dev.lukadjo.rawp.impl.mapper.HttpRawpRequestMapper;
import dev.lukadjo.rawp.impl.mapper.HttpRawpRequestMappingResult;
import dev.lukadjo.rawp.impl.mapper.HttpRawpRequestMappingResultStatus;
import dev.lukadjo.rawp.impl.model.HttpRequest;
import dev.lukadjo.rawp.impl.processor.RawpRequestProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RawpTestRequestBuilder {

    private final HttpRawpRequestMapper mapper;
    private final RawpRequestProcessor processor;

    private String path;
    private String httpMethod;
    private String body;
    private final Map<String, List<String>> headers = new HashMap<>();

    RawpTestRequestBuilder(HttpRawpRequestMapper mapper, RawpRequestProcessor processor) {
        this.mapper = mapper;
        this.processor = processor;
    }

    public RawpTestRequestBuilder path(String path) {
        this.path = path;
        return this;
    }

    public RawpTestRequestBuilder method(String method) {
        this.httpMethod = method;
        return this;
    }

    public RawpTestRequestBuilder body(String body) {
        this.body = body;
        return this;
    }

    public RawpTestRequestBuilder header(String name, String value) {
        this.headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
        return this;
    }

    public RawpTestResponse execute() {
        HttpRequest httpRequest = HttpRequest.builder()
                .path(path)
                .httpMethod(httpMethod)
                .body(body)
                .headers(headers)
                .build();

        HttpRawpRequestMappingResult mappingResult = mapper.mapHttpRequestToRawpRequest(httpRequest);

        if (mappingResult.getStatus() != HttpRawpRequestMappingResultStatus.SUCCESS) {
            return new RawpTestResponse(mappingResult, null);
        }

        Object responseBody = processor.processRawpEndpointRequest(mappingResult.getRawpRequest());
        return new RawpTestResponse(mappingResult, responseBody);
    }
}
