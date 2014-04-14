package es.dexusta.ticketcompra.model;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Clase abstracta que engloba todos los comportamientos comunes para los objetos que representan
 * las entidades del modelo de datos.
 * @author asincrono
 *
 */
public abstract class DBObject implements Parcelable {
	private long mId;
	
	public DBObject() {
	    
	}
	
    /**
     * Establece un identificador para el objeto (se corresponderá con el campo _id de la base de 
     * datos).
     * @param id el identificador del objeto en la base de datos.
     */
    public void setId(long id) {
        mId = id;
    }
    
    public long getId() {
        return mId;
    }
	
	protected DBObject(Parcel in) {
	    mId = in.readLong();
	}
	
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);        
    }
    
}
