package ca.turix.dot5.read;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import ca.turix.dot5.R;
import ca.turix.dot5.kernel.D5Act;

public class D5ReadAct extends D5Act {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.read_act);
        
        try{
            MediaPlayer mp = MediaPlayer.create(getBaseContext(), R.raw.traffic);
            if (mp != null) {
                    mp.start();
            }
            mp.setOnCompletionListener(mCompletionListener);
        } catch (Exception e) {
            Log.w("ReadAct sound playing", e.getMessage());
        }
    }
    
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            mp.release();
        }
    };
}
