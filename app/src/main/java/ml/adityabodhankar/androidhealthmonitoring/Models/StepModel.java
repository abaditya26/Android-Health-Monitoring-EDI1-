package ml.adityabodhankar.androidhealthmonitoring.Models;

public class StepModel {
    private String date, steps, uid;

    public StepModel() {
    }

    public StepModel(String date, String steps, String uid) {
        this.date = date;
        this.steps = steps;
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
