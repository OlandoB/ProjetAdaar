package regex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/* class permettant l'utilisation de notre Egrep sur un regex et un fichier donner. Par défaut, le fichier est test2.txt
et la regex est demandé à l'exécution
 */
public class Experim {
	public static void main(String args[]) {
        // Initialisation
		System.out.println("Welcome to Bogota, Mr. Thomas Anderson.");
		String regEx;
		String fileName;
        // affectation du fichier et de la regEx
		if (args.length!=0) {
			regEx = args[0];
			fileName = args[1];
		} else {
			Scanner scanner = new Scanner(System.in);
			System.out.print("  >> Please enter a regEx: ");
			regEx = scanner.next();
			fileName = "test2.txt";
		}
		RegEx re = new RegEx(regEx);
		System.out.println("  >> Parsing regEx \""+regEx+"\".");
		System.out.println("  >> ...");

		if (regEx.length()<1) {
			System.err.println("  >> ERROR: empty regEx.");
		} else {

			System.out.print("  >> ASCII codes: ["+(int)regEx.charAt(0));
			for (int i=1;i<regEx.length();i++) System.out.print(","+(int)regEx.charAt(i));
			System.out.println("].");
			try {
                // Initialisation du regExTree
				long debut, fin, total;
				debut = System.currentTimeMillis();
				RegExTree ret = re.parse();
                // Création de l'automate + affichage
				Automaton automate = Automaton.treeToAutomaton(ret);
                //System.out.println(automate);

                // déterminisation de l'automate + affichage
				Automaton det = Automaton.determine(automate);
				//System.out.println(det);

                // écriture de l'automate sous un fichier . dot
				DotWriter.automatonToDot(automate);

                // utilisation de notre Egrep si l'option a été donnée dans la ligne de commande on l'utilise
				File file = new File(fileName);
				Scanner reader = new Scanner(file);
				if(args.length > 2)
					Egrep.myEgrep(reader, det, args[2]);
				else
					Egrep.myEgrep(reader, det, null);

                // Fin + affichage du temps d'execution
				fin = System.currentTimeMillis();
				total = fin-debut;
				System.out.println("time execution : "+total+" milliseconds");

			}  catch (Exception e) {
				if(e instanceof FileNotFoundException){
					System.err.println(" >> Error: File not found");
				}else
					System.err.println("  >> ERROR: syntax error for regEx \""+regEx+"\".");
			}
		}
		System.out.println("  >> ...");
		System.out.println("  >> Parsing completed.");
		System.out.println("Goodbye Mr. Anderson.");

	}
}
