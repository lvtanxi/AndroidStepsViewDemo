package anton46.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anton46.stepsview.StepsView;

import anton46.sample.view.popwindow.DatePickerDialog;


public class MainActivity extends AppCompatActivity {

    private final String[] views = {"View 1", "View 2", "View 3", "View 4", "View 5", "View 6",
            "View 7", "View 8", "View 9", "View 10", "View 11", "View 12"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView mListView = (ListView) findViewById(R.id.list);

        MyAdapter adapter = new MyAdapter(this, 0);
        adapter.addAll(views);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new ListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position%2==0){
                    new DatePickerDialog.Builder(MainActivity.this)
                            .onDatePickedListener(new DatePickerDialog.OnDatePickedListener() {
                                @Override
                                public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                                    Toast.makeText(MainActivity.this, dateDesc, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .minYear(2016) //min year in loop
                            .build()
                            .show();
                    return;
                }
                     DateDialog dateDialog=new DateDialog(MainActivity.this);
                dateDialog.show();
            }
        });
    }

    public static class MyAdapter extends ArrayAdapter<String> {

        private final String[] labels = {"Step 1", "Step 2", "Step 3", "Step 4", "Step 5"};

        public MyAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.mLabel.setText(getItem(position));

            holder.mStepsView.setCompletedPosition(position % labels.length)
                    .setLabels(labels)
                    .drawView();

            return convertView;
        }

        static class ViewHolder {

            TextView mLabel;
            StepsView mStepsView;

            public ViewHolder(View view) {
                mLabel = (TextView) view.findViewById(R.id.label);
                mStepsView = (StepsView) view.findViewById(R.id.stepsView);
            }
        }
    }

}