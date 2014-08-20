import java.util.Vector;

/**
 * The NPC update thread is responsible for updating the movement, status, combat and dialog states for all NPCs across
 * the game.  Only one NPC update thread per Zone Server (or per planet, in the Combined server) should be active at any given time.
 * @author Darryl
 *
 */
public class NPCUpdateThread implements Runnable {
	private Vector<NPC> vAllNPCs;
	private ZoneServer server;
	private Thread myThread;
	private long lLastUpdateTimeMS;
	private long lCurrentUpdateTimeMS;
	private long lDeltaUpdateTimeMS;
	private int iClusterID;
	//private NPCSpawnManager manager;
	private Vector<DynamicLairSpawn> vLairSpawns;
	
	/**
	 * Construct a new NPC Update Thread for the given Zone Server.
	 * @param server -- The Zone Server which we will be maining NPC's on.
	 */
	public NPCUpdateThread(ZoneServer server, int clusterID) {
		this.server = server;
		iClusterID = clusterID;
		vAllNPCs = new Vector<NPC>();
		lLastUpdateTimeMS = System.currentTimeMillis();
		lCurrentUpdateTimeMS = lLastUpdateTimeMS;
		lDeltaUpdateTimeMS = 0;
		vLairSpawns = new Vector<DynamicLairSpawn>();
	}
	
	public void startThread() {
		myThread = new Thread(this);
		myThread.setName(Constants.PlanetNames[iClusterID] + " NPC update thread");
		myThread.start();
	}
	/**
	 * Add a new, single, random Non-Player Character to the server.
	 */
	public void addRandomNPC() {
		NPC npc = new NPC();
		// Set up a bunch of stuff about the NPC
		npc.setServer(server);
		vAllNPCs.add(npc);
	}

	/**
	 * Remove an NPC from the list of Random NPC spawns, and despawns it from all Players in range.
	 * Most likely, this is called because the given NPC is dead and needs to be despawned.
	 * @param npc -- The NPC to be removed.
	 */
	public void removeRandomNPC(NPC npc) {
		vAllNPCs.remove(npc);
		Vector<Player> vPlayersAroundNPC = server.getPlayersAroundNPC(npc);
		for (int i = 0; i < vPlayersAroundNPC.size(); i++) {
			Player player = vPlayersAroundNPC.elementAt(i);
			try {
				player.despawnItem(npc);
			} catch (Exception e) {
				System.out.println("Unable to despawn NPC with ID " + npc.getID() + " to player " + player.getFullName() + ": " + e.toString());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * The main loop of the NPC update thread.
	 */
	public void run() {
		while (myThread != null) {
			try {
				lCurrentUpdateTimeMS = System.currentTimeMillis(); 
				lDeltaUpdateTimeMS = lCurrentUpdateTimeMS - lLastUpdateTimeMS;
				lLastUpdateTimeMS = lCurrentUpdateTimeMS;
				try {
					synchronized(this) {
						Thread.yield();
						wait(100);
					}
				} catch (Exception e) {
					System.out.println("NPCUpdateThread: Error waiting -- " + e.toString());
					e.printStackTrace();
				}
				/*if (iClusterID == Constants.TATOOINE) {
					System.out.println("NPC Update thread for Tatooine Loop time: " + lDeltaUpdateTimeMS);
				}*/
				if (vLairSpawns != null) {
					if (!vLairSpawns.isEmpty()) {
						// This handles our lair spawns.
						for (int i = 0; i < vLairSpawns.size(); i++) {
							DynamicLairSpawn lairSpawn = vLairSpawns.elementAt(i);
							lairSpawn.update(lDeltaUpdateTimeMS);
						}
					}
				}
				
				// This handles Player-made NPCs.

				for (int i =0; i < vAllNPCs.size(); i++) {
					try {
						NPC npc = vAllNPCs.elementAt(i);
						if (npc.getServer() == null) {
							npc.setServer(server);
						}
						// TODO -- This will be / is handled by the Grid system.
						//Vector<Player> vObjectsBeforeMovement = server.getPlayersAroundNPC(npc);
                        if(npc.isTerminal() && !npc.IsSkillTrainer())
                        {
                           
                        }
                        else
                        {
                            npc.update(lDeltaUpdateTimeMS);
                        }
                        
                        
						/*Vector<Player> vObjectsAfterMovement = server.getPlayersAroundNPC(npc);
						Vector<Player> vStillSpawned = new Vector<Player>();
						
						for (int j = 0; j < vObjectsBeforeMovement.size(); j++) {
							Player p = vObjectsBeforeMovement.elementAt(j);
							if (!vObjectsAfterMovement.contains(p)) {
								p.despawnItem(npc);
							}
						}
						for (int j = 0; j < vObjectsAfterMovement.size(); j++) {
							Player p = vObjectsAfterMovement.elementAt(j);
							if (!vObjectsBeforeMovement.contains(p)) {
								p.spawnItem(npc);
								
							} else {
								vStillSpawned.add(p);
							}
						}
						if (npc.getIsPositionChanged()) {
							
						}*/
					} catch (Exception e) {
						System.out.println("Error updating NPCs: " + e.toString());
						e.printStackTrace();
					}
				}
			} catch (OutOfMemoryError ee) {
				Runtime.getRuntime().gc();
				System.out.println("NPC Update thread for planet ID " + iClusterID + " reports out of memory.  Waiting 5 seconds for more heap space.");
				synchronized(this) {
					try {
						Thread.yield();
						wait(5000);
					} catch (Exception e) {
						// D'oh!
					}
				}
			} catch (Exception e) {
				System.out.println("Error in NPC Update Thread: " + e.toString());
				e.printStackTrace();
			}
		}
	}
	
	protected void addNPC(NPC n) {
		vAllNPCs.add(n);
	}
	
	protected synchronized void removeNPC(NPC n) {
		vAllNPCs.remove(n);
	}
	
	//protected void setSpawnManager(NPCSpawnManager manager) {
	//	this.manager = manager;
	//}
	
	protected void addDynamicLairSpawn(DynamicLairSpawn spawn) {
		vLairSpawns.add(spawn);
	}
	
	protected void removeLairSpawn(DynamicLairSpawn spawn) {
		vLairSpawns.removeElement(spawn);
	}
}
