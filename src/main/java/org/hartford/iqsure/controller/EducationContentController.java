package org.hartford.iqsure.controller;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.response.EducationContentDTO;
import org.hartford.iqsure.service.EducationContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/education")
@RequiredArgsConstructor
public class EducationContentController {

    private final EducationContentService service;

    @GetMapping
    public ResponseEntity<List<EducationContentDTO>> getByLanguage(@RequestParam(defaultValue = "en") String language) {
        return ResponseEntity.ok(service.getByLanguage(language));
    }

    @GetMapping("/{topic}")
    public ResponseEntity<EducationContentDTO> getByTopicAndLanguage(
            @PathVariable String topic,
            @RequestParam(defaultValue = "en") String language) {
        EducationContentDTO content = service.getByTopicAndLanguage(topic, language);
        if (content != null) {
            return ResponseEntity.ok(content);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/tts")
    public ResponseEntity<byte[]> getTtsAudio(
            @RequestParam String text,
            @RequestParam String language) {
        try {
            String encodedText = java.net.URLEncoder.encode(text, "UTF-8");
            String urlStr = "https://translate.googleapis.com/translate_tts?client=gtx&ie=UTF-8&tl=" + language + "&q=" + encodedText;
            java.net.URL url = new java.net.URL(urlStr);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            
            java.io.InputStream is = conn.getInputStream();
            byte[] audioBytes = is.readAllBytes();
            is.close();
            
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.parseMediaType("audio/mpeg"));
            return new ResponseEntity<>(audioBytes, headers, org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
