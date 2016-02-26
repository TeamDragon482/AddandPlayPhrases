package dragon.tamu.playphrase;

import android.animation.LayoutTransition;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    //Variables for Drawer
    ListView mDrawerList;
    RelativeLayout mDrawerPane;

    //Variables for ListView
    ExpandeableListAdapter mListAdapter;
    ExpandableListView mListView;
    ArrayList<String> mCategoryList; //List of categories
    HashMap<String, ArrayList<String>> mPhraseList;


    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

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

        mListAdapter = new ExpandeableListAdapter(this, mCategoryList, mPhraseList);

        //Set the adapter
        mListView.setAdapter(mListAdapter);

        //Adding languages to the pull out list.
        for (Language l : Language.values()) {
            mLanguages.add(l);
        }

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        //LanguageListAdapter adapter = new LanguageListAdapter(this, mLanguages);
        ArrayAdapter<Language> adapter = new ArrayAdapter<Language>(this, R.layout.drawer_item, mLanguages);
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mDrawerList.setAdapter(adapter);



        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this.getApplicationContext(), "Selected " + mLanguages.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        //Setup SearchView animation
        LinearLayout searchBar = (LinearLayout) searchView.findViewById(R.id.search_bar);
        LayoutTransition lt = new LayoutTransition();
        lt.enableTransitionType(LayoutTransition.CHANGING);
        searchBar.setLayoutTransition(lt);


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

    //Temporary just to populate the pullout menu until we have back-end
    enum Language {English, French, Arabic, German}


}