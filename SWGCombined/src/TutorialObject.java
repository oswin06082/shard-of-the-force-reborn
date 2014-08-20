/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
/**
 *
 * @author Tomas Cruz
 */
public final class TutorialObject extends Structure {
    public final static long serialVersionUID = 1;       
    private transient ZoneServer zServer;
    private Terminal tutorialTravelTerminal;
    private boolean bHasCompleted;
    private Player owner;
    private boolean [] partPlayed;
    private long lPlayDelay;
    //private long lPlaySubpartDelay;
    private int iPartsToPlay;
    private boolean bHasEnteredTutorial;
    private boolean bWelcomePlayed;
    private boolean bChatEvent;
    private boolean waitingChatEvent;
    
    private boolean bRadialEvent;
    private boolean waitingRadialEvent;
    private boolean bTutorialBypassed;

    /**
     * Scene Objects
     */
    private Terminal greetOfficer;
    private TangibleItem suppliesDrum;
    // Objects End

    public TutorialObject(int TemplateID, ZoneServer zServer){
        bHasEnteredTutorial = false;
        bTutorialBypassed = false;
        iPartsToPlay = 12;
        partPlayed = new boolean [iPartsToPlay];
        for(int i = 0; i < iPartsToPlay; i++)
        {
            partPlayed[i] = false;
        }
        lPlayDelay = 5000;
        bWelcomePlayed = false;
        bChatEvent = false;
        waitingChatEvent = false;
        bRadialEvent = false;
        waitingRadialEvent = false;
        bHasCompleted = false;
        this.zServer = zServer;
        this.setTemplateID(TemplateID);
        this.setPlanetID(Constants.TUTORIAL);
        this.setIsStaticObject(false);
        this.setX(0);
        this.setY(0);
        this.setZ(0);
        this.setStructureType(Constants.STRUCTURE_TYPE_TUTORIAL);
        setTutorialRandomPosition();
        this.setIFacingDirection(0);
        this.setOrientationS(0);
        this.setOrientationW(1);
        this.setID(zServer.getNextObjectID());        
        this.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
        
        int iCellCount = 14; // Entered tutorial.
        if(TemplateID == 211) // Skipped tutorial.
        {
            iCellCount = 1;
        }
        
        this.makeTutorialCells(zServer, iCellCount);        
        //this is the travel terminal in the end of the tutorial building.
        {
            Terminal t = new Terminal();
            t.setServer(zServer);
            t.setFirstName("Travel Terminal");
            t.setID(zServer.getNextObjectID());
            t.setParentID(this.getID());
            //zServer.addObjectIDToAllUsedID(t.getID());            
            t.setTerminalType(Constants.TERMINAL_TYPES_TRAVEL_TUTORIAL);
            t.setTicketID(-1);
            t.setLocationID(-1);
            t.setPortID(-1);
            t.setIsFactionTerminal((byte)0x00);            
            t.setCRC(1946349820);
            int TerminalCell = 11;
            if(TemplateID == 211)
            {
                TerminalCell = 1;
            }
            Enumeration<Cell> cEnum = this.getCellsInBuilding().elements();
            Cell c = null;
            while(cEnum.hasMoreElements())
            {
                Cell tc = cEnum.nextElement();
                if(tc.getCellNum() == TerminalCell)
                {
                    c = tc;
                }
            }
            if (c == null) {
            	
            }
            long cellID = c.getID();  // Tutorial cell not found.
            t.setScriptName("NoScript.js");
            t.setCellID(cellID);
            t.setPlanetID(Constants.TUTORIAL);
            t.setFactionID(0);
            t.setCREO3Bitmask(0x108); // Maybe?

            float cellX = 27.5f;
            float cellY = -169.3f;
            float cellZ = -3.5f;
            
            if(TemplateID == 211)
            {
                cellX = 27.5f;
                cellY = -169.3f;
                cellZ = -3.5f;
            }            
            t.setX(this.getX() + cellX);
            t.setY(this.getY() + cellY);
            t.setZ(this.getZ() + cellZ);

            t.setCellX(cellX);
            t.setCellY(cellY);
            t.setCellZ(cellZ);

            t.setOrientationN(0);
            t.setOrientationS(0);
            t.setOrientationE(0);
            t.setOrientationW(1);            
            t.setCustomizationData(null);
            t.setPVPStatus(0);
            t.setStance(null, Constants.STANCE_STANDING, true);
            t.clearAllStates(false);
            t.setMoodID(0);
            c.addCellObject(t);
            tutorialTravelTerminal = t;
            //zServer.addObjectIDToAllUsedID(t.getID());
            zServer.addObjectToAllObjects(t, true, false);
        }
        this.createSceneObjects();
    }

    protected void update(long lDeltaMS){

            if(this.bHasEnteredTutorial && !bTutorialBypassed)
            {
                lPlayDelay -= lDeltaMS;
            }
            else if(bTutorialBypassed)
            {
                //any tutorial updates that need to be done when bypassed need to go here
                return;
            }
            //all tutorial updates while player goes to tutorial go here
            if(lPlayDelay <= 0)
            {                
                //System.out.println("Tutorial Object Update");
                for(int i = 0; i < iPartsToPlay; i++)
                {                    
                    if(!this.partPlayed[i])
                    {
                        //System.out.println("Playing Part: " + (i + 1));
                        switch(i)
                        {
                            case 0:
                            {
                                this.tutorialPlayPartOne();
                                this.partPlayed[i] = true;
                                lPlayDelay = 10000;
                                i = iPartsToPlay + 1;
                                break;
                            }
                            case 1:
                            {
                                this.tutorialPlayPartOneOne();
                                this.partPlayed[i] = true;
                                lPlayDelay = 9000;
                                waitingChatEvent = true;
                                i = iPartsToPlay + 1;
                                break;
                            }
                            case 2:
                            {
                                this.tutorialPlayPartOneTwo();
                                this.partPlayed[i] = true;
                                lPlayDelay = 4500;
                                i = iPartsToPlay + 1;
                                break;
                            }
                            case 3:
                            {
                                this.tutorialPlayPartOneThree();
                                this.partPlayed[i] = true;
                                lPlayDelay = 12000;
                                i = iPartsToPlay + 1;
                                break;
                            }
                            case 4:
                            {
                                if(!bChatEvent)
                                {
                                    this.tutorialPlayPartOneThreeRepeat();
                                    lPlayDelay = 9000;
                                    i = iPartsToPlay + 1;
                                }
                                else
                                {
                                    this.partPlayed[i] = true;
                                    lPlayDelay = 3000;
                                    i = iPartsToPlay + 1;
                                }
                                break;
                            }
                            case 5:
                            {
                                this.tutorialPlayPartOneFour();
                                this.partPlayed[i] = true;
                                this.greetOfficer.animateNPC(owner.getClient(), "beckon");
                                this.greetOfficer.speakNPC(owner.getClient(), "A straggler, eh?  Don't be shy, come on forward.", (short)0, (short)0);
                                lPlayDelay = 10000;
                                this.waitingRadialEvent = true;
                                i = iPartsToPlay + 1;
                                break;
                            }
                            case 6:
                            {
                                if(!this.bRadialEvent)
                                {
                                    this.tutorialPlayPartOneFour();
                                    i = iPartsToPlay + 1;
                                    lPlayDelay = 8000;
                                }
                                else
                                {                                    
                                    lPlayDelay = 2000;
                                    this.partPlayed[i] = true;
                                    i = iPartsToPlay + 1;
                                }
                                break;
                            }
                            default:
                            {
                                i = iPartsToPlay + 1;
                            }
                        }
                    }                   
                }
            }
    }    

    public void setTutorialRandomPosition(){

        {
            int x,y,z;
            z=0;
            x = SWGGui.getRandomInt(SWGGui.getRandomInt(-8000,-1),SWGGui.getRandomInt(1,8000));
            y = SWGGui.getRandomInt(SWGGui.getRandomInt(-8000,-1),SWGGui.getRandomInt(1,8000));
            setX(x);
            setY(y);
            setY(z);
            boolean proceed = false;
            while(!proceed )
            {
                boolean hasclearance = true;
                ConcurrentHashMap<Long, SOEObject> vOAO = zServer.getAllObjects();
                Enumeration <SOEObject> oEnum = vOAO.elements();
                while(oEnum.hasMoreElements())
                {
                    SOEObject o = oEnum.nextElement();
                    if(o.getPlanetID() == getPlanetID() && ZoneServer.getRangeBetweenObjects(this, o) < 256)
                    {
                        x += SWGGui.getRandomInt(-32,32);
                        y += SWGGui.getRandomInt(-32,32);
                        setX(x);
                        setY(y);
                        hasclearance = false;
                        oEnum = vOAO.elements();
                    }
                }
                if(hasclearance)
                {
                    proceed = true;
                }
            }
            System.out.println("Tutorial Random Coords Set" + getX() + "," + getY());

            //setZ(getServer().getHeightAtCoodinates(getX(), getY(), getPlanetID()));

        }


    }

    public Terminal getTutorialTravelTerminal() {
        return tutorialTravelTerminal;
    }


    public void spawnTutorialObjects(Player p){
        try{
                Enumeration<Cell> cEnum = this.getCellsInBuilding().elements();
                while(cEnum.hasMoreElements())
                {
                    Cell c = cEnum.nextElement();
                    Enumeration<SOEObject> oEnum = c.getCellObjects().elements();
                    while(oEnum.hasMoreElements())
                    {
                        SOEObject o = oEnum.nextElement();
                        o.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_RAW_SPAWN);
                        p.addDelayedSpawnObject(o, System.currentTimeMillis() + 5000);
                    }
                }
        }catch(Exception e){
            DataLog.logException("Exception while spawning tutorial objects.", "TutorialObject",true, true, e);
        }
    }

    public boolean hasCompleted() {
        return bHasCompleted;
    }

    public void setHasCompleted(boolean bHasCompleted) {
        this.bHasCompleted = bHasCompleted;
        //zServer.removeFromTree(tutorialTravelTerminal);
        //zServer.removeFromTree(this);
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public boolean hasEnteredTutorial() {
        return bHasEnteredTutorial;
    }

    public void setHasEnteredTutorial(boolean bHasEnteredTutorial) {
        this.bHasEnteredTutorial = bHasEnteredTutorial;
    }

    public void setChatEvent(boolean bChatEvent) {
        this.bChatEvent = bChatEvent;
        waitingChatEvent = false;
    }

    public boolean isWaitingChatEvent() {
        return waitingChatEvent;
    }

    public void tutorialPlayWelcome(boolean bTutorialBypassed){
        try{
            if(!bWelcomePlayed && !bTutorialBypassed)
            {
                bWelcomePlayed = true;
                owner.getClient().insertPacket(PacketFactory.buildPlaySoundFileMessage(0, "sound/tut_01_welcome.snd",Constants.PLAY_MUSIC_FILE_ONCE,(byte)0));
                owner.getClient().insertPacket(PacketFactory.buildChatSystemMessage("newbie_tutorial/system_messages","welcome"));
            }
            else if(bTutorialBypassed)
            {
                this.bTutorialBypassed = bTutorialBypassed;
                owner.getClient().insertPacket(PacketFactory.buildPlaySoundFileMessage(0, "sound/tut_01_welcome.snd",Constants.PLAY_MUSIC_FILE_ONCE,(byte)0));
                owner.getClient().insertPacket(PacketFactory.buildChatSystemMessage("newbie_tutorial/system_messages","welcome"));
            }
        }catch(Exception e){
            DataLog.logException("Exception in Tutorial Object", "tutorialPlayWelcome", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
    }

    public void tutorialPlayPartOne(){
        try{
            owner.getClient().insertPacket(PacketFactory.buildPlaySoundFileMessage(0, "sound/tut_02_movement.snd",Constants.PLAY_MUSIC_FILE_ONCE,(byte)0));
            owner.getClient().insertPacket(PacketFactory.buildChatSystemMessage("newbie_tutorial/system_messages","part_1"));
            owner.getClient().insertPacket(PacketFactory.buildChatSystemMessage("newbie_tutorial/system_messages","movement_keyboard"));
            owner.getClient().insertPacket(PacketFactory.buildChatSystemMessage("newbie_tutorial/system_messages","movement_mouse"));
        }catch(Exception e){
            DataLog.logException("Exception in Tutorial Object", "tutorialPlayPartOne", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
    }

    public void tutorialPlayPartOneOne(){
        try{
            owner.getClient().insertPacket(PacketFactory.buildPlaySoundFileMessage(0, "sound/tut_03_scroll_out.snd",Constants.PLAY_MUSIC_FILE_ONCE,(byte)0));
            owner.getClient().insertPacket(PacketFactory.buildChatSystemMessage("newbie_tutorial/system_messages","mousewheel"));
        }catch(Exception e){
            DataLog.logException("Exception in Tutorial Object", "tutorialPlayPartOneOne", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
    }

    public void tutorialPlayPartOneTwo(){
        try{
            owner.getClient().insertPacket(PacketFactory.buildPlaySoundFileMessage(0, "sound/tut_04_chat.snd",Constants.PLAY_MUSIC_FILE_ONCE,(byte)0));
            owner.getClient().insertPacket(PacketFactory.buildChatSystemMessage("newbie_tutorial/system_messages","chatwindow"));
        }catch(Exception e){
            DataLog.logException("Exception in Tutorial Object", "tutorialPlayPartOneOne", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
    }

    public void tutorialPlayPartOneThree(){
        try{
            //owner.getClient().insertPacket(PacketFactory.buildPlaySoundFileMessage(0, "sound/tut_05_remind_chat.snd",Constants.PLAY_MUSIC_FILE_ONCE,(byte)0));
            owner.getClient().insertPacket(PacketFactory.buildChatSystemMessage("newbie_tutorial/system_messages","chatprompt"));
        }catch(Exception e){
            DataLog.logException("Exception in Tutorial Object", "tutorialPlayPartOneOne", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
    }
    public void tutorialPlayPartOneThreeRepeat(){
        try{
            owner.getClient().insertPacket(PacketFactory.buildPlaySoundFileMessage(0, "sound/tut_05_remind_chat.snd",Constants.PLAY_MUSIC_FILE_ONCE,(byte)0));
            owner.getClient().insertPacket(PacketFactory.buildChatSystemMessage("newbie_tutorial/system_messages","repeatchatprompt"));
        }catch(Exception e){
            DataLog.logException("Exception in Tutorial Object", "tutorialPlayPartOneOne", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
    }
    
    public void tutorialPlayPartOneFour(){
        try{
            owner.getClient().insertPacket(PacketFactory.buildPlaySoundFileMessage(0, "sound/tut_08_imperialofficer.snd",Constants.PLAY_MUSIC_FILE_ONCE,(byte)0));
            owner.getClient().insertPacket(PacketFactory.buildChatSystemMessage("newbie_tutorial/system_messages","tut_08"));
        }catch(Exception e){
            DataLog.logException("Exception in Tutorial Object", "tutorialPlayPartOneOne", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
    }

    private void createSceneObjects(){
        if(this.getTemplateID() == 210)
        {
            //Full Tutorial
            //Cell 1 Items
            /**
             * Entrance Imperial Officer.
             * Templates to be used:
             * 4463, 1, 'object/mobile/shared_dressed_imperial_officer_f.iff', 'human_base_female', -1, , '', , 1458378929, 0, 'npc_name', 0, '', '', '', '', -1, -1, 'noScript.js', 0, 0, '-1'
             * 4464, 1, 'object/mobile/shared_dressed_imperial_officer_m.iff', 'human_base_male', -1, , '', , 3423844128, 0, 'npc_name', 0, '', '', '', '', -1, -1, 'noScript.js', 0, 0, '-1'
             * 4465, 1, 'object/mobile/shared_dressed_imperial_officer_m_2.iff', 'human_base_male', -1, , '', , 519244536, 0, 'npc_name', 0, '', '', '', '', -1, -1, 'noScript.js', 0, 0, '-1'
             * 4466, 1, 'object/mobile/shared_dressed_imperial_officer_m_3.iff', 'human_base_male', -1, , '', , 1476291957, 0, 'npc_name', 0, '', '', '', '', -1, -1, 'noScript.js', 0, 0, '-1'
             * 4467, 1, 'object/mobile/shared_dressed_imperial_officer_m_4.iff', 'human_base_male', -1, , '', , 2887599713, 0, 'npc_name', 0, '', '', '', '', -1, -1, 'noScript.js', 0, 0, '-1'
             * 4468, 1, 'object/mobile/shared_dressed_imperial_officer_m_5.iff', 'human_base_male', -1, , '', , 3843039724, 0, 'npc_name', 0, '', '', '', '', -1, -1, 'noScript.js', 0, 0, '-1'
             * 4469, 1, 'object/mobile/shared_dressed_imperial_officer_m_6.iff', 'human_base_male', -1, , '', , 1040681339, 0, 'npc_name', 0, '', '', '', '', -1, -1, 'noScript.js', 0, 0, '-1'
             */
             int iGOTemplate = SWGGui.getRandomInt(4463,4469);

             greetOfficer = new Terminal();
             greetOfficer.setServer(getServer());
             greetOfficer.setTemplateID(iGOTemplate);
             greetOfficer.setTerminalType(Constants.TERMINAL_TYPES_TUTORIAL_GREET_OFFICER);
             greetOfficer.setCREO3Bitmask(Constants.BITMASK_CREO3_QUEST_NPC);
             greetOfficer.setFactionID(Constants.FACTION_IMPERIAL);
             greetOfficer.setPVPStatus(Constants.PVP_STATUS_IS_NORMAL_NON_ATTACKABLE);
             greetOfficer.setPlanetID(this.getPlanetID());
             Enumeration<Cell> cEnum = this.getCellsInBuilding().elements();
             Cell c = null;
             while(cEnum.hasMoreElements())
             {
                Cell tc = cEnum.nextElement();
                if(tc.getCellNum() == 1)
                {
                    c = tc;
                }
             }
             greetOfficer.setCellID(c.getID());
             greetOfficer.setX(this.getX() + -6.252006f);
             greetOfficer.setY(this.getY() + -2.9777334f);
             greetOfficer.setZ(this.getZ() + 0.008646428f);
             greetOfficer.setCellX(-6.252006f);
             greetOfficer.setCellY(-2.9777334f);
             greetOfficer.setCellZ(0.008646428f);
             greetOfficer.setOrientationN(0);
             greetOfficer.setOrientationS(0.6918406f);
             greetOfficer.setOrientationE(0);
             greetOfficer.setOrientationW(0.72205025f);
             String [] newName = this.zServer.generateRandomName("human");
             greetOfficer.setFirstName(newName[0]);
             greetOfficer.setLastName(newName[1]);
             int[] hams = new int[9];
             for(int i = 0; i < 9; i++)
             {
                 hams[i] = SWGGui.getRandomInt(1800, 3000);
             }
             greetOfficer.setHam(hams);
             greetOfficer.setID(zServer.getNextObjectID());
             zServer.addObjectToAllObjects(greetOfficer, true, false);
             //End Officer
             /**
              * Supplies Drum and Loot Items
              * 8868, 1, 'object/tangible/container/drum/shared_tatt_drum_1.iff', 'tatt_drum', -1, , '', , 3342593323, 0, 'container_name', 0, 'tatt_drum', 'container_name', '', '', -1, -1, 'noScript.js', 0, 0, '-1'
              */
             suppliesDrum = new TangibleItem();
             suppliesDrum.setTemplateID(8868);
             suppliesDrum.setID(zServer.getNextObjectID());
             try {
            	 suppliesDrum.setName("Supplies", false);
	             suppliesDrum.setPVPStatus(Constants.PVP_STATUS_IS_NORMAL_NON_ATTACKABLE);
	             suppliesDrum.setPlanetID(this.getPlanetID());
	             suppliesDrum.setCellID(c.getID());
	             suppliesDrum.setX(this.getX() + -6.1310787f);
	             suppliesDrum.setY(this.getY() + -4.490353f);
	             suppliesDrum.setZ(this.getZ() + 0.007174975f);
	             suppliesDrum.setCellX(-6.1310787f);
	             suppliesDrum.setCellY(-4.490353f);
	             suppliesDrum.setCellZ(0.007174975f);
	             suppliesDrum.setOrientationN(0);
	             suppliesDrum.setOrientationS(0.6918406f);
	             suppliesDrum.setOrientationE(0);
	             suppliesDrum.setOrientationW(0.72205025f);
	             suppliesDrum.setMaxCondition(1000, false);             
	             suppliesDrum.setCanBePickedUp(false);
	             suppliesDrum.setEquipped(c, Constants.EQUIPPED_STATE_UNEQUIPPED);
             } catch (Exception e) {
            	 // Can't happen -- we're not actually building the packet.
             }

             zServer.addObjectToAllObjects(suppliesDrum, true, false);

             // These are not necessary -- adding the object to all used objects does this.
             //zServer.addObjectIDToAllUsedID(greetOfficer.getID());
             //zServer.addObjectIDToAllUsedID(suppliesDrum.getID());
             c.addCellObject(greetOfficer);
             c.addCellObject(suppliesDrum);
             //Cell 2 Items

        }
        else
        {
            //Tutorial Bypassed

        }
    }

    public void setRadialEvent(boolean bRadialEvent) {
        this.bRadialEvent = bRadialEvent;
        waitingRadialEvent = false;
    }

    public boolean isWaitingRadialEvent() {
        return waitingRadialEvent;
    }

    public TangibleItem getSuppliesDrum() {
        return suppliesDrum;
    }

    private void makeTutorialCells(ZoneServer zServer, int iCellCount){
        //this.iCellCount = iCellCount;
        this.setIsStaticObject(false);
        this.setStructureType(Constants.STRUCTURE_TYPE_TUTORIAL);
        for(int i = 0; i < iCellCount; i++)
        {
            
            Cell c = new Cell(this,i+1,true);//always plus 1 otherwise the first cell takesover the building's id
            c.setPlanetID(this.getPlanetID());
            c.setID(zServer.getNextObjectID());
            c.setOrientationW(this.getOrientationW());
            c.setOrientationS(this.getOrientationS());
            c.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);                       
            System.out.println("Creating Cell " + (i + 1) + " For Structure ID: " + this.getID() + " CellID:" + c.getID() );
            getCellsInBuilding().put(c.getID(), c);
            //zServer.addObjectIDToAllUsedID(c.getID());
            zServer.addObjectToAllObjects(c,false, false);
        }
    }
}

