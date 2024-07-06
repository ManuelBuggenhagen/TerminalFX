package org.example.terminalfx;

public class Vertrag {
    private int vertragid;
    private String angebot;
    private String beginn;
    private String ende;
    private float vergutung;
    private String zustand;
    private String notizG;
    private String notizI;

    public Vertrag(int vertragid, String angebot, String beginn, String ende, float vergutung, String zustand, String notizG, String notizI) {
        this.vertragid = vertragid;
        this.angebot = angebot;
        this.beginn = beginn;
        this.ende = ende;
        this.vergutung = vergutung;
        this.zustand = zustand;
        this.notizG = notizG;
        this.notizI = notizI;
    }

    public int getVertragid() {
        return vertragid;
    }

    public String getAngebot() {
        return angebot;
    }

    public String getBeginn() {
        return beginn;
    }

    public String getEnde() {
        return ende;
    }

    public float getVergutung() {
        return vergutung;
    }

    public String getZustand() {
        return zustand;
    }

    public String getNotizG() {
        return notizG;
    }

    public String getNotizI() {
        return notizI;
    }
}
