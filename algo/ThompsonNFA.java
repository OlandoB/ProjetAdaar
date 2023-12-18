import java.util.*;
import java.io.*;
import java.lang.reflect.Array;

public class ThompsonNFA {
    static final int SPLIT = 256;
    static final int MATCH = 257;
    static final char CONCAT = '.';
    static final char OR = '|';
    static final char OPEN_PAREN = '(';
    static final char CLOSE_PAREN = ')';
    static final Set<Character> OPERATORS = Set.of('*', '+', '?');
    static final char ZERO_OR_ONE = '?';
    static final char ZERO_OR_MORE = '*';
    static final char ONE_OR_MORE = '+';

    private int stateId = 0;
    private int listId = 0;

    

    // Constructor and variables

    public ThompsonNFA() {
        /*
        stateId : attribuer un ID unique à chaque état dans le NFA 
        listId : identifie la liste actuelle du fragment NFA. Lors de la correspondance, à chaque caractère correspondant, une nouvelle liste est créée et le listId augmente de 1.
        */
        this.stateId = 0;
        this.listId = 0;
    }

    public int getStateId() {
       /*
       Cette fonction sert à attribuer un ID unique à chaque état dans l'automate fini non déterministe (NFA).
       */
       this.stateId++;
       return this.stateId;
    }
    
    public String re2post(String regex) {
        /*
        Convert the input infix regular expression to a postfix expression.
        */
        
        int nalt = 0, natom = 0;
        ArrayList<Integer> paren = new ArrayList<Integer>();
        String res = "";
        
        int size = regex.length();
		for(int i = 0; i < size ; i ++){
		    if(regex.charAt(i) == this.OPEN_PAREN){
		        if(natom > 1){
		            natom--;
		            res = Utils.append_operator(res, this.CONCAT, 1);
		        }
		        //paren.add(nalt, natom)
		        nalt = 0; natom = 0;
		    }else
		    if (regex.charAt(i) == this.OR){
		        if(natom == 0){
                    return null;
                }
                res = Utils.append_operator(res, this.CONCAT, natom - 1);
                natom = 0; nalt++;
		    }else
            if (regex.charAt(i) == this.CLOSE_PAREN){
                if(paren.isEmpty() || natom == 0){
                    return null;
                }
                res = Utils.append_operator(res, this.CONCAT, natom - 1);
                res = Utils.append_operator(res, this.OR, nalt);
                natom++;
                nalt += paren.get(paren.size() - 1);
                paren.remove(paren.size() - 1);
            }else
            if (this.OPERATORS.contains(regex.charAt(i))){
                if(natom == 0){
                    return null;
                }
                res += regex.charAt(i);
            }else{
                if(natom > 1){
                    natom--;
                    res = Utils.append_operator(res, this.CONCAT, 1);
                }
                res += regex.charAt(i);
                natom++;
            }
		}
        if(paren.isEmpty()){
            return null; 
        }
        res = Utils.append_operator(res, this.CONCAT, natom - 1);
        res = Utils.append_operator(res, this.OR, nalt);
        
        return res;
    }

    public Frag post2nfa(String postfix) {
        /*
        Convert the postfix expression to a NFA.
        */
        Stack<Frag> stack = new Stack<Frag>();
        Frag e1, e2, e;
        int n;
        int size = postfix.length();
        for(int i = 0; i < size; i++){
            switch(postfix.charAt(i)){
                case ThompsonNFA.CONCAT:
                    e2 = stack.pop();
                    e1 = stack.pop();
                    e1.out = e2.start;
                    e = new Frag(e1.start, e2.out);
                    stack.push(e);
                    break;
                case ThompsonNFA.OR:
                    e2 = stack.pop();
                    e1 = stack.pop();
                    n = getStateId();
                    e = new Frag(n, e1.start);
                    e.out = e2.start;
                    e1.patch(e2.out);
                    e2.patch(n);
                    stack.push(e);
                    break;
                case ThompsonNFA.ZERO_OR_ONE:
                    e = stack.pop();
                    n = getStateId();
                    e1 = new Frag(n, e.start);
                    e2 = new Frag(n, null);
                    e.patch(n);
                    e1.out = e2.start;
                    stack.push(e1);
                    break;
                case ThompsonNFA.ZERO_OR_MORE:
                    e = stack.pop();
                    n = getStateId();
                    e1 = new Frag(n, e.start);
                    e.patch(n);
                    e1.out = e1.start;
                    stack.push(e1);
                    break;
                case ThompsonNFA.ONE_OR_MORE:
                    e = stack.pop();
                    n = getStateId();
                    e1 = new Frag(n, e.start);
                    e.patch(n);
                    e1.out = e1.start;
                    stack.push(e);
                    break;
                default:
                    n = getStateId();
                    e = new Frag(n, null);
                    e.out = n + 1;
                    e.start = n;
                    stack.push(e);
                    break;
            }
        }
        e = stack.pop();
        if(!stack.isEmpty()){
            return null;
        }
        e.patch(this.getStateId());
        return e;
    }

    
    public boolean match(String regex, String s) {
        /*
        start : NFA
        input_str : Chaîne à matcher
        Cette fonction utilise le NFA construit pour matcher la chaîne d'entrée. Si elle réussit à la matcher, elle renvoie True.
        */
        String postfix = re2post(regex);
        Frag e = post2nfa(postfix);
        if(e == null){
            return false;
        }
        ArrayList<Integer> l1 = e.startlist();
        ArrayList<Integer> l2 = new ArrayList<Integer>();
        for(int i = 0; i < s.length(); i++){
            l2 = step(l1, s.charAt(i));
            l1 = l2;
        }
        return l1.contains(e.out);
    }

    
    public boolean is_match(String regex, String s) {
        /*
        states : Fragments du NFA qui correspondent avec succès après avoir parcouru la chaîne d'entrée.
        Cette fonction vérifie si, après avoir terminé le parcours, l'un des fragments NFA restants est 
        dans l'état MATCH. Si un fragment du NFA est dans l'état MATCH, alors il y a une correspondance réussie.
        */
        String postfix = re2post(regex);
        Frag e = post2nfa(postfix);
        if(e == null){
            return false;
        }
        ArrayList<Integer> l1 = e.startlist();
        ArrayList<Integer> l2 = new ArrayList<Integer>();
        for(int i = 0; i < s.length(); i++){
            l2 = step(l1, s.charAt(i));
            l1 = l2;
        }
        return l1.contains(e.out);
    }

    

    public ArrayList<Integer> step(ArrayList<Integer> l, char c) {
        /*
        cstates : Fragments du NFA actuellement correspondants avec succès.
        c : Caractère à matcher.
        nstates : Liste des états suivants, utilisée pour stocker tous les fragments du NFA qui correspondent avec succès au caractère c.
        Cette fonction vise à matcher le caractère c dans tous les fragments actuels du NFA qui correspondent avec succès. Si le match est 
        réussi, l'état suivant de ce fragment du NFA est ajouté à la liste nstates pour matcher le caractère suivant.
        */
        
        return null;
    }

    //add_state

    public void add_state(ArrayList<State> states, State state) {
        /*
        states : Liste des fragments NFA.
        state : État/fragment du NFA à ajouter.
        Cette fonction est utilisée pour ajouter "state" à "states", en prenant note que :
        1. Un état vide ou un état déjà existant ne doit pas être ajouté à nouveau.
        2. En cas d'état SPLIT, ses deux états de branche doivent être ajoutés.
        */

        // Si "state" est vide ou si "state" est déjà dans "states", ne pas l'ajouter à nouveau.
        if (state == null || states.contains(state)) {
            return;
        }

        // Si "state" est un état SPLIT, ajouter ses deux états de branche.
        if (state.c == SPLIT) {
            add_state(states, state.out1);
            add_state(states, state.out2);
        }else{
            states.add(state);
        }
    }

    // start_states
    public ArrayList<State> start_states(ArrayList<State> states) {
        /*
        states : Liste des fragments NFA.
        Cette fonction est utilisée pour ajouter tous les états de départ des fragments NFA à la liste "states".
        */
        for (State state : states) {
            add_state(states, state);
        }
        return null;
    }

    

    
}