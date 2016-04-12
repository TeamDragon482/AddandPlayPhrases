package dragon.tamu.playphrase;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.Animator;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.ViewAnimationUtils;
import android.view.animation.LinearInterpolator;


/**
 * Created by marky on 3/29/2016.
 */
public class RecordingFragment extends Fragment {
    private Spinner phrase_spinner, category_spinner, language_spinner;
    private int phrase_spinner_pos, category_spinner_pos, language_spinner_pos;
    private ImageButton btnSubmit, btnPlay, btnStartRecording, btnStopRecording;
    private TextView textPlaceholder;
    private List<String> phrase_list = new ArrayList<String>();
    private List<String> category_list = new ArrayList<String>();
    private List<String> language_list = new ArrayList<String>();
    private EditText newPhraseText, newCategoryText, newLanguageText, newLanguageAbbr;
    private Button btnSavePhrase, btnSaveCategory, btnSaveLanguage;
    private Boolean phraseSaved, categorySaved, languageSaved;
    //ThingsAdapter adapter;
    FragmentActivity listener;

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }
    }


    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
        //Inflate the layout for this fragment
        View recordingFragmentView = inflater.inflate(R.layout.recording_fragment, container, false);

        //This bit of code is for a fancy expandable animation.
        recordingFragmentView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                int radius = (int) Math.hypot(right, bottom);
                radius *= 2;
                int xCoor = getArguments().getInt("xCoor");
                int yCoor = getArguments().getInt("yCoor");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Animator reveal = ViewAnimationUtils.createCircularReveal(v, xCoor, yCoor, 0, radius);
                    reveal.setInterpolator(new LinearInterpolator());
                    reveal.setDuration(1000);
                    reveal.start();
                }
            }
        });


        return recordingFragmentView;
    }


    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        phrase_spinner = (Spinner) getView().findViewById(R.id.phrase_spinner);
        category_spinner = (Spinner) getView().findViewById(R.id.category_spinner);
        language_spinner = (Spinner) getView().findViewById(R.id.language_spinner);
        btnSubmit = (ImageButton) getView().findViewById(R.id.btnSubmit);
        btnPlay = (ImageButton) getView().findViewById(R.id.btnPlay);
        btnStartRecording = (ImageButton) getView().findViewById(R.id.btnStartRecording);
        btnStopRecording = (ImageButton) getView().findViewById(R.id.btnStopRecording);
        textPlaceholder = (TextView) getView().findViewById(R.id.textPlaceholder);
        newPhraseText = (EditText) getView().findViewById(R.id.newPhraseText);
        newCategoryText = (EditText) getView().findViewById(R.id.newCategoryText);
        newLanguageText = (EditText) getView().findViewById(R.id.newLanguageText);
        newLanguageAbbr = (EditText) getView().findViewById(R.id.newLanguageAbbr);
        btnSavePhrase = (Button) getView().findViewById(R.id.savePhrase);
        btnSaveCategory = (Button) getView().findViewById(R.id.saveCategory);
        btnSaveLanguage = (Button) getView().findViewById(R.id.saveLanguage);
        phrase_spinner_pos = 0;
        category_spinner_pos = 0;
        language_spinner_pos = 0;
        phraseSaved = true;
        categorySaved = true;
        languageSaved = true;

        addItemsOnPhraseSpinner();
        addItemsOnCategorySpinner();
        addItemsOnLanguageSpinner();

        btnPlay.setEnabled(false);
        btnSubmit.setEnabled(false);
        textPlaceholder.setBackgroundColor(0xFFFFFFFF);

        phrase_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                phrase_spinner_pos = pos;
                if (pos == 1) {
                    phraseSaved = false;
                    newPhraseText.setVisibility(View.VISIBLE);
                    btnSavePhrase.setVisibility(View.VISIBLE);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                category_spinner_pos = pos;
                if (pos == 1) {
                    categorySaved = false;
                    newCategoryText.setVisibility(View.VISIBLE);
                    btnSaveCategory.setVisibility(View.VISIBLE);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                language_spinner_pos = pos;
                if (pos == 1) {
                    languageSaved = false;
                    newLanguageText.setVisibility(View.VISIBLE);
                    newLanguageAbbr.setVisibility(View.VISIBLE);
                    btnSaveLanguage.setVisibility(View.VISIBLE);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phrase_spinner_pos !=0 && category_spinner_pos != 0  && language_spinner_pos != 0 && phraseSaved && categorySaved && languageSaved) {
                    Toast.makeText(getActivity(),
                            "OnClickListener : " +
                                    "\nPhrase Spinner : " + String.valueOf(phrase_spinner.getSelectedItem()) +
                                    "\nCategory Spinner : " + String.valueOf(category_spinner.getSelectedItem()) +
                                    "\nLanguage Spinner : " + String.valueOf(language_spinner.getSelectedItem()),
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(),
                            "You must select a Phrase, a Category, and a Language",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),
                        "OnClickListener : Recording should PLAYBACK now!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnStartRecording.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getActivity(),
                        "OnClickListener : Recording should START now!",
                        Toast.LENGTH_SHORT).show();
                btnStartRecording.setVisibility(View.INVISIBLE);
                btnStopRecording.setVisibility(View.VISIBLE);
            }
        });

        btnStopRecording.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getActivity(),
                        "OnClickListener : Recording should STOP now!",
                        Toast.LENGTH_SHORT).show();
                btnStartRecording.setVisibility(View.VISIBLE);
                btnStopRecording.setVisibility(View.INVISIBLE);
                btnPlay.setEnabled(true);
                btnSubmit.setEnabled(true);
            }
        });

        btnSavePhrase.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                if ((""+newPhraseText.getText()).length() >= 2) {
                    Toast.makeText(getActivity(),
                            "OnClickListener : NEW PHRASE: "+newPhraseText.getText()+" should be saved now!",
                            Toast.LENGTH_SHORT).show();

                    addOneItemOnPhraseSpinner("" + newPhraseText.getText());
                    newPhraseText.setVisibility(View.INVISIBLE);
                    btnSavePhrase.setVisibility(View.INVISIBLE);
                    phraseSaved = true;
                }
                else {
                    Toast.makeText(getActivity(),
                            "NEW PHRASE must have at least 2 characters!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSaveCategory.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                if ((""+newCategoryText.getText()).length() >= 2) {
                    Toast.makeText(getActivity(),
                            "OnClickListener : NEW CATEGORY: "+newCategoryText.getText()+" should be saved now!",
                            Toast.LENGTH_SHORT).show();

                    addOneItemOnCategorySpinner("" + newCategoryText.getText());
                    newCategoryText.setVisibility(View.INVISIBLE);
                    btnSaveCategory.setVisibility(View.INVISIBLE);
                    categorySaved = true;
                }
                else {
                    Toast.makeText(getActivity(),
                            "NEW CATEGORY must have at least 2 characters!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSaveLanguage.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                if ((""+newLanguageText.getText()).length() >= 2 && (""+newLanguageAbbr.getText()).length() >= 2 && (""+newLanguageAbbr.getText()).length() <= 4) {
                    Toast.makeText(getActivity(),
                            "OnClickListener : NEW LANGUAGE: "+newLanguageText.getText()+ '\n' +
                                            "with ABBR: "+newLanguageAbbr.getText()+" should be saved now!",
                            Toast.LENGTH_SHORT).show();

                    addOneItemOnLanguageSpinner("" + newLanguageText.getText(), "" + newLanguageAbbr.getText());
                    newLanguageText.setVisibility(View.INVISIBLE);
                    newLanguageAbbr.setVisibility(View.INVISIBLE);
                    btnSaveLanguage.setVisibility(View.INVISIBLE);
                    languageSaved = true;
                }
                else {
                    Toast.makeText(getActivity(),
                            "NEW LANGUAGE must have at least 2 characters!\n" +
                            "LANGUAGE ABBREVIATION must have between 2 and 4 characters",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void addOneItemOnPhraseSpinner(String newPhrase){
        phrase_spinner = (Spinner) getView().findViewById(R.id.phrase_spinner);
        phrase_list.add(newPhrase);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, phrase_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //phrase_spinner.setAdapter(dataAdapter);
        phrase_spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                dataAdapter,
                R.layout.contact_phrase_spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                this.getActivity()));
        phrase_spinner.setSelection(phrase_list.size());
    }

    public void addItemsOnPhraseSpinner(){
        phrase_spinner = (Spinner) getView().findViewById(R.id.phrase_spinner);
        //List<String> phrase_list = new ArrayList<String>();
        phrase_list.add("Add New Phrase");
        phrase_list.add("Stay in the boat");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, phrase_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phrase_spinner.setPrompt("Select Phrase...");
        //phrase_spinner.setAdapter(dataAdapter);
        phrase_spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                dataAdapter,
                R.layout.contact_phrase_spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                this.getActivity()));
    }

    public void addOneItemOnCategorySpinner(String newCategory){
        category_spinner = (Spinner) getView().findViewById(R.id.category_spinner);
        category_list.add(newCategory);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, category_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //phrase_spinner.setAdapter(dataAdapter);
        category_spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                dataAdapter,
                R.layout.contact_phrase_spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                this.getActivity()));
        category_spinner.setSelection(category_list.size());
    }

    public void addItemsOnCategorySpinner(){
        category_spinner = (Spinner) getView().findViewById(R.id.category_spinner);
        //List<String> category_list = new ArrayList<String>();
        category_list.add("Add New Category");
        category_list.add("Entering Karaoke Zone");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, category_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setPrompt("Select Category...");
        //category_spinner.setAdapter(dataAdapter);
        category_spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                dataAdapter,
                R.layout.contact_category_spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                this.getActivity()));

    }

    public void addOneItemOnLanguageSpinner(String newLanguage, String newAbbr){
        language_spinner = (Spinner) getView().findViewById(R.id.language_spinner);
        String langAndAbbr = newLanguage + " [" + newAbbr.toUpperCase() + "]";
        language_list.add(langAndAbbr);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, language_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //phrase_spinner.setAdapter(dataAdapter);
        language_spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                dataAdapter,
                R.layout.contact_phrase_spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                this.getActivity()));
        language_spinner.setSelection(language_list.size());
    }

    public void addItemsOnLanguageSpinner(){
        language_spinner = (Spinner) getView().findViewById(R.id.language_spinner);
        //List<String> language_list = new ArrayList<String>();
        language_list.add("Add New Language");
        language_list.add("Engrish");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, language_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language_spinner.setPrompt("Select Language...");
        //language_spinner.setAdapter(dataAdapter);
        language_spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                                      dataAdapter,
                                      R.layout.contact_language_spinner_row_nothing_selected,
                                      // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                                      this.getActivity()));

    }

}

//set newPhraseText and savePhrase to be visible when phrase pos == 0
//similar for Category and Language (include newLanguageAbbr)
//Add input to phrase_list/category_list/language_list upon hitting respective Save button and set values to invisible
//
//CONNECT SPINNERS TO ACTUAL LISTS