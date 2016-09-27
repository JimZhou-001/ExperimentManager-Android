package com.blogspot.jimzhou001.experimentmanager.Activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.blogspot.jimzhou001.experimentmanager.Receiver.AlarmReceiver;
import com.blogspot.jimzhou001.experimentmanager.Note;
import com.blogspot.jimzhou001.experimentmanager.R;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class NoteEditActivity extends Activity {

    private Button back;
    private TextView topTitle;
    private Button save;
    private TextView date;
    private Button changeDate;
    private TextView time;
    private Button changeTime;
    private EditText title;
    private Button button_time;
    private int notePosition;
    private long newMillis = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noteedit);

        if (getSharedPreferences("NoteEditActivity", MODE_PRIVATE).getBoolean("firstStart", true)==true) {
            SharedPreferences.Editor editor = getSharedPreferences("NoteEditActivity", MODE_PRIVATE).edit();
            editor.putBoolean("firstStart", false);
            editor.commit();
            new AlertDialog.Builder(NoteEditActivity.this)
                    .setTitle("温馨提示")
                    .setMessage("为了防止应用退出后无法正常发送提醒通知，请您务必允许此应用在后台运行，同时尽可能设置为开机自动启动！")
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent =  new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                        }
                    }).show();
        }

        notePosition = getIntent().getIntExtra("NotePosition", MainActivity.getSize());

        topTitle = (TextView)findViewById(R.id.topTitle);
        Intent intent = getIntent();
        topTitle.setText(intent.getStringExtra("topTitle"));

        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("notePosition", notePosition);
                intent.putExtra("title", title.getText().toString());
                intent.putExtra("date", date.getText().toString());
                intent.putExtra("time", time.getText().toString());
                intent.putExtra("millis", newMillis);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        save = (Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (topTitle.getText().toString().equals("添加实验安排")) {
                    MainActivity.noteDatabaseHelper.addNote(MainActivity.noteDatabase, title.getText().toString(), date.getText().toString(), time.getText().toString(), newMillis);
                } else {
                    ContentValues values = new ContentValues();
                    values.put("title", title.getText().toString());
                    values.put("date", date.getText().toString());
                    values.put("time", time.getText().toString());
                    values.put("millis", newMillis);
                    MainActivity.noteDatabase.update("Notes", values, "millis = ?", new String[] {String.valueOf(MainActivity.getNote(notePosition).getMillis())});
                }
                
                try {
                    MediaPlayer mp = new MediaPlayer();
                    mp.setDataSource(NoteEditActivity.this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    mp.prepare();
                    mp.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(NoteEditActivity.this, "新的实验安排已保存", Toast.LENGTH_SHORT).show();

            }
        });

        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        title = (EditText)findViewById(R.id.title);
        date = (TextView)findViewById(R.id.date);
        time = (TextView)findViewById(R.id.time);

        if (topTitle.getText().toString().equals("添加实验安排")) {
            title.setHint("请在此输入实验安排");
            date.setText(c.get(Calendar.YEAR) + "年" + (c.get(Calendar.MONTH) + 1) + "月" + c.get(Calendar.DAY_OF_MONTH) + "日");//月份从0计数
            int h = c.get(Calendar.HOUR_OF_DAY);
            int m = c.get(Calendar.MINUTE);
            String H = h < 10 ? "0" + h : "" + h;
            String M = m < 10 ? "0" + m : "" + m;
            time.setText(H + ":" + M);
        } else {
            Note note = MainActivity.getNote(notePosition);
            title.setText(note.getTitle());
            date.setText(note.getDate());
            time.setText(note.getTime());
        }

        final DatePickerDialog datePickerDialog = new DatePickerDialog(NoteEditActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Calendar temp = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
                        if (i<temp.get(Calendar.YEAR)||
                                (i==temp.get(Calendar.YEAR)&&i1<temp.get(Calendar.MONTH))||
                                (i==temp.get(Calendar.YEAR)&&i1==temp.get(Calendar.MONTH)&&i2<temp.get(Calendar.DAY_OF_MONTH))) {
                            Toast.makeText(NoteEditActivity.this, "不能穿越到过去哦，重新选择日期吧！", Toast.LENGTH_SHORT).show();
                        } else {
                            setNewMillis((new GregorianCalendar(i, i1, i2, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))).getTimeInMillis());
                            date.setText(i + "年" + (i1+1) + "月" + i2 + "日");
                        }
                    }
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        );
        changeDate = (Button)findViewById(R.id.changedate);
        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        final TimePickerDialog timePickerDialog = new TimePickerDialog(NoteEditActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        int h = c.get(Calendar.HOUR_OF_DAY);
                        int m = c.get(Calendar.MINUTE);
                        String hour, minute;
                        if (i<10) {
                            hour = "0" + i;
                        } else {
                            hour = "" + i;
                        }
                        if (i1<10) {
                            minute = "0" + i1;
                        } else {
                            minute = "" + i1;
                        }
                        setNewMillis((new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), i, i1)).getTimeInMillis());
                        time.setText(hour + ":" + minute);
                    }
                },
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true
        );
        changeTime = (Button)findViewById(R.id.changetime);
        changeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });

        button_time = (Button)findViewById(R.id.button_time);
        button_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.timedialog, null);
                new AlertDialog.Builder(NoteEditActivity.this)
                        .setTitle("请输入计时时间")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(layout)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                try {
                                    AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                                    Intent intent = new Intent(NoteEditActivity.this, AlarmReceiver.class);
                                    PendingIntent pi = PendingIntent.getBroadcast(NoteEditActivity.this, 0, intent, 0);
                                    int minute = Integer.parseInt(((EditText)layout.findViewById(R.id.minute)).getText().toString());
                                    int second = Integer.parseInt(((EditText)layout.findViewById(R.id.second)).getText().toString());
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000*60*minute + 1000*second, pi);
                                    } else {
                                        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000*60*minute + 1000*second, pi);
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(NoteEditActivity.this, "您的的输入有误，请输入合理的数字", Toast.LENGTH_SHORT);
                                }
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });

        if ((!topTitle.getText().toString().equals("添加实验安排"))&&MainActivity.getNote(notePosition).getMillis()<System.currentTimeMillis()) {
            button_time.setVisibility(View.VISIBLE);
        } else {
            button_time.setVisibility(View.GONE);
        }

    }

    private void setNewMillis(long newMillis) {
        this.newMillis = newMillis;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("notePosition", notePosition);
        intent.putExtra("title", title.getText().toString());
        intent.putExtra("date", date.getText().toString());
        intent.putExtra("time", time.getText().toString());
        intent.putExtra("millis", newMillis);
        setResult(RESULT_OK, intent);
        finish();
    }
}
