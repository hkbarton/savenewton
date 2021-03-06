package ml.game.android.SaveNewton;

import ml.game.android.SaveNewton.GameLogic.Apple;
import ml.game.android.SaveNewton.GameLogic.Arrow;
import ml.game.android.SaveNewton.GameLogic.Bow;
import ml.game.android.SaveNewton.GameLogic.Newton;
import ml.game.android.SaveNewton.GameLogic.TipText;
import ml.game.android.SaveNewton.GameLogic.ToolButton;
import ml.game.android.SaveNewton.GameLogic.WeaponSelectButton;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
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
            if (canvas==null){ // lock canvas fail
            	return;
            }
            canvas.drawColor(0, Mode.CLEAR);
            // draw background cloud, background should be drawn on background view to improve performance
            canvas.drawBitmap(GameResource.GameStage_Cloud, 
                    mGameLogic.CloudLeftPos, mGameLogic.CloudTopPos1, null);
            canvas.drawBitmap(GameResource.GameStage_Cloud, 
                    mGameLogic.CloudLeftPos + GameResource.GameStageCloudDis, 
                    mGameLogic.CloudTopPos2, null);
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
                case Apple.AppleType_LowGravity:
                    canvas.drawBitmap(GameResource.SpecialApple[apple.CurrentShowFrame], drawMatrix, transPaint);
                    break;
                }
            }
            // draw tip text
            drawTipText(canvas, drawMatrix, transPaint);
            // draw score and gold coin
            drawNumberFromRight(canvas, 
                    GameResource.GameStageWidth - GameResource.ScoreRightPosFromRightBorder,
                    GameResource.StatInfoTopPos, AchievementMgt.StatData.Score);
            canvas.drawBitmap(GameResource.GoldCoin, GameResource.GoldCoinLeftPos, 
            		GameResource.StatInfoTopPos, null);
            drawNumberFromLeft(canvas, GameResource.GoldCoinNumberLeftPos,
                    GameResource.StatInfoTopPos, DataAccess.GDGold);
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
                drawNumberFromRight(canvas, GameResource.BowIconCountRightPos, 
                        GameResource.BowIconCountTopPos, DataAccess.GDStrongBowCount);
                canvas.drawBitmap(GameResource.GoldenBow[bow.CurrentShowFrame], 
                        GameResource.BowLeftPos, bow.TopPos, null);
                break;
            case Bow.BowType_Weak:
                canvas.drawBitmap(GameResource.BowIcon[GameResource.WeakBowIcon_FrameIndex], 
                        GameResource.BowIconLeftPos, GameResource.BowIconTopPos, null);
                drawNumberFromRight(canvas, GameResource.BowIconCountRightPos, 
                        GameResource.BowIconCountTopPos, DataAccess.GDWeakBowCount);
                canvas.drawBitmap(GameResource.NormalBow[bow.CurrentShowFrame], 
                        GameResource.BowLeftPos, bow.TopPos, null);
                break;
            }
            // draw front stage
            canvas.drawBitmap(GameResource.GameStage_Front, 
                    GameResource.GameStageFrontLeftPos, GameResource.GameStageFrontTopPos, null);
            // draw tool buttons
            float buttonLeftPos = GameResource.ToolButtonLastLeftPos;
            for(int i=mGameLogic.ToolButtons.length-1;i>=0;i--){
            	ToolButton toolButton = mGameLogic.ToolButtons[i];
            	toolButton.setPosAndSize(buttonLeftPos, GameResource.ToolButtonTopPos, GameResource.ToolButtonWidth);
            	// draw button background
            	if (toolButton.isSelected){
            		canvas.drawBitmap(GameResource.SelectedToolButtonBG, buttonLeftPos, GameResource.ToolButtonTopPos, null);
            	}else{
            		canvas.drawBitmap(GameResource.ToolButtonBG, buttonLeftPos, GameResource.ToolButtonTopPos, null);
            	}
            	// draw button icon and count
            	if (toolButton instanceof WeaponSelectButton){
            		String count = "0";
            		WeaponSelectButton wbutton = (WeaponSelectButton)toolButton;
            		if (wbutton.WeaponType==WeaponSelectButton.WeaponButtonType_StrongBow){
            			canvas.drawBitmap(GameResource.WeaponButtonStrongBow, buttonLeftPos, GameResource.ToolButtonTopPos, null);
            			count = String.valueOf(DataAccess.GDStrongBowCount);
            		}else if (wbutton.WeaponType==WeaponSelectButton.WeaponButtonType_WeakBow){
            			canvas.drawBitmap(GameResource.WeaponButtonWeakBow, buttonLeftPos, GameResource.ToolButtonTopPos, null);
            			count = String.valueOf(DataAccess.GDWeakBowCount);
            		}
            		float textWidth = GameResource.WeaponCountTextPaint.measureText(count);
            		canvas.drawText(count, buttonLeftPos + (GameResource.ToolButtonHalfWidth-textWidth/2), 
            				GameResource.ToolButtonTopPos + GameResource.ToolButtonHalfWidth, 
            				GameResource.WeaponCountTextPaint);
            	}
            	buttonLeftPos -= GameResource.ToolButtonLeftMargin + GameResource.ToolButtonWidth;
            }
            GameResource.ToolButtonFirstLeftPos = buttonLeftPos;
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
    
    private void drawTipText(Canvas canvas, Matrix drawMatrix, Paint transPaint){
        // NOTE: do not use setAlpha to set transparent here, because it will set the original 
        // transparent pixel of bitmap with wrong alpha value, use set ColorMatrixColorFilter
        // here for resolve this problem
        transPaint.setAlpha(255);
        for (int i=0; i<mGameLogic.Tips.size();i++){
            TipText tip = mGameLogic.Tips.get(i);
            if (!tip.CanShow){
            	continue;
            }
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
                        tip.DrawNumberSymbol, tip.WeaponIconType);
            }
        }
        transPaint.setColorFilter(null);
    }
    
    private void drawNumberFromRight(Canvas canvas, int rightPos, int topPos, int value){
        String valueStr = String.valueOf(value);
        int length = valueStr.length();
        for (int i=length-1; i>=0; i--){
            int leftPos = rightPos - (length-i) * GameResource.NumberWidth[GameResource.NumberSize_Normal] - 
                (length-i-1) * GameResource.NumberSplitWidth;
            canvas.drawBitmap(GameResource.Numbers[valueStr.charAt(i)-48], leftPos, topPos, null);
        }
    }
    
    private void drawNumberFromLeft(Canvas canvas, int leftPos, int topPos, int value){
        String valueStr = String.valueOf(value);
        int length = valueStr.length();
        for (int i=0; i<length; i++){
            canvas.drawBitmap(GameResource.Numbers[valueStr.charAt(i)-48], leftPos, topPos, null);
            leftPos = leftPos + GameResource.NumberWidth[GameResource.NumberSize_Normal] + GameResource.NumberSplitWidth;
        }
    }
    
    private void drawNumber(Canvas canvas, Matrix drawMatrix, Paint transPaint, 
            int numberSize, float digitWidth, int value, boolean drawSymbol, int weaponIconType){
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
        if (weaponIconType>=0){
	        if (weaponIconType==TipText.WeaponIcon_StrongBow){
	        	canvas.drawBitmap(GameResource.TipIconWeapon_StrongBow, drawMatrix, transPaint);
	        }else if (weaponIconType==TipText.WeaponIcon_WeakBow){
	        	canvas.drawBitmap(GameResource.TipIconWeapon_WeakBow, drawMatrix, transPaint);
	        }
	        drawMatrix.postTranslate(digitWidth + GameResource.NumberSplitWidth * 2, 0);
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