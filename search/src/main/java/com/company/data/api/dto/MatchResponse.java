package com.company.data.api.dto;

import com.company.data.api.model.CompanyProfile;

public record MatchResponse(CompanyProfile profile, double score) {
}
