package dragon.tamu.playphrase;


import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

public class PhraseViewHolder_NoDrag extends ChildViewHolder implements View.OnClickListener {

    public TextView mPhraseText;
    public Phrase mPhrase;
    public OnItemClickListener mListener;

    public PhraseViewHolder_NoDrag(View itemView, OnItemClickListener listener)
    {
        super(itemView);

        mPhraseText = (TextView) itemView.findViewById(R.id.phrase_view);

        mListener = listener;

        itemView.setOnClickListener(this);
    }

    public Phrase getPhrase() {
        return mPhrase;
    }

    public void setPhrase(Phrase phrase)
    {
        mPhrase = phrase;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null)
            mListener.onItemCLick(v, mPhrase);
    }

    public interface OnItemClickListener {
        void onItemCLick(View v, Phrase p);
    }
}
