package dragon.tamu.playphrase;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.Model.ParentWrapper;

import java.util.Collections;
import java.util.List;

public class RecyclerListAdapter extends ExpandableRecyclerAdapter<CategoryViewHolder, PhraseViewHolder> implements ItemTouchHelperAdapter{

    LayoutInflater mInflater;
    List<ParentListItem> mList;
    OnStartDragListener mOnStartDragListener;
    Context mContext;

    public RecyclerListAdapter(Context context, List<ParentListItem> parentItemList, OnStartDragListener listener) {
        super(parentItemList);

        mContext = context;

        mInflater = LayoutInflater.from(context);

        mList = parentItemList;

        mOnStartDragListener = listener;
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
    public void onBindParentViewHolder(final CategoryViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
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
        if (parentViewHolder.getCategory().getCategoryTitle().equalsIgnoreCase("uncategorized"))
        {
            parentViewHolder.itemView.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    int action = event.getAction();
                    switch (action)
                    {
                        case MotionEvent.ACTION_MOVE:
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                    }
                    return false;
                }
            });
        }

    }

    @Override
    public void onBindChildViewHolder(final PhraseViewHolder phraseViewHolder, int i, Object o)
    {

        Phrase phrase = (Phrase) o;
        phraseViewHolder.setPhrase((Phrase) o);
        phraseViewHolder.mPhraseText.setText(phrase.getPhraseText());

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
    public boolean onItemSwiped(RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof CategoryViewHolder)
        {
            int parentIndex = mList.indexOf(((CategoryViewHolder) viewHolder).getCategory());
            mList.remove(parentIndex);
            notifyParentItemRemoved(parentIndex);
            ((EditActivity) mContext).saveList();
            return true;
        }
        else if(viewHolder instanceof PhraseViewHolder)
        {
            //TODO give a warning before deleting.
            int fromPosition = viewHolder.getAdapterPosition();
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
            list.remove(fromPosition - 1 - parentIndex);
            notifyChildItemRemoved(parentListIndex, fromPosition - 1 - parentIndex);
            ((EditActivity) mContext).saveList();
            return true;
        }
        return false;
    }

}
