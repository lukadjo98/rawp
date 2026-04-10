package dev.lukadjo.rawp.impl.mapper;


import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lukadjo.rawp.impl.model.HttpRequest;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.core.io.ClassPathResource;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpRawpRequestMapperImplTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ParameterizedTest
    @CsvSource(value = {
            "rest/1_httpRequest.json, rest/1_rawpRequest.json, rest/rawp-mapping-rest.groovy",
            "soap/2_httpRequest.json, soap/2_rawpRequest.json, soap/rawp-mapping-soap.groovy"
    })
    void testDummy(String httpRequestFilename, String expectedRawpResponseFilename, String mappingScriptName) throws IOException, JSONException, ScriptException {

        HttpRawpRequestMapper httpMapper = new HttpRawpRequestMapperImpl(new ClassPathResource("http-rawp-examples/"+mappingScriptName));


        HttpRequest httpRequest = objectMapper.readValue(new File("./src/test/resources/http-rawp-examples/" + httpRequestFilename), HttpRequest.class);
        HttpRawpRequestMappingResult mappingResult = httpMapper.mapHttpRequestToRawpRequest(httpRequest);

        String expectedMappingResultJson = FileUtils.readFileToString(new File("./src/test/resources/http-rawp-examples/" + expectedRawpResponseFilename), StandardCharsets.UTF_8);
        String actualMappingResultJson = objectMapper.writeValueAsString(mappingResult);
        JSONAssert.assertEquals(expectedMappingResultJson, actualMappingResultJson, JSONCompareMode.LENIENT);
    }
}