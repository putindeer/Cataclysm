package org.cataclysm.game.pantheon.phase;

public enum PantheonPhase {
    WARDEN_FIGHT,
    HYDRA_FIGHT,
    PALE_KING_FIGHT,
    VOID_LORD_FIGHT,
    RAGNAROK_FIGHT,
    FINAL_PHASE,

    BREAK, //Used for a 5-minute break
    WAITING, //Used when waiting for players to start the pantheon
    IDDLE, //Used when the pantheon is not active
}
