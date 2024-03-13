package projetA.back.entity;

import java.util.Map;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import projetA.back.Utils;

@Entity
public class BookKeywords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String keyword;

    @ElementCollection
    private Map<Integer, Integer> booksOcc;


    public BookKeywords() {}

    public BookKeywords(String keyword, Map<Integer, Integer> booksOcc) {
        this.keyword = keyword;
        this.booksOcc = booksOcc;
    }

    

    public Integer getId(){
        return this.id;
    }

    public String getKeyword(){
        return this.keyword;
    }
    
    public Map<Integer, Integer> getBooksOcc(){
        return this.booksOcc;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public void setKeyword(String keyword){
        this.keyword = keyword;
    }

    public void setBooksOcc(Map<Integer, Integer> booksOcc){
        this.booksOcc = booksOcc;
    }

    public void addOcc(Integer bookId, Integer occurrence) {
        if (this.booksOcc.containsKey(bookId)) {
            int currentOccurrence = this.booksOcc.get(bookId);
            this.booksOcc.put(bookId, currentOccurrence + occurrence);
        } else {
            this.booksOcc.put(bookId, occurrence);
        }
    }
}