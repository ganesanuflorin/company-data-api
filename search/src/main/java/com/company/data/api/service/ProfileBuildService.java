package com.company.data.api.service;

import com.company.data.api.dto.CompanyInfoDTO;
import com.company.data.api.feign.ScraperClient;
import com.company.data.api.feign.ScraperModel;
import com.company.data.api.model.CompanyProfile;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ProfileBuildService {

    private final CsvParserService csvParserService;
    private final ScraperClient scraperClient;
    private final MergeService mergeService;
    private final CompanyProfileIndexer companyProfileIndexer;

    public ResponseEntity<?> buildProfiles(MultipartFile fileForScraper, MultipartFile fileForCompany) {

        List<ScraperModel> scraperModels = scraperClient.scrapeFromCSV(fileForScraper);

        Map<String, CompanyInfoDTO> companyInfoMap = csvParserService.parseCsvData(fileForCompany);

        List<CompanyProfile> mergedProfiles = mergeService.mergeData(companyInfoMap, scraperModels);

        if (mergedProfiles.isEmpty()) {
            return ResponseEntity.ok("No profiles were created.");
        }

        companyProfileIndexer.saveAll(mergedProfiles);
        return ResponseEntity.ok("Profiles created successfully.");

    }
}
