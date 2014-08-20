/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;
import java.util.*;
/**
 *
 * @author Tomas Cruz
 */
public class CustomData implements Serializable{
    public final static long serialVersionUID = 1;

    private byte [] operandList ;
    private byte [] operandValue ;
    private byte [] operandDefaultValue ;
    private byte [] customData;
    private byte [] terminator;
    private byte iOperandCount;
    private byte iListCount;
    private long lObjectID;
    private byte raceByte;
    private byte dataOffset;
    private byte [] tempCustomData;

    public CustomData(byte [] customData, long lObjectID, int iTypeRaceID){
        this.customData = customData;
        this.lObjectID = lObjectID;
        iListCount = customData[0];
        iOperandCount = customData[1];
        terminator = new byte[2];
        terminator[0] = customData[customData.length-2];
        terminator[1] = customData[customData.length-1];
        operandList = new byte[customData[1]];
        operandValue = new byte[customData[1]];
        operandDefaultValue = new byte[customData[1]];
        dataOffset = 2;
        raceByte = -1;
        switch(iTypeRaceID)
        {
            case 10:
            {
                dataOffset = 3;
                raceByte = customData[2];
                break;
            }
        }
        byte operCounter = 0;
        for(byte i = dataOffset; i < customData.length-2; i++)
         {
            operandList[operCounter] = customData[i];
            operandValue[operCounter] = customData[i+1];
            if(operandValue[operCounter] == (byte)0xFF)
            {
                operandDefaultValue[operCounter] = customData[i+2];
                System.out.println(operCounter + " Operand: " + PacketUtils.getByteCode(operandList[operCounter]) + " Value " +PacketUtils.getByteCode(operandValue[operCounter]) + " " + PacketUtils.getByteCode(operandDefaultValue[operCounter]));
                i+=2;
                operCounter++;
            }
            else
            {
                //operandValue[operCounter] = data[i+1];
                System.out.println(operCounter + " Operand: " + PacketUtils.getByteCode(operandList[operCounter]) + " Value " +PacketUtils.getByteCode(operandValue[operCounter]) );
                i++;
                operCounter++;
            }
         }
    }


    protected void changeCustomizationValue(byte operand, byte value, byte defval, boolean commit){
            byte iIndex = 0;
            //find the operand position in the array
            for(byte i = 0; i < this.operandList.length; i ++)
            {
                if(this.operandList[i] == operand)
                {
                    iIndex = i;
                }
            }
            this.operandValue[iIndex] = value;
            if(this.operandValue[iIndex] == (byte)0xFF)
            {
                this.operandDefaultValue[iIndex] = defval;
            }
            //rebuild the customization string.
            Vector<Byte> newCustomization = new Vector<Byte>();
            newCustomization.add(this.iListCount);
            newCustomization.add(this.iOperandCount);
            if(this.raceByte != -1)
            {
                newCustomization.add(this.raceByte);
            }
            for(byte i = 0; i < this.operandList.length; i++)
            {
                newCustomization.add(this.operandList[i]);
                newCustomization.add(this.operandValue[i]);
                if(this.operandValue[i] == (byte)0xFF)
                {
                    newCustomization.add(this.operandDefaultValue[i]);
                }
            }
            newCustomization.add(this.terminator[0]);
            newCustomization.add(this.terminator[1]);

            int iNewCustomLength = newCustomization.size();
            byte [] newCustomData = new byte[iNewCustomLength];
            for(int i = 0; i < newCustomization.size(); i++)
            {
                newCustomData[i] = newCustomization.get(i);
            }

            PacketUtils.printPacketToScreen(this.customData,this.customData.length,"Old Custom Data");
            PacketUtils.printPacketToScreen(newCustomData,newCustomData.length,"New Custom Data");
            tempCustomData = newCustomData;
            if(commit)
            {
                commitChanges();
            }
    }

    protected void commitChanges(){
        this.customData = this.tempCustomData;        
    }

    protected byte [] getCustomizationData(){
        return this.customData;
    }
}
