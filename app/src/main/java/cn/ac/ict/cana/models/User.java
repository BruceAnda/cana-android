package cn.ac.ict.cana.models;

import static java.util.UUID.randomUUID;

/**
 * Author: saukymo
 * Date: 9/13/16
 */
public class User {
    public long id;
    public final String uuid;
    public final String name;
    public final int age;
    public final boolean gender;
    public  String clinicalNumber = null;
    public  String studyNumber = null;
    public  String identification = null;

    public User(long userId, String uuid, String userName, int userAge, boolean userGender) {
        id = userId;
        name = userName;
        age = userAge;
        gender = userGender;
        this.uuid = uuid;
    }

    public User(String userName, int userAge, boolean userGender) {
        id = 0;
        name = userName;
        age = userAge;
        gender = userGender;
        uuid = randomUUID().toString();
    }
    public User(String userName, int userAge, boolean userGender, String clinicalNumber, String studyNumber, String identification)
    {
        id = 0;
        name = userName;
        age = userAge;
        gender = userGender;
        this.clinicalNumber = clinicalNumber;
        this.studyNumber  = studyNumber;
        this.identification = identification;
        uuid = randomUUID().toString();
    }
}
