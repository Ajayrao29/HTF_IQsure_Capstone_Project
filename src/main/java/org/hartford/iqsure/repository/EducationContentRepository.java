package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.EducationContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EducationContentRepository extends JpaRepository<EducationContent, Long> {
    Optional<EducationContent> findByTopicAndLanguage(String topic, String language);
    List<EducationContent> findByLanguage(String language);
}
