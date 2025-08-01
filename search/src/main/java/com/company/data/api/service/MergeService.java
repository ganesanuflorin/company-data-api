package com.company.data.api.service;


import com.company.data.api.dto.CompanyInfoDTO;
import com.company.data.api.feign.ScraperModel;
import com.company.data.api.model.CompanyProfile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MergeService {

    public List<CompanyProfile> mergeData(
            Map<String, CompanyInfoDTO> companyInfoMap,
            List<ScraperModel> scraperModels) {
        List<CompanyProfile> merged = new ArrayList<>();

        for (ScraperModel scraperModel : scraperModels) {
            String domain = scraperModel.domain();
            CompanyInfoDTO companyInfo = companyInfoMap.get(domain);

            if (companyInfo != null) {

                CompanyProfile profile = new CompanyProfile();
                profile.setId(UUID.randomUUID().toString());
                profile.setDomain(domain);
                profile.setCompanyCommercialName(companyInfo.companyCommercialName());
                profile.setCompanyLegalName(companyInfo.companyLegalName());
                profile.setCompanyAllAvailableNames(companyInfo.companyAllAvailableNames());
                profile.setPhoneNumbers(scraperModel.phoneNumbers());
                profile.setSocialMediaLinks(scraperModel.socialMediaLinks());
                profile.setAddresses(scraperModel.addresses());

                merged.add(profile);
            }
        }
        return merged;
    }

}
