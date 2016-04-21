package dragon.tamu.playphrase;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import android.support.v4.app.Fragment;


/**
 * Created by marky on 3/29/2016.
 */
public class RecordingFragment extends Fragment {
    FragmentActivity listener;
    FileAccessor fileSystem;
    private Spinner phrase_spinner, category_spinner, language_spinner;
    private int phrase_spinner_pos, category_spinner_pos, language_spinner_pos;
    private ImageButton btnSubmit, btnPlay, btnPause, btnStartRecording, btnStopRecording;
    private List<String> phrase_list = new ArrayList<String>();
    private List<String> category_list = new ArrayList<String>();
    private List<String> language_list = new ArrayList<String>();
    private List<String> abbreviation_list = new ArrayList<String>();
    private ArrayList<Category> catList = new ArrayList<Category>();
    private Set<String> langList;
    private Collection<String> abbrList;
    private EditText newPhraseText, newCategoryText, newLanguageText, newLanguageAbbr;
    private ImageButton btnCancelPhrase, btnCancelCategory, btnCancelLanguage;
    private Boolean phraseSaved, categorySaved, languageSaved, abbrSaved;
    private Boolean firstOpen = true;
    private Boolean recordStopped = false;
    private Boolean cont = true;
    private String finalPhraseName = "";
    private String finalLangName = "";
    private String finalLangAbbr = "";
    private String finalFilePath;
    private String finalCatName = "";
    private MediaPlayer mediaPlayer = null;
    private MediaRecorder mediaRecorder = null;
    private Visualizer mVisualizer;
    private Snackbar snackbar;
    private Boolean snackbarShown = false;
    private Boolean lockEdit = false;
    private Boolean abbrExists = false;
    //For visualization
    private VisualizerView visualizerView;

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        File f = new File(activity.getFilesDir().getAbsolutePath() + "/recordings");
        boolean exists = (f.mkdir() || f.isDirectory());
        Log.d("direcitory exists", exists + "");
        finalFilePath = f.getAbsolutePath() + "/temp.mp4";
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        clearText();
        catList = fileSystem.getInfoList();
        addItemsOnPhraseSpinner();
        addItemsOnCategorySpinner();
        addItemsOnLanguageSpinner();
        resumeSpinners();
        super.onResume();
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
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
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        //Getting a handle of views from XML
        phrase_spinner = (Spinner) getView().findViewById(R.id.phrase_spinner);
        category_spinner = (Spinner) getView().findViewById(R.id.category_spinner);
        language_spinner = (Spinner) getView().findViewById(R.id.language_spinner);
        btnSubmit = (ImageButton) getView().findViewById(R.id.btnSubmit);
        btnPlay = (ImageButton) getView().findViewById(R.id.btnPlay);
        btnPause = (ImageButton) getView().findViewById(R.id.btnPause);
        btnStartRecording = (ImageButton) getView().findViewById(R.id.btnStartRecording);
        btnStopRecording = (ImageButton) getView().findViewById(R.id.btnStopRecording);
        //textPlaceholder = (TextView) getView().findViewById(R.id.textPlaceholder);
        newPhraseText = (EditText) getView().findViewById(R.id.newPhraseText);
        newCategoryText = (EditText) getView().findViewById(R.id.newCategoryText);
        newLanguageText = (EditText) getView().findViewById(R.id.newLanguageText);
        newLanguageAbbr = (EditText) getView().findViewById(R.id.newLanguageAbbr);
        btnCancelPhrase = (ImageButton) getView().findViewById(R.id.cancelPhrase); //save buttons are now Cancel
        btnCancelCategory = (ImageButton) getView().findViewById(R.id.cancelCategory);
        btnCancelLanguage = (ImageButton) getView().findViewById(R.id.cancelLanguage);
        visualizerView = (VisualizerView) getView().findViewById(R.id.visualizer_view);

        phrase_spinner_pos = 0;
        category_spinner_pos = 0;
        language_spinner_pos = 0;
        phraseSaved = true;
        categorySaved = true;
        languageSaved = true;
        abbrSaved = true;
        /*//THIS WAS THE CULPRIT OF THE NON DELETING
        ((EditActivity) getActivity()).loadList();*/
        fileSystem = ((EditActivity) getActivity()).fileSystem;
        catList = fileSystem.getInfoList();

        if (firstOpen) {
            addItemsOnPhraseSpinner();
            addItemsOnCategorySpinner();
            addItemsOnLanguageSpinner();
        }
        firstOpen = false;

        //btnPlay.setEnabled(false);
        //btnSubmit.setEnabled(false);
        //textPlaceholder.setBackgroundColor(0xFFFFFFFF);

        phrase_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                phrase_spinner_pos = pos;

                ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, category_list);
                dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                category_spinner.setPrompt("Select Category...");
                category_spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                        dataAdapter2,
                        R.layout.contact_category_spinner_row_nothing_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        getActivity()));

                if (pos == 1) {
                    phraseSaved = false;
                    lockEdit = false;
                    newPhraseText.setVisibility(View.VISIBLE);

                    newPhraseText.setFocusableInTouchMode(true);
                    newPhraseText.requestFocus();

                    //TODO doesn't seem to do anything
                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(newPhraseText, InputMethodManager.SHOW_IMPLICIT);

                    btnCancelPhrase.setVisibility(View.VISIBLE);

                } else {
                    //Change Category list/selection to match selected phrase
                    Category category = null;
                    cont = true;
                    for (Category cat : catList) {
                        List<Object> phraseList = cat.phraseList;
                        for (int i = 0; i < phraseList.size(); i++) {
                            Phrase phr = (Phrase) phraseList.get(i);
                            if (phr.getPhraseText().equalsIgnoreCase((String)phrase_spinner.getAdapter().getItem(pos))) {
                                category = cat;
                                break;
                            }
                        }
                        if (category != null) break;
                    }
                    if (category != null) {
                        for (int i = 1; i < category_spinner.getAdapter().getCount(); i++) {
                            if (category_spinner.getAdapter().getItem(i).toString().equals(category.name)) {
                                List<String> short_list = new ArrayList<String>();
                                short_list.add(category.name);
                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, short_list);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                //category_spinner.setPrompt("Select Category...");

                                category_spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                                        dataAdapter,
                                        R.layout.contact_category_spinner_row_nothing_selected,
                                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                                        getActivity()));
                                category_spinner.setSelection(1);

                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(newCategoryText.getWindowToken(), 0);

                                lockEdit = true;
                            }
                        }
                    }

                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                category_spinner_pos = pos;
                if (pos == 1 && !lockEdit) {
                    categorySaved = false;
                    newCategoryText.setVisibility(View.VISIBLE);

                    newCategoryText.setFocusableInTouchMode(true);
                    newCategoryText.requestFocus();
                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(newCategoryText, InputMethodManager.SHOW_IMPLICIT);

                    btnCancelCategory.setVisibility(View.VISIBLE);
                } else {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(newCategoryText.getWindowToken(), 0);
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
                    abbrSaved = false;
                    newLanguageText.setVisibility(View.VISIBLE);
                    newLanguageAbbr.setVisibility(View.VISIBLE);
                    btnCancelLanguage.setVisibility(View.VISIBLE);

                    newLanguageText.setFocusableInTouchMode(true);
                    newLanguageText.requestFocus();
                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(newLanguageText, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(newCategoryText.getWindowToken(), 0);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phrase_spinner_pos != 0 && category_spinner_pos != 0 && language_spinner_pos != 0 && recordStopped && (phrase_spinner_pos != 1 || ("" + newPhraseText.getText()).length() >= 2) && (lockEdit || category_spinner_pos != 1 || ("" + newCategoryText.getText()).length() >= 2) && (language_spinner_pos != 1 || (("" + newLanguageText.getText()).length() >= 2 && ("" + newLanguageAbbr.getText()).length() >= 2 && ("" + newLanguageAbbr.getText()).length() <= 3)) && !abbrExists) {

                    //Establish values to be saved and then call addPhrase
                    if (phrase_spinner_pos == 1) {
                        finalPhraseName = newPhraseText.getText().toString();
                    } else {
                        finalPhraseName = phrase_spinner.getSelectedItem().toString();
                    }
                    if (category_spinner_pos == 1 && !lockEdit) {
                        finalCatName = newCategoryText.getText().toString();
                    } else {
                        finalCatName = category_spinner.getSelectedItem().toString();
                    }
                    if (language_spinner_pos == 1) {
                        finalLangName = newLanguageText.getText().toString();
                        finalLangAbbr = newLanguageAbbr.getText().toString();
                    } else {
                        String temp = language_spinner.getSelectedItem().toString();
                        temp.replace(" ","");
                        temp.replace("]","");
                        String delimiter = "\\W";
                        String[] parts = temp.split(delimiter);
                        finalLangName = parts[0];
                        finalLangAbbr = parts[1];
                    }
                    //ONLY EXISTING ONE IS catname_langname_phrasename.mp3
                    //finalFilePath = "C:\\Users\\Marc\\Studio Projects\\new\\AddandPlayPhrases\\playphrase\\src\\main\\res\\raw\\" + finalCatName + "_" + finalLangName + "_" + finalPhraseName + ".mp3";

                    addPhrase(finalPhraseName, finalLangName, finalLangAbbr, finalFilePath, finalCatName);


                    /*Toast.makeText(getActivity(),
                            "OnClickListener : " +
                                    "\nPhrase Spinner : " + String.valueOf(phrase_spinner.getSelectedItem()) +
                                    "\nCategory Spinner : " + String.valueOf(category_spinner.getSelectedItem()) +
                                    "\nLanguage Spinner : " + String.valueOf(language_spinner.getSelectedItem()),
                            Toast.LENGTH_SHORT).show();*/
                } else if (phrase_spinner_pos == 0) {
                    snackbar = Snackbar
                            .make(view, "Phrase Not Selected", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                    /*Toast.makeText(getActivity(),
                            "You must select a Phrase, a Category, and a Language",
                            Toast.LENGTH_SHORT).show();*/
                } else if (category_spinner_pos == 0) {
                    snackbar = Snackbar
                            .make(view, "Category Not Selected", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                } else if (language_spinner_pos == 0) {
                    snackbar = Snackbar
                            .make(view, "Language Not Selected", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                } else if (!recordStopped) {
                    snackbar = Snackbar
                            .make(view, "Finish Recording First", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                } else if (abbrExists) {
                    snackbar = Snackbar
                            .make(view, "Abbreviation Already Exists", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                } else if (!(("" + newPhraseText.getText()).length() >= 2) && phrase_spinner_pos == 1) {
                    snackbar = Snackbar
                            .make(view, "Phrase Must Have 2+ Characters", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                } else if (!(("" + newCategoryText.getText()).length() >= 2) && category_spinner_pos == 1 && !lockEdit) {
                    snackbar = Snackbar
                            .make(view, "Category Must Have 2+ Characters", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                } else if (!(("" + newLanguageText.getText()).length() >= 2) && language_spinner_pos == 1) {
                    snackbar = Snackbar
                            .make(view, "Language Must Have 2+ Characters", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                } else if (!(("" + newLanguageAbbr.getText()).length() >= 2 && ("" + newLanguageAbbr.getText()).length() <= 3)) {
                    snackbar = Snackbar
                            .make(view, "Abbreviation Must Have 2-3 Characters", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                }
            }
        });

        btnPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordStopped) {
                    snackbar = Snackbar
                            .make(view, "PLAYBACK", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                    /*Toast.makeText(getActivity(),
                            "OnClickListener : Recording should PLAYBACK now!",
                            Toast.LENGTH_SHORT).show();*/

                    startPlay();
                    btnPlay.setVisibility(View.INVISIBLE);
                    btnPause.setVisibility(View.VISIBLE);

                } else {
                    snackbar = Snackbar
                            .make(view, "Finish Recording First", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                    /*Toast.makeText(getActivity(),
                            "Cannot playback until recording has stopped!",
                            Toast.LENGTH_SHORT).show();*/
                }
            }
        });

        btnPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordStopped) {
                    snackbar = Snackbar
                            .make(view, "PAUSE PLAYBACK", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                    /*Toast.makeText(getActivity(),
                            "OnClickListener : Recording should PLAYBACK now!",
                            Toast.LENGTH_SHORT).show();*/

                    mediaPlayer.pause();
                    btnPause.setVisibility(View.INVISIBLE);
                    btnPlay.setVisibility(View.VISIBLE);

                } else {
                    snackbar = Snackbar
                            .make(view, "Finish Recording First", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                    /*Toast.makeText(getActivity(),
                            "Cannot playback until recording has stopped!",
                            Toast.LENGTH_SHORT).show();*/
                }
            }
        });

        btnStartRecording.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar = Snackbar
                        .make(view, "Start Recording", Snackbar.LENGTH_SHORT);

                snackbar.show();
                /*Toast.makeText(getActivity(),
                        "OnClickListener : Recording should START now!",
                        Toast.LENGTH_SHORT).show();*/
                if (mediaPlayer == null) {

                } else if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                } else {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                startRecord();

                btnPause.setVisibility(View.INVISIBLE);
                btnPlay.setVisibility(View.VISIBLE);
                btnStartRecording.setVisibility(View.INVISIBLE);
                btnStopRecording.setVisibility(View.VISIBLE);
                recordStopped = false;
            }
        });

        btnStopRecording.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar = Snackbar
                        .make(view, "Stop Recording", Snackbar.LENGTH_SHORT);

                snackbar.show();
                /*Toast.makeText(getActivity(),
                        "OnClickListener : Recording should STOP now!",
                        Toast.LENGTH_SHORT).show();*/

                stopRecord();

                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.catname_langname_phrasename);

                btnStartRecording.setVisibility(View.VISIBLE);
                btnStopRecording.setVisibility(View.INVISIBLE);
                btnPlay.setEnabled(true);
                btnSubmit.setEnabled(true);
                recordStopped = true;
            }
        });


        //NOW A CANCEL
        btnCancelPhrase.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                newPhraseText.setText("");
                newPhraseText.setVisibility(View.INVISIBLE);
                btnCancelPhrase.setVisibility(View.INVISIBLE);
                phrase_spinner.setSelection(0);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(newCategoryText.getWindowToken(), 0);
                /*
                if ((""+newPhraseText.getText()).length() >= 2) {
                    snackbar = Snackbar
                            .make( view, "New Phrase Saved", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                    //Toast.makeText(getActivity(),
                    //        "OnClickListener : NEW PHRASE: "+newPhraseText.getText()+" should be saved now!",
                    //        Toast.LENGTH_SHORT).show();

                    addOneItemOnPhraseSpinner("" + newPhraseText.getText());
                    newPhraseText.setVisibility(View.INVISIBLE);
                    btnCancelPhrase.setVisibility(View.INVISIBLE);
                    phraseSaved = true;
                }
                else {
                    snackbar = Snackbar
                            .make( view, "Phrase Must Have 2+ Characters", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                    //Toast.makeText(getActivity(),
                    //        "NEW PHRASE must have at least 2 characters!",
                    //        Toast.LENGTH_SHORT).show();
                }
                */
            }
        });

        btnCancelCategory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                newCategoryText.setText("");
                newCategoryText.setVisibility(View.INVISIBLE);
                btnCancelCategory.setVisibility(View.INVISIBLE);
                category_spinner.setSelection(0);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(newCategoryText.getWindowToken(), 0);
                /*
                if ((""+newCategoryText.getText()).length() >= 2) {
                    snackbar = Snackbar
                            .make( view, "New Category Saved", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                    /*Toast.makeText(getActivity(),
                            "OnClickListener : NEW CATEGORY: "+newCategoryText.getText()+" should be saved now!",
                            Toast.LENGTH_SHORT).show();

                    addOneItemOnCategorySpinner("" + newCategoryText.getText());
                    newCategoryText.setVisibility(View.INVISIBLE);
                    btnCancelCategory.setVisibility(View.INVISIBLE);
                    categorySaved = true;
                }
                else {
                    snackbar = Snackbar
                            .make( view, "Category Must Have 2+ Characters", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                    /*Toast.makeText(getActivity(),
                            "NEW CATEGORY must have at least 2 characters!",
                            Toast.LENGTH_SHORT).show();
                }
                */
            }
        });

        btnCancelLanguage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                newLanguageText.setText("");
                newLanguageAbbr.setText("");
                newLanguageText.setVisibility(View.INVISIBLE);
                newLanguageAbbr.setVisibility(View.INVISIBLE);
                btnCancelLanguage.setVisibility(View.INVISIBLE);
                language_spinner.setSelection(0);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(newCategoryText.getWindowToken(), 0);

                /*
                if ((""+newLanguageText.getText()).length() >= 2 && (""+newLanguageAbbr.getText()).length() >= 2 && (""+newLanguageAbbr.getText()).length() <= 4) {
                    snackbar = Snackbar
                            .make( view, "New Language Saved", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                    /*Toast.makeText(getActivity(),
                            "OnClickListener : NEW LANGUAGE: "+newLanguageText.getText()+ '\n' +
                                            "with ABBR: "+newLanguageAbbr.getText()+" should be saved now!",
                            Toast.LENGTH_SHORT).show();

                    addOneItemOnLanguageSpinner("" + newLanguageText.getText(), "" + newLanguageAbbr.getText());
                    newLanguageText.setVisibility(View.INVISIBLE);
                    newLanguageAbbr.setVisibility(View.INVISIBLE);
                    btnCancelLanguage.setVisibility(View.INVISIBLE);
                    languageSaved = true;
                }
                else if(!((""+newLanguageText.getText()).length() >= 2)) {
                    snackbar = Snackbar
                            .make( view, "Language Must Have 2+ Characters", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                    /*Toast.makeText(getActivity(),
                            "NEW LANGUAGE must have at least 2 characters!\n" +
                            "LANGUAGE ABBREVIATION must have between 2 and 4 characters",
                            Toast.LENGTH_SHORT).show();
                }
                else if(!((""+newLanguageAbbr.getText()).length() >= 2)) {
                    snackbar = Snackbar
                            .make( view, "Abbreviation Must Have 2-4 Characters", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                }
                else if(!((""+newLanguageAbbr.getText()).length() <= 4)) {
                    snackbar = Snackbar
                            .make( view, "Abbreviation Must Have 2-4 Characters", Snackbar.LENGTH_SHORT);

                    snackbar.show();
                }
                */
            }
        });

        newPhraseText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent key) {
                boolean handled = false;
                //DO THE HANDLING


                //hide keyboard and save data
                newPhraseText.requestFocus();
                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(newPhraseText, InputMethodManager.SHOW_IMPLICIT);
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(newCategoryText.getWindowToken(), 0);

                    //if (!phraseSaved) {
                    if (("" + newPhraseText.getText()).length() >= 2) {
//                        snackbar = Snackbar
//                                .make(view, "New Phrase Saved", Snackbar.LENGTH_SHORT);
//
//                        snackbar.show();
                                /*Toast.makeText(getActivity(),
                                        "OnClickListener : NEW PHRASE: "+newPhraseText.getText()+" should be saved now!",
                                        Toast.LENGTH_SHORT).show();*/

                        //addOneItemOnPhraseSpinner("" + newPhraseText.getText());
                        InputMethodManager imm2 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm2.hideSoftInputFromWindow(newPhraseText.getWindowToken(), 0);
                        //newPhraseText.setVisibility(View.INVISIBLE);
                        //btnCancelPhrase.setVisibility(View.INVISIBLE);
                        phraseSaved = true;
                    } else {
                        snackbar = Snackbar
                                .make(view, "Phrase Must Have 2+ Characters", Snackbar.LENGTH_SHORT);

                        snackbar.show();
                                /*Toast.makeText(getActivity(),
                                        "NEW PHRASE must have at least 2 characters!",
                                        Toast.LENGTH_SHORT).show();*/
                    }
                    //}

                    handled = true;
                }
                return handled;
            }
        });

        newCategoryText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent key) {
                boolean handled = false;
                //DO THE HANDLING


                //hide keyboard and save data
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(newCategoryText.getWindowToken(), 0);

                    //if (!categorySaved) {
                    if (("" + newCategoryText.getText()).length() >= 2) {
//                        snackbar = Snackbar
//                                .make(view, "New Category Saved", Snackbar.LENGTH_SHORT);
//
//                        snackbar.show();
                                /*Toast.makeText(getActivity(),
                                        "OnClickListener : NEW CATEGORY: "+newCategoryText.getText()+" should be saved now!",
                                        Toast.LENGTH_SHORT).show();*/

                        //addOneItemOnCategorySpinner("" + newCategoryText.getText());
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(newCategoryText.getWindowToken(), 0);
                        //newCategoryText.setVisibility(View.INVISIBLE);
                        //btnCancelCategory.setVisibility(View.INVISIBLE);
                        categorySaved = true;
                    } else {
                        snackbar = Snackbar
                                .make(view, "Category Must Have 2+ Characters", Snackbar.LENGTH_SHORT);

                        snackbar.show();
                                /*Toast.makeText(getActivity(),
                                        "NEW CATEGORY must have at least 2 characters!",
                                        Toast.LENGTH_SHORT).show();*/
                    }
                    //}

                    handled = true;
                }
                return handled;
            }
        });

        newLanguageText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent key) {
                boolean handled = false;
                //DO THE HANDLING


                //hide keyboard and save data
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(newCategoryText.getWindowToken(), 0);

                    //if (!languageSaved || !abbrSaved) {
                    if (("" + newLanguageText.getText()).length() >= 2 ) {
//                        snackbar = Snackbar
//                                .make(view, "New Language Saved", Snackbar.LENGTH_SHORT);
//
//                        snackbar.show();
                        /*Toast.makeText(getActivity(),
                                "OnClickListener : NEW LANGUAGE: "+newLanguageText.getText()+ '\n' +
                                                "with ABBR: "+newLanguageAbbr.getText()+" should be saved now!",
                                Toast.LENGTH_SHORT).show();*/

                        //addOneItemOnLanguageSpinner("" + newLanguageText.getText(), "" + newLanguageAbbr.getText());
                        //newLanguageAbbr.setText(newLanguageAbbr.getText().toString().toUpperCase());
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(newLanguageText.getWindowToken(), 0);


                        //newLanguageAbbr.setFocusableInTouchMode(true);
                        //newLanguageAbbr.requestFocus();
                        //imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        //imm.showSoftInput(newLanguageAbbr, InputMethodManager.SHOW_IMPLICIT);


                        //newLanguageText.setVisibility(View.INVISIBLE);
                        //newLanguageAbbr.setVisibility(View.INVISIBLE);
                        //btnCancelLanguage.setVisibility(View.INVISIBLE);
                        languageSaved = true;
                    } else if (!(("" + newLanguageText.getText()).length() >= 2)) {
                        snackbar = Snackbar
                                .make(view, "Language Must Have 2+ Characters", Snackbar.LENGTH_SHORT);

                        snackbar.show();
                        /*Toast.makeText(getActivity(),
                                "NEW LANGUAGE must have at least 2 characters!\n" +
                                "LANGUAGE ABBREVIATION must have between 2 and 4 characters",
                                Toast.LENGTH_SHORT).show();*/
                    }
                    //}


                    handled = true;
                }
                return handled;
            }
        });

        newLanguageAbbr.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent key) {
                boolean handled = false;

                //hide keyboard and save data
                newLanguageAbbr.setFocusableInTouchMode(true);
                newLanguageAbbr.requestFocus();
                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(newLanguageAbbr, InputMethodManager.SHOW_IMPLICIT);
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(newCategoryText.getWindowToken(), 0);

                    //Check if the abbreviation exists for another language
                    String[] tempAbbrArray = new String[abbrList.size()];
                    tempAbbrArray = abbrList.toArray(tempAbbrArray);
                    abbrExists = false;
                    for (int i=0; i < tempAbbrArray.length; i++) {
                        if (newLanguageAbbr.getText().toString().equalsIgnoreCase(tempAbbrArray[i])) {
                            abbrExists = true;
                            break;
                        }
                    }

                    //if (!languageSaved || !abbrSaved) {
                    if (("" + newLanguageAbbr.getText()).length() >= 2 && ("" + newLanguageAbbr.getText()).length() <= 3 && !abbrExists) {
//                        snackbar = Snackbar
//                                .make(view, "New Language Saved", Snackbar.LENGTH_SHORT);
//
//                        snackbar.show();

                        //addOneItemOnLanguageSpinner("" + newLanguageText.getText(), "" + newLanguageAbbr.getText());
                        newLanguageAbbr.setText(newLanguageAbbr.getText().toString().toUpperCase());
                        InputMethodManager imm2 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm2.hideSoftInputFromWindow(newLanguageAbbr.getWindowToken(), 0);
                        //newLanguageText.setVisibility(View.INVISIBLE);
                        //newLanguageAbbr.setVisibility(View.INVISIBLE);
                        //btnCancelLanguage.setVisibility(View.INVISIBLE);
                        languageSaved = true;
                    } else if (!(("" + newLanguageAbbr.getText()).length() >= 2)) {
                        snackbar = Snackbar
                                .make(view, "Abbreviation Must Have 2-3 Characters", Snackbar.LENGTH_SHORT);

                        snackbar.show();
                    } else if (!(("" + newLanguageAbbr.getText()).length() <= 3)) {
                        snackbar = Snackbar
                                .make(view, "Abbreviation Must Have 2-3 Characters", Snackbar.LENGTH_SHORT);

                        snackbar.show();
                    } else if (abbrExists) {
                        snackbar = Snackbar
                                .make(view, "Abbreviation Already Exists", Snackbar.LENGTH_SHORT);

                        snackbar.show();
                    }
                    //}


                    handled = true;
                }
                return handled;
            }
        });

    }

    public void addOneItemOnPhraseSpinner(String newPhrase) {
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

    public void addItemsOnPhraseSpinner() {
        phrase_spinner = (Spinner) getView().findViewById(R.id.phrase_spinner);

        phrase_list.clear();
        phrase_list.add("Add New Phrase");
        //phrase_list.add("Stay in the boat");

        for (Category cat : catList) {
            List<Object> phraseList = cat.phraseList;
            for (int i = 0; i < phraseList.size(); i++) {
                String phr = ((Phrase) phraseList.get(i)).getPhraseText();
                phrase_list.add(phr);
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, phrase_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phrase_spinner.setPrompt("Select Phrase...");

        phrase_spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                dataAdapter,
                R.layout.contact_phrase_spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                this.getActivity()));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isRemoving() && mediaPlayer != null) {
            if (mVisualizer != null)
                mVisualizer.release();
            mediaPlayer.release();
            mediaPlayer = null;
            if (mediaRecorder != null) {
                mediaRecorder.release();
                mediaRecorder = null;

            }
        }
    }

    public void resumeSpinners() {
        phrase_spinner = (Spinner) getView().findViewById(R.id.phrase_spinner);
        category_spinner = (Spinner) getView().findViewById(R.id.category_spinner);
        language_spinner = (Spinner) getView().findViewById(R.id.language_spinner);

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, phrase_list);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phrase_spinner.setPrompt("Select Phrase...");
        phrase_spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                dataAdapter1,
                R.layout.contact_phrase_spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                this.getActivity()));

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, category_list);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setPrompt("Select Category...");
        category_spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                dataAdapter2,
                R.layout.contact_category_spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                this.getActivity()));

        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, language_list);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language_spinner.setPrompt("Select Language...");
        language_spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                dataAdapter3,
                R.layout.contact_language_spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                this.getActivity()));
    }

    public void addOneItemOnCategorySpinner(String newCategory) {
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

    public void addItemsOnCategorySpinner() {
        category_spinner = (Spinner) getView().findViewById(R.id.category_spinner);
        category_list.clear();
        category_list.add("Add New Category");
        //category_list.add("Entering Karaoke Zone");

        for (Category cat : catList) {
            category_list.add(cat.getCategoryTitle());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, category_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setPrompt("Select Category...");

        category_spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                dataAdapter,
                R.layout.contact_category_spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                this.getActivity()));

    }

    public void addOneItemOnLanguageSpinner(String newLanguage, String newAbbr) {
        language_spinner = (Spinner) getView().findViewById(R.id.language_spinner);
        String langAndAbbr = newLanguage + " [" + newAbbr.toUpperCase() + "]";
        language_list.add(langAndAbbr);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, language_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        language_spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                dataAdapter,
                R.layout.contact_phrase_spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                this.getActivity()));
        language_spinner.setSelection(language_list.size());
    }

    public void addItemsOnLanguageSpinner() {
        language_spinner = (Spinner) getView().findViewById(R.id.language_spinner);
        langList = fileSystem.getLangList().keySet();
        abbrList = fileSystem.getLangList().values();
        String[] tempLangArray = new String[langList.size()];
        String[] tempAbbrArray = new String[abbrList.size()];
        tempLangArray = langList.toArray(tempLangArray);
        tempAbbrArray = abbrList.toArray(tempAbbrArray);
        language_list.clear();

        language_list.add("Add New Language");
        //language_list.add("Engrish");

        for (int i=0; i < tempLangArray.length && i < tempAbbrArray.length; i++) {
            language_list.add(tempLangArray[i] + " [" + tempAbbrArray[i] + "]");
            abbreviation_list.add(tempAbbrArray[i]);
        }

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

    public void addPhrase(String phraseName, String language, String abbr, String filePath, String categoryName) {
        Boolean phraseExists = false;
        Category category = null;
        cont = true;
        for (Category cat : catList) {
            if (cat.getCategoryTitle().equalsIgnoreCase(categoryName)) {
                category = cat;
                break;
            }
        }
        String uncategorized = "Uncategorized";
        /*if (category == null)
        {
            for (Category cat : catList) {
                if (cat.getCategoryTitle().equalsIgnoreCase(uncategorized)) {
                    category = cat;
                    //phraseExists = true;
                    break;
                }
            }
        }*/
        Phrase phr = null;
        if (category != null) {
            List<Object> phraseList = category.phraseList;
            for (int i = 0; i < phraseList.size(); i++) {
                phr = (Phrase) phraseList.get(i);
                if (phr.getPhraseText().equalsIgnoreCase(phraseName)) {
                    phraseExists = true;
                    break;
                }
            }
        }

        if(phraseExists && phr != null){
            cont = false;
            Map<String,String> lang = phr.phraseLanguages;
            if (lang.containsKey(language)) {
                //Then phrase / language combination exists
                final String pName = phraseName;
                final String lName = language;
                final String lAbbr = abbr.toUpperCase();
                final String fPath = filePath;
                final String cName = categoryName;


                //TODO get this portion of code working
                snackbar = Snackbar
                        .make(getView(), "Continuing will overwrite existing phrase. Continue anyways?", Snackbar.LENGTH_INDEFINITE)
                        .setAction("CONTINUE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                addCategory(finalCatName);
//                                //((EditActivity) getActivity()).loadList();
//                                addLanguage(finalLangName, finalLangAbbr);
//                                //((EditActivity) getActivity()).loadList();
//                                fileSystem.addPhrase(pName, lName + lAbbr, fPath, cName);
//                                Log.d("Recording Fragment", "Added Phrase");
//                                //((EditActivity) getActivity()).loadList();
                                ((EditActivity) getActivity()).addPhrase(pName, cName, lName + lAbbr, fPath);
                                Snackbar snackbar1 = Snackbar
                                        .make(getView(), "Saved!", Snackbar.LENGTH_SHORT);

                                snackbar1.show();
                                onResume();
                            }
                        })
                        .setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event){
                                snackbarShown = false;
                            }
                        });
                snackbarShown = true;
                Log.d("Recording Fragment","Phrase already exists");

                snackbar.show();
            } else {
                addLanguage(finalLangName, finalLangAbbr);
                ((EditActivity) getActivity()).addPhrase(phraseName, categoryName, language, filePath);
                onResume();
                snackbar = Snackbar
                        .make(getView(), "Saved!", Snackbar.LENGTH_SHORT);

                snackbar.show();
                //getActivity().onBackPressed();
            }
        }


        if (cont) {
            /*addCategory(finalCatName);
            ((EditActivity) getActivity()).loadList();
            addLanguage(finalLangName, finalLangAbbr);
            ((EditActivity) getActivity()).loadList();
            //TODO Find out why new languages aren't showing up on Language Pane despite calling addLanguage
            fileSystem.addPhrase(phraseName, language + abbr.toUpperCase(), filePath, categoryName);
            Log.d("Recording Fragment", "Added Phrase");
            ((EditActivity) getActivity()).loadList();*/
            addLanguage(finalLangName, finalLangAbbr);
            ((EditActivity) getActivity()).addPhrase(phraseName, finalCatName, finalLangName, filePath);
            //getActivity().onBackPressed();
            snackbar = Snackbar
                    .make(getView(), "Saved!", Snackbar.LENGTH_SHORT);

            snackbar.show();
        }
    }

    private void clearText() {
        newCategoryText.getText().clear();
        newPhraseText.getText().clear();
        newLanguageAbbr.getText().clear();
        newLanguageText.getText().clear();
    }

    public void addCategory(String categoryName) {
        Boolean exists = false;
        for (Category cat : catList) {
            if (cat.getCategoryTitle().equalsIgnoreCase(categoryName)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            ((EditActivity) getActivity()).addCategory(categoryName);
            Log.d("Recording Fragment", "Added Category");
        }
    }

    public void addLanguage(String languageName, String languageAbbr) {
        if (!(fileSystem.getLangList().containsKey(languageName))) {

            fileSystem.addLanguage(languageName, languageAbbr);
            Log.d("Recording Fragment", "Added Language");
            //((EditActivity) getActivity()).loadList();
            Log.d("After Language Added", "");
        }
    }

    private void startRecord() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC_ELD);
        //mediaRecorder.setAudioEncoder(MediaRecorder.getAudioSourceMax());
        mediaRecorder.setAudioEncodingBitRate(128000);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setOutputFile(finalFilePath);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Log.d("Recording Test", "prepare() failed");
        }

        mediaRecorder.start();

    }

    private void stopRecord() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void startPlay() {
        mediaPlayer = new MediaPlayer();
        try{
            mediaPlayer.setDataSource(finalFilePath);
            mediaPlayer.prepare();
            setupVisualizerFxAndUI();
            mVisualizer.setEnabled(true);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    btnPause.setVisibility(View.INVISIBLE);
                    btnPlay.setVisibility(View.VISIBLE);
                    mVisualizer.setEnabled(false);
                }
            });
            mediaPlayer.start();
        } catch (IOException e) {
            Log.d("Playback Test", "prepare() failed");
        }
    }

    private void stopPlay() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public Boolean getSnackbarStatus() {
        return snackbarShown;
    }

    public void dismissSnackbar() {
        snackbar.dismiss();
    }

    //Used to setup visualization
    //http://android-er.blogspot.com/2015/02/create-audio-visualizer-for-mediaplayer.html
    private void setupVisualizerFxAndUI() {

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        visualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }
}


//
//Themes -- properties
//Change XML back to linearlayout inside scrollview


