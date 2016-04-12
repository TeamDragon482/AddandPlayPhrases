package dragon.tamu.playphrase;

import android.animation.Animator;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

/**
 * Created by Seth on 3/24/2016.
 * Meant to be used for inputting data on phrases.
 */
public class AddPhraseFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_phrase_fragment_layout, container, false);

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

}
