# Undo School — Course Search Application

A Spring Boot application that indexes course data into Elasticsearch and provides a powerful search API with filters, pagination, sorting, and autocomplete features.

## Tech Stack
- **Java 17**
- **Spring Boot 3.5.11**
- **Spring Data Elasticsearch**
- **Elasticsearch 8.13.0** (via Docker)
- **Lombok**
- **Maven**

## Project Setup

### 1. Prerequisites
- Docker Desktop installed and running.
- Java 17+ installed.

### 2. Start Elasticsearch
Run the following command to start the Elasticsearch cluster:
```bash
docker-compose up -d
```
Elasticsearch will be available at `http://localhost:9200`. Security and SSL are disabled for development convenience.

### 3. Run the Application
Start the Spring Boot application using the Maven wrapper:
```bash
./mvnw spring-boot:run
```
On startup, the application will:
1. Delete the existing `courses` index (if any).
2. Create the index with proper mappings (including completion fields).
3. Index 55 sample course documents from `src/main/resources/sample-courses.json`.

## API Usage

### 1. Basic Search
**Endpoint:** `GET /api/search`

| Parameter | Type | Description |
|---|---|---|
| `q` | `String` | Full-text search on title and description (supports fuzzy matching). |
| `category` | `String` | Filter by category (e.g., "Math", "Science", "Art"). |
| `type` | `String` | Filter by type (`COURSE`, `ONE_TIME`, `CLUB`). |
| `minAge` / `maxAge` | `Integer` | Filter by age range. |
| `minPrice` / `maxPrice` | `Double` | Filter by price range. |
| `startDate` | `String` | Filter by date (ISO-8601). |
| `sort` | `String` | Sorting: `upcoming` (default), `priceAsc`, `priceDesc`. |
| `page` | `int` | Zero-based page index. |
| `size` | `int` | Results per page. |

**Example Search (Fuzzy + Category Filter):**
```bash
curl "http://localhost:8080/api/search?q=Algeba&category=Math"
```

### 2. Autocomplete (Bonus Feature)
**Endpoint:** `GET /api/search/suggest`

Returns up to 10 matching course titles based on the provided prefix.

**Example Suggestion:**
```bash
curl "http://localhost:8080/api/search/suggest?q=Intr"
```
**Response:** `["Introduction to Algebra"]`

## Development Pace (Git Commits)
The project was built incrementally with 7 clean commits:
1. `feat: initial Spring Boot project setup`
2. `feat: add Docker Compose for Elasticsearch`
3. `feat: add 55 sample course documents`
4. `feat: define CourseDocument entity, repository, and startup bulk-indexer`
5. `feat: implement search with filters, pagination, sorting, and REST controller`
6. `feat: add autocomplete suggestions and fuzzy search (bonus)`
7. `fix: resolve suggest service 500 error`

## Implementation Details
- **Fuzziness:** Implemented in `multi_match` query using `fuzziness: AUTO`.
- **Autocomplete:** Uses the Elasticsearch `completion` suggester on a dedicated `suggest` field.
- **Data Indexing:** Automated on startup via `ApplicationRunner` and `IndexOperations`.
- **Search Logic:** Dynamically built using a `BoolQuery.Builder` in the `CourseSearchService`.
