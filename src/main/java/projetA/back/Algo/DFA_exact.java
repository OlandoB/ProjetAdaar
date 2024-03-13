package projetA.back.Algo;

import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DFA_exact {
    // **********************************************************************************************
    // * Ce code Java est similaire à dfa.java, la seule différence étant que dfa.java permet une   *
    // * correspondance partielle, tandis que ce code est conçu pour une correspondance complète.   *
    // **********************************************************************************************
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

    public DFA_exact(String inputPost) throws IllegalArgumentException {
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


    public int match(String inputStr) {
        DState current = startDState;

        for (char c : inputStr.toCharArray()) {
            if (current.next.containsKey(c)) {
                current = current.next.get(c);
            } else {
                current = nextDState(current, c);
            }
        }
        return isMatch(current.states);
    }

    private int isMatch(List<State> states) {
        for (State state : states) {
            if (state.c == MATCH) {
                return 1;
            }
        }
        return 0;
    }


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

    private void step(List<State> cStates, char c, List<State> nStates) {
        listId++;
        for (State state : cStates) {
            if (state.c == c) {
                addState(nStates, state.out);
            }
        }
    }

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

    private DState startDState(State start) {
        List<State> states = new ArrayList<>();
        return getDState(startStates(states, start));
    }


    private List<State> startStates(List<State> states, State start) {
        this.listId++;
        addState(states, start);
        return states;
    }


    private DState nextDState(DState dstate, char c) {
        List<State> nStates = new ArrayList<>();
        step(dstate.states, c, nStates);
        DState next = getDState(nStates);
        dstate.next.put(c, next);
        return next;
    }

}
