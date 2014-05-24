package es.dexusta.ticketcompra.localdataaccess;

import java.util.List;

import es.dexusta.ticketcompra.model.DBObject;

/**
 * Created by asincrono on 22/05/14.
 */
public interface DataAccessCallback<T extends DBObject> {
    //public void onComplete(Types.Operation operation, boolean result, List<T> dataList, int info);
    public void onComplete(List<T> results, boolean result);
}
