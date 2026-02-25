package com.undoschool.coursesearch.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.undoschool.coursesearch.document.CourseDocument;
import com.undoschool.coursesearch.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataIndexer implements ApplicationRunner {

    private final CourseRepository courseRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 1. Delete existing index
        IndexOperations indexOps = elasticsearchOperations.indexOps(CourseDocument.class);
        if (indexOps.exists()) {
            indexOps.delete();
            log.info("Deleted existing 'courses' index");
        }

        // 2. Create index and put mapping
        indexOps.create();
        indexOps.putMapping(indexOps.createMapping());
        log.info("Created 'courses' index with mappings");

        // 3. Read sample data
        InputStream is = getClass().getResourceAsStream("/sample-courses.json");
        List<CourseDocument> courses = objectMapper.readValue(is,
                new TypeReference<List<CourseDocument>>() {
                });

        // 4. Populate the suggest field for autocomplete
        courses.forEach(c -> {
            if (c.getTitle() != null) {
                c.setSuggest(new Completion(new String[] { c.getTitle() }));
            }
        });

        // 5. Bulk save
        courseRepository.saveAll(courses);
        log.info("Indexed {} courses into Elasticsearch", courses.size());
    }
}
