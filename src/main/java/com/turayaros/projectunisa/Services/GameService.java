package com.turayaros.projectunisa.Services;

import com.turayaros.projectunisa.Models.Giocatori;
import com.turayaros.projectunisa.Models.JsonPartita;
import com.turayaros.projectunisa.Models.Partita;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
public class GameService {
    private final List<Giocatori> listaplayer = Collections.synchronizedList(new ArrayList<>());
    private final List<Partita> listaPartite = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, String> pgmap = Collections.synchronizedMap(new HashMap<>());
    private final Set<String> waitingPlayers = Collections.synchronizedSet(new HashSet<>());

    private static final Logger logger = Logger.getLogger(GameService.class.getName());



    public String rForGame(String idp) {
        logger.info(String.valueOf(idp));
        if (idp == null) return "wait";
        // Already matched -> return partita id (also covers both polling callers).
        if (pgmap.containsKey(idp)) return pgmap.get(idp);
        // Already queued but not matched yet -> keep waiting (no duplicate queueing).
        if (waitingPlayers.contains(idp)) return "wait";
        synchronized (this) {
            if (pgmap.containsKey(idp)) return pgmap.get(idp);
            if (waitingPlayers.contains(idp)) return "wait";
            listaplayer.add(new Giocatori(idp));
            waitingPlayers.add(idp);
            if (listaplayer.size() % 2 == 0) {
                String idPartita = readyForGame();
                logger.info("after ready" + idPartita);
                return idPartita;
            }
        }
        return "wait";
    }

    public String startGame(String idp) {
        if (idp == null || idp.isBlank() || !pgmap.containsKey(idp)) return "wait";
        return pgmap.get(idp);
    }

    public List<Integer> sendCarte(String idp) {
        Partita partita = findPartitaByIdString(idp);
        if (partita != null) return partita.getCarte();
        synchronized (listaPartite) {
            if (!listaPartite.isEmpty()) return listaPartite.get(0).getCarte();
        }
        return Collections.emptyList();
    }

    public String partitaChiusa(String idp) {
        Partita partita = findPartitaByIdString(idp);
        if (partita == null) return "not found";
        return partita.isPartitaFinita() ? "finita" : "ok";
    }

    public String sendTurno(String idp) {
        Partita partita = findPartitaByIdString(idp);
        if (partita == null) return "0";
        return partita.getTurn();
    }

    public String sendCarta(String idp, int carta, String player) {
        Partita partita = findPartitaByIdString(idp);
        if (partita == null) return "0";
        partita.setCartaP1(carta);
        if (player != null && partita.getIdFirstPlayer() != null
                && player.contains(partita.getIdFirstPlayer())) {
            partita.setTurn(partita.getIdSecondPlayer());
        } else {
            partita.setTurn(partita.getIdFirstPlayer());
        }
        logger.info(String.valueOf(carta));
        return "100";
    }

    public int getCarta(String idp) {
        Partita partita = findPartitaByIdString(idp);
        if (partita == null) return 0;
        return partita.getCartaP1();
    }

    public String fine(String idp) {
        Partita partita = findPartitaByIdString(idp);
        if (partita == null) return "not found";
        partita.setPartitaFinita(true);
        return "finita";
    }

    // ---- Logic moved from PostApiController (thin-controller refactor) ----

    public Partita findPartitaById(JsonPartita partitajson) {
        if (partitajson != null) {
            Partita found = findPartitaByIdString(partitajson.getIdPartita());
            if (found != null) return found;
        }
        // Legacy fallback: return a dummy partita when nothing matches.
        return new Partita("id1", "id2", "id1id2", carte());
    }

    public Partita getAnyUpdate(String idpartita) {
        return findPartitaByIdString(idpartita);
    }

    public String putData(Partita p) {
        logger.info("arrived data from " + p.getTurn() + "\n" + p.toString());
        synchronized (listaPartite) {
            for (int i = 0; i < listaPartite.size(); i++) {
                Partita partita = listaPartite.get(i);
                if (partita.getIdPartita().contains(p.getIdPartita())) {
                    listaPartite.set(i, p);
                    return "inserted";
                }
            }
        }
        return "inserted";
    }

    public Partita updateData(JsonPartita partitaJson) {
        if (partitaJson == null) return null;
        synchronized (listaPartite) {
            for (Partita partita : listaPartite) {
                if (partita.getIdPartita().contains(partitaJson.getIdPartita())) {
                    if (partita.getIdFirstPlayer() != null
                            && partitaJson.getGiocatore() != null
                            && partita.getIdFirstPlayer().contains(partitaJson.getGiocatore())) {
                        partita.setCartaP1(partitaJson.getCarta());
                        partita.setPosCarta1(partitaJson.getPos());
                    } else {
                        partita.setCartaP2(partitaJson.getCarta());
                        partita.setPosCarta2(partitaJson.getPos());
                    }
                    return partita;
                }
            }
        }
        return null;
    }

    private Partita findPartitaByIdString(String idPartita) {
        if (idPartita == null) return null;
        synchronized (listaPartite) {
            for (Partita partita : listaPartite) {
                if (partita.getIdPartita() != null && partita.getIdPartita().contains(idPartita)) {
                    return partita;
                }
            }
        }
        return null;
    }

    public synchronized String readyForGame() {
        Giocatori player1 = listaplayer.get(listaplayer.size() - 2);
        Giocatori player2 = listaplayer.get(listaplayer.size() - 1);
        player1.setReadyForGame(true);
        player2.setReadyForGame(true);

        List<Integer> l = carte();
        Partita p = new Partita(player1.getId(), player2.getId(), player1.getId() + player2.getId(), l);
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
        logger.info(p.getIdPartita());
        pgmap.put(player1.getId(), p.getIdPartita());
        pgmap.put(player2.getId(), p.getIdPartita());
        listaPartite.add(p);
        return p.getIdPartita();
    }

    private List<Integer> carte() {
        List<Integer> carte = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            carte.add(i);
        }
        // Mescola la lista in ordine casuale
        Collections.shuffle(carte);
        return  carte;
    }
}
