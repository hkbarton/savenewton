package ml.game.android.SaveNewton;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.google.android.gms.ads.*;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.achievement.OnAchievementUpdatedListener;
import com.google.android.gms.games.leaderboard.OnScoreSubmittedListener;
import com.google.android.gms.games.leaderboard.SubmitScoreResult;
import com.google.example.games.basegameutils.BaseGameActivity;

public class actGameView extends BaseGameActivity{
    private Vibrator mVibrator;
    private GameView mGameView;
    private RelativeLayout mMainLayout;
    private ImageView imgBtnVibration, imgBtnSound, imgBtnReturn;
    private ImageView menuRestart, menuPostLocal, menuPostOnline, menuReturn;
    private AdView mAdView;
    private Animation[] mMenuInAnimations, mMenuOutAnimations;
    
    @SuppressLint("HandlerLeak")
	private Handler mGameEventHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
            case GameLogic.GameEvent_BowInit:
                AudioResource.playSoundEffect(AudioResource.SoundEffectID_Bow);
                break;
            case GameLogic.GameEvent_ReleaseArrow:
                AudioResource.playSoundEffect(AudioResource.SoundEffectID_Arrow);
                break;
            case GameLogic.GameEvent_ArrowInApple:
                AudioResource.playSoundEffect(AudioResource.SoundEffectID_ArrowInApple);
                break;
            case GameLogic.GameEvent_ArrowInNewton:
                AudioResource.playSoundEffect(AudioResource.SoundEffectID_ArrowInNewton);
                break;
            case GameLogic.GameEvent_ArrowEnd:
                AudioResource.playSoundEffect(AudioResource.SoundEffectID_ArrowEnd);
                break;
            case GameLogic.GameEvent_EarnPrize:
                AudioResource.playSoundEffect(AudioResource.SoundEffectID_Prize);
                if (DataAccess.Pref_IsVibration && mVibrator!=null){
                    mVibrator.vibrate(200);
                }
                break;
            case GameLogic.GameEvent_AppleHitNewton:
                AudioResource.playSoundEffect(AudioResource.SoundEffectID_AppleHit);
                break;
            case GameLogic.GameEvent_LowGravityCountDown:
                if (DataAccess.Pref_IsVibration && mVibrator!=null){
                    mVibrator.vibrate(60);
                }
                break;
            case GameLogic.GameEvent_GamePause:
            	ADManager.refreshAD(mAdView);
                showPauseControl();
                break;
            case GameLogic.GameEvent_GameResume:
            	ADManager.hideAdDelay(mAdView, 4);
                hidePauseControl();
                break;
            case GameLogic.GameEvent_GameOvering:
                ADManager.refreshAD(mAdView);
                break;
            case GameLogic.GameEvent_GameOver:
                showMenu();
                DataAccess.saveAllGameData(actGameView.this);
                break;
            case GameLogic.GameEvent_StatDataChange:
                List<AchievementMgt.LocalAchievement> unlockedAchievements = 
                    AchievementMgt.unlockAchievementsByStatData(actGameView.this);
                if (unlockedAchievements!=null && unlockedAchievements.size()>0){
                    Toast.makeText(actGameView.this, 
                            AchievementMgt.getAchievementsUnlockTip(actGameView.this, unlockedAchievements), 
                            Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setContentView(R.layout.gameview);
        mMainLayout = (RelativeLayout)this.findViewById(R.id.actGameView_mainLayout);
        mGameView = new GameView(this, mGameEventHandler);
        mMainLayout.addView(mGameView, 
                new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mVibrator = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
        initPauseControlLayout();
        mAdView = ADManager.loadAD(this, mMainLayout);
        ADManager.hideAdDelay(mAdView, 10);
        initAnimation();
    }
    
    @Override
    public void onStart(){
        super.onStart();
        if (DataAccess.Pref_IsSound){
            AudioResource.playBGM(this, AudioResource.BGMID_Play);
        }
    }
    
    @Override
    public void onStop(){
        super.onStop();
        AudioResource.stopBGM(AudioResource.BGMID_Play);
        DataAccess.saveAllGameData(this);
    }
    
    private void initAnimation(){
        mMenuInAnimations = new Animation[4];
        mMenuOutAnimations = new Animation[4];
        mMenuInAnimations[0] = AnimationUtils.loadAnimation(this, R.anim.menu_popup);
        mMenuInAnimations[1] = AnimationUtils.loadAnimation(this, R.anim.menu_popup);
        mMenuInAnimations[2] = AnimationUtils.loadAnimation(this, R.anim.menu_popup);
        mMenuInAnimations[3] = AnimationUtils.loadAnimation(this, R.anim.menu_popup);
        mMenuOutAnimations[0] = AnimationUtils.loadAnimation(this, R.anim.menu_dis);
        mMenuOutAnimations[1] = AnimationUtils.loadAnimation(this, R.anim.menu_dis);
        mMenuOutAnimations[2] = AnimationUtils.loadAnimation(this, R.anim.menu_dis);
        mMenuOutAnimations[3] = AnimationUtils.loadAnimation(this, R.anim.menu_dis);
        mMenuOutAnimations[0].setAnimationListener(mMenuOutAnimation_Listener);
    }
    
    private void initPauseControlLayout(){
        int imgBtnPadding = 20;
        int imgBtnSize = GameResource.NormalBtnSize + imgBtnPadding * 2;
        imgBtnVibration = new ImageView(this);
        imgBtnVibration.setId(1000);
        imgBtnVibration.setScaleType(ScaleType.FIT_CENTER);
        imgBtnVibration.setPadding(imgBtnPadding, imgBtnPadding, imgBtnPadding, imgBtnPadding);
        if (DataAccess.Pref_IsVibration){
            imgBtnVibration.setImageResource(R.drawable.btnshake_on);
        }else{
            imgBtnVibration.setImageResource(R.drawable.btnshake_off);
        }
        RelativeLayout.LayoutParams imgBtnVibrationLy = new RelativeLayout.LayoutParams(imgBtnSize, imgBtnSize);
        imgBtnVibrationLy.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imgBtnVibrationLy.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        imgBtnVibrationLy.bottomMargin = 50;
        mMainLayout.addView(imgBtnVibration, imgBtnVibrationLy);
        imgBtnVibration.setOnClickListener(btnVibration_Click);
        imgBtnSound = new ImageView(this);
        imgBtnSound.setScaleType(ScaleType.FIT_CENTER);
        imgBtnSound.setPadding(imgBtnPadding, imgBtnPadding, imgBtnPadding, imgBtnPadding);
        if (DataAccess.Pref_IsSound){
            imgBtnSound.setImageResource(R.drawable.btnsound_on);
        }else{
            imgBtnSound.setImageResource(R.drawable.btnsound_off);
        }
        RelativeLayout.LayoutParams imgBtnSoundLy = new RelativeLayout.LayoutParams(imgBtnSize, imgBtnSize);
        imgBtnSoundLy.addRule(RelativeLayout.LEFT_OF, imgBtnVibration.getId());
        imgBtnSoundLy.addRule(RelativeLayout.ALIGN_TOP, imgBtnVibration.getId());
        mMainLayout.addView(imgBtnSound, imgBtnSoundLy);
        imgBtnSound.setOnClickListener(btnSound_Click);
        imgBtnReturn = new ImageView(this);
        imgBtnReturn.setScaleType(ScaleType.FIT_CENTER);
        imgBtnReturn.setPadding(imgBtnPadding, imgBtnPadding, imgBtnPadding, imgBtnPadding);
        imgBtnReturn.setImageResource(R.drawable.btnreturn);
        RelativeLayout.LayoutParams imgBtnReturnLy = new RelativeLayout.LayoutParams(imgBtnSize, imgBtnSize);
        imgBtnReturnLy.addRule(RelativeLayout.RIGHT_OF, imgBtnVibration.getId());
        imgBtnReturnLy.addRule(RelativeLayout.ALIGN_TOP, imgBtnVibration.getId());
        mMainLayout.addView(imgBtnReturn, imgBtnReturnLy);
        imgBtnReturn.setOnClickListener(btnToMainMenu_Click);
        hidePauseControl();
    }
    
    private void hidePauseControl(){
        imgBtnSound.setVisibility(View.GONE);
        imgBtnReturn.setVisibility(View.GONE);
        imgBtnVibration.setVisibility(View.GONE);
    }
    
    private void showPauseControl(){
        imgBtnVibration.setVisibility(View.VISIBLE);
        imgBtnSound.setVisibility(View.VISIBLE);
        imgBtnReturn.setVisibility(View.VISIBLE);
    }
    
    private void changePrefSound(){
        if (DataAccess.Pref_IsSound){
            DataAccess.setSoundPref(this, false);
            AudioResource.stopBGM(AudioResource.BGMID_Play);
            imgBtnSound.setImageResource(R.drawable.btnsound_off);
        }else{
            DataAccess.setSoundPref(this, true);
            AudioResource.playBGM(this, AudioResource.BGMID_Play);
            imgBtnSound.setImageResource(R.drawable.btnsound_on);
        }
    }
    
    private void changePrefVibration(){
        if (DataAccess.Pref_IsVibration){
            DataAccess.setVibrationPref(this, false);
            imgBtnVibration.setImageResource(R.drawable.btnshake_off);

        }else{
            DataAccess.setVibrationPref(this, true);
            if (mVibrator!=null){
                mVibrator.vibrate(200);
            }
            imgBtnVibration.setImageResource(R.drawable.btnshake_on);
        }
    }
    
    private void createMenu(){
        if (menuReturn==null){
            menuReturn = new ImageView(this);
            menuReturn.setId(2000);
            menuReturn.setScaleType(ScaleType.FIT_CENTER);
            menuReturn.setImageResource(R.drawable.wbtnreturn);
            RelativeLayout.LayoutParams menuReturnLy = new RelativeLayout.LayoutParams(
                    GameResource.WBtn4Width, GameResource.WBtn4Height);
            menuReturnLy.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            menuReturnLy.leftMargin = GameResource.WBtnLeftMargin;
            mMainLayout.addView(menuReturn, menuReturnLy);
            menuReturn.setOnClickListener(btnToMainMenu_Click);
        }
        if (menuPostOnline==null){
            menuPostOnline = new ImageView(this);
            menuPostOnline.setId(2001);
            menuPostOnline.setScaleType(ScaleType.FIT_CENTER);
            menuPostOnline.setImageResource(R.drawable.wbtnpostonline);
            RelativeLayout.LayoutParams menuPostOnlineLy = new RelativeLayout.LayoutParams(
                    GameResource.WBtn3Width, GameResource.WBtn3Height);
            menuPostOnlineLy.addRule(RelativeLayout.ABOVE, menuReturn.getId());
            menuPostOnlineLy.addRule(RelativeLayout.ALIGN_LEFT, menuReturn.getId());
            mMainLayout.addView(menuPostOnline, menuPostOnlineLy);
            menuPostOnline.setOnClickListener(btnPostOnline_Click);
        }
        if (menuPostLocal==null){
            menuPostLocal = new ImageView(this);
            menuPostLocal.setId(2002);
            menuPostLocal.setScaleType(ScaleType.FIT_CENTER);
            menuPostLocal.setImageResource(R.drawable.wbtnpostlocal);
            RelativeLayout.LayoutParams menuPostLocalLy = new RelativeLayout.LayoutParams(
                    GameResource.WBtn2Width, GameResource.WBtn2Height);
            menuPostLocalLy.addRule(RelativeLayout.ABOVE, menuPostOnline.getId());
            menuPostLocalLy.addRule(RelativeLayout.ALIGN_LEFT, menuPostOnline.getId());
            mMainLayout.addView(menuPostLocal, menuPostLocalLy);
            menuPostLocal.setOnClickListener(btnPostLocal_Click);
        }
        if (menuRestart==null){
            menuRestart = new ImageView(this);
            menuRestart.setScaleType(ScaleType.FIT_CENTER);
            menuRestart.setImageResource(R.drawable.wbtnrestart);
            RelativeLayout.LayoutParams menuRestartLy = new RelativeLayout.LayoutParams(
                    GameResource.WBtn1Width, GameResource.WBtn1Height);
            menuRestartLy.addRule(RelativeLayout.ABOVE, menuPostLocal.getId());
            menuRestartLy.addRule(RelativeLayout.ALIGN_LEFT, menuPostLocal.getId());
            mMainLayout.addView(menuRestart, menuRestartLy);
            menuRestart.setOnClickListener(btnRestart_Click);
        } 
    }
    
    private void showMenu(){
        createMenu();
        menuRestart.setVisibility(View.VISIBLE);
        menuPostLocal.setVisibility(View.VISIBLE);
        menuPostOnline.setVisibility(View.VISIBLE);
        menuReturn.setVisibility(View.VISIBLE);
        menuRestart.startAnimation(mMenuInAnimations[0]);
        menuPostLocal.startAnimation(mMenuInAnimations[1]);
        menuPostOnline.startAnimation(mMenuInAnimations[2]);
        menuReturn.startAnimation(mMenuInAnimations[3]);
    }
    
    private void restartGame(){
        menuRestart.startAnimation(mMenuOutAnimations[0]);
        menuPostLocal.startAnimation(mMenuOutAnimations[1]);
        menuPostOnline.startAnimation(mMenuOutAnimations[2]);
        menuReturn.startAnimation(mMenuOutAnimations[3]);
    }
    
    private AlertDialog diagPostLocalScore;
    private void showPostLocalScoreDiag(){
        if (diagPostLocalScore==null){
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            final EditText txtInput = new EditText(this);
            ab.setPositiveButton(R.string.diag_btnSave, 
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = txtInput.getText().toString();
                            if (name==null || name.trim().length()==0){
                                name = actGameView.this.getString(R.string.anonymousUserID);
                            }
                            DataAccess.saveLocalScore(actGameView.this, name, AchievementMgt.StatData.Score);
                            Intent intent = new Intent(actGameView.this, actLocalScore.class);
                            actGameView.this.startActivity(intent);
                        }
                    });
            ab.setNegativeButton(R.string.diag_btnCancel, null);
            diagPostLocalScore = ab.create();
            diagPostLocalScore.setView(txtInput, 20, 10, 20, 10);
        }
        int score = AchievementMgt.StatData.Score;
        int rank = DataAccess.getScoreRank(this, score);
        diagPostLocalScore.setTitle(String.format(
                this.getString(R.string.diagPostLocalScore_TitleTp), score, rank));
        diagPostLocalScore.show();
    }
    
    private AnimationListener mMenuOutAnimation_Listener = new AnimationListener(){
        @Override
        public void onAnimationEnd(Animation animation) {
            menuRestart.setVisibility(View.GONE);
            menuPostLocal.setVisibility(View.GONE);
            menuPostOnline.setVisibility(View.GONE);
            menuReturn.setVisibility(View.GONE);
            ADManager.hideAdDelay(mAdView, 4);
            mGameView.restartGame();
        }
        @Override
        public void onAnimationRepeat(Animation animation) { }
        @Override
        public void onAnimationStart(Animation animation) { }
    };
    
    private OnClickListener btnVibration_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
            changePrefVibration();
        }
    };
    
    private OnClickListener btnSound_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
            changePrefSound();
        }
    };
    
    private OnClickListener btnToMainMenu_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
            actGameView.this.finish();
        }
    };
    
    private OnClickListener btnRestart_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
            restartGame();
        }
    };
    
    private OnClickListener btnPostLocal_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
            showPostLocalScoreDiag();
        }
    };
    
    private OnClickListener btnPostOnline_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
        	actGameView.this.beginUserInitiatedSignIn();
        }
    };

    // Google Play Service Game Client Callback
	@Override
	public void onSignInFailed() {
		// do nothing here, use default action that BaseGameUtils provide
	}
	
	private void showGotoOnlineDiag(){
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("View online result");
		ab.setMessage("Do you want view leaderboard or achievements online now?");
		ab.setPositiveButton("Leaderboard", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				startActivityForResult(getGamesClient().getLeaderboardIntent(
					actGameView.this.getString(R.string.playLeaderboardID)), 0);
			}
		});
		ab.setNeutralButton("Achievements", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				startActivityForResult(getGamesClient().getAchievementsIntent(), 0);
			}
		});
		ab.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		ab.show();
	}

	private int mAchievementSubmitCnt = 0;
	private int mAchievementFailCnt = 0;
	@Override
	public void onSignInSucceeded() {
		// post score
		mAchievementSubmitCnt = 0;
		mAchievementFailCnt = 0;
		final GamesClient client = this.getGamesClient();
		client.submitScoreImmediate(new OnScoreSubmittedListener(){
			@Override public void onScoreSubmitted(int statusCode,
					SubmitScoreResult result) {
				if (statusCode==GamesClient.STATUS_OK){
					// post score succeed, post achievement now
					final String[] localUnlockIDs = DataAccess.getLocalUnlockAchievementIDs(actGameView.this);
					if (localUnlockIDs!=null && localUnlockIDs.length>0){
						for (int i=0;i<localUnlockIDs.length;i++){
							client.unlockAchievementImmediate(new OnAchievementUpdatedListener(){
								@Override public void onAchievementUpdated(int statusCode, String achievementId) {
									mAchievementSubmitCnt++;
									if (statusCode!=GamesClient.STATUS_OK){
										mAchievementFailCnt++;
									}
									if (mAchievementSubmitCnt==localUnlockIDs.length){
										if (mAchievementFailCnt > 0){ // part failed
											Toast.makeText(actGameView.this, 
												actGameView.this.getString(R.string.tipSubmitAchievementOnlineFail), 
												Toast.LENGTH_LONG).show();
										}
										showGotoOnlineDiag();
									}
								}
							}, localUnlockIDs[i]);
						}
					}else{
						showGotoOnlineDiag();
					}
				}else{
					Toast.makeText(actGameView.this, 
						actGameView.this.getString(R.string.tipSubmitScoreAndAchievementOnlineFail), 
						Toast.LENGTH_LONG).show();
				}
			}
		}, this.getString(R.string.playLeaderboardID), AchievementMgt.StatData.Score);
	}
}