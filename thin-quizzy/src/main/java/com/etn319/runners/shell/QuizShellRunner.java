package com.etn319.runners.shell;

import com.etn319.service.quiz.Quiz;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ConditionalOnProperty(value = "spring.shell.interactive.enabled", havingValue = "true", matchIfMissing = true)
public class QuizShellRunner {
    private final Quiz quiz;

    public QuizShellRunner(Quiz quiz) {
        this.quiz = quiz;
    }

    @ShellMethod(value = "Login command", key = "login")
    @ShellMethodAvailability("isUserNotLoggedIn")
    public String login(@ShellOption(defaultValue = "Unknown Hero") String userName) {
        quiz.applyParticipant(userName);
        return String.format("Welcome, %s!", userName);
    }

    @ShellMethod(value = "Start quiz command", key = "start")
    @ShellMethodAvailability("isUserLoggedIn")
    public String start() {
        quiz.clearResults();
        quiz.run();
        return "Share your result with 'share' command!";
    }

    @ShellMethod(value = "Share your results", key = "share")
    @ShellMethodAvailability("resultsAvailableToShare")
    public String share() {
        quiz.shareResults();
        return "Results are successfully sent to your school teacher and your mom!\n" +
                "Type 'start' to take another attempt.";
    }

    @ShellMethod(value = "Logout command", key = "logout")
    @ShellMethodAvailability("isUserLoggedIn")
    public void logout() {
        quiz.dropParticipant();
    }

    private Availability isUserLoggedIn() {
        if (quiz.participantApplied())
            return Availability.available();
        else
            return Availability.unavailable("Not logged in");
    }

    private Availability isUserNotLoggedIn() {
        if (!quiz.participantApplied())
            return Availability.available();
        else
            return Availability.unavailable("Already logged in");
    }

    private Availability resultsAvailableToShare() {
        if (quiz.isFinished())
            return Availability.available();
        else
            return Availability.unavailable("Nothing to share");
    }
}
