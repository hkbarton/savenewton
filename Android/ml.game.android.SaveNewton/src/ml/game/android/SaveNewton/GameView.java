package ml.game.android.SaveNewton;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{
    private GameMainThread mGameThread;
    private GameLogic mGameLogic;
    
    public GameView(Context context, Handler gameEventHandler) {
        super(context);
        this.getHolder().addCallback(this);
        mGameLogic = new GameLogic(gameEventHandler);
    }
    
    public void restartGame(){
        mGameLogic.resetGame();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    public void surfaceCreated(SurfaceHolder holder) {
        mGameThread  = new GameMainThread(holder, mGameLogic);
        mGameThread.startGame();
    }

    // note: this CallBack will be called every times when activity stop
    public void surfaceDestroyed(SurfaceHolder holder) {
        mGameThread.stopGame();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();
        // Deal Game Pause Signal
        if (action==MotionEvent.ACTION_DOWN){
            mGameLogic.dealGamePauseSignalUserInput(event.getX(), event.getY());
        }
        // Bow Control
        if (mGameLogic.getGameStatus()==GameLogic.GameStatus_Running){
            switch (action){
            case MotionEvent.ACTION_DOWN:
                mGameLogic.CurBow.pullBow(event.getX(), event.getY());
                mGameLogic.clickToolButton(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                mGameLogic.CurBow.moveBow(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                mGameLogic.CurBow.releaseBow(event.getY());
                break;
            }
        }
        return true;
    }
}