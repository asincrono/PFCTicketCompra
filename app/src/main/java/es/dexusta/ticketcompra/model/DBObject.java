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
	
	protected DBObject(Parcel in) {
	    mId = in.readLong();
	}
    
    public long getId() {
        return mId;
    }
	
    /**
     * Establece un identificador para el objeto (se corresponderá con el campo _id de la base de
     * datos).
     * @param id el identificador del objeto en la base de datos.
     */
    public void setId(long id) {
        mId = id;
    }
	
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);        
    }
    
}
