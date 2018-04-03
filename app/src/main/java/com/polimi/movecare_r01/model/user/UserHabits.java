package com.polimi.movecare_r01.model.user;


/**
 * Weekly user habits
 */
public class UserHabits {
    private User user;
    private int num_outHome;
    private String favouriteWalkingPlace;
    private enum favouriteWalkingDay {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY;
    }
}
