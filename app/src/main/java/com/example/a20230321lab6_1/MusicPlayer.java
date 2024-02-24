package com.example.a20230321lab6_1;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 在这里设置音乐播放功能的服务
 *
 */

public class MusicPlayer extends Service {

    // 设置两个成员变量
    private MediaPlayer player;//声明一个多媒体对象
    private Timer timer;//声明一个时钟对象
//    public int currentMusic = 0;

    public MusicPlayer (){}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicControl();//将MusicControl()返回给onBind（）方法，这样绑定服务的时候，可以把音乐控制器实例化。
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();//实例化多媒体
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player==null) return;
        if (player.isLooping()) player.stop(); //停止播放音乐
        player.release();//释放资源
        player = null;
    }
    //创建一个内部类MusicControl，功能是让主程序控制sevise里面的多媒体对象。IBinder 是Binder的子类，因此要返回MusicControl给IBinder。
    class MusicControl extends Binder{
        public void play() {//开始播放
            try{
                player.reset();//重置音乐播放器
                switch (MainActivity.getCurrentMusic()){
                    case 1:
                        player = MediaPlayer.create(getApplicationContext(),R.raw.song1);
                        MainActivity.iv_cover.setImageResource(R.drawable.jay1);
                        MainActivity.btn_exit.setText("稻香");
                        break;
                    case 2:
                        player = MediaPlayer.create(getApplicationContext(),R.raw.song2);
                        MainActivity.iv_cover.setImageResource(R.drawable.jay2);
                        MainActivity.btn_exit.setText("花海");
                        break;
                    case 3:
                        player = MediaPlayer.create(getApplicationContext(),R.raw.song3);
                        MainActivity.iv_cover.setImageResource(R.drawable.jay3);
                        MainActivity.btn_exit.setText("兰亭序");
                        break;
                    case 4:
                        player = MediaPlayer.create(getApplicationContext(),R.raw.song4);
                        MainActivity.iv_cover.setImageResource(R.drawable.jay4);
                        MainActivity.btn_exit.setText("青花瓷");
                        break;
                    case 5:
                        player = MediaPlayer.create(getApplicationContext(),R.raw.song5);
                        MainActivity.iv_cover.setImageResource(R.drawable.jay5);
                        MainActivity.btn_exit.setText("晴天");
                        break;
                }
                player.start(); //开始播放音乐
                addTimer();//添加计时器
            }catch (Exception exception) {//catch用来处理播放时产生的异常
                exception.printStackTrace();
            }
        }
        public void pausePlay(){//暂停播放
            player.pause();
        }
        public void continuePlay(){//继续播放
            player.start();
        }
        public void stopPlay(){ //暂停播放
            player.stop();
            player.release();
            try {
                timer.cancel();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        public void seekTo(int progress){//定义播放位置播放
            player.seekTo(progress);
        }
    }

    //添加计时器，计时器是一个多线程的东西，用于设置音乐播放器中的进度条信息
    public void addTimer(){
        if (timer == null){
            timer = new Timer();
            TimerTask task = new TimerTask() {//实例化一个计时任务对象
                @Override
                public void run() { //run就是多线程的一个东西,用于service和主线程（即MainActivity）之间的通信
                    if (player == null) return; //如果player没有实例化，就不执行下面的代码。
                    int duration = player.getDuration();//获取歌曲总长度
                    int currentDuration = player.getCurrentPosition();//获取歌曲当前播放进度
                    //将音乐的总时长、播放时长封装到消息对象中去；
                    Message message = MainActivity.handler.obtainMessage();//在主线程获取一个消息空间
                    Bundle bundle = new Bundle();//定义一个包裹，将歌曲总长度和当前播放长度打包放进去
                    bundle.putInt("duration",duration);  //键值对
                    bundle.putInt("currentDuration",currentDuration);
                    message.setData(bundle);//将消息包 括给message
                    MainActivity.handler.sendMessage(message);//将消息添加到主线程中
                }
            };
            //开始计时任务后5ms，执行第一次任务，以后每500ms执行一次任务
            timer.schedule(task, 5,500);  //每多少秒发送一次数据
        }
    }
}
