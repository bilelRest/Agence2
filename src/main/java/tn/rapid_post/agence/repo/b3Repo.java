package tn.rapid_post.agence.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.rapid_post.agence.entity.B3;
import tn.rapid_post.agence.entity.Douane;
import tn.rapid_post.agence.entity.RetourB3;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface b3Repo extends JpaRepository<B3,Long> {
    boolean existsByNumB3(String b3);
    List<B3> findByNotifiedFalse();
    B3 findByNumB3IgnoreCaseContaining(String b);
    B3 findByIdB3(long b);
    @Query("SELECT b FROM B3 b WHERE b.nom LIKE %:name%")
    List<B3> findByNumB3RoOrNomPrenB3Ro( @Param("name") String name);
    @Query(value = "SELECT * FROM b3 WHERE " +
            "(CAST(:date1 AS date) IS NULL OR date_notif >= CAST(:date1 AS date)) AND " +
            "(CAST(:date2 AS date) IS NULL OR date_notif <= CAST(:date2 AS date)) AND " +
            "(:key IS NULL OR :key = '' OR " +
            "numb3 LIKE CONCAT('%', :key, '%') OR " +
            "nom LIKE CONCAT('%', :key, '%') OR " +
            "(regexp_replace(:key, '[^0-9]', '', 'g') ~ '^[0-9]+$' AND num_tel = CAST(regexp_replace(:key, '[^0-9]', '', 'g') AS integer))" +
            ") AND (:dest IS NULL OR :dest = '' OR destination LIKE CONCAT('%', :dest, '%'))",
            nativeQuery = true)
    Page<B3> searchBetweenDatesWithKeyAndEtat(
            @Param("date1") String date1Str,
            @Param("date2") String date2Str,
            @Param("key") String key,
            @Param("dest") String dest,
            Pageable pageable);

    @Query(value = """
    SELECT * FROM b3
    WHERE
        numb3 ILIKE CONCAT('%', :key, '%')
        OR nom ILIKE CONCAT('%', :key, '%')
        OR (
            regexp_replace(:key, '[^0-9]', '', 'g') ~ '^[0-9]+$'
            AND num_tel = CAST(regexp_replace(:key, '[^0-9]', '', 'g') AS bigint)
        )
        OR destination ILIKE CONCAT('%', :key, '%')
""", nativeQuery = true)
    Page<B3> searchWithKey(

            @Param("key") String key,
            Pageable pageable);
    List<B3> findByNumTel(int i);

    Page<B3> findByDateNotifBetween(LocalDate date1, LocalDate date2, PageRequest of);

    Page<B3> findByDestination(String dest, PageRequest of);


}
