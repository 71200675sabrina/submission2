package com.example.storyapp.customview

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.graphics.Canvas
import android.text.Editable
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.example.storyapp.R


class CustomViewEmail : AppCompatEditText {
    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet){
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int): super(
        context,
        attributeSet,
        defStyleAttr
    ){
        init()
    }

    private fun init(){
        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s!!).matches()){
                    error = context.getString(R.string.email_error)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })



    }

    override fun onDraw(canvas: Canvas?){
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}