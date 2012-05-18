package ca.turix.dot5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class Dot5TextView extends TextView implements OnTouchListener {
    
    public Dot5TextView(Context context, String text, Class<? extends Activity> activityClass)
    {
        super(context);
        
        if (activityClass != null) {
            m_intent = new Intent(context, activityClass);
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
        if (null != m_intent && view == this && 0 != (MotionEvent.ACTION_UP | event.getAction()))
        {
            getContext().startActivity(m_intent);
            return true;
        }
        
        return false;
    }
    
    Intent m_intent;
}
