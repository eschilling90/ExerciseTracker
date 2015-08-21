package erik.exercisetracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eriks_000 on 8/18/2015.
 */
public class CurrentWorkout {
    String name;
    String description;
    String tags;
    List<Exercise> exercises = new ArrayList<>();

    public void addToWorkout(ExerciseContent exerciseContent) {
        addToWorkout(exerciseContent, "");
    }

    public void addToWorkout(ExerciseContent exerciseContent, String sets) {
        Exercise exercise = new Exercise();
        exercise.exercise = exerciseContent;
        exercise.sets = sets;
        exercises.add(exercise);
    }

    public List<ExerciseContent> getWorkoutExercises() {
        List<ExerciseContent> exerciseContents = new ArrayList<>();
        for (Exercise exercise : exercises) {
            exerciseContents.add(exercise.exercise);
        }
        return exerciseContents;
    }

    public void removeFromWorkout(int j) {
        exercises.remove(j);
    }

    public ExerciseContent getExercise(int j) {
        return exercises.get(j).exercise;
    }

    private class Exercise {
        ExerciseContent exercise;
        String sets;
    }
}
