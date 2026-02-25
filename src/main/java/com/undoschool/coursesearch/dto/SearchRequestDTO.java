package com.undoschool.coursesearch.dto;

import lombok.Data;

@Data
public class SearchRequestDTO {
    private String q; // full-text search keyword
    private Integer minAge; // age range filter (lower)
    private Integer maxAge; // age range filter (upper)
    private String category; // exact match: "Math", "Science", etc.
    private String type; // exact match: "ONE_TIME", "COURSE", "CLUB"
    private Double minPrice; // price range filter (lower)
    private Double maxPrice; // price range filter (upper)
    private String startDate; // ISO-8601, show courses on or after this date
    private String sort = "upcoming"; // "upcoming" | "priceAsc" | "priceDesc"
    private int page = 0; // zero-based page index
    private int size = 10; // results per page
}
