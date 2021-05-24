package com.example.inspboard.activities

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.inspboard.R
import kotlinx.android.synthetic.main.fragment_register_namepass.*

class NamePassFragment : Fragment() {
    private lateinit var mListener : Listener
    interface Listener {
        fun onRegister(name: String, password: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_register_namepass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enableButtonIfAllTextsNonEmpty(button_register, edit_text_name, edit_text_password)
        edit_text_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                val name = edit_text_name.text.toString()
                if (name.isEmpty()) {
                    button_register.text = "continue"
                } else {
                    val nameContinue = toName(name)
                    button_register.text = "continue as $nameContinue"
                }
            }
        })

        button_register.setOnClickListener {
            val name = edit_text_name.text.toString()
            val password = edit_text_password.text.toString()
            mListener.onRegister(name, password)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
    }
}