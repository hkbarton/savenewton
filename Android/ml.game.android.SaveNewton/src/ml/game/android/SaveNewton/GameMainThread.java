package ml.game.android.SaveNewton;

import ml.game.android.SaveNewton.GameLogic.Apple;
import ml.game.android.SaveNewton.GameLogic.Arrow;
import ml.game.android.SaveNewton.GameLogic.Bow;
import ml.game.android.SaveNewton.GameLogic.Newton;
import ml.game.android.SaveNewton.GameLogic.TipText;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.SurfaceHolder;

public class GameMainThread extends Thread{
    private SurfaceHolder mGameViewHolder;
    private GameLogic mGameLogic;
    private boolean mRunningFlag;
    private boolean mStopThreadFlag;
    private boolean mHaveDrawLastFrame;
    
    public GameMainThread(SurfaceHolder gameViewHolder, GameLogic gameLogic){
        mGameViewHolder = gameViewHolder;
        mGameLogic = gameLogic;
        mRunningFlag = false;
        mStopThreadFlag = false;
        mHaveDrawLastFrame = false;
    }
    
    @Override
    public void run(){
        Paint transPaint = new Paint();
        Matrix drawMatrix = new Matrix();
        while(mRunningFlag){
            if (mStopThreadFlag){
                mGameLogic.pauseGame(System.currentTimeMillis());
                mStopThreadFlag = false;
                break;
            }
            mGameLogic.runGame(System.currentTimeMillis());
            if (mGameLogic.getGameStatus()==GameLogic.GameStatus_Running
                    || mGameLogic.getGameStatus()==GameLogic.GameStatus_GameOvering){
                drawGameFrame(transPaint, drawMatrix, false);
                mHaveDrawLastFrame = false;
            }else{
                if (!mHaveDrawLastFrame){
                    drawGameFrame(transPaint, drawMatrix, true);
                    mHaveDrawLastFrame = true;
                }
            }
        }
    }
    
    private void drawGameFrame(Paint transPaint, Matrix drawMatrix, boolean isEndFrame){
        Canvas canvas = null;
        try{
            canvas = mGameViewHolder.lockCanvas(null);
            // draw background
            int skyBackLeftPos = 0;
            for (int i=0;i<GameResource.GameStageSkyBackDrawCount;i++){
                canvas.drawBitmap(GameResource.GameStage_Back0, 
                        skyBackLeftPos, GameResource.GameStageSkyBackTopPos, null);
                skyBackLeftPos += GameResource.GameStageSkyBackWidth;
            }
            canvas.drawBitmap(GameResource.GameStage_Cloud, 
                    mGameLogic.CloudLeftPos, mGameLogic.CloudTopPos1, null);
            canvas.drawBitmap(GameResource.GameStage_Cloud, 
                    mGameLogic.CloudLeftPos + GameResource.GameStageCloudDis, 
                    mGameLogic.CloudTopPos2, null);
            canvas.drawBitmap(GameResource.GameStage_Back1, 0, 0, null);
            // draw arrow
            for (int i=0;i<mGameLogic.Arrows.size();i++){
                Arrow arrow = mGameLogic.Arrows.get(i);
                transPaint.setAlpha((int)(255 * arrow.Transparence));
                drawMatrix.reset();
                if (arrow.Degree>0){
                    drawMatrix.postRotate(arrow.Degree);
                }
                drawMatrix.postTranslate(arrow.LeftPos, arrow.TopPos);
                canvas.drawBitmap(GameResource.Arrow[arrow.CurrentShowFrame], drawMatrix, transPaint);
                if (arrow.DrawWArrowGroundPart){
                    canvas.drawBitmap(GameResource.WeakArrowGroundPart, 
                            arrow.WArrowGroundLeftPos, arrow.WArrowGroundTopPos, transPaint);
                }
            }
            // draw apple
            for (int i=0; i<mGameLogic.Apples.size(); i++){
                Apple apple = mGameLogic.Apples.get(i);
                transPaint.setAlpha((int)(255 * apple.Transparence));
                drawMatrix.reset();
                if (apple.ScaleRate < 1){
                    drawMatrix.postScale(apple.ScaleRate, apple.ScaleRate);
                }
                if (apple.Degree != 0){
                    drawMatrix.postRotate(apple.Degree, GameResource.AppleWidth/2f, GameResource.AppleHeight/2f);
                }
                drawMatrix.postTranslate(apple.LeftPos, apple.TopPos);
                switch(apple.Type){
                case Apple.AppleType_Normal:
                    canvas.drawBitmap(GameResource.NormalApple[apple.CurrentShowFrame], drawMatrix, transPaint);
                    break;
                case Apple.AppleType_Golden:
                    canvas.drawBitmap(GameResource.GoldenApple[apple.CurrentShowFrame], drawMatrix, transPaint);
                    break;
                case Apple.AppleType_Weak:
                    canvas.drawBitmap(GameResource.GreenApple[apple.CurrentShowFrame], drawMatrix, transPaint);
                    break;
                case Apple.AppleType_Special:
                    canvas.drawBitmap(GameResource.SpecialApple[apple.CurrentShowFrame], drawMatrix, transPaint);
                    break;
                }
            }
            // draw tip text
            drawTipText(canvas, drawMatrix, transPaint);
            // draw score
            drawAllProgress(canvas);
            drawNumber(canvas, 
                    GameResource.GameStageWidth - GameResource.ScoreRightPosFromRightBorder,
                    GameResource.StatInfoTopPos, AchievementMgt.StatData.Score);
            // draw Newton
            Newton curNewton = mGameLogic.CurNewton;
            if (curNewton!=null){
                Bitmap newtonBitmap = GameResource.Newton[curNewton.CurFrame];
                drawMatrix.reset();
                if (curNewton.Direction==Newton.Direction_Left){
                    drawMatrix.postScale(-1, 1);
                    drawMatrix.postTranslate(curNewton.LeftPos + newtonBitmap.getWidth(), curNewton.TopPos);
                }else{
                    drawMatrix.postTranslate(curNewton.LeftPos, curNewton.TopPos);
                }
                canvas.drawBitmap(newtonBitmap, drawMatrix, null);
            }
            // draw bow bar
            canvas.drawBitmap(GameResource.BowStatusBar, 0, 0, null);
            canvas.drawBitmap(GameResource.BowHandleBar, 0, GameResource.BowStatusBarHeight, null);
            // draw bow and bow icon
            Bow bow = mGameLogic.CurBow;
            switch(bow.Type){
            case Bow.BowType_Normal:
                canvas.drawBitmap(GameResource.BowIcon[GameResource.NormalBowIcon_FrameIndex], 
                        GameResource.BowIconLeftPos, GameResource.BowIconTopPos, null);
                canvas.drawBitmap(GameResource.NormalBow[bow.CurrentShowFrame], 
                        GameResource.BowLeftPos, bow.TopPos, null);
                break;
            case Bow.BowType_Golden:
                canvas.drawBitmap(GameResource.BowIcon[GameResource.GoldenBowIcon_FrameIndex], 
                        GameResource.BowIconLeftPos, GameResource.BowIconTopPos, null);
                drawNumber(canvas, GameResource.BowIconCountRightPos, 
                        GameResource.BowIconCountTopPos, bow.GoldenBowCount);
                canvas.drawBitmap(GameResource.GoldenBow[bow.CurrentShowFrame], 
                        GameResource.BowLeftPos, bow.TopPos, null);
                break;
            case Bow.BowType_Weak:
                canvas.drawBitmap(GameResource.BowIcon[GameResource.WeakBowIcon_FrameIndex], 
                        GameResource.BowIconLeftPos, GameResource.BowIconTopPos, null);
                drawNumber(canvas, GameResource.BowIconCountRightPos, 
                        GameResource.BowIconCountTopPos, bow.WeakBowCount);
                canvas.drawBitmap(GameResource.NormalBow[bow.CurrentShowFrame], 
                        GameResource.BowLeftPos, bow.TopPos, null);
                break;
            }
            // draw front stage
            canvas.drawBitmap(GameResource.GameStage_Front, 
                    GameResource.GameStageFrontLeftPos, GameResource.GameStageFrontTopPos, null);
            // draw end frame special
            if (isEndFrame){
                if (mGameLogic.getGameStatus()==GameLogic.GameStatus_Pause){
                    Paint maskPaint = new Paint();
                    maskPaint.setColor(0);
                    maskPaint.setAlpha(100);
                    canvas.drawRect(0, 0, GameResource.GameStageWidth, GameResource.GameStageHeight, maskPaint);
                    canvas.drawBitmap(GameResource.PauseGameTip, 
                            GameResource.PauseGameTipLeftPos, GameResource.PauseGameTipTopPos, null);
                }
            }
        }finally{
            if (canvas!=null){
                mGameViewHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
    
    private void drawAllProgress(Canvas canvas){
        // draw continue hit award
        int leftPos = drawScoreProgress(canvas, GameResource.BonusProgress, GameResource.ProgressBarStartLeftPos, 
                mGameLogic.ContinueHitCount, GameLogic.MaxContinueHitCount, GameLogic.GoldenContinueHit);
        // draw continue miss award
        drawScoreProgress(canvas, GameResource.PunishProgress, leftPos+GameResource.ProgressBarSplitWidth,
                mGameLogic.ContinueMissCount, GameLogic.MaxContinueMissCount, GameLogic.GoldenContinueMiss);
    }
    
    private int drawScoreProgress(Canvas canvas, Bitmap[] res, int startLeftPos, 
            int progressValue, int progressMax, int progressAwardValue){
        int topPos = GameResource.StatInfoTopPos;
        int leftPos = startLeftPos;
        int normalIncreaseLeftPos = res[3].getWidth();
        canvas.drawBitmap(res[0], leftPos, topPos, null);
        leftPos += res[0].getWidth();
        if (progressValue < 1){
            canvas.drawBitmap(res[1], leftPos, topPos, null);
        }else{
            canvas.drawBitmap(res[2], leftPos, topPos, null);
        }
        leftPos += res[1].getWidth();
        for (int i=2;i<progressMax;i++){
            if (i <= progressValue){
                if(i==progressAwardValue){
                    canvas.drawBitmap(res[6], leftPos, topPos, null);
                }else{
                    canvas.drawBitmap(res[4], leftPos, topPos, null);
                }
            }else{
                if(i==progressAwardValue){
                    canvas.drawBitmap(res[5], leftPos, topPos, null);
                }else{
                    canvas.drawBitmap(res[3], leftPos, topPos, null);
                }
            }
            leftPos += normalIncreaseLeftPos;
        }
        if (progressValue==progressMax){
            canvas.drawBitmap(res[8], leftPos, topPos, null);
        }else{
            canvas.drawBitmap(res[7], leftPos, topPos, null);
        }
        return leftPos += res[8].getWidth();
    }
    
    private void drawTipText(Canvas canvas, Matrix drawMatrix, Paint transPaint){
        // NOTE: do not use setAlpha to set transparent here, because it will set the original 
        // transparent pixel of bitmap with wrong alpha value, use set ColorMatrixColorFilter
        // here for resolve this problem
        transPaint.setAlpha(255);
        for (int i=0; i<mGameLogic.Tips.size();i++){
            TipText tip = mGameLogic.Tips.get(i);
            drawMatrix.reset();
            drawMatrix.postScale(tip.ScaleRate, tip.ScaleRate);
            drawMatrix.postTranslate(tip.LeftPos, tip.TopPos);
            if (tip.Type==TipText.TipType_ScaleTextTip 
                    && tip.ScaleTextFrame!=GameResource.ScaleTipText_CountDown_Frame){
                canvas.drawBitmap(GameResource.ScaleTipTexts[tip.ScaleTextFrame], drawMatrix, null);
            }else if (tip.Type==TipText.TipType_NewtonSpeakText){
                canvas.drawBitmap(GameResource.NewtonSpeakText[tip.SpeakTextFrame], drawMatrix, null);
            }else{
                transPaint.setColorFilter(new ColorMatrixColorFilter(new float[]{
                        1,0,0,0,0,
                        0,1,0,0,0,
                        0,0,1,0,0,
                        0,0,0,1,-(255-255*tip.Transparence)
                }));
                drawNumber(canvas, drawMatrix, transPaint, tip.NumberSize, 
                        GameResource.NumberWidth[tip.NumberSize] * tip.ScaleRate, tip.NumberValue, 
                        tip.DrawNumberSymbol);
            }
        }
        transPaint.setColorFilter(null);
    }
    
    private void drawNumber(Canvas canvas, int rightPos, int topPos, int value){
        String valueStr = String.valueOf(value);
        int length = valueStr.length();
        for (int i=length-1; i>=0; i--){
            int leftPos = rightPos - (length-i) * GameResource.NumberWidth[GameResource.NumberSize_Normal] - 
                (length-i-1) * GameResource.NumberSplitWidth;
            canvas.drawBitmap(GameResource.Numbers[valueStr.charAt(i)-48], leftPos, topPos, null);
        }
    }
    
    private void drawNumber(Canvas canvas, Matrix drawMatrix, Paint transPaint, 
            int numberSize, float digitWidth, int value, boolean drawSymbol){
        Bitmap[] numbers = null;
        switch(numberSize){
        case GameResource.NumberSize_Normal:
            numbers = GameResource.Numbers;
            break;
        case GameResource.NumberSize_Middle:
            numbers = GameResource.MNumbers;
            break;
        case GameResource.NumberSize_Big:
            numbers = GameResource.LNumbers;
            break;
        case GameResource.NumberSize_SuperBig:
            numbers = GameResource.SLNumbers;
            break;
        }
        if (drawSymbol){
            canvas.drawBitmap(GameResource.NumberSymbols[numberSize], drawMatrix, transPaint);
            drawMatrix.postTranslate(digitWidth + GameResource.NumberSplitWidth, 0);
        }
        String valueStr = String.valueOf(value);
        for (int i=0;i<valueStr.length();i++){
            canvas.drawBitmap(numbers[valueStr.charAt(i)-48], drawMatrix, transPaint);
            drawMatrix.postTranslate(digitWidth + GameResource.NumberSplitWidth, 0);
        }
    }
    
    public void startGame(){
        mRunningFlag = true;
        this.start();
    }
    
    public void stopGame(){
        mStopThreadFlag = true;
        // Must join thread for wait game thread end, this made the SurfaceView destroy 
        // after game thread end, because game thread have hold SurfaceView, if SurfaceView
        // destroy first, game thread may be throw unexpected exception.
        try {
            this.join();
        } catch (InterruptedException e) { }
    }
}