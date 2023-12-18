package regex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class Automaton {
	private Map<State, Set<Transition>> transitions = new HashMap<>();

	public Automaton(){}



	public static Automaton treeToAutomaton(RegExTree tree) throws Exception {
		// non-deterministic automaton so only one accepting state and one starting state
		Automaton res = new Automaton();
		if (tree.root == RegEx.ALTERN) {
			if (tree.subTrees.size() != 2) {
				throw new Exception("alternation does not have two terms");
			}

			// new starting and accepting states
			State s1 = new State(State.getCpt());
			s1.setStarting(true);
			State s2 = new State(State.getCpt());
			s2.setAccepting(true);

			// appels récursifs
			Automaton r1 = treeToAutomaton(tree.subTrees.get(0));
			Automaton r2 = treeToAutomaton(tree.subTrees.get(1));

			// merging
			List<State> r1_starting_list = r1.getStartingStates();
			List<State> r2_starting_list = r2.getStartingStates();
			if (r1_starting_list.size() != 1 || r2_starting_list.size() != 1) {
				throw new Exception("more than 1 starting state");
			}
			State r1_starting = r1_starting_list.get(0);
			r1_starting.setStarting(false);
			State r2_starting = r2_starting_list.get(0);
			r2_starting.setStarting(false);
			List<State> r1_accepting_list = r1.getAcceptingStates();
			List<State> r2_accepting_list = r2.getAcceptingStates();
			if (r1_accepting_list.size() != 1 || r2_accepting_list.size() != 1) {
				throw new Exception("more than 1 accepting state");
			}
			State r1_accepting = r1_accepting_list.get(0);
			r1_accepting.setAccepting(false);
			State r2_accepting = r2_accepting_list.get(0);
			r2_accepting.setAccepting(false);
			res.copyTransitions(r1.getTransitions());
			res.copyTransitions(r2.getTransitions());
			Set<Transition> tmp = new  HashSet<>();
			tmp.add(new Transition(Transition.EPSILON,r1_starting));
			tmp.add(new Transition(Transition.EPSILON,r2_starting));
			res.addTransitions(s1, tmp);
			tmp = new  HashSet<>();
			tmp.add(new Transition(Transition.EPSILON, s2));
			res.addTransitions(r1_accepting, tmp);
			res.addTransitions(r2_accepting, tmp);
			res.addTransitions(s2, new HashSet<>());
		} else if (tree.root == RegEx.CONCAT) {
			if (tree.subTrees.size() != 2) {
				throw new Exception("concatenation does not have two terms");
			}
			// appels récursifs
			Automaton r1 = treeToAutomaton(tree.subTrees.get(0));
			Automaton r2 = treeToAutomaton(tree.subTrees.get(1));

			// merging
			List<State> r1_accepting_list = r1.getAcceptingStates();
			List<State> r2_starting_list = r2.getStartingStates();
			if (r1_accepting_list.size() != 1 || r2_starting_list.size() != 1) {
				throw new Exception("more than 1 starting and accepting state");
			}
			State r1_accepting = r1_accepting_list.get(0);
			r1_accepting.setAccepting(false);
			State r2_starting = r2_starting_list.get(0);
			r2_starting.setStarting(false);

			res.copyTransitions(r1.getTransitions());
			res.copyTransitions(r2.getTransitions());
			Set<Transition> tmp = new HashSet<>();
			tmp.add(new Transition(Transition.EPSILON, r2_starting));
			res.addTransitions(r1_accepting, tmp);
		} else if (tree.root == RegEx.ETOILE) {
			if (tree.subTrees.size() != 1) {
				throw new Exception("étoile does not have one term");
			}
			// new starting and accepting states
			State s1 = new State(State.getCpt());
			s1.setStarting(true);
			State s2 = new State(State.getCpt());
			s2.setAccepting(true);

			// appels récursifs
			Automaton r1 = treeToAutomaton(tree.subTrees.get(0));

			// merging
			List<State> r1_starting_list = r1.getStartingStates();
			List<State> r1_accepting_list = r1.getAcceptingStates();
			if (r1_accepting_list.size() != 1 || r1_starting_list.size() != 1) {
				throw new Exception("more than 1 starting and accepting state");
			}
			State r1_accepting = r1_accepting_list.get(0);
			r1_accepting.setAccepting(false);
			State r1_starting = r1_starting_list.get(0);
			r1_starting.setStarting(false);
			res.copyTransitions(r1.getTransitions());

			// ajout des transitions
			Set<Transition> tmp = new HashSet<>();
			tmp.add(new Transition(Transition.EPSILON, r1_starting));
			tmp.add(new Transition(Transition.EPSILON, s2));
			res.addTransitions(r1_accepting, tmp);
			tmp = new HashSet<>();
			tmp.add(new Transition(Transition.EPSILON, r1_starting));
			tmp.add(new Transition(Transition.EPSILON, s2));
			res.addTransitions(s1, tmp);
			res.addTransitions(s2, new HashSet<>());
		} else if (tree.root == RegEx.DOT) {
			// feuille
			if (!tree.subTrees.isEmpty()) {
				throw new Exception("supposedly a leaf but has subtrees");
			}
			State s1 = new State(State.getCpt());
			s1.setStarting(true);
			State s2 = new State(State.getCpt());
			s2.setAccepting(true);
			Set<Transition> tmp = new HashSet<>();
			tmp.add(new Transition(Transition.DOT, s2));
			res.addTransitions(s1, tmp);
			res.addTransitions(s2, new HashSet<>());
		} else {
			// feuille
			if (!tree.subTrees.isEmpty()) {
				throw new Exception("supposedly a leaf but has subtrees");
			}
			State s1 = new State(State.getCpt());
			s1.setStarting(true);
			State s2 = new State(State.getCpt());
			s2.setAccepting(true);
			Set<Transition> tmp = new HashSet<>();
			tmp.add(new Transition(tree.root, s2));
			res.addTransitions(s1, tmp);
			res.addTransitions(s2, new HashSet<>());
		}
		return res;
	}





	public static Set<State> epsilonClosure(Collection<State> init, Automaton a) {
		List<State> res = new ArrayList<>();
		Map<State, Set<Transition>> trans = a.getTransitions();
		res.addAll(init);
		int i=0;
		while (i<res.size()) {
			State curr = res.get(i);
			for (Transition t : trans.get(curr)) {
				if (t.label == Transition.EPSILON && !res.contains(t.aim)) {
					res.add(t.aim);
				}
			}
			i++;
		}
		return new HashSet<>(res);
	}


	public static Set<State> subset(Set<State> init, int label, Automaton a) {
		Set<State> res = new HashSet<>();
		Map<State, Set<Transition>> trans = a.getTransitions();
		for (State curr : init) {
			for (Transition t : trans.get(curr)) {
				if ((t.label == label || t.label == Transition.DOT) && !res.contains(t.aim)) {
					res.add(t.aim);
				}
			}
		}
		return epsilonClosure(res, a);
	}


	// fonction de déterminisation de l'automate courant
	public static Automaton determine(Automaton a) {
		Map<Set<State>, State> memory = new HashMap<>();
		Stack<Set<State>> pile = new Stack<>();
		Automaton res = new Automaton();

		// initialisation
		Set<Integer> labels = a.getTransitionsLabelSet();
		Set<State> curr = epsilonClosure(a.getStartingStates(), a);
		State currState = new State(State.getCpt());
		currState.setStarting(isStarting(curr));
		currState.setAccepting(isAccepting(curr));
		memory.put(curr, currState);
		pile.add(curr);
		Set<State> toVisit;
		State newState;
		Set<Transition> tmp;

		while (!pile.isEmpty()) {
			curr = pile.pop();
			currState = memory.get(curr);
			tmp = new HashSet<>();
			for (Integer l : labels) {
				toVisit = subset(curr, l, a);
				if (toVisit.isEmpty()) {
					continue;
				}
				newState = memory.get(toVisit);
				if (newState == null) {
					newState = new State(State.getCpt());
					newState.setStarting(isStarting(toVisit));
					newState.setAccepting(isAccepting(toVisit));
					memory.put(toVisit, newState);
					pile.push(toVisit);
				}
				tmp.add(new Transition(l,newState));
			}
			res.addTransitions(currState, tmp);
		}
//		printMemory(memory);
		return res;
	}






	// Affiche l'automate
	private static void printMemory(Map<Set<State>, State> mem) {
		System.out.println("printing memory");
		State tmp;
		for (Set<State> ls : mem.keySet()) {
			tmp = mem.get(ls);
			System.out.println(tmp+" ::");
			for (State s : ls) {
				System.out.println("\t-->"+s+"");
			}
		}
	}

	// Parcours de l'automate sur une ligne donnée Renvoie true si la ligne est acceptable false sinon
	public boolean parcours(Automaton automate, String line) {
		// Initialisation
		Set<State> entryStateList = new HashSet<>(automate.getStartingStates());
		int phraseI = 0; // indice parcourant la ligne
		int phraseLen = line.length();

		// Tant que la phrase n'est pas entièrement parcouru ou alors que j'ai des étants d'entrée.
		while (phraseI < phraseLen && !entryStateList.isEmpty()) {
			int charPhrase = line.charAt(phraseI);
			Set<State> nextEntryStates = new HashSet<>();

			// pour tous les états d'entrées
			for (State state : entryStateList) {
				Set<Transition> transitions = automate.getTransitions().get(state);
// on regarde s'il possede des transitions avec comme label la lettre courante de la ligne
				if (transitions != null) {
					for (Transition transition : transitions) {
						if (transition.label == charPhrase || transition.label == Transition.DOT) {
							nextEntryStates.add(transition.getEndState());
							//System.out.println("jai trouvé !");
							if(transition.getEndState().accepting)
								return true;
						}
					}
				}
			}
			// cas pas de transition
			if (nextEntryStates.isEmpty()) {
				return false;
			}
			// Nouvelle entrée de transition possible
			entryStateList = nextEntryStates;
			phraseI++;
		}
		// fin de la ligne
		return false;
	}

	// Rajoute dans les transitions de l'automate courant une nouvelle map.
	public void copyTransitions(Map<State, Set<Transition>> other) {
		transitions.putAll(other);
	}

	// Ajoute dans la map des transition le set en argument a l'état de l'argument dans la map ou lui créer une clef sinon

	public void addTransitions(State s, Set<Transition> t) {
		if (transitions.containsKey(s)) {
			transitions.get(s).addAll(t);
		} else {
			transitions.put(s, t);
		}
	}


    // transformation de l'automate en string
	@Override
	public String toString() {
		String res = "";
		for (State s : transitions.keySet()) {
			res+=s.toString();
			res+="\n";
			for (Transition t : transitions.get(s)) {
				res+=t.toString();
				res+="\n";
			}
		}
		return res;
	}

	// return tous les états sortant de l'automate
	public List<State> getAcceptingStates() {
		List<State> res = new ArrayList<>();
		for (State s : transitions.keySet()) {
			if (s.accepting) res.add(s);
		}
		return res;
	}

	// return tous les états entrant de l'automate
	public List<State> getStartingStates() {
		List<State> res = new ArrayList<>();
		for (State s : transitions.keySet()) {
			if (s.starting) res.add(s);
		}
		return res;
	}

	// return Le label de toutes les transitions sous forme de set
	public Set<Integer> getTransitionsLabelSet() {
		Set<Integer> res = new HashSet<>();
		for (Set<Transition> lt : transitions.values()) {
			for (Transition t : lt) {
				res.add(t.label);
			}
		}
		res.remove(0);
		return res;
	}

	// return la map de transition de l'automate courant.
	public Map<State, Set<Transition>> getTransitions() {
		return transitions;
	}



	// return True si un état a pour état starting dans la collection en argument, faux sinon
	public static boolean isStarting(Collection<State> l) {
		for (State s : l) {
			if (s.starting) {
				return true;
			}
		}
		return false;
	}

	// return True si un état a pour état accepting dans la collection en argument, faux sinon
	public static boolean isAccepting(Collection<State> l) {
		for (State s : l) {
			if (s.accepting) {
				return true;
			}
		}
		return false;
	}

}


// Class représentant un état de n'autre automate
class State {
	private static int cpt = 0; //compteur pour nommer les états
	protected int label;
	protected boolean starting;
	protected boolean accepting;

	public State(int label) {
		this.label = label;
		starting = false;
		accepting = false;
	}
	public void setStarting(boolean starting) {
		this.starting = starting;
	}
	public void setAccepting(boolean accepting) {
		this.accepting = accepting;
	}

	// return l'Int représentant le label de l'état que l'on veut créer. Gère également l'incrémentation du compteur.
	public static int getCpt() {
		int oldcpt = cpt;
		cpt++;
		return oldcpt;
	}

	@Override
	public String toString() {
		return "("+label+", "+starting+", "+accepting+")";
	}

}


// Class représentant une transition dans notre automaton
class Transition {
	static final int EPSILON = 0;
	static final int DOT = -1;
	protected int label;
	protected State aim;

	public Transition(int label, State aim) {
		this.label = label;
		this.aim = aim;
	}
	@Override
	public String toString() {
		char letter;
		if (label == EPSILON) letter = ':';
		else if (label == DOT) letter = '.';
		else letter = (char)label;
		return "  --"+letter+"-->"+aim.toString();
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof Transition) {
			Transition other = (Transition)o;
			if (other.label == label) {
				return other.aim.equals(aim);
				// entre non-deterministe et DFA, pas les memes labels parce que compteur global
			}
		}
		return false;
	}
	public State getEndState() {return aim;}

}