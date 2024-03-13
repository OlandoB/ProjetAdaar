package projetA.back.Algo;

import java.util.Scanner;


public class Kmp {



    // création de la lps table de la regEx donnée
    public static int[] lpsTable(String regEx) {
        int[] lps = new int[regEx.length() + 1];
        lps[0] = 0;
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




    public static void myKMPOcc(Scanner reader, String regEx){
        int nbline = 1, cptRec = 0;
        String line= "";
        int res = 0;
        // parcours de toutes les lignes et ajout a res si valide
        while(reader.hasNextLine()) {
            line = reader.nextLine();
            nbline++;
            res += lineKmpOcc(line, regEx);

        }

        System.out.println(res + " est le nombre d'oocc du mot " + regEx + " le file a " + nbline + " lignes et la ligne final est " + line);
    }

    public static int lineKmpOcc(String line, String regEx) {
        int[] lps = lpsTable(regEx); // tableau lps de la regEx
        int lineI = 0; // indice courant de la ligne
        int lpsJ = 1; // indice courant de lps
        int regExK = 0; // indice courant du plus long prefix dans la regEx
        int cptWord = 0; // compteur du nombre de mot pour l'option w
        int end = regEx.length(); // longueur de la regEx
        int lineLength = line.length(); // longueur de la ligne
        String[] words = line.split(" "); // tableau de mots pour l'option w
        int occ = 0;
        while(lineI < lineLength){
            if(line.charAt(lineI) == ' ')
                cptWord++;
            //System.out.println("Valeur de lpsJ [ " + lpsJ + " ] valeur de regExK [ " + regExK + " ] valeur de lineI [ " + lineI + " ] pour l'analyse de " + line.charAt(lineI));
            if(line.charAt(lineI) == regEx.charAt(regExK)){
                if(regExK == end - 1) {
                    occ++;
                    regExK = lps[regExK - 1];
                    lpsJ = regExK + 1;
                } else {
                    lpsJ++;
                    regExK++;
                }
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
        return occ;
    }

}