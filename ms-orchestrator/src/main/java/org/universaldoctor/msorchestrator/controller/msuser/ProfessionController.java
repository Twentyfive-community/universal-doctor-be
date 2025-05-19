package org.universaldoctor.msorchestrator.controller.msuser;

import model.Profession;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profession")
public class ProfessionController {

    private final ProducerTemplate producerTemplate;

    public ProfessionController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestBody Profession profession) {
        return ResponseEntity.ok(producerTemplate.requestBody("direct:add", profession, String.class));
    }
}


