package tn.rapid_post.agence.sec.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tn.rapid_post.agence.sec.entity.AppRole;
import tn.rapid_post.agence.sec.entity.AppUser;
import tn.rapid_post.agence.sec.entity.Permission;
import tn.rapid_post.agence.sec.repo.PermissionRepository;
import tn.rapid_post.agence.sec.repo.RoleRepository;
import tn.rapid_post.agence.sec.repo.UserRepository;
import tn.rapid_post.agence.sec.service.AppUserInterfaceImpl;

import java.util.*;

@Controller
public class PermissionController {

    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private AppUserInterfaceImpl appUserInterface;


    @Autowired
    private RoleRepository roleRepository;
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

    @GetMapping("permission")
    public String permission(Model model,
                             @RequestParam(value = "nom", required = false) String nom,
                             @RequestParam(value = "path", required = false) String path) {
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
model.addAttribute("logged",findLogged().getUsername().toUpperCase());
        model.addAttribute("isAdmin", isAdmin);
        boolean success = false;
        boolean exist = false;

        if (StringUtils.hasText(nom) && StringUtils.hasText(path)) {
            Permission permission = permissionRepository.findByPath(path);
            if (permission != null) {
                exist = true;
            } else {
                permissionRepository.save(new Permission(nom, path));
                success = true;
            }
        }
        model.addAttribute("permiss",permissionRepository.findAll());

        model.addAttribute("success", success);
        model.addAttribute("exist", exist);
        return "permission";
    }

    @GetMapping("/roles")
    public String showRolesAndUserRoles(
            @RequestParam(value = "id", required = false) String userId,
            @RequestParam(value = "exist",required = false)boolean roleExist,
            @RequestParam(value = "echec",required = false)boolean echec,
            Model model) {
        model.addAttribute("echec",echec);
        model.addAttribute("roleExist",roleExist);
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("logged",findLogged().getUsername().toUpperCase());
        model.addAttribute("isAdmin", isAdmin);
        List<AppRole> roles = roleRepository.findAll();
        model.addAttribute("roles", roles);
        AppUser appUser1=new AppUser();
System.out.println("id recu"+userId);
boolean exist=false;
        if (StringUtils.hasText(userId)) {
            System.out.println("id recu string hastext"+userId);
            Optional<AppUser> appUser=appUserRepo.findById(Long.parseLong(userId));
            if (appUser.isPresent()){
                appUser1=appUser.get();
                exist=true;
                System.out.println("APPUSER nom "+appUser1.getUsername().toUpperCase());
            }

        }
        model.addAttribute("roles",roleRepository.findAll());
        model.addAttribute("exist",exist);
        model.addAttribute("selectedUser",appUser1);


        return "role-select";
    }
    @PostMapping("addrole")
    public String addrole(@RequestParam(value = "role",required = false)String role){
        if (StringUtils.hasText(role)){

            if (roleRepository.findByName(role.toUpperCase()).isPresent()){
                return "redirect:/roles?exist="+true;
            }

        appUserInterface.AddNewRole(new AppRole(role.toUpperCase()));
    }
    return "redirect:/role?roleName="+role;
    }





    @GetMapping("/role")
    public String showRoleManagement(@RequestParam(value = "roleName", required = false) String roleName,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("logged",findLogged().getUsername().toUpperCase());
        model.addAttribute("isAdmin", isAdmin);
        if (!StringUtils.hasText(roleName)) {
            return "redirect:/roles"; // redirection vers la page de sélection
        }

        Optional<AppRole> optionalRole = roleRepository.findByName(roleName);
        if (optionalRole.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Rôle introuvable : " + roleName);
            return "redirect:/roles";
        }

        AppRole role = optionalRole.get();
        model.addAttribute("role", role);
        model.addAttribute("permissions", permissionRepository.findAll());
        return "role";
    }


    @PostMapping("/roleadd")
    public String updateRolePermissions(
            @RequestParam("roleName") String roleName,
            @RequestParam(value = "permissionIds", required = false) List<Long> permissionIds,
            RedirectAttributes redirectAttributes) {

        try {
            AppRole role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Rôle non trouvé"));

            Set<Permission> permissions = (permissionIds == null || permissionIds.isEmpty())
                    ? new HashSet<>()
                    : new HashSet<>(permissionRepository.findAllById(permissionIds));

            role.setPermissionList(new ArrayList<>(permissions));
            roleRepository.save(role);

            redirectAttributes.addFlashAttribute("success", "Permissions mises à jour avec succès");
            return "redirect:/role?roleName=" + roleName;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
            return "redirect:/role?roleName=" + roleName;
        }
    }
    @GetMapping(value = "utilisateur")
    public String utilisateur(Model model,
                              @RequestParam(value = "id",required = false)String id,
                              @RequestParam(value = "exist",required = false)boolean exist){
       model.addAttribute("exist",exist);
        System.out.println(id);
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("logged",findLogged().getUsername().toUpperCase());
        model.addAttribute("isAdmin", isAdmin);
        boolean edit=false;
        AppUser appUser=new AppUser();
        if (StringUtils.hasText(id)){
            System.out.println("id non vide "+id);
           Optional<AppUser> user= appUserRepo.findById(Long.parseLong(id));
           if (user.isPresent()){
               appUser=user.get();
               edit=true;
           }
        }else {
            appUser=new AppUser();
            appUser.setUsername("");
            appUser.setPassword("");
        }
        model.addAttribute("edit",edit);
        model.addAttribute("formUser",appUser);
        List<AppUser> appUsers=appUserRepo.findAll();
        List<AppRole> appRoleList=roleRepository.findAll();
        model.addAttribute("rolesList",appRoleList);
        model.addAttribute("appUsers",appUsers);
        return "utilisateur";
    }
    @PostMapping("deleteusr")
    public String deleteUsr(@RequestParam(value = "id")String id){
        if (StringUtils.hasText(id)){
            try {
                appUserRepo.deleteById(Long.parseLong(id));
                return "redirect:/utilisateur?success="+true;
            }catch (Exception e){}
            return "redirect:/utilisateur?success="+false;

        }else {
            return "redirect:/utilisateur?success="+false;
        }

    }
    @PostMapping("adduser")
    public String adduser(@RequestParam(value = "id",required = false)String id,
                          @RequestParam(value = "username")String username,
                          @RequestParam(value = "password")String password){
        if (StringUtils.hasText(id)){
            Optional<AppUser> appUser=appUserRepo.findById(Long.parseLong(id));
            appUser.get().setUsername(username);
            appUser.get().setPassword(password);
            appUserRepo.save(appUser.get());
        }else {
            Optional<AppUser> appUser=appUserRepo.findByUsername(username);
            if (appUser.isPresent()){
                System.out.println("utilisateur existant ");
                return "redirect:/utilisateur?exist="+true;
            }else {
//                appUserRepo.save(new AppUser(username,password));
            }
        }

        return "redirect:/utilisateur";
    }
    @PostMapping("/edituserrole")
    public String updateUserRoles(
            @RequestParam("id") Long userId,
            @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
            RedirectAttributes redirectAttributes) {

        Optional<AppUser> userOpt = appUserRepo.findById(userId);
        if (userOpt.isPresent()) {
            AppUser user = userOpt.get();
            List<AppRole> selectedRoles = (roleIds != null) ? roleRepository.findAllById(roleIds) : new ArrayList<>();
            user.setRoles(selectedRoles);
            appUserRepo.save(user);
            redirectAttributes.addFlashAttribute("success", "Rôles mis à jour !");
        }

        return "redirect:/roles?id=" + userId;
    }


}
