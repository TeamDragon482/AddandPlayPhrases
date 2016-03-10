package dragon.tamu.playphrase;

public class Phrase
{
    private String mPhraseText;

    public Phrase(String text)
    {
        mPhraseText = text;
    }

    public String getPhraseText() {
        return mPhraseText;
    }

    public void setPhraseText(String mPhraseText) {
        this.mPhraseText = mPhraseText;
    }
}
