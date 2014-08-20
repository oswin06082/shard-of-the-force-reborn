import java.util.Vector;

/**
 * A Group object is a collection of Players and/or Player Pets which are cohesive.
 * @author Darryl
 *
 */
public class Group extends SOEObject{
	public final static long serialVersionUID = 1;
	private final static byte MAX_GROUP_SIZE = 20;
	private Player groupLeader;        
	private Vector<SOEObject> vGroupMembers;
    private int iGroupUpdateCounter;
    private long lGroupAge;
    private long lLeaderOffLineTimer;
    private long lNextMemberUpdate;
    private long lGroupUpdateDelta;
        private Player updateObject;
        
        //5463, 'object/group/shared_group_object.iff', '', '', '', '', '', '', '', '', '', '', 2022504856, 0
        
	/**
	 * Construct a new (empty) group.
	 */
	public Group(Player p) {
		super();
                this.setID(p.getServer().getNextObjectID());        
                this.setCRC(2022504856);
                this.setOrientationW(1);
                p.getServer().addObjectToAllObjects(this, false,false);
                vGroupMembers = new Vector<SOEObject>();
                
                groupLeader = p;
                updateObject = p;
                
                groupLeader.setGroupObject(this);
                lGroupAge = 0;
                lNextMemberUpdate = 0;
                lGroupUpdateDelta = 0;
                iGroupUpdateCounter = 0;
                
	}
	
        public boolean groupLeaderOnlineStatus(){
            return groupLeader.getOnlineStatus();
        }

        public Player getUpdateObject() {
            return updateObject;
        }

        public void update( long lDeltaMS ){
            try{
                this.lGroupAge += lDeltaMS;

                if(lNextMemberUpdate <= 0)
                { 
                    
                    //update group timers every two minutes
                    lNextMemberUpdate = 1000 * 60 * 2;
                    for(int i = 0; i < this.vGroupMembers.size();i++)
                    {
                        SOEObject o = this.vGroupMembers.get(i);
                        if(o instanceof Player)
                        {
                            Player p = (Player)o;
                            p.updateGroupTime(lGroupUpdateDelta);                            
                        }
                    }                
                    lGroupUpdateDelta = 0;
                }
                else
                {
                    lGroupUpdateDelta += lDeltaMS;
                    lNextMemberUpdate -= lDeltaMS;
                    if(lNextMemberUpdate < 0 )
                    {
                        lNextMemberUpdate = 0;
                    }
                }
                if(!groupLeader.getOnlineStatus())
                {
                    lLeaderOffLineTimer += lDeltaMS;
                    if(lLeaderOffLineTimer >= 1000*60*5)
                    {
                        //need to elect a new group leader cause ours went offline for more than 5 mins.
                        this.removeMemberFromGroup(groupLeader,Constants.GROUP_REMOVE_REASON_LEAVE);
                    }
                }
                else
                {
                    lLeaderOffLineTimer = 0;
                }

                if(!updateObject.getOnlineStatus())
                {
                    //the one making updates to the group is offline we need a new update source.
                    //lets elect the oldest player to be our new update source.
                    long lLongest = 0;
                    Player oldest = null;
                    SOEObject currentUpdateObject = updateObject;
                    for(int i = 0; i < this.vGroupMembers.size(); i++)
                    {
                        SOEObject o = this.vGroupMembers.get(i);
                        if(o instanceof Player)
                        {
                            Player p = (Player)o;
                            long lTime = p.getGroupTime();
                            if(lTime >= lLongest && !currentUpdateObject.equals(p))
                            {
                                lLongest = lTime;
                                oldest = p;
                            }
                        }
                    }
                    updateObject = oldest;
                }
            }catch(Exception e){
                System.out.println("Exception caught in Group.update() " + e);
                e.printStackTrace();
            }
        }
        
	/**
	 * Get the list of Players which are members of this Group.
	 * @return A Vector containing all the Players of this Group.
	 */
	public Vector<SOEObject> getGroupMembers() {
		return vGroupMembers;
	}

	/**
	 * Adds a Player to this group as a member.
	 * @param p
	 */
	public boolean addMemberToGroup(SOEObject o) {
		try{
			if (vGroupMembers.size() < MAX_GROUP_SIZE){                        
                //notify new member to all members                           
                getGroupUpdateCounter(true);
                for(int i = 0; i < vGroupMembers.size(); i++)
                {
                    SOEObject gM = vGroupMembers.get(i);
                    if(gM instanceof Player)
                    {   Player pGM = (Player)gM;
                        pGM.getClient().insertPacket(PacketFactory.buildDeltasMessageGRUP(Constants.BASELINES_GRUP, (byte)6, (short)1,(short)1,Constants.Group_AddMember,this, o, this.iGroupUpdateCounter,pGM,(short)-1));                        
                    }                        
                }                        
                vGroupMembers.add(o);
                if(o instanceof Player)
                {
                    Player p = (Player)o;
                    
                    p.setGroupObject(this);                            
                    p.spawnItem(this);
                    p.getClient().insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO,(byte)6,(short)1,(short)6,p,this.getID()));
                    p.setGroupHost(0);
                }
                
                return true;
			}else {
			    groupLeader.getClient().insertPacket(PacketFactory.buildChatSystemMessage(
                		"group",
                		"full",
                		0l,
                		"",
                		"",
                		"",
                		0l,
                		"",
                		"",
                		"",
                		0l,
                		"",
                		"",
                		"",
                		0,
                		0f, false));                        
			}
            return false;
        }catch(Exception e){
            System.out.println("Exception Caught in Group.addMemberToGroup " + e);
            e.printStackTrace();
        }
        return false;
	}
	/**
	 * Removes a Player from the group.
	 * @param p -- The player to be removed.
	 */
	public void removeMemberFromGroup(SOEObject o, byte reason) {
            try{
                if(o instanceof Player)
                {
                    if(this.groupLeader.equals(o))
                    {
                        this.electNewLeader();
                    }
                    Player p = (Player)o;
                    p.setGroupObject(null);
                    p.setGroupHost(0);
                    p.getClient().insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO,(byte)6, (short)1, (short)6,p, p.getGroupID()));                
                    switch(reason)
                    {
                        case Constants.GROUP_REMOVE_REASON_LEAVE:
                        {
                            //p.getClient().insertPacket(PacketFactory.buildChatSystemMessage("You leave the Group."));
                            this.sendPacketToGroup(PacketFactory.buildChatSystemMessage(
                            		"group",
                            		"other_left_prose",
                            		p.getID(),
                            		p.getSTFFileName(),
                            		p.getSTFFileIdentifier(),
                            		p.getFirstName(),
                            		0l,
                            		"",
                            		"",
                            		"",
                            		0l,
                            		"",
                            		"",
                            		"",
                            		0,
                            		0f, false));
                            break;
                        }
                        case Constants.GROUP_REMOVE_REASON_KICK:
                        {
                            p.getClient().insertPacket(PacketFactory.buildChatSystemMessage(
                            		"group",
                            		"removed",
                            		0l,
                            		"",
                            		"",
                            		"",
                            		0l,
                            		"",
                            		"",
                            		"",
                            		0l,
                            		"",
                            		"",
                            		"",
                            		0,
                            		0f, false));
                            this.sendPacketToGroup(PacketFactory.buildChatSystemMessage(
                            		"group",
                            		"other_left_prose",
                            		p.getID(),
                            		p.getSTFFileName(),
                            		p.getSTFFileIdentifier(),
                            		p.getFirstName(),
                            		0l,
                            		"",
                            		"",
                            		"",
                            		0l,
                            		"",
                            		"",
                            		"",
                            		0,
                            		0f, false));
                            break;
                        }
                    }
                }
                short deleteIndex = (short)vGroupMembers.indexOf(o);
                vGroupMembers.remove(o);
                //this is going to have to be chnaged for the proper delta
                for(int i = 0; i < vGroupMembers.size(); i++)
                {
                    SOEObject gM = vGroupMembers.get(i);
                    if(gM instanceof Player)
                    {
                        Player pGM = (Player)gM;
                        //pGM.getClient().insertPacket(PacketFactory.buildSceneDestroyObject(this));
                        if(vGroupMembers.size() > 1)
                        {
                            //pGM.spawnItem(this);
                            pGM.getClient().insertPacket(PacketFactory.buildDeltasMessageGRUP(Constants.BASELINES_GRUP, (byte)6, (short)1,(short)1,Constants.Group_DeleteMember,this, o, this.iGroupUpdateCounter,pGM,deleteIndex));
                        }
                        else
                        {
                            pGM.setGroupObject(null);
                            pGM.getClient().insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO,(byte)6, (short)1, (short)6,pGM, pGM.getGroupID()));                
                            pGM.getClient().insertPacket(PacketFactory.buildChatSystemMessage(
                            		"group",
                            		"disbanded",
                            		0l,
                            		"",
                            		"",
                            		"",
                            		0l,
                            		"",
                            		"",
                            		"",
                            		0l,
                            		"",
                            		"",
                            		"",
                            		0,
                            		0f, false));
                        }
                    }
                }
                if(vGroupMembers.size() == 1)
                {
                    this.groupLeader.getServer().removeObjectFromAllObjects(this.getID());
                }
            }catch(Exception e){
                System.out.println("Exception Caught in Group.removeMemberFromGroup " + e);
                e.printStackTrace();
            }		
	}
	
	/**
	 * Sets the given Player as the group leader, provided the player is a member of the group.
	 * If the Player is not a member of the group, this function does nothing.
	 * @param p
	 */
	public void setGroupLeader(Player p) {
            try{
		if (vGroupMembers.contains(p)) {
                    int iNewLeaderIndex = vGroupMembers.indexOf(p);
                    SOEObject newLeader = vGroupMembers.get(iNewLeaderIndex);
                    SOEObject oldLeader = vGroupMembers.get(0);                    
                    groupLeader = (Player)newLeader;
                    Vector<SOEObject> vTempMemberList = new Vector<SOEObject>();
                    for(int i = 0; i < vGroupMembers.size(); i++)
                    {
                        if(i == 0)
                        {
                            vTempMemberList.add(newLeader);
                        }
                        else if(i == iNewLeaderIndex)
                        {
                            vTempMemberList.add(oldLeader);
                        }
                        else
                        {
                            vTempMemberList.add(vGroupMembers.get(i));
                        }
                    }
                    vGroupMembers.clear();
                    vGroupMembers.addAll(vTempMemberList);
                    
                    for(int i = 0; i < vGroupMembers.size();i++)
                    {
                        SOEObject gM = vGroupMembers.get(i);
                        if(gM instanceof Player)
                        {
                            Player pGM = (Player)gM;
                            pGM.getClient().insertPacket(PacketFactory.buildDeltasMessageGRUP(Constants.BASELINES_GRUP, (byte)6, (short)1,(short)1,Constants.Group_GroupLeader,this, newLeader, this.iGroupUpdateCounter,pGM,(short)-1));
                            pGM.getClient().insertPacket(PacketFactory.buildDeltasMessageGRUP(Constants.BASELINES_GRUP, (byte)6, (short)1,(short)1,Constants.Group_GroupLeader,this, oldLeader, this.iGroupUpdateCounter,pGM,(short)-1));
                            // %TU is now the group leader.
                            pGM.getClient().insertPacket(PacketFactory.buildChatSystemMessage(
                            		"group",
                            		"new_leader",
                            		groupLeader.getID(),
                            		groupLeader.getSTFFileName(),
                            		groupLeader.getSTFFileIdentifier(),
                            		groupLeader.getFirstName(),
                            		0l,
                            		"",
                            		"",
                            		"",
                            		
                            		0l,
                            		"",
                            		"",
                            		"",
                            		0,
                            		0f, false));
                        }
                    }                  
		}
            }catch(Exception e){
                System.out.println("Exception Caught in Group.setGroupLeader() " + e);
                e.printStackTrace();
            }
	}
        
        public void disbandGroup(){
            try{
                for(int i = 0; i < vGroupMembers.size(); i++)
                {
                    SOEObject gM = vGroupMembers.get(i);
                    if(gM instanceof Player)
                    {
                        Player pGM = (Player)gM;
                        pGM.getClient().insertPacket(PacketFactory.buildSceneDestroyObject(this));
                        pGM.setGroupObject(null);
                        pGM.getClient().insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO,(byte)6, (short)1, (short)6,pGM, pGM.getGroupID()));                
                        pGM.getClient().insertPacket(PacketFactory.buildChatSystemMessage(
                        		"group",
                        		"disbanded",
                        		0l,
                        		"",
                        		"",
                        		"",
                        		0l,
                        		"",
                        		"",
                        		"",
                        		0l,
                        		"",
                        		"",
                        		"",
                        		0,
                        		0f, false));                        
                    }
                }
            }catch(Exception e){
                System.out.println("Exception Caught in Grou.disbandGroup() + e");
                e.printStackTrace();
            }
        }
        
        public long getGroupLeaderID(){
            return groupLeader.getID();
        }
        
        public Player getGroupLeader(){
            return groupLeader;
        }
        
        public int getGroupUpdateCounter(boolean bIncrement) {
        	if (bIncrement) {
        		iGroupUpdateCounter++;
        	}
            return iGroupUpdateCounter;
        }

        protected short getMemberIndex(SOEObject o){

            for(short i = 0; i < vGroupMembers.size(); i++)
            {
                if(vGroupMembers.get(i).equals(o))
                {
                    return i;
                }
            }
            return -1; //were about to have a crash possibly lol
        }   
        
        protected void sendPacketToGroup(byte [] packet){
            try{
                for(short i = 0; i < vGroupMembers.size(); i++)
                {
                    if(vGroupMembers.get(i) instanceof Player)
                    {
                        Player p = (Player)vGroupMembers.get(i);
                        p.getClient().insertPacket(packet);
                    }
                }
            }catch(Exception e){
                System.out.println("Exception caught in Group.sendPacketToGroup() " + e);
                e.printStackTrace();
            }
        }
        
        protected int getNextMemberIndex(){
            return vGroupMembers.size();
        }



        private void electNewLeader(){
            try{        
                long lLongest = 0;
                Player oldest = null;
                SOEObject currentLeader = groupLeader;
                for(int i = 0; i < this.vGroupMembers.size(); i++)
                {
                    SOEObject o = this.vGroupMembers.get(i);
                    if(o instanceof Player)
                    {
                        Player p = (Player)o;
                        long lTime = p.getGroupTime();
                        if(lTime >= lLongest && !currentLeader.equals(p))
                        {
                            lLongest = lTime;
                            oldest = p;
                        }
                    }
                }
                this.setGroupLeader(oldest);                 
               
            }catch(Exception e){
                System.out.println("Exception caught in Group.electNewLeader() " + e);
                e.printStackTrace();                
            }
        }
        
        protected Vector<Player> getPlayerObjectsInGroup(){
            Vector<Player> retVal = new Vector<Player>();
            for(int i = 0; i < vGroupMembers.size(); i++)
            {
                if(vGroupMembers.get(i) instanceof Player)
                {
                    retVal.add((Player)vGroupMembers.get(i));
                }
            }            
            return retVal;
        }
        
        protected void reArrangeGroup(){
            try{
                for(int i = 0; i < vGroupMembers.size(); i++)
                {
                    if(vGroupMembers.get(i) instanceof Player)
                    {
                        Player p = (Player)vGroupMembers.get(i);
                        p.getClient().insertPacket(PacketFactory.buildDeltasMessageGRUP(Constants.BASELINES_GRUP, (byte)6, (short)1, (short)1,Constants.Group_ClearList, this, null, iGroupUpdateCounter, p, (short)-1));                        
                        for(int r = 0; r < vGroupMembers.size(); r++)
                        {
                            if(vGroupMembers.get(r) instanceof Player)
                            {                                
                                p.getClient().insertPacket(PacketFactory.buildDeltasMessageGRUP(Constants.BASELINES_GRUP, (byte)6, (short)1, (short)1,Constants.Group_AddMember, this, (Player)vGroupMembers.get(r), iGroupUpdateCounter, p, (short)-1));                        
                            }
                        }
                    }                    
                }

            }catch(Exception e){
                System.out.println("Excption Caught in Group.reArrangeGroup " + e);
                e.printStackTrace();
            }            
        }
}
