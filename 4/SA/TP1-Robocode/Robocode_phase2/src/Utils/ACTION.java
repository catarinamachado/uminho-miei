package Utils;

import java.io.Serializable;

public enum ACTION implements Serializable {
    ATTACK,             //Attacks someone
    HOLD,               //Stop any action
    MAKE_PRESSURE,      //Stay close to a zone attacking generally
    SCANNING,
    THINKING,
    MOVINGTOTARGET;
}