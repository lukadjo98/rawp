package dev.lukadjo.rawp.impl.mapper;


import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lukadjo.rawp.impl.model.HttpRequest;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpRawpRequestMapperImplTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpRawpRequestMapper httpMapper = new HttpRawpRequestMapperImpl();

    @ParameterizedTest
    @CsvSource(value = {
            "1_httpRequest.json,1_rawpRequest.json",
            "2_httpRequest.json,2_rawpRequest.json"
    })
    void testDummy(String httpRequestFilename, String expectedRawpResponseFilename) throws IOException, JSONException {

        HttpRequest httpRequest = objectMapper.readValue(new File("./src/test/resources/http-rawp-examples/" + httpRequestFilename), HttpRequest.class);
        HttpRawpRequestMappingResult mappingResult = httpMapper.mapHttpRequestToRawpRequest(httpRequest);

        String expectedMappingResultJson = FileUtils.readFileToString(new File("./src/test/resources/http-rawp-examples/" + expectedRawpResponseFilename), StandardCharsets.UTF_8);
        String actualMappingResultJson = objectMapper.writeValueAsString(mappingResult);
        JSONAssert.assertEquals(expectedMappingResultJson, actualMappingResultJson, JSONCompareMode.LENIENT);
    }
}
