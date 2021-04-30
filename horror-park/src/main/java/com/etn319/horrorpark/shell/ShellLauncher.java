package com.etn319.horrorpark.shell;

import com.etn319.horrorpark.domain.Attender;
import com.etn319.horrorpark.domain.AttenderGroup;
import com.etn319.horrorpark.misc.AttenderGroupCoordinator;
import com.etn319.horrorpark.misc.Crematory;
import com.etn319.horrorpark.misc.HorrorPark;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ShellComponent
public class ShellLauncher {
    private final HorrorPark horrorPark;
    private final Crematory crematory;
    private final AttenderGroupCoordinator groupCoordinator;
    private List<Attender> attenders = new ArrayList<>();

    public ShellLauncher(HorrorPark horrorPark, Crematory crematory, AttenderGroupCoordinator groupCoordinator) {
        this.horrorPark = horrorPark;
        this.crematory = crematory;
        this.groupCoordinator = groupCoordinator;
    }

    @ShellMethod(value = "Add an attender", key = "add")
    public String add(@ShellOption("n") String name) {
        Attender attender = new Attender(name);
        attenders.add(attender);
        return String.format("Added: %s", attender.toDetailedString());
    }

    @ShellMethod(value = "Enter the Horror Park", key = "go")
    public String go() {
        AttenderGroup attenderGroup = new AttenderGroup();
        attenderGroup.setAttenders(new HashSet<>(attenders));
        groupCoordinator.setInitialGroup(attenderGroup);
        AttenderGroup returnedGroup = horrorPark.enter(attenderGroup);
        if (returnedGroup == null) {
            log.info("Не дождались группу на выходе");
        } else {
            String sAlive = returnedGroup.getAttenders().stream()
                    .map(Attender::toStringWithHealth)
                    .collect(Collectors.joining(", "));
            String sDead = attenders.stream()
                    .filter(Attender::isDead)
                    .map(Attender::toStringWithHealth)
                    .collect(Collectors.joining(", "));
            log.info("Вышли живыми из парка: {}", sAlive);
            log.info("Уехали на катафалках: {}", sDead);
            attenders = aliveAttenders(returnedGroup);
        }
        return "The end";
    }

    @ShellMethod(value = "Visit crematory", key = {"cr", "F"})
    public String visitCrematory() {
        Collection<Attender> ashes = crematory.visit();
        if (ashes != null) {
            String sAttenders = ashes.stream()
                    .map(Attender::toString)
                    .collect(Collectors.joining(", "));
            log.info("В крематории обнаружены {}", sAttenders);
        } else {
            log.info("В крематории сейчас сиеста - работы нет");
        }
        return "Visited";
    }

    @ShellMethod(value = "New group", key = "new")
    public String newGroup() {
        attenders = new ArrayList<>();
        return "Группа расформирована";
    }

    private List<Attender> aliveAttenders(AttenderGroup group) {
        if (group == null)
            return new ArrayList<>();
        return group.getAttenders().stream()
                .filter(Attender::isAlive)
                .collect(Collectors.toList());
    }
}
