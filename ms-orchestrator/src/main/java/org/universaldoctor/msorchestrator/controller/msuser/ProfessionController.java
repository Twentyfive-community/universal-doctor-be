package org.universaldoctor.msorchestrator.controller.msuser;

import model.Profession;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profession")
public class ProfessionController {

    @Autowired
    private ProducerTemplate producerTemplate;

    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestBody Profession profession) {
        return ResponseEntity.ok(producerTemplate.requestBody("direct:add", profession, String.class));
    }
}


