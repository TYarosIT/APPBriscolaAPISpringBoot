package com.turayaros.projectunisa.Services;

import com.turayaros.projectunisa.Models.Partita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GameService matchmaking and in-game flow")
class GameServiceTest {

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService();
    }

    private String matchTwoPlayers(String p1, String p2) {
        assertThat(gameService.rForGame(p1)).isEqualTo("wait");
        String idPartita = gameService.rForGame(p2);
        assertThat(idPartita).isNotEqualTo("wait");
        assertThat(idPartita).contains(p1).contains(p2);
        return idPartita;
    }

    @Test
    @DisplayName("first player waits, second player creates the partita")
    void firstWaitsSecondCreates() {
        assertThat(gameService.rForGame("alice")).isEqualTo("wait");

        String idPartita = gameService.rForGame("bob");

        assertThat(idPartita).isEqualTo("alicebob");
        assertThat(gameService.rForGame("alice")).isEqualTo("alicebob");
        assertThat(gameService.rForGame("bob")).isEqualTo("alicebob");
        assertThat(gameService.startGame("alice")).isEqualTo("alicebob");
        assertThat(gameService.startGame("bob")).isEqualTo("alicebob");
    }

    @Test
    @DisplayName("re-queueing waiting player stays wait without duplicating")
    void sameWaitingPlayerIsIdempotent() {
        assertThat(gameService.rForGame("alice")).isEqualTo("wait");
        assertThat(gameService.rForGame("alice")).isEqualTo("wait");

        // Only one player queued, so next distinct player pairs with alice.
        assertThat(gameService.rForGame("bob")).isEqualTo("alicebob");
    }

    @Test
    @DisplayName("null and unknown players wait")
    void nullAndUnknownStartGameWait() {
        assertThat(gameService.rForGame(null)).isEqualTo("wait");
        assertThat(gameService.startGame(null)).isEqualTo("wait");
        assertThat(gameService.startGame("ghost")).isEqualTo("wait");
    }

    @Test
    @DisplayName("created partita has shuffled deck, hands, turn and flags")
    void createdPartitaIsInitialized() {
        String idPartita = matchTwoPlayers("alice", "bob");

        Partita partita = gameService.getAnyUpdate(idPartita);

        assertThat(partita).isNotNull();
        assertThat(partita.getIdFirstPlayer()).isEqualTo("alice");
        assertThat(partita.getIdSecondPlayer()).isEqualTo("bob");
        assertThat(partita.getTurn()).isEqualTo("alice");
        assertThat(partita.isPartitaFinita()).isFalse();
        assertThat(partita.getCartaP1()).isEqualTo(-1);
        assertThat(partita.getCartaP2()).isEqualTo(-1);
        assertThat(partita.getCarte()).hasSize(40).doesNotHaveDuplicates();
        assertThat(List.of(partita.getCartaP11(), partita.getCartaP12(),
                partita.getCartaP13(), partita.getCartaP21(),
                partita.getCartaP22(), partita.getCartaP23()))
                .isSubsetOf(partita.getCarte());
    }

    @Test
    @DisplayName("sendCarta stores card and flips turn to opponent")
    void sendCartaFlipsTurn() {
        String idPartita = matchTwoPlayers("alice", "bob");

        assertThat(gameService.sendCarta(idPartita, 5, "alice")).isEqualTo("100");
        assertThat(gameService.getCarta(idPartita)).isEqualTo(5);
        assertThat(gameService.sendTurno(idPartita)).isEqualTo("bob");

        assertThat(gameService.sendCarta(idPartita, 9, "bob")).isEqualTo("100");
        assertThat(gameService.sendTurno(idPartita)).isEqualTo("alice");
    }

    @Test
    @DisplayName("unknown partita ids return safe defaults")
    void unknownIds() {
        assertThat(gameService.sendCarte("missing")).isEmpty();
        assertThat(gameService.sendTurno("missing")).isEqualTo("0");
        assertThat(gameService.sendCarta("missing", 1, "alice")).isEqualTo("0");
        assertThat(gameService.getCarta("missing")).isZero();
        assertThat(gameService.partitaChiusa("missing")).isEqualTo("not found");
        assertThat(gameService.fine("missing")).isEqualTo("not found");
    }

    @Test
    @DisplayName("sendCarte returns deck, partitaChiusa and fine track end")
    void deckAndFinishFlow() {
        String idPartita = matchTwoPlayers("alice", "bob");

        assertThat(gameService.sendCarte(idPartita)).hasSize(40);
        assertThat(gameService.partitaChiusa(idPartita)).isEqualTo("ok");
        assertThat(gameService.fine(idPartita)).isEqualTo("finita");
        assertThat(gameService.partitaChiusa(idPartita)).isEqualTo("finita");
    }
}
