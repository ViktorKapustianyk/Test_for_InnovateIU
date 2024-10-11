package org.viktor_kapustianyk;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private Map<String, Document> documents = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null) {
            String newIdForDocument = UUID.randomUUID().toString();
            document.setId(newIdForDocument);
            document.setCreated(Instant.now());
        } else {
            Document exsistDocument = documents.get(document.getId());
            if (exsistDocument != null) {
                document.setCreated(exsistDocument.getCreated());
            }
        }
        documents.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        List<Document> filteredDocuments = new ArrayList<>(documents.values());

        if (request.getTitlePrefixes() != null) {
            filteredDocuments = filterByTitlePrefixes(filteredDocuments, request.getTitlePrefixes());
        }
        if (request.getContainsContents() != null) {
            filteredDocuments = filterByContainsContents(filteredDocuments, request.getContainsContents());
        }
        if (request.getAuthorIds() != null) {
            filteredDocuments = filterByAuthorsIds(filteredDocuments, request.getAuthorIds());
        }
        if (request.getCreatedFrom() != null) {
            filteredDocuments = filterByCreatedFrom(filteredDocuments, request.getCreatedFrom());
        }
        if (request.getCreatedTo() != null) {
            filteredDocuments = filterByCreatedTo(filteredDocuments, request.getCreatedTo());
        }

        return filteredDocuments;
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documents.get(id));
    }

    private List<Document> filterByTitlePrefixes(List<Document> documents, List<String> titlePrefixes) {
        List<Document> result = new ArrayList<>();
        for (Document doc : documents) {
            for (String prefix : titlePrefixes) {
                if (doc.getTitle().startsWith(prefix)) {
                    result.add(doc);
                    break;
                }
            }
        }
        return result;
    }

    private List<Document> filterByContainsContents(List<Document> documents, List<String> containsContents) {
        List<Document> result = new ArrayList<>();
        for (Document doc : documents) {
            for (String content : containsContents) {
                if (doc.getContent().contains(content)) {
                    result.add(doc);
                    break;
                }
            }
        }
        return result;
    }

    private List<Document> filterByAuthorsIds(List<Document> documents, List<String> authorIds) {
        List<Document> result = new ArrayList<>();
        for (Document doc : documents) {
            if (authorIds.contains(doc.getAuthor().getId())) {
                result.add(doc);
            }
        }
        return result;
    }

    private List<Document> filterByCreatedFrom(List<Document> documents, Instant date) {
        List<Document> result = new ArrayList<>();
        for (Document doc : documents) {
            if (!doc.getCreated().isBefore(date)) {
                result.add(doc);
            }
        }
        return result;
    }
    private List<Document> filterByCreatedTo(List<Document> documents, Instant date) {
        List<Document> result = new ArrayList<>();
        for (Document doc : documents) {
            if (!doc.getCreated().isAfter(date)) {
                result.add(doc);
            }
        }
        return result;
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}
