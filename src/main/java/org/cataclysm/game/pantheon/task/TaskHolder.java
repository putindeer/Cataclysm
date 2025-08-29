package org.cataclysm.game.pantheon.task;

import java.util.concurrent.ScheduledFuture;

public record TaskHolder(String identifier, ScheduledFuture<?> future) {
}
