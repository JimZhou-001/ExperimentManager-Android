package com.blogspot.jimzhou001.experimentmanager.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.jimzhou001.experimentmanager.Adapter.ColumnAdapter;
import com.blogspot.jimzhou001.experimentmanager.Adapter.NoteAdapter;
import com.blogspot.jimzhou001.experimentmanager.AlarmService;
import com.blogspot.jimzhou001.experimentmanager.Note;
import com.blogspot.jimzhou001.experimentmanager.NoteDatabaseHelper;
import com.blogspot.jimzhou001.experimentmanager.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends Activity {

    private ViewPager columnPager;
    private ArrayList<View> columnViews = new ArrayList<View>();
    private int[] linearLayoutIds = {R.id.experiment, R.id.news, R.id.data, R.id.me};
    private int[] imageViewIds = {R.id.iv_experiment, R.id.iv_news, R.id.iv_data, R.id.iv_me};
    private int[] imageResourceIds = {R.drawable.experiment, R.drawable.news, R.drawable.data, R.drawable.me};
    private int[] imageResourceSelectedIds = {R.drawable.experiment_selected, R.drawable.news_selected, R.drawable.data_selected, R.drawable.me_selected};
    private int[] textViewIds = {R.id.tv_experiment, R.id.tv_news, R.id.tv_data, R.id.tv_me};
    public static NoteDatabaseHelper noteDatabaseHelper;
    public static SQLiteDatabase noteDatabase;
    private static List<Note> notes = new ArrayList<Note>();
    private ListView noteListView;
    private NoteAdapter noteAdapter;
    private Calendar today;
    private View view1;
    private ImageView newswebview_back;
    private ListView newsListView;
    private ProgressBar newsProgressBar;
    private WebView newsWebView;
    private View view2;
    private ImageView webview_back;
    private ListView websites;
    private ProgressBar progressBar;
    private WebView webView;
    private View view3;
    private List<Note> historyNotes = new ArrayList<Note>();
    private ListView lv_history;
    private NoteAdapter historyAdapter;
    private View view4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view1 = View.inflate(MainActivity.this, R.layout.page_experiment, null);
        noteDatabaseHelper = new NoteDatabaseHelper(this, "NewsDataBase.db", null, 1);
        noteDatabase = noteDatabaseHelper.getWritableDatabase();
        ImageView add = (ImageView) view1.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
                intent.putExtra("topTitle", "添加实验安排");
                startActivityForResult(intent, 1);
            }
        });
        noteListView = (ListView) view1.findViewById(R.id.listviewnotes);
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
                intent.putExtra("NotePosition", i);
                intent.putExtra("topTitle", "编辑实验安排");
                startActivityForResult(intent, 2);
            }
        });
        noteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("您要删除这条记录吗？")
                        .setMessage("一旦删除，无法恢复。\n经历无价，谨慎操作！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {
                                noteDatabase.delete("Notes", "millis = ?", new String[] { String.valueOf(notes.get(i).getMillis()) });
                                if (lv_history.getVisibility()==View.VISIBLE&&historyNotes.contains(notes.get(i))) {
                                    historyAdapter.remove(notes.get(i));
                                }
                                noteAdapter.remove(notes.get(i));
                            }
                        })
                        .setNegativeButton("取消", null).show();
                return true;
            }
        });
        today = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));//此时此刻

        //今日零时
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        columnViews.add(view1);

        view2 = View.inflate(MainActivity.this, R.layout.page_news, null);
        newswebview_back = (ImageView)view2.findViewById(R.id.newswebview_back);
        String[] news = { "生物谷", "丁香园", "生物通", "科学网", "生物帮", "生物秀", "生物探索", "绿谷生物网", "中国生物技术信息网", "中国生物技术发展中心" };
        ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, news);
        newsListView = (ListView)view2.findViewById(R.id.listviewnews);
        newsListView.setAdapter(newsAdapter);
        newsProgressBar = (ProgressBar)view2.findViewById(R.id.newsprogressbar);
        newsWebView = (WebView)view2.findViewById(R.id.newswebview);
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                newsListView.setVisibility(View.GONE);
                newswebview_back.setVisibility(View.VISIBLE);
                newswebview_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newswebview_back.setVisibility(View.GONE);
                        newsListView.setVisibility(View.VISIBLE);
                        newsProgressBar.setVisibility(View.GONE);
                        newsWebView.setVisibility(View.GONE);
                    }
                });
                newsProgressBar.setVisibility(View.VISIBLE);
                newsProgressBar.setProgress(0);
                newsWebView.setVisibility(View.VISIBLE);
                newsWebView.getSettings().setJavaScriptEnabled(true);
                newsWebView.setWebViewClient(new WebViewClient());
                newsWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        if (newProgress==100) {
                            newsProgressBar.setVisibility(View.GONE);
                        } else {
                            if (newsProgressBar.getVisibility()==View.GONE) {
                                newsProgressBar.setVisibility(View.VISIBLE);
                            }
                            newsProgressBar.setProgress(newProgress);
                        }
                    }
                });
                switch (i) {
                    case 0:
                        newsWebView.loadUrl("http://www.bioon.com/");
                        break;
                    case 1:
                        newsWebView.loadUrl("http://www.dxy.cn/");
                        break;
                    case 2:
                        newsWebView.loadUrl("http://www.ebiotrade.com/");
                        break;
                    case 3:
                        newsWebView.loadUrl("http://www.sciencenet.cn/");
                        break;
                    case 4:
                        newsWebView.loadUrl("http://www.bio1000.com/");
                        break;
                    case 5:
                        newsWebView.loadUrl("http://www.bbioo.com/");
                        break;
                    case 6:
                        newsWebView.loadUrl("http://www.biodiscover.com/");
                        break;
                    case 7:
                        newsWebView.loadUrl("http://www.ibioo.com/");
                        break;
                    case 8:
                        newsWebView.loadUrl("http://www.biotech.org.cn/");
                        break;
                    case 9:
                        newsWebView.loadUrl("http://www.cncbd.org.cn/");
                        break;
                }
                newsWebView.setDownloadListener(new DownloadListener() {
                    @Override
                    public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                        startActivity(intent);
                    }
                });
            }
        });
        columnViews.add(view2);

        view3 = View.inflate(MainActivity.this, R.layout.page_data, null);
        webview_back = (ImageView)view3.findViewById(R.id.webview_back);
        String[] data = { "微软学术搜索", "NCBI", "Cell", "Nature", "Science", "MedSci", "中国知网" };
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, data);
        websites = (ListView)view3.findViewById(R.id.websites);
        websites.setAdapter(dataAdapter);
        progressBar = (ProgressBar)view3.findViewById(R.id.progressbar);
        webView = (WebView)view3.findViewById(R.id.webview);
        websites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                websites.setVisibility(View.GONE);
                webview_back.setVisibility(View.VISIBLE);
                webview_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        webview_back.setVisibility(View.GONE);
                        websites.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        webView.setVisibility(View.GONE);
                    }
                });
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
                webView.setVisibility(View.VISIBLE);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient());
                webView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        if (newProgress==100) {
                            progressBar.setVisibility(View.GONE);
                        } else {
                            if (progressBar.getVisibility()==View.GONE) {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                            progressBar.setProgress(newProgress);
                        }
                    }
                });
                switch (i) {
                    case 0:
                        webView.loadUrl("http://cn.bing.com/academic?mkt=zh-CN");
                        break;
                    case 1:
                        webView.loadUrl("http://www.ncbi.nlm.nih.gov/");
                        break;
                    case 2:
                        webView.loadUrl("http://www.cell.com/");
                        break;
                    case 3:
                        webView.loadUrl("http://www.nature.com/");
                        break;
                    case 4:
                        webView.loadUrl("http://www.sciencemag.org/");
                        break;
                    case 5:
                        webView.loadUrl("http://www.medsci.cn/sci/");
                        break;
                    case 6:
                        webView.loadUrl("http://cnki.net/");
                        break;
                }
                webView.setDownloadListener(new DownloadListener() {
                    @Override
                    public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                        startActivity(intent);
                    }
                });
            }
        });
        columnViews.add(view3);

        view4 = View.inflate(MainActivity.this, R.layout.page_me, null);
        final TextView history = (TextView)view4.findViewById(R.id.history);
        historyAdapter = new NoteAdapter(MainActivity.this, R.layout.item_note, historyNotes);
        lv_history = (ListView)view4.findViewById(R.id.lv_history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lv_history.getVisibility()==View.GONE) {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.durationdialog, null);
                    Date date = new Date();
                    final EditText year1 = (EditText)layout.findViewById(R.id.year1);
                    year1.setText(date.getYear()+1900 + "");
                    final EditText month1 = (EditText)layout.findViewById(R.id.month1);
                    month1.setText(date.getMonth()+1 + "");
                    final EditText year2 = (EditText)layout.findViewById(R.id.year2);
                    year2.setText(date.getYear()+1900 + "");
                    final EditText month2 = (EditText)layout.findViewById(R.id.month2);
                    month2.setText(date.getMonth()+1 + "");
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("请输入时间段")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(layout)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    historyNotes.clear();
                                    Date startDate = new Date(Integer.parseInt(year1.getText().toString())-1900, Integer.parseInt(month1.getText().toString())-1, 1);
                                    Date endDate = new Date(Integer.parseInt(year2.getText().toString())-1900, Integer.parseInt(month2.getText().toString()), 1);
                                    Cursor cursor = noteDatabase.query("Notes", null, "millis>=" + startDate.getTime() + " and millis<" + endDate.getTime(), null, null, null, "millis");
                                    if (cursor.moveToFirst()) {
                                        do {
                                            String title = cursor.getString(cursor.getColumnIndex("title"));
                                            String date = cursor.getString(cursor.getColumnIndex("date"));
                                            String time = cursor.getString(cursor.getColumnIndex("time"));
                                            long millis = cursor.getLong(cursor.getColumnIndex("millis"));
                                            Note note = new Note(title, date, time, millis);
                                            historyNotes.add(note);
                                        } while (cursor.moveToNext());
                                        lv_history.setAdapter(historyAdapter);
                                        lv_history.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(MainActivity.this, "该时间段内没有记录哦！", Toast.LENGTH_SHORT).show();
                                    }
                                    cursor.close();
                                }
                            })
                            .setNegativeButton("取消", null).show();
                } else {
                    lv_history.setVisibility(View.GONE);
                }
            }
        });
        lv_history.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("您要删除这条记录吗？")
                        .setMessage("一旦删除，无法恢复。\n经历无价，谨慎操作！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {
                                noteDatabase.delete("Notes", "millis = ?", new String[] { String.valueOf(historyNotes.get(i).getMillis()) });
                                if (notes.contains(historyNotes.get(i))) {
                                    noteAdapter.remove(historyNotes.get(i));
                                }
                                historyAdapter.remove(historyNotes.get(i));
                                if (historyAdapter.isEmpty()) {
                                    lv_history.setVisibility(View.GONE);
                                }
                            }
                        })
                        .setNegativeButton("取消", null).show();
                return true;
            }
        });
        columnViews.add(view4);

        columnPager = (ViewPager) findViewById(R.id.column_pager);
        columnPager.setAdapter(new ColumnAdapter(columnViews));
        setDefaultColor();
        ((ImageView) findViewById(imageViewIds[0])).setImageResource(imageResourceSelectedIds[0]);
        ((TextView) findViewById(textViewIds[0])).setTextColor(0xffffd700);

        for (int id : linearLayoutIds) {
            ((LinearLayout) findViewById(id)).setOnClickListener(new ClickListener());
        }

        columnPager.setOnPageChangeListener(new ChangeListener());

        Intent intent = new Intent(MainActivity.this, AlarmService.class);
        startService(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(getIntent().getIntExtra("id", 0));

        notes.clear();
        Cursor cursor = noteDatabase.query("Notes", null, "millis>=" + today.getTimeInMillis(), null, null, null, "millis");

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                long millis = cursor.getLong(cursor.getColumnIndex("millis"));
                Note tempNote = new Note(title, date, time, millis);
                notes.add(tempNote);
            } while (cursor.moveToNext());
        }
        cursor.close();

        noteAdapter = new NoteAdapter(this, R.layout.item_note, notes);
        noteListView.setAdapter(noteAdapter);

    }

    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            setDefaultColor();
            switch (view.getId()) {
                case R.id.experiment:
                    columnPager.setCurrentItem(0);
                    break;
                case R.id.news:
                    columnPager.setCurrentItem(1);
                    break;
                case R.id.data:
                    columnPager.setCurrentItem(2);
                    break;
                case R.id.me:
                    columnPager.setCurrentItem(3);
                    break;
            }
        }
    }

    class ChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setDefaultColor();
            ((ImageView) findViewById(imageViewIds[position])).setImageResource(imageResourceSelectedIds[position]);
            ((TextView) findViewById(textViewIds[position])).setTextColor(0xffffd700);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void setDefaultColor() {
        int i = 0;
        for (int id : imageViewIds) {
            ((ImageView) findViewById(id)).setImageResource(imageResourceIds[i++]);
        }
        for (int id : textViewIds) {
            ((TextView) findViewById(id)).setTextColor(Color.WHITE);
        }
    }

    @Override
    public void onBackPressed() {
        if (columnPager.getCurrentItem()==1&&newsListView.getVisibility()==View.GONE) {
            if (newsWebView.canGoBack()) {
                newsProgressBar.setVisibility(View.VISIBLE);
                newsProgressBar.setProgress(0);
                newsWebView.goBack();
            } else {
                newswebview_back.setVisibility(View.GONE);
                newsListView.setVisibility(View.VISIBLE);
                newsProgressBar.setVisibility(View.GONE);
                newsWebView.setVisibility(View.GONE);
            }
        } else if (columnPager.getCurrentItem()==2&&websites.getVisibility()==View.GONE) {
            if (webView.canGoBack()) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
                webView.goBack();
            } else {
                webview_back.setVisibility(View.GONE);
                websites.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    public static Note getNote(int position) {
        return notes.get(position);
    }

    public static int getSize() {
        return notes.size();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int notePosition = data.getIntExtra("notePosition", 0);
        String title = data.getStringExtra("title");
        String date = data.getStringExtra("date");
        String time = data.getStringExtra("time");
        long millis = data.getLongExtra("millis", System.currentTimeMillis());
        switch (requestCode) {
            case 1:
                if (requestCode == RESULT_OK) {
                    notes.add(new Note(title, date, time, millis));
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    notes.set(notePosition, new Note(title, date, time, millis));
                }
                break;
        }
    }

}

