package regex;

import java.util.Scanner;


// class permettant le lancement de notre egrep sur un fichier
// Option w permet d'afficher le mot et la ligne qui a permis d'afficher la ligne
// option nl permet d'afficher le nombre de ligne ayant été accepter
// Sans option on affiche toutes les lignes accepter par l'automate
public class Egrep {
    public static void myEgrep(Scanner reader, Automaton det, String option) {
        // initialisation
        String res = "";
        int cptLines = 0, cptValid = 0, cptWord;
        String line, currentLine; // variable utiliser dans l'option w et nl
        String[] words; // variables utiliser dans l'option w
        boolean addline;

        while(reader.hasNextLine()) {
            // initialisation du tour de la boucle
            currentLine = reader.nextLine();
            line = currentLine;
            words = line.split(" ");
            cptWord = 0;

            while(!line.isEmpty()) {
                // comptage des mots
                if(line.charAt(0) == ' ')
                    cptWord++;

                if (det.parcours(det, line)){
                    cptValid++;
                    if(option != null){
                        if(option.equals("w"))
                            System.out.println("Word : " + words[cptWord] + " is check, lines " + cptLines + " word number : " + cptWord);
                    }
                    else
                        System.out.println(currentLine);

                    break;
                }

                // avancement dans la ligne
                line = line.substring(1);

            }
            cptLines++;
        }
        // affichages du nombre de ligne ou des lignes
        if(option != null){
            if(option.equals("nl"))
                System.out.println(cptValid+" lignes présentant le motif");
        }

    }
}
