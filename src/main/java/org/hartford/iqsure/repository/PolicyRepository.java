/*
 * FILE: PolicyRepository.java | LOCATION: repository/
 * PURPOSE: Database access for "policies" table. Used by PolicyService.java, UserPolicyService.java.
 * ENTITY: Policy.java (entity/)
 */
package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    List<Policy> findByIsActiveTrue();

    List<Policy> findByPolicyTypeAndIsActiveTrue(Policy.PolicyType policyType);

    boolean existsByTitle(String title);
}
