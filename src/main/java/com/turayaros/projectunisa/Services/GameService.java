package com.turayaros.projectunisa.Services;

import com.turayaros.projectunisa.Models.Giocatori;
import com.turayaros.projectunisa.Models.Partita;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.logging.Logger;

@Service
public class GameService {
    private Giocatori player1, player2;
    private  List<Giocatori> listaplayer = new ArrayList<>();
    private  List<Partita> listaPartite = new ArrayList<>();
    private Map<String,String> pgmap = new HashMap<>();
    private String turn;

    private static final Logger logger = Logger.getLogger(GameService.class.getName());



    public String rForGame( String idp){
        logger.info(idp);
        if(pgmap.containsKey(idp)) return pgmap.get(idp);
        else{
            listaplayer.add(new Giocatori((idp)));
            if(listaplayer.size()%2 == 0) readyForGame();
            return "new";
        }

    }

    public String startGame( String idp) {
        if (!pgmap.containsKey(idp)) return "wait";
        else return pgmap.get(idp);
    }


    public void readyForGame(){
        player1 = listaplayer.get(listaplayer.size()-2);
        player2 = listaplayer.getLast();
        player1.setReadyForGame(true);
        player2.setReadyForGame(true);
        turn = player1.getId();

        List<Integer> l = carte();
        Partita p = new Partita(player1.getId(),player2.getId(),player1.getId()+player2.getId(),turn,l);
        p.setPartitaFinita(false);
        p.setCartaP1(-1);
        p.setCartaP2(-1);
        System.out.println(p.getIdPartita());
        pgmap.put(player1.getId(),p.getIdPartita());
        pgmap.put(player2.getId(),p.getIdPartita());
        listaPartite.add(p);
    }

    private List carte(){
        List<Integer> carte = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            carte.add(i);
        }
        // Mescola la lista in ordine casuale
        Collections.shuffle(carte);
        return  carte;
    }
}
