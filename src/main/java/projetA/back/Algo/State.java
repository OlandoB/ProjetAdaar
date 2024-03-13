package projetA.back.Algo;

public class State {
    int id;
    int c;
    State out;
    State out1;
    int lastList;

    public State(int id, int c) {
        this.id = id;
        this.c = c;
        this.out = null;
        this.out1 = null;
        this.lastList = 0;
    }
}
