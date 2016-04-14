package dragon.tamu.playphrase;

import android.animation.Animator;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Elizabeth on 4/12/2016.
 * Meant to be used for inputting data on category.
 */
public class AddCategoryFragment extends Fragment {

    private TextView catText;
    private EditText categoryName;

    FragmentActivity listener;
    FileAccessor fileSystem;

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
        {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.add_category_fragment, container, false);
        //TODO: Would be better to try to get the UI to match what we turned in
            // (like it's at the top, and when you hit save/enter/done the edit closes and the category
            //  is added to the top of the category list)
        //  but for right now, I'm just going to try to get something that does in face create a category

        //This bit of code is for a fancy expandable animation.
        v.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
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
        return v;
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        fileSystem = ((EditActivity) getActivity()).fileSystem;

        categoryName = (EditText) getView().findViewById(R.id.category_name);

        categoryName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addCategory(categoryName.getText().toString());

                    // hide keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(categoryName.getWindowToken(), 0);

                    handled = true;
                }
                return handled;
            }
        });

    }

    @Override
    public void onResume()
    {
        categoryName.setText("");
        categoryName.requestFocus();

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(categoryName, InputMethodManager.SHOW_IMPLICIT);

        super.onResume();
    }

    public void addCategory(String catName)
    {
        fileSystem.addCategory(catName);
        Log.d("Edit Activity", "Added Category");
        ((EditActivity) getActivity()).loadList();
        getActivity().onBackPressed();

    }
}
