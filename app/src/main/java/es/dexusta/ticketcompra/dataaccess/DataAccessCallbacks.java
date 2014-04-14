package es.dexusta.ticketcompra.dataaccess;

import java.util.List;

import es.dexusta.ticketcompra.dataaccess.AsyncStatement.Option;
import es.dexusta.ticketcompra.dataaccess.Types.Operation;
import es.dexusta.ticketcompra.model.DBObject;

public interface DataAccessCallbacks<T extends DBObject> {
    public void onDataProcessed(int processed, List<T> dataList,Operation operation, boolean result);
    public void onDataReceived(List<T> results);
    public void onInfoReceived(Object result, Option option);    
}
