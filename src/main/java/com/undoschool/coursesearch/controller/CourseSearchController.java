package com.undoschool.coursesearch.controller;

import com.undoschool.coursesearch.dto.SearchRequestDTO;
import com.undoschool.coursesearch.dto.SearchResponseDTO;
import com.undoschool.coursesearch.service.CourseSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class CourseSearchController {

    private final CourseSearchService searchService;

    @GetMapping
    public ResponseEntity<SearchResponseDTO> search(SearchRequestDTO request) {
        return ResponseEntity.ok(searchService.search(request));
    }
}
