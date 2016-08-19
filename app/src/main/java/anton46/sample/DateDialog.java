package anton46.sample;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.view.View.GONE;


public class DateDialog extends Dialog implements View.OnClickListener {
    private static final String deFormat = "yyyy年MM月dd日";
    private NumberPicker yearPicker, monthPicker, dayPicker;
    private Calendar cal;
    private int year;
    private int month;
    private int day;
    private int yearIndex;
    private int dayIndex;
    private int count = 0;
    private int minYear=1940;
    private TimeDialogCancelBack cancelBack;
    private TimeDialogConfirmBack confirmBack;
    private Button cancel, confirm;
    private boolean hideDayPicker = false;
    private Context mContext;

    public DateDialog(Context context) {
        super(context, R.style.def_alert_dialog);
        mContext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_data_dialog);
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.width =getDisplayMetrics();  //宽度设置为全屏
        getWindow().setAttributes(p);
        initView();
        binaData();
        setListener();
    }
    public  int getDisplayMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private void setListener() {
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        if (hideDayPicker) return;
        yearPicker.setOnSelectListener(new NumberPicker.OnSelectListener() {

            @Override
            public void selecting(int id, String text) {

            }

            @Override
            public void endSelect(int id, String text) {
                int selectedY = Integer.valueOf(text.replace("年", ""));
                if (year != selectedY) {
                    year = selectedY;
                    changeDays();
                }
            }
        });
        monthPicker.setOnSelectListener(new NumberPicker.OnSelectListener() {

            @Override
            public void selecting(int id, String text) {
            }

            @Override
            public void endSelect(int index, String text) {
                if (month != index) {
                    month = index;
                    changeDays();
                }
            }
        });
        dayPicker.setOnSelectListener(new NumberPicker.OnSelectListener() {

            @Override
            public void selecting(int id, String text) {
            }

            @Override
            public void endSelect(int id, String text) {
                dayIndex = id;
            }
        });

    }

    private void changeDays(){
        dayPicker.setData(getDays(year, month));
        dayIndex = 0 == dayIndex ? day - 1 : dayIndex;
        dayIndex = dayIndex >= count ? count - 1 : dayIndex;
        dayPicker.setDefault(dayIndex);
    }

    private void binaData() {
        yearPicker.setData(getYears());
        yearPicker.setDefault(yearIndex);
        monthPicker.setData(getMonths());
        monthPicker.setDefault(month - 1);
        if (!hideDayPicker) {
            dayPicker.setData(getDays(year,month-1));
            dayPicker.setDefault(day - 1);
        }
    }
    private List<String> getDays(int cyear, int cmonth) {
        List<String> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,cyear);
        calendar.set(Calendar.MONTH, cmonth);
        //get max day in month
        count= calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < count; i++) {
            days.add(format2LenStr(i + 1, "日"));
        }
        return days;
    }

    public  String format2LenStr(int num, String unit) {
        return ((num < 10) ? "0" + num : String.valueOf(num)) + unit;
    }

    private List<String> getMonths() {
        List<String> months = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            months.add(format2LenStr(i,"月"));
        }
        return months;
    }

    private List<String> getYears() {
        List<String> years = new ArrayList<>();
        int nowYear = cal.get(Calendar.YEAR);
        boolean flag = true;
        for (int i = minYear; i <= nowYear; i++) {
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
        yearPicker = (NumberPicker) findViewById(R.id.choose_year);
        monthPicker = (NumberPicker) findViewById(R.id.choose_month);
        dayPicker = (NumberPicker) findViewById(R.id.choose_day);
        if (hideDayPicker) {
            dayPicker.setVisibility(GONE);
        }
        cancel = (Button) findViewById(R.id.choose_time_cancel);
        confirm = (Button) findViewById(R.id.choose_time_confirm);
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
         void cancelBack();
    }

    public interface TimeDialogConfirmBack {
         void confirmBack(DateDialog dataDialog);
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

    public DateDialog setMinYear(int minYear) {
        this.minYear = minYear;
        return this;
    }
}