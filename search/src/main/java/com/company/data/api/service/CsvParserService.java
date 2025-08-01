package com.company.data.api.service;

import com.company.data.api.dto.CompanyInfoDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CsvParserService {

    public Map<String, CompanyInfoDTO> parseCsvData(MultipartFile file) {

        try {
            Path tempFile = Files.createTempFile("company_data", ".csv");
            file.transferTo(tempFile.toFile());

            try (Stream<String> lines = Files.lines(tempFile)) {
                // Skip the header line and parse the CSV data
                return lines.skip(1)
                        .parallel()
                        .map(line -> line.split(","))
                        .filter(values -> values.length >= 4)
                        .collect(Collectors.toConcurrentMap(
                                values -> values[0].trim(),
                                values -> new CompanyInfoDTO(
                                        values[0].trim(),
                                        values[1].trim(),
                                        values[2].trim(),
                                        values[3].trim()),
                                (a, b) -> b));
            } finally {
                // Clean up the temporary file
                Files.deleteIfExists(tempFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
