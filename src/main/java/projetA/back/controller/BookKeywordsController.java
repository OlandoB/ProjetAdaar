package projetA.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import projetA.back.entity.BookKeywords;
import projetA.back.repository.BookKeywordRepository;

import java.util.Map;

@RestController
@RequestMapping("/keywords")
public class BookKeywordsController {

    @Autowired
    private BookKeywordRepository bookKeywordRepository;

    @GetMapping("/{keyword}")
    public Map<Integer, Integer> getBookOccurrencesByKeyword(@PathVariable String keyword) {
        BookKeywords keywordEntity = bookKeywordRepository.findByKeyword(keyword);
        if (keywordEntity != null) {
            return keywordEntity.getBooksOcc();
        } else {
            // Gérer le cas où le mot-clé n'est pas trouvé
            return null;
        }
    }

    // Autres méthodes du contrôleur pour les opérations CRUD (create, update, delete)
}