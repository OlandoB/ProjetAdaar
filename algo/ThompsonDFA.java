import java.util.ArrayList;
import java.util.Set;

public class ThompsonDFA {
    public static int SPLIT = 256;
    public static int MATCH = 257;

    private int listId;
    private Dstate dstate;
    private ThompsonNFA regexNFA;
    private String postNFA;
    private Frag start;
    private ArrayList<State> sDstart;


    public ThompsonDFA(String inputStr) {
        this.listId = 0;
        this.dstate = new Dstate();
        this.regexNFA = new ThompsonNFA();
        this.postNFA = regexNFA.re2post(inputStr);
        if(postNFA == null) {
            System.out.println("Error: Invalid regex");
            return;
        }
        this.start = regexNFA.post2nfa(inputStr);
        this.sDstart = startDstate(start);
    }

    //match(regex)
    public boolean match(String inputStr){
        /*
        start : DFA
        input_str : Chaîne de caractères en entrée
        Cette fonction est utilisée pour faire correspondre la chaîne d'entrée avec le DFA.
        */ 

        ArrayList<State> start = this.sDstart;
        int size = inputStr.length();
        int index;
        ArrayList<State> nextStates = new ArrayList<State>();
        boolean isIn = false;
        for(int i = 0; i < size; i++) {
            index = (int) inputStr.charAt(i);
            for(State s : start) {
                if(s.c == index) {
                    nextStates.add(s);
                    isIn = true;
                    break;
                }
            }
            if (isIn) {
                nextStates = nextDstate(start, inputStr.charAt(i));
                isIn = false;
            }
            if(is_match(nextStates.states)){
                return true;
            } else if (nextStates.isEmpty()){
                return false;
                start = nextStates;
            }
        }
        return is_match(start.states);
    }

    public boolean is_match(ArrayList<State> states) {
        for(State s : states) {
            if(s.c == MATCH) {
                return true;
            }
        }
        return false;
    }

    public int statesCmp(ArrayList<Dstate> states1, ArrayList<Dstate> states2) {
        /*
        states1 : Ensemble des états du fragment DFA
        states2 : Ensemble des états du fragment DFA
        Cette fonction est utilisée pour comparer deux états de DFA, selon les règles suivantes :
        1. Si len(states1) < len(states2), renvoie -1
        2. Si len(states1) > len(states2), renvoie 1
        3. Si les deux ont la même longueur, compare les adresses mémoire de chaque état dans states1 et states2
         */
        if(states1.size() < states2.size()) {
            return -1;
        } else if(states1.size() > states2.size()) {
            return 1;
        } else {
            int size1 = states1.size();
            for(int i = 0; i < size1; i++) {
                /*if (System.identityHashCode(states1[i]) < System.identityHashCode(states2[i])){
                    return -1;
                }else if (System.identityHashCode(states1[i]) > System.identityHashCode(states2[i])){
                    return 0;
                }*/
            }
            return 0;
        }
    }

    public Dstate getDstate(Dstate states) {
        /*
        states : Ensemble des états
        Cette fonction utilise l'ensemble des états comme clé pour obtenir le DState correspondant à cet ensemble.
        */
        Dstate dstate = this.dstate;
        int cmp;
        while(dstate != null){
            //cmp = statesCmp(states, dstate.getStates());
            if(cmp < 0) {
                dstate = dstate.getLeft();
            } else if(cmp > 0) {
                dstate = dstate.getRight();
            } else {
                return dstate;
            }
        }
        //dstate = 
        return dstate;
    }

    

    public ArrayList<State> step(ArrayList<State> cstates, int c, ArrayList<State> nstates) {
        /*
        cstates : Fragments du NFA actuellement correspondants avec succès.
        c : Caractère à matcher.
        nstates : Liste des états suivants, utilisée pour stocker tous les fragments du NFA qui correspondent avec succès au caractère c.
        Cette fonction vise à matcher le caractère c dans tous les fragments actuels du NFA qui correspondent avec succès. Si le match est 
        réussi, l'état suivant de ce fragment du NFA est ajouté à la liste nstates pour matcher le caractère suivant.
        */
        ArrayList<State> res = new ArrayList<State>();
        this.listId++;
        for(State s : cstates) {
            if(s.c == c) {
                res.add(s.getOut());
            }
        }
        return res;
    }

    

    public void addState(ArrayList<State> states, State state) {
        /*
        states : Liste des fragments NFA.
        state : État/fragment du NFA à ajouter.
        Cette fonction est utilisée pour ajouter "state" à "states", en prenant note que :
        1. Un état vide ou un état déjà existant ne doit pas être ajouté à nouveau.
        2. En cas d'état SPLIT, ses deux états de branche doivent être ajoutés.
            */
        if(state == null || state.lastList == this.listId) {
            return;
        }
        state.lastList = this.listId;
        if(state.c == SPLIT) {
            addState(states, state.out1);
            addState(states, state.out2);
            return;
        }
        states.add(state);
    }

    public ArrayList<State> startDstate(Frag start) {
        /*
        start : Fragment NFA de départ.
        Cette fonction est utilisée pour obtenir le DState correspondant au fragment NFA de départ.
        */
        ArrayList<State> states = new ArrayList<State>();
        this.listId++;
        addState(states, start.getOut());
        return getDstate(new Dstate(states));
    }

    public ArrayList<State> nextDstate(ArrayList<State> cstates, int c) {
        /*
        cstates : Fragments du NFA actuellement correspondants avec succès.
        c : Caractère à matcher.
        Cette fonction est utilisée pour obtenir le DState correspondant au fragment NFA suivant.
        */
        ArrayList<State> states = new ArrayList<State>();
        ArrayList<State> nstates = new ArrayList<State>();
        this.listId++;
        for(State s : cstates) {
            if(s.c == c) {
                addState(nstates, s.getOut());
            }
        }
        return getDstate(new Dstate(nstates));
    }

}

