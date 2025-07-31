package com.company.data.api.feign;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ScraperClientTest {

    @Autowired
    private ScraperClient scraperClient;

     @Test
     void testScrapeWebsiteReal() {
        String testUrl = "https://timent.com/";

         ScrapeModel result = scraperClient.scrapeWebsite(testUrl);

         assertNotNull(result);
         assertEquals(testUrl + "contact", result.domain());
         assertEquals("success", result.status());
     }
}