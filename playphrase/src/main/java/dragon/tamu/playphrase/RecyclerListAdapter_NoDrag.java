package dragon.tamu.playphrase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

public class RecyclerListAdapter_NoDrag extends ExpandableRecyclerAdapter<CategoryViewHolder, PhraseViewHolder_NoDrag>{

    LayoutInflater mInflater;
    List<ParentListItem> mList;
    MainActivity activity;

    public RecyclerListAdapter_NoDrag(Context context, List<ParentListItem> parentItemList) {
        super(parentItemList);

        mInflater = LayoutInflater.from(context);

        mList = parentItemList;

        activity = (MainActivity) context;

    }

    @Override
    public CategoryViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.expandable_group_item, viewGroup, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public PhraseViewHolder_NoDrag onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.expandable_inner_item, viewGroup, false);

        return new PhraseViewHolder_NoDrag(view, activity);

    }

    @Override
    public void onBindParentViewHolder(final CategoryViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        Category category = (Category) parentListItem;
        parentViewHolder.setCategory((Category) parentListItem);
        parentViewHolder.mCategoryTitle.setText(category.getCategoryTitle());
    }

    @Override
    public void onBindChildViewHolder(final PhraseViewHolder_NoDrag phraseViewHolder, int i, Object o)
    {

        Phrase phrase = (Phrase) o;
        phraseViewHolder.setPhrase((Phrase) o);
        phraseViewHolder.mPhraseText.setText(phrase.getPhraseText());

    }
}
