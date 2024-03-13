package projetA.back.Algo;

import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DFA {

    private static final int SPLIT = 256;
    private static final int MATCH = 257;
    private final State start;

    private DState startDState;
    private int listId = 0;
    private DState dstate;



    private static class DState {
        List<State> states;
        Map<Character, DState> next;
        DState left;
        DState right;

        DState(List<State> states) {
            this.states = states;
            this.next = new HashMap<>();
            this.left = null;
            this.right = null;
        }
    }
//    Description de la fonction: Construit un DFA à partir d'une expression régulière en notation postfixe.
//    Paramètre: inputPost - La chaîne d'entrée contenant l'expression régulière en notation postfixe.
//    Détails de l'implémentation: Initialise le DFA en convertissant l'expression régulière en un NFA, puis en démarrant l'état de ce NFA. Lève une IllegalArgumentException si l'expression régulière est invalide.
    public DFA(String inputPost) throws IllegalArgumentException {
        this.listId = 0;
        this.dstate = null;

        NFA regexNFA = new NFA();
        String postNFA = regexNFA.re2post(inputPost);

        if (postNFA == null) {
            throw new IllegalArgumentException("Invalid regex pattern.");
        }

        this.start = regexNFA.post2nfa(postNFA);
        this.startDState = startDState(this.start);
    }

//    Description de la fonction: Vérifie si la chaîne d'entrée correspond à l'expression régulière du DFA.
//    Paramètre: inputStr - La chaîne à tester contre l'expression régulière du DFA.
//    Détails de l'implémentation: Parcourt la chaîne d'entrée caractère par caractère, mettant à jour l'état courant du DFA et vérifie à chaque étape si un état final est atteint. Retourne 1 si une correspondance est trouvée, 0 sinon.
    public int match(String inputStr) {
        DState current = startDState;

        for (char c : inputStr.toCharArray()) {
            if (current.next.containsKey(c)) {
                current = current.next.get(c);
            } else {
                current = nextDState(current, c);
            }
            if (isMatch(current.states)==1) {
                return 1;
            } else if (current.states.isEmpty()) {
                return 0;
            }
        }
        return isMatch(current.states);
    }
//    Description de la fonction: Détermine si les états actuels incluent un état de correspondance.
//    Paramètre: states - La liste des états actuels à vérifier.
//    Détails de l'implémentation: Vérifie chaque état de la liste pour voir s'il s'agit d'un état de correspondance, retournant 1 si trouvé, sinon 0.
    private int isMatch(List<State> states) {
        for (State state : states) {
            if (state.c == MATCH) {
                return 1;
            }
        }
        return 0;
    }

//    Description de la fonction: Compare deux listes d'états pour déterminer leur ordre relatif.
//    Paramètre: states1, states2 - Les listes d'états à comparer.
//    Détails de l'implémentation: Compare les tailles des listes d'états, puis les identifiants uniques des états pour déterminer l'ordre.
    private int statesCmp(List<State> states1, List<State> states2) {
        if (states1.size() < states2.size()) {
            return -1;
        } else if (states1.size() > states2.size()) {
            return 1;
        }

        for (int i = 0; i < states1.size(); i++) {
            int comparison = Integer.compare(System.identityHashCode(states1.get(i)),
                    System.identityHashCode(states2.get(i)));
            if (comparison != 0) {
                return comparison;
            }
        }

        return 0;
    }

//    Description de la fonction: Récupère ou crée un nouvel état DFA pour un ensemble donné d'états NFA.
//    Paramètre: states - La liste des états NFA.
//    Détails de l'implémentation: Trie les états NFA, recherche dans l'arbre des états DFA existants, ou crée un nouvel état DFA si aucun existant n'est trouvé.
//
    private DState getDState(List<State> states) {
        Collections.sort(states, (s1, s2) -> Integer.compare(System.identityHashCode(s1),
                System.identityHashCode(s2)));

        DState current = this.dstate;
        while (current != null) {
            int cmp = statesCmp(states, current.states);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                return current;
            }
        }

        DState newState = new DState(states);
        return newState;
    }

//    Description de la fonction: Met à jour l'ensemble des états NFA actuels en fonction du caractère d'entrée.
//            Paramètre: cStates - L'ensemble actuel des états NFA, c - Le caractère d'entrée, nStates - L'ensemble des nouveaux états NFA à mettre à jour.
//    Détails de l'implémentation: Parcourt les états actuels, ajoutant les états de sortie à l'ensemble des nouveaux états si le caractère correspond.
    private void step(List<State> cStates, char c, List<State> nStates) {
        listId++;
        for (State state : cStates) {
            if (state.c == c) {
                addState(nStates, state.out);
            }
        }
    }

//    Description de la fonction: Ajoute un état à l'ensemble des états actuels s'il n'a pas déjà été ajouté pendant la même recherche.
//    Paramètre: states - L'ensemble des états à mettre à jour, state - L'état à ajouter.
//    Détails de l'implémentation: Ajoute l'état à l'ensemble s'il n'est pas null et n'a pas été visité lors de cette recherche, en traitant récursivement les états SPLIT.
//
    private void addState(List<State> states, State state) {
        if (state == null || state.lastList == listId) {
            return;
        }

        state.lastList = listId;
        if (state.c == SPLIT) {
            addState(states, state.out);
            addState(states, state.out1);
        } else {
            if (!states.contains(state)) {
                states.add(state);
            }
        }
    }

//    Description de la fonction: Initialise l'état de départ du DFA.
//    Paramètre: start - L'état de départ du NFA.
//    Détails de l'implémentation: Crée et retourne le premier état DFA en appelant getDState sur l'ensemble initial d'états NFA.

    private DState startDState(State start) {
        List<State> states = new ArrayList<>();
        return getDState(startStates(states, start));
    }

//    Description de la fonction: Initialise et retourne l'ensemble des états de départ pour la recherche.
//    Paramètre: states - La liste des états à initialiser, start - L'état de départ du NFA.
//    Détails de l'implémentation: Incrémente listId, ajoute l'état de départ à l'ensemble des états, et retourne cet ensemble.
    private List<State> startStates(List<State> states, State start) {
        this.listId++;
        addState(states, start);
        return states;
    }

//    Description de la fonction: Détermine et retourne le prochain état DFA basé sur l'état actuel et le caractère d'entrée.
//            Paramètre: dstate - L'état DFA actuel, c - Le caractère d'entrée.
//    Détails de l'implémentation: Crée un nouvel ensemble d'états NFA basé sur le caractère d'entrée, récupère ou crée le DState correspondant, et met à jour la transition de l'état actuel vers ce nouvel état.
    private DState nextDState(DState dstate, char c) {
        List<State> nStates = new ArrayList<>();
        step(dstate.states, c, nStates);
        DState next = getDState(nStates);
        dstate.next.put(c, next);
        return next;
    }

}
