package com.straiberry.android.common.custom.edittext

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.straiberry.android.common.R
import com.straiberry.android.common.extensions.dp
import com.straiberry.android.common.extensions.gone
import com.straiberry.android.common.extensions.onClick
import com.straiberry.android.common.extensions.visible
import com.straiberry.android.common.helper.VibratorHelper

class StraiberryEditText @JvmOverloads constructor(
    context: Context,
    private val attributes: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attributes, defStyleAttr) {
    private val textInputLayout = TextInputLayout(
        context,
        attributes,
        com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox
    )
    private val textInputEditText = TextInputEditText(context)
    private val linearLayout = LinearLayout(context)
    private val imageButtonTogglePassword = ImageButton(context)
    private var isPasswordToggleEnabled = false
    private var androidHintText = ""
    private val _focusChange = MutableLiveData<Boolean>()
    val focusChange: LiveData<Boolean> = _focusChange

    init {
        // Set default attributes
        isInEditMode
        cardElevation = CardElevationDefault
        radius = Radius

        // Set frame layout
        LinearLayout.LayoutParams(context, attributes).apply {
            // Set background
            background =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.custom_edit_text_not_focused
                )

            height = LayoutParams.MATCH_PARENT
            width = LayoutParams.MATCH_PARENT
        }

        addView(linearLayout)
        linearLayout.apply {
            layoutParams.width = LayoutParams.MATCH_PARENT
            layoutParams.height = LayoutParams.MATCH_PARENT
            gravity = Gravity.CENTER_VERTICAL
        }

        linearLayout.addView(textInputLayout)
        // Setup image button toggle password
        imageButtonTogglePassword.apply {
            background = null
            layoutParams = LinearLayout.LayoutParams(
                imageButtonPasswordVisibilitySize,
                imageButtonPasswordVisibilitySize
            ).apply {
                rightMargin = imageButtonPasswordVisibilityMargin
                leftMargin = imageButtonPasswordVisibilityMargin
            }
            setImageResource(R.drawable.ic_eye_close)
            gone()
            // Change the visibility of password
            onClick {
                if (isPasswordVisible) {
                    textInputEditText.apply {
                        transformationMethod = PasswordTransformationMethod.getInstance()
                        setSelection(this.text.toString().length)
                    }
                    setImageResource(R.drawable.ic_eye_close)
                    isPasswordVisible = false
                } else {
                    textInputEditText.apply {
                        transformationMethod = null
                        setSelection(this.text.toString().length)
                    }
                    setImageResource(R.drawable.ic_eye_open)
                    isPasswordVisible = true
                }
            }
        }
        linearLayout.addView(imageButtonTogglePassword)
        // Setup input layout
        textInputLayout.apply {
            layoutParams = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, EditTextWeight
            ).apply {
                textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            }
            id = id.plus(5)
            setPadding(0, 0, 0, PaddingBottom)
            gravity = Gravity.CENTER_VERTICAL or Gravity.START
            boxBackgroundColor = Color.TRANSPARENT
            boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
        }
        // Setup edit text
        textInputEditText.apply {
            setBackgroundColor(Color.TRANSPARENT)
            setPadding(PaddingLeft, 0, PaddingRight, 0)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, TextSize)
            width = LayoutParams.MATCH_PARENT
            height = LayoutParams.MATCH_PARENT
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            setTextColor(ContextCompat.getColor(context,R.color.editTextTextColor))
            setHintTextColor(ContextCompat.getColor(context,R.color.gray200))
            maxLines = DefaultMaxLine
            inputType = InputType.TYPE_CLASS_TEXT
            // Set text appearance
            if (Build.VERSION.SDK_INT < 23)
                setTextAppearance(context, R.style.TextAppearance_EditText)
            else
                setTextAppearance(R.style.TextAppearance_EditText)
            imeOptions = EditorInfo.IME_ACTION_DONE
        }
        textInputLayout.addView(textInputEditText)
        /** When focus change's then change background and add shadow */
        textInputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // If is in code mode then change border and text color to gray
                if (isCode) {
                    textInputEditText.setTextColor(ContextCompat.getColor(context, R.color.gray500))
                    linearLayout.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_enter_code_focused
                    )
                }else
                linearLayout.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.custom_edit_text_focused
                )
                _focusChange.value = true
                cardElevation = CardElevationFocus
            } else {
                if (isCode)
                    if (textInputEditText.text.toString()=="") {
                        linearLayout.background = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_enter_code_background
                        )
                    }else
                        linearLayout.background = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_enter_code_not_focused
                        )
                else {
                    linearLayout.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.custom_edit_text_not_focused
                    )
                 }
                if (isCode)
                    if (Build.VERSION.SDK_INT < 23)
                        textInputEditText.setTextAppearance(context, R.style.TextAppearance_EditText_Code)
                    else
                        textInputEditText.setTextAppearance(R.style.TextAppearance_EditText_Code)
                _focusChange.value = false
                cardElevation = CardElevationDefault
            }
        }
        setupAttribute()
    }

    private fun setupAttribute(){
        // Toggle password
        val straiberryEditText: TypedArray = context.obtainStyledAttributes(
            attributes,
            R.styleable.StraiberryEditText
        )
        isPasswordToggleEnabled = straiberryEditText.getBoolean(
            R.styleable.StraiberryEditText_passwordToggleEnabled,
            false
        )
        if (isPasswordToggleEnabled) {
            imageButtonTogglePassword.visible()
            textInputEditText.apply {
                imeOptions = EditorInfo.IME_FLAG_FORCE_ASCII
                transformationMethod = PasswordTransformationMethod.getInstance()
            }
            textInputLayout.apply {
                endIconDrawable=null
                endIconMode = TextInputLayout.END_ICON_NONE
            }
        }
        // Hint text
        if (straiberryEditText.getString(R.styleable.StraiberryEditText_hintText) != null)
            androidHintText = straiberryEditText.getString(R.styleable.StraiberryEditText_hintText)!!
        if (androidHintText != "")
            textInputEditText.apply { hint = androidHintText }

        // Max Line
        if (straiberryEditText.getInt(R.styleable.StraiberryEditText_maxLine, 0) > 1)
            textInputEditText.apply {
                maxLines = straiberryEditText.getInt(R.styleable.StraiberryEditText_maxLine, 0)
            }

        // Max Length
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] =
            InputFilter.LengthFilter(straiberryEditText.getInt(R.styleable.StraiberryEditText_maxLength, 0))
        if (straiberryEditText.getInt(R.styleable.StraiberryEditText_maxLength, 0) > 0)
            textInputEditText.apply { filters = filterArray }

        // Text Alignment
        if (straiberryEditText.getBoolean(R.styleable.StraiberryEditText_isTextAlignmentCenter, false))
            textInputEditText.apply {
                setPadding(0, 0, 0, 0)
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                gravity = Gravity.CENTER }

        // Input Type
        if (straiberryEditText.getBoolean(R.styleable.StraiberryEditText_isNumber, false))
            textInputEditText.apply { inputType = InputType.TYPE_CLASS_NUMBER }
        isCode = straiberryEditText.getBoolean(R.styleable.StraiberryEditText_isCode,false)

        straiberryEditText.recycle()
    }

    /**
     * Getting the edit text that we have in our card view
     */
    fun getEditText(): TextInputEditText = textInputEditText

    /**
     * Getting the text from edit text
     */
    fun getText() = textInputEditText.text.toString().trim()

    /**
     * Shake and vibrate when input is incorrect
     */
    fun shake(activity: Activity) {
        this.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                R.anim.anim_shake_custom_edit_text
            )
        )
        VibratorHelper().vibrateForEditText(activity)
    }
    /**
     * Make border of edit text gray when its not focused
     */
    fun editTextNotSelected() {
        background = ContextCompat.getDrawable(
            context,
            R.drawable.ic_enter_code_background
        )
    }


    companion object {
        private val Radius = 35F.dp
        private const val EditTextWeight= 1.0f
        private val CardElevationDefault = 0F.dp
        private val CardElevationFocus = 12F.dp
        private val PaddingLeft = 30.dp
        private val PaddingRight = 30.dp
        private val PaddingBottom = 8.dp
        private const val TextSize = 14f
        private val imageButtonPasswordVisibilitySize = 24.dp
        private val imageButtonPasswordVisibilityMargin = 30.dp
        private var isPasswordVisible = false
        private var isCode=false
        private const val DefaultMaxLine= 1
    }
}
