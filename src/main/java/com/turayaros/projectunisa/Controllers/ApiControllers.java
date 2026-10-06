package com.turayaros.projectunisa.Controllers;

import com.turayaros.projectunisa.Models.Giocatori;
import com.turayaros.projectunisa.Models.Partita;
import com.turayaros.projectunisa.Services.GameService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.turayaros.projectunisa.*;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class ApiControllers {

    private Giocatori player1, player2;

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
        for (Partita partita : listaPartite)
            if (partita.getIdPartita().contains(idp))
                return partita.getCarte();

        return listaPartite.getFirst().getCarte();
    }
    @GetMapping(value = "/partitachiusa")
    @ResponseBody
    public String partitaChiusa(@RequestParam String idp){
        for (Partita partita : listaPartite)
            if (partita.getIdPartita().contains(idp))
                if(partita.isPartitaFinita()) return "finita";
                else return "ok";
        return "not found";
    }

    @GetMapping(value = "/turno")
    @ResponseBody
    public String sendTurno(@RequestParam String idp){
        for (Partita partita : listaPartite)
            if (partita.getIdPartita().contains(idp))
                return partita.getTurn();
        return "0";
    }

    @GetMapping(value = "/setCarta")
    @ResponseBody
    public String sendCarte(@RequestParam String idp,int carta,String player){
        for (Partita partita : listaPartite) {
            if (partita.getIdPartita().contains(idp)) {
                partita.setCartaP1(carta);
                if(player.contains(partita.getIdFirstPlayer())) partita.setTurn(partita.getIdSecondPlayer());
                else partita.setTurn(partita.getIdFirstPlayer());
                System.out.println(carta);
                return "100";
            }

        }
        return "0";
    }

    @GetMapping(value = "/getCarta")
    @ResponseBody
    public int getCarta(@RequestParam String idp){
        for (Partita partita : listaPartite) {
            if (partita.getIdPartita().contains(idp)) {
                return partita.getCartaP1();
            }

        }
        return 0;
    }

    @GetMapping(value = "/finish")
    @ResponseBody
    public String fine(@RequestParam String idp){
        for (Partita partita : listaPartite) {
            if (partita.getIdPartita().contains(idp)) {
                partita.setPartitaFinita(true);
                return "finita";
            }

        }
        return "not found";
    }







}

