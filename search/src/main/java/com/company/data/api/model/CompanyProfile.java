package com.company.data.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Document(indexName = "company_profile")
public class CompanyProfile {

    @Id
    private String id;
    private String domain;
    private String companyCommercialName;
    private String companyLegalName;
    private String companyAllAvailableNames;
    private List<String> phoneNumbers;
    private List<String> socialMediaLinks;
    private List<String> addresses;
}
