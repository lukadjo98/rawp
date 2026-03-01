package dev.lukadjo.rawp.impl.model;

import dev.lukadjo.rawp.api.RawpMethodType;
import lombok.Data;

import java.util.Map;

@Data
public class RawpRequest {

    private String api;
    private String methodName;
    private Map<String, Object> args;
    private RawpMethodType methodType;

}
