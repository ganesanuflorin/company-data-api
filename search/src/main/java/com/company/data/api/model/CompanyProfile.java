package com.company.data.api.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Setter
@Getter
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
