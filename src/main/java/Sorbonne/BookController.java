package Sorbonne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.sql.Date;


@Controller
@RequestMapping(path="/demo")
public class BookController {
    @Autowired
    private BookRepository bookRepository;

    @PostMapping(path="/book")
    public @ResponseBody String addNewBook (@RequestParam String title,
                                            @RequestParam String author, @RequestParam String date
                                            @RequestParam String language, @RequestParam String conteString
                                            @RequestParam String otherBooks) {


        Book Book = new Book("test", null, null, null, null, null);

        bookRepository.save(n);
        return "Saved";
    }


    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }
}