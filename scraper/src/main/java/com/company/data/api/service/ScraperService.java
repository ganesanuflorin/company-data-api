package com.company.data.api.service;

import com.company.data.api.model.ScrapeModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScraperService {


    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?:\\+1\\s*|1[\\s.-]?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}");
    private static final List<String> SOCIAL_MEDIA_DOMAINS = List.of("facebook.com", "linkedin.com", "twitter.com", "instagram.com", "youtube.com");
    private static final List<String> RELEVANT_KEYWORDS = List.of("contact", "about", "company", "info");


    public ScrapeModel scrapeWebsite(String url) {
        Set<String> phoneNumbers = new HashSet<>();
        Set<String> socialMediaLinks = new HashSet<>();
        Set<String> visited = new HashSet<>();

        try {
            Document mainDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10_000)
                    .get();

            visited.add(url);
            extractDataFromDoc(mainDoc, phoneNumbers, socialMediaLinks);

            Elements links = mainDoc.select("a[href]");
            for (Element link : links) {
                String href = link.absUrl("href");

                boolean isInternal = href.startsWith(url);
                boolean isAlreadyVisited = visited.contains(href);

                if (isInternal && !isAlreadyVisited) {
                    for (String keyword : RELEVANT_KEYWORDS) {
                        if (href.toLowerCase().contains(keyword)) {
                            try {
                                Document subDoc = Jsoup.connect(href)
                                        .userAgent("Mozilla/5.0")
                                        .timeout(8000)
                                        .get();

                                visited.add(href);
                                extractDataFromDoc(subDoc, phoneNumbers, socialMediaLinks);

                            } catch (Exception ignore) {
                                ignore.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ScrapeModel(url, new ArrayList<>(phoneNumbers), new ArrayList<>(socialMediaLinks));
    }

    private void extractDataFromDoc(Document doc, Set<String> phones, Set<String> socialLinks) {
        Matcher matcher = PHONE_PATTERN.matcher(doc.body().text());
        while (matcher.find()) {
            phones.add(matcher.group().trim());
        }

        Elements aTags = doc.select("a[href]");
        for (Element aTag : aTags) {
            String href = aTag.absUrl("href");
            for (String domain : SOCIAL_MEDIA_DOMAINS) {
                if (href.contains(domain)) {
                    socialLinks.add(href);
                }
            }
        }
    }
}
