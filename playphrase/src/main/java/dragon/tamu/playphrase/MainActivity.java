package dragon.tamu.playphrase;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    //Variables for Drawer
    ListView mDrawerList;
    RelativeLayout mDrawerPane;

    //Variables for ListView
    ExpandeableCategoryListAdapter mListAdapter;
    ExpandableListView mListView;
    ArrayList<String> mCategoryList; //List of categories
    HashMap<String, ArrayList<String>> mPhraseList;


    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ActionBar mActionBar;

    ArrayList<Language> mLanguages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Setting up category expandable list view

        //get the listview
        mListView = (ExpandableListView)findViewById(R.id.expandableListView);

        //prepare the list data
        //This method is temporary and is just used to populate the expandable list for demonstration
        prepareListData();

        mListAdapter = new ExpandeableCategoryListAdapter(this, mCategoryList, mPhraseList);

        //Set the adapter
        mListView.setAdapter(mListAdapter);
        mListView.requestFocus();

        //TODO add onClickListener for the expandable list view

        //Adding languages to the pull out list.
        Collections.addAll(mLanguages, Language.values());


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

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        //SearchView initialization and settings
        SearchView searchView = (SearchView) findViewById(R.id.search_view);







        // Populate the Navigation Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        //LanguageListAdapter adapter = new LanguageListAdapter(this, mLanguages);
        ArrayAdapter<Language> adapter = new ArrayAdapter<>(this, R.layout.drawer_item, mLanguages);
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mDrawerList.setAdapter(adapter);



        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this.getApplicationContext(), "Selected " + mLanguages.get(position), Toast.LENGTH_SHORT).show();
               //TODO proper handling for what to do when a new language is selected goes here.
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        /*SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
*/

        //TODO need to setup handling for search bar inputs.


        return true;
    }


    private void prepareListData()
    {
        mCategoryList = new ArrayList<>();
        mPhraseList = new HashMap<>();

        //Adding categories here
        mCategoryList.add("Approaching Shore");
        mCategoryList.add("Recently Spotted");
        mCategoryList.add("Displaying Panic");

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
        return super.onOptionsItemSelected(item);
    }



    //Temporary just to populate the pullout menu until we have back-end
    enum Language {English, French, Arabic, German}


}