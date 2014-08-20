
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class SOEOutputStream extends FilterOutputStream implements DataOutput {
    protected int written;
    private short opcode;
    private short sequence;
    private short updateType;
    public SOEOutputStream(OutputStream out) {
    	super(out);
    }

    public synchronized void setOpcode(short value) throws IOException {
    	if (written == 0) {
    		writeShort(value);
    		opcode = value;
    		
    	} else throw new IOException("Cannot set opcode after other data written.");
    }
    
    public synchronized void setSequence(short value) throws IOException{
    	if (written == 2) {
    		writeReversedShort(value);
    		sequence = value;
    		
    	} else throw new IOException("Sequence must be set immediately after writing opcode.");
    }
    
    public synchronized void setSequence(int value) throws IOException {
    	setSequence((short) value);
    }
    
    public synchronized void setUpdateType(short value) throws IOException {
    	if (written == 4) {
    		writeShort(value);
    		updateType = value;
    	} else throw new IOException("UpdateType must be set immediately after writing sequence.");
    }
    
    public synchronized void writeReversedShort(short value) throws IOException{
    	writeShort(Short.reverseBytes(value));
    }
    
    public synchronized void writeReversedInt(int value) throws IOException{
    	writeInt(Integer.reverseBytes(value));
    }
    
    public synchronized void writeReversedLong(long value) throws IOException {
    	writeLong(Long.reverseBytes(value));
    }
    
    private void incCount(int value) {
        int temp = written + value;
        if (temp < 0) {
            temp = Integer.MAX_VALUE;
        }
        written = temp;
    }

    public synchronized void write(int b) throws IOException {
	out.write(b);
        incCount(1);
    }

    public synchronized void write(byte b[], int off, int len)
	throws IOException
    {
	out.write(b, off, len);
	incCount(len);
    }

    public void flush() throws IOException {
	out.flush();
    }
    public final void writeBoolean(boolean v) throws IOException {
	out.write(v ? 1 : 0);
	incCount(1);
    }
    public final void writeByte(int v) throws IOException {
	out.write(v);
        incCount(1);
    }

    public final void writeShort(int v) throws IOException {
        out.write((v >>> 0) & 0xFF);
        out.write((v >>> 8) & 0xFF);
        incCount(2);
    }

    public final void writeChar(int v) throws IOException {
        out.write((v >>> 0) & 0xFF);
        out.write((v >>> 8) & 0xFF);
        incCount(2);
    }
    public final void writeInt(int v) throws IOException {
        out.write((v >>>  0) & 0xFF);
        out.write((v >>>  8) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>> 24) & 0xFF);
        incCount(4);
    }

    private byte writeBuffer[] = new byte[8];

    public final void writeLong(long v) throws IOException {
        writeBuffer[7] = (byte)(v >>> 56);
        writeBuffer[6] = (byte)(v >>> 48);
        writeBuffer[5] = (byte)(v >>> 40);
        writeBuffer[4] = (byte)(v >>> 32);
        writeBuffer[3] = (byte)(v >>> 24);
        writeBuffer[2] = (byte)(v >>> 16);
        writeBuffer[1] = (byte)(v >>>  8);
        writeBuffer[0] = (byte)(v >>>  0);
        out.write(writeBuffer, 0, 8);
        incCount(8);
    }

    public final void writeFloat(float v) throws IOException {
    	writeInt(Float.floatToIntBits(v));
    }

    public final void writeDouble(double v) throws IOException {
	writeLong(Double.doubleToLongBits(v));
    }

    public final void writeBytes(String s) throws IOException {
	int len = s.length();
	for (int i = 0 ; i < len ; i++) {
	    out.write((byte)s.charAt(i));
	}
	incCount(len);
    }

    public final void writeChars(String s) throws IOException {
        int len = s.length();
        for (int i = 0 ; i < len ; i++) {
            int v = s.charAt(i);
            out.write((v >>> 0) & 0xFF); 
            out.write((v >>> 8) & 0xFF); 
        }
        incCount(len * 2);
    }

    public final void writeUTF(String str) throws IOException {
    	if (str == null) {
    		writeShort(0);
    	} else {
	    	int strLen = str.length();
	    	writeShort(strLen);
	    	for (int i = 0; i < str.length(); i++) {
	    		writeByte(str.charAt(i));
	    	}
    	}
    }

    public final void writeUTF16(String str) throws IOException {
    	if (str == null) {
    		writeInt(0);
    	} else {
	    	int strLen = str.length();
	    	writeInt(strLen);
	    	for (int i = 0; i < str.length(); i++) {
	    		writeShort(str.charAt(i));
	    	}
    	}
    }

    public final int size() {
        /**
         * Returns the total number of Bytes Written to the SOEOutputStream Object.
         */
    	return written;
    }
    public byte[] getBuffer() {
        /**
         * Returns the Entire Contents of the SOEOutputStream
         */
    	ByteArrayOutputStream bOut = (ByteArrayOutputStream)out;
    	return bOut.toByteArray();
    }
    
  protected short getOpcode() {
	  return opcode;
  }
  
  protected short getSequence() {
	  return sequence;
  }
  
  protected short getUpdateType() {
	  return updateType;
  }
 
}