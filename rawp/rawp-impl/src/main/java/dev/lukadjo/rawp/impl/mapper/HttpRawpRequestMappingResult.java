package dev.lukadjo.rawp.impl.mapper;

import dev.lukadjo.rawp.impl.model.RawpRequest;
import lombok.Data;

@Data
public class HttpRawpRequestMappingResult {

    private HttpRawpRequestMappingResultStatus status;
    private String statusMessage;
    private RawpRequest rawpRequest;

}
