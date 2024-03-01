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
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        emailEditText=findViewById(R.id.Email_edit_text);
        passwordEditText=findViewById(R.id.password_edit_text);
        confirmPasswordEditText=findViewById(R.id.comfirm_password_edit_text);
        createAccountBtn =findViewById(R.id.create_account_btn);
        progressBar =findViewById(R.id.progress_bar);
        loginBtnTextView=findViewById(R.id.login_text_view_btn);
        createAccountBtn.setOnClickListener(v->CreateAccount());
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
                            Utility.showToast(CreateAccountActivity.this,"Tạo tài khoản thành công, Kiểm tra email để xác minh");

                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }else {
                            //failure
                            Utility.showToast(CreateAccountActivity.this,task.getException().getLocalizedMessage());



                        }
                    }
                }
        );

    }
    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }

    }
    boolean validateData(String email,String password,String confirmPassword){
        //validate the data are input by user.
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email không hợp lệ");
            return false;
        }
        if (password.length()<6){
            passwordEditText.setError("Password phải nhiều hơn 6 ký tự");
            return false;
        }
        if (!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("Password không giống nhau");
            return false;
        }
        return true;
    }
}