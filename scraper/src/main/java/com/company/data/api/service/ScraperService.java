package com.company.data.api.service;

import com.company.data.api.model.ScrapeModel;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScraperService {


    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?:\\+1[\\s\\-]?)?(\\(?\\d{3}\\)?[\\s\\-.]?)\\d{3}[\\s\\-.]?\\d{4}"
    );
    private static final List<String> SOCIAL_MEDIA_DOMAINS = List.of("facebook.com", "linkedin.com", "twitter.com", "instagram.com", "youtube.com");
    private static final Pattern ADDRESS_PATTERN = Pattern.compile(
            "(PO Box\\s+\\d{3,6}"
                    + "|\\d{5},?\\s+[A-Za-z\\s]+,?\\s+[A-Za-z]{2,},?\\s*United States"
                    + "|\\d{1,6}\\s+[\\w\\s.\\-]+,?\\s+[\\w\\s.]+,?\\s+(?:[A-Z]{2}|[A-Za-z]{4,}|[A-Z][a-z]{2,}\\.?|[A-Z]{2,})\\s+\\d{5}(-\\d{4})?(,?\\s*United States)?)",
            Pattern.CASE_INSENSITIVE
    );

    public ScrapeModel scrapeWebsite(String url) {

        if (url == null || url.isEmpty()) {
            return new ScrapeModel(url, "invalid", List.of(), List.of(), List.of());
        }
        int statusCode = validateUrl(url);
        if (statusCode != 200) {
            return new ScrapeModel(url, "invalid", List.of(), List.of(), List.of());
        }
        ScrapeModel result = new ScrapeModel(url, "invalid", List.of(), List.of(), List.of());
        Set<String> phoneNumbers = new HashSet<>();
        Set<String> addresses = new HashSet<>();
        Set<String> socialMediaLinks = new HashSet<>();
        List<String> paths = List.of("", "/contact", "/contact-us");
        HashMap<String, String> pathMap = new HashMap<>();
        for (String path : paths) {
            String fullUrl = url.endsWith("/") ? url + path.replaceFirst("^/", "") : url + path;

            try {
                Document doc = Jsoup.connect(fullUrl)
                        .userAgent("Mozilla/5.0")
                        .timeout(10_000)
                        .get();
                result = scrapeWithJsoup(doc, fullUrl, phoneNumbers, addresses, socialMediaLinks);
                pathMap.put(fullUrl, result.getStatus());

            } catch (IOException e) {
                pathMap.put(fullUrl, "failed");
                System.err.println("Failed to connect to " + fullUrl + ": " + e.getMessage());
            } catch (RuntimeException e) {
                pathMap.put(fullUrl, "runtime_error");
                System.err.println("Error processing " + fullUrl + ": " + e.getMessage());
            }
        }
        boolean seleniumCondition = result.getPhoneNumbers().isEmpty() &&
                result.getAddresses().isEmpty() &&
                result.getSocialMediaLinks().isEmpty();
        if (seleniumCondition) {
            for (String path : pathMap.keySet()) {
                if (pathMap.get(path).equals("success")) {
                    result = scrapewWithSelenium(path, phoneNumbers, addresses, socialMediaLinks);
                    if (!seleniumCondition) {
                        return result;
                    }
                }
            }
        }
        return result;
    }

    private int validateUrl(String url) {
        int statusCode = 0;
        try {
            statusCode = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10_000)
                    .ignoreHttpErrors(true)
                    .execute()
                    .statusCode();
        } catch (IOException e) {
            statusCode = 404;
            System.err.println("Failed to connect to " + url + ": " + e.getMessage());
        }
        return statusCode;
    }

    private ScrapeModel scrapewWithSelenium(String url, Set<String> phoneNumbers, Set<String> addresses, Set<String> socialMediaLinks) {

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.get(url);

            String bodyText = driver.findElement(By.tagName("body")).getText();
            extractDataFromText(bodyText, phoneNumbers, addresses);

            List<WebElement> links = driver.findElements(By.tagName("a"));
            for (WebElement link : links) {
                getSocialLinks(link, socialMediaLinks);
            }

            String status = (phoneNumbers.isEmpty() && socialMediaLinks.isEmpty() && addresses.isEmpty())
                    ? "empty_after_fallback" : "success_fallback";

            return new ScrapeModel(url, status,
                    new ArrayList<>(phoneNumbers),
                    new ArrayList<>(socialMediaLinks),
                    new ArrayList<>(addresses));

        } catch (RuntimeException e) {
            return new ScrapeModel(url, "selenium_failed", List.of(), List.of(), List.of());
        } finally {
            driver.quit();
        }

    }

    private static void getSocialLinks(WebElement link, Set<String> socialMediaLinks) {
        String href = link.getAttribute("href");
        if (href != null) {
            for (String domain : SOCIAL_MEDIA_DOMAINS) {
                if (href.toLowerCase().contains(domain)) {
                    socialMediaLinks.add(href);
                }
            }
        }
    }

    private ScrapeModel scrapeWithJsoup(Document doc, String url, Set<String> phoneNumbers, Set<String> addresses, Set<String> socialMediaLinks) {

        String htmlContent = doc.body().html();
        String plainText = Jsoup.parse(htmlContent.replaceAll("(?i)<br\\s*/?>", " ")).text().replaceAll("\\s+", " ").trim();

        extractDataFromText(plainText, phoneNumbers, addresses);
        Elements aTags = doc.select("a[href]");
        for (Element aTag : aTags) {
            String href = aTag.absUrl("href");
            for (String domain : SOCIAL_MEDIA_DOMAINS) {
                if (href.contains(domain)) {
                    socialMediaLinks.add(href);
                }
            }
        }

        return new ScrapeModel(url, "success", new ArrayList<>(phoneNumbers), new ArrayList<>(socialMediaLinks), new ArrayList<>(addresses));
    }

    private void extractDataFromText(String text, Set<String> phones, Set<String> addresses) {


        Matcher matcher = PHONE_PATTERN.matcher(text);
        while (matcher.find()) {
            phones.add(matcher.group().trim());
        }

        Matcher addressMatcher = ADDRESS_PATTERN.matcher(text);
        while (addressMatcher.find()) {
            addresses.add(addressMatcher.group().trim());
        }
    }


    public List<ScrapeModel> scrapeFromCSV(MultipartFile file) {
        List<Future<ScrapeModel>> futureList = new ArrayList<>();
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cores * 2);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            futureList = reader.lines()
                    .map(line -> "http://" + line.trim())
                    .map(url -> executor.submit(() -> scrapeWebsite(url)))
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<ScrapeModel> results = new ArrayList<>();
        for (Future<ScrapeModel> future : futureList) {
            try {
                results.add(future.get());
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        executor.shutdown();
        return results;
    }
}
