import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;

public class STFInputStream extends FilterInputStream {
	public STFInputStream(InputStream in) throws IOException {
		super(in);
	}

	public final byte readByte() throws IOException {
		int ch = in.read();
		if (ch < 0) {
			throw new EOFException();
		}
		return (byte)(ch);
	}

    public final short readShort() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short)((ch2 << 8) + (ch1 << 0));
    }

    public final char readChar() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (char)((ch2 << 8) + (ch1 << 0));
    }

    public final int readInt() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
    }

    public final String readUTF16() throws IOException, UTFDataFormatException {
    	int utflen = readInt();
    	if (utflen < 0) throw new UTFDataFormatException("Error:  Negative string length");
    	char[] chararr = new char[utflen];
    	for (int i = 0; i < utflen; i++) {
    		chararr[i] = (char)((byte)readShort());
    	}
    	return new String(chararr);
    }
}
