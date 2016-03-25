package dragon.tamu.playphrase;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

public class CategoryViewHolder extends ParentViewHolder
{
    public Category mCategory;
    public TextView mCategoryTitle;
    public ImageView handle;

    public CategoryViewHolder(View itemView)
    {
        super(itemView);

        mCategoryTitle = (TextView) itemView.findViewById(R.id.category_view);
        handle = (ImageView) itemView.findViewById(R.id.handle);
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
