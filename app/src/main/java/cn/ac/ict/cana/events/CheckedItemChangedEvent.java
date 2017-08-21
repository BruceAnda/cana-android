package cn.ac.ict.cana.events;

/**
 * Author: saukymo
 * Date: 9/18/16
 */
public class CheckedItemChangedEvent {
    public final int count;
    public CheckedItemChangedEvent(int count){
        this.count = count;
    }
}
