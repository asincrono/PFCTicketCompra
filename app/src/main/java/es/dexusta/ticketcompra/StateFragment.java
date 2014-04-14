package es.dexusta.ticketcompra;

import java.util.HashMap;

import android.app.Fragment;
import android.os.Bundle;

/**
 * StateFragment is a fragment that should be used to hold Activity state between configuration changes.
 * Data is stored in a HashMap where Strings are used as keys. 
 * @author asincrono
 *
 */
public final class StateFragment extends Fragment {
    private HashMap<String, Object> mContents = new HashMap<String, Object>();

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
