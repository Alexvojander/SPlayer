/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.manager;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;

import com.dmplayer.DMPlayerApplication;
import com.dmplayer.db.FavoritePlayTableHelper;
import com.dmplayer.db.MostAndRecentPlayTableHelper;
import com.dmplayer.models.SongDetail;
import com.dmplayer.utility.DMPlayerUtility;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MediaController implements NotificationManager.NotificationCenterDelegate, SensorEventListener {

    private boolean isPaused = true;
    private MediaPlayer audioPlayer = null;

    private AudioTrack audioTrackPlayer = null;
    private int lastProgress = 0;
    private boolean useFrontSpeaker;

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private boolean ignoreProximity;
    private PowerManager.WakeLock proximityWakeLock;

    private final Object playerSync = new Object();
    private final Object playerSongDetailSync = new Object();
    private boolean playMusicAgain = false;

    private int lastTag = 0;
    public int currentPlaylistNum;
    private boolean shuffleMusic;

    private final Object progressTimerSync = new Object();
    private Timer progressTimer = null;

    private final Object sync = new Object();
    private int ignoreFirstProgress = 0;
    private long lastPlayPcm;
    private long currentTotalPcmDuration;

    public int type = 0;
    public int id = -1;
    public String path = "";
    private int repeatMode;

    private static volatile MediaController Instance = null;

    private String TAG = "MediaController";

    public static MediaController getInstance() {
        MediaController localInstance = Instance;
        if (localInstance == null) {
            synchronized (MediaController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new MediaController();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    @Override
    public void newSongLoaded(Object... args) {

    }

    public MediaPlayer getAudioPlayer(){return audioPlayer;}
    public int generateObserverTag() {
        return lastTag++;
    }

    public SongDetail getPlayingSongDetail() {
        return MusicPreference.playingSongDetail;
    }

    public boolean isPlayingAudio(SongDetail messageObject) {
        return !(audioTrackPlayer == null && audioPlayer == null || messageObject == null || MusicPreference.playingSongDetail == null || MusicPreference.playingSongDetail != null);
    }

    public boolean isAudioPaused() {
        return isPaused;
    }


    public void playNextSong() {
        playNextSong(false);
    }

    public void playPreviousSong() {
        List<SongDetail> currentPlayList = shuffleMusic ? MusicPreference.shuffledPlaylist : MusicPreference.playlist;

        currentPlaylistNum--;
        if (currentPlaylistNum < 0) {
            currentPlaylistNum = currentPlayList.size() - 1;
        }
        if (currentPlaylistNum < 0 || currentPlaylistNum >= currentPlayList.size()) {
            return;
        }
        playMusicAgain = true;
        MusicPreference.playingSongDetail.audioProgress = 0.0f;
        MusicPreference.playingSongDetail.audioProgressSec = 0;
        playAudio(currentPlayList.get(currentPlaylistNum));
    }

    private void stopProgressTimer() {
        synchronized (progressTimerSync) {
            if (progressTimer != null) {
                try {
                    progressTimer.cancel();
                    progressTimer = null;
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }
    }

    private void stopProximitySensor() {
        if (ignoreProximity) {
            return;
        }
        try {
            useFrontSpeaker = false;
            NotificationManager.getInstance().postNotificationName(NotificationManager.audioRouteChanged, useFrontSpeaker);
            if (sensorManager != null && proximitySensor != null) {
                sensorManager.unregisterListener(this);
            }
            if (proximityWakeLock != null && proximityWakeLock.isHeld()) {
                proximityWakeLock.release();
            }
        } catch (Throwable e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public boolean playAudio(SongDetail mSongDetail) {
        if (mSongDetail == null) {
            return false;
        }
        if ((audioTrackPlayer != null || audioPlayer != null) &&
                MusicPreference.playingSongDetail != null &&
                mSongDetail.getId() == MusicPreference.playingSongDetail.getId()) {
            if (isPaused) {
                resumeAudio(mSongDetail);
            }
            return true;
        }
        if (audioTrackPlayer != null) {
            MusicPlayerService.setIgnoreAudioFocus();
        }
        cleanupPlayer(!playMusicAgain, false);
        playMusicAgain = false;
        File file = null;

        try {
            audioPlayer = new MediaPlayer();
            audioPlayer.setAudioStreamType(useFrontSpeaker ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC);
            audioPlayer.setDataSource(mSongDetail.getPath());
            audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    MusicPreference.playingSongDetail.audioProgress = 0.0f;
                    MusicPreference.playingSongDetail.audioProgressSec = 0;
                    if (!MusicPreference.playlist.isEmpty() && MusicPreference.playlist.size() > 1) {
                        playNextSong(true);
                    } else {
                        cleanupPlayer(true, true);
                    }
                }
            });
            audioPlayer.prepare();
            audioPlayer.start();
            startProgressTimer();
        } catch (Exception e) {
            if (audioPlayer != null) {
                audioPlayer.release();
                audioPlayer = null;
                isPaused = false;
                MusicPreference.playingSongDetail = null;
            }
            return false;
        }
        isPaused = false;
        lastProgress = 0;
        MusicPreference.playingSongDetail = mSongDetail;
        NotificationManager.getInstance().postNotificationName(NotificationManager.audioDidStarted, mSongDetail);

        if (audioPlayer != null) {
            try {
                if (MusicPreference.playingSongDetail.audioProgress != 0) {
                    int seekTo = (int) (audioPlayer.getDuration() * MusicPreference.playingSongDetail.audioProgress);
                    audioPlayer.seekTo(seekTo);
                }
            } catch (Exception e2) {
                MusicPreference.playingSongDetail.audioProgress = 0;
                MusicPreference.playingSongDetail.audioProgressSec = 0;
            }
        } else if (audioTrackPlayer != null) {
            if (MusicPreference.playingSongDetail.audioProgress == 1) {
                MusicPreference.playingSongDetail.audioProgress = 0;
            }

        }

        if (MusicPreference.playingSongDetail != null) {
            Intent intent = new Intent(DMPlayerApplication.applicationContext, MusicPlayerService.class);
            DMPlayerApplication.applicationContext.startService(intent);
        } else {
            Intent intent = new Intent(DMPlayerApplication.applicationContext, MusicPlayerService.class);
            DMPlayerApplication.applicationContext.stopService(intent);
        }

        storeResentPlay(DMPlayerApplication.applicationContext, mSongDetail);
        NotificationManager.getInstance().notifyNewSongLoaded(NotificationManager.newaudioloaded, mSongDetail);

        return true;
    }

    private void playNextSong(boolean byStop) {
        List<SongDetail> currentPlayList = shuffleMusic ? MusicPreference.shuffledPlaylist : MusicPreference.playlist;

        if (byStop && repeatMode == 2) {
            cleanupPlayer(false, false);
            playAudio(currentPlayList.get(currentPlaylistNum));
            return;
        }
        currentPlaylistNum++;
        if (currentPlaylistNum >= currentPlayList.size()) {
            currentPlaylistNum = 0;
            if (byStop && repeatMode == 0) {
                stopProximitySensor();
                if (audioPlayer != null || audioTrackPlayer != null) {
                    if (audioPlayer != null) {
                        try {
                            audioPlayer.stop();
                        } catch (Exception e) {
                            Log.e(TAG, Log.getStackTraceString(e));
                        }
                        try {
                            audioPlayer.release();
                            audioPlayer = null;
                        } catch (Exception e) {
                            Log.e(TAG, Log.getStackTraceString(e));
                        }
                    } else if (audioTrackPlayer != null) {
                        synchronized (playerSongDetailSync) {
                            try {
                                audioTrackPlayer.pause();
                                audioTrackPlayer.flush();
                            } catch (Exception e) {
                                Log.e(TAG, Log.getStackTraceString(e));
                            }
                            try {
                                audioTrackPlayer.release();
                                audioTrackPlayer = null;
                            } catch (Exception e) {
                                Log.e(TAG, Log.getStackTraceString(e));
                            }
                        }
                    }
                    stopProgressTimer();
                    lastProgress = 0;
                    isPaused = true;
                    MusicPreference.playingSongDetail.audioProgress = 0.0f;
                    MusicPreference.playingSongDetail.audioProgressSec = 0;
                    NotificationManager.getInstance().postNotificationName(NotificationManager.audioPlayStateChanged, MusicPreference.playingSongDetail.getId());
                }
                return;
            }
        }
        if (currentPlaylistNum < 0 || currentPlaylistNum >= currentPlayList.size()) {
            return;
        }
        playMusicAgain = true;
        MusicPreference.playingSongDetail.audioProgress = 0.0f;
        MusicPreference.playingSongDetail.audioProgressSec = 0;
        playAudio(currentPlayList.get(currentPlaylistNum));
    }

    public boolean pauseAudio(SongDetail messageObject) {
        stopProximitySensor();
        if (audioTrackPlayer == null && audioPlayer == null || messageObject == null || MusicPreference.playingSongDetail == null || MusicPreference.playingSongDetail != null
                && MusicPreference.playingSongDetail.getId() != messageObject.getId()) {
            return false;
        }
        stopProgressTimer();
        try {
            if (audioPlayer != null) {
                audioPlayer.pause();
            } else if (audioTrackPlayer != null) {
                audioTrackPlayer.pause();
            }
            isPaused = true;
            NotificationManager.getInstance().postNotificationName(NotificationManager.audioPlayStateChanged, MusicPreference.playingSongDetail.getId());
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            isPaused = true;
            return false;
        }
        return true;
    }

    public boolean resumeAudio(SongDetail messageObject) {
        if (audioTrackPlayer == null && audioPlayer == null || messageObject == null || MusicPreference.playingSongDetail == null || MusicPreference.playingSongDetail != null
                && MusicPreference.playingSongDetail.getId() != messageObject.getId()) {
            return false;
        }
        try {
            startProgressTimer();
            if (audioPlayer != null) {
                audioPlayer.start();
            } else if (audioTrackPlayer != null) {
                audioTrackPlayer.play();
            }
            isPaused = false;
            NotificationManager.getInstance().postNotificationName(NotificationManager.audioPlayStateChanged, MusicPreference.playingSongDetail.getId());
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return false;
        }
        return true;
    }

    public void stopAudio() {
        stopProximitySensor();
        if (audioTrackPlayer == null && audioPlayer == null || MusicPreference.playingSongDetail == null) {
            return;
        }
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            } else if (audioTrackPlayer != null) {
                audioTrackPlayer.pause();
                audioTrackPlayer.flush();
            }
        } catch (Exception e) {}
        try {
            if (audioPlayer != null) {
                audioPlayer.release();
                audioPlayer = null;
            } else if (audioTrackPlayer != null) {
                synchronized (playerSongDetailSync) {
                    audioTrackPlayer.release();
                    audioTrackPlayer = null;
                }
            }
        } catch (Exception e) {}
        stopProgressTimer();
        isPaused = false;

        Intent intent = new Intent(DMPlayerApplication.applicationContext, MusicPlayerService.class);
        DMPlayerApplication.applicationContext.stopService(intent);
    }

    private void startProgressTimer() {
        synchronized (progressTimerSync) {
            if (progressTimer != null) {
                try {
                    progressTimer.cancel();
                    progressTimer = null;
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
            progressTimer = new Timer();
            progressTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (sync) {
                        DMPlayerUtility.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if (MusicPreference.playingSongDetail != null && (audioPlayer != null || audioTrackPlayer != null) && !isPaused) {
                                    try {
                                        if (ignoreFirstProgress != 0) {
                                            ignoreFirstProgress--;
                                            return;
                                        }
                                        int progress;
                                        float value;
                                        if (audioPlayer != null) {
                                            progress = audioPlayer.getCurrentPosition();
                                            value = (float) lastProgress / (float) audioPlayer.getDuration();
                                            if (progress <= lastProgress) {
                                                return;
                                            }
                                        } else {
                                            progress = (int) (lastPlayPcm / 48.0f);
                                            value = (float) lastPlayPcm / (float) currentTotalPcmDuration;
                                            if (progress == lastProgress) {
                                                return;
                                            }
                                        }
                                        lastProgress = progress;
                                        MusicPreference.playingSongDetail.audioProgress = value;
                                        MusicPreference.playingSongDetail.audioProgressSec = lastProgress / 1000;
                                        NotificationManager.getInstance().postNotificationName(NotificationManager.audioProgressDidChanged,
                                                MusicPreference.playingSongDetail.getId(), value);
                                    } catch (Exception e) {
                                        Log.e(TAG, Log.getStackTraceString(e));
                                    }
                                }
                            }
                        });
                    }
                }
            }, 0, 17);
        }
    }

    public boolean setPlaylist(List<SongDetail> allSongsList, SongDetail current, int type, int id) {
        this.type = type;
        this.id = id;

        if (MusicPreference.playingSongDetail == current) {
            return playAudio(current);
        }
        playMusicAgain = !MusicPreference.playlist.isEmpty();
        MusicPreference.playlist.clear();
        if (allSongsList != null && allSongsList.size() >= 1) {
            MusicPreference.playlist.addAll(allSongsList);
        }

        currentPlaylistNum = MusicPreference.playlist.indexOf(current);
        if (currentPlaylistNum == -1) {
            MusicPreference.playlist.clear();
            MusicPreference.shuffledPlaylist.clear();
            return false;
        }
        if (shuffleMusic) {
            currentPlaylistNum = 0;
        }
        return playAudio(current);
    }

    public boolean seekToProgress(SongDetail mSongDetail, float progress) {
        if (audioTrackPlayer == null && audioPlayer == null) {
            return false;
        }
        try {
            if (audioPlayer != null) {
                int seekTo = (int) (audioPlayer.getDuration() * progress);
                audioPlayer.seekTo(seekTo);
                lastProgress = seekTo;
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return false;
        }
        return true;
    }

    public void cleanupPlayer(Context context, boolean notify, boolean stopService) {
        MusicPreference.saveLastSong(context, getPlayingSongDetail());
        MusicPreference.saveLastSongListType(context, type);
        MusicPreference.saveLastAlbID(context, id);
        MusicPreference.saveLastPosition(context, currentPlaylistNum);
        MusicPreference.saveLastPath(context, path);
        cleanupPlayer(notify, stopService);
    }

    public void cleanupPlayer(boolean notify, boolean stopService) {
        pauseAudio(getPlayingSongDetail());
        stopProximitySensor();
        if (audioPlayer != null) {
            try {
                audioPlayer.reset();
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            try {
                audioPlayer.stop();
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            try {
                audioPlayer.release();
                audioPlayer = null;
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        } else if (audioTrackPlayer != null) {
            synchronized (playerSongDetailSync) {
                try {
                    audioTrackPlayer.pause();
                    audioTrackPlayer.flush();
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
                try {
                    audioTrackPlayer.release();
                    audioTrackPlayer = null;
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }
        stopProgressTimer();
        isPaused = true;
        if (stopService) {
            Intent intent = new Intent(DMPlayerApplication.applicationContext, MusicPlayerService.class);
            DMPlayerApplication.applicationContext.stopService(intent);
        }
    }

    /**
     * Store Recent Play Data
     */
    public synchronized void storeResentPlay(final Context context, final SongDetail mDetail) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    MostAndRecentPlayTableHelper.getInstance(context).insertSong(mDetail);
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Store FAVORITE Play Data
     */
    public synchronized void storeFavoritePlay(final Context context, final SongDetail mDetail, final int isFav) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    FavoritePlayTableHelper.getInstance(context).insertSong(mDetail, isFav);
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public synchronized void checkIsFavorite(final Context context, final SongDetail mDetail, final View v) {
        new AsyncTask<Void, Void, Void>() {
            boolean isFavorite = false;

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    isFavorite = FavoritePlayTableHelper.getInstance(context).isSongFavorite(mDetail);
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                v.setSelected(isFavorite);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public boolean playAudioFromStream(MediaPlayer mediaPlayer) {
//        if (mSongDetail == null) {
//            return false;
//        }
//        if ((audioTrackPlayer != null || audioPlayer != null) &&
//                MusicPreference.playingSongDetail != null &&
//                mSongDetail.getId() == MusicPreference.playingSongDetail.getId()) {
//            if (isPaused) {
//                resumeAudio(mSongDetail);
//            }
//            return true;
//        }
//        if (audioTrackPlayer != null) {
//            MusicPlayerService.setIgnoreAudioFocus();
//        }
//        cleanupPlayer(!playMusicAgain, false);
//        playMusicAgain = false;
//        File file = null;
        try {
            if (audioPlayer != null) {
                audioPlayer.pause();

            }
            isPaused = true;
           // NotificationManager.getInstance().postNotificationName(NotificationManager.audioPlayStateChanged, MusicPreference.playingSongDetail.getId());
        } catch (Exception e) {
            Log.e("tmessages", e.toString());
            isPaused = true;
            return false;
        }

        try {
            audioPlayer = new MediaPlayer();
            audioPlayer=mediaPlayer;

           // audioPlayer.setAudioStreamType(useFrontSpeaker ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC);
           // audioPlayer.setDataSource(mSongDetail.getPath());
//            audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    MusicPreference.playingSongDetail.audioProgress = 0.0f;
//                    MusicPreference.playingSongDetail.audioProgressSec = 0;
//                    //if (!MusicPreference.playlist.isEmpty() && MusicPreference.playlist.size() > 1) {
//                   //     playNextSong(true);
//                   //} else {
//                    //    cleanupPlayer(true, true);
//                    //}
//                }
//            });
            audioPlayer.prepare();
            audioPlayer.start();
            startProgressTimer();
        } catch (Exception e) {
            if (audioPlayer != null) {
                audioPlayer.release();
                audioPlayer = null;
                isPaused = false;
                MusicPreference.playingSongDetail = null;
            }
            return false;
        }
        isPaused = false;
        lastProgress = 0;
//        MusicPreference.playingSongDetail = mSongDetail;
//        NotificationManager.getInstance().postNotificationName(NotificationManager.audioDidStarted, mSongDetail);

//        if (audioPlayer != null) {
//            try {
//                if (MusicPreference.playingSongDetail.audioProgress != 0) {
//                    int seekTo = (int) (audioPlayer.getDuration() * MusicPreference.playingSongDetail.audioProgress);
//                    audioPlayer.seekTo(seekTo);
//                }
//            } catch (Exception e2) {
//                MusicPreference.playingSongDetail.audioProgress = 0;
//                MusicPreference.playingSongDetail.audioProgressSec = 0;
//            }
//        } else if (audioTrackPlayer != null) {
//            if (MusicPreference.playingSongDetail.audioProgress == 1) {
//                MusicPreference.playingSongDetail.audioProgress = 0;
//            }
//
//        }

//        if (MusicPreference.playingSongDetail != null) {
//            Intent intent = new Intent(ApplicationDMPlayer.applicationContext, MusicPlayerService.class);
//            ApplicationDMPlayer.applicationContext.startService(intent);
//        } else {
//            Intent intent = new Intent(ApplicationDMPlayer.applicationContext, MusicPlayerService.class);
//            ApplicationDMPlayer.applicationContext.stopService(intent);
//        }

        //storeResentPlay(ApplicationDMPlayer.applicationContext, mSongDetail);
       // NotificationManager.getInstance().notifyNewSongLoaded(NotificationManager.newaudioloaded, mSongDetail);

        return true;
    }
}
