package anton46.sample.view.popwindow;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import anton46.sample.R;
import anton46.sample.view.LoopView;

/**
 * User: 吕勇
 * Date: 2016-08-18
 * Time: 09:55
 * Description:
 */
public class DatePickerDialog extends Dialog implements View.OnClickListener {

    private static final int DEFAULT_MIN_YEAR = 1900;
    private static final String DEFAULT_FORMART = "yyyy年-MM月-dd日";
    public Button cancelBtn;
    public Button confirmBtn;
    public LoopView yearLoopView;
    public LoopView monthLoopView;
    public LoopView dayLoopView;
    public View pickerContainer;
    private int yearPos = 0;
    private int monthPos = 0;
    private int dayPos = 0;
    private Builder builder;

    List<String> yearList = new ArrayList();
    List<String> monthList = new ArrayList();
    List<String> dayList = new ArrayList();

    public DatePickerDialog(Context context,Builder builder) {
        super(context, R.style.def_alert_dialog);
        this.builder=builder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_date_picker);
        WindowManager m =builder.mActivity.getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.width =getDisplayMetrics();  //宽度设置为全屏
        getWindow().setAttributes(p);
        initViews();
        initData();
        bindListener();
    }

    public  int getDisplayMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        builder.mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private void initViews() {
        cancelBtn = (Button) findViewById(R.id.btn_cancel);
        confirmBtn = (Button) findViewById(R.id.btn_confirm);
        yearLoopView = (LoopView) findViewById(R.id.picker_year);
        monthLoopView = (LoopView) findViewById(R.id.picker_month);
        dayLoopView = (LoopView) findViewById(R.id.picker_day);
        pickerContainer =findViewById(R.id.container_picker);
    }

    private void bindListener() {

        yearLoopView.setLoopListener(new LoopView.LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                yearPos = item;
                initDayPickerView();
            }
        });
        monthLoopView.setLoopListener(new LoopView.LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                monthPos = item;
                initDayPickerView();
            }
        });
        dayLoopView.setLoopListener(new LoopView.LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                dayPos = item;
            }
        });
        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        pickerContainer.setOnClickListener(this);
    }

    private void initData() {
        setCanceledOnTouchOutside(true);
        setSelectedDate(builder.dateChose);
        cancelBtn.setText(builder.textCancel);
        cancelBtn.setTextColor(builder.colorCancel);
        cancelBtn.setTextSize(builder.btnTextSize);
        confirmBtn.setTextSize(builder.btnTextSize);
        confirmBtn.setText(builder.textConfirm);
        confirmBtn.setTextColor(builder.colorConfirm);
        yearLoopView.setTextSize(builder.viewTextSize);
        monthLoopView.setTextSize(builder.viewTextSize);
        dayLoopView.setTextSize(builder.viewTextSize);

        initPickerViews(); // init year and month loop view
        initDayPickerView(); //init day loop view
    }
    private void initPickerViews() {

        int yearCount = builder.maxYear - builder.minYear;

        for (int i = 0; i < yearCount; i++) {
            yearList.add(format2LenStr(builder.minYear + i, "年"));
        }

        for (int j = 0; j < 12; j++) {
            monthList.add(format2LenStr(j + 1, "月"));
        }

        yearLoopView.setDataList((ArrayList) yearList);
        yearLoopView.setInitPosition(yearPos);

        monthLoopView.setDataList((ArrayList) monthList);
        monthLoopView.setInitPosition(monthPos);
    }

    private void initDayPickerView() {

        int dayMaxInMonth;
        Calendar calendar = Calendar.getInstance();
        dayList = new ArrayList<>();

        calendar.set(Calendar.YEAR, builder.minYear + yearPos);
        calendar.set(Calendar.MONTH, monthPos);
        dayMaxInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < dayMaxInMonth; i++) {
            dayList.add(format2LenStr(i + 1, "日"));
        }
        dayLoopView.setDataList((ArrayList) dayList);
        dayLoopView.setInitPosition(dayPos);
    }


    public String format2LenStr(int num, String unit) {
        return ((num < 10) ? "0" + num : String.valueOf(num)) + unit;
    }
    public void setSelectedDate(String dateStr) {

        if (!TextUtils.isEmpty(dateStr)) {

            long milliseconds = getLongFromyyyyMMdd(dateStr);
            Calendar calendar = Calendar.getInstance(Locale.CHINA);

            if (milliseconds != -1) {

                calendar.setTimeInMillis(milliseconds);
                yearPos = calendar.get(Calendar.YEAR) - builder.minYear;
                monthPos = calendar.get(Calendar.MONTH);
                dayPos = calendar.get(Calendar.DAY_OF_MONTH) - 1;
            }
        }
    }

    private  long getLongFromyyyyMMdd(String date) {
        SimpleDateFormat mFormat = new SimpleDateFormat(DEFAULT_FORMART, Locale.getDefault());
        Date parse = null;
        try {
            parse = mFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (parse != null) {
            return parse.getTime();
        } else {
            return -1;
        }
    }
    @Override
    public void onClick(View view) {
        if (view== confirmBtn) {
            if (null != builder.mDatePickedListener) {
                int year = builder.minYear + yearPos;
                int month = monthPos + 1;
                int day = dayPos + 1;
                StringBuffer sb = new StringBuffer();

                sb.append(year)
                        .append("年")
                        .append("-")
                        .append(format2LenStr(month, "月"))
                        .append("-")
                        .append(format2LenStr(day, "日"));
                builder.mDatePickedListener.onDatePickCompleted(year, month, day, sb.toString());
            }
        }
        dismiss();
    }

    public static class Builder {
        private Activity mActivity;
        private DatePickerDialog.OnDatePickedListener mDatePickedListener;
        private boolean showDayMonthYear = false;
        private int minYear = DEFAULT_MIN_YEAR;
        private int maxYear = Calendar.getInstance().get(Calendar.YEAR) + 1;
        private String textCancel = "取消";
        private String textConfirm = "确定";
        private String dateChose = getStrDate();
        private int colorCancel = Color.parseColor("#999999");
        private int colorConfirm = Color.parseColor("#303F9F");
        private int btnTextSize = 16;//text btnTextsize of cancel and confirm button
        private int viewTextSize = 25;


        public Builder(Activity activity) {
            this.mActivity = activity;
        }


        public DatePickerDialog.Builder minYear(int minYear) {
            this.minYear = minYear;
            return this;
        }

        public DatePickerDialog.Builder onDatePickedListener(DatePickerDialog.OnDatePickedListener listener) {
            this.mDatePickedListener = listener;
            return this;
        }

        public DatePickerDialog.Builder maxYear(int maxYear) {
            this.maxYear = maxYear;
            return this;
        }

        public DatePickerDialog.Builder textCancel(String textCancel) {
            this.textCancel = textCancel;
            return this;
        }

        public DatePickerDialog.Builder textConfirm(String textConfirm) {
            this.textConfirm = textConfirm;
            return this;
        }

        public DatePickerDialog.Builder dateChose(String dateChose) {
            this.dateChose = dateChose;
            return this;
        }

        public DatePickerDialog.Builder colorCancel(int colorCancel) {
            this.colorCancel = colorCancel;
            return this;
        }

        public DatePickerDialog.Builder colorConfirm(int colorConfirm) {
            this.colorConfirm = colorConfirm;
            return this;
        }

        public DatePickerDialog.Builder btnTextSize(int textSize) {
            this.btnTextSize = textSize;
            return this;
        }

        public DatePickerDialog.Builder viewTextSize(int textSize) {
            this.viewTextSize = textSize;
            return this;
        }

        public DatePickerDialog build() {
            if (minYear > maxYear) {
                throw new IllegalArgumentException();
            }
            return new DatePickerDialog(mActivity,this);
        }

        public DatePickerDialog.Builder showDayMonthYear(boolean useDayMonthYear) {
            this.showDayMonthYear = useDayMonthYear;
            return this;
        }
        public  String getStrDate() {
            SimpleDateFormat dd = new SimpleDateFormat(DEFAULT_FORMART, Locale.CHINA);
            return dd.format(new Date());
        }

    }

    public interface OnDatePickedListener {

        /**
         * Listener when date has been checked
         *
         * @param year
         * @param month
         * @param day
         * @param dateDesc
         */
        void onDatePickCompleted(int year, int month, int day, String dateDesc);
    }
}
