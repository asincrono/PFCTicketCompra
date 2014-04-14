package es.dexusta.ticketcompra.model;

import com.google.cloud.backend.android.CloudEntity;

public class DBOCloudEntity extends CloudEntity {
    
    public static final String PROPERTY_UNIV_ID = "universal_id";

    public DBOCloudEntity(String kindName) {
        super(kindName);       
    }

    public void setUnivId(String univ_id) {
        put(PROPERTY_UNIV_ID, univ_id);        
    }
    
    public String getInstallation() {        
        return (String) get(PROPERTY_UNIV_ID);  
    }    
    
//    public long getLocalId() {
//        String localIdStr = (String) get(PROPERTY_LOCAL_ID);
//        if (localIdStr == null) {
//            return -1;
//        }
//        return Long.parseLong(localIdStr);
//    }        
}
