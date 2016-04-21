package dragon.tamu.playphrase;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

public class CategoryViewHolder extends ParentViewHolder
{
    public Category mCategory;
    public TextView mCategoryTitle;
    public ImageView handle;
    public ImageView rename;
    public ImageView arrow;

    public CategoryViewHolder(View itemView)
    {
        super(itemView);

        mCategoryTitle = (TextView) itemView.findViewById(R.id.category_view);
        handle = (ImageView) itemView.findViewById(R.id.handle);
        rename = (ImageView) itemView.findViewById(R.id.category_name_edit_pencil);
        arrow = (ImageView) itemView.findViewById(R.id.parent_list_item_expand_arrow);
        arrow.setVisibility(View.INVISIBLE);
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
