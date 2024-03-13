package projetA.back.Algo;

import projetA.back.entity.Book;
import projetA.back.entity.BookKeywords;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KeyWords {

        public KeyWords(){
        }

        public int recherche(String mode, String regex, BookKeywords book){
            int res = -1;

             if (Objects.equals(mode,"2")) {
                res=DFAmaintitre(regex,book);
            } else if (Objects.equals(mode,"3")){
                res = Kmp(regex,book);
            }
             System.out.println(book.getKeyword());
            System.out.println(res);
            return res;
        }


        public static int Kmp(String input_post, BookKeywords book){
            Kmp kmp = new Kmp();
            if (kmp.lineKmpOcc(book.getKeyword(), input_post)!=0){
                return 1;
            }else{
                return 0;
            }
        }
        public static int DFAmaintitre(String input_post, BookKeywords book) {

            DFA_exact regexDFA = new DFA_exact(input_post);
            if(regexDFA.match(book.getKeyword())==1){
                return 1;}

            return 0;
        }



}
