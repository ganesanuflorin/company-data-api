package com.company.data.api.util;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.company.data.api.enums.MatchBoost;
import org.springframework.stereotype.Component;

@Component
public class QueryHelper {
    public Query termQuery(String field, String value, MatchBoost boost) {
        return new Query.Builder()
                .term(t -> t.field(field).value(FieldValue.of(value)).boost(boost.value()))
                .build();
    }

    public Query matchPhraseQuery(String field, String value, MatchBoost boost) {
        return new Query.Builder()
                .matchPhrase(mp -> mp.field(field).query(value).boost(boost.value()))
                .build();
    }

    public Query matchQuery(String field, String value, MatchBoost boost) {
        return new Query.Builder()
                .match(m -> m.field(field).query(value).boost(boost.value()))
                .build();
    }

    public Query fuzzyQuery(String field, String value, MatchBoost boost) {
        return new Query.Builder()
                .match(m -> m.field(field)
                        .query(value)
                        .fuzziness("AUTO")
                        .operator(Operator.And)
                        .boost(boost.value()))
                .build();
    }
}
