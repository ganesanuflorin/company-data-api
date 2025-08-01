package com.company.data.api.controller;

import com.company.data.api.service.ProfileBuildService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class ProfileBuildController {

    private final ProfileBuildService profileBuildService;

    @PostMapping(value = "/build/profiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> build(@RequestParam("file_for_scraper") MultipartFile fileForScraper,
                                   @RequestParam("file_for_company") MultipartFile fileForCompany) {
        return profileBuildService.buildProfiles(fileForScraper, fileForCompany);

    }
}
