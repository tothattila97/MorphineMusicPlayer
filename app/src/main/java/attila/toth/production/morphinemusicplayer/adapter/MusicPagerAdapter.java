package attila.toth.production.morphinemusicplayer.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import attila.toth.production.morphinemusicplayer.R;
import attila.toth.production.morphinemusicplayer.fragment.AlbumFragment;
import attila.toth.production.morphinemusicplayer.fragment.ArtistFragment;
import attila.toth.production.morphinemusicplayer.fragment.MusicFragment;


public class MusicPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    public MusicPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment ret = null;
        switch (position) {
            case 0:
                ret = new MusicFragment();
                break;
            case 1:
                ret = new AlbumFragment();
                break;
            case 2:
                ret = new ArtistFragment();
                break;
        }
        return ret;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch (position) {
            case 0:
                title = context.getString(R.string.szamok);
                break;
            case 1:
                title = context.getString(R.string.albumok);
                break;
            case 2:
                title = context.getString(R.string.eloadok);
                break;
            default:
                title = "";
        }
        return title;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
