package anton46.sample;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DateDialog extends Dialog implements View.OnClickListener {
    private static final String deFormat = "yyyy年MM月dd日";
    private Context context;
    private ScrollerNumberPicker yearPicker, monthPicker, dayPicker;
    private Calendar cal;
    private String months_big = "135781012";
    private int year;
    private int month;
    private int day;
    private int yearIndex;
    private int dayIndex;
    private int count = 0;
    private TimeDialogCancelBack cancelBack;
    private TimeDialogConfirmBack confirmBack;
    private Button cancel, confirm;
    private boolean hideDayPicker = false;

    public DateDialog(Context context) {
        super(context, R.style.def_alert_dialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        binaData();
        setListener();
    }

    private void setListener() {
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        if (hideDayPicker) return;
        yearPicker.setOnSelectListener(new ScrollerNumberPicker.OnSelectListener() {

            @Override
            public void selecting(int id, String text) {

            }

            @Override
            public void endSelect(int id, String text) {
                int selectedY = Integer.valueOf(text.replace("年", ""));
                if (year != selectedY) {
                    year = selectedY;
                    dayPicker.setData(getDays(selectedY, String.valueOf(month)));
                }
            }
        });
        monthPicker.setOnSelectListener(new ScrollerNumberPicker.OnSelectListener() {

            @Override
            public void selecting(int id, String text) {
            }

            @Override
            public void endSelect(int id, String text) {
                int selectedM = Integer.valueOf(text.replace("月", ""));
                if (month != selectedM) {
                    month = selectedM;
                    dayPicker.setData(getDays(year, String.valueOf(month)));
                    dayIndex = 0 == dayIndex ? day - 1 : dayIndex;
                    dayIndex = dayIndex >= count ? count - 1 : dayIndex;
                    dayPicker.setDefault(dayIndex);
                }
            }
        });
        dayPicker.setOnSelectListener(new ScrollerNumberPicker.OnSelectListener() {

            @Override
            public void selecting(int id, String text) {
            }

            @Override
            public void endSelect(int id, String text) {
                dayIndex = id;
            }
        });

    }

    private void binaData() {
        yearPicker.setData(getYears());
        yearPicker.setDefault(yearIndex);
        monthPicker.setData(getMonths());
        monthPicker.setDefault(month - 1);
        if (!hideDayPicker) {
            dayPicker.setData(getDays(year, String.valueOf(month)));
            dayPicker.setDefault(day - 1);
        }
    }

    private List<String> getDays(int cyear, String cmonth) {
        List<String> days = new ArrayList<>();
        if ("2".equals(cmonth)) {
            // 闰年
            if ((cyear % 4 == 0 && cyear % 100 != 0) || cyear % 400 == 0)
                count = 29;
            else
                count = 28;
        } else if (months_big.contains(cmonth)) {
            count = 31;
        } else {
            count = 30;
        }
        for (int i = 1; i < 10; i++) {
            days.add("0" + i + "日");
        }
        for (int i = 10; i <= count; i++) {
            days.add(i + "日");
        }
        return days;
    }

    private List<String> getMonths() {
        List<String> months = new ArrayList<String>();
        for (int i = 1; i < 10; i++) {
            months.add("0" + i + "月");
        }
        for (int i = 10; i <= 12; i++) {
            months.add(i + "月");
        }
        return months;
    }

    private List<String> getYears() {
        List<String> years = new ArrayList<String>();
        int nowYear = cal.get(Calendar.YEAR);
        boolean flag = true;
        for (int i = 1940; i <= nowYear; i++) {
            if (year != i && flag) {
                yearIndex++;
            } else {
                flag = false;
            }
            years.add(i + "年");
        }
        return years;
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_data_dialog, null);
        yearPicker = (ScrollerNumberPicker) view.findViewById(R.id.choose_year);
        monthPicker = (ScrollerNumberPicker) view.findViewById(R.id.choose_month);
        dayPicker = (ScrollerNumberPicker) view.findViewById(R.id.choose_day);
        if (hideDayPicker) {
            dayPicker.setVisibility(View.GONE);
        }
        cancel = (Button) view.findViewById(R.id.choose_time_cancel);
        confirm = (Button) view.findViewById(R.id.choose_time_confirm);
        setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        cal = Calendar.getInstance();
        year = 0 == year ? cal.get(Calendar.YEAR) : year;
        month = 0 == month ? cal.get(Calendar.MONTH) + 1 : month;
        day = 0 == day ? cal.get(Calendar.DAY_OF_MONTH) : day;
    }

    public String resultTime() {
        StringBuffer buffer = new StringBuffer(yearPicker.getSelectedText());
        buffer.append(monthPicker.getSelectedText());
        if (!hideDayPicker) {
            buffer.append(dayPicker.getSelectedText());
        }
        return buffer.toString();
    }

    public String resultTime(String format) {
        try {
            String resultTime = resultTime();
            if (null == format) {
                return resultTime;
            }
            SimpleDateFormat df = new SimpleDateFormat(deFormat);
            Date date = df.parse(resultTime);
            SimpleDateFormat dfs = new SimpleDateFormat(format);
            return dfs.format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public interface TimeDialogCancelBack {
        public void cancelBack();
    }

    public interface TimeDialogConfirmBack {
        public void confirmBack(DateDialog dataDialog);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.choose_time_cancel){
            if (null != cancelBack)
                cancelBack.cancelBack();
        }else{
            if (null != confirmBack)
                confirmBack.confirmBack(this);
        }
        dismiss();
    }

    public DateDialog setDayGone() {
        hideDayPicker = true;
        return this;
    }

    public String getYearStr() {
        return yearPicker.getSelectedText().replace("年", "");
    }

    public int getYearInt() {
        return Integer.valueOf(getYearStr());
    }

    public String getMonthStr() {
        return monthPicker.getSelectedText().replace("月", "");
    }

    public int getMonthInt() {
        return Integer.valueOf(getMonthStr());
    }

    public String getDayStr() {
        if (hideDayPicker) {
            return "0";
        }
        return dayPicker.getSelectedText().replace("日", "");
    }

    public int getDayhInt() {
        return Integer.valueOf(getDayStr());
    }

    public DateDialog setYear(int year) {
        this.year = year;
        return this;
    }

    public DateDialog setMonth(int month) {
        this.month = month;
        return this;
    }

    public DateDialog setDay(int day) {
        this.day = day;
        return this;
    }

}