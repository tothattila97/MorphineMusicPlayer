package attila.toth.production.morphinemusicplayer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import attila.toth.production.morphinemusicplayer.adapter.MusicPagerAdapter;
import attila.toth.production.morphinemusicplayer.fragment.MusicFragment;
import attila.toth.production.morphinemusicplayer.model.MusicModel;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private ViewPager viewPager;

    private ImageView backIV;
    private Toolbar toolbar;
    private android.speech.SpeechRecognizer sr;
    private SearchView searchView;
    private MusicPagerAdapter musicPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar= findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        backIV = findViewById(R.id.backIV);
        //Pager itt van beállítva fehérre
        PagerTabStrip pagerTabStrip = findViewById(R.id.pagerTabStrip);
        pagerTabStrip.setTabIndicatorColor(Color.WHITE);
        pagerTabStrip.setTextColor(Color.WHITE);

        sr = android.speech.SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new SpeechRecognizer());

        //Ennek nem biztos hogy itt a helye de működik...
        ViewPager mainViewPager =
                (ViewPager) findViewById(R.id.musicViewPager);
        musicPagerAdapter =
                new MusicPagerAdapter(getSupportFragmentManager(), this);
        mainViewPager.setAdapter(musicPagerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*ViewPager mainViewPager =
                (ViewPager) findViewById(R.id.musicViewPager);
        MusicPagerAdapter detailsPagerAdapter =
                new MusicPagerAdapter(getSupportFragmentManager(), this);
        mainViewPager.setAdapter(detailsPagerAdapter);*/
    }

    Menu mymenu = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mymenu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mytoolbarmenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                //Toast.makeText(getBaseContext(), "Megnyomtam a kereseset", Toast.LENGTH_SHORT).show();
                //TODO: Toolbarban levo kereses megvalositasa + hangfelismerés
                mymenu.clear();
                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.searchtoolbar, mymenu);
                getSupportActionBar().setDisplayShowTitleEnabled(false);

                SearchManager searchManager =
                        (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                searchView =
                        (SearchView) mymenu.findItem(R.id.action_search).getActionView();
                searchView.setSearchableInfo(
                        searchManager.getSearchableInfo(getComponentName()));
                //TODO: Ha megváltozik a SEARCHVIEW textje akkor a recycleviewt frissíteni kell
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        List<MusicModel> querylist = new ArrayList<MusicModel>();
                        for (int i= 0; i< MusicFragment.getMusicFragment().adapter.musics.size(); i++){
                            if(MusicFragment.getMusicFragment().adapter.musics.get(i).getMusicName().toLowerCase().contains(query.toLowerCase())){
                                querylist.add(MusicFragment.getMusicFragment().adapter.musics.get(i));
                            }
                        }
                        MusicFragment.getMusicFragment().adapter.musics = querylist;
                        MusicFragment.getMusicFragment().adapter.notifyDataSetChanged();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if(newText.equals("")){
                            MusicFragment.getMusicFragment().getAllMP3(MusicFragment.getMusicFragment().adapter);
                            MusicFragment.getMusicFragment().orderMP3(MusicFragment.getMusicFragment().adapter.musics);
                            MusicFragment.getMusicFragment().cutBeginAndEnd(MusicFragment.getMusicFragment().adapter.musics);
                        }
                        List<MusicModel> querylist = new ArrayList<MusicModel>();
                        for (int i= 0; i< MusicFragment.getMusicFragment().adapter.musics.size(); i++){
                            if(MusicFragment.getMusicFragment().adapter.musics.get(i).getMusicName().toLowerCase().contains(newText.toLowerCase())){
                                querylist.add(MusicFragment.getMusicFragment().adapter.musics.get(i));
                            }
                        }
                        MusicFragment.getMusicFragment().adapter.musics.clear();
                        MusicFragment.getMusicFragment().adapter.musics = querylist;
                        MusicFragment.getMusicFragment().adapter.notifyDataSetChanged();
                        return true;
                    }
                });
                break;

            case R.id.search_with_speech:
                //Toast.makeText(getBaseContext(), "Megnyomtam a hangfelismerest", Toast.LENGTH_SHORT).show();
                //TODO: elinditani a hangfelismerest

                /*sr = android.speech.SpeechRecognizer.createSpeechRecognizer(this);
                sr.setRecognitionListener(new SpeechRecognizer());*/

                if(isConnected()){
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hu-HU");
                    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "attila.toth.production.morphinemusicplayer");
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                    sr.startListening(intent);
                }
                else {
                    Toast.makeText(getBaseContext(), R.string.pleaseTurnOnWifi, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {

        try {
            if (sr != null) {
                sr.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    public  boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net!=null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    class SpeechRecognizer implements RecognitionListener{

        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
        }

        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
        }

        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged");
        }

        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech");
        }

        public void onError(int error) {
            Log.d(TAG, "error " + error);
        }

        public void onResults(Bundle results) {
            Log.d(TAG, "onResults " + results);
            ArrayList<String> data = results
                    .getStringArrayList(
                            android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
            //tvDetectedText.setText("");
            //tvDetectedText.setText(data.get(0));
            searchView.setQuery(data.get(0),false);
        }

        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }
    }
}
