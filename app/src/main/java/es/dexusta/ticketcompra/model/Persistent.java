package es.dexusta.ticketcompra.model;

import android.content.Context;

public interface Persistent {
    public void setUpdated(boolean updated);
    
    public boolean isUpdated();
    
    public DBOCloudEntity getEntity(Context context);
}
