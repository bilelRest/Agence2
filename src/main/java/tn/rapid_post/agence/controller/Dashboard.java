package tn.rapid_post.agence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import tn.rapid_post.agence.entity.B3;
import tn.rapid_post.agence.entity.Douane;
import tn.rapid_post.agence.entity.HistoryDouane;
import tn.rapid_post.agence.entity.RetourB3;
import tn.rapid_post.agence.repo.b3Repo;
import tn.rapid_post.agence.repo.douaneRepo;
import tn.rapid_post.agence.repo.historyDouanerepo;
import tn.rapid_post.agence.repo.retourB3Rep;
import tn.rapid_post.agence.sec.entity.AppRole;
import tn.rapid_post.agence.sec.entity.AppUser;
import tn.rapid_post.agence.sec.repo.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class Dashboard {
    @Autowired
    private douaneRepo douaneRepo;
    @Autowired
    private UserRepository appUserRepo;
    @Autowired
    private historyDouanerepo historyDouanerepo;
    @Autowired
    private RestTemplate restTemplate;

    public AppUser findLogged() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {


            String utilisateur = ((UserDetails) authentication.getPrincipal()).getUsername();


            Optional<AppUser> appUser1 = appUserRepo.findByUsername(utilisateur);
            return appUser1.orElse(null);

        } else return null;
    }

    @GetMapping("/dashdouane")
    public String dashboard(Model model,
                            @RequestParam(value = "etat", required = false) String etat1,
                            @RequestParam(value = "key", required = false) String key,
                            @RequestParam(value = "date1", required = false) LocalDate date1,
                            @RequestParam(value = "date2", required = false) LocalDate date2,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {



        // Traitement des paramètres de filtre
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("isAdmin",isAdmin);
        model.addAttribute("logged",findLogged().getNomPrenom().toUpperCase());
        Boolean etat = null;
        boolean not = true;
        model.addAttribute("pageSizes", List.of(5, 10, 20, 50));

        if (StringUtils.hasText(etat1)) {
            if (etat1.equals("true")) {
                etat = true;
                not = false;
            } else if (etat1.equals("false")) {
                etat = false;
                not = false;
            }
        }

        Page<Douane> douanePage;
        Pageable pageable = PageRequest.of(page, size);

        if (date1 != null && date2 != null) {
            // Recherche par dates
            if (StringUtils.hasText(key)) {
                key = key.trim().toLowerCase() ;
                if (etat == null) {
                    douanePage = douaneRepo.searchBetweenDatesWithKey(date1, date2, key, pageable);
                } else {
                    douanePage = douaneRepo.searchBetweenDatesWithKeyAndEtat(date1, date2, key, etat, pageable);
                }
            } else {
                if (etat == null) {
                    douanePage = douaneRepo.findBetweenDates(date1, date2, pageable);
                } else {
                    douanePage = douaneRepo.findBetweenDatesByEtat(date1, date2, etat, pageable);
                }
            }
        } else {
            // Recherche sans dates
            if (StringUtils.hasText(key)) {
                key =  key.trim().toLowerCase() ;
                if (etat == null) {
                    douanePage = douaneRepo.searchMultiFields(key, pageable);
                } else {
                    douanePage = douaneRepo.searchMultiFieldsByDelivered(key, etat, pageable);
                }
            } else {
                if (etat == null) {
                    douanePage = douaneRepo.findAll(pageable);
                } else {
                    douanePage = douaneRepo.findByDelivered(etat, pageable);
                }
            }
        }

        // Ajout des attributs au modèle
        model.addAttribute("date1", date1);
        model.addAttribute("date2", date2);
        model.addAttribute("list", douanePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", douanePage.getTotalPages());
        model.addAttribute("totalItems", douanePage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("etat", etat);
        model.addAttribute("key", key != null ? key.replace("%", "") : "");
        model.addAttribute("not", not);

        return "dashdouane";
    }
    @GetMapping("history")
    public String history(Model model,
                          @RequestParam(value = "colis",required = false)String colis){
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("isAdmin",isAdmin);
        model.addAttribute("logged",findLogged().getNomPrenom().toUpperCase());
        List<HistoryDouane> historyDouaneList=historyDouanerepo.findByNumColis(colis);
        model.addAttribute("history",historyDouaneList);
        return "history";
    }
    @GetMapping("aviseditadmin")
    public String aviseditadmin(Model model,
                                @RequestParam(value = "id",required = false)String id,
                                @RequestParam(value = "exist",required = false)String exist,
                                @RequestParam(value = "status",required = false)String status){
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("isAdmin",isAdmin);
        model.addAttribute("logged",findLogged().getNomPrenom().toUpperCase());
        if (StringUtils.hasText(id)){
            Optional<Douane> douane=douaneRepo.findById(Long.parseLong(id));
            if (douane.isPresent()){
                model.addAttribute("douane",douane.get());
            }

        }
        if (StringUtils.hasText(exist)){
            model.addAttribute("exist",Boolean.parseBoolean(exist));
        }
        if (StringUtils.hasText(status)){
            model.addAttribute("status",Boolean.parseBoolean(status));
        }

        return "aviseditadmin";
    }
    @PostMapping("/update-colis")
    public String updateColis(
            @RequestParam("numColis") String numColis,
            @RequestParam("bloc") String bloc,
            @RequestParam("dateArrivee")  LocalDate dateArrivee,
            @RequestParam( "dateSortie")  LocalDate dateSortie,
            @RequestParam("origin") String origin,
            @RequestParam("nom") String nom,
            @RequestParam("poid") String poid,
            @RequestParam("nbColis") String nbColis,
            @RequestParam(value = "sequence",required = false) String sequence,
            @RequestParam(value = "droitDouane",required = false) String droitDouane,
            Model model) {

        try {
            // Création de l'objet Douane avec les paramètres
            Douane douane = douaneRepo.findByNumColisIgnoreCase(numColis);
            douane.setNumColis(numColis);
            douane.setBloc(Long.parseLong(bloc));
            douane.setDateArrivee(dateArrivee);
            douane.setDateSortie(dateSortie);
            douane.setOrigin(origin);
            douane.setNom(nom);
            douane.setPoid(Double.parseDouble(poid));
            douane.setNbColis(Integer.parseInt(nbColis));


if (StringUtils.hasText(sequence)) {
    List<Douane> douaneBySequence = douaneRepo.findBySequence(sequence);
    boolean sequenceValide = true;

    for (Douane d : douaneBySequence) {
        if (Objects.equals(d.getNumColis(), numColis)) {
            douane.setSequence(sequence);
            break;
        }

        if (d.getDateSortie().getYear() == LocalDate.now().getYear()) {
            sequenceValide = false;
        }
    }

    if (sequenceValide) {
        douane.setSequence(sequence);
    }


}else {
    douane.setSequence(null);
    douane.setDelivered(false);
    douane.setDateSortie(null);
    douane.setFraisMagasin(0);
    douane.setFraisDedouane(0);
    douane.setFraisReemballage(0);
    douane.setTotPayer(0);
}
if (!douane.isDelivered()) {
    douane.setDroitDouane(0);
    douane.setFraisMagasin(0);
    douane.setFraisDedouane(0);
    douane.setFraisReemballage(0);
    douane.setTotPayer(0);
}else {
if (StringUtils.hasText(droitDouane)) {
    douane.setDroitDouane(Double.parseDouble(droitDouane));
    long daysBetween = ChronoUnit.DAYS.between(dateArrivee, dateSortie);
    double magasinage = Math.max(0, (daysBetween - 6) * douane.getNbColis());
    double fraisFixes = douane.getNbColis() * 6;
    double total = fraisFixes + magasinage + Double.parseDouble(droitDouane);

    douane.setFraisMagasin(magasinage);
    douane.setFraisDedouane(douane.getNbColis() * 4);
    douane.setFraisReemballage(douane.getNbColis() * 2);
    douane.setTotPayer(total);
}}
            historyDouanerepo.save(new HistoryDouane(douane.getDateArrivee(),douane.getDateSortie(),douane.getNumColis(),String.valueOf(douane.getTotPayer()),douane.getSequence(), findLogged().getUsername(),String.valueOf(douane.getBloc()), "Modification par administrateur "));
            douaneRepo.save(douane);

                return "redirect:/aviseditadmin?status="+true+"&id="+douane.getIdDouane();





        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors de la mise à jour: " + e.getMessage());
            return "redirect:/aviseditadmin?status="+false;
        }
    }
    @Autowired
     b3Repo repoB3;
    @Autowired
    retourB3Rep retourRep;
    @GetMapping("dashb3")
    public String dashb3(Model model,
                         @RequestParam(value = "key", required = false) String key,
                         @RequestParam(value = "date1", required = false) LocalDate date1,
                         @RequestParam(value = "date2", required = false) LocalDate date2,
                         @RequestParam(value = "dest", required = false) String dest,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         @RequestParam(value = "id",required = false)String id) {

        // Set default dates if not provided
        date1 = date1 == null ? LocalDate.now().minusDays(7) : date1;
        date2 = date2 == null ? LocalDate.now() : date2;

        // Check if user is admin
        boolean isAdmin = findLogged().getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()));

        Page<B3> b3Page;
if (StringUtils.hasText(id)){
    Optional<B3> b3Optional=repoB3.findById(Long.parseLong(id));
    if (b3Optional.isPresent()){
    Optional<RetourB3> retourB3=retourRep.findByNumB3(b3Optional.get().getNumB3());
    if (retourB3.isPresent()){
        retourB3.get().setB3(null);
        retourRep.save(retourB3.get());
    }
    b3Optional.get().setRetourId(null);
    repoB3.save(b3Optional.get());
    b3Optional.ifPresent(b3 -> repoB3.delete(b3));}
}
       b3Page=repoB3.searchBetweenDatesWithKeyAndEtat(date1.toString(),date2.toString(),key!=null?key:"",dest!=null?dest:"",PageRequest.of(page,size));

        // Add attributes to model
        model.addAttribute("date1", date1);
        model.addAttribute("date2", date2);
        model.addAttribute("list", b3Page.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", b3Page.getTotalPages());
        model.addAttribute("totalItems", b3Page.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("key", key != null ? key : "");
        model.addAttribute("dest", dest != null ? dest : "");
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("logged", findLogged().getNomPrenom().toUpperCase());

        return "dashb3";
    }
}