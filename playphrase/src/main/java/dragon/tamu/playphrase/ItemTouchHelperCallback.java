package dragon.tamu.playphrase;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback
{

    private final ItemTouchHelperAdapter mAdapter;

    public ItemTouchHelperCallback(ItemTouchHelperAdapter adapter)
    {
        mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
    {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeflags = ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeflags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        mAdapter.onItemMoved(viewHolder, target);
        return true;
    }



    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        if(viewHolder instanceof CategoryViewHolder) {
            if (!((CategoryViewHolder) viewHolder).getCategory().name.equals("Uncategorized")) {
                if (direction == ItemTouchHelper.RIGHT) {
                    mAdapter.onItemSwiped(viewHolder);
                }
            }
        }
        else if(viewHolder instanceof PhraseViewHolder) {
            if (direction == ItemTouchHelper.RIGHT) {
                mAdapter.onItemSwiped(viewHolder);
            }
        }

    }


    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }


}
