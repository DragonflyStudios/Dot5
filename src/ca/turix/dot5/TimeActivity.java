package ca.turix.dot5;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

// TODO: change this to use R.string values for the various lists

public class TimeActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.time);

        m_timeView = (RelativeLayout)findViewById(R.id.time);
        
        m_setOfDaysAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, 
                stringsFromStringIds(DAYSETS_STRING_IDS));
        m_setOfDaysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_setOfDaysSpinner = (Spinner)findViewById(R.id.set_of_days);
        m_setOfDaysSpinner.setAdapter(m_setOfDaysAdapter);
        m_setOfDaysSpinner.setOnItemSelectedListener(new SetOfDaysOnItemSelectedListener());

        m_daysAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,
                stringsFromStringIds(DAYS_STRING_IDS));
        m_daysView = (ListView)findViewById(R.id.days);
        m_daysView.setItemsCanFocus(false);
        m_daysView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        m_daysView.setAdapter(m_daysAdapter);
        m_daysView.setOnItemClickListener(new DaysOnItemClickListener());

        // TODO: the code for Time and Length is pretty much identical.
        //       refactor!
        
        m_sameTimeCheckBox = (CheckBox)findViewById(R.id.same_time);
        m_sameTimeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) {
                    m_sameTimeDisplay.setVisibility(View.VISIBLE);
                    m_timesView.setVisibility(View.INVISIBLE);
                } else {
                    m_sameTimeDisplay.setVisibility(View.INVISIBLE);
                    m_timesView.setVisibility(View.VISIBLE);
                }
            }
        });
        
        m_times = new String[] {
            "01:23am", "01:23am", "01:23am", "01:23am", 
            "01:23am", "01:23am", "01:23am"
        };
        m_timesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, m_times);
        m_timesView = (ListView)findViewById(R.id.times);
        m_timesView.setItemsCanFocus(false);
        m_timesView.setAdapter(m_timesAdapter);
        m_timesView.setOnItemClickListener(new TimesOnItemClickListener());
        m_timePicker = (TimePicker)findViewById(R.id.time_picker);
        m_pickTimeItemPosition = -1;

        m_sameTimeDisplay = (TextView)findViewById(R.id.same_time_display);
        m_sameTimeDisplay.setText(SAME_TIME_DEFAULT);
        m_sameTimeDisplay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_timePicker.setVisibility(View.VISIBLE);
            }
        });
        
        m_sameLengthCheckBox = (CheckBox)findViewById(R.id.same_length);
        m_sameLengthCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) {
                    m_chooseLengthSpinner.setVisibility(View.VISIBLE);
                    m_lengthsView.setVisibility(View.INVISIBLE);
                } else {
                    m_chooseLengthSpinner.setVisibility(View.INVISIBLE);
                    m_lengthsView.setVisibility(View.VISIBLE);
                }
            }
        });
        
        m_lengths = new String[] {
            "30 mins", "30 mins", "30 mins", "30 mins", 
            "30 mins", "30 mins", "30 mins"
        };
        m_lengthsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, m_lengths);
        m_lengthsView = (ListView)findViewById(R.id.lengths);
        m_lengthsView.setItemsCanFocus(false);
        m_lengthsView.setAdapter(m_lengthsAdapter);
        m_lengthsView.setOnItemClickListener(new LengthsOnItemClickListener());

        m_chooseLengthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                LENGTH_CHOICES);
        m_chooseLengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_chooseLengthSpinner = (Spinner)findViewById(R.id.choose_length);
        m_chooseLengthSpinner.setAdapter(m_chooseLengthAdapter);
        m_chooseLengthSpinner.setOnItemSelectedListener(new ChooseLengthOnItemSelectedListener());
        m_chooseLengthSpinner.setSelection(3); // 3 == 30 minutes
        m_chooseLengthItemPosition = -1;

    }

    private String[] stringsFromStringIds(int[] stringIds)
    {
        String[] strings = new String[stringIds.length];
        
        for (int i = 0; i < stringIds.length; i++) {
            strings[i] = getString(stringIds[i]);
        }
        
        return strings;
    }
    
    // TODO: define this as a string array by XML?
    private static final int[] DAYSETS_STRING_IDS = new int[] {
        R.string.everyday, R.string.some_days, R.string.weekdays, R.string.weekends
    };
    
    // TODO: define this as a string array by XML?
    private static final int[] DAYS_STRING_IDS = new int[] {
        R.string.Monday, R.string.Tuesday, R.string.Wednesday, 
        R.string.Thursday, R.string.Friday,
        R.string.Saturday, R.string.Sunday
    };

    private static final String[] TIME_CHOICES = new String[] {
        "1:00am", "2:00am", "11:59pm", "3:00am", 
        "23:45am", "no such time", "25:01"
    };

    // TODO: define this as a string array
    //       use ALT values (or equivalent) for the numerical values
    private static final String[] LENGTH_CHOICES = new String[] {
        "10 mins", "15 mins", "20 mins", "30 mins", "45 mins", "60 mins", "heart's content"
    };
    
    private static final String SAME_TIME_DEFAULT = "01:23am";

    private RelativeLayout m_timeView;
    
    private Spinner m_setOfDaysSpinner;
    private ArrayAdapter<String> m_setOfDaysAdapter;
    private ListView m_daysView;
    private ArrayAdapter<String> m_daysAdapter;
    
    private CheckBox m_sameTimeCheckBox;
    private String[] m_times;
    private ArrayAdapter<String> m_timesAdapter;
    private ListView m_timesView;
    private TextView m_sameTimeDisplay;
    private TimePicker m_timePicker;
    private int m_pickTimeItemPosition;

    private CheckBox m_sameLengthCheckBox;
    private String[] m_lengths;
    private ArrayAdapter<String> m_lengthsAdapter;
    private ListView m_lengthsView;
    private Spinner m_chooseLengthSpinner;
    private ArrayAdapter<String> m_chooseLengthAdapter;
    private int m_chooseLengthItemPosition;
    
    public class SetOfDaysOnItemSelectedListener implements OnItemSelectedListener
    {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
        {
            switch (DAYSETS_STRING_IDS[pos]) {
            case R.string.everyday :
                // OK. hard coding the number of days in a week!
                for (int itemPos = 0; itemPos < 7; itemPos++)
                    m_daysView.setItemChecked(itemPos, true);
                break;
            case R.string.some_days :
                // do nothing
                break;
            case R.string.weekdays :
                // well only 5 days
                for (int itemPos = 0; itemPos < 7; itemPos++)
                    m_daysView.setItemChecked(itemPos, itemPos < 5 ? true : false);
                break;
            case R.string.weekends :
                for (int itemPos = 0; itemPos < 7; itemPos++)
                    m_daysView.setItemChecked(itemPos, itemPos < 5 ? false : true);
                break;
            default :
                Log.i(getClass().getName(), "impossible: an unknown item for day sets was selected!");
                break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView parent) { }
    }

    public class DaysOnItemClickListener implements OnItemClickListener
    {
        @Override
        public void onItemClick (AdapterView<?> parent, View view, int position, long id)
        {
            SparseBooleanArray sba = m_daysView.getCheckedItemPositions();
            
            boolean allFirst5 = sba.get(0) && sba.get(1) && sba.get(2) && sba.get(3) && sba.get(4);
            boolean noneFirst5 = !(sba.get(0) || sba.get(1) || sba.get(2) || sba.get(3) || sba.get(4));
            boolean bothLast2 = sba.get(5) && sba.get(6);
            boolean neitherLast2 = !(sba.get(5) || sba.get(6));
            boolean everyday = allFirst5 && bothLast2;
            boolean weekdays = allFirst5 && neitherLast2;
            boolean weekends = noneFirst5 && bothLast2;
            
            if (everyday)
                m_setOfDaysSpinner.setSelection(0, true);
            else if (weekdays)
                m_setOfDaysSpinner.setSelection(2, true);
            else if (weekends)
                m_setOfDaysSpinner.setSelection(3, true);
            else // some days
                m_setOfDaysSpinner.setSelection(1, true);
        }
    }

    public class TimesOnItemClickListener implements OnItemClickListener
    {
        @Override
        public void onItemClick (AdapterView<?> parent, View view, int position, long id)
        {
            m_pickTimeItemPosition = position;
            m_timesView.setVisibility(View.INVISIBLE);
            m_sameTimeDisplay.setVisibility(View.VISIBLE);
        }
    }

    
    public class LengthsOnItemClickListener implements OnItemClickListener
    {
        @Override
        public void onItemClick (AdapterView<?> parent, View view, int position, long id)
        {
            m_chooseLengthItemPosition = position;
            m_chooseLengthSpinner.performClick();
        }
    }
    
    public class ChooseLengthOnItemSelectedListener implements OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
        {
            // get text and set the item in lengths to the text
            if (m_chooseLengthItemPosition != -1) {
                m_lengths[m_chooseLengthItemPosition] = LENGTH_CHOICES[pos];
                m_lengthsAdapter.notifyDataSetChanged();
                m_chooseLengthItemPosition = -1;
            }
        }

        public void onNothingSelected(AdapterView parent) { }
    }
    
    
}



/* TODO
 * [ ] Use TimePicker
 * [ ] Content provider for Dot5Activity
 */
