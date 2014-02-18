package ml.game.android.SaveNewton;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class AchievementMgt{
    // Achievement Description
    // Junior Shooter, Score>=30 in One Round
    private static final int JuniorShooter_Score = 30;
    // Senior Shooter, Score>=60 in One Round
    private static final int SeniorShooter_Score = 60;
    // Shooting Master, Score>=120 in One Round
    private static final int ShootingMaster_Score = 120;
    // Compote, continue hit apple 20 times and more in One Round
    private static final int Compote_HitCount = 20;
    // Fruit Feast, continue hit apple 40 times and more in One Round
    private static final int FruitFeast_HitCount = 40;
    // Apple Killer, continue hit apple 80 times and more in One Round
    private static final int AppleKiller_HitCount = 80;
    // Skill Student, hit apple 20 times using weak bow in One Round
    private static final int SkillStudent_HitCount = 20;
    // Skill Master, continue hit apple 30 times using weak bow in One Round
    private static final int SkillMaster_ContinueHitCount = 30;
    // Apple Defender, hide achievement, continue miss apple 30 times in One Round
    private static final int AppleDefender_MissCount = 30;
    // Scientist Killer, hide achievement, kill Newton 100 times
    private static final int ScientistKiller_Count = 100;
    
    public static class StatData{
        // Round Stat.
        public int Score;
        public int ContinueShootCount;
        public int ContinueMissCount;
        public int WeakBowShootCount;
        public int ContinueWeakBowShootCount;
        // Game Stat.
        public int KillNewtonTimes;
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
                        if (data.Score>=JuniorShooter_Score){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688132, "Senior Shooter"){
                    @Override public boolean unlock(StatData data){
                        if (data.Score>=SeniorShooter_Score){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688142, "Shooting Master"){
                    @Override public boolean unlock(StatData data){
                        if (data.Score>=ShootingMaster_Score){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688152, "Compote"){
                    @Override public boolean unlock(StatData data){
                        if (data.ContinueShootCount>=Compote_HitCount){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688162, "Fruit Feast"){
                    @Override public boolean unlock(StatData data){
                        if (data.ContinueShootCount>=FruitFeast_HitCount){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688172, "Apple Killer"){
                    @Override public boolean unlock(StatData data){
                        if (data.ContinueShootCount>=AppleKiller_HitCount){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688182, "Skill Student"){
                    @Override public boolean unlock(StatData data){
                        if (data.WeakBowShootCount>=SkillStudent_HitCount){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688182, "Skill Master"){
                    @Override public boolean unlock(StatData data){
                        if (data.ContinueWeakBowShootCount>=SkillMaster_ContinueHitCount){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688192, "Scientist Killer"){
                    @Override public boolean unlock(StatData data){
                        if (data.KillNewtonTimes>=ScientistKiller_Count){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement(688212, "Apple Defender"){
                    @Override public boolean unlock(StatData data){
                        if (data.ContinueMissCount>=AppleDefender_MissCount){
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
        StatData.ContinueWeakBowShootCount = 0;
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
