package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.InsuredMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsuredMemberRepository extends JpaRepository<InsuredMember, Long> {
    List<InsuredMember> findByUserPolicy_Id(Long userPolicyId);
}
