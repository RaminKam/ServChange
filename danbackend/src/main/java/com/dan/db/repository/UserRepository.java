package com.dan.db.repository;


import com.dan.db.entities.auth.RoleName;
import com.dan.db.entities.auth.UserDB;
import com.dan.db.entities.auth.UserStatus;
import com.dan.db.entities.common.UniverDB;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDB, Long> {

    @Query("select count(u) from UserDB u join u.roles r where r.name='ROLE_USER' and u.wasAt>?1")
    Long countUsersByScrapedAt(Instant at);

    @Query("select count(u) from UserDB u join u.roles r where r.name='ROLE_USER' and u.createdAt>?1")
    Long countNewUsersByScrapedAt(Instant at);


    Optional<UserDB> findByUsernameOrEmail(String username, String email);

    Optional<UserDB> findFirstByVerificationUrlAndEmail(String verificated, String email);


    Boolean existsByUsername(String username);

    Boolean existsByStudentid(String studentid);

    Boolean existsByEmail(String email);

    @Query("select u from UserDB u join u.roles r where r.name=?1 and u.removed<>true")
    Page<UserDB> findAllUsersWithRole(RoleName role, String search, Pageable pageable);

    @Query("select u from UserDB u join u.roles r where r.name in (?1) and u.removed<>true")
    List<UserDB> findAllUsersWithRole(List<RoleName> role);

    @Query("select u from UserDB u join u.roles r where r.name=?1 and u.univer=?2 and u.removed<>true")
    Page<UserDB> findAllUsersWithRoleAndUniver(RoleName role, UniverDB univer, Pageable pageable);

    @Query("select u from UserDB u join u.roles r where r.name in (?1) and u.univer=?2 and u.removed<>true")
    List<UserDB> findAllUsersWithRoleAndUniver(List<RoleName> role, UniverDB univer);


    @Query("select  u from UserDB u join u.roles r where u.active=true AND  r.name=?1  and u.removed<>true  order by function('RAND')")
    Page<UserDB> findRandomROle(RoleName role, Pageable pageable);

    @Query("select  u from UserDB u join u.roles r where u.active=true AND  r.name=?1 and u.univer=?2  and u.removed<>true order by function('RAND')")
    Page<UserDB> findRandomROle(RoleName role, UniverDB univer, Pageable pageable);

    @Query("select count(u) from UserDB u join u.roles r where u.active=true AND  r.name=?1 and ((?2) is null OR u.univer=?2) and u.removed<>true")
    Long countUsersInUniver(RoleName role, UniverDB univer);


    @Query("select distinct u from UserDB u  where (((?3) is null) or u.univer=?3) " +
            "and(u.moderation.moderator=?1) and (u.status in(?2)) and u.removed<>true ")
    Page<UserDB> findAllNotModeratedUsers(
            UserDB moderator,
            List<UserStatus> userStatus,
            UniverDB univer,
            String search,
            Pageable pageable);

    @Query("select distinct u from UserDB u  where (u.moderation.moderator=?1) and (u.status in(com.dan.db.entities.auth.UserStatus.READY))")
    List<UserDB> findAllNotModeratedUsers( UserDB moderator);

    @Query("select u from UserDB u join u.roles r where r.name=com.dan.db.entities.auth.RoleName.ROLE_ADMIN or (r.name=com.dan.db.entities.auth.RoleName.ROLE_MAIN_CURATOR and u.univer=?1) and u.removed<>true")
    List<UserDB> findAllUsersWithRoleForCurator(UniverDB univer);

    @Query("select u from UserDB u join u.roles r where r.name=com.dan.db.entities.auth.RoleName.ROLE_ADMIN or (r.name=com.dan.db.entities.auth.RoleName.ROLE_CURATOR and u.univer=?1) and u.removed<>true")
    List<UserDB> findAllUsersWithRoleForMainCurator(UniverDB univer);


    @Query("select u from UserDB u join u.roles r where (r.name=com.dan.db.entities.auth.RoleName.ROLE_USER) and u.removed<>true")
    List<UserDB>     findAllActiveClients();

    @Query("select count(u) from UserDB u join u.roles r where (r.name=com.dan.db.entities.auth.RoleName.ROLE_USER) and u.removed<>true")
    Long     findTotalRegisteredClients();

}
