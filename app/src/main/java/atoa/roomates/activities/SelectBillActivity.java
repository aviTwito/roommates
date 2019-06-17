package atoa.roomates.activities;
/**
 * Copyright 2016 Avi twito,Or Am-Amshalem
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import atoa.roomates.R;


/**
 * class to represents the different types of bills
 * when the user wish to add a new bill he has to choose the bill type from an ENUM class which represents the bill types
 */
public class SelectBillActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnInternet,btnGas,btnTv,btnArnona,
    btnWater,btnElectricity,btnOther;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        setContentView(R.layout.select_bill);
        setButtons();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().hide();
    }


    /**
     * called when the activity is created
     * initializing the activity button
     */
    private void setButtons() {
        btnInternet = (Button)findViewById(R.id.btnInternet);
        btnInternet.setOnClickListener(this);
        btnGas = (Button)findViewById(R.id.btnGas);
        btnGas.setOnClickListener(this);
        btnTv = (Button)findViewById(R.id.btnTv);
        btnTv.setOnClickListener(this);
        btnArnona = (Button)findViewById(R.id.btnArnona);
        btnArnona.setOnClickListener(this);
        btnWater = (Button)findViewById(R.id.btnWater);
        btnWater.setOnClickListener(this);
        btnElectricity = (Button)findViewById(R.id.btnElctricity);
        btnElectricity.setOnClickListener(this);
        btnOther = (Button)findViewById(R.id.btnOther);
        btnOther.setOnClickListener(this);
    }

    /**
     * called when the one of the bills button is pressed
     * starts 'addNewBill' activity. the selected bill button is passed with a tradition to the 'addNewBill' activity
     * @param v is one of the bill buttons the user has pressed
     */
    @Override
    public void onClick(View v) {
        String transitionName = getString(R.string.btnTransmistion);
        ActivityOptions transitionActivityOptions;
        Intent i = new Intent(SelectBillActivity.this, AddBillActivity.class);
        View sharedView;
        switch (v.getId()){
            case R.id.btnArnona:
                sharedView = btnArnona;
                i.putExtra("id" , v.getId());
                transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(SelectBillActivity.this, sharedView, transitionName);
                startActivity(i, transitionActivityOptions.toBundle());
                break;
            case R.id.btnOther:
                sharedView = btnOther;
                i.putExtra("id" , v.getId());
                transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(SelectBillActivity.this, sharedView, transitionName);
                startActivity(i, transitionActivityOptions.toBundle());
                break;
            case R.id.btnGas:
                sharedView = btnGas;
                i.putExtra("id" , v.getId());
                transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(SelectBillActivity.this, sharedView, transitionName);
                startActivity(i, transitionActivityOptions.toBundle());
                break;
            case R.id.btnInternet:
                sharedView = btnInternet;
                i.putExtra("id" , v.getId());
                transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(SelectBillActivity.this, sharedView, transitionName);
                startActivity(i, transitionActivityOptions.toBundle());
                break;
            case R.id.btnTv:
                sharedView = btnTv;
                i.putExtra("id" , v.getId());
                transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(SelectBillActivity.this, sharedView, transitionName);
                startActivity(i, transitionActivityOptions.toBundle());
                break;
            case R.id.btnWater:
                sharedView = btnWater;
                i.putExtra("id" , v.getId());
                transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(SelectBillActivity.this, sharedView, transitionName);
                startActivity(i, transitionActivityOptions.toBundle());
                break;
            case R.id.btnElctricity:
                sharedView = btnElectricity;
                i.putExtra("id" , v.getId());
                transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(SelectBillActivity.this, sharedView, transitionName);
                startActivity(i, transitionActivityOptions.toBundle());
                break;
        }
    }
}
