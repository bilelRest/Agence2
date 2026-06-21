package tn.rapid_post.agence.sec.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.rapid_post.agence.sec.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission,Long> {
    Permission findByPath(String path);
}
