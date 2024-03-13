package projetA.back.Algo;

import projetA.back.entity.Book;

import java.util.*;

import projetA.back.Algo.JsonFileFetcher;

public class Titre {

    public Titre(){
    }

    public int recherche(String mode, String regex, Book book){
        int res = -1;
        if (Objects.equals(mode, "1")){
            res=NFAmaintitre(regex,book);
        } else if (Objects.equals(mode,"2")) {
            res=DFAmaintitre(regex,book);
        } else if (Objects.equals(mode,"3")){
            res = Kmp(regex,book);
        }
        System.out.println(res);
        return res;
    }
    static Map<String, Integer> resultat_mot_fois = new HashMap<>();

    public static int Kmp(String input_post, Book book){
        Kmp kmp = new Kmp();
        String titre =JsonFileFetcher.gettitre(book.getBookId());
        System.out.println(titre);
        if (kmp.lineKmpOcc(titre,input_post)!=0){
            return 1;
        }else{
            return 0;
        }
    }
    public static int DFAmaintitre(String input_post, Book book) {
            String titre =JsonFileFetcher.gettitre(book.getBookId());
            System.out.println(titre);
            DFA regexDFA = new DFA(input_post);
            if (titre.length()<1){
                return 0;
            }
            for (int i = 0; i < titre.length(); i++){
                String substr = titre.substring(i);
                if(regexDFA.match(substr)==1)
                    return 1;
            }
            return 0;
    }
    public static int NFAmaintitre(String input_post, Book book) {

        String titre =JsonFileFetcher.gettitre(book.getBookId());
        System.out.println(titre);
        NFA regex = new NFA();
        String post = regex.re2post(input_post);
        State start = regex.post2nfa(post);
        assert titre != null;
        return regex.match(start,titre);
    }


}