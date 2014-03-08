package ml.game.android.SaveNewton;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public class GameBackgroundView extends View{
	public GameBackgroundView(Context context) {
		super(context);
	}
	
	@Override
	public void onDraw(Canvas canvas){
		int skyBackLeftPos = 0;
        for (int i=0;i<GameResource.GameStageSkyBackDrawCount;i++){
            canvas.drawBitmap(GameResource.GameStage_Back0, 
                    skyBackLeftPos, GameResource.GameStageSkyBackTopPos, null);
            skyBackLeftPos += GameResource.GameStageSkyBackWidth;
        }
        canvas.drawBitmap(GameResource.GameStage_Back1, 0, 0, null);
	}
}