package attila.toth.production.morphinemusicplayer.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.util.ArrayList;
import java.util.List;

import attila.toth.production.morphinemusicplayer.R;
import attila.toth.production.morphinemusicplayer.model.MusicModel;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> implements SectionTitleProvider{

    public List<MusicModel> musics;
    public final OnItemClickListener listener;

    public MusicAdapter(OnItemClickListener listen) {
        this.listener = listen;
        musics = new ArrayList<>();
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_music, parent, false);
        MusicViewHolder viewHolder = new MusicViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MusicViewHolder holder, int position) {
        holder.position = position;
        //TODO: eltárolni album képét
        if(musics.get(position).getAlbumArt() != null){
            holder.albumImageView.setImageBitmap(musics.get(position).getAlbumArt());
        }
        else {
            //TODO ha nincs hozzá akkor ne töltse be
            holder.albumImageView.setImageResource(R.drawable.ic_music_note_grey600_48dp);
        }
        holder.nameTextView.setText(musics.get(position).getMusicTitle());
        holder.albumTextView.setText(musics.get(position).getMusicAlbum());
    }

    @Override
    public int getItemCount() {
        return musics.size();
    }

    public void addMusic(MusicModel item){
        musics.add(item);
        notifyItemInserted(musics.size() -1);
        //notifyDataSetChanged();
    }

    public MusicModel getMusic(int position){
        return musics.get(position);
    }

    @Override
    public String getSectionTitle(int position) {
        return getMusic(position).getMusicTitle().substring(0, 1);
    }

    public  class MusicViewHolder extends RecyclerView.ViewHolder{

        int position;

        ImageView albumImageView;
        TextView nameTextView;
        TextView albumTextView;

        public MusicViewHolder(View itemView) {
            super(itemView);

            albumImageView = itemView.findViewById(R.id.albumImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            albumTextView = itemView.findViewById(R.id.albumTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.OnItemClick(musics.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void OnItemClick(MusicModel item);
    }

}

