package tn.rapid_post.agence.sec.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.rapid_post.agence.sec.entity.AppRole;
import tn.rapid_post.agence.sec.entity.AppUser;
import tn.rapid_post.agence.sec.repo.RoleRepository;
import tn.rapid_post.agence.sec.repo.UserRepository;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class AppUserInterfaceImpl implements AppUserInterface {
    private UserRepository appUserRepo;
    private RoleRepository appRoleRepo;

    private PasswordEncoder encoder;

    public AppUserInterfaceImpl(UserRepository appUserRepo, RoleRepository appRoleRepo, PasswordEncoder encoder) {
        this.appUserRepo = appUserRepo;
        this.appRoleRepo = appRoleRepo;

        this.encoder = encoder;
    }


    @Override
    public AppRole AddNewRole(AppRole appRole) {
       if( appRoleRepo.findByName(appRole.getName()).isPresent()){
           return null;
       }

        return appRoleRepo.save(appRole);
    }

    @Override
    public AppUser AddUser(AppUser appUser) {
        String pass=encoder.encode(appUser.getPassword());
        appUser.setPassword(pass);
        return appUserRepo.save(appUser);
    }
    public boolean SaveUser(AppUser appUser){
        try {
            appUserRepo.save(appUser);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean AddRoleToUser(String userName, String roleName) {
        Optional<AppUser> appUser=appUserRepo.findByUsername(userName);
        Optional<AppRole> appRole=appRoleRepo.findByName(roleName);
        if( appUser.get().getRoles().contains(appRole)){
            System.out.println("Role deja existant");
            return false;
        }else {
            appUser.get().getRoles().add(appRole.get());
            appUserRepo.save(appUser.get());
            System.out.println("Role ajouté avec success");
            return true;
        }
    }

    @Override
    public AppUser LoadUserByUserName(String userName) {
        return appUserRepo.findByUsername(userName).get();
    }

    @Override
    public List<AppUser> ListUser() {
        return appUserRepo.findAll();
    }


}
