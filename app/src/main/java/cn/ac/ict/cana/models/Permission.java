package cn.ac.ict.cana.models;

/**
 * Author: saukymo
 * Date: 9/12/16
 */
public class Permission{
    public final String permissionName;
    public Boolean permissionStatus;

    public Permission(String permission) {
        permissionName = permission;
        permissionStatus = true;
    }

}
