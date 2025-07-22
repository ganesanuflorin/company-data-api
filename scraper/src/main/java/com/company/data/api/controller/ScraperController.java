package com.company.data.api.controller;

import com.company.data.api.model.ScrapeModel;
import com.company.data.api.service.ScraperService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/scrape")
@AllArgsConstructor
public class ScraperController {

    private final ScraperService scraperService;

    @GetMapping("/url")
    public ScrapeModel scrape(@RequestParam String url) {
        return scraperService.scrapeWebsite(url);
    }

    @PostMapping("/processor")
    public List<ScrapeModel> scrapeFromCSV(@RequestParam MultipartFile file) {
        return scraperService.scrapeFromCSV(file);
    }
}
