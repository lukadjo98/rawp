package dev.lukadjo.rawp.impl.registry;

import dev.lukadjo.rawp.impl.model.RawpEndpoint;
import dev.lukadjo.rawp.impl.model.RawpRequest;

public interface RawpEndpointRegistry {

    RawpEndpoint matchEndpointForRequest(RawpRequest request);

}
