import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Enumeration;

/**
 *
 * @author Tomas Cruz
 * This is the Terminal Class. This will be used to make all sorts of terminals.
 * This includes but not limited to , playerTravel Terminals, playerTravel Droids and Structure Terminals.
 */
public class Terminal extends NPC {
    public final static long serialVersionUID = 1l;
    //private boolean bIsTerminal = true;
    //private String sTerminalName = "Terminal";
    private int iTerminalType = -1;
    private int iLocationID = -1;
    private int iTicketID = -1;
    private int iPortID = -1;
    private byte bIsFactionalTerminal = 0;
    private long lDBID;
    private long lNextDesireTime = 0;
    private short unkMissionRefreshShort;
    private boolean bHasDesires;
    private Structure myStructure;
    
    public Terminal(){
    	super();
        this.setCREO3Bitmask(Constants.BITMASK_CREO3_TERMINAL);
        //this.setPVPBitmask(Constants.PVP_STATUS_IS_ITEM);
        setPVPStatus(Constants.PVP_STATUS_IS_NORMAL_NON_ATTACKABLE);
        setIsTerminal(true);
    }
        
    /**
     * Terminal(String _sTerminalName, int _iTerminalType, int iLocationID)
     * Spawn a Terminal i/e housing or structure terminal.
     * @param _sTerminalName - Determines the title Displayed on the Terminal.
     * @param _iTerminalType - Determines the type of terminal, i/e House, playerTravel, Ticket Droid etc...
     * @param iLocationID - Determines the Location Identifier for said terminal. used in travel or housing.
     * @param _bIsFactionalTerminal - determines if this is a factional specific terminal.
     * @param _lDBID - Sets the Database Table id for this terminal
     */
    public Terminal(String _sTerminalName, int _iTerminalType, int _iLocationID, byte _bIsFactionalTerminal, long _lDBID){
        setFirstName(_sTerminalName);
        iTerminalType = _iTerminalType;
        iLocationID = _iLocationID;
        bIsFactionalTerminal = _bIsFactionalTerminal;
        lDBID = _lDBID;
        this.setCREO3Bitmask(Constants.BITMASK_CREO3_TERMINAL);
        //this.setPVPBitmask(Constants.PVP_STATUS_IS_ITEM);
        setPVPStatus(Constants.PVP_STATUS_IS_NORMAL_NON_ATTACKABLE);
    }
   
    /**
     * Terminal(String _sTerminalName, int _iTerminalType, int iLocationID)
     * Spawn a playerTravel Terminal
     * @param _sTerminalName - Determines the title Displayed on the Terminal
     * @param _iTerminalType - Determines the type of terminal, i/e House, playerTravel, Ticket Droid etc...
     * @param _iLocationID - Determines the Location Identifier for said terminal. used in travel or housing.
     * @param _iTicketID - Determines the ticket That This Terminal can take. -1 = None. ticket destination IDS will be destination id for planet/port.
     * @param _iPortID - Determines the id of the port this terminal resides in. -1 = None. Each Starport or Shuttle port will have an id.
     * @param _bIsFactionalTerminal - determines if this is a factional specific terminal.
     * @param _lDBID - Sets the Database Table id for this terminal
     */
    public Terminal(String _sTerminalName, int _iTerminalType, int _iLocationID, int _iTicketID, int _iPortID, byte _bIsFactionalTerminal, long _lDBID) {
                setFirstName(_sTerminalName);
               iTerminalType = _iTerminalType;
               iLocationID = _iLocationID;
               iTicketID = _iTicketID;
               iPortID = _iPortID;        
               bIsFactionalTerminal = _bIsFactionalTerminal;
               lDBID = _lDBID;
               this.setCREO3Bitmask(Constants.BITMASK_CREO3_TERMINAL);
               this.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
    }

    public boolean isBHasDesires() {
        return bHasDesires;
    }

    public void setBHasDesires(boolean bHasDesires) {
        this.bHasDesires = bHasDesires;
    }    
    
    public void setTerminalType(int t){
        iTerminalType = t;
    }
     
    public int getTerminalType(){
        return iTerminalType;
    }
    
    public void setLocationID(int l){
        iLocationID = l;
    }
    
    public int getLocationID(){
        return iLocationID;
    }
    
    public void setTicketID(int i){
        iTicketID = i;
    }
    
    public int getTicketID(){
        return iTicketID;
    }
    
    public void setPortID(int p){
        iPortID = p;
    }
    
    public int getPortID(){
        return iPortID;
    }
    
    public void setIsFactionTerminal(byte b){
        bIsFactionalTerminal = b;
    }
    
    public byte getIsFactionTerminal(){
        return bIsFactionalTerminal;
    }
    
    public void setDBID(long id){
        lDBID = id;
    }
    
    public long getDBID(){
        return lDBID;
    }

    public void update(long lDeltaTimeMS) {
            if(IsSkillTrainer())
            {
                /**
                 * This determines the Desires of an NPC
                 * Radom a number between 0 and 600
                 * not all numbers between 0 and 600 are in the case labels
                 * add new case handlers ad desired.
                 */
            	lNextDesireTime -= lDeltaTimeMS;

                if(lNextDesireTime <= 0)
                {
                    lNextDesireTime = SWGGui.getRandomLong((1000*60*2),(1000*60*30));
                    int Desire = SWGGui.getRandomInt(0,600);

                    // This code appears to be stuck in a continuous loop on MrAgent's server.  
                    // NPC's shouldn't be spending their time watching players dancing or singing.
                    if (false) {
	                    Vector<Player> vPLe = getServer().getPlayersAroundNPC(this);
	                    int iEntertaining = 0;
	                    for(int i = 0; i < vPLe.size(); i++)
	                    {
	                        Player p = vPLe.get(i);
	                        if(p.isDancing() || p.isPlayingMusic())
	                        {
	                            iEntertaining++;
	                        }
	                    }
	                    if(iEntertaining >= 1)
	                    {
	                        try{
	                            byte [] stc = setStance(Constants.STANCE_ANIMATING_SKILL, false, true);
	                            for(int i = 0; i < vPLe.size(); i++)
	                            {
	                                Player p = vPLe.get(i);
	                                p.getClient().insertPacket(stc);
	                                p.getClient().insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)6, (short)1, (short)0x03, this, "entertained", false));
	                            }
	                        }catch(Exception e){
	                            DataLog.logException("Exception in Terminal Update", "Terminal", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
	                        }
	                    }
	                    else
	                    {
	                        try{
	                            byte [] stc = setStance(Constants.STANCE_STANDING, false, true);
	                            for(int i = 0; i < vPLe.size(); i++)
	                            {
	                                Player p = vPLe.get(i);
	                                p.getClient().insertPacket(stc);
	                                p.getClient().insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)6, (short)1, (short)0x03, this, "", false));
	                            }
	
	                        }catch(Exception e){
	                            DataLog.logException("Exception in Terminal Update", "Terminal", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
	                        }
	                    }
                    }

                    switch(Desire)
                    {
                        case 85:
                        case 146:
                        case 61:
                        case 45:
                        case 60:
                        case 108:
                        case 152:
                        case 181:
                        case 120:
                        {
                            Vector<Player> vPL = getServer().getPlayersAroundNPC(this);
                            if(vPL!=null && !vPL.isEmpty())
                            {
                                if(ZoneServer.getRangeBetweenObjects(this, vPL.get(0)) <= 6)
                                {
                                    animateNPC(vPL.get(0).getClient(), "yawn");
                                }
                            }
                            break;
                        }
                        case 70:
                        case 91:
                        case 188:
                        case 129:
                        case 115:
                        case 144:
                        case 5:
                        case 75:
                        case 140:
                        case 21:
                        {
                            Vector<Player> vPL = this.getServer().getPlayersAroundNPC(this);
                            if(vPL!=null && vPL.size() !=0 )
                            {
                                if(ZoneServer.getRangeBetweenObjects(this, vPL.get(0)) <= 6)
                                {
                                    this.animateNPC(vPL.get(0).getClient(), "fiddle");
                                }
                            }
                            break;
                        }
                        case 74:
                        case 178:
                        case 126:
                        case 128:
                        case 33:
                        case 111:
                        case 14:
                        case 149:
                        case 176:
                        case 116:                    
                        case 136:
                        case 200:
                        case 164:
                        case 172:
                        case 6:
                        case 78:
                        case 31:
                        case 81:
                        case 23:
                        case 26:
                        case 54:
                         {
                           Vector<Player> vPL = this.getServer().getPlayersAroundNPC(this);
                            if(vPL!=null && vPL.size() !=0 )
                            {
                                if(ZoneServer.getRangeBetweenObjects(this, vPL.get(0)) <= 6)
                                {
                                    this.animateNPC(vPL.get(0).getClient(), "forage");
                                }
                            }
                            break;
                        }
                        case 30:
                        case 25:
                        case 112:
                        case 59:
                        case 125:
                        case 51:
                        case 92:
                        case 194:
                        case 68:
                        case 173:
                        case 28:
                        case 20:
                        case 169:
                        case 22:
                        case 130:
                        case 36:
                        case 191:
                        case 96:
                        case 185:
                         {
                            Vector<Player> vPL = this.getServer().getPlayersAroundNPC(this);
                            if(vPL!=null && vPL.size() !=0 )
                            {
                                if(ZoneServer.getRangeBetweenObjects(this, vPL.get(0)) <= 6)
                                {
                                    this.animateNPC(vPL.get(0).getClient(), "bored");
                                }
                            }
                            break;
                        }
                        case 9:
                        case 34:
                        case 98:
                        case 10:
                        case 189:
                        case 82:
                        case 62:
                        case 67:
                        case 138:
                        case 83:
                        case 167:
                        case 37:
                        case 171:
                        case 186:
                        case 32:
                        case 12:
                        case 196:
                        case 177:
                        case 101:
                        case 132:
                        case 93:
                        case 17:
                        case 8:
                        case 197:
                        case 46:
                        case 118:
                        case 29:
                        case 77:
                        case 88:
                        {
                           Vector<Player> vPL = this.getServer().getPlayersAroundNPC(this);
                            if(vPL!=null && vPL.size() !=0 )
                            {
                                if(ZoneServer.getRangeBetweenObjects(this, vPL.get(0)) <= 6)
                                {
                                    this.speakNPC(vPL.get(0).getClient(), "Man when will this shift be over!", (short)this.getMoodID(), (short)this.getMoodID());
                                    this.animateNPC(vPL.get(0).getClient(), "stretch");
                                }
                            }
                            break;
                        }    
                        case 155:
                        case 86:
                        case 18:
                        case 35:
                        case 80:
                        case 40:
                        case 11:
                        case 50:
                        case 127:
                        case 121:
                        case 113: 
                        case 131:
                        case 135:
                        case 76:
                        case 150:
                        case 58:
                        case 139:
                        case 43:
                        case 184:
                        case 114:
                        {
                            Vector<Player> vPL = this.getServer().getPlayersAroundNPC(this);
                            if(vPL!=null && vPL.size() !=0 )
                            {
                                if(ZoneServer.getRangeBetweenObjects(this, vPL.get(0)) <= 6)
                                {
                                    this.speakNPC(vPL.get(0).getClient(), "Nice to See you again " + vPL.get(0).getFirstName() + " how are things going?", (short)this.getMoodID(), (short)this.getMoodID());
                                    this.animateNPC(vPL.get(0).getClient(), "wave");
                                }
                            }
                            break;
                        }    
                        default:
                        {
                            //System.out.println("NPC Desire: " + Desire + " Not Matched");
                        }
                    }
                }
            }
    }
    
    
    /**
     * This is the Terminal Use Item Request. 
     * @Override
     * @param client - ZoneClient requesting the Use Terminal Request.
     */
    
    public void useItem(ZoneClient client, byte commandID){
       // System.out.println("Terminal useItem Executing");
    	Player player = client.getPlayer();
        switch(this.iTerminalType)
        {
            
            case Constants.TERMINAL_TYPES_TRAVEL_GENERAL:
            {               
                if(!ZoneServer.isInRange(client.getPlayer(), this, 6f))
                {
                 try{
                     client.insertPacket(PacketFactory.buildChatSystemMessage(
                     		"player_structure",
                     		"must_be_admin",
                     		0l,
                     		null,
                     		null,
                     		null,
                     		0l,
                     		null,
                     		null,
                     		null,
                     		0l,
                     		null,
                     		null,
                     		null,
                     		0,
                     		0f, false));

                        return;
                    }catch(Exception e){
                        System.out.println("Exception Caught in Terminal Range Check " + e);
                        e.printStackTrace();
                    }
                }
               // System.out.println("Entering Ticket Purchase Mode");
                try{
                    client.getPlayer().setLastUsedTravelTerminal(this);
                    client.insertPacket(PacketFactory.buildEnterTicketPurchaseMode(client.getPlayer(), this));
                }catch(Exception e){
                    e.printStackTrace();
                    System.out.println("Exception " + e.toString());
                }
                break;
            }
            case Constants.TERMINAL_TYPES_TICKET_DROID:
            {
                if(!ZoneServer.isInRange(client.getPlayer(), this, 6f))
                {
                 try{
                	 // Your target is too far away to %TO
                     client.insertPacket(PacketFactory.buildChatSystemMessage(
                     		"cmd_err",
                     		"target_range_prose",
                     		0l,
                     		null,
                     		null,
                     		null,
                     		0l,
                     		null,
                     		null,
                     		null,
                     		0l,
                     		"ui_radial",
                     		"converse_start",
                     		null,
                     		0,
                     		0f, false));

                        return;
                    }catch(Exception e){
                        System.out.println("Exception Caught in Terminal Range Check " + e);
                        e.printStackTrace();
                    }
                }
                try{      
                    
                    System.out.println("Ticket Droid Use Selected");
                    Vector<TangibleItem> iL = client.getPlayer().getInventoryItems();
                    Vector<SOEObject> ttL = new Vector<SOEObject>();
                    int TicketCount = 0;
                    for(int i =0; i < iL.size(); i++)
                    {
                        TangibleItem Item = iL.get(i);
                        if (Item instanceof TravelTicket) {
                        	TravelTicket travelTicket = (TravelTicket)Item;
                        	TravelDestination departureDestination = travelTicket.getDepartureInformation();
                        	if (departureDestination != null) {
                        		if(departureDestination.getTicketID() == this.getTicketID())
		                            {
		                                ttL.add(travelTicket);
		                                TicketCount++;
		                            }
                        	}
                        }
                    }
                    if(!ttL.isEmpty() )
                    {
                        Shuttle S = client.getServer().getShuttleObjectByTicketID(iTicketID);
                        if(S != null)
                        {                            
                            if(S.getIsShuttleBoarding())
                            {
                            	SUIWindow w = new SUIWindow(player);
                            	w.setWindowType(Constants.SUI_TRAVEL_SELECT_TICKET);
                            	String sWindowType = "Script.listBox";
                            	//String sWindowTitle = "msgBoardShuttle";
                            	String sDataListTitle = "@travel:ticket_collector_name";
                            	String sDataListPrompt = "@travel:boarding_ticket_selection";
                            	TravelTicket[] ticketsArr = new TravelTicket[ttL.size()];
                            	ticketsArr = ttL.toArray(ticketsArr);
                            	String[] sTicketsNames = new String[ticketsArr.length];
                            	for (int i = 0; i < ticketsArr.length; i++) {
                            		sTicketsNames[i] = Constants.PlanetNames[ticketsArr[i].getArrivalInformation().getDestinationPlanet()] + " - " + ticketsArr[i].getArrivalInformation().getDestinationName();
                            	}
                            	client.insertPacket(w.SUIScriptListBox(client, sWindowType, sDataListTitle, sDataListPrompt, sTicketsNames, ttL, 0, 0));
                            	//PacketFactory.buildSUIUseTravelTicketList(client.getPlayer(), ttL));  
                            }
                            else
                            {                                    
                               long mins, boardtime, seconds;
                                boardtime = (S.getTimeToArrival());
                                System.out.println("Board time calculations.");
                                System.out.println("Milliseconds: " + boardtime);
                                seconds = (boardtime/1000);
                                System.out.println("Seconds: " + seconds);
                                mins = (seconds/60);
                                System.out.println("Minutes: " + mins);
                                seconds = seconds - (mins * 60);
                                System.out.println("Mins + seconds: " + mins + " and " + seconds);
                                // STF fileage.
                                if (boardtime <= 0) {
                                	client.insertPacket(PacketFactory.buildChatSystemMessage("The Next Shuttle is about to begin boarding."));
                                } else {
	                                if(mins >= 1)
	                                {
	                                	if (seconds != 0) {
	                                		client.insertPacket(PacketFactory.buildChatSystemMessage("The Next Shuttle Will be Boarding in " + mins  + " Minutes and " + seconds + " Seconds"));
	                                	} else {
	                                		client.insertPacket(PacketFactory.buildChatSystemMessage("The Next Shuttle Will be Boarding in " + mins  + " Minutes"));
	                                	}
	                                }
	                                else if(mins == 0 && seconds >= 1)
	                                {
	                                    client.insertPacket(PacketFactory.buildChatSystemMessage("The Next Shuttle Will be Boarding in " + seconds + " Seconds"));
	                                }
                                }                                

                            }                                                         
                            return;
                        }
                        
                    }
                    else
                    {          
                        Shuttle S = client.getServer().getShuttleObjectByTicketID(iTicketID);
                        if(S != null)
                        {
                            if(S.getIsShuttleBoarding())
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You do not have a Ticket to Board this Shuttle"));
                            }
                            else
                            {
                                long mins, boardtime, seconds;
                                boardtime = (S.getTimeToArrival());                                                                                                  
                                seconds = (boardtime/1000);
                                mins = (seconds/60);
                                seconds = seconds - (mins * 60);   
                                if (boardtime <= 0) {
                                	client.insertPacket(PacketFactory.buildChatSystemMessage("The Next Shuttle is about to begin boarding."));
                                } else {
	                                if(mins >= 1)
	                                {
	                                	if (seconds != 0) {
	                                		client.insertPacket(PacketFactory.buildChatSystemMessage("The Next Shuttle Will be Boarding in " + mins  + " Minutes and " + seconds + " Seconds"));
	                                	} else {
	                                		client.insertPacket(PacketFactory.buildChatSystemMessage("The Next Shuttle Will be Boarding in " + mins  + " Minutes"));
	                                	}
	                                }
	                                else if(mins == 0 && seconds >= 1)
	                                {
	                                    client.insertPacket(PacketFactory.buildChatSystemMessage("The Next Shuttle Will be Boarding in " + seconds + " Seconds"));
	                                }
                                }                                
                                //client.insertPacket(PacketFactory.buildChatSystemMessage("WARNING: You do not have a Ticket to Board this Shuttle.")); // Did not happen in Live.
                            }
                            return;
                        }    
                    }
                    System.out.println("Error While using travel droid. Check fo Null Shuttle at Droid " + this.iTicketID);
                }catch(Exception e){
                    System.out.println("Exception Caught in Use Ticket Droid " + e);
                    e.printStackTrace();
                }  
                break;
            }    
            case Constants.TERMINAL_TYPES_CHARACTER_BUILDER:
            {   
                if(!ZoneServer.isInRange(client.getPlayer(), this, 12f))
                {
                 try{
                        client.insertPacket(PacketFactory.buildChatSystemMessage("@"+this.getSTFFileName()+":"+this.getSTFFileIdentifier() + " is too far away to Converse"));
                        return;
                    }catch(Exception e){
                        System.out.println("Exception Caught in Terminal Range Check " + e);
                        e.printStackTrace();
                    }
                }
                System.out.println("Character Builder Terminal Selected");
                try{
                        client.insertPacket(PacketFactory.buildChatSystemMessage("Character Builder Terminal Use"));
                        SUIWindow W = new SUIWindow(player);
                        W.setWindowType(Constants.SUI_FROG_CHARACTER_BUILDER);
                        String[] sList = new String[9];
                        sList[0] = "Skills - Skills for your Character";
                        sList[1] = "Weapons - Get the Fire Power You Need!";
                        sList[2] = "Resources - Make any item with these materials";
                        sList[3] = "Deeds - Houses, Droids, Vehicles";
                        sList[4] = "Wearables - New Threads for a night out";
                        sList[5] = "Armor - Dont go on a party unprotected";
                        sList[6] = "XP - Experience To Train";    
                        sList[7] = "Credits - Moola to buy stuff!";   
                        sList[8] = "Tools - Survey, Crafting and more...";
                        
                        W.setOriginatingObject(this);
                        String WindowTypeString = "handleSUI";
                        String DataListTitle = "Character Builder";
                        String DataListPrompt = "This is the Character Builder Terminal. Select One of the following to Build Your Character.";                                               
                        client.insertPacket(W.SUIScriptListBox(client, WindowTypeString, DataListTitle,DataListPrompt, sList, null, 0, 0));
                }catch(Exception e){
                    System.out.println("Exception Caught in Use Character Builder Terminal " + e);
                    e.printStackTrace();
                }
                break;
            }
            case Constants.TERMINAL_TYPES_SKILL_TRAINER:
            {      
                
                if(ZoneServer.isInRange(client.getPlayer(), this, 6f))
                {   
                    System.out.println("Skill trainer In Use TemplateID:" + getTemplateID());
                    this.handleSkillTrainerConversationStart(client);
                }               
                break;
            }
            case Constants.TERMINAL_TYPES_TERMINAL_BANK:
            {
                if(!ZoneServer.isInRange(client.getPlayer(), this, 6f))
                {
                 try{
                        client.insertPacket(PacketFactory.buildChatSystemMessage(this.getFullName() + " is too far away to Converse"));
                        return;
                    }catch(Exception e){
                        System.out.println("Exception Caught in Terminal Range Check " + e);
                        e.printStackTrace();
                    }
                }
                if(!ZoneServer.isInRange(client.getPlayer(), this, 6f))
                {
                 try{
                        client.insertPacket(PacketFactory.buildChatSystemMessage("@"+this.getSTFFileName()+":"+this.getSTFFileIdentifier() + " is too far away to Converse"));
                        return;
                    }catch(Exception e){
                        System.out.println("Exception Caught in Terminal Range Check " + e);
                        e.printStackTrace();
                    }
                }
                try{
                    switch(commandID)
                    {
                        case 20:  // 45, 1989986645, 1, 0, 20, '', '', 3, 0
                        {
                            break;
                        }
                        case 7:   //   46, 1989986645, 2, 0, 7, '', '', 3, 0 // this should not be coming here
                        {
                            break;
                        }
                        case 107: //   47, 1989986645, 3, 1, 107, '@sui:bank_credits', '', 3, 0
                        {
                            SUIWindow W = new SUIWindow(player);
                            W.setWindowType(Constants.SUI_BANK_WINDOW);
                            W.setOriginatingObject(client.getPlayer());
                            String sWindowTypeString = "handleDepositWithdraw";
                            String sTransferBoxTitle = "@base_player:bank_title";
                            String sTransferBoxPrompt = "@base_player:bank_prompt";
                            String sFromLabel = "Cash";
                            String sToLabel = "Bank";
                            int iFromAmount = client.getPlayer().getInventoryCredits();
                            int iToAmount = client.getPlayer().getBankCredits();
                            int iConversionRatioFrom = 1; 
                            int iConversionRatioTo = 1;

                            client.insertPacket(W.SUIScriptTransferBox(client, sWindowTypeString, sTransferBoxTitle, sTransferBoxPrompt, sFromLabel, sToLabel, iFromAmount, iToAmount, iConversionRatioFrom, iConversionRatioTo));
                            break;
                        }
                        case 108: //   48, 1989986645, 4, 1, 108, '@sui:bank_items', '', 3, 0 //safety deposit
                        {                                       
                            client.insertPacket(PacketFactory.buildOpenContainerMessage(client.getPlayer().getBank(), 0));
                            client.insertPacket(PacketFactory.buildOpenContainerMessage(client.getPlayer().getInventory(), -1));
                            break;
                        }
                        case 109: //   49, 1989986645, 5, 1, 109, '@sui:bank_depositall', '', 3, 0
                        {
                            int iInvCredits = client.getPlayer().getInventoryCredits();
                            client.getPlayer().transferInventoryCreditsToBank();
                            //client.insertPacket(PacketFactory.buildFlyTextSTFMessage(client.getPlayer(), "base_player", "prose_deposit_success", "", "", iInvCredits));
                            client.insertPacket(PacketFactory.buildChatSystemMessage(
                            		"base_player",
                            		"prose_deposit_success",
                            		0,
                            		null,
                            		null,
                            		null,
                            		0,
                            		null,
                            		null,
                            		null,
                            		0,
                            		null,
                            		null,
                            		null,
                            		iInvCredits,
                            		0f,
                            		true
                            ));

                            break;
                        }
                        case 110: //   50, 1989986645, 6, 1, 110, '@sui:bank_withdrawall', '', 3, 0
                        {
                            int iBankCredits = client.getPlayer().getBankCredits();
                            client.getPlayer().transferBankCreditsToInventory();
                            //client.insertPacket(PacketFactory.buildFlyTextSTFMessage(client.getPlayer(), "base_player", "prose_withdraw_success", "", "", iBankCredits));
                            client.insertPacket(PacketFactory.buildChatSystemMessage(
                            		"base_player",
                            		"prose_withdraw_success",
                            		0,
                            		null,
                            		null,
                            		null,
                            		0,
                            		null,
                            		null,
                            		null,
                            		0,
                            		null,
                            		null,
                            		null,
                            		iBankCredits,
                            		0f,
                            		true
                            ));

                            break;
                        }
                        default:
                        {
                            client.insertPacket(PacketFactory.buildChatSystemMessage("Bank Terminal Use Clicked unhandled commandID " + (int)commandID));
                        }
                    }
                }catch(Exception e){
                    System.out.println("Exception Caught in Bank Terminal Use Item:" + e);
                    e.printStackTrace();
                }
                break;
            }  
            case Constants.TERMINAL_TYPES_STRUCTURE_MAINTENANCE:
            {
                if(!ZoneServer.isInRange(client.getPlayer(), this, 32f))
                {
                 try{
                        client.insertPacket(PacketFactory.buildChatSystemMessage("@"+this.getSTFFileName()+":"+this.getSTFFileIdentifier() + " is too far away to Converse"));
                        return;
                    }catch(Exception e){
                        System.out.println("Exception Caught in Terminal Range Check " + e);
                        e.printStackTrace();
                    }
                }
                this.handleStructureMaintenanceTerminal(client, commandID);
                break;
            }
            case Constants.TERMINAL_TYPES_STRUCTURE_SIGN:
            {
                if(!ZoneServer.isInRange(client.getPlayer(), this, 32f))
                {
                 try{
                        client.insertPacket(PacketFactory.buildChatSystemMessage("@"+this.getSTFFileName()+":"+this.getSTFFileIdentifier() + " is too far away to read"));
                        return;
                    }catch(Exception e){
                        System.out.println("Exception Caught in Terminal Range Check " + e);
                        e.printStackTrace();
                    }
                }
                if(this.getParentID() != 0)
                {
                    Structure s = (Structure)client.getServer().getObjectFromAllObjects(this.getParentID());                    
                    Player owner = (Player)client.getServer().getObjectFromAllObjects(s.getStructureOwnerID());                    
                    SUIWindow w = new SUIWindow(player);
                    w.setWindowType(Constants.SUI_STRUCTURE_SHOW_SIGN);
                    w.setOriginatingObject(this);
                    String WindowTypeString = "handleSui";
                    String WindowTitle = this.getFullName() + " Sign";
                    String WindowPromptContent = "Structure Owner: " + owner.getFullName();
                    boolean bEnableCancel = true;
                    boolean bEnableRevert = false;
                    long ObjectID = 0;
                    long PlayerID = 0;
                    client.insertPacket(w.SUIScriptMessageBox(client, WindowTypeString, WindowTitle, WindowPromptContent, bEnableCancel, bEnableRevert, ObjectID, PlayerID));
                }
                else
                {
                    SUIWindow w = new SUIWindow(player);
                    w.setWindowType(Constants.SUI_STRUCTURE_SHOW_SIGN);
                    w.setOriginatingObject(this);
                    String WindowTypeString = "handleSui";
                    String WindowTitle = "Sign";
                    String WindowPromptContent = "Structure Sign";
                    boolean bEnableCancel = false;
                    boolean bEnableRevert = false;
                    long ObjectID = 0;
                    long PlayerID = 0;
                    client.insertPacket(w.SUIScriptMessageBox(client, WindowTypeString, WindowTitle, WindowPromptContent, bEnableCancel, bEnableRevert, ObjectID, PlayerID));
                }
                break;
            }
            case Constants.TERMINAL_TYPES_TERMINAL_ELEVATOR_UP:
            case Constants.TERMINAL_TYPES_TERMINAL_ELEVATOR_DOWN:
            case Constants.TERMINAL_TYPES_TERMINAL_ELEVATOR_UP_DOWN:
            {
                this.handleElevatorAction(iTerminalType, client, commandID);
                break;
            }
            case Constants.TERMINAL_TYPES_TRAVEL_TUTORIAL:
            {
                try{
                    client.insertPacket(PacketFactory.buildObjectControllerStartingLocationsWindow(client.getPlayer(), DatabaseInterface.getStartingLocations()));
                }catch(Exception e){
                    DataLog.logException("Error Sending Starting Locations Window", "Terminal",true, false, e);
                }
                break;
            }
            case Constants.TERMINAL_TYPES_CAMP_MANAGEMENT:
            {
                Camp c = (Camp)this.getServer().getObjectFromAllObjects(this.getParentID());
                c.useItem(client, commandID);
                break;
            }
            case Constants.TERMINAL_TYPES_TUTORIAL_GREET_OFFICER:
            {
                handleTutorialGreetOfficerRequest( client, commandID);
                break;
            }
            default:
            {
                System.out.println("Terminal Type not recognized. Code needs to be written to handle use request. Type ID: " + this.iTerminalType + " " + this.getFullName() + " dbID " + this.getDBID());
            }
        }        
    }

    private void handleElevatorAction(int iElevatorTerminalType,ZoneClient client,byte commandID){
        try{
            if(client.getPlayer().getCellID() == this.getCellID())
            {
                switch(iElevatorTerminalType)
                {
                    case Constants.TERMINAL_TYPES_TERMINAL_ELEVATOR_UP:
                    {
                        switch(commandID)
                        {
                            case 0x15://21dec
                            {
                               client.getPlayer().setCellZ(client.getPlayer().getCellZ() + Constants.ELEVATOR_UP_DOWN_AMOUNT);
                               client.insertPacket(PacketFactory.buildPlayClientEffectObjectMessage(client.getPlayer(),Constants.ELEVATOR_UP_EFFECT));
                               client.insertPacket(PacketFactory.buildObjectControllerPlayerDataTransformWithParentToClient(client.getPlayer(), 0x0B));                               
                              break;  
                            }
                        }
                        break;
                    }
                    case Constants.TERMINAL_TYPES_TERMINAL_ELEVATOR_DOWN:
                    {
                        switch(commandID)
                        {
                            case 0x15://21dec
                            {
                                client.getPlayer().setCellZ(client.getPlayer().getCellZ() - Constants.ELEVATOR_UP_DOWN_AMOUNT);
                                client.insertPacket(PacketFactory.buildPlayClientEffectObjectMessage(client.getPlayer(),Constants.ELEVATOR_DOWN_EFFECT));
                                client.insertPacket(PacketFactory.buildObjectControllerPlayerDataTransformWithParentToClient(client.getPlayer(),  0x0B));                               
                                break;
                            }
                        }
                        break;
                    }
                    case Constants.TERMINAL_TYPES_TERMINAL_ELEVATOR_UP_DOWN:
                    {
                        switch(commandID)
                        {
                            case 0x15://21dec
                            {
                               client.getPlayer().setCellZ(client.getPlayer().getCellZ() + Constants.ELEVATOR_UP_DOWN_AMOUNT);
                               client.insertPacket(PacketFactory.buildPlayClientEffectObjectMessage(client.getPlayer(),Constants.ELEVATOR_UP_EFFECT));
                               client.insertPacket(PacketFactory.buildObjectControllerPlayerDataTransformWithParentToClient(client.getPlayer(), 0x0B));                               
                              break;  
                            }
                            case 0x16://22dec
                            {
                                client.getPlayer().setCellZ(client.getPlayer().getCellZ() - Constants.ELEVATOR_UP_DOWN_AMOUNT);
                                client.insertPacket(PacketFactory.buildPlayClientEffectObjectMessage(client.getPlayer(),Constants.ELEVATOR_DOWN_EFFECT));
                                client.insertPacket(PacketFactory.buildObjectControllerPlayerDataTransformWithParentToClient(client.getPlayer(),  0x0B));                               
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            else
            {
                client.insertPacket(PacketFactory.buildChatSystemMessage("You must be in the elevator to use the controls."));
            }
        }catch(Exception e){
            System.out.println("Exception Caught in Terminal.handleElevatorAction " + e );
            e.printStackTrace();
        }
    }
    
    private void handleSkillTrainerConversationStart(ZoneClient c){
        {                    
               // float facingAngle = this.absoluteBearingDegrees(client.getPlayer(), this.getID());
               // System.out.println("Facing Angle Degrees: " + facingAngle);
               // System.out.println("Facing Angle Radians: " + (this.absoluteBearingRadians(client.getPlayer(), this.getID())));

        	// Had to reverse the arguments in this function call, due to changes made to SOEObject.absoluteBearingDegrees
        	//this.setMovementAngle((this.absoluteBearingRadians(c.getPlayer(), this.getID())));
        		//setMovementAngle(absoluteBearingRadians(this, c.getPlayer()));
        	setMovementAngle(absoluteBearingRadians(c.getPlayer(), this));
                //System.out.println("Angle Set At: " + this.getMovementAngle());
                //protected void updatePosition(int moveCounter,           float newN,             float newS,            float newE,             float newW, float newX, float newZ, float newY, float fVelocity,long cellID) throws IOException {
               // this.setNewOrientations(facingAngle);
                
                    try{
                        this.updateAngle(c);
                        
                        System.out.println("Skill Trainer Dialog Requested for " + this.getFullName() + " TemplateID: " + this.getTemplateID());
                        Vector<DialogOption> DO = new Vector<DialogOption>();
                        DialogOption O = new DialogOption(true,"skill_teacher","opt1_1");//What skills can I learn right now?
                        DO.add(O);
                        O = new DialogOption(true,"skill_teacher","opt1_2");//What skills will I be able to learn next?
                        DO.add(O);
                        //O = new DialogOption(true,"skill_teacher","opt1_3");//Skills you have that I do not...
                        //DO.add(O);
                        //O = new DialogOption(true,"skill_teacher","opt1_4");//All of your skills...
                        //DO.add(O);
                        //O = new DialogOption(true,"skill_teacher","back");
                        //DO.add(O);
                        
                        String ConversationSTFFile = "skill_teacher";
                        String ConversationSTFString = "";
                        String ArgumentSTFFile = "";
                        String ArgumentSTFStringName = "";
                        String ArgumentInt = "0";
                        int Counter = 0;
                        String NonArgument = "";
                        String AnimationString = "beckon";
                        
                        switch(this.getTemplateID())
                        {                    
                            case 3698: //3698, 'object/mobile/shared_dressed_1handsword_trainer_01.iff'
                            case 3699: //3699, 'object/mobile/shared_dressed_1handsword_trainer_02.iff'
                            case 3700: //3700, 'object/mobile/shared_dressed_1handsword_trainer_03.iff'
                            {
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("combat_brawler_1handmelee_04")))
                                {
                                    ConversationSTFString = "trainer_1hsword";     
                                    
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                              
                                    O = new DialogOption(true,"skl_n","combat_brawler_1handmelee_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }

                                break;
                            }
                            case 3701: //3701, 'object/mobile/shared_dressed_2handsword_trainer_01.iff'
                            case 3702: //3702, 'object/mobile/shared_dressed_2handsword_trainer_02.iff'
                            case 3703: //3703, 'object/mobile/shared_dressed_2handsword_trainer_03.iff'
                            {
                                     
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("combat_brawler_2handmelee_04")))
                                {
                                    ConversationSTFString = "trainer_2hsword";  
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","combat_brawler_2handmelee_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 3722: //3722, 'object/mobile/shared_dressed_architect_trainer_01.iff'
                            case 3723: //3723, 'object/mobile/shared_dressed_architect_trainer_02.iff'
                            case 3724: //3724, 'object/mobile/shared_dressed_architect_trainer_03.iff'
                            {
                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("crafting_artisan_engineering_04")))
                                {
                                    ConversationSTFString = "trainer_architect";   
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","crafting_artisan_engineering_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 3725: //3725, 'object/mobile/shared_dressed_armorsmith_trainer_01.iff'
                            case 3726: //3726, 'object/mobile/shared_dressed_armorsmith_trainer_02.iff'
                            case 3727: //3727, 'object/mobile/shared_dressed_armorsmith_trainer_03.iff'
                            {                                    
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("crafting_artisan_engineering_04")))
                                {
                                    ConversationSTFString = "trainer_armorsmith";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","crafting_artisan_engineering_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 3728: //3728, 'object/mobile/shared_dressed_artisan_trainer_01.iff'
                            case 3729: //3729, 'object/mobile/shared_dressed_artisan_trainer_02.iff'
                            case 3730: //3730, 'object/mobile/shared_dressed_artisan_trainer_03.iff'
                            {
                                ConversationSTFString = "trainer_artisan";    
                                if(this.getIFFFileName().contains("trainer_02")){
                                    AnimationString = "curtsey";
                                }                           
                                
                                break;
                            }
                            case 3791: //3791, 'object/mobile/shared_dressed_bioengineer_trainer_01.iff'
                            case 3792: //3792, 'object/mobile/shared_dressed_bioengineer_trainer_02.iff'
                            case 3793: //3793, 'object/mobile/shared_dressed_bioengineer_trainer_03.iff'
                            {
                                  
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("outdoors_scout_harvest_04")) && c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("science_medic_crafting_04")))
                                {                                    
                                    ConversationSTFString = "trainer_bioengineer"; 
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","outdoors_scout_harvest_04");
                                    DO.add(O);          
                                    O = new DialogOption(true,"skl_n","science_medic_crafting_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 3861: //3861, 'object/mobile/shared_dressed_bountyhunter_trainer_01.iff'
                            case 3862: //3862, 'object/mobile/shared_dressed_bountyhunter_trainer_02.iff'
                            case 3863: //3863, 'object/mobile/shared_dressed_bountyhunter_trainer_03.iff'
                            case 3864: //3864, 'object/mobile/shared_dressed_bountyhunter_trainer_04.iff'
                            {
                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("combat_marksman_master")) && c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("outdoors_scout_movement_04")))
                                {
                                    ConversationSTFString = "trainer_bountyhunter";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","combat_marksman_master");
                                    DO.add(O);          
                                    O = new DialogOption(true,"skl_n","outdoors_scout_movement_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 3866: //3866, 'object/mobile/shared_dressed_brawler_trainer_01.iff'
                            case 3867: //3867, 'object/mobile/shared_dressed_brawler_trainer_02.iff'
                            case 3868: //3868, 'object/mobile/shared_dressed_brawler_trainer_03.iff'
                            {
                                ConversationSTFString = "trainer_brawler";                                
                                break;
                            }
                            case 3899: //3899, 'object/mobile/shared_dressed_carbine_trainer_01.iff'
                            case 3900: //3900, 'object/mobile/shared_dressed_carbine_trainer_02.iff'
                            case 3901: //3901, 'object/mobile/shared_dressed_carbine_trainer_03.iff'
                            {
                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("combat_marksman_carbine_04")))
                                {
                                    ConversationSTFString = "trainer_carbine";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","combat_marksman_carbine_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 3909: //3909, 'object/mobile/shared_dressed_chef_trainer_devaronian_male_01.iff'
                            case 3910: //3910, 'object/mobile/shared_dressed_chef_trainer_human_female_01.iff'
                            case 3911: //3911, 'object/mobile/shared_dressed_chef_trainer_human_male_01.iff'
                            {
                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("crafting_artisan_domestic_04")))
                                {
                                    ConversationSTFString = "trainer_chef";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    O = new DialogOption(true,"skl_n","crafting_artisan_domestic_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 3932: //3932, 'object/mobile/shared_dressed_combatmedic_trainer_human_female_01.iff'
                            case 3933: //3933, 'object/mobile/shared_dressed_combatmedic_trainer_human_male_01.iff'
                            case 3934: //3934, 'object/mobile/shared_dressed_combatmedic_trainer_rodian_male_01.iff'
                            {                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("combat_marksman_support_04")) && c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("science_medic_master")))
                                {
                                    ConversationSTFString = "trainer_combatmedic";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","combat_marksman_support_04");
                                    DO.add(O);          
                                    O = new DialogOption(true,"skl_n","science_medic_master");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 3936: //3936, 'object/mobile/shared_dressed_commando_trainer_human_male_01.iff'
                            case 3937: //3937, 'object/mobile/shared_dressed_commando_trainer_rodian_male_01.iff'
                            case 3938: //3938, 'object/mobile/shared_dressed_commando_trainer_trandoshan_male_01.iff'
                            {
                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("combat_marksman_master")) && c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("combat_brawler_unarmed_04")))
                                {
                                    ConversationSTFString = "trainer_commando";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","combat_marksman_master");
                                    DO.add(O);          
                                    O = new DialogOption(true,"skl_n","combat_brawler_unarmed_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 4131: //4131, 'object/mobile/shared_dressed_creaturehandler_trainer_human_male_01.iff'
                            case 4132: //4132, 'object/mobile/shared_dressed_creaturehandler_trainer_rodian_female_01.iff'
                            case 4133: //4133, 'object/mobile/shared_dressed_creaturehandler_trainer_zabrak_male_01.iff'
                            {                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("outdoors_scout_movement_04")) && c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("outdoors_scout_harvest_04")))
                                {
                                    ConversationSTFString = "trainer_creaturehandler";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","outdoors_scout_movement_04");
                                    DO.add(O);          
                                    O = new DialogOption(true,"skl_n","outdoors_scout_harvest_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 4174: //4174, 'object/mobile/shared_dressed_dancer_trainer_human_female_01.iff'
                            case 4175: //4175, 'object/mobile/shared_dressed_dancer_trainer_human_female_02.iff'
                            case 4176: //4176, 'object/mobile/shared_dressed_dancer_trainer_twk_female_01.iff'
                            {
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("social_entertainer_dance_04")) && c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("social_entertainer_healing_04")))
                                {
                                    ConversationSTFString = "trainer_dancer";   
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","social_entertainer_dance_04");
                                    DO.add(O);          
                                    O = new DialogOption(true,"skl_n","social_entertainer_healing_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 4249: //4249, 'object/mobile/shared_dressed_doctor_trainer_human_female_01.iff'
                            case 4250: //4250, 'object/mobile/shared_dressed_doctor_trainer_moncal_female_01.iff'
                            case 4251: //4251, 'object/mobile/shared_dressed_doctor_trainer_moncal_male_01.iff'
                            {
                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("science_medic_master")) )
                                {
                                    ConversationSTFString = "trainer_doctor";   
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","science_medic_master");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 4259: //4259, 'object/mobile/shared_dressed_droidengineer_trainer_human_male_01.iff'
                            case 4260: //4260, 'object/mobile/shared_dressed_droidengineer_trainer_moncal_male_01.iff'
                            case 4261: //4261, 'object/mobile/shared_dressed_droidengineer_trainer_rodian_male_01.iff'
                            {
                                
                                 if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("crafting_artisan_engineering_04")) )
                                {
                                    ConversationSTFString = "trainer_droidengineer";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","crafting_artisan_engineering_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 4283: //4283, 'object/mobile/shared_dressed_entertainer_trainer_human_female_01.iff'
                            case 4284: //4284, 'object/mobile/shared_dressed_entertainer_trainer_twk_female_01.iff'
                            case 4285: //4285, 'object/mobile/shared_dressed_entertainer_trainer_twk_male_01.iff'
                            {
                                ConversationSTFString = "trainer_entertainer";                            
                                break;
                            }                        
                            case 4440: //4440, 'object/mobile/shared_dressed_image_designer_trainer_01.iff'
                            case 4441: //4441, 'object/mobile/shared_dressed_image_designer_trainer_02.iff'
                            case 4442: //4442, 'object/mobile/shared_dressed_image_designer_trainer_03.iff'
                            {
                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("social_entertainer_hairstyle_04")) )
                                {
                                    ConversationSTFString = "trainer_imagedesigner";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","social_entertainer_hairstyle_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            /**
                             * @todo setup imeprial trainers by name
                             */
                            case 4472: //4472, 'object/mobile/shared_dressed_imperial_trainer_alozen.iff'
                            case 4473: //4473, 'object/mobile/shared_dressed_imperial_trainer_holfheim.iff'
                            case 4474: //4474, 'object/mobile/shared_dressed_imperial_trainer_oberhaur.iff'
                            {
                                break;
                            }
                           
                            case 4588: //4588, 'object/mobile/shared_dressed_marksman_trainer_01.iff'
                            case 4589: //4589, 'object/mobile/shared_dressed_marksman_trainer_02.iff'
                            case 4590: //4590, 'object/mobile/shared_dressed_marksman_trainer_03.iff'
                            {
                                ConversationSTFString = "trainer_marksman";
                                
                                break;
                            }
                            case 4621: //4621, 'object/mobile/shared_dressed_medic_trainer_01.iff'
                            case 4622: //4622, 'object/mobile/shared_dressed_medic_trainer_02.iff'
                            case 4623: //4623, 'object/mobile/shared_dressed_medic_trainer_03.iff'
                            {
                                ConversationSTFString = "trainer_medic";                            
                                break;
                            }
                            case 4653: //4653, 'object/mobile/shared_dressed_merchant_trainer_01.iff'
                            case 4654: //4654, 'object/mobile/shared_dressed_merchant_trainer_02.iff'
                            case 4655: //4655, 'object/mobile/shared_dressed_merchant_trainer_03.iff'
                            {
                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("crafting_artisan_business_04")) )
                                {
                                    ConversationSTFString = "trainer_merchant";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","crafting_artisan_business_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 4670: //4670, 'object/mobile/shared_dressed_musician_trainer_01.iff'
                            case 4671: //4671, 'object/mobile/shared_dressed_musician_trainer_02.iff'
                            case 4672: //4672, 'object/mobile/shared_dressed_musician_trainer_03.iff'
                            {                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("social_entertainer_music_04")) && c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("social_entertainer_healing_04")))
                                {
                                    ConversationSTFString = "trainer_musician";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","social_entertainer_music_04");
                                    DO.add(O);          
                                    O = new DialogOption(true,"skl_n","social_entertainer_healing_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 4833: //4833, 'object/mobile/shared_dressed_pistol_trainer_01.iff'
                            case 4834: //4834, 'object/mobile/shared_dressed_pistol_trainer_02.iff'
                            case 4835: //4835, 'object/mobile/shared_dressed_pistol_trainer_03.iff'
                            {
                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("combat_marksman_pistol_04")))
                                {
                                    ConversationSTFString = "trainer_pistol";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","combat_marksman_pistol_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 4843: //4843, 'object/mobile/shared_dressed_polearm_trainer_01.iff'
                            case 4844: //4844, 'object/mobile/shared_dressed_polearm_trainer_02.iff'
                            case 4845: //4845, 'object/mobile/shared_dressed_polearm_trainer_03.iff'
                            {
                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("combat_brawler_polearm_04")))
                                {
                                    ConversationSTFString = "trainer_polearm"; 
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","combat_brawler_polearm_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 4862: //4862, 'object/mobile/shared_dressed_ranger_trainer_01.iff'
                            case 4863: //4863, 'object/mobile/shared_dressed_ranger_trainer_02.iff'
                            case 4864: //4864, 'object/mobile/shared_dressed_ranger_trainer_03.iff'
                            {
                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("outdoors_scout_master")))
                                {
                                    ConversationSTFString = "trainer_ranger";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","outdoors_scout_master");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 5034: //5034, 'object/mobile/shared_dressed_rifleman_trainer_01.iff'
                            case 5035: //5035, 'object/mobile/shared_dressed_rifleman_trainer_02.iff'
                            case 5036: //5036, 'object/mobile/shared_dressed_rifleman_trainer_03.iff'
                            {
                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("combat_marksman_rifle_04")))
                                {
                                    ConversationSTFString = "trainer_rifleman";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","combat_marksman_rifle_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }                        
                            case 5065: //5065, 'object/mobile/shared_dressed_scout_trainer_01.iff'
                            case 5066: //5066, 'object/mobile/shared_dressed_scout_trainer_02.iff'
                            case 5067: //5067, 'object/mobile/shared_dressed_scout_trainer_03.iff'
                            {
                                ConversationSTFString = "trainer_scout";
                                break;
                            }
                            case 5152: //5152, 'object/mobile/shared_dressed_smuggler_trainer_01.iff'
                            case 5153: //5153, 'object/mobile/shared_dressed_smuggler_trainer_02.iff'
                            case 5154: //5154, 'object/mobile/shared_dressed_smuggler_trainer_03.iff'
                            {
                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("combat_brawler_unarmed_04")) && c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("combat_marksman_pistol_04")))
                                {
                                    ConversationSTFString = "trainer_smuggler";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","combat_brawler_unarmed_04");
                                    DO.add(O);          
                                    O = new DialogOption(true,"skl_n","combat_marksman_pistol_04");
                                    DO.add(O);                                              
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            /**
                             * @todo - Setup Rebel Trainers
                             */
                            case 5158: //5158, 'object/mobile/shared_dressed_space_rebel_trainer_brother_vrovel.iff'
                            case 5159: //5159, 'object/mobile/shared_dressed_space_rebel_trainer_lady_viopa.iff'
                            case 5160: //5160, 'object/mobile/shared_dressed_space_rebel_trainer_major_eker.iff'
                            {
                                break;
                            }
                            case 5175: //5175, 'object/mobile/shared_dressed_squad_leader_trainer_01.iff'
                            case 5176: //5176, 'object/mobile/shared_dressed_squad_leader_trainer_02.iff'
                            case 5177: //5177, 'object/mobile/shared_dressed_squad_leader_trainer_03.iff'
                            {
                                                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("outdoors_scout_movement_04")) && c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("outdoors_scout_camp_04")) && c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("combat_marksman_support_04")))
                                {
                                    ConversationSTFString = "trainer_squadleader";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","outdoors_scout_movement_04");
                                    DO.add(O);          
                                    O = new DialogOption(true,"skl_n","outdoors_scout_camp_04");
                                    DO.add(O);          
                                    O = new DialogOption(true,"skl_n","combat_marksman_support_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 5237: //5237, 'object/mobile/shared_dressed_tailor_trainer_01.iff'
                            case 5238: //5238, 'object/mobile/shared_dressed_tailor_trainer_02.iff'
                            case 5239: //5239, 'object/mobile/shared_dressed_tailor_trainer_03.iff'
                            {
                                
                                 if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("crafting_artisan_domestic_04")))
                                {
                                    ConversationSTFString = "trainer_tailor";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","crafting_artisan_domestic_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 5365: //5365, 'object/mobile/shared_dressed_unarmed_trainer_01.iff' // aka teras kasi
                            case 5366: //5366, 'object/mobile/shared_dressed_unarmed_trainer_02.iff'
                            case 5367: //5367, 'object/mobile/shared_dressed_unarmed_trainer_03.iff'
                            {
                                
                                 if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("combat_brawler_unarmed_04")))
                                {
                                    ConversationSTFString = "trainer_unarmed";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","combat_brawler_unarmed_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 5393: //5393, 'object/mobile/shared_dressed_weaponsmith_trainer_02.iff'
                            case 5394: //5394, 'object/mobile/shared_dressed_weaponsmith_trainer_03.iff'
                            {
                                
                                if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("crafting_artisan_engineering_04")))
                                {
                                    ConversationSTFString = "trainer_weaponsmith";
                                }
                                else
                                {                                
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    DO.clear();
                                    
                                    O = new DialogOption(true,"skl_n","crafting_artisan_engineering_04");
                                    DO.add(O);          
                                    ConversationSTFString = "no_qualify_prompt";                                
                                }
                                break;
                            }
                            case 4339: //4339, 'object/mobile/shared_dressed_fs_trainer.iff'
                                {
                                 if(c.getPlayer().hasSkill(c.getServer().getSkillIndexFromName("jedi")))
                                {
                                    ConversationSTFString = "trainer_fs";
                                }
                                else
                                {
                                     //SWGGui.getRandomInt(0,4);
                                    //public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
                                    //DO.clear();
                                    //ConversationSTFString = "too_complicated";
                                     ConversationSTFString = "trainer_fs";
                                }
                                break;
                            }
                            case 4491: //4491, 'object/mobile/shared_dressed_jedi_trainer_chiss_male_01.iff'
                                {
                                     ConversationSTFString = "trainer_fs";
                                break;
                            }
                            case 4492: //4492, 'object/mobile/shared_dressed_jedi_trainer_nikto_male_01.iff'
                                {                                 
                                     ConversationSTFString = "trainer_fs";
                                break;
                            }
                            case 4493: //4493, 'object/mobile/shared_dressed_jedi_trainer_old_human_male_01.iff'
                                {
                                     ConversationSTFString = "trainer_fs";
                                break;
                            }
                            case 4494: //4494, 'object/mobile/shared_dressed_jedi_trainer_twilek_female_01.iff'
                            {                 
                                    ConversationSTFString = "trainer_fs";
                                break;
                            }
                            case 4475: //4475, 'object/mobile/shared_dressed_imperial_trainer_space_01.iff'
                            case 4476: //4476, 'object/mobile/shared_dressed_imperial_trainer_space_02.iff'
                            case 4477: //4477, 'object/mobile/shared_dressed_imperial_trainer_space_03.iff'
                            case 5056: //5056, 'object/mobile/shared_dressed_rsf_tier2_trainer.iff'
                            case 5962: //5962, 'object/mobile/shared_space_shipwright_trainer_01.iff'
                            case 5963: //5963, 'object/mobile/shared_space_shipwright_trainer_02.iff'
                            case 5964: //5964, 'object/mobile/shared_space_shipwright_trainer_03.iff'
                            case 5965: //5965, 'object/mobile/shared_space_starfighter_engineer_trainer_01.iff'
                            case 5966: //5966, 'object/mobile/shared_space_starfighter_engineer_trainer_02.iff'
                            case 5967: //5967, 'object/mobile/shared_space_starfighter_engineer_trainer_03.iff'

                            default:
                            {
                                //trainer_unknown	Well then... Speak up... What might you be interested in learning about?
                                //ConversationSTFFile = "";
                                ConversationSTFString = "trainer_unknown";                            
                                System.out.println("Terminal Type not recognized. Code needs to be written to handle use request. Type ID: " + this.iTerminalType + " " + this.getFullName() + " dbID " + this.getDBID());
                            }
                        }
                        System.out.println("Sending Dialog");
                        if(this.getIFFFileName().contains("female")){
                            AnimationString = "curtsey";
                        }
                        this.animateNPC(c, AnimationString);                         
                        this.sendSkillTrainerDialog(c, ConversationSTFFile, ConversationSTFString, ArgumentSTFFile, ArgumentSTFStringName, ArgumentInt, Counter, NonArgument, DO , 0);
                      
                    }catch(Exception e){
                        System.out.println("Exception Caught in Skill Trainer " + e);
                        e.printStackTrace();
                    }
                }
    }
    protected void handleSkillTrainerConversationResponse(ZoneClient c, String[] Selections){
        System.out.println("handleSkillTrainerConversationResponse for Player: " + c.getPlayer().getFullName() + " Selections:");
        for(int i =0; i < Selections.length; i++)
        {
            System.out.println("Selection: " + i + " " + Selections[i]);
        }
        
        switch(this.getTemplateID())
        {
            case 3698: //  3698, 'object/mobile/shared_dressed_1handsword_trainer_01.iff', 'h_trainer'
            case 3699: //  3699, 'object/mobile/shared_dressed_1handsword_trainer_02.iff', 'h_trainer'
            case 3700: //  3700, 'object/mobile/shared_dressed_1handsword_trainer_03.iff', 'h_trainer'
                { this.handleSkillTrainerConversation(c, Selections, Constants.ONEHANDSWORD_SKILLS,"trainer_1hsword");  break; }
            case 3701: //  3701, 'object/mobile/shared_dressed_2handsword_trainer_01.iff', 'h_trainer'
            case 3702: //  3702, 'object/mobile/shared_dressed_2handsword_trainer_02.iff', 'h_trainer'
            case 3703: //  3703, 'object/mobile/shared_dressed_2handsword_trainer_03.iff', 'h_trainer'
                { this.handleSkillTrainerConversation(c, Selections, Constants.TWOHANDSWORD_SKILLS,"trainer_2hsword");  break; }
            case 3722: //  3722, 'object/mobile/shared_dressed_architect_trainer_01.iff', 'architect_trainer'
            case 3723: //  3723, 'object/mobile/shared_dressed_architect_trainer_02.iff', 'architect_trainer'
            case 3724: //  3724, 'object/mobile/shared_dressed_architect_trainer_03.iff', 'architect_trainer'
                { this.handleSkillTrainerConversation(c, Selections, Constants.ARCHITECH_SKILLS,"trainer_architect");  break; }
            case 3725: //  3725, 'object/mobile/shared_dressed_armorsmith_trainer_01.iff', 'armorsmith_trainer'
            case 3726: //  3726, 'object/mobile/shared_dressed_armorsmith_trainer_02.iff', 'armorsmith_trainer'
            case 3727: //  3727, 'object/mobile/shared_dressed_armorsmith_trainer_03.iff', 'armorsmith_trainer'
                { this.handleSkillTrainerConversation(c, Selections, Constants.ARMORSMITH_SKILLS,"trainer_armorsmith");  break; }
            case 3728: //  3728, 'object/mobile/shared_dressed_artisan_trainer_01.iff', 'artisan_trainer'
            case 3729: //  3729, 'object/mobile/shared_dressed_artisan_trainer_02.iff', 'artisan_trainer'
            case 3730: //  3730, 'object/mobile/shared_dressed_artisan_trainer_03.iff', 'artisan_trainer'
                { this.handleSkillTrainerConversation(c, Selections, Constants.ARTISAN_SKILLS,"trainer_artisan");  break; }
            case 3791: //  3791, 'object/mobile/shared_dressed_bioengineer_trainer_01.iff', 'bioengineer_trainer'
            case 3792: //  3792, 'object/mobile/shared_dressed_bioengineer_trainer_02.iff', 'bioengineer_trainer'
            case 3793: //  3793, 'object/mobile/shared_dressed_bioengineer_trainer_03.iff', 'bioengineer_trainer'
                { this.handleSkillTrainerConversation(c, Selections, Constants.BIOENGINEER_SKILLS,"trainer_bioengineer");  break; }
            case 3861: //  3861, 'object/mobile/shared_dressed_bountyhunter_trainer_01.iff', 'bountyhunter_trainer'
            case 3862: //  3862, 'object/mobile/shared_dressed_bountyhunter_trainer_02.iff', 'bountyhunter_trainer'
            case 3863: //  3863, 'object/mobile/shared_dressed_bountyhunter_trainer_03.iff', 'bountyhunter_trainer'
            case 3864: //  3864, 'object/mobile/shared_dressed_bountyhunter_trainer_04.iff', 'bountyhunter_trainer'
                { this.handleSkillTrainerConversation(c, Selections, Constants.BOUNTYHUNTER_SKILLS,"trainer_bountyhunter");  break; }
            case 3866: //  3866, 'object/mobile/shared_dressed_brawler_trainer_01.iff', 'bountyhunter_trainer'
            case 3867: //  3867, 'object/mobile/shared_dressed_brawler_trainer_02.iff', 'brawler_trainer'
            case 3868: //  3868, 'object/mobile/shared_dressed_brawler_trainer_03.iff', 'brawler_trainer'
                { this.handleSkillTrainerConversation(c, Selections, Constants.BRAWLER_SKILLS,"trainer_brawler");  break; }
            case 3899: //  3899, 'object/mobile/shared_dressed_carbine_trainer_01.iff', 'carbine_trainer'
            case 3900: //  3900, 'object/mobile/shared_dressed_carbine_trainer_02.iff', 'carbine_trainer'
            case 3901: //  3901, 'object/mobile/shared_dressed_carbine_trainer_03.iff', 'carbine_trainer'
                { this.handleSkillTrainerConversation(c, Selections, Constants.CARBINE_SKILLS,"trainer_carbine");  break; }
            case 3909: //  3909, 'object/mobile/shared_dressed_chef_trainer_devaronian_male_01.iff', 'devaronian_base_male'
            case 3910: //  3910, 'object/mobile/shared_dressed_chef_trainer_human_female_01.iff', 'human_base_female'
            case 3911: //  3911, 'object/mobile/shared_dressed_chef_trainer_human_male_01.iff', 'human_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.CHEF_SKILLS,"trainer_chef");  break; }
            case 3932: //  3932, 'object/mobile/shared_dressed_combatmedic_trainer_human_female_01.iff', 'human_base_female'
            case 3933: //  3933, 'object/mobile/shared_dressed_combatmedic_trainer_human_male_01.iff', 'human_base_male'
            case 3934: //  3934, 'object/mobile/shared_dressed_combatmedic_trainer_rodian_male_01.iff', 'rodian_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.COMBATMEDIC_SKILLS,"trainer_combatmedic");  break; }
            case 3936: //  3936, 'object/mobile/shared_dressed_commando_trainer_human_male_01.iff', 'human_base_male'
            case 3937: //  3937, 'object/mobile/shared_dressed_commando_trainer_rodian_male_01.iff', 'rodian_base_male'
            case 3938: //  3938, 'object/mobile/shared_dressed_commando_trainer_trandoshan_male_01.iff', 'trandoshan_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.COMMANDO_SKILLS,"trainer_commando");  break; }
            case 4131: //  4131, 'object/mobile/shared_dressed_creaturehandler_trainer_human_male_01.iff', 'human_base_male'
            case 4132: //  4132, 'object/mobile/shared_dressed_creaturehandler_trainer_rodian_female_01.iff', 'rodian_base_female'
            case 4133: //  4133, 'object/mobile/shared_dressed_creaturehandler_trainer_zabrak_male_01.iff', 'zabrak_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.CREATUREHANDLER_SKILLS,"trainer_creaturehandler");  break; }
            case 4174: //  4174, 'object/mobile/shared_dressed_dancer_trainer_human_female_01.iff', 'human_base_female'
            case 4175: //  4175, 'object/mobile/shared_dressed_dancer_trainer_human_female_02.iff', 'human_base_female'
            case 4176: //  4176, 'object/mobile/shared_dressed_dancer_trainer_twk_female_01.iff', 'twilek_base_female'
                { this.handleSkillTrainerConversation(c, Selections, Constants.DANCER_SKILLS,"trainer_dancer");  break; }
            case 4249: //  4249, 'object/mobile/shared_dressed_doctor_trainer_human_female_01.iff', 'human_base_female'
            case 4250: //  4250, 'object/mobile/shared_dressed_doctor_trainer_moncal_female_01.iff', 'moncal_base_female'
            case 4251: //  4251, 'object/mobile/shared_dressed_doctor_trainer_moncal_male_01.iff', 'moncal_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.DOCTOR_SKILLS,"trainer_doctor");  break; }
            case 4259: //  4259, 'object/mobile/shared_dressed_droidengineer_trainer_human_male_01.iff', 'human_base_male'
            case 4260: //  4260, 'object/mobile/shared_dressed_droidengineer_trainer_moncal_male_01.iff', 'moncal_base_male'
            case 4261: //  4261, 'object/mobile/shared_dressed_droidengineer_trainer_rodian_male_01.iff', 'rodian_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.DROIDENGINEER_SKILLS,"trainer_droidengineer");  break; }
            case 4283: //  4283, 'object/mobile/shared_dressed_entertainer_trainer_human_female_01.iff', 'human_base_female'
            case 4284: //  4284, 'object/mobile/shared_dressed_entertainer_trainer_twk_female_01.iff', 'twilek_base_female'
            case 4285: //  4285, 'object/mobile/shared_dressed_entertainer_trainer_twk_male_01.iff', 'twilek_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.ENTERTAINER_SKILLS,"trainer_entertainer");  break; }            
            case 4440: //  4440, 'object/mobile/shared_dressed_image_designer_trainer_01.iff', 'human_base_female'
            case 4441: //  4441, 'object/mobile/shared_dressed_image_designer_trainer_02.iff', 'twilek_base_female'
            case 4442: //  4442, 'object/mobile/shared_dressed_image_designer_trainer_03.iff', 'zabrak_base_female'
                { this.handleSkillTrainerConversation(c, Selections, Constants.IMAGEDESIGNER_SKILLS,"trainer_imagedesigner");  break; }
            /**
             * @todo - Setup Imperial trainers, skills and constants
             */
            case 4472: //  4472, 'object/mobile/shared_dressed_imperial_trainer_alozen.iff', 'alozen'
                { this.handleSkillTrainerConversation(c, Selections, Constants.ARTISAN_SKILLS,"trainer_unknown");  break; }
            case 4473: //  4473, 'object/mobile/shared_dressed_imperial_trainer_holfheim.iff', 'holfheim'
                { this.handleSkillTrainerConversation(c, Selections, Constants.ARTISAN_SKILLS,"trainer_unknown");  break; }
            case 4474: //  4474, 'object/mobile/shared_dressed_imperial_trainer_oberhaur.iff', 'oberhaur'
                { this.handleSkillTrainerConversation(c, Selections, Constants.ARTISAN_SKILLS,"trainer_unknown");  break; }
            case 4475: //  4475, 'object/mobile/shared_dressed_imperial_trainer_space_01.iff', 'akal_colzet'
                { this.handleSkillTrainerConversation(c, Selections, Constants.ARTISAN_SKILLS,"trainer_unknown");  break; }
            case 4476: //  4476, 'object/mobile/shared_dressed_imperial_trainer_space_02.iff', 'hakasha_sireen'
                { this.handleSkillTrainerConversation(c, Selections, Constants.ARTISAN_SKILLS,"trainer_unknown");  break; }
            case 4477: //  4477, 'object/mobile/shared_dressed_imperial_trainer_space_03.iff', 'barn_sinkko'
                { this.handleSkillTrainerConversation(c, Selections, Constants.ARTISAN_SKILLS,"trainer_unknown");  break; }
            //----------------------------------------------------------------------------------------------------------------------
            /**
             * @todo - setup FS Skills and unlocking, skills and constants
             */
            //jedi or fs trainers are not randomized !!!!!
            case 4339: //  4339, 'object/mobile/shared_dressed_fs_trainer.iff', 'trandoshan_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.JEDIPADAWAN_SKILLS,"trainer_fs");  break; }
            case 4491: //  4491, 'object/mobile/shared_dressed_jedi_trainer_chiss_male_01.iff', 'chiss_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.JEDIDSJOURNEYMAN_SKILLS,"trainer_fs");  break; }
            case 4492: //  4492, 'object/mobile/shared_dressed_jedi_trainer_nikto_male_01.iff', 'nikto_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.JEDIDSMASTER_SKILLS,"trainer_fs");  break; }
            case 4493: //  4493, 'object/mobile/shared_dressed_jedi_trainer_old_human_male_01.iff', 'human_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.JEDIFS_SKILLS,"trainer_fs");  break; }
            case 4494: //  4494, 'object/mobile/shared_dressed_jedi_trainer_twilek_female_01.iff', 'twilek_base_female'
                { this.handleSkillTrainerConversation(c, Selections, Constants.JEDILSJOURNEYMAN_SKILLS,"trainer_fs");  break; }
            case 4481: //  4481, 'object/mobile/shared_dressed_industrialist_trainer_01.iff', 'twilek_base_female'
                { this.handleSkillTrainerConversation(c, Selections, Constants.JEDILSMASTER_SKILLS,"trainer_fs");  break; }            
            //end jedi trainers
            //----------------------------------------------------------------------------------------------------------------------
            case 4588: //  4588, 'object/mobile/shared_dressed_marksman_trainer_01.iff', 'rodian_base_male'
            case 4589: //  4589, 'object/mobile/shared_dressed_marksman_trainer_02.iff', 'human_base_male'
            case 4590: //  4590, 'object/mobile/shared_dressed_marksman_trainer_03.iff', 'aqualish_base_female'
                { this.handleSkillTrainerConversation(c, Selections, Constants.MARKSMAN_SKILLS,"trainer_marksman");  break; }
            case 4621: //  4621, 'object/mobile/shared_dressed_medic_trainer_01.iff', 'human_base_male'
            case 4622: //  4622, 'object/mobile/shared_dressed_medic_trainer_02.iff', 'gran_base_male'
            case 4623: //  4623, 'object/mobile/shared_dressed_medic_trainer_03.iff', 'moncal_base_female'
                { this.handleSkillTrainerConversation(c, Selections, Constants.MEDIC_SKILLS,"trainer_medic");  break; }
            case 4653: //  4653, 'object/mobile/shared_dressed_merchant_trainer_01.iff', 'human_base_male'
            case 4654: //  4654, 'object/mobile/shared_dressed_merchant_trainer_02.iff', 'human_base_female'
            case 4655: //  4655, 'object/mobile/shared_dressed_merchant_trainer_03.iff', 'twilek_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.MERCHANT_SKILLS,"trainer_merchant");  break; }
            case 4670: //  4670, 'object/mobile/shared_dressed_musician_trainer_01.iff', 'bith_base_male'
            case 4671: //  4671, 'object/mobile/shared_dressed_musician_trainer_02.iff', 'kitonak_male'
            case 4672: //  4672, 'object/mobile/shared_dressed_musician_trainer_03.iff', 'max_rebo'
                { this.handleSkillTrainerConversation(c, Selections, Constants.MUSICIAN_SKILLS,"trainer_musician");  break; }
            case 4833: //  4833, 'object/mobile/shared_dressed_pistol_trainer_01.iff', 'bothan_base_female'
            case 4834: //  4834, 'object/mobile/shared_dressed_pistol_trainer_02.iff', 'rodian_base_female'
            case 4835: //  4835, 'object/mobile/shared_dressed_pistol_trainer_03.iff', 'aqualish_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.PISTOL_SKILLS,"trainer_pistol");  break; }
            case 4843: //  4843, 'object/mobile/shared_dressed_polearm_trainer_01.iff', 'zabrak_base_male'
            case 4844: //  4844, 'object/mobile/shared_dressed_polearm_trainer_02.iff', 'trandoshan_base_male'
            case 4845: //  4845, 'object/mobile/shared_dressed_polearm_trainer_03.iff', 'devaronian_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.POLEARM_SKILLS,"trainer_polearm");  break; }
            case 4862: //  4862, 'object/mobile/shared_dressed_ranger_trainer_01.iff', 'human_base_male'
            case 4863: //  4863, 'object/mobile/shared_dressed_ranger_trainer_02.iff', 'nikto_base_male'
            case 4864: //  4864, 'object/mobile/shared_dressed_ranger_trainer_03.iff', 'sullustan_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.RANGER_SKILLS,"trainer_ranger");  break; }
            case 5034: //  5034, 'object/mobile/shared_dressed_rifleman_trainer_01.iff', 'human_base_male'
            case 5035: //  5035, 'object/mobile/shared_dressed_rifleman_trainer_02.iff', 'rodian_base_male'
            case 5036: //  5036, 'object/mobile/shared_dressed_rifleman_trainer_03.iff', 'nikto_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.RIFLEMAN_SKILLS,"trainer_rifleman");  break; }
            /**
             * @todo - Setup RSF Tier 2 trainers, skills and constants
             */
            case 5056: //  5056, 'object/mobile/shared_dressed_rsf_tier2_trainer.iff', 'human_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.ARTISAN_SKILLS,"trainer_unknown");  break; }
            case 5065: //  5065, 'object/mobile/shared_dressed_scout_trainer_01.iff', 'human_base_male'
            case 5066: //  5066, 'object/mobile/shared_dressed_scout_trainer_02.iff', 'rodian_base_male'
            case 5067: //  5067, 'object/mobile/shared_dressed_scout_trainer_03.iff', 'nikto_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.SCOUT_SKILLS,"trainer_scout");  break; }
            case 5152: //  5152, 'object/mobile/shared_dressed_smuggler_trainer_01.iff', 'nikto_base_male'
            case 5153: //  5153, 'object/mobile/shared_dressed_smuggler_trainer_02.iff', 'human_base_male'
            case 5154: //  5154, 'object/mobile/shared_dressed_smuggler_trainer_03.iff', 'rodian_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.SMUGGLER_SKILLS,"trainer_smiggler");  break; }
            /**
             * @todo - Setup Rebel Trainers, skills and constants
             */
            case 5158: //  5158, 'object/mobile/shared_dressed_space_rebel_trainer_brother_vrovel.iff', 'vrovel'
            case 5159: //  5159, 'object/mobile/shared_dressed_space_rebel_trainer_lady_viopa.iff', 'viopa'
            case 5160: //  5160, 'object/mobile/shared_dressed_space_rebel_trainer_major_eker.iff', 'eker'
                { this.handleSkillTrainerConversation(c, Selections, Constants.ARTISAN_SKILLS,"trainer_unknown");  break; }
            case 5175: //  5175, 'object/mobile/shared_dressed_squad_leader_trainer_01.iff', 'nikto_base_male'
            case 5176: //  5176, 'object/mobile/shared_dressed_squad_leader_trainer_02.iff', 'zabrak_base_male'
            case 5177: //  5177, 'object/mobile/shared_dressed_squad_leader_trainer_03.iff', 'human_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.SQUADLEADER_SKILLS,"trainer_squadleader");  break; }
            case 5237: //  5237, 'object/mobile/shared_dressed_tailor_trainer_01.iff', 'bith_base_male'
            case 5238: //  5238, 'object/mobile/shared_dressed_tailor_trainer_02.iff', 'human_base_female'
            case 5239: //  5239, 'object/mobile/shared_dressed_tailor_trainer_03.iff', 'twilek_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.TAILOR_SKILLS,"trainer_tailor");  break; }
            case 5365: //  5365, 'object/mobile/shared_dressed_unarmed_trainer_01.iff', 'zabrak_base_male'
            case 5366: //  5366, 'object/mobile/shared_dressed_unarmed_trainer_02.iff', 'human_base_male'
            case 5367: //  5367, 'object/mobile/shared_dressed_unarmed_trainer_03.iff', 'nikto_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.UNARMED_SKILLS,"trainer_unarmed");  break; }
            case 5393: //  5393, 'object/mobile/shared_dressed_weaponsmith_trainer_02.iff', 'human_base_male'
            case 5394: //  5394, 'object/mobile/shared_dressed_weaponsmith_trainer_03.iff', 'moncal_base_male'
                { this.handleSkillTrainerConversation(c, Selections, Constants.WEAPONSMITH_SKILLS,"trainer_weaponsmith");  break; }
            /**
             * @todo - Setup Shipwright Skills
             */
            case 5962: //5962, 'object/mobile/shared_space_shipwright_trainer_01.iff'
            case 5963: //5963, 'object/mobile/shared_space_shipwright_trainer_02.iff'
            case 5964: //5964, 'object/mobile/shared_space_shipwright_trainer_03.iff'
                { this.handleSkillTrainerConversation(c, Selections, Constants.SHIPWRIGHT_SKILLS,"trainer_shipwright");  break; }
            /**
             * @todo - Setup Starship Trainers
             */
            case 5965: //5965, 'object/mobile/shared_space_starfighter_engineer_trainer_01.iff'
            case 5966: //5966, 'object/mobile/shared_space_starfighter_engineer_trainer_02.iff'
            case 5967: //5967, 'object/mobile/shared_space_starfighter_engineer_trainer_03.iff'
                { this.handleSkillTrainerConversation(c, Selections, Constants.ARTISAN_SKILLS,"trainer_unknown");  break; }
            default:
            {
                System.out.println("Undefined Skill trainer Conversation Response Trainer DBID:" + this.getDBID());
            }
        }
        
    }
    
    private void sendSkillTrainerDialog(ZoneClient c, String ConversationSTFFile,String ConversationSTFString , String ArgumentSTFFile , String ArgumentSTFStringName, String ArgumentInt, int Counter ,String NonDialog ,Vector<DialogOption> DO ,int LastListIndex){
        
        String [] M = new String[7];                        
        M[0] = ConversationSTFFile;
        M[1] = ConversationSTFString;
        M[2] = ArgumentSTFFile;
        M[3] = ArgumentSTFStringName;
        M[4] = ArgumentInt;
        M[5] = Integer.toString(Counter);
        M[6] = Integer.toString(LastListIndex);

        c.getPlayer().setLastConversationNPC(this.getID());
        c.getPlayer().setLastConversationMenu(M);
        c.getPlayer().setLastConversationMenuOptions(DO);
       
        try{
            c.insertPacket(PacketFactory.buildStartNPCConversation(c.getPlayer(),this));
            c.insertPacket(PacketFactory.buildNPCConversationMessage(c.getPlayer(), ConversationSTFFile, ConversationSTFString,Integer.parseInt(ArgumentInt),NonDialog,ArgumentSTFFile,ArgumentSTFStringName, true));
            c.insertPacket(PacketFactory.buildNPCConversationOptions(c.getPlayer(), DO));
        }catch(Exception e){
            System.out.println("Error While Sending Dialog to SkillTrainer " + e);
            e.printStackTrace();
        }
    }
    
    private void handleSkillTrainerConversation(ZoneClient c, String [] Selections, String[] TrainerSkills, String MainMenu){
        Player player = c.getPlayer();
        ZoneServer server = c.getServer();
        	System.out.println("--------------------- " + System.currentTimeMillis() + " handleSkillTrainerConversation ---------------------");
        //String [] TrainerSkills = SkillSet; // Pointless
        String TrainerMainMenu = MainMenu;
        
        String [] PreviousConversationMenu = player.getLastConversationMenu();
        Vector<DialogOption> PreviousMenuOptions = player.getLastConversationMenuOptions();
        Vector<DialogOption> DO = new Vector<DialogOption>();
        DialogOption O;
        int iSelection = Integer.parseInt(Selections[0]);
        String ConversationSTFFile = "skill_teacher";
        String ConversationSTFString = "default";
        String ArgumentSTFFile = "";
        String ArgumentSTFStringName = "";
        String ArgumentInt = "0";
        int Counter = 0;
        int LastListIndex = 0;
        boolean processbackbutton = true;
        
        String NonArgument = "";
        System.out.println("BEGIN PROCESSING: Previous Conversation Menu: " + PreviousConversationMenu[1]);
        if(PreviousConversationMenu[1].equals(TrainerMainMenu))
        {
            System.out.println("Processing Trainer Main Menu: " + TrainerMainMenu);
            processbackbutton = false;
            //System.out.println("Handling Main Menu Response Selection");
            if(PreviousMenuOptions.get(iSelection).getStringSTF().equals("opt1_1")) //opt1_1	I'm interested in learning a skill.
            {
                ConversationSTFString ="msg1_1";
                DO.clear();
                int numSkillsOffered = 0;
                for(int i = 1; i < TrainerSkills.length; i++)
                {
                	if (!player.hasSkill(server.getSkillIndexFromName(TrainerSkills[i]))) {
                		if (TrainerSkills[i].contains("novice")) {
	                         O = new DialogOption(true,"skl_n",TrainerSkills[i]);
	                         DO.add(O);
	                         numSkillsOffered++;
                		} else {
                			System.out.println("Train " + TrainerSkills[i]);
                			Skills theSkill = c.getServer().getSkillFromName(TrainerSkills[i]);
	                		int[] iRequisiteSkillID = theSkill.getRequiredSkillIDs();
		                    boolean hasRequisites = true;
		                    System.out.println("Number of prerequisites: " + iRequisiteSkillID.length);
		                    for(int r = 0; r < iRequisiteSkillID.length; r++)
		                    {
	                    		System.out.println("Requisite skill ID: " + iRequisiteSkillID[r]);
		                    	if (iRequisiteSkillID[r] > 0) {
		                    		hasRequisites =  hasRequisites && player.hasSkill(iRequisiteSkillID[r]);
		                    		System.out.println("Still qualifies? " + hasRequisites);
		                    	}
		                    }
		                    
		                    // Do they have enough experience to learn the skill.
		                    PlayerExperience experience = player.getPlayData().getExperienceList().get(theSkill.getExperienceType());
		                    int iRequisiteExperienceToLearn = theSkill.getExperienceCost();
		                    // If they have the experience, check and see if they have enough.  If they DON'T have the experience, only allow training if the skill requires no experience at all.
		                    if (experience != null) {
		                    	if (experience.getExperienceName() != null) {
		                    		System.out.println("Experience cost of skill: " + iRequisiteExperienceToLearn + ", experience name: " + experience.getExperienceName());
		                    		if (iRequisiteExperienceToLearn > 0) {
				                    	System.out.println("Experience not null.  Current experience: " + experience.getCurrentExperience());
				                    	hasRequisites = hasRequisites && (experience.getCurrentExperience() >= iRequisiteExperienceToLearn);
		                    		}
		                    	}
		                    } else {
		                    	hasRequisites = (iRequisiteExperienceToLearn == 0);
		                    }
		                    if(hasRequisites)
		                    {
		                    	System.out.println("Has requisites -- add skill to list.");
		                         O = new DialogOption(true,"skl_n",TrainerSkills[i]);
		                         DO.add(O);
		                         numSkillsOffered++;
		                    } else {
		                    	System.out.println("Does NOT have requisites -- do not offer skill.");
		                    }
                		}
                	}
                }
                if (numSkillsOffered == 0) {
                	ConversationSTFString = "error_empty_category";
                }
                O = new DialogOption(true,"skill_teacher","back");
                DO.add(O);
                System.out.println("msg1_1 Options Sent:" + DO.size());
            }
            else if(PreviousMenuOptions.get(iSelection).getStringSTF().equals("opt1_2"))//opt1_2 What skills will I be able to learn next?
            {               
                System.out.println("Processing opt1_2 Response");
                ConversationSTFString ="msg2_2"; //Here are the skills I can teach you next, if you have gained enough experience...
                for(int i = 1; i < TrainerSkills.length;i++)
                {                    
                	if (!player.hasSkill(server.getSkillIndexFromName(TrainerSkills[i]))) {
                		/*if (TrainerSkills[i].contains("novice")) {
	                         O = new DialogOption(true,"skl_n",TrainerSkills[i]);
	                         DO.add(O);
                		} else {
	                		int[] iRequisiteSkillID = client.getServer().getSkillFromName(TrainerSkills[i]).getRequiredSkillIDs();
		                    boolean hasRequisites = true;                    
		                    for(int r = 0; r < iRequisiteSkillID.length; r++)
		                    {
		                    	if (iRequisiteSkillID[r] > 0) {
		                            hasRequisites = client.getPlayer().hasSkill(iRequisiteSkillID[r]);
		                    	}
		                    }
		                    if(hasRequisites)
		                    {*/
		                         O = new DialogOption(true,"skl_n",TrainerSkills[i]);
		                         DO.add(O);
		                    /*}
                		}*/
                	}
                }                
                O = new DialogOption(true,"skill_teacher","back");
                DO.add(O);
                System.out.println("msg2_2 Options Sent:" + DO.size());
            }
            else if(PreviousMenuOptions.get(iSelection).getStringSTF().equals("opt1_3"))//opt1_3	Skills you have that I do not...
            {
                ConversationSTFString ="msg2_3";//Skills that I possess that you do not?! Well, what skill are you interested in?
                
                for(int i = 1; i < TrainerSkills.length;i++)
                {                     
                    if(!player.hasSkill(c.getServer().getSkillIndexFromName(TrainerSkills[i])))
                    {
                         O = new DialogOption(true,"skl_n",TrainerSkills[i]);
                         DO.add(O);
                    }
                } 
                
                O = new DialogOption(true,"skill_teacher","back");
                DO.add(O);
                System.out.println("msg2_3 Options Sent:" + DO.size());
            }
            else if(PreviousMenuOptions.get(iSelection).getStringSTF().equals("opt1_4"))//opt1_4	All of your skills...
            {
                ConversationSTFString ="msg2_4";//All of my skills? Oh well, which one do you want to know about?
                for(int i = 1; i < TrainerSkills.length;i++)
                {
                    O = new DialogOption(true,"skl_n",TrainerSkills[i]);
                    DO.add(O);
                }
                O = new DialogOption(true,"skill_teacher","back");
                DO.add(O);
                System.out.println("msg2_4 Options Sent:" + DO.size());
            }
        }
        //else if(PreviousConversationMenu[1].equals("msg1_1") == 0 || PreviousConversationMenu[1].equals("msg2_4") == 0) //msg1_1	Heya. What kind of skills would you like to know about?
        else if(PreviousConversationMenu[1].equals("msg2_1") || PreviousConversationMenu[1].equals("msg2_2")|| PreviousConversationMenu[1].equals("msg2_3") ) //msg2_3	Skills that I possess that you do not?! Well, what skill are you interested in?
        {   
            processbackbutton = false;
            if(PreviousMenuOptions.get(iSelection).getStringSTF().equals("back"))
            {
                this.handleSkillTrainerConversationStart(c);
                return; 
            }            
            for(int i = 1; i < TrainerSkills.length; i++)
            {
                if(PreviousMenuOptions.get(iSelection).getStringSTF().equals(TrainerSkills[i]))
                {
                    ConversationSTFFile = "skl_d";
                    ConversationSTFString = TrainerSkills[i];
                    NonArgument = "";
                    //O = new DialogOption(true,"skl_n",TrainerSkills[i]);
                    //DO.add(O);
                }
            }          
            O = new DialogOption(true,"skill_teacher","back");
            DO.add(O);
        }    
        else if(PreviousConversationMenu[1].equals("msg1_1") || PreviousConversationMenu[1].equals("msg2_4")) //msg1_1	Heya. What kind of skills would you like to know about?
        //else if(PreviousConversationMenu[1].equals("msg2_1") == 0 || PreviousConversationMenu[1].equals("msg2_2") == 0 || PreviousConversationMenu[1].equals("msg2_3") == 0) //msg2_3	Skills that I possess that you do not?! Well, what skill are you interested in?
        {
            System.out.println("Processing Previous Menu: " + PreviousConversationMenu[1]);
            if(PreviousMenuOptions.get(iSelection).getStringSTF().equals("back"))
            {
                this.handleSkillTrainerConversationStart(c);
                return; 
            }
            processbackbutton = false;
                //give a list of all skills that are availabe that the player doesnt have yet if he has xp or not.
                //String sRequisites = client.getServer().getSkillFromIndex(client.getServer().getSkillIndexFromName(PreviousMenuOptions.get(iSelection).getStringSTF())).getRequisiteSkillString();
                //String [] aRequisites = sRequisites.split(",");
                int[] iRequisiteSkills = c.getServer().getSkillFromName(PreviousMenuOptions.get(iSelection).getStringSTF()).getRequiredSkillIDs();
            	boolean hasrequisites = true;
                boolean hasxprequired = true;
                boolean hascredits = true;
                boolean hasskillpoints = true;
                Skills RequestedSkill = c.getServer().getSkillFromName(PreviousMenuOptions.get(iSelection).getStringSTF());
                Hashtable<Integer, PlayerExperience> XP = player.getPlayData().getExperienceList();
                //PlayerExperience SkillXP = null;
                System.out.println("Requisite Skill Count for " + PreviousMenuOptions.get(iSelection).getStringSTF() + " is: " + iRequisiteSkills.length);
                for(int r = 0; r < iRequisiteSkills.length; r++)
                {
                	if (iRequisiteSkills[r] > 0) {
	                	//System.out.println("Required Skill: " + aRequisites[r] + " L:" + aRequisites[r].length());
	                    if(!player.hasSkill(iRequisiteSkills[r]))
	                    {
	                        System.out.println("Player does not have Requisites");
	                        String skillName = c.getServer().getSkillNameFromIndex(iRequisiteSkills[r]);
	                        if(!skillName.contains(TrainerSkills[0]) || !skillName.contains(" "))
	                        {
	                            O = new DialogOption(true,"skl_n",skillName);
	                            DO.add(O);
	                            hasrequisites = false;
	                        }
	                    }
                	}
                }
                int CurrentExperience = 0;
                int ExperienceCost = RequestedSkill.getExperienceCost();
                
                int requestedSkillXPType = RequestedSkill.getExperienceType();
                PlayerExperience exp = XP.get(requestedSkillXPType);
                if (exp != null) {
                	CurrentExperience = exp.getCurrentExperience();
                } else {
                	CurrentExperience = 0;
                }
                if(ExperienceCost == 0)
                {
                    hasxprequired = true;
                }
                
                hascredits = player.hasEnoughCredits(RequestedSkill.getCreditsCost());
                
                int SKL = player.getPlayData().getSkillPointsLeft();
                int SKLDif = 0;
                if(SKL < RequestedSkill.getPointsCost())
                {
                    SKLDif = RequestedSkill.getPointsCost() - SKL;
                }                
                System.out.println(player.getFullName() + " Skill Points Left " + SKL + " Experience for Skill " + RequestedSkill.getName() + ": " + CurrentExperience + " Exp Cost:" + RequestedSkill.getExperienceCost() + " has enough Credits: " + hascredits + " Credit Cost:" + RequestedSkill.getCreditsCost());
                
                if(hasrequisites && hasxprequired && hascredits && hasskillpoints)
                {
                    System.out.println("Player Qualifies for the skill");
                    processbackbutton = false;
                    DO.clear();
                    ConversationSTFString = "prose_cost";
                    ArgumentSTFFile = "skl_n";
                    ArgumentSTFStringName = PreviousMenuOptions.get(iSelection).getStringSTF();
                    ArgumentInt = Integer.toString(RequestedSkill.getCreditsCost());
                    O = new DialogOption(true,"skill_teacher","yes");
                    DO.add(O);
                    O = new DialogOption(true,"skill_teacher","no");
                    DO.add(O);
                    player.setSkillBeingPurchased(RequestedSkill.getSkillID());
                }
                else if(!hasrequisites)
                {
                    System.out.println("Does not have requisites");
                    ConversationSTFString = "no_qualify_prompt";
                    DO.clear();
                    for(int i = 0 ; i < iRequisiteSkills.length; i++)
                    {
                    	if (iRequisiteSkills[i] > 0) {
	                        O = new DialogOption(true,"skl_n",c.getServer().getSkillNameFromIndex(iRequisiteSkills[i]));
	                        DO.add(O);
                    	}
                    }
                    
                }
                else if(!hascredits)
                {
                    System.out.println("Does not have credits");
                    ConversationSTFString = "prose_nsf";
                    ArgumentSTFFile = "skl_n";
                    ArgumentSTFStringName = PreviousMenuOptions.get(iSelection).getStringSTF();
                    ArgumentInt = Integer.toString(RequestedSkill.getCreditsCost());
                    
                }
                else if(!hasskillpoints)
                {                   
                    System.out.println("Does not have skillpoints");
                    if(SKL == 0)
                    {
                        ConversationSTFString = "no_skill_pts";                        
                    }
                    if(SKL >= 1)
                    {
                        ConversationSTFString = "nsf_skill_pts";                        
                        ArgumentSTFFile = "skl_n";
                        ArgumentSTFStringName = PreviousMenuOptions.get(iSelection).getStringSTF();
                        ArgumentInt = Integer.toString(SKLDif);                        
                    }
                }
                else if(!hasxprequired)
                {
                    System.out.println("No XP");
                    ConversationSTFFile  = "";
                    ConversationSTFString = "You must have at least %DI %TO Experience to Train that Skill";
                    ArgumentSTFFile = "exp_n";
                    ArgumentSTFStringName = DatabaseInterface.getExperienceNameFromID(RequestedSkill.getExperienceType());
                    ArgumentInt = Integer.toString(RequestedSkill.getExperienceCost());                        
                }
                else
                {
                    System.out.println("Else");
                    ConversationSTFString = "no_qualify_prompt";
                }
                System.out.println("Adding back button");
                O = new DialogOption(true,"skill_teacher","back");
                DO.add(O);
               
                
        }
        else if(PreviousConversationMenu[1].equals("prose_cost"))
        {
        	
            processbackbutton = false;
            if(PreviousMenuOptions.get(iSelection).getStringSTF().equals("yes"))
            {
            	Skills skillToTrain = c.getServer().getSkillFromIndex(player.getSkillBeingPurchased());
                byte trainValue = player.getPlayData().canTrainSkill(skillToTrain);
                if(trainValue == Constants.SKILL_TRAIN_SUCCESS) {
                	// If they can afford it, go ahead and train it.  
                	if (player.debitCredits(skillToTrain.getCreditsCost())) {
                		player.setSkill(skillToTrain.getSkillID(), true, true);
                		player.updateExperience(skillToTrain, skillToTrain.getExperienceType(), -skillToTrain.getExperienceCost());
                        ConversationSTFString = "success";
                        O = new DialogOption(true,"skill_teacher","back");
                        DO.add(O);
                        try{                         
                            c.insertPacket(PacketFactory.buildChatSystemMessage(
                            		"skill_teacher",
                            		"prose_pay",
                            		0,
                            		null,
                            		null,
                            		null,
                            		0,
                            		null,
                            		null,
                            		null,
                            		0,
                            		"skl_n",
                            		skillToTrain.getName(),
                            		null,
                            		skillToTrain.getCreditsCost(),
                            		0f,
                            		true
                            ));
                            //c.insertPacket(PacketFactory.buildFlyTextSTFMessage(player, "skill_teacher", "prose_pay", "skl_n", skillToTrain.getName(), skillToTrain.getCreditsCost()));
	                        //c.insertPacket(PacketFactory.buildFlyTextSTFMessage(player, "skill_teacher", "prose_skill_learned", "skl_n", skillToTrain.getName(), skillToTrain.getCreditsCost()));
                            c.insertPacket(PacketFactory.buildChatSystemMessage(
                            		"skill_teacher",
                            		"prose_skill_learned",
                            		0,
                            		null,
                            		null,
                            		null,
                            		0,
                            		null,
                            		null,
                            		null,
                            		0,
                            		"skl_n",
                            		skillToTrain.getName(),
                            		null,
                            		skillToTrain.getCreditsCost(),
                            		0f,
                            		true
                            ));

                        }catch(Exception e){
                            System.out.println("Exception caught while sending fly text message skill success " + e);
                            e.printStackTrace();
                        }
                	} else {
                		ConversationSTFString = "prose_nsf";
                        ArgumentSTFFile = "skl_n";
                        ArgumentSTFStringName = skillToTrain.getName();
                        ArgumentInt = String.valueOf(skillToTrain.getCreditsCost());                        
                		
                	}
                } else if (trainValue == Constants.SKILL_TRAIN_NSF_EXPERIENCE) {
                	ArgumentSTFFile = "exp_n";
                    ConversationSTFString = "You must have at least %DI %TO Experience to Train that Skill";
                	ArgumentSTFStringName = DatabaseInterface.getExperienceNameFromID(skillToTrain.getExperienceType());
                	ArgumentInt = String.valueOf(skillToTrain.getExperienceCost());
                	
                	//insertSkill = player.getPlayData().canTrainSkill(c.getServer().getSkillFromIndex(c.getServer().getSkillIndexFromName(TrainerSkills[0])));
                } else if (trainValue == Constants.SKILL_TRAIN_NSF_POINTS) {
                	ArgumentSTFFile = "skl_n";
                	ArgumentSTFStringName = skillToTrain.getName();
                	ArgumentInt = String.valueOf(skillToTrain.getPointsCost());
                } else {
                    ConversationSTFString = "error_grant_skill";
                    O = new DialogOption(true,"skill_teacher","back");
                    DO.add(O);
                }
            }
            else if(PreviousMenuOptions.get(iSelection).getStringSTF().equals("no"))
            {                
                ConversationSTFString = "msg_no";
                O = new DialogOption(true,"skill_teacher","back");
                DO.add(O);
            }
            if(PreviousMenuOptions.get(iSelection).getStringSTF().equals("back"))
            {
                this.handleSkillTrainerConversationStart(c);
                return; 
            }
        }
        else if(PreviousConversationMenu[1].equals("msg_yes"))
        {
        	Skills skillToTrain = c.getServer().getSkillFromIndex(player.getSkillBeingPurchased());
            try{
                //c.insertPacket(PacketFactory.buildFlyTextSTFMessage(player, "skill_teacher", "prose_skill_learned", "skl_n", skillToTrain.getName(), skillToTrain.getCreditsCost()));
                c.insertPacket(PacketFactory.buildChatSystemMessage(
                		"skill_teacher",
                		"prose_skill_learned",
                		0,
                		null,
                		null,
                		null,
                		0,
                		null,
                		null,
                		null,
                		0,
                		"skl_n",
                		skillToTrain.getName(),
                		null,
                		skillToTrain.getCreditsCost(),
                		0f,
                		true
                ));

            }catch(Exception e){
                System.out.println("Exception caught while sending fly text message skill success " + e);
                e.printStackTrace();
            }            
        }
        else if(PreviousConversationMenu[1].equals("msg_no"))
        {
            this.handleSkillTrainerConversationStart(c);
            return; 
        }
        else if(PreviousConversationMenu[1].equals("success"))
        {
            this.handleSkillTrainerConversationStart(c);
            return;    
        }
        else if(PreviousConversationMenu[1].contains("Experience") )
        {
            this.handleSkillTrainerConversationStart(c);
            return;   
        }
        else if(PreviousConversationMenu[1].equals("no_qualify_prompt"))
        {
            processbackbutton = false;
            //System.out.println("Processing no_qualify_prompt Response; iSelection: " + iSelection);
            if(PreviousMenuOptions.get(iSelection).getStringSTF().equals("back"))
            {
                this.handleSkillTrainerConversationStart(c);
                return;
            }
           // System.out.println("no_qualify_prompt Redirect, Previous Menu:" + PreviousConversationMenu[1] + " iSelection:" + iSelection);
            processbackbutton = false;
            if(PreviousMenuOptions.get(iSelection) == null || PreviousMenuOptions.get(iSelection).getStringSTF().contains("Experience"))
            {
                this.handleSkillTrainerConversationStart(c);
                return;
            }
            else
            {
                //System.out.println("iSelection: " + iSelection + " PMOSize: " + PreviousMenuOptions.size());
                for(int i = 0; i < TrainerSkills.length; i++)
                {                    
                    if(PreviousMenuOptions.get(iSelection).getStringSTF().equals(TrainerSkills[i]))
                    {
                        Selections[0] = Integer.toString(iSelection);
                        PreviousConversationMenu[1] = "msg2_1";
                        player.setLastConversationMenu(PreviousConversationMenu);
                        this.handleSkillTrainerConversation(c, Selections, TrainerSkills, MainMenu);
                        return;
                    }

                }
            }
           // System.out.println("We need to handle this Selection: " + PreviousMenuOptions.get(iSelection).getStringSTF());
            
            //since the trainer does not have this skill we tell the player to talk to a skill trainer for said skill.
            String sSkillName = PreviousMenuOptions.get(iSelection).getStringSTF();
           // System.out.println("Requisite parent should be : " + sk.getName());
            
            if(sSkillName.contains("novice"))
            {
                //System.out.println("Redirecting Parent Skill");
                
                ArgumentSTFStringName = sSkillName.replace("_novice","");
                ConversationSTFFile = "";
                ConversationSTFString = "You must speak to a %TO trainer to acquire that skill";
                ArgumentSTFFile = "skl_t";
                DO.clear();                
                this.sendSkillTrainerDialog(c, ConversationSTFFile, ConversationSTFString, ArgumentSTFFile, ArgumentSTFStringName, ArgumentInt, Counter, NonArgument, DO,LastListIndex);
                return;
            }
            return;
        }
        else
        {
            System.out.println("Last Else, Previous Menu:" + PreviousConversationMenu[1] + " iSelection:" + iSelection);
            processbackbutton = false;
            for(int i = 0; i < TrainerSkills.length; i++)
            {
                if(PreviousConversationMenu[1].equals(TrainerSkills[i]))
                {
                    PreviousConversationMenu[1] = "msg2_1";
                    player.setLastConversationMenu(PreviousConversationMenu);
                    this.handleSkillTrainerConversation(c, Selections, TrainerSkills, MainMenu);
                }
                
            }
        }
        
        
       
        try{
            if(processbackbutton)
            {
                System.out.println("Procesing Back Button");
                if(PreviousMenuOptions==null){
                    this.handleSkillTrainerConversationStart(c);
                    return;    
                }
                if(PreviousMenuOptions.get(iSelection)!= null && PreviousMenuOptions.get(iSelection).getStringSTF().equals("back"))//back	Can we start again?
                {
                    this.handleSkillTrainerConversationStart(c);
                    return;                    
                }
            }
            System.out.println("Not Procesing Back Button");
        }catch(Exception e){
            System.out.println("Exception Caught handle Dialog " + e);
            e.printStackTrace();
        }
        System.out.println("Last Sending Menu: " + ConversationSTFString);
        if(ConversationSTFString.equals("default"))
        {
            return;
        }
        this.sendSkillTrainerDialog(c, ConversationSTFFile, ConversationSTFString, ArgumentSTFFile, ArgumentSTFStringName, ArgumentInt, Counter, NonArgument, DO,LastListIndex);
        
    }

    public short getUnkMissionRefreshShort() {
        return unkMissionRefreshShort;
    }

    public void setUnkMissionRefreshShort(short unkMissionRefreshShort) {
        this.unkMissionRefreshShort = unkMissionRefreshShort;
    }
        
    protected void refreshMissionList(ZoneClient client){
        try{            
            switch(iTerminalType)
            {
              
                case Constants.TERMINAL_TYPES_MISSION_GENERAL:
                {
                    Vector<MissionTemplate> myMt = getServer().getMissionTemplates(iTerminalType, getPlanetID());
                   // System.out.println("General Mission terminal Refresh Requested"); 
                    
                    //System.out.println("Templates Retrieved for this Terminal: " + myMt.size());
                   // for(int i = 0; i < myMt.size();i++)
                    //{
                     //   System.out.println("Mission ID: " + myMt.get(i).getMissionID() + " Template String: "+ myMt.get(i).getMissionStringFile());
                   // }
                    
                    int lastMissiontype = -1;
                    
                    Vector<MissionObject> vML = client.getPlayer().getMissionBag().getVMissionList();
                    if(vML!=null && !vML.isEmpty())
                    {                       
                        client.getPlayer().getMissionBag().clearMissionObjectRefreshList();                        
                        int iMaxMissions = SWGGui.getRandomInt(4,vML.size());
                        
                        // NO -- for this type of terminal, always display 5 missions.
                        iMaxMissions = 10;
                        //System.out.println("Max Missions: " + iMaxMissions);
                        for(int i = 0; i < iMaxMissions; i++)
                        {                            
                            int iRamdomMissionTemplate = 0;                            
                            iRamdomMissionTemplate = SWGGui.getRandomInt(SWGGui.getRandomInt(1,myMt.size()),myMt.size());                                                                                    
                            MissionTemplate mT = myMt.get(iRamdomMissionTemplate);
                            if(lastMissiontype == -1)
                            {
                                lastMissiontype = mT.getMissionTypeCRC();
                                //System.out.println("Setting initial Mission type");
                            }
                            else
                            {
                                int lpctr = 0;
                                //System.out.println("Randomizing Mission");
                                if(lastMissiontype == Constants.MISSION_TYPE_CRC_DESTROY)
                                {
                                    while(mT.getMissionTypeCRC() != Constants.MISSION_TYPE_CRC_DELIVER && lpctr < myMt.size())
                                    {
                                        iRamdomMissionTemplate = SWGGui.getRandomInt(SWGGui.getRandomInt(1,myMt.size()),myMt.size());
                                        mT = myMt.get(iRamdomMissionTemplate);              
                                        lpctr++;
                                    }                                    
                                }
                                else if(lastMissiontype == Constants.MISSION_TYPE_CRC_DELIVER)
                                {
                                    while(mT.getMissionTypeCRC() != Constants.MISSION_TYPE_CRC_DESTROY && lpctr < myMt.size())
                                    {
                                        iRamdomMissionTemplate = SWGGui.getRandomInt(SWGGui.getRandomInt(1,myMt.size()),myMt.size());
                                        mT = myMt.get(iRamdomMissionTemplate);                                        
                                        lpctr++;
                                    }                                    
                                }                                
                                //System.out.println("Randomizing Mission Done");
                                
                            }
                            lastMissiontype = mT.getMissionTypeCRC();
                            //System.out.println("Mission CRC:" + mT.getMissionTypeCRC() + " Deliver:" + Constants.MISSION_TYPE_CRC_DELIVER + " Destroy:" + Constants.MISSION_TYPE_CRC_DESTROY );
                            
                            int iRandomMissionID = SWGGui.getRandomInt(1,mT.getMissionNumberOfEntries());   
                            //System.out.println("Ranodm Mission ID:" + iRandomMissionID);
                            MissionObject m = vML.get(i);                               
                            //MissionObject m = m1.getMissionObjectCopy();                                                          
                            m.clearUpdates();
                            //-------------------------------------------------------------------
                            m.setMissionTargetDisplayString("");
                            m.setMissionPlanetID(this.getPlanetID());                             
                            m.setIMissionTemplateChosen(mT.getMissionID());                            
                            m.setSMissionSTFDetailIdentifier("m" + iRandomMissionID + "d");
                            m.setSMissionSTFTextIdentifier("m" + iRandomMissionID + "t");
                            m.setIDisplayObjectCRC(mT.getMissionDisplayObjectCRC());
                            m.setIMissionTypeCRC(mT.getMissionTypeCRC());
                            m.setIDiffcultyLevel(mT.getMissionDifficulty());
                            m.setSMissionSTFString("mission/" + mT.getMissionStringFile());
                            //System.out.println("Set 1");
                            if(mT.getMissionTypeCRC() == Constants.MISSION_TYPE_CRC_DELIVER)
                            {
                                m.setIMissionType(Constants.MISSION_TYPE_DELIVER);    
                                //ItemTemplate iT = getServer().getTemplateData(mT.getMissionDisplayObjectCRC());
                                if(mT.getMissionStringFile().contains("mission_deliver"))
                                {
                                    m.setMissionTargetDisplayString("@mission/" + mT.getMissionStringFile() + ":m" + iRandomMissionID + "l");
                                }
                                else if(mT.getMissionStringFile().contains("mission_npc_deliver"))
                                {
                                    m.setMissionTargetDisplayString("@mission/" + mT.getMissionStringFile() + ":m" + iRandomMissionID + "i");
                                }
                            }
                            else if(mT.getMissionTypeCRC() == Constants.MISSION_TYPE_CRC_DESTROY)
                            {
                                m.setIMissionType(Constants.MISSION_TYPE_DESTROY); 
                                int iLt = SWGGui.getRandomInt(0,mT.getMissionAllowedLairTemplates().length);
                                LairTemplate lT = getServer().getLairTemplate(mT.getMissionAllowedLairTemplates()[iLt]);
                                if(lT!=null)
                                {                                    
                                    m.setMissionTargetDisplayString("@lair_n:" + lT.getSLlairName());
                                    m.setILairTemplateChosen(lT.getILairTemplate());
                                    m.setIDisplayObjectCRC(getServer().getTemplateData(lT.getILairTemplate()).getCRC());                                    
                                }
                            }         
                            //System.out.println("Set 2");
                            m.setSMissionGiver(client.getServer().generateRandomName("human"));                         
                            if(m.getIMissionType() == Constants.MISSION_TYPE_DESTROY)
                            {
                                //System.out.println("Pos 1");
                                m.setX(this.getX());
                                m.setY(this.getY());
                                
                                if(!setMissionRandomPosition(m,128,512))
                                {
                                    System.out.println("Error Setting Random Mission Coordinates");
                                }
                            }
                            else
                            {
                                //System.out.println("Pos 2");
                                m.setX(this.getX());
                                m.setY(this.getY());
                                if(!setMissionRandomPosition(m,64,256))
                                {
                                    System.out.println("Error Setting Random Mission Coordinates");
                                }
                                MissionObject c = new MissionObject();                                    
                                c.setX(this.getX());
                                c.setY(this.getY());
                                c.setZ(0);  
                                m.setIPickupPlanetID(client.getPlayer().getPlanetID());
                                if(this.setMissionRandomPosition(c, 61, 128))
                                {
                                    m.setPickupX(c.getX());
                                    m.setPickupY(c.getY());
                                    m.setPickupZ(c.getZ());
                                }
                                else
                                {
                                    m.setPickupX(client.getPlayer().getX() + 30);
                                    m.setPickupY(client.getPlayer().getY() + 30);
                                    m.setPickupZ(client.getPlayer().getZ());
                                }
                            }  
                            //System.out.println("Set 3");
                            int iPlayerLevel = 0;
                            if(client.getPlayer().getGroupID() !=0)
                            {
                                //TODO:  Iterate through the player's group (if any) and get each player / pet's CON level for a composite mission level.
                            	
                            	
                            }
                            else
                            {
                                iPlayerLevel = client.getPlayer().getConLevel(); // The CON level represents the player's offensive output.  
                                if(iPlayerLevel == 0)
                                {
                                    iPlayerLevel = 4;
                                }
                                //System.out.println("Player Level: " + iPlayerLevel);
                                if(m.getIMissionType() == Constants.MISSION_TYPE_DELIVER)
                                {
                                    m.setIDiffcultyLevel(SWGGui.getRandomInt(2,12) + (int)(iPlayerLevel * .025) );
                                }
                                else if((m.getIMissionType() == Constants.MISSION_TYPE_DESTROY))
                                {
                                    m.setIDiffcultyLevel((SWGGui.getRandomInt(1,iPlayerLevel) * (m.getIDifficultyIdentifier() + (int)(iPlayerLevel))));
                                    if(m.getIDiffcultyLevel() > 90)
                                    {
                                        m.setIDiffcultyLevel(90);
                                    }
                                    else if(m.getIDiffcultyLevel() <= 1)
                                    {
                                        m.setIDiffcultyLevel(SWGGui.getRandomInt(1,15) + (int)(iPlayerLevel * .025) );
                                    }
                                    else if(m.getIDiffcultyLevel() == 0)
                                    {
                                        m.setIDiffcultyLevel(1);
                                    }
                                    
                                }   
                            }                            
                            m.setIMissionPayout(((m.getIDiffcultyLevel() * SWGGui.getRandomInt(1,30)) * (m.getIDifficultyIdentifier() + 1) ));                            
                            if(m.getIMissionPayout() <= 80)
                            {
                                m.setIMissionPayout(80 + SWGGui.getRandomInt(3,15));
                            }
                            
                           // System.out.println("Sending Delta for MissionID:" + m.getID() + " Giver:" + m.getSMissionGiverName() + " Index:" + i);                                                       
                            client.getPlayer().getMissionBag().addMissionObjectToRefreshList(m);                                                   
                            client.insertPacket(PacketFactory.buildMissionObjectDelta(m,false));                            
                        }
                    }
                    System.out.println("Mission List Sent");
                    break;
                }                            
                case Constants.TERMINAL_TYPES_TERMINAL_MISSION_ARTISAN: //case Constants.TERMINAL_TYPES_MISSION_CRAFTER:
                {
                    
                    Vector<MissionTemplate> myMt = getServer().getMissionTemplates(iTerminalType, getPlanetID());
                   // System.out.println("Crafter/Artisan Mission terminal Refresh Requested");                    
                    //System.out.println("Templates Retrieved for this Terminal: " + myMt.size());                                       
                    int lastMissiontype = -1;                    
                    Vector<MissionObject> vML = client.getPlayer().getMissionBag().getVMissionList();
                    if(vML!=null && !vML.isEmpty())
                    {                       
                        client.getPlayer().getMissionBag().clearMissionObjectRefreshList();                        
                        int iMaxMissions = SWGGui.getRandomInt(4,vML.size());
                        //System.out.println("Max Missions: " + iMaxMissions);
                        for(int i = 0; i < iMaxMissions; i++)
                        {                            
                            int iRandomMissionTemplate = 0;  
                            iRandomMissionTemplate = SWGGui.getRandomInt(SWGGui.getRandomInt(0,myMt.size()),myMt.size());
                           // System.out.print("Random Template ID:" + iRandomMissionTemplate);
                            MissionTemplate mT = myMt.get(iRandomMissionTemplate);
                            if(mT == null)
                            {
                                //client.insertPacket(PacketFactory.buildChatSystemMessage("Error While Retrieving Missions Contact a CSR Code: " + mT.getMissionID() + ":00" ));
                                return;
                            }
                            if(lastMissiontype == -1)
                            {                                
                                lastMissiontype = mT.getMissionTypeCRC();
                            }
                            else
                            {   int lpctr = 0;                                   
                                if(lastMissiontype == Constants.MISSION_TYPE_CRC_CRAFTING)
                                {                                    
                                    while(mT.getMissionTypeCRC() != Constants.MISSION_TYPE_CRC_SURVEY && lpctr < myMt.size())
                                    {
                                        iRandomMissionTemplate = SWGGui.getRandomInt(SWGGui.getRandomInt(0,myMt.size()),myMt.size());
                                        mT = myMt.get(iRandomMissionTemplate);                                        
                                        lpctr++;
                                    }                                                                        
                                }
                                else if(lastMissiontype == Constants.MISSION_TYPE_CRC_SURVEY)
                                {
                                    while(mT.getMissionTypeCRC() != Constants.MISSION_TYPE_CRC_CRAFTING && lpctr < myMt.size())
                                    {
                                        iRandomMissionTemplate = SWGGui.getRandomInt(SWGGui.getRandomInt(0,myMt.size()),myMt.size());
                                        mT = myMt.get(iRandomMissionTemplate);                                        
                                        lpctr++; //infinite loop prevention
                                    }                                                                        
                                }
                                
                            }
                            lastMissiontype = mT.getMissionTypeCRC();
                            //System.out.println("Mission CRC:" + mT.getMissionTypeCRC() + " Deliver:" + Constants.MISSION_TYPE_CRC_DELIVER + " Destroy:" + Constants.MISSION_TYPE_CRC_DESTROY );
                            
                            int iRandomMissionID = SWGGui.getRandomInt(1,mT.getMissionNumberOfEntries());   
                            
                            MissionObject m = vML.get(i);                               
                            //MissionObject m = m1.getMissionObjectCopy();                                                          
                            m.clearUpdates();
                            //-------------------------------------------------------------------
                            m.setMissionTargetDisplayString("");
                            m.setMissionPlanetID(this.getPlanetID());                             
                            m.setIMissionTemplateChosen(mT.getMissionID());                            
                            m.setSMissionSTFDetailIdentifier("m" + iRandomMissionID + "d");
                            m.setSMissionSTFTextIdentifier("m" + iRandomMissionID + "t");
                            MissionCollateral cT = getServer().getMissionCollateral(mT.getMissionID(), iRandomMissionID);
                            if(cT==null)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("Error While Retrieving Missions Contact a CSR Code: " + mT.getMissionID() + ":" + iRandomMissionID));
                                return;
                            }
                            m.setIDisplayObjectCRC(cT.getDisplaycrc());
                            
                            int iPlayerLevel = 0;
                            iPlayerLevel = ((250 - client.getPlayer().getPlayData().getSkillPointsLeft()) / 55);  
                            m.setSMissionSTFString("mission/" + mT.getMissionStringFile());
                            m.setIMissionTypeCRC(mT.getMissionTypeCRC());
                            if(mT.getMissionTypeCRC() == Constants.MISSION_TYPE_CRC_CRAFTING)
                            {
                                m.setIMissionType(Constants.MISSION_TYPE_CRAFTING);                                                                
                                m.setMissionTargetDisplayString("@item_n:" + cT.getDisplaytext());
                                m.setIDiffcultyLevel(mT.getMissionDifficulty());  
                                if(m.getIDiffcultyLevel() == 0)
                                {
                                    m.setIDiffcultyLevel(1);
                                }
                            }
                            else if(mT.getMissionTypeCRC() == Constants.MISSION_TYPE_CRC_SURVEY)
                            {
                                m.setIMissionType(Constants.MISSION_TYPE_SURVEY);                                 
                                //C16065F3   // 3244320243
                                m.setMissionTargetDisplayString("@mission/survey/survey_resource_names:" + cT.getDisplaytext());
                                m.setIDiffcultyLevel(SWGGui.getRandomInt(35,95));
                                m.setSMissionSTFDetailIdentifier("m" + iRandomMissionID + "o");
                            }
                            
                            m.setILairTemplateChosen(cT.getCollateralid());
                            m.setSMissionGiver(client.getServer().generateRandomName("human"));                         
                            
                            m.setX(this.getX());
                            m.setY(this.getY());
                            if(!setMissionRandomPosition(m,64,128))
                            {
                                System.out.println("Error Setting Random Mission Coordinates");
                                client.insertPacket(PacketFactory.buildChatSystemMessage("Error While Retrieving Missions Contact a CSR Code: " + mT.getMissionID() + ":" + iRandomMissionID + ":01"));
                                return;
                            }
                            MissionObject c = new MissionObject();                                    
                            c.setX(this.getX());
                            c.setY(this.getY());
                            c.setZ(0);  
                            m.setIPickupPlanetID(client.getPlayer().getPlanetID());
                            if(this.setMissionRandomPosition(c, 128, 1000))
                            {
                                m.setPickupX(c.getX());
                                m.setPickupY(c.getY());
                                m.setPickupZ(c.getZ());
                            }
                            else
                            {
                                m.setPickupX(client.getPlayer().getX() + 30);
                                m.setPickupY(client.getPlayer().getY() + 30);
                                m.setPickupZ(client.getPlayer().getZ());
                            }
                            
                            if(mT.getMissionTypeCRC() == Constants.MISSION_TYPE_CRC_CRAFTING)
                            {
                                m.setIMissionPayout( SWGGui.getRandomInt(iPlayerLevel,iPlayerLevel+5) * ((int)ZoneServer.getRangeBetweenObjects(c, m)/2));
                            }
                            else if(mT.getMissionTypeCRC() == Constants.MISSION_TYPE_CRC_SURVEY)
                            {
                                m.setIMissionPayout( m.getIDiffcultyLevel() * SWGGui.getRandomInt(3,8));
                            }                            
                            if(m.getIMissionPayout() <= 200)
                            {
                                m.setIMissionPayout(200 + SWGGui.getRandomInt(3,15));
                            }
                            
                            //System.out.println("Sending Delta for MissionID:" + m.getID() + " Giver:" + m.getSMissionGiverName() + " Index:" + i);                                                       
                            client.getPlayer().getMissionBag().addMissionObjectToRefreshList(m);                                                   
                            client.insertPacket(PacketFactory.buildMissionObjectDelta(m,false));                            
                        }
                    }                    
                    break;
                }
                case Constants.TERMINAL_TYPES_MISSION_EXPLORER://case Constants.TERMINAL_TYPES_TERMINAL_MISSION_SCOUT:                
                {
                    
                    Vector<MissionTemplate> myMt = getServer().getMissionTemplates(iTerminalType, getPlanetID());
                    //System.out.println("Explorer/Scout Mission terminal Refresh Requested");
                    //System.out.println("Templates Retrieved for this Terminal: " + myMt.size());                                       
                    int lastMissiontype = -1;                    
                    Vector<MissionObject> vML = client.getPlayer().getMissionBag().getVMissionList();
                    if(vML!=null && !vML.isEmpty())
                    {
                        client.getPlayer().getMissionBag().clearMissionObjectRefreshList();                        
                        int iMaxMissions = SWGGui.getRandomInt(4,vML.size());
                        //System.out.println("Max Missions: " + iMaxMissions);
                        for(int i = 0; i < iMaxMissions; i++)
                        {
                            int iRandomMissionTemplate = 0;  
                            iRandomMissionTemplate = SWGGui.getRandomInt(SWGGui.getRandomInt(0,myMt.size()),myMt.size());
                           // System.out.println("Random Template ID:" + iRandomMissionTemplate);
                            MissionTemplate mT = myMt.get(iRandomMissionTemplate);
                            if(mT == null)
                            {
                                //client.insertPacket(PacketFactory.buildChatSystemMessage("Error While Retrieving Missions Contact a CSR Code: " + mT.getMissionID() + ":00" ));
                                return;
                            }
                            if(lastMissiontype == -1)
                            {                                
                                lastMissiontype = mT.getMissionTypeCRC();
                            }
                            else
                            {   int lpctr = 0;                                   
                                if(lastMissiontype == Constants.MISSION_TYPE_CRC_RECON)
                                {                                    
                                    while(mT.getMissionTypeCRC() != Constants.MISSION_TYPE_CRC_HUNTING && lpctr < myMt.size())
                                    {
                                        iRandomMissionTemplate = SWGGui.getRandomInt(SWGGui.getRandomInt(0,myMt.size()),myMt.size());
                                        mT = myMt.get(iRandomMissionTemplate);                                        
                                        lpctr++;
                                    }                                                                        
                                }
                                else if(lastMissiontype == Constants.MISSION_TYPE_CRC_HUNTING)
                                {
                                    while(mT.getMissionTypeCRC() != Constants.MISSION_TYPE_CRC_RECON && lpctr < myMt.size())
                                    {
                                        iRandomMissionTemplate = SWGGui.getRandomInt(SWGGui.getRandomInt(0,myMt.size()),myMt.size());
                                        mT = myMt.get(iRandomMissionTemplate);                                        
                                        lpctr++; //infinite loop prevention
                                    }                                                                        
                                }                                
                            }
                            lastMissiontype = mT.getMissionTypeCRC();
                            //-------------------------------
                            int iRandomMissionID = SWGGui.getRandomInt(1,mT.getMissionNumberOfEntries());   
                            
                            MissionObject m = vML.get(i);                               
                            //MissionObject m = m1.getMissionObjectCopy();                                                          
                            m.clearUpdates();
                            //-------------------------------------------------------------------
                            m.setMissionTargetDisplayString("");
                            m.setMissionPlanetID(this.getPlanetID());                             
                            m.setIMissionTemplateChosen(mT.getMissionID());                            
                            
                            m.setSMissionSTFTextIdentifier("m" + iRandomMissionID + "t");
                            m.setIDisplayObjectCRC(mT.getMissionDisplayObjectCRC());
                            m.setIMissionTypeCRC(mT.getMissionTypeCRC());
                            m.setIDiffcultyLevel(mT.getMissionDifficulty());
                            m.setSMissionSTFString("mission/" + mT.getMissionStringFile());
                            if(mT.getMissionTypeCRC() == Constants.MISSION_TYPE_CRC_RECON)
                            {
                                m.setIMissionType(Constants.MISSION_TYPE_RECON);                                
                                m.setSMissionSTFDetailIdentifier("m" + iRandomMissionID + "o");
                            }
                            else if(mT.getMissionTypeCRC() == Constants.MISSION_TYPE_CRC_HUNTING)
                            {
                                m.setIMissionType(Constants.MISSION_TYPE_HUNTING); 
                                m.setSMissionSTFDetailIdentifier("m" + iRandomMissionID + "d");
                                if(mT.getMissionID() >= 111 && mT.getMissionID() <= 114 )
                                {
                                    m.setSMissionSTFDetailIdentifier("m" + iRandomMissionID + "o");
                                }
                                int iLt = SWGGui.getRandomInt(0,mT.getMissionAllowedLairTemplates().length);
                                LairTemplate lT = getServer().getLairTemplate(mT.getMissionAllowedLairTemplates()[iLt]);                                
                                if(lT!=null)
                                {                                    
                                    m.setMissionTargetDisplayString("@" + lT.getSMobNameStfFile() + ":" + lT.getSLlairName());
                                    m.setILairTemplateChosen(lT.getILairTemplate());
                                    m.setIDisplayObjectCRC(getServer().getTemplateData(lT.getILairTemplate()).getCRC()); 
                                    ItemTemplate iT = getServer().getTemplateData(lT.getIMob1Template());
                                    if(iT!=null)
                                    {
                                        m.setIDisplayObjectCRC(iT.getCRC());
                                    }
                                }                                
                            }                             
                            m.setSMissionGiver(client.getServer().generateRandomName("human"));  
                            
                            /*
                            if(m.getIMissionType() == Constants.MISSION_TYPE_HUNTING)
                            {*/
                            m.setX(this.getX());
                            m.setY(this.getY());

                            if(!setMissionRandomPosition(m,128,512))
                            {
                                System.out.println("Error Setting Random Mission Coordinates");
                            }
                          
                            int iPlayerLevel = 0;
                            if(client.getPlayer().getGroupID() !=0)
                            {
                                //for now groups are not in so to do is to calcualte based on group
                            }
                            else
                            {
                                iPlayerLevel = ((250 - client.getPlayer().getPlayData().getSkillPointsLeft()) / 55);  
                                if(iPlayerLevel == 0)
                                {
                                    iPlayerLevel = 5;
                                }
                               // System.out.println("Player Level: " + iPlayerLevel);
                                if(m.getIMissionType() == Constants.MISSION_TYPE_RECON)
                                {
                                    m.setIDiffcultyLevel(SWGGui.getRandomInt(2,12) + (int)(iPlayerLevel * .025) );
                                    m.setIMissionPayout(m.getIDiffcultyLevel() * (SWGGui.getRandomInt(2,15) * iPlayerLevel));                            
                                }
                                else if((m.getIMissionType() == Constants.MISSION_TYPE_HUNTING))
                                {
                                     m.setIDiffcultyLevel(SWGGui.getRandomInt(5,50));
                                     m.setIMissionPayout(m.getIDiffcultyLevel() * (SWGGui.getRandomInt(2,30) * iPlayerLevel));                            
                                }   
                            }                            
                            
                            
                            if(m.getIMissionPayout() <= 200)
                            {
                                m.setIMissionPayout(200 + SWGGui.getRandomInt(3,15));
                            }
                            
                            //System.out.println("Sending Delta for MissionID:" + m.getID() + " Giver:" + m.getSMissionGiverName() + " Index:" + i);                                                       
                            client.getPlayer().getMissionBag().addMissionObjectToRefreshList(m);                                                   
                            client.insertPacket(PacketFactory.buildMissionObjectDelta(m,false));                            
                        }
                    }                    
                    break;
                }                
                case Constants.TERMINAL_TYPES_TERMINAL_MISSION_ENTERTAINER: //case Constants.TERMINAL_TYPES_MISSION_ENTERTAINER:
                {                    
                    Vector<MissionTemplate> myMt = getServer().getMissionTemplates(iTerminalType, getPlanetID());
                   // System.out.println("Entertainer Mission terminal Refresh Requested Type: " + iTerminalType );
                    //System.out.println("Templates Retrieved for this Terminal: " + myMt.size());                                       
                    int lastMissiontype = -1;                    
                    Vector<MissionObject> vML = client.getPlayer().getMissionBag().getVMissionList();
                    if(vML!=null && !vML.isEmpty())
                    {
                        client.getPlayer().getMissionBag().clearMissionObjectRefreshList();                        
                        int iMaxMissions = SWGGui.getRandomInt(4,vML.size());
                        //System.out.println("Max Missions: " + iMaxMissions);
                        for(int i = 0; i < iMaxMissions; i++)
                        {
                            int iRandomMissionTemplate = 0;  
                            iRandomMissionTemplate = SWGGui.getRandomInt(SWGGui.getRandomInt(0,myMt.size()),myMt.size());
                            //System.out.println("Random Template ID:" + iRandomMissionTemplate);
                            MissionTemplate mT = myMt.get(iRandomMissionTemplate);
                            if(mT == null)
                            {
                                //client.insertPacket(PacketFactory.buildChatSystemMessage("Error While Retrieving Missions Contact a CSR Code: " + mT.getMissionID() + ":00" ));
                                return;
                            }
                            if(lastMissiontype == -1)
                            {                                
                                lastMissiontype = mT.getMissionTypeCRC();
                            }
                            else
                            {   int lpctr = 0;                                   
                                if(lastMissiontype == Constants.MISSION_TYPE_CRC_DANCER)
                                {                                    
                                    while(mT.getMissionTypeCRC() != Constants.MISSION_TYPE_CRC_MUSICIAN && lpctr < myMt.size())
                                    {
                                        iRandomMissionTemplate = SWGGui.getRandomInt(SWGGui.getRandomInt(0,myMt.size()),myMt.size());
                                        mT = myMt.get(iRandomMissionTemplate);                                        
                                        lpctr++;
                                    }                                                                        
                                }
                                else if(lastMissiontype == Constants.MISSION_TYPE_CRC_MUSICIAN)
                                {
                                    while(mT.getMissionTypeCRC() != Constants.MISSION_TYPE_CRC_DANCER && lpctr < myMt.size())
                                    {
                                        iRandomMissionTemplate = SWGGui.getRandomInt(SWGGui.getRandomInt(0,myMt.size()),myMt.size());
                                        mT = myMt.get(iRandomMissionTemplate);                                        
                                        lpctr++; //infinite loop prevention
                                    }                                                                        
                                }                                
                            }
                            lastMissiontype = mT.getMissionTypeCRC();
                            //-------------------------------
                            int iRandomMissionID = SWGGui.getRandomInt(1,mT.getMissionNumberOfEntries());                               
                            MissionObject m = vML.get(i);                               
                            //MissionObject m = m1.getMissionObjectCopy();                                                          
                            m.clearUpdates();
                            //-------------------------------------------------------------------
                            m.setMissionPlanetID(this.getPlanetID());                             
                            m.setIMissionTemplateChosen(mT.getMissionID());
                            m.setSMissionSTFTextIdentifier("m" + iRandomMissionID + "t");
                            m.setSMissionSTFDetailIdentifier("m" + iRandomMissionID + "o");
                            m.setIDisplayObjectCRC(mT.getMissionDisplayObjectCRC());
                            m.setIMissionTypeCRC(mT.getMissionTypeCRC());
                            m.setIDiffcultyLevel(mT.getMissionDifficulty());
                            m.setSMissionSTFString("mission/" + mT.getMissionStringFile());
                            m.setSMissionGiver(client.getServer().generateRandomName("human"));  
                            m.setMissionTargetDisplayString("@mission/mission_generic:dancer");
                            //lets choose an entertainer collateral, this is where the mission coordinates are.
                            
                            Vector<MissionCollateral> vMC = getServer().getMissionCollateralVector(mT.getMissionID(),m.getMissionPlanetID());
                            if(vMC != null && !vMC.isEmpty())
                            {
                                MissionCollateral mC = vMC.get(SWGGui.getRandomInt(0,vMC.size()));
                                m.setMissionTargetDisplayString(mC.getDisplaytext());
                                m.setMissionX(mC.getEntertainerX());
                                m.setMissionY(mC.getEntertainerY());
                                m.setMissionZ(mC.getEntertainerZ());
                                m.setICollateralID(mC.getCollateralid());
                                m.setMissionTargetDisplayString(mC.getDisplaytext());
                            }
                            else
                            {
                                m.setMissionX(getX());
                                m.setMissionY(getY());
                                m.setMissionZ(getZ());
                            }
                            int iSkillLvl = 0;
                            if(m.getIMissionTypeCRC() == Constants.MISSION_TYPE_CRC_MUSICIAN)
                            {
                                if(client.getPlayer().getPlayData().hasSkill(11)){iSkillLvl += 5;}
                                if(client.getPlayer().getPlayData().hasSkill(17)){iSkillLvl += 5;}
                                if(client.getPlayer().getPlayData().hasSkill(18)){iSkillLvl += 5;}
                                if(client.getPlayer().getPlayData().hasSkill(19)){iSkillLvl += 5;}
                                if(client.getPlayer().getPlayData().hasSkill(20)){iSkillLvl += 10;}
                                if(client.getPlayer().getPlayData().hasSkill(12)){iSkillLvl += 10;}
                                if(client.getPlayer().getPlayData().hasSkill(281)){iSkillLvl += 10;}
                                if(client.getPlayer().getPlayData().hasSkill(291)){iSkillLvl += 10;}
                                if(client.getPlayer().getPlayData().hasSkill(292)){iSkillLvl += 10;}
                                if(client.getPlayer().getPlayData().hasSkill(293)){iSkillLvl += 10;}
                                if(client.getPlayer().getPlayData().hasSkill(294)){iSkillLvl += 15;}                                   
                            }
                            else if(m.getIMissionTypeCRC() == Constants.MISSION_TYPE_CRC_DANCER)
                            {
                                if(client.getPlayer().getPlayData().hasSkill(11)){iSkillLvl += 5;}
                                if(client.getPlayer().getPlayData().hasSkill(21)){iSkillLvl += 5;}
                                if(client.getPlayer().getPlayData().hasSkill(22)){iSkillLvl += 5;}
                                if(client.getPlayer().getPlayData().hasSkill(23)){iSkillLvl += 5;}
                                if(client.getPlayer().getPlayData().hasSkill(24)){iSkillLvl += 10;}
                                if(client.getPlayer().getPlayData().hasSkill(12)){iSkillLvl += 10;}
                                if(client.getPlayer().getPlayData().hasSkill(272)){iSkillLvl += 10;}
                                if(client.getPlayer().getPlayData().hasSkill(273)){iSkillLvl += 10;}
                                if(client.getPlayer().getPlayData().hasSkill(274)){iSkillLvl += 10;}
                                if(client.getPlayer().getPlayData().hasSkill(275)){iSkillLvl += 10;}
                                if(client.getPlayer().getPlayData().hasSkill(263)){iSkillLvl += 15;}   
                            }
                           
                            if(m.getIDiffcultyLevel() == 0)
                            {
                                m.setIDiffcultyLevel(iSkillLvl);
                            }
                            m.setIMissionPayout(SWGGui.getRandomInt(5,45) * (iSkillLvl + 1));
                            client.getPlayer().getMissionBag().addMissionObjectToRefreshList(m);                                                   
                            client.insertPacket(PacketFactory.buildMissionObjectDelta(m,false));                            
                        }                            
                    }
                    break;
                }    
                case Constants.TERMINAL_TYPES_MISSION_BOUNTY_HUNTER:
                {
                    
                    if(client.getPlayer().getPlayData().hasSkill(528))//bounty hunter novice
                    {
                        Vector<MissionTemplate> myMt = getServer().getMissionTemplates(iTerminalType, getPlanetID());
                       // System.out.println("BH Mission terminal Refresh Requested");
                       // System.out.println("Templates Retrieved for this Terminal: " + myMt.size());
                     
                        
                        int iPlayerFaction = client.getPlayer().getFactionCRC();
                        
                        Vector<MissionObject> vML = client.getPlayer().getMissionBag().getVMissionList();
                        if(vML!=null && !vML.isEmpty())
                        {                       
                            client.getPlayer().getMissionBag().clearMissionObjectRefreshList();                        
                            int iMaxMissions = SWGGui.getRandomInt(4,vML.size());
                            //System.out.println("Max Missions: " + iMaxMissions);
                            for(int i = 0; i < iMaxMissions; i++)
                            {                            
                                int iRamdomMissionTemplate = 0;    
                                boolean proceed = false;
                                int ctr = 0;
                                while(!proceed || ctr > myMt.size())
                                {
                                    iRamdomMissionTemplate = SWGGui.getRandomInt(SWGGui.getRandomInt(1,myMt.size()),myMt.size());
                                    MissionTemplate r = myMt.get(iRamdomMissionTemplate);
                                    if(r.getMissionRequiredFaction() == iPlayerFaction || r.getMissionRequiredFaction() == 0)
                                    {
                                        proceed = true;
                                    }
                                }                                     
                                MissionTemplate mT = myMt.get(iRamdomMissionTemplate);
                                
                                int iRandomMissionID = SWGGui.getRandomInt(1,mT.getMissionNumberOfEntries());   

                                MissionObject m = vML.get(i);                               
                                //MissionObject m = m1.getMissionObjectCopy();                                                          
                                m.clearUpdates();
                                //-------------------------------------------------------------------
                                m.setMissionTargetDisplayString("");
                                m.setMissionPlanetID(this.getPlanetID());                             
                                m.setIMissionTemplateChosen(mT.getMissionID());                            
                                m.setSMissionSTFDetailIdentifier("m" + iRandomMissionID + "d");
                                m.setSMissionSTFTextIdentifier("m" + iRandomMissionID + "t");
                                m.setIDisplayObjectCRC(mT.getMissionDisplayObjectCRC());
                                m.setIMissionTypeCRC(mT.getMissionTypeCRC());
                                m.setIDiffcultyLevel(mT.getMissionDifficulty());
                                m.setSMissionSTFString("mission/" + mT.getMissionStringFile());
                                m.setIMissionType(Constants.MISSION_TYPE_BOUNTY);                               
                                
                                //m.setSMissionGiverName("");
                                if(SWGGui.getRandomInt(0,100) >= 50)
                                {
                                   m.setSMissionGiver(client.getServer().generateRandomName("human"));
                                }                                 
                                else
                                {
                                    m.setSMissionGiverName(Constants.BH_MISSION_ORIGINATOR_NAMES[SWGGui.getRandomInt(0, Constants.BH_MISSION_ORIGINATOR_NAMES.length)] );                                
                                }
                                //@mission/mission_bounty_jedi:imperial_jedi
                                if(m.getSMissionSTFString().contains("jedi"))
                                {
                                    /**
                                     * This has to be changed to set to the correct value when player bounties are included
                                     */
                                    int iRandomTarget = SWGGui.getRandomInt(1,3);
                                    switch(iRandomTarget)
                                    {
                                        case 1:
                                        {
                                            m.setMissionTargetDisplayString("@mission/mission_bounty_jedi:imperial_jedi");
                                            break;
                                        }
                                        case 2:
                                        {
                                            m.setMissionTargetDisplayString("@mission/mission_bounty_jedi:rebel_jedi");
                                            break;
                                        }
                                        case 3:                                        
                                        default:
                                        {
                                            m.setMissionTargetDisplayString("@mission/mission_bounty_jedi:neutral_jedi");
                                        }
                                    }
                                }
                                int iSkillLvl = 0;                                
                                if(client.getPlayer().getPlayData().hasSkill(528)){iSkillLvl += 2;}
                                if(client.getPlayer().getPlayData().hasSkill(530)){iSkillLvl += 3;}
                                if(client.getPlayer().getPlayData().hasSkill(531)){iSkillLvl += 4;}
                                if(client.getPlayer().getPlayData().hasSkill(532)){iSkillLvl += 6;}
                                if(client.getPlayer().getPlayData().hasSkill(533)){iSkillLvl += 8;}
                                if(client.getPlayer().getPlayData().hasSkill(534)){iSkillLvl += 3;}
                                if(client.getPlayer().getPlayData().hasSkill(535)){iSkillLvl += 4;}
                                if(client.getPlayer().getPlayData().hasSkill(536)){iSkillLvl += 6;}
                                if(client.getPlayer().getPlayData().hasSkill(537)){iSkillLvl += 8;}
                                if(client.getPlayer().getPlayData().hasSkill(538)){iSkillLvl += 3;}
                                if(client.getPlayer().getPlayData().hasSkill(539)){iSkillLvl += 4;}
                                if(client.getPlayer().getPlayData().hasSkill(540)){iSkillLvl += 6;}
                                if(client.getPlayer().getPlayData().hasSkill(541)){iSkillLvl += 8;}
                                if(client.getPlayer().getPlayData().hasSkill(542)){iSkillLvl += 3;}
                                if(client.getPlayer().getPlayData().hasSkill(543)){iSkillLvl += 4;}
                                if(client.getPlayer().getPlayData().hasSkill(544)){iSkillLvl += 6;}
                                if(client.getPlayer().getPlayData().hasSkill(545)){iSkillLvl += 8;}
                                if(client.getPlayer().getPlayData().hasSkill(529)){iSkillLvl += 14;}
                                
                                iSkillLvl += SWGGui.getRandomInt(1,8);
                                m.setIDiffcultyLevel(iSkillLvl);
                                
                                m.setIMissionPayout(((m.getIDiffcultyLevel() * SWGGui.getRandomInt(150,600)) * (m.getIDifficultyIdentifier() + 1) ));                            
                                if(m.getIMissionPayout() <= 1250)
                                {
                                    m.setIMissionPayout(1250 + SWGGui.getRandomInt(3,15));
                                }

                                //System.out.println("Sending Delta for MissionID:" + m.getID() + " Giver:" + m.getSMissionGiverName() + " Index:" + i);                                                       
                                client.getPlayer().getMissionBag().addMissionObjectToRefreshList(m);                                                   
                                client.insertPacket(PacketFactory.buildMissionObjectDelta(m,false));                            
                            }
                        }
                    }
                    else
                    {
                        client.insertPacket(PacketFactory.buildChatSystemMessage("You are not authorized to use that terminal.")); // TODO:  STF-i-fy.
                    }                    
                    break;
                }
                default:
                {
                    System.out.println("Terminal Type not recognized refreshMissionList(). Code needs to be written to handle use request. Type ID: " + this.iTerminalType + " " + this.getFullName() + " dbID " + this.getDBID());
                }
            }
        }catch(Exception e){
            System.out.println("exception in Terminal.refreshMissionList() " + e);
            e.printStackTrace();
        }
    }
    
    /**
     * This function will pick a random location on a planetary map.
     * This is for missions.
     */
    private boolean setMissionRandomPosition(MissionObject m, int iMinRange, int iMaxRange){
        
        {
            //System.out.println("Setting Random Position");
             int iRange = SWGGui.getRandomInt(iMinRange,iMaxRange);            
           
            boolean proceed = false;
            ConcurrentHashMap<Long, SOEObject> vOAO = getServer().getAllObjects();
            //System.out.println("Filtering " + vOAO.size() + " Objects");
            Enumeration <SOEObject> eEnum = vOAO.elements();
            ConcurrentHashMap<Long, SOEObject> oInPlanet = new ConcurrentHashMap<Long, SOEObject>();
            while(eEnum.hasMoreElements())
            {
                SOEObject temp = eEnum.nextElement();
                if(m.getPlanetID() == temp.getPlanetID() && !(temp instanceof Terminal) && !( ZoneServer.getRangeBetweenObjects(m, temp) < iRange))
                {
                    oInPlanet.put(temp.getID(), temp);
                }
            }
            //System.out.println("Filtered Objects " + oInPlanet.size() );
            while(!proceed )
            {
                boolean hasclearance = true;
                
                Enumeration <SOEObject> oEnum = oInPlanet.elements();
                while(oEnum.hasMoreElements())
                {
                    SOEObject o = oEnum.nextElement();
                 
                    if((ZoneServer.getRangeBetweenObjects(m, o) < iRange))
                    {                           
                        m.setMissionX(m.getMissionX() + SWGGui.getRandomInt(-32,32) );
                        m.setMissionY(m.getMissionY() + SWGGui.getRandomInt(-32,32));
                        hasclearance = false;
                        oEnum = oInPlanet.elements();
                    }                
                }
                if(hasclearance)
                {
                    proceed = true;
                }
            }
           // System.out.println("Mission Random Coords Set" + m.getX() + "," + m.getY());
            if(!getServer().setObjectHeightAndCoordinates(m))
            {
                System.out.println("Height Setting Error in Mission.setMissionRandomPosition while getting setObjectHeightAndCoordinates");
            }            
            //System.out.println("Mission Random Coords Set after Height set " + m.getX() + "," + m.getY());
            
            return true;
        }
       
    }
    
    private void handleStructureMaintenanceTerminal(ZoneClient client , byte commandID){
    	Player player = client.getPlayer();
    	try{
        switch(commandID)
        {                   //Button
                            //|  CommandID
                            //|   |
                            //|   |    STF STRING
                            //|   |     |
                            //1,  7, ''
            case 118:       //2,  118, '@player_structure:management'
            {
                break;
            }
            case 117:       //3,  117, '@player_structure:permissions'
            {
                break;
            }    
            case (byte)128: //4,  128, '@player_structure:permission_destroy'
            {
                if(myStructure.getStructureOwnerID() == client.getPlayer().getID() || client.getUpdateThread().getIsGM() || client.getUpdateThread().getIsDeveloper())
                {     
                    if(myStructure.getCountOfItemsInStructure() == 0)
                    {
                        SUIWindow w = new SUIWindow(player);
                        w.setWindowType(Constants.SUI_STRUCTURE_CONFIRM_REDEED);
                        w.setOriginatingObject(myStructure);
                        String WindowTypeString = "handleSUI";
                        String DataListTitle = myStructure.getStructureName() + "         Confirm Structure Destruction";
                        String DataListPrompt = "@player_structure:confirm_destruction_d1 \r\n @player_structure:confirm_destruction_d2 \r\n @player_structure:confirm_destruction_d3a";
                        DataListPrompt += " \\#00BB00 @player_structure:confirm_destruction_d3b";
                        DataListPrompt += " \r\n\r\n \\#EEEEEE @player_structure:confirm_destruction_d4";
                        
                        String sCanRedeed = " \\#FF0000 @player_structure:can_redeed_no_suffix ";
                        //String sColor = " \\#FF0000 ";//red
                        String sWhite = " \\#EEEEEE ";
                        if((myStructure.getConditionDamage() == 0) && (myStructure.getMaintenancePool() >= 800))
                        {
                            sCanRedeed = " \\#00BB00 @player_structure:can_redeed_yes_suffix ";
                            //sColor = " \\#00BB00 ";//green
                        }
                        String sConditionColor = " \\#FF0000 ";
                        if(myStructure.getConditionDamage()== 0)
                        {
                            sConditionColor = " \\#00BB00 ";//green
                        }
                        String sMaintColor = " \\#FF0000 ";
                        Deed d = (Deed)client.getServer().getObjectFromAllObjects(myStructure.getDeedID());
                        if(myStructure.getMaintenancePool() >= d.getRedeedfee())
                        {
                            sMaintColor = " \\#00BB00 ";
                        }
                        String sList[] = new String[6];
                        sList[0] = "@player_structure:can_redeed_alert" + sCanRedeed + sWhite; 
                        sList[1] = "@player_structure:redeed_condition " + sConditionColor + (myStructure.getMaxCondition()) + sWhite + "/" + sConditionColor + (myStructure.getMaxCondition() - myStructure.getConditionDamage());
                        sList[2] = "@player_structure:redeed_maintenance " + sMaintColor + myStructure.getMaintenancePool() + sWhite + "/" + sMaintColor + d.getRedeedfee() + sWhite;//condition
                        sList[3] = "--- Select an Option to Proceed ---";
                        sList[4] = "Yes Proceed With Redeed";
                        sList[5] = "No Cancel Redeed";
                        client.insertPacket(w.SUIScriptListBox(client, WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
                    }
                    else
                    {
                        client.insertPacket(PacketFactory.buildChatSystemMessage(
                        		"player_structure",
                        		"clear_building_for_delete",
                        		0l,
                        		null,
                        		null,
                        		null,
                        		0l,
                        		null,
                        		null,
                        		null,
                        		0l,
                        		null,
                        		null,
                        		null,
                        		0,
                        		0f,
                        		false
                        ));
                    }
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"destroy_must_be_owner",
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0,
                    		0f,
                    		false
                    ));
                }
                
                break;
            }
            case 124:       //5,  124, '@player_structure:management_status'
            {
                Player owner = (Player)client.getServer().getObjectFromAllObjects(myStructure.getStructureOwnerID());
                if(myStructure.isAdmin(client.getPlayer().getID())) 
                {
                    SUIWindow w = new SUIWindow(player);
                    w.setWindowType(Constants.SUI_STRUCTURE_SHOW_STATUS);
                    w.setOriginatingObject(myStructure);
                    String WindowTypeString = "handleSUI";
                    String DataListTitle = "@player_structure:structure_status_t";
                    String DataListPrompt = "@player_structure:structure_name_prompt " + myStructure.getStructureName();
                    String sList[];
                    Structure residence = client.getPlayer().getPlayData().getResidence();
                    if (residence != null) {
                    	if (residence.equals(myStructure)) {
                    		sList = new String[8];
                            sList[8] = "You have declared this Structure Your Residence";	
                    	} else {
                            sList = new String[7];
                        }
                    } else {
                        sList = new String[7];
                    }
                                        
                    sList[0] = "@player_structure:owner_prompt " + owner.getFullName(); //owner
                    sList[1] = "@player_structure:structure_status_t " + myStructure.getCurrentStructureStatusString(); //status private / public
                    sList[2] = "@player_structure:condition_prompt " + myStructure.getCurrentConditionString();//condition
                    sList[3] = "@player_structure:current_maint_pool " + myStructure.getCurrentStructureMaintenancePoolString(); ////maint pool int /days hours
                    sList[4] = "@player_structure:maintenance_rate_prompt " + myStructure.getIMaintenanceRate() + " cr/hr";//main rate
                    sList[5] = "@player_structure:items_in_building_prompt " + myStructure.getCountOfItemsInStructure() ;//no of items in bdg
                    sList[6] = "Storage Capcity: " + myStructure.getIStructureMaxItemsCapacity(); //max storage                
                    
                    client.insertPacket(w.SUIScriptListBox(client, WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"must_be_admin",
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0,
                    		0f, false));
                }
                break;
            }
            case (byte)129: //6,  129, '@player_structure:management_pay'
            {
                
                if(myStructure.isAdmin(client.getPlayer().getID()))
                {
                    //Player owner = (Player)client.getServer().getObjectFromAllObjects(s.getStructureOwnerID());
                    SUIWindow w = new SUIWindow(player);
                    w.setWindowType(Constants.SUI_STRUCTURE_PAY_MAINTENANCE);
                    w.setOriginatingObject(myStructure);                
                    String sTransferBoxPrompt = "@player_structure:give_maintenance";
                    String sFromLabel = "@player_structure:total_funds";
                    String sToLabel = "@player_structure:to_pay";
                    int iFromAmount = client.getPlayer().getCashOnHand();
                    int iToAmount = 0; //sent in as 0 cause you cannot withdraw from a structure.
                    int iConversionRatioFrom = 1; 
                    int iConversionRatioTo = 1;
                    client.insertPacket(w.SUIScriptTransferBox(client, sToLabel, sTransferBoxPrompt, sTransferBoxPrompt, sFromLabel, sToLabel, iFromAmount, iToAmount, iConversionRatioFrom, iConversionRatioTo));
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"must_be_admin",
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0,
                    		0f, false));

                }
                break;
            }
            case (byte)166: //7,  166, '@player_structure:management_name_structure'
            {                             
                
                if(myStructure.getStructureOwnerID() == client.getPlayer().getID())
                {
                    SUIWindow w = new SUIWindow(player); 
                    w.setWindowType(Constants.SUI_RENAME_STRUCTURE);
                    Terminal sign = myStructure.getStructureSign();
                    w.setOriginatingObject(sign);                    
                    String sCurrentName = sign.getFullName();                   
                    client.insertPacket(w.SUIScriptTextInputBox(client, "handleFilterInput","@player_structure:structure_name_prompt", "@player_structure:management_name_structure", true, "Enabled", "Visible", 127, sCurrentName));
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"must_be_owner",
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0,
                    		0f, false));

                }
                
                break;
            }
            case 127:       //8,  127, '@player_structure:management_residence'
            {
                
                if(client.getPlayer().getID() == myStructure.getStructureOwnerID())
                {
                    client.getPlayer().getPlayData().setResidence(myStructure);
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"change_residence",
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0,
                    		0f, false));

                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"must_be_owner",
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0,
                    		0f, false));

                }
                break;
            }
            case 121:       //9,  121, '@player_structure:permission_admin'
            case (byte)201: //list admins
            {
                        
                if(myStructure.isAdmin(client.getPlayer().getID()))
                {
                    SUIWindow w = new SUIWindow(player);
                    w.setWindowType(Constants.SUI_STRUCTURE_SHOW_ADMIN_LIST);
                    w.setOriginatingObject(myStructure);
                    String WindowTypeString = "handleSUI";
                    String DataListTitle = "ADMIN LIST";
                    String DataListPrompt = "This is a list of Admins for this Structure";
                    String sList[] = new String[myStructure.getVAdminList().size()];
                    for(int i =0; i < myStructure.getVAdminList().size();i++)
                    {
                        Player p = client.getServer().getPlayer(myStructure.getVAdminList().get(i));
                        sList[i] = p.getFullName();
                    }                    
                    client.insertPacket(w.SUIScriptListBox(client, WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
                    
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"must_be_admin",
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0,
                    		0f, false));

                }
                break;
            }
            case 119:       //10, 119, '@player_structure:permission_enter'
            case (byte)204:    
            {
               
                if(myStructure.isAdmin(client.getPlayer().getID()))
                {
                    SUIWindow w = new SUIWindow(player);
                    w.setWindowType(Constants.SUI_STRUCTURE_SHOW_ENTRY_LIST);
                    w.setOriginatingObject(myStructure);
                    String WindowTypeString = "handleSUI";
                    String DataListTitle = "ENTRY LIST";
                    String DataListPrompt = "This is the Entry list for this Structure";
                    String sList[] = new String[myStructure.getVEnterList().size()];
                    for(int i =0; i < myStructure.getVEnterList().size();i++)
                    {
                        Player p = client.getServer().getPlayer(myStructure.getVEnterList().get(i));
                        sList[i] = p.getFullName();
                    }                    
                    client.insertPacket(w.SUIScriptListBox(client, WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"must_be_admin",
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0,
                    		0f, false));

                }
                break;
            }
            case 120:       //11, 120, '@player_structure:permission_banned'
            case (byte)207:
            {
                
                if(myStructure.isAdmin(client.getPlayer().getID()))
                {
                    SUIWindow w = new SUIWindow(player);
                    w.setWindowType(Constants.SUI_STRUCTURE_SHOW_BANNED_LIST);
                    w.setOriginatingObject(myStructure);
                    String WindowTypeString = "handleSUI";
                    String DataListTitle = "BAN LIST";
                    String DataListPrompt = "This is the Ban list for this Structure";
                    String sList[] = new String[myStructure.getVBanList().size()];
                    for(int i =0; i < myStructure.getVBanList().size();i++)
                    {
                        Player p = client.getServer().getPlayer(myStructure.getVBanList().get(i));
                        sList[i] = p.getFullName();
                    }                    
                    client.insertPacket(w.SUIScriptListBox(client, WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"must_be_admin",
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0,
                    		0f, false));

                }
                break;
            }
            case (byte)202:// add admin
            {
                
                if(myStructure.isAdmin(client.getPlayer().getID()))
                {
                    SUIWindow w = new SUIWindow(player);    
                    w.setWindowType(Constants.SUI_STRUCTURE_ADD_PLAYER_TO_ADMIN);
                    w.setOriginatingObject(myStructure);                                        
                    client.insertPacket(w.SUIScriptTextInputBox(client, "handleFilterInput","Enter a player name to add to the Admin List.\r\n\r\nNote: Owners are added to the Admin List By Default.", "ADD ADMIN PERMISSION", true, "Enabled", "Visible", 127, ""));
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"must_be_owner",
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0,
                    		0f, false));

                }
                
                break;
            }
            case (byte)203:// remove admin
            {
                
                if(myStructure.isAdmin(client.getPlayer().getID()))
                {
                    SUIWindow w = new SUIWindow(player);
                    w.setWindowType(Constants.SUI_STRUCTURE_REMOVE_ADMIN);
                    w.setOriginatingObject(myStructure);
                    String WindowTypeString = "handleSUI";
                    String DataListTitle = "REMOVE ADMIN";
                    String DataListPrompt = "This is a list of Admins for this Structure. Select an Entry to remove from the Admin List.\r\n\r\nNOTE: Owners Cannot Be Removed from this list.";
                    String sList[] = new String[myStructure.getVAdminList().size()];
                    for(int i =0; i < myStructure.getVAdminList().size();i++)
                    {
                        Player p = client.getServer().getPlayer(myStructure.getVAdminList().get(i));
                        sList[i] = p.getFullName();
                    }                    
                    client.insertPacket(w.SUIScriptListBox(client, WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"must_be_admin",
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0,
                    		0f, false));

                }
                break;
            }
            case (byte)205:// add entry
            {
                
                if(myStructure.isAdmin(client.getPlayer().getID()))
                {
                    SUIWindow w = new SUIWindow(player);      
                    w.setWindowType(Constants.SUI_STRUCTURE_ADD_PLAYER_TO_ENTRY);
                    w.setOriginatingObject(myStructure);                                        
                    client.insertPacket(w.SUIScriptTextInputBox(client, "handleFilterInput","Enter a player name to add to the Entry List.\r\n\r\nNote: Admins and Owners are added to this list by default.", "ADD ENTRY PERMISSION", true, "Enabled", "Visible", 127, ""));
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"must_be_owner",
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0,
                    		0f, false));

                }
                
                break;
            }
            case (byte)206:// remove entry
            {
                
                if(myStructure.isAdmin(client.getPlayer().getID()))
                {
                    SUIWindow w = new SUIWindow(player);
                    w.setWindowType(Constants.SUI_STRUCTURE_REMOVE_ENTRY);
                    w.setOriginatingObject(myStructure);
                    String WindowTypeString = "handleSUI";
                    String DataListTitle = "REMOVE ENTRY";
                    String DataListPrompt = "This is the Entry list for this Structure. Select an Entry to remove from the list.\r\n\r\nNOTE: Owners and Admins Cannot Be Removed from this list.";
                    String sList[] = new String[myStructure.getVEnterList().size()];
                    for(int i =0; i < myStructure.getVEnterList().size();i++)
                    {
                        Player p = client.getServer().getPlayer(myStructure.getVEnterList().get(i));
                        sList[i] = p.getFullName();
                    }                    
                    client.insertPacket(w.SUIScriptListBox(client, WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"must_be_admin",
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0,
                    		0f, false));

                }
                break;
            }    
            case (byte)208:// add ban
            {
               
                if(myStructure.isAdmin(client.getPlayer().getID()))
                {
                    SUIWindow w = new SUIWindow(player);          
                    w.setWindowType(Constants.SUI_STRUCTURE_ADD_PLAYER_TO_BAN);
                    w.setOriginatingObject(myStructure);                                        
                    client.insertPacket(w.SUIScriptTextInputBox(client, "handleFilterInput","Enter a player name to add to the Ban List.\r\n\r\nNote: Admins and Owners Cannot be Added to the Ban List.", "ADD BAN ENTRY", true, "Enabled", "Visible", 127, ""));
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"must_be_owner",
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0,
                    		0f, false));

                }
                
                break;
            }
            case (byte)209:// remove ban
            {
                
                if(myStructure.isAdmin(client.getPlayer().getID()))
                {
                    SUIWindow w = new SUIWindow(player);
                    w.setWindowType(Constants.SUI_STRUCTURE_REMOVE_BAN);
                    w.setOriginatingObject(myStructure);
                    String WindowTypeString = "handleSUI";
                    String DataListTitle = "REMOVE BAN";
                    String DataListPrompt = "This is the Ban list for this Structure. Select an Entry to remove from the list.";
                    String sList[] = new String[myStructure.getVBanList().size()];
                    for(int i =0; i < myStructure.getVBanList().size();i++)
                    {
                        Player p = client.getServer().getPlayer(myStructure.getVBanList().get(i));
                        sList[i] = p.getFullName();
                    }                    
                    client.insertPacket(w.SUIScriptListBox(client, WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"must_be_admin",
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0,
                    		0f, false));

                }
                break;
            }
            case 122:       //12,122,@player_structure:structure_status_t //public or private
            {
                
                if(myStructure.getStructureOwnerID() == client.getPlayer().getID())
                {
                    if(myStructure.getStructureStatus() == Constants.STRUCTURE_PERMISSIONS_PRIVATE)
                    {
                        client.insertPacket(PacketFactory.buildChatSystemMessage(
                        		"player_structure",
                        		"structure_now_public",
                        		0l,
                        		null,
                        		null,
                        		null,
                        		0l,
                        		null,
                        		null,
                        		null,
                        		0l,
                        		null,
                        		null,
                        		null,
                        		0,
                        		0f, false));

                        myStructure.setStructureStatus(Constants.STRUCTURE_PERMISSIONS_PUBLIC);
                    }
                    else if(myStructure.getStructureStatus() == Constants.STRUCTURE_PERMISSIONS_PUBLIC)
                    {
                        client.insertPacket(PacketFactory.buildChatSystemMessage(
                        		"player_structure",
                        		"structure_now_private",
                        		0l,
                        		null,
                        		null,
                        		null,
                        		0l,
                        		null,
                        		null,
                        		null,
                        		0l,
                        		null,
                        		null,
                        		null,
                        		0,
                        		0f, false));

                        myStructure.setStructureStatus(Constants.STRUCTURE_PERMISSIONS_PRIVATE);
                    } 
                    myStructure.updateStructureCellPermissions(client);                    
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"must_be_owner",
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0l,
                    		null,
                    		null,
                    		null,
                    		0,
                    		0f, false));

                }
                
                break;
            }
            default:
            {
                client.insertPacket(PacketFactory.buildChatSystemMessage("Command ID: " + (int)commandID + " Not Implemented."));
                System.out.println("Unhandled command ID in Terminal.handleStructureMaintenanceTerminal commandID:" + (int)commandID);
            }
        }
        }catch(Exception e){
            System.out.println("Exception caught in Terminal.handleStructureMaintenanceTerminal() " + e);
            e.printStackTrace();
        }
        
       
    }
    
     protected void renameTerminal(ZoneClient client , String newName){
         try{
        //System.out.println("Terminal Rename requested To: " + newName);
        if(newName.contains("\\#"))
        {
            //\#FF0000Shards of the Force\#ffffff\#FF8C00 SWG Dev House\#FFFFFF
            //System.out.println("Name Had Color Codes in it.");
            int stlen = newName.length();
                        
            String sOriginalName = newName;
            
            for(int i = 0; i < stlen; i++)
            {
                int pos = newName.indexOf("\\#");
                if(pos != -1)
                {
                    //System.out.println("Pos" + pos);
                    if(pos == 0)
                    {
                        newName = newName.substring(pos+8);
                    }
                    else
                    {
                        String t = newName.substring(0,pos);
                        String r = t + newName.substring(pos+8);
                        newName = r;                        
                    }                    
                    //System.out.println("newName:" + newName);
                }                
            }
            if(client.getServer().profanityCheck(newName))
            {
                this.setFirstName(sOriginalName);
                this.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_CYCLE);
                client.getPlayer().addDelayedSpawnObject(this, System.currentTimeMillis() + 500);
                client.insertPacket(PacketFactory.buildChatSystemMessage(
                		"player_structure",
                		"structure_renamed",
                		0l,
                		null,
                		null,
                		null,
                		0l,
                		null,
                		null,
                		null,
                		0l,
                		null,
                		null,
                		null,
                		0,
                		0f, false));

                client.getServer().getGUI().getDB().updatePlayer(client.getPlayer(), false,false);
            }
            else
            {
                //System.out.println("NameDeclined");
                client.insertPacket(PacketFactory.buildChatSystemMessage(
                		"player_structure",
                		"obscene",
                		0l,
                		null,
                		null,
                		null,
                		0l,
                		null,
                		null,
                		null,
                		0l,
                		null,
                		null,
                		null,
                		0,
                		0f, false));

            }
        }
        else if(client.getServer().profanityCheck(newName))
        {
            //System.out.println("NameAccepted");            
            this.setFirstName(newName);
            this.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_CYCLE);
            client.getPlayer().addDelayedSpawnObject(this, System.currentTimeMillis() + 500);
            client.insertPacket(PacketFactory.buildChatSystemMessage(
            		"player_structure",
            		"structure_renamed",
            		0l,
            		null,
            		null,
            		null,
            		0l,
            		null,
            		null,
            		null,
            		0l,
            		null,
            		null,
            		null,
            		0,
            		0f, false));

            client.getServer().getGUI().getDB().updatePlayer(client.getPlayer(), false,false);
        }   
        else
        {
            //System.out.println("NameDeclined");
            client.insertPacket(PacketFactory.buildChatSystemMessage(
            		"player_structure",
            		"obscene",
            		0l,
            		null,
            		null,
            		null,
            		0l,
            		null,
            		null,
            		null,
            		0l,
            		null,
            		null,
            		null,
            		0,
            		0f, false));

        }
         }catch(Exception e){
             System.out.println("Exception Caught in Terminal.renameTerminal() " + e);
             e.printStackTrace();
         }
     }

     private void handleTutorialGreetOfficerRequest(ZoneClient client, byte commandID){
            
            switch(commandID)
            {
                case 25://converse
                {
                    String ConversationSTFFile = "newbie_tutorial/newbie_convo";
                    String ConversationSTFString = "convo_1_start";
                    String ArgumentSTFFile = "";
                    String ArgumentSTFStringName = "";
                    String ArgumentInt = "0";
                    int Counter = 0;
                    String NonDialog = "";
                    Vector<DialogOption> DO = new Vector<DialogOption>();
                    DialogOption o = new DialogOption(true, "newbie_tutorial/newbie_convo", "convo_1_reply_1");
                    DO.add(o);
                    o = new DialogOption(true, "newbie_tutorial/newbie_convo", "convo_1_reply_2");
                    DO.add(o);
                    int LastListIndex = 0;
                    this.sendSkillTrainerDialog(client, ConversationSTFFile, ConversationSTFString, ArgumentSTFFile, ArgumentSTFStringName, ArgumentInt, Counter, NonDialog, DO, LastListIndex);
                    return;
                }
            }
            System.out.println("Unhandled Tutorial Officer Request " + commandID);
     }
     protected void handleTutorialConversationResponse(ZoneClient c, String[] Selections){
            switch(this.getTerminalType())
            {
                case Constants.TERMINAL_TYPES_TUTORIAL_GREET_OFFICER:
                {
                    this.handleTutorialGreetOfficerResponse(c, Selections);
                    break;
                }
            }
     }

     private void handleTutorialGreetOfficerResponse(ZoneClient c, String[] Selections){

         /*System.out.println("handleTutorialGreetOfficerResponse");
         for(int i =0; i < Selections.length; i++)
         {
             System.out.println(i + "[" + Selections[i] + "]");
         }*/
         int iSelection = Integer.parseInt(Selections[0]);
         String [] sLastConversationMenu = c.getPlayer().getLastConversationMenu();
         //Vector<DialogOption> vMenuOptions = c.getPlayer().getLastConversationMenuOptions();
         System.out.println("Last Menu Title: " + sLastConversationMenu[1]);
         if(sLastConversationMenu[1].compareTo("convo_1_start")==0)
         {
             switch(iSelection)
             {
                 case 0:
                 {
                        String ConversationSTFFile = "newbie_tutorial/newbie_convo";
                        String ConversationSTFString = "convo_1_more";
                        String ArgumentSTFFile = "";
                        String ArgumentSTFStringName = "";
                        String ArgumentInt = "0";
                        int Counter = 0;
                        String NonDialog = "";
                        Vector<DialogOption> DO = new Vector<DialogOption>();
                        DialogOption o = new DialogOption(true, "newbie_tutorial/newbie_convo", "convo_1_agree");
                        DO.add(o);
                        o = new DialogOption(true, "newbie_tutorial/newbie_convo", "convo_1_reply_3");
                        DO.add(o);
                        int LastListIndex = 0;
                        this.sendSkillTrainerDialog(c, ConversationSTFFile, ConversationSTFString, ArgumentSTFFile, ArgumentSTFStringName, ArgumentInt, Counter, NonDialog, DO, LastListIndex);
                        break;
                 }
                 case 1:
                 {
                        String ConversationSTFFile = "newbie_tutorial/newbie_convo";
                        String ConversationSTFString = "convo_1_stuff";
                        String ArgumentSTFFile = "";
                        String ArgumentSTFStringName = "";
                        String ArgumentInt = "0";
                        int Counter = 0;
                        String NonDialog = "";
                        Vector<DialogOption> DO = new Vector<DialogOption>();
                        DialogOption o = new DialogOption(true, "newbie_tutorial/newbie_convo", "convo_1_agree");
                        DO.add(o);
                        int LastListIndex = 0;
                        this.sendSkillTrainerDialog(c, ConversationSTFFile, ConversationSTFString, ArgumentSTFFile, ArgumentSTFStringName, ArgumentInt, Counter, NonDialog, DO, LastListIndex);

                        //c.getPlayer().getTutorial().getSuppliesDrum()
                        try{
                            c.insertPacket(PacketFactory.buildObjectSpeak(c.getPlayer(), c.getPlayer().getTutorial().getSuppliesDrum(), null, "@newbie_tutorial/newbie_convo:open_me", (short)0, (short)0));
                             /*
                            "newbie_tutorial/newbie_convo",
                            "open_me",
                            c.getPlayer().getTutorial().getSuppliesDrum().getID(), c.getPlayer().getTutorial().getSuppliesDrum().getSTFFileName(), c.getPlayer().getTutorial().getSuppliesDrum().getSTFFileIdentifier(), c.getPlayer().getTutorial().getSuppliesDrum().getName(),
                            0, null, null, null,
                            0, null, null, null,
                            0, 0.0f, true
                            ));*/
                        }catch(Exception e){
                            DataLog.logException("Error While Sending Flytext for Tutorial Supplies Drum", "Terminal", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
                        }
                        break;
                 }
             }

         }
         else if(sLastConversationMenu[1].compareTo("convo_1_more")==0)
         {
            switch(iSelection)
            {
                case 0:
                {
                    String ConversationSTFFile = "newbie_tutorial/newbie_convo";
                    String ConversationSTFString = "convo_1_start";
                    String ArgumentSTFFile = "";
                    String ArgumentSTFStringName = "";
                    String ArgumentInt = "0";
                    int Counter = 0;
                    String NonDialog = "";
                    Vector<DialogOption> DO = new Vector<DialogOption>();
                    DialogOption o = new DialogOption(true, "newbie_tutorial/newbie_convo", "convo_1_reply_1");
                    DO.add(o);
                    o = new DialogOption(true, "newbie_tutorial/newbie_convo", "convo_1_reply_2");
                    DO.add(o);
                    int LastListIndex = 0;
                    this.sendSkillTrainerDialog(c, ConversationSTFFile, ConversationSTFString, ArgumentSTFFile, ArgumentSTFStringName, ArgumentInt, Counter, NonDialog, DO, LastListIndex);
                    break;
                }
                case 1:
                {
                    String ConversationSTFFile = "newbie_tutorial/newbie_convo";
                    String ConversationSTFString = "convo_1_explain";
                    String ArgumentSTFFile = "";
                    String ArgumentSTFStringName = "";
                    String ArgumentInt = "0";
                    int Counter = 0;
                    String NonDialog = "";
                    Vector<DialogOption> DO = new Vector<DialogOption>();
                    DialogOption o = new DialogOption(true, "newbie_tutorial/newbie_convo", "convo_1_agree");
                    DO.add(o);
                    int LastListIndex = 0;
                    this.sendSkillTrainerDialog(c, ConversationSTFFile, ConversationSTFString, ArgumentSTFFile, ArgumentSTFStringName, ArgumentInt, Counter, NonDialog, DO, LastListIndex);
                    this.animateNPC(c, "calm");
                    break;
                }
            }
         }
         else if(sLastConversationMenu[1].compareTo("convo_1_explain")==0)
         {
             switch(iSelection)
            {
                case 0:
                {
                    String ConversationSTFFile = "newbie_tutorial/newbie_convo";
                    String ConversationSTFString = "convo_1_start";
                    String ArgumentSTFFile = "";
                    String ArgumentSTFStringName = "";
                    String ArgumentInt = "0";
                    int Counter = 0;
                    String NonDialog = "";
                    Vector<DialogOption> DO = new Vector<DialogOption>();
                    DialogOption o = new DialogOption(true, "newbie_tutorial/newbie_convo", "convo_1_reply_1");
                    DO.add(o);
                    o = new DialogOption(true, "newbie_tutorial/newbie_convo", "convo_1_reply_2");
                    DO.add(o);
                    int LastListIndex = 0;
                    this.sendSkillTrainerDialog(c, ConversationSTFFile, ConversationSTFString, ArgumentSTFFile, ArgumentSTFStringName, ArgumentInt, Counter, NonDialog, DO, LastListIndex);
                    break;
                }
             }
         }
         else if(sLastConversationMenu[1].compareTo("convo_1_stuff")==0)
         {
            switch(iSelection)
            {
                case 0:
                {
                    String ConversationSTFFile = "newbie_tutorial/newbie_convo";
                    String ConversationSTFString = "convo_1_start";
                    String ArgumentSTFFile = "";
                    String ArgumentSTFStringName = "";
                    String ArgumentInt = "0";
                    int Counter = 0;
                    String NonDialog = "";
                    Vector<DialogOption> DO = new Vector<DialogOption>();
                    DialogOption o = new DialogOption(true, "newbie_tutorial/newbie_convo", "convo_1_reply_1");
                    DO.add(o);
                    o = new DialogOption(true, "newbie_tutorial/newbie_convo", "convo_1_reply_2");
                    DO.add(o);
                    int LastListIndex = 0;
                    this.sendSkillTrainerDialog(c, ConversationSTFFile, ConversationSTFString, ArgumentSTFFile, ArgumentSTFStringName, ArgumentInt, Counter, NonDialog, DO, LastListIndex);
                    break;
                }
            }
         }
     }

	public void setStructure(Structure myStructure) {
		this.myStructure = myStructure;
	}

	public Structure getStructure() {
		return myStructure;
	}

}; //End Of Class.
