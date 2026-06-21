package tn.rapid_post.agence.sec.service;



import tn.rapid_post.agence.sec.entity.AppRole;
import tn.rapid_post.agence.sec.entity.AppUser;

import java.util.List;

public interface AppUserInterface {
    AppRole AddNewRole(AppRole appRole);
    AppUser AddUser(AppUser appUser);
    boolean AddRoleToUser(String userName, String roleName);
    AppUser LoadUserByUserName(String userName);
    List<AppUser> ListUser();


}
