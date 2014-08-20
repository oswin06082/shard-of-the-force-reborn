import java.util.Vector;


/**
 *
 * @author Tomas Cruz
 */
public final class Camp extends TangibleItem {

    public final static long serialVersionUID = 1;
    private static int iDefaultTemplateID = 12035;
    //private static String sDefaultIFFFileName = "object/tangible/scout/camp/shared_camp_basic.iff";
    //private static int iDefaultCRC = 1292354861;
    private static int iDefaultAdminTerminalTemplateID = 14092;
    private long lTimeToLive;
    //private int iRequiredSkill;
    //private long lCampHeartbeat;
    private int iCampXPMultiplier;
    private long iXPTimeToLive;
    private DeedTemplate dT;
    private Terminal adminTerminal;
    private Player campOwner;
    private long lAbandonTimer;
    Vector<Long> vCampVisitors;
    Vector<Long> vGreetedVisitors;
    private boolean  bPropsMade;
    private long lProbBuildTimer;
    private Vector<SOEObject> vCampPropList;
    public Camp(){
        super();
        setTemplateID(iDefaultTemplateID);
        lTimeToLive = 1000 * 60 * 10;
        iXPTimeToLive = 1000 * 60 * 10;
        //iRequiredSkill = DatabaseInterface.getTemplateDataByID(iDefaultTemplateID).getRequiredSkillID();
        //this.setStructureType(Constants.STRUCTURE_TYPE_CAMP);
        this.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());
        this.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
        //lCampHeartbeat = 2000;
        this.setBSendsEquipedState(false);
        vCampVisitors = new Vector<Long>();
        vGreetedVisitors = new Vector<Long>();
        lAbandonTimer = 0;
        bPropsMade = false;
        lProbBuildTimer = 2500;
    }

    public Camp(int iTemplateID, long lTimeToLive){
        super();
        setTemplateID(iTemplateID);
        this.lTimeToLive = lTimeToLive;
        iXPTimeToLive = (int)lTimeToLive;
        try {
	        this.setConditionDamage(0, false);
	        this.setMaxCondition(1000, false);
        } catch (Exception e) {
        	// Can't happen -- not building the packets.
        }
        //iRequiredSkill = DatabaseInterface.getTemplateDataByID(iTemplateID).getRequiredSkillID();
       // this.setStructureType(Constants.STRUCTURE_TYPE_CAMP);
        this.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());
        this.setPVPStatus(Constants.PVP_STATUS_IS_NORMAL_NON_ATTACKABLE);
        //lCampHeartbeat = 2000;
        this.setBSendsEquipedState(false);
        vCampVisitors = new Vector<Long>();
        vGreetedVisitors = new Vector<Long>();
        lAbandonTimer = 0;
        bPropsMade = false;
        lProbBuildTimer = 2500;
    }

   // @Override
    public void update(long lElapsedTime, ZoneServer server){
        try{
            /*
            if(lCampHeartbeat <= 0)
            {
                System.out.println("Camp Update TTL:" + (lTimeToLive - lElapsedTime));
                lCampHeartbeat = 60000;
            }
            else
            {
                lCampHeartbeat-=lElapsedTime;
            }
             * */
            if(this.lTimeToLive <= 0)
            {
               disbandCamp(server, lTimeToLive,false);
            }
            else
            {
                this.lTimeToLive-=lElapsedTime;
            }
            Vector<Player> vPL = server.getPlayersAroundObject(this,false);
            for(int i = 0; i < vPL.size(); i++)
            {
                Player p = vPL.get(i);
                if(!this.vCampVisitors.contains(p.getID()) && ZoneServer.isInRange(p, this, 16)) 
                {
                    this.vCampVisitors.add(p.getID());
                    this.vGreetedVisitors.add(p.getID());
                    p.getClient().insertPacket(PacketFactory.buildChatSystemMessage("You have entered " + this.getName()));
                    p.setIsInCamp(true);
                }
                else if(this.vCampVisitors.contains(p.getID()) && ZoneServer.isInRange(p, this, 16) )
                {
                    if(!this.vGreetedVisitors.contains(p.getID()))
                    {
                        vGreetedVisitors.add(p.getID());
                        p.getClient().insertPacket(PacketFactory.buildChatSystemMessage("You have entered " + this.getName()));
                        p.setIsInCamp(true);
                    }
                }
            }
            for(int i =0; i < vCampVisitors.size();i++)
            {
                Player p = server.getPlayer(vCampVisitors.get(i));
                if(vCampVisitors.contains(p.getID()) && (!ZoneServer.isInRange(p, this, 16)) && this.vGreetedVisitors.contains(p.getID()))
                {                    
                    {
                        vGreetedVisitors.remove(p.getID());
                        p.getClient().insertPacket(PacketFactory.buildChatSystemMessage("You have left " + this.getName()));
                        p.setIsInCamp(false);
                    }
                }
            }
            if(!this.getCampOwner().isInCamp() && !this.getCampOwner().hasSkill(320))
            {
                lAbandonTimer+=lElapsedTime;
                //System.out.println("ABT:" + lAbandonTimer + " TT:" + (long)(this.iXPTimeToLive / 2));
                if(lAbandonTimer >= (long)(this.iXPTimeToLive / 2))
                {
                    disbandCamp(server, lTimeToLive,true);
                }
            }
            else
            {
                lAbandonTimer = 0;
            }
            //Decay the camp
            this.setConditionDamage(this.getConditionDamage() + (int)(this.getMaxCondition() / iXPTimeToLive), false);
            //-----
            //this sets the props for the camp to be made.
            if(!bPropsMade)
            {
                if(lProbBuildTimer <= 0)
                {
                    makeProps(server);                    
                }
                else
                {
                    lProbBuildTimer-=lElapsedTime;
                }
            }
        }catch(Exception e){
            DataLog.logException("Exception in Update", "Camp", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
    }

   // @Override
    public void useItem(ZoneClient client, byte commandID){
        try{
            switch(commandID)
            {
                case 124: //status
                {
                    SUIWindow w = new SUIWindow(client.getPlayer());
                    w.setWindowType(Constants.SUI_STRUCTURE_SHOW_STATUS);
                    w.setOriginatingObject(this);
                    String WindowTypeString = "handleSUI";
                    String DataListTitle = "@player_structure:structure_status_t";
                    String DataListPrompt = "@player_structure:structure_name_prompt " + this.getName();
                    String sList[] = new String[3];
                    sList[0] = "@player_structure:owner_prompt " + this.getCampOwner().getFullName(); //owner
                    sList[1] = "Time Left: " + (this.lTimeToLive / 1000 / 60) + " Mins";
                    sList[2] = "Visitors: " + this.vCampVisitors.size();
                    client.insertPacket(w.SUIScriptListBox(client, WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
                    
                    break;
                }
                case (byte)182: //disband
                {
                    if(this.getCampOwner().equals(client.getPlayer()))
                    {
                        this.disbandCamp(client.getServer(), lTimeToLive,false);
                    }
                    else
                    {
                        client.insertPacket(PacketFactory.buildChatSystemMessage("You must be Camp owner to Disband."));
                    }
                    break;
                }
                default:
                {
                    //System.out.println("Unhandled Command id in Camp Terminal. ID: " + commandID);
                }
            }
        }catch(Exception e){
            DataLog.logException("Error in useItem", "Camp",ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
    }

    protected Terminal makeTerminal(long lTerminalID, DeedTemplate dT, ZoneServer server){
        Terminal t = new Terminal();
        t.setServer(server);
        t.setTemplateID(iDefaultAdminTerminalTemplateID);
        t.setTerminalType(Constants.TERMINAL_TYPES_CAMP_MANAGEMENT);
        t.setID(lTerminalID);
        t.setPVPStatus(Constants.PVP_STATUS_IS_NORMAL_NON_ATTACKABLE);
        t.setCREO3Bitmask(Constants.BITMASK_CREO3_TERMINAL);
        
        t.setX(this.getX() + dT.getTerminalX());
        t.setY(this.getY() + dT.getTerminalY());
        t.setZ(this.getZ() + dT.getTerminalZ());
        t.setCellX(this.getX() + dT.getTerminalX());
        t.setCellY(this.getY() + dT.getTerminalY());
        t.setCellZ(this.getZ() + dT.getTerminalZ());
        t.setPlanetID(this.getPlanetID());
        t.setOrientationN(dT.getTerminal_oI());
        t.setOrientationS(dT.getTerminal_oJ());
        t.setOrientationE(dT.getTerminal_oK());
        t.setOrientationW(dT.getTerminal_oW());
        t.setParentID(this.getID());
        t.setFirstName("Camp Management Terminal");
        t.setRadialCondition(Constants.RADIAL_CONDITION.CAMP_TERMINAL_NORMAL.ordinal());
        adminTerminal = t;
        //this.setAdminTerminal(t);
        server.addObjectToAllObjects(t, true, false);
        return t;
    }

    protected void makeProps(ZoneServer server){
        try{
            vCampPropList = new Vector<SOEObject>();
            for(int i =0; i < dT.getICampPropTemplateID().length;i++)
            {
                ItemTemplate iT = DatabaseInterface.getTemplateDataByID(dT.getICampPropTemplateID()[i]);
                
                if(iT.getTemplateID() == Constants.FACTION_BANNER_NEUTRAL)
                {
                    if(this.getCampOwner().getFactionID() == Constants.FACTION_IMPERIAL)
                    {
                        iT = DatabaseInterface.getTemplateDataByID(Constants.FACTION_BANNER_IMPERIAL);
                    }
                    else if(this.getCampOwner().getFactionID() == Constants.FACTION_REBEL)
                    {
                        iT = DatabaseInterface.getTemplateDataByID(Constants.FACTION_BANNER_REBEL);
                    }
                    //System.out.println("Camp Prop: " + iT.getIFFFileName() + " TID:" + iT.getTemplateID());
                }
                if(iT.getIFFFileName().contains("/tangible"))
                {
                    TangibleItem prop = new TangibleItem();
                    prop.setID(server.getNextObjectID());
                    //prop.setSerialNumber(server.getNextSerialNumber());
                    prop.setTemplateID(iT.getTemplateID());
                    prop.setConditionDamage(0, false);
                    prop.setMaxCondition(1000, false);
                    prop.addBitToPVPStatus(Constants.PVP_STATUS_IS_NORMAL_NON_ATTACKABLE);
                    prop.setParentID(this.getID());
                    prop.setX(this.getX() + dT.getCampPropX()[i]);
                    prop.setY(this.getY() + dT.getCampPropY()[i]);
                    prop.setZ(this.getZ() + dT.getCampPropZ()[i]);
                    prop.setOrientationN(dT.getCampPropOrientationN()[i]);
                    prop.setOrientationS(dT.getCampPropOrientationS()[i]);
                    prop.setOrientationE(dT.getCampPropOrientationE()[i]);
                    prop.setOrientationW(dT.getCampPropOrientationW()[i]);
                    if(prop.getTemplateID() == this.getTemplateID())
                    {
                        prop.setName(this.getName(), false);
                        prop.setRadialCondition(Constants.RADIAL_CONDITION.CAMP_TERMINAL_NORMAL.ordinal());
                    }
                    server.addObjectToAllObjects(prop, true, false);
                    vCampPropList.add(prop);
                }
                else if(iT.getIFFFileName().contains("/static"))
                {
                    StaticItem prop = new StaticItem();
                    prop.setID(server.getNextObjectID());
                    //prop.setSerialNumber(server.getNextSerialNumber());
                    prop.setTemplateID(iT.getTemplateID());
                    prop.setConditionDamage(0, false);
                    prop.setMaxCondition(1000, false);
                    prop.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);
                    prop.setParentID(this.getID());
                    prop.setX(this.getX() + dT.getCampPropX()[i]);
                    prop.setY(this.getY() + dT.getCampPropY()[i]);
                    prop.setZ(this.getZ() + dT.getCampPropZ()[i]);
                    prop.setOrientationN(dT.getCampPropOrientationN()[i]);
                    prop.setOrientationS(dT.getCampPropOrientationS()[i]);
                    prop.setOrientationE(dT.getCampPropOrientationE()[i]);
                    prop.setOrientationW(dT.getCampPropOrientationW()[i]);
                    prop.setName(this.getName(), false);
                    server.addObjectToAllObjects(prop, true, false);
                    vCampPropList.add(prop);
                }
            }
            Vector<Player> vPL = server.getPlayersAroundObject(this, false);
            for(int i = 0; i < vPL.size();i++)
            {
                Player p = vPL.get(i);
                for(int x = 0; x < vCampPropList.size(); x++)
                {
                    SOEObject t = vCampPropList.get(x);
                    p.spawnItem(t);
                    p.getClient().insertPacket(PacketFactory.buildObjectControllerDataTransformObjectToClient(t,0x21));
                }
                p.spawnItem(this.adminTerminal);
                p.getClient().insertPacket(PacketFactory.buildObjectControllerDataTransformObjectToClient(adminTerminal,0x21));
            }
            bPropsMade = true;
        }catch(Exception e){
            DataLog.logException("Error in makeProps", "Camp", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
    }

    protected void disbandCamp(ZoneServer server, long lTime, boolean bAbandoned){
        try{
            
            server.removeObjectFromAllObjects(this,false);
            server.removeObjectFromAllObjects(this.getAdminTerminal(),false);
            for(int i = 0; i < vCampPropList.size(); i++)
            {
                SOEObject t = vCampPropList.get(i);
                server.removeObjectFromAllObjects(t,false);
            }
            
            campOwner.setCurrentCampObject(null);
            Vector<Player> vPL = server.getPlayersAroundObject(this, false);
            for(int i = 0; i < vPL.size();i++)
            {
                Player p = vPL.get(i);
                p.despawnItem(this);
                p.despawnItem(adminTerminal);
                for(int x = 0; x < vCampPropList.size(); x++)
                {
                    SOEObject t = vCampPropList.get(x);
                    p.despawnItem(t);
                }
            }
            campOwner.getClient().insertPacket(PacketFactory.buildChatSystemMessage("Camp Disbanded."));
            int visitorXPBonus = 2 * this.vCampVisitors.size();
            //System.out.println("lTime:" + lTime + " :iXPTimeToLive: " + iXPTimeToLive + " : iCampXPMultiplier: " + iCampXPMultiplier);
            if(lTime <= 0 && !bAbandoned)
            {
                campOwner.updateExperience(null, DatabaseInterface.getExperienceIDFromName("camp"),(int)(iXPTimeToLive / iCampXPMultiplier) + visitorXPBonus);
            }
            else if(lTime >= 1 && !bAbandoned)
            {
                int iDeltaTimeToLive = (int)(iXPTimeToLive - lTime);
                campOwner.updateExperience(null, DatabaseInterface.getExperienceIDFromName("camp"),(int)(iDeltaTimeToLive / iCampXPMultiplier) + visitorXPBonus);
            }
        }catch(Exception e){
            DataLog.logException("Error While Disbanding Camp", "Camp", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
    }

    public int getICampXPMultiplier() {
        return iCampXPMultiplier;
    }

    public void setICampXPMultiplier(int iCampXPMultiplier) {
        this.iCampXPMultiplier = iCampXPMultiplier;
    }

    public DeedTemplate getDeedTemplate() {
        return dT;
    }

    public void setDeedTemplate(DeedTemplate dT) {
        this.dT = dT;
    }

    public Player getCampOwner() {
        return campOwner;
    }

    public void setCampOwner(Player campOwner) {
        this.campOwner = campOwner;
    }

    public Terminal getAdminTerminal() {
        return adminTerminal;
    }

    public void setAdminTerminal(Terminal adminTerminal) {
        this.adminTerminal = adminTerminal;
    }

	public int experiment(long iExperimentalIndex, int numExperimentationPointsUsed, Player thePlayer) {
		// Could camps be experimented on?  Wouldn't it make more sense for a Camp to extend Structure?
		return 0;
	}


}
