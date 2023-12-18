package regex;

import java.io.*;
import java.util.Scanner;


/* class qui permet la mise en fichier sur 10 jets du temps mis pour effectuer la recherche de chacun des 3 algorithmes
avec pour le nom du fichier :
<Data_D_K_E_forRegex_<nom de la regEx utilisé>_onFile_<Nom du file utilisé>>
*/

public class Egrep_Kmp_Dfa {
    public static void main(String[] args) {
        // ------------------------- Preparation -------------------------
        System.out.println("Welcome to Bogota, Mr. Thomas Anderson.");
        String regEx;
        String fileName;
        if (args.length!=0) {
            regEx = args[0];
            fileName = args[1];
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("  >> Please enter a regEx: ");
            regEx = scanner.next();
            fileName = "babylon.txt";
        }
        RegEx re = new RegEx(regEx);

        if (regEx.length()<1) {
            System.err.println("  >> ERROR: empty regEx.");
        } else {

            try {
                long debut, fin, totalDfa, totalKmp;
                File file = new File(fileName);
                Scanner reader;
                String nameDirection =  "Data_D_K_E_forRegEx_" + regEx + "_onFile_" + fileName;
                File fichier = new File(nameDirection);
                PrintWriter writer;
                for(int i = 0; i < 10 ; i++){
                    // ----------------------------- Dfa -----------------------------
                    reader = new Scanner(file);
                    debut = System.currentTimeMillis();
                    RegExTree ret = re.parse();
                    Automaton automate = Automaton.treeToAutomaton(ret);
                    Automaton det = Automaton.determine(automate);
                    Egrep.myEgrep(reader, det, "nl");
                    fin = System.currentTimeMillis();
                    totalDfa = fin-debut;
                    System.out.println("time execution for Dfa: "+totalDfa+" milliseconds");
                    DotWriter.automatonToDot(det);

                    // ----------------------------- Kmp -----------------------------
                    /*reader = new Scanner(file);
                    debut = System.currentTimeMillis();
                    Kmp.myKMP(reader, regEx, "nl");
                    fin = System.currentTimeMillis();
                    totalKmp = fin-debut;
                    System.out.println("time execution for Kmp: "+totalKmp+" milliseconds");*/

                    // ---------------------------- Egrep -----------------------------

                    writer = new PrintWriter(new FileWriter("worstCaseBabylon.txt", true));
                    writer.println(totalDfa);
                    writer.close();
                    /*String[] command = {"bash", "./EgrepTime.sh", regEx, fileName, nameDirection};
                    ProcessBuilder egrep = new ProcessBuilder(command);
                    Process process = egrep.start();
                    process.waitFor();*/
                }






            }  catch (Exception e) {
                if(e instanceof FileNotFoundException){
                    System.err.println(" >> Error: File not found");
                }else
                    System.err.println("  >> ERROR: syntax error for regEx \""+regEx+"\".");
            }
        }


    }
}
