package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.response.EducationContentDTO;
import org.hartford.iqsure.entity.EducationContent;
import org.hartford.iqsure.repository.EducationContentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationContentService {

    private final EducationContentRepository repository;

    public List<EducationContentDTO> getByLanguage(String language) {
        return repository.findByLanguage(language).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public EducationContentDTO getByTopicAndLanguage(String topic, String language) {
        return repository.findByTopicAndLanguage(topic, language)
                .map(this::toDTO)
                .orElse(null);
    }

    private EducationContentDTO toDTO(EducationContent entity) {
        return EducationContentDTO.builder()
                .id(entity.getId())
                .topic(entity.getTopic())
                .language(entity.getLanguage())
                .title(entity.getTitle())
                .content(entity.getContent())
                .build();
    }
}
