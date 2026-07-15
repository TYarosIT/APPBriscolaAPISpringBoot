package com.turayaros.projectunisa.Models;

public class JsonPartita {

        private String idPartita;
        private String giocatore;
        private int Carta;
        private int pos;



    public String getIdPartita() {
        return idPartita;
    }

    public void setIdPartita(String idPartita) {
        this.idPartita = idPartita;
    }

    public String getGiocatore() {
        return giocatore;
    }

    public void setGiocatore(String giocatore) {
        this.giocatore = giocatore;
    }

    public int getCarta() {
        return Carta;
    }

    public void setCarta(int carta) {
        Carta = carta;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
