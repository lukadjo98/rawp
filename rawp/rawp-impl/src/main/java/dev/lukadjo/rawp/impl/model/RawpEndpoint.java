package dev.lukadjo.rawp.impl.model;

import lombok.Data;

import java.lang.reflect.Method;


/**
 * This will be used for matching from HttpRequest
 * All Rawp endpoints will be registered during startup
 */
@Data
public class RawpEndpoint {

    private Object serviceInstance;
    private Method method;

}
