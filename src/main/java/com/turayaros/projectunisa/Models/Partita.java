package com.turayaros.projectunisa.Models;

import java.util.List;

public class Partita {

    private String idFirstPlayer;
    private String idSecondPlayer;
    private String idPartita;
    private List<Integer> carte;
    private String turn;
    private int cartaP1;
    private int cartaP2;
    private boolean partitaFinita;
    private int cartaP11;
    private int cartaP12;
    private int cartaP13;
    private int cartaP21;
    private int cartaP22;
    private int cartaP23;
    private int posCarta1;
    private int posCarta2;


    public Partita() {
    }

    public Partita(String idFirstPlayer, String idSecondPlayer, String idPartita, String turn, int cartaP1, int cartaP2, int cartaP11, int cartaP12, int cartaP13, int cartaP21, int cartaP22, int cartaP23, int posCarta1, int posCarta2, boolean partitaFinita) {
        this.idFirstPlayer = idFirstPlayer;
        this.idSecondPlayer = idSecondPlayer;
        this.idPartita = idPartita;
        this.turn = turn;
        this.cartaP1 = cartaP1;
        this.cartaP2 = cartaP2;
        this.cartaP11 = cartaP11;
        this.cartaP12 = cartaP12;
        this.cartaP13 = cartaP13;
        this.cartaP21 = cartaP21;
        this.cartaP22 = cartaP22;
        this.cartaP23 = cartaP23;
        this.posCarta1 = posCarta1;
        this.posCarta2 = posCarta2;
        this.partitaFinita = partitaFinita;
    }

    public Partita(String idFirstPlayer, String idSecondPlayer, String idPartita, String turn, List<Integer> carte) {
        this.idFirstPlayer = idFirstPlayer;
        this.idSecondPlayer = idSecondPlayer;
        this.idPartita = idPartita;
        this.turn = turn;
        this.carte = carte;
    }

    public Partita(String idFirstPlayer, String idSecondPlayer, String idPartita, List<Integer> carte) {
        this.idFirstPlayer = idFirstPlayer;
        this.idSecondPlayer = idSecondPlayer;
        this.idPartita = idPartita;
        this.carte = carte;
    }

    public String getIdFirstPlayer() {
        return idFirstPlayer;
    }

    public void setIdFirstPlayer(String idFirstPlayer) {
        this.idFirstPlayer = idFirstPlayer;
    }

    public String getIdSecondPlayer() {
        return idSecondPlayer;
    }

    public void setIdSecondPlayer(String idSecondPlayer) {
        this.idSecondPlayer = idSecondPlayer;
    }

    public String getIdPartita() {
        return idPartita;
    }

    public void setIdPartita(String idPartita) {
        this.idPartita = idPartita;
    }

    public List<Integer> getCarte() {
        return carte;
    }

    public void setCarte(List<Integer> carte) {
        this.carte = carte;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }



    public boolean isPartitaFinita() {
        return partitaFinita;
    }

    public void setPartitaFinita(boolean partitaFinita) {
        this.partitaFinita = partitaFinita;
    }

    public int getCartaP1() {
        return cartaP1;
    }

    public void setCartaP1(int cartaP1) {
        this.cartaP1 = cartaP1;
    }

    public int getCartaP2() {
        return cartaP2;
    }

    public void setCartaP2(int cartaP2) {
        this.cartaP2 = cartaP2;
    }

    public int getCartaP11() {
        return cartaP11;
    }

    public void setCartaP11(int cartaP11) {
        this.cartaP11 = cartaP11;
    }

    public int getCartaP12() {
        return cartaP12;
    }

    public void setCartaP12(int cartaP12) {
        this.cartaP12 = cartaP12;
    }

    public int getCartaP13() {
        return cartaP13;
    }

    public void setCartaP13(int cartaP13) {
        this.cartaP13 = cartaP13;
    }

    public int getCartaP21() {
        return cartaP21;
    }

    public void setCartaP21(int cartaP21) {
        this.cartaP21 = cartaP21;
    }

    public int getCartaP22() {
        return cartaP22;
    }

    public void setCartaP22(int cartaP22) {
        this.cartaP22 = cartaP22;
    }

    public int getCartaP23() {
        return cartaP23;
    }

    public void setCartaP23(int cartaP23) {
        this.cartaP23 = cartaP23;
    }

    public int getPosCarta1() {
        return posCarta1;
    }

    public void setPosCarta1(int posCarta1) {
        this.posCarta1 = posCarta1;
    }

    public int getPosCarta2() {
        return posCarta2;
    }

    public void setPosCarta2(int posCarta2) {
        this.posCarta2 = posCarta2;
    }

    @Override
    public String toString() {
        return "Partita{" +
                "idFirstPlayer='" + idFirstPlayer + '\'' +
                ", idSecondPlayer='" + idSecondPlayer + '\'' +
                ", idPartita='" + idPartita + '\'' +
                ", carte=" + carte +
                ", turn='" + turn + '\'' +
                ", cartaP1=" + cartaP1 +
                ", cartaP2=" + cartaP2 +
                ", partitaFinita=" + partitaFinita +
                ", cartaP11=" + cartaP11 +
                ", cartaP12=" + cartaP12 +
                ", cartaP13=" + cartaP13 +
                ", cartaP21=" + cartaP21 +
                ", cartaP22=" + cartaP22 +
                ", cartaP23=" + cartaP23 +
                ", posCarta1=" + posCarta1 +
                ", posCarta2=" + posCarta2 +
                '}';
    }
}
