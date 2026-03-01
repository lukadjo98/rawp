package dev.lukadjo.rawp.impl.mapper;

import dev.lukadjo.rawp.impl.model.RawpRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HttpRawpRequestMappingResult {

    private HttpRawpRequestMappingResultStatus status;
    private String statusMessage;
    private RawpRequest rawpRequest;

}
