/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dmplayer.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.utility.vk.VkMusicHelper;
import com.dmplayer.utility.vk.VkProfileHelper;
import com.dmplayer.manager.MediaController;
import com.dmplayer.manager.NotificationManager;
import com.dmplayer.models.SongDetail;
import com.dmplayer.ui.observablelib.ObservableScrollView;
import com.dmplayer.ui.observablelib.ObservableScrollViewCallbacks;
import com.dmplayer.ui.observablelib.ScrollState;
import com.dmplayer.ui.observablelib.ScrollUtils;
import com.dmplayer.phonemedia.PhoneMediaControl;
import com.dmplayer.ui.slidinguppanel.SlidingUpPanelLayout;
import com.dmplayer.ui.ExpandableHeightListView;
import com.dmplayer.ui.PlayPauseView;
import com.dmplayer.ui.Slider;
import com.dmplayer.utility.DMPlayerUtility;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements View.OnClickListener,
        ObservableScrollViewCallbacks,
        Slider.OnValueChangedListener,
        NotificationManager.NotificationCenterDelegate {

    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;

    private SharedPreferences sp;
    private int color = 0xFFFFFF;
    private Context context;

    private long id = -1;
    private long tagFor = -1;
    private String albumname = "";
    private String title_one = "";
    private String title_sec = "";

    private VkMusicHelper vkMusicHelper;
    private int vkType = -1;
    private String vkAlbumId;
    private String vkPlaylistName;

    private ImageView banner;
    private ImageView fab_button;
    private TextView displayMainString, displayFirstSubString, displaySecondSubString;
    private ExpandableHeightListView songsList;
    private AllSongsListAdapter mSongsListAdapter;
    private List<SongDetail> songList = new ArrayList<>();

    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set your theme first
        context = PlaylistActivity.this;
        theme();

        //Set your Layout view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        initialize();
        getBundleValues();

        initSlidingUpPanel();

        loadAlreadyPlaying();
        addObserver();
        fabanim();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isExpand) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
            overridePendingTransition(0, 0);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeObserver();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bottombar_play:

            case R.id.btn_play:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    PlayPauseEvent(v);
                break;

            case R.id.btn_forward:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    MediaController.getInstance().playNextSong();
                break;

            case R.id.btn_backward:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    MediaController.getInstance().playPreviousSong();
                break;

            case R.id.btn_suffel:

                break;

            case R.id.btn_toggle:

                break;

            case R.id.bottombar_img_Favorite:
                if (MediaController.getInstance().getPlayingSongDetail() != null) {
                    MediaController.getInstance().storeFavoritePlay(context, MediaController.getInstance().getPlayingSongDetail(), v.isSelected() ? 0 : 1);
                    v.setSelected(!v.isSelected());
                    DMPlayerUtility.animateHeartButton(v);
                    findViewById(R.id.like).setSelected(!v.isSelected());
                    DMPlayerUtility.animatePhotoLike(findViewById(R.id.big_like), findViewById(R.id.like));
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = color;
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(banner, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() { }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) { }

    private void setupVkMusicHelper() {
        SharedPreferences sp = getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        vkMusicHelper = new VkMusicHelper.Builder(this)
                .setLogged(sp.getBoolean(VkProfileHelper.SP_LOGGED, false))
                .setUserId(sp.getString(VkProfileHelper.SP_USER_ID, ""))
                .setToken(sp.getString(VkProfileHelper.SP_ACCESS_TOKEN, ""))
                .build();
    }

    //Catch  theme changed from settings
    public void theme() {
        sp = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        int theme = sp.getInt("THEME", 0);
        DMPlayerUtility.settingTheme(context, theme);
    }

    private void initialize() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbarView = findViewById(R.id.toolbar_actionbar);

        // Setup RecyclerView inside drawer
        final TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        color = typedValue.data;

        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, color));
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.addScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        banner = (ImageView) findViewById(R.id.banner);
        displayMainString = (TextView) findViewById(R.id.tv_albumname);
        displayFirstSubString = (TextView) findViewById(R.id.tv_title_frst);
        displaySecondSubString = (TextView) findViewById(R.id.tv_title_sec);
        songsList = (ExpandableHeightListView) findViewById(R.id.listView_songs);
        mSongsListAdapter = new AllSongsListAdapter(context);

        songsList.setAdapter(mSongsListAdapter);

        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

        try {
            fab_button = (ImageView) findViewById(R.id.fab_button);
            fab_button.setColorFilter(color);
            fab_button.setImageAlpha(255);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setupVkMusicHelper();
    }

    private void getBundleValues() {
        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            tagFor = bundle.getLong("tagfor");
//
//            if (tagFor == PhoneMediaControl.SongsLoadFor.VK_PLAYLIST.ordinal()) {
//                vkType = bundle.getInt("playlisttype");
//                vkPlaylistName = bundle.getString("playlistname");
//                vkAlbumId = bundle.getString("playlistid");
//
//                title_one = bundle.getString("title_one");
//            } else if (tagFor == PhoneMediaControl.SongsLoadFor.LOCAL_PLAYLIST.ordinal()) {
//
//            } else {
//                id = bundle.getLong("id");
//                tagFor = bundle.getLong("tagfor");
//                albumname = bundle.getString("albumname");
//                title_one = bundle.getString("title_one");
//                title_sec = bundle.getString("title_sec");
//            }
//        }
//
//        if (tagFor == PhoneMediaControl.SongsLoadFor.GENRE.ordinal()) {
//            loadSongsGenres(id);
//        } else if (tagFor == PhoneMediaControl.SongsLoadFor.ALBUM.ordinal()) {
//            loadSongsAlbum(id);
//        } else if (tagFor == PhoneMediaControl.SongsLoadFor.ARTIST.ordinal()) {
//            loadSongsArtist(id);
//        } else if (tagFor == PhoneMediaControl.SongsLoadFor.LOCAL_PLAYLIST.ordinal()) {
//
//        } else if (tagFor == PhoneMediaControl.SongsLoadFor.VK_PLAYLIST.ordinal()) {
//            loadVkPlaylist(vkType, vkAlbumId, vkPlaylistName);
//        }
//
//        displayMainString.setText(albumname);
//        displayFirstSubString.setText(title_one);
//        displaySecondSubString.setText(title_sec);
    }

    private void loadSongsAlbum(long id) {
        PhoneMediaControl.setPhoneMediaControlInterface(new PhoneMediaControl.PhoneMediaControlInterface() {

            @Override
            public void loadSongsComplete(List<SongDetail> songsList_) {
                songList = songsList_;
                mSongsListAdapter.notifyDataSetChanged();
                if (songList != null && songList.size() >= 1) {
                    displaySecondSubString.setText(songList.size() + " songs");
                }
            }
        });
        mPhoneMediaControl.loadMusicListAsync(context, id, PhoneMediaControl.SongsLoadFor.ALBUM, "");

        String contentURI = "content://media/external/audio/albumart/" + id;
        imageLoader.displayImage(contentURI, banner, options);
    }

    private void loadSongsArtist(long id) {
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhoneMediaControlInterface(new PhoneMediaControl.PhoneMediaControlInterface() {

            @Override
            public void loadSongsComplete(List<SongDetail> songsList_) {
                songList = songsList_;
                mSongsListAdapter.notifyDataSetChanged();
                if (songList != null && songList.size() >= 1) {
                    String contentURI = "content://media/external/audio/media/" + songList.get(0).getId() + "/albumart";
                    imageLoader.displayImage(contentURI, banner, options);
                }
            }
        });
        mPhoneMediaControl.loadMusicListAsync(context, id, PhoneMediaControl.SongsLoadFor.ARTIST, "");
    }

    private void loadSongsGenres(long id) {
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhoneMediaControlInterface(new PhoneMediaControl.PhoneMediaControlInterface() {

            @Override
            public void loadSongsComplete(List<SongDetail> songsList_) {
                songList = songsList_;
                mSongsListAdapter.notifyDataSetChanged();
                if (songList != null && songList.size() >= 1) {
                    String contentURI = "content://media/external/audio/media/" + songList.get(0).getId() + "/albumart";
                    imageLoader.displayImage(contentURI, banner, options);
                    displaySecondSubString.setText(songList.size() + " songs");
                }
            }
        });
        mPhoneMediaControl.loadMusicListAsync(context, id, PhoneMediaControl.SongsLoadFor.GENRE, "");
    }

    private void loadSongsLocalPlaylist(long id) { }

    private void loadVkPlaylist(final int type, final String id, final String name){
        PhoneMediaControl.setPhoneMediaControlInterface(new PhoneMediaControl.PhoneMediaControlInterface() {

            @Override
            public void loadSongsComplete(final List<SongDetail> songsList_) {
                songList = songsList_;
                mSongsListAdapter.notifyDataSetChanged();

                if (songList != null && songList.size() >= 1) {
                    imageLoader.displayImage("", banner, options);
                    displayMainString.setText(name);
                }
            }
        });

        new LoadPlaylistTask().execute();
    }

    private class LoadPlaylistTask extends AsyncTask<Void, Void, List<SongDetail>> {

        @Override
        protected List<SongDetail> doInBackground(Void... params) {
//            return vkMusicHelper.loadMusicList(vkType, vkAlbumId, vkPlaylistName)
//                    .getSongsList();
            return null;
        }

        @Override
        protected void onPostExecute(List<SongDetail> songDetails) {
            super.onPostExecute(songDetails);

            mPhoneMediaControl.loadMusicListAsync(songDetails);
        }
    }

    public class AllSongsListAdapter extends BaseAdapter {
        private Context context = null;
        private LayoutInflater layoutInflater;

        public AllSongsListAdapter(Context mContext) {
            this.context = mContext;
            this.layoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder mViewHolder;
            if (convertView == null) {
                mViewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_song, null);
                mViewHolder.song_row = (LinearLayout) convertView.findViewById(R.id.song_row);
                mViewHolder.textViewSongName = (TextView) convertView.findViewById(R.id.song_name);
                mViewHolder.textViewSongArtisNameAndDuration = (TextView) convertView.findViewById(R.id.song_details);
                mViewHolder.imageSongThm = (ImageView) convertView.findViewById(R.id.song_icon_art);
                mViewHolder.imagemore = (ImageView) convertView.findViewById(R.id.song_icon_option_more);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            SongDetail mDetail = songList.get(position);

            String audioDuration = "";
            try {
                audioDuration = DMPlayerUtility.getAudioDuration(Long.parseLong(mDetail.getDuration()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            mViewHolder.textViewSongArtisNameAndDuration.setText((audioDuration.isEmpty() ? "" : audioDuration + " | ") + mDetail.getArtist());
            mViewHolder.textViewSongName.setText(mDetail.getTitle());
            String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";
            imageLoader.displayImage(contentURI, mViewHolder.imageSongThm, options);

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    SongDetail mDetail = songList.get(position);
                    if (mDetail != null) {
                        if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
                            MediaController.getInstance().pauseAudio(mDetail);
                        } else {
                            MediaController.getInstance().setPlaylist(songList, mDetail, (int) tagFor, (int) id);
                        }
                    }

                }
            });
            mViewHolder.imagemore.setColorFilter(Color.DKGRAY);
            mViewHolder.imagemore.setImageAlpha(255);

            mViewHolder.imagemore.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        PopupMenu popup = new PopupMenu(context, v);
                        popup.getMenuInflater().inflate(R.menu.list_item_option, popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.playnext:
                                        break;
                                    case R.id.addtoque:
                                        break;
                                    case R.id.addtoplaylist:
                                        break;
                                    case R.id.gotoartis:
                                        break;
                                    case R.id.gotoalbum:
                                        break;
                                    case R.id.delete:
                                        break;
                                    default:
                                        break;
                                }

                                return true;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return (songList != null) ? songList.size() : 0;
        }

        class ViewHolder {
            TextView textViewSongName;
            ImageView imageSongThm, imagemore;
            TextView textViewSongArtisNameAndDuration;
            LinearLayout song_row;
        }
    }

    /*-----------------ALL Work Related to Slide Panel-----------------*/

    private static final String TAG = "ActivityPlaylist";
    private SlidingUpPanelLayout mLayout;
    private RelativeLayout slidepanelchildtwo_topviewone;
    private RelativeLayout slidepanelchildtwo_topviewtwo;
    private boolean isExpand = false;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageView songAlbumbg;
    private ImageView img_bottom_slideone;
    private ImageView img_bottom_slidetwo;
    private TextView txt_playesongname;
    private TextView txt_songartistname;

    private TextView txt_playesongname_slidetoptwo;
    private TextView txt_songartistname_slidetoptwo;

    private TextView txt_timeprogress;
    private TextView txt_timetotal;
    private ImageView imgbtn_backward;
    private ImageView imgbtn_forward;
    private ImageView imgbtn_toggle;
    private ImageView imgbtn_suffel;
    private ImageView img_Favorite;
    private PlayPauseView btn_playpause;
    private PlayPauseView btn_playpausePanel;
    private Slider audio_progress;
    private boolean isDragingStart = false;
    private int TAG_Observer;
//imp
    private void initSlidingUpPanel() {
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        // songAlbumbg = (ImageView) findViewById(R.id.image_songAlbumbg);
        songAlbumbg = (ImageView) findViewById(R.id.image_songsAlbum);
        img_bottom_slideone = (ImageView) findViewById(R.id.img_bottom_slideone);
        img_bottom_slidetwo = (ImageView) findViewById(R.id.img_bottom_slidetwo);
        txt_timeprogress = (TextView) findViewById(R.id.slidepanel_time_progress);
        txt_timetotal = (TextView) findViewById(R.id.slidepanel_time_total);
        imgbtn_backward = (ImageView) findViewById(R.id.btn_backward);
        imgbtn_forward = (ImageView) findViewById(R.id.btn_forward);
        imgbtn_toggle = (ImageView) findViewById(R.id.btn_toggle);
        imgbtn_suffel = (ImageView) findViewById(R.id.btn_suffel);
        btn_playpause = (PlayPauseView) findViewById(R.id.btn_play);
        audio_progress = (Slider) findViewById(R.id.audio_progress_control);
        btn_playpausePanel = (PlayPauseView) findViewById(R.id.bottombar_play);
        img_Favorite = (ImageView) findViewById(R.id.bottombar_img_Favorite);

        TypedValue typedvaluecoloraccent = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedvaluecoloraccent, true);
        final int coloraccent = typedvaluecoloraccent.data;
        audio_progress.setBackgroundColor(coloraccent);
        audio_progress.setValue(0);

        audio_progress.setOnValueChangedListener(this);
        imgbtn_backward.setOnClickListener(this);
        imgbtn_forward.setOnClickListener(this);
        imgbtn_toggle.setOnClickListener(this);
        imgbtn_suffel.setOnClickListener(this);
        img_Favorite.setOnClickListener(this);

        btn_playpausePanel.Pause();
        btn_playpause.Pause();

        txt_playesongname = (TextView) findViewById(R.id.txt_playesongname);
        txt_songartistname = (TextView) findViewById(R.id.txt_songartistname);
        txt_playesongname_slidetoptwo = (TextView) findViewById(R.id.txt_playesongname_slidetoptwo);
        txt_songartistname_slidetoptwo = (TextView) findViewById(R.id.txt_songartistname_slidetoptwo);

        slidepanelchildtwo_topviewone = (RelativeLayout) findViewById(R.id.slidepanelchildtwo_topviewone);
        slidepanelchildtwo_topviewtwo = (RelativeLayout) findViewById(R.id.slidepanelchildtwo_topviewtwo);

        slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
        slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);

        slidepanelchildtwo_topviewone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

            }
        });

        slidepanelchildtwo_topviewtwo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            }
        });

        ((PlayPauseView) findViewById(R.id.bottombar_play)).setOnClickListener(this);
        ((PlayPauseView) findViewById(R.id.btn_play)).setOnClickListener(this);

        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);

                if (slideOffset == 0.0f) {
                    isExpand = false;
                    slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
                    slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);
                } else if (slideOffset > 0.0f && slideOffset < 1.0f) {
                    // if (isExpand) {
                    // slidepanelchildtwo_topviewone.setAlpha(1.0f);
                    // slidepanelchildtwo_topviewtwo.setAlpha(1.0f -
                    // slideOffset);
                    // } else {
                    // slidepanelchildtwo_topviewone.setAlpha(1.0f -
                    // slideOffset);
                    // slidepanelchildtwo_topviewtwo.setAlpha(1.0f);
                    // }

                } else {
                    isExpand = true;
                    slidepanelchildtwo_topviewone.setVisibility(View.INVISIBLE);
                    slidepanelchildtwo_topviewtwo.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");
                isExpand = true;
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
                isExpand = false;
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });

    }

    private void loadAlreadyPlaying() {
        SongDetail mSongDetail = MediaController.getInstance().getPlayingSongDetail();
        if (mSongDetail != null) {
            loadSongsDetails(mSongDetail);
            updateTitle(false);
            MediaController.getInstance().checkIsFavorite(context, mSongDetail, img_Favorite);
        }
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }

    }

    public void addObserver() {
        TAG_Observer = MediaController.getInstance().generateObserverTag();
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioDidReset);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioPlayStateChanged);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioDidStarted);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioProgressDidChanged);
        NotificationManager.getInstance().addObserver(this, NotificationManager.newaudioloaded);
    }

    public void removeObserver() {
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioDidReset);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioPlayStateChanged);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioDidStarted);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioProgressDidChanged);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.newaudioloaded);
    }

    public void loadSongsDetails(SongDetail mDetail) {
        String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";
        imageLoader.displayImage(contentURI, songAlbumbg, options, animateFirstListener);
        imageLoader.displayImage(contentURI, img_bottom_slideone, options, animateFirstListener);
        imageLoader.displayImage(contentURI, img_bottom_slidetwo, options, animateFirstListener);

        txt_playesongname.setText(mDetail.getTitle());
        txt_songartistname.setText(mDetail.getArtist());
        txt_playesongname_slidetoptwo.setText(mDetail.getTitle());
        txt_songartistname_slidetoptwo.setText(mDetail.getArtist());

        if (txt_timetotal != null) {
            long duration = Long.valueOf(mDetail.getDuration());
            txt_timetotal.setText(duration != 0 ? String.format("%d:%02d", duration / 60, duration % 60) : "-:--");
        }
        updateProgress(mDetail);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationManager.audioDidStarted || id == NotificationManager.audioPlayStateChanged || id == NotificationManager.audioDidReset) {
            updateTitle(id == NotificationManager.audioDidReset && (Boolean) args[1]);
        } else if (id == NotificationManager.audioProgressDidChanged) {
            SongDetail mSongDetail = MediaController.getInstance().getPlayingSongDetail();
            updateProgress(mSongDetail);
        }
    }

    @Override
    public void newSongLoaded(Object... args) {
        MediaController.getInstance().checkIsFavorite(context, (SongDetail) args[0], img_Favorite);
    }

    private void updateTitle(boolean shutdown) {
        SongDetail mSongDetail = MediaController.getInstance().getPlayingSongDetail();
        if (mSongDetail == null && shutdown) {
            return;
        } else {
            updateProgress(mSongDetail);
            if (MediaController.getInstance().isAudioPaused()) {
                btn_playpausePanel.Pause();
                btn_playpause.Pause();
            } else {
                btn_playpausePanel.Play();
                btn_playpause.Play();
            }
            SongDetail audioInfo = MediaController.getInstance().getPlayingSongDetail();
            loadSongsDetails(audioInfo);

            if (txt_timetotal != null) {
                long duration = Long.valueOf(audioInfo.getDuration());
                txt_timetotal.setText(duration != 0 ? String.format("%d:%02d", duration / 60, duration % 60) : "-:--");
            }
        }
    }

    private void updateProgress(SongDetail mSongDetail) {
        if (audio_progress != null && mSongDetail != null) {
            // When SeekBar Dragging Don't Show Progress
            if (!isDragingStart) {
                // Progress Value coming in point it range 0 to 1
                audio_progress.setValue((int) (mSongDetail.audioProgress * 100));
            }
            String timeString = String.format("%d:%02d", mSongDetail.audioProgressSec / 60, mSongDetail.audioProgressSec % 60);
            txt_timeprogress.setText(timeString);
        }
    }

    private void PlayPauseEvent(View v) {
        if (MediaController.getInstance().isAudioPaused()) {
            MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingSongDetail());
            ((PlayPauseView) v).Play();
        } else {
            MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
            ((PlayPauseView) v).Pause();
        }
    }

    @Override
    public void onValueChanged(int value) {
        MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingSongDetail(), (float) value / 100);
    }


    private void fabanim() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(fab_button, "scaleX", 0.0f, 1.0f);
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(fab_button, "scaleY", 0.0f, 1.0f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(fab_button, "alpha", 0.0f, 1.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(anim, anim1, anim2);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

}
