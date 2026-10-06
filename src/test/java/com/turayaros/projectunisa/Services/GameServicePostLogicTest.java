package com.turayaros.projectunisa.Services;

import com.turayaros.projectunisa.Models.JsonPartita;
import com.turayaros.projectunisa.Models.Partita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GameService partita lookup and updates")
class GameServicePostLogicTest {

    private GameService gameService;
    private String idPartita;

    @BeforeEach
    void setUp() {
        gameService = new GameService();
        gameService.rForGame("alice");
        idPartita = gameService.rForGame("bob");
    }

    @Test
    @DisplayName("findPartitaById returns stored partita")
    void findPartitaByIdFound() {
        JsonPartita query = new JsonPartita();
        query.setIdPartita(idPartita);

        assertThat(gameService.findPartitaById(query).getIdPartita())
                .isEqualTo(idPartita);
    }

    @Test
    @DisplayName("findPartitaById falls back to dummy partita")
    void findPartitaByIdFallback() {
        JsonPartita query = new JsonPartita();
        query.setIdPartita("missing");

        Partita fallback = gameService.findPartitaById(query);

        assertThat(fallback.getIdPartita()).isEqualTo("id1id2");
        assertThat(fallback.getCarte()).hasSize(40);
    }

    @Test
    @DisplayName("getAnyUpdate returns null when unknown")
    void getAnyUpdateUnknown() {
        assertThat(gameService.getAnyUpdate("missing")).isNull();
        assertThat(gameService.getAnyUpdate(null)).isNull();
    }

    @Test
    @DisplayName("putData replaces the stored partita")
    void putDataReplaces() {
        Partita updated = gameService.getAnyUpdate(idPartita);
        updated.setTurn("bob");
        updated.setCartaP1(7);

        assertThat(gameService.putData(updated)).isEqualTo("inserted");
        assertThat(gameService.getAnyUpdate(idPartita).getTurn()).isEqualTo("bob");
        assertThat(gameService.getCarta(idPartita)).isEqualTo(7);
    }

    @Test
    @DisplayName("updateData routes card and pos to player 1 or 2")
    void updateDataRoutesByPlayer() {
        JsonPartita moveP1 = new JsonPartita();
        moveP1.setIdPartita(idPartita);
        moveP1.setGiocatore("alice");
        moveP1.setCarta(11);
        moveP1.setPos(2);

        Partita afterP1 = gameService.updateData(moveP1);
        assertThat(afterP1.getCartaP1()).isEqualTo(11);
        assertThat(afterP1.getPosCarta1()).isEqualTo(2);

        JsonPartita moveP2 = new JsonPartita();
        moveP2.setIdPartita(idPartita);
        moveP2.setGiocatore("bob");
        moveP2.setCarta(22);
        moveP2.setPos(0);

        Partita afterP2 = gameService.updateData(moveP2);
        assertThat(afterP2.getCartaP2()).isEqualTo(22);
        assertThat(afterP2.getPosCarta2()).isEqualTo(0);
    }

    @Test
    @DisplayName("updateData returns null for unknown or null input")
    void updateDataUnknown() {
        JsonPartita unknown = new JsonPartita();
        unknown.setIdPartita("missing");

        assertThat(gameService.updateData(unknown)).isNull();
        assertThat(gameService.updateData(null)).isNull();
    }
}
