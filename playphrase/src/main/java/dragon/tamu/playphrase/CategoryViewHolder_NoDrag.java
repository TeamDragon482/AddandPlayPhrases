package dragon.tamu.playphrase;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

public class CategoryViewHolder_NoDrag extends ParentViewHolder
{
    public Category mCategory;
    public TextView mCategoryTitle;

    public CategoryViewHolder_NoDrag(View itemView)
    {
        super(itemView);

        mCategoryTitle = (TextView) itemView.findViewById(R.id.category_view);
    }
    public void setCategory(Category category)
    {
        mCategory = category;
    }
    public int itemCount()
    {
        return mCategory.getChildItemList().size();
    }

    public Category getCategory()
    {
        return mCategory;
    }
}
