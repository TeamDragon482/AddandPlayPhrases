package dragon.tamu.playphrase;

import java.util.ArrayList;
import java.lang.String;
import java.lang.Object;


public class Category {
    public String name;
    public ArrayList<Phrase> phraseList;

    //Constructors
    public Category(String s){
        name = s;
        phraseList = new ArrayList<>();
    }
    public Category(String s, ArrayList<Phrase> aL){
        name = s;
        phraseList.addAll(aL);
    }

    public void addPhrase(Phrase p){
        phraseList.add(p);
    }

    @Override
    public String toString() {
        String result = name;
        for(int i=0; i<phraseList.size(); i++)
        {
            result += "\n" + phraseList.get(i);
        }
        return result;
    }
}