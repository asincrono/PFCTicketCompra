package es.dexusta.ticketcompra.control;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import es.dexusta.ticketcompra.model.DBObject;

public abstract class DBObjectAdapter<T extends DBObject> extends BaseAdapter {
    private List<T> mList;
//    private Context mContext;
    private LayoutInflater mInflater;
    private final Object mLock = new Object();

    public DBObjectAdapter(Context context) {
        init (context, null);
    }

    public DBObjectAdapter(Context context, List<T> list) {        
        init(context, list);       
    }

    private void init(Context context, List<T> list) {
//        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);

        if (mList == null) {
            mList = new ArrayList<T>();
        }
    }

    protected LayoutInflater getInflater() {
        return mInflater;
    }
    
//    protected Context getContext() {
//        return mContext;
//    }

    public void add(T data) {
        synchronized (mLock) {
            mList.add(data);
        }
        notifyDataSetChanged();      
    }

    public void addAll(List<T> list) {
        synchronized (mLock) {
            mList.addAll(list);   
        }        
        notifyDataSetChanged(); 
    }
    
    public void clear() {
        synchronized (mLock) {
            mList.clear();
        }
        notifyDataSetChanged();
    }

    public List<T> sawpList(List<T> list) {
        List<T> old = mList;
        
        if (list != null) {
            mList = list;            
        }
        else {
            mList = new ArrayList<T>();
        }
        
        notifyDataSetChanged();
        return old;
    }
    
    public void updateList(List<T> list) {
        if (list != null) {
            mList = list;    
        }            
        else {
            mList.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {        
        return mList.size();        
    }

    @Override
    public T getItem(int position) {
        if (position < 0 || position >= mList.size()) {
            return null;
        }
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mList.size()) {
            return -1;
        }
        return mList.get(position).getId();
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);    
}
