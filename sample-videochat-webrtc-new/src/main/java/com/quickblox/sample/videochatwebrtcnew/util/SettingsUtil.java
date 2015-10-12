package com.quickblox.sample.videochatwebrtcnew.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.quickblox.sample.videochatwebrtcnew.R;
import com.quickblox.sample.videochatwebrtcnew.User;
import com.quickblox.videochat.webrtc.QBRTCMediaConfig;

import java.util.List;

public class SettingsUtil {

    private static final String TAG = SettingsUtil.class.getSimpleName();

    private static void setSettingsForMultiCall(List<User> users){
        if (users.size() <= 2) {
            int width = QBRTCMediaConfig.getVideoWidth();
            if (width > QBRTCMediaConfig.VideoQuality.VGA_VIDEO.width){
                QBRTCMediaConfig.setVideoWidth(QBRTCMediaConfig.VideoQuality.VGA_VIDEO.width);
                QBRTCMediaConfig.setVideoHeight(QBRTCMediaConfig.VideoQuality.VGA_VIDEO.height);
            }
        } else {
            //set to minimum settings
            QBRTCMediaConfig.setVideoWidth(QBRTCMediaConfig.VideoQuality.QBGA_VIDEO.width);
            QBRTCMediaConfig.setVideoHeight(QBRTCMediaConfig.VideoQuality.QBGA_VIDEO.height);
            QBRTCMediaConfig.setVideoHWAcceleration(false);
            QBRTCMediaConfig.setVideoCodec(null);
            QBRTCMediaConfig.setVideoFps(0);
        }
    }

    public static void setSettingsStrategy(List<User> users, SharedPreferences sharedPref, Context context){
        if (users.size() == 1){
            setSettingsFromPreferences(sharedPref, context);
        } else {
            setSettingsForMultiCall(users);
        }
    }


    private static void setSettingsFromPreferences(SharedPreferences sharedPref, Context context) {

        // Check HW codec flag.
        boolean hwCodec = sharedPref.getBoolean(context.getString(R.string.pref_hwcodec_key),
                Boolean.valueOf(context.getString(R.string.pref_hwcodec_default)));

        QBRTCMediaConfig.setVideoHWAcceleration(hwCodec);
        // Get video resolution from settings.
        int resolutionItem = Integer.parseInt(sharedPref.getString(context.getString(R.string.pref_resolution_key),
                "0"));
        Log.e(TAG, "resolutionItem =: " + resolutionItem);
        for (QBRTCMediaConfig.VideoQuality quality : QBRTCMediaConfig.VideoQuality.values()){
            if (quality.ordinal() == resolutionItem){
                Log.e(TAG, "resolution =: " + quality.height + ":"+quality.width);
                QBRTCMediaConfig.setVideoHeight(quality.height);
                QBRTCMediaConfig.setVideoWidth(quality.width);
                break;
            }
        }

        // Get camera fps from settings.
        int fps = Integer.parseInt(sharedPref.getString(context.getString(R.string.pref_fps_key),
                "0"));
        Log.e(TAG, "cameraFps =: " + fps);
        QBRTCMediaConfig.setVideoFps(fps);

        // Get start bitrate.
        String bitrateTypeDefault = context.getString(R.string.pref_startbitrate_default);
        String bitrateType = sharedPref.getString(
                context.getString(R.string.pref_startbitrate_key), bitrateTypeDefault);
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = sharedPref.getString(context.getString(R.string.pref_startbitratevalue_key),
                    context.getString(R.string.pref_startbitratevalue_default));
            int startBitrate = Integer.parseInt(bitrateValue);
            QBRTCMediaConfig.setVideoStartBitrate(startBitrate);
        }

        int videoCodecItem = Integer.parseInt(getPreferenceString(sharedPref, context,  R.string.pref_videocodec_key, "0"));
        for (QBRTCMediaConfig.VideoCodec codec : QBRTCMediaConfig.VideoCodec.values()){
            if (codec.ordinal() == videoCodecItem){
                Log.e(TAG, "videoCodecItem =: " + codec.getDescription());
                QBRTCMediaConfig.setVideoCodec(codec);
                break;
            }
        }

        String audioCodecDescription = getPreferenceString(sharedPref, context,  R.string.pref_audiocodec_key,
                R.string.pref_audiocodec_def);
        QBRTCMediaConfig.AudioCodec audioCodec = QBRTCMediaConfig.AudioCodec.ISAC.getDescription()
                .equals(audioCodecDescription) ?
                QBRTCMediaConfig.AudioCodec.ISAC : QBRTCMediaConfig.AudioCodec.OPUS;
        Log.e(TAG, "audioCodec =: " + audioCodec.getDescription());
        QBRTCMediaConfig.setAudioCodec(audioCodec);

    }

    private static String getPreferenceString(SharedPreferences sharedPref ,Context context,  int StrRes, int StrResDefValue){
        return sharedPref.getString(context.getString(StrRes),
                context.getString(StrResDefValue));
    }

    private static String getPreferenceString(SharedPreferences sharedPref ,Context context, int StrRes, String defValue){
        return sharedPref.getString(context.getString(StrRes),
                defValue);
    }
}
