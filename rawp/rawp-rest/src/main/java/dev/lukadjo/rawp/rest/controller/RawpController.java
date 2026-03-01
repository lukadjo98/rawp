package dev.lukadjo.rawp.rest.controller;

import dev.lukadjo.rawp.impl.mapper.HttpRawpRequestMapper;
import dev.lukadjo.rawp.impl.mapper.HttpRawpRequestMappingResult;
import dev.lukadjo.rawp.impl.model.HttpRequest;
import dev.lukadjo.rawp.impl.model.RawpRequest;
import dev.lukadjo.rawp.impl.processor.RawpRequestProcessor;
import dev.lukadjo.rawp.rest.exception.HttpRawpRequestMappingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("${rawp.rest.base-path}")
public class RawpController {

    private final HttpRawpRequestMapper httpRawpRequestMapper;
    private final RawpRequestProcessor httpRequestProcessor;

    public RawpController(HttpRawpRequestMapper httpRawpRequestMapper, RawpRequestProcessor httpRequestProcessor) {
        this.httpRawpRequestMapper = httpRawpRequestMapper;
        this.httpRequestProcessor = httpRequestProcessor;
    }

    @RequestMapping("/**")
    public Object handleRequest(HttpServletRequest request) throws IOException {

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setBody(readBody(request));
        httpRequest.setHttpMethod(request.getMethod());
        httpRequest.setPath(request.getServletPath());
        httpRequest.setHeaders(readHeaders(request));

        HttpRawpRequestMappingResult httpMappingResult = httpRawpRequestMapper.mapHttpRequestToRawpRequest(httpRequest);
        switch (httpMappingResult.getStatus()) {

            case SUCCESS -> {
                RawpRequest rawpRequest = httpMappingResult.getRawpRequest();
                return httpRequestProcessor.processRawpEndpointRequest(rawpRequest);
            }
            case INVALID_BODY, INVALID_PATH, INVALID_HEADERS -> {
                throw new HttpRawpRequestMappingException("Bad request!");
            }
            default -> {
                throw new IllegalArgumentException("Unknown http rawp request mapping result status: " + httpMappingResult.getStatus());
            }

        }

    }

    private Map<String, List<String>> readHeaders(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        name -> name,
                        name -> Collections.list(request.getHeaders(name))
                ));
    }

    private String readBody(HttpServletRequest request) throws IOException {

        byte[] bodyBytes = request.getInputStream().readAllBytes();
        return new String(bodyBytes, StandardCharsets.UTF_8);

    }

}
