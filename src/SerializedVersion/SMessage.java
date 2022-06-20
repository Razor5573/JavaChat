package SerializedVersion;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SMessage implements Serializable {
    private String message;
    private String type;
    private String source;
    private String date;

    public SMessage(String type, String msg) {
        this.type = type;
        message = msg;
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = sdfDate.format(new Date());
    }

    public SMessage(String type, String msg, String source) {
        this.type = type;
        this.source = source;
        message = msg;
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = sdfDate.format(new Date());
    }


    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return type + message;
    }

    public String getDate() {
        return date;
    }

    public String getSource() {
        return source;
    }
}
