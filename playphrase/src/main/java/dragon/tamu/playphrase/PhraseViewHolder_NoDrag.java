package dragon.tamu.playphrase;


import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

public class PhraseViewHolder_NoDrag extends ChildViewHolder{

    public TextView mPhraseText;
    public Phrase mPhrase;

    public PhraseViewHolder_NoDrag(View itemView)
    {
        super(itemView);

        mPhraseText = (TextView) itemView.findViewById(R.id.phrase_view);
    }

    public void setPhrase(Phrase phrase)
    {
        mPhrase = phrase;
    }
    public Phrase getPhrase()
    {
        return mPhrase;
    }
}
