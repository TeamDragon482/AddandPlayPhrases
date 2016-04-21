package dragon.tamu.playphrase;

import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlayManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener{

    private MediaPlayer mp;
    private boolean repeat;
    private int curPosition;
    private List<String> phraseFiles;

    public PlayManager(){
        repeat = false;
    }

    public void toggleRepeat(boolean b)
    {
        repeat = b;
    }

    public void playPhrase(Phrase p, ArrayList<String> languages) {
        phraseFiles = new LinkedList<String>();
        for (int i = 0; i < languages.size(); i++) {
            if(p.phraseLanguages.containsKey(languages.get(i)))
                phraseFiles.add(p.phraseLanguages.get(languages.get(i)));
        }
        curPosition = 0;
        mp = new MediaPlayer();
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        playQueue();
    }

    public void stopPhrase(){
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    private void playQueue() {
        if (curPosition >= phraseFiles.size() && !repeat) {
            mp.release();
        } else {
            // mp.release();
            try {
                mp.setDataSource(phraseFiles.get(curPosition));
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                e.printStackTrace();
             /* Left out for now because I want error
             curPosition++;
              playQueue();
               */
            }
            curPosition++;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playQueue();
    }
}