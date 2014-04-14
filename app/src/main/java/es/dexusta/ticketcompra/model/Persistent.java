package es.dexusta.ticketcompra.model;

import android.content.Context;

public interface Persistent {
    public boolean isUpdated();
    
    public void setUpdated(boolean updated);
    
    public DBOCloudEntity getEntity(Context context);
}
