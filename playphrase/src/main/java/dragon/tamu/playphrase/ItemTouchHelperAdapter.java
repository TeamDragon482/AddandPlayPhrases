package dragon.tamu.playphrase;

import android.support.v7.widget.RecyclerView;

public interface ItemTouchHelperAdapter {
    boolean onItemMoved(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target);
}
