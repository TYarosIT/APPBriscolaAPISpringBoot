package com.turayaros.projectunisa.Models;

public class Giocatori {

    private String id;
    private boolean readyForGame;
    private String info;

    public Giocatori(String id, String info) {
        this.id = id;
        this.info = info;
    }

    public boolean isReadyForGame() {
        return readyForGame;
    }

    public void setReadyForGame(boolean readyForGame) {
        this.readyForGame = readyForGame;
    }

    public Giocatori(String id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
