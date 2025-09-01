package org.cataclysm.game.world.day;

import org.bukkit.Bukkit;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.json.JsonConfig;
import org.cataclysm.game.world.day.events.ChangeDayEvent;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public record DayManager(DayData data) {
    private static final LocalDate actualDate = LocalDate.now(ZoneId.of("Asia/Jakarta"));
    private static final int DAY_LIMIT = 35;

    public DayManager(@NotNull DayData data) {
        this.data = data;
    }

    public int getDay() {
        return (int) ChronoUnit.DAYS.between(this.data.getStartDate(), actualDate);
    }

    public void setDay(int day) {
        var newDay = Math.max(0, Math.min(DAY_LIMIT, day));

        var add = actualDate.minusDays(newDay);
        var month = add.getMonthValue();

        var date = add.getYear() + "-" + month + "-";
        if (month < 10) date = add.getYear() + "-0" + month + "-";

        var dayOfMonth = add.getDayOfMonth();
        if (dayOfMonth < 10) {
            date = date + "0" + dayOfMonth;
        } else {
            date = date + dayOfMonth;
        }

        this.data.setStartDate(LocalDate.parse(date));

        Cataclysm.setDay(this.getDay());
        Bukkit.getPluginManager().callEvent(new ChangeDayEvent(this.getDay()));
    }

    public void save(@NotNull JsonConfig jsonConfig) {
        jsonConfig.setJsonObject(Cataclysm.getGson().toJsonTree(this.data).getAsJsonObject());
        try {
            jsonConfig.save();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void restore(@NotNull JsonConfig jsonConfig) {
        var data = DayData.generate();

        if (!jsonConfig.getJsonObject().entrySet().isEmpty()) {
            data = Cataclysm.getGson().fromJson(jsonConfig.getJsonObject(), DayData.class);
        }

        var manager = new DayManager(data);
        Cataclysm.setDayManager(manager);
        Cataclysm.setDay(manager.getDay());
    }
}