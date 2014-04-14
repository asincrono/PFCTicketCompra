package es.dexusta.ticketcompra.dataaccess;

import java.util.List;

import es.dexusta.ticketcompra.model.DBObject;

public abstract class DataAccess<D extends DBObject> {
//    private final List<DataSourceListener<D>> mListeners;
//    
//    public DataAccess(List<DataSourceListener<D>> listeners) {
//        mListeners = listeners;
//    }
//    
//    public List<DataSourceListener<D>> getListeners() {
//        return mListeners;
//    }
    
    private DataAccessCallbacks<D> mListener;
    
    public void setCallback(DataAccessCallbacks<D> listener) {
        mListener = listener;
    }
    
    public DataAccessCallbacks<D> getCallback() {
        return mListener;
    }
    
    public boolean hasCallback() {
        return mListener != null;
    }
    
    public abstract void query(String rawQuery, String[] args);
    public abstract void list();
    public abstract void insert(List<D> dataList);
    public abstract void update(List<D> dataList);
    public abstract void delete(List<D> dataList);
    public abstract void deleteAll();
    public abstract void getCount();
}
