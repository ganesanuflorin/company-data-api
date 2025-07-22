package com.company.data.api.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ScrapeModel {
    private String url;
    private String status;
    private List<String> phoneNumbers;
    private List<String> socialMediaLinks;


}
