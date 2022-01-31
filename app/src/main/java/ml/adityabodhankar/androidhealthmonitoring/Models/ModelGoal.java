package ml.adityabodhankar.androidhealthmonitoring.Models;

public class ModelGoal {
    private String stepGoal, caloriesGoal;

    public ModelGoal() {
    }
    public ModelGoal(String stepGoal, String caloriesGoal) {
        this.stepGoal = stepGoal;
        this.caloriesGoal = caloriesGoal;
    }
    public String getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(String stepGoal) {
        this.stepGoal = stepGoal;
    }

    public String getCaloriesGoal() {
        return caloriesGoal;
    }

    public void setCaloriesGoal(String caloriesGoal) {
        this.caloriesGoal = caloriesGoal;
    }

}
