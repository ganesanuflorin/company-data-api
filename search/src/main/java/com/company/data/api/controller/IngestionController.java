package com.company.data.api.controller;

import com.company.data.api.feign.ScraperClient;
import com.company.data.api.feign.ScraperModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IngestionController {

    private final ScraperClient scraperClient;

    public IngestionController(ScraperClient scraperClient) {
        this.scraperClient = scraperClient;
    }

    @PostMapping(value = "ingest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> ingest(@RequestParam("file") MultipartFile file) {
        List<ScraperModel> result = scraperClient.scrapeFromCSV(file);
        return ResponseEntity.ok(result);
    }
}
