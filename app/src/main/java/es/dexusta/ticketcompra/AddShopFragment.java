package es.dexusta.ticketcompra;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import es.dexusta.ticketcompra.control.AddShopCallbacks;
import es.dexusta.ticketcompra.control.RegionAdapter;
import es.dexusta.ticketcompra.control.SubregionAdapter;
import es.dexusta.ticketcompra.control.TownAdapter;
import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.DataAccessCallbacks;
import es.dexusta.ticketcompra.dataaccess.DataSource;
import es.dexusta.ticketcompra.dataaccess.Keys;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.Region;
import es.dexusta.ticketcompra.model.Shop;
import es.dexusta.ticketcompra.model.Subregion;
import es.dexusta.ticketcompra.model.Town;

public class AddShopFragment extends Fragment {
    private static final String  TAG   = "AddShopFragment";
    private static final boolean DEBUG = true;

    private AddShopCallbacks     mCallbacks;
    private DataSource           mDS;

    private Spinner              mSpnRegion;
    private Spinner              mSpnSubregion;
    private Spinner              mSpnTown;

    private RegionAdapter        mRegionAdapter;
    private SubregionAdapter     mSubregionAdapter;
    private TownAdapter          mTownAdapter;

    private EditText             mEdtAddress;

    private Chain                mChain;

    public static AddShopFragment newInstance() {
        return new AddShopFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mChain = savedInstanceState.getParcelable(Keys.KEY_CHAIN);
        }
        View v = inflater.inflate(R.layout.add_shop_fragment, container, false);

        mDS = DataSource.getInstance(getActivity().getApplicationContext());

        mSpnRegion = (Spinner) v.findViewById(R.id.spn_region);
        mSpnSubregion = (Spinner) v.findViewById(R.id.spn_subregion);
        mSpnTown = (Spinner) v.findViewById(R.id.spn_town);

        mEdtAddress = (EditText) v.findViewById(R.id.edt_address);

        mSpnRegion.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Reach for the correspondent subregions.
                mDS.getSubregionBy(mRegionAdapter.getItem(position));
                if (DEBUG)
                    Log.d(TAG, "Reaching for subregions of: " + mRegionAdapter.getItemId(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        mSpnSubregion.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDS.getTownsBy(mSubregionAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        // Now I reach for regions and
        mDS.setRegionCallback(new DataAccessCallbacks<Region>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Region> results) {
                mRegionAdapter = new RegionAdapter(getActivity(), results);
                mSpnRegion.setAdapter(mRegionAdapter);
                mSpnRegion.setEnabled(true);
            }

            @Override
            public void onDataProcessed(int processed, List<Region> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }
        });

        mDS.setSubregionCallback(new DataAccessCallbacks<Subregion>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Subregion> results) {
                if (DEBUG) Log.d(TAG, "Subregions returned: " + results.size());
                mSubregionAdapter = new SubregionAdapter(getActivity(), results);
                mSpnSubregion.setAdapter(mSubregionAdapter);
                mSpnSubregion.setEnabled(true);
            }

            @Override
            public void onDataProcessed(int processed, List<Subregion> dataList,
                    Operation operation, boolean result) {
                // TODO Auto-generated method stub

            }
        });

        mDS.setTownCallback(new DataAccessCallbacks<Town>() {

            @Override
            public void onInfoReceived(Object result, Option option) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(List<Town> results) {
                mTownAdapter = new TownAdapter(getActivity(), results);
                mSpnTown.setAdapter(mTownAdapter);
                mSpnTown.setEnabled(true);
            }

            @Override
            public void onDataProcessed(int processed, List<Town> dataList, Operation operation,
                    boolean result) {
                // TODO Auto-generated method stub

            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof AddShopCallbacks) {
            mCallbacks = (AddShopCallbacks) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implemenet AddShopCallbacks");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        //mCallbacks.hideAcceptCancelActionBar();
        mCallbacks = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCallbacks.showAcceptCancelActionBar(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // Save data =
                // 1.- Generate shop object.
                // 2.- Do the callback.
                mChain = mCallbacks.getChain();
                Shop shop = new Shop();

                shop.setChainId(mChain.getId());

                Town town = (Town) mSpnTown.getItemAtPosition(mSpnTown.getSelectedItemPosition());
                shop.setTownName(town.getName());
                shop.setTownId(town.getId());

                shop.setAddress(mEdtAddress.getText().toString());

                if (DEBUG)
                    Log.d(TAG, "onClickAccept.");
                mCallbacks.onAcceptAddShop(shop);
            }
        }, new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (DEBUG)
                    Log.d(TAG, "onClickCancel.");
                // back to the previous fragment.

                // getFragmentManager().popBackStack();
                // getFragmentManager().popBackStackImmediate();
                mCallbacks.onCancelAddShop();
            }
        });
        mDS.listRegions();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Keys.KEY_CHAIN, mChain);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart.");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG)
            Log.d(TAG, "onResume.");
        mChain = mCallbacks.getChain();
        if (mChain == null) throw new AssertionError("Returned chain can't be null");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause.");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (BuildConfig.DEBUG)
            Log.d(TAG, "onStop.");
        mCallbacks.hideSoftKeyboard(mEdtAddress);
        mCallbacks.hideAcceptCancelActionBar();
    }
}
