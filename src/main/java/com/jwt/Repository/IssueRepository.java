package com.jwt.Repository;


import com.jwt.Entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue,Long> {
    //List<Issue> findByUserEmail(String email); @Query("SELECT i FROM Issue i JOIN i.assignedUsers u WHERE u.username = :username")
    //    List<Issue> findIssuesByAssignedUsername(@Param("username") String username);
    //    List<Issue> findIssuesByAssignedUsername(@Param("username") String username);
//    @Query("SELECT i FROM Issue i JOIN i.assignedUsers u WHERE u.user_id = :user_id")
//    List<Issue> findIssuesByAssignedUsername(@Param("user_id") Long user_id);
    @Query("SELECT i FROM Issue i JOIN i.assignedUsers u WHERE u.id = :userId")
    List<Issue> findIssuesByAssignedId(@Param("userId") Long userId);

//    @Query("SELECT i FROM Issue i JOIN i.assignedUsers u WHERE u.id = :id")
//    List<Issue> findIssuesByAssignedUsername(@Param("id") Long id);
}
