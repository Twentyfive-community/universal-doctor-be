package org.universaldoctor.msorchestrator.controller.msuser;

import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import request.profession.AddProfessionReq;
import request.profession.UpdateProfessionReq;

@RestController
@RequestMapping("/profession")
public class ProfessionController {

    private final ProducerTemplate producerTemplate;

    public ProfessionController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @PostMapping("/save")
    public ResponseEntity<String> add(@RequestBody AddProfessionReq addProfessionReq) {
        return ResponseEntity.ok(producerTemplate.requestBody("direct:save", addProfessionReq, String.class));
    }

    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestBody UpdateProfessionReq updateProfessionReq) {
        return ResponseEntity.ok(producerTemplate.requestBody("direct:update", updateProfessionReq, String.class));
    }

    @PutMapping("/toggle-status")
    public ResponseEntity<String> toggleStatus(@RequestParam("professionName") String professionName) {
        return ResponseEntity.ok(producerTemplate.requestBody("direct:toggleStatus", professionName, String.class));
    }

    @GetMapping("/get-by-name")
    public ResponseEntity<String> getByName(@RequestParam("name") String name) {
        return ResponseEntity.ok(producerTemplate.requestBody("direct:getByName", name, String.class));
    }

    @GetMapping("/get-all")
    public ResponseEntity<String> getAll(@RequestParam(defaultValue = "true", name = "active") boolean active) {
        return ResponseEntity.ok(producerTemplate.requestBody("direct:getAll", active, String.class));
    }
}


