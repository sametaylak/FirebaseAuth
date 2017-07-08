package com.gelecegiyazankadinlar.firebaseauth;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Kullanıcı var yani giriş yapmış
                    Log.d("AuthState", "onAuthStateChanged: Giriş yapıldı " + user.getUid());
                } else {
                    // Kullanıcı yok yani çıkış yapmış
                    Log.d("AuthState", "onAuthStateChanged: Çıkış yapıldı");
                }
            }
        };

        auth.addAuthStateListener(authStateListener);

        final EditText email = (EditText) findViewById(R.id.email);
        final EditText parola = (EditText) findViewById(R.id.parola);

        Button kayit = (Button) findViewById(R.id.kayit);

        kayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = email.getText().toString();
                String parolaText = parola.getText().toString();

                auth.createUserWithEmailAndPassword(emailText, parolaText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Hesabınız oluşturuldu.
                            Toast.makeText(MainActivity.this, "Hesabınız oluşturuldu", Toast.LENGTH_SHORT).show();
                            DatabaseReference kullanicilar = FirebaseDatabase.getInstance().getReference("kullanicilar");
                            kullanicilar.child(task.getResult().getUser().getUid()).setValue(new Kullanici("usernamee", "biom"));
                        } else {
                            // Hesabınız oluşturulurken bir hata meydana geldi.
                            Toast.makeText(MainActivity.this, "Hesap oluşturulurken bir hata meydana geldi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        Button giris = (Button) findViewById(R.id.giris);
        giris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = email.getText().toString();
                String parolaText = parola.getText().toString();

                auth.signInWithEmailAndPassword(emailText, parolaText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Giriş başarılı!", Toast.LENGTH_SHORT).show();
                            DatabaseReference kullanicilar = FirebaseDatabase.getInstance().getReference("kullanicilar");
                            kullanicilar.child(task.getResult().getUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                                    Toast.makeText(MainActivity.this, kullanici.getUsername(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "Giriş başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        Button sifremiUnuttum = (Button) findViewById(R.id.sifremiunuttum);
        sifremiUnuttum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.sendPasswordResetEmail("sametaylak29@gmail.com").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Mail gönderildi!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Mail gönderilemedi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    static class Kullanici {
        private String username;
        private String bio;

        Kullanici(String kullaniciadi, String bio) {
            this.username = kullaniciadi;
            this.bio = bio;
        }

        Kullanici() {}

        public String getUsername() {
            return username;
        }

        public void setUsername(String kullaniciadi) {
            this.username = kullaniciadi;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }
    }
}
