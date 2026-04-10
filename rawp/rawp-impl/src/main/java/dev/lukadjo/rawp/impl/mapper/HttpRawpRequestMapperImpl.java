package dev.lukadjo.rawp.impl.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dev.lukadjo.rawp.impl.model.HttpRequest;
import dev.lukadjo.rawp.impl.model.RawpRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

@Component
public class HttpRawpRequestMapperImpl implements HttpRawpRequestMapper {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();

    private final ScriptEngine engine;
    private final CompiledScript compiledScript;

    public HttpRawpRequestMapperImpl(
            @Value("classpath:${rawp.mapping.script.path}") Resource scriptResource)
            throws IOException, ScriptException {
        this.engine = new ScriptEngineManager().getEngineByName("groovy");
        if (this.engine == null) {
            throw new IllegalStateException("Groovy script engine not found on classpath");
        }
        try (Reader reader = new InputStreamReader(scriptResource.getInputStream())) {
            this.compiledScript = ((Compilable) this.engine).compile(reader);
        }
    }

    @Override
    public HttpRawpRequestMappingResult mapHttpRequestToRawpRequest(HttpRequest httpRequest) {
        Bindings bindings = engine.createBindings();
        bindings.put("httpRequest", httpRequest);
        bindings.put("jsonMapper", jsonMapper);
        bindings.put("xmlMapper", xmlMapper);

        try {
            Object result = compiledScript.eval(bindings);
            if (result instanceof RawpRequest rawpRequest) {
                return HttpRawpRequestMappingResult.builder()
                        .rawpRequest(rawpRequest)
                        .status(HttpRawpRequestMappingResultStatus.SUCCESS)
                        .statusMessage(null)
                        .build();
            }
            return HttpRawpRequestMappingResult.builder()
                    .rawpRequest(null)
                    .status(HttpRawpRequestMappingResultStatus.INVALID_PATH)
                    .statusMessage("Mapping script did not return a RawpRequest.")
                    .build();
        } catch (ScriptException e) {
            return HttpRawpRequestMappingResult.builder()
                    .rawpRequest(null)
                    .status(HttpRawpRequestMappingResultStatus.INVALID_BODY)
                    .statusMessage("Mapping script error: " + e.getMessage())
                    .build();
        }
    }
}