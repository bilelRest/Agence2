package tn.rapid_post.agence.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.rapid_post.agence.entity.Douane;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface douaneRepo extends JpaRepository<Douane,Long> {
    @Query("SELECT d FROM Douane d WHERE  d.delivered = true AND d.dateSortie BETWEEN ?1 AND ?2")
    List<Douane> findBetweenDates(LocalDate startDate, LocalDate endDate);


    @Query("SELECT d FROM Douane d WHERE d.dateArrivee BETWEEN ?1 AND ?2")
    List<Douane> findBetweenDatesDash(@Param("date1") LocalDate startDate,@Param("date2") LocalDate endDate);

    List<Douane> findByPrintedFalse();
    @Query(value = "SELECT * FROM douane d WHERE " +
            "d.num_colis = :key OR " +
            "( :key ~ '^[0-9]+$' AND CAST(:key AS BIGINT) BETWEEN d.bloc AND d.bloc_fin )",
            nativeQuery = true)
    List<Douane> findByNumColisIgnoreCase1(@Param("key") String key);
Douane findByNumColisIgnoreCase(@Param("key") String key);
    Douane findByBloc(Long bloc);

    List<Douane> findByDeliveredTrue();
    List<Douane> findByDeliveredFalse();

    List<Douane> findBySequence(String sequence);


    // 1. Correction de searchMultiFields
    @Query("SELECT d FROM Douane d WHERE " +
            "(CONCAT('', d.numColis) = :key OR CONCAT('', d.bloc) = :key) " +
            "OR (LOWER(d.nom) LIKE LOWER(CONCAT('%', :key, '%')))")
    Page<Douane> searchMultiFields(@Param("key") String key, Pageable pageable);

    // 2. Correction de searchMultiFieldsByDelivered
    @Query("SELECT d FROM Douane d WHERE d.delivered = :etat AND (" +
            "LOWER(d.numColis) LIKE LOWER(CONCAT('%', :key, '%')) OR " +
            "LOWER(d.nom) LIKE LOWER(CONCAT('%', :key, '%')) OR " +
            "CONCAT('', d.sequence) LIKE CONCAT('%', :key, '%') OR " +
            "CONCAT('', d.bloc) LIKE CONCAT('%', :key, '%'))")
    Page<Douane> searchMultiFieldsByDelivered(@Param("key") String key, @Param("etat") boolean etat, Pageable pageable);
    @Query("SELECT d FROM Douane d WHERE d.dateArrivee BETWEEN :date1 AND :date2")
    Page<Douane> findBetweenDates(@Param("date1") LocalDate date1, @Param("date2") LocalDate date2, Pageable pageable);

    @Query("SELECT d FROM Douane d WHERE d.dateArrivee BETWEEN :date1 AND :date2 AND d.delivered = :etat")
    Page<Douane> findBetweenDatesByEtat(@Param("date1") LocalDate date1, @Param("date2") LocalDate date2, @Param("etat") boolean etat, Pageable pageable);
    @Query("SELECT d FROM Douane d WHERE d.dateArrivee BETWEEN :date1 AND :date2 AND d.delivered = :etat AND d.appUser.id = :id")
    List<Douane> findBetweenDatesByEtatUser(@Param("date1") LocalDate date1, @Param("date2") LocalDate date2, @Param("etat") boolean etat,@Param("id")long id);

    @Query("SELECT d FROM Douane d WHERE d.dateArrivee BETWEEN :date1 AND :date2 AND (" +
            "(CONCAT('', d.numColis) = :key OR CONCAT('', d.bloc) = :key) OR " +
            "(LOWER(d.numColis) LIKE LOWER(CONCAT('%', :key, '%')) OR " +
            "LOWER(d.nom) LIKE LOWER(CONCAT('%', :key, '%')) OR " +
            "CONCAT('', d.sequence) LIKE CONCAT('%', :key, '%') OR " +
            "CONCAT('', d.bloc) LIKE CONCAT('%', :key, '%')))")
    Page<Douane> searchBetweenDatesWithKey(
            @Param("date1") LocalDate date1,
            @Param("date2") LocalDate date2,
            @Param("key") String key,
            Pageable pageable);
    @Query("SELECT d FROM Douane d WHERE d.dateArrivee BETWEEN :date1 AND :date2 AND d.delivered = :etat AND (" +
            "(CONCAT('', d.numColis) = :key OR CONCAT('', d.bloc) = :key) OR " +
            "(LOWER(d.numColis) LIKE LOWER(CONCAT('%', :key, '%')) OR " +
            "LOWER(d.nom) LIKE LOWER(CONCAT('%', :key, '%')) OR " +
            "CONCAT('', d.sequence) LIKE CONCAT('%', :key, '%') OR " +
            "CONCAT('', d.bloc) LIKE CONCAT('%', :key, '%')))")
    Page<Douane> searchBetweenDatesWithKeyAndEtat(
            @Param("date1") LocalDate date1,
            @Param("date2") LocalDate date2,
            @Param("key") String key,
            @Param("etat") boolean etat,
            Pageable pageable);
    Page<Douane> findByDelivered(Boolean etat, Pageable pageable);


    @Query("SELECT d FROM Douane d WHERE d.situation=false and d.appUser.id = :id AND d.delivered = true AND d.dateSortie BETWEEN :startDate AND :endDate")
    List<Douane> findBetweenDatesUser(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      @Param("id") long id);

    List<Douane> findByDateArriveeOrderByIdDouaneAsc(LocalDate date);
    Douane findTopByOrderByBlocDesc();;
}


//    List<Douane> findByValidatedTrue();
//
//    List<Douane> findByValidatedFalse();
//    @Query("SELECT d FROM Douane d WHERE d.delivered = false AND d.dateSortie BETWEEN ?1 AND ?2 OR d.dateSortie BETWEEN ?1 AND ?2")
//
//    List<Douane> findBetweenDatesAndDeliverdTrue(LocalDate date1, LocalDate date2);

