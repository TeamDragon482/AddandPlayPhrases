package dragon.tamu.playphrase;


import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

public class PhraseViewHolder_NoDrag extends ChildViewHolder implements View.OnClickListener {

    public TextView mPhraseText;
    public Phrase mPhrase;
    public MainActivity mainActivity;

    public PhraseViewHolder_NoDrag(View itemView, MainActivity activity)
    {
        super(itemView);

        mPhraseText = (TextView) itemView.findViewById(R.id.phrase_view);
        mainActivity = activity;
    }

    public void setPhrase(Phrase phrase)
    {
        mPhrase = phrase;
    }
    public Phrase getPhrase()
    {
        return mPhrase;
    }

    @Override
    public void onClick(View v) {
        PlayManager pm = new PlayManager();
        //pm.playPhrase(mPhrase, getselectAbrv());
    }
}
