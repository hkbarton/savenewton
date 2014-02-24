package ml.game.android.SaveNewton;

import com.google.android.gms.ads.AdView;
import com.google.example.games.basegameutils.BaseGameActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class actMain extends BaseGameActivity {
    private static final int MenuType_Main = 0;
    private static final int MenuType_Online = 2;
    private static final int LoadEvent_FinishLoadResource = 0;
    
    private boolean mLoadComplete = false;
    private int mMenuType = MenuType_Main;
    private ImageView imgLoadingTip,imgBtnSound,imgBtnVibration;
    private ImageView btnMenu1, btnMenu2, btnMenu3, btnMenu4;
    private RelativeLayout mainly;
    private Animation[] mMenuInAnimations, mMenuOutAnimations;
    private AdView mAdView;
    
    private Handler mLoadEventHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
            case LoadEvent_FinishLoadResource:
                finishLoadResource();
                break;
            }
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameResource.preInit(this.getWindowManager().getDefaultDisplay().getWidth(), 
                this.getWindowManager().getDefaultDisplay().getHeight());
        initLayout();
        mAdView = ADManager.loadAD(this, mainly);
        asyncLoadResource();
    }
    
    @Override
    public void onStart(){
        super.onStart();
        setBtnStatusByData();
        if (mLoadComplete && DataAccess.Pref_IsSound){
            AudioResource.playBGM(this, AudioResource.BGMID_Title);
        }
    }
    
    
    @Override
    public void onResume(){
    	super.onResume();
    	ADManager.refreshAD(mAdView);
    }
    
    @Override
    public void onStop(){
        super.onStop();
        AudioResource.stopBGM(AudioResource.BGMID_Title);
    }
    
    @Override
    public void onDestroy(){
        super.onDestroy();
        AudioResource.releaseResource();
    }
    
    private void initLayout(){
        setContentView(R.layout.main);
        mainly = (RelativeLayout)this.findViewById(R.id.actMain_MainLayout);
        imgLoadingTip = new ImageView(this);
        imgLoadingTip.setScaleType(ScaleType.FIT_CENTER);
        imgLoadingTip.setImageResource(R.drawable.loadingtitle);
        RelativeLayout.LayoutParams loadingTipLayout = new RelativeLayout.LayoutParams(
                GameResource.MenuLoadingTipWidth, GameResource.MenuLoadingTipHeight);
        loadingTipLayout.addRule(RelativeLayout.CENTER_IN_PARENT);
        mainly.addView(imgLoadingTip, loadingTipLayout);
    }
    
    private void asyncLoadResource(){
        Thread loadThread = new Thread(){
            @Override
            public void run(){
                GameResource.init(actMain.this.getResources());
                AudioResource.initAudio(actMain.this);
                AchievementMgt.initAchievements();
                DataAccess.init(actMain.this);
                AchievementMgt.init(actMain.this);
                mMenuInAnimations = new Animation[4];
                mMenuOutAnimations = new Animation[4];
                mMenuInAnimations[0] = AnimationUtils.loadAnimation(actMain.this, R.anim.menu_popup);
                mMenuInAnimations[1] = AnimationUtils.loadAnimation(actMain.this, R.anim.menu_popup);
                mMenuInAnimations[2] = AnimationUtils.loadAnimation(actMain.this, R.anim.menu_popup);
                mMenuInAnimations[3] = AnimationUtils.loadAnimation(actMain.this, R.anim.menu_popup);
                mMenuOutAnimations[0] = AnimationUtils.loadAnimation(actMain.this, R.anim.menu_dis);
                mMenuOutAnimations[1] = AnimationUtils.loadAnimation(actMain.this, R.anim.menu_dis);
                mMenuOutAnimations[2] = AnimationUtils.loadAnimation(actMain.this, R.anim.menu_dis);
                mMenuOutAnimations[3] = AnimationUtils.loadAnimation(actMain.this, R.anim.menu_dis);
                mMenuOutAnimations[0].setAnimationListener(mMenuOutAnimation_Listener);
                mLoadEventHandler.sendEmptyMessage(LoadEvent_FinishLoadResource);
            }
        };
        loadThread.start();
    }
    
    private void finishLoadResource(){
        mLoadComplete = true;
        if (DataAccess.Pref_IsSound){
            AudioResource.playBGM(this, AudioResource.BGMID_Title);
        }
        imgLoadingTip.setVisibility(View.INVISIBLE);
        initLayoutAfterLoad();
    }
    
    private void initLayoutAfterLoad(){
        ImageView imgBtnClose = new ImageView(this);
        imgBtnClose.setScaleType(ScaleType.FIT_CENTER);
        imgBtnClose.setImageResource(R.drawable.btnclose);
        RelativeLayout.LayoutParams imgBtnCloseLy = new RelativeLayout.LayoutParams(
                GameResource.SmallBtnSize, GameResource.SmallBtnSize);
        imgBtnCloseLy.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imgBtnCloseLy.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        imgBtnCloseLy.setMargins(0, 5, 10, 0);
        mainly.addView(imgBtnClose, imgBtnCloseLy);
        imgBtnClose.setOnClickListener(btnClose_Click);
        imgBtnSound = new ImageView(this);
        imgBtnSound.setId(1000);
        imgBtnSound.setScaleType(ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams imgBtnSoundLy = new RelativeLayout.LayoutParams(
                GameResource.NormalBtnSize, GameResource.NormalBtnSize);
        imgBtnSoundLy.addRule(RelativeLayout.CENTER_VERTICAL);
        imgBtnSoundLy.leftMargin = 10;
        mainly.addView(imgBtnSound,imgBtnSoundLy);
        imgBtnSound.setOnClickListener(btnSound_Click);
        imgBtnVibration = new ImageView(this);
        imgBtnVibration.setScaleType(ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams imgBtnVibrationLy = new RelativeLayout.LayoutParams(
                GameResource.NormalBtnSize, GameResource.NormalBtnSize);
        imgBtnVibrationLy.addRule(RelativeLayout.BELOW, imgBtnSound.getId());
        imgBtnVibrationLy.addRule(RelativeLayout.ALIGN_LEFT, imgBtnSound.getId());
        imgBtnVibrationLy.topMargin = 10;
        mainly.addView(imgBtnVibration, imgBtnVibrationLy);
        imgBtnVibration.setOnClickListener(btnVibration_Click);
        ImageView imgBtnInfo = new ImageView(this);
        imgBtnInfo.setScaleType(ScaleType.FIT_CENTER);
        imgBtnInfo.setImageResource(R.drawable.btninfo);
        RelativeLayout.LayoutParams imgBtnInfoLy = new RelativeLayout.LayoutParams(
                GameResource.NormalBtnSize, GameResource.NormalBtnSize);
        imgBtnInfoLy.addRule(RelativeLayout.ABOVE, imgBtnSound.getId());
        imgBtnInfoLy.addRule(RelativeLayout.ALIGN_LEFT, imgBtnSound.getId());
        imgBtnInfoLy.bottomMargin = 10;
        mainly.addView(imgBtnInfo, imgBtnInfoLy);
        imgBtnInfo.setOnClickListener(btnInfo_Click);
        setBtnStatusByData();
        initMenuLayout();
        setMenuEvent();
    }
    
    private void setBtnStatusByData(){
        if (imgBtnSound!=null){
            if (DataAccess.Pref_IsSound){
                imgBtnSound.setImageResource(R.drawable.btnsound_on);
            }else{
                imgBtnSound.setImageResource(R.drawable.btnsound_off);
            }
        }
        if (imgBtnVibration!=null){
            if (DataAccess.Pref_IsVibration){
                imgBtnVibration.setImageResource(R.drawable.btnshake_on);
            }else{
                imgBtnVibration.setImageResource(R.drawable.btnshake_off);
            }
        }
    }
    
    private void animateInMenu(){
    	btnMenu1.startAnimation(mMenuInAnimations[0]);
        btnMenu2.startAnimation(mMenuInAnimations[1]);
        btnMenu3.startAnimation(mMenuInAnimations[2]);
        btnMenu4.startAnimation(mMenuInAnimations[3]);
    }
    
    private void animateOutMenu(){
    	btnMenu1.startAnimation(mMenuOutAnimations[0]);
        btnMenu2.startAnimation(mMenuOutAnimations[1]);
        btnMenu3.startAnimation(mMenuOutAnimations[2]);
        btnMenu4.startAnimation(mMenuOutAnimations[3]);
    }
    
    private void initMenuLayout(){
        mMenuType = MenuType_Main;
        btnMenu4 = new ImageView(this);
        btnMenu4.setId(2000);
        btnMenu4.setScaleType(ScaleType.FIT_CENTER);
        btnMenu4.setImageResource(R.drawable.wbtnonlinesel);
        RelativeLayout.LayoutParams menu4Ly = new RelativeLayout.LayoutParams(
                GameResource.WBtn4Width, GameResource.WBtn4Height);
        menu4Ly.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        menu4Ly.leftMargin = GameResource.WBtnLeftMargin;
        mainly.addView(btnMenu4, menu4Ly);
        btnMenu3 = new ImageView(this);
        btnMenu3.setId(2001);
        btnMenu3.setScaleType(ScaleType.FIT_CENTER);
        btnMenu3.setImageResource(R.drawable.wbtnscoresel);
        RelativeLayout.LayoutParams menu3Ly = new RelativeLayout.LayoutParams(
                GameResource.WBtn3Width, GameResource.WBtn3Height);
        menu3Ly.addRule(RelativeLayout.ABOVE, btnMenu4.getId());
        menu3Ly.addRule(RelativeLayout.ALIGN_LEFT, btnMenu4.getId());
        mainly.addView(btnMenu3, menu3Ly);
        btnMenu2 = new ImageView(this);
        btnMenu2.setId(2002);
        btnMenu2.setScaleType(ScaleType.FIT_CENTER);
        btnMenu2.setImageResource(R.drawable.wbtnstoresel);
        RelativeLayout.LayoutParams menu2Ly = new RelativeLayout.LayoutParams(
                GameResource.WBtn2Width, GameResource.WBtn2Height);
        menu2Ly.addRule(RelativeLayout.ABOVE, btnMenu3.getId());
        menu2Ly.addRule(RelativeLayout.ALIGN_LEFT, btnMenu3.getId());
        mainly.addView(btnMenu2, menu2Ly);
        btnMenu1 = new ImageView(this);
        btnMenu1.setScaleType(ScaleType.FIT_CENTER);
        btnMenu1.setImageResource(R.drawable.wbtnplaysel);
        RelativeLayout.LayoutParams menu1Ly = new RelativeLayout.LayoutParams(
                GameResource.WBtn1Width, GameResource.WBtn1Height);
        menu1Ly.addRule(RelativeLayout.ABOVE, btnMenu2.getId());
        menu1Ly.addRule(RelativeLayout.ALIGN_LEFT, btnMenu2.getId());
        mainly.addView(btnMenu1, menu1Ly);
        animateInMenu();
    }
    
    private void setMenuEvent(){
        if (mMenuType==MenuType_Main){
            btnMenu1.setOnClickListener(menuPlay_Click);
            btnMenu2.setOnClickListener(menuStore_Click);
            btnMenu3.setOnClickListener(menuScore_Click);
            btnMenu4.setOnClickListener(menuOnline_Click);
        }else if (mMenuType==MenuType_Online){
        	// TODO
        	btnMenu4.setOnClickListener(menuToMainMenu_Click);
        }
    }
    
    private void toOnlineMenu(){
    	mMenuType = MenuType_Online;
    	animateOutMenu();
    }
    
    private void toMainMenu(){
        mMenuType = MenuType_Main;
        animateOutMenu();
    }
    
    private void resetMenuByType(){
        if (mMenuType==MenuType_Main){
            btnMenu1.setImageResource(R.drawable.wbtnplaysel);
            btnMenu2.setImageResource(R.drawable.wbtnstoresel);
            btnMenu3.setImageResource(R.drawable.wbtnscoresel);
            btnMenu4.setImageResource(R.drawable.wbtnonlinesel);
        }else if (mMenuType==MenuType_Online){
        	btnMenu1.setImageResource(R.drawable.wbtnonlinetop);
        	btnMenu2.setImageResource(R.drawable.wbtnonlinelb);
            btnMenu3.setImageResource(R.drawable.wbtnonlineac);
        	btnMenu4.setImageResource(R.drawable.wbtnreturn);
        }
        setMenuEvent();
    }
    
    private AnimationListener mMenuOutAnimation_Listener = new AnimationListener(){
        @Override
        public void onAnimationEnd(Animation animation) {
            resetMenuByType();
            animateInMenu();
        }
        @Override
        public void onAnimationRepeat(Animation animation) {}
        @Override
        public void onAnimationStart(Animation animation) {}
    };
    
    private void changePrefSound(){
        if (DataAccess.Pref_IsSound){
            DataAccess.setSoundPref(actMain.this, false);
            AudioResource.stopBGM(AudioResource.BGMID_Title);
            imgBtnSound.setImageResource(R.drawable.btnsound_off);
        }else{
            DataAccess.setSoundPref(actMain.this, true);
            AudioResource.playBGM(actMain.this, AudioResource.BGMID_Title);
            imgBtnSound.setImageResource(R.drawable.btnsound_on);
        }
    }
    
    private void changePrefVibration(){
        if (DataAccess.Pref_IsVibration){
            DataAccess.setVibrationPref(actMain.this, false);
            imgBtnVibration.setImageResource(R.drawable.btnshake_off);
        }else{
            DataAccess.setVibrationPref(actMain.this, true);
            Vibrator vibrator = (Vibrator)actMain.this.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator!=null){
                vibrator.vibrate(200);
            }
            imgBtnVibration.setImageResource(R.drawable.btnshake_on);
        }
    }
    
    private AlertDialog diagHelp;
    private void showHelpDiag(){
        if (diagHelp==null){
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setTitle(R.string.diagInfo_Title);
            ab.setPositiveButton(R.string.diag_btnOK, null);
            diagHelp = ab.create();
            LayoutInflater lyInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            diagHelp.setView(lyInflater.inflate(R.layout.diaginfo, null));
        }
        diagHelp.show();
    }
    
    private OnClickListener btnClose_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
            actMain.this.finish();
        }
    };
    
    private OnClickListener btnSound_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
            changePrefSound();
        }
    };
    
    private OnClickListener btnVibration_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
            changePrefVibration();
        }
    };
    
    private OnClickListener btnInfo_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
            showHelpDiag();
        }
    };
    
    private OnClickListener menuPlay_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(actMain.this, actGameView.class);
            actMain.this.startActivity(intent);
        }
    };
    
    private OnClickListener menuScore_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(actMain.this, actLocalScore.class);
            actMain.this.startActivity(intent);
        }
    };
    
    private OnClickListener menuOnline_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
        	actMain.this.beginUserInitiatedSignIn();
        }
    };
    
    private OnClickListener menuStore_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
        	Intent intent = new Intent(actMain.this, actStore.class);
            actMain.this.startActivity(intent);
        }
    };
    
    private OnClickListener menuToMainMenu_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
            toMainMenu();
        }
    };

	@Override
	public void onSignInFailed() {
		// do nothing here, use default action that BaseGameUtils provide
	}

	@Override
	public void onSignInSucceeded() {
		toOnlineMenu();
	}
}