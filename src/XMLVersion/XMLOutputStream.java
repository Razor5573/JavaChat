package XMLVersion;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class XMLOutputStream extends ByteArrayOutputStream {

    private DataOutputStream outChannel;

    public XMLOutputStream(OutputStream outchannel) {
        super();
        this.outChannel = new DataOutputStream(outchannel);
    }

    public void send() throws IOException {
        byte[] data = toByteArray();
        outChannel.writeInt(data.length);
        outChannel.write(data);
        reset();
    }
}