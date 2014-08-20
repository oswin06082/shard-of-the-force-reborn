import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author Tomas Cruz
 */
public final class CreaturePet extends NPC{
	public final static long serialVersionUID = 1l;

    /**
     *  PET_FOLLOW              20		TRUE	142 100
        PET_STAY                20		TRUE	143 101
        PET_GUARD               20		TRUE	144 102
        PET_FRIEND              20		TRUE	145 103
        PET_ATTACK              20		TRUE	146 104
        PET_PATROL              20		TRUE	147 105
        PET_GET_PATROL_POINT	20		TRUE	148 106
        PET_CLEAR_PATROL_POINTS	20		TRUE	149 107
        PET_ASSUME_FORMATION_1	20		TRUE	150 108
        PET_ASSUME_FORMATION_2	20		TRUE	151 109
        PET_TRANSFER            20		TRUE	152 110
        PET_RELEASE             20		TRUE	153 111
        PET_TRICK_1             20		TRUE	154 112
        PET_TRICK_2             20		TRUE	155 113
        PET_TRICK_3             20		TRUE	156 114
        PET_TRICK_4             20		TRUE	157 115
        PET_GROUP               20		TRUE	158 116
        PET_TAME                8		TRUE	159 117
        PET_FEED                6		TRUE	160 118
        PET_SPECIAL_ATTACK_ONE	6		TRUE	161 119
        PET_SPECIAL_ATTACK_TWO	6		TRUE	162 120
        PET_RANGED_ATTACK       6		TRUE	163 121
     */

    private float followXOffset, followYOffset; // These will be different for each pet, depending on what the pet is and what it is doing.  Therefore, they should not be static.

    private String [] sSpatialCommands;
    private String [] sRadialCommands;
    private Player friend;
    private Player master;
    private transient byte trainCommandID;
    private transient boolean isInTrainingMode;
    private transient Vector<Waypoint> vPatrolPoints;
    private int eatLevel;
    private long lPetStomachTick;
    private boolean bCanGrow;
    private boolean bFullGrown;
    private float growthLevel;
    private long lGrowthTick;
    private Vector<Byte> vTrainedCommands;
    private IntangibleObject datapadControlIcon;
    private transient SOEObject objectToFollow;
    private transient boolean isOnPatrol;
    private transient long lNextPatrolStep;
    private transient int iCurrentPatrolStep;
    private transient Waypoint currentPatrolPoint;

    private transient int iRadialCondition = 0;
    private Hashtable<Character, RadialMenuItem> vRadials;
    private boolean isAnimal;

    /**
     *  337, 'outdoors_creaturehandler', ' '
        338, 'outdoors_creaturehandler_novice', 'pet_follow,pet_release,pet_attack,tame'
        339, 'outdoors_creaturehandler_master', 'pet_transfer,pet_rangedattack'
        340, 'outdoors_creaturehandler_taming_01', ' '
        341, 'outdoors_creaturehandler_taming_02', ' '
        342, 'outdoors_creaturehandler_taming_03', 'pet_specialattack1'
        343, 'outdoors_creaturehandler_taming_04', 'pet_specialattack2'
        344, 'outdoors_creaturehandler_training_01', 'pet_stay'
        345, 'outdoors_creaturehandler_training_02', 'pet_guard'
        346, 'outdoors_creaturehandler_training_03', 'pet_patrol'
        347, 'outdoors_creaturehandler_training_04', 'pet_formation'
        348, 'outdoors_creaturehandler_healing_01', 'trick1'
        349, 'outdoors_creaturehandler_healing_02', 'emboldenpets'
        350, 'outdoors_creaturehandler_healing_03', 'trick2'
        351, 'outdoors_creaturehandler_healing_04', 'enragepets'
        352, 'outdoors_creaturehandler_support_01', 'pet_group'
        353, 'outdoors_creaturehandler_support_02', 'pet_followother'
        354, 'outdoors_creaturehandler_support_03', 'pet_friend'
        355, 'outdoors_creaturehandler_support_04', 'train_mount'
     */

    private static final int [] iCommandSkillRelation = {
            338,//sSpatialCommands[0] = "follow";
            344,//sSpatialCommands[1] = "stay";
            345,//sSpatialCommands[2] = "guard";
            354,//sSpatialCommands[3] = "friend";
            338,//sSpatialCommands[4] = "attack";
            346,//sSpatialCommands[5] = "patrol";
            346,//sSpatialCommands[6] = "setpatrol";
            346,//sSpatialCommands[7] = "clearpatrol";
            347,//sSpatialCommands[8] = "formation1";
            347,//sSpatialCommands[9] = "formation2";
            339,//sSpatialCommands[10] = "transfer";
            338,//sSpatialCommands[11] = "release";
            348,//sSpatialCommands[12] = "trick1";
            350,//sSpatialCommands[13] = "trick2";
            339,//sSpatialCommands[14] = "trick3";
            339,//sSpatialCommands[15] = "trick4";
            352,//sSpatialCommands[16] = "group";
            338,//sSpatialCommands[17] = null; //Tame not used, done this way to keep relationship to radials
            0,//sSpatialCommands[18] = null; //feed not used,
            342,//sSpatialCommands[19] = "special1";
            343,//sSpatialCommands[20] = "special2";
            339,//sSpatialCommands[21] = "ranged";
           // 338,//sSpatialCommands[22] = "store";//friggin oddball
    };

    public CreaturePet(){
        //command index from the array + 142 = pet radial for the same command.
        sSpatialCommands = new String [22];
        sSpatialCommands[0] = "follow";
        sSpatialCommands[1] = "stay";
        sSpatialCommands[2] = "guard";
        sSpatialCommands[3] = "friend";
        sSpatialCommands[4] = "attack";
        sSpatialCommands[5] = "patrol";
        sSpatialCommands[6] = "setpatrol";
        sSpatialCommands[7] = "clearpatrol";
        sSpatialCommands[8] = "formation1";
        sSpatialCommands[9] = "formation2";
        sSpatialCommands[10] = "transfer";
        sSpatialCommands[11] = "store";
        sSpatialCommands[12] = "trick1";
        sSpatialCommands[13] = "trick2";
        sSpatialCommands[14] = "trick3";
        sSpatialCommands[15] = "trick4";
        sSpatialCommands[16] = "group";
        sSpatialCommands[17] = null; //not used, done this way to keep relationship to radials
        sSpatialCommands[18] = null; //not used,
        sSpatialCommands[19] = "special1";
        sSpatialCommands[20] = "special2";
        sSpatialCommands[21] = "ranged";
        //sSpatialCommands[22] = "store";//friggin oddball
        this.setBIsPet(true);
        this.setBIsWild(false);
        this.setBIsTameable(false);
        this.setBIsBaby(false);
        this.setBCanRoam(false);
        followXOffset = 1.5f;
        followYOffset = 1.5f;
        sRadialCommands  = new String [22];
        sRadialCommands[0] = "@pet/pet_menu:menu_follow";//"follow";
        sRadialCommands[1] = "@pet/pet_menu:menu_stay";//"stay";
        sRadialCommands[2] = "@pet/pet_menu:menu_guard";//"guard";
        sRadialCommands[3] = "@pet/pet_menu:menu_friend";//"friend";
        sRadialCommands[4] = "@pet/pet_menu:menu_attack";//"attack";
        sRadialCommands[5] = "@pet/pet_menu:menu_patrol";//"patrol";
        sRadialCommands[6] = "@pet/pet_menu:menu_get_patrol_point";//"setpatrol";
        sRadialCommands[7] = "@pet/pet_menu:menu_clear_patrol_points";//"clearpatrol";
        sRadialCommands[8] = "@pet/pet_menu:menu_assume_formation_1";//"formation1";
        sRadialCommands[9] = "@pet/pet_menu:menu_assume_formation_2";//"formation2";
        sRadialCommands[10] = "@pet/pet_menu:menu_transfer"; //"transfer";
        sRadialCommands[11] = "@pet/pet_menu:menu_release"; //"release";
        sRadialCommands[12] = "@pet/pet_menu:menu_trick_1"; //"trick1";
        sRadialCommands[13] = "@pet/pet_menu:menu_trick_2"; //"trick2";
        sRadialCommands[14] = "@pet/pet_menu:menu_trick_3"; //"trick3";
        sRadialCommands[15] = "@pet/pet_menu:menu_trick_4"; //"trick4";
        sRadialCommands[16] = "@pet/pet_menu:menu_group"; //"group";
        sRadialCommands[17] = "@pet/pet_menu:menu_tame"; //null; //not used, done this way to keep relationship to radials
        sRadialCommands[18] = "@pet/pet_menu:menu_feed"; //null; //not used,
        sRadialCommands[19] = "Special Attack 1"; //"special1";
        sRadialCommands[20] = "Special Attack 2"; //"special2";
        sRadialCommands[21] = "Ranged Attack"; //"ranged";
       // sRadialCommands[22] = "@pet/pet_menu:menu_store"; //"store";//friggin oddball

    }
    
    public void update(long lDeltaTimeMS) {
    	try{
           
            if(lPetStomachTick <= 0)
            {
                lPetStomachTick = 60000;

                if(eatLevel <= 0)
                {
                    eatLevel = 0;
                    this.sendPacketToRange(PacketFactory.buildNPCAnimation(this, "beg"));
                }
                else
                {
                    eatLevel -= 1;
                }
            }
            else
            {
                lPetStomachTick-=lDeltaTimeMS;
            }

            if(!this.bFullGrown && this.bCanGrow)
            {
                if(this.lGrowthTick <= 0)
                {
                    this.lGrowthTick = 60000 * 60;
                    this.growthLevel += 1.0f / 10;
                    if(this.growthLevel >= 1.0f)
                    {
                        this.bFullGrown = true;
                        this.bCanGrow = false;
                    }
                }
                else
                {
                    this.lGrowthTick-=lDeltaTimeMS;
                }
            }

            if(this.isOnPatrol)
            {
                //System.out.println("Patrol");
                if(vPatrolPoints==null)
                {
                    this.isOnPatrol = false;
                    
                }
                if(this.lNextPatrolStep <= 0 )
                {
                    
                    
                    currentPatrolPoint = vPatrolPoints.get(this.iCurrentPatrolStep);
                    iCurrentPatrolStep++;
                    if(iCurrentPatrolStep > (vPatrolPoints.size() -1))
                    {
                        iCurrentPatrolStep = 0;
                    }
                    
                    SOEObject targetLocation = new Waypoint();
                    targetLocation.setX(currentPatrolPoint.getX());
                    targetLocation.setY(currentPatrolPoint.getY());
                    targetLocation.setZ(currentPatrolPoint.getZ());
                    targetLocation.setPlanetID(currentPatrolPoint.getPlanetID());
                    this.lNextPatrolStep = (long)((1000 * (ZoneServer.getRangeBetweenObjects(this, currentPatrolPoint))) / 2);
                    if(this.lNextPatrolStep <= 0)
                    {
                        this.lNextPatrolStep = 5000;
                    }
                    
                    this.setX(currentPatrolPoint.getX());
                    this.setY(currentPatrolPoint.getY());
                    this.setZ(currentPatrolPoint.getZ());
                    this.setMovementAngle(this.absoluteBearingRadians(this, targetLocation));
                    this.setVelocity(1.0f);
                    master.getServer().moveObjectInTree(this, this.getX(), this.getY(), this.getPlanetID(), this.getPlanetID());
                    this.sendPacketToRange(PacketFactory.buildUpdateTransformMessage(this));                    
                }
                else
                {
                    this.lNextPatrolStep-=lDeltaTimeMS;
                    if(this.lNextPatrolStep < 0)
                    {
                        this.lNextPatrolStep = 0;
                    }
                }
            }
        }catch(Exception e){
            DataLog.logException("Exception in Update","CreaturePet", ZoneServer.ZoneRunOptions.bLogToConsole,true, e);
        }
    }

    protected void processSpatialCommand(ZoneClient client, String [] sCommand)
    {
        
        //System.out.println("Process Spatial Command To Pet");
        //for(int i =0; i < sCommand.length; i++)
        //{
        //    System.out.println("C: " + i + " : " + sCommand[i]);
        //}
        
        byte commandID = 0;
        for(commandID = 0; commandID < sSpatialCommands.length; commandID++)
        {
            if(sSpatialCommands[commandID]!=null && sSpatialCommands[commandID].toLowerCase().contentEquals(sCommand[5]))
            {
                commandID += (byte)(142);
                break;
            }
        }
        if(this.isInTrainingMode())
        {
            this.trainCommand(client, sCommand);
        }
        else
        {
            this.petCommand(client, commandID, sCommand);
        }
    }

    public boolean isInTrainingMode() {
        return isInTrainingMode;
    }

    public void setIsInTrainingMode(boolean isInTrainingMode) {
        this.isInTrainingMode = isInTrainingMode;
    }

    public byte getTrainCommandID() {
        return trainCommandID;
    }

    public void setTrainCommandID(byte trainCommandID) {
        this.trainCommandID = trainCommandID;
    }

    protected void trainCommand(ZoneClient client, String [] sCommand){
        try{
            if(hasState(Constants.STATE_COMBAT))
            {
                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot train a pet while you or the pet is in combat."));
                this.setIsInTrainingMode(false);
            }
            else
            {


                if(client.getServer().getGUI().getDB().isNameAppropriate(sCommand[5]) != Constants.NAME_ACCEPTED)
                {
                    petSpeak(Constants.PET_ANIMAL_NEGATIVE_RESPONSE);
                    client.insertPacket(PacketFactory.buildChatSystemMessage("pet/pet_menu","pet_nolearn"));
                    this.setIsInTrainingMode(false);
                    return;
                }
                byte commandIndex = (byte)(getTrainCommandID() - (byte)142);
                if(this.getVTrainedCommands() == null)
                {
                    this.vTrainedCommands = new Vector<Byte>();
                }
                if(SWGGui.getRandomInt(0,iCommandSkillRelation[commandIndex]) == 0)
                {
                    if(this.isAnimal)
                    {
                        petSpeak(Constants.PET_ANIMAL_NEGATIVE_RESPONSE);
                    }
                    else
                    {
                        petSpeak(Constants.PET_NPC_RESPONSES[0]);
                    }
                    this.setIsInTrainingMode(false);
                    client.insertPacket(PacketFactory.buildChatSystemMessage("pet/pet_menu","pet_nolearn"));
                    return;
                }
                if(!this.vTrainedCommands.contains(commandIndex))
                {
                    this.vTrainedCommands.add(commandIndex);
                    client.getPlayer().updateExperience(null, DatabaseInterface.getExperienceIDFromName("creaturehandler"),150);
                }
                
                if(client.getPlayer().hasSkill(iCommandSkillRelation[commandIndex]))
                {
                    sSpatialCommands[commandIndex] = sCommand[5];
                    
                    if(this.isAnimal())
                    {
                        petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                    }
                    else
                    {
                        petSpeak(Constants.PET_NPC_RESPONSES[2]);
                    }
                    client.insertPacket(PacketFactory.buildChatSystemMessage("pet/pet_menu","pet_learn"));
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage("You lack the skill to train that command."));
                }
            }
            this.setIsInTrainingMode(false);
            if(vTrainedCommands.size() >= 4)
            {
                int sameCount = 0;
                String sLast = "";
                for(int i =0; i < vTrainedCommands.size(); i++)
                {
                    int c = vTrainedCommands.get(i);
                    String sCurrent = this.sSpatialCommands[c];
                    //System.out.println("Checking Current: " + sCurrent);
                    if(sLast.isEmpty())
                    {
                        sLast = sCurrent;
                        sameCount = 0;
                    }
                    else if(sCurrent.contentEquals(sLast))
                    {
                        sLast = sCurrent;
                        if(sameCount == 0)
                        {
                            sameCount = 1;
                        }
                        sameCount++;
                    }
                    else
                    {
                        sLast = "";
                        sameCount = 0;
                    }
                    if(sameCount == 4)
                    {
                        this.setFirstName(sLast);
                        this.sendPacketToRange(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)3,(short)1, (short)2, this, this.getFullName(), true));
                        this.getDatapadControlIcon().setCustomName(sLast, false);
                        client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_ITNO, (byte)3,(short)1, (short)2, this.getDatapadControlIcon(), this.getDatapadControlIcon().getCustomName(), true));
                        //System.out.println("Pet Renamed to: " + this.getFullName());
                        break;
                    }
                }
                //System.out.println("Same Count: " + sameCount);
            }
        }catch(Exception e){
            DataLog.logException("Exception while trainCommand", "CreaturePet", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
    }

    protected void petCommand(ZoneClient client, byte commandID,String [] sCommand){
        try{
            try{
            if(sCommand == null || sCommand.length == 0)
            {
                sCommand = new String[6];
                for(int i = 0; i < sCommand.length; i++)
                {
                    sCommand[i] = "";
                }
            }
            }catch(Exception s){
                if(s instanceof java.lang.ArrayIndexOutOfBoundsException)
                {
                    sCommand = new String[6];
                    for(int i = 0; i < sCommand.length; i++)
                    {
                        sCommand[i] = "";
                    }
                }
            }
            switch(commandID)
            {
                case (byte)100:
                case (byte)101:
                case (byte)102:
                case (byte)103:
                case (byte)104:
                case (byte)105:
                case (byte)106:
                case (byte)107:
                case (byte)108:
                case (byte)109:
                case (byte)110:
                case (byte)111:
                case (byte)112:
                case (byte)113:
                case (byte)114:
                case (byte)115:
                case (byte)116:
                case (byte)117:
                case (byte)118:
                case (byte)119:
                case (byte)120:
                case (byte)121:
                {
                    this.setTrainCommandID((byte)(commandID + 42));
                    this.setIsInTrainingMode(true);
                    petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                    return;
                }
            }
            if(commandID == 59 || commandID == (byte)164 )
            {
                commandID = (byte)153;
            }
            if(!this.getVTrainedCommands().contains((byte)(commandID - (byte)142)))
            {
                
                return;
            }
            switch(commandID)
            {                
                case (byte)153: //PET_RELEASE                
                {
                    this.isOnPatrol = false;
                    if(client.getPlayer().equals(this.master))
                    {
                        if(this.isFollowing())
                        {
                            objectToFollow.removePetFollowingObject(this);
                            objectToFollow = null;
                        }
                        this.datapadControlIcon.useItem(client, (byte)59);
                    }
                    break;
                }
                case (byte)142: //PET_FOLLOW
                {
                    this.isOnPatrol = false;
                    if(objectToFollow!=null)
                    {
                        objectToFollow.removePetFollowingObject(this);
                        objectToFollow = null;
                    }
                    if(client.getPlayer().getTargetID() == 0)
                    {
                        objectToFollow = client.getPlayer();
                        objectToFollow.addPetFollowingObject(this);
                        if(this.isAnimal)
                        {
                            this.petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                        }
                        else
                        {
                            this.petSpeak(Constants.PET_NPC_RESPONSES[1]);
                        }
                    }
                    else
                    {
                        objectToFollow = client.getServer().getObjectFromAllObjects(client.getPlayer().getTargetID());
                        if(objectToFollow.equals(this))
                        {
                            objectToFollow = client.getPlayer();
                        }
                        objectToFollow.addPetFollowingObject(this);
                        if(sCommand[0].contentEquals("silent"))
                        {
                            return;
                        }
                         if(this.isAnimal)
                         {
                             this.petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                         }
                         else
                         {
                            this.petSpeak(Constants.PET_NPC_RESPONSES[1]);
                         }
                    }
                    break;
                }
                case (byte)143: //PET_STAY
                {
                    if(objectToFollow!=null)
                    {
                        objectToFollow.removePetFollowingObject(this);
                    }
                    objectToFollow = null;
                    this.isOnPatrol = false;
                    if(sCommand[0].contentEquals("silent"))
                        {
                            return;
                        }
                     if(this.isAnimal)
                     {
                         this.petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                     }
                     else
                     {
                        this.petSpeak(Constants.PET_NPC_RESPONSES[1]);
                     }
                    break;
                }
                case (byte)144: //PET_GUARD
                {
                    if(client.getPlayer().equals(this.master) || client.getPlayer().equals(this.friend))
                    {

                    }
                    if(sCommand[0].contentEquals("silent"))
                        {
                            return;
                        }
                     if(this.isAnimal)
                     {
                         this.petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                     }
                     else
                     {
                        this.petSpeak(Constants.PET_NPC_RESPONSES[1]);
                     }
                    break;
                }
                case (byte)145: //PET_FRIEND
                {
                    if(client.getPlayer().equals(this.master) || client.getPlayer().equals(this.friend))
                    {
                        if(this.friend != null)
                        {
                            this.friend = null;
                        }
                        if(client.getPlayer().getTargetID() != 0)
                        {
                            this.friend = client.getServer().getPlayer(client.getPlayer().getTargetID());
                            friend.addFriendPet(this);
                            if(sCommand[0].contentEquals("silent"))
                            {
                                return;
                            }
                            if(this.isAnimal)
                             {
                                 this.petSpeak(Constants.PET_ANIMAL_NEGATIVE_RESPONSE);
                             }
                             else
                             {
                                this.petSpeak(Constants.PET_NPC_RESPONSES[0]);
                             }
                        }
                    }
                    break;
                }
                case (byte)146: //PET_ATTACK
                {

                    if(sCommand[0].contentEquals("silent"))
                        {
                            return;
                        }
                    if(this.isAnimal)
                     {
                         this.petSpeak(Constants.PET_ANIMAL_NEGATIVE_RESPONSE);
                     }
                     else
                     {
                        this.petSpeak(Constants.PET_NPC_RESPONSES[0]);
                     }
                    break;
                }
                case (byte)147: //PET_PATROL
                {
                    if(this.vPatrolPoints == null || this.vPatrolPoints.isEmpty())
                    {
                        if(sCommand[0].contentEquals("silent"))
                        {
                            return;
                        }
                        if(this.isAnimal)
                         {
                             this.petSpeak(Constants.PET_ANIMAL_NEGATIVE_RESPONSE);
                         }
                         else
                         {
                            this.petSpeak(Constants.PET_NPC_RESPONSES[0]);
                         }
                    }
                    else
                    {
                        if(objectToFollow!=null)
                        {
                            objectToFollow.removePetFollowingObject(this);
                        }
                        objectToFollow = null;
                        iCurrentPatrolStep = (vPatrolPoints.size() - 1);
                        this.lNextPatrolStep = 2000;
                        if(sCommand[0].contentEquals("silent"))
                        {
                            return;
                        }
                         if(this.isAnimal)
                         {
                             this.petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                         }
                         else
                         {
                            this.petSpeak(Constants.PET_NPC_RESPONSES[1]);
                         }
                        this.isOnPatrol = true;
                    }
                    break;
                }
                case (byte)148: //PET_GET_PATROL_POINT
                {
                    this.isOnPatrol = false;
                    this.setPatrolPoint(client,client.getPlayer().getX(), client.getPlayer().getY(),client.getPlayer().getZ());
                    if(sCommand[0].contentEquals("silent"))
                        {
                            return;
                        }
                    if(this.isAnimal)
                     {
                         this.petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                     }
                     else
                     {
                        this.petSpeak(Constants.PET_NPC_RESPONSES[1]);
                     }
                    break;
                }
                case (byte)149: //PET_CLEAR_PATROL_POINTS
                {
                    this.isOnPatrol = false;
                    this.clearPatrolPoints(client);
                     if(this.isAnimal)
                     {
                         this.petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                     }
                     else
                     {
                        this.petSpeak(Constants.PET_NPC_RESPONSES[1]);
                     }
                    break;
                }
                case (byte)150: //PET_ASSUME_FORMATION_1
                {
                    int iPetFormationCount = master.getCalledPets().size();
                    if(iPetFormationCount <= 1)
                    {
                        this.setFollowXOffset(-2.5f);
                        this.setFollowYOffset(-2.5f);
                        if(sCommand[0].contentEquals("silent"))
                        {
                            return;
                        }
                        if(this.isAnimal)
                         {
                             this.petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                         }
                         else
                         {
                            this.petSpeak(Constants.PET_NPC_RESPONSES[1]);
                         }
                        return;
                    }
                    for(int i = 0; i < iPetFormationCount; i++)
                    {
                        switch(i)
                        {
                            case 0:
                            {
                                CreaturePet pet = master.getCalledPets().get(i);
                                if(pet.equals(this))
                                {
                                    this.setFollowXOffset(-2.5f);
                                    this.setFollowYOffset(-2.5f);
                                }
                                break;
                            }
                            case 1:
                            {
                                CreaturePet pet = master.getCalledPets().get(i);
                                if(pet.equals(this))
                                {
                                    this.setFollowXOffset(2.5f);
                                    this.setFollowYOffset(2.5f);
                                }
                                break;
                            }
                            case 2:
                            {
                                CreaturePet pet = master.getCalledPets().get(i);
                                if(pet.equals(this))
                                {
                                    this.setFollowXOffset(-3.5f);
                                    this.setFollowYOffset(-3.5f);
                                }
                                break;
                            }
                            case 3:
                            {
                                CreaturePet pet = master.getCalledPets().get(i);
                                if(pet.equals(this))
                                {
                                    this.setFollowXOffset(3.5f);
                                    this.setFollowYOffset(3.5f);
                                }
                                break;
                            }
                        }
                    }
                    if(sCommand[0].contentEquals("silent"))
                        {
                            return;
                        }
                    if(this.isAnimal)
                     {
                         this.petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                     }
                     else
                     {
                        this.petSpeak(Constants.PET_NPC_RESPONSES[1]);
                     }
                    break;
                }
                case (byte)151: //PET_ASSUME_FORMATION_2
                {
                    break;
                }
                case (byte)152: //PET_TRANSFER
                {
                    if(client.getPlayer().equals(this.master))
                    {


                        if(sCommand[0].contentEquals("silent"))
                        {
                            return;
                        }
                        if(this.isAnimal)
                         {
                             this.petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                         }
                         else
                         {
                            this.petSpeak(Constants.PET_NPC_RESPONSES[1]);
                         }
                    }
                    break;
                }               
                case (byte)154: //PET_TRICK_1
                {
                    if(sCommand[0].contentEquals("silent"))
                        {
                            return;
                        }
                        if(this.isAnimal)
                         {
                             this.petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                         }
                         else
                         {
                            this.petSpeak(Constants.PET_NPC_RESPONSES[1]);
                         }
                    break;
                }
                case (byte)155: //PET_TRICK_2
                {
                    if(sCommand[0].contentEquals("silent"))
                        {
                            return;
                        }
                        if(this.isAnimal)
                         {
                             this.petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                         }
                         else
                         {
                            this.petSpeak(Constants.PET_NPC_RESPONSES[1]);
                         }
                    break;
                }
                case (byte)156: //PET_TRICK_3
                {
                    if(sCommand[0].contentEquals("silent"))
                        {
                            return;
                        }
                        if(this.isAnimal)
                         {
                             this.petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                         }
                         else
                         {
                            this.petSpeak(Constants.PET_NPC_RESPONSES[1]);
                         }
                    break;
                }
                case (byte)157: //PET_TRICK_4
                {
                    if(sCommand[0].contentEquals("silent"))
                        {
                            return;
                        }
                        if(this.isAnimal)
                         {
                             this.petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                         }
                         else
                         {
                            this.petSpeak(Constants.PET_NPC_RESPONSES[1]);
                         }
                    break;
                }
                case (byte)158: //PET_GROUP
                {
                    if(sCommand[0].contentEquals("silent"))
                        {
                            return;
                        }
                        if(this.isAnimal)
                         {
                             this.petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                         }
                         else
                         {
                            this.petSpeak(Constants.PET_NPC_RESPONSES[1]);
                         }
                    break;
                }
                case (byte)159: //PET_TAME
                {

                    break;
                }
                case (byte)160: //PET_FEED
                {
                    if(eatLevel < 100)
                    {
                        eatLevel = 100;
                        this.sendPacketToRange(PacketFactory.buildNPCAnimation(this, "eat"));
                        if(sCommand[0].contentEquals("silent"))
                        {
                            return;
                        }
                         if(this.isAnimal)
                         {
                             this.petSpeak(Constants.PET_ANIMAL_POSITIVE_RESPONSE);
                         }
                         else
                         {
                            this.petSpeak(Constants.PET_NPC_RESPONSES[1]);
                         }
                    }
                    else
                    {
                        client.insertPacket(PacketFactory.buildChatSystemMessage("Your pet is not hungry"));
                    }
                    break;
                }
                case (byte)161: //PET_SPECIAL_ATTACK_ONE
                {
                    break;
                }
                case (byte)162: //PET_SPECIAL_ATTACK_TWO
                {
                    break;
                }
                case (byte)163: //PET_RANGED_ATTACK
                {
                    break;
                }
                case (byte)205: // mount
                {
                    break;
                }
                case (byte)206://dismount
                {
                    break;
                }
            }

        }catch(Exception e){
                DataLog.logException("Exception in Pet Command", "CreaturePet", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
            }
    }

    public Player getFriend() {
        return friend;
    }

    public void setFriend(Player friend) {
        this.friend = friend;
    }

    public Player getMaster() {
        return master;
    }

    public void setMaster(Player master) {
        this.master = master;
    }

    private void setPatrolPoint(ZoneClient client, float x, float y, float z){
        try{
            if(vPatrolPoints == null)
            {
                vPatrolPoints = new Vector<Waypoint>();
            }
            boolean patrolPointExists = false;
            for(int i = 0 ; i < vPatrolPoints.size();i++)
            {
                Waypoint w = vPatrolPoints.get(i);
                if(w.getX() == x && w.getY() == y)
                {
                    patrolPointExists = true;
                }
            }
            if(!patrolPointExists)
            {
                Waypoint w = new Waypoint();
                w.setX(x);
                w.setY(y);
                w.setZ(z);
                w.setPlanetID(client.getPlayer().getPlanetID());
                vPatrolPoints.add(w);
                client.insertPacket(PacketFactory.buildChatSystemMessage("pet/pet_menu","patrol_added"));
            }

        }catch(Exception e){
            DataLog.logException("Exception in setPatrolPoint", "CreaturePet", ZoneServer.ZoneRunOptions.bLogToConsole,true, e);
        }
    }

    private void clearPatrolPoints(ZoneClient client){
        try{
            if(vPatrolPoints == null)
            {
                vPatrolPoints = new Vector<Waypoint>();
            }
            vPatrolPoints.clear();
            client.insertPacket(PacketFactory.buildChatSystemMessage("pet/pet_menu","patrol_removed"));
        }catch(Exception e){
            DataLog.logException("Exception in clearPatrolPoints", "CreaturePet", ZoneServer.ZoneRunOptions.bLogToConsole,true, e);
        }
    }

    private void sendPacketToRange(byte [] packet){
        try{
            Vector<Player> vPL = master.getServer().getPlayersAroundNPC(this);
            for(int i = 0; i < vPL.size(); i++)
            {
                Player p = vPL.get(i);
                p.getClient().insertPacket(packet);
            }
        }catch(Exception e){
            DataLog.logException("Exception while sendingPacketToRange", "CreaturePet", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
    }

    private void petSpeak(String spoken){
         try{
            Vector<Player> vPL = master.getServer().getPlayersAroundNPC(this);
            for(int i = 0; i < vPL.size(); i++)
            {
                Player p = vPL.get(i);
                p.getClient().insertPacket(PacketFactory.buildNPCSpeak(p, this, null, spoken, (short)0, (short)0));
            }
        }catch(Exception e){
            DataLog.logException("Exception while sendingPacketToRange", "CreaturePet", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
    }

    public boolean canGrow() {
        return bCanGrow;
    }

    public void setCanGrow(boolean bCanGrow) {
        this.bCanGrow = bCanGrow;
    }

    public float getGrowthLevel() {
        return growthLevel;
    }

    public void setGrowthLevel(float growthLevel) {
        this.growthLevel = growthLevel;
    }

    public boolean isFullGrown() {
        return bFullGrown;
    }

    public void setFullGrown(boolean bFullGrown) {
        this.bFullGrown = bFullGrown;
    }

    public Vector<Byte> getVTrainedCommands() {
        if(vTrainedCommands == null)
        {
            vTrainedCommands = new Vector<Byte>();
        }
        return vTrainedCommands;
    }

    public String[] getSpatialCommands() {
        return sSpatialCommands;
    }

    public IntangibleObject getDatapadControlIcon() {
        return datapadControlIcon;
    }

    public void setDatapadControlIcon(IntangibleObject datapadControlIcon) {
        this.datapadControlIcon = datapadControlIcon;
    }

    public float getFollowXOffset() {
        return followXOffset;
    }

    public void setFollowXOffset(float followXoffset) {
        this.followXOffset = followXoffset;
    }

    public float getFollowYOffset() {
        return followYOffset;
    }

    public void setFollowYOffset(float followYOffset) {
        this.followYOffset = followYOffset;
    }

    protected boolean isFollowing(){
        if(this.objectToFollow!=null)
        {
            return true;
        }
        return false;
    }
    
    protected SOEObject objectBeingFollowed(){
        return this.objectToFollow;
    }

    @Override
    public Hashtable<Character, RadialMenuItem> getRadialMenus(ZoneClient client){
    
        try{

           // if(vRadials == null || vRadials.size() == 0)
            {
                
                Hashtable<Character, RadialMenuItem> retHash = new Hashtable<Character, RadialMenuItem>();
                //(byte ButtonNumber, byte ParentButton, char CommandID,byte ActionLocation, String ButtonText)
                //examine
                byte buttonID = 1;
                byte parentID = 0;
                RadialMenuItem newRadial = new RadialMenuItem(buttonID,parentID,(char)7,(byte)1,"");
                retHash.put(newRadial.getCommandID(), newRadial);
                buttonID++;
                if(this.getMaster() != null && client.getPlayer().equals(this.getMaster()) || this.getFriend() != null && client.getPlayer().equals(this.getFriend()))
                {
                    //commands
                    newRadial = new RadialMenuItem(buttonID,parentID,(char)141,(byte)1,"Commands");
                    retHash.put(newRadial.getCommandID(), newRadial);
                    buttonID++;
                    parentID = 2;
                    for(byte i = 0; i < this.vTrainedCommands.size();i++)
                    {
                        byte iTrainedCommand = vTrainedCommands.get(i);
                        newRadial = new RadialMenuItem((byte)(buttonID),parentID,(char)(iTrainedCommand+(byte)142),(byte)3,sRadialCommands[iTrainedCommand]);
                        retHash.put(newRadial.getCommandID(), newRadial);
                        buttonID++;
                    }
                    parentID = 0;
                    if(client.getPlayer().hasSkill(338))
                    {
                        if(master.equals(client.getPlayer()))
                        {
                            newRadial = new RadialMenuItem(buttonID,parentID,(char)140,(byte)1,"@pet/pet_menu:menu_command");
                            retHash.put(newRadial.getCommandID(), newRadial);
                            parentID = buttonID;
                            buttonID++;

                            for(int i = 0; i < iCommandSkillRelation.length; i++)
                            {
                                if(client.getPlayer().hasSkill(iCommandSkillRelation[i]))
                                {
                                    if(i != 17)  //its already tamed so we dont send the tame option.
                                    {
                                        newRadial = new RadialMenuItem(buttonID,parentID,(char)(i+100),(byte)3,sRadialCommands[i]);
                                        retHash.put(newRadial.getCommandID(), newRadial);
                                        buttonID++;
                                    }
                                }
                            }
                        }
                    }
                    parentID = 0;
                    if(client.getPlayer().hasSkill(355) && this.bFullGrown)
                    {
                        newRadial = new RadialMenuItem(buttonID,parentID,(char)207,(byte)1,"");
                        retHash.put(newRadial.getCommandID(), newRadial);
                    }
                }
                
                return retHash;
            }
           // return vRadials;
        }catch(Exception e){
            DataLog.logException("Exception caught in getRadialMenus", "CreaturePet",ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
        return vRadials;
    }

    @Override
    protected void setRadialCondition(int condition){
        iRadialCondition = condition;
    }

    @Override
    protected int getRadialCondition(){
        return iRadialCondition;
    }

    public boolean isAnimal() {
        return isAnimal;
    }

    public void setIsAnimal(boolean isAnimal) {
        this.isAnimal = isAnimal;
    }


}
