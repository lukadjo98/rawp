package dev.lukadjo.rawp.impl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HttpRequest {

    private String path;
    private Map<String, List<String>> headers;
    private String httpMethod;
    private String body;

}
