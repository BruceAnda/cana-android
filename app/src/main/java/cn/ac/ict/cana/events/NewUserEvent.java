package cn.ac.ict.cana.events;

import cn.ac.ict.cana.models.User;

/**
 * Author: saukymo
 * Date: 9/21/16
 */
public class NewUserEvent {
    final public User user;
    public NewUserEvent(User user) {
        this.user = user;
    }
}
