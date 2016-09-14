package dessert.chenxi.li.dessert_ui.WeatherFragment;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;

import dessert.chenxi.li.dessert_ui.MainActivity;
import dessert.chenxi.li.dessert_ui.R;
import dessert.chenxi.li.dessert_ui.dashboardView.view.DashboardView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private DashboardView dashboardView;
    private TextView tvHum, tvPm25, tvGaswarning, tvFirewarning;
    private LinearLayout lyGaswarning, lyFireWarning;
    private Handler handler;
    private Vibrator vibrator;

    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        handler = new Handler() {
            public void handleMessage(Message msg) {
                // 处理信息
                int tmp = (int)(Math.random()*100);
                int hum = (int)(Math.random()*100);
                int pm25 = (int)(Math.random()*1000);
                dashboardView.setPercent(tmp);
                tvHum.setText(String.valueOf(hum));
                tvPm25.setText(String.valueOf(pm25));
                int tmpWarn = 90;
                int humWarn = 10;
                int pm25Warn = 980;

                //火灾提醒
                if ((tmp > tmpWarn && hum < humWarn) || (pm25 > pm25Warn)){
                    //提醒
                    if ((tmp > tmpWarn && hum < humWarn) && (pm25 > pm25Warn)){
                        lyFireWarning.setBackgroundColor(Color.RED);
                        tvFirewarning.setText("危险");
                        lyGaswarning.setBackgroundColor(Color.RED);
                        tvGaswarning.setText("危险");
                    }else if ((tmp > tmpWarn && hum < humWarn) && !(pm25 > pm25Warn)){
                        lyFireWarning.setBackgroundColor(Color.RED);
                        tvFirewarning.setText("危险");
                    }else if (!(tmp > tmpWarn && hum < humWarn) && (pm25 > pm25Warn)){
                        lyGaswarning.setBackgroundColor(Color.RED);
                        tvGaswarning.setText("危险");
                    }
                    //震动
                    vibrator.vibrate(new long[]{100,10,100,1000}, 0);
//                  warnAlarm();
                }else {
                    //取消震动
                    vibrator.cancel();
                    lyFireWarning.setBackgroundColor(Color.rgb(31, 186, 243));
                    tvFirewarning.setText("无");
                    lyGaswarning.setBackgroundColor(Color.rgb(31, 186, 243));
                    tvGaswarning.setText("无");
                }

                super.handleMessage(msg);
            }
        };
    }

    public class MyThread implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    Thread.sleep(5000);// 线程暂停10秒，单位毫秒
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);// 发送消息
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

//    public void warnAlarm (){
//        mMediaPlayer = MediaPlayer.create(this.getActivity(), getSystemDefultRingtoneUri());
//        mMediaPlayer.setLooping(true);//设置循环
//        try {
//            mMediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mMediaPlayer.start();
//
//    }
//    private Uri getSystemDefultRingtoneUri() {
//        return RingtoneManager.getActualDefaultRingtoneUri(this.getActivity(),
//                RingtoneManager.TYPE_RINGTONE);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        dashboardView = (DashboardView) view.findViewById(R.id.dashboard_tmp);
        tvHum = (TextView) view.findViewById(R.id.tv_hum);
        tvPm25 = (TextView) view.findViewById(R.id.tv_pm25);
        tvGaswarning = (TextView) view.findViewById(R.id.tv_gasWarning);
        tvFirewarning = (TextView) view.findViewById(R.id.tv_fireWarning);
        lyGaswarning = (LinearLayout) view.findViewById(R.id.ly_gasWaring);
        lyFireWarning = (LinearLayout) view.findViewById(R.id.ly_fireWaring);
        vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        dashboardView.setMaxNum(100);
        dashboardView.setPercent(0);
        dashboardView.setText("♪ ");
        dashboardView.setUnit("℃");
        dashboardView.setStartColor(Color.rgb(240,50,21));
        dashboardView.setEndColor(Color.rgb(112,230,194));

        new Thread(new MyThread()).start();
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
