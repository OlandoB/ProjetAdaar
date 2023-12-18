package Sorbonne;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Date;

@Entity
public class Book {
    @Id
    private Integer id;
    private String title;
    private String author;
    private Date release;
    private String language;
    private String content;
    private List<String> otherBooks;

    public Book(){}
    public Book(String title,
                String author, Date release,
                String language, String content,
                List<String> otherBooks){
        this.title = title;
        this.author = author;
        this.release = release;
        this.language = language;
        this.content = content;
        this.otherBooks = otherBooks;
    }

    public Integer getId(){
        return this.id;
    }
    public String getTitle(){
        return this.title;
    }
    public String getAuthor(){
        return this.author;
    }
    public Date getRelease(){
        return this.release;
    }
    public String getLanguage(){
        return this.language;
    }
    public String getContent(){
        return this.content;
    }
    public List<String> getoOtherBooks(){
        return this.otherBooks;
    }

    private void setId(Integer id){
        this.id = id;
    }
    private void setTitle(String title){
        this.title = title;
    }
    private void setAuthor(String author){
        this.author = author;
    }
    private void setRelease(Date release){
        this.release = release;
    }
    private void setLanguage(String language){
        this.language = languge;
    }
    private void setContent(String content){
        this.content = content;
    }
    private void setOtherBooks(List<String> otherBooks){
        this.otherBooks = otherBooks;
    }



}