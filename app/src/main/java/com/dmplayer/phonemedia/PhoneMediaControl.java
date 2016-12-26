/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.phonemedia;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import com.dmplayer.db.FavoritePlayTableHelper;
import com.dmplayer.db.MostAndRecentPlayTableHelper;
import com.dmplayer.manager.MediaController;
import com.dmplayer.models.SongDetail;

import java.util.ArrayList;
import java.util.List;

import static com.dmplayer.utility.DMPlayerUtility.closeCursor;

public class PhoneMediaControl {
    private Cursor cursor = null;
    private static volatile PhoneMediaControl Instance = null;

    private static final String TAG = "PhoneMediaControl";

    public enum SongsLoadFor {
        ALL, GENRE, ARTIST, ALBUM, MUSIC_INTENT, MOST_PLAY, FAVORITE, LOCAL_PLAYLIST
    }

    public static PhoneMediaControl getInstance() {
        PhoneMediaControl localInstance = Instance;
        if (localInstance == null) {
            synchronized (MediaController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new PhoneMediaControl();
                }
            }
        }
        return localInstance;
    }

    @Deprecated
    public void loadMusicListAsync(final Context context, final long id,
                                   final SongsLoadFor songsloadfor, final String path) {
        new AsyncTask<Void, Void, Void>() {
            List<SongDetail> songsList = null;

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    songsList = getList(context, id, songsloadfor, path);
                } catch (Exception e) {
                    closeCursor(cursor);
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (phonemediacontrolinterface != null) {
                    phonemediacontrolinterface.loadSongsComplete(songsList);
                }
            }
        }.execute();
    }

    public List<SongDetail> loadMusicList(final Context context, final long id,
                                          final SongsLoadFor songsloadfor, final String path)
            throws IllegalThreadStateException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalThreadStateException("Method mustn't run on UI thread!");
        }

        List<SongDetail> playlist = null;
        try {
            playlist = getList(context, id, songsloadfor, path);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            //TODO: ??
            closeCursor(cursor);
        }

        return playlist;
    }

    public void loadMusicListAsync(List<SongDetail> songsList) {
        if (phonemediacontrolinterface != null) {
            phonemediacontrolinterface.loadSongsComplete(songsList);
        }
    }

    public List<SongDetail> getList(final Context context, final long id, final SongsLoadFor songsLoadFor, final String path) {
        List<SongDetail> songsList = new ArrayList<>();
        final String[] projectionSongs = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION};

        String sortOrder;
        String selection;
        switch (songsLoadFor) {
            case ALL:
                selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";
                sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
                cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projectionSongs, selection, null, sortOrder);

                songsList = getSongsFromCursor(cursor);
                break;

            case GENRE:
                Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", id);
                sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
                cursor = context.getContentResolver().query(uri,
                        projectionSongs, null, null, sortOrder);

                songsList = getSongsFromCursor(cursor);
                break;

            case ARTIST:
                selection = MediaStore.Audio.Media.ARTIST_ID + "=" + id + " AND " + MediaStore.Audio.Media.IS_MUSIC + "=1";
                sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
                cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projectionSongs, selection, null, sortOrder);

                songsList = getSongsFromCursor(cursor);
                break;

            case ALBUM:
                selection = MediaStore.Audio.Media.ALBUM_ID + "=" + id + " AND " + MediaStore.Audio.Media.IS_MUSIC + "=1";
                sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
                cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projectionSongs, selection, null, sortOrder);

                songsList = getSongsFromCursor(cursor);
                break;

            case MUSIC_INTENT:
                selection = MediaStore.Audio.Media.DATA + "='" + path + "' AND " + MediaStore.Audio.Media.IS_MUSIC + "=1";
                sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
                cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projectionSongs, selection, null, sortOrder);

                songsList = getSongsFromCursor(cursor);
                break;

            case MOST_PLAY:
                cursor = MostAndRecentPlayTableHelper.getInstance(context).getMostPlayed();
                songsList = getSongsFromSQLDBCursor(cursor);
                break;

            case FAVORITE:
                cursor = FavoritePlayTableHelper.getInstance(context).getFavoriteSongList();
                songsList = getSongsFromSQLDBCursor(cursor);
                break;
        }

        return songsList;
    }

    private List<SongDetail> getSongsFromCursor(Cursor cursor) {
        ArrayList<SongDetail> songs = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() >= 1) {
                int _id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int album_id = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int display_name = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

                while (cursor.moveToNext()) {
                    final int ID = cursor.getInt(_id);
                    final String ARTIST = cursor.getString(artist);
                    final String TITLE = cursor.getString(title);
                    final String DISPLAY_NAME = cursor.getString(display_name);
                    final String DURATION = cursor.getString(duration);
                    final String PATH = cursor.getString(data);

                    SongDetail song = new SongDetail(ID, album_id, ARTIST, TITLE, PATH, DISPLAY_NAME, DURATION);
                    songs.add(song);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            closeCursor(cursor);
        }
        return songs;
    }

    private List<SongDetail> getSongsFromSQLDBCursor(Cursor cursor) {
        List<SongDetail> songList = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() >= 1) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndex(FavoritePlayTableHelper.ID));
                    long albumId = cursor.getLong(cursor.getColumnIndex(FavoritePlayTableHelper.ALBUM_ID));
                    String artist = cursor.getString(cursor.getColumnIndex(FavoritePlayTableHelper.ARTIST));
                    String title = cursor.getString(cursor.getColumnIndex(FavoritePlayTableHelper.TITLE));
                    String displayName = cursor.getString(cursor.getColumnIndex(FavoritePlayTableHelper.DISPLAY_NAME));
                    String duration = cursor.getString(cursor.getColumnIndex(FavoritePlayTableHelper.DURATION));
                    String path = cursor.getString(cursor.getColumnIndex(FavoritePlayTableHelper.PATH));

                    SongDetail mSongDetail = new SongDetail((int) id, (int) albumId, artist,
                            title, path, displayName, "" + (Long.parseLong(duration) * 1000));
                    songList.add(mSongDetail);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            closeCursor(cursor);
        }
        return songList;
    }

    private static PhoneMediaControlInterface phonemediacontrolinterface;
    public static void setPhoneMediaControlInterface(PhoneMediaControlInterface phonemediacontrolinterface) {
        PhoneMediaControl.phonemediacontrolinterface = phonemediacontrolinterface;
    }
    public interface PhoneMediaControlInterface {
        void loadSongsComplete(List<SongDetail> songsList);
    }
}