package org.universaldoctor.msuser.controller;

import model.Profession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.universaldoctor.msuser.service.ProfessionService;

@RestController
@RequestMapping("/profession")
public class ProfessionController {

    private final ProfessionService professionService;

    public ProfessionController(ProfessionService professionService) {
        this.professionService = professionService;
    }

    @PostMapping("/add")
    public ResponseEntity<Profession> save(@RequestBody Profession profession) {
        return ResponseEntity.ok(professionService.save(profession));
    }
}
