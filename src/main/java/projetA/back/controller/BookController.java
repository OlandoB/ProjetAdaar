package projetA.back.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import projetA.back.entity.Book;
import projetA.back.entity.BookKeywords;
import projetA.back.repository.BookKeywordRepository;
import projetA.back.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.sql.Date;
import projetA.back.Utils;
import projetA.back.Algo.KeyWords;
import projetA.back.Algo.Titre;


@Controller
@RequestMapping(path="/projetADaar")
public class BookController {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookKeywordRepository bookKeywordRepository;
    private final int numberOfBooksToRetrieve = 40;
    private final double threshold = 0.5;

    /* add the numberOfBooksToRetrieve first book on the application */
    @PostMapping(path="/bookKeyword")
    public @ResponseBody String addNewBookAndKeyword () {
        Book book;
        int i;
        for (i = 1; i <= numberOfBooksToRetrieve; i++) {
            try { 
                book = GutenbergApiClient.retrieveBooksFromGutenberg(i);
                Map<String, Integer> keywordsBook = Book.top(book.getKeywordsBook(), 50);
                for (Map.Entry<String, Integer> entry : keywordsBook.entrySet()) {
                    String keyword = entry.getKey();
                    Integer occurrences = entry.getValue();
                    BookKeywords existingKeyword = bookKeywordRepository.findByKeyword(keyword);
                    if (existingKeyword != null) {
                        existingKeyword.getBooksOcc().put(book.getId(), occurrences);
                        bookKeywordRepository.save(existingKeyword);
                    } else {
                        Map<Integer, Integer> booksOcc = new HashMap<>();
                        booksOcc.put(book.getId(), occurrences);
                        BookKeywords newKeyword = new BookKeywords(keyword, booksOcc);
                        bookKeywordRepository.save(newKeyword);
                    }
                }
                System.out.println("Livre recup : " + i);
                bookRepository.save(book);
            } catch (Exception e) {
                System.out.println("Error while retrieving book " + i);
                continue;
            }
            
        }
        System.out.println("Tous les livres sont créés");
        makeJaccardGraph();
        System.out.println("Graph fini");
        calculateClosenessCentralityOfAllBook((List<Book>)bookRepository.findAll());
        System.out.println("Closeness centrality fini");
        return "good";
    }

    
    @PostMapping(path="/book")
    public @ResponseBody String addNewBook (@RequestParam int i) {
        Book book;
        book = GutenbergApiClient.retrieveBooksFromGutenberg(i);
        book.addOtherBook(5);
        book.addOtherBook(65);
        bookRepository.save(book);
         return "good";
    }

    /* make the jaccard graph of every book with a threshold of 50% */
    @GetMapping(path="/makeJaccardGraph")
    public @ResponseBody String makeJaccardGraph () {
        List<Book> allBooks =  (List<Book>)bookRepository.findAll();
        for(Book book : allBooks){
            for(Book otherBook : allBooks){
                if(book.getId() != otherBook.getId()){
                    double distance = calculateDistance(book.getKeywordsBook(), otherBook.getKeywordsBook());
                    if(distance >= threshold){
                        book.addOtherBook(otherBook.getId());
                        bookRepository.save(book);
                    }
                }
            }
        }
        return "good";
    }



    /* set the closeness centrality of every book */
    public String calculateClosenessCentralityOfAllBook (List<Book> allBooks) {
        for(Book book : allBooks){
            book.setClossnessCentrality(calculateClosenessCentrality(book, allBooks));
            bookRepository.save(book);
        }
         return "good";
    }

    /* return all book from bookrepository */
    @GetMapping(path="allBook")
    public @ResponseBody Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /* return all book from bookKywordrepository */
    @GetMapping(path="allKeywords")
    public @ResponseBody Iterable<BookKeywords> getAllKeywords() {
        return bookKeywordRepository.findAll();
    }
    

    /* return the distance between 2 book */
    public static double calculateDistance(Map<String, Integer> document1, Map<String, Integer> document2) {
        double numerator = 0.0;
        double denominator = 0.0;
        Set<String> allWords = unionOfWords(document1.keySet(), document2.keySet());
        for (String word : allWords) {
            int k1 = document1.getOrDefault(word, 0);
            int k2 = document2.getOrDefault(word, 0);
            numerator += Math.max(k1, k2) - Math.min(k1, k2);
            denominator += Math.max(k1, k2);
        }

        if (denominator == 0.0) {
            return 0.0;
        }
        return numerator / denominator;
    }

    /* return the closenessCentrality of the book v */
    public double calculateClosenessCentrality(Book v, List<Book> allBooks) {
        int n = allBooks.size();
        double sigmaUV = 0.0;
        for (Book u : allBooks) {
            if (u.getId() != v.getId()) {
                double distance = calculateDistance(u.getKeywordsBook(), v.getKeywordsBook());
                sigmaUV += distance;
            }
        }
        if (sigmaUV == 0.0) {
            return 0.0;
        }
        return (n - 1) / sigmaUV;
    }

    /* return the union of 2 set */
    private static Set<String> unionOfWords(Set<String> set1, Set<String> set2) {
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        return union;
    }


    /* utilise Algo.Titre pour farie recherche de title en algo TMP*/
    @GetMapping(path="/searchtitlekmp")
    public @ResponseBody List<Integer> searchKMPOnTitle(@RequestParam String keyword) {
        String regex = keyword;

        long startTime = System.currentTimeMillis();

        List<Book> allBooks =  (List<Book>)bookRepository.findAll();

        List<Integer> result = new ArrayList<Integer>();
        Titre titre = new Titre();
        for(Book book : allBooks){
            int res = titre.recherche("3",regex,book);

            if (res ==1){
                result.add(book.getBookId());
            }
            System.out.println(book.getBookId());

        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("TMP trouve le resultat （millsecond）: " + elapsedTime);
        return result;
    }

    /* utilise Algo.Keywotd pour farie recherche de keyword en algo TMP*/
    @GetMapping(path="/search/globalKeyword/kmp")
    public @ResponseBody List<Integer> searchKMPOnGlobalKeyword(@RequestParam String keyword) {
        long startTime = System.currentTimeMillis();

        List<BookKeywords> allBooks =  (List<BookKeywords>)bookKeywordRepository.findAll();

        KeyWords kw = new KeyWords();
        List<Integer> result = new ArrayList<>();
        for(BookKeywords keyword_all : allBooks){
            if (kw.recherche("3",keyword,keyword_all)==1){
                result = keyword_all.getBooksOcc().entrySet().stream()
                        .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed()) // 根据次数降序排序
                        .map(Map.Entry::getKey) // 获取书籍ID
                        .collect(Collectors.toList()); // 收集到List中
                System.out.println(result);
                return result;
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("TMP trouve le resultat （millsecond）: " + elapsedTime);
        return result;
    }
    /* utilise Algo.Keywords pour farie recherche de title en algo DFA*/
    @GetMapping(path="/search/title/RegEx")
    public @ResponseBody List<Integer> searchRegExOnTitle(@RequestParam String keyword) {
        String regex = keyword;

        long startTime = System.currentTimeMillis();

        List<Book> allBooks =  (List<Book>)bookRepository.findAll();

        List<Integer> result = new ArrayList<Integer>();
        Titre titre = new Titre();
        for(Book book : allBooks){
            int res = titre.recherche("2",regex,book);

            if (res >=1){
                result.add(book.getBookId());
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("TMP trouve le resultat （millsecond）: " + elapsedTime);
        return result;
    }

    /* utilise Algo.Keywords pour farie recherche de keywords en algo DFA*/
    @GetMapping(path="/search/globalKeyword/RegEx")
    public @ResponseBody List<Integer> searchRegExOnGlobalKeyword(@RequestParam String keyword) {

        long startTime = System.currentTimeMillis();

        List<BookKeywords> allBooks =  (List<BookKeywords>)bookKeywordRepository.findAll();

        KeyWords kw = new KeyWords();
        List<Integer> result = new ArrayList<>();
        for(BookKeywords keyword_all : allBooks){
            if (kw.recherche("2",keyword,keyword_all)==1){
                System.out.println(keyword_all.getBooksOcc());
                result = keyword_all.getBooksOcc().entrySet().stream()
                        .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed()) // 根据次数降序排序
                        .map(Map.Entry::getKey) // 获取书籍ID
                        .collect(Collectors.toList()); // 收集到List中
                System.out.println(result);
                return result;
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("TMP trouve le resultat （millsecond）: " + elapsedTime);
        return result;
    }
    
    

    /* return the index.html */
    @GetMapping("/")
    public String showIndex() {
        return "index.html";
    }

    @GetMapping(path="/getrelatedbookid")
    public @ResponseBody List<Integer> getlelatedbookid(@RequestParam Integer id){
        List<Book> allBooks =  (List<Book>)bookRepository.findAll();
    for(Book book: allBooks){
        if (Objects.equals(book.getBookId(), id)){
            return book.getOtherBooks();
        }
    }
    return null;
    }

    
    
}
