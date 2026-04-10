package dev.lukadjo.rawp.test;

import dev.lukadjo.rawp.impl.mapper.HttpRawpRequestMappingResult;
import dev.lukadjo.rawp.impl.mapper.HttpRawpRequestMappingResultStatus;
import lombok.Getter;

import java.util.Objects;

@Getter
public class RawpTestResponse {

    private final HttpRawpRequestMappingResult mappingResult;
    private final Object body;

    RawpTestResponse(HttpRawpRequestMappingResult mappingResult, Object body) {
        this.mappingResult = mappingResult;
        this.body = body;
    }

    public RawpTestResponse assertOk() {
        if (mappingResult.getStatus() != HttpRawpRequestMappingResultStatus.SUCCESS) {
            throw new AssertionError("Expected mapping status SUCCESS but was "
                    + mappingResult.getStatus() + ": " + mappingResult.getStatusMessage());
        }
        return this;
    }

    public RawpTestResponse assertResponseBody(Object expected) {
        if (!Objects.equals(body, expected)) {
            throw new AssertionError("Expected response body [" + expected + "] but was [" + body + "]");
        }
        return this;
    }

}