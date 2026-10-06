package com.turayaros.projectunisa.Controllers;

import com.turayaros.projectunisa.Models.JsonPartita;
import com.turayaros.projectunisa.Models.Partita;
import com.turayaros.projectunisa.Services.GameService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostApiController {

    private final GameService gameService;

    public PostApiController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/getPartita")
    public Partita findPartitaById(
            @RequestBody JsonPartita partitajson) {
        return gameService.findPartitaById(partitajson);
    }

    @PostMapping("/getAnyUpdate")
    public Partita getAnyUpdate(
            @RequestParam String idpartita) {
        return gameService.getAnyUpdate(idpartita);
    }

    @PostMapping("/putData")
    public String postResponseController2(
            @RequestBody Partita p) {
        return gameService.putData(p);
    }

    @PostMapping(value = "/updateData")
    public Partita udapteData(@RequestBody JsonPartita partitaJson) {
        return gameService.updateData(partitaJson);
    }

    @PostMapping(value = "/ready")
    public String rForGame(@RequestParam String idp) {
        return gameService.rForGame(idp);
    }
}
