import java.util.ArrayList;
import java.util.Set;

public class Dstate {
    private ArrayList<Dstate> states;
    private Set<Dstate> next;
    private Dstate right;
    private Dstate left;
    
    public Dstate(Dstate state){
        this.states = new ArrayList<Dstate>();
        this.states.add(state);
        this.next = null;
        this.right = null;
        this.left = null;
    }
    public Dstate(){}

    public ArrayList<Dstate> getStates() {
        return this.states;
    }

    public void setStates(ArrayList<Dstate> states) {
        this.states = states;
    }

    public Set<Dstate> getNext() {
        return this.next;
    }

    public void setNext(Set<Dstate> next) {
        this.next = next;
    }

    public Dstate getRight() {
        return this.right;
    }

    public void setRight(Dstate right) {
        this.right = right;
    }

    public Dstate getLeft() {
        return this.left;
    }

    public void setLeft(Dstate left) {
        this.left = left;
    }

    public void addState(Dstate state){
        this.states.add(state);
    }
}
