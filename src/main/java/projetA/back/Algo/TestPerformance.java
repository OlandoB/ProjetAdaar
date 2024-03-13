package projetA.back.Algo;

import org.springframework.boot.SpringApplication;
import projetA.back.BackApplication;

import java.util.ArrayList;
import java.util.List;

public class TestPerformance {
    public static void main(String[] args) {
        List<String> test_donnee =  new ArrayList<String>();
        for (int  i = 0; i < 20000000; i++){
            test_donnee.add("ababababababababababc");

        }
        test_donnee.add("abababababababababababd");
        String input_post="abd";
        // test pour kmp
        long startTime = System.currentTimeMillis();
        Kmp kmp = new Kmp();
        int i = 0;
        for (String test_mot: test_donnee){

            if (kmp.lineKmpOcc(test_mot, input_post)!=0){
                System.out.println("TMP trouve ");
                System.out.println(i);
                break;
            }else{
                i+=1;
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("TMP trouve le resultat （millsecond）: " + elapsedTime);

        // test pour DFA
        i=0;
        startTime = System.currentTimeMillis();
        DFA regexDFA = new DFA(input_post);
        for (String test_mot: test_donnee){
            if (regexDFA.match(test_mot)==1){

                System.out.println("DFA trouve ");
                System.out.println(i);
                break;
            }else{
                i+=1;
            }
        }
        endTime = System.currentTimeMillis();
        elapsedTime = endTime - startTime;
        System.out.println("DFA trouve le resultat （millsecond）: " + elapsedTime);
    }
}
