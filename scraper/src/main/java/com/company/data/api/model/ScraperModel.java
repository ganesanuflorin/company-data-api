package com.company.data.api.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ScraperModel {
    private String domain;
    private String status;
    private List<String> phoneNumbers;
    private List<String> socialMediaLinks;
    private List<String> addresses;

}
