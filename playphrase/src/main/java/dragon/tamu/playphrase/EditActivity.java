package dragon.tamu.playphrase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity implements OnStartDragListener {

    private ItemTouchHelper touchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        RecyclerView listView = (RecyclerView)findViewById(R.id.edit_list_view);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setHasFixedSize(true);
        listView.setItemAnimator(new DefaultItemAnimator());


        RecyclerListAdapter adapter = new RecyclerListAdapter(this, generateList(), this);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(listView);

        listView.setAdapter(adapter);
    }

    //temporary generator for demonstration purposes
    private List<ParentListItem> generateList()
    {
        ArrayList<ParentListItem> categoryList = new ArrayList<>();

        ArrayList<Object> shore = new ArrayList<>();
        shore.add(new Phrase("Please sit your butt down"));
        shore.add(new Phrase("Slow down your approach speed"));
        shore.add(new Phrase("If you start to sink, push off the person next to you"));

        ArrayList<Object> spotted = new ArrayList<>();
        spotted.add(new Phrase("Follow the people who know what they're doing"));
        spotted.add(new Phrase("Look for the guiding light"));
        spotted.add(new Phrase("Beware of alligators"));

        ArrayList<Object> panic = new ArrayList<>();
        panic.add(new Phrase("This is no time to panic"));
        panic.add(new Phrase("Everything will be fine"));
        panic.add(new Phrase("Seriously though, there are alligators"));
        panic.add(new Phrase("Keith smells"));

        categoryList.add(new Category(shore, "Approaching Shore"));
        categoryList.add(new Category(spotted, "Recently Spotted"));
        categoryList.add(new Category(panic, "Displaying Panic"));

        return categoryList;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder)
    {
        touchHelper.startDrag(viewHolder);
    }


    /*private void prepareListData()
    {
        mCategoryList = new ArrayList<>();
        mPhraseList = new HashMap<>();

        //Adding categories here

        ArrayList<String> shore = new ArrayList<>();
        shore.add("Please sit your butt down");
        shore.add("Slow down your approach speed");
        shore.add("If you start to sink, push off the person next to you");

        ArrayList<String> spotted = new ArrayList<>();
        spotted.add("Follow the people who know what they're doing");
        spotted.add("Look for the guiding light");
        spotted.add("Beware of alligators");

        ArrayList<String> panic = new ArrayList<>();
        panic.add("This is no time to panic");
        panic.add("Everything will be fine");
        panic.add("Seriously though, there are alligators");
        panic.add("Keith smells");

        mPhraseList.put(mCategoryList.get(0), shore);
        mPhraseList.put(mCategoryList.get(1), spotted);
        mPhraseList.put(mCategoryList.get(2), panic);

    }*/
}
