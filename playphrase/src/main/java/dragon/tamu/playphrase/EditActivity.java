package dragon.tamu.playphrase;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity implements OnStartDragListener {

    private ItemTouchHelper touchHelper;

    private FloatingActionButton fab, addPhraseButton, addCategoryButton;
    private boolean isFabOpen;
    private Animation rotate_forward, rotate_backward, fab_open, fab_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        RecyclerView listView = (RecyclerView)findViewById(R.id.edit_list_view);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setHasFixedSize(true);
        listView.setItemAnimator(new DefaultItemAnimator());

        //Code for floating action buttons
        isFabOpen = false;
        fab = (FloatingActionButton)findViewById(R.id.fab);
        addPhraseButton = (FloatingActionButton)findViewById(R.id.fab1);
        addCategoryButton = (FloatingActionButton)findViewById(R.id.fab2);
        //Animations for fab
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });


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
    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            addPhraseButton.startAnimation(fab_close);
            addCategoryButton.startAnimation(fab_close);
            addPhraseButton.setClickable(false);
            addCategoryButton.setClickable(false);
            isFabOpen = false;


        } else {
            fab.startAnimation(rotate_forward);
            addPhraseButton.startAnimation(fab_open);
            addCategoryButton.startAnimation(fab_open);
            addPhraseButton.setClickable(true);
            addCategoryButton.setClickable(true);
            isFabOpen = true;
        }
    }
}
