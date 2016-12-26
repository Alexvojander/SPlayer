/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SongDetail implements Serializable {
	private int id;
	private int albumId;
	private String artist;
	private String title;
	private String displayName;
	private String duration;
	private String path;

	public float audioProgress = 0.0f;
	public int audioProgressSec = 0;

	private static final String TAG = "SongDetail";

	public SongDetail(int _id, int _album_id, String _artist, String _title, String _path, String _display_name, String _duration) {
		this.id = _id;
		this.albumId = _album_id;
		this.artist = _artist;
		this.title = _title;
		this.path = _path;
		this.displayName = _display_name;
		this.duration = TextUtils.isEmpty(_duration) ? "0" : String.valueOf((Long.valueOf(_duration) / 1000));
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAlbumId() {
		return albumId;
	}

	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Bitmap getCover(Context context) {

		// ImageLoader.getInstance().getDiskCache().g
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		Bitmap curThumb = null;
		try {
			Uri uri = Uri.parse("content://media/external/audio/media/" + getId() + "/albumart");
			ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
			if (pfd != null) {
				FileDescriptor fd = pfd.getFileDescriptor();
				curThumb = BitmapFactory.decodeFileDescriptor(fd);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		return curThumb;
	}

	public byte[] getBytes(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            return bos.toByteArray();
        } catch (Exception ex) {
			Log.e(TAG, ex.getMessage());
			return null;
		}
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                bos.close();
            } catch (Exception ex) {
				Log.e(TAG, ex.getMessage());
            }
        }
    }

    @Nullable
	public static SongDetail getSongDetail(byte[] songDetail){
        ByteArrayInputStream bis = new ByteArrayInputStream(songDetail);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return (SongDetail) in.readObject();
        } catch (Exception ex) {
			Log.e(TAG, ex.getMessage());
            return null;
        }
        finally
		{
            try {
                bis.close();
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
				Log.e(TAG, ex.getMessage());
            }
        }
    }
}
