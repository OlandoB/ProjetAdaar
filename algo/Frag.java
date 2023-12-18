import java.util.ArrayList;
public class Frag{
        /*
        start : État de départ du fragment NFA.
        out : État de sortie du fragment NFA.
        */
        public int start;
        public Integer out;

        public Frag(int start, Integer out) {
            this.start = start;
            this.out = out;
        }

        public void patch(int out) {
            /*
            out : État de sortie du fragment NFA.
            Cette fonction est utilisée pour mettre à jour l'état de sortie du fragment NFA.
            */
            this.out = out;
        }

        public void patch(ArrayList<Integer> out) {
            /*
            out : Liste des états de sortie du fragment NFA.
            Cette fonction est utilisée pour mettre à jour l'état de sortie du fragment NFA.
            */
            this.out = out.get(0);
        }

        public ArrayList<Integer> startlist() {
            /*
            Cette fonction est utilisée pour retourner une liste contenant l'état de départ du fragment NFA.
            */
            ArrayList<Integer> l = new ArrayList<Integer>();
            l.add(this.start);
            return l;
        }
    }