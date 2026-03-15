package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByUser_UserId(Long userId);

    List<Claim> findByStatus(Claim.ClaimStatus status);

    List<Claim> findByAssignedOfficer_UserId(Long officerId);

    Optional<Claim> findByClaimNumber(String claimNumber);

    List<Claim> findByStatusIn(List<Claim.ClaimStatus> statuses);
}
