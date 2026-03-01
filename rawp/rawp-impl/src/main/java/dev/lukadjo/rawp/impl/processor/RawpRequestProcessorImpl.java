package dev.lukadjo.rawp.impl.processor;

import dev.lukadjo.rawp.impl.model.RawpEndpoint;
import dev.lukadjo.rawp.impl.model.RawpRequest;
import dev.lukadjo.rawp.impl.registry.RawpEndpointRegistry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RawpRequestProcessorImpl implements RawpRequestProcessor {

    private final RawpEndpointRegistry rawpEndpointRegistry;

    public RawpRequestProcessorImpl(RawpEndpointRegistry rawpEndpointRegistry) {
        this.rawpEndpointRegistry = rawpEndpointRegistry;
    }

    @Override
    public Object processRawpEndpointRequest(RawpRequest rawpRequest) {

        RawpEndpoint rawpEndpoint = rawpEndpointRegistry.matchEndpointForRequest(rawpRequest);
        try {
            List<Object> args = new ArrayList<>();
            if (rawpRequest.getArgs() != null) {
                args = new ArrayList<>(rawpRequest.getArgs().values());
            }
            return rawpEndpoint.getMethod().invoke(rawpEndpoint.getServiceInstance(), args.toArray());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while processing rawp request", e);
        }

    }

}
