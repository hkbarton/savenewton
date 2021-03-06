package ml.game.android.SaveNewton;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class AchievementMgt{
    // Achievement Description
    // Junior Shooter, Score>=50 in One Round
    private static final int JuniorShooter_Score = 50;
    // Senior Shooter, Score>=100 in One Round
    private static final int SeniorShooter_Score = 100;
    // Shooting Master, Score>=200 in One Round
    private static final int ShootingMaster_Score = 200;
    // Compote, continue hit apple 30 times and more in One Round
    private static final int Compote_HitCount = 30;
    // Fruit Feast, continue hit apple 50 times and more in One Round
    private static final int FruitFeast_HitCount = 50;
    // Apple Killer, continue hit apple 100 times and more in One Round
    private static final int AppleKiller_HitCount = 100;
    // Skill Student, hit apple 20 times using weak bow in One Round
    private static final int SkillStudent_HitCount = 20;
    // Skill Master, continue hit apple 30 times using weak bow in One Round
    private static final int SkillMaster_ContinueHitCount = 30;
    // Apple Defender, hide achievement, continue miss apple 50 times in One Round
    private static final int AppleDefender_MissCount = 50;
    // Scientist Killer, hide achievement, kill Newton 30 times
    private static final int ScientistKiller_Count = 30;
    
    public static class StatData{
    	public int CurrentHighestScore;
        // Round Stat.
        public int Score;
        public int ContinueShootCount;
        public int ContinueMissCount;
        public int WeakBowShootCount;
        public int ContinueWeakBowShootCount;
        // Game Stat.
        public int KillNewtonTimes;
        
        public boolean isHighestScore(){
        	if (Score > CurrentHighestScore){
        		return true;
        	}
        	return false;
        }
    }
    
    public static class LocalAchievement{
        public String ID;
        public String Name;
        
        public LocalAchievement(String id, String name){
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
    private static List<String> sUnlockAchievementIDs;
    
    public static void initAchievements(){
        Achievements = new LocalAchievement[]{
                new LocalAchievement("CgkI-cSrmt8BEAIQAQ", "Junior Shooter"){
                    @Override public boolean unlock(StatData data){
                        if (data.Score>=JuniorShooter_Score){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement("CgkI-cSrmt8BEAIQAg", "Senior Shooter"){
                    @Override public boolean unlock(StatData data){
                        if (data.Score>=SeniorShooter_Score){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement("CgkI-cSrmt8BEAIQAw", "Shooting Master"){
                    @Override public boolean unlock(StatData data){
                        if (data.Score>=ShootingMaster_Score){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement("CgkI-cSrmt8BEAIQBA", "Compote"){
                    @Override public boolean unlock(StatData data){
                        if (data.ContinueShootCount>=Compote_HitCount){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement("CgkI-cSrmt8BEAIQBQ", "Fruit Feast"){
                    @Override public boolean unlock(StatData data){
                        if (data.ContinueShootCount>=FruitFeast_HitCount){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement("CgkI-cSrmt8BEAIQBg", "Apple Killer"){
                    @Override public boolean unlock(StatData data){
                        if (data.ContinueShootCount>=AppleKiller_HitCount){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement("CgkI-cSrmt8BEAIQBw", "Skill Student"){
                    @Override public boolean unlock(StatData data){
                        if (data.WeakBowShootCount>=SkillStudent_HitCount){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement("CgkI-cSrmt8BEAIQCA", "Skill Master"){
                    @Override public boolean unlock(StatData data){
                        if (data.ContinueWeakBowShootCount>=SkillMaster_ContinueHitCount){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement("CgkI-cSrmt8BEAIQCQ", "Apple Defender"){
                    @Override public boolean unlock(StatData data){
                        if (data.ContinueMissCount>=AppleDefender_MissCount){
                            return true;
                        }
                        return false;
                    }
                },
                new LocalAchievement("CgkI-cSrmt8BEAIQCg", "Scientist Killer"){
                    @Override public boolean unlock(StatData data){
                        if (data.KillNewtonTimes>=ScientistKiller_Count){
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
        StatData.CurrentHighestScore = DataAccess.getHightestScore(context);
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
