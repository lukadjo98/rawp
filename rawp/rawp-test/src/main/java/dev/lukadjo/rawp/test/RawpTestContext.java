package dev.lukadjo.rawp.test;

import dev.lukadjo.rawp.impl.mapper.HttpRawpRequestMapper;
import dev.lukadjo.rawp.impl.mapper.HttpRawpRequestMapperImpl;
import dev.lukadjo.rawp.impl.processor.RawpRequestProcessor;
import dev.lukadjo.rawp.impl.processor.RawpRequestProcessorImpl;
import dev.lukadjo.rawp.impl.registry.RawpEndpointRegistry;
import dev.lukadjo.rawp.impl.registry.RawpEndpointRegistryImpl;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

public class RawpTestContext {

    private final HttpRawpRequestMapper mapper;
    private final RawpRequestProcessor processor;

    private RawpTestContext(HttpRawpRequestMapper mapper, RawpRequestProcessor processor) {
        this.mapper = mapper;
        this.processor = processor;
    }

    public static Builder builder() {
        return new Builder();
    }

    public RawpTestRequestBuilder request() {
        return new RawpTestRequestBuilder(mapper, processor);
    }

    public static class Builder {

        private String groovyScriptPath;
        private final List<Object> components = new ArrayList<>();

        public Builder groovyScript(String path) {
            this.groovyScriptPath = path;
            return this;
        }

        public Builder component(Object... components) {
            this.components.addAll(List.of(components));
            return this;
        }

        public RawpTestContext build() {
            GenericApplicationContext applicationContext = new GenericApplicationContext();
            for (int i = 0; i < components.size(); i++) {
                applicationContext.getBeanFactory().registerSingleton("rawpComponent" + i, components.get(i));
            }
            applicationContext.refresh();

            Resource scriptResource = resolveResource(groovyScriptPath);

            try {
                HttpRawpRequestMapper mapper = new HttpRawpRequestMapperImpl(scriptResource);
                RawpEndpointRegistry registry = new RawpEndpointRegistryImpl(applicationContext);
                RawpRequestProcessor processor = new RawpRequestProcessorImpl(registry);
                return new RawpTestContext(mapper, processor);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to initialize RawpTestContext", e);
            }
        }

        private Resource resolveResource(String path) {
            if (path.startsWith("classpath:")) {
                return new ClassPathResource(path.substring("classpath:".length()));
            }
            return new FileSystemResource(path);
        }
    }
}