package es.dexusta.ticketcompra.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

import android.content.Context;

/**
 * Created by asincrono on 7/08/13.
 */
public class Installation {
    private static int num_char = 36;
    private static String sID = null;
    private static final String INSTALLATION = "installation";

    public synchronized static String id(Context context) {

        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if(!installation.exists()) {
                    sID = writeInstallationFile(installation);
                }
                else {
                    sID = readInstallationFile(installation);
                }
            }
            catch (Exception e) {

            }
        }
        return sID;
    }   

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static String writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
        return id;
    }

    
}
