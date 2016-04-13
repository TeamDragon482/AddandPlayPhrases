package dragon.tamu.playphrase;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Main FileAccessor for Application
    FileAccessor fileSystem;

    //Variables for Drawer
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    ArrayList<String> currentlySelectedLang;

    //Variables for ListView
    RecyclerListAdapter_NoDrag mListAdapter;
    RecyclerView mListView;
    List<ParentListItem> mCategoryList; //List of categories

    ArrayAdapter<String> adapter;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ActionBar mActionBar;

    ArrayList<String> mLanguages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //fileSystem = new FileAccessor(MainActivity.this.getBaseContext());
        currentlySelectedLang = new ArrayList<>();
        //Setting up category expandable list view

        //get the list view
        mListView = (RecyclerView)findViewById(R.id.expandableListView);


//        mListAdapter = new RecyclerListAdapter_NoDrag(this, mCategoryList);

        //Set the adapter
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setHasFixedSize(true);
        mListView.setItemAnimator(new DefaultItemAnimator());
//        mListView.setAdapter(mListAdapter);
        mListView.requestFocus();



        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);


        //Sets up the menu button to toggle between the language bar and the main screen
        mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setTitle(R.string.drawer_close_title);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close)
        {
            @Override
            public void onDrawerClosed(View v)
            {
                //TODO save which languages are selected here
                super.onDrawerClosed(v);
                mActionBar.setTitle(R.string.drawer_close_title);
                invalidateOptionsMenu();
            }
            @Override
            public void onDrawerOpened(View v)
            {
                super.onDrawerOpened(v);
                mActionBar.setTitle(R.string.drawer_open_title);
                invalidateOptionsMenu();
            }
        };


        mDrawerToggle.syncState();

        //SearchView initialization and settings
        SearchView searchView = (SearchView) findViewById(R.id.search_view);







        // Populate the Navigation Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
//        adapter = new ArrayAdapter<>(this, R.layout.drawer_item, mLanguages);
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//        mDrawerList.setAdapter(adapter);



        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //When a language is Selected in Language Pane
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //If the language isn't in the current list add it
                if(currentlySelectedLang.indexOf(mLanguages.get(position)) == -1){
                    currentlySelectedLang.add(mLanguages.get(position));
                }
                //If it is it means its a deselecting so we remove it
                else {
                    currentlySelectedLang.remove(mLanguages.get(position));
                }

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Main Activity", "OnResume");
        fileSystem = new FileAccessor(MainActivity.this.getBaseContext());
        // if(currentlySelectedLang.isEmpty()) {
        prepareListData();
        // } else{
        //     prepareSelectedListData();
        // }
        prepareLanguageListData();
        mListAdapter = new RecyclerListAdapter_NoDrag(this, mCategoryList);
        mListView.setAdapter(mListAdapter);
        adapter = new ArrayAdapter<>(this, R.layout.drawer_item, mLanguages);
        mDrawerList.setAdapter(adapter);
    }


    //Sets up phrases and Categories
    private void prepareListData()
    {
        mCategoryList = new ArrayList<>();
        for(Category cat :  fileSystem.getLocalInformationList()){
            List<Object> phraseList = cat.phraseList;
            ArrayList<String> phraseNames = new ArrayList<>();
            for(int i = 0; i < phraseList.size(); i++){
                phraseNames.add(((Phrase)phraseList.get(i)).name);
            }
            mCategoryList.add(new Category(phraseList, cat.name));
        }
    }

    private void prepareSelectedListData() {
        mCategoryList = new ArrayList<>();
        for (Category cat : fileSystem.getLocalInformationList()) {
            List<Object> phraseList = cat.phraseList;
            ArrayList<String> phraseNames = new ArrayList<>();
            for (int i = 0; i < phraseList.size(); i++) {
                phraseNames.add(((Phrase) phraseList.get(i)).name);
            }
            mCategoryList.add(new Category(phraseList, cat.name));
        }
    }

    private void prepareLanguageListData(){
        mLanguages = new ArrayList<>();
        for(String lang : fileSystem.extractLangNames()){
            mLanguages.add(lang);
        }
    }

    @Override
    protected void onPostCreate(Bundle b)
    {
        super.onPostCreate(b);
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        mDrawerToggle.onOptionsItemSelected(item);
        switch(item.getItemId())
        {
            case R.id.edit_menu:
                Intent edit_intent = new Intent(this, EditActivity.class);
                //This is where any data goes that will be stuffed into the intent launching the new activity


                startActivity(edit_intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}