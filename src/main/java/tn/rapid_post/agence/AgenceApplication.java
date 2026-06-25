package tn.rapid_post.agence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.rapid_post.agence.entity.Douane;
import tn.rapid_post.agence.repo.douaneRepo;
import tn.rapid_post.agence.sec.entity.AppRole;
import tn.rapid_post.agence.sec.service.AppUserInterfaceImpl;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class AgenceApplication {
	public static void main(String[] args) {
		SpringApplication.run(AgenceApplication.class, args);
	}
@Autowired
	douaneRepo repo;
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CommandLineRunner start(AppUserInterfaceImpl appUserInterface) {
		return args -> {
			List<Douane> douaneList=repo.findAll();
//			for (Douane douane:douaneList){
//				String bloc = douane.getBloc().toString();
//				if (bloc.contains("-")) {
//					String avant = bloc.substring(0, bloc.indexOf("-"));
//					System.out.println(avant);
//				} else {
//					System.out.println(bloc); // ou gestion d'erreur
//				}
//			}
			List<Douane> updatedLis=new ArrayList<>();
			for (Douane douane:douaneList){
				douane.setBlocFin(douane.getBloc()+douane.getNbColis()-1);
				updatedLis.add(douane);
			}
			repo.saveAll(updatedLis);
//Long max= repo.findTopByOrderByBlocDesc().getBloc();
//System.out.println("valeur max "+max);
			// exemple d'utilisation au démarrage

//appUserInterface.AddRoleToUser("bilel","ADMIN");
// appUserInterface.AddUser(...);
		};
	}
}
