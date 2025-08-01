package com.company.data.api.service;

import com.company.data.api.model.CompanyProfile;
import lombok.AllArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CompanyProfileIndexer {

    private final ElasticsearchOperations es;

    public void saveAll(List<CompanyProfile> profiles) {
        es.save(profiles);
    }
}
