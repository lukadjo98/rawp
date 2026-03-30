package dev.lukadjo.rawp.impl.registry;

import dev.lukadjo.rawp.api.RawpComponent;
import dev.lukadjo.rawp.api.RawpMethod;
import dev.lukadjo.rawp.impl.model.RawpEndpoint;
import dev.lukadjo.rawp.impl.model.RawpRequest;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

@Component
public class RawpEndpointRegistryImpl implements RawpEndpointRegistry {

    private final Map<String, List<RawpEndpoint>> allEndpointsByApi;

    public RawpEndpointRegistryImpl(ApplicationContext applicationContext) {
        allEndpointsByApi = new HashMap<>();

        String[] rawpComponentNames = applicationContext.getBeanNamesForAnnotation(RawpComponent.class);
        for (String rawpComponentName : rawpComponentNames) {
            Object rawpComponent = applicationContext.getBean(rawpComponentName);
            Class<?> beanClass = AopUtils.getTargetClass(rawpComponent);
            Arrays.stream(beanClass.getDeclaredMethods()).forEach(m -> {
                RawpMethod annotationValue = AnnotationUtils.findAnnotation(m, RawpMethod.class);
                if (annotationValue != null) {
                    String apiName = annotationValue.api();
                    if (!allEndpointsByApi.containsKey(apiName)) {
                        allEndpointsByApi.put(apiName, new ArrayList<>());
                    }
                    List<RawpEndpoint> apiEndpoints = allEndpointsByApi.get(apiName);
                    apiEndpoints.add(RawpEndpoint.builder().serviceInstance(rawpComponent).method(m).methodName(annotationValue.name()).apiName(apiName).methodType(annotationValue.type()).build());
                }
            });

        }

    }

    @Override
    public List<String> getAllApis() {
        return new ArrayList<>(allEndpointsByApi.keySet());
    }

    @Override
    public List<RawpEndpoint> getAllApiEndpoints(String api) {
        return allEndpointsByApi.get(api);
    }

    @Override
    public RawpEndpoint matchEndpointForRequest(RawpRequest request) {
        List<RawpEndpoint> rawpEndpoints = allEndpointsByApi.values().stream().flatMap(List::stream).filter(re -> re.getMethodName().equals(request.getMethodName()) && re.getApiName().equals(request.getApi()) && re.getMethodType().equals(request.getMethodType())).toList();

        Assert.state(!rawpEndpoints.isEmpty(), "Rawp endpoint not matched:" + " method type: " + request.getMethodType() + " api:" + request.getApi() + " methodName: " + request.getMethodName());
        Assert.state(rawpEndpoints.size() == 1, "Multiple rawp endpoints matched:" + " method type: " + request.getMethodType() + " api:" + request.getApi() + " methodName: " + request.getMethodName());

        return rawpEndpoints.getFirst();
    }

}
