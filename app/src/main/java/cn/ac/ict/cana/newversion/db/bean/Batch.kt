package cn.ac.ict.cana.newversion.db.bean

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

@SuppressLint("ParcelCreator")
/**
 * 数据批次表
 * Created by zhaoliang on 2017/9/7.
 */
class Batch(var patientName: String, var time: Long, var batch: String) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readLong(),
            parcel.readString())


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(patientName)
        parcel.writeLong(time)
        parcel.writeString(batch)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Batch> {
        val TABLE_NAME = "Batch"

        val ID = "Id"
        val TIME = "Time"
        val BATCH = "Batch"

        // 病人信息
        val PATIENT_NAME = "PatientInfo"
        val PATIENT_SEX = "Sex"
        val PATIENT_AGE = "Age"
        val PATIENT_MEDICINE = "Medicine"
        val PATIENT_OPEN = "Open"

        override fun createFromParcel(parcel: Parcel): Batch {
            return Batch(parcel)
        }

        override fun newArray(size: Int): Array<Batch?> {
            return arrayOfNulls(size)
        }
    }
}