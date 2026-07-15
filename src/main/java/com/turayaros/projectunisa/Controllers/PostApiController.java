package com.turayaros.projectunisa.Controllers;

import com.turayaros.projectunisa.Models.Giocatori;
import com.turayaros.projectunisa.Models.JsonPartita;
import com.turayaros.projectunisa.Models.Partita;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/post")
public class PostApiController {
    private static List<Giocatori> listaplayer = new ArrayList<>();
    private static List<Partita> listaPartite = new ArrayList<>();
    private Map<String,String> pgmap = new HashMap<>();
    private static List<String> Stringlistaplayer = new ArrayList<>();
    private Giocatori player1, player2;

    @PostMapping("/getPartita")
    @ResponseBody
    public Partita findPartitaById(
            @RequestBody JsonPartita partitajson) {
        List<Integer> l = carte();
        Partita p1 = new Partita("id1","id2","id1id2",l);
        for (Partita partita : listaPartite){
            if (partita.getIdPartita().contains(partitajson.getIdPartita())){
                return partita;
            }
        }
        return p1;
    }

    @PostMapping("/getAnyUpdate")
    @ResponseBody
    public Partita getAnyUpdate(
            @RequestParam  String idpartita) {
        for (Partita partita : listaPartite){
            if (partita.getIdPartita().contains(idpartita)){
                return partita;
            }
        }
        return null;
    }

    @PostMapping("/putData")
    @ResponseBody
    public String postResponseController2(
             @RequestBody Partita p) {
        System.out.println("arrived data from "+p.getTurn()+"\n"+p.toString());

        for (Partita partita : listaPartite){
            if (partita.getIdPartita().contains(p.getIdPartita())){
                listaPartite.remove(partita);
                listaPartite.add(p);
            }
        }
        return "inserted";
    }
    @PostMapping(value = "/updateData")
    @ResponseBody
    public Partita udapteData(@RequestParam JsonPartita partitaJson){

        for (Partita partita : listaPartite){
            if (partita.getIdPartita().contains(partitaJson.getIdPartita())){
                if(partita.getIdFirstPlayer().contains(partitaJson.getGiocatore())){
                    partita.setCartaP1(partitaJson.getCarta());
                    partita.setPosCarta1(partitaJson.getPos());
                }
                else {
                    partita.setCartaP2(partitaJson.getCarta());
                    partita.setPosCarta2(partitaJson.getPos());
                }
                return partita;
            }
        }

    return  null;

    }

    @PostMapping(value = "/ready")
    @ResponseBody
    public String rForGame(@RequestParam String idp){
        System.out.println(idp);
//        for (Partita partita : listaPartite){
//            if (partita.getIdPartita().contains(idp)){
//                return idp;
//            }
//        }
        if(pgmap.containsKey(idp)) return pgmap.get(idp);
        if(Stringlistaplayer.contains(idp)) return "wait";
        else{
            listaplayer.add(new Giocatori((idp)));
            Stringlistaplayer.add(idp);
        }

        if(listaplayer.size()%2 == 0){
            String idPartita = readyForGame();
            System.out.println("after ready"+idPartita);
            return idPartita;
        }
        return "wait";

    }
//    @PostMapping("/newPlayer")
//    @ResponseBody
//    public String postResponseController3(
//             @RequestBody String id) {
//
//
//        listaplayer
//        List<Integer> l = carte();
//        Partita p1 = new Partita("id1","id2","id1id2",l);
//        return p1;
//    }


    private String readyForGame(){
        player1 = listaplayer.get(listaplayer.size()-2);
        player2 = listaplayer.getLast();
        player1.setReadyForGame(true);
        player2.setReadyForGame(true);

        List<Integer> l = carte();
        Partita p = new Partita(player1.getId(),player2.getId(),player1.getId()+player2.getId(),l);
        p.setCartaP11(l.get(0));
        p.setCartaP12(l.get(1));
        p.setCartaP13(l.get(2));

        p.setCartaP21(l.get(3));
        p.setCartaP22(l.get(4));
        p.setCartaP23(l.get(5));
        p.setPartitaFinita(false);
        p.setCartaP1(-1);
        p.setCartaP2(-1);

        p.setTurn(player1.getId());
        System.out.println(p.getIdPartita());
        pgmap.put(player1.getId(),p.getIdPartita());
        pgmap.put(player2.getId(),p.getIdPartita());
        listaPartite.add(p);
        return p.getIdPartita();
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
