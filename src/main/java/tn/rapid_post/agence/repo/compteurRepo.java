package tn.rapid_post.agence.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.rapid_post.agence.entity.Compteur;

public interface compteurRepo extends JpaRepository<Compteur,Integer> {
}
