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

import java.util.Collections;
import java.util.List;

public class RecyclerListAdapter extends ExpandableRecyclerAdapter<CategoryViewHolder, PhraseViewHolder> implements ItemTouchHelperAdapter{

    LayoutInflater mInflater;
    List<ParentListItem> mList;
    OnStartDragListener mOnStartDragListener;

    public RecyclerListAdapter(Context context, List<ParentListItem> parentItemList, OnStartDragListener listener) {
        super(parentItemList);

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
        }


        return false;
    }




    /*private static final String[] STRINGS = new String[]{
            "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"
    };

    private final List<String> mItems = new ArrayList<>();

    public RecyclerListAdapter() {
        mItems.addAll(Arrays.asList(STRINGS));
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_inner_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.textView.setText(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if(fromPosition < toPosition)
        {
            for(int i = fromPosition; i < toPosition; i++)
            {
                Collections.swap(mItems, i, i+1);
            }

        }
        else
        {
            for( int i = fromPosition; i > toPosition; i--)
            {
                Collections.swap(mItems, i, i-1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }*/
}
