package ml.game.android.SaveNewton.lite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

public class DataAccess{
    private static final String PrefKey_IsSound = "pis";
    private static final String PrefKey_IsVibration = "piv";
    private static final String PrefKey_Level = "pl";
    private static final String Database_Name = "save_newton_db";
    private static final String TableName_Score = "local_score";
    private static final int Max_LocalScore_Count = 100;
    
    public static boolean Pref_IsSound;
    public static boolean Pref_IsVibration;
    public static float Pref_Level;
    
    public static void init(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Pref_Level = pref.getFloat(PrefKey_Level, -1);
        if (Pref_Level==-1){ // not initialized, initialize preferences
            Pref_IsSound = true;
            Pref_IsVibration = true;
            Pref_Level = GameLogic.DifficultyLevel_Normal;
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(PrefKey_IsSound, Pref_IsSound);
            editor.putBoolean(PrefKey_IsVibration, Pref_IsVibration);
            editor.putFloat(PrefKey_Level, Pref_Level);
            editor.commit();
        }else{
            Pref_IsSound = pref.getBoolean(PrefKey_IsSound, true);
            Pref_IsVibration = pref.getBoolean(PrefKey_IsVibration, true);
        }
        initDatabase(context);
    }
    
    private static void initDatabase(Context context){
        SQLiteDatabase db = context.openOrCreateDatabase(Database_Name, Context.MODE_PRIVATE, null);
        // Check table version and drop old table if needed. Not implement temporary.
        // Create new table
        db.execSQL("CREATE TABLE IF NOT EXISTS local_score(" + 
                "rid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," + 
                "name TEXT NOT NULL," + 
                "score INTEGER NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS achievement(" + 
                "aid INTEGER NOT NULL PRIMARY KEY," + 
                "status INTEGER NOT NULL DEFAULT 0);");
        // Init table data
        Cursor cursor = db.query("achievement", new String[]{"aid"}, null, null, null, null, null);
        if (cursor.getCount()==0){
            for (int i=0; i<AchievementMgt.Achievements.length; i++){
                ContentValues data = new ContentValues();
                data.put("aid", AchievementMgt.Achievements[i].ID);
                db.insert("achievement", null, data);
            }
        }
        cursor.close();
        db.close();
    }
    
    public static void setSoundPref(Context context, boolean value){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PrefKey_IsSound, value);
        editor.commit();
        Pref_IsSound = value;
    }
    
    public static void setVibrationPref(Context context, boolean value){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PrefKey_IsVibration, value);
        editor.commit();
        Pref_IsVibration = value;
    }
    
    public static void addLevelPref(Context context){
        if (Pref_Level==GameLogic.DifficultyLevel_Training){
            Pref_Level = GameLogic.DifficultyLevel_Easy;
        }else if (Pref_Level==GameLogic.DifficultyLevel_Easy){
            Pref_Level = GameLogic.DifficultyLevel_Normal;
        }else if (Pref_Level==GameLogic.DifficultyLevel_Normal){
            Pref_Level = GameLogic.DifficultyLevel_Hard;
        }else if (Pref_Level==GameLogic.DifficultyLevel_Hard){
            Pref_Level = GameLogic.DifficultyLevel_Crazy;
        }else if (Pref_Level==GameLogic.DifficultyLevel_Crazy){
            Pref_Level = GameLogic.DifficultyLevel_Training;
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(PrefKey_Level, Pref_Level);
        editor.commit();
    }
    
    public static void saveLocalScore(Context context, String name, int score){
        SQLiteDatabase db = context.openOrCreateDatabase(Database_Name, Context.MODE_PRIVATE, null);
        Cursor cursor = db.query(TableName_Score, new String[]{"rid"}, null, null, null, null, "score");
        if (cursor!=null && cursor.getCount()>=Max_LocalScore_Count){
            cursor.moveToFirst();
            db.delete(TableName_Score, "rid=?", new String[]{String.valueOf(cursor.getInt(0))});
        }
        cursor.close();
        ContentValues data = new ContentValues();
        data.put("name", name);
        data.put("score", score);
        db.insert(TableName_Score, null, data);
        db.close();
    }
    
    public static int getScoreRank(Context context, int score){
        int result = 1;
        SQLiteDatabase db = context.openOrCreateDatabase(Database_Name, Context.MODE_PRIVATE, null);
        Cursor cursor = db.query(TableName_Score, 
                new String[]{"rid"}, "score>?", new String[]{String.valueOf(score)}, 
                null, null, null);
        if (cursor!=null){
            result = cursor.getCount() + 1;
        }
        cursor.close();
        db.close();
        if (result>=Max_LocalScore_Count){
            result = Max_LocalScore_Count;
        }
        return result;
    }
    
    public static List<Map<String,String>> getScoreList(Context context){
        List<Map<String,String>> result = null;
        SQLiteDatabase db = context.openOrCreateDatabase(Database_Name, Context.MODE_PRIVATE, null);
        Cursor cursor = db.query(TableName_Score, 
                new String[]{"name,score"}, null, null, null, null, "score DESC");
        if (cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            result = new ArrayList<Map<String,String>>();
            int i = 1;
            do{
                HashMap<String, String> item = new HashMap<String, String>();
                item.put("rank", String.valueOf(i));
                item.put("name", cursor.getString(0));
                item.put("score", String.valueOf(cursor.getInt(1)));
                result.add(item);
                i++;
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }
    
    public static List<Integer> getUnlockAchievementIDs(Context context){
        List<Integer> result = new ArrayList<Integer>();
        SQLiteDatabase db = context.openOrCreateDatabase(Database_Name, Context.MODE_PRIVATE, null);
        Cursor cursor = db.query("achievement", new String[]{"aid"}, "status<>?", 
                new String[]{String.valueOf(AchievementMgt.AchievementStatus_Locked)}, null, null, null);
        if (cursor!=null && cursor.moveToFirst()){
            do{
                result.add(cursor.getInt(0));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }
    
    public static void unlockAchievementLocal(Context context, int aid){
        SQLiteDatabase db = context.openOrCreateDatabase(Database_Name, Context.MODE_PRIVATE, null);
        ContentValues data = new ContentValues();
        data.put("aid", aid);
        data.put("status", AchievementMgt.AchievementStatus_UnLockLocal);
        db.update("achievement", data, "aid=?", new String[]{String.valueOf(aid)});
        db.close();
    }
    
    public static int[] getLocalUnlockAchievementIDs(Context context){
        int[] result = null;
        SQLiteDatabase db = context.openOrCreateDatabase(Database_Name, Context.MODE_PRIVATE, null);
        Cursor cursor = db.query("achievement", new String[]{"aid"}, "status=?", 
                new String[]{String.valueOf(AchievementMgt.AchievementStatus_UnLockLocal)}, null, null, null);
        if (cursor!=null && cursor.getCount()>0 && cursor.moveToFirst()){
            result = new int[cursor.getCount()];
            int i=0;
            do{
                result[i] = cursor.getInt(0);
                i++;
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }
    
    public static void unlockAchievementOnline(Context context, int aid){
        SQLiteDatabase db = context.openOrCreateDatabase(Database_Name, Context.MODE_PRIVATE, null);
        ContentValues data = new ContentValues();
        data.put("aid", aid);
        data.put("status", AchievementMgt.AchievementStatus_UnLockOnline);
        db.update("achievement", data, "aid=?", new String[]{String.valueOf(aid)});
        db.close();
    }
}