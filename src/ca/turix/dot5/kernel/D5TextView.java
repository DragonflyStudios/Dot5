package ca.turix.dot5.kernel;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class D5TextView extends TextView implements OnTouchListener {
    
    public D5TextView(Activity hostActivity, String text, Class<? extends D5Activity> d5activityClass)
    {
        super(hostActivity);
        
        m_hostActivity = hostActivity;
        
        if (d5activityClass != null) {
            m_intent = new Intent(hostActivity, d5activityClass);
            setOnTouchListener(this);

            SpannableString content = new SpannableString(text);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            setText(content);
        } else
            setText(text);
    }
    
    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        if (null != m_intent  &&  view == this  &&  0 != (MotionEvent.ACTION_UP & event.getAction()))
            m_hostActivity.startActivity(m_intent);
        
        return true;
    }
    
    Intent m_intent;
    Activity m_hostActivity;
}
