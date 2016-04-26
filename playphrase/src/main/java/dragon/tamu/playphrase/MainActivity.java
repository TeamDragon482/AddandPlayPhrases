package dragon.tamu.playphrase;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PhraseViewHolder_NoDrag.OnItemClickListener
{

    //Main FileAccessor for Application
    FileAccessor fileSystem;

    //Variables for Drawer
    ListView mDrawerList;
    RelativeLayout mDrawerPane;

    //For Language Select Pane
    ArrayList<String> currentlySelectedLang;
    Button deleteSelectedButton;

    //Variables for ListView
    RecyclerListAdapter_NoDrag mListAdapter;
    RecyclerView mListView;
    List<ParentListItem> mCategoryList; //List of categories
    List<ParentListItem> mFullList; //List of categories

    ArrayAdapter<String> adapter;
    HashMap<String, String> mLanguages;
    ArrayList<String> displayLanguages;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ActionBar mActionBar;
    private ArrayList<ParentListItem> searchList;

    //For playback
    private PlayManager playManager;
    private Animation slideUp, slideDown;
    private ImageButton playButton, stopButton, pauseButton, repeatButton;
    private RelativeLayout playBackLayout;
    private TextView playBackText;
    private boolean playbackVisible;
    private Runnable hidePlayback = new Runnable() {
        @Override
        public void run() {
            playBackLayout.startAnimation(slideDown);
            playbackVisible = false;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //fileSystem = new FileAccessor(MainActivity.this.getBaseContext());

        handleIntent(getIntent());

        //Set up Selected Languages
        currentlySelectedLang = new ArrayList<>();

        //Setting up category expandable list view
        //get the list view
        mListView = (RecyclerView) findViewById(R.id.expandableListView);

        //Set the adapter
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setHasFixedSize(true);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.requestFocus();


        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        //Sets up the menu button to toggle between the language bar and the main screen
        mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setTitle(R.string.drawer_close_title);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View v)
            {
                super.onDrawerClosed(v);
                mActionBar.setTitle(R.string.drawer_close_title);
                invalidateOptionsMenu();

                if (currentlySelectedLang.isEmpty())
                {
                    prepareListData();
                    Log.d("MainActivityDrawerClose", "Non-Sorted List");
                }
                else
                {
                    prepareSelectedListData();
                    Log.d("MainActivityDrawerClose", "Sorted List");
                }
            }

            @Override
            public void onDrawerOpened(View v)
            {
                super.onDrawerOpened(v);
                mActionBar.setTitle(R.string.drawer_open_title);
                invalidateOptionsMenu();
                if (currentlySelectedLang.size() == 0)
                    grayDeleteButton(true);
                //Re-populate Language Pane when switching back from Other Activities
                for (int i = 0; i < currentlySelectedLang.size(); i++)
                {
                    mDrawerList.setItemChecked(displayLanguages.indexOf(currentlySelectedLang.get(i)), true);
                }
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_language_white_24dp);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        //SearchView initialization and settings
        final SearchView searchView = (SearchView) findViewById(R.id.search_view);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mListView.getWindowToken(), 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                if (TextUtils.isEmpty(newText))
                    loadList();
                else
                {
                    mListAdapter.collapseAllParents();
                    upDateWithQuery(newText);
                }
                return true;
            }
        });




        // Populate the Navigation Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            //When a language is Selected in Language Pane
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //If the language isn't in the current list add it
                if (!currentlySelectedLang.contains(displayLanguages.get(position)))
                {
                    currentlySelectedLang.add(displayLanguages.get(position));
                    grayDeleteButton(false);
                    prepareSelectedListData();
                }
                //If it is it means its a deselecting so we remove it
                else
                {
                    currentlySelectedLang.remove(displayLanguages.get(position));
                    if (currentlySelectedLang.size() == 0) {
                        loadList();
                        grayDeleteButton(true);
                    }
                }
            }
        });

        //Logic for deleting a language
        deleteSelectedButton = (Button) findViewById(R.id.deleted_selected_lang_button);
        deleteSelectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this).setTitle("Delete Language").setMessage("Deleting this language will delete all phrases associated with it. Would you like to continue anyway?")
                        .setPositiveButton(R.string.delete_language, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i = displayLanguages.size() - 1; i >= 0; i--)
                                    if (mDrawerList.isItemChecked(i)) {
                                        String s = displayLanguages.get(i);
                                        removeLangauge(s.substring(0, s.indexOf('[')), s.substring(s.indexOf('[') + 1, s.indexOf(']')));
                                        displayLanguages.remove(i);
                                        if (currentlySelectedLang.contains(s)) {
                                            currentlySelectedLang.remove(s);
                                            if (currentlySelectedLang.size() == 0)
                                                loadList();
                                        }
                                    }
                                adapter.notifyDataSetChanged();
                                mDrawerLayout.closeDrawer(Gravity.LEFT);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel_delete_language, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();

            }
        });

        if (currentlySelectedLang.size() == 0)
            grayDeleteButton(true);


        //Setup for anything having to do with playback
        playManager = new PlayManager();
        playManager.setOnPausePlayClickListener(new OnPausePlayClickListener() {
            @Override
            public void OnPausePlayClick() {
                togglePausePlay();
            }
        });
        playManager.setOnStopPlayClickListener(new OnStopPlayClickListener() {
            @Override
            public void onStopPlayClick() {
                playBackLayout.removeCallbacks(hidePlayback);
                playBackLayout.postDelayed(hidePlayback, 5000);
            }
        });
        playBackLayout = (RelativeLayout) findViewById(R.id.playback_layout);
        playButton = (ImageButton) findViewById(R.id.play_button);
        pauseButton = (ImageButton) findViewById(R.id.pause_button);
        repeatButton = (ImageButton) findViewById(R.id.repeat_button);
        stopButton = (ImageButton) findViewById(R.id.stop_button);
        playBackText = (TextView) findViewById(R.id.currently_playing_phrase);

        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slideUp.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mListView.getLayoutParams();
                params.addRule(RelativeLayout.ABOVE, R.id.playback_layout);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                mListView.setLayoutParams(params);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        slideDown.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mListView.getLayoutParams();
                params.addRule(RelativeLayout.ABOVE, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                mListView.setLayoutParams(params);
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });

        playbackVisible = false;

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playManager.resumePlayer();
                playBackLayout.removeCallbacks(hidePlayback);
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playManager.pausePlayer();
            }
        });
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playManager.toggleRepeat())
                    repeatButton.setImageResource(R.drawable.ic_repeat_green_700_48dp);
                else
                    repeatButton.setImageResource(R.drawable.ic_repeat_black_48dp);
                playBackLayout.removeCallbacks(hidePlayback);
                if (!playManager.isPlaying())
                    playBackLayout.postDelayed(hidePlayback, 5000);
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playManager.stopPhrase();
            }
        });

    }

    public void grayDeleteButton(boolean b) {
        if (b) {
            deleteSelectedButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            deleteSelectedButton.setEnabled(false);
        } else {
            deleteSelectedButton.getBackground().setColorFilter(null);
            deleteSelectedButton.setEnabled(true);
        }

    }
    private void upDateWithQuery(String query)
    {
        searchList = new ArrayList<>();
        for (ParentListItem category : mCategoryList)
        {
            if (((Category) category).getCategoryTitle().toLowerCase().contains(query.toLowerCase()))
                searchList.add(category);
            else
            {
                Category newCat = null;
                ArrayList<Object> phraseList = (ArrayList<Object>) category.getChildItemList();
                for (int i = 0; i < phraseList.size(); i++)
                {
                    Phrase myPhrase = (Phrase) phraseList.get(i);
                    if (myPhrase.getPhraseText().toLowerCase().contains(query.toLowerCase()))
                    {

                        if (newCat == null)
                        {
                            newCat = new Category(((Category) category).getCategoryTitle());
                        }
                        newCat.addPhrase(myPhrase);

                    }

                }
                if (newCat != null)
                {
                    searchList.add(newCat);
                }
            }
        }
        mListAdapter = new RecyclerListAdapter_NoDrag(this, searchList, fileSystem, getLangNamesFromConcat(currentlySelectedLang));
        mListAdapter.setOnItemClickListener(this);
        mListView.setAdapter(mListAdapter);
        mListAdapter.expandAllParents();
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent)
    {
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            String query = intent.getStringExtra(SearchManager.QUERY);
            upDateWithQuery(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("Main Activity", "OnResume");
        if (searchList == null)
        {
            loadList();
        }
        if (currentlySelectedLang.size() == 0)
            grayDeleteButton(true);
        mListView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mListView.getWindowToken(), 0);
    }

    //Sets up phrases and Categories
    private void prepareListData() {
        mCategoryList = new ArrayList<>();
        mFullList = new ArrayList<>();
        for (Category cat : fileSystem.getLocalInformationList())
        {
            List<Object> phraseList = cat.phraseList;
            mCategoryList.add(new Category(phraseList, cat.name));
        }
        mFullList.addAll(mCategoryList);

    }

    private void prepareSelectedListData()
    {
        mCategoryList = new ArrayList<>();
        for (Category cat : fileSystem.getLocalInformationList())
        {
            List<Object> phraseList = cat.phraseList;
            List<Object> phraseListFinal = new ArrayList<>();
            for (int i = 0; i < phraseList.size(); i++)
            {
                for (int j = 0; j < currentlySelectedLang.size(); j++)
                {
                    //If the current phrase has the language in its language map add it to the Final List. Only need one language
                    if (((Phrase) phraseList.get(i)).phraseLanguages.containsKey(currentlySelectedLang.get(j).substring(0, currentlySelectedLang.get(j).indexOf('[')))) {
                        phraseListFinal.add(phraseList.get(i));
                        break;
                    }
                }
            }
            if (phraseListFinal.size() > 0)
                mCategoryList.add(new Category(phraseListFinal, cat.name));
        }
        mListAdapter = new RecyclerListAdapter_NoDrag(this, mCategoryList, fileSystem, getLangNamesFromConcat(currentlySelectedLang));
        mListAdapter.setOnItemClickListener(this);
        mListView.setAdapter(mListAdapter);

    }

    private void prepareLanguageListData()
    {
        displayLanguages = new ArrayList<>();
        mLanguages = (HashMap<String, String>) fileSystem.getLangList();
        for (String s : mLanguages.keySet())
        {
            String abbrev = mLanguages.get(s);
            displayLanguages.add(s + "[" + abbrev + "]");
        }

    }

    @Override
    protected void onPostCreate(Bundle b) {
        super.onPostCreate(b);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mDrawerToggle.onOptionsItemSelected(item);
        switch (item.getItemId())
        {
            case R.id.edit_menu:
                Intent edit_intent = new Intent(this, EditActivity.class);
                //This is where any data goes that will be stuffed into the intent launching the new activity
                startActivity(edit_intent);
                return true;
            case R.id.home:
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadList()
    {
        fileSystem = new FileAccessor(MainActivity.this.getBaseContext());
        if (currentlySelectedLang.isEmpty()) {
        prepareListData();
        } else {
            prepareSelectedListData();
        }
        prepareLanguageListData();
        mListAdapter = new RecyclerListAdapter_NoDrag(this, mCategoryList, fileSystem, getLangNamesFromConcat(currentlySelectedLang));
        mListAdapter.setOnItemClickListener(this);
        mListView.setAdapter(mListAdapter);
        adapter = new ArrayAdapter<>(this, R.layout.drawer_item, displayLanguages);
        mDrawerList.setAdapter(adapter);
    }

    //Removes the language and any recordings involving the language
    //Also deletes a phrase if that was the last language it contained
    private void removeLangauge(String langName, String langAbbrv) {
        fileSystem.removeLanguage(langName);
        for (int i = 0; i < mFullList.size(); i++) {
            Category cat = (Category) mFullList.get(i);
            for (int j = 0; j < cat.phraseList.size(); j++) {
                Phrase p = (Phrase) cat.phraseList.get(j);
                if (p.phraseLanguages.containsKey(langName)) {
                    p.phraseLanguages.remove(langName);
                    if (p.phraseLanguages.size() == 0)
                        fileSystem.removePhrase(p.getPhraseText(), cat.getCategoryTitle());
                }
            }
        }

    }

    public ArrayList<String> getselectAbrv() {
        ArrayList<String> selectedAbrv = new ArrayList<>();
        for (int i = 0; i < currentlySelectedLang.size(); i++) {
            selectedAbrv.add(fileSystem.languageList.get(currentlySelectedLang.get(i)));
        }
        return selectedAbrv;
    }

    public void togglePausePlay() {
        if (playButton.getVisibility() == View.VISIBLE) {
            playButton.setVisibility(View.INVISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
        } else {
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.INVISIBLE);
        }
    }

    private ArrayList<String> getLangNamesFromConcat(List<String> langList) {
        ArrayList<String> langaugeList = new ArrayList<>();
        for (String s : langList) {
            langaugeList.add(s.substring(0, s.indexOf('[')));
        }
        return langaugeList;
    }
    @Override
    public void onItemCLick(View v, Phrase p) {
        playBackLayout.removeCallbacks(hidePlayback);
        List<ParentListItem> list = mListAdapter.mList;
        ArrayList<String> langaugeList = getLangNamesFromConcat(currentlySelectedLang);
            playBackText.setText(p.getPhraseText());
            playManager.playPhrase(p, langaugeList);
            if (!playbackVisible) {
                playBackLayout.startAnimation(slideUp);
                playbackVisible = true;
            }
    }

    interface OnPausePlayClickListener {
        void OnPausePlayClick();
    }

    interface OnStopPlayClickListener {
        void onStopPlayClick();
    }
}