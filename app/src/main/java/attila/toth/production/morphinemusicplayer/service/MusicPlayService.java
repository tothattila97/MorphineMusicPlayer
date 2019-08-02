package attila.toth.production.morphinemusicplayer.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import attila.toth.production.morphinemusicplayer.PlayerActivity;
import attila.toth.production.morphinemusicplayer.R;
import attila.toth.production.morphinemusicplayer.fragment.MusicFragment;
import attila.toth.production.morphinemusicplayer.model.MusicModel;

import static attila.toth.production.morphinemusicplayer.PlayerActivity.EXTRA_MUSIC;
import static attila.toth.production.morphinemusicplayer.PlayerActivity.EXTRA_MUSIC_DUR;
import static attila.toth.production.morphinemusicplayer.PlayerActivity.playerActivity;

public class MusicPlayService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener{

    public static final int NOTIFICATION_ID = 1000;
    public static final String KILL_ACTION = "KILL";
    public static final String PREV_ACTION = "PREVIOUS";
    public static final String NEXT_ACTION = "NEXT";
    public static final String PAUSE_ACTION = "PAUSE";
    public static final String MEDIA_PLAYER_KEY ="player_key";

    public final IBinder binder = new MusicPlayBinder();
    public static MediaPlayer mp = new MediaPlayer();   //nem volt static

    public static NotificationCompat.Builder b;
    public  static RemoteViews contentView;

    public boolean isBinded = false;
    public String[] extras;
    public String[] nextactmusic;
    public int nextidx;
    public int extraduration;

    public static MusicPlayService musicPlayServiceInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        musicPlayServiceInstance = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        Bundle bundle = intent.getExtras();
        extras = bundle.getStringArray(EXTRA_MUSIC);
        nextactmusic = bundle.getStringArray(EXTRA_MUSIC);
        extraduration = bundle.getInt(EXTRA_MUSIC_DUR);
        String musicpath = extras[0];

        //String mpath = Environment.getExternalStorageDirectory().getPath() + "/mp3 converter/extracted audio/Snapchat-1197699745_high_quality.mp3";
        //String mpath = "/storage/7A2C-2126/Zene/Vegyes/ByeAlex - Fekete.mp3";
        //playMP3(musicpath);
        //String  emulatorpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/rockstar.mp3";  //"/sdcard/Download/rockstar.mp3";
        //playMP3(emulatorpath);
        //rawmp3play();
        //startForeground(NOTIFICATION_ID,buildForegroundNotif(extras[1], extras[2]) );

        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle bundle = intent.getExtras();
        String[] extra;
        extra = bundle.getStringArray(EXTRA_MUSIC);
        extraduration = bundle.getInt(EXTRA_MUSIC_DUR);
        String musicpath = extra[0];

        playMP3(musicpath);
        startForeground(NOTIFICATION_ID,buildForegroundNotif(extras[4], extras[2]) );

        return START_STICKY;
    }

    private Notification buildForegroundNotif(String name, String album){
        b=new NotificationCompat.Builder(this);
        contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        contentView.setTextViewText(R.id.notifnameTV, name);
        contentView.setTextViewText(R.id.notifalbumTV, album);

        Intent killIntent = new Intent(this, SwitchButtonListener.class);
        killIntent.setAction(KILL_ACTION);
        PendingIntent pendingKillIntent = PendingIntent.getBroadcast(this, 0, killIntent, 0);
        contentView.setOnClickPendingIntent(R.id.notifkillIB, pendingKillIntent);

        Intent nextIntent = new Intent(this, SwitchButtonListener.class);
        nextIntent.setAction(NEXT_ACTION);
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);
        contentView.setOnClickPendingIntent(R.id.notifnextIB, pendingNextIntent);

        Intent pauseIntent = new Intent(this, SwitchButtonListener.class);
        pauseIntent.setAction(PAUSE_ACTION);
        PendingIntent pendingPauseIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, 0);
        contentView.setOnClickPendingIntent(R.id.notifpauseIB, pendingPauseIntent);

        Intent prevIntent = new Intent(this, SwitchButtonListener.class);
        prevIntent.setAction(PREV_ACTION);
        PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(this, 0, prevIntent, 0);
        contentView.setOnClickPendingIntent(R.id.notifprevIB, pendingPrevIntent);

        b.setOngoing(true)
                .setContent(contentView)
                .setSmallIcon(android.R.drawable.btn_radio)
                .setTicker("mikéne");

        return(b.build());
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //killMediaPlayer();
        return super.onUnbind(intent);
    }

    /*private void rawmp3play(){
        MediaPlayer mplayer = MediaPlayer.create(getBaseContext(), R.raw.mymusic);
        mplayer.start();
    }*/

    public MediaPlayer getMediaPlayer() {
        return mp;
    }

    public static MusicPlayService getInstance(){
        return musicPlayServiceInstance;
    }

    private void playMP3(String path){

        try{
            if(mp==null){
                mp = new MediaPlayer();
            }
            mp.reset();
            mp.setDataSource(path);
            //TODO: ha eznincs kommentezve akkor elszáll...
            //mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mp.setOnCompletionListener(this);
            mp.setOnPreparedListener(this);
            mp.prepareAsync();  //prepare helyett nem blokkolja a main thread-et
            mp.setLooping(false);
            //mp.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public void killMediaPlayer() {
        if (mp !=null) {
            try {
                mp.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        //mikor leiratkoznak a serviceről akkor le kell állítani a mediaplayert is
        //killMediaPlayer();
        super.unbindService(conn);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //TODO: ha vége egy számnak utána beidőzítve elindítani a következőt
        Toast.makeText(getApplicationContext(),"Hello, vége a zenének itt indítom az újat", Toast.LENGTH_SHORT).show();
        /**
         * lejátsza az aktuálisat utána a következőt és újraindítja a következőt nem megy 1-nél tovább
         * valahogy le kell a mediaplayerbol kerni mit jatszik
         */

        mp.reset();  // stop vagy pause
        MusicModel act = new MusicModel();
        for(int i = 0; i < MusicFragment.getMusicFragment().adapter.musics.size(); i++){
            if(MusicFragment.getMusicFragment().adapter.musics.get(i).getMusicName().equals(nextactmusic[1])){
                nextidx = i;
                act = MusicFragment.getMusicFragment().adapter.musics.get(i);
            }
        }
        MusicModel next = MusicFragment.getMusicFragment().adapter.musics.get(nextidx+=1);
        try{
            MusicPlayService.getInstance().getMediaPlayer().setDataSource(next.getMusicPath());
            MusicPlayService.getInstance().getMediaPlayer().prepare();
            //MusicPlayService.getInstance().getMediaPlayer().setLooping(false);
            PlayerActivity.getPlayerActivity().nameOfMusictv.setText(next.getMusicName());
            PlayerActivity.getPlayerActivity().artistOfMusictv.setText(next.getMusicArtist());
            PlayerActivity.getPlayerActivity().musicSeekBar.setProgress(0);
            PlayerActivity.getPlayerActivity().startprogress = 0;
            playerActivity.varminute = 0; playerActivity.varsecond = 0;
            //Log.v("SEEKBAR RESTART LOG", String.valueOf(PlayerActivity.getPlayerActivity().startprogress));
            PlayerActivity.getPlayerActivity().musicSeekBar.setMax(next.getMusicDuration());
            int minutes = PlayerActivity.getPlayerActivity().getSeekBarMinutes(next.getMusicDuration());
            int seconds = PlayerActivity.getPlayerActivity().getSeekBarSeconds(next.getMusicDuration());
            PlayerActivity.getPlayerActivity().setSeekBarTextView(minutes,seconds);
            MusicPlayService.SwitchButtonListener sw = new MusicPlayService.SwitchButtonListener();
            sw.refreshNotification();
        }catch (Exception e){ e.printStackTrace();}
        nextactmusic[0] = next.getMusicPath();
        nextactmusic[1] = next.getMusicName();
        nextactmusic[2] = next.getMusicAlbum();
        nextactmusic[3] = next.getMusicArtist();

    }

    public class MusicPlayBinder extends Binder{
        public MusicPlayService getService() {
            return MusicPlayService.this;
        }
    }

    public static class SwitchButtonListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Hello", "Bejutottam ide egyáltalán");

            final String action = intent.getAction();

            switch (action) {
                case KILL_ACTION:
                    MusicPlayService.getInstance().killMediaPlayer();
                    MusicPlayService.getInstance().stopForeground(true);
                    //PlayerActivity.getPlayerActivity().finishAndRemoveTask();
                    //MusicFragment.getMusicFragment().getActivity().finishAndRemoveTask();
                    break;
                case PREV_ACTION:
                    PlayerActivity.getPlayerActivity().playPreviousMusic();
                    refreshNotification();
                    break;
                case PAUSE_ACTION:
                    PlayerActivity.getPlayerActivity().playOrPauseMusic();
                    break;
                case NEXT_ACTION:
                    PlayerActivity.getPlayerActivity().playNextMusic();
                    refreshNotification();
                    break;
            }
        }

        //TODO: átstruktúrálni a service-be ez ottani feladat nem a BR-é
        public void refreshNotification(){
            contentView.setTextViewText(R.id.notifnameTV, PlayerActivity.getPlayerActivity().nameOfMusictv.getText());
            contentView.setTextViewText(R.id.notifalbumTV, PlayerActivity.getPlayerActivity().artistOfMusictv.getText());
            b.setContent(contentView);
            NotificationManager notificationManager = (NotificationManager)MusicPlayService.getInstance().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID,b.build());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
        stopForeground(true);
    }
}
