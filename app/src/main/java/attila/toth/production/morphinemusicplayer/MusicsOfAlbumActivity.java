package attila.toth.production.morphinemusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import attila.toth.production.morphinemusicplayer.adapter.MusicAdapter;
import attila.toth.production.morphinemusicplayer.fragment.AlbumFragment;
import attila.toth.production.morphinemusicplayer.fragment.MusicFragment;
import attila.toth.production.morphinemusicplayer.model.MusicModel;
import attila.toth.production.morphinemusicplayer.service.MusicPlayService;

public class MusicsOfAlbumActivity extends AppCompatActivity {

    public RecyclerView musicsOfAlbumRecyclerView;
    public MusicAdapter adapter;

    public MusicPlayService musicPlayService;
    public boolean bound;

    String albumextra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musics_of_album);

        Bundle bundle = getIntent().getExtras();
        albumextra = bundle.getString(AlbumFragment.ALBUM_TAG);

        //Toast.makeText(getBaseContext(), albumextra, Toast.LENGTH_SHORT).show();

        musicsOfAlbumRecyclerView = findViewById(R.id.musicsOfAlbumRecycler);
        musicsOfAlbumRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MusicAdapter(new MusicAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(MusicModel item) {
                Intent activityintent = new Intent(getApplicationContext(), PlayerActivity.class);
                String[] extra=  new String[5];
                extra[0]= item.getMusicPath();
                extra[1] = item.getMusicName();
                extra[2] = item.getMusicAlbum();
                extra[3] = item.getMusicArtist();
                extra[4] = item.getMusicTitle();
                int extraduration = item.getMusicDuration();
                activityintent.putExtra(PlayerActivity.EXTRA_MUSIC, extra);
                activityintent.putExtra(PlayerActivity.EXTRA_MUSIC_DUR, extraduration);
                startActivity(activityintent);
                //TODO: elindítani a háttérbe egy binded servicet is
                Intent serviceIntent = new Intent(getApplicationContext(), MusicPlayService.class);
                String path = item.getMusicPath();
                serviceIntent.putExtra(PlayerActivity.EXTRA_MUSIC, extra);
                serviceIntent.putExtra(PlayerActivity.EXTRA_MUSIC_DUR, extraduration);
                /**
                 * ezzel a foreground-os elindítással elszáll, gondolom le kell állítani
                 * getActivity().startForegroundService(serviceIntent);
                 *
                 * valami bindService fgv-t kellene hasznalni
                 */
                bindService(serviceIntent, new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        MusicPlayService.MusicPlayBinder musicPlayBinder = (MusicPlayService.MusicPlayBinder) service;
                        musicPlayService = musicPlayBinder.getService();
                        //MusicPlayService.getInstance();
                        bound = true;
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        bound = false;
                    }
                }, Context.BIND_AUTO_CREATE);
                startService(serviceIntent);
            }
        });

        getMusicsOfSpecificAlbum(adapter, albumextra);
        MusicFragment.getMusicFragment().orderMP3(adapter.musics);

        musicsOfAlbumRecyclerView.setAdapter(adapter);
    }

    public void getMusicsOfSpecificAlbum(MusicAdapter adapter,String album){

        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] selectionArgs = {album};
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0" + " AND " + MediaStore.Audio.Media.ALBUM + " =?";
        Cursor cursor = getContentResolver().query(allsongsuri,null, selection, selectionArgs, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    MusicModel music = new MusicModel();
                    music.setMusicName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                    music.setMusicPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                    music.setMusicAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                    music.setMusicArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                    music.setMusicDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                    music.setMusicTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    adapter.addMusic(music);
                } while (cursor.moveToNext());

            }
            cursor.close();

        }

    }
}
