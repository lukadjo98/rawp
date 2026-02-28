package dev.lukadjo.rawp.impl.model;

import lombok.Data;

import java.util.Map;

@Data
public class RawpRequest {

    private String rawpServiceName;
    private String rawpMethodName;
    private Map<String, Object> args;
    private RawpRequestMethodType methodType;



}
