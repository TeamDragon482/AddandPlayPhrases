package dragon.tamu.playphrase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

public class RecyclerListAdapter_NoDrag extends ExpandableRecyclerAdapter<CategoryViewHolder_NoDrag, PhraseViewHolder_NoDrag> {

    LayoutInflater mInflater;
    List<ParentListItem> mList;
    PhraseViewHolder_NoDrag.OnItemClickListener mListener;

    public RecyclerListAdapter_NoDrag(Context context, List<ParentListItem> parentItemList) {
        super(parentItemList);

        mInflater = LayoutInflater.from(context);

        mList = parentItemList;

    }

    @Override
    public CategoryViewHolder_NoDrag onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.expandable_group_item, viewGroup, false);
        return new CategoryViewHolder_NoDrag(view);
    }

    @Override
    public PhraseViewHolder_NoDrag onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.expandable_inner_item, viewGroup, false);

        return new PhraseViewHolder_NoDrag(view, mListener);

    }

    @Override
    public void onBindParentViewHolder(final CategoryViewHolder_NoDrag parentViewHolder, int position, ParentListItem parentListItem) {
        Category category = (Category) parentListItem;
        parentViewHolder.setCategory((Category) parentListItem);
        parentViewHolder.mCategoryTitle.setText(category.getCategoryTitle());
        if (category.phraseList.size() == 0)
            parentViewHolder.mArrow.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBindChildViewHolder(final PhraseViewHolder_NoDrag phraseViewHolder, int i, Object o)
    {

        Phrase phrase = (Phrase) o;
        phraseViewHolder.setPhrase((Phrase) o);
        phraseViewHolder.mPhraseText.setText(phrase.getPhraseText());

    }

    public void setOnItemClickListener(PhraseViewHolder_NoDrag.OnItemClickListener listener) {
        mListener = listener;
    }
}
