package model.upgrade;

import model.GameModifiers;

public class PatienceUpgrade extends Upgrade {

    public PatienceUpgrade() {
        super("Cozy Music", "Customers wait 15% longer per level", 120, 2);
    }

    @Override
    public void apply(GameModifiers modifiers) {
        modifiers.setPatienceMultiplier(1.0 + 0.15 * getLevel());
    }
}
