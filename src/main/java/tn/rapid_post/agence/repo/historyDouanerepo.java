package tn.rapid_post.agence.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.rapid_post.agence.entity.Douane;
import tn.rapid_post.agence.entity.HistoryDouane;

import java.util.List;

public interface historyDouanerepo extends JpaRepository<HistoryDouane,Long> {

@Query("SELECT h FROM HistoryDouane  h where h.numColis = :colis")
    List<HistoryDouane> findByNumColis(@Param("colis") String colis);
}
