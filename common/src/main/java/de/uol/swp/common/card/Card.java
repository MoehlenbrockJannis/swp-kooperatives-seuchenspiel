package de.uol.swp.common.card;

import de.uol.swp.common.util.Color;

import java.io.Serializable;

public abstract class Card implements Serializable {

    public abstract String getTitle();

    public abstract Color getColor();

    @Override
    public String toString() {
        return getTitle();
    }
}
