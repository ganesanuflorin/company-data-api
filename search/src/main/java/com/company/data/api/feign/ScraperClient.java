package com.company.data.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(
        name = "scraperClient",
        url = "${scraper.api.url}",
        configuration = FeignMultipartSupportConfig.class
)
public interface ScraperClient {

    @GetMapping("/url")
    ScraperModel scrapeWebsite(@RequestParam("url") String url);

    @PostMapping(value = "/processor", consumes = "multipart/form-data")
    List<ScraperModel> scrapeFromCSV(@RequestPart("file") MultipartFile file);
}
