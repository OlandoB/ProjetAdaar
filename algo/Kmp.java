package regex;

import java.util.Scanner;

// class permettant l'utilisation du Kmp avec les mêmess options que dans Egrep
public class Kmp {
    // main de test de Kmp
    public static void main(String[] args) {
        int [] test = lpsTable("mami");
        for(int value : test)
            System.out.print(value + " ");
        System.out.println("\n le premier test affiche : " + Kmp.lineKMP("mamame mamu mia mama mi mamamia", "mami", 0, null));
        System.out.println(" le second test affiche : " + Kmp.lineKMP("mamame mamu mia mama mi mamd", "mami", 0, null));
    }

    // Méthode pour pouvoir lire un fichier à partir d'un scanner, d'une regEx et d'une potentielle option
    public static void myKMP(Scanner reader, String regEx,String option){
        int nbline = 1, reslen, cptValid = 0;
        String line;
        String res = "";
        // parcours de toutes les lignes et ajout a res si valide
        while(reader.hasNextLine()) {
            reslen = res.length();
            line = reader.nextLine();
            nbline++;
            res += lineKMP(line, regEx, nbline, option);

            if( reslen !=  res.length())
                cptValid ++;
        }

        if(option != null)
            if(option.equals("nl"))
                System.out.println(cptValid+" lignes présentant le motif");
        else
            System.out.println(res);
    }

    // création de la lps table de la regEx donnée
    public static int[] lpsTable(String regEx) {
        int[] lps = new int[regEx.length() + 1];
        lps[0] = -1;
        int lpsI = 0; // indice courant représentant le plus long prefixe
        int tableJ = 1; // indice parcourant tous le tableau
        int regExLen = regEx.length();

        while(tableJ < regExLen - 1){
            // prefixe de longueur I
            if(regEx.charAt(lpsI) == regEx.charAt(tableJ)){
                lpsI++;
                lps[tableJ + 1] = lpsI;
            }else{
                // prefixe de longueur 0
                lpsI = 0;
            }
            tableJ++;
        }
        return lps;
    }

    // Methode permettant l'analyse de la ligne grace a la methode KMP
    public static String lineKMP(String line, String regEx, int cptLines, String option){
        int[] lps = lpsTable(regEx); // tableau lps de la regEx
        int lineI = 0; // indice courant de la ligne
        int lpsJ = 1; // indice courant de lps
        int regExK = 0; // indice courant du plus long prefix dans la regEx
        int cptWord = 0; // compteur du nombre de mot pour l'option w
        int end = regEx.length(); // longueur de la regEx
        int lineLength = line.length(); // longueur de la ligne
        String[] words = line.split(" "); // tableau de mots pour l'option w

        while(lineI < lineLength){
            if(line.charAt(lineI) == ' ')
                cptWord++;
            //System.out.println("Valeur de lpsJ [ " + lpsJ + " ] valeur de regExK [ " + regExK + " ] valeur de lineI [ " + lineI + " ] pour l'analyse de " + line.charAt(lineI));
            if(line.charAt(lineI) == regEx.charAt(regExK)){
                if(regExK == end - 1) {
                    if(option != null){
                        if(option == "w")
                            System.out.println("Word : " + words[cptWord] + " is check, lines " + cptLines + " word number : " + cptWord);
                    }else
                        System.out.println(line);
                    return line;
                }
                lpsJ++;
                regExK++;

            }else {
                lpsJ = lps[lpsJ - 1];
                if(lpsJ == -1 || lpsJ == 0){
                    lpsJ = 1;
                    regExK = 0;
                }else{
                    //System.out.println("je reprend a partir de " + regEx.charAt(lpsJ) + " dans mon mot avec un lps de " + lpsJ + " a savoir " + regEx.charAt(regExK) + " sur la ligne " + line.charAt(lineI));
                    lineI--;
                    regExK = lpsJ;
                    lpsJ++;
                }
            }
            lineI++;
        }
        return "";
    }

}
