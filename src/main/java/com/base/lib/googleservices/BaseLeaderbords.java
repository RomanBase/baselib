package com.base.lib.googleservices;

import com.base.lib.engine.Base;
import com.base.lib.engine.common.FileHelper;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;

/**
 *
 */
public class BaseLeaderbords {

    public static void sudmit(String tableID, int score){

        if(BaseApiClient.isConnected()) {
            Games.Leaderboards.submitScore(BaseApiClient.getClient(), tableID, score);
        }
    }

    public static void sudmit(int tableID, int score){

        sudmit(FileHelper.resourceString(tableID), score);
    }

    public static void showDefaultLeaderbordsActivity(String tableID){

        if(BaseApiClient.isConnected()) {
            Base.activity.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(BaseApiClient.getClient(), tableID), BaseApiClient.REQUEST_LEADERBORDS);
        }
    }

    public static void showDefaultLeaderbordsActivity(int tableID){

        showDefaultLeaderbordsActivity(FileHelper.resourceString(tableID));
    }

    public static void showDefaultLeaderbordsActivity(){

        if(BaseApiClient.isConnected()) {
            Base.activity.startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(BaseApiClient.getClient()), BaseApiClient.REQUEST_LEADERBORDS);
        }
    }

    public static void getLeaderbordScore(String tableID, final ScoreReceiver receiver) {

        if(BaseApiClient.isConnected()) {
            Games.Leaderboards.loadCurrentPlayerLeaderboardScore(BaseApiClient.getClient(), tableID, LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
                @Override
                public void onResult(final Leaderboards.LoadPlayerScoreResult scoreResult) {
                    if (scoreResult != null && GamesStatusCodes.STATUS_OK == scoreResult.getStatus().getStatusCode() && scoreResult.getScore() != null) {
                        // here you can get the score like this
                        receiver.onScoreReceive(scoreResult.getScore().getRawScore());
                    }
                }
            });
        }
    }

    public static void getLeaderbordScore(int tableID, final ScoreReceiver receiver) {

        getLeaderbordScore(FileHelper.resourceString(tableID), receiver);
    }

    public static interface ScoreReceiver {

        public void onScoreReceive(long rawScore);
    }
}
