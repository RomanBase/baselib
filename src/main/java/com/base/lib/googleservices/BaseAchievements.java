package com.base.lib.googleservices;

import android.view.View;

import com.base.lib.engine.BaseActivity;
import com.base.lib.engine.common.file.FileHelper;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;

/**
 *
 */
public class BaseAchievements { //todo connection double checked in a lot of cases

    public static void bindParentViewForPopups(View view){

        if(BaseApiClient.isConnected()) {
            Games.setViewForPopups(BaseApiClient.getClient(), view);
        }
    }

    public static void unlock(String id) {

        if (BaseApiClient.isConnected()) {
            Games.Achievements.unlock(BaseApiClient.getClient(), id);
        }
    }

    public static void unlock(int resourceID) {

        unlock(FileHelper.resourceString(resourceID));
    }

    public static void increment(String id) {

        if (BaseApiClient.isConnected()) {
            Games.Achievements.increment(BaseApiClient.getClient(), id, 1);
        }
    }

    public static void increment(int resourceID) {

        increment(FileHelper.resourceString(resourceID));
    }

    public static void increment(String id, int count) {

        if (BaseApiClient.isConnected()) {
            Games.Achievements.increment(BaseApiClient.getClient(), id, count);
        }
    }

    public static void increment(int resourceID, int count) {

        increment(FileHelper.resourceString(resourceID), count);
    }

    public static void setProgress(String id, int progress){

        if(BaseApiClient.isConnected()) {
            Games.Achievements.setSteps(BaseApiClient.getClient(), id, progress);
        }
    }

    public static void setProgress(int resourceID, int progress){

        setProgress(FileHelper.resourceString(resourceID), progress);
    }

    public static void showDefaultAchievementsActivity(BaseActivity activity) {

        if (BaseApiClient.isConnected()) {
            activity.startActivityForResult(Games.Achievements.getAchievementsIntent(BaseApiClient.getClient()), BaseApiClient.REQUEST_ACHIEVEMENTS);
        }
    }

    public static void getAchievements(final Receiver receiver) {

        if(BaseApiClient.isConnected()) {
            Games.Achievements.load(BaseApiClient.getClient(), false).setResultCallback(new ResultCallback<Achievements.LoadAchievementsResult>() {
                @Override
                public void onResult(Achievements.LoadAchievementsResult loadAchievementsResult) {
                    AchievementBuffer buffer = loadAchievementsResult.getAchievements();
                    receiver.onAchievementsLoaded(buffer);
                    buffer.release();
                }
            });
        }
    }

    public interface Receiver {

        public void onAchievementsLoaded(AchievementBuffer achievements);
    }

}
