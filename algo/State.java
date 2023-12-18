public class State{
    /*
    c : Caractère à matcher.
    out1 : Premier état de branche.
    out2 : Deuxième état de branche.
    */
    public char c;
    public State out1;
    public State out2;
    public int lastList;
    public State(){}
    public State(char c, State out1, State out2) {
        this.c = c;
        this.out1 = out1;
        this.out2 = out2;
        this.lastList = 0;
    }

    public State getOut(){
        return this.out1;
    }
}