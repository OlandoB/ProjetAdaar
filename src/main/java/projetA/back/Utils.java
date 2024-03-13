package projetA.back;

import projetA.back.entity.Book;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.List;
import java.util.Comparator;
import org.springframework.core.io.ClassPathResource;


public class Utils {
    public static final AtomicInteger idCounter = new AtomicInteger(0);

    public static Book makeBook(String filePath) throws IOException {
        BufferedReader reader = null;

        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            String line;
            int metaDataInt = 0;
            boolean endMetaData = false;
            String author = "";
            String title = "";
            String releaseDate = "";
            String language = "";
            boolean dateRelease = false;

            while ((line = reader.readLine()) != null && !endMetaData) {
                boolean metadata = false;

                String[] words = line.split(" ");
                String res = "";

                for (String word : words) {
                    if (word.equals("Title:")) {
                        metadata = true;
                        continue;
                    }
                    if (word.equals("Author:")) {
                        metadata = true;
                        continue;
                    }
                    if (word.equals("Release")) {
                        metadata = true;
                        dateRelease = true;
                        continue;
                    }
                    if (word.equals("Language:")) {
                        metadata = true;
                        continue;
                    }
                    if (!metadata)
                        break;
                    if (word.equals("[EBook"))
                        break;
                    if (dateRelease) {
                        dateRelease = !dateRelease;
                        continue;
                    }

                    res += word + " ";
                }

                if (metaDataInt == 0 && !res.equals("")) {
                    title = res;
                    metaDataInt++;
                } else if (metaDataInt == 1 && !res.equals("")) {
                    author = res;
                    metaDataInt++;
                } else if (metaDataInt == 2 && !res.equals("")) {
                    releaseDate = res;
                    metaDataInt++;
                } else if (metaDataInt == 3 && !res.equals("")) {
                    language = res;
                    metaDataInt++;
                    endMetaData = true;
                }
            }
            //Book book = new Book(title, author, releaseDate, language, null, filePath, 0);
            //book.setKeywordsBook(countKeywordsBook(filePath));
            //top20(book.getKeywordsBook());
            return null;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static String extractContent(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        boolean isInsideContent = false;
        BufferedReader reader = null;
        
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("*** START")) {
                    isInsideContent = true;
                    continue;
                }

                if (line.startsWith("*** END")) {
                    isInsideContent = false;
                    break;
                }

                if (isInsideContent) {
                    contentBuilder.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }

        return contentBuilder.toString();
    }


 





    public static void testmakeBook(String file){
        try {
            Book test = makeBook(file);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

   

    
    private static boolean containsSpecialCharacter(String word) {
        // Vérifie si le mot contient des caractères spéciaux
        return !word.matches("[a-zA-Z0-9]+");
    }
   public static Map<String, Integer> countKeywordsBook(BufferedReader reader) {
        Map<String, Integer> wordOccurrences = new HashMap<>();
        try {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                
                if (line.startsWith("*** START")) {
                    continue;
                }

                if (line.startsWith("*** END")) {
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
                i++;
            }
            System.out.println(" VOICI MON I : "+i + " \n\n\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wordOccurrences;
    }
}
