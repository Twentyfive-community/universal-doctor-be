package org.universaldoctor.msuser.controller;

import dto.BaseController;
import model.Profession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.universaldoctor.msuser.service.ProfessionService;
import request.profession.AddProfessionReq;
import response.profession.GetAllProfessionRes;
import response.profession.GetByNameProfessionRes;
import request.profession.UpdateProfessionReq;
import response.ResponseWrapper;

@RestController
@RequestMapping("/profession")
public class ProfessionController extends BaseController {

    private final ProfessionService professionService;

    public ProfessionController(ProfessionService professionService) {
        this.professionService = professionService;
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseWrapper<Void>> save(@RequestBody AddProfessionReq addProfessionReq) {
        professionService.save(addProfessionReq);
        return created("profession saved with name " + addProfessionReq.getName());
    }

    @GetMapping("/get-all")
    public ResponseEntity<ResponseWrapper<GetAllProfessionRes>> getAll(@RequestParam(defaultValue = "true", name= "active") boolean active) {
        return ok(professionService.getAll(active),"professions retrieved successfully ");
    }

    @GetMapping("/get-by-name")
    public ResponseEntity<ResponseWrapper<GetByNameProfessionRes>> getByName(@RequestParam("name") String name) {
        return ok(professionService.getByName(name),"profession retrieved successfully ");
    }
    @PutMapping("/update")
    public ResponseEntity<ResponseWrapper<Void>> update(@RequestBody UpdateProfessionReq updateProfessionReq) {
        professionService.update(updateProfessionReq);
        return updated("profession updated with name " + updateProfessionReq.getNewName());
    }

    @PutMapping("/toggle-status")
    public ResponseEntity<ResponseWrapper<Void>> toggleStatus(@RequestParam("professionName") String professionName) {
        Profession profession =professionService.toggleStatus(professionName);
        return updated("Now " +profession.getName()+" has status: " +profession.getActive());
    }
}
