package com.undoschool.coursesearch.dto;

import com.undoschool.coursesearch.document.CourseDocument;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchResponseDTO {
    private long total; // total matching documents
    private int page; // current page
    private int size; // page size
    private List<CourseDocument> courses; // matching results
}
