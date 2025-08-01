package com.company.data.api.service;

import com.company.data.api.dto.CompanyInfoDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CsvParserService {

    public Map<String, CompanyInfoDTO> parseCsvData(MultipartFile file) {

        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.readLine();
            return reader.lines()
                    .map(line -> line.split(","))
                    .filter(values -> values.length >= 4)
                    .collect(Collectors.toMap(values -> values[0].trim(),
                            values -> new CompanyInfoDTO(
                                    values[0].trim(),
                                    values[1].trim(),
                                    values[2].trim(),
                                    values[3].trim()), (a, b) -> b));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
