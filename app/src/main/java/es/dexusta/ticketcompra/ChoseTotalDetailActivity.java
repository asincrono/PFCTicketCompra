package es.dexusta.ticketcompra;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.model.Shop;

public class ChoseTotalDetailActivity extends Activity {
    private static final String TAG = "ChoseTotalDetailActivity";
    private static final boolean DEBUG = true;
    
    private Shop mShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    
        super.onCreate(savedInstanceState);
        mShop = getIntent().getParcelableExtra(Keys.KEY_SHOP);        
    }
    
    public void onClickChoseTotal(View v) {
        // Start add total activity.
        Intent i = new Intent(this, AddTotalActivity.class);
        i.putExtra(Keys.KEY_SHOP, mShop);
//        i.setFlags(flags);
        startActivity(i);
        finish();
    }
    
    public void onClickChoseDetail(View v) {
        // Start new add detail activity.
        Intent i = new Intent(this, AddDetailActivity.class);
        i.putExtra(Keys.KEY_SHOP, mShop);
        startActivity(i);
        finish();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    

}
