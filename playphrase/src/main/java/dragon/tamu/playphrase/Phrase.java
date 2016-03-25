package dragon.tamu.playphrase;


public class Phrase
{
    private String mPhraseText;
	public Map<String, String> phraseLanguages; //LangAbrv, fileLocation

    public Phrase(String text)
    {
        mPhraseText = text;
		phraseLanguages = new HashMap<>();
    }
	public Phrase(String s, Map<String, String> pL){
        mPhraseText = s;
        phraseLanguages.putAll(pL);
    }
    public void addLanguage(String langAbrv, String fileLocation){
        phraseLanguages.put(langAbrv, fileLocation);


    public String getPhraseText() {
        return mPhraseText;
    }

    public void setPhraseText(String mPhraseText) {
        this.mPhraseText = mPhraseText;
    }
}

