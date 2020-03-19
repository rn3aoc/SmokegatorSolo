package wildfire.volunteers.smokegator.solo.data;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import wildfire.volunteers.smokegator.solo.R;

public class ImportSocial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_social);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

    }
}
