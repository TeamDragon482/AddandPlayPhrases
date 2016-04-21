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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity implements OnStartDragListener
{


    //Members for fragments
    public Fragment addCategoryFrag;
    public Fragment recordingFragment;
    List<ParentListItem> mCategoryList; //List of categories

    RecyclerView listView;
    FileAccessor fileSystem;
    private RecyclerListAdapter mAdapter;
    private ItemTouchHelper touchHelper;
    //Add Phrase/Category members
    private FloatingActionButton fab, addPhraseButton, addCategoryButton;

    private TextView addCat, addPhrase;
    private View maskView;
    private boolean isFabOpen;
    private Animation rotate_forward, rotate_backward, fab_open, fab_close, slide_in, slide_out;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getSupportActionBar().setTitle("Edit Phrases");

        listView = (RecyclerView) findViewById(R.id.edit_list_view);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setHasFixedSize(true);
        listView.setItemAnimator(new DefaultItemAnimator());


        //Instantiating the fragment
        addCategoryFrag = new AddCategoryFragment();
        recordingFragment = new RecordingFragment();

        //Code for floating action buttons
        isFabOpen = false;
        fab = (FloatingActionButton) findViewById(R.id.fab);
        addPhraseButton = (FloatingActionButton) findViewById(R.id.fab1);
        addCategoryButton = (FloatingActionButton) findViewById(R.id.fab2);
        //Code for action button labels
        addPhrase = (TextView) findViewById(R.id.fab1_tView);
        addCat = (TextView) findViewById(R.id.fab2_tView);
        //Dark background
        maskView = findViewById(R.id.dark_opaque_background);
        //Animations for fab
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_appear);
        slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_disappear);

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                animateFAB();
            }
        });

        addPhraseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startFragmentFromButton(v, recordingFragment);
            }
        });
        addCategoryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startFragmentFromButton(v, addCategoryFrag);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_screen_menu, menu);

        menu.getItem(0).getActionView().findViewById(R.id.edit_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed()
    {
        if (getFragmentManager().getBackStackEntryCount() > 0)
        {
            maskView.setVisibility(View.INVISIBLE);
            getFragmentManager().popBackStack();
            fab.show();
        }
        else
            super.onBackPressed();
    }

    //temporary generator for demonstration purposes
    private List<ParentListItem> generateList()
    {

        mCategoryList = new ArrayList<>();
        for (Category cat : fileSystem.getLocalInformationList()) {
            List<Object> phraseList = cat.phraseList;
            ArrayList<String> phraseNames = new ArrayList<>();
            for (int i = 0; i < phraseList.size(); i++) {
                phraseNames.add(((Phrase) phraseList.get(i)).name);
            }
            mCategoryList.add(new Category(phraseList, cat.name));
        }

        return mCategoryList;
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    @Override
    protected void onStop()

    {
        super.onStop();
        Log.d("Edit Activity", "OnStop");
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        saveList();
        Log.d("Edit Activity", "OnPause");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadList();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder)
    {
        touchHelper.startDrag(viewHolder);
    }

    public void animateFAB()
    {

        if (isFabOpen)
        {
            fab.startAnimation(rotate_backward);
            addPhraseButton.startAnimation(fab_close);
            addCategoryButton.startAnimation(fab_close);
            addCat.startAnimation(slide_out);
            addPhrase.startAnimation(slide_out);
            addPhraseButton.setClickable(false);
            addCategoryButton.setClickable(false);
            isFabOpen = false;
            maskView.setVisibility(View.INVISIBLE);
        }
        else
        {
            fab.startAnimation(rotate_forward);
            addPhraseButton.startAnimation(fab_open);
            addCategoryButton.startAnimation(fab_open);
            addCat.startAnimation(slide_in);
            addPhrase.startAnimation(slide_in);
            addPhraseButton.setClickable(true);
            addCategoryButton.setClickable(true);
            isFabOpen = true;
            maskView.setVisibility(View.VISIBLE);
        }
    }

    private void startFragmentFromButton(View view, Fragment fragment)
    {
        saveList();
        mAdapter.collapseAllParents();
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
        maskView.setVisibility(View.VISIBLE);
        getFragmentManager().beginTransaction().add(R.id.edit_coord_layout, fragment, "phrase_add_frag").addToBackStack(null).commit();
    }

    public void loadList()
    {
        fileSystem = new FileAccessor(EditActivity.this.getBaseContext());
        mAdapter = new RecyclerListAdapter(this, generateList(), this);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(listView);

        listView.setAdapter(mAdapter);

    }

    public void saveList() {
        ArrayList<Category> temp = new ArrayList<>();
        for (int i = 0; i < mCategoryList.size(); i++) {
            temp.add((Category) mCategoryList.get(i));
        }
        fileSystem.saveInfoToFile(temp);
    }

    public void addCategory(String catName) {
        fileSystem.addCategory(catName);
        mCategoryList.add(0, new Category(catName));
        mAdapter.notifyParentItemInserted(0);

    }

    public void renameCategory(String oldCatName, String newCatName) {
        Category cat = null;
        for (int i = 0; i < mCategoryList.size(); i++) {
            if (((Category) mCategoryList.get(i)).getCategoryTitle().equals(oldCatName))
                cat = (Category) mCategoryList.get(i);
            break;
        }
        cat.setCategoryTitle(newCatName);
        //edit mCategoryList to correctky display the new category name
    }

    public int containsCategoryName(String categoryName) {
        for (int i = 0; i < mCategoryList.size(); i++) {
            if (((Category) mCategoryList.get(i)).getCategoryTitle().equals(categoryName))
                return i;
        }
        return -1;
    }

    public void addPhrase(String phraseText, String catName, String langName, String filePath) {
        int catIndex;
        if ((catIndex = containsCategoryName(catName)) == -1) {
            addCategory(catName);
            catIndex = 0;
        }
        boolean phraseExists = false;
        for (int i = 0; i < mCategoryList.size(); i++) {
            Category c = (Category) mCategoryList.get(i);
            for (int j = 0; j < c.phraseList.size(); j++) {
                Phrase p = (Phrase) c.phraseList.get(j);
                if (p.getPhraseText().equals(phraseText)) {
                    phraseExists = true;
                    break;
                }
            }
        }
        if (!phraseExists) {
            fileSystem.addPhrase(phraseText, langName, filePath, catName);
            mAdapter.notifyChildItemInserted(catIndex, 0);
        }
        else
        {
            fileSystem.addPhrase(phraseText, langName, filePath, catName);
        }


    }


}
