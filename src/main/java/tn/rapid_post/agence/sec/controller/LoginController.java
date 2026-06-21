package tn.rapid_post.agence.sec.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.session.InMemoryWebSessionStore;
import tn.rapid_post.agence.sec.entity.AppRole;
import tn.rapid_post.agence.sec.entity.AppUser;
import tn.rapid_post.agence.sec.entity.Permission;
import tn.rapid_post.agence.sec.repo.RoleRepository;
import tn.rapid_post.agence.sec.repo.UserRepository;
import tn.rapid_post.agence.sec.service.AppUserInterfaceImpl;
import tn.rapid_post.agence.sec.service.CustomUserDetailsService;

import java.net.Authenticator;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;

@Controller
public class LoginController {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AppUserInterfaceImpl userInterface;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private PasswordEncoder encoder;
    @GetMapping("loginpath")
    public String afterLoginUser(HttpServletRequest request, HttpSession session) {
        String username = request.getUserPrincipal().getName();

        if (StringUtils.hasText(username)) {
            System.out.println("=== Authentification réussie ===");
            System.out.println("Nom d'utilisateur connecté : " + username);

            Optional<AppUser> optionalUser = userRepository.findByUsername(username);
            if (optionalUser.isPresent()) {
                AppUser appUser = optionalUser.get();
                List<AppRole> roles = appUser.getRoles();

                if (roles.isEmpty()) {

                } else {

                    for (AppRole role : roles) {

                        List<Permission> permissions = role.getPermissionList();
                        if (permissions.isEmpty()) {

                        } else {
                            for (Permission permission : permissions) {
                            }
                        }
                    }
                }
            } else {
                System.out.println("Utilisateur introuvable dans la base de données.");
            }
        } else {
            System.out.println("Nom d'utilisateur vide ou null !");
        }

        return "redirect:/welcome"; // redirection après login
    }
    public AppUser findLogged() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {


            String utilisateur = ((UserDetails) authentication.getPrincipal()).getUsername();


            Optional<AppUser> appUser1 = userRepository.findByUsername(utilisateur);
            return appUser1.orElse(null);

        } else return null;
    }

    @GetMapping("login")
    public String loginUser( ){

        return "login";
    }
    @GetMapping("/new")
    public String newUser(Model model,@RequestParam(value = "exist",required = false)boolean exist){
        model.addAttribute("exist",exist);
        model.addAttribute("new","Hello from thymeleaf");
        return "new";
    }

@PostMapping("newuser")
    public String newuser(@RequestParam(value = "nom")String nom,
                          @RequestParam(value = "login")String login,
                          @RequestParam(value = "password")String password){
        System.out.println(nom+" "+login+" "+password);
        AppUser appUser=new AppUser(nom,login,password,true);
        if(userInterface.LoadUserByUserName(login)!=null)
    {
        return "redirect:/new?exist="+true;
    }else{
        userInterface.AddUser(appUser);
        return "redirect:/utilisateur";
    }
}
    @GetMapping("updtps")
    public String updatePassword(Model model,@RequestParam(value = "status",required = false)String status){
        if (StringUtils.hasText(status)){
            model.addAttribute("status",Boolean.parseBoolean(status));
        }
        model.addAttribute("logged",findLogged().getUsername());
        return "updps";
    }
@PostMapping("updatepwd")
    public String updatepwd(@RequestParam(value = "password")String password,
                            @RequestParam(value = "newpwd")String newpwd){
       AppUser appUser= userInterface.LoadUserByUserName(findLogged().getUsername());
        if(appUser!=null){
System.out.println("App user non null");
            if (encoder.matches( password,appUser.getPassword())){
                System.out.println("Mot de passe correspond ");
                appUser.setPassword(encoder.encode(newpwd));
                userRepository.save(appUser);
                return "redirect:/welcome?status="+true;

            }else {
                System.out.println("Mot de passe ne correspond pas");
                return "redirect:/updtps?status="+false;
            }
    }else {
            return "redirect:/updtps?status="+false;

        }
}
}
