package model.upgrade;

import model.GameModifiers;

public class SecondStoveUpgrade extends Upgrade {

    public SecondStoveUpgrade() {
        super("Second Stove", "Unlock another stove to cook two dishes at once", 200, 1);
    }

    @Override
    public void apply(GameModifiers modifiers) {
        modifiers.setSecondStove(getLevel() > 0);
    }
}
