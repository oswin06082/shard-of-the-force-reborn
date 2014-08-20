import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
/**
 * The Structure class represents Buildings in the SWG game.
 * @author Darryl
 *
 */
public class Structure extends TangibleItem {
	public final static long serialVersionUID = 1;
	private String sIFFName;
	private Hashtable<Long, Cell> cellsInBuilding;
    private long deedID;        
    private int iCellCount;
    private Terminal structureSign;
    private Terminal adminTerminal;
    private Terminal guildTerminal;
    private TangibleItem structureBase;
    private int iMaintenancePool;
    private int iPowerPool;
    private int iMaintenanceRate;
    private int iPowerRate;
    private long lNextMaintenanceTick;
    private int iFacingDirection;
    private byte structureType;
    private TangibleItem noBuildZone;
    //private int currentCondition;
    private long structureOwnerID;
    
    private Vector<Long> vAdminList;
    private Vector<Long> vEnterList;
    private Vector<Long> vBanList;
    private Vector<Long> vHopperAdminList;
    
    //private Vector<Long> vHopperList;
    private short structureStatus;
    //private boolean bUpdateThreadIsUsing;
    private int iStructureMaxItemsCapacity;
    private long lCreationDate;
    //private long lLastMaintenanceTick;
    private Waypoint structureWaypoint;
    private boolean bConstructionEmailSent;
    private int iDestructionCode;
    private boolean usespower = false;
    private boolean installationActive;
    private String sStructureName;
    private boolean structurenamechanged;
    private int iOutputHopperSize;
    private int iInputHopperSize;
    private byte installationType;
    private int lotsize;
    private int iSignTemplateIndex;
    private boolean isGuild;
    private long lGuildObjectID;
    private long lGuildTickTime;
    //private boolean isStructureMarker;
    //private long lNextMovementUpdate; //LOL YES STRUCTURES MOVE BELIEVE ME !!!! PF
    private Vector<Terminal> vElevatorTerminals;

    private transient long constructionMarkerID;
    private transient Structure constructionMarkerObject;
    private transient ZoneServer server;
    private int iAnimationBitmask;
	/**
	 * Construct a new Structure.
	 */
	public Structure() {
		super();
		sIFFName = "";
		cellsInBuilding = new Hashtable<Long, Cell>();
        //world structures need admin lists too for restricting access to them and not to cause null pointers hehe.
        vAdminList = new Vector<Long>();
        vEnterList = new Vector<Long>();
        vBanList = new Vector<Long>();
        structureStatus = Constants.STRUCTURE_PERMISSIONS_PUBLIC;
	}

	/**
	 * Construct a new Structure, with the given IFF file name.
	 * @param sName -- The iff filename of the structure.
	 */
	public Structure(String sName, long deedID) {
		super();		
		cellsInBuilding = new Hashtable<Long, Cell>();
        this.deedID = deedID;                
        sIFFName = sName;
        //world structures need admin lists too for restricting access to them and not to cause null pointers hehe.
        vAdminList = new Vector<Long>();
        vEnterList = new Vector<Long>();
        vBanList = new Vector<Long>();
        structureStatus = Constants.STRUCTURE_PERMISSIONS_PUBLIC;
	}
	
        public Structure(int iDeedTemplateID, long deedID,Deed d, float x, float y, float z, int planetid, float oW, float oS, int iFacingDirection, String sOwnerName,long lStructureOwnerID, ZoneServer server) throws IOException {
                super();
                this.server = server;
                cellsInBuilding = new Hashtable<Long, Cell>();
                this.deedID = deedID;
                DeedTemplate dT = DatabaseInterface.getDeedTemplateByID(d.getDeedTemplateID());
                
                iMaintenancePool = d.getISurplusMaintenance();
                iPowerPool = d.getISurplusPower();
                iMaintenanceRate = dT.getMaint_per_hour();
                iPowerRate = d.getIEnergyMaintenanceRate();//need to get this from the template
                this.iFacingDirection = iFacingDirection;
                this.structureOwnerID = lStructureOwnerID;
                //vHopperList = new Vector<Long>();
                vAdminList = new Vector<Long>();
                vEnterList = new Vector<Long>();
                vBanList = new Vector<Long>();
                vHopperAdminList = new Vector<Long>();
                structureStatus = Constants.STRUCTURE_PERMISSIONS_PRIVATE;
                //bUpdateThreadIsUsing = false;
                iStructureMaxItemsCapacity = dT.getStructure_items_capacity();
                lCreationDate = System.currentTimeMillis();
                bConstructionEmailSent = false;
                usespower = dT.usesPower();
                lotsize = dT.getLotsused();
                iSignTemplateIndex = 0;
                lGuildObjectID = 0;
                lGuildTickTime = 0;
                //isStructureMarker = false;
                if(dT!=null)
                {
                    ItemTemplate iT = d.getServer().getTemplateData(dT.getObject_iff_template_id());
                    if(iT!=null)
                    {                       
                        sIFFName = iT.getIFFFileName();
                        System.out.println("Building IFF Set at: " + sIFFName + " template ID: " + iT.getTemplateID() + " CRC:" + iT.getCRC());
                        if(sIFFName.contains("building"))
                        {
                            this.structureType = Constants.STRUCTURE_TYPE_BUILDING;
                        }
                        
                        this.setIsBuilding(true);                       
                        this.setCellID(0);
                        this.setDeedID(deedID);
                        this.setID(d.getServer().getNextObjectID());                        
                        this.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);                                               
                        this.setTemplateID(iT.getTemplateID());
                        /**
                         * based on template id we will set the resource type if this structure is a harvester                          
                         */
                        
                        this.setX(x);
                        this.setY(y);
                        this.setZ(z);
                        this.setPlanetID(planetid);  
                        this.setOrientationN(0);
                        this.setOrientationS(oS);
                        this.setOrientationE(0);
                        this.setOrientationW(oW);
                        setMaxCondition(5000, false);                        

                        this.iCellCount = dT.getNumber_cells();
                        if(this.iCellCount >= 1)
                        {
                            iT = d.getServer().getTemplateData(Constants.CELL_OBJECT_TEMPLATE_ID);
                            for(int i = 1; i <= this.iCellCount; i++)
                            {
                                System.out.println("Creating Cell " + i + " For Structure ID: " + this.getID() + " CellID:" + (this.getID() + i + 1) );

                                Cell c = new Cell(this,i,true);
                                c.setPlanetID(planetid);
                                c.setOrientationW(1);
                                c.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
                                c.setTemplateID(iT.getTemplateID());
                                c.setCRC(iT.getCRC());
                                c.setID(d.getServer().getNextObjectID());
                                d.getServer().addObjectToAllObjects(c, true, false);
                                //d.getServer().addObjectIDToAllUsedID(c.getID());
                                cellsInBuilding.put(c.getID(), c);
                            }
                        }
                    }
                    this.structureSign = null;
                    if(dT.isSign_required())
                    {
                        iT = d.getServer().getTemplateData(dT.getObject_sign_template_id()[iSignTemplateIndex]);//default sign template index is 0 for all structures
                        this.structureSign = new Terminal();
                        structureSign.setServer(server);
                        this.structureSign.setTerminalType(Constants.TERMINAL_TYPES_STRUCTURE_SIGN);
                        this.structureSign.setCellID(0);                        
                        this.structureSign.setID(d.getServer().getNextObjectID());
                        this.structureSign.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
                        this.structureSign.setCREO3Bitmask(Constants.BITMASK_CREO3_TERMINAL);
                        this.structureSign.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());                      
                        this.structureSign.setTemplateID(iT.getTemplateID());
                        structureSign.setStructure(this);
                        String sNewStructureName = "";                        
                        switch(this.getTemplateID())
                        {
                            case 15436:// 'object/installation/generators/shared_power_generator_fusion_style_1.iff'
                            case 15437:// 'object/installation/generators/shared_power_generator_photo_bio_style_1.iff'
                            case 15438:// 'object/installation/generators/shared_power_generator_solar_style_1.iff'
                            case 15439:// 'object/installation/generators/shared_power_generator_wind_style_1.iff'
                            case 15442:// 'object/installation/mining_organic/shared_mining_organic_flora_farm_heavy.iff'
                            case 15443:// 'object/installation/mining_organic/shared_mining_organic_flora_farm_medium.iff'
                            case 15441:// 'object/installation/mining_organic/shared_mining_organic_flora_farm.iff'
                            case 15444:// 'object/installation/mining_gas/shared_mining_gas_harvester_style_1.iff'
                            case 15445:// 'object/installation/mining_gas/shared_mining_gas_harvester_style_2.iff'
                            case 15446:// 'object/installation/mining_gas/shared_mining_gas_harvester_style_3.iff'
                            case 15447:// 'object/installation/mining_liquid/shared_mining_liquid_harvester_style_1.iff'
                            case 15448:// 'object/installation/mining_liquid/shared_mining_liquid_harvester_style_2.iff'
                            case 15449:// 'object/installation/mining_liquid/shared_mining_liquid_harvester_style_3.iff'
                            case 15450:// 'object/installation/mining_liquid/shared_mining_liquid_moisture_harvester.iff'
                            case 15452:// 'object/installation/mining_liquid/shared_mining_liquid_moisture_harvester_medium.iff'
                            case 15451:// 'object/installation/mining_liquid/shared_mining_liquid_moisture_harvester_heavy.iff'
                            case 15428:// 'object/installation/mining_ore/shared_mining_ore_harvester_heavy.iff'
                            case 15429:// 'object/installation/mining_ore/shared_mining_ore_harvester_style_1.iff'
                            case 15430:// 'object/installation/mining_ore/shared_mining_ore_harvester_style_2.iff'
                            {
                                sNewStructureName = "@" + this.getSTFFileName() + ":" + this.getSTFFileIdentifier();
                                break;
                            }
                            case 469:
                            case 468:
                            case 470:
                            {
                                sNewStructureName = sOwnerName + "'s Emporium";
                                break;
                            }
                            default:
                            {
                                sNewStructureName = sOwnerName + "'s House";
                            }
                        }
                        this.structureSign.setFirstName(sNewStructureName);                        
                        this.structureSign.setX(this.getX() + dT.getSignX()[this.iFacingDirection]);
                        this.structureSign.setY(this.getY() + dT.getSignY()[this.iFacingDirection]);                        
                        this.structureSign.setZ(this.getZ() + dT.getSignZ()[this.iFacingDirection]);
                        this.structureSign.setPlanetID(planetid);  
                        this.structureSign.setOrientationN(dT.getSign_oI()[this.iFacingDirection]);
                        this.structureSign.setOrientationS(dT.getSign_oJ()[this.iFacingDirection]);
                        this.structureSign.setOrientationE(dT.getSign_oK()[this.iFacingDirection]);
                        this.structureSign.setOrientationW(dT.getSign_oW()[this.iFacingDirection]);
                        this.structureSign.setParentID(this.getID());                        
                    }
                    this.structureBase = null;
                    if(dT.isBase_required())
                    {
                        iT = d.getServer().getTemplateData(dT.getObject_base_template_id());
                        this.structureBase = new TangibleItem();                                               
                        this.structureBase.setCellID(0);                        
                        this.structureBase.setID(d.getServer().getNextObjectID());
                        this.structureBase.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
                        this.structureBase.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());                     
                        this.structureBase.setTemplateID(iT.getTemplateID());
                        this.structureBase.setX(x);
                        this.structureBase.setY(y);
                        this.structureBase.setZ(z + 1.0f);
                        this.structureBase.setPlanetID(planetid);
                        
                        if(this.iFacingDirection == 0 || this.iFacingDirection == 2 )
                        {
                            this.structureBase.setOrientationN(this.getOrientationN());
                            this.structureBase.setOrientationS(this.getOrientationS());
                            this.structureBase.setOrientationE(this.getOrientationE());
                            this.structureBase.setOrientationW(this.getOrientationW());
                        }
                        else if(this.iFacingDirection == 1 )
                        {                            
                            this.structureBase.setOrientationN(this.getOrientationN());
                            this.structureBase.setOrientationS(0.7f);
                            this.structureBase.setOrientationE(this.getOrientationE());
                            this.structureBase.setOrientationW(0.7f);
                        }
                        else if(this.iFacingDirection == 3)
                        {
                            this.structureBase.setOrientationN(this.getOrientationN());
                            this.structureBase.setOrientationS(0.7f);
                            this.structureBase.setOrientationE(this.getOrientationE());
                            this.structureBase.setOrientationW(-0.7f);
                        }                        
                        this.structureBase.setParentID(this.getID());                        
                    }
                    this.adminTerminal = null;
                    if(dT.isTerminal_required())
                    {
                        iT = d.getServer().getTemplateData(dT.getObject_admin_terminal_template_id());                       
                        this.adminTerminal = new Terminal();
                        adminTerminal.setServer(server);
                        this.adminTerminal.setTerminalType(Constants.TERMINAL_TYPES_STRUCTURE_MAINTENANCE);
                        long cellNum = dT.getTerminal_cell_id();
                        Enumeration<Cell> vCellEnum = cellsInBuilding.elements();
                        while (vCellEnum.hasMoreElements()) {
                        	Cell cell = vCellEnum.nextElement();
                        	if (cell.getCellNum() == cellNum) {
                        		adminTerminal.setCellID(cell.getID());
                        		cell.addCellObject(adminTerminal);
                        		break;
                        	}
                        }
                        
                        //this.adminTerminal.setCellID(this.getID() + dT.getTerminal_cell_id());                        
                        this.adminTerminal.setID(d.getServer().getNextObjectID());
                        this.adminTerminal.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
                        this.adminTerminal.setCREO3Bitmask(Constants.BITMASK_CREO3_TERMINAL);
                        this.adminTerminal.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());                   
                        this.adminTerminal.setTemplateID(iT.getTemplateID());
                        this.adminTerminal.setX(this.getX() + dT.getTerminalX());
                        this.adminTerminal.setY(this.getY() + dT.getTerminalY());
                        this.adminTerminal.setZ(this.getZ() + dT.getTerminalX());
                        this.adminTerminal.setCellX(dT.getTerminalX());
                        this.adminTerminal.setCellY(dT.getTerminalY());
                        this.adminTerminal.setCellZ(dT.getTerminalZ());
                        this.adminTerminal.setPlanetID(planetid);  
                        this.adminTerminal.setOrientationN(dT.getTerminal_oI());
                        this.adminTerminal.setOrientationS(dT.getTerminal_oJ());
                        this.adminTerminal.setOrientationE(dT.getTerminal_oK());
                        this.adminTerminal.setOrientationW(dT.getTerminal_oW());   
                        this.adminTerminal.setParentID(this.getID());
                        adminTerminal.setStructure(this);
                    }
                    this.guildTerminal = null;
                    isGuild = false;
                    if(dT.isIsGuild())
                    {
                        isGuild = dT.isIsGuild();
                        iT = d.getServer().getTemplateData(dT.getGuild_terminal_template_id());                       
                        this.guildTerminal = new Terminal();
                        guildTerminal.setServer(server);
                        this.guildTerminal.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_GUILD);   
                        long cellNum = dT.getTerminal_cell_id();
                        Enumeration<Cell> vCellEnum = cellsInBuilding.elements();
                        while (vCellEnum.hasMoreElements()) {
                        	Cell cell = vCellEnum.nextElement();
                        	if (cell.getCellNum() == cellNum) {
                        		guildTerminal.setCellID(cell.getID());
                        		cell.addCellObject(guildTerminal);
                        		break;
                        	}
                        }

                        //this.guildTerminal.setCellID(this.getID() + dT.getTerminal_cell_id());                        
                        this.guildTerminal.setID(d.getServer().getNextObjectID());
                        this.guildTerminal.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
                        this.guildTerminal.setCREO3Bitmask(Constants.BITMASK_CREO3_TERMINAL);
                        this.guildTerminal.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());                     
                        this.guildTerminal.setTemplateID(iT.getTemplateID());
                        this.guildTerminal.setX(this.getX() + dT.getGuild_terminal_x());
                        this.guildTerminal.setY(this.getY() + dT.getGuild_terminal_y());
                        this.guildTerminal.setZ(this.getZ() + dT.getGuild_terminal_z());
                        this.guildTerminal.setCellX(dT.getGuild_terminal_x());
                        this.guildTerminal.setCellY(dT.getGuild_terminal_y());
                        this.guildTerminal.setCellZ(dT.getGuild_terminal_z());
                        this.guildTerminal.setPlanetID(planetid);  
                        this.guildTerminal.setOrientationN(dT.getGuild_terminal_oI());
                        this.guildTerminal.setOrientationS(dT.getGuild_terminal_oJ());
                        this.guildTerminal.setOrientationE(dT.getGuild_terminal_oK());
                        this.guildTerminal.setOrientationW(dT.getGuild_terminal_oW());   
                        this.guildTerminal.setParentID(this.getID());
                        guildTerminal.setStructure(this);
                        Player owner = server.getPlayer(this.getStructureOwnerID());
                        //set the guild id to be the same as the  player in case they packed up the guild hall and moved it
                        //it will be the same guild.
                        this.lGuildObjectID = owner.getGuildID();
                        //-------------------------------
                        
                    }
                    //Construction Marker;
                    this.constructionMarkerObject = null;
                    if(dT.getConstruction_marker_template_id()!=0)
                    {
                        iT = d.getServer().getTemplateData(dT.getConstruction_marker_template_id());
                        this.constructionMarkerObject = new Structure();    
                        this.constructionMarkerObject.setStructureType(Constants.STRUCTURE_TYPE_INSTALLATION);                       
                        this.constructionMarkerObject.setCellID(0);                        
                        this.constructionMarkerObject.setID(d.getServer().getNextObjectID());
                        this.constructionMarkerObject.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);     
                        this.constructionMarkerObject.setTemplateID(iT.getTemplateID());                        
                        this.constructionMarkerObject.setX(this.getX());
                        this.constructionMarkerObject.setY(this.getY());
                        this.constructionMarkerObject.setZ(this.getZ());
                        this.constructionMarkerObject.setPlanetID(planetid);  
                        //Bloody Structure Markers GO BACKWARDS!!!!!!
                        if(this.iFacingDirection == 0 || this.iFacingDirection == 2)
                        {
                            this.constructionMarkerObject.setOrientationN(this.getOrientationE());
                            this.constructionMarkerObject.setOrientationS(this.getOrientationW());
                            this.constructionMarkerObject.setOrientationE(this.getOrientationN());
                            this.constructionMarkerObject.setOrientationW(this.getOrientationS());
                        }
                        else if(this.iFacingDirection == 1 )
                        {
                            this.constructionMarkerObject.setOrientationN(this.getOrientationN());
                            this.constructionMarkerObject.setOrientationS(0.7f);
                            this.constructionMarkerObject.setOrientationE(this.getOrientationE());
                            this.constructionMarkerObject.setOrientationW(-0.7f);
                        }
                        else if(this.iFacingDirection == 3)
                        {
                            this.constructionMarkerObject.setOrientationN(this.getOrientationN());
                            this.constructionMarkerObject.setOrientationS(0.7f);
                            this.constructionMarkerObject.setOrientationE(this.getOrientationE());
                            this.constructionMarkerObject.setOrientationW(0.7f);
                        }
                        this.constructionMarkerObject.setParentID(this.getID());
                        this.setConstructionMarkerID(this.constructionMarkerObject.getID());
                        //isStructureMarker = true;                       
                    }
                    //add elevator terminals. We do it separate from the guild because there are structures that may need elevators
                    //that are not guild halls and not all guild halls use elevators.
                    if(dT.getIElevatorTerminalCount()!=0)
                    {
                        vElevatorTerminals = new Vector<Terminal>();
                        for(int i = 0;i < dT.getIElevatorTerminalCount(); i++)
                        {
                            Terminal et = new Terminal();
                            et.setServer(server);
                            et.setID(server.getNextObjectID());
                            et.setCRC(dT.getIElevatorTerminalCRC()[i]);
                            switch(et.getTemplateID())
                            {
                                case 14063: //up
                                { et.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_ELEVATOR_UP); break;}
                                case 14062: //down
                                { et.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_ELEVATOR_DOWN); break;}
                                case 14061: //up down
                                { et.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_ELEVATOR_UP_DOWN); break;}
                            }
                            et.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());
                            long cellNum = dT.getTerminal_cell_id();
                            Enumeration<Cell> vCellEnum = cellsInBuilding.elements();
                            while (vCellEnum.hasMoreElements()) {
                            	Cell cell = vCellEnum.nextElement();
                            	if (cell.getCellNum() == cellNum) {
                            		et.setCellID(cell.getID());
                            		cell.addCellObject(et);
                            		break;
                            	}
                            }

                            et.setCellID(this.getID() + dT.getIElevatorTerminalCellID()[i]);
                            et.setCellX(dT.getFElevatorTerminalX()[i]);
                            et.setCellY(dT.getFElevatorTerminalY()[i]);
                            et.setCellZ(dT.getFElevatorTerminalZ()[i]);
                            et.setX(this.getX() + dT.getFElevatorTerminalX()[i]);
                            et.setY(this.getY() + dT.getFElevatorTerminalY()[i]);
                            et.setZ(this.getZ() + dT.getFElevatorTerminalZ()[i]);
                            et.setParentID(this.getID());
                            et.setOrientationN(dT.getFElevatorTerminaloI()[i]);
                            et.setOrientationS(dT.getFElevatorTerminaloJ()[i]);
                            et.setOrientationE(dT.getFElevatorTerminaloK()[i]);
                            et.setOrientationW(dT.getFElevatorTerminaloW()[i]);
                            et.setPlanetID(planetid);
                            et.setStructure(this);
                            et.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
                            vElevatorTerminals.add(et);                            
                        }
                    }
                    
                }
                this.sStructureName = null;
                structureWaypoint = new Waypoint();                        
                structureWaypoint.setX(x);
                structureWaypoint.setY(y);
                structureWaypoint.setZ(z);
                structureWaypoint.setIsActivated(true);
                structureWaypoint.setName("@" + this.getSTFFileName() + ":" + this.getSTFFileIdentifier());
                structureWaypoint.setWaypointType(Constants.WAYPOINT_TYPE_PLAYER_CREATED);
                structureWaypoint.setPlanetCRC(Constants.PlanetCRCForWaypoints[planetid]);
                structureWaypoint.setID(d.getServer().getNextObjectID());
                d.setLCurrentStructureID(this.getID());                
                this.addToAdminList(this.getStructureOwnerID());   
                
                this.lNextMaintenanceTick = 1000*60*60;  //1 hour from now
                //lLastMaintenanceTick = System.currentTimeMillis();
	}
	/**
	 * Gets the IFF filename.
	 * @return The IFF filename.
	 */
	public String getIFFName() {
		return sIFFName;
	}
	
	/**
	 * Adds a Cell to this building.  Each structure is divided into a group of (roughly cubical) cells.
	 * @param c -- The Cell.
	 */
	public void addCell(Cell c) {
		cellsInBuilding.put(c.getID(), c);
	}
	
	/**
	 * Gets a Cell by it's object ID.
	 * @param id -- The Object ID of the Cell we are looking for.
	 * @return The Cell, or null if the Cell with the given ID is not in this Structure.
	 */
	public Cell getCell(long id) {
		return cellsInBuilding.get(id);
	}
	
	/**
	 * Treats the hash table like an array and gets the value of the cell that fits
	 *  the position in said hashtable
	 *  @param index -- The index in the array needed
	 *  @return -- The cell or null if the index is invalid.
	 */
	
	public Cell getCellByIndex(int index)
	{
		int size = cellsInBuilding.keySet().size();
		if(index < size && index >= 0)
		{
			return cellsInBuilding.get(cellsInBuilding.keySet().toArray(new Long[cellsInBuilding.keySet().size()])[index]);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Gets the number of Cells this Structure currently contains.
	 * @return The cell count.
	 */
	public int getCurrentCellCount() {
		return cellsInBuilding.size();
	}

        public long getDeedID() {
            return deedID;
        }

        public void setDeedID(long deedID) {
            this.deedID = deedID;
        }
        
        
        public void update(long lElapsedTime, ZoneServer server){
            //System.out.println("Structure.Update() Next Maint Tick in: " + (double)(this.lNextMaintenanceTick / 1000 / 60 ) + " Minutes" );
            if(this.server == null)
            {
                this.server = server;
            }
            try{
                if(this.lNextMaintenanceTick < 0)
                {
                    //System.out.println("Structure.Update() Maintenance Tick '" + this.getStructureName() + "' ID:" + this.getID());
                    this.lNextMaintenanceTick = 1000*60*60; // 1 hour
                    this.iMaintenancePool -= this.iMaintenanceRate;
                    //lLastMaintenanceTick = System.currentTimeMillis();
                    if(iMaintenancePool < 0)
                    {
                        int currentDamage = getConditionDamage();
                    	currentDamage += 10;
                    	byte[] update = setConditionDamage(currentDamage, true);
                    	
                    }
                    //----------------------------
                    Player owner = (Player)server.getObjectFromAllObjects(this.structureOwnerID);
                    server.getGUI().getDB().updatePlayer(owner, false,false);
                    int currentCondition = getConditionDamage();
                    //lLastMaintenanceTick = System.currentTimeMillis();  
                    if(currentCondition > 0)
                    {                
                    	// TODO:  STF files!
                         String Message = this.getStructureName() + " is currently at " + this.getEmailConditionString() + " condition.  It will be destroyed if it reaches 0.  If you wish to keep this structure, you should immediately add maintenance. \r\n";
                         Message += "Structure Coordinates:\r\n";
                         Message += "X:" + this.getX() + "\r\n";
                         Message += "Y:" + this.getY() + "\r\n";
                         Message += "Planet: " + Constants.PlanetNames[this.getPlanetID()] + "\r\n";                         
                         Vector<Waypoint> WL = new Vector<Waypoint>();                 
                         SWGEmail E = new SWGEmail(-1,Constants.SERVER_STRUCTURE_MANAGER_OBJECT_ID,this.structureOwnerID,"Structure Damaged!",Message,WL,false);                 
                         server.queueEmailNewClientMessage(E);        
                    }
                    //--------------------------------
                    if(usespower && this.installationActive)
                    {                    
                        if(this.iPowerPool >= 1)
                        {
                            this.iPowerPool -= this.iPowerRate;
                        }
                        else
                        {
                            //installationActive = false;
                            this.deactivateInstallation();
                            String Message = "@" + this.getSTFFileIdentifier() + ":" + this.getSTFFileName() + " is currently Out of Power \r\n";
                            Message += "Structure Coordinates:\r\n";
                            Message += "X:" + this.getX() + "\r\n";
                            Message += "Y:" + this.getY() + "\r\n";
                            Message += "Planet: " + Constants.PlanetNames[this.getPlanetID()] + "\r\n";                         
                            Vector<Waypoint> WL = new Vector<Waypoint>();                 
                            SWGEmail E = new SWGEmail(-1,Constants.SERVER_STRUCTURE_MANAGER_OBJECT_ID,this.structureOwnerID,"Structure Out of Power",Message,WL,false);                 
                            server.queueEmailNewClientMessage(E); 
                        }
                    }
                    
                    //----------------------------
                    //Update Guild and Player to the DB
                    server.getGUI().getDB().updatePlayer(owner,false,false);                            
                }
                else
                {
                    this.lNextMaintenanceTick -= lElapsedTime;
                }

                if(usespower && this.installationActive)
                {
                    if(this.iPowerPool <= 0)
                    {
                        lNextMaintenanceTick = 0;
                    }
                }
                //-------------------------------------
                if(this.getStructureWaypoint() != null && !bConstructionEmailSent)
                {
                	
                    bConstructionEmailSent = true;
                    System.out.println("Sending Construction Email and Waypoint.");
                    Player player = (Player)server.getObjectFromAllObjects(this.structureOwnerID);                    
                    if (player == null) {
                    	System.out.println("Error:  Structure at " + getX() + ", " + getY() + " " + Constants.PlanetNames[getPlanetID()] + " has no owner.");
                    	Deed d = (Deed)server.getObjectFromAllObjects(getDeedID());
                    	if (d != null) {
                    		d.redeedStructure(this, server);
                    	} else {
                    		server.removeObjectFromAllObjects(this, true);
                    	}
                    	server.removePlayerStructureFromAllStructures(getID());
                    	return;
                    	
                    }
                    server.addObjectToAllObjects(this.getStructureWaypoint(), false,false);
                    
                    player.addWaypoint(this.getStructureWaypoint(),true);
                    String Message = "Construction of your Structure is now complete. You have " + player.getFreeLots() + " lots remaining. ";
                    Vector<Waypoint> WL = new Vector<Waypoint>();
                    WL.add(this.structureWaypoint);
                    SWGEmail E = new SWGEmail(-1,Constants.SERVER_STRUCTURE_MANAGER_OBJECT_ID,player.getID(),"Construction Complete",Message,WL,false);
                    E.setTransactionRequester(player.getClient());
                    server.queueEmailNewClientMessage(E);                        

                }
                else
                {
                    //System.out.println("Not Sending wp or email!!!!! EML Sent: " + bConstructionEmailSent);
                    if(this.getStructureWaypoint() == null)
                    {
                        System.out.println("Structure Waypoint is null");
                    }
                }
                //-----------------------------------
                if(structurenamechanged)
                {
                    //lets update everyone around us.
                    structurenamechanged = false;
                    Vector<Player> vPL = server.getPlayersAroundObject(this, false);
                    for(int i = 0; i < vPL.size();i ++)
                    {
                        Player p = vPL.get(i);
                        p.getClient().insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_INSO, (byte)0x03,(short)1, (short)2,this, this.getStructureName(), true));
                    }
                }
                //-------------------------------
                if(this.isGuild && lGuildObjectID != 0 && lGuildTickTime <= 0)
                {
                    //guild tick goes here
                }
                else
                {
                    lGuildTickTime-=lElapsedTime;
                    if(lGuildTickTime < 0)
                    {
                        lGuildTickTime = 0;
                    }
                }
                //--------------HARVESTER RELATED HERE
            }catch(Exception e){
                DataLog.logException("Exception Caught in update() ","Structure",ZoneServer.ZoneRunOptions.bLogToConsole,true,e);
            }
        }

        public Terminal getAdminTerminal() {
            
            if(this.adminTerminal!=null)
            {
                // this corrects a structure terminal to use the coordinates in the deed template.
                // this allows for template errors to be auto corrected in case a deed template error
                // existed in the template at some point. The terminal should correct it self upon the server 
                // reloading the deed template. This will allow for corrections without redeeding the structure.
                //also to play some nice april fools tricks LMAO
                Deed d = (Deed)server.getObjectFromAllObjects(this.getDeedID());
                d.setServer(server);
                DeedTemplate dT = DatabaseInterface.getDeedTemplateByID(d.getDeedTemplateID());                                
                adminTerminal.setX(getX() + dT.getTerminalX());
                adminTerminal.setY(getY() + dT.getTerminalY());
                adminTerminal.setZ(getZ() + dT.getTerminalX());
                adminTerminal.setCellX(dT.getTerminalX());
                adminTerminal.setCellY(dT.getTerminalY());
                adminTerminal.setCellZ(dT.getTerminalZ());
                adminTerminal.setOrientationN(dT.getTerminal_oI());
                adminTerminal.setOrientationS(dT.getTerminal_oJ());
                adminTerminal.setOrientationE(dT.getTerminal_oK());
                adminTerminal.setOrientationW(dT.getTerminal_oW());  
                long cellNum = dT.getTerminal_cell_id();
                Enumeration<Cell> vCellEnum = cellsInBuilding.elements();
                while (vCellEnum.hasMoreElements()) {
                	Cell cell = vCellEnum.nextElement();
                	if (cell.getCellNum() == cellNum) {
                		adminTerminal.setCellID(cell.getID());
                	}
                }

                //adminTerminal.setCellID(this.getID() + dT.getTerminal_cell_id());
                //lets fix structure capacity too in case it has been increased
                iStructureMaxItemsCapacity = dT.getStructure_items_capacity();
            }            
            return adminTerminal;
        }

        public void setAdminTerminal(Terminal adminTerminal) {
            this.adminTerminal = adminTerminal;
        }

        public Terminal getStructureSign() {
            
            if(this.structureSign!=null)
            {
                // this corrects a structure sign to use the coordinates in the deed template.
                // this allows for template errors to be auto corrected in case a deed template error
                // existed in the template at some point. The sign should correct it self upon the server 
                // reloading the deed template. This will allow for corrections without redeeding the structure.
                Deed d = (Deed)this.getServer().getObjectFromAllObjects(this.getDeedID());
                d.setServer(server);
                DeedTemplate dT = DatabaseInterface.getDeedTemplateByID(d.getDeedTemplateID());                                
                structureSign.setX(getX() + dT.getSignX()[iFacingDirection]);
                structureSign.setY(getY() + dT.getSignY()[iFacingDirection]);                        
                structureSign.setZ(getZ() + dT.getSignZ()[iFacingDirection]);                
                structureSign.setOrientationN(dT.getSign_oI()[iFacingDirection]);
                structureSign.setOrientationS(dT.getSign_oJ()[iFacingDirection]);
                structureSign.setOrientationE(dT.getSign_oK()[iFacingDirection]);
                structureSign.setOrientationW(dT.getSign_oW()[iFacingDirection]);                
            }
            return structureSign;
        }

        public void setStructureSign(Terminal structureSign) {
            this.structureSign = structureSign;
        }

        public int getMaintenancePool() {
            return iMaintenancePool;
        }

        public void setMaintenancePool(int iMaintenancePool) {
            this.iMaintenancePool = iMaintenancePool;
        }

        public int getPowerPool() {
            return iPowerPool;
        }

        public void setPowerPool(int iPowerPool) {
            this.iPowerPool = iPowerPool;
        }
        
        public byte[] addToMaintenancePool(int iMaintenancePool, boolean bUpdate){
            this.iMaintenancePool += iMaintenancePool;
            int conditionDamage = getConditionDamage();
            
            if (conditionDamage > 0) {
            	int iAmountToAddToCurrentCondition = iMaintenancePool / 4;
            	if (iAmountToAddToCurrentCondition > (conditionDamage)) {
            		iAmountToAddToCurrentCondition = (conditionDamage);
            		iMaintenancePool -= (iAmountToAddToCurrentCondition * 4);
            		conditionDamage = 0;
            	}
            	try {
            		return setConditionDamage(conditionDamage, bUpdate);
            	} catch (IOException e) {
            		System.out.println("Error building deltas message updating structure condition -- returning null.");
            		return null;
            	}
            }
            return null;
        }
        
        protected byte[] setConditionDamage(int iCondition, boolean bUpdate) throws IOException {
    		super.setConditionDamage(iCondition, false);
    		if (bUpdate) {
    			return PacketFactory.buildDeltasMessage(Constants.BASELINES_INSO, (byte)3, (short)1, (short)10, this, getConditionDamage());
    		}
    		return null;
    	}
        
        public void addToPowerPool(int iPowerPool) {
            this.iPowerPool += iPowerPool;
        }       
        
        
        public void correctTerminalCoordinates(float x,float y, float oI, float oJ, float oK, float oW,ZoneClient client){
            this.adminTerminal.setX(x);
            this.adminTerminal.setY(y);
            this.adminTerminal.setOrientationN(oI);
            this.adminTerminal.setOrientationS(oJ);
            this.adminTerminal.setOrientationE(oK);
            this.adminTerminal.setOrientationW(oW);
            try{
                client.getPlayer().spawnItem(this.adminTerminal);
            }catch(Exception e){
                System.out.println("Exception Caught while correctTerminalCoordinates " + e);
                e.printStackTrace();
            }
            
            System.out.println("Terminal Coordinates corrected.");
        }
        
        public void correctSignCoordinates(float x,float y, float oI, float oJ, float oK, float oW,ZoneClient client){
            this.structureSign.setX(x);
            this.structureSign.setY(y);
            this.structureSign.setOrientationN(oI);
            this.structureSign.setOrientationS(oJ);
            this.structureSign.setOrientationE(oK);
            this.structureSign.setOrientationW(oW);
            try{
                client.getPlayer().spawnItem(this.structureSign);
            }catch(Exception e){
                System.out.println("Exception Caught while correctSignCoordinates " + e);
                e.printStackTrace();
            }
            System.out.println("Sign Coordinates corrected.");
        }

        public Hashtable<Long, Cell> getCellsInBuilding() {
            return cellsInBuilding;
        }

        public TangibleItem getStructureBase() {
            return structureBase;
        }
        
        public void setStructureBase(TangibleItem base) {
        	structureBase = base;
        }

        public long getConstructionMarkerID() {
            return constructionMarkerID;
        }

        private void setConstructionMarkerID(long constructionMarkerID) {
            this.constructionMarkerID = constructionMarkerID;
        }

        public Structure getConstructionMarkerObject() {
            return constructionMarkerObject;
        }
        
        public void setConstructionMarker(Structure marker) {
        	constructionMarkerObject = marker;
        	setConstructionMarkerID(marker.getID());
        }

        public byte getStructureType() {
            return structureType;
        }

        public void setStructureType(byte structureType) {
            this.structureType = structureType;
        }

        protected void clearConstructionMarker(){
            constructionMarkerObject = null;
            constructionMarkerID = 0;
        }

        public long getStructureOwnerID() {
            return structureOwnerID;
        }

        public void setStructureOwnerID(long structureOwnerID) {
            this.structureOwnerID = structureOwnerID;
        }

        public boolean isAdmin(long playerID){
            return this.vAdminList.contains(playerID);
        }

        public boolean hasEntry(long playerID){
            if(this.structureStatus == Constants.STRUCTURE_PERMISSIONS_PRIVATE && !this.vEnterList.contains(playerID))
            {
                return false;
            }                
            return this.vEnterList.contains(playerID);
        }
        
        public boolean isBanned(long playerID){
            return this.vBanList.contains(playerID);
        }
        
        public boolean addToAdminList(long playerID){
            if(!vAdminList.contains(playerID))
            {
                vAdminList.add(playerID);             
            }
            if(!vEnterList.contains(playerID))
            {
                vEnterList.add(playerID);
            }
            if(!vHopperAdminList.contains(playerID))
            {
                vHopperAdminList.add(playerID);
            }
            if(vBanList.contains(playerID))
            {
                vBanList.remove(playerID);                                      
            }
            return this.vAdminList.contains(playerID);
        }
        
        public boolean addToEntryList(long playerID){
            if(!vEnterList.contains(playerID))
            {
                vEnterList.add(playerID);
            }
            if(vBanList.contains(playerID))
            {
                vBanList.remove(playerID);                                      
            }
            return this.vEnterList.contains(playerID);
        }
        
        public boolean addtoBanList(long playerID, ZoneClient client){
            try{
                if(playerID == this.getStructureOwnerID())
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"cannot_remove_owner",
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
                    		0f, false
                    ));
                    return false;
                }                
                if(!vBanList.contains(playerID))
                {
                    vBanList.add(playerID);                                      
                }
                if(vEnterList.contains(playerID))
                {
                    vEnterList.remove(playerID);
                }
                if(vAdminList.contains(playerID))
                {
                    vAdminList.remove(playerID);                        
                }  
                return this.vBanList.contains(playerID);
                
            }catch(Exception e){
                System.out.println("Exception in Structure.addtoBanList() " + e);
                e.printStackTrace();
            }
            return false;
        }

        public boolean removeFromAdminList(long playerID, ZoneClient client){
            try{
                if(playerID == this.getStructureOwnerID())
                {
                	client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"cannot_remove_owner",
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
                    		0f, false
                    ));

                    return false;
                }
                if(vAdminList.contains(playerID))
                {
                    vAdminList.remove(playerID);                        
                }  
                if(vHopperAdminList.contains(playerID))
                {
                    vHopperAdminList.remove(playerID);
                }
                return true;
            }catch(Exception e){
                System.out.println("Exception caught in Structure.removeFromAdminList() " + e);
                e.printStackTrace();
            }
            return false;
        }
        
        public boolean removeFromEnterList(long playerID, ZoneClient client){
             try{
                if(playerID == this.getStructureOwnerID())
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"cannot_remove_owner",
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
                    		0f, false
                    ));

                    return false;
                }
                if(vEnterList.contains(playerID))
                {
                    vEnterList.remove(playerID);                        
                } 
                if(vAdminList.contains(playerID))
                {
                    vAdminList.remove(playerID);                        
                }  
                return true;
            }catch(Exception e){
                System.out.println("Exception caught in Structure.removeFromAdminList() " + e);
                e.printStackTrace();
            }
            return false;
        }
        
         public boolean removeFromBanList(long playerID){
                                
                if(vBanList.contains(playerID))
                {
                    vBanList.remove(playerID);                        
                }                  
                return true;
        }
         
        public short getStructureStatus() {
            return structureStatus;
        }

        public void setStructureStatus(short structureStatus) {
            this.structureStatus = structureStatus;
        }

        public byte getCurrentHarvesterCondition(){
        	// !!!
        	int condition = this.getMaxCondition() - getConditionDamage();
            double c = (condition + 1) / this.getMaxCondition() * 100;
            return (byte)c;
        }


        /*public boolean isBUpdateThreadIsUsing() {
            return bUpdateThreadIsUsing;
        }

        public void setBUpdateThreadIsUsing(boolean bUpdateThreadIsUsing) {
            this.bUpdateThreadIsUsing = bUpdateThreadIsUsing;
        }*/

        public String getStructureName(){
            if(this.structureSign == null)
            {
                if(this.sStructureName!=null && !this.sStructureName.isEmpty())
                {
                    return this.sStructureName;
                }
                else if(this.sStructureName==null)
                {
                    return "";
                }
                return "@" + this.getSTFFileName() + ":" + this.getSTFFileIdentifier();
            }
            return this.structureSign.getFullName();
        }
        public void setStructureName(String sName){
            if(this.structureSign == null)
            {
                this.sStructureName = sName;
                structurenamechanged = true;
                return;
            }
            this.structureSign.setFirstName(sName);
        }

        public int getIMaintenanceRate() {
            return iMaintenanceRate;
        }

        public int getCountOfItemsInStructure(){
            int retval = 0;
            Enumeration <Cell> cEnum = this.cellsInBuilding.elements();
            while(cEnum.hasMoreElements())
            {
                Cell c = cEnum.nextElement();
                Enumeration <SOEObject> oEnum = c.getCellObjects().elements();
                while(oEnum.hasMoreElements())
                {
                    SOEObject o = oEnum.nextElement();
                    if(o instanceof TangibleItem)
                    {
                        retval++;
                    }
                }
            }
            
            return retval;
        }

        public int getIStructureMaxItemsCapacity() {
            return iStructureMaxItemsCapacity;
        }

        public String getEmailConditionString(){
            int iCondition = getMaxCondition() - getConditionDamage();
            String retval = iCondition + " / " + getMaxCondition();
            return retval;
        }
        
        public String getCurrentConditionString(){
            
            int iCurrentCondition = ((1 + getConditionDamage() / getMaxCondition() ) *100);
            
            if(iCurrentCondition <= 0)
            {
                int iRepair = getConditionDamage() * 4;
                return iCurrentCondition + "%(" + Integer.toString(iRepair) + " credits to repair.)";
            }
            
            return Integer.toString(iCurrentCondition) + "%";
        }
        
        public String getCurrentStructureStatusString(){
                String sStatus = "";
                switch((short)getStructureStatus())
                {
                    case 1:
                    {
                        sStatus = " Private";
                        break;
                    }
                    case 2:
                    {
                        sStatus = " Public";
                        break;
                    }
                }
                return sStatus;
        }
        
        public String getCurrentStructureMaintenancePoolString(){
            String retVal = getMaintenancePool() + " (" + (getMaintenancePool() / getIMaintenanceRate() /24 ) + " days)";
            return retVal;
        }

        public long getLCreationDate() {
            return lCreationDate;
        }

        public Waypoint getStructureWaypoint() {
            return structureWaypoint;
        }

        public void setStructureWaypoint(Waypoint structureWaypoint) {
            this.structureWaypoint = structureWaypoint;
        }

        public boolean canRedeed(){
            boolean retval = false;
          
            if(getMaxCondition() - getConditionDamage() == getMaxCondition())
            {
                retval = true;
            }
            Deed d = (Deed)server.getObjectFromAllObjects(deedID);
            if(getMaintenancePool() >= d.getRedeedfee())
            {
                retval = true;
            }
            else
            {
                retval = false;
            }
            
            return retval;
        }

        public int getIDestructionCode() {
            return iDestructionCode;
        }

        public void setIDestructionCode(int iDestructionCode) {
            this.iDestructionCode = iDestructionCode;
        }

        public boolean isInstallationActive() {
            return installationActive;
        }

        public void setInstallationActive(boolean installationActive) {
            this.installationActive = installationActive;
        }

        public void activateInstallation(){
            setIAnimationBitmask(257);
            installationActive = true;
        }
        
        public void deactivateInstallation(){
            this.setIAnimationBitmask(256);
            installationActive = false;

        }
        
        public void useItem(ZoneClient client, byte commandID){
            try{
            	Player player = client.getPlayer();
                switch(this.getTemplateID())
                {
                    case 15428:
                    case 15429:
                    case 15430:
                    case 15431:
                    case 15432:
                    case 15433:
                    case 15434:
                    case 15435:
                    case 15436:
                    case 15437:
                    case 15438:
                    case 15439:
                    case 15440:
                    case 15441:
                    case 15442:
                    case 15443:
                    case 15444:
                    case 15445:
                    case 15446:
                    case 15447:
                    case 15448:
                    case 15449:
                    case 15450:
                    case 15451:
                    case 15452:
                    {
                        switch(commandID)
                        {                  // Button Number
                                            // |  ParentButton
                                            // |  |  Button Command ID
                                            // |  |   |
                                            // |  |   |    Button Text String                            
                            case (byte)207://  3, 1, 207, '@player_structure:permission_destroy'
                            {                                   
                                if(this.getStructureOwnerID() == client.getPlayer().getID())
                                {                                    
                                    if(!isInstallationActive())
                                    {
                                        System.out.println("Structure Destruction Requested.");
                                        SUIWindow w = new SUIWindow(player);
                                        w.setWindowType(Constants.SUI_STRUCTURE_CONFIRM_REDEED);
                                        w.setOriginatingObject(this);
                                        String WindowTypeString = "handleSUI";
                                        String DataListTitle = this.getStructureName() + "         Confirm Structure Destruction";
                                        String DataListPrompt = "@player_structure:confirm_destruction_d1 \r\n @player_structure:confirm_destruction_d2 \r\n @player_structure:confirm_destruction_d3a";
                                        DataListPrompt += " \\#00BB00 @player_structure:confirm_destruction_d3b";
                                        DataListPrompt += " \r\n\r\n \\#EEEEEE @player_structure:confirm_destruction_d4";

                                        String sCanRedeed = " \\#FF0000 @player_structure:can_redeed_no_suffix ";
                                        //String sColor = " \\#FF0000 ";//red
                                        String sWhite = " \\#EEEEEE "; // NO -- True white is #FFFFFF
                                        if(getConditionDamage() == 0 && this.getMaintenancePool() >= 800)
                                        {
                                            sCanRedeed = " \\#00BB00 @player_structure:can_redeed_yes_suffix ";
                                            //sColor = " \\#00BB00 ";//green
                                        }
                                        String sConditionColor = " \\#FF0000 ";
                                        if(getConditionDamage() == 0)
                                        {
                                            sConditionColor = " \\#00BB00 ";//green
                                        }
                                        String sMaintColor = " \\#FF0000 ";
                                        Deed d = (Deed)server.getObjectFromAllObjects(deedID);
                                        if(this.getMaintenancePool() >= d.getRedeedfee())
                                        {
                                            sMaintColor = " \\#00BB00 ";
                                        }                                       
                                        
                                        String sList[] = new String[6];
                                        sList[0] = "@player_structure:can_redeed_alert" + sCanRedeed + sWhite; 
                                        sList[1] = "@player_structure:redeed_condition " + sConditionColor + (this.getMaxCondition()) + sWhite + "/" + sConditionColor + (this.getMaxCondition() - this.getConditionDamage());
                                        sList[2] = "@player_structure:redeed_maintenance " + sMaintColor + this.getMaintenancePool() + sWhite + "/" + sMaintColor + d.getRedeedfee() + sWhite;//condition
                                        sList[3] = "--- Select an Option to Proceed ---";
                                        sList[4] = "OK To Proceed With Redeed";
                                        sList[5] = "Cancel To Abort Redeed";
                                        client.insertPacket(w.SUIScriptListBox(client, WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
                                    }
                                    else if(this.isInstallationActive())
                                    {
                                        if(this instanceof Harvester)
                                        {
                                           client.insertPacket(PacketFactory.buildChatSystemMessage(
                                                "player_structure",
                                                "destroy_deactivate_first"
                                        ));
                                        }
                                        else
                                        {
                                            client.insertPacket(PacketFactory.buildChatSystemMessage(
                                                "player_structure",
                                                "deactivate_factory_for_delete"
                                        ));
                                        }
                                    }
                                }                                
                                else
                                {
                                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                                    		"player_structure",
                                    		"destroy_must_be_owner"   ));
                                }

                                break;
                            }    
                            case (byte)203://  4, 1, 203, '@player_structure:management_status'
                            {                                
                                Player owner = (Player)client.getServer().getObjectFromAllObjects(this.getStructureOwnerID());
                                if(this.isAdmin(client.getPlayer().getID())) 
                                {
                                    SUIWindow w = new SUIWindow(player);
                                    w.setWindowType(Constants.SUI_STRUCTURE_SHOW_STATUS);
                                    w.setOriginatingObject(this);
                                    String WindowTypeString = "handleSUI";
                                    String DataListTitle = "@player_structure:structure_status_t";
                                    String DataListPrompt = "@player_structure:structure_name_prompt " + this.getStructureName();
                                    String sList[] = new String[7];
                                    sList[0] = "@player_structure:owner_prompt " + owner.getFullName(); //owner
                                    sList[1] = "@player_structure:structure_status_t " + this.getCurrentStructureStatusString(); //status private / public
                                    sList[2] = "@player_structure:condition_prompt " + this.getCurrentConditionString();//condition
                                    sList[3] = "@player_structure:current_maint_pool " + this.getCurrentStructureMaintenancePoolString(); ////maint pool int /days hours
                                    sList[4] = "@player_structure:power_reserve_prompt " + this.getPowerPool() + " units";
                                    sList[5] = "@player_structure:maintenance_rate_prompt " + this.getIMaintenanceRate() + " cr/hr";//main rate
                                    sList[6] = "Storage Capcity: " + this.getIOutputHopperSize(); //max storage                
                                    client.insertPacket(w.SUIScriptListBox(client, WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
                                }
                                else
                                {
                                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                                    		"player_structure",
                                    		"destroy_must_be_owner",
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
                                    		0f, false
                                    ));
                                }
                                break;
                            }
                            case (byte)208://  5, 1, 208, '@player_structure:management_pay'
                            {
                                if(this.vAdminList.contains(client.getPlayer().getID()))
                                {
                                    SUIWindow w = new SUIWindow(player);
                                    w.setWindowType(Constants.SUI_STRUCTURE_PAY_MAINTENANCE);
                                    w.setOriginatingObject(this);                
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
                                    client.insertPacket(PacketFactory.buildChatSystemMessage("You must be structure admin to do that"));
                                }
                                break;
                            }
                            case (byte)50:
                            case (byte)166://  6, 1, 166, '@player_structure:management_name_structure'
                            {                               
                                if(this.getStructureOwnerID() == client.getPlayer().getID())
                                {
                                    SUIWindow w = new SUIWindow(player);  
                                    w.setWindowType(Constants.SUI_RENAME_STRUCTURE);
                                    w.setOriginatingObject(this);                    
                                    String sCurrentName = this.getStructureName();                   
                                    client.insertPacket(w.SUIScriptTextInputBox(client, "handleFilterInput","@player_structure:structure_name_prompt", "@player_structure:management_name_structure", true, "Enabled", "Visible", 127, sCurrentName));
                                }
                                else
                                {
                                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                                    		"player_structure",
                                    		"destroy_must_be_owner",
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
                                    		0f, false
                                    ));

                                }

                                break;
                            }
                            case (byte)157://  7, 1, 157, '@harvester:manage'
                            {
                                //System.out.println("@harvester:manage");
                                if(this.isAdmin(client.getPlayer().getID()))
                                {
                                    client.insertPacket(PacketFactory.buildWorldUpdateOpenHarvesterOperatorWindow(this));
                                    client.insertPacket(PacketFactory.buildObjectControllerOpenHarvesterOperatorWindow(this));
                                }
                                else
                                {
                                    client.insertPacket(PacketFactory.buildChatSystemMessage("You must be Structure Admin to operate."));
                                }
                                break;
                            }
                            case (byte)70://  8, 1, 70, '@player_structure:management_power'
                            {
                                if(this.isAdmin(client.getPlayer().getID()))
                                {
                                    int iPowerOnHand = 0;
                                    Vector<ResourceContainer> vRCList = new Vector<ResourceContainer>();
                                    for(int i =0; i < client.getPlayer().getInventoryItems().size();i++)
                                    {
                                        TangibleItem o = client.getPlayer().getInventoryItems().get(i);
                                        if(o instanceof ResourceContainer)
                                        {
                                            vRCList.add((ResourceContainer)o);
                                        }
                                    }
                                    int iPowerCount = 0;
                                    if(vRCList.isEmpty())
                                    {
                                        client.insertPacket(PacketFactory.buildChatSystemMessage("You do not have any power in your inventory."));
                                    }
                                    else
                                    {
                                        
                                        for(int i=0; i < vRCList.size();i++)
                                        {
                                            ResourceContainer r = vRCList.get(i);
                                            SpawnedResourceData rd = server.getResourceManager().getResourceByID(r.getResourceSpawnID());
                                           // System.out.println("Resource in inventory Type: " + rd.getResourceType());
                                            if(rd.getResourceType().contains("Radioactive"))
                                            {
                                                if(rd.getPotentialEnergy() >= 650)
                                                {
                                                    iPowerOnHand += (r.getStackQuantity() * 5) / 2;
                                                }
                                                else
                                                {
                                                    iPowerOnHand += (r.getStackQuantity() * 195) / 100;
                                                }
                                                iPowerCount++;
                                            }
                                            else if(rd.getResourceType().contains("Solar"))
                                            {
                                                if(rd.getPotentialEnergy() >= 950)
                                                {
                                                    iPowerOnHand += (r.getStackQuantity() * 3) / 2;
                                                }
                                                else
                                                {
                                                    iPowerOnHand += r.getStackQuantity();
                                                }
                                                iPowerCount++;
                                            }
                                            else if(rd.getResourceType().contains("Wind"))
                                            {
                                                iPowerOnHand += r.getStackQuantity();
                                                iPowerCount++;
                                            }
                                        }
                                    }
                                    //client.insertPacket(PacketFactory.buildChatSystemMessage("You have " + iPowerCount + " power source in your inventory."));
                                    SUIWindow w = new SUIWindow(player);
                                    w.setWindowType(Constants.SUI_STRUCTURE_PAY_POWER);
                                    w.setOriginatingObject(this);
                                    String sTransferBoxPrompt = "Select the amount of power to deposit"; //.\r\n\r\nCurrent Power Reserves at: " + this.getIPowerPool();
                                    String sFromLabel = "Total Power";
                                    String sToLabel = "To Deposit";
                                    int iFromAmount = iPowerOnHand;
                                    int iToAmount = 0;//<--always 0 because we do not allow for power to be retrieved.
                                    int iConversionRatioFrom = 1;
                                    int iConversionRatioTo = 1;
                                    client.insertPacket(w.SUIScriptTransferBox(client, sToLabel, sTransferBoxPrompt, sTransferBoxPrompt, sFromLabel, sToLabel, iFromAmount, iToAmount, iConversionRatioFrom, iConversionRatioTo));
                                }
                                else
                                {
                                    client.insertPacket(PacketFactory.buildChatSystemMessage("You must be Structure Admin to add power."));
                                }
                                break;
                            }
                            case (byte)121: // 10, 9, 121, '@player_structure:permission_admin', '', 3, 0
                            {
                                if(this.isAdmin(client.getPlayer().getID()))
                                {
                                    SUIWindow w = new SUIWindow(player);
                                    w.setWindowType(Constants.SUI_STRUCTURE_SHOW_ADMIN_LIST);
                                    w.setOriginatingObject(this);
                                    String WindowTypeString = "handleSUI";
                                    String DataListTitle = "ADMIN LIST";
                                    String DataListPrompt = "This is a list of Admins for this Structure";
                                    String sList[] = new String[this.getVAdminList().size()];
                                    for(int i =0; i < this.getVAdminList().size();i++)
                                    {
                                        Player p = client.getServer().getPlayer(this.getVAdminList().get(i));
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
                                if(this.isAdmin(client.getPlayer().getID()))
                                {
                                    SUIWindow w = new SUIWindow(player);
                                    w.setWindowType(Constants.SUI_STRUCTURE_ADD_PLAYER_TO_ADMIN);
                                    w.setOriginatingObject(this);
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
                            case (byte)204:// remove admin
                            {
                                if(this.isAdmin(client.getPlayer().getID()))
                                {
                                    SUIWindow w = new SUIWindow(player);
                                    w.setWindowType(Constants.SUI_STRUCTURE_REMOVE_ADMIN);
                                    w.setOriginatingObject(this);
                                    String WindowTypeString = "handleSUI";
                                    String DataListTitle = "REMOVE ADMIN";
                                    String DataListPrompt = "This is a list of Admins for this Structure. Select an Entry to remove from the Admin List.\r\n\r\nNOTE: Owners Cannot Be Removed from this list.";
                                    String sList[] = new String[this.getVAdminList().size()];
                                    for(int i =0; i < this.getVAdminList().size();i++)
                                    {
                                        Player p = client.getServer().getPlayer(this.getVAdminList().get(i));
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
                        }
                        break;
                    }                    
                    default:
                    {
                        System.out.println("Unhandled Structure Template in Structure.useItem Template ID:" + this.getTemplateID());
                    }
                }
            }catch(Exception e){
                System.out.println("Exception Caught in Structure.useItem " + e);
                e.printStackTrace();
            }
        }

        public int getIInputHopperSize() {
            return iInputHopperSize;
        }

        public void setIInputHopperSize(int iInputHopperSize) {
            this.iInputHopperSize = iInputHopperSize;
        }

        public int getIOutputHopperSize() {
            
            return iOutputHopperSize;
        }

        public void setIOutputHopperSize(int iOutputHopperSize) {
            this.iOutputHopperSize = iOutputHopperSize;
        }

        public Vector<Long> getVAdminList() {
            return vAdminList;
        }

        public Vector<Long> getVBanList() {
            return vBanList;
        }

        public Vector<Long> getVEnterList() {
            return vEnterList;
        }
        
        public void updateStructureCellPermissions(ZoneClient client){
            try{
                Vector<Player> vPL = client.getServer().getPlayersAroundObject(this,true);
                Hashtable<Long,Cell> vCells = this.getCellsInBuilding();
                for(int i = 0; i < vPL.size();i++)
                {
                    Player p = vPL.get(i);
                    Enumeration <Cell> cEnum = vCells.elements();
                    while(cEnum.hasMoreElements())
                    {
                        Cell c = cEnum.nextElement();
                        p.getClient().insertPacket(PacketFactory.buildUpdateCellPermissionMessage(c, this, p));
                        if(p.getCellID() == c.getID() && this.getStructureStatus() == Constants.STRUCTURE_PERMISSIONS_PRIVATE && !this.hasEntry(p.getID()))
                        {                                                                                    
                            p.getClient().insertPacket(PacketFactory.buildUpdateContainmentMessage(p, null, -1));                                
                        }
                    }                        
                }
            }catch(Exception e){
                System.out.println("Exception caught in Structure.updateStructureCellPermissions " + e);
                e.printStackTrace();
            }
        }

        public ZoneServer getServer() {
            return server;
        }

        public void setServer(ZoneServer server) {
            this.server = server;
        }

        public Vector<Long> getVHopperAdminList() {
            return vHopperAdminList;
        }

        public void setVHopperAdminList(Vector<Long> vHopperAdminList) {
            this.vHopperAdminList = vHopperAdminList;
        }
        
        public boolean addToHopperAdminList(long playerID){
            if(!vHopperAdminList.contains(playerID))
            {
                vHopperAdminList.add(playerID);
            }
            return vHopperAdminList.contains(playerID);
        }
        
        public boolean removeFromHopperAdminList(long playerID, ZoneClient client){
            try{
                if(playerID == this.getStructureOwnerID())
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"cannot_remove_owner",
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
                    		0f, false
                    ));

                    return false;
                }
                if(vHopperAdminList.contains(playerID))
                {
                    vHopperAdminList.remove(playerID);
                }
                return vHopperAdminList.contains(playerID);
            }catch(Exception e){
                System.out.println("Exception Caught in Structure.removeFromHopperAdminList " + e);
                e.printStackTrace();
            }
            return false;
        }

        public int getLotsize() {
            return lotsize;
        }

        public Terminal getGuildTerminal() {
            if(this.guildTerminal!=null)
            {
                // this corrects a structure terminal to use the coordinates in the deed template.
                // this allows for template errors to be auto corrected in case a deed template error
                // existed in the template at some point. The terminal should correct it self upon the server 
                // reloading the deed template. This will allow for corrections without redeeding the structure.
                //also to play some nice april fools tricks LMAO
                Deed d = (Deed)this.getServer().getObjectFromAllObjects(this.getDeedID());
                d.setServer(server);
                DeedTemplate dT = DatabaseInterface.getDeedTemplateByID(d.getDeedTemplateID());                                
                this.guildTerminal.setX(this.getX() + dT.getGuild_terminal_x());
                this.guildTerminal.setY(this.getY() + dT.getGuild_terminal_y());
                this.guildTerminal.setZ(this.getZ() + dT.getGuild_terminal_z());
                this.guildTerminal.setCellX(dT.getGuild_terminal_x());
                this.guildTerminal.setCellY(dT.getGuild_terminal_y());
                this.guildTerminal.setCellZ(dT.getGuild_terminal_z());                  
                this.guildTerminal.setOrientationN(dT.getGuild_terminal_oI());
                this.guildTerminal.setOrientationS(dT.getGuild_terminal_oJ());
                this.guildTerminal.setOrientationE(dT.getGuild_terminal_oK());
                this.guildTerminal.setOrientationW(dT.getGuild_terminal_oW());   
                long cellNum = dT.getTerminal_cell_id();
                Enumeration<Cell> vCellEnum = cellsInBuilding.elements();
                while (vCellEnum.hasMoreElements()) {
                	Cell cell = vCellEnum.nextElement();
                	if (cell.getCellNum() == cellNum) {
                		guildTerminal.setCellID(cell.getID());
                	}
                }

                //this.guildTerminal.setCellID(this.getID() + dT.getTerminal_cell_id());
            }
            return guildTerminal;
        }

        public void setGuildTerminal(Terminal guildTerminal) {
            this.guildTerminal = guildTerminal;
        }

        public boolean bIsGuild() {
            return isGuild;
        }

        public Vector<Terminal> getVElevatorTerminals() {
            if(vElevatorTerminals==null)
            {
                vElevatorTerminals = new Vector<Terminal>();
            }
            return vElevatorTerminals;
        }

        protected void spawnObjectsInBuildingCells(ZoneClient client){
            Player player = client.getPlayer();
            String sName = player.getFirstName();
        	try {
	            Enumeration<Cell> cEnum = this.cellsInBuilding.elements();
	        	int cellCount = 0;
	        	int objectCount = 0;
	            while(cEnum.hasMoreElements())
	            {
	                Cell c = cEnum.nextElement();
	                Enumeration<SOEObject> cOBj =  c.getCellObjects().elements();
	                
	                while(cOBj.hasMoreElements())
	                {
	                	
	                    SOEObject o = cOBj.nextElement();
	                    if (o instanceof Player) {
	                    	Player tarPlayer = (Player)o;
	                    	System.out.println(sName + " enters cell -- spawning " + tarPlayer.getFirstName() + " to him.");
	                    }
	                    //o.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_SPAWN);
	                    try {
	                    	client.getPlayer().spawnItem(o);
	                    } catch (IOException e) {
	                    	System.out.println("Error in spawnObjectsInBuildingCells: " + e.toString());
	                    	e.printStackTrace();
	                    }
	                    objectCount++;
	                }
	                System.out.println("Spawned " + objectCount + " in cell index " + c.getCellNum());
	                cellCount++;
	            }
	            System.out.println("Done spawning structure objects -- spawned " + objectCount + " total objects");
        	} catch (Exception e) {
        		System.out.println("Exception in spawnObjectsInBuildingCells: " + e.toString());
        		e.printStackTrace();
        	}
        }
        
        protected void despawnObjectsInBuildingCells(ZoneClient client){
            Enumeration<Cell> cEnum = this.cellsInBuilding.elements();
            while(cEnum.hasMoreElements())
            {
                Cell c = cEnum.nextElement();
                Enumeration<SOEObject> cOBj =  c.getCellObjects().elements();
                while(cOBj.hasMoreElements())
                {
                    SOEObject o = cOBj.nextElement();
                    
                    //o.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_SPAWN);
                    try {
                    	client.getPlayer().despawnItem(o);	
                    } catch (IOException e) {
                    	System.out.println("Error in spawnObjectsInBuildingCells: " + e.toString());
                    }
                }
            }
        }

    public int getIFacingDirection() {
        return iFacingDirection;
    }

    protected void setIFacingDirection(int iFacingDirection){
        this.iFacingDirection = iFacingDirection;
    }

    public int getPowerRate() {
        return iPowerRate;
    }


    public boolean usesPower() {
        return usespower;
    }

    public int getIAnimationBitmask() {
        return iAnimationBitmask;
    }

    public void setIAnimationBitmask(int iAnimationBitmask) {
        this.iAnimationBitmask = iAnimationBitmask;
    }
    
    public void fixTerminalPointers() {
        if (structureSign != null) {
        	structureSign.setStructure(this);
        }
        if (adminTerminal != null) {
        	adminTerminal.setStructure(this);
        }
        if (guildTerminal != null) {
        	guildTerminal.setStructure(this);
        }
        if (!vElevatorTerminals.isEmpty()) {
	        for (int i = 0; i < vElevatorTerminals.size(); i++) {
	        	Terminal evelatorTerminal = vElevatorTerminals.elementAt(i);
	        	evelatorTerminal.setStructure(this);
	        }
        }
    }
    
    public int experiment(int[] iExperimentalIndex, int[] numExperimentationPointsUsed, Player thePlayer) throws IOException {
		// Depends on what it is.
		System.out.println("Structure experimenting."); // Structures themselves cannot be experimented on.  We must experiment on their deed object.
		return 0;
	}
	
    public boolean contains(SOEObject o) {
    	boolean bContained = false;
    	Enumeration<Cell> vCells = cellsInBuilding.elements();
    	while (vCells.hasMoreElements() && !bContained) {
    		Cell c = vCells.nextElement();
    		bContained = c.contains(o);
    	}
    	return bContained;
    }
}
