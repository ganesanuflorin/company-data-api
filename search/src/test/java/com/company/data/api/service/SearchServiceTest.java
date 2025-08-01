package com.company.data.api.service;

import com.company.data.api.dto.MatchRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class SearchServiceTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testMatch() throws Exception {
        var csvRes = new ClassPathResource("API-input-sample.csv");
        try (var reader = new InputStreamReader(csvRes.getInputStream(), StandardCharsets.UTF_8);
             var parser = CSVParser.parse(reader, CSVFormat.DEFAULT
                     .builder()
                     .setHeader()              // reading the first line as header
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .build())) {

            int total = 0;
            int matched = 0;

            for (CSVRecord rec : parser) {
                total++;
                String name = rec.get("input name");
                String phone = safe(rec, "input phone");
                String website = safe(rec, "input website");
                String facebook = safe(rec, "input_facebook");

                var req = new MatchRequest(name, website, phone, facebook);
                var json = objectMapper.writeValueAsString(req);
                var mvcRes = mockMvc.perform(post("/search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andReturn()
                        .getResponse();

                System.out.println("Testing: " + mvcRes.getContentAsString());
                if (mvcRes.getStatus() == 200) {
                    matched++;
                } else {
                    System.out.printf("No match (HTTP %d) for: name='%s', phone='%s', website='%s', fb='%s'%n",
                            mvcRes.getStatus(), name, phone, website, facebook);
                }
            }

            double rate = total == 0 ? 0.0 : (matched * 100.0 / total);
            System.out.printf("Matched %d / %d (%.2f%%)%n", matched, total, rate);

            assertThat(total).isGreaterThan(0);
        }
    }

    private static String safe(CSVRecord rec, String header) {
        try {
            String v = rec.get(header);
            return v == null ? "" : v;
        } catch (IllegalArgumentException e) {
            return "";
        }
    }
}
