package dev.lukadjo.rawp.rest.openapi;

import dev.lukadjo.rawp.impl.openapi.OpenApiSpecGenerator;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@CrossOrigin
@RequestMapping("/swagger")
public class OpenApiSpecController {

    private final OpenApiSpecGenerator openApiSpecGenerator;

    public OpenApiSpecController(OpenApiSpecGenerator openApiSpecGenerator) {
        this.openApiSpecGenerator = openApiSpecGenerator;
    }

    @GetMapping("/{api}.yaml")
    public ResponseEntity<byte[]> downloadSpec(@PathVariable("api") String api) {
        String result = openApiSpecGenerator.generateOpenApiSpec(api);
        HttpHeaders headers = new HttpHeaders();
        // This header triggers the browser to download instead of display
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("spec.yaml").build()
        );
        headers.setContentType(MediaType.TEXT_PLAIN);

        return ResponseEntity.ok()
                .headers(headers)
                .body(result.getBytes(StandardCharsets.UTF_8));
    }

}
