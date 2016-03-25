package dragon.tamu.playphrase;

import java.lang.String;
import java.lang.Object;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

public class Category implements ParentListItem{


    private List<Object> mPhraseList;
    private String mCategoryTitle;

    public Category(String mCategoryTitle) {
        this.mCategoryTitle = mCategoryTitle;
    }
    public Category(List<Object> phraseList, String title)
    {
        mPhraseList = phraseList;
        mCategoryTitle = title;
    }

    public void setChildItemList(List<Object> list)
    {
        mPhraseList = list;
    }

    public String getCategoryTitle() {
        return mCategoryTitle;
    }

    public void setCategoryTitle(String mCategoryTitle) {
        this.mCategoryTitle = mCategoryTitle;
    }

    @Override
    public List<?> getChildItemList() {
        return mPhraseList;

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
	public void addPhrase(Phrase p){
        phraseList.add(p);

    }
}
