package tn.rapid_post.agence.entity;

import jakarta.persistence.*;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;


@Entity
    public class HistoryDouane {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private LocalDate dateArrivee;
        private LocalDate dateSortiee;
        private String numColis;
        private String fraisTotal;
        private String sequence;


        private String username; // 👈 utilisateur qui a fait l’action
private String bloc;
        private Time time=Time.valueOf(LocalTime.now());

        private String action;

        private LocalDate dateAction=LocalDate.now();

        public HistoryDouane() {}

    public HistoryDouane(LocalDate dateArrivee, LocalDate dateSortiee, String numColis, String fraisTotal, String sequence, String username, String bloc, String action) {
        this.dateArrivee = dateArrivee;
        this.dateSortiee = dateSortiee;
        this.numColis = numColis;
        this.fraisTotal = fraisTotal;
        this.sequence = sequence;
        this.username = username;
        this.bloc = bloc;
        this.action = action;
    }

    public String getBloc() {
        return bloc;
    }

    public void setBloc(String bloc) {
        this.bloc = bloc;
    }

    public LocalDate getDateArrivee() {
        return dateArrivee;
    }

    public void setDateArrivee(LocalDate dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    public LocalDate getDateSortiee() {
        return dateSortiee;
    }

    public void setDateSortiee(LocalDate dateSortiee) {
        this.dateSortiee = dateSortiee;
    }

    public String getNumColis() {
        return numColis;
    }

    public void setNumColis(String numColis) {
        this.numColis = numColis;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getFraisTotal() {
        return fraisTotal;
    }

    public void setFraisTotal(String fraisTotal) {
        this.fraisTotal = fraisTotal;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDate getDateAction() {
        return dateAction;
    }

    public void setDateAction(LocalDate dateAction) {
        this.dateAction = dateAction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
