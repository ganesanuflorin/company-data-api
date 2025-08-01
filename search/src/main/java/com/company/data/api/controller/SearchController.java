package com.company.data.api.controller;

import com.company.data.api.dto.MatchRequest;
import com.company.data.api.dto.MatchResponse;
import com.company.data.api.service.SearchService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/search")
@RestController
public class SearchController {

    private final SearchService searchService;

    @PostMapping
    public ResponseEntity<MatchResponse> match(@RequestBody MatchRequest matchRequest) {
        return searchService.match(matchRequest)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
