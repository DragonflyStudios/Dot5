package ca.turix.widgets;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import ca.turix.dot5.R;

public class TimePickerPopup
{
    
    public TimePickerPopup(ViewGroup parent, int hourOfDay, int minute, boolean is24HourView, OnTimeChangedListener callBack)
    {
        LayoutInflater inflater =
                (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TimePicker m_timePicker = (TimePicker)inflater.inflate(R.layout.time_picker, parent, false);

        m_timePicker.setIs24HourView(is24HourView);
        m_timePicker.setCurrentHour(hourOfDay);
        m_timePicker.setCurrentMinute(minute);
        m_timePicker.setOnTimeChangedListener(callBack);
        m_timePicker.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        m_popupWindow = new PopupWindow(m_timePicker, m_timePicker.getMeasuredWidth(), m_timePicker.getMeasuredHeight());
        m_popupWindow.setOutsideTouchable(true);
        m_popupWindow.setBackgroundDrawable(new BitmapDrawable());
        m_popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    private PopupWindow m_popupWindow;
    private TimePicker m_timePicker;
}
