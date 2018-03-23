package prabhu.company.echo18beta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class FeedbakActivity extends AppCompatActivity {

    int count = 0;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbak);
        textView=findViewById(R.id.question2);
        if (count == 0) {
            textView.setText("Rate your signal");
            RadioGroup group = new RadioGroup(this);
            RadioButton[] rb = new RadioButton[4];

            rb[0] = new RadioButton(this);
            rb[0].setText("Exellent");
            rb[0].setId(0 + 100);
            group.addView(rb[0]);

            rb[1] = new RadioButton(this);
            rb[1].setText("Good");
            rb[1].setId(1 + 100);
            group.addView(rb[1]);

            rb[2] = new RadioButton(this);
            rb[2].setText("Average");
            rb[2].setId(2 + 100);
            group.addView(rb[2]);

            rb[3] = new RadioButton(this);
            rb[3].setText("Poor");
            rb[3].setId(3 + 100);
            group.addView(rb[3]);
        }
    }
}
