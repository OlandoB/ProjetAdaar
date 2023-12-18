package regex;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Class permettant l'écriture sous format dot de notre automate
public class DotWriter {
    public static void automatonToDot(Automaton auto){
        Map<State, Set<Transition>> data = auto.getTransitions();
        try {
            // Récupération des prérequis
            BufferedWriter writer = new BufferedWriter(new FileWriter("graph.dot"));
            writer.write("digraph G {");
            writer.newLine();
            List<State> startingList = auto.getStartingStates();
            List<State> acceptingList = auto.getAcceptingStates();
            String line = "";

            // Mise en couleurs des entrées et sorties dans le fichier dot
            for(State s : startingList) {
                writer.write(s.label + "[shape=doublecircle, color=red];\n");
            }
            for(State s : acceptingList) {
                writer.write(s.label + "[shape=doublecircle, color=green];\n");
            }

            // Ajout de toutes les transitions
            for (State s : data.keySet()) {
                for (Transition t : data.get(s)) {
                    if(t.label == Transition.EPSILON)
                        line += s.label + " -> " + t.aim.label + "[label=\"" + "epsilon" + "\"];\n";
                    else if (t.label == Transition.DOT)
                        line += s.label + " -> " + t.aim.label + "[label=\"" + "." + "\"];\n";
                    else
                        line += s.label + " -> " + t.aim.label + "[label=\"" + (char)t.label + "\"];\n";
                    writer.write(line);
                    line = "";
                }
            }

            // fermeture du fichier
            writer.newLine();
            writer.write("}");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
