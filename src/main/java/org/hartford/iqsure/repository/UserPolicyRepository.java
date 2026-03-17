/*
 * FILE: UserPolicyRepository.java | LOCATION: repository/
 * PURPOSE: Database access for "user_policies" table. Used by UserPolicyService.java.
 * ENTITY: UserPolicy.java (entity/) — stores purchased policies with final premium
 */
package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.UserPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPolicyRepository extends JpaRepository<UserPolicy, Long> {

    List<UserPolicy> findByUser_UserId(Long userId);

    boolean existsByUser_UserIdAndPolicy_PolicyId(Long userId, Long policyId);

    List<UserPolicy> findByStatus(UserPolicy.PolicyStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT up FROM UserPolicy up WHERE up.assignedUnderwriter.userId = :underwriterId")
    List<UserPolicy> findByAssignedUnderwriter_UserId(@org.springframework.data.repository.query.Param("underwriterId") Long underwriterId);

    @org.springframework.data.jpa.repository.Query("SELECT up FROM UserPolicy up WHERE up.assignedUnderwriter.userId = :underwriterId AND up.status = :status")
    List<UserPolicy> findByAssignedUnderwriter_UserIdAndStatus(@org.springframework.data.repository.query.Param("underwriterId") Long underwriterId, @org.springframework.data.repository.query.Param("status") UserPolicy.PolicyStatus status);

    List<UserPolicy> findByStatusIn(List<UserPolicy.PolicyStatus> statuses);
}
