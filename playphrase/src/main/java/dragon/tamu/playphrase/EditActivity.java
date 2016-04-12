package dragon.tamu.playphrase;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity implements OnStartDragListener {

    //Members for fragments
    public Fragment addPhraseFrag;
    private ItemTouchHelper touchHelper;
    //Add Phrase/Category members
    private FloatingActionButton fab, addPhraseButton, addCategoryButton;
    private TextView addCat, addPhrase;
    private boolean isFabOpen;
    private Animation rotate_forward, rotate_backward, fab_open, fab_close, slide_in, slide_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        RecyclerView listView = (RecyclerView)findViewById(R.id.edit_list_view);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setHasFixedSize(true);
        listView.setItemAnimator(new DefaultItemAnimator());


        //Instantiating the fragment
        addPhraseFrag = new AddPhraseFragment();

        //Code for floating action buttons
        isFabOpen = false;
        fab = (FloatingActionButton)findViewById(R.id.fab);
        addPhraseButton = (FloatingActionButton)findViewById(R.id.fab1);
        addCategoryButton = (FloatingActionButton)findViewById(R.id.fab2);
        //Code for action button labels
        addPhrase = (TextView) findViewById(R.id.fab1_tView);
        addCat = (TextView) findViewById(R.id.fab2_tView);
        //Animations for fab
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_appear);
        slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_disappear);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });

        addPhraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentFromButton(v, addPhraseFrag);
            }
        });
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentFromButton(v, null);
            }
        });


        RecyclerListAdapter adapter = new RecyclerListAdapter(this, generateList(), this);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(listView);

        listView.setAdapter(adapter);
    }

    //@Override
    //protected void onResume(Bundle savedInstanceState) {
        //super.onResume(savedInstanceState);

    //}

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            fab.show();
        } else
            super.onBackPressed();
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
            addCat.startAnimation(slide_out);
            addPhrase.startAnimation(slide_out);
            addPhraseButton.setClickable(false);
            addCategoryButton.setClickable(false);
            isFabOpen = false;


        } else {
            fab.startAnimation(rotate_forward);
            addPhraseButton.startAnimation(fab_open);
            addCategoryButton.startAnimation(fab_open);
            addCat.startAnimation(slide_in);
            addPhrase.startAnimation(slide_in);
            addPhraseButton.setClickable(true);
            addCategoryButton.setClickable(true);
            isFabOpen = true;
        }
    }

    private void startFragmentFromButton(View view, Fragment fragment) {
        Bundle args = new Bundle();
        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        args.putInt("xCoor", originalPos[0]);
        args.putInt("yCoor", originalPos[1]);
        fragment.setArguments(args);
        animateFAB();
        fab.hide();
        getFragmentManager().beginTransaction().add(R.id.edit_coord_layout, fragment, "phrase_add_frag").addToBackStack(null).commit();
       /* CoordinatorLayout root = (CoordinatorLayout) findViewById( R.id.edit_coord_layout );
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics( dm );

        int originalPos[] = new int[2];
        view.getLocationOnScreen( originalPos);

        int xDest = dm.widthPixels/2;
        xDest -= (view.getMeasuredWidth()/2);
        int yDest = dm.heightPixels/2 - (view.getMeasuredHeight()/2);

        TranslateAnimation anim = new TranslateAnimation( 0, xDest - originalPos[0] , 0, yDest - originalPos[1] );
        anim.setDuration(1000);
        anim.setFillAfter(true);
        view.startAnimation(anim);
*/
}
    public void addCategory()
    {
    }
}
