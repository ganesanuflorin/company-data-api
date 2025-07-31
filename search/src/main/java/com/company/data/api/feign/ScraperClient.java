package com.company.data.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(
        name = "scraperClient",
        url = "${scraper.api.url}"
)
public interface ScraperClient {

    @GetMapping("/url")
    ScrapeModel scrapeWebsite(@RequestParam("url") String url);

    @PostMapping("/processor")
    List<ScrapeModel> scrapeFromCSV(@RequestParam MultipartFile file);
}
