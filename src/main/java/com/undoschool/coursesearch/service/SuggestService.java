package com.undoschool.coursesearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import com.undoschool.coursesearch.document.CourseDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuggestService {

    private final ElasticsearchClient elasticsearchClient;

    public List<String> suggest(String prefix) {
        try {
            SearchResponse<Void> response = elasticsearchClient.search(s -> s
                    .index("courses")
                    .suggest(sg -> sg
                            .suggesters("title-suggest", fs -> fs
                                    .prefix(prefix)
                                    .completion(c -> c
                                            .field("suggest")
                                            .size(10)
                                            .skipDuplicates(true)))),
                    Void.class);

            return response.suggest().get("title-suggest").stream()
                    .flatMap(s -> s.completion().options().stream())
                    .map(CompletionSuggestOption::text)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch suggestions", e);
        }
    }
}
