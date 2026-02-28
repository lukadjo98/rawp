package dev.lukadjo.rawp.impl.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class HttpRequest {

    private String path;
    private Map<String, List<String>> headers;
    private String httpMethod;
    private String body;

}
