package dev.lukadjo.rawp.impl.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lukadjo.rawp.impl.model.RawpEndpoint;
import dev.lukadjo.rawp.impl.model.RawpRequest;
import dev.lukadjo.rawp.impl.registry.RawpEndpointRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Component
public class RawpRequestProcessorImpl implements RawpRequestProcessor {

    private final RawpEndpointRegistry rawpEndpointRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RawpRequestProcessorImpl(RawpEndpointRegistry rawpEndpointRegistry) {
        this.rawpEndpointRegistry = rawpEndpointRegistry;
    }

    @Override
    public Object processRawpEndpointRequest(RawpRequest rawpRequest) {

        RawpEndpoint rawpEndpoint = rawpEndpointRegistry.matchEndpointForRequest(rawpRequest);
        try {
            List<Object> args = new ArrayList<>();
            if (rawpRequest.getArgs() != null) {
                Assert.state(rawpRequest.getArgs().size() == rawpEndpoint.getMethod().getParameters().length, "Params number not matched");
                List<Object> requestArgs = new ArrayList<>(rawpRequest.getArgs().values());
                for (int i = 0; i < requestArgs.size(); i++) {
                    args.add(objectMapper.convertValue(requestArgs.get(i), rawpEndpoint.getMethod().getParameters()[i].getType()));
                }
            }

            return rawpEndpoint.getMethod().invoke(rawpEndpoint.getServiceInstance(), args.toArray());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while processing rawp request", e);
        }

    }

}
