package cn.ac.ict.cana.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import cn.ac.ict.cana.R
import kotlinx.android.synthetic.main.input_dialog.*

/**
 * Created by zhaoliang on 2017/9/11.
 */
class InputDialog(context: Context?, var titleMessage: String, var cInputType: Int, val conotentHint: String) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.input_dialog)
        tv_title.text = titleMessage
        et_content.inputType = cInputType
        et_content.hint = conotentHint
        et_content.setLines(4)
        et_content.setHorizontallyScrolling(false)
        btn_ok.setOnClickListener {
            val text = et_content.text.toString()
            onInputContentChangeListener.onContentChange(text)
            dismiss()
        }
        btn_cancel.setOnClickListener {
            dismiss()
        }
    }


    lateinit var onInputContentChangeListener: OnInputContentChangeListener

    interface OnInputContentChangeListener {
        fun onContentChange(text: String)
    }
}