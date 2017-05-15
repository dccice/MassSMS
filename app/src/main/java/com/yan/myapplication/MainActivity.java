package com.yan.myapplication;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.style.EasyEditSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yan.myapplication.Adapter.PhoneAdapter;
import com.yan.myapplication.bean.Phone;
import com.yan.myapplication.utils.FileUtils;
import com.yan.myapplication.utils.ReadExcel;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private Button selectButton;
    private String path = "";
    private TextView tv_all;
    private Button send;
    private RecyclerView recycleView;
    private EditText tv_content;
    private PhoneAdapter adapter;
    private SweetAlertDialog pDialog;
    private String content;
    private int i;
    private Button jiaocheng;
    //是否可点击
    private boolean isclick;
    String SENT_SMS_ACTION = "SENT_SMS_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tv_all.setText("共" + adapter.getData().size() + "条" + "已发送" + i + "条");
            if (i == adapter.getData().size()) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }

            }

        }
    };

    private void initView() {
        tv_all = (TextView) findViewById(R.id.tv_all);
        tv_content = (EditText) findViewById(R.id.tv_content);
        recycleView = (RecyclerView) findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        selectButton = (Button) findViewById(R.id.select);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
                if (EasyPermissions.hasPermissions(MainActivity.this, perms)) {
                    OpenFile();
                } else {
                    EasyPermissions.requestPermissions(this, "读取文件", 123, perms);
                }

            }
        });
        jiaocheng = (Button) findViewById(R.id.jiaocheng);
        jiaocheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "当你点这个按钮的时候我就开始怀疑你的智商了，这么简单还需要教程吗？？？", Toast.LENGTH_LONG).show();
            }
        });
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter == null || adapter.getData().size() == 0) {
                    Toast.makeText(MainActivity.this, "亲，请先选择好要发送的人哦，不然我无能为力啊", Toast.LENGTH_LONG).show();
                    return;
                }
                String[] perms = {Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE};
                if (EasyPermissions.hasPermissions(MainActivity.this, perms)) {
                    Send();
                } else {
                    EasyPermissions.requestPermissions(this, "发送短信", 6666, perms);
                }

            }
        });
    }

    private void Send() {
        send.setClickable(false);
        content = tv_content.getText().toString();
        pDialog.setTitleText("努力发送短信中，脸都憋红了");
        pDialog.show();
        new MyThread().run();
    }

    private void OpenFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), 1);
        send.setClickable(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String path = FileUtils.getPath(this, uri);

            Toast.makeText(MainActivity.this, path, Toast.LENGTH_SHORT).show();
            this.path = path;
            new ReadLoad().execute(path);
        }

    }

    public void sendMessag(String phone, String message, int i, int num) {
        SmsManager smsManager = SmsManager.getDefault();
        if (message != null && message.length() > 0) {
            if (message.length() > 70) {
                ArrayList<String> msgs = smsManager.divideMessage(message);
                smsManager.sendMultipartTextMessage(phone, null, msgs, null, null);
            } else {
                smsManager.sendTextMessage(phone, null, message, null, null);
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == 123) {
            OpenFile();
        } else if (requestCode == 666) {
            Send();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    private class MyThread extends Thread {
        @Override
        public void run() {

            for (i = 0; i < adapter.getData().size(); i++) {
                sendMessag(adapter.getData().get(i).getPhone(), content, i + 1, adapter.getData().size());
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }
    }

    private class ReadLoad extends AsyncTask<String, Void, ArrayList<Phone>> {


        public ReadLoad() {
            pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        }

        @Override
        protected void onPreExecute() {
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86")

            );
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected ArrayList<Phone> doInBackground(String... params) {
            return ReadExcel.getXlsData(params[0], 0);
        }

        @Override
        protected void onPostExecute(ArrayList<Phone> Phones) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (Phones != null && Phones.size() > 0) {
                //存在数据
                adapter = new PhoneAdapter(Phones);
                recycleView.setAdapter(adapter);
                tv_all.setText("总共" + Phones.size() + "个人");
            } else {
                //加载失败


            }

        }
    }
}
