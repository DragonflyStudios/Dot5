package ca.turix.dot5;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import ca.turix.dot5.read.D5ReadBookActivity;

public class Dot5 extends Activity {

    private final static Object[][] Dot5Pairs = {
        { "I am to read", null },
        { "The Grapes of Wrath", D5ReadBookActivity.class },
        { "by", null },
        { "John Steinbeck", D5ReadBookActivity.class },
        { "everyday", D5TimeActivity.class },
        { "at", null },
        { "10:30pm", D5TimeActivity.class },
        { "for", null },
        { "30 minutes", D5TimeActivity.class },
        { ".", null }
    };
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.main);
        m_mainView = (FrameLayout)this.findViewById(R.id.screen);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Log.d("metrics height", " = " + metrics.heightPixels);
        Log.d("metrics width", " = " + metrics.widthPixels);

        Dot5TextView d5tvSpace = new Dot5TextView(this, " ", null);
        d5tvSpace.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int spaceWidth = d5tvSpace.getMeasuredWidth();

        Dot5TextView d5tvfp = new Dot5TextView(this, "fp", null);
        d5tvfp.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int lineHeight = d5tvfp.getMeasuredWidth();
        Log.d("spaceWidth", " = " + spaceWidth);
        Log.d("lineHeight", " = " + lineHeight);
                
        m_reflower = new Reflower(metrics.widthPixels, metrics.heightPixels, lineHeight, spaceWidth);

        int size = Dot5Pairs.length;
        int[] boxWidths = new int[size];
        Dot5TextView[] d5tvs = new Dot5TextView[size];
        
        for(int i = 0; i < size; i++) {
            Dot5TextView d5tv = new Dot5TextView(this, (String)Dot5Pairs[i][0], (Class<? extends D5Activity>)Dot5Pairs[i][1]);
            d5tv.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            boxWidths[i] = d5tv.getMeasuredWidth();
            d5tvs[i] = d5tv;
        }
        
        Point[] coordinates = m_reflower.reflow(boxWidths);

        for (int i = 0; i < size; i++) {
            int left = coordinates[i].x;
            int top = coordinates[i].y;

            Log.d(d5tvs[i].getText().toString(), "left = " + left);
            Log.d(d5tvs[i].getText().toString(), "top = " + top);
            
            // TODO: do we have to be this complicated?
            ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(d5tvs[i].getMeasuredWidth(), d5tvs[i].getMeasuredHeight());
            marginParams.setMargins(left, top, 0, 0);
            FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(marginParams);
            frameParams.gravity = Gravity.TOP;
            m_mainView.addView(d5tvs[i], frameParams);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }
        
    FrameLayout m_mainView;
    Reflower m_reflower;
}

/* TODO:
   [x] move this out of the Eclipse workspace
   [x] check this into GitHub ...
   [x] design an "EveryView" extends TextView
       [x] text is styled
       [x] an EveryView may have an action -- starting another activity
           [x] time activity 
           [x] book activity etc ...
       [x] text with action is further underlined
   [x] design a Layout for laying out a sequence of EveryViews ...
   [x] time activity
   [ ] develop a content sharing protocol across the activities
   [ ] book activity
   [ ] develop an XML schema specifying the "sentence"
   [ ] raised background with shadows (see exhibits)
*/