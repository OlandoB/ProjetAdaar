package projetA.back.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import projetA.back.entity.Book;

public class GutenbergApiClient {

    public static void main(String[] args) {
        int numberOfBooksToRetrieve = 1664;
        List<Book> books = new ArrayList<Book>();
        for (int i = 1; i <= numberOfBooksToRetrieve; i++) {
            Book book = retrieveBooksFromGutenberg(i);
            book = null;
            System.out.println("Livre recup : " + i);
        }
        System.out.println("Nombre de livres récupérés : " + books.size());
    }

    

    public static Book retrieveBooksFromGutenberg(int numberOfBooks) {
        String apiUrl = "https://www.gutenberg.org/cache/epub/" + numberOfBooks + "/pg" + numberOfBooks + ".txt";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            Map<String, Integer> wordOccurrences = countKeywordsBook(reader);
            
            reader.close();

            connection.disconnect();
            return new Book(numberOfBooks, wordOccurrences);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static Map<String, Integer> countKeywordsBook(BufferedReader reader) {
        Map<String, Integer> wordOccurrences = new HashMap<>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                
                if (line.startsWith("*** START")) {
                    continue;
                }

                if (line.startsWith("Updated editions will")) {
                    break;
                }
                // Diviser la ligne en mots en utilisant l'espace comme séparateur
                String[] words = line.split("\\s+");

                // Mettre à jour la map des occurrences
                for (String word : words) {
                    // Nettoyer le mot en enlevant la ponctuation et en le mettant en minuscules
                    word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();

                    // Exclure les mots de moins de 5 lettres et ceux contenant des caractères spéciaux
                    if (word.length() > 5 && !containsSpecialCharacter(word)) {
                        wordOccurrences.put(word, wordOccurrences.getOrDefault(word, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wordOccurrences;
    }

    public static void printMap(Map<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    private static boolean containsSpecialCharacter(String word) {
        // Vérifie si le mot contient des caractères spéciaux
        return !word.matches("[a-zA-Z0-9]+");
    }

   
}
