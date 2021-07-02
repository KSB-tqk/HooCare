package cf.khanhsb.icare_v2.Model;

import android.widget.TextView;

public class ExerciseHistoryItem {
    private String workoutTitle, workoutDay, workoutTime, workoutDuration, workoutKcal;
    private int image;

    public ExerciseHistoryItem(String workoutTitle
            ,String workoutDay
            ,String workoutTime
            ,String workoutDuration
            ,String workoutKcal
            , int mImage) {
        this.workoutTitle = workoutTitle;
        this.workoutDay = workoutDay;
        this.workoutTime = workoutTime;
        this.workoutDuration = workoutDuration;
        this.workoutKcal = workoutKcal;
        this.image = mImage;
    }

    public String getWorkoutTitle() {
        return workoutTitle;
    }

    public void setWorkoutTitle(String workoutTitle) {
        this.workoutTitle = workoutTitle;
    }

    public String getWorkoutDay() {
        return workoutDay;
    }

    public void setWorkoutDay(String workoutDay) {
        this.workoutDay = workoutDay;
    }

    public String getWorkoutTime() {
        return workoutTime;
    }

    public void setWorkoutTime(String workoutTime) {
        this.workoutTime = workoutTime;
    }

    public String getWorkoutDuration() {
        return workoutDuration;
    }

    public void setWorkoutDuration(String workoutDuration) {
        this.workoutDuration = workoutDuration;
    }

    public String getWorkoutKcal() {
        return workoutKcal;
    }

    public void setWorkoutKcal(String workoutKcal) {
        this.workoutKcal = workoutKcal;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
