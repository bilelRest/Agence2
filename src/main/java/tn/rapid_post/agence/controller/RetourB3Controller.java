package tn.rapid_post.agence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.rapid_post.agence.entity.B3;
import tn.rapid_post.agence.entity.RetourB3;
import tn.rapid_post.agence.repo.b3Repo;
import tn.rapid_post.agence.repo.retourB3Rep;
import tn.rapid_post.agence.sec.entity.AppRole;
import tn.rapid_post.agence.sec.entity.AppUser;
import tn.rapid_post.agence.sec.repo.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class RetourB3Controller {
    @Autowired
    private retourB3Rep b3Rep;
    @Autowired
    private b3Repo repo;
    @Autowired
    private UserRepository appUserRepo;
    public AppUser findLogged() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {


            String utilisateur = ((UserDetails) authentication.getPrincipal()).getUsername();


            Optional<AppUser> appUser1 = appUserRepo.findByUsername(utilisateur);
            return appUser1.orElse(null);

        } else return null;
    }
    @GetMapping("/retourb3rcp")
    public String retourb3rcp(Model model,
                              @RequestParam(value = "numb3", required = false) String numb3,
                              @RequestParam(value = "name",required = false)String name) {
        boolean autorized = false;

        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("isAdmin",isAdmin);
        model.addAttribute("logged",findLogged().getNomPrenom().toUpperCase());
        String id="";
        List<RetourB3> results=new ArrayList<>();

        if (StringUtils.hasText(name)&&StringUtils.hasText(numb3)){
            if (b3Rep.findBynumB3IgnoreCaseContaining(numb3).isPresent()){
                model.addAttribute("exist",true);
                return "recepb3";
            }
            RetourB3 b3=new RetourB3(numb3,name,null);
            b3Rep.save(b3);
            Optional<RetourB3> retourB3=b3Rep.findBynumB3IgnoreCaseContaining(numb3);
            results.add((RetourB3) retourB3.orElse(null)) ;
            model.addAttribute("results",results);

            return "recepb3";
        }

        // Validation des paramètres
        if (StringUtils.hasText(numb3)) {
            Optional<RetourB3> existingB3 = b3Rep.findBynumB3IgnoreCaseContaining(numb3);

            if (existingB3.isPresent()) {
                model.addAttribute("exist", true);
                model.addAttribute("message", "Le B3 existe déjà dans le système de retour");
            } else {
                B3 b3=repo.findByNumB3IgnoreCaseContaining(numb3);
                if (b3==null){
                    model.addAttribute("nob3",true);
                    model.addAttribute("num",numb3);
                    return "recepb3";
                }
                if (b3!=null) {
                    RetourB3 newB3 = new RetourB3(numb3, b3.getNom(),b3);
                    b3Rep.save(newB3);
                    model.addAttribute("success", true);
                }

                Optional retourB3=b3Rep.findBynumB3IgnoreCaseContaining(numb3);


                id=retourB3.isPresent()? ((RetourB3) retourB3.get()).getId() :"";


                model.addAttribute("message", "B3 ajouté avec succès");
                model.addAttribute("id",id);
            }
        }

        // Ajout des résultats pour l'affichage


        return "recepb3";
    }
    @GetMapping("/retourb3")
    public String retourb3(Model model,
                           @RequestParam(value = "numb3", required = false) String numb3,
                           @RequestParam(value = "name", required = false) String name) {

        List<RetourB3> results = new ArrayList<>();
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("isAdmin",isAdmin);
        model.addAttribute("logged",findLogged().getNomPrenom().toUpperCase());
        model.addAttribute("numb3",numb3!=null?numb3.toUpperCase():"");
        model.addAttribute("name",name!=null?name.toUpperCase():"");
        try {
            if (StringUtils.hasText(numb3)) {
                // Recherche par numéro B3

              Optional<RetourB3> retourB3 = b3Rep.findBynumB3IgnoreCaseContaining(numb3);
              if (retourB3.isPresent()) {
                  results.add((RetourB3) retourB3.get());
              }
            }
        if (StringUtils.hasText(name)) {
                // Recherche par nom (insensible à la casse)
                results = b3Rep.findByNomPrenB3RoContainingIgnoreCase(name.trim());
            }

        } catch (NumberFormatException e) {
            model.addAttribute("error", "Format de numéro B3 invalide");
        }

        model.addAttribute("results", results);
        return "retourb3";
    }
}
