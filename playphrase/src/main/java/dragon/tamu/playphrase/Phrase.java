package dragon.tamu.playphrase;


import java.util.HashMap;
import java.util.Map;

public class Phrase
{
    public String name;
	public Map<String, String> phraseLanguages; //LangAbrv, fileLocation

    public Phrase(String text)
    {
        name = text;
		phraseLanguages = new HashMap<>();
    }
	public Phrase(String s, Map<String, String> pL){
        name = s;
        phraseLanguages.putAll(pL);
    }
    public void addLanguage(String langAbrv, String fileLocation) {
        phraseLanguages.put(langAbrv, fileLocation);
    }


    public String getPhraseText() {
        return name;
    }

    public void setPhraseText(String mPhraseText) {
        this.name = mPhraseText;
    }
}

