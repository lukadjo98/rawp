package dev.lukadjo.rawp.impl.mapper;

import dev.lukadjo.rawp.impl.model.HttpRequest;
import org.springframework.stereotype.Component;

@Component
public class HttpRawpRequestMapperImpl implements HttpRawpRequestMapper {

    @Override
    public HttpRawpRequestMappingResult mapHttpRequestToRawpRequest(HttpRequest httpRequest) {

        return HttpRawpRequestMappingResult.builder()
                .rawpRequest(null)
                .status(HttpRawpRequestMappingResultStatus.INVALID_HEADERS)
                .statusMessage("Invalid headers")
                .build();
    }
}
