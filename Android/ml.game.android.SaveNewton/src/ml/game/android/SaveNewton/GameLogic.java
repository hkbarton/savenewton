package ml.game.android.SaveNewton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import android.os.Handler;

// develop note
// 1. Golden apple is strong bow, green apple is weak bow, each contain 5 ammo
// 2. 1 apple = 1 apple score, but *2 when shot by weak bow

public class GameLogic{
	public static final float Default_DifficultyLevel = 1.4f;
	public static final int Default_AppleScore = 1;
	public static final int WeakBow_ScoreMultiple = 2;
	
    public static final int GameStatus_Running = 0;
    public static final int GameStatus_Pause = 1;
    public static final int GameStatus_GameOvering = 2;
    public static final int GameStatus_GameOver = 3;
    
    public static final int GameEvent_BowInit = 0;
    public static final int GameEvent_ReleaseArrow = 1;
    public static final int GameEvent_ArrowInApple = 2;
    public static final int GameEvent_ArrowInNewton = 3;
    public static final int GameEvent_ArrowEnd = 4;
    public static final int GameEvent_EarnPrize = 5;
    public static final int GameEvent_AppleHitNewton = 6;
    public static final int GameEvent_LowGravityCountDown = 7;
    public static final int GameEvent_NewtonSpeak = 8;
    public static final int GameEvent_GamePause = 100;
    public static final int GameEvent_GameResume = 101;
    public static final int GameEvent_GameOvering = 102;
    public static final int GameEvent_GameOver = 103;
    public static final int GameEvent_StatDataChange = 200;
    
    // base gravity in milliseconds, means the time cost that apple fall from top to bottom
    private static final int BaseGravity = 5000;
    private static final int GameOverGravity = 2000;
    private static final int GameOveringTime = 2500;
    private static final float CloudRunSpeed = 22000f;
    
    // Special Apple Setting
    private static final int SpecialApple_CoolTime = 5000;
    private long mLastSpecialAppleCreateTime;
    private int mGoldenAppleChance, mWeakAppleChance, mGravityAppleChance;
    // Gravity Apple
    private static final int LowGravityRate = 5;
    private static final int LowGravityApple_PersistTime = 6000;
    private long mLowGravityTime;
    private boolean mIsLowGravity;
    private boolean[] mLowGravityAppCountDownShowFlag;
    
    // the time space in millisecond between two apple create
    // the minimum value of this should greater than AppleGrownupSpeed
    private static final int AppleCreateSpeed_Init = 3000;
    private static final int AppleCreateSpeed_CutDown_Value = 300;
    private static final int AppleCreateSpeed_BaseMinValue = 800;
    private static final int AppleCreateSpeedChangeTime = 6000;
    private static final double DegreeToRadianConst = Math.PI / 180;
    
    private Handler mGameEventHandler;
    private Random mRandomGenerator;
    private int mGameStatus, mSavedGameStatusWhenPause;
    private long mLastFrameTime;
    private long mGameStartTime;
    private long mStartPauseTime, mTotalPauseTime;
    private int mAppleCreateSpeed, mCurAppleCreateSpeed;
    private long mLastAppleCreateTime;
    private long mPassedTimeFromLastAppleCreate;
    private Stack<Integer> mNextAppleType;
    private int mGravity, mCurGravity;
    private int mAppleCreateSpeedMinValue;
    private int mGameOveringTime;
    
    public float CloudLeftPos, CloudTopPos1, CloudTopPos2;
    
    private List<Apple> mRemovedApple;
    private List<Arrow> mRemovedArrow;
    private List<TipText> mRemovedTips;
    public List<Apple> Apples;
    public List<Arrow> Arrows;
    public List<TipText> Tips;
    public Bow CurBow;
    public Newton CurNewton;
    
    public GameLogic(Handler gameEventHandler){
        mGameEventHandler = gameEventHandler;
        Apples = new ArrayList<Apple>();
        mRemovedApple = new ArrayList<Apple>();
        Arrows = new ArrayList<Arrow>();
        mRemovedArrow = new ArrayList<Arrow>();
        Tips = new ArrayList<TipText>();
        mRemovedTips = new ArrayList<TipText>();
        mCurGravity = mGravity = (int)(BaseGravity + BaseGravity * (1-Default_DifficultyLevel));
        mAppleCreateSpeedMinValue = (int)(AppleCreateSpeed_BaseMinValue + 
                AppleCreateSpeed_BaseMinValue * (1-Default_DifficultyLevel));
        mNextAppleType = new Stack<Integer>();
        mLowGravityAppCountDownShowFlag = new boolean[LowGravityApple_PersistTime/1000];
        CloudLeftPos = GameResource.GameStageCloudInitLeftPos;
        CloudTopPos1 = GameResource.GameStageCloudTopPos1;
        CloudTopPos2 = GameResource.GameStageCloudTopPos2;
        resetGame();
    }
    
    public void resetGame(){
        mCurGravity = mGravity;
        mLastFrameTime = 0;
        mGameStartTime = 0;
        mGameStatus = GameStatus_Running;
        mAppleCreateSpeed = mCurAppleCreateSpeed = AppleCreateSpeed_Init;
        mLastAppleCreateTime = 0;
        mPassedTimeFromLastAppleCreate = 0;
        mStartPauseTime = 0;
        mTotalPauseTime = 0;
        mLastSpecialAppleCreateTime = 0;
        mLowGravityTime = 0;
        mIsLowGravity = false;
        mGoldenAppleChance = DataAccess.getChanceDataByLevel(DataAccess.GDGoldenAppleLevel);
        mWeakAppleChance = DataAccess.getChanceDataByLevel(DataAccess.GDGreenAppleLevel);
        mGravityAppleChance = DataAccess.getChanceDataByLevel(DataAccess.GDGravityAppleLevel);
        Apples.clear();
        mRemovedApple.clear();
        Arrows.clear();
        mRemovedArrow.clear();
        CurBow = new Bow();
        mRandomGenerator = new Random();
        mNextAppleType.clear();
        mNextAppleType.push(Apple.AppleType_Normal);
        Tips.clear();
        mRemovedTips.clear();
        CurNewton = new Newton();
        resetLowGravityAppCountDownShowFlag();
        mGameOveringTime = 0;
        AchievementMgt.resetRoundStatData();
    }
    
    public void pauseGame(long pauseTime){
        if (mGameStatus==GameStatus_Running || mGameStatus==GameStatus_GameOvering){
            mSavedGameStatusWhenPause = mGameStatus;
            mGameStatus = GameStatus_Pause;
            mPassedTimeFromLastAppleCreate = pauseTime - mLastAppleCreateTime;
            mStartPauseTime = pauseTime;
            mGameEventHandler.sendEmptyMessage(GameEvent_GamePause);
        }
    }
    
    public void resumeGame(long resumeTime){
        if (mGameStatus==GameStatus_Pause){
            mGameStatus = mSavedGameStatusWhenPause;
            mLastFrameTime = resumeTime;
            mLastAppleCreateTime = resumeTime - mPassedTimeFromLastAppleCreate;
            mTotalPauseTime += resumeTime - mStartPauseTime;
            mGameEventHandler.sendEmptyMessage(GameEvent_GameResume);
        }
    }
    
    public int getGameStatus(){
        return mGameStatus;
    }
    
    public void dealGamePauseSignalUserInput(float x){
        if (mGameStatus!=GameStatus_GameOver
                && x>GameResource.BowHandleRightPosLimit && x<=GameResource.GameStageWidth){
            if (mGameStatus==GameStatus_Pause){
                resumeGame(System.currentTimeMillis());
            }else{
                pauseGame(System.currentTimeMillis());
            }
        }
    }
    
    public void runGame(long curFrameTime){
        if (mGameStatus==GameStatus_Running || mGameStatus==GameStatus_GameOvering){
            if (mLastFrameTime==0){ // first frame
                mGameStartTime = curFrameTime;
                mLastFrameTime = curFrameTime;
                return;
            }
            int timeDisFromLastFrame = (int)(curFrameTime - mLastFrameTime);
            runCloud(timeDisFromLastFrame);
            if (mGameStatus==GameStatus_Running){
                CurBow.runBow(timeDisFromLastFrame);
                runArrow(timeDisFromLastFrame);
                runApple(timeDisFromLastFrame);
                generateApple(curFrameTime);
                runTip(timeDisFromLastFrame);
                removeObject();
                speedUpAppleCreate(curFrameTime);
                runLowGravity(timeDisFromLastFrame);
                if (CurNewton!=null){
                    CurNewton.run(timeDisFromLastFrame);
                }
            }else{// Game Overing
                mCurGravity = GameOverGravity;
                runArrow(timeDisFromLastFrame);
                runApple(timeDisFromLastFrame);
                runTip(timeDisFromLastFrame);
                removeObject();
                if (CurNewton!=null){
                    CurNewton.run(timeDisFromLastFrame);
                }
                mGameOveringTime += timeDisFromLastFrame;
                if (mGameOveringTime>=GameOveringTime){
                    mGameStatus = GameStatus_GameOver;
                    mGameEventHandler.sendEmptyMessage(GameEvent_GameOver);
                }
            }
            mLastFrameTime = curFrameTime;
        }
    }
    
    private void runCloud(int timeDisFromLastFrame){
        CloudLeftPos -= timeDisFromLastFrame * (GameResource.GameStageWidth/CloudRunSpeed);
        if (CloudLeftPos<=-GameResource.GameStageCloudWidth){
            CloudLeftPos += GameResource.GameStageCloudDis;
            // swap top position
            float tmp = CloudTopPos1;
            CloudTopPos1 = CloudTopPos2;
            CloudTopPos2 = tmp;
        }
    }
    
    private void runArrow(int timeDisFromLastFrame){
        for(int i=0;i<Arrows.size();i++){
            Arrows.get(i).run(timeDisFromLastFrame);
        }
    }
    
    private void runApple(int timeDisFromLastFrame){
        for(int i=0;i<Apples.size();i++){
            Apples.get(i).run(timeDisFromLastFrame);
        }
    }
    
    private void generateApple(long curFrameTime){
        boolean needCreate = false;
        if (mLastAppleCreateTime==0 || curFrameTime - mLastAppleCreateTime >= mCurAppleCreateSpeed){
            needCreate = true;
        }
        if (needCreate){
            int appleType = mNextAppleType.pop();
            Apples.add(new Apple(mRandomGenerator.nextInt(GameResource.AppleCreatePos_Count), appleType)); 
            mLastAppleCreateTime = curFrameTime;
            // check if need create special apple
            if (curFrameTime - mLastSpecialAppleCreateTime > SpecialApple_CoolTime){
            	// generate low gravity apple
            	if (mRandomGenerator.nextInt(mGoldenAppleChance) > mGoldenAppleChance-2){
            		mNextAppleType.push(Apple.AppleType_Golden);
            	}else if (mRandomGenerator.nextInt(mWeakAppleChance) > mWeakAppleChance-2){
            		mNextAppleType.push(Apple.AppleType_Weak);
            	}else if (mRandomGenerator.nextInt(mGravityAppleChance) > mGravityAppleChance-2){
	                mNextAppleType.push(Apple.AppleType_LowGravity);
	            }
            }
            if (!mNextAppleType.isEmpty()){ // have alreday create special apple
            	mLastSpecialAppleCreateTime = curFrameTime;
            }else{
            	mNextAppleType.push(Apple.AppleType_Normal);
            }
        }
    }
    
    private void runTip(int timeDisFromLastFrame){
        for (int i=0; i<Tips.size(); i++){
            Tips.get(i).run(timeDisFromLastFrame);
        }
    }
    
    private void removeObject(){
        for(int i=0;i<mRemovedApple.size();i++){
            Apples.remove(mRemovedApple.get(i));
        }
        mRemovedApple.clear();
        for (int i=0;i<mRemovedArrow.size();i++){
            Arrows.remove(mRemovedArrow.get(i));
        }
        mRemovedArrow.clear();
        for (int i=0;i<mRemovedTips.size();i++){
            Tips.remove(mRemovedTips.get(i));
        }
        mRemovedTips.clear();
    }
    
    private void hitApple(int appleType, int arrowType, float hitCenterLeftPos, float hitCenterTopPos){
        AchievementMgt.StatData.ContinueShootCount++;
        AchievementMgt.StatData.ContinueMissCount = 0;
        int addedScore = Default_AppleScore;
        if (arrowType==Arrow.ArrowType_Weak){
            addedScore *= WeakBow_ScoreMultiple;
            AchievementMgt.StatData.WeakBowShootCount++;
            AchievementMgt.StatData.ContinueWeakBowShootCount++;
        }else{
        	AchievementMgt.StatData.ContinueWeakBowShootCount = 0;
        }
        switch(appleType){
        case Apple.AppleType_Golden:
        	mGameEventHandler.sendEmptyMessage(GameEvent_EarnPrize);
        	// TODO instead change weapon immediately, save weapon ammo instead, add special tips
            CurBow.setBowType(Bow.BowType_Golden);
            break;
        case Apple.AppleType_Weak:
        	mGameEventHandler.sendEmptyMessage(GameEvent_EarnPrize);
        	// TODO instead change weapon immediately, save weapon ammo instead, add special tips
            CurBow.setBowType(Bow.BowType_Weak);
            break;
        case Apple.AppleType_LowGravity:
            mCurGravity = mGravity * LowGravityRate;
            mCurAppleCreateSpeed = mAppleCreateSpeed * LowGravityRate;
            mLowGravityTime = 0;
            mIsLowGravity = true;
            Tips.add(new TipText(GameResource.ScaleTipText_LowGravityApple_Frame,
                    GameResource.GameCenterLeftPos, GameResource.GameCenterTopPos,
                    TipText.ScaleTextTipScaleTime_Normal, TipText.ScaleTextTipRemoveTime_Normal, 0));
            break;
        }
        AchievementMgt.StatData.Score += addedScore;
        mGameEventHandler.sendEmptyMessage(GameEvent_StatDataChange);
        Tips.add(new TipText(hitCenterLeftPos, hitCenterTopPos, addedScore));
    }
    
    private void missHit(){
        AchievementMgt.StatData.ContinueMissCount++;
        AchievementMgt.StatData.ContinueShootCount = 0;
        AchievementMgt.StatData.ContinueWeakBowShootCount = 0;
        mGameEventHandler.sendEmptyMessage(GameEvent_StatDataChange);
    }
    
    private void speedUpAppleCreate(long curFrameTime){
        if (mAppleCreateSpeed > mAppleCreateSpeedMinValue){
            long passTimeFromGameStart = curFrameTime - mGameStartTime - mTotalPauseTime;
            int curAppleCreateSpeed = (int)(AppleCreateSpeed_Init - 
                (passTimeFromGameStart / AppleCreateSpeedChangeTime) * AppleCreateSpeed_CutDown_Value);
            mAppleCreateSpeed = mCurAppleCreateSpeed = curAppleCreateSpeed < mAppleCreateSpeedMinValue ? 
                    mAppleCreateSpeedMinValue : curAppleCreateSpeed;
        }
    }
    
    private void resetLowGravityAppCountDownShowFlag(){
        for (int i=0;i<mLowGravityAppCountDownShowFlag.length;i++){
        	mLowGravityAppCountDownShowFlag[i] = false;
        }
    }
    
    private void runLowGravity(int timeDisFromLastFrame){
        if (mIsLowGravity){
        	mLowGravityTime += timeDisFromLastFrame;
            if (mLowGravityTime >= LowGravityApple_PersistTime){
                mCurAppleCreateSpeed = mAppleCreateSpeed;
                mCurGravity = mGravity;
                mLowGravityTime = 0;
                mIsLowGravity = false;
                resetLowGravityAppCountDownShowFlag();
            }else{
                int remainTime = (int)(LowGravityApple_PersistTime - mLowGravityTime);
                int remainTimeInSecond = Math.round(remainTime / 1000f);
                int roundRemainTime = remainTimeInSecond * 1000;
                if (roundRemainTime < LowGravityApple_PersistTime && remainTime <= roundRemainTime
                        && !mLowGravityAppCountDownShowFlag[remainTimeInSecond]){
                    mGameEventHandler.sendEmptyMessage(GameEvent_LowGravityCountDown);
                    mLowGravityAppCountDownShowFlag[remainTimeInSecond] = true;
                    Tips.add(new TipText(GameResource.ScaleTipText_CountDown_Frame,
                            GameResource.GameCenterLeftPos, GameResource.GameCenterTopPos, 
                             TipText.ScaleTextTipScaleTime_Fast, TipText.ScaleTextTipRemoveTime_Fast, 
                             remainTimeInSecond));
                }
            }
        }
    }
    
    public class Apple{
        public static final int AppleType_Normal = 0;
        public static final int AppleType_Golden = 1;
        public static final int AppleType_Weak = 2;
        public static final int AppleType_LowGravity = 3;
        
        private static final int AppleStatus_Normal = 0;
        private static final int AppleStatus_Exploded = 1;
        private static final int AppleStatus_Ground = 2;
        private static final int AppleStatus_AfterHitNewton = 3;
        
        private static final int AppleGrownupSpeed = 300;
        private static final int AppleDisappearSpeed = 200;
        private static final int AppleExplodTime = 320;
        private static final int AppleLandscapeSpeedAfterHitNewton = 3000;
        private static final float AppleDegreeAfterHitNewton = 120f;
        private static final int AppleDegreeChangeTimeAfterHitNewton = 600;
        
        private int mCreateTime, mGroundTime, mExplodTime;
        private int mStatus;
        private float mHitNewtonTopPos, mHitNewtonLeftPos;
        
        public int Type;
        public float Transparence; // from 0 to 1, 0 means completely transparent
        public float ScaleRate;
        public float TopPos, LeftPos; // position in Pixel, used to draw
        public int CurrentShowFrame;
        public float Degree;
        
        // Value of createPos is 0-5, means the position of the apple appear
        public Apple(int createPos, int type){ 
            Type = type;
            Transparence = 1f;
            ScaleRate = 1f / GameResource.AppleGrownup_FrameCount;
            TopPos = GameResource.AppleInitTopPos;
            LeftPos = GameResource.AppleLeftPos[createPos];
            CurrentShowFrame = 0;
            mStatus = AppleStatus_Normal;
            mCreateTime = 0;
            mGroundTime = 0;
            mExplodTime = 0;
            Degree = 0;
        }
        
        private void collisionDetect(){
            if (CurrentShowFrame==GameResource.AppleNormal_Frame && mStatus==AppleStatus_Normal){
                // check shot by arrow
                List<Arrow> arrows = Arrows;
                for (int i=0;i<arrows.size();i++){
                    if (arrows.get(i).isShotApple(LeftPos, TopPos)){
                        mGameEventHandler.sendEmptyMessage(GameEvent_ArrowInApple);
                        hitApple(Type, arrows.get(i).mType, LeftPos + GameResource.AppleWidth/2, 
                                TopPos + GameResource.AppleHeight/2);
                        mStatus = AppleStatus_Exploded;
                        break;
                    }
                }
                // check hit newton
                if (CurNewton!=null && mStatus==AppleStatus_Normal && CurNewton.isHittedByApple(LeftPos, TopPos)){
                    mGameEventHandler.sendEmptyMessage(GameEvent_StatDataChange);
                    mGameEventHandler.sendEmptyMessage(GameEvent_AppleHitNewton);
                    mStatus = AppleStatus_AfterHitNewton;
                    mHitNewtonTopPos = TopPos;
                    mHitNewtonLeftPos = LeftPos;
                    mGameStatus = GameStatus_GameOvering;
                    mGameEventHandler.sendEmptyMessage(GameEvent_GameOvering);
                }
            }
        }
        
        public void run(int timeDisFromLastFrame){
            int framePassed = 0;
            int gravity = mCurGravity;
            switch(mStatus){
            case AppleStatus_Normal:
                mCreateTime += timeDisFromLastFrame;
                if (mCreateTime<=AppleGrownupSpeed){ // grown up
                    ScaleRate = ((float)mCreateTime / 
                            (AppleGrownupSpeed / GameResource.AppleGrownup_FrameCount) + 1) / 
                            (float)GameResource.AppleGrownup_FrameCount;
                    ScaleRate = ScaleRate>1 ? 1 : ScaleRate;
                }else{ // fall
                    ScaleRate = 1;
                    CurrentShowFrame = GameResource.AppleNormal_Frame;
                    TopPos += timeDisFromLastFrame * (GameResource.GameStageHeight / (float)gravity);
                    if (TopPos >= GameResource.GameStageBottom - GameResource.AppleHeight){
                        TopPos = GameResource.GameStageBottom - GameResource.AppleHeight;
                        mStatus = AppleStatus_Ground;
                    }
                }
                collisionDetect();
                break;
            case AppleStatus_Exploded:
                mExplodTime += timeDisFromLastFrame;
                framePassed = (int)(mExplodTime / 
                        (AppleExplodTime / GameResource.AppleExplod_FrameCount));
                if (framePassed>0){
                    CurrentShowFrame = GameResource.AppleExplod_BeginFrame + framePassed - 1;
                }
                if (CurrentShowFrame > GameResource.AppleExplod_EndFrame){
                    mRemovedApple.add(this);
                }
                break;
            case AppleStatus_Ground:
                mGroundTime += timeDisFromLastFrame;
                framePassed = (int)(mGroundTime / 
                        (AppleDisappearSpeed/GameResource.AppleDisappear_FrameCount));
                Transparence = 1 - framePassed * GameResource.Disappear_Transparent_Disvalue;
                if (framePassed >= GameResource.AppleDisappear_FrameCount){
                    mRemovedApple.add(this);
                }
                break;
            case AppleStatus_AfterHitNewton:
                int symbol = CurNewton.Direction==Newton.Direction_Left ? -1 : 1;
                if (Math.abs(Degree) < AppleDegreeAfterHitNewton){
                    Degree += (timeDisFromLastFrame / (float)AppleDegreeChangeTimeAfterHitNewton) * 
                        AppleDegreeAfterHitNewton * symbol; 
                    Degree = Math.abs(Degree) > AppleDegreeAfterHitNewton ? 
                            AppleDegreeAfterHitNewton*symbol : Degree;
                }
                LeftPos += timeDisFromLastFrame * symbol *
                    (GameResource.GameStageWidth / (float)AppleLandscapeSpeedAfterHitNewton);
                float leftPosInc = Math.abs(LeftPos - mHitNewtonLeftPos);
                // y = 0.02x^2 - 2x + c  c = HitTopPos x = LeftPosInc y = TopPos
                TopPos = 0.02f * (float)Math.pow(leftPosInc, 2) - 2 * leftPosInc + mHitNewtonTopPos;
                if (TopPos >= GameResource.GameStageBottom - GameResource.AppleHeight){
                    TopPos = GameResource.GameStageBottom - GameResource.AppleHeight;
                    mStatus = AppleStatus_Ground;
                }
                break;
            }
        }
    }
    
    public class Bow{
        public static final int BowType_Normal = 0;
        public static final int BowType_Golden = 1;
        public static final int BowType_Weak = 2;
        
        private static final int GoldenBowCountIncrease = 5;
        private static final int WeakBowCountIncrease = 5;
        
        private static final int HandleStatus_Ready = 0;
        private static final int HandleStatus_Pull = 1;
        private static final int HandleStatus_Release = 2;
        // below time in millisecond
        private static final int Bow_Pull_Time = 200;
        private static final int Bow_Release_Time = 40;
        private static final int Bow_Cool_Time = 400;
        
        public int Type;
        public float TopPos;
        public int CurrentShowFrame;
        public int GoldenBowCount;
        public int WeakBowCount;
        
        private int mHandleStatus;
        private boolean mIsTracking;
        private long mPullTime;
        private long mReleaseTime;
        private boolean mReleaseSignal;
        
        public Bow(){
            Type = BowType_Normal;
            GoldenBowCount = 0;
            WeakBowCount = 0;
            TopPos = GameResource.BowHandleTopPosLimit - GameResource.BowHeight/2;
            CurrentShowFrame = 1;
            mHandleStatus = HandleStatus_Ready;
            mPullTime = 0;
            mReleaseTime = 0;
        }
        
        private float dealYPos(float yPos){
            if (yPos<GameResource.BowHandleTopPosLimit){
                yPos = GameResource.BowHandleTopPosLimit;
            }
            if (yPos>GameResource.BowHandleBottomPosLimit){
                yPos = GameResource.BowHandleBottomPosLimit;
            }
            return yPos;
        }
        
        public void pullBow(float xPos, float yPos){
            if (mHandleStatus==HandleStatus_Ready
                    && !mIsTracking && xPos <= GameResource.BowHandleRightPosLimit
                    && yPos >= GameResource.BowStatusBarHeight){
                mGameEventHandler.sendEmptyMessage(GameEvent_BowInit);
                mIsTracking = true;
                mReleaseSignal = false;
                mHandleStatus = HandleStatus_Pull;
                TopPos = dealYPos(yPos) - GameResource.BowHeight/2;
            }
        }
        
        public void moveBow(float xPos, float yPos){
            if (mIsTracking){
                if (xPos<=GameResource.BowHandleRightPosLimit){
                    TopPos = dealYPos(yPos) - GameResource.BowHeight/2;
                }else{
                    releaseBow(yPos);
                }
            }
        }
        
        public void releaseBow(float yPos){
            if (mIsTracking){
                mGameEventHandler.sendEmptyMessage(GameEvent_ReleaseArrow);
                mReleaseSignal = true;
                TopPos = dealYPos(yPos) - GameResource.BowHeight/2;
                mIsTracking = false;
            }
        }
        
        public void setBowType(int type){
            switch(type){
            case BowType_Golden:
                GoldenBowCount += GoldenBowCountIncrease;
                WeakBowCount = 0;
                break;
            case BowType_Weak:
                WeakBowCount += WeakBowCountIncrease;
                GoldenBowCount = 0;
                break;
            }
            if (GoldenBowCount > 0){
                Type = BowType_Golden;
            }else if (WeakBowCount > 0){
                Type = BowType_Weak;
            }
        }
        
        private void releaseArrow(){
            int arrowType = Arrow.ArrowType_Normal;
            switch(Type){
            case BowType_Golden:
                arrowType = Arrow.ArrowType_Golden;
                GoldenBowCount--;
                if (GoldenBowCount<=0){
                    GoldenBowCount = 0;
                    Type = BowType_Normal;
                }
                break;
            case BowType_Weak:
                arrowType = Arrow.ArrowType_Weak;
                WeakBowCount--;
                if (WeakBowCount<=0){
                    WeakBowCount = 0;
                    Type = BowType_Normal;
                }
                break;
            }
            long arrowIdentify = System.currentTimeMillis();
            if (arrowType==Arrow.ArrowType_Golden){
                for (int i=0;i<GameResource.GoldenBow_ArrowCount;i++){
                    Arrows.add(new Arrow(arrowType, 
                            TopPos + GameResource.GoldenArrowRelativeTopPos[i], 
                            GameResource.ArrowLeftPos, arrowIdentify));
                }
            }else{
                Arrows.add(new Arrow(arrowType, 
                        TopPos + GameResource.ArrowRelativeTopPos, GameResource.ArrowLeftPos, arrowIdentify));
            }
        }
        
        public void runBow(int timeDisFromLastFrame){
            if (mHandleStatus==HandleStatus_Pull){
                mPullTime += timeDisFromLastFrame;
                if (mPullTime>=Bow_Pull_Time){
                    CurrentShowFrame = GameResource.Bow_MaxPull_Frame;
                    if (mReleaseSignal){
                        mReleaseSignal = false;
                        mHandleStatus = HandleStatus_Release;
                        mPullTime = 0;
                    }
                }else{
                    CurrentShowFrame = 1 + (int)(mPullTime / 
                            (Bow_Pull_Time/GameResource.Bow_MaxPull_Frame));
                    CurrentShowFrame = CurrentShowFrame>GameResource.Bow_MaxPull_Frame?
                            GameResource.Bow_MaxPull_Frame:CurrentShowFrame;
                }
            }else if (mHandleStatus==HandleStatus_Release){
                mReleaseTime += timeDisFromLastFrame;
                if (mReleaseTime>=Bow_Release_Time){
                    if (CurrentShowFrame>0){
                        CurrentShowFrame = 0; // frame 0 is empty bow
                        releaseArrow();
                    }
                    if (mReleaseTime>=Bow_Release_Time + Bow_Cool_Time){
                        CurrentShowFrame = 1;
                        mHandleStatus = HandleStatus_Ready;
                        mReleaseTime = 0;
                    }
                }else{
                    CurrentShowFrame = GameResource.Bow_MaxPull_Frame - (int)(mReleaseTime / 
                            (Bow_Release_Time/GameResource.Bow_MaxPull_Frame));
                    CurrentShowFrame = CurrentShowFrame>0 ? CurrentShowFrame : 1;
                }
            }
        }
    }
    
    private static class GoldenArrowMissStat{
        public long Identify;
        public int CurMissCount;
        
        public void reset(long identify){
            Identify = identify;
            CurMissCount = GameResource.GoldenBow_ArrowCount;
        }
    }
    
    private static GoldenArrowMissStat mGoldenArrowMissStat = new GoldenArrowMissStat();
    
    public class Arrow{
        private static final int ArrowType_Normal = 0;
        private static final int ArrowType_Weak = 1;
        private static final int ArrowType_Golden = 2;
        
        private static final int ArrowStatus_Normal = 0;
        private static final int ArrowStatus_Inserted = 1;
        private static final int ArrowStatus_HaveHittedApple = 2;
        private static final int ArrowStatus_HaveShotNewton = 3;
        private static final int ArrowStatus_Over = 4; // do nothing, only used after shot Newton
        
        private static final int NormalArrowSpeed = 600; 
        
        private static final int WeakArrowInitSpeed = 600;
        private static final int WeakArrowSpeedDecelerate = 30;
        private static final int WeakArrowInitFallSpeed = 4000;
        private static final int WeakArrowFallAccelerate = 70;
        private static final float WeakArrowDegreeStep = 1.5f;
        private static final float WeakArrowMaxDegree = 90f;
        private static final int WeakArrowStatusChangeTime = 20; // run new speed per 10 milliseconds
        private static final float RotateDegreeAfterShotNewton = 90f;
        private static final int RotateTimeAfterShotNewton = 600;
        
        private static final int ArrowInsertedDisappearSpeed = 400;
        private static final int ArrowHittedDisappearSpeed = 100;
        
        private int mType;
        private int mStatus;
        private long mCreateTime;
        private long mDisappearTime;
        private long mArrowIdentify;
        private float mInitLeftPos;
        private float mArrowHeadLeftPos, mArrowHeadTopPos;
        private float mDegreeWhenHitNewton, mHeadLeftPosWhenHitNewton, mHeadTopPosWhenHitNewton;
        private float mHeadLeftPosIncAfterHitNewton, mHeadTopPosIncAfterHitNewton;
        private int mShotNewtonTimeCnt, mShotNewtonTime;
        
        public float TopPos, LeftPos;
        public float Transparence;
        public float Degree;
        public int CurrentShowFrame;
        public float WArrowGroundLeftPos, WArrowGroundTopPos;
        public boolean DrawWArrowGroundPart;
        
        public Arrow(int type, float topPos, float leftPos, long arrowIdentify){
            mType = type;
            mStatus = ArrowStatus_Normal;
            TopPos = topPos;
            mInitLeftPos = leftPos;
            LeftPos = leftPos;
            mArrowHeadLeftPos = leftPos + GameResource.ArrowWidth;
            mArrowHeadTopPos = topPos + GameResource.ArrowHeight / 2;
            Transparence = 1f;
            Degree = 0;
            CurrentShowFrame = 0;
            DrawWArrowGroundPart = false;
            mCreateTime = 0;
            mDisappearTime = 0;
            mArrowIdentify = arrowIdentify;
            mShotNewtonTimeCnt = 0;
            mShotNewtonTime = 0;
        }
        
        public boolean isShotApple(float appleLeftPos, float appleTopPos){
            if (mStatus==ArrowStatus_Normal){
                // note: For normal arrow, increase width of apple judgment should increase hit rate
                // because normal arrow move so fast. For weak arrow do not increase hit rate
                // because weak arrow move so slowly.
                float hitRateIncreaseValue = mType==ArrowType_Weak? 1 : 1.5f;
                if (mArrowHeadLeftPos > appleLeftPos 
                        && mArrowHeadLeftPos < appleLeftPos + GameResource.AppleWidth * hitRateIncreaseValue
                        && mArrowHeadTopPos > appleTopPos
                        && mArrowHeadTopPos < appleTopPos + GameResource.AppleHeight){
                    mStatus = ArrowStatus_HaveHittedApple;
                    return true;
                }
            }
            return false;
        }
        
        private void disappearArrow(int timeDisFromLastFrame, int disappearSpeed){
            mDisappearTime += timeDisFromLastFrame;
            int framePassed = (int)(mDisappearTime / 
                (disappearSpeed / GameResource.ArrowDisappear_FrameCount));
            Transparence = 1 - framePassed * GameResource.Disappear_Transparent_Disvalue;
            if (framePassed >= GameResource.ArrowDisappear_FrameCount){
                mRemovedArrow.add(this);
            }
        }
        
        private void moveArrow(int timeDisFromLastFrame){
            mCreateTime += timeDisFromLastFrame;
            if (mType==ArrowType_Weak){
                int changeCnt = (int)mCreateTime / WeakArrowStatusChangeTime;
                int curSpeed = WeakArrowInitSpeed + changeCnt * WeakArrowSpeedDecelerate;
                LeftPos += timeDisFromLastFrame * (GameResource.GameStageWidth / (float)curSpeed);
                float curFallSpeed = WeakArrowInitFallSpeed - changeCnt * WeakArrowFallAccelerate; 
                TopPos += timeDisFromLastFrame * (GameResource.GameStageHeight / (float)curFallSpeed);
                Degree = changeCnt * WeakArrowDegreeStep;
                Degree = Degree>=WeakArrowMaxDegree ? WeakArrowMaxDegree : Degree;
                mArrowHeadLeftPos = (float)(LeftPos + 
                        GameResource.ArrowWidth * Math.cos(Degree * DegreeToRadianConst));
                mArrowHeadTopPos = (float)(TopPos + GameResource.ArrowHeight / 2 + 
                        GameResource.ArrowWidth * Math.sin(Degree * DegreeToRadianConst));
            }else{
                LeftPos = mInitLeftPos + mCreateTime * 
                        (GameResource.GameStageWidth / (float)NormalArrowSpeed);
                mArrowHeadLeftPos = LeftPos + GameResource.ArrowWidth;
            }
        }
        
        private void missHitApple(){
            if (mType==ArrowType_Golden){
                if (mArrowIdentify!=mGoldenArrowMissStat.Identify){
                    mGoldenArrowMissStat.reset(mArrowIdentify);
                }
                mGoldenArrowMissStat.CurMissCount--;
                if (mGoldenArrowMissStat.CurMissCount<=0){
                    missHit();
                }
            }else{
                missHit();
            }
        }
        
        private void collisionDetect(){
            if (CurNewton!=null && CurNewton.isShotByArrow(mArrowHeadLeftPos, mArrowHeadTopPos,
                    mType==ArrowType_Weak ? 1 : 1.5f)){
                AchievementMgt.StatData.KillNewtonTimes++;
                mGameEventHandler.sendEmptyMessage(GameEvent_StatDataChange);
                mGameEventHandler.sendEmptyMessage(GameEvent_ArrowInNewton);
                mArrowHeadLeftPos = CurNewton.LeftPos + GameResource.NewtonArrowShotLeftPos;
                if (mType==ArrowType_Weak){
                    LeftPos = mArrowHeadLeftPos 
                        - (float)(GameResource.ArrowWidth * Math.cos(Degree*DegreeToRadianConst));
                }else{
                    LeftPos = mArrowHeadLeftPos - GameResource.ArrowWidth;
                }
                mStatus = ArrowStatus_HaveShotNewton;
                mDegreeWhenHitNewton = Degree;
                mHeadLeftPosWhenHitNewton = mArrowHeadLeftPos;
                mHeadTopPosWhenHitNewton = mArrowHeadTopPos;
                mHeadLeftPosIncAfterHitNewton = GameResource.GameStageBottom - mArrowHeadTopPos 
                    + CurNewton.LeftPos - mArrowHeadLeftPos; 
                float widthFromNewtonRight=CurNewton.LeftPos+GameResource.NormalNewtonWidth-mArrowHeadLeftPos;
                mHeadTopPosIncAfterHitNewton = GameResource.GameStageBottom
                    -widthFromNewtonRight - mArrowHeadTopPos;
                // 4 is Newton frame picture fix value
                mGameStatus = GameStatus_GameOvering;
                mGameEventHandler.sendEmptyMessage(GameEvent_GameOvering);
            }
        }
        
        public void run(int timeDisFromLastFrame){
            switch(mStatus){
            case ArrowStatus_Normal:
                moveArrow(timeDisFromLastFrame);
                if (mType==ArrowType_Weak && mArrowHeadTopPos >= GameResource.GameStageBottom){ // weak arrow
                    mGameEventHandler.sendEmptyMessage(GameEvent_ArrowEnd);
                    float topPosDis = mArrowHeadTopPos - GameResource.GameStageBottom;
                    TopPos -= topPosDis;
                    mArrowHeadTopPos -= topPosDis;
                    mStatus = ArrowStatus_Inserted;
                    WArrowGroundLeftPos = mArrowHeadLeftPos - GameResource.WArrowGroundPartWidth;
                    WArrowGroundTopPos = mArrowHeadTopPos - 
                        GameResource.WArrowGroundPartHeight + GameResource.ArrowHeight / 2;
                    DrawWArrowGroundPart = true;
                    missHitApple();
                }else if (mType!=ArrowType_Weak && LeftPos >= GameResource.ArrowInsertTreeLeftPos){ // normal arrow
                    mGameEventHandler.sendEmptyMessage(GameEvent_ArrowEnd);
                    LeftPos = GameResource.ArrowInsertTreeLeftPos;
                    // mArrowHeadLeftPos = LeftPos + GameResource.ArrowWidth; // not need calculate here, not used
                    CurrentShowFrame = GameResource.ArrowNormal_InsertTree_Frame;
                    mStatus = ArrowStatus_Inserted;
                    missHitApple();
                }else{
                    collisionDetect();
                }
                break;
            case ArrowStatus_Inserted:
                disappearArrow(timeDisFromLastFrame, ArrowInsertedDisappearSpeed);
                break;
            case ArrowStatus_HaveHittedApple: // if arrow hitted apple, it can still move on
                moveArrow(timeDisFromLastFrame);
                disappearArrow(timeDisFromLastFrame, ArrowHittedDisappearSpeed);
                break;
            case ArrowStatus_HaveShotNewton:
                mShotNewtonTimeCnt += timeDisFromLastFrame;
                mShotNewtonTime += timeDisFromLastFrame;
                if (mShotNewtonTimeCnt>=100){ // change each 100ms
                    float frameRate = mShotNewtonTime / (float)RotateTimeAfterShotNewton;
                    Degree = mDegreeWhenHitNewton + frameRate * RotateDegreeAfterShotNewton;
                    if (Degree >= mDegreeWhenHitNewton + RotateDegreeAfterShotNewton){
                        Degree = mDegreeWhenHitNewton + RotateDegreeAfterShotNewton;
                        LeftPos = mHeadLeftPosWhenHitNewton + mHeadLeftPosIncAfterHitNewton - 
                            (float)Math.cos(Degree*DegreeToRadianConst)*GameResource.ArrowWidth;
                        TopPos = mHeadTopPosWhenHitNewton + mHeadTopPosIncAfterHitNewton - 
                            (float)Math.sin(Degree*DegreeToRadianConst)*GameResource.ArrowWidth;
                        mStatus = ArrowStatus_Over;
                    }else{
                        LeftPos = mHeadLeftPosWhenHitNewton + frameRate * mHeadLeftPosIncAfterHitNewton - 
                            (float)Math.cos(Degree*DegreeToRadianConst)*GameResource.ArrowWidth;
                        TopPos = mHeadTopPosWhenHitNewton + frameRate * mHeadTopPosIncAfterHitNewton - 
                            (float)Math.sin(Degree*DegreeToRadianConst)*GameResource.ArrowWidth;
                    }
                    mShotNewtonTimeCnt = 0;
                }
                break;
            }
        }
    }
    
    public class TipText{
        public static final int TipType_ScaleTextTip = 0;
        public static final int TipType_ScoreTip = 1;
        public static final int TipType_NewtonSpeakText = 2;
        
        public static final int ScaleTextTipScaleTime_Normal = 200;
        public static final int ScaleTextTipScaleTime_Fast = 100;
        public static final int ScaleTextTipRemoveTime_Normal = 1000;
        public static final int ScaleTextTipRemoveTime_Fast = 500;
        
        private static final int MiddleNumber_ScoreLimit = 70;
        private static final int BigNumber_ScoreLimit = 140;
        private static final int ScoreTip_RemoveTime = 1000;
        private static final float ScoreTip_MoveupSpeed = 6000f;
        private static final int SpeakTextScaleTime = 300;
        private static final int SpeakTextRemoveTime = 3000;
        
        public int Type;
        public float LeftPos, TopPos;
        public int NumberValue;
        public int NumberSize;
        public boolean DrawNumberSymbol;
        public float ScaleRate;
        public float Transparence;
        public int ScaleTextFrame;
        public int SpeakTextFrame;
        
        private int mCreateTime;
        private float mCenterLeftPos, mCenterTopPos;
        private float mBottomTopPos; // used for Newton speak text
        private int mScaleTextTipScaleTime, mScaleTextTipRemoveTime;
        private int[] mScaleTextShakeTimeData;
        private float[] mScaleTextShakeScaleData;
        private int mScaleTextShakeEndTime;
        
        private void initTipText(int type, float centerLeftPos, float centerTopPos, 
                int numberValue, int numberSize){
            Type = type;
            LeftPos = centerLeftPos;
            TopPos = centerTopPos;
            mCreateTime = 0;
            mCenterLeftPos = centerLeftPos;
            mCenterTopPos = centerTopPos;
            ScaleRate = 1f;
            Transparence = 1f;
            NumberValue = numberValue;
            NumberSize = numberSize;
            DrawNumberSymbol = false;
        }
        
        // for scale text tip
        public TipText(int scaleTextFrame, float centerLeftPos, float centerTopPos, 
                int tipTextScaleTime, int tipTextRemoveTime, int numberValue){
            initTipText(TipType_ScaleTextTip, centerLeftPos, centerTopPos, 
                    numberValue, GameResource.NumberSize_SuperBig);
            mScaleTextTipScaleTime = tipTextScaleTime;
            mScaleTextTipRemoveTime = tipTextRemoveTime;
            ScaleTextFrame = scaleTextFrame;
            ScaleRate = 0f;
            mScaleTextShakeTimeData = new int[]{0,50,100,150,200,250,300};
            mScaleTextShakeScaleData = new float[]{-0.2f,0.2f,-0.1f,0.1f,-0.05f,0.05f};
            mScaleTextShakeEndTime = tipTextScaleTime + 350;
            // 350 means mScaleTextShakeTimeData[mScaleTextShakeTimeData.length-1] + 50
        }
        
        // for score tip
        public TipText(float centerLeftPos, float centerTopPos, int numberValue){
            int numberSize = GameResource.NumberSize_Normal;
            if (numberValue > MiddleNumber_ScoreLimit && numberValue <= BigNumber_ScoreLimit){
                numberSize = GameResource.NumberSize_Middle;
            }else if (numberValue > BigNumber_ScoreLimit){
                numberSize = GameResource.NumberSize_Big;
            }
            initTipText(TipType_ScoreTip, centerLeftPos, centerTopPos, numberValue, numberSize);
            DrawNumberSymbol = true;
            int scoreLength = String.valueOf(numberValue).length();
            int numberWidth = (scoreLength + 1) * GameResource.NumberWidth[numberSize] + // add 1 is for symbol
                scoreLength * GameResource.NumberSplitWidth;
            LeftPos = centerLeftPos - numberWidth/2f;
        }
        
        // for newton speak text
        public TipText(int speakFrame, float centerLeftPos, float bottomTopPos){
            Type = TipType_NewtonSpeakText;
            SpeakTextFrame = speakFrame;
            mBottomTopPos = bottomTopPos;
            mCenterLeftPos = centerLeftPos;
            ScaleRate = 0;
            Transparence = 1f;
            // LeftPos = centerLeftPos - (GameResource.NewtonSpeakTextWidth * ScaleRate)/2;
            LeftPos = centerLeftPos;
            // TopPos = bottomTopPos - (GameResource.NewtonSpeakTextHeight * ScaleRate);
            TopPos = bottomTopPos;
        }
        
        public void run(int timeDisFromLastFrame){
            mCreateTime += timeDisFromLastFrame;
            switch(Type){
            case TipType_ScaleTextTip:
                if (mCreateTime <= mScaleTextTipScaleTime){
                    ScaleRate = mCreateTime / (float)mScaleTextTipScaleTime;
                    LeftPos = mCenterLeftPos - (GameResource.ScaleTipTextWidth[ScaleTextFrame] * ScaleRate)/2;
                    TopPos = mCenterTopPos - (GameResource.ScaleTipTextHeight[ScaleTextFrame] * ScaleRate)/2;
                }else if (mCreateTime <= mScaleTextShakeEndTime){
                    int shakeTime = mCreateTime - mScaleTextTipScaleTime;
                    for (int i=1;i<mScaleTextShakeTimeData.length;i++){
                        if (shakeTime <= mScaleTextShakeTimeData[i]){
                            ScaleRate += ((float)(shakeTime-mScaleTextShakeTimeData[i-1])/
                                    (mScaleTextShakeTimeData[i] - mScaleTextShakeTimeData[i-1])) * 
                                    mScaleTextShakeScaleData[i-1];
                            break;
                        }else{
                            ScaleRate = 1f;
                        }
                    }
                    ScaleRate = ScaleRate>=1f ? 1f : ScaleRate;
                    LeftPos = mCenterLeftPos - (GameResource.ScaleTipTextWidth[ScaleTextFrame] * ScaleRate)/2;
                    TopPos = mCenterTopPos - (GameResource.ScaleTipTextHeight[ScaleTextFrame] * ScaleRate)/2;
                }
                if (mCreateTime>=mScaleTextTipRemoveTime){
                    mRemovedTips.add(this);
                }
                break;
            case TipType_ScoreTip:
                Transparence = 1 - mCreateTime / (float)ScoreTip_RemoveTime;
                TopPos = mCenterTopPos - mCreateTime * (GameResource.GameStageHeight / ScoreTip_MoveupSpeed);
                if (mCreateTime>=ScoreTip_RemoveTime){
                    mRemovedTips.add(this);
                }
                break;
            case TipType_NewtonSpeakText:
                if (ScaleRate < 1){
                    ScaleRate = mCreateTime / (float)SpeakTextScaleTime;
                    ScaleRate = ScaleRate>=1f ? 1f : ScaleRate;
                    LeftPos = mCenterLeftPos - (GameResource.NewtonSpeakTextWidth * ScaleRate)/2;
                    TopPos = mBottomTopPos - GameResource.NewtonSpeakTextHeight * ScaleRate;
                }
                if (mCreateTime>=SpeakTextRemoveTime){
                    mRemovedTips.add(this);
                }
                break;
            }
        }
    }
    
    public class Newton{
        public static final int Direction_Right = 0;
        public static final int Direction_Left = 1;
        
        public static final int SpeakType_ContinueHit = 0;
        public static final int SpeakType_ContinueMiss = 1;
        
        private static final int NewtonStatus_Normal = 0;
        private static final int NewtonStatus_Turning = 1;
        private static final int NewtonStatus_Thinking = 2;
        private static final int NewtonStatus_Speaking = 3;
        private static final int NewtonStatus_Falling = 4;
        private static final int NewtonStatus_Over = 5;
        
        private static final int FallReason_HitByApple = 0;
        private static final int FallReason_ShotByArrow = 1;
        
        private static final int WalkFrame_Time = 80;
        private static final float Walk_Speed = 15000f;
        private static final int Turning_Time = 50;
        private static final int Thinking_CoolTime = 10000;
        private static final int ThinkingFrame_Time = 150;
        private static final int SpeakingFrame_Time = 200;
        private static final int FallingFrame_Time = 100;
        
        public int Direction;
        public float LeftPos, TopPos;
        public int CurFrame;
        
        private int mStatus;
        private int mWalkFrameTimeCnt;
        private int mWalkFrameCnt;
        private int mTurningTime;
        private int mNotThinkingTime, mThinkingTimeCnt;
        private int mSpeakingTimeCnt;
        private int mFallingTimeCnt;
        private int mFallReason;
        private float mFallStartLeftPos, mFallStartTopPos;
        
        public Newton(){
            Direction = Direction_Right;
            LeftPos = GameResource.NewtonLeftLeftPosLimit;
            TopPos = GameResource.NewtonTopPos;
            CurFrame = GameResource.NewtonStand_Frame;
            mStatus = NewtonStatus_Normal;
            mWalkFrameTimeCnt = 0;
            mWalkFrameCnt = 0;
            mTurningTime = 0;
            mNotThinkingTime = 0;
            mThinkingTimeCnt = 0;
            mSpeakingTimeCnt = 0;
            mFallingTimeCnt = 0;
        }
        
        public void Speaking(int speakType){
            if ((mStatus==NewtonStatus_Normal || mStatus==NewtonStatus_Thinking)
                    && mRandomGenerator.nextInt(2)>0){
                mStatus = NewtonStatus_Speaking;
                CurFrame = GameResource.NewtonSpeakingStart_Frame;
                int frame = speakType==SpeakType_ContinueHit ? GameResource.SpeakText_ContinueHit_StartFrame
                        : GameResource.SpeakText_ContinueMiss_StartFrame;
                frame += mRandomGenerator.nextInt(GameResource.SpeakText_FrameCntOfEachStatus);
                Tips.add(new TipText(frame, LeftPos + GameResource.NormalNewtonWidth/2f, TopPos));
                mGameEventHandler.sendMessage(
                        mGameEventHandler.obtainMessage(GameEvent_NewtonSpeak, frame, 0));
            }
        }
        
        public void fall(int fallReason){
            mFallStartLeftPos = LeftPos;
            mFallStartTopPos = TopPos;
            mStatus = NewtonStatus_Falling;
            CurFrame = GameResource.NewtonStand_Frame;
            mFallReason = fallReason;
        }
        
        public boolean isHittedByApple(float appleLeftPos, float appleTopPos){
            if (mStatus!=NewtonStatus_Falling && mStatus!=NewtonStatus_Over){
                float checkLeftPos = LeftPos + GameResource.NewtonHitCheckLeftPos;
                float checkRightPos = LeftPos + GameResource.NewtonHitCheckRightPos;
                float checkBottomPos = TopPos + GameResource.NewtonAppleHitCheckBottomPos;
                float appleRightPos = appleLeftPos + GameResource.AppleWidth;
                float appleBottomPos = appleTopPos + GameResource.AppleHeight;
                if (appleLeftPos <= checkRightPos && appleRightPos >= checkLeftPos
                        && appleBottomPos >= TopPos && appleTopPos <= checkBottomPos){
                    fall(FallReason_HitByApple);
                    return true;
                }
            }
            return false;
        }
        
        public boolean isShotByArrow(float arrowHeadLeftPos, float arrowHeadTopPos, 
                float hateRateIncreaseValue){
            // note: when Newton is falling, it can still be shot, it is useful when golden arrow shot
            if (mStatus!=NewtonStatus_Over && LeftPos >= GameResource.BowStatusBarWidth){
                float checkLeftPos = LeftPos + GameResource.NewtonHitCheckLeftPos;
                float checkRightPos = LeftPos + GameResource.NewtonHitCheckRightPos * hateRateIncreaseValue;
                float checkBottomPos = TopPos + GameResource.NewtonArrowShotCheckBottomPos;
                if (arrowHeadLeftPos >= checkLeftPos && arrowHeadLeftPos <= checkRightPos
                        && arrowHeadTopPos > TopPos && arrowHeadTopPos <= checkBottomPos){ 
                    fall(FallReason_ShotByArrow);
                    return true;
                }
            }
            return false;
        }
        
        private void changeWalkFrame(){
           mWalkFrameCnt++;
           if (mWalkFrameCnt <= GameResource.NewtonWalk_FrameCount){
               if (mWalkFrameCnt==GameResource.NewtonHalfWalk_FrameCount){
                   CurFrame = GameResource.NewtonStand_Frame;
               }else{
                   CurFrame = mWalkFrameCnt < GameResource.NewtonHalfWalk_FrameCount ? 
                           mWalkFrameCnt : mWalkFrameCnt - 1;
               }
           }else{
               mWalkFrameCnt = 0;
               CurFrame = GameResource.NewtonStand_Frame;
           }
        }
        
        private void randomThinking(int timeDisFromLastFrame){
            mNotThinkingTime += timeDisFromLastFrame;
            if (mNotThinkingTime >= Thinking_CoolTime){
                mNotThinkingTime = 0;
                if (mRandomGenerator.nextInt(2)>0){
                    mStatus = NewtonStatus_Thinking;
                    CurFrame = GameResource.NewtonThinkingStart_Frame;
                }
            }
        }
        
        private void restoreWalkStatus(){
            mStatus = NewtonStatus_Normal;
            CurFrame = GameResource.NewtonStand_Frame;
            mWalkFrameTimeCnt = 0;
        }
        
        private void setFallNewtonPos(int fallFrame){
            int frameWidth = GameResource.Newton[fallFrame].getWidth();
            int frameHeight = GameResource.Newton[fallFrame].getHeight();
            if (mFallReason==FallReason_HitByApple && Direction==Direction_Left){
                LeftPos = mFallStartLeftPos + GameResource.NormalNewtonWidth - frameWidth;
            }
            TopPos = mFallStartTopPos + GameResource.NormalNewtonHeight - frameHeight;
        }
        
        public void run(int timeDisFromLastFrame){
            switch(mStatus){
            case NewtonStatus_Normal:
                mWalkFrameTimeCnt += timeDisFromLastFrame;
                if (mWalkFrameTimeCnt>=WalkFrame_Time){
                    changeWalkFrame();
                    mWalkFrameTimeCnt = 0;
                }
                if (Direction==Direction_Right){
                    LeftPos += timeDisFromLastFrame * (GameResource.GameStageWidth / Walk_Speed);
                    if (LeftPos>=GameResource.NewtonRightLeftPosLimit){
                        LeftPos = GameResource.NewtonRightLeftPosLimit;
                        CurFrame = GameResource.NewtonStand_Frame;
                        mStatus = NewtonStatus_Turning;
                    }
                }else{
                    LeftPos -= timeDisFromLastFrame * (GameResource.GameStageWidth / Walk_Speed);
                    if (LeftPos<=GameResource.NewtonLeftLeftPosLimit){
                        LeftPos = GameResource.NewtonLeftLeftPosLimit;
                        CurFrame = GameResource.NewtonStand_Frame;
                        mStatus = NewtonStatus_Turning;
                    }
                }
                randomThinking(timeDisFromLastFrame);
                break;
            case NewtonStatus_Turning:
                mTurningTime += timeDisFromLastFrame;
                if (mTurningTime >= Turning_Time){
                    Direction = Direction==Direction_Left ? Direction_Right : Direction_Left;
                    mTurningTime = 0;
                    restoreWalkStatus();
                }
                break;
            case NewtonStatus_Thinking:
                mThinkingTimeCnt += timeDisFromLastFrame;
                if (mThinkingTimeCnt>=ThinkingFrame_Time){
                    mThinkingTimeCnt = 0;
                    CurFrame++;
                }
                if (CurFrame>GameResource.NewtonThinkingEnd_Frame){
                    restoreWalkStatus();
                }
                break;
            case NewtonStatus_Speaking:
                mSpeakingTimeCnt += timeDisFromLastFrame;
                if (mSpeakingTimeCnt>=SpeakingFrame_Time){
                    mSpeakingTimeCnt = 0;
                    CurFrame++;
                }
                if (CurFrame > GameResource.NewtonSpeakingEnd_Frame){
                    restoreWalkStatus();
                }
                break;
            case NewtonStatus_Falling:
                mFallingTimeCnt += timeDisFromLastFrame;
                int fallStartFrame = GameResource.NewtonFallStart_Frame;
                int fallEndFrame = GameResource.NewtonFallEnd_Frame;
                if (mFallReason==FallReason_ShotByArrow && Direction==Direction_Left){
                    fallStartFrame = GameResource.NewtonShotByArrowInFaceStart_Frame;
                    fallEndFrame = GameResource.NewtonShotByArrowInFaceEnd_Frame;
                }
                if (mFallingTimeCnt>=FallingFrame_Time){
                    mFallingTimeCnt = 0;
                    if (CurFrame==GameResource.NewtonStand_Frame){
                        CurFrame = fallStartFrame;
                    }else{
                        CurFrame++;
                    }
                    setFallNewtonPos(CurFrame);
                    if (CurFrame >= fallEndFrame){
                        mStatus = NewtonStatus_Over;
                        CurFrame = fallEndFrame;
                        float endTipLeftPos = Direction==Direction_Left && mFallReason==FallReason_HitByApple ? 
                                LeftPos + GameResource.FallNewtonWidth - GameResource.FallNewtonBodyWidth
                                : LeftPos + GameResource.FallNewtonBodyWidth;
                        int tipFrame = mFallReason==FallReason_HitByApple ? 
                                GameResource.SpeakText_HitByApple_StartFrame 
                                : GameResource.SpeakText_ShotByArrow_StartFrame;
                        tipFrame += mRandomGenerator.nextInt(GameResource.SpeakText_FrameCntOfEachStatus);
                        Tips.add(new TipText(tipFrame, endTipLeftPos, TopPos));
                        mGameEventHandler.sendMessage(
                                mGameEventHandler.obtainMessage(GameEvent_NewtonSpeak, tipFrame, 0));
                    }
                }
                break;
                // NewtonStatus_Over do nothing
            }
        }
    }
}