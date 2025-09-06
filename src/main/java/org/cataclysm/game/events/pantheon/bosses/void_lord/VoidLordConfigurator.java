package org.cataclysm.game.events.pantheon.bosses.void_lord;

import org.bukkit.attribute.Attribute;

public class VoidLordConfigurator {
    private final VoidLord lord;

    public VoidLordConfigurator(VoidLord lord) {
        this.lord = lord;
    }

    public void applySetUp(boolean configure) {
        this.lord.getController().getInventory().addItem(this.lord.getSword());
        this.lord.setUpBossBar(configure);
        this.applyAttributes(configure);
        this.lord.setCurrentPhase(0); // same as above
        this.lord.setElapsing(true); // so the first phase starts on the core's first tick
        this.lord.handleEvents();
    }

    public void applyAttributes(boolean empowered) {
        if (empowered) applyEmpoweredAttributes();
        else applyBaseAttributes();
    }

    private void applyEmpoweredAttributes() {
        lord.setAttribute(Attribute.KNOCKBACK_RESISTANCE, 2.0);
        lord.setAttribute(Attribute.MOVEMENT_SPEED, 0.225);
        lord.setAttribute(Attribute.JUMP_STRENGTH, 0.9);
        lord.setAttribute(Attribute.STEP_HEIGHT, 1.0);
        lord.setAttribute(Attribute.SCALE, 1.5);
    }

    private void applyBaseAttributes() {
        lord.setAttribute(Attribute.MOVEMENT_SPEED, 0.1);
        lord.resetAttribute(Attribute.KNOCKBACK_RESISTANCE);
        lord.resetAttribute(Attribute.JUMP_STRENGTH);
        lord.resetAttribute(Attribute.STEP_HEIGHT);
        lord.resetAttribute(Attribute.SCALE);
    }
}
