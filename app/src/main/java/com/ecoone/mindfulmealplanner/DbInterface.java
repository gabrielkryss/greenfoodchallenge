package com.ecoone.mindfulmealplanner;

import android.widget.LinearLayout;

import com.ecoone.mindfulmealplanner.db.AppDatabase;
import com.ecoone.mindfulmealplanner.db.Plan;
import com.ecoone.mindfulmealplanner.db.User;

import java.util.List;
import java.util.Locale;

public abstract class DbInterface {

    private static AppDatabase mDb;

    public static void setDb(AppDatabase db){mDb = db;}

    public static void addUser(final String username,
                        final String gender,
                        final String currentPlan) {
        User user = new User();
        user.username = username;
        user.gender = gender;
        user.currentPlanName =currentPlan;
        mDb.userDao().addUser(user);
    }

    public static String getGender(final String username) {
        return mDb.userDao().getUserGender(username);
    }

    public static String getCurrentPlanName(final String username) {
        return mDb.userDao().getCurrentPlanName(username);
    }

    public static void updateUserCurrentPlanName(final String username,
                                                 final String newPlanName) {
        User user = mDb.userDao().getUser(username);
        user.currentPlanName = newPlanName;
        mDb.userDao().updateUser(user);
    }

    public static List<String> getAllPlansName(final String username) {
        return mDb.planDao().getAllPlansName(username);
    }


    public static void addPlan(final String username,
                               final String planName,
                               final float[] foodAmount) {
        Plan plan = new Plan();
        plan.username = username;
        plan.planName = planName;
        plan.beef = foodAmount[0];
        plan.pork = foodAmount[1];
        plan.chicken = foodAmount[2];
        plan.fish = foodAmount[3];
        plan.eggs = foodAmount[4];
        plan.beans = foodAmount[5];
        plan.vegetables = foodAmount[6];
        mDb.planDao().addPlan(plan);
    }

    public static StringBuilder getUserDatatoString(final String username) {
        StringBuilder sb = new StringBuilder();
        User user = mDb.userDao().getUser(username);

        sb.append(String.format(Locale.CANADA,
                "Username: %s, Gender: %s, Current Plan name: %s\n\n" ,
                user.username, user.gender, user.currentPlanName));

        return sb;
    }

    public static Plan getCurrentPlan(final String username) {
        String currentPlanName = getCurrentPlanName(username);
        return mDb.planDao().getPlan(username, currentPlanName);
    }

    // Change the plan name which is the current plan of the User in Plan table
    // Also change the currentplan name entry in User table
    // need to change user and plan table
    // 1. update user
    // 2. delete target plan and add the plan with new name
    public static void changeCurrentPlanName(final String username,
                                             final String newPlanName) {
        User user = mDb.userDao().getUser(username);
        String oldPlanName = user.currentPlanName;
        user.currentPlanName = newPlanName;
        mDb.userDao().updateUser(user);
        // get old plan
        Plan oldPlan = mDb.planDao().getPlan(username, oldPlanName);
        // create new plan
        Plan newPlan = mDb.planDao().getPlan(username, oldPlanName);
        newPlan.planName = newPlanName;
        mDb.planDao().deletePlan(oldPlan);
        mDb.planDao().addPlan(newPlan);
    }

    public static void updateCurrentPlan(final String username, final Plan plan) {
        Plan currentPlan = getCurrentPlan(username);
        Plan newPlan = getCurrentPlan(username);
        mDb.planDao().deletePlan(currentPlan);
        newPlan.beef = plan.beef;
        newPlan.pork = plan.pork;
        newPlan.chicken = plan.chicken;
        newPlan.fish = plan.fish;
        newPlan.eggs = plan.eggs;
        newPlan.beans = plan.beans;
        newPlan.vegetables = plan.vegetables;
        mDb.planDao().addPlan(newPlan);
    }

    public static float[] getPlanArray(final Plan plan) {
        float[] foodAmount = new float[7];
        foodAmount[0] = plan.beef;
        foodAmount[1] = plan.pork;
        foodAmount[2] = plan.chicken;
        foodAmount[3] = plan.fish;
        foodAmount[4] = plan.eggs;
        foodAmount[5] = plan.beans;
        foodAmount[6] = plan.vegetables;
        return foodAmount;
    }

//    public static StringBuilder getPlanDatatoString(final Plan plan) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(String.format(Locale.CANADA,
//                "%s: Beef: %d, Pork: %d, Chicken: %d, Fish: %d, " +
//                        "Eggs: %d, Beans: %d, Vegetables: %d\n\n", plan.planName,
//                plan.beef, plan.pork, plan.chicken, plan.fish, plan.eggs,
//                plan.beans, plan.vegetables));
//        return sb;
//    }
//
//    public static StringBuilder getUserPlansDatatoString(final String username) {
//        StringBuilder sb = new StringBuilder();
//        List<Plan> allPlans = mDb.planDao().getAllPlans(username);
//        for (Plan plan: allPlans) {
//            sb.append(String.format(Locale.CANADA,
//                    "%s: Beef: %d, Pork: %d, Chicken: %d, Fish: %d" +
//                            "Eggs: %d, Beans: %d, Vegetables: %d\n\n", plan.planName,
//                    plan.beef, plan.pork, plan.chicken, plan.fish, plan.eggs,
//                    plan.beans, plan.vegetables));
//        }
//        return sb;
//    }
}
