package com.ashraf.rxformvalidation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;

public class LoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mEmailSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mProgressView = findViewById(R.id.login_progress);
        mLoginFormView = findViewById(R.id.login_form);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        RxView.clicks(mEmailSignInButton)
                .doOnNext(object -> Toast.makeText(this, "SignIn call should be called", Toast.LENGTH_SHORT).show()).subscribe();

        //region observables
        Observable<Boolean> emailObservable = RxTextView.textChanges(mEmailView)
                .map(charSequence -> isEmailValid(charSequence.toString()))
                .doOnNext(aBoolean -> {
                    if (aBoolean)
                        mEmailView.setTextColor(getResources().getColor(R.color.colorGreen));
                    else
                        mEmailView.setTextColor(getResources().getColor(R.color.colorRed));
                });

        Observable<Boolean> passwordbservable = RxTextView.textChanges(mPasswordView)
                .map(charSequence -> isPasswordValid(charSequence.toString()))
                .doOnNext(aBoolean -> {
                    if (aBoolean)
                        mPasswordView.setTextColor(getResources().getColor(R.color.colorGreen));
                    else
                        mPasswordView.setTextColor(getResources().getColor(R.color.colorRed));
                });
        /**
         * combine latest will aggregates the latest values of each of the
         * source ObservableSources each time an item is received from either of the source ObservableSources, where this
         * aggregation is defined by a specified function.
         */
        Observable.combineLatest(emailObservable, passwordbservable, (b1, b2) -> {
                    if (b1 && b2)
                        mEmailSignInButton.setEnabled(true);
                    else
                        mEmailSignInButton.setEnabled(false);
                    return "success";
                }
        ).subscribe();
        //endregion

    }

    //region animations
    private void hideBtn() {

        // get the center for the clipping circle
        int cx = mEmailSignInButton.getWidth() / 2;
        int cy = mEmailSignInButton.getHeight() / 2;

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

        // create the animation (the final radius is zero)
        Animator anim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim =
                    ViewAnimationUtils.createCircularReveal(mEmailSignInButton, cx, cy, initialRadius, 0);
        }
        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mEmailSignInButton.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();

    }

    private void visibleBtn() {
        // previously invisible view;

        // get the center for the clipping circle
        int cx = mEmailSignInButton.getWidth() / 2;
        int cy = mEmailSignInButton.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(mEmailSignInButton, cx, cy, 0, finalRadius);
        }

        // make the view visible and start the animation
        mEmailSignInButton.setVisibility(View.VISIBLE);
        anim.start();
    }
    //endregion

    //region validation
    private boolean isEmailValid(String email) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    private boolean isPasswordValid(String password) {

        return password.length() >= 6;
    }
    //endregion

    //region helper methods
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    //endregion
}

