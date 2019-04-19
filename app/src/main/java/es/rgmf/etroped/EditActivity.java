/*
 * Copyright (c) 2017-2019 Román Ginés Martínez Ferrández (rgmf@riseup.net)
 *
 * This file is part of Etroped (http://etroped.rgmf.es).
 *
 * Etroped is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Etroped is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Etroped.  If not, see <https://www.gnu.org/licenses/>.
 */

package es.rgmf.etroped;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import es.rgmf.etroped.adapter.SportsIconsListAdapter;
import es.rgmf.etroped.data.ActivityProvider;
import es.rgmf.etroped.data.entity.ActivityEntity;
import es.rgmf.etroped.util.EtropedPreferences;

/**
 *
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class EditActivity extends AppCompatActivity {

    private static final String TAG = EditActivity.class.getSimpleName();

    public static final String EXTRA_ACTIVITY_ID = "extra_activity_id";
    public static final String EXTRA_OK = "extra_ok";

    private ActivityEntity mActivity;

    private EditText etName;
    private EditText etComment;
    private ImageView ivSportIcon;
    private TextView tvSport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get extra data.
        Intent intent = getIntent();
        int activityId = intent.getIntExtra(EXTRA_ACTIVITY_ID, -1);

        // Get ActivityEntity.
        mActivity = ActivityProvider.getActivity(this, activityId);

        // Views.
        etName = (EditText) findViewById(R.id.et_name);
        etComment = (EditText) findViewById(R.id.et_comment);
        ivSportIcon = (ImageView) findViewById(R.id.iv_sport_icon);
        tvSport = (TextView) findViewById(R.id.tv_sport);

        // Set values.
        etName.setText(mActivity.getName());
        etComment.setText(mActivity.getComment());
        ivSportIcon.setImageResource(
                EtropedPreferences.getSportDrawableById(mActivity.getFkSport())
        );
        tvSport.setText(EtropedPreferences.getSportNameById(this, mActivity.getFkSport()));

        // When user clicks on sport icon it shows an Alert with a list options to choose sport to
        // recording.
        ivSportIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // The data and adapter.
                final Integer[] nameIds = new Integer[]{
                        R.string.sport_bike,
                        R.string.sport_running,
                        R.string.sport_walking
                };
                final Integer[] drawableIds = new Integer[] {
                        R.drawable.ic_directions_bike_black_48dp,
                        R.drawable.ic_directions_run_black_48dp,
                        R.drawable.ic_directions_walk_black_48dp
                };
                SportsIconsListAdapter adapter = new SportsIconsListAdapter(EditActivity.this,
                        nameIds, drawableIds);

                // The custom title layout.
                LayoutInflater inflater = EditActivity.this.getLayoutInflater();
                View titleLayout = inflater.inflate(R.layout.custom_title_dialog_layout, null);

                // Builds and shows alert dialog.
                // When it selects a sport it changes the selected sport.
                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                builder.setCustomTitle(titleLayout)
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ivSportIcon.setImageResource(drawableIds[i]);
                                tvSport.setText(nameIds[i]);
                            }
                        });
                builder.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            int numOfUpdates = ActivityProvider.updateActivitySport(
                    this,
                    mActivity.getId(),
                    etName.getText().toString(),
                    etComment.getText().toString(),
                    EtropedPreferences.getSportIdFromNameInPreferences(
                            this, tvSport.getText().toString()));

            Intent returnIntent = new Intent();

            if (numOfUpdates > 0) {
                setResult(Activity.RESULT_OK, returnIntent);

                Toast.makeText(
                        this,
                        getString(R.string.toast_activity_updated),
                        Toast.LENGTH_LONG).show();
            }
            else {
                setResult(Activity.RESULT_CANCELED, returnIntent);

                Toast.makeText(
                        this,
                        getString(R.string.toast_activity_not_updated),
                        Toast.LENGTH_LONG).show();
            }

            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
