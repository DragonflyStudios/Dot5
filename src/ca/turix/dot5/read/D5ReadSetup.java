package ca.turix.dot5.read;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import ca.turix.dot5.R;
import ca.turix.dot5.kernel.D5Setup;

public class D5ReadSetup extends D5Setup {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.read_setup);
    }
}
