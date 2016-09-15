package dessert.chenxi.li.dessert_ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Set;

import dessert.chenxi.li.dessert_ui.DataBase.DataBaseUtil;
import dessert.chenxi.li.dessert_ui.HomeFragment.HomeFragment;
import dessert.chenxi.li.dessert_ui.LoginActivity.LoginActivity;
import dessert.chenxi.li.dessert_ui.UsbSerial.UsbService;
import dessert.chenxi.li.dessert_ui.VideoFragment.VideoFragment;
import dessert.chenxi.li.dessert_ui.WeatherFragment.WeatherFragment;

public class MainActivity extends AppCompatActivity {
    /*
     * Notifications from UsbService will be received here.
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


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

    //位置更改
    private Spinner locationSpinner;

    //  用于对四个界面的fragment的管理
    private FragmentManager fragmentManager;

    private String account;

    //UsbSerial变量
    private UsbService usbService;
    private EditText editText, display;
    private Button btnSendUsb;
    private MyHandler mHandler;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    //设备所需变量
    public static final String ACTION_USB_PERMISSION = "dessert.chenxi.li.dessert.USB_PERMISSION";
    private String locUrl = "http://192.168.50.198:8080/DataServer/setDevice";

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
        mHandler = new MyHandler(this);
//        display = (EditText) findViewById(R.id.etUsbDevice);
//        editText = (EditText) findViewById(R.id.etSendUsb);
//        btnSendUsb = (Button) findViewById(R.id.btn_sendUsb);
//        btnSendUsb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!editText.getText().toString().equals("")) {
//                    String data = editText.getText().toString();
//                    if (usbService != null) { // if UsbService was correctly binded, Send data
//                        usbService.write(data.getBytes());
//                    }
//                }
//            }
//        });

        //  初始化界面管理器
        fragmentManager = getFragmentManager();
        //  初始化界面
        setTabSelection(1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.menu_pic));

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

        locationSpinner = (Spinner)findViewById(R.id.locationValues);
        ArrayAdapter<CharSequence> baudAdapter = ArrayAdapter
                .createFromResource(this, R.array.location_values,
                        R.layout.my_spinner_textview);
        baudAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
        locationSpinner.setAdapter(baudAdapter);
        locationSpinner.setGravity(0x10);
        locationSpinner.setSelection(0);

        /*set the adapter listeners for baud */
        locationSpinner.setOnItemSelectedListener(new MyOnBaudSelectedListener());
    }

    public class MyOnBaudSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            String loction = parent.getItemAtPosition(position).toString();
            OkHttpUtil.postLocParams(locUrl,account,"1",loction);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent){

        }

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
            Toast.makeText(getApplicationContext(), "已注销", Toast.LENGTH_SHORT).show();
            OkHttpUtil.setResult(false);
            String name = getAccount();
            DataBaseUtil.deleteInSql(this, name);
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(i);
            MainActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //登陆标记
    public String getAccount(){
        return account;
    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    mActivity.get().display.append(data);
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

}
