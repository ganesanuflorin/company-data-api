# Company-Data-Api

This project is a multi–module Spring Boot application that extracts, enriches, stores, and queries company profiles.

## 🔹 Overview
The system is divided into three main modules:

### Scraper Module
- Uses **Jsoup** and **Selenium** to crawl websites.
- Extracts key datapoints such as:
    - Phone numbers
    - Addresses
    - Social media links
- Handles both static and dynamically generated websites.

### Search Module
- Uses **ElasticSearch** to index and store company profiles.
- Merges the scraped data with an additional dataset (`sample-websites-company-names.csv`) containing:
    - Domain
    - Company commercial name
    - Company legal name
    - All available names
- Provides a **REST API** to query company profiles by one or multiple datapoints (e.g., name, website, phone number, or Facebook profile).


## 🔹 Workflow
1. **Scraping** – Websites are crawled to extract raw company information.
2. **Enrichment** – The extracted data is merged with the CSV dataset.
3. **Indexing** – Profiles are stored in ElasticSearch.
4. **Querying** – A REST API allows searching by different datapoints to retrieve the full company profile.

---

## 🔹 Goal
The goal of this project is to achieve a **high match rate** between input queries and stored company profiles by combining **web–scraped data** with **structured CSV data**, and leveraging **ElasticSearch** for flexible and scalable search.

## 🚀 Usage

### 1. Run ElasticSearch
Make sure you have an ElasticSearch instance running locally (default: `http://localhost:9200`).

You can start one with Docker:
```bash
docker compose up -d
```


