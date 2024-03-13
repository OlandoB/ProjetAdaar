package projetA.back.Algo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class NFA {
    private static final int SPLIT = 256;
    private static final int MATCH = 257;
    private static final char CONCAT = '.';
    private static final char OR = '|';
    private static final char OPEN_PAREN = '(';
    private static final char CLOSE_PAREN = ')';
    private static final char ZERO_OR_ONE = '?';
    private static final char ZERO_OR_MORE = '*';
    private static final char ONE_OR_MORE = '+';
    private int stateId = 0;
    private int listId = 0;
    public NFA(){

    }

//    Description de la fonction: Crée et retourne une nouvelle liste contenant un seul état.
//    Paramètre: state - L'état à ajouter à la liste.
//    Détails de l'implémentation: Crée une nouvelle liste, ajoute l'état fourni à la liste, et retourne cette liste.
    private List<State> list1(State state) {
        List<State> list = new ArrayList();
        list.add(state);
        return list;
    }


    private int getStateId() {
        return this.stateId++;
    }

//    Description de la fonction: Convertit une expression régulière infixe en notation postfixe.
//    Paramètre: regex - L'expression régulière à convertir.
//    Détails de l'implémentation: Utilise un algorithme basé sur une pile pour convertir l'expression régulière, en gérant les opérateurs de concaténation implicite, d'union, et les parenthèses.
    public String re2post(String regex) {
        Stack<Integer> paren = new Stack();
        StringBuilder res = new StringBuilder();
        int nalt = 0;
        int natom = 0;
        char[] var6 = regex.toCharArray();
        int var7 = var6.length;

        label81:
        for(int var8 = 0; var8 < var7; ++var8) {
            char c = var6[var8];
            switch (c) {
                case '(':
                    if (natom > 1) {
                        --natom;
                        res.append('.');
                    }

                    paren.push(nalt);
                    paren.push(natom);
                    nalt = 0;
                    natom = 0;
                    break;
                case ')':
                    if (paren.isEmpty() || natom == 0) {
                        return null;
                    }

                    while(true) {
                        --natom;
                        if (natom <= 0) {
                            while(nalt-- > 0) {
                                res.append('|');
                            }

                            natom = (Integer)paren.pop();
                            nalt = (Integer)paren.pop();
                            ++natom;
                            continue label81;
                        }

                        res.append('.');
                    }
                case '*':
                case '+':
                case '?':
                    if (natom == 0) {
                        return null;
                    }

                    res.append(c);
                    break;
                case '|':
                    if (natom == 0) {
                        return null;
                    }

                    while(true) {
                        --natom;
                        if (natom <= 0) {
                            ++nalt;
                            continue label81;
                        }

                        res.append('.');
                    }
                default:
                    if (natom > 1) {
                        --natom;
                        res.append('.');
                    }

                    res.append(c);
                    ++natom;
            }
        }

        if (!paren.isEmpty()) {
            return null;
        } else {
            while(true) {
                --natom;
                if (natom <= 0) {
                    while(nalt-- > 0) {
                        res.append('|');
                    }

                    return res.toString();
                }

                res.append('.');
            }
        }
    }

//    Description de la fonction: Connecte une liste d'états de sortie à un nouvel état.
//    Paramètre: out - La liste des états à patcher, state - L'état de destination pour les transitions.
//    Détails de l'implémentation: Itère sur la liste out et met à jour le ou les états de sortie pour pointer vers state.
    private void patch(List<State> out, State state) {
        Iterator var3 = out.iterator();

        while(var3.hasNext()) {
            State s = (State)var3.next();
            if (s != null) {
                if (s.c == 256) {
                    if (s.out == null) {
                        s.out = state;
                    } else if (s.out1 == null) {
                        s.out1 = state;
                    }
                } else {
                    s.out = state;
                }
            }
        }

    }

//    Description de la fonction: Concatène deux listes d'états de sortie.
//    Paramètre: out1, out2 - Les listes d'états à concaténer.
//    Détails de l'implémentation: Crée une nouvelle liste contenant tous les éléments de out1 suivis par tous les éléments de out2.
    private List<State> append(List<State> out1, List<State> out2) {
        List<State> result = new ArrayList(out1);
        result.addAll(out2);
        return result;
    }
//Description de la fonction: Construit un NFA à partir d'une expression régulière en notation postfixe.
//Paramètre: postfix - L'expression régulière en notation postfixe.
//Détails de l'implémentation: Utilise une pile de fragments pour construire le NFA, en créant de nouveaux états et en patchant les listes de sortie selon les opérations spécifiées dans l'expression.
    public State post2nfa(String postfix) {
        Stack<Frag> stack = new Stack();
        char[] var3 = postfix.toCharArray();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            char c = var3[var5];
            Frag e2;
            Frag e1;
            State s;
            switch (c) {
                case '*':
                    e2 = (Frag)stack.pop();
                    s = new State(this.getStateId(), 256);
                    s.out = e2.start;
                    this.patch(e2.out, s);
                    stack.push(new Frag(s, this.list1(s)));
                    break;
                case '+':
                    e2 = (Frag)stack.pop();
                    s = new State(this.getStateId(), 256);
                    s.out = e2.start;
                    this.patch(e2.out, s);
                    stack.push(new Frag(e2.start, this.list1(s)));
                    break;
                case '.':
                    e2 = (Frag)stack.pop();
                    e1 = (Frag)stack.pop();
                    this.patch(e1.out, e2.start);
                    stack.push(new Frag(e1.start, e2.out));
                    break;
                case '?':
                    e2 = (Frag)stack.pop();
                    s = new State(this.getStateId(), 256);
                    s.out = e2.start;
                    stack.push(new Frag(s, this.append(e2.out, this.list1(s))));
                    break;
                case '|':
                    e2 = (Frag)stack.pop();
                    e1 = (Frag)stack.pop();
                    s = new State(this.getStateId(), 256);
                    s.out = e1.start;
                    s.out1 = e2.start;
                    stack.push(new Frag(s, this.append(e1.out, e2.out)));
                    break;
                default:
                    s = new State(this.getStateId(), c);
                    stack.push(new Frag(s, this.list1(s)));
            }
        }

        Frag e = (Frag)stack.pop();
        if (!stack.isEmpty()) {
            return null;
        } else {
            this.patch(e.out, new State(this.getStateId(), 257));
            return e.start;
        }
    }
//  Description de la fonction: Teste si une chaîne donnée correspond à l'expression régulière représentée par le NFA.
//Paramètre: start - L'état de départ du NFA, inputStr - La chaîne à tester.
//Détails de l'implémentation: Simule l'exécution du NFA pour l'entrée donnée et retourne 1 si la chaîne correspond, sinon 0.
    public int match(State start, String inputStr) {
        new ArrayList();
        List<State> nStates = new ArrayList();
        List<State> cStates = this.startStates(start);
        char[] var5 = inputStr.toCharArray();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            char c = var5[var7];
            this.step((List)cStates, c, nStates);
            cStates = new ArrayList(nStates);
            if (nStates.isEmpty()) {
                return 0;
            }

            if (this.isMatch((List)cStates)) {
                return 1;
            }

            nStates.clear();
        }

        return 0;
    }
//  Description de la fonction: Vérifie si l'un des états actuels est un état de correspondance.
//Paramètre: states - La liste des états à vérifier.
//Détails de l'implémentation: Parcourt states et retourne true si un état de correspondance est trouvé.
    private boolean isMatch(List<State> states) {
        Iterator var2 = states.iterator();

        State state;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            state = (State)var2.next();
        } while(state.c != 257);

        return true;
    }
//  Description de la fonction: Avance le NFA d'un caractère dans la chaîne d'entrée.
//Paramètre: cStates - La liste des états courants, c - Le caractère d'entrée, nStates - La liste des nouveaux états après l'avancement.
//Détails de l'implémentation: Met à jour nStates en fonction des transitions possibles depuis cStates avec le caractère c.
    private void step(List<State> cStates, char c, List<State> nStates) {
        ++this.listId;
        Iterator var4 = cStates.iterator();

        while(var4.hasNext()) {
            State state = (State)var4.next();
            if (state.c == c) {
                this.addState(nStates, state.out);
            }
        }

    }
//  Description de la fonction: Ajoute un état à la liste des états courants s'il n'a pas déjà été ajouté.
//Paramètre: states - La liste des états à mettre à jour, state - L'état à ajouter.
//Détails de l'implémentation: Ajoute state à states s'il n'est pas null et s'il n'a pas encore été visité pendant cette simulation.
    private void addState(List<State> states, State state) {
        if (state != null && state.lastList != this.listId) {
            state.lastList = this.listId;
            if (state.c == 256) {
                this.addState(states, state.out);
                this.addState(states, state.out1);
            } else {
                states.add(state);
            }

        }
    }
//  Description de la fonction: Initialise la liste des états de départ pour la simulation du NFA.
//Paramètre: start - L'état de départ du NFA.
//Détails de l'implémentation: Crée une nouvelle liste d'états, y ajoute l'état de départ, et retourne cette liste.
    private List<State> startStates(State start) {
        List<State> states = new ArrayList();
        this.addState(states, start);
        return states;
    }


//  Description de la fonction: Représente un fragment d'un NFA en construction, avec un état de départ et une liste d'états de sortie.
//Paramètre: start - L'état de départ du fragment, out - La liste des états de sortie du fragment.
//Détails de l'implémentation: Stocke start et out pour une utilisation ultérieure dans la construction du NFA.
    private static class Frag {
        State start;
        List<State> out;

        Frag(State start, List<State> out) {
            this.start = start;
            this.out = out;
        }
    }
}
