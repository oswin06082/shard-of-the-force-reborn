/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Vector;
/**
 *
 * @author Tomas Cruz
 */
public class TradeObject extends SOEObject{ // This shouldn't extend SOEObject is it is not a client object

    public final static long serialVersionUID = 1l;
    private transient ZoneServer server;
    private Vector<SOEObject> vTradeListToOriginator;
    private Vector<SOEObject> vTradeListToRecipient;
    private int cashToOriginator;
    private int cashToRecipient;
    private Player originator;
    private Player recipient;
    private int tradeStatus;
    private int iTradeRequestID;
    private int iRandomInt;
    private String sTradeLog;
    private boolean originatorTradeVerified;
    private boolean recipientTradeVerified;
    private long lTimeToLive;
    private boolean originatorTradeAcceptChecked;
    private boolean recipientTradeAcceptChecked;
    
    public TradeObject(ZoneServer server){
        this.server = server;
        vTradeListToOriginator = new Vector<SOEObject>();
        vTradeListToRecipient = new Vector<SOEObject>();
        sTradeLog = "Trade Object Created: " + System.currentTimeMillis() + "\r\n";
        originatorTradeVerified = false;
        recipientTradeVerified = false;
        lTimeToLive = 1000 * 60 * 10;
        originatorTradeAcceptChecked = false;
        recipientTradeAcceptChecked = false;
    }

    public int getCashToOriginator() {
        return cashToOriginator;
    }

    public void setCashToOriginator(int cashToOriginator) {
        this.cashToOriginator = cashToOriginator;
        sTradeLog += "setCashToOriginator: " + cashToOriginator + "\r\n";
    }

    public int getCashToRecipient() {
       
        return cashToRecipient;
        
    }

    public void setCashToRecipient(int cashToRecipient) {
        this.cashToRecipient = cashToRecipient;
        sTradeLog += "setCashToRecipient: " + cashToRecipient + "\r\n";
    }

    public Player getOriginator() {        
        return originator;
    }

    public void setOriginator(Player originator) {
        sTradeLog += "setOriginator: " + originator.getID() + ":" + originator.getFullName() + "\r\n";
        this.originator = originator;
    }

    public Player getRecipient() {
        return recipient;
    }

    public void setRecipient(Player recipient) {
        sTradeLog += "setRecipient: " + recipient.getID() + ":" + recipient.getFullName() + "\r\n";
        this.recipient = recipient;
    }

    public ZoneServer getServer() {
        return server;
    }

    public void setServer(ZoneServer server) {
        this.server = server;
    }

    public Vector<SOEObject> getTradeListToOriginator() {
        return vTradeListToOriginator;
    }

    public void setTradeListToOriginator(Vector<SOEObject> tradeListToOriginator) {
        this.vTradeListToOriginator = tradeListToOriginator;
    }

    public Vector<SOEObject> getTradeListToRecipient() {
        return vTradeListToRecipient;
    }

    public void setTradeListToRecipient(Vector<SOEObject> tradeListToRecipient) {
        this.vTradeListToRecipient = tradeListToRecipient;
    }

    public int getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(int tradeStatus) {
        sTradeLog += "setTradeStatus: " + tradeStatus + "\r\n";
        this.tradeStatus = tradeStatus;
    }

    public int getITradeRequestID() {
        return iTradeRequestID;
    }

    public void setITradeRequestID(int iTradeRequestID) {
        this.iTradeRequestID = iTradeRequestID;
    }

    public int getRandomInt() {
        return iRandomInt;
    }

    public void setRandomInt(int iRandomInt) {
        this.iRandomInt = iRandomInt;
    }
    
    protected void notifyTradeBegin(){
        try{
            lTimeToLive+=1000*60*10;
            sTradeLog += "notifyTradeBegin: \r\n";
            System.out.println("TradeObject.notifyTradeBegin()");
            this.recipient.getClient().insertPacket(PacketFactory.buildBeginTradeMessage(originator));
            this.originator.getClient().insertPacket(PacketFactory.buildBeginTradeMessage(recipient));
        }catch(Exception e){
            System.out.println("Eception Caught in TradeObject.notifyTradeBegin() " + e);
            e.printStackTrace();
            notifyAbortTrade(recipient);
            notifyAbortTrade(originator);
        }
    }
    
    protected void notifyCashOfferedUpdate(Player player, int iCashAmount){
        try{
            lTimeToLive+=1000*60*10;
            System.out.println("TradeObject.notifyCashOfferedUpdate()");
            sTradeLog += "notifyCashOfferedUpdate: " + iCashAmount + " Player: " + player.getID() + ":" + player.getFullName() + " \r\n";
            if(player.equals(this.originator))
            {
                recipient.getClient().insertPacket(PacketFactory.buildUpdateTradeWindowCredits(iCashAmount));
                this.cashToRecipient = iCashAmount;
            }
            else if(player.equals(this.recipient))
            {
                originator.getClient().insertPacket(PacketFactory.buildUpdateTradeWindowCredits(iCashAmount));
                this.cashToOriginator = iCashAmount;
            }
        }catch(Exception e){
            System.out.println("Eception Caught in TradeObject.notifyCashOfferedUpdate() " + e);
            e.printStackTrace();
            notifyAbortTrade(player);
        }
    }
   
    protected void notifyItemAddedToWindow(Player player, long lObjectID){
        try{
            lTimeToLive+=1000*60*10;
            sTradeLog += "notifyItemAddedToWindow: " + lObjectID + " From Player: " + player.getID() + ":" + player.getFullName() + " \r\n";
            System.out.println("TradeObject.notifyItemAddedToWindow()");
            //this is done here because the baselines sent do not need to be ended and are limited to only this
            if(player.equals(this.originator))
            {
                System.out.println("Adding object to recipient window");
                SOEObject o = server.getObjectFromAllObjects(lObjectID);
                if(!vTradeListToRecipient.contains(o))
                {
                    this.vTradeListToRecipient.add(o);
                }
                
                recipient.getClient().insertPacket(PacketFactory.buildAddItemToTradeWindow(lObjectID));
                recipient.getClient().insertPacket(PacketFactory.buildSceneCreateObjectByCRC(o,false));
                if(o instanceof TangibleItem)
                {
                    TangibleItem t = (TangibleItem)o;                    
                    recipient.getClient().insertPacket(PacketFactory.buildUpdateContainmentMessage(t,t.getContainer(), -1));
                    recipient.getClient().insertPacket(PacketFactory.buildBaselineTANO3(t));
                    recipient.getClient().insertPacket(PacketFactory.buildBaselineTANO6(t));                    
                }
                else if(o instanceof IntangibleObject)
                {
                    IntangibleObject i = (IntangibleObject)o;                    
                    recipient.getClient().insertPacket(PacketFactory.buildUpdateContainmentMessage(i,i.getContainer(), -1));
                    recipient.getClient().insertPacket(PacketFactory.buildBaselineITNO3(i));
                    recipient.getClient().insertPacket(PacketFactory.buildBaselineITNO6(i));                    
                }
                else if(o instanceof ResourceContainer)
                {
                    ResourceContainer r = (ResourceContainer) o;            
                    recipient.getClient().insertPacket(PacketFactory.buildUpdateContainmentMessage(r, r.getContainer(), r.getEquippedStatus()));
                    recipient.getClient().insertPacket(PacketFactory.buildBaselineRCNO3(r));
                    recipient.getClient().insertPacket(PacketFactory.buildBaselineRCNO6(r));            
                }
                recipient.getClient().insertPacket(PacketFactory.buildSceneEndBaselines(o));    
                sTradeLog += "notifyItemAddedToWindow: " + this.vTradeListToRecipient.size() + " Items to Recipient \r\n";
            }
            else if(player.equals(this.recipient))
            {
                System.out.println("Adding object to originator window");
               SOEObject o = server.getObjectFromAllObjects(lObjectID);
               if(!vTradeListToOriginator.contains(o))
               {
                   this.vTradeListToOriginator.add(o);
               }
               originator.getClient().insertPacket(PacketFactory.buildAddItemToTradeWindow(lObjectID));
               originator.getClient().insertPacket(PacketFactory.buildSceneCreateObjectByCRC(o,false));
               if(o instanceof TangibleItem)
                {
                    TangibleItem t = (TangibleItem)o;                    
                    originator.getClient().insertPacket(PacketFactory.buildUpdateContainmentMessage(t,t.getContainer(), -1));
                    originator.getClient().insertPacket(PacketFactory.buildBaselineTANO3(t));
                    originator.getClient().insertPacket(PacketFactory.buildBaselineTANO6(t));                    
                }
                else if(o instanceof IntangibleObject)
                {
                    IntangibleObject i = (IntangibleObject)o;                    
                    originator.getClient().insertPacket(PacketFactory.buildUpdateContainmentMessage(i,i.getContainer(), -1));
                    originator.getClient().insertPacket(PacketFactory.buildBaselineITNO3(i));
                    originator.getClient().insertPacket(PacketFactory.buildBaselineITNO6(i));                    
                }
                else if(o instanceof ResourceContainer)
                {
                    ResourceContainer r = (ResourceContainer) o;            
                    originator.getClient().insertPacket(PacketFactory.buildUpdateContainmentMessage(r, r.getContainer(), r.getEquippedStatus()));
                    originator.getClient().insertPacket(PacketFactory.buildBaselineRCNO3(r));
                    originator.getClient().insertPacket(PacketFactory.buildBaselineRCNO6(r));            
                }
                originator.getClient().insertPacket(PacketFactory.buildSceneEndBaselines(o));            
                sTradeLog += "notifyItemAddedToWindow: " + this.vTradeListToOriginator.size() + " Items to Originator \r\n";
            }
            
            
        }catch(Exception e){
            System.out.println("Eception Caught in TradeObject.notifyItemAddedToWindow() " + e);
            e.printStackTrace();
            notifyAbortTrade(player);
        }
    }
    
    protected void notifyAbortTrade(Player player){
        try{
            lTimeToLive+=1000*60*10;
            sTradeLog += "notifyAbortTrade: Player: " + player.getID() + ":" + player.getFullName() + " \r\n";
            System.out.println("TradeObject.notifyAbortTrade()");
            if(player.equals(this.originator))
            {
                originator.removeCurrentTradeObject();
                originator.removeTradeRequest(this);
                recipient.getClient().insertPacket(PacketFactory.buildAbortTradeMessage());
            }
            else if(player.equals(this.recipient))
            {
                recipient.removeCurrentTradeObject();
                recipient.removeTradeRequest(this);
                originator.getClient().insertPacket(PacketFactory.buildAbortTradeMessage());
            }
            
            DataLogObject L = new DataLogObject("TradeObject.notifyAbortTrade()",sTradeLog,Constants.LOG_SEVERITY_INFO);
            DataLog.qServerLog.add(L);
            System.out.println(sTradeLog);
        }catch(Exception e){
            System.out.println("Eception Caught in TradeObject.notifyAbortTrade() " + e);
            e.printStackTrace();
        }
    }
    
    protected void notifyTradeAcceptedChecked(Player player){
        try{
            lTimeToLive+=1000*60*10;
            //sTradeLog += "notifyTradeAcceptedChecked: Player: " + player.getID() + ":" + player.getFullName() + " \r\n";
            System.out.println("TradeObject.notifyTradeAcceptedChecked()");
            if(player.equals(this.originator))
            {
                this.originatorTradeAcceptChecked = true;
                recipient.getClient().insertPacket(PacketFactory.buildCheckAcceptTrade());
            }
            else if(player.equals(this.recipient))
            {
               this.recipientTradeAcceptChecked = true;
               originator.getClient().insertPacket(PacketFactory.buildCheckAcceptTrade());
            }
        }catch(Exception e){
            System.out.println("Exception Caught in TradeObject.notifyTradeAcceptedChecked() " + e);
            e.printStackTrace();
            notifyAbortTrade(player);
        }
    }
    
    protected void notifyTradeAcceptedUnChecked(Player player){
        try{
            lTimeToLive+=1000*60*10;
            //sTradeLog += "notifyTradeAcceptedUnChecked: Player: " + player.getID() + ":" + player.getFullName() + " \r\n";
            System.out.println("TradeObject.notifyTradeAcceptedUnChecked()");
            if(player.equals(this.originator))
            {
                this.originatorTradeAcceptChecked = false;
                recipient.getClient().insertPacket(PacketFactory.buildUnCheckAcceptTrade());
            }
            else if(player.equals(this.recipient))
            {
                this.recipientTradeAcceptChecked = false;
               originator.getClient().insertPacket(PacketFactory.buildUnCheckAcceptTrade());
            }
        }catch(Exception e){
            System.out.println("Exception Caught in TradeObject.notifyTradeAcceptedUnChecked() " + e);
            e.printStackTrace();
            notifyAbortTrade(player);
        }
    }
    
    protected void notifyVerifyTrade(Player player){
        try{
            lTimeToLive+=1000*60*10;
            sTradeLog += "notifyVerifyTrade: Player: " + player.getID() + ":" + player.getFullName() + " \r\n";
            System.out.println("TradeObject.notifyVerifyTrade()");
            if(player.equals(this.originator))
            {
                this.originatorTradeVerified = true;
                recipient.getClient().insertPacket(PacketFactory.buildVerifyTradeMessage());
            }
            else if(player.equals(this.recipient))
            {
               this.recipientTradeVerified = true;
               originator.getClient().insertPacket(PacketFactory.buildVerifyTradeMessage());
            }
            
            
        }catch(Exception e){
            System.out.println("Exception Caught in TradeObject.notifyVerifyTrade() " + e);
            e.printStackTrace();
            notifyAbortTrade(player);
        }
    }
    
    protected void update(long lDeltaMS){
        
        if(lTimeToLive < 0)
        {
            sTradeLog += "TradeObject.update() TimeToLive Expired Aborting Trade\r\n";
            this.notifyAbortTrade(originator);
        }
        else
        {
            lTimeToLive-=lDeltaMS;
            if(lTimeToLive < 0 )
            {
                lTimeToLive = 0;
            }
        }
        if(recipientTradeVerified && originatorTradeVerified && recipientTradeAcceptChecked && originatorTradeAcceptChecked  )
        {
            completeTrade();
        }
    }
    
    protected void completeTrade(){
        try{
            lTimeToLive+=1000*60*10;
            if(recipientTradeVerified && originatorTradeVerified)
            {
               sTradeLog += "completeTrade:TradeCompleted \r\n";
               
               if(this.cashToRecipient >= 1)
               {
                   sTradeLog += "notifyVerifyTrade:TradeCompleted deposit Credits to Recipient\r\n";
                   recipient.creditInventoryCredits(cashToRecipient);
                   originator.debitInventoryCredits(cashToRecipient);                   
               }
               if(this.cashToOriginator >= 1)
               {
                   sTradeLog += "notifyVerifyTrade:TradeCompleted deposit Credits to Originator\r\n";
                   originator.creditInventoryCredits(cashToOriginator);
                   recipient.debitInventoryCredits(cashToOriginator);                   
               }
               if(this.vTradeListToRecipient.size() >= 1)
               {
                   sTradeLog += "notifyVerifyTrade:TradeCompleted deposit Items to Recipient\r\n";
                   for(int i = 0; i < vTradeListToRecipient.size(); i++)
                   {
                       SOEObject o = vTradeListToRecipient.get(i);
                       if(o instanceof TangibleItem)
                       {
                           TangibleItem t = (TangibleItem)o;
                           originator.removeItemFromInventory(t);
                           originator.getInventory().removeLinkedObject(t);
                           originator.despawnItem(t);
                           recipient.addItemToInventory(t);
                           recipient.getInventory().addLinkedObject(t);
                           t.setEquipped(recipient.getInventory(),-1);
                           t.setOwner(recipient);
                           recipient.spawnItem(t);
                       }
                       else if(o instanceof IntangibleObject)
                       {
                           IntangibleObject io = (IntangibleObject)o;
                           originator.getDatapad().removeIntangibleObject(io);
                           originator.despawnItem(io);
                           recipient.getDatapad().addIntangibleObject(io);
                           io.setContainer(recipient.getDatapad(),-1, false);
                           recipient.spawnItem(io);
                       }
                       else if(o instanceof ResourceContainer)
                       {
                           ResourceContainer r = (ResourceContainer)o;
                           originator.removeItemFromInventory(r);
                           originator.getInventory().removeLinkedObject(r);
                           originator.despawnItem(r);
                           recipient.addItemToInventory(r);
                           recipient.getInventory().addLinkedObject(r);
                           r.setEquipped(recipient.getInventory(),-1);
                           r.setOwner(recipient);                           
                           recipient.spawnItem(r);
                       }
                       recipient.getClient().insertPacket(PacketFactory.buildUpdateContainmentMessage(o, recipient.getInventory(),-1));
                       
                   }
               }
               if(this.vTradeListToOriginator.size() >= 1)
               {
                   sTradeLog += "notifyVerifyTrade:TradeCompleted deposit Items to Originator\r\n";
                   for(int i = 0; i < vTradeListToOriginator.size(); i++)
                   {
                       SOEObject o = vTradeListToOriginator.get(i);
                       if(o instanceof TangibleItem)
                       {
                           TangibleItem t = (TangibleItem)o;
                           recipient.removeItemFromInventory(t);
                           recipient.getInventory().removeLinkedObject(t);
                           recipient.despawnItem(t);
                           originator.addItemToInventory(t);
                           originator.getInventory().addLinkedObject(t);
                           t.setEquipped(originator.getInventory(),-1);
                           t.setOwner(originator);
                           originator.spawnItem(t);
                       }
                       else if(o instanceof IntangibleObject)
                       {
                           IntangibleObject io = (IntangibleObject)o;
                           recipient.getDatapad().removeIntangibleObject(io);
                           recipient.despawnItem(io);
                           originator.getDatapad().addIntangibleObject(io);
                           io.setContainer(originator.getDatapad(),-1, false);
                           originator.spawnItem(io);
                       }
                       else if(o instanceof ResourceContainer)
                       {
                           ResourceContainer r = (ResourceContainer)o;
                           recipient.removeItemFromInventory(r);
                           recipient.getInventory().removeLinkedObject(r);
                           recipient.despawnItem(r);
                           originator.addItemToInventory(r);
                           originator.getInventory().addLinkedObject(r);
                           r.setEquipped(originator.getInventory(),-1);
                           r.setOwner(originator);
                           originator.spawnItem(r);                           
                       }
                       originator.getClient().insertPacket(PacketFactory.buildUpdateContainmentMessage(o, originator.getInventory(),-1));
                       
                   }
               }
               originator.getClient().insertPacket(PacketFactory.buildTradeCompleteMessage()); 
               recipient.getClient().insertPacket(PacketFactory.buildTradeCompleteMessage());
            }            
            sTradeLog += "Trade Completed Successfully.\r\n----------------------------------------------\r\n";
            DataLogObject L = new DataLogObject("TradeObject.completeTrade()",sTradeLog,Constants.LOG_SEVERITY_INFO);
            DataLog.qServerLog.add(L);
            System.out.println(sTradeLog);
            originator.removeCurrentTradeObject();
            originator.removeTradeRequest(this);
            recipient.removeCurrentTradeObject();
            recipient.removeTradeRequest(this);
        }catch(Exception e){
            System.out.println("Exception Caught in tradeObject.completeTrade " + e);
            e.printStackTrace();
        }
    }
            
}
