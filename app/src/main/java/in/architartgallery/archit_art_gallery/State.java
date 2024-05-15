package in.architartgallery.archit_art_gallery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class State extends AppCompatActivity {

    private ImageView back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);

        back_button = findViewById(R.id.back_button);

        back_button.setOnClickListener(e -> {
            onBackPressed();
        });
    }
}