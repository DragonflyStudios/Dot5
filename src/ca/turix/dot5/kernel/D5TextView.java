package ca.turix.dot5.kernel;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import ca.turix.dot5.R;

public class D5TextView extends TextView implements OnClickListener {
    
    public D5TextView(Activity hostActivity, String text, Class<? extends D5Activity> d5activityClass)
    {
        super(hostActivity);
        
        m_hostActivity = hostActivity;
        
        if (d5activityClass != null) {
            m_intent = new Intent(hostActivity, d5activityClass);
            setOnClickListener(this);

            SpannableString content = new SpannableString(text);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            setText(content);
        } else
            setText(text);
        
        this.setTextSize(getResources().getDimension(R.dimen.d5text_size));
    }
    
    @Override
    public void onClick(View v)
    {
        if (null != m_intent  &&  v == this)
            m_hostActivity.startActivity(m_intent);
    }

    Intent m_intent;
    Activity m_hostActivity;
}
