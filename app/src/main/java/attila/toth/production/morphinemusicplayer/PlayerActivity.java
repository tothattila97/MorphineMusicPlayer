package attila.toth.production.morphinemusicplayer;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import attila.toth.production.morphinemusicplayer.fragment.MusicFragment;
import attila.toth.production.morphinemusicplayer.model.MusicModel;
import attila.toth.production.morphinemusicplayer.service.MusicPlayService;

public class PlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    public static final String EXTRA_MUSIC = "extra_music";
    public static final String EXTRA_MUSIC_DUR = "extra_duration";

    public static PlayerActivity playerActivity = null;

    public TextView musicDurTV;
    public TextView nameOfMusictv;
    public TextView artistOfMusictv;
    public TextView musicSecondCountertv;
    public SeekBar musicSeekBar;
    public ToggleButton playPauseButton;
    public ImageButton nextIB;
    public ImageButton prevIB;
    public ImageButton repeatIB;
    public int previdx;
    public int nextidx;

    public String[] nextactmusic = MusicPlayService.getInstance().extras;
    public String[] prevactmusic = MusicPlayService.getInstance().extras;

    Handler mHandler = new Handler();
    private int length;
    private int presses = 0;
    public int startprogress = 0;
    public int varminute = 0; public int varsecond = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //TextView extra = findViewById(R.id.extratv);

        nameOfMusictv = findViewById(R.id.nameOfMusictv);
        artistOfMusictv = findViewById(R.id.artistOfMusictv);
        musicSeekBar = findViewById(R.id.musicSeekBar);
        musicSeekBar.setOnSeekBarChangeListener(this);
        musicDurTV = findViewById(R.id.musicDurationTV);
        musicSecondCountertv = findViewById(R.id.musicSecondCounterTV);
        playPauseButton = findViewById(R.id.playPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playOrPauseMusic();
            }
        });

        nextIB = findViewById(R.id.nextIB);
        nextIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextMusic();
            }
        });

        prevIB = findViewById(R.id.previousIB);
        prevIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousMusic();
            }
        });

        repeatIB = findViewById(R.id.repeatIB);
        repeatIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(presses % 2 == 0 ){
                    MusicPlayService.getInstance().getMediaPlayer().setLooping(true);
                    repeatIB.setColorFilter(Color.rgb(105, 229, 36));
                    presses++;
                }
                else {
                    MusicPlayService.getInstance().getMediaPlayer().setLooping(false);
                    repeatIB.setColorFilter(Color.rgb(255,255,255));
                    presses++;
                }
            }
        });

        Bundle bundle = getIntent().getExtras();
        String[] extras;
        extras = bundle.getStringArray(EXTRA_MUSIC);
        int extraduration = bundle.getInt(EXTRA_MUSIC_DUR);
        //int seconds = (int) (extraduration / 1000) % 60 ;
        //int minutes = (int) ((extraduration / (1000*60)) % 60);
        int seconds = getSeekBarSeconds(extraduration);
        int minutes = getSeekBarMinutes(extraduration);
        //extra.setText(extras[0] + ", "+ extras[1]+ ", "+ extras[2]+", " + extras[3] + ", " + extraduration);

        nameOfMusictv.setText(extras[4]);
        artistOfMusictv.setText(extras[3]);
        musicSecondCountertv.setText(String.valueOf(varminute)+":0"+String.valueOf(varsecond));
        setSeekBarTextView(minutes, seconds);
        musicSeekBar.setProgress(startprogress);
        musicSeekBar.setMax(extraduration);
        updateSeekBarProgress();

        playerActivity = this;
    }

    public static PlayerActivity getPlayerActivity(){return playerActivity;}

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public int getSeekBarMinutes(int duration){
        int minutes = (int) ((duration / (1000*60)) % 60);
        return  minutes;
    }

    public int getSeekBarSeconds(int duration){
        int seconds = (int) (duration / 1000) % 60 ;
        return seconds;
    }
    public void setSeekBarTextView(int minutes, int seconds){
        if(seconds >= 10){
            musicDurTV.setText(String.valueOf(minutes) + ":" + String.valueOf(seconds));
        }
        else musicDurTV.setText(String.valueOf(minutes) + ":0" + String.valueOf(seconds));
    }

    public void updateSeekBarProgress(){
        //if(MusicPlayService.getInstance().getMediaPlayer().isPlaying()){
            varsecond++;
            if(varsecond == 60){
                varminute++;
                varsecond = 0;
                musicSecondCountertv.setText(String.valueOf(varminute) + ":0" + String.valueOf(varsecond));
            }
            else if (varsecond >= 10){
                musicSecondCountertv.setText(String.valueOf(varminute) + ":" + String.valueOf(varsecond));
            }
            else musicSecondCountertv.setText(String.valueOf(varminute) + ":0" + String.valueOf(varsecond));
            musicSeekBar.setProgress(startprogress+=1000);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateSeekBarProgress();
                }
            },1000);
       //}
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, boolean fromUser) {

        if (fromUser) {
            startprogress = progress;
            seekBar.setProgress(startprogress);
            MusicPlayService.getInstance().getMediaPlayer().seekTo(progress);
            varsecond = (int) (progress / 1000) % 60 ;
            varminute = (int) ((progress / (1000*60)) % 60);
            musicSecondCountertv.setText(String.valueOf(varminute)+":"+String.valueOf(varsecond));
            //Toast.makeText(getApplicationContext(), String.valueOf(progress), Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), String.valueOf(seekBar.getMax()), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {/*MusicPlayService.getInstance().mp.seekTo(seekedprogress);*/}

    public void playPreviousMusic(){
        MusicPlayService.getInstance().getMediaPlayer().reset();  // stop vagy pause
        MusicModel act = new MusicModel();
        for(int i = 0; i < MusicFragment.getMusicFragment().adapter.musics.size(); i++){
            if(MusicFragment.getMusicFragment().adapter.musics.get(i).getMusicName().equals(prevactmusic[1])){
                previdx = i;
                act = MusicFragment.getMusicFragment().adapter.musics.get(i);
            }
        }
        MusicModel prev = MusicFragment.getMusicFragment().adapter.musics.get(previdx-=1);
        try{
            MusicPlayService.getInstance().getMediaPlayer().setDataSource(prev.getMusicPath());
            MusicPlayService.getInstance().getMediaPlayer().prepare();
            MusicPlayService.getInstance().getMediaPlayer().setLooping(false); //true-ról
            nameOfMusictv.setText(prev.getMusicName());
            artistOfMusictv.setText(prev.getMusicArtist());
            PlayerActivity.getPlayerActivity().musicSeekBar.setProgress(0);
            startprogress = 0; varminute = 0; varsecond = 0;
            PlayerActivity.getPlayerActivity().musicSeekBar.setMax(prev.getMusicDuration());
            int minutes = PlayerActivity.getPlayerActivity().getSeekBarMinutes(prev.getMusicDuration());
            int seconds = PlayerActivity.getPlayerActivity().getSeekBarSeconds(prev.getMusicDuration());
            PlayerActivity.getPlayerActivity().setSeekBarTextView(minutes,seconds);
            MusicPlayService.SwitchButtonListener sw = new MusicPlayService.SwitchButtonListener();
            sw.refreshNotification();
        }catch (Exception e){ e.printStackTrace();}
        prevactmusic[0] = prev.getMusicPath();
        prevactmusic[1] = prev.getMusicName();
        prevactmusic[2] = prev.getMusicAlbum();
        prevactmusic[3] = prev.getMusicArtist();
    }

    public void playNextMusic(){
        MusicPlayService.getInstance().getMediaPlayer().reset();  // stop vagy pause
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
            MusicPlayService.getInstance().getMediaPlayer().setLooping(false);
            nameOfMusictv.setText(next.getMusicName());
            artistOfMusictv.setText(next.getMusicArtist());
            PlayerActivity.getPlayerActivity().musicSeekBar.setProgress(0);
            startprogress = 0; varminute = 0; varsecond = 0;
            PlayerActivity.getPlayerActivity().musicSeekBar.setMax(next.getMusicDuration());
            int minutes = PlayerActivity.getPlayerActivity().getSeekBarMinutes(next.getMusicDuration());
            int seconds = PlayerActivity.getPlayerActivity().getSeekBarSeconds(next.getMusicDuration());
            PlayerActivity.getPlayerActivity().setSeekBarTextView(minutes,seconds);
            MusicPlayService.SwitchButtonListener sw = new MusicPlayService.SwitchButtonListener();
            sw.refreshNotification();
        }catch (Exception e){ e.printStackTrace();}
        nextactmusic[0] = next.getMusicPath();
        nextactmusic[1] = next.getMusicName();
        nextactmusic[2] =next.getMusicAlbum();
        nextactmusic[3] =next.getMusicArtist();
    }

    public void playOrPauseMusic(){
        if(playPauseButton.isChecked()){ //pause icon visible
            MusicPlayService.getInstance().getMediaPlayer().pause();
            length = MusicPlayService.getInstance().getMediaPlayer().getCurrentPosition();
        }
        else{  //play icon visible
            MusicPlayService.getInstance().getMediaPlayer().seekTo(length);
            MusicPlayService.getInstance().getMediaPlayer().start();
        }
    }
}
