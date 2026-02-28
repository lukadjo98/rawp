package dev.lukadjo.rawp.impl.mapper;

import dev.lukadjo.rawp.impl.model.HttpRequest;

public interface HttpRawpRequestMapper {

    HttpRawpRequestMappingResult mapHttpRequestToRawpRequest(HttpRequest httpRequest);

}
