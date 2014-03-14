package ml.game.android.SaveNewton;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public final class GameResource{
    public static final float Disappear_Transparent_Disvalue = 0.2f;
    public static final int AppleCreatePos_Count = 6;
    public static final int AppleGrownup_FrameCount = 6;
    public static final int AppleDisappear_FrameCount = 4;
    public static final int AppleExplod_FrameCount = 4;
    public static final int AppleNormal_Frame = 1;
    public static final int AppleExplod_BeginFrame = 2;
    public static final int AppleExplod_EndFrame = 4;
    public static final int Bow_MaxPull_Frame = 4;
    public static final int ArrowNormal_InsertTree_Frame = 1;
    public static final int ArrowDisappear_FrameCount = 4;
    public static final int NormalBowIcon_FrameIndex = 0;
    public static final int GoldenBowIcon_FrameIndex = 1;
    public static final int WeakBowIcon_FrameIndex = 2;
    public static final int GoldenBow_ArrowCount = 3;
    public static final int ScaleTipText_LowGravityApple_Frame = 0;
    public static final int ScaleTipText_CountDown_Frame = 1;
    public static final int ScaleTipText_HightScore_Frame = 2;
    public static final int NumberSize_Normal = 0;
    public static final int NumberSize_Middle = 1;
    public static final int NumberSize_Big = 2;
    public static final int NumberSize_SuperBig = 3;
    public static final int NewtonStand_Frame = 0;
    public static final int NewtonWalk_StartFrame = 1;
    public static final int NewtonWalk_FrameCount = 9;
    public static final int NewtonHalfWalk_FrameCount = 5;
    public static final int NewtonThinkingStart_Frame = 9;
    public static final int NewtonThinkingEnd_Frame = 15;
    public static final int NewtonSpeakingStart_Frame = 16;
    public static final int NewtonSpeakingEnd_Frame = 22;
    public static final int NewtonFallStart_Frame = 23;
    public static final int NewtonFallEnd_Frame = 28;
    public static final int NewtonShotByArrowInFaceStart_Frame = 29;
    public static final int NewtonShotByArrowInFaceEnd_Frame = 34;
    public static final int SpeakText_FrameCntOfEachStatus = 3;
    public static final int SpeakText_ContinueHit_StartFrame = 0;
    public static final int SpeakText_ContinueMiss_StartFrame = 3;
    public static final int SpeakText_ShotByArrow_StartFrame = 6;
    public static final int SpeakText_HitByApple_StartFrame = 9;
    
    public static final int NumberSplitWidth = 2; // in pixel
    public static final int ScoreRightPosFromRightBorder = 4;
    
    private static final int GameStage_MaxCutHeight = 50;
    private static final int GameStage_MaxCutWidth = 180;
    private static final float AppleTopPos_Rate = 0.12f; // height from top border, in percent of total height
    private static final float ApplePos0_Rate = 0.66f; // width from right border, in percent of total width
    private static final float ApplePos1_Rate = 0.56f;
    private static final float ApplePos2_Rate = 0.46f;
    private static final float ApplePos3_Rate = 0.36f;
    private static final float ApplePos4_Rate = 0.26f;
    private static final float ApplePos5_Rate = 0.16f;
    private static final float GameStageSkyBackBottomPos_Rate = 0.4f;
    // define the bottom border, in percent of total height from physic bottom
    private static final float GameStage_LandBottom_Rate = 0.06f; 
    private static final float BowBar_Width_Rate = 0.15f; // width of bow bar, in percent of total width
    private static final float Bow_LeftPos_Rate = 0.03f; // in percent of total width
    private static final float Bow_BottomLimit_Rate = 0.04f; // in percent of total height
    private static final float Arrow_InsertTree_WidthRate = 0.05f; // in percent of total width, from right border
    private static final float GoldenBow_ArrowPos_Increase = 0.25f; // in percent of total bow height
    private static final float Newton_HitCheck_LeftPosRate = 0.25f;
    private static final float Newton_HitCheck_RightPosRate = 0.75f;
    private static final float Newton_AppleHitCheck_BottomPosRate = 0.2f;
    private static final float Newton_ArrowShotCheck_BottomPosRate = 0.9f;
    private static final float FallNewton_BodyWidthRate = 0.78f;
    private static final float Newton_ArrowShot_LeftPosRate = 0.7f;
    // Tool Button Design Size and Position
    private static final int ToolButtonOriWidthHeight = 50;
    private static final int ToolButtonTextSize = 20;
    // Store Item Icon
    private static final int StoreItemIconOriWidth = 120;
    private static final int StoreItemOriTopMargin = 30;
    private static final int StoreItemIconOriLeftMargin = 20;
    private static final int StoreItemIconOriPadding = 30;
    private static final int StoreItemLevelStarOriSize = 20;
    // hight score view ori top margin
    private static final int HightScoreViewOriTopMargin = 80;
    
    public static int GameStageWidth; // in Pixel
    public static int GameStageHeight; // in Pixel
    public static float GameStageSkyBackTopPos;
    public static int GameStageSkyBackDrawCount;
    public static int GameStageSkyBackWidth;
    public static int GameStageFrontLeftPos, GameStageFrontTopPos;
    public static float GameStageBottom;
    public static float GameStageCloudTopPos1, GameStageCloudTopPos2, GameStageCloudInitLeftPos;
    public static int GameStageCloudWidth, GameStageCloudDis;
    // Tool Button calculate Size and Position
    public static float ToolButtonWidth, ToolButtonTopPos, ToolButtonLeftMargin, ToolButtonHalfWidth;
    public static float ToolButtonLastLeftPos, ToolButtonFirstLeftPos;
    // Gold Coin
    public static int GoldCoinLeftPos, GoldCoinNumberLeftPos;
    // Store Item Background Size
    public static int StoreItemBgSize, StoreItemIconLeftMargin, StoreItemTopMargin, StoreItemIconPadding, StoreItemLevelStarSize;
    // High Score View top margin
    public static int HightScoreViewTopMargin;
    
    public static Bitmap GameStage_Back0, GameStage_Back1, GameStage_Front, GameStage_Cloud;
    public static Bitmap[] NormalApple, SpecialApple, GoldenApple, GreenApple;
    public static Bitmap BowStatusBar, BowHandleBar;
    public static Bitmap[] NormalBow, GoldenBow;
    public static Bitmap[] BowIcon;
    public static Bitmap[] Arrow;
    public static Bitmap WeakArrowGroundPart;
    public static Bitmap[] Numbers, MNumbers, LNumbers, SLNumbers, NumberSymbols;
    public static Bitmap GoldCoin;
    public static Bitmap[] ScaleTipTexts;
    public static Bitmap PauseGameTip;
    public static Bitmap[] Newton;
    public static Bitmap[] NewtonSpeakText;
    public static Bitmap ToolButtonBG, SelectedToolButtonBG;
    public static Bitmap WeaponButtonStrongBow, WeaponButtonWeakBow;
    public static Paint WeaponCountTextPaint;
    public static Bitmap TipIconWeapon_StrongBow, TipIconWeapon_WeakBow;
    
    public static int AppleInitTopPos;
    public static int[] AppleLeftPos;
    public static int AppleWidth, AppleHeight;
    public static int BowStatusBarWidth, BowStatusBarHeight;
    public static int BowIconTopPos, BowIconLeftPos;
    public static int BowIconCountRightPos, BowIconCountTopPos;
    public static int BowLeftPos; // top position will be calculate in program
    public static int BowWidth, BowHeight;
    public static int BowHandleTopPosLimit, BowHandleRightPosLimit, BowHandleBottomPosLimit;
    public static int ArrowInsertTreeLeftPos;
    public static int ArrowWidth, ArrowHeight;
    public static int WArrowGroundPartWidth, WArrowGroundPartHeight;
    public static int ArrowLeftPos, ArrowRelativeTopPos;
    public static int[] GoldenArrowRelativeTopPos;
    public static int[] NumberWidth;
    public static int StatInfoTopPos;
    public static int[] ScaleTipTextWidth, ScaleTipTextHeight;
    public static float GameCenterLeftPos, GameCenterTopPos;
    public static float PauseGameTipLeftPos, PauseGameTipTopPos;
    public static int NormalNewtonWidth, NormalNewtonHeight, FallNewtonWidth, FallNewtonBodyWidth;
    public static float NewtonLeftLeftPosLimit, NewtonRightLeftPosLimit, NewtonTopPos;
    public static int NewtonSpeakTextWidth, NewtonSpeakTextHeight;
    public static float NewtonHitCheckLeftPos, NewtonHitCheckRightPos;
    public static float NewtonAppleHitCheckBottomPos, NewtonArrowShotCheckBottomPos; 
    public static float NewtonArrowShotLeftPos;
    
    // for menu and title
    private static final float GameMenuOriWidth = 500f;
    private static final float GameMenuOriHeight = 320f;
    private static final int OriMenuLoadingTipWidth = 117;
    private static final int OriMenuLoadingTipHeight = 20;
    private static float sGameMenuWScaleRate, sGameMenuHScaleRate;
    public static int MenuLoadingTipWidth, MenuLoadingTipHeight;
    public static int SmallBtnSize, NormalBtnSize;
    public static int WBtn1Width, WBtn1Height, WBtn2Width, WBtn2Height;
    public static int WBtn3Width, WBtn3Height, WBtn4Width, WBtn4Height;
    public static int WBtnLeftMargin;
    public static int BuyButtonWidth, BuyButtonHeight;
    
    private static BitmapFactory.Options sNoScaleOp;
    
    public static void preInit(int oriWidth, int oriHeight){
        GameStageWidth = oriWidth < oriHeight ? oriHeight : oriWidth;
        GameStageHeight = oriWidth < oriHeight ? oriWidth : oriHeight;
        sGameMenuWScaleRate = GameStageWidth / GameMenuOriWidth;
        sGameMenuHScaleRate = GameStageHeight / GameMenuOriHeight;
        MenuLoadingTipWidth = (int)(sGameMenuWScaleRate * OriMenuLoadingTipWidth);
        MenuLoadingTipHeight = (int)(sGameMenuWScaleRate * OriMenuLoadingTipHeight);
    }
    
    public static void init(Resources resource){
        initSizeForMenu(resource);
        initBitmapResource(resource);
        initGameConstNumbers();
    }
    
    private static void initSizeForMenu(Resources resource){
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resource, R.drawable.btnclose, op);
        SmallBtnSize = (int)(sGameMenuHScaleRate * op.outWidth * 1.2f);
        BitmapFactory.decodeResource(resource, R.drawable.btninfo, op);
        NormalBtnSize = (int)(sGameMenuHScaleRate * op.outWidth * 1.2f);
        BitmapFactory.decodeResource(resource, R.drawable.wbtnplay, op);
        WBtn1Width = (int)(sGameMenuHScaleRate * op.outWidth);
        WBtn1Height = (int)(sGameMenuHScaleRate * op.outHeight);
        BitmapFactory.decodeResource(resource, R.drawable.wbtnstore, op);
        WBtn2Width = (int)(sGameMenuHScaleRate * op.outWidth);
        WBtn2Height = (int)(sGameMenuHScaleRate * op.outHeight);
        BitmapFactory.decodeResource(resource, R.drawable.wbtnscore, op);
        WBtn3Width = (int)(sGameMenuHScaleRate * op.outWidth);
        WBtn3Height = (int)(sGameMenuHScaleRate * op.outHeight);
        BitmapFactory.decodeResource(resource, R.drawable.wbtnonline, op);
        WBtn4Width = (int)(sGameMenuHScaleRate * op.outWidth);
        WBtn4Height = (int)(sGameMenuHScaleRate * op.outHeight);
        WBtnLeftMargin = GameStageWidth / 2;
        // buy button
        BitmapFactory.decodeResource(resource, R.drawable.buybutton, op);
        BuyButtonWidth = (int)(sGameMenuHScaleRate * op.outWidth);
        BuyButtonHeight = (int)(sGameMenuHScaleRate * op.outHeight);
    }
    
    private static Bitmap scaleGameStage(Bitmap oriGameStage){
        if (oriGameStage!=null){
            int oriWidth = oriGameStage.getWidth();
            int oriHeight = oriGameStage.getHeight();
            float oriWHRatio = (float)oriWidth / (float)oriHeight;
            float newWHRatio = (float)GameStageWidth / (float)GameStageHeight;
            int cuttedWidth = 0;
            int cuttedHeight = 0;
            if (oriWHRatio < newWHRatio){ // will cut height of game stage
                cuttedWidth = oriWidth;
                int tmpHeight = (int)(cuttedWidth/newWHRatio);
                cuttedHeight = (oriHeight - tmpHeight) < GameStage_MaxCutHeight ? 
                        tmpHeight : oriHeight - GameStage_MaxCutHeight;
            }else{ // will cut width of game stage
                cuttedHeight = oriHeight;
                int tmpWidth = (int)(cuttedHeight * newWHRatio);
                cuttedWidth = (oriWidth - tmpWidth) < GameStage_MaxCutWidth ? 
                        tmpWidth : oriWidth - GameStage_MaxCutWidth;
            }
            Matrix scaleMatrix = new Matrix();
            scaleMatrix.postScale((float)GameStageWidth/(float)cuttedWidth, 
                    (float)GameStageHeight/(float)cuttedHeight);
            Bitmap newImg = Bitmap.createBitmap(oriGameStage, 
                    oriWidth-cuttedWidth, oriHeight - cuttedHeight, 
                    cuttedWidth, cuttedHeight, scaleMatrix, true);
            return newImg;
        }
        return null;
    }
    
    private static void initBitmapResource(Resources res){
        // init game stage
        sNoScaleOp = new BitmapFactory.Options();
        sNoScaleOp.inScaled = false;
        Bitmap oriGameStage_Back1 = BitmapFactory.decodeResource(res, R.drawable.gamestage_back1, sNoScaleOp);
        GameStage_Back1 = scaleGameStage(oriGameStage_Back1);
        float hScaleRate = (float)GameStage_Back1.getHeight() / oriGameStage_Back1.getHeight();
        float wScaleRate = (float)GameStage_Back1.getWidth() / oriGameStage_Back1.getWidth();
        Bitmap oriGameStage_Front = BitmapFactory.decodeResource(res, R.drawable.gamestage_front, sNoScaleOp);
        GameStage_Front = Bitmap.createScaledBitmap(oriGameStage_Front, 
                (int)(oriGameStage_Front.getWidth() * hScaleRate), 
                (int)(oriGameStage_Front.getHeight()* hScaleRate), true);
        Bitmap oriGameStage_Back0 = BitmapFactory.decodeResource(res, R.drawable.gamestage_back0, sNoScaleOp);
        GameStageSkyBackWidth = (int)(oriGameStage_Back0.getWidth() * hScaleRate);
        int gameStageSkyBackHeight = (int)(oriGameStage_Back0.getHeight() * hScaleRate);
        GameStage_Back0 = Bitmap.createScaledBitmap(oriGameStage_Back0, 
                GameStageSkyBackWidth, gameStageSkyBackHeight, true);
        GameStageSkyBackTopPos = GameStageHeight - 
            (GameStageHeight * GameStageSkyBackBottomPos_Rate + gameStageSkyBackHeight);
        GameStageSkyBackDrawCount = GameStageWidth % GameStageSkyBackWidth==0 ? 
                GameStageWidth/GameStageSkyBackWidth : GameStageWidth/GameStageSkyBackWidth + 1;
        Bitmap oriGameStageCloud = BitmapFactory.decodeResource(res, R.drawable.gamestage_cloud, sNoScaleOp);
        GameStageCloudWidth = (int)(oriGameStageCloud.getWidth()*hScaleRate);
        GameStage_Cloud = Bitmap.createScaledBitmap(oriGameStageCloud, 
                GameStageCloudWidth, (int)(oriGameStageCloud.getHeight()*hScaleRate), true);
        GameStageCloudTopPos1 = GameStageSkyBackTopPos + gameStageSkyBackHeight/3f;
        GameStageCloudTopPos2 = GameStageSkyBackTopPos + gameStageSkyBackHeight/4f;
        GameStageCloudInitLeftPos = GameStageWidth/2;
        GameStageCloudDis = GameStageWidth/2 + GameStageCloudWidth/2;
        // init game resource
        initAppleResource(res, hScaleRate);
        initBowRelated(res, hScaleRate, wScaleRate);
        initArrow(res, wScaleRate);
        initText(res, hScaleRate);
        initNewton(res, hScaleRate);
        initNewtonSpeakText(res, hScaleRate);
        initToolButton(res, wScaleRate);
        sNoScaleOp = null;
        // init size number for normal activity
        StoreItemBgSize = (int)(StoreItemIconOriWidth * wScaleRate);
        StoreItemIconLeftMargin = (int)(StoreItemIconOriLeftMargin * wScaleRate);
        StoreItemTopMargin = (int)(StoreItemOriTopMargin * hScaleRate);
        StoreItemIconPadding = (int)(StoreItemIconOriPadding * wScaleRate);
        StoreItemLevelStarSize = (int)(StoreItemLevelStarOriSize * wScaleRate);
        // high score view top margin
        HightScoreViewTopMargin = (int)(HightScoreViewOriTopMargin * hScaleRate);
    }
    
    private static void initAppleResource(Resources res, float scaleRate){
        // Normal Apple
        NormalApple = new Bitmap[5];
        NormalApple[0] = BitmapFactory.decodeResource(res, R.drawable.napple0, sNoScaleOp);
        NormalApple[0] = Bitmap.createScaledBitmap(NormalApple[0], 
                (int)(NormalApple[0].getWidth()*scaleRate), 
                (int)(NormalApple[0].getHeight()*scaleRate), true);
        NormalApple[1] = BitmapFactory.decodeResource(res, R.drawable.napple1, sNoScaleOp);
        NormalApple[1] = Bitmap.createScaledBitmap(NormalApple[1], 
                (int)(NormalApple[1].getWidth()*scaleRate), 
                (int)(NormalApple[1].getHeight()*scaleRate), true);
        NormalApple[2] = BitmapFactory.decodeResource(res, R.drawable.napple2, sNoScaleOp);
        NormalApple[2] = Bitmap.createScaledBitmap(NormalApple[2], 
                (int)(NormalApple[2].getWidth()*scaleRate), 
                (int)(NormalApple[2].getHeight()*scaleRate), true);
        NormalApple[3] = BitmapFactory.decodeResource(res, R.drawable.napple3, sNoScaleOp);
        NormalApple[3] = Bitmap.createScaledBitmap(NormalApple[3], 
                (int)(NormalApple[3].getWidth()*scaleRate), 
                (int)(NormalApple[3].getHeight()*scaleRate), true);
        NormalApple[4] = BitmapFactory.decodeResource(res, R.drawable.napple4, sNoScaleOp);
        NormalApple[4] = Bitmap.createScaledBitmap(NormalApple[4], 
                (int)(NormalApple[4].getWidth()*scaleRate), 
                (int)(NormalApple[4].getHeight()*scaleRate), true);
        // Special Apple
        SpecialApple = new Bitmap[5];
        SpecialApple[0] = BitmapFactory.decodeResource(res, R.drawable.sapple0, sNoScaleOp);
        SpecialApple[0] = Bitmap.createScaledBitmap(SpecialApple[0], 
                (int)(SpecialApple[0].getWidth()*scaleRate), 
                (int)(SpecialApple[0].getHeight()*scaleRate), true);
        SpecialApple[1] = BitmapFactory.decodeResource(res, R.drawable.sapple1, sNoScaleOp);
        SpecialApple[1] = Bitmap.createScaledBitmap(SpecialApple[1], 
                (int)(SpecialApple[1].getWidth()*scaleRate), 
                (int)(SpecialApple[1].getHeight()*scaleRate), true);
        SpecialApple[2] = NormalApple[2];
        SpecialApple[3] = NormalApple[3];
        SpecialApple[4] = NormalApple[4];
        // Golden Apple
        GoldenApple = new Bitmap[5];
        GoldenApple[0] = BitmapFactory.decodeResource(res, R.drawable.gapple0, sNoScaleOp);
        GoldenApple[0] = Bitmap.createScaledBitmap(GoldenApple[0], 
                (int)(GoldenApple[0].getWidth()*scaleRate), 
                (int)(GoldenApple[0].getHeight()*scaleRate), true);
        GoldenApple[1] = BitmapFactory.decodeResource(res, R.drawable.gapple1, sNoScaleOp);
        GoldenApple[1] = Bitmap.createScaledBitmap(GoldenApple[1], 
                (int)(GoldenApple[1].getWidth()*scaleRate), 
                (int)(GoldenApple[1].getHeight()*scaleRate), true);
        GoldenApple[2] = BitmapFactory.decodeResource(res, R.drawable.gapple2, sNoScaleOp);
        GoldenApple[2] = Bitmap.createScaledBitmap(GoldenApple[2], 
                (int)(GoldenApple[2].getWidth()*scaleRate), 
                (int)(GoldenApple[2].getHeight()*scaleRate), true);
        GoldenApple[3] = BitmapFactory.decodeResource(res, R.drawable.gapple3, sNoScaleOp);
        GoldenApple[3] = Bitmap.createScaledBitmap(GoldenApple[3], 
                (int)(GoldenApple[3].getWidth()*scaleRate), 
                (int)(GoldenApple[3].getHeight()*scaleRate), true);
        GoldenApple[4] = BitmapFactory.decodeResource(res, R.drawable.gapple4, sNoScaleOp);
        GoldenApple[4] = Bitmap.createScaledBitmap(GoldenApple[4], 
                (int)(GoldenApple[4].getWidth()*scaleRate), 
                (int)(GoldenApple[4].getHeight()*scaleRate), true);
        // Green Apple
        GreenApple = new Bitmap[5];
        GreenApple[0] = BitmapFactory.decodeResource(res, R.drawable.wapple0, sNoScaleOp);
        GreenApple[0] = Bitmap.createScaledBitmap(GreenApple[0], 
                (int)(GreenApple[0].getWidth()*scaleRate), 
                (int)(GreenApple[0].getHeight()*scaleRate), true);
        GreenApple[1] = BitmapFactory.decodeResource(res, R.drawable.wapple1, sNoScaleOp);
        GreenApple[1] = Bitmap.createScaledBitmap(GreenApple[1], 
                (int)(GreenApple[1].getWidth()*scaleRate), 
                (int)(GreenApple[1].getHeight()*scaleRate), true);
        GreenApple[2] = BitmapFactory.decodeResource(res, R.drawable.wapple2, sNoScaleOp);
        GreenApple[2] = Bitmap.createScaledBitmap(GreenApple[2], 
                (int)(GreenApple[2].getWidth()*scaleRate), 
                (int)(GreenApple[2].getHeight()*scaleRate), true);
        GreenApple[3] = BitmapFactory.decodeResource(res, R.drawable.wapple3, sNoScaleOp);
        GreenApple[3] = Bitmap.createScaledBitmap(GreenApple[3], 
                (int)(GreenApple[3].getWidth()*scaleRate), 
                (int)(GreenApple[3].getHeight()*scaleRate), true);
        GreenApple[4] = BitmapFactory.decodeResource(res, R.drawable.wapple4, sNoScaleOp);
        GreenApple[4] = Bitmap.createScaledBitmap(GreenApple[4], 
                (int)(GreenApple[4].getWidth()*scaleRate), 
                (int)(GreenApple[4].getHeight()*scaleRate), true);
    }
    
    private static void initBowRelated(Resources res, float hScaleRate, float wScaleRate){
        BowStatusBar = BitmapFactory.decodeResource(res, R.drawable.bow_status_bar, sNoScaleOp);
        BowStatusBar = Bitmap.createScaledBitmap(BowStatusBar, 
                (int)(GameStageWidth*BowBar_Width_Rate), 
                (int)(BowStatusBar.getHeight()*hScaleRate), true);
        BowHandleBar = BitmapFactory.decodeResource(res, R.drawable.bow_handle_bar, sNoScaleOp);
        BowHandleBar = Bitmap.createScaledBitmap(BowHandleBar, 
                (int)(GameStageWidth*BowBar_Width_Rate), 
                (int)(BowHandleBar.getHeight()*hScaleRate), true);
        BowIcon = new Bitmap[3];
        BowIcon[0] = BitmapFactory.decodeResource(res, R.drawable.ic_bow0, sNoScaleOp);
        int bowIconWidth = (int)(BowIcon[0].getWidth() * hScaleRate);
        int bowIconHeight = (int)(BowIcon[0].getHeight() * hScaleRate);
        BowIcon[0] = Bitmap.createScaledBitmap(BowIcon[0], bowIconWidth, bowIconHeight, true);
        BowIcon[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.ic_bow1, sNoScaleOp), 
                bowIconWidth, bowIconHeight, true);
        BowIcon[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.ic_bow2, sNoScaleOp), 
                bowIconWidth, bowIconHeight, true);
        Bitmap emptyBow = BitmapFactory.decodeResource(res, R.drawable.bow, sNoScaleOp);
        emptyBow = Bitmap.createScaledBitmap(emptyBow, 
                (int)(emptyBow.getWidth()*wScaleRate), (int)(emptyBow.getHeight()*wScaleRate), true);
        NormalBow = new Bitmap[5];
        NormalBow[0] = emptyBow;
        NormalBow[1] = BitmapFactory.decodeResource(res, R.drawable.nbow1, sNoScaleOp);
        NormalBow[1] = Bitmap.createScaledBitmap(NormalBow[1], 
                (int)(NormalBow[1].getWidth()*wScaleRate), (int)(NormalBow[1].getHeight()*wScaleRate), true);
        NormalBow[2] = BitmapFactory.decodeResource(res, R.drawable.nbow2, sNoScaleOp);
        NormalBow[2] = Bitmap.createScaledBitmap(NormalBow[2], 
                (int)(NormalBow[2].getWidth()*wScaleRate), (int)(NormalBow[2].getHeight()*wScaleRate), true);
        NormalBow[3] = BitmapFactory.decodeResource(res, R.drawable.nbow3, sNoScaleOp);
        NormalBow[3] = Bitmap.createScaledBitmap(NormalBow[3], 
                (int)(NormalBow[3].getWidth()*wScaleRate), (int)(NormalBow[3].getHeight()*wScaleRate), true);
        NormalBow[4] = BitmapFactory.decodeResource(res, R.drawable.nbow4, sNoScaleOp);
        NormalBow[4] = Bitmap.createScaledBitmap(NormalBow[4], 
                (int)(NormalBow[4].getWidth()*wScaleRate), (int)(NormalBow[4].getHeight()*wScaleRate), true);
        GoldenBow = new Bitmap[5];
        GoldenBow[0] = emptyBow;
        GoldenBow[1] = BitmapFactory.decodeResource(res, R.drawable.gbow1, sNoScaleOp);
        GoldenBow[1] = Bitmap.createScaledBitmap(GoldenBow[1], 
                (int)(GoldenBow[1].getWidth()*wScaleRate), (int)(GoldenBow[1].getHeight()*wScaleRate), true);
        GoldenBow[2] = BitmapFactory.decodeResource(res, R.drawable.gbow2, sNoScaleOp);
        GoldenBow[2] = Bitmap.createScaledBitmap(GoldenBow[2], 
                (int)(GoldenBow[2].getWidth()*wScaleRate), (int)(GoldenBow[2].getHeight()*wScaleRate), true);
        GoldenBow[3] = BitmapFactory.decodeResource(res, R.drawable.gbow3, sNoScaleOp);
        GoldenBow[3] = Bitmap.createScaledBitmap(GoldenBow[3], 
                (int)(GoldenBow[3].getWidth()*wScaleRate), (int)(GoldenBow[3].getHeight()*wScaleRate), true);
        GoldenBow[4] = BitmapFactory.decodeResource(res, R.drawable.gbow4, sNoScaleOp);
        GoldenBow[4] = Bitmap.createScaledBitmap(GoldenBow[4], 
                (int)(GoldenBow[4].getWidth()*wScaleRate), (int)(GoldenBow[4].getHeight()*wScaleRate), true);
    }
    
    private static void initArrow(Resources res, float wScaleRate){
        Arrow = new Bitmap[2];
        Arrow[0] = BitmapFactory.decodeResource(res, R.drawable.arrow0, sNoScaleOp);
        Arrow[0] = Bitmap.createScaledBitmap(Arrow[0], 
                (int)(Arrow[0].getWidth() * wScaleRate), (int)(Arrow[0].getHeight() * wScaleRate), true);
        Arrow[1] = BitmapFactory.decodeResource(res, R.drawable.arrow1, sNoScaleOp);
        Arrow[1] = Bitmap.createScaledBitmap(Arrow[1], 
                (int)(Arrow[1].getWidth()*wScaleRate), (int)(Arrow[1].getHeight()*wScaleRate), true);
        WeakArrowGroundPart = BitmapFactory.decodeResource(res, R.drawable.warrow_groundpart, sNoScaleOp);
        WeakArrowGroundPart = Bitmap.createScaledBitmap(WeakArrowGroundPart, 
                (int)(WeakArrowGroundPart.getWidth()*wScaleRate), 
                (int)(WeakArrowGroundPart.getHeight()*wScaleRate), true);
    }
    
    private static void initText(Resources res, float hScaleRate){
        // init numbers and number symbol
        NumberSymbols = new Bitmap[3];
        Numbers = new Bitmap[10];
        Numbers[0] = BitmapFactory.decodeResource(res, R.drawable.nt0, sNoScaleOp);
        int numberWidth = (int)(Numbers[0].getWidth() * hScaleRate);
        int numberHeight = (int)(Numbers[0].getHeight() * hScaleRate);
        Numbers[0] = Bitmap.createScaledBitmap(Numbers[0], numberWidth, numberHeight, true);
        Numbers[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.nt1, sNoScaleOp), 
                numberWidth, numberHeight, true);
        Numbers[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.nt2, sNoScaleOp), 
                numberWidth, numberHeight, true);
        Numbers[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.nt3, sNoScaleOp), 
                numberWidth, numberHeight, true);
        Numbers[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.nt4, sNoScaleOp), 
                numberWidth, numberHeight, true);
        Numbers[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.nt5, sNoScaleOp), 
                numberWidth, numberHeight, true);
        Numbers[6] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.nt6, sNoScaleOp), 
                numberWidth, numberHeight, true);
        Numbers[7] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.nt7, sNoScaleOp), 
                numberWidth, numberHeight, true);
        Numbers[8] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.nt8, sNoScaleOp), 
                numberWidth, numberHeight, true);
        Numbers[9] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.nt9, sNoScaleOp), 
                numberWidth, numberHeight, true);
        NumberSymbols[0] = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.add0, sNoScaleOp), 
                numberWidth, numberHeight, true);
        MNumbers = new Bitmap[10];
        MNumbers[0] = BitmapFactory.decodeResource(res, R.drawable.mnt0, sNoScaleOp);
        int mnumberWidth = (int)(MNumbers[0].getWidth() * hScaleRate);
        int mnumberHeight = (int)(MNumbers[0].getHeight() * hScaleRate);
        MNumbers[0] = Bitmap.createScaledBitmap(MNumbers[0], mnumberWidth, mnumberHeight, true);
        MNumbers[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.mnt1, sNoScaleOp), 
                mnumberWidth, mnumberHeight, true);
        MNumbers[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.mnt2, sNoScaleOp), 
                mnumberWidth, mnumberHeight, true);
        MNumbers[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.mnt3, sNoScaleOp), 
                mnumberWidth, mnumberHeight, true);
        MNumbers[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.mnt4, sNoScaleOp), 
                mnumberWidth, mnumberHeight, true);
        MNumbers[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.mnt5, sNoScaleOp), 
                mnumberWidth, mnumberHeight, true);
        MNumbers[6] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.mnt6, sNoScaleOp), 
                mnumberWidth, mnumberHeight, true);
        MNumbers[7] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.mnt7, sNoScaleOp), 
                mnumberWidth, mnumberHeight, true);
        MNumbers[8] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.mnt8, sNoScaleOp), 
                mnumberWidth, mnumberHeight, true);
        MNumbers[9] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.mnt9, sNoScaleOp), 
                mnumberWidth, mnumberHeight, true);
        NumberSymbols[1] = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.add1, sNoScaleOp), 
                mnumberWidth, mnumberHeight, true);
        LNumbers = new Bitmap[10];
        LNumbers[0] = BitmapFactory.decodeResource(res, R.drawable.lnt0, sNoScaleOp);
        int lnumberWidth = (int)(LNumbers[0].getWidth() * hScaleRate);
        int lnumberHeight = (int)(LNumbers[0].getHeight() * hScaleRate);
        LNumbers[0] = Bitmap.createScaledBitmap(LNumbers[0], lnumberWidth, lnumberHeight, true);
        LNumbers[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.lnt1, sNoScaleOp), 
                lnumberWidth, lnumberHeight, true);
        LNumbers[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.lnt2, sNoScaleOp), 
                lnumberWidth, lnumberHeight, true);
        LNumbers[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.lnt3, sNoScaleOp), 
                lnumberWidth, lnumberHeight, true);
        LNumbers[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.lnt4, sNoScaleOp), 
                lnumberWidth, lnumberHeight, true);
        LNumbers[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.lnt5, sNoScaleOp), 
                lnumberWidth, lnumberHeight, true);
        LNumbers[6] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.lnt6, sNoScaleOp), 
                lnumberWidth, lnumberHeight, true);
        LNumbers[7] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.lnt7, sNoScaleOp), 
                lnumberWidth, lnumberHeight, true);
        LNumbers[8] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.lnt8, sNoScaleOp), 
                lnumberWidth, lnumberHeight, true);
        LNumbers[9] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.lnt9, sNoScaleOp), 
                lnumberWidth, lnumberHeight, true);
        NumberSymbols[2] = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.add2, sNoScaleOp), 
                lnumberWidth, lnumberHeight, true);
        SLNumbers = new Bitmap[10];
        SLNumbers[0] = BitmapFactory.decodeResource(res, R.drawable.slnt0, sNoScaleOp);
        int slnumberWidth = (int)(SLNumbers[0].getWidth() * hScaleRate);
        int slNumberHeight = (int)(SLNumbers[0].getHeight() * hScaleRate);
        SLNumbers[0] = Bitmap.createScaledBitmap(SLNumbers[0], slnumberWidth, slNumberHeight, true);
        SLNumbers[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.slnt1, sNoScaleOp), 
                slnumberWidth, slNumberHeight, true);
        SLNumbers[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.slnt2, sNoScaleOp), 
                slnumberWidth, slNumberHeight, true);
        SLNumbers[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.slnt3, sNoScaleOp), 
                slnumberWidth, slNumberHeight, true);
        SLNumbers[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.slnt4, sNoScaleOp), 
                slnumberWidth, slNumberHeight, true);
        SLNumbers[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.slnt5, sNoScaleOp), 
                slnumberWidth, slNumberHeight, true);
        SLNumbers[6] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.slnt6, sNoScaleOp), 
                slnumberWidth, slNumberHeight, true);
        SLNumbers[7] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.slnt7, sNoScaleOp), 
                slnumberWidth, slNumberHeight, true);
        SLNumbers[8] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.slnt8, sNoScaleOp), 
                slnumberWidth, slNumberHeight, true);
        SLNumbers[9] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.slnt9, sNoScaleOp), 
                slnumberWidth, slNumberHeight, true);
        // init Tip text
        ScaleTipTexts = new Bitmap[3];
        ScaleTipTexts[0] = BitmapFactory.decodeResource(res, R.drawable.tip_gravity, sNoScaleOp);
        ScaleTipTexts[0] = Bitmap.createScaledBitmap(ScaleTipTexts[0], 
                (int)(ScaleTipTexts[0].getWidth()*hScaleRate), 
                (int)(ScaleTipTexts[0].getHeight()*hScaleRate), true);
        // fake, if ScaleTipText is CountDown number, it will use drawNumber instead
        ScaleTipTexts[1] = SLNumbers[0];
        ScaleTipTexts[2] = BitmapFactory.decodeResource(res, R.drawable.tip_highscore, sNoScaleOp);
        ScaleTipTexts[2] = Bitmap.createScaledBitmap(ScaleTipTexts[2], 
                (int)(ScaleTipTexts[2].getWidth()*hScaleRate), 
                (int)(ScaleTipTexts[2].getHeight()*hScaleRate), true);
        // pause game tip
        PauseGameTip = BitmapFactory.decodeResource(res, R.drawable.pausetip, sNoScaleOp);
        PauseGameTip = Bitmap.createScaledBitmap(PauseGameTip, 
                (int)(PauseGameTip.getWidth()*hScaleRate), 
                (int)(PauseGameTip.getHeight()*hScaleRate), true);
        // Tip Icon for Weapin Icon
        TipIconWeapon_StrongBow = BitmapFactory.decodeResource(res, R.drawable.tipicon_weapon_strongbow, sNoScaleOp);
        TipIconWeapon_StrongBow = Bitmap.createScaledBitmap(TipIconWeapon_StrongBow, 
                (int)(TipIconWeapon_StrongBow.getWidth()*hScaleRate), 
                (int)(TipIconWeapon_StrongBow.getHeight()*hScaleRate), true);
        TipIconWeapon_WeakBow = BitmapFactory.decodeResource(res, R.drawable.tipicon_weapon_weakbow, sNoScaleOp);
        TipIconWeapon_WeakBow = Bitmap.createScaledBitmap(TipIconWeapon_WeakBow, 
                (int)(TipIconWeapon_WeakBow.getWidth()*hScaleRate), 
                (int)(TipIconWeapon_WeakBow.getHeight()*hScaleRate), true);
        // Gold Coin
        GoldCoin = BitmapFactory.decodeResource(res, R.drawable.goldcoin, sNoScaleOp);
        GoldCoin = Bitmap.createScaledBitmap(GoldCoin, (int)(GoldCoin.getWidth()*hScaleRate), 
                (int)(GoldCoin.getHeight()*hScaleRate), true);
    }
    
    private static void initNewton(Resources res, float hScaleRate){
        Newton = new Bitmap[35];
        Newton[0] = BitmapFactory.decodeResource(res, R.drawable.newton0, sNoScaleOp);
        int normalNewtonWidth = (int)(Newton[0].getWidth() * hScaleRate);
        int normalNewtonHeight = (int)(Newton[0].getHeight() * hScaleRate);
        Newton[0] = Bitmap.createScaledBitmap(Newton[0], normalNewtonWidth, normalNewtonHeight, true);
        Newton[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton1, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton2, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton3, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton4, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton5, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[6] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton6, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[7] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton7, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[8] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton8, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[9] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton9, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[10] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton10, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[11] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton11, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[12] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton12, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[13] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton13, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[14] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton14, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[15] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton15, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[16] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton16, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[17] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton17, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[18] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton18, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[19] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton19, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[20] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton20, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[21] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton21, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[22] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.newton22, sNoScaleOp), 
                normalNewtonWidth, normalNewtonHeight, true);
        Newton[23] = BitmapFactory.decodeResource(res, R.drawable.newton23, sNoScaleOp);
        Newton[23] = Bitmap.createScaledBitmap(Newton[23], 
                (int)(Newton[23].getWidth()*hScaleRate), (int)(Newton[23].getHeight()*hScaleRate), true);
        Newton[24] = BitmapFactory.decodeResource(res, R.drawable.newton24, sNoScaleOp);
        Newton[24] = Bitmap.createScaledBitmap(Newton[24], 
                (int)(Newton[24].getWidth()*hScaleRate), (int)(Newton[24].getHeight()*hScaleRate), true);
        Newton[25] = BitmapFactory.decodeResource(res, R.drawable.newton25, sNoScaleOp);
        Newton[25] = Bitmap.createScaledBitmap(Newton[25], 
                (int)(Newton[25].getWidth()*hScaleRate), (int)(Newton[25].getHeight()*hScaleRate), true);
        Newton[26] = BitmapFactory.decodeResource(res, R.drawable.newton26, sNoScaleOp);
        Newton[26] = Bitmap.createScaledBitmap(Newton[26], 
                (int)(Newton[26].getWidth()*hScaleRate), (int)(Newton[26].getHeight()*hScaleRate), true);
        Newton[27] = BitmapFactory.decodeResource(res, R.drawable.newton27, sNoScaleOp);
        Newton[27] = Bitmap.createScaledBitmap(Newton[27], 
                (int)(Newton[27].getWidth()*hScaleRate), (int)(Newton[27].getHeight()*hScaleRate), true);
        Newton[28] = BitmapFactory.decodeResource(res, R.drawable.newton28, sNoScaleOp);
        Newton[28] = Bitmap.createScaledBitmap(Newton[28], 
                (int)(Newton[28].getWidth()*hScaleRate), (int)(Newton[28].getHeight()*hScaleRate), true);
        Newton[29] = BitmapFactory.decodeResource(res, R.drawable.newton29, sNoScaleOp);
        Newton[29] = Bitmap.createScaledBitmap(Newton[29], 
                (int)(Newton[29].getWidth()*hScaleRate), (int)(Newton[29].getHeight()*hScaleRate), true);
        Newton[30] = BitmapFactory.decodeResource(res, R.drawable.newton30, sNoScaleOp);
        Newton[30] = Bitmap.createScaledBitmap(Newton[30], 
                (int)(Newton[30].getWidth()*hScaleRate), (int)(Newton[30].getHeight()*hScaleRate), true);
        Newton[31] = BitmapFactory.decodeResource(res, R.drawable.newton31, sNoScaleOp);
        Newton[31] = Bitmap.createScaledBitmap(Newton[31], 
                (int)(Newton[31].getWidth()*hScaleRate), (int)(Newton[31].getHeight()*hScaleRate), true);
        Newton[32] = BitmapFactory.decodeResource(res, R.drawable.newton32, sNoScaleOp);
        Newton[32] = Bitmap.createScaledBitmap(Newton[32], 
                (int)(Newton[32].getWidth()*hScaleRate), (int)(Newton[32].getHeight()*hScaleRate), true);
        Newton[33] = BitmapFactory.decodeResource(res, R.drawable.newton33, sNoScaleOp);
        Newton[33] = Bitmap.createScaledBitmap(Newton[33], 
                (int)(Newton[33].getWidth()*hScaleRate), (int)(Newton[33].getHeight()*hScaleRate), true);
        Newton[34] = BitmapFactory.decodeResource(res, R.drawable.newton34, sNoScaleOp);
        Newton[34] = Bitmap.createScaledBitmap(Newton[34], 
                (int)(Newton[34].getWidth()*hScaleRate), (int)(Newton[34].getHeight()*hScaleRate), true);
    }
    
    private static void initNewtonSpeakText(Resources res, float hScaleRate){
        NewtonSpeakText = new Bitmap[12];
        NewtonSpeakText[0] = BitmapFactory.decodeResource(res, R.drawable.stext0, sNoScaleOp);
        int width = (int)(NewtonSpeakText[0].getWidth() * hScaleRate);
        int height = (int)(NewtonSpeakText[0].getHeight() * hScaleRate);
        NewtonSpeakText[0] = Bitmap.createScaledBitmap(NewtonSpeakText[0], width, height, true);
        NewtonSpeakText[1] = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.stext1, sNoScaleOp), 
                width, height, true);
        NewtonSpeakText[2] = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.stext2, sNoScaleOp), 
                width, height, true);
        NewtonSpeakText[3] = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.stext3, sNoScaleOp), 
                width, height, true);
        NewtonSpeakText[4] = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.stext4, sNoScaleOp), 
                width, height, true);
        NewtonSpeakText[5] = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.stext5, sNoScaleOp), 
                width, height, true);
        NewtonSpeakText[6] = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.stext6, sNoScaleOp), 
                width, height, true);
        NewtonSpeakText[7] = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.stext7, sNoScaleOp), 
                width, height, true);
        NewtonSpeakText[8] = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.stext8, sNoScaleOp), 
                width, height, true);
        NewtonSpeakText[9] = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.stext9, sNoScaleOp), 
                width, height, true);
        NewtonSpeakText[10] = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.stext10, sNoScaleOp), 
                width, height, true);
        NewtonSpeakText[11] = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.stext11, sNoScaleOp), 
                width, height, true);
    }
    
    private static void initToolButton(Resources res, float wScaleRate){
    	ToolButtonBG = BitmapFactory.decodeResource(res, R.drawable.tool_button_bg, sNoScaleOp);
    	ToolButtonBG = Bitmap.createScaledBitmap(ToolButtonBG, (int)(ToolButtonOriWidthHeight*wScaleRate), 
    			(int)(ToolButtonOriWidthHeight*wScaleRate), true);
    	SelectedToolButtonBG = BitmapFactory.decodeResource(res, R.drawable.tool_button_selected_bg, sNoScaleOp);
    	SelectedToolButtonBG = Bitmap.createScaledBitmap(SelectedToolButtonBG, (int)(ToolButtonOriWidthHeight*wScaleRate), 
    			(int)(ToolButtonOriWidthHeight*wScaleRate), true);
    	WeaponButtonStrongBow = BitmapFactory.decodeResource(res, R.drawable.ic_weapon_strongbow, sNoScaleOp);
    	WeaponButtonStrongBow = Bitmap.createScaledBitmap(WeaponButtonStrongBow, (int)(ToolButtonOriWidthHeight*wScaleRate), 
    			(int)(ToolButtonOriWidthHeight*wScaleRate), true);
    	WeaponButtonWeakBow = BitmapFactory.decodeResource(res, R.drawable.ic_weapon_weakbow, sNoScaleOp);
    	WeaponButtonWeakBow = Bitmap.createScaledBitmap(WeaponButtonWeakBow, (int)(ToolButtonOriWidthHeight*wScaleRate), 
    			(int)(ToolButtonOriWidthHeight*wScaleRate), true);
    	WeaponCountTextPaint = new Paint();
    	WeaponCountTextPaint.setColor(Color.YELLOW);
    	WeaponCountTextPaint.setStyle(Style.FILL);
    	WeaponCountTextPaint.setTextSize(ToolButtonTextSize * wScaleRate);
    }
    
    private static void initGameConstNumbers(){
        GameStageFrontLeftPos = GameStageWidth - GameStage_Front.getWidth();
        GameStageFrontTopPos = GameStageHeight - GameStage_Front.getHeight();
        GameStageBottom = GameStageHeight * (1-GameStage_LandBottom_Rate);
        AppleWidth = NormalApple[AppleNormal_Frame].getWidth();
        AppleHeight = NormalApple[AppleNormal_Frame].getHeight();
        AppleInitTopPos = (int)(AppleTopPos_Rate * GameStageHeight);
        AppleLeftPos = new int[AppleCreatePos_Count];
        AppleLeftPos[0] = (int)((1 - ApplePos0_Rate) * GameStageWidth) - AppleWidth;
        AppleLeftPos[1] = (int)((1 - ApplePos1_Rate) * GameStageWidth) - AppleWidth;
        AppleLeftPos[2] = (int)((1 - ApplePos2_Rate) * GameStageWidth) - AppleWidth;
        AppleLeftPos[3] = (int)((1 - ApplePos3_Rate) * GameStageWidth) - AppleWidth;
        AppleLeftPos[4] = (int)((1 - ApplePos4_Rate) * GameStageWidth) - AppleWidth;
        AppleLeftPos[5] = (int)((1 - ApplePos5_Rate) * GameStageWidth) - AppleWidth;
        // bow related
        BowStatusBarWidth = BowStatusBar.getWidth();
        BowStatusBarHeight = BowStatusBar.getHeight();
        BowWidth = NormalBow[0].getWidth();
        BowHeight = NormalBow[0].getHeight();
        BowLeftPos = (int)(GameStageWidth * Bow_LeftPos_Rate);
        BowHandleTopPosLimit = BowStatusBar.getHeight() + BowHeight/2;
        BowHandleRightPosLimit = BowHandleBar.getWidth();
        BowHandleBottomPosLimit = (int)(GameStageHeight * (1 - Bow_BottomLimit_Rate)) - BowHeight/2;
        // Gold Coin
        GoldCoinLeftPos = BowStatusBarWidth + 5;
        GoldCoinNumberLeftPos = GoldCoinLeftPos + GoldCoin.getWidth() + 3;
        // arrow related
        ArrowWidth = Arrow[0].getWidth();
        ArrowHeight = Arrow[0].getHeight();
        ArrowInsertTreeLeftPos = (int)(GameStageWidth * (1-Arrow_InsertTree_WidthRate)) - ArrowWidth;
        WArrowGroundPartWidth = WeakArrowGroundPart.getWidth();
        WArrowGroundPartHeight = WeakArrowGroundPart.getHeight();
        ArrowLeftPos = BowLeftPos + BowWidth - ArrowWidth;
        ArrowRelativeTopPos = BowHeight/2 - ArrowHeight/2;
        GoldenArrowRelativeTopPos = new int[GoldenBow_ArrowCount];
        for (int i=1;i<=GoldenBow_ArrowCount;i++){
            GoldenArrowRelativeTopPos[i-1] = (int)(BowHeight * i * GoldenBow_ArrowPos_Increase - ArrowHeight/2);
        }
        // tip text or icon related
        NumberWidth = new int[4];
        NumberWidth[NumberSize_Normal] = Numbers[0].getWidth();
        NumberWidth[NumberSize_Middle] = MNumbers[0].getWidth();
        NumberWidth[NumberSize_Big] = LNumbers[0].getWidth();
        NumberWidth[NumberSize_SuperBig] = SLNumbers[0].getWidth();
        BowIconTopPos = BowStatusBarHeight/2 - BowIcon[0].getHeight()/2;
        BowIconLeftPos = BowStatusBar.getWidth()/2 - BowIcon[0].getWidth()/2 - NumberWidth[NumberSize_Normal]/2; 
        BowIconCountRightPos = BowIconLeftPos + BowIcon[0].getWidth() 
            + NumberWidth[NumberSize_Normal] + NumberSplitWidth;
        BowIconCountTopPos = (int)(BowIconTopPos + BowIcon[0].getHeight() - Numbers[0].getHeight());
        StatInfoTopPos = BowIconTopPos - 3;
        ScaleTipTextWidth = new int[ScaleTipTexts.length];
        ScaleTipTextHeight = new int[ScaleTipTexts.length];
        for (int i=0;i<ScaleTipTexts.length;i++){
            ScaleTipTextWidth[i] = ScaleTipTexts[i].getWidth();
            ScaleTipTextHeight[i] = ScaleTipTexts[i].getHeight();
        }
        NewtonSpeakTextWidth = NewtonSpeakText[0].getWidth();
        NewtonSpeakTextHeight = NewtonSpeakText[0].getHeight();
        // Game Center Position
        GameCenterLeftPos = (GameStageWidth - BowStatusBarWidth)/2.0f + BowStatusBarWidth;
        GameCenterTopPos = GameStageHeight/2.0f;
        // pause tip pos
        PauseGameTipLeftPos = GameStageWidth/2 - PauseGameTip.getWidth()/2;
        PauseGameTipTopPos = GameStageHeight/2 - PauseGameTip.getHeight()/2;
        // newton pos
        NormalNewtonWidth = Newton[0].getWidth();
        NormalNewtonHeight = Newton[0].getHeight();
        FallNewtonWidth = Newton[NewtonFallEnd_Frame].getWidth();
        NewtonLeftLeftPosLimit = -Newton[0].getWidth();
        NewtonRightLeftPosLimit = GameStageWidth;
        NewtonTopPos = GameStageBottom - Newton[0].getHeight();
        NewtonHitCheckLeftPos = NormalNewtonWidth * Newton_HitCheck_LeftPosRate;
        NewtonHitCheckRightPos = NormalNewtonWidth * Newton_HitCheck_RightPosRate;
        NewtonAppleHitCheckBottomPos = Newton[0].getHeight() * Newton_AppleHitCheck_BottomPosRate;
        NewtonArrowShotCheckBottomPos = Newton[0].getHeight() * Newton_ArrowShotCheck_BottomPosRate;
        FallNewtonBodyWidth = (int)(FallNewtonWidth * FallNewton_BodyWidthRate);
        NewtonArrowShotLeftPos = NormalNewtonWidth * Newton_ArrowShot_LeftPosRate;
        // Tool Button
        ToolButtonWidth = ToolButtonBG.getWidth();
        ToolButtonHalfWidth = ToolButtonWidth/2;
        ToolButtonTopPos = GameStageHeight - (ToolButtonWidth/2 + ToolButtonWidth);
        ToolButtonLastLeftPos = GameStageWidth - (ToolButtonWidth * 2 + ToolButtonWidth/2);
        ToolButtonFirstLeftPos = ToolButtonLastLeftPos;
        ToolButtonLeftMargin = ToolButtonWidth/2;
    }
}