package github.everett.li;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private List<Entry<String, Period>> mList = new ArrayList<Entry<String, Period>>();

    private ListView mMyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMyListView = (ListView) findViewById(R.id.MyListView);
        final StableArrayAdapter adapter = new StableArrayAdapter(this, mList);
        mMyListView.setAdapter(adapter);

        final MediaPlayerTestTask test1 = new MediaPlayerTestTask(getApplicationContext());
        final AudioRecorderTestTask test2 = new AudioRecorderTestTask();
        new Thread(new Runnable() {

            @Override
            public void run() {
                test2.run();
                try {
                    final List<Entry<String, Period>> list2 = test2.get();
                    mList.addAll(list2);
                    runOnUiThread(new Runnable() {
                        
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                test1.run();
                try {
                    final List<Entry<String, Period>> list1 = test1.get();
                    mList.addAll(list1);
                    runOnUiThread(new Runnable() {
                        
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    static class ViewHolder {
        public TextView key;
        public TextView period;
        public TextView start;
        public TextView end;
    }

    @SuppressWarnings("rawtypes")
    private class StableArrayAdapter extends ArrayAdapter {

        private final Context mContext;
        private final List<Entry<String, Period>> mList;

        @SuppressWarnings("unchecked")
        public StableArrayAdapter(Context context, List<Entry<String, Period>> list) {
            super(context, R.layout.rowlayout, list);
            mContext = context;
            mList = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
               rowView = inflater.inflate(R.layout.rowlayout, null);
               ViewHolder viewHolder = new ViewHolder();
               viewHolder.key = (TextView)rowView.findViewById(R.id.key);
               viewHolder.period = (TextView)rowView.findViewById(R.id.period);
               viewHolder.start = (TextView)rowView.findViewById(R.id.start);
               viewHolder.end = (TextView)rowView.findViewById(R.id.end);
               rowView.setTag(viewHolder);
            }
            ViewHolder holder = (ViewHolder) rowView.getTag();
            Entry<String, Period> entry = mList.get(position);
            String key = entry.getKey();
            holder.key.setText(key);
            holder.period.setText(entry.getValue().getPeriod()+"ms");
            holder.start.setText("start:"+entry.getValue().getStartTs());
            holder.end.setText("end:"+entry.getValue().getEndTs());
            return rowView;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
