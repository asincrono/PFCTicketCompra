package es.dexusta.ticketcompra;

import android.app.Fragment;
import android.os.Bundle;

import java.util.HashMap;

/**
 * StateFragment is a fragment that should be used to hold Activity state between configuration changes.
 * Data is stored in a HashMap where Strings are used as keys. 
 * @author asincrono
 *
 */
public final class StateFragment extends Fragment {
    private HashMap<String, Object> mContents = new HashMap<String, Object>();

    public static StateFragment newInstance(HashMap<String, Object> contents) {
        StateFragment fragment = new StateFragment();
        fragment.mContents = contents;
        return fragment;
    }

    public static StateFragment newInstance(String [] keys, Object[] values) {
        if (keys.length != values.length) {
            throw new IllegalArgumentException("keys and values arrays should have the same length");
        } else {
            StateFragment fragment = new StateFragment();

            int length = keys.length;
            HashMap<String, Object> map = new HashMap<String, Object>(length);

            for (int i = 0; i < length; i += 1) {
                map.put(keys[i], values[i]);
            }
            fragment.mContents = map;

            return fragment;
        }
    }

    public static StateFragment newInstance (String key, Object value) {
        StateFragment fragment = new StateFragment();

        HashMap<String, Object> map = new HashMap<String, Object>(1);
        map.put(key, value);

        fragment.mContents = map;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    
    public void put(String key, Object value) {
        mContents.put(key, value);
    }
    
    public Object get(String key) {
        return mContents.get(key);
    }
    
}
