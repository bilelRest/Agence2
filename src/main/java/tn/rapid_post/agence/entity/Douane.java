package tn.rapid_post.agence.entity;

import jakarta.persistence.*;
import org.springframework.boot.context.properties.bind.DefaultValue;
import tn.rapid_post.agence.sec.entity.AppUser;

import java.time.LocalDate;

@Entity
public class Douane {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDouane;
    private String nom;
    private String numColis;
    private LocalDate dateSortie;
    private LocalDate dateArrivee;
    private Integer nbColis;
    private double droitDouane;
    private double fraisDedouane;
    private double fraisReemballage;
    private double fraisMagasin;
    private double totPayer;
    private Double poid;
    private String observation;
    private boolean printed;
    private boolean delivered;
    private String origin;
    private Long bloc;
    private String sequence;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean rePrint;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
private boolean situation;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean validateSituation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;


    private boolean validated;

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public Douane(String nom, String numColis, LocalDate dateSortie, LocalDate dateArrivee, Integer nbColis, double droitDouane, double fraisDedouane, double fraisReemballage, double fraisMagasin, double totPayer, Double poid, String observation, boolean printed, boolean delivered, String origin, Long bloc, AppUser appUser, boolean validated, String sequence, boolean rePrint, boolean situation, boolean validateSituation) {
        this.nom = nom;
        this.numColis = numColis;
        this.dateSortie = dateSortie;
        this.dateArrivee = dateArrivee;
        this.nbColis = nbColis;
        this.droitDouane = droitDouane;
        this.fraisDedouane = fraisDedouane;
        this.fraisReemballage = fraisReemballage;
        this.fraisMagasin = fraisMagasin;
        this.totPayer = totPayer;
        this.poid = poid;
        this.observation = observation;
        this.printed = printed;
        this.delivered = delivered;
        this.origin = origin;
        this.bloc = bloc;
        this.appUser = appUser;
        this.validated = validated;
        this.sequence=sequence;

        this.rePrint = rePrint;
        this.situation = situation;
        this.validateSituation = validateSituation;
    }

    public boolean isRePrint() {
        return rePrint;
    }

    public void setRePrint(boolean rePrint) {
        this.rePrint = rePrint;
    }
    public Douane(){

    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean vallidated) {
        this.validated = vallidated;
    }

    public String getOrigin() {
        return origin!=null?origin.toUpperCase():origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Long getBloc() {
        return bloc;
    }

    public void setBloc(Long bloc) {
        this.bloc = bloc;
    }

    public Douane(boolean printed, boolean rePrint, boolean situation, boolean validateSituation) {
        this.printed = printed;
        this.rePrint = rePrint;
        this.situation = situation;
        this.validateSituation = validateSituation;
    }

    public boolean isPrinted() {
        return printed;
    }

    public void setPrinted(boolean printed) {
        this.printed = printed;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public Douane(Long idDouane, String nom, String numColis, LocalDate dateSortie, LocalDate dateArrivee, Integer nbColis, double droitDouane, double fraisDedouane, double fraisReemballage, double fraisMagasin, double totPayer, Double poid, String observation, boolean printed, boolean delivered, String origin, Long bloc, boolean rePrint, boolean situation, boolean validateSituation, boolean validated) {
        this.idDouane = idDouane;
        this.nom = nom;
        this.numColis = numColis;
        this.dateSortie = dateSortie;
        this.dateArrivee = dateArrivee;
        this.nbColis = nbColis;
        this.droitDouane = droitDouane;
        this.fraisDedouane = fraisDedouane;
        this.fraisReemballage = fraisReemballage;
        this.fraisMagasin = fraisMagasin;
        this.totPayer = totPayer;
        this.poid = poid;
        this.observation = observation;
        this.printed = printed;
        this.delivered = delivered;
        this.origin = origin;
        this.bloc = bloc;
        this.rePrint = rePrint;
        this.situation = situation;
        this.validateSituation = validateSituation;
        this.validated=validated;
    }

    public Long getIdDouane() {
        return idDouane;
    }

    public void setIdDouane(Long idDouane) {
        this.idDouane = idDouane;
    }

    public boolean isSituation() {
        return situation;
    }

    public void setSituation(boolean situation) {
        this.situation = situation;
    }

    public String getNom() {
        return nom!=null?nom.toUpperCase():nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNumColis() {
        return numColis!=null?numColis.toUpperCase():numColis;
    }

    public void setNumColis(String numColis) {
        this.numColis = numColis.toUpperCase();
    }

    public LocalDate getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(LocalDate dateSortie) {
        this.dateSortie = dateSortie;
    }

    public LocalDate getDateArrivee() {
        return dateArrivee;
    }

    public void setDateArrivee(LocalDate dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    public Integer getNbColis() {
        return nbColis;
    }

    public void setNbColis(int nbColis) {
        this.nbColis = nbColis;
    }

    public double getDroitDouane() {
        return droitDouane;
    }

    public void setDroitDouane(double droitDouane) {
        this.droitDouane = droitDouane;
    }

    public double getFraisDedouane() {
        return fraisDedouane;
    }

    public void setFraisDedouane(double fraisDedouane) {
        this.fraisDedouane = fraisDedouane;
    }

    public double getFraisMagasin() {
        return fraisMagasin;
    }

    public void setFraisMagasin(double fraisMagasin) {
        this.fraisMagasin = fraisMagasin;
    }

    public double getFraisReemballage() {
        return fraisReemballage;
    }

    public void setFraisReemballage(double fraisReemballage) {
        this.fraisReemballage = fraisReemballage;
    }

    public double getTotPayer() {
        return totPayer;
    }

    public void setTotPayer(double totPayer) {
        this.totPayer = totPayer;
    }

    public Double getPoid() {
        return poid;
    }

    public void setPoid(Double poid) {
        this.poid = poid;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public Douane(boolean rePrint, boolean situation, boolean validateSituation) {
        this.rePrint = rePrint;
        this.situation = situation;
        this.validateSituation = validateSituation;
    }

    public boolean isValidateSituation() {
        return validateSituation;
    }

    public void setValidateSituation(boolean validateSituation) {
        this.validateSituation = validateSituation;
    }
}
