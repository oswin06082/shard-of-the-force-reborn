import java.io.DataInput;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UTFDataFormatException;

public class SOEInputStream extends FilterInputStream implements DataInput {
	/*
	 * Creates a new SOEInputStream class with the InputStream provided.
	 * Reads, at a minimum, the first 2 bytes of data from the InputStream and stores them as the opcode.
	 * If the resulting short equals SOE_CHL_DATA_A, SOE_DATA_FRAG_A, or SOE_ACK_A, this reads
	 * the packet sequence (an additional 2 bytes).
	 * If the opcode equals SOE_CHL_DATA_A, this reads the 5th and 6th bytes of the stream, and stores them, low byte first,
	 * as the update type.
	 */
	
    public SOEInputStream(InputStream in) throws IOException {
    	super(in);
    	// Fill the buffer, so we can get it if we want it.
    	
    	
    	
    	
    }

   
    public final int read(byte b[]) throws IOException {
    	return in.read(b, 0, b.length);
    }

    public final int read(byte b[], int off, int len) throws IOException {
    	return in.read(b, off, len);
    }

    public final void readFully(byte b[]) throws IOException {
    	readFully(b, 0, b.length);
    }

    public final void readFully(byte b[], int off, int len) throws IOException {
		if (len < 0)
		    throw new IndexOutOfBoundsException();
		int n = 0;
		while (n < len) {
		    int count = in.read(b, off + n, len - n);
		    if (count < 0)
			throw new EOFException();
		    n += count;
		}
    }

    public final int skipBytes(int n) throws IOException {
		int total = 0;
		int cur = 0;
	
		while ((total<n) && ((cur = (int) in.skip(n-total)) > 0)) {
		    total += cur;
		}
	
		return total;
    }

    public final boolean readBoolean() throws IOException {
		int ch = in.read();
		if (ch < 0)
		    throw new EOFException();
		return (ch != 0);
    }

    public final byte readByte() throws IOException {
		int ch = in.read();
		if (ch < 0)
		    throw new EOFException();
		return (byte)(ch);
    }

    public final int readUnsignedByte() throws IOException {
		int ch = in.read();
		if (ch < 0)
		    throw new EOFException();
		return ch;
    }

    public final short readShort() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short)((ch2 << 8) + (ch1 << 0));
    }

    public final int readUnsignedShort() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (ch2 << 8) + (ch1 << 0);
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

    private byte readBuffer[] = new byte[8];

    public final long readLong() throws IOException {
        readFully(readBuffer, 0, 8);
        return (((long)readBuffer[7] << 56) +
                ((long)(readBuffer[6] & 255) << 48) +
		((long)(readBuffer[5] & 255) << 40) +
                ((long)(readBuffer[4] & 255) << 32) +
                ((long)(readBuffer[3] & 255) << 24) +
                ((readBuffer[2] & 255) << 16) +
                ((readBuffer[1] & 255) <<  8) +
                ((readBuffer[0] & 255) <<  0));
    }

    public final float readFloat() throws IOException {
    	return Float.intBitsToFloat(readInt());
    }

    public final double readDouble() throws IOException {
    	return Double.longBitsToDouble(readLong());
    }

    private char lineBuffer[];

    @Deprecated
    public final String readLine() throws IOException {
	char buf[] = lineBuffer;

	if (buf == null) {
	    buf = lineBuffer = new char[128];
	}

	int room = buf.length;
	int offset = 0;
	int c;

loop:	while (true) {
	    switch (c = in.read()) {
	      case -1:
	      case '\n':
		break loop;

	      case '\r':
		int c2 = in.read();
		if ((c2 != '\n') && (c2 != -1)) {
		    if (!(in instanceof PushbackInputStream)) {
			this.in = new PushbackInputStream(in);
		    }
		    ((PushbackInputStream)in).unread(c2);
		}
		break loop;

	      default:
		if (--room < 0) {
		    buf = new char[offset + 128];
		    room = buf.length - offset - 1;
		    System.arraycopy(lineBuffer, 0, buf, 0, offset);
		    lineBuffer = buf;
		}
		buf[offset++] = (char) c;
		break;
	    }
	}
	if ((c == -1) && (offset == 0)) {
	    return null;
	}
	return String.copyValueOf(buf, 0, offset);
    }

    
    public final String readUTF() throws IOException {
        int utflen = readUnsignedShort();
        char[] chararr = new char[utflen];
        for (int i = 0; i < utflen; i++) {
        	chararr[i] = (char)readByte();
        }
        return new String(chararr);
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
    public final short readReversedShort() throws IOException {
    	return Short.reverseBytes(readShort());
    }
    
    public final int readReversedInt() throws IOException {
    	return Integer.reverseBytes(readInt());
    }
    public final long readReversedLong() throws IOException {
    	return Long.reverseBytes(readLong());
    }
    
   
}
