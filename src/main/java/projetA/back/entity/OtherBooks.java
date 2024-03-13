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


@Entity
public class OtherBooks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer otherBookId;

    public OtherBooks() {
    }

    public OtherBooks(Integer otherBookId) {
        this.otherBookId = otherBookId;
    }

    // Getters et setters
    public Integer getId() {
        return this.id;
    }

    public Integer getOtherBookId() {
        return this.otherBookId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setOtherBookId(Integer otherBookId) {
        this.otherBookId = otherBookId;
    }

}