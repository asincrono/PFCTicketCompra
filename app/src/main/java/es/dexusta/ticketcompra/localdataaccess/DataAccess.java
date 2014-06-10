package es.dexusta.ticketcompra.localdataaccess;

import java.util.List;

import es.dexusta.ticketcompra.model.DBObject;

/**
 * Created by asincrono on 22/05/14.
 */
public abstract class DataAccess<T extends DBObject> {
    public abstract void list(DataAccessCallback<T> callback);
    public abstract void read(String sqlQuery, String[] args, DataAccessCallback<T> callback);
    public abstract void insert(List<T> dataList, DataAccessCallback<T> callback);
    public abstract void update(List<T> dataList, DataAccessCallback<T> callback);
    public abstract void delete(List<T> dataList, DataAccessCallback<T> callback);
    public abstract void deleteAll(DataAccessCallback<T> callback);
}
