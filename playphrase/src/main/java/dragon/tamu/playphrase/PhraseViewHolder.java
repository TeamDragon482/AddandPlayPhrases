package dragon.tamu.playphrase;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

public class PhraseViewHolder extends ChildViewHolder{

    public TextView mPhraseText, mAbbrevs;
    public ImageView mDragIcon;
    public Phrase mPhrase;

    public PhraseViewHolder(View itemView)
    {
        super(itemView);

        mPhraseText = (TextView) itemView.findViewById(R.id.phrase_view);
        mAbbrevs = (TextView) itemView.findViewById(R.id.phrase_abbrevs);
        mDragIcon = (ImageView) itemView.findViewById(R.id.handle);
    }

    public Phrase getPhrase() {
        return mPhrase;
    }

    public void setPhrase(Phrase phrase)
    {
        mPhrase = phrase;
    }
}
