package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {
    EditText emailEditText,passwordEditText,confirmPasswordEditText;
    Button createAcccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        emailEditText=findViewById(R.id.Email_edit_text);
        passwordEditText=findViewById(R.id.password_edit_text);
        confirmPasswordEditText=findViewById(R.id.comfirm_password_edit_text);
        createAcccountBtn =findViewById(R.id.create_account_btn);
        progressBar =findViewById(R.id.progress_bar);
        loginBtnTextView=findViewById(R.id.login_text_view_btn);
        createAcccountBtn.setOnClickListener(v->CreateAccount());
        loginBtnTextView.setOnClickListener(v->finish());
    }
    void CreateAccount(){
        String email =emailEditText.getText().toString();
        String password=passwordEditText.getText().toString();
        String confirmPassword=confirmPasswordEditText.getText().toString();

        boolean isValidated = validateData(email,password,confirmPassword);
        if(!isValidated){
            return;
        }
        createAccountInFireBase(email,password,confirmPassword);

    }
    void createAccountInFireBase(String email,String password,String confirmPassword){
        changeInProgress(true);
        FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        changeInProgress(false);
                        if(task.isSuccessful()){
                            // tạo acc thành công
                            Toast.makeText(CreateAccountActivity.this,"Succesfully create account,Check email to verify",Toast.LENGTH_SHORT).show();
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }else {
                            //failure
                            Toast.makeText(CreateAccountActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();


                        }
                    }
                }
        );

    }
    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAcccountBtn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            createAcccountBtn.setVisibility(View.VISIBLE);
        }

    }
    boolean validateData(String email,String password,String confirmPassword){
        //validate the data are input by user.
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("email is invalid");
            return false;
        }
        if (password.length()<6){
            passwordEditText.setError("Password length is invalid");
            return false;
        }
        if (!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("Password not matched");
            return false;
        }
        return true;
    }
}