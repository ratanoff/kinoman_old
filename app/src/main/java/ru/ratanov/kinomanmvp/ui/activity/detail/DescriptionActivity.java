package ru.ratanov.kinomanmvp.ui.activity.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.ratanov.kinomanmvp.R;

public class DescriptionActivity extends AppCompatActivity {

    public static final String EXTRA_DESCRIPTION = "extra_description";

    @BindView(R.id.description_activity_textview)
    TextView mTextView;

    public static Intent newIntent(Context context, String description) {
        Intent intent = new Intent(context, DescriptionActivity.class);
        intent.putExtra(EXTRA_DESCRIPTION, description);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity_description);
        ButterKnife.bind(this);

        mTextView.setText(getIntent().getStringExtra(EXTRA_DESCRIPTION));
    }

}
