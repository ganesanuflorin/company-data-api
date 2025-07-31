package com.company.data.api.feign;

import java.util.List;

public record ScraperModel(String domain,
                           String status,
                           List<String> phoneNumbers,
                           List<String> socialMediaLinks,
                           List<String> addresses) {
}
