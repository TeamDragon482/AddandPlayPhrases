package dragon.tamu.playphrase;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;

public class Category implements ParentListItem{


    public List<Object> phraseList;
    public String name;

    public Category(String name) {
        this.name = name;
        phraseList = new ArrayList<>();

    }
    public Category(List<Object> phraseList, String title)
    {
        this.phraseList = phraseList;
        name = title;
    }

    public void setChildItemList(List<Object> list)
    {
        phraseList = list;
    }

    public String getCategoryTitle() {
        return name;
    }

    public void setCategoryTitle(String mCategoryTitle) {
        this.name = mCategoryTitle;
    }

    @Override
    public List<?> getChildItemList() {
        return phraseList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
	public void addPhrase(Phrase p){
        phraseList.add(p);

    }
}
