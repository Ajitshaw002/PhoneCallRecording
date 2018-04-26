package com.example.ajit.virusss;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MyCallIntentService  extends Service
        implements MediaRecorder.OnInfoListener{

private MediaRecorder mRecorder;
//setting maximum file size to be recorded
private long Audio_MAX_FILE_SIZE=1000000;//1Mb

private int[]amplitudes=new int[100];
private int i=0;

private File mOutputFile;
    private long mStartTime;

    @Override
public void onCreate(){
        super.onCreate();
        }

@Override
public int onStartCommand(Intent intent,int flags,
        int startId){
        super.onStartCommand(intent,flags,startId);
        return Service.START_STICKY;
        }

@Override
public void onStart(Intent intent,int startId){
        super.onStart(intent,startId);
        startRecording();
        }
@Nullable
@Override
public IBinder onBind(Intent intent){
        return null;
        }
private void startRecording(){
        mRecorder=new MediaRecorder();
        mRecorder.setOnInfoListener(this);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setMaxFileSize(Audio_MAX_FILE_SIZE);
        mRecorder.setOutputFormat
        (MediaRecorder.OutputFormat.MPEG_4);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN)
        {
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
        mRecorder.setAudioEncodingBitRate(96000);
        }else{
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioEncodingBitRate(96000);
        }
        mRecorder.setAudioSamplingRate(16000);
        mOutputFile=getOutputFile();
        mOutputFile.getParentFile().mkdirs();
        mRecorder.setOutputFile(mOutputFile.getAbsolutePath());

        try{
        mRecorder.prepare();
        mRecorder.start();
        mStartTime= SystemClock.elapsedRealtime();
        }catch(IOException e){
        }
        }

protected void stopRecording(boolean saveFile){
        mRecorder.stop();
        mRecorder.release();
        mRecorder=null;
        mStartTime=0;
        if(!saveFile&&mOutputFile!=null){
        mOutputFile.delete();
        }

        // to stop the service by itself
        stopSelf();

        }
private File getOutputFile(){
        SimpleDateFormat dateFormat=new SimpleDateFormat
        ("yyyyMMdd_HHmmssSSS", Locale.US);
        return new File(Environment.getExternalStorageDirectory()
        .getAbsolutePath().toString()
        +"/Voice Recorder/RECORDING_"
        +dateFormat.format(new Date())
        +".m4a");
        }
@Override
public void onInfo(MediaRecorder mr,int what,int extra){
        //check whether file size has reached to 1MB to stop recording
        if(what==MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED){
        stopRecording(true);
        }
        }

@Override
public void onDestroy(){
        super.onDestroy();
        stopRecording(true);
        }
        }
