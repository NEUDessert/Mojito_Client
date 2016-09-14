package dessert.chenxi.li.dessert_ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;
import dessert.chenxi.li.dessert_ui.DataBase.DataBase;
import dessert.chenxi.li.dessert_ui.DataBase.DataBaseUtil;
import dessert.chenxi.li.dessert_ui.HomeFragment.HomeFragment;
import dessert.chenxi.li.dessert_ui.LoginActivity.LoginActivity;
import dessert.chenxi.li.dessert_ui.VideoFragment.VideoFragment;
import dessert.chenxi.li.dessert_ui.WeatherFragment.WeatherFragment;

public class MainActivity extends AppCompatActivity {
    //  三个界面
    private HomeFragment fraTabHome;
    private VideoFragment fraTabVideo;
    private WeatherFragment fraTabWeather;

    //    底部三个按钮
    private LinearLayout TabWeather;
    private LinearLayout TabVideo;
    private LinearLayout TabHome;

    //  三个对应的图标
    private ImageView ivWeather;
    private ImageView ivVideo;
    private ImageView ivHome;

    //  三个图标的对应的文字
    private TextView tvWeather;
    private TextView tvVideo;
    private TextView tvHome;
    private TextView tvLocation;

    //位置更改
    private Spinner locationSpinner;

    //  用于对四个界面的fragment的管理
    private FragmentManager fragmentManager;

    private String account;

    //设备所需变量
    private EditText readText;
    public byte[] readBuffer;
    private Button btnOpen;
    private boolean isOpen;
    private Handler handler;
    public static final String ACTION_USB_PERMISSION = "dessert.chenxi.li.dessert.USB_PERMISSION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        account =intent.getStringExtra("account");

        //从上个Activity传过来的值
        Toast.makeText(this, account+"登陆", Toast.LENGTH_SHORT).show();

        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保存屏幕常亮

        //  初识化控件
        initViews();
        //  初始化界面管理器
        fragmentManager = getFragmentManager();
        //  初始化界面
        setTabSelection(1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);

//            Bundle bundle = new Bundle();
//            bundle.putBoolean("isLogin", false);
//            Intent intent = new Intent();
//            intent.setClass(MainActivity.this, LoginActivity.class);
//            intent.putExtra("bundle", bundle);
//            startActivityForResult(intent, 1);
//
//        initViews();


//        MyApp.driver = new CH34xUARTDriver(
//                (UsbManager)getSystemService(Context.USB_SERVICE)
//                , this, ACTION_USB_PERMISSION);
//
//        if(!MyApp.driver.UsbFeatureSupported()){ //判断系统是否支持USB HOST
//            Dialog dialog = new AlertDialog.Builder(MainActivity.this)
//                    .setTitle("提示").setMessage("您的手机不支持USB HOST，请更换其他手机尝试。")
//                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            System.exit(0);
//                        }
//                    }).create();
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();
//        }
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保存屏幕常亮
//        readBuffer = new byte[512];
//        isOpen = false;
//
//        handler = new Handler(){
//            public void handleMessage(Message msg){
//                readText.append((String)msg.obj);
//            }
//        };
    }

    //  定义控件、文本和设置点击的事件侦听器
    private void initViews() {
        TabWeather = (LinearLayout) findViewById(R.id.tab_bottom_weather);
        TabVideo = (LinearLayout) findViewById(R.id.tab_bottom_video);
        TabHome = (LinearLayout) findViewById(R.id.tab_bottom_home);

        ivWeather = (ImageView) findViewById(R.id.iv_tab_bottom_weather);
        ivVideo = (ImageView) findViewById(R.id.iv_tab_bottom_video);
        ivHome = (ImageView) findViewById(R.id.iv_tab_bottom_home);

        tvWeather = (TextView) findViewById(R.id.tv_weather);
        tvVideo = (TextView) findViewById(R.id.tv_video);
        tvHome = (TextView) findViewById(R.id.tv_home);

        TabWeather.setOnClickListener(ClickHandler);
        TabVideo.setOnClickListener(ClickHandler);
        TabHome.setOnClickListener(ClickHandler);

        tvLocation = (TextView) findViewById(R.id.tv_deviceName);
        locationSpinner = (Spinner)findViewById(R.id.locationValues);
//        if (isLogin) {
        ArrayAdapter<CharSequence> baudAdapter = ArrayAdapter
                .createFromResource(this, R.array.location_values,
                        R.layout.my_spinner_textview);
        baudAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
        locationSpinner.setAdapter(baudAdapter);
//        }else {
//            ArrayAdapter<CharSequence> baudAdapter = ArrayAdapter
//                    .createFromResource(this, R.array.notLogin,
//                            R.layout.my_spinner_textview);
//            baudAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
//            locationSpinner.setAdapter(baudAdapter);
//            locationSpinner.setEnabled(false);
//        }
        locationSpinner.setGravity(0x10);
        locationSpinner.setSelection(0);

//        readText = (EditText) findViewById(R.id.et_dataString);
//        btnOpen = (Button) findViewById(R.id.btn_open);
    }

    //  定义事件侦听器操作
    private View.OnClickListener ClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
    //  通过ID来决定当前界面
                case R.id.tab_bottom_weather:
                    setTabSelection(REQUEST_CODE_WEATHER);
                    break;
                case R.id.tab_bottom_video:
                    setTabSelection(REQUEST_CODE_VIDEO);
                    break;
                case R.id.tab_bottom_home:
                    setTabSelection(REQUEST_CODE_HOME);
                    break;
            }
        }
    };

//    public void onClick(View v){
//        switch (v.getId()){
//            case R.id.btn_open:
//                //打开流程主要步骤为ResumeUsblist, UartInit
//                if (!isOpen){
//                    if (!MyApp.driver.ResumeUsbList()){// ResumeUsbList方法用于枚举CH34X设备以及打开相关设备
//                        Toast.makeText(MainActivity.this, "打开设备失败！",
//                                Toast.LENGTH_SHORT).show();
//                        MyApp.driver.CloseDevice();
//                    }else {
//                        if (!MyApp.driver.UartInit()){//对串口设备进行初始化操作
//                            Toast.makeText(MainActivity.this, "设备初始化失败！",
//                                    Toast.LENGTH_SHORT).show();
//                            Toast.makeText(MainActivity.this, "打开设备失败！",
//                                    Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        Toast.makeText(MainActivity.this, "打开设备成功！",
//                                Toast.LENGTH_SHORT).show();
//                        isOpen = true;
//                        new readThread().start();//开启线程读取串口接收的数据
//                    }
//                }else {
//                    MyApp.driver.CloseDevice();
//                    isOpen = false;
//                }
//                break;
//
//            default:
//                break;
//
//        }
//    }
//
//    public class readThread extends Thread {
//        public void run(){
//            byte[] buffer = new byte[64];
//
//            while(true){
//                Message msg = Message.obtain();
//                if(!isOpen){
//                    break;
//                }
//                int length = MyApp.driver.ReadData(buffer, 64);
//                if(length > 0){
//                    String recv = toHexString(buffer, length);
//                    msg.obj = recv;
//                    handler.sendMessage(msg);
//                }
//            }
//        }
//    }
//
//    /**
//     * 将byte[]数组转化为String类型
//     * @param arg
//     *            需要转换的byte[]数组
//     * @param length
//     *            需要转换的数组长度
//     * @return 转换后的String队形
//     */
//    private String toHexString(byte[] arg, int length) {
//        String result = new String();
//        if(arg != null){
//            for (int i = 0; i < length; i++) {
//                result = result
//                        + (Integer.toHexString(arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
//                        + Integer.toHexString(arg[i] < 0 ? arg[i] + 256 : arg[i])
//                        : Integer.toHexString(arg[i] < 0 ? arg[i] + 256 : arg[i]))
//                        + " ";
//            }
//            return result;
//        }
//        return "";
//    }

    public final static int REQUEST_CODE_WEATHER = 1 ;
    public final static int REQUEST_CODE_VIDEO = 2 ;
    public final static int REQUEST_CODE_HOME = 3 ;

    private void setTabSelection(int index) {
        //  重置控件为初始状态
        resetBtn();
        //  页面切换的方法
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //  隐藏fragment，防止多个页面重叠的情况。
        hideFragments(transaction);

        switch (index) {
        //  设置选中时的图片颜色及字体颜色，若对应的fragment为空则创建，将此时的界面内容交给对应的fragment。若不为空，则将它显示出来。
            case REQUEST_CODE_WEATHER:
                ivWeather.setImageResource(R.drawable.weather_l);
                tvWeather.setTextColor(this.getResources().getColor(R.color.colorPrimary));
                if (fraTabWeather == null){
                    fraTabWeather = new WeatherFragment();
                    transaction.add(R.id.id_content, fraTabWeather);
                } else {
                    transaction.show(fraTabWeather);
                }
                break;

            case REQUEST_CODE_VIDEO:
                ivVideo.setImageResource(R.drawable.video_l);
                tvVideo.setTextColor(this.getResources().getColor(R.color.colorPrimary));
                if (fraTabVideo == null){
                    fraTabVideo = new VideoFragment();
                    transaction.add(R.id.id_content, fraTabVideo);
                } else {
                    transaction.show(fraTabVideo);
                }
                break;

            case REQUEST_CODE_HOME:
                ivHome.setImageResource(R.drawable.home_l);
                tvHome.setTextColor(this.getResources().getColor(R.color.colorPrimary));
                if (fraTabHome == null){
                    fraTabHome = new HomeFragment();
                    transaction.add(R.id.id_content, fraTabHome);
                    fraTabHome.setAccount(account);
                } else {
                    transaction.show(fraTabHome);
                }
                break;
        }
        transaction.commit();
    }

    private void resetBtn() {
        //  重置图标及文本的颜色
        ivWeather.setImageResource(R.drawable.weather_h);
        ivVideo.setImageResource(R.drawable.video_h);
        ivHome.setImageResource(R.drawable.home_h);

        tvWeather.setTextColor(this.getResources().getColor(R.color.text_gray));
        tvVideo.setTextColor(this.getResources().getColor(R.color.text_gray));
        tvHome.setTextColor(this.getResources().getColor(R.color.text_gray));
    }

    //  将所有的fragment均设为隐藏状态。便于下一步的选择并显示。
    private void hideFragments(FragmentTransaction transaction){
        if (fraTabWeather != null){
            transaction.hide(fraTabWeather);
        }

        if (fraTabVideo != null){
            transaction.hide(fraTabVideo);
        }

        if (fraTabHome != null){
            transaction.hide(fraTabHome);
        }
    }

    //  后退事件处理
    private long lastClickTime = 0;
    public void onBackPressed() {
        if (lastClickTime <= 0){
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            lastClickTime = System.currentTimeMillis();
        }
        else {
            long currentClickTime = System.currentTimeMillis();
            if (currentClickTime - lastClickTime < 1000){
                finish();
            }
            else {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                lastClickTime = currentClickTime;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_toolbar_value, menu);
        return true;
    }

    //下拉选项事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Toast.makeText(getApplicationContext(), "分享啦", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_binaryData) {
            Toast.makeText(getApplicationContext(), "相机坏了", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_historyData) {
            Toast.makeText(getApplicationContext(), "还没有", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_moreSetting) {
            Toast.makeText(getApplicationContext(), "等一会儿吧", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_signOut){
            Toast.makeText(getApplicationContext(), "等一会儿吧", Toast.LENGTH_SHORT).show();
            String name = getAccount();
            DataBaseUtil.deleteInSql(this, name);
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(i);
            MainActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (1 == requestCode) {
//            if (1 == resultCode) {
//                Bundle bundle = data.getBundleExtra("bundle");
//                setIsLogin(bundle.getBoolean("isLogin"));
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    //登陆标记
    public String getAccount(){
        return account;
    }
}
