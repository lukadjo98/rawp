package dev.lukadjo.rawp.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("${rawp.rest.base-path}")
public class RawpController {

    @RequestMapping("/**")
    public String handleRequest(HttpServletRequest request) {
        return "Hello World";
    }

}
