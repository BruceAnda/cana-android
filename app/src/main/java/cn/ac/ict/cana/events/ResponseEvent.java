package cn.ac.ict.cana.events;

/**
 * Author: saukymo
 * Date: 9/14/16
 */
public class ResponseEvent {
    public final boolean success;
    public final long id;
    public final int total;
    public final int groupPosition;
    public final int childPosition;
    public ResponseEvent(boolean success, long id, int total, int groupPosition, int childPosition) {
        this.success = success;
        this.id = id;
        this.total = total;
        this.groupPosition = groupPosition;
        this.childPosition = childPosition;
    }
}
