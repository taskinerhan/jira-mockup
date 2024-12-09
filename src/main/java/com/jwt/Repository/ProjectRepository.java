package com.jwt.Repository;
import com.jwt.Entity.Project;
import com.jwt.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project,Long> {
    Optional<Project> findByIdAndCreatedBy_Id(Long projectId, Long userId);
    Optional<Project> findById(Long projectId);
    List<Project> findByCreatedBy(User createdBy);
    Project findByName(String name);
    @Query("SELECT p FROM Project p WHERE p.deleted = true")
    List<Project> findDeletedProjects();
    List<Project> findByDeletedFalse();
    List<Project> findByCreatedBy_Id(Long userId);
}
