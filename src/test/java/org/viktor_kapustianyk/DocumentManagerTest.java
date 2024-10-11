package org.viktor_kapustianyk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    void testSaveNewDocument() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Test Title")
                .content("Test Content")
                .author(DocumentManager.Author.builder()
                        .id("author1")
                        .name("Author Name")
                        .build())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId());
        assertEquals("Test Title", savedDocument.getTitle());
        assertEquals("Test Content", savedDocument.getContent());
    }

    @Test
    void testSearchByTitleContentAuthor() {
        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .title("Test Title 1")
                .content("Content 1")
                .author(DocumentManager.Author.builder()
                        .id("author1")
                        .name("Author 1")
                        .build())
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .title("Sample Title 2")
                .content("New Content 2")
                .author(DocumentManager.Author.builder()
                        .id("author2")
                        .name("Author 2")
                        .build())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest searchRequest = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Test"))
                .containsContents(List.of("Content"))
                .authorIds(List.of("author1"))
                .build();

        List<DocumentManager.Document> searchResults = documentManager.search(searchRequest);

        assertEquals(1, searchResults.size());
        assertEquals("Test Title 1", searchResults.get(0).getTitle());
        assertEquals("Content 1", searchResults.get(0).getContent());
        assertEquals("author1", searchResults.get(0).getAuthor().getId());
    }

    @Test
    void testFindById() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Test Title")
                .content("Test Content")
                .author(DocumentManager.Author.builder()
                        .id("author1")
                        .name("Author Name")
                        .build())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);
        Optional<DocumentManager.Document> documentFindById = documentManager.findById(savedDocument.getId());

        assertTrue(documentFindById.isPresent());
        assertEquals(savedDocument.getId(), documentFindById.get().getId());
    }
}