package erik.exercisetracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eriks_000 on 8/18/2015.
 */
public class CurrentWorkout {
    public static List<ExerciseContent> exercises = new ArrayList<>();
    public static void addToWorkout(ExerciseContent exerciseContent) {
        exercises.add(exerciseContent);
    }

    public static List<ExerciseContent> getWorkoutExercises() {
        return exercises;
    }

    public static void removeFromWorkout(int j) {
        exercises.remove(j);
    }
}
