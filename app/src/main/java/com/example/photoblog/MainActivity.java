package com.example.photoblog;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.photoblog.Fragment.AccountFragment;
import com.example.photoblog.Fragment.HomeFragment;
import com.example.photoblog.Fragment.NotificationFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class  MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationViewId;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

       if(firebaseAuth.getCurrentUser() != null) {

           //아래 탭바
           bottomNavigationViewId = findViewById(R.id.bottomNavigationViewId);

           bottomNavigationViewId.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
           replaceFragment(new HomeFragment());

       }
    }

        private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeNavId:
                        replaceFragment(new HomeFragment());
                        return true;
                    case R.id.notificationNavId:
                        replaceFragment(new NotificationFragment());
                        return true;
                    case R.id.accountNavId:
                        replaceFragment(new AccountFragment());
                        return true;
                }
                return false;
            }
        };

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.i("Main","main");
        //로그인 안돼있으면 로그인 창으로 이동
        if(currentUser == null){
          sendToLogin();
        }else {

            //로그인 돼있는데 setup 안돼있으면 setupactivity로 이동
            currentUserId = firebaseAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(!task.getResult().exists()){
                            Intent intent = new Intent(MainActivity.this,SetupActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }else{
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //오른쪽 위 메뉴
        getMenuInflater().inflate(R.menu.menu_item_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.searchId:
                return true;
            case R.id.accountSetting:
                Intent intent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(intent);
                return true;
            case R.id.signOut:
                signOut();
                return true;
        }
        return false;
    }

    private void signOut() {
        firebaseAuth.signOut();
        finish();
        sendToLogin();

    }

    private void sendToLogin() {
        Intent intent = new Intent(MainActivity.this,SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutId,fragment);
        fragmentTransaction.commit();

    }
}
