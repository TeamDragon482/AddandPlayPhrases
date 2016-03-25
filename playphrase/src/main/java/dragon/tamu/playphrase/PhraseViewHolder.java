package dragon.tamu.playphrase;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

public class PhraseViewHolder extends ChildViewHolder{

    public TextView mPhraseText;
    public ImageView mDragIcon;
    public Phrase mPhrase;

    public PhraseViewHolder(View itemView)
    {
        super(itemView);

        mPhraseText = (TextView) itemView.findViewById(R.id.phrase_view);
        mDragIcon = (ImageView) itemView.findViewById(R.id.handle);
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
