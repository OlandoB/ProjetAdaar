package projetA.back.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import projetA.back.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

//drop tables book, book_keywords, book_keywords_books_occ, book_other_books, keywords_of_book, other_books;
@Entity
public class Book {
  @Id
  private Integer bookId;
  @OneToMany(cascade = CascadeType.ALL)
  private List<OtherBooks> otherBooks;
  private double clossnessCentrality;
  @ElementCollection
  @CollectionTable(name = "keywords_of_book", joinColumns = @JoinColumn(name = "book_id"))
  @MapKeyColumn(name = "keyword")
  @Column(name = "frequency")
  @Lob
  private Map<String, Integer> keywordsBook;

 
  

  public Book(){this.otherBooks = new ArrayList<OtherBooks>();}
  public Book(List<OtherBooks> otherBooks,  Integer bookId){
                this.otherBooks = otherBooks;
                this.bookId = bookId;
                this.otherBooks = new ArrayList<OtherBooks>();
            }
  public Book(Integer bookId, Map<String, Integer> keywordsBook) {
    this.bookId = bookId;
    this.keywordsBook = keywordsBook;
    this.otherBooks = new ArrayList<OtherBooks>();
  }

  public Integer getId(){return this.bookId;}
  public Integer getBookId(){return this.bookId;}
  public List<Integer> getOtherBooks(){
      List<Integer> res = new ArrayList<Integer>();
      for(OtherBooks ob :otherBooks ){
          res.add(ob.getOtherBookId());
      }
      return res;}


  public Map<String, Integer> getKeywordsBook(){return this.keywordsBook;}
  public double getClossnessCentrality(){return this.clossnessCentrality;}

  
  public void setId(Integer id){this.bookId = id;}
  public void setBookId(Integer id){this.bookId = id;}
  public void setOtherBooks(List<OtherBooks> otherBooks){this.otherBooks = otherBooks;}
  public void setKeywordsBook(Map<String, Integer> keywordsBook){
    this.keywordsBook = keywordsBook;
  }
  public void setClossnessCentrality(double clossnessCentrality){this.clossnessCentrality = clossnessCentrality;}

  public void addOtherBook(Integer bookId){
    boolean exist = false;
    for(OtherBooks otherBook : this.otherBooks){
      if(otherBook.getOtherBookId() == bookId){
        exist = true;
        break;
      }
    }
    if(!exist){
      this.otherBooks.add(new OtherBooks(bookId));
    }
  }


  public static Map<String, Integer> top(Map<String, Integer> keywords, int top){
        // Triez la map par valeurs (occurrences) dans l'ordre décroissant
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(keywords.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Créez une nouvelle map pour stocker les 20 occurrences les plus élevées
        Map<String, Integer> top20Occurrences = new HashMap<>();

        // Ne conservez que les 20 occurrences les plus élevées dans la nouvelle map
        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            if (count >= top) {
                break;
            }
            top20Occurrences.put(entry.getKey(), entry.getValue());
            count++;
        }

        // Affichez les 20 occurrences les plus élevées
        /*System.out.println("Les 20 occurrences les plus élevées :");
        for (Map.Entry<String, Integer> entry : top20Occurrences.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("taillle de top20Occurrences : " + top20Occurrences.size());
        */
        return top20Occurrences;
    }
}