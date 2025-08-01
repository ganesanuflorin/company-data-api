package com.company.data.api.controller;

import com.company.data.api.dto.CompanyInfoDTO;
import com.company.data.api.feign.ScraperClient;
import com.company.data.api.feign.ScraperModel;
import com.company.data.api.service.CsvParserService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class IngestionController {

    private final ScraperClient scraperClient;
    private final CsvParserService csvParserService;

    @PostMapping(value = "ingest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> ingest(@RequestParam("file_for_scraper") MultipartFile fileForScraper,
                                    @RequestParam("file_for_company") MultipartFile fileForCompany) {
        List<ScraperModel> scraperModelList = scraperClient.scrapeFromCSV(fileForScraper);
        Map<String, CompanyInfoDTO> companyInfoDTOMap = csvParserService.parseCsvData(fileForCompany);
        return ResponseEntity.ok(scraperModelList);
    }
}
