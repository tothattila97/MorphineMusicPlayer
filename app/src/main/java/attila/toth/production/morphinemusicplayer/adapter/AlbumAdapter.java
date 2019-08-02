package attila.toth.production.morphinemusicplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import attila.toth.production.morphinemusicplayer.R;
import attila.toth.production.morphinemusicplayer.model.MusicModel;


public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    public List<MusicModel> albums;
    public final OnAlbumClickListener listener;

    public AlbumAdapter(OnAlbumClickListener listener){
        this.albums = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public AlbumAdapter.AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_album_layout, parent, false);
        AlbumViewHolder viewHolder = new AlbumViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AlbumAdapter.AlbumViewHolder holder, int position) {
        holder.position = position;
        holder.albumTV.setText(albums.get(position).getMusicAlbum());
        holder.artistTV.setText(albums.get(position).getMusicArtist());
    }

    public void addAlbum(MusicModel item){
        albums.add(item);
        notifyItemInserted(albums.size() -1);
        //notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {return albums.size();}

    //public MusicModel getMusic(int position){return albums.get(position);}

    public  class AlbumViewHolder extends RecyclerView.ViewHolder{

        int position;

        ImageView albumIV;
        TextView artistTV;
        TextView albumTV;

        public AlbumViewHolder(View itemView) {
            super(itemView);

            albumIV = itemView.findViewById(R.id.albumIV);
            artistTV = itemView.findViewById(R.id.artistTV);
            albumTV = itemView.findViewById(R.id.albumTV);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.OnAlbumClick(albums.get(position));
                    }
                }
            });
        }
    }

    public interface OnAlbumClickListener{
        void OnAlbumClick(MusicModel item);
    }
}
