package com.lzm.svt.twparking.modules.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.lzm.svt.twparking.MainActivity
import com.lzm.svt.twparking.NetworkQueue
import com.lzm.svt.twparking.R
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.util.HashMap

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE)
                ?: return
        val token = sharedPref.getString(getString(R.string.pref_token_key), "NA")

        if (token != "NA") {
            goToMain()
        }
        
        setContentView(R.layout.activity_login)

        // Set up the login form.
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener { attemptLogin() }
    }

    private fun attemptLogin() {
        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            makePostRequest(emailStr, passwordStr)

//            mAuthTask = UserLoginTask(this, emailStr, passwordStr)
//            mAuthTask!!.execute(null as Void?)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        //TODO: Replace this with your own logic
        return password.length > 4
    }

    private fun makePostRequest(mEmail: String, mPassword: String) {
        val networkQueue = NetworkQueue.getInstance(this)
        val url = "https://twparking-staging.herokuapp.com/api/"
        val path = "People/login"

        val activity = this

        val requestParams = HashMap<String, String>()
        requestParams["email"] = mEmail
        requestParams["password"] = mPassword


        val postRequest = object : JsonObjectRequest(Method.POST, url + path, null,
                Response.Listener { response ->
                    showProgress(false)
                    val token = response.get("id")
                    val userId = response.get("userId")

                    val sharedPref = activity.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString(getString(R.string.pref_token_key), token as String)
                        putInt(getString(R.string.pref_userId_key), userId as Int)
                        apply()
                    }

                    goToMain()
                },
                Response.ErrorListener {
                    showProgress(false)
                    println("----------------------------------------------------------------")
                    println("ERROR!!!")
                    println("----------------------------------------------------------------")
                    password.error = getString(R.string.error_incorrect_password)
                    password.requestFocus()
                }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Accept"] = "application/json"
                return headers
            }

            override fun getBody(): ByteArray {
                return JSONObject(requestParams).toString().toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        networkQueue.addToRequestQueue(postRequest)
    }

    private fun goToMain() {
        val intent = MainActivity.newIntent(this)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }
}
