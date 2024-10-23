package org.example;

import java.time.LocalTime;
import java.util.*;

public class MovieScheduler {

    public static List<ShowSchedule> createSchedule(List<Movie> movies, List<Screen> screens) {
        // Validate inputs
        if (movies == null || movies.isEmpty() || screens == null || screens.isEmpty()) {
            return Collections.emptyList();
        }

        // Sort movies by duration to prioritize scheduling shorter movies first
        movies.sort(Comparator.comparingInt(m -> m.durationMinutes));

        List<ShowSchedule> schedule = new ArrayList<>();

        for (Screen screen : screens) {
            for (TimeSlot timeSlot : screen.availableSlots) {
                LocalTime currentTime = timeSlot.startTime;

                while (true) {
                    boolean movieScheduled = false;

                    // Try to schedule the shortest movie that fits
                    for (Movie movie : movies) {
                        LocalTime potentialEndTime = currentTime.plusMinutes(movie.durationMinutes);

                        // Check if movie fits in the current time slot including cleaning break
                        if (!potentialEndTime.isAfter(timeSlot.endTime)) {
                            // Schedule the movie
                            schedule.add(new ShowSchedule(
                                    screen.screenNumber,
                                    movie.name,
                                    currentTime,
                                    potentialEndTime
                            ));

                            // Update current time with cleaning break
                            currentTime = potentialEndTime.plusMinutes(30);
                            movieScheduled = true;
                            break; // Break to schedule next movie
                        }
                    }

                    // If no movie fits, exit the loop
                    if (!movieScheduled) {
                        break;
                    }

                    // Check if there's enough time for the shortest movie after the break
                    if (currentTime.plusMinutes(movies.get(0).durationMinutes).isAfter(timeSlot.endTime)) {
                        break;
                    }
                }
            }
        }

        // Sort the schedule by screen number and start time
        schedule.sort(Comparator.comparingInt(ShowSchedule::getScreenNumber)
                .thenComparing(ShowSchedule::getStartTime));

        return schedule;
    }

    // Classes as per the provided definitions
    static class Movie {
        String name;
        int durationMinutes;

        public Movie(String name, int durationMinutes) {
            this.name = name;
            this.durationMinutes = durationMinutes;
        }
    }

    static class Screen {
        int screenNumber;
        List<TimeSlot> availableSlots;

        public Screen(int screenNumber, List<TimeSlot> availableSlots) {
            this.screenNumber = screenNumber;
            this.availableSlots = availableSlots;
        }
    }

    static class TimeSlot {
        LocalTime startTime;
        LocalTime endTime;

        public TimeSlot(LocalTime startTime, LocalTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    static class ShowSchedule {
        int screenNumber;
        String movieName;
        LocalTime startTime;
        LocalTime endTime;

        public ShowSchedule(int screenNumber, String movieName, LocalTime startTime, LocalTime endTime) {
            this.screenNumber = screenNumber;
            this.movieName = movieName;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public int getScreenNumber() {
            return screenNumber;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        // For displaying the schedule
        @Override
        public String toString() {
            return "Screen " + screenNumber + ": " + movieName + " from " + startTime + " to " + endTime;
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        // Movies list
        List<Movie> movies = Arrays.asList(
                new Movie("Avatar", 180),
                new Movie("Spider-Man", 150),
                new Movie("Inception", 160)
        );

        // Available screens with time slots
        List<Screen> screens = Arrays.asList(
                new Screen(1, Arrays.asList(new TimeSlot(LocalTime.of(9, 0), LocalTime.of(23, 0)))),
                new Screen(2, Arrays.asList(new TimeSlot(LocalTime.of(9, 0), LocalTime.of(23, 0))))
        );

        // Generate schedule
        List<ShowSchedule> schedule = createSchedule(movies, screens);

        // Display the schedule
        for (ShowSchedule show : schedule) {
            System.out.println(show);
        }
    }
}