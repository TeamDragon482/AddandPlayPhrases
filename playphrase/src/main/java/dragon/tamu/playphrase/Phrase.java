package dragon.tamu.playphrase;

import java.util.HashMap;
import java.util.Map;
import java.lang.String;


public class Phrase {
    public String name;
    public Map<String, String> phraseLanguages; //LangAbrv, fileLocation

    //Constructor
    public Phrase(String s){
        name = s;
        phraseLanguages = new HashMap<>();
    }

    public Phrase(String s, Map<String, String> pL){
        name = s;
        phraseLanguages.putAll(pL);
    }
    public void addLanguage(String langAbrv, String fileLocation){
        phraseLanguages.put(langAbrv, fileLocation);
    }
}