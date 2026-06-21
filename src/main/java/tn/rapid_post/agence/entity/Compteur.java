package tn.rapid_post.agence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Compteur {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private  int counter=0;
    private int valeur=0;

    public Compteur(int id, int counter, int valeur) {
        this.id = id;
        this.counter = counter;
        this.valeur = valeur;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public Compteur() {
    }

    public Compteur(int id, int counter) {
        this.id = id;
        this.counter = counter;
    }

    public int getValeur() {
        return valeur;
    }

    public void setValeur(int valeur) {
        this.valeur = valeur;
    }
}
