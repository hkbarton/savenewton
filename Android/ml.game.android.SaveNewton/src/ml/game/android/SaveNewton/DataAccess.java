package ml.game.android.SaveNewton;

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
            Pref_Level = GameLogic.Default_DifficultyLevel;
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
        initSensiveGameData(context);
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
        db.execSQL("CREATE TABLE IF NOT EXISTS gamedata(" + 
                "key TEXT NOT NULL PRIMARY KEY," + 
        		"value TEXT NOT NULL);");
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
    
    //-------------------------------------------- sensitive game data -----------------------------------------
    private static final String GameData_CurrentKey = "gd_current_key";
    
    public static final String GameData_GoldenAppleLevel = "gd_golden_apple_level";
    public static final String GameData_GreenAppleLevel = "gd_green_apple_level";
    public static final String GameData_GravityAppleLevel = "gd_gravity_apple_level";
    public static final String GameData_StrongBowCount = "gd_strong_bow_count";
    public static final String GameData_WeakBowCount = "gd_weak_bow_count";
    public static final String GameData_Gold = "gd_gold";
    public static final String GameData_DollarToGold1 = "gd_dollar2gold1";
    public static final String GameData_DollarToGold2 = "gd_dollar2gold2";
    public static final String GameData_DollarToGold3 = "gd_dollar2gold3";
    public static final String GameData_ShowAD = "gd_show_ad";
    
    public static final int GameData_MaxLevel = 5;
    private static final int GameData_LevelBaseCost = 500;
    private static final int GameData_BowWeaponUnitCost = 10;
    private static final int GameData_BasicLevelChance = 60; // means 1/60
    private static final int GameData_LevelChanceStep = 10;
    public static final int GameData_MaxWeaponCount = 999;
    public static final int GameData_WeaponCountInOneApple = 2;
    public static final int GameData_DollarToGold1Value = 10000; // $0.99
    public static final int GameData_DollarToGold2Value = 22000; // $1.99
    public static final int GameData_DollarToGold3Value = 35000; // $2.99
    
    private static String sGDCurrentKey;
    
    public static int GDGoldenAppleLevel, GDGreenAppleLevel, GDGravityAppleLevel;
    public static int GDStrongBowCount, GDWeakBowCount; // weapons
    public static int GDGold;
    public static int GDShowAD;
    
    private static HashMap<String, Float> sGameDataDefaultValue = new HashMap<String, Float>();
    
    private static void initSensiveGameData(Context context){
    	SQLiteDatabase db = context.openOrCreateDatabase(Database_Name, Context.MODE_PRIVATE, null);
    	updateGameDataUpdateDateStr(context, db, false);
    	GDGoldenAppleLevel = (int)getGameData(context, db, GameData_GoldenAppleLevel);
    	GDGreenAppleLevel = (int)getGameData(context, db, GameData_GreenAppleLevel);
    	GDGravityAppleLevel = (int)getGameData(context, db, GameData_GravityAppleLevel);
    	GDStrongBowCount = (int)getGameData(context, db, GameData_StrongBowCount);
    	GDWeakBowCount = (int)getGameData(context, db, GameData_WeakBowCount);
    	GDGold = (int)getGameData(context, db, GameData_Gold);
    	GDShowAD = (int)getGameData(context, db, GameData_ShowAD);
    	db.close();
    }
    
    private static void updateGameDataUpdateDateStr(Context context, SQLiteDatabase db, boolean force){
    	String newKey = SecueUtil.getRandomEncryptKey();
    	ContentValues data = new ContentValues();
		data.put("value", newKey);
    	Cursor cursor = db.query("gamedata", new String[]{"value"}, "key=?", new String[]{GameData_CurrentKey}, null, null, null);
    	if (cursor!=null && cursor.getCount()>0 && cursor.moveToFirst()){
    		// update
    		sGDCurrentKey = cursor.getString(0);
    		if (force){
    			db.update("gamedata", data, "key=?", new String[]{GameData_CurrentKey});
    			sGDCurrentKey = newKey;
    		}
    	}else{
    		data.put("key", GameData_CurrentKey);
    		db.insert("gamedata", null, data);
    		sGDCurrentKey = newKey;
    	}
    	cursor.close();
    }
    
    private static String getValueStoreStr(String key, float value){
    	return key + "`" + String.valueOf(value);
    }
    
    private static float getGameData(Context context, SQLiteDatabase db, String key){
    	Cursor cursor = db.query("gamedata", new String[]{"value"}, "key=?", new String[]{key}, null, null, null);
    	boolean foundValue = false;
    	float result = sGameDataDefaultValue.get(key);
    	if (cursor!=null && cursor.getCount()>0 && cursor.moveToFirst()){
    		String oriValue = cursor.getString(0);
    		String strValue = SecueUtil.decryptData(oriValue, sGDCurrentKey);
    		if (strValue!=null){
    			String[] valueData = strValue.split("`");
    			if (valueData[0].equals(key)){
	    			try{
	    				result = Float.parseFloat(valueData[1]);
	    				foundValue = true;
	    			}catch(Exception ex){
	    				result = sGameDataDefaultValue.get(key);
	    			}
    			}
    		}
    	}
    	if (!foundValue){
    		ContentValues data = new ContentValues();
    		String encrptValue = SecueUtil.encryptData(getValueStoreStr(key, result), sGDCurrentKey);
    		data.put("key", key);
    		data.put("value", encrptValue);
    		db.insert("gamedata", null, data);
    	}
    	cursor.close();
    	return result;
    }
    
    public static void saveAllGameData(Context context){
    	SQLiteDatabase db = context.openOrCreateDatabase(Database_Name, Context.MODE_PRIVATE, null);
    	updateGameDataUpdateDateStr(context, db, true);
    	ContentValues data = new ContentValues();
    	data.put("key", GameData_GoldenAppleLevel);
		data.put("value", SecueUtil.encryptData(getValueStoreStr(GameData_GoldenAppleLevel, GDGoldenAppleLevel), sGDCurrentKey));
		db.update("gamedata", data, "key=?", new String[]{GameData_GoldenAppleLevel});
		data.clear();
		data.put("key", GameData_GreenAppleLevel);
		data.put("value", SecueUtil.encryptData(getValueStoreStr(GameData_GreenAppleLevel, GDGreenAppleLevel), sGDCurrentKey));
		db.update("gamedata", data, "key=?", new String[]{GameData_GreenAppleLevel});
		data.clear();
		data.put("key", GameData_GravityAppleLevel);
		data.put("value", SecueUtil.encryptData(getValueStoreStr(GameData_GravityAppleLevel, GDGravityAppleLevel), sGDCurrentKey));
		db.update("gamedata", data, "key=?", new String[]{GameData_GravityAppleLevel});
		data.clear();
		data.put("key", GameData_StrongBowCount);
		data.put("value", SecueUtil.encryptData(getValueStoreStr(GameData_StrongBowCount, GDStrongBowCount), sGDCurrentKey));
		db.update("gamedata", data, "key=?", new String[]{GameData_StrongBowCount});
		data.clear();
		data.put("key", GameData_WeakBowCount);
		data.put("value", SecueUtil.encryptData(getValueStoreStr(GameData_WeakBowCount, GDWeakBowCount), sGDCurrentKey));
		db.update("gamedata", data, "key=?", new String[]{GameData_WeakBowCount});
		data.clear();
		data.put("key", GameData_Gold);
		data.put("value", SecueUtil.encryptData(getValueStoreStr(GameData_Gold, GDGold), sGDCurrentKey));
		db.update("gamedata", data, "key=?", new String[]{GameData_Gold});
		data.clear();
		data.put("key", GameData_ShowAD);
		data.put("value", SecueUtil.encryptData(getValueStoreStr(GameData_ShowAD, GDShowAD), sGDCurrentKey));
		db.update("gamedata", data, "key=?", new String[]{GameData_ShowAD});
		data.clear();
    	db.close();
    }
    
    public static int getChanceDataByLevel(int level){
    	return GameData_BasicLevelChance - GameData_LevelChanceStep * level;
    }
    
    public static int getNextLevelCost(String gameDataKey){
    	int nextLevel = 1;
    	if (gameDataKey.equals(GameData_GoldenAppleLevel)){
    		nextLevel = GDGoldenAppleLevel + 1;
    	}else if (gameDataKey.equals(GameData_GreenAppleLevel)){
    		nextLevel = GDGreenAppleLevel + 1;
    	}else if (gameDataKey.equals(GameData_GravityAppleLevel)){
    		nextLevel = GDGravityAppleLevel + 1;
    	}
    	if (nextLevel > GameData_MaxLevel){
    		return 0;
    	}
    	return (int)(Math.pow(2, nextLevel-1)*GameData_LevelBaseCost);
    }
    
    public static void levelUp(String gameDataKey){
    	int cost = getNextLevelCost(gameDataKey);
    	if (cost>0 && GDGold>cost){
    		GDGold = GDGold - cost;
	    	if (gameDataKey.equals(GameData_GoldenAppleLevel)){
	    		GDGoldenAppleLevel++;
	    	}else if (gameDataKey.equals(GameData_GreenAppleLevel)){
	    		GDGreenAppleLevel++;
	    	}else if (gameDataKey.equals(GameData_GravityAppleLevel)){
	    		GDGravityAppleLevel++;
	    	}
    	}
    }
    
    public static int getBowWeaponCost(int count){
    	if (count>0){
    		return count * GameData_BowWeaponUnitCost;
    	}
    	return 0;
    }
    
    public static void buyBowWeapon(String gameDataKey, int count){
    	int cost = getBowWeaponCost(count);
    	if (cost>0 && GDGold>cost){
    		if (gameDataKey.equals(GameData_StrongBowCount)){
    			GDGold = GDGold - cost;
    			GDStrongBowCount += count;
    		}else if (gameDataKey.equals(GameData_WeakBowCount)){
    			GDGold = GDGold - cost;
    			GDWeakBowCount += count;
    		}
    	}
    }
    
    public static void buyBilledData(Context context, String gameDataKey){
    	if (gameDataKey.equals(GameData_DollarToGold1)){
    		GDGold += GameData_DollarToGold1Value;
    	}else if (gameDataKey.equals(GameData_DollarToGold2)){
    		GDGold += GameData_DollarToGold2Value;
    	}else if (gameDataKey.equals(GameData_DollarToGold3)){
    		GDGold += GameData_DollarToGold3Value;
    	}else if (gameDataKey.equals(GameData_ShowAD)){
    		GDShowAD = 0;
    	}
    	saveAllGameData(context);
    }
    
    public static void increaseStrongBowCountFromApple(){
    	if (GDStrongBowCount < GameData_MaxWeaponCount){
    		GDStrongBowCount += GameData_WeaponCountInOneApple;
    	}
    }
    
    public static void increaseWeakBowCountFromApple(){
    	if (GDWeakBowCount < GameData_MaxWeaponCount){
    		GDWeakBowCount += GameData_WeaponCountInOneApple;
    	}
    }
    
    public static float getGameDataByKey(String key){
    	if (key.equals(GameData_GoldenAppleLevel)){
    		return GDGoldenAppleLevel;
    	}
    	if (key.equals(GameData_GreenAppleLevel)){
    		return GDGreenAppleLevel;
    	}
    	if (key.equals(GameData_GravityAppleLevel)){
    		return GDGravityAppleLevel;
    	}
    	if (key.equals(GameData_StrongBowCount)){
    		return GDStrongBowCount;
    	}
    	if (key.equals(GameData_WeakBowCount)){
    		return GDWeakBowCount;
    	}
    	return 0;
    }
    
    // init sensitive game data
    static{
    	sGameDataDefaultValue.put(GameData_GoldenAppleLevel, 0f);
    	sGameDataDefaultValue.put(GameData_GreenAppleLevel, 0f);
    	sGameDataDefaultValue.put(GameData_GravityAppleLevel, 0f);
    	sGameDataDefaultValue.put(GameData_StrongBowCount, 5f);
    	sGameDataDefaultValue.put(GameData_WeakBowCount, 5f);
    	sGameDataDefaultValue.put(GameData_Gold, 99000f); // TODO
    	sGameDataDefaultValue.put(GameData_ShowAD, 1f);
    }
}