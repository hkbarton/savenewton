package ml.game.android.SaveNewton.lite;

import android.content.Context;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;

public class AudioResource{
    public static final int BGMID_Title = 1;
    public static final int BGMID_Play = 2;
    
    private static final int SoundEffectCount = 8;
    
    private static SoundPool sSoundEffectPool;
    private static AsyncPlayer sBgmPlayer;
    private static int sCurBGMID;
    
    public static int SoundEffectID_AppleHit;
    public static int SoundEffectID_Arrow;
    public static int SoundEffectID_ArrowEnd;
    public static int SoundEffectID_ArrowInApple;
    public static int SoundEffectID_ArrowInNewton;
    public static int SoundEffectID_Bow;
    public static int SoundEffectID_Prize;
    public static int[] SoundEffectID_Speaks;
    
    public static void initAudio(Context context){
        sSoundEffectPool = new SoundPool(SoundEffectCount, AudioManager.STREAM_MUSIC, 0);
        sBgmPlayer = new AsyncPlayer("SaveNewton");
        SoundEffectID_AppleHit = sSoundEffectPool.load(context, R.raw.applehit, 0);
        SoundEffectID_Arrow = sSoundEffectPool.load(context, R.raw.arrow, 0);
        SoundEffectID_ArrowEnd = sSoundEffectPool.load(context, R.raw.arrowend, 0);
        SoundEffectID_ArrowInApple = sSoundEffectPool.load(context, R.raw.arrowinapple, 0);
        SoundEffectID_ArrowInNewton = sSoundEffectPool.load(context, R.raw.arrowinnewton, 0);
        SoundEffectID_Bow = sSoundEffectPool.load(context, R.raw.bow, 0);
        SoundEffectID_Prize = sSoundEffectPool.load(context, R.raw.prize, 0);
        SoundEffectID_Speaks = new int[12];
        SoundEffectID_Speaks[0] = sSoundEffectPool.load(context, R.raw.speak0, 0);
        SoundEffectID_Speaks[1] = sSoundEffectPool.load(context, R.raw.speak1, 0);
        SoundEffectID_Speaks[2] = sSoundEffectPool.load(context, R.raw.speak2, 0);
        SoundEffectID_Speaks[3] = sSoundEffectPool.load(context, R.raw.speak3, 0);
        SoundEffectID_Speaks[4] = sSoundEffectPool.load(context, R.raw.speak4, 0);
        SoundEffectID_Speaks[5] = sSoundEffectPool.load(context, R.raw.speak5, 0);
        SoundEffectID_Speaks[6] = sSoundEffectPool.load(context, R.raw.speak6, 0);
        SoundEffectID_Speaks[7] = sSoundEffectPool.load(context, R.raw.speak7, 0);
        SoundEffectID_Speaks[8] = sSoundEffectPool.load(context, R.raw.speak8, 0);
        SoundEffectID_Speaks[9] = sSoundEffectPool.load(context, R.raw.speak9, 0);
        SoundEffectID_Speaks[10] = sSoundEffectPool.load(context, R.raw.speak10, 0);
        SoundEffectID_Speaks[11] = sSoundEffectPool.load(context, R.raw.speak11, 0);
    }
    
    public static void playBGM(Context context, int bgmID){
        if (bgmID==sCurBGMID){
            return;
        }
        String bgmURI = null;
        switch(bgmID){
        case BGMID_Title:
            bgmURI = "android.resource://ml.game.android.SaveNewton.lite/raw/titlebgm";
            break;
        case BGMID_Play:
            bgmURI = "android.resource://ml.game.android.SaveNewton.lite/raw/playbgm";
            break;
        }
        if (bgmURI!=null){
            sCurBGMID = bgmID;
            sBgmPlayer.play(context, Uri.parse(bgmURI), true, AudioManager.STREAM_MUSIC);
        }
    }
    
    public static void playSoundEffect(int effectID){
        if (effectID > 0 && DataAccess.Pref_IsSound){
            sSoundEffectPool.play(effectID, 0.9f, 0.9f, 0, 0, 1.0f);
        }
    }
    
    public static void stopBGM(int bgmID){
        if (bgmID==sCurBGMID){
            sBgmPlayer.stop();
            sCurBGMID = 0;
        }
    }
    
    public static void releaseResource(){
        sBgmPlayer = null;
        if (sSoundEffectPool!=null){
            sSoundEffectPool.release();
            sSoundEffectPool = null;
        }
    }
}