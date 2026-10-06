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
    private  final List<Giocatori> listaplayer = new ArrayList<>();
    private  final List<Partita> listaPartite = new ArrayList<>();
    private final Map<String,String> pgmap = new HashMap<>();
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

    public List<Integer> sendCarte(@RequestParam String idp){
        for (Partita partita : listaPartite)
            if (partita.getIdPartita().contains(idp))
                return partita.getCarte();

        return listaPartite.getFirst().getCarte();
    }

    public String partitaChiusa(@RequestParam String idp){
        for (Partita partita : listaPartite)
            if (partita.getIdPartita().contains(idp))
                if(partita.isPartitaFinita()) return "finita";
                else return "ok";
        return "not found";
    }

    public String sendTurno(@RequestParam String idp){
        for (Partita partita : listaPartite)
            if (partita.getIdPartita().contains(idp))
                return partita.getTurn();
        return "0";
    }

    public String sendCarte(@RequestParam String idp,int carta,String player) {
        for (Partita partita : listaPartite) {
            if (partita.getIdPartita().contains(idp)) {
                partita.setCartaP1(carta);
                if (player.contains(partita.getIdFirstPlayer())) partita.setTurn(partita.getIdSecondPlayer());
                else partita.setTurn(partita.getIdFirstPlayer());
                System.out.println(carta);
                return "100";
            }

        }
        return "0";
    }

    public int getCarta(@RequestParam String idp){
        for (Partita partita : listaPartite) {
            if (partita.getIdPartita().contains(idp)) {
                return partita.getCartaP1();
            }

        }
        return 0;
    }

    public String fine(@RequestParam String idp){
        for (Partita partita : listaPartite) {
            if (partita.getIdPartita().contains(idp)) {
                partita.setPartitaFinita(true);
                return "finita";
            }

        }
        return "not found";
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
