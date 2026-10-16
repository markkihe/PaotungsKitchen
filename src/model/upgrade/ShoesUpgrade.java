package model.upgrade;

import model.GameModifiers;

public class ShoesUpgrade extends Upgrade {

    public ShoesUpgrade() {
        super("Running Shoes", "Chef walks faster", 120, 2);
    }

    @Override
    public void apply(GameModifiers modifiers) {
        modifiers.setChefSpeedBonus(getLevel());
    }
}
