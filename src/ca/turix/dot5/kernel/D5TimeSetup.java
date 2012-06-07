package ca.turix.dot5.kernel;

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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import ca.turix.dot5.R;
import ca.turix.dot5.R.array;
import ca.turix.dot5.R.id;
import ca.turix.dot5.R.integer;
import ca.turix.dot5.R.layout;
import ca.turix.dot5.R.string;
import ca.turix.utils.time.LengthOfTime;
import ca.turix.utils.time.TimeOfDay;
import ca.turix.widgets.TimePickerPopup;

// TODO: debug!

// TODO: Content provider for Dot5Activity
//       1. data protocol
//       2. implementation

public class D5TimeSetup extends D5Setup implements OnTimeChangedListener {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.time);

        m_timeView = (RelativeLayout)findViewById(R.id.time);
        
        m_setOfDaysAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, 
                getResources().getStringArray(R.array.day_sets));
        m_setOfDaysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_setOfDaysSpinner = (Spinner)findViewById(R.id.set_of_days);
        m_setOfDaysSpinner.setAdapter(m_setOfDaysAdapter);
        m_setOfDaysSpinner.setOnItemSelectedListener(new SetOfDaysSpinnerOnItemSelectedListener());

        m_daysAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,
                getResources().getStringArray(R.array.days));
        m_daysView = (ListView)findViewById(R.id.days);
        m_daysView.setItemsCanFocus(false);
        m_daysView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        m_daysView.setAdapter(m_daysAdapter);
        m_daysView.setOnItemClickListener(new DaysOnItemClickListener());

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
        
        m_times = new TimeOfDay[7];
        int defaultTimeOfDay = getResources().getInteger(R.integer.defaultTimeOfDay);
        for (int i = 0; i < 7; i++)
            m_times[i] = new TimeOfDay(defaultTimeOfDay);
        m_timesAdapter = new ArrayAdapter<TimeOfDay>(this, android.R.layout.simple_list_item_1, m_times);
        m_timesView = (ListView)findViewById(R.id.times);
        m_timesView.setItemsCanFocus(false);
        m_timesView.setAdapter(m_timesAdapter);
        m_timesView.setOnItemClickListener(new TimesOnItemClickListener());

        int sameTime = getResources().getInteger(R.integer.defaultTimeOfDay);
        m_timePickerItemPosition = -1;
        m_timePickerPopup = new TimePickerPopup(m_timeView, sameTime, false, this);
        m_sameTime = new TimeOfDay(sameTime);
        m_sameTimeDisplay = (TextView)findViewById(R.id.same_time_display);
        m_sameTimeDisplay.setText(m_sameTime.toString());
        m_sameTimeDisplay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_timePickerPopup.setTime(m_sameTime.getTime());
                m_timePickerPopup.show();
            }
        });
        
        m_sameLengthCheckBox = (CheckBox)findViewById(R.id.same_length);
        m_sameLengthCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) {
                    m_lengthsView.setVisibility(View.INVISIBLE);
                    m_isSelectingForSameLength = true;
                    m_lengthSpinner.setSelection(m_sameLengthSelection);
                    m_lengthSpinner.setVisibility(View.VISIBLE);
                } else {
                    m_isSelectingForSameLength = false;
                    m_lengthSpinner.setVisibility(View.INVISIBLE);
                    m_lengthsView.setVisibility(View.VISIBLE);
                }
            }
        });
        m_isSelectingForSameLength = true; // default
        
        int[] lengthChoicesInMinutes = getResources().getIntArray(R.array.lengthOptions);
        int count = lengthChoicesInMinutes.length;
        m_lengthOptions = new LengthOfTime[count];
        for (int i = 0; i < count; i++)
            m_lengthOptions[i] = new LengthOfTime(lengthChoicesInMinutes[i], getString(R.string.heartsContent));
        m_lengthSelections = new int[7];
        m_selectedLengths = new LengthOfTime[7];
        int defaultLengthIndex = getResources().getInteger(R.integer.defaultLengthIndex);
        for (int i = 0; i < 7; i++) {
            m_lengthSelections[i] = defaultLengthIndex;
            m_selectedLengths[i] = m_lengthOptions[defaultLengthIndex];
        }
        m_sameLengthSelection = defaultLengthIndex;
        
        m_lengthsAdapter = new ArrayAdapter<LengthOfTime>(this, android.R.layout.simple_list_item_1, m_selectedLengths);
        m_lengthsView = (ListView)findViewById(R.id.lengths);
        m_lengthsView.setItemsCanFocus(false);
        m_lengthsView.setAdapter(m_lengthsAdapter);
        m_lengthsView.setOnItemClickListener(new LengthsOnItemClickListener());

        m_chooseLengthAdapter = new ArrayAdapter<LengthOfTime>(this, android.R.layout.simple_spinner_item,
                m_lengthOptions);
        m_chooseLengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_lengthSpinner = (Spinner)findViewById(R.id.choose_length);
        m_lengthSpinner.setAdapter(m_chooseLengthAdapter);
        m_lengthSpinner.setOnItemSelectedListener(new LengthSpinnerOnItemSelectedListener());
        m_lengthSpinner.setSelection(defaultLengthIndex);
        m_lengthSelectionsItemPosition = -1;
    }

    private final static int[] DAYSETS_STRING_IDS = new int[] {
        R.string.everyday, R.string.some_days, R.string.weekdays, R.string.weekends
    };
    
    private LengthOfTime[] m_lengthOptions;
    private RelativeLayout m_timeView;
    
    private Spinner m_setOfDaysSpinner;
    private ArrayAdapter<String> m_setOfDaysAdapter;
    private ListView m_daysView;
    private ArrayAdapter<String> m_daysAdapter;
    
    private CheckBox m_sameTimeCheckBox;
    private TimeOfDay[] m_times;
    private ArrayAdapter<TimeOfDay> m_timesAdapter;
    private ListView m_timesView;
    private TimePickerPopup m_timePickerPopup;
    private int m_timePickerItemPosition;
    private TextView m_sameTimeDisplay;
    private TimeOfDay m_sameTime;

    private CheckBox m_sameLengthCheckBox;
    private int[] m_lengthSelections;
    private LengthOfTime[] m_selectedLengths;
    private int m_sameLengthSelection;
    private ArrayAdapter<LengthOfTime> m_lengthsAdapter;
    private ListView m_lengthsView;
    private Spinner m_lengthSpinner;
    private ArrayAdapter<LengthOfTime> m_chooseLengthAdapter;
    private int m_lengthSelectionsItemPosition;
    private boolean m_isSelectingForSameLength;
    
    public class SetOfDaysSpinnerOnItemSelectedListener implements OnItemSelectedListener
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
        public void onNothingSelected(AdapterView<?> parent) { }
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
            m_timePickerItemPosition = position;
            m_timePickerPopup.setTime(m_times[m_timePickerItemPosition].getTime());
            m_timePickerPopup.show();
        }
    }

    public class LengthsOnItemClickListener implements OnItemClickListener
    {
        @Override
        public void onItemClick (AdapterView<?> parent, View view, int position, long id)
        {
            m_lengthSpinner.setSelection(m_lengthSelections[position]);
            m_lengthSelectionsItemPosition = position;
            m_lengthSpinner.performClick();
        }
    }
    
    public class LengthSpinnerOnItemSelectedListener implements OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
        {
            if (m_isSelectingForSameLength) {
                m_sameLengthSelection = pos;
            } else {
                m_lengthSelections[m_lengthSelectionsItemPosition] = pos;
                m_selectedLengths[m_lengthSelectionsItemPosition] = m_lengthOptions[pos];
                m_lengthsAdapter.notifyDataSetChanged();
            }
        }

        public void onNothingSelected(AdapterView<?> parent) { }
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
    {
        if (m_timePickerItemPosition != -1) {
            m_times[m_timePickerItemPosition].setTime(TimeOfDay.timeOfDayInMinutes(hourOfDay, minute));
            m_timesAdapter.notifyDataSetChanged();
        } else {
            m_sameTime.setTime(TimeOfDay.timeOfDayInMinutes(hourOfDay, minute));
            m_sameTimeDisplay.setText(m_sameTime.toString());
        }
    }

}
