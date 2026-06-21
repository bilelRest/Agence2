

package tn.rapid_post.agence.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tn.rapid_post.agence.entity.B3;
import tn.rapid_post.agence.entity.Compteur;
import tn.rapid_post.agence.entity.RetourB3;
import tn.rapid_post.agence.repo.compteurRepo;
import tn.rapid_post.agence.repo.retourB3Rep;
import tn.rapid_post.agence.sec.entity.AppRole;
import tn.rapid_post.agence.sec.entity.AppUser;
import tn.rapid_post.agence.sec.repo.UserRepository;
import tn.rapid_post.agence.service.ApiService;
import tn.rapid_post.agence.repo.b3Repo;

import java.io.UnsupportedEncodingException;
import java.net.http.HttpRequest;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller


public class SmsController {

    @Autowired
    private ApiService smsService;
    @Autowired
    private b3Repo b3Rep;
    @Autowired
    private retourB3Rep rep;
    @Autowired
    private UserRepository appUserRepo;
    @Autowired
    private compteurRepo compteur;

    public AppUser findLogged() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {


            String utilisateur = ((UserDetails) authentication.getPrincipal()).getUsername();


            Optional<AppUser> appUser1 = appUserRepo.findByUsername(utilisateur);
            return appUser1.orElse(null);

        } else return null;
    }
    @GetMapping("/sms")
    public String sms(Model model,
                      @RequestParam(value = "exist", required = false) boolean exist,
                      @RequestParam(value = "status", required = false) String status,
                      @RequestParam(value = "id", required = false) String id,
                      @RequestParam(value = "echec", required = false) boolean echec,
                      @RequestParam(value = "reset", required = false) String reset,
                      @RequestParam(value = "forfait", required = false) boolean forfait,
                      @CookieValue(value = "ipad", required = false) String ipadCookie,
                      @RequestParam(value = "ipad", required = false) String ipadParam,
                      @RequestParam(value = "raz",required = false)boolean raz,
                      HttpServletResponse response) {
        LocalDate date=LocalDate.now().plusDays(2);
        if (date.getDayOfWeek()==DayOfWeek.SATURDAY) {
            System.out.println("Condition demain jeudi donc on ajoute 3");
            date = LocalDate.now().plusDays(4);
        }
        if (date.getDayOfWeek()==DayOfWeek.SUNDAY) {
            System.out.println("Condition demain jeudi donc on ajoute 3");
            date = LocalDate.now().plusDays(3);
        }

        // Mise à jour du compteur si "reset" est fourni
        compteur.findById(1).ifPresent(c -> {
            if (raz){
c.setCounter(0);
compteur.save(c);
            }
            if (StringUtils.hasText(reset)) {
                c.setValeur(Integer.parseInt(reset));
                compteur.save(c);
            }
            model.addAttribute("counter", c);
        });


        // Gestion de l'adresse IP à travers les cookies ou param
        String ipad = StringUtils.hasText(ipadParam) ? ipadParam : ipadCookie;
        if (StringUtils.hasText(ipadParam)) {
            Cookie cookie = new Cookie("ipad", ipadParam);
            cookie.setPath("/");
            cookie.setMaxAge(86400); // 24h
            response.addCookie(cookie);
        }


        model.addAttribute("ipad", ipad);
        model.addAttribute("logged", findLogged().getNomPrenom().toUpperCase());
        model.addAttribute("echec", echec);
        model.addAttribute("forfait", forfait);
        model.addAttribute("date",date);

        boolean isAdmin = findLogged().getRoles().stream()
                .anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()));
        model.addAttribute("isAdmin", isAdmin);

        B3 b3mod = new B3();
        if (StringUtils.hasText(id) && !StringUtils.hasText(status)) {
            b3mod = b3Rep.findByIdB3(Long.parseLong(id));
            if (b3mod == null) b3mod = new B3(); // fallback
        }
        model.addAttribute("b3mod", b3mod);

        // Chargement de la liste B3 non notifiés
        List<B3> b3List = b3Rep.findByNotifiedFalse()
                .stream()
                .sorted(Comparator.comparing(B3::getIdB3).reversed())
                .collect(Collectors.toList());
        model.addAttribute("b3List", b3List);

        model.addAttribute("exist", exist);
        model.addAttribute("status", status);
        model.addAttribute("id", id);

        return "sms";
    }
    @PostMapping("/sms")
    public String sms(@RequestParam String tel,
                      @RequestParam String ref,
                      @RequestParam String post,
                      @RequestParam(value = "nom", defaultValue = "") String nom,
                      @RequestParam(value = "resend", required = false, defaultValue = "false") boolean resend,
                      @RequestParam(value = "id", required = false) String id,
                      @CookieValue(value = "ipad", required = false) String ipadCookie,
                      @RequestParam(value = "ipad", required = false) String ipadParam,
                      @RequestParam(value = "date",required = false) String date,
                      HttpServletRequest request,
                      HttpServletResponse response) throws UnsupportedEncodingException {

        String ipad = StringUtils.hasText(ipadParam) ? ipadParam : ipadCookie;
        if (!StringUtils.hasText(ipad)) {
            return "redirect:/sms?status=false";
        }

//         Vérification de forfait
        Compteur compteur1 = compteur.findById(1).orElseThrow();
        if (compteur1.getCounter() >= compteur1.getValeur()) {
            return "redirect:/sms?forfait=true";
        }


        // Gestion du "resend"
        if (resend) {
            B3 existing = b3Rep.findByNumB3IgnoreCaseContaining(ref);
            if (existing != null && smsService.getApiData(existing, ipad,date)) {
               compteur1.setCounter(compteur1.getCounter() + 1);
                compteur.save(compteur1);
                return "redirect:/sms?status=true&id=" + existing.getIdB3() + "&ipad=" + ipad;
            } else {
                return "redirect:/sms?status=false&id=" + (existing != null ? existing.getIdB3() : "") + "&ipad=" + ipad;
            }
        }

        // Mise à jour
        if (StringUtils.hasText(id)) {
            B3 b3 = b3Rep.findByIdB3(Long.parseLong(id));
            if (b3 != null) {
                b3.setNom(nom);
                b3.setDestination(post);
                b3.setNumTel(Integer.parseInt(tel));
                b3.setNumB3(ref.toUpperCase());
                b3Rep.save(b3);
               if( smsService.getApiData(b3, ipad,date)){
                   compteur1.setCounter(compteur1.getCounter() + 1);
                   compteur.save(compteur1);
               }
                return "redirect:/sms?ipad=" + ipad;
            }
        }

        // Vérification de doublon
        if (b3Rep.existsByNumB3(ref)) {
            return "redirect:/sms?exist=true&ipad=" + ipad;
        }

        // Ajout nouveau B3
        B3 b3 = new B3();
        b3.setNom(nom);
        b3.setDestination(post);
        b3.setNumTel(Integer.parseInt(tel));
        b3.setNumB3(ref.toUpperCase());
        b3Rep.save(b3);

        if (smsService.getApiData(b3, ipad,date)) {
            compteur1.setCounter(compteur1.getCounter() + 1);
            compteur.save(compteur1);
            return "redirect:/sms?status=true&id=" + b3.getIdB3() + "&ipad=" + ipad;
        } else {
            return "redirect:/sms?status=false&id=" + b3.getIdB3() + "&ipad=" + ipad;
        }
    }

    @GetMapping("/b3consul")
    public String consulterB3(Model model,
                              @RequestParam(value = "query", required = false) String query,
                              @RequestParam(value = "error", required = false) Boolean error) {
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("logged",findLogged().getNomPrenom().toUpperCase());
        model.addAttribute("isAdmin", isAdmin);
        // Ajout de l'attribut error si présent
        if (error != null && error) {
            model.addAttribute("error", "Aucun B3 trouvé avec cette référence");
        }

        // Traitement de la recherche seulement si query n'est pas vide
        if (StringUtils.hasText(query)) {
            try {
                Long id = Long.parseLong(query);
                b3Rep.findById(id)
                        .ifPresent(b3 -> model.addAttribute("b3", b3));
            } catch (NumberFormatException e) {
                model.addAttribute("error", "Format d'ID invalide");
            }
        }

        return "b3consul";
    }

//    @PostMapping("/findb3")
//    public String rechercherB3(@RequestParam(value = "query") String query,
//                               RedirectAttributes redirectAttributes) {
//
//        // Validation de l'entrée
//        if (!StringUtils.hasText(query)) {
//            redirectAttributes.addAttribute("error", true);
//            return "redirect:/b3consul";
//        }
//
//        // Recherche par numéro B3
//        B3 b3 = b3Rep.findByNumB3(query.trim());
//
//        if (b3 != null) {
//            return "redirect:/b3consul?query=" + b3.getIdB3();
//        } else {
//            redirectAttributes.addAttribute("error", true);
//            return "redirect:/b3consul";
//        }
//    }
@GetMapping("/")
public String home(Model model) {

    boolean isAdmin = false;
    for (AppRole appRole : findLogged().getRoles()) {
        if ("ADMIN".equals(appRole.getName())) {
            isAdmin = true;
            break;
        }
    }
    model.addAttribute("logged",findLogged().getNomPrenom().toUpperCase());
    model.addAttribute("isAdmin", isAdmin);
    return "template";
}

    @GetMapping("/findb3")
    public String retourb3(Model model,
                           @RequestParam(value = "numb3", required = false) String numb3,
                           @RequestParam(value = "tel", required = false) String tel) {
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("numb3",numb3!=null?numb3.toUpperCase():null);
        model.addAttribute("tel",tel);
        model.addAttribute("logged",findLogged().getNomPrenom().toUpperCase());
        model.addAttribute("isAdmin", isAdmin);
        List<B3> b3List = new ArrayList<>();

        if (StringUtils.hasText(numb3)) {
            Optional<RetourB3> optionalRetour = rep.findBynumB3IgnoreCaseContaining(numb3);
            if (optionalRetour.isPresent()) {
                RetourB3 retour = optionalRetour.get();
                B3 b3 = new B3();
                b3.setRetourId(retour.getId());
                b3.setNom(retour.getNomPrenB3Ro());
                b3.setNumB3(retour.getNumB3());
                try {
                    b3.setNumTel(retour.getB3().getNumTel());
                } catch (Exception e) {
                    b3.setNumTel(0);
                }
                b3List.add(b3);
            }
                B3 b3 = b3Rep.findByNumB3IgnoreCaseContaining(numb3);
                if (b3 != null) {
                    b3.setRetourId(String.valueOf(b3.getIdB3()));
                    b3List.add(b3);

            }
        }

        if (StringUtils.hasText(tel)) {
            List<RetourB3> retourList = rep.findByNumTel(Integer.parseInt(tel));
            for (RetourB3 retour : retourList) {
                B3 b3 = new B3();
                b3.setRetourId(retour.getId());
                b3.setNom(retour.getNomPrenB3Ro());
                b3.setNumB3(retour.getNumB3());
                try {
                    b3.setNumTel(retour.getB3().getNumTel());
                } catch (Exception e) {
                    b3.setNumTel(0);
                }
                b3List.add(b3);
            }

            // Si aucun retour trouvé, chercher directement dans la table B3

                List<B3> foundB3List = b3Rep.findByNumTel(Integer.parseInt(tel));
                for (B3 b3 : foundB3List) {
                    b3.setRetourId(String.valueOf(b3.getIdB3()));
                    b3List.add(b3);


            }
        }

        model.addAttribute("results", b3List);
        return "b3consul";
    }
    @PostMapping("deletesms")
    public String deletesms(@RequestParam(value = "id")String id,
                            @RequestParam(value = "dash",required = false)String dash){
        if(StringUtils.hasText(id)){
            System.out.println("id recu "+id);
        try {
          Optional<B3> b3=  b3Rep.findById(Long.parseLong(id));
          System.out.println(b3.get().toString());
          if (b3.isPresent()){

              b3Rep.delete(b3.get());
              if (!StringUtils.hasText(dash)){
              return "redirect:/sms";}else {
                  return "redirect:/dashb3";
              }
          }
        }catch (Exception e){
            return "redirect:/sms?echec="+true;
        }
        }

        return "redirect:/sms";
    }


    //    @GetMapping("/send_sms")
//    public String sendSms(@RequestParam String numero,@RequestParam String message,@RequestParam String fact) {
//
//        try {
//            smsService.getApiData(numero,message,fact);
//            return "SMS envoyé avec succès.";
//        } catch (Exception e) {
//            return "Erreur lors de l'envoi du SMS.";
//        }
 //   }
    class FindB3{
        String id;
        String nom;
        String num;
        String tel;
}
}


