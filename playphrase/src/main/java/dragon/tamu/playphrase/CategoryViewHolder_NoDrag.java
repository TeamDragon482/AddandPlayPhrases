package dragon.tamu.playphrase;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

public class CategoryViewHolder_NoDrag extends ParentViewHolder
{
    public Category mCategory;
    public TextView mCategoryTitle;
    public ImageView mArrow;

    public CategoryViewHolder_NoDrag(View itemView)
    {
        super(itemView);
        RelativeLayout mLayout = (RelativeLayout) itemView.findViewById(R.id.partition2);
        mCategoryTitle = (TextView) itemView.findViewById(R.id.category_view);
        mArrow = (ImageView) itemView.findViewById(R.id.parent_list_item_expand_arrow);
    }

    public int itemCount()
    {
        return mCategory.getChildItemList().size();
    }

    public Category getCategory()
    {
        return mCategory;
    }

    public void setCategory(Category category)
    {
        mCategory = category;
    }
}
