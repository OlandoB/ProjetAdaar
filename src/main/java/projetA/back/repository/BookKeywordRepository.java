package projetA.back.repository;

import org.springframework.data.repository.CrudRepository;

import projetA.back.entity.BookKeywords;



public interface BookKeywordRepository extends CrudRepository<BookKeywords, Integer> {

    BookKeywords findByKeyword(String keyword);

}