package com.turayaros.projectunisa.Controllers;

import com.turayaros.projectunisa.Services.GameService;

import org.springframework.web.bind.annotation.*;
import com.turayaros.projectunisa.*;

import java.util.*;


@RestController
public class ApiControllers {
//
//    private Giocatori player1, player2;

    private final GameService gameService;

    public ApiControllers(GameService gameService) {
        this.gameService = gameService;
    }
    @GetMapping(value = "/w")
    public String getPage(){
        return "welcome";
    }

    @GetMapping(value = "/ready")
    @ResponseBody
    public String ready(@RequestParam String idp) {
        return gameService.rForGame(idp);
    }



    @GetMapping(value = "/start")
    @ResponseBody
    public String startGame(@RequestParam String idp){
        return gameService.startGame(idp);

    }

    @GetMapping(value = "/carte")
    @ResponseBody
    public List<Integer> sendCarte(@RequestParam String idp){
        return gameService.sendCarte(idp);
    }

    @GetMapping(value = "/partitachiusa")
    @ResponseBody
    public String partitaChiusa(@RequestParam String idp){
        return gameService.partitaChiusa(idp);
    }

    @GetMapping(value = "/turno")
    @ResponseBody
    public String sendTurno(@RequestParam String idp){
        return gameService.sendTurno(idp);
    }

    @GetMapping(value = "/setCarta")
    @ResponseBody
    public String sendCarta(@RequestParam String idp, int carta, String player){
       return gameService.sendCarta(idp,carta,player);
    }

    @GetMapping(value = "/getCarta")
    @ResponseBody
    public int getCarta(@RequestParam String idp){
        return gameService.getCarta(idp);
    }

    @GetMapping(value = "/finish")
    @ResponseBody
    public String fine(@RequestParam String idp){
        return gameService.fine(idp);
    }







}

