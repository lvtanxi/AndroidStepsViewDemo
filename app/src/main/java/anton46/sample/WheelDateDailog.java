package anton46.sample;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import anton46.sample.timepicker.CollectionWheelAdapter;
import anton46.sample.timepicker.OnWheelChangedListener;
import anton46.sample.timepicker.WheelView;

/**
 * User: 吕勇
 * Date: 2016-08-19
 * Time: 08:28
 * Description:
 */
public class WheelDateDailog extends Dialog implements OnWheelChangedListener, View.OnClickListener {
    private WheelView mWheelYear;
    private WheelView mWheelMonth;
    private WheelView mWheelDay;
    private int actualmaximum;
    private int dayIndex;
    private Button mChooseTimeCancel;
    private Button mChooseTimeConfirm;
    private WheelDateDailogListener mDateDailogListener;

    private int minYear = 1940;


    public WheelDateDailog(Context context) {
        super(context, R.style.def_alert_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_date_picker);
        fullWindow();
        assignViews();
        initData();
        bindListener();
    }

    private void bindListener() {
        mWheelYear.addChangingListener(this);
        mWheelMonth.addChangingListener(this);
        mWheelDay.addChangingListener(this);
        mChooseTimeCancel.setOnClickListener(this);
        mChooseTimeConfirm.setOnClickListener(this);
    }

    private void initData() {
        mWheelYear.setAdapter(new CollectionWheelAdapter<>(getYears()));
        mWheelMonth.setAdapter(new CollectionWheelAdapter<>(getMonths()));
        mWheelMonth.setCyclic(true);
        mWheelDay.setAdapter(new CollectionWheelAdapter<>(getDays()));
        mWheelDay.setCyclic(true);

    }

    private List<String> getDays() {
        Calendar calendar = Calendar.getInstance();
        return getDays(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
    }


    private List<String> getDays(int cyear, int cmonth) {
        List<String> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, cyear);
        calendar.set(Calendar.MONTH, cmonth);
        actualmaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= actualmaximum; i++) {
            days.add(format2LenStr(i, "日"));
        }
        return days;
    }


    private List<String> getMonths() {
        List<String> months = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            months.add(format2LenStr(i, "月"));
        }
        return months;
    }


    private void assignViews() {
        mWheelYear = (WheelView) findViewById(R.id.wheel_year);
        mWheelMonth = (WheelView) findViewById(R.id.wheel_month);
        mWheelDay = (WheelView) findViewById(R.id.wheel_day);
        mChooseTimeCancel = (Button) findViewById(R.id.choose_time_cancel);
        mChooseTimeConfirm = (Button) findViewById(R.id.choose_time_confirm);
    }

    private List<String> getYears() {
        List<String> years = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int nowYear = calendar.get(Calendar.YEAR);
        for (int i = minYear; i <= nowYear; i++) {
            years.add(i + "年");
        }
        return years;
    }

    public String format2LenStr(int num, String unit) {
        return ((num < 10) ? "0" + num : String.valueOf(num)) + unit;
    }

    public void fullWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = Gravity.BOTTOM;
        window.setAttributes(attributes);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel.getId() != R.id.wheel_day) {
            String cuYeay = mWheelYear.getAdapter().getItem(mWheelYear.getCurrentItem()).replace("年", "");
            String cuMonth = mWheelMonth.getAdapter().getItem(mWheelMonth.getCurrentItem()).replace("月", "");
            mWheelDay.getAdapter().bindData(getDays(Integer.valueOf(cuYeay), Integer.valueOf(cuMonth) - 1));
            dayIndex = dayIndex >= actualmaximum ? actualmaximum - 1 : dayIndex;
            mWheelDay.setCurrentItem(dayIndex);
            mWheelDay.notifyDataChange();
            return;
        }
        dayIndex = mWheelDay.getCurrentItem();
    }


    public WheelDateDailog setMinYear(int minYear) {
        this.minYear = minYear;
        return this;
    }


    public WheelDateDailog setDateDailogListener(WheelDateDailogListener dateDailogListener) {
        mDateDailogListener = dateDailogListener;
        return this;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.choose_time_confirm && mDateDailogListener != null) {
            mDateDailogListener.onResult(">>>>>>>>>>>>>");
        }
        dismiss();
    }

    public interface WheelDateDailogListener {
        void onResult(String result);
    }

}
