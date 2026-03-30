package dev.lukadjo.rawp.impl.registry;

import dev.lukadjo.rawp.impl.model.RawpEndpoint;
import dev.lukadjo.rawp.impl.model.RawpRequest;

import java.util.List;

public interface RawpEndpointRegistry {

    List<String> getAllApis();

    List<RawpEndpoint> getAllApiEndpoints(String api);

    RawpEndpoint matchEndpointForRequest(RawpRequest request);

}
