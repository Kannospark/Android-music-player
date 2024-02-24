package com.example.a20230321lab6_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{
    public static ImageView iv_cover;
    private static SeekBar sb;
    private static TextView tv_progress,tv_total;

    public static Button btn_play,btn_exit,btn_next,btn_last;
    private int isPlaying = 0;  //0代表未开始播放，1代表正在播放，-1代表暂停播放
    private static int currentMusic = 1;  //记录当前播放第几首歌曲，默认第一首
    public static int getCurrentMusic(){  //将需要播放的歌曲传给MusicPlayer
        return currentMusic;
    }
    public static void changeCurrentMusic(int i) { currentMusic = i; }

    private ObjectAnimator animator; //声明一个动画组件ObjectAnimator

    private MusicPlayer.MusicControl control;//声明MusicService中的音乐控制器

    private ServiceConnection connection = new ServiceConnection() { //声明服务连接
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            control = (MusicPlayer.MusicControl) iBinder;//实例化音乐控制对象，即control。
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init(){
        iv_cover = findViewById(R.id.iv_cover);
        sb = findViewById(R.id.sb);
        tv_progress = findViewById(R.id.tv_progress);
        tv_total = findViewById(R.id.tv_total);
        btn_play = findViewById(R.id.btn_play);
        btn_next = findViewById(R.id.btn_next);
        btn_last = findViewById(R.id.btn_last);
        btn_exit = findViewById(R.id.btn_exit);

        OnClick monclick = new OnClick();
        btn_play.setOnClickListener(monclick);
        btn_next.setOnClickListener(monclick);
        btn_last.setOnClickListener(monclick);
        btn_exit.setOnClickListener(monclick);

        //执行动画的对象是iv_cover，// 动画效果是0-360°旋转（用的是浮点数，所以加个f）。
        animator = ObjectAnimator.ofFloat(iv_cover,"rotation",0.0f,360.0f);
        animator.setDuration(10000); //旋转一周的时长，单位是毫秒，此处设置了10s
        animator.setInterpolator(new LinearInterpolator());//设置匀速转动
        animator.setRepeatCount(-1);//设置循环，此处设置的是无限循环。如果是正值，意味着转动多少圈。

        //声明一个意图，该意图进行服务的启动，意思是将MusicService里面的服务要传到主程序这里来。
        Intent mintent = new Intent(MainActivity.this,MusicPlayer.class);
        bindService(mintent,connection,BIND_AUTO_CREATE);//建立意图中MainActivity与MusicService两对象的服务连接

        seekBarListener msbListener = new seekBarListener();
        sb.setOnSeekBarChangeListener(msbListener);
        //设置图片和界面
        switch (MainActivity.getCurrentMusic()){
            case 1:
                MainActivity.iv_cover.setImageResource(R.drawable.jay1);
                MainActivity.btn_exit.setText("稻香");
                break;
            case 2:
                MainActivity.iv_cover.setImageResource(R.drawable.jay2);
                MainActivity.btn_exit.setText("花海");
                break;
            case 3:
                MainActivity.iv_cover.setImageResource(R.drawable.jay3);
                MainActivity.btn_exit.setText("兰亭序");
                break;
            case 4:
                MainActivity.iv_cover.setImageResource(R.drawable.jay4);
                MainActivity.btn_exit.setText("青花瓷");
                break;
            case 5:
                MainActivity.iv_cover.setImageResource(R.drawable.jay5);
                MainActivity.btn_exit.setText("晴天");
                break;
        }

    }
    // 设置播放、暂停、继续和退出按钮的监听（或点击）事件
    class OnClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_play:
                    //播放音乐
                    if(isPlaying == 0){
                        control.play();
                        //光盘开始转
                        animator.start();
                        btn_play.setText("||");
                        isPlaying = 1;
                        break;
                    }
                    //停止播放音乐
                    else if(isPlaying == 1){
                        control.pausePlay();
                        animator.pause();
                        btn_play.setText(">");
                        isPlaying = -1;
                        break;
                    }
                    else if(isPlaying == -1){
                        //继续播放音乐
                        control.continuePlay();
                        //光盘继续转
                        animator.resume();
                        btn_play.setText("||");
                        isPlaying = 1;
                        break;
                    }
                case R.id.btn_next:
                    //切换下一首
                    currentMusic = (currentMusic % 5) + 1;  //五次一循环
                    isPlaying = 1;
                    control.play();
                    animator.start();
                    btn_play.setText("||");
                    break;
                case R.id.btn_last:
                    currentMusic--;
                    if(currentMusic == 0) currentMusic = 5;
                    isPlaying = 1;
                    control.play();
                    animator.start();
                    btn_play.setText("||");
                    break;
                case R.id.btn_exit:
                    finish();
                    Intent mintent = null;
                    mintent = new Intent(MainActivity.this, Enrty.class);
                    startActivity(mintent);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        control.stopPlay();
        unbindService(connection);
        super.onDestroy();
    }
    //Handler主要用于异步消息的处理，在这里是处理子线程MusicService传来的消息
    public static Handler handler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();
            int duration = bundle.getInt("duration");//把音乐时长放在bundle里
            int currentDuration = bundle.getInt("currentDuration");//把音乐当前播放时长放在bundle里

            sb.setMax(duration);
            sb.setProgress(currentDuration);

            //显示总时长
            int minite = duration / 1000 /60;
            int second = duration / 1000 % 60;
            String strMinite = "";
            String strSecond = "";
            if (minite < 10){
                strMinite = "0" +minite;
            }else {
                strMinite = minite + "";
            }
            if (second < 10){
                strSecond = "0" + second;
            }else {
                strSecond = second + "";
            }
            tv_total.setText(strMinite + ":" + strSecond);
            //显示播放时长
            minite = currentDuration / 1000 /60;
            second = currentDuration / 1000 % 60;

            if (minite < 10){
                strMinite = "0" +minite;
            }else {
                strMinite = minite + "";
            }
            if (second < 10){
                strSecond = "0" + second;
            }else {
                strSecond = second + "";
            }
            tv_progress.setText(strMinite + ":" + strSecond);
        }
    };

    //给进度条设置监听
    class seekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        //进度条行进过程的监听
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (i == seekBar.getMax()){
                animator.pause();
            }
            if (b){//判断是否来自用户
                control.seekTo(i);
            }
        }
        @Override
        //用户开始滑动进度条的监听
        public void onStartTrackingTouch(SeekBar seekBar) {
            control.pausePlay();
            animator.pause();
        }

        @Override
        //用户停止滑动进度条的监听
        public void onStopTrackingTouch(SeekBar seekBar) {
            control.continuePlay();
            animator.resume();
        }
    }
}
