package model.upgrade;

import model.GameModifiers;

public class FastStoveUpgrade extends Upgrade {

    public FastStoveUpgrade() {
        super("Fast Stove", "Cooking time -20% per level", 150, 2);
    }

    @Override
    public void apply(GameModifiers modifiers) {
        modifiers.setCookTimeMultiplier(1.0 - 0.2 * getLevel());
    }
}
