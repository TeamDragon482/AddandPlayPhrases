package dragon.tamu.playphrase;

<<<<<<< remotes/origin/Seth_Dev
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

public class Category implements ParentListItem{

=======
import java.lang.String;
import java.lang.Object;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

public class Category implements ParentListItem{

>>>>>>> local
    private List<Object> mPhraseList;
    private String mCategoryTitle;

    public Category(String mCategoryTitle) {
        this.mCategoryTitle = mCategoryTitle;
<<<<<<< remotes/origin/Seth_Dev
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
=======
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
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
	public void addPhrase(Phrase p){
        phraseList.add(p);
>>>>>>> local
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
