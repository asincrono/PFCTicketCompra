package es.dexusta.ticketcompra;



import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.HashMap;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public final class SupportStateFragment extends Fragment {
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
