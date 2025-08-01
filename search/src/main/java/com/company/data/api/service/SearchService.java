package com.company.data.api.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.company.data.api.dto.MatchRequest;
import com.company.data.api.dto.MatchResponse;
import com.company.data.api.enums.MatchBoost;
import com.company.data.api.model.CompanyProfile;
import com.company.data.api.util.Normalizer;
import com.company.data.api.util.QueryHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class SearchService {

    private final ElasticsearchOperations es;
    private final QueryHelper queryHelper;
    private final Normalizer norm;

    public Optional<MatchResponse> match(MatchRequest matchRequest) {

        MatchRequest normReq = normalizeMatchRequest(matchRequest);


        List<Query> shoulds = new ArrayList<>();

        if (has(normReq.phoneNumber())) {
            shoulds.add(queryHelper.termQuery("phoneNumbers", normReq.phoneNumber(), MatchBoost.PHONE));
        }
        if (has(normReq.website())) {
            shoulds.add(queryHelper.termQuery("domain", normReq.website(), MatchBoost.DOMAIN));
        }
        if (has(normReq.facebook())) {
            shoulds.add(queryHelper.matchPhraseQuery("socialMediaLinks", "facebook.com/" + normReq.facebook(), MatchBoost.FACEBOOK));
            shoulds.add(queryHelper.matchPhraseQuery("socialMediaLinks", normReq.facebook(), MatchBoost.FACEBOOK));
        }
        if (has(normReq.name())) {
            shoulds.add(queryHelper.fuzzyQuery("companyCommercialName", normReq.name(), MatchBoost.NAME));
            shoulds.add(queryHelper.fuzzyQuery("companyLegalName", normReq.name(), MatchBoost.NAME));
            shoulds.add(queryHelper.matchQuery("companyAllAvailableNames", normReq.name(), MatchBoost.NAME));
        }

        if (shoulds.isEmpty()) {
            return Optional.empty();
        }
        BoolQuery bool = new BoolQuery.Builder()
                .should(shoulds)
                .minimumShouldMatch("1")
                .build();

        NativeQuery nq = NativeQuery.builder()
                .withQuery(q -> q.bool(bool))
                .withMaxResults(5)
                .build();

        SearchHits<CompanyProfile> hits = es.search(nq, CompanyProfile.class);
        if (hits.getTotalHits() == 0) {
            return Optional.empty();
        }

        var best = hits.getSearchHits().getFirst();
        if (best.getScore() < 2.0f) return Optional.empty();

        return Optional.of(new MatchResponse(best.getContent(), best.getScore()));
    }

    private MatchRequest normalizeMatchRequest(MatchRequest matchRequest) {
        String nName = norm.normalizeName(matchRequest.name());
        String nDomain = norm.normalizeDomain(matchRequest.website());
        String nPhone = norm.normalizePhone(matchRequest.phoneNumber());
        String nFacebook = norm.normalizeFacebook(matchRequest.facebook());
        return new MatchRequest(nName, nDomain, nPhone, nFacebook);
    }

    private boolean has(String s) {
        return s != null && !s.isBlank();
    }
}
