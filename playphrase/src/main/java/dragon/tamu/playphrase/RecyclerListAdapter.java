package dragon.tamu.playphrase;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.Model.ParentWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RecyclerListAdapter extends ExpandableRecyclerAdapter<CategoryViewHolder, PhraseViewHolder> implements ItemTouchHelperAdapter{

    public Fragment renameCategoryFrag;
    LayoutInflater mInflater;
    List<ParentListItem> mList;
    OnStartDragListener mOnStartDragListener;
    Context mContext;
    FileAccessor mFileSystem;
    EditActivity.RenameCategoryClickListener mListener;

    public RecyclerListAdapter(Context context, List<ParentListItem> parentItemList, OnStartDragListener listener, FileAccessor fileSystem) {
        super(parentItemList);

        mContext = context;

        mInflater = LayoutInflater.from(context);

        mList = parentItemList;

        mOnStartDragListener = listener;

        mFileSystem = fileSystem;
    }

    @Override
    public CategoryViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.expandable_group_item_drag, viewGroup, false);

        return new CategoryViewHolder(view);
    }

    @Override
    public PhraseViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.expandable_inner_item_drag, viewGroup, false);

        return new PhraseViewHolder(view);

    }

    @Override
    public void onBindParentViewHolder(final CategoryViewHolder parentViewHolder, final int position, final ParentListItem parentListItem)
    {
        Category category = (Category) parentListItem;
        parentViewHolder.setCategory((Category) parentListItem);
        parentViewHolder.mCategoryTitle.setText(category.getCategoryTitle());

        parentViewHolder.handle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    collapseAllParents();
                    mOnStartDragListener.onStartDrag(parentViewHolder);

                }
                return false;
            }
        });
        if (category.getCategoryTitle().equalsIgnoreCase("Uncategorized"))
        {
            parentViewHolder.rename.setVisibility(View.INVISIBLE);
//            parentViewHolder.arrow.setVisibility(View.VISIBLE);
        }
        if (category.getChildItemList().size() == 0)
            parentViewHolder.arrow.setVisibility(View.INVISIBLE);
        parentViewHolder.rename.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                renameCategoryFrag = new RenameCategoryFragment();
                Bundle args = new Bundle();
                int originalPos[] = new int[2];
                v.getLocationOnScreen(originalPos);
                DisplayMetrics dm = new DisplayMetrics();
                ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
                args.putInt("xCoor", originalPos[0]);
                args.putInt("yCoor", originalPos[1]);
                args.putString("cat", ((Category) parentListItem).getCategoryTitle());
                renameCategoryFrag.setArguments(args);
                ((Activity) mContext).getFragmentManager().beginTransaction().add(R.id.edit_coord_layout, renameCategoryFrag, "cat_rename_frag").addToBackStack(null).commit();
                if (mListener != null) {
                    mListener.onRenameCategoryClick();
                }
                return false;
            }
        });

    }

    @Override
    public void onBindChildViewHolder(final PhraseViewHolder phraseViewHolder, int i, Object o)
    {

        Phrase phrase = (Phrase) o;
        phraseViewHolder.setPhrase((Phrase) o);
        phraseViewHolder.mPhraseText.setText(phrase.getPhraseText());
        String abbrevText = "";
        Map<String, String> langToAbbrev = mFileSystem.getLangList();
        for (String s : phrase.phraseLanguages.keySet()) {
            if (langToAbbrev.containsKey(s)) {
                if (abbrevText.length() == 0) {
                    abbrevText += langToAbbrev.get(s);
                } else
                    abbrevText += ", " + langToAbbrev.get(s);
            }
        }
        phraseViewHolder.mAbbrevs.setText(abbrevText);
        phraseViewHolder.mDragIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mOnStartDragListener.onStartDrag(phraseViewHolder);
                }
                return false;
            }
        });


    }
    public void notifyChildItemAdopted(int fromParentIndex, int toParentIndex, int fromParentSize)
    {
        if(fromParentIndex < toParentIndex)
        {
            Object fromChild = mItemList.remove(fromParentIndex + fromParentSize);
            mItemList.add(fromParentIndex + fromParentSize + 1, fromChild);
            notifyItemMoved(fromParentIndex + fromParentSize, fromParentIndex + fromParentSize + 1);
        }
        if (fromParentIndex > toParentIndex)
        {
            Object fromChild = mItemList.remove(fromParentIndex + 1);
            mItemList.add(fromParentIndex, fromChild);
            notifyItemMoved(fromParentIndex + 1, fromParentIndex);
        }
    }


    @Override
    public boolean onItemMoved(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        Log.d("Drag", "FromPosition " + fromPosition);
        Log.d("Drag", "ToPosition " + toPosition);
        if(viewHolder instanceof CategoryViewHolder)
        {
            if(fromPosition < toPosition)
            {
                for(int i = fromPosition; i < toPosition; i++)
                {
                    Collections.swap(mList, i, i+1);
                }
            }
            else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mList, i, i - 1);
                }
            }
            notifyParentItemMoved(fromPosition, toPosition);
            return true;

        }
        if(target instanceof CategoryViewHolder)
        {
            if(!((CategoryViewHolder) target).isExpanded())
                expandParent(((CategoryViewHolder) target).getCategory());
            if(fromPosition > toPosition && toPosition > 0) {
                int index = mList.indexOf(((CategoryViewHolder) target).getCategory()) - 1;
                int listIndex = -1;
                int listItemCount = mItemList.size();
                for (int i = 0; i < listItemCount; i++) {
                    Object listItem = mItemList.get(i);
                    if (listItem instanceof ParentWrapper) {
                        if (((ParentWrapper) listItem).getParentListItem().equals(mList.get(index))) {
                            listIndex = i;
                        }
                    }
                }
                assert listIndex >= 0;
                if(!((ParentWrapper)mItemList.get(listIndex)).isExpanded()) {
                    expandParent(mList.get(index));

                    fromPosition += mList.get(index).getChildItemList().size();
                    toPosition += mList.get(index).getChildItemList().size();
                }
            }

        }
        if(viewHolder instanceof PhraseViewHolder)
        {
            int parentIndex = -1;
            int parentListIndex = 0;
            List<?> list = null;
            for(int i = 0; i < mList.size(); i++)
            {
                list = mList.get(i).getChildItemList();
                if(list.contains(((PhraseViewHolder) viewHolder).getPhrase()))
                {
                    int itemIndex = list.indexOf(((PhraseViewHolder) viewHolder).getPhrase());
                    parentIndex = fromPosition - 1 - itemIndex;
                    parentListIndex = i;
                    break;
                }
            }
            // This is to remove a warning in the code. If the list IS null, the program will crash here instead.
            assert list != null;
            if(toPosition <= parentIndex + list.size() && toPosition > parentIndex && parentIndex != -1) {
                if (fromPosition < toPosition) {
                    for (int i = fromPosition - parentIndex - 1; i < toPosition - parentIndex - 1; i++) {
                            Collections.swap(list, i, i + 1);
                    }
                }
                if (fromPosition > toPosition) {
                    for (int i = fromPosition - parentIndex - 1; i > toPosition - parentIndex - 1; i--) {
                            Collections.swap(list, i, i - 1);
                    }
                }
                notifyChildItemMoved(parentListIndex, fromPosition - 1 - parentIndex, toPosition - 1 - parentIndex);
                return true;
            }
            else if(target instanceof CategoryViewHolder && toPosition > 0)
            {
                int targetParentListIndex;
                int targetParentIndex = -1;
                int childToPosition = -1;
                Phrase p = ((PhraseViewHolder) viewHolder).getPhrase();
                if(fromPosition < toPosition)
                {
                    //Remove from current category
                    list.remove(fromPosition - 1 - parentIndex);

                    //and then add to new category
                    List<Object> tempList = (List<Object>)mList.get(parentListIndex + 1).getChildItemList();
                    tempList.add(0, p);
                    list = tempList;

                    targetParentIndex = toPosition;

                }
                if(fromPosition > toPosition)
                {
                    //Remove from current category
                    list.remove(fromPosition - 1 - parentIndex);

                    //and then add to new category
                    List<Object> tempList = (List<Object>)mList.get(parentListIndex - 1).getChildItemList();
                    tempList.add(p);
                    list = tempList;

                    targetParentListIndex = parentListIndex - 1;
                    targetParentIndex = toPosition - 1 - (mList.get(targetParentListIndex).getChildItemList().size() - 1);
                    childToPosition = list.size() - 1;
                }
                notifyChildItemAdopted(parentIndex, targetParentIndex, mList.get(parentListIndex).getChildItemList().size() + 1);
                /*notifyChildItemRemoved(parentListIndex, fromPosition - 1 - parentIndex);
                notifyChildItemInserted(targetParentListIndex, childToPosition);*/
                return true;

            }
            return false;
        }



        return false;
    }

    @Override
    public boolean onItemSwiped(final RecyclerView.ViewHolder viewHolder)
    {

        int adapterPosition = viewHolder.getAdapterPosition();
        if (viewHolder instanceof PhraseViewHolder)
        {
            int fromPosition = viewHolder.getAdapterPosition();
            int parentIndex = -1;
            int parentListIndex = 0;
            List<?> list;
            for (int i = 0; i < mList.size(); i++)
            {
                list = mList.get(i).getChildItemList();
                if (list.contains(((PhraseViewHolder) viewHolder).getPhrase()))
                {
                    int itemIndex = list.indexOf(((PhraseViewHolder) viewHolder).getPhrase());
                    parentListIndex = i;
                    break;
                }
            }
            adapterPosition = parentListIndex;
        }
        final int finalAdapterPosition = adapterPosition;
        Snackbar snackbar = Snackbar
                .make(viewHolder.itemView, "Are you sure you want to delete?", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        onUndo(viewHolder, finalAdapterPosition);

                    }
                });
        snackbar.show();

        return onDismiss(viewHolder);
    }

    public void onUndo(RecyclerView.ViewHolder viewHolder, int position)
    {
        if (viewHolder instanceof CategoryViewHolder)
        {
            Category c = ((CategoryViewHolder) viewHolder).getCategory();
            mList.add(position, c);
            notifyParentItemInserted(position);
            int unCatIndex = -1;
            for (ParentListItem p : mList)
            {

                if (((Category) p).getCategoryTitle().equalsIgnoreCase("Uncategorized"))
                {
                    unCatIndex = mList.indexOf(p);
                    break;
                }
            }
            List<Object> list = (List<Object>) mList.get(unCatIndex).getChildItemList();
            List<Object> list2 = (List<Object>) c.getChildItemList();
            for (int i = 0; i < list.size(); i++)
            {
                Object o = list.get(i);
                {
                    if (list2.contains(o))
                    {
                        int index = list.indexOf(o);
                        list.remove(index);
                        notifyChildItemRemoved(unCatIndex, index);
                    }
                }

            }
            notifyParentItemChanged(unCatIndex);
        }
        if (viewHolder instanceof PhraseViewHolder)
        {
            List<Object> list = (List<Object>) mList.get(position).getChildItemList();
            list.add(0, ((PhraseViewHolder) viewHolder).getPhrase());
            notifyChildItemInserted(position, 0);
            notifyParentItemChanged(position);
        }
    }

    public boolean onDismiss(RecyclerView.ViewHolder viewHolder)
    {
        if (viewHolder instanceof CategoryViewHolder)
        {
            int parentIndex = mList.indexOf(((CategoryViewHolder) viewHolder).getCategory());
            List<Object> list = new ArrayList<>(mList.get(parentIndex).getChildItemList());
            mList.remove(parentIndex);
            notifyParentItemRemoved(parentIndex);
            int unCatIndex = -1;
            for (ParentListItem p : mList) {

                if (((Category) p).getCategoryTitle().equalsIgnoreCase("Uncategorized")) {
                    unCatIndex = mList.indexOf(p);
                    break;
                }
            }
            if (unCatIndex >= 0) {
                List<Object> uncatList = (List<Object>) mList.get(unCatIndex).getChildItemList();
                int og_size = uncatList.size();
                uncatList.addAll(list);
                notifyChildItemRangeInserted(unCatIndex, og_size, list.size());
                notifyParentItemChanged(unCatIndex);
            }
            ((EditActivity) mContext).saveList(false);
            return true;
        }
        else if (viewHolder instanceof PhraseViewHolder)
        {
            int fromPosition = viewHolder.getAdapterPosition();
            int parentIndex = -1;
            int parentListIndex = 0;
            List<?> list = null;
            for (int i = 0; i < mList.size(); i++)
            {
                list = mList.get(i).getChildItemList();
                if (list.contains(((PhraseViewHolder) viewHolder).getPhrase()))
                {
                    int itemIndex = list.indexOf(((PhraseViewHolder) viewHolder).getPhrase());
                    parentIndex = fromPosition - 1 - itemIndex;
                    parentListIndex = i;
                    break;
                }
            }
            list.remove(fromPosition - 1 - parentIndex);
            notifyChildItemRemoved(parentListIndex, fromPosition - 1 - parentIndex);
            if (mList.get(parentListIndex).getChildItemList().size() == 0)
                notifyParentItemChanged(parentListIndex);
            ((EditActivity) mContext).saveList(false);
            return true;
        }
        return false;
    }

    public void setRenameCategoryClickListener(EditActivity.RenameCategoryClickListener listener)
    {
        mListener = listener;
    }

}
