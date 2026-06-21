package tn.rapid_post.agence.controller;

import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tn.rapid_post.agence.entity.Douane;
import tn.rapid_post.agence.entity.HistoryDouane;
import tn.rapid_post.agence.repo.douaneRepo;
import tn.rapid_post.agence.repo.historyDouanerepo;
import tn.rapid_post.agence.reports.Reporter;
import tn.rapid_post.agence.sec.entity.AppUser;
import tn.rapid_post.agence.sec.repo.UserRepository;


import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Controller
@Validated
public class Printer {

    @Autowired
    private douaneRepo douaneRep;

    @Autowired
    private Reporter reporter;
    @Autowired
    private UserRepository appUserRepo;
    @Autowired
    private historyDouanerepo historyDouanerepo;
    public AppUser findLogged() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {


            String utilisateur = ((UserDetails) authentication.getPrincipal()).getUsername();


            Optional<AppUser> appUser1 = appUserRepo.findByUsername(utilisateur);
            return appUser1.orElse(null);

        } else return null;
    }
    @GetMapping(value = "/print-sortie", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateDouaneReport(
            @RequestParam(value = "colis")String colis,
            @RequestParam(value = "reprint",required = false)boolean reprint
            ) throws JRException, IOException {

    Douane douane = douaneRep.findByNumColisIgnoreCase(colis);
    douane.setDateSortie(LocalDate.now());
    douane.setDelivered(true);
    douaneRep.save(douane);
    douane.setAppUser(findLogged());

        historyDouanerepo.save(new HistoryDouane(douane.getDateArrivee(),
                douane.getDateSortie(),douane.getNumColis(),
                String.valueOf(douane.getTotPayer()),douane.getSequence(),
                findLogged().getUsername(), String.valueOf( douane.getBloc()), "Livraison"));
    List<Douane> douaneList = Collections.singletonList(douane);
    Map<String, Object> parameters = new HashMap<>();
    byte[] pdfBytes = reporter.printdelivered(parameters, douaneList, false);
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"print.pdf\"")  // Affichage dans le navigateur
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);


    }
    @GetMapping("reprintdelivered")
    public ResponseEntity<byte[]> reprintdelivered(@RequestParam(value = "colis")String colis) throws JRException {
        List<Douane> douaneList=new ArrayList<>();
        if (StringUtils.hasText(colis)){
            Douane douane= douaneRep.findByNumColisIgnoreCase(colis);
            if (douane!=null){
                douaneList.add(douane);
            }
        }
        Map<String, Object> parameters = new HashMap<>();


        byte[] pdfBytes = reporter.reprintdelivered(parameters, douaneList,false);
        return  ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"print.pdf\"")  // Affichage dans le navigateur
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);

    }


    @GetMapping("reprintnotdelivered")
    public ResponseEntity<byte[]> reprintnotdelivered(@RequestParam(value = "colis")String colis) throws JRException {
        List<Douane> douaneList=new ArrayList<>();
        if (StringUtils.hasText(colis)){
            Douane douane= douaneRep.findByNumColisIgnoreCase(colis);
            if (douane!=null){
                douaneList.add(douane);
            }
        }
        Map<String, Object> parameters = new HashMap<>();


        byte[] pdfBytes = reporter.reprintnotdelivered(parameters, douaneList,false);
        return  ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"print.pdf\"")  // Affichage dans le navigateur
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);

    }
    @GetMapping(value = "/print-list")
    public ResponseEntity<byte[]> printLlist(@RequestParam(value = "id",required = false)String id) throws JRException, IOException {
        List<Douane> douaneList =new ArrayList<>();
        if (id!=null){
            Optional douane=douaneRep.findById(Long.parseLong(id));
            if (douane.isPresent()){
                douaneList.add((Douane) douane.get());
            }
        }else {
            douaneList=douaneRep.findByPrintedFalse();
        }

        for (Douane d:douaneList){
            d.setPrinted(true);
            douaneRep.save(d);
        }

        Map<String, Object> parameters = new HashMap<>();


        byte[] pdfBytes = reporter.printnotdelivered(parameters, douaneList,false);
        return  ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"print.pdf\"")  // Affichage dans le navigateur
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}