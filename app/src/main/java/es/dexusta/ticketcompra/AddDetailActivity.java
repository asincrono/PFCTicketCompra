package es.dexusta.ticketcompra;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.cloud.backend.android.CloudBackendActivity;
import es.dexusta.ticketcompra.R;

import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.model.Detail;
import es.dexusta.ticketcompra.model.Product;

public class AddDetailActivity extends CloudBackendActivity implements OnItemSelectedListener{
    private static final String  TAG   = "AddDetailActivity";
    private static final boolean DEBUG = true;

    private TextView             mTvProductName;
    private EditText             mEdtPrice;
    private EditText             mEdtUnits;
    // to use as weight or volume.
    private EditText             mEdtSecondUnit;

    private Spinner              mSpinner;

    private Product              mProduct;

    private ArrayList<Detail>    mDetails;
    
    enum UnitType {WEIGHT, VOLUME};
    private UnitType mUnitType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_detail_fragment);

        mProduct = getIntent().getParcelableExtra(Keys.KEY_PRODUCT);

        // Get the temporal list of details.
        mDetails = getIntent().getExtras().getParcelableArrayList(Keys.KEY_DETAIL_LIST);

        mTvProductName = (TextView) findViewById(R.id.tv_lbl_product_name);
        mEdtPrice = (EditText) findViewById(R.id.edt_price);
        mEdtUnits = (EditText) findViewById(R.id.edt_units);
        mEdtSecondUnit = (EditText) findViewById(R.id.edt_second_unit);
        mSpinner = (Spinner) findViewById(R.id.spn_second_unit);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.chose_weight_volume, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        mSpinner.setAdapter(adapter);
    }

    public void onClickAccept(View v) {
        // Validate detail
        Detail detail = validate();
        if (detail != null) {                        
            // Add the new detail to the details list.
            mDetails.add(detail);
            // Go back to receipt info (detail list plus total).
            Intent i = new Intent();
        }
        else {
            
        }
    }

    public void onClickCancel(View v) {
        // Go back to receipt info (detail list plus total).
    }

    private Detail validate() {
        Detail detail = null;
        return detail;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            mUnitType = UnitType.WEIGHT;
        }
        else {
            mUnitType = UnitType.VOLUME;
        }        
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mUnitType = UnitType.WEIGHT;        
    }

}
