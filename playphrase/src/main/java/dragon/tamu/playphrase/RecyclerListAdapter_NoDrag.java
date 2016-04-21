package dragon.tamu.playphrase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecyclerListAdapter_NoDrag extends ExpandableRecyclerAdapter<CategoryViewHolder_NoDrag, PhraseViewHolder_NoDrag> {

    LayoutInflater mInflater;
    List<ParentListItem> mList;
    PhraseViewHolder_NoDrag.OnItemClickListener mListener;
    FileAccessor fileSystem;
    ArrayList<String> currentlySelectedLangs;

    public RecyclerListAdapter_NoDrag(Context context, List<ParentListItem> parentItemList, FileAccessor fileSystem, ArrayList<String> selectedLangNames) {
        super(parentItemList);

        mInflater = LayoutInflater.from(context);
        this.fileSystem = fileSystem;
        mList = parentItemList;
        currentlySelectedLangs = selectedLangNames;

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
        String abbrevText = "";
        Map<String, String> langToAbbrev = fileSystem.getLangList();
        for (String s : phrase.phraseLanguages.keySet()) {
            if (langToAbbrev.containsKey(s)) {
                if (currentlySelectedLangs.size() > 0) {
                    if (currentlySelectedLangs.contains(s)) {
                        if (abbrevText.length() == 0) {
                            abbrevText += langToAbbrev.get(s);
                        } else
                            abbrevText += ", " + langToAbbrev.get(s);
                    }
                } else {
                    if (abbrevText.length() == 0) {
                        abbrevText += langToAbbrev.get(s);
                    } else
                        abbrevText += ", " + langToAbbrev.get(s);
                }
            }
        }
        phraseViewHolder.mAbbrevs.setText(abbrevText);

    }

    public void setOnItemClickListener(PhraseViewHolder_NoDrag.OnItemClickListener listener) {
        mListener = listener;
    }
}
