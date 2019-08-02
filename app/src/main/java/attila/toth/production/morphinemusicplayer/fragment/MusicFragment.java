package attila.toth.production.morphinemusicplayer.fragment;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.futuremind.recyclerviewfastscroll.FastScroller;

import org.apache.commons.lang3.StringUtils;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import attila.toth.production.morphinemusicplayer.PlayerActivity;
import attila.toth.production.morphinemusicplayer.R;
import attila.toth.production.morphinemusicplayer.adapter.MusicAdapter;
import attila.toth.production.morphinemusicplayer.model.MusicModel;
import attila.toth.production.morphinemusicplayer.service.MusicPlayService;

public class MusicFragment extends Fragment {

    public RecyclerView recyclerView;
    public MusicAdapter adapter;

    public FastScroller fastScroller;


    public MusicPlayService musicPlayService;
    boolean bound = false;

    public static MusicFragment musicFragment = null;

    public MusicFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        fastScroller = view.findViewById(R.id.fastscroll);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MusicAdapter(new MusicAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(MusicModel item) {
                //elindítja a lejátszást mutató activityt
                Intent activityintent = new Intent(getContext(), PlayerActivity.class);
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
                Intent serviceIntent = new Intent(getActivity(), MusicPlayService.class);
                String path = item.getMusicPath();
                serviceIntent.putExtra(PlayerActivity.EXTRA_MUSIC, extra);
                serviceIntent.putExtra(PlayerActivity.EXTRA_MUSIC_DUR, extraduration);
                /**
                 * ezzel a foreground-os elindítással elszáll, gondolom le kell állítani
                 * getActivity().startForegroundService(serviceIntent);
                 *
                 * valami bindService fgv-t kellene hasznalni
                 */
                getActivity().bindService(serviceIntent, new ServiceConnection() {
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
                getActivity().startService(serviceIntent);
            }
        });

        getAllMP3(adapter);

        orderMP3(adapter.musics);
        cutBeginAndEnd(adapter.musics);

        recyclerView.setAdapter(adapter);

        fastScroller.setRecyclerView(recyclerView);

        return view;
    }

    public static MusicFragment getMusicFragment(){return musicFragment;}

    public void getAllMP3(MusicAdapter adapter) {

        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cursor = getActivity().getContentResolver().query(allsongsuri, null, selection, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    MusicModel music = new MusicModel();
                    music.setMusicName(cursor
                            .getString(cursor
                                    .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                    //int song_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                    music.setMusicPath(cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA)));
                    //fullsongpath.add(fullpath);

                    music.setMusicAlbum(cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                    //int album_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                    //music.setMusicAlbumArt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));

              //TODO valahogy fixelni ezt a képlekérést az adott számhoz és a beállítást a MusicAdapterben
                   MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    byte[] rawArt;
                    Bitmap art;
                    BitmapFactory.Options bfo=new BitmapFactory.Options();

                    mmr.setDataSource(getContext(), Uri.parse(music.getMusicPath()));
                    rawArt = mmr.getEmbeddedPicture();
                    if (null != rawArt) {
                        art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
                        Bitmap scaled = Bitmap.createScaledBitmap(art, (int)pxFromDp(48), (int) pxFromDp(48), true);
                        music.setAlbumArt(scaled);
                    }

                    music.setMusicArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                    //int artist_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));

                    music.setMusicDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));

                    music.setMusicTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    adapter.addMusic(music);

                } while (cursor.moveToNext());

            }
            cursor.close();

        }
    }

    private float pxFromDp(float dp)
    {
        return dp * getResources().getDisplayMetrics().density;
    }

    public void orderMP3(List<MusicModel> orderable){
        /*String Hungarian = "< a,A < á,Á < b,B < c,C < cs,Cs,CS < d,D < dz,Dz,DZ < dzs,Dzs,DZS" +
                " < e,E < é,É < f,F < g,G < gy,Gy,GY < h,H < i,I < í,Í < j,J" +
                " < k,K < l,L < ly,Ly,LY < m,M < n,N < ny,Ny,NY < o,O < ó,Ó" +
                " < ö,Ö < ő,Ő < p,P < q,Q < r,R < s,S < sz,Sz,SZ < t,T" +
                " < ty,Ty,TY < u,U < ú,Ú < ü,Ü < ű,Ű < v,V < w,W < x,X < y,Y < z,Z < zs,Zs,ZS";
        try {
            RuleBasedCollator ruleBasedCollator = new RuleBasedCollator(Hungarian);
            sortStrings(ruleBasedCollator,orderable);
        } catch (ParseException pe) {
            System.out.println("Parse exception for rules");
        }*/
        if (orderable.size() > 0) {
            Collections.sort(orderable, new Comparator<MusicModel>() {
                @Override
                public int compare(final MusicModel object1, final MusicModel object2) {
                    return object1.getMusicTitle().compareTo(object2.getMusicTitle());
                }
            });
        }
    }

    public void sortStrings(Collator collator, List<MusicModel> sortable){
        MusicModel temp;
        for (int i = 0; i< sortable.size() -1 ; i++){
            if(collator.compare(sortable.get(i).getMusicTitle() , sortable.get(i+1).getMusicTitle()) > 0){
                temp = sortable.get(i);
                sortable.add(i, sortable.get(i+1));
                sortable.add(i+1, temp);
            }
        }
    }

    public void cutBeginAndEnd(List<MusicModel> cuttable){

        for(int i = 0; i< cuttable.size(); i++){
            cuttable.get(i).setMusicName(StringUtils.removeEnd(cuttable.get(i).getMusicName(), ".mp3"));
            cuttable.get(i).setMusicName(StringUtils.removeAll(cuttable.get(i).getMusicName(), "[0-9]"));
            StringUtils.trim(cuttable.get(i).getMusicName());
        }
    }

}
