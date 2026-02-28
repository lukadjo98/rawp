package dev.lukadjo.rawp.impl.processor;

import dev.lukadjo.rawp.impl.model.RawpRequest;

public interface RawpRequestProcessor {

    Object processRawpEndpointRequest(RawpRequest rawpRequest);

}
