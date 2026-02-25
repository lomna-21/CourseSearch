package com.undoschool.coursesearch.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.undoschool.coursesearch.document.CourseDocument;
import com.undoschool.coursesearch.dto.SearchRequestDTO;
import com.undoschool.coursesearch.dto.SearchResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public SearchResponseDTO search(SearchRequestDTO request) {
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        // 1. Full-text search on title + description (with fuzziness)
        if (request.getQ() != null && !request.getQ().isBlank()) {
            boolBuilder.must(m -> m.multiMatch(mm -> mm
                    .query(request.getQ())
                    .fields("title^2", "description")
                    .fuzziness("AUTO")));
        }

        // 2. Category exact filter
        if (request.getCategory() != null) {
            boolBuilder.filter(f -> f.term(t -> t
                    .field("category")
                    .value(request.getCategory())));
        }

        // 3. Type exact filter
        if (request.getType() != null) {
            boolBuilder.filter(f -> f.term(t -> t
                    .field("type")
                    .value(request.getType())));
        }

        // 4. Age range filter
        if (request.getMinAge() != null || request.getMaxAge() != null) {
            boolBuilder.filter(f -> f.range(r -> r
                    .number(n -> {
                        n.field("minAge");
                        if (request.getMinAge() != null)
                            n.gte(request.getMinAge().doubleValue());
                        if (request.getMaxAge() != null)
                            n.lte(request.getMaxAge().doubleValue());
                        return n;
                    })));
        }

        // 5. Price range filter
        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
            boolBuilder.filter(f -> f.range(r -> r
                    .number(n -> {
                        n.field("price");
                        if (request.getMinPrice() != null)
                            n.gte(request.getMinPrice());
                        if (request.getMaxPrice() != null)
                            n.lte(request.getMaxPrice());
                        return n;
                    })));
        }

        // 6. Date filter (on or after startDate)
        if (request.getStartDate() != null) {
            boolBuilder.filter(f -> f.range(r -> r
                    .date(d -> d
                            .field("nextSessionDate")
                            .gte(request.getStartDate()))));
        }

        List<SortOptions> sortOptions = buildSort(request.getSort());

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.bool(boolBuilder.build()))
                .withSort(sortOptions)
                .withPageable(PageRequest.of(request.getPage(), request.getSize()))
                .build();

        SearchHits<CourseDocument> hits = elasticsearchOperations.search(
                query, CourseDocument.class);

        List<CourseDocument> courses = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new SearchResponseDTO(
                hits.getTotalHits(),
                request.getPage(),
                request.getSize(),
                courses);
    }

    private List<SortOptions> buildSort(String sort) {
        return switch (sort != null ? sort : "upcoming") {
            case "priceAsc" -> List.of(SortOptions.of(s -> s
                    .field(f -> f.field("price").order(SortOrder.Asc))));
            case "priceDesc" -> List.of(SortOptions.of(s -> s
                    .field(f -> f.field("price").order(SortOrder.Desc))));
            default -> List.of(SortOptions.of(s -> s
                    .field(f -> f.field("nextSessionDate").order(SortOrder.Asc))));
        };
    }
}
