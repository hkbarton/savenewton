package ml.game.android.SaveNewton;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class AchievementMgt{
    public static class StatData{
        // Round Stat.
        public int Score;
        public int ContinueShootCount;
        public int ContinueMissCount;
        public int WeakBowShootCount;
        // Game Stat.
        public int KillNewtonTimes;
        public int AppleHitNewtonTimes;
    }
    
    public static class LocalAchievement{
        public int ID;
        public String Name;
        
        public LocalAchievement(int id, String name){
            ID = id;
            Name = name;
        }
        
        public boolean unlock(StatData data){
            return false;
        }
    }
    
    public static final int AchievementStatus_Locked = 0;
    public static final int AchievementStatus_UnLockLocal = 1;
    public static final int AchievementStatus_UnLockOnline = 2;
    
    public static StatData StatData;
    public static LocalAchievement[] Achievements;
    private static List<Integer> sUnlockAchievementIDs;
    
    public static void initAchievements(){
        Achievements = new LocalAchievement[]{
                new LocalAchievement(688122, "Junior Shooter"){
                    @Override public boolean unlock(StatData data){
                        if (data.Score>=1000){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688132, "Senior Shooter"){
                    @Override public boolean unlock(StatData data){
                        if (data.Score>=5000){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688142, "Shooting Master"){
                    @Override public boolean unlock(StatData data){
                        if (data.Score>=10000){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688152, "Compote"){
                    @Override public boolean unlock(StatData data){
                        if (data.ContinueShootCount>=10){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688162, "Fruit Feast"){
                    @Override public boolean unlock(StatData data){
                        if (data.ContinueShootCount>=20){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688172, "Apple Killer"){
                    @Override public boolean unlock(StatData data){
                        if (data.ContinueShootCount>=50){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688182, "Skill Master"){
                    @Override public boolean unlock(StatData data){
                        if (data.WeakBowShootCount>=30){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688192, "Scientist Killer"){
                    @Override public boolean unlock(StatData data){
                        if (data.KillNewtonTimes>=5){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688202, "Scientist Assistant"){
                    @Override public boolean unlock(StatData data){
                        if (data.AppleHitNewtonTimes>=5){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688212, "Apple Defender"){
                    @Override public boolean unlock(StatData data){
                        if (data.ContinueMissCount>=30){
                            return true;
                        }
                        return false;
                    }
                }
        };
    }
    
    public static void init(Context context){
        StatData = new StatData();
        sUnlockAchievementIDs = DataAccess.getUnlockAchievementIDs(context);
    }
    
    public static void resetRoundStatData(){
        StatData.Score = 0;
        StatData.ContinueShootCount = 0;
        StatData.ContinueMissCount = 0;
        StatData.WeakBowShootCount = 0;
    }
    
    public static List<LocalAchievement> unlockAchievementsByStatData(Context context){
        if (sUnlockAchievementIDs.size()==Achievements.length){
            return null;
        }
        List<LocalAchievement> result = new ArrayList<LocalAchievement>();
        for(int i=0; i<Achievements.length; i++){
            if (Achievements[i].unlock(StatData) && !sUnlockAchievementIDs.contains(Achievements[i].ID)){
                sUnlockAchievementIDs.add(Achievements[i].ID);
                DataAccess.unlockAchievementLocal(context, Achievements[i].ID);
                result.add(Achievements[i]);
            }
        }
        return result;
    }
    
    public static String getAchievementsUnlockTip(Context context, List<LocalAchievement> achievements){
        if (achievements!=null && achievements.size()>0){
            String achievementsDes = "";
            for (int i=0; i<achievements.size(); i++){
                achievementsDes += String.format("\"%s\", ", achievements.get(i).Name);
            }
            if (achievementsDes.length()>0){
                achievementsDes = achievementsDes.substring(0, achievementsDes.length()-2);
            }
            return String.format(context.getString(R.string.tipUnlockAchievement), achievementsDes);
        }
        return null;
    }
}