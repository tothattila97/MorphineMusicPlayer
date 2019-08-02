package attila.toth.production.morphinemusicplayer.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import attila.toth.production.morphinemusicplayer.MusicsOfAlbumActivity;
import attila.toth.production.morphinemusicplayer.R;
import attila.toth.production.morphinemusicplayer.adapter.AlbumAdapter;
import attila.toth.production.morphinemusicplayer.model.MusicModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends Fragment {

    public static final String ALBUM_TAG = "ALBUM_FRAGMENT_TAG";

    public RecyclerView albumRecyclerView;
    public AlbumAdapter albumAdapter;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_album, container, false);
        int numberOfColumns = 3;
        albumRecyclerView = view.findViewById(R.id.albumRecyclerView);
        albumRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        albumAdapter = new AlbumAdapter(new AlbumAdapter.OnAlbumClickListener() {
            @Override
            public void OnAlbumClick(MusicModel item) {
                Intent albumintent = new Intent(getContext(), MusicsOfAlbumActivity.class);
                albumintent.putExtra(ALBUM_TAG, item.getMusicAlbum());
                startActivity(albumintent);
            }
        });

        getAllAlbumsAndDetails(albumAdapter);

        albumRecyclerView.setAdapter(albumAdapter);

        return view;
    }


    public void getAllAlbumsAndDetails(AlbumAdapter adapter) {

        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {"DISTINCT " + MediaStore.Audio.Media.ALBUM};
        Cursor cursor = getActivity().getContentResolver().query(allsongsuri,projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    MusicModel music = new MusicModel();
                    //music.setMusicName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                    //music.setMusicPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                    music.setMusicAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                    //music.setMusicArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                    //music.setMusicDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                    //music.setMusicTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    adapter.addAlbum(music);
                } while (cursor.moveToNext());

            }
            cursor.close();

        }
    }

}
