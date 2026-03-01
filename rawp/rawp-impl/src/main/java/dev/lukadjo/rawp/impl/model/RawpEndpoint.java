package dev.lukadjo.rawp.impl.model;

import dev.lukadjo.rawp.api.RawpMethodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;


/**
 * This will be used for matching from HttpRequest
 * All Rawp endpoints will be registered during startup
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RawpEndpoint {

    private Object serviceInstance;
    private Method method;
    private String methodName;
    private RawpMethodType methodType;
    private String apiName;

}
