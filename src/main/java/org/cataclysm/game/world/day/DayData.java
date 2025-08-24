package org.cataclysm.game.world.day;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DayData {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private String startDateString;

    public DayData(LocalDate startDate) {
        this.setStartDate(startDate);
    }

    public void setStartDate(@NotNull LocalDate startDate) {
        this.startDateString = startDate.format(FORMATTER);
    }

    public LocalDate getStartDate() {
        return LocalDate.parse(startDateString, FORMATTER);
    }

    public static @NotNull DayData generate() {
        return new DayData(LocalDate.now(ZoneId.of("Asia/Jakarta")));
    }
}
