package com.company.data.api.dto;

public record CompanyInfoDTO(String domain,
                             String companyCommercialName,
                             String companyLegalName,
                             String companyAllAvailableNames) {
}
