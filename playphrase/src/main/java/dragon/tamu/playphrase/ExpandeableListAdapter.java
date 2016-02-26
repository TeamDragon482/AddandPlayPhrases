package dragon.tamu.playphrase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Seth on 2/25/2016.
 */

public class ExpandeableListAdapter extends BaseExpandableListAdapter
{
    private Context mContext;
    private ArrayList<String> mCategoryList; //List of categories
    private HashMap<String, ArrayList<String>> mPhraseList;

    public ExpandeableListAdapter(Context context, ArrayList<String> categoryList, HashMap<String, ArrayList<String>> phraseList)
    {
        mContext = context;
        mCategoryList = categoryList;
        mPhraseList = phraseList;
    }
    @Override
    public int getGroupCount() {
        return mCategoryList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mPhraseList.get(mCategoryList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mCategoryList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mPhraseList.get(mCategoryList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String categoryText = (String)getGroup(groupPosition);
        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_group_item, null);
        }
        TextView categoryView = (TextView) convertView.findViewById(R.id.category_view);
        categoryView.setText(categoryText);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String phraseText = (String) getChild(groupPosition, childPosition);
        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_inner_item, null);
        }
        TextView phraseView = (TextView) convertView.findViewById(R.id.phrase_view);
        phraseView.setText(phraseText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
