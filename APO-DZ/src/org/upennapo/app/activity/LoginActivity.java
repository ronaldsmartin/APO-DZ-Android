package org.upennapo.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import org.upennapo.app.R;

public class LoginActivity extends Activity {

    public static final String LOGGED_IN_KEY = "USER_LOGGED_IN";
    public static final String USER_FIRSTNAME_KEY = "USER_FIRST_NAME";
    public static final String USER_LASTNAME_KEY = "USER_LAST_NAME";
    public static final String LOGOUT_INTENT = "USER_LOGOUT_INTENT";

    private static final String ALUM_LOGGED_IN = "ALUM_LOGGED_IN";
    private static final String ALUM_MODE_ENABLED = "ALUM_MODE_DEFAULT";

    /**
     * Check if Alumni Mode was unlocked to enable/disable menu options.
     *
     * @param context The context used to access SharedPreferences
     * @return Whether or not Alumni Mode is unlocked
     */
    public static boolean isAlumniModeDefault(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(context.getString(R.string.app_global_storage_key), MODE_PRIVATE);
        return prefs.getBoolean(ALUM_MODE_ENABLED, false);
    }

    /**
     * Set if Alumni Mode is the default activity.
     * @param context The context used to access SharedPreferences
     */
    public static void setAlumniModeDefault(Context context, boolean setAsDefault) {
        SharedPreferences.Editor editor = context
                .getSharedPreferences(context.getString(R.string.app_global_storage_key), MODE_PRIVATE)
                .edit();
        editor.putBoolean(ALUM_MODE_ENABLED, setAsDefault);
        editor.apply();
    }

    public static boolean isAlumLoggedIn(Context context) {
        final String storageKey = context.getString(R.string.app_global_storage_key);
        SharedPreferences prefs = context.getSharedPreferences(storageKey, MODE_PRIVATE);

        return prefs.getBoolean(ALUM_LOGGED_IN, false);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getIntent().getBooleanExtra(LOGOUT_INTENT, false)) {
            updateNameLabel();
            setLogoutButtonVisibility(true);
        } else if (isAlumLoggedIn()) {
            proceedToAlumApp();
        } else if (isBrotherLoggedIn() && isUsernameStored()) {
            proceedToActiveBrotherApp();
        }
    }

    public void logout(View view) {
        // Thanks to http://stackoverflow.com/questions/2115758/how-to-display-alert-dialog-in-android
        if (isBrotherLoggedIn() || isUsernameStored() || isAlumLoggedIn()) {
            // Confirm logout before proceeding.
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Logging out will clear all app data from your phone.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            SharedPreferences.Editor editor =
                                    getSharedPreferences(getString(R.string.app_global_storage_key), MODE_PRIVATE).edit();
                            editor.clear();
                            if (editor.commit()) {
                                resetNameLabel();
                                setLogoutButtonVisibility(false);

                                // Tell the user logout is complete.
                                Toast logoutNotification = Toast.makeText(LoginActivity.this, "Logout successful!", Toast.LENGTH_SHORT);
                                logoutNotification.show();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    /**
     * Opened when clicking login button in the LoginView
     *
     * @param view - the button in activity_login.xml
     */
    public void login(View view) {
        if (isAlumLoggedIn()) {
            proceedToAlumApp();
        } else if (isBrotherLoggedIn() && isUsernameStored()) {
            proceedToActiveBrotherApp();
        } else if (isBrotherLoggedIn()) {
            showNamePrompt();
        } else {
            showPasswordPrompt("Prove yourself");
        }
    }

    private void updateNameLabel() {
        // Change the prefix.
        TextView welcomeLabel = (TextView) findViewById(R.id.login_status);
        if (isUsernameStored()) {
            // Add the name.
            SharedPreferences prefs =
                    getSharedPreferences(getString(R.string.app_global_storage_key), MODE_PRIVATE);
            String firstName = prefs.getString(USER_FIRSTNAME_KEY, "");
            String lastName = prefs.getString(USER_LASTNAME_KEY, "");
            final String newText =
                    getString(R.string.login_status_logged_in) + " " + firstName + " " + lastName;
            welcomeLabel.setText(newText);
        } else if (isAlumLoggedIn()) {
            welcomeLabel.setText(R.string.login_status_logged_in_alum);
        } else {
            resetNameLabel();
        }
    }

    private void resetNameLabel() {
        TextView welcomeLabel = (TextView) findViewById(R.id.login_status);
        welcomeLabel.setText(R.string.login_status_logged_out);
    }

    private boolean isAlumLoggedIn() {
        return isAlumLoggedIn(this);
    }

    private boolean isBrotherLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_global_storage_key), MODE_PRIVATE);
        return prefs.getBoolean(LOGGED_IN_KEY, false);
    }

    private boolean isUsernameStored() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_global_storage_key), MODE_PRIVATE);
        return prefs.contains(USER_FIRSTNAME_KEY) && prefs.contains(USER_LASTNAME_KEY);
    }

    private void proceedToActiveBrotherApp() {
        Intent openAppIntent = new Intent(this, MainActivity.class);
        startActivity(openAppIntent);
        finish();
    }

    /**
     * Depending on the user's preferences, go either to
     * {@link org.upennapo.app.activity.AlumModeActivity Alumni Mode} or to
     * {@link org.upennapo.app.activity.MainActivity Active Brother Mode}.
     * The default is Alumni Mode.
     * <p/>
     * This setting is toggled in {@link org.upennapo.app.fragment.NavigationDrawerFragment} via
     * {@link #setAlumniModeDefault(android.content.Context, boolean) setAlumniModeDefault}
     */
    private void proceedToAlumApp() {
        if (isAlumniModeDefault(this)) {
            Intent openAppIntent = new Intent(this, AlumModeActivity.class);
            startActivity(openAppIntent);
            finish();
        } else {
            proceedToActiveBrotherApp();
        }
    }

    protected void showPasswordPrompt(String title) {
        // This code snippet is thanks to
        // http://www.androidsnippets.com/prompt-user-input-with-an-alertdialog

        AlertDialog.Builder prompt = new AlertDialog.Builder(this);
        prompt.setTitle(title);
        prompt.setMessage(R.string.dialog_enter_password_msg);

        // Create an EditText view to get user input that censors the input password.
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        prompt.setView(input);

        prompt.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Retrieve the user input.
                String value = input.getText().toString();

                SharedPreferences.Editor editor =
                        getSharedPreferences(getString(R.string.app_global_storage_key), MODE_PRIVATE).edit();

                // User entered password correctly.
                if (value.equals(getString(R.string.app_password))) {
                    // Store that we have logged in.
                    editor.putBoolean(LOGGED_IN_KEY, true);
                    editor.apply();

                    // Continue to the main view.
                    showNamePrompt();
                } else if (getString(R.string.app_password_alum).equals(value)) {
                    // Store that we have logged in as an alum.
                    editor.putBoolean(ALUM_MODE_ENABLED, true);
                    editor.putBoolean(ALUM_LOGGED_IN, true);
                    editor.apply();

                    // Continue to the main view.
                    proceedToAlumApp();
                } else {
                    // User entered incorrect password. Try again with new prompt.
                    showPasswordPrompt("Incorrect Password.");
                }
            }
        });

        prompt.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        prompt.show();
    }

    /**
     * Prompt the User for his/her name.
     */
    private void showNamePrompt() {
        FragmentManager fm = getFragmentManager();
        NamePromptDialogFragment namePrompt = new NamePromptDialogFragment();
        namePrompt.show(fm, "NamePromptDialogFragment");

        updateNameLabel();
    }

    /**
     * Show/hide the logout button.
     *
     * @param showButton Whether or not to show the logout button
     */
    private void setLogoutButtonVisibility(boolean showButton) {
        final int visibility = showButton ? View.VISIBLE : View.GONE;
        findViewById(R.id.logout_button).setVisibility(visibility);
    }

    public static class NamePromptDialogFragment extends DialogFragment implements OnEditorActionListener {

        private EditText firstNameField, lastNameField;

        /**
         *
         */
        public NamePromptDialogFragment() {
            // Auto-generated constructor stub
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.name_prompt_dialog_fragment, container);

            // Get references to the EditText fields in the dialog.
            this.firstNameField = (EditText) view.findViewById(R.id.name_prompt_dialog_first_name_entry);
            this.lastNameField = (EditText) view.findViewById(R.id.name_prompt_dialog_last_name_entry);

            // Set a listener so that pressing the enter button submits the text in the entry fields.
            this.lastNameField.setOnEditorActionListener(this);

            // Show soft keyboard automatically
            this.firstNameField.requestFocus();
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            View view = getActivity().getLayoutInflater().inflate(R.layout.name_prompt_dialog_fragment, null);
            final EditText firstNameEditText = (EditText) view.findViewById(R.id.name_prompt_dialog_first_name_entry),
                    lastNameEditText = (EditText) view.findViewById(R.id.name_prompt_dialog_last_name_entry);

            // Customize the window.
            alertDialogBuilder.setView(view);
            alertDialogBuilder.setTitle(R.string.name_entry_title);
            alertDialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Return input text to activity
                    final String firstName = firstNameEditText.getText().toString().trim(),
                            lastName = lastNameEditText.getText().toString().trim();

                    // Validate data, proceeding only if both fields are nonempty.
                    if (firstName.equals("") || lastName.equals("")) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Blank Name")
                                .setMessage("Please enter your first and last name exactly as it appears on the APO spreadsheet.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else {
                        // Store that we have logged in.
                        SharedPreferences.Editor editor = getActivity()
                                .getSharedPreferences(getString(R.string.app_global_storage_key), MODE_PRIVATE)
                                .edit();
                        editor.putString(LoginActivity.USER_FIRSTNAME_KEY, firstName);
                        editor.putString(LoginActivity.USER_LASTNAME_KEY, lastName);
                        editor.apply();

                        // Update the login status label.
                        LoginActivity rootActivity = (LoginActivity) getActivity();
                        rootActivity.updateNameLabel();

                        // Tell the user logout is complete.
                        Toast loginNotification = Toast.makeText(getActivity(), "Greetings, " + firstName + "!", Toast.LENGTH_SHORT);
                        loginNotification.show();

                        dialog.dismiss();
                    }
                }
            });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            return alertDialogBuilder.create();
        }


        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (EditorInfo.IME_ACTION_DONE == actionId) {
                dismiss();
                return true;
            }
            return false;
        }
    }
}
