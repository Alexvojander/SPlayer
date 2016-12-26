/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.fragments;

import com.dmplayer.R;
import com.dmplayer.manager.MediaController;
import com.dmplayer.ui.VisualizerView;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class FragmentEqualizer extends BaseFragment {

	private MediaPlayer mMediaPlayer;
	private Visualizer mVisualizer;
	private Equalizer mEqualizer;

	private LinearLayout mLinearLayout;
	private VisualizerView mVisualizerView;
	private  View vieww;

	private static final float VISUALIZER_HEIGHT_DIP = 200f;


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		vieww=view;
		init();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_equalizer;
	}

	@Override
	public void onDestroy() {
		if(mVisualizer!=null ){
			mVisualizer.release();
		}
		if( mEqualizer!=null){
			mEqualizer.release();
		}

		super.onDestroy();
	}

	private void init() {

		getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

		mMediaPlayer= MediaController.getInstance().getAudioPlayer();
		//mMediaPlayer = MediaPlayer.create(view1.getContext(),R.raw.oxxxy);

		//mMediaPlayer.start();

		if(mMediaPlayer==null){
			mMediaPlayer = MediaPlayer.create(getActivity(),R.raw.oxxxy);
		}
		if(MediaController.getInstance().getAudioPlayer()!=null){
			mMediaPlayer= MediaController.getInstance().getAudioPlayer();
		}
		//mMediaPlayer= MediaController.getInstance().getAudioPlayer();
		mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
		mEqualizer.setEnabled(true);

		setupVisualizerFxAndUI();
		setupEqualizerFxAndUI();

		if(MediaController.getInstance().getAudioPlayer()!=null){
			mMediaPlayer= MediaController.getInstance().getAudioPlayer();
		}
		mVisualizer.setEnabled(true);
		mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			public void onCompletion(MediaPlayer mediaPlayer) {
//                disable the visualizer as it's no longer needed
				mVisualizer.setEnabled(false);
			}
		});

	}
	/* shows spinner with list of equalizer presets to choose from
- updates the seekBar progress and gain levels according
to those of the selected preset*/
	private void equalizeSound() {
//        set up the spinner
		ArrayList<String> equalizerPresetNames = new ArrayList<String>();
		ArrayAdapter<String> equalizerPresetSpinnerAdapter
				= new ArrayAdapter<String>(vieww.getContext(),android.R.layout.simple_spinner_item, equalizerPresetNames);
		equalizerPresetSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner equalizerPresetSpinner = ButterKnife.findById(vieww, R.id.spinner);

//        get list of the device's equalizer presets
		for (short i = 0; i < mEqualizer.getNumberOfPresets(); i++) {
			equalizerPresetNames.add(mEqualizer.getPresetName(i));
		}

		equalizerPresetSpinner.setAdapter(equalizerPresetSpinnerAdapter);

//        handle the spinner item selections
		equalizerPresetSpinner.setOnItemSelectedListener(new AdapterView
				.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent,
									   View view, int position, long id) {
				//first list item selected by default and sets the preset accordingly
				mEqualizer.usePreset((short) position);
//                get the number of frequency bands for this equalizer engine
				short numberFrequencyBands = mEqualizer.getNumberOfBands();
//                get the lower gain setting for this equalizer band
				final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];

//                set seekBar indicators according to selected preset
				for (short i = 0; i < numberFrequencyBands; i++) {
					short equalizerBandIndex = i;
					SeekBar seekBar = ButterKnife.findById(vieww,equalizerBandIndex);

//                    get current gain setting for this equalizer band
//                    set the progress indicator of this seekBar to indicate the current gain value
					seekBar.setProgress(mEqualizer
							.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
//                not used
			}
		});
	}

	/* displays the SeekBar sliders for the supported equalizer frequency bands
     user can move sliders to change the frequency of the bands*/
	private void setupEqualizerFxAndUI() {

//        get reference to linear layout for the seekBars
		mLinearLayout = ButterKnife.findById(vieww, R.id.linearLayoutEqual);

//        equalizer heading
//		TextView equalizerHeading = new TextView(vieww.getContext());
//		equalizerHeading.setText("Equalizer");
//		equalizerHeading.setTextSize(20);
//		equalizerHeading.setGravity(Gravity.CENTER_HORIZONTAL);
//		mLinearLayout.addView(equalizerHeading);

//        get number frequency bands supported by the equalizer engine
		short numberFrequencyBands = mEqualizer.getNumberOfBands();

//        get the level ranges to be used in setting the band level
//        get lower limit of the range in milliBels
		final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
//        get the upper limit of the range in millibels
		final short upperEqualizerBandLevel = mEqualizer.getBandLevelRange()[1];

//        loop through all the equalizer bands to display the band headings, lower
//        & upper levels and the seek bars
		for (short i = 0; i < numberFrequencyBands; i++) {
			final short equalizerBandIndex = i;

//            frequency header for each seekBar
			TextView frequencyHeaderTextview = new TextView(vieww.getContext());
			frequencyHeaderTextview.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			frequencyHeaderTextview.setGravity(Gravity.CENTER_HORIZONTAL);
			frequencyHeaderTextview
					.setText((mEqualizer.getCenterFreq(equalizerBandIndex) / 1000) + " Hz");
			mLinearLayout.addView(frequencyHeaderTextview);

//            set up linear layout to contain each seekBar
			LinearLayout seekBarRowLayout = new LinearLayout(vieww.getContext());
			seekBarRowLayout.setOrientation(LinearLayout.HORIZONTAL);

//            set up lower level textview for this seekBar
			TextView lowerEqualizerBandLevelTextview = new TextView(vieww.getContext());
			lowerEqualizerBandLevelTextview.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			lowerEqualizerBandLevelTextview.setText((lowerEqualizerBandLevel / 100) + " dB");
//            set up upper level textview for this seekBar
			TextView upperEqualizerBandLevelTextview = new TextView(vieww.getContext());
			upperEqualizerBandLevelTextview.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			upperEqualizerBandLevelTextview.setText((upperEqualizerBandLevel / 100) + " dB");

			//            **********  the seekBar  **************
//            set the layout parameters for the seekbar
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.weight = 1;

//            create a new seekBar
			SeekBar seekBar = new SeekBar(vieww.getContext());
//            give the seekBar an ID
			seekBar.setId(i);

			seekBar.setLayoutParams(layoutParams);
			seekBar.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);
//            set the progress for this seekBar
			seekBar.setProgress(mEqualizer.getBandLevel(equalizerBandIndex));

//            change progress as its changed by moving the sliders
			seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				public void onProgressChanged(SeekBar seekBar, int progress,
											  boolean fromUser) {
					mEqualizer.setBandLevel(equalizerBandIndex,
							(short) (progress + lowerEqualizerBandLevel));
				}

				public void onStartTrackingTouch(SeekBar seekBar) {
					//not used
				}

				public void onStopTrackingTouch(SeekBar seekBar) {
					//not used
				}
			});

//            add the lower and upper band level textviews and the seekBar to the row layout
			seekBarRowLayout.addView(lowerEqualizerBandLevelTextview);
			seekBarRowLayout.addView(seekBar);
			seekBarRowLayout.addView(upperEqualizerBandLevelTextview);

			mLinearLayout.addView(seekBarRowLayout);

			//        show the spinner
			equalizeSound();
		}
	}

	/*displays the audio waveform*/
	private void setupVisualizerFxAndUI() {

		mLinearLayout =  ButterKnife.findById(vieww, R.id.linearLayoutVisual);
		// Create a VisualizerView to display the audio waveform for the current settings
		mVisualizerView = new VisualizerView(vieww.getContext());
		mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				(int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));
		mLinearLayout.addView(mVisualizerView);
		// Create the Visualizer object and attach it to our media player.

		mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());

		mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

		mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
			public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
											  int samplingRate) {
				mVisualizerView.updateVisualizer(bytes);
			}

			public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
			}
		}, Visualizer.getMaxCaptureRate() / 2, true, false);
	}



}