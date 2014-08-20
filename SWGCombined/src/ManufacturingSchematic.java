import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;


public class ManufacturingSchematic extends IntangibleObject {
	public final static long serialVersionUID = 1l;
	private final static String stfFileName = "string_id_table";
	private final String stfFileIdentifier = null; // This is crappy coding practice, but it gets the point across.
	private long[] lComponentSerials;
	private long lSerialNumber;

	private float fBaseCraftingComplexity;
	private float fCurrentCraftingComplexity;
	
	private String sCrafterName;
	private CraftingSchematic cSchematic;
	private TangibleItem itemBeingCrafted;
	private CraftingTool toolUsedToCraft;
	private byte iIngredientUpdateCounter = 1;
	private boolean bHasSentFirstBaseline7DeltaStageAddIngredient = false;
	
	private int iNumberOfFactoryItems = 0;
	// NOTE:  vID 0 through vID 6 all have the same length to them.  So, when we get the schematicComponentData, we should set up all 6 arrays.
	// vID 0
	private CraftingSchematicComponent[] schematicComponentData;
	private int iSchematicComponentDataUpdateCount;
	
	
	// MSCO3 vID 5
	private Vector<ManufacturingSchematicAttribute> vSchematicAttributes;
	private int iSchematicAttributeUpdateCount = 0;
	private boolean bCanRecoverInstalledItems = true;
	
	// vID 1
	private int[] vID1IntArray;
	private int vID1IntArrayUpdateCount;

	// vID 2
	private long[][] vObjectIDBySlot;
	private int iObjectIdBySlotUpdateCount;
	
	private long[][] vSceneObjectIDBySlot;
	
	// vID 3
	private int[] vSlotResourceQuantityInserted;
	private int iSlotResourceQuantityUpdateCount;
	
	// vID 4
	private int[] vID4IntArray;
	private int vID4IntArrayUpdateCount;
	
	// vID 5
	private int[] vID5IntArray;
	private int vID5IntArrayUpdateCount;
	
	//vID6
	private int[] vID6IntArray;
	private int vID6IntArrayUpdateCount;
	
	
	
	private byte vID7 = 4;
	
	// vID8
	private CraftingExperimentationAttribute[] vExperimentalAttributes;
	private int iExperimentalAttributesUpdateCount;
	
	private float[] vID9CurrentExperimentationValueFloatArray;
	private int vID9CurrentExperimentationValueUpdateCount;
	
	private float[] vID10FloatArray;
	private int vID10FloatArrayUpdateCount;
	
	private float[] vID11FloatArray;
	private int vID11FloatArrayUpdateCount;
	
	private float[] vID12MaxExperimentationValue;
	private int vID12FloatArrayUpdateCount;
	
	private String[] vID13StringArray;
	private int vID13StringArrayUpdateCount;
	
	private int[] vID14IntArray;
	private int vID14IntArrayUpdateCount;
	
	private int[] vID15IntArray;
	private int vID15IntArrayUpdateCount;
	
	private int[] vID16IntArray;
	private int vID16IntArrayUpdateCount;
	
	private byte vID17;
	
	private float vID18;
	
	private int[] vID19IntArray;
	private int vID19IntArrayUpdateCount;
	
	private byte vID20;
	

	
	protected byte[] addSchematicAttribute(ManufacturingSchematicAttribute attrib, boolean bUpdateZone) throws IOException {
		vSchematicAttributes.add(0, attrib);
		if (bUpdateZone) {
			return PacketFactory.buildDeltasMSCO3SchematicAttribute(this, 1, (byte)0);
		} 
		return null;
	}
	
	protected byte[] addSchematicAttribute(Vector<ManufacturingSchematicAttribute> vAttribs, boolean bUpdateZone) throws IOException {
		vSchematicAttributes.addAll(vAttribs);
		if (bUpdateZone) {
			return PacketFactory.buildDeltasMSCO3SchematicAttribute(this, vAttribs.size(), Constants.DELTA_CREATING_ITEM);
		}
		return null;
	}
	
	protected Vector<ManufacturingSchematicAttribute> getSchematicAttributes(){
		return vSchematicAttributes;
	}
	
	protected int getSchematicAttributeUpdateCount(boolean bUpdate) {
		if (bUpdate) {
			iSchematicAttributeUpdateCount++;
		}
		return iSchematicAttributeUpdateCount;
	}
	
	protected void setSchematicAttributeUpdateCount(int numUpdates) {
		iSchematicAttributeUpdateCount= numUpdates;
	}
	 
	public int[] getVID1IntArray() {
		return vID1IntArray;
	}

	public void setVID1IntArray(int[] intArray) {
		vID1IntArray = intArray;
	}

	public int getVID1IntArrayUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			vID1IntArrayUpdateCount++;
		}
		return vID1IntArrayUpdateCount;
	}

	public void setVID1IntArrayUpdateCount(int intArrayUpdateCount) {
		vID1IntArrayUpdateCount = intArrayUpdateCount;
	}

	public long[] getObjectIDsBySlot(int slotIndex) {
		return vObjectIDBySlot[slotIndex];
	}
	public long[][] getAllObjectIDsInSlots() {
		return vObjectIDBySlot;
	}
	public long[][] getAllSceneIDsInSlots() {
		return vSceneObjectIDBySlot;
	}
	public void setObjectIDsBySlot(int slotIndex, long[] objectIDBySlot) {
		vObjectIDBySlot[slotIndex] = objectIDBySlot;
	}
		
	public int getObjectIdBySlotUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			iObjectIdBySlotUpdateCount++;
		}
		return iObjectIdBySlotUpdateCount;
	}

	public void setObjectIdBySlotUpdateCount(int objectIdBySlotUpdateCount) {
		iObjectIdBySlotUpdateCount = objectIdBySlotUpdateCount;
	}

	public int[] getSlotResourceQuantityInserted() {
		return vSlotResourceQuantityInserted;
	}
	public int getSlotResourceQuantityInsertedAtIndex(int index) {
		return vSlotResourceQuantityInserted[index];
	}

	public void setSlotResourceQuantityInserted(int[] slotResourceQuantityInserted) {
		vSlotResourceQuantityInserted = slotResourceQuantityInserted;
	}

	public int getSlotResourceQuantityUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			iSlotResourceQuantityUpdateCount++;
		}
		return iSlotResourceQuantityUpdateCount;
	}

	public void setSlotResourceQuantityUpdateCount(
			int slotResourceQuantityUpdateCount) {
		iSlotResourceQuantityUpdateCount = slotResourceQuantityUpdateCount;
	}

	public int[] getVID4IntArray() {
		return vID4IntArray;
	}

	public void setVID4IntArray(int[] intArray) {
		vID4IntArray = intArray;
	}

	public int getVID4IntArrayUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			vID4IntArrayUpdateCount++;
		}
		return vID4IntArrayUpdateCount;
	}

	public void setVID4IntArrayUpdateCount(int intArrayUpdateCount) {
		vID4IntArrayUpdateCount = intArrayUpdateCount;
	}

	public int[] getVID5IntArray() {
		return vID5IntArray;
	}

	public void setVID5IntArray(int[] intArray) {
		vID5IntArray = intArray;
	}

	public int getVID5IntArrayUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			vID5IntArrayUpdateCount++;
		}
		return vID5IntArrayUpdateCount;
	}

	public void setVID5IntArrayUpdateCount(int intArrayUpdateCount) {
		vID5IntArrayUpdateCount = intArrayUpdateCount;
	}

	public int[] getVID6IntArray() {
		return vID6IntArray;
	}

	public void setVID6IntArray(int[] intArray) {
		vID6IntArray = intArray;
	}

	public int getVID6IntArrayUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			vID6IntArrayUpdateCount++;
		}
		return vID6IntArrayUpdateCount;
	}

	public void setVID6IntArrayUpdateCount(int intArrayUpdateCount) {
		vID6IntArrayUpdateCount = intArrayUpdateCount;
	}

	public byte getVID7() {
		return vID7;
	}

	public byte[] setVID7(byte vid7, boolean bUpdate) throws IOException{
		vID7 = vid7;
		if (bUpdate) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_MSCO, (byte)7, (short)1, (short)7, this, vID7);
		} else {
			return null;
		}
	}

	public CraftingExperimentationAttribute[] getExperimentalAttributes() {
		return vExperimentalAttributes;
	}

	public void setExperimentalAttributes(
			CraftingExperimentationAttribute[] experimentalAttributes) {
		// vID 8
		// First thing we're going to do is get a copy of the attributes.
		CraftingExperimentationAttribute[] tempArray = new CraftingExperimentationAttribute[experimentalAttributes.length];
		int numActualAttributes = 0;
		for (int i = 0; i < experimentalAttributes.length; i++) {
			if (experimentalAttributes[i] != null) {
				CraftingExperimentationAttribute attr = experimentalAttributes[i];
				if (attr.getStfFileName() != null) {
					// Valid attribute
					tempArray[numActualAttributes] = attr;
					numActualAttributes++;
				}
			}
		}
		vExperimentalAttributes = Arrays.copyOfRange(tempArray, 0, numActualAttributes);
		vID9CurrentExperimentationValueFloatArray = new float[numActualAttributes];
		vID10FloatArray = new float[numActualAttributes];
		vID11FloatArray = new float[numActualAttributes];
		vID12MaxExperimentationValue = new float[numActualAttributes];
	}

	public int getExperimentalAttributesUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			iExperimentalAttributesUpdateCount++;
		}
		return iExperimentalAttributesUpdateCount;
	}

	public void setExperimentalAttributesUpdateCount(
			int experimentalAttributesUpdateCount) {
		iExperimentalAttributesUpdateCount = experimentalAttributesUpdateCount;
	}

	// vID9 is actually a float array -- this is the experimental values of the properties in vID 8.
	public float[] getVID9CurrentExperimentalValueArray() {
		return vID9CurrentExperimentationValueFloatArray;
	}

	public void setVID9CurrentExperimentalValueArray(float[] intArray) {
		vID9CurrentExperimentationValueFloatArray = intArray;
	}

	public int getVID9CurrentExperimentalValueUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			
			vID9CurrentExperimentationValueUpdateCount++;
		}
		return vID9CurrentExperimentationValueUpdateCount;
	}

	public void setVID9CurrentExperimentalValueUpdateCount(int intArrayUpdateCount) {
		vID9CurrentExperimentationValueUpdateCount = intArrayUpdateCount;
	}

	public float[] getVID10FloatArray() {
		return vID10FloatArray;
	}

	public void setVID10FloatArray(float[] intArray) {
		vID10FloatArray = intArray;
	}

	public int getVID10FloatArrayUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			vID10FloatArrayUpdateCount++;
		}
		return vID10FloatArrayUpdateCount;
	}

	public void setVID10FloatArrayUpdateCount(int intArrayUpdateCount) {
		vID10FloatArrayUpdateCount = intArrayUpdateCount;
	}

	public float[] getVID11FloatArray() {
		return vID11FloatArray;
	}

	public void setVID11FloatArray(float[] intArray) {
		vID11FloatArray = intArray;
	}

	public int getVID11FloatArrayUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			vID11FloatArrayUpdateCount++;
		}
		return vID11FloatArrayUpdateCount;
	}

	public void setVID11FloatArrayUpdateCount(int intArrayUpdateCount) {
		vID11FloatArrayUpdateCount = intArrayUpdateCount;
	}

	public float[] getVID12CurrentExperimentationArray() {
		return vID12MaxExperimentationValue;
	}

	public void setVID12CurrentExperimentationArray(float[] intArray) {
		vID12MaxExperimentationValue = intArray;
	}

	public int getVID12MaxExperimentationArrayUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			vID12FloatArrayUpdateCount++;
		}
		return vID12FloatArrayUpdateCount;
	}

	public void setVID12CurrentExperimentationArrayUpdateCount(int intArrayUpdateCount) {
		vID12FloatArrayUpdateCount = intArrayUpdateCount;
	}

	public String[] getVID13StringArray() {
		return vID13StringArray;
	}

	public void setVID13StringArray(String[] intArray) {
		vID13StringArray = intArray;
	}

	public int getVID13StringArrayUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			vID13StringArrayUpdateCount++;
		}
		return vID13StringArrayUpdateCount;
	}

	public void setVID13StringArrayUpdateCount(int intArrayUpdateCount) {
		vID13StringArrayUpdateCount = intArrayUpdateCount;
	}

	public int[] getVID14IntArray() {
		return vID14IntArray;
	}

	public void setVID14IntArray(int[] intArray) {
		vID14IntArray = intArray;
	}

	public int getVID14IntArrayUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			vID14IntArrayUpdateCount++;
		}
		return vID14IntArrayUpdateCount;
	}

	public void setVID14IntArrayUpdateCount(int intArrayUpdateCount) {
		vID14IntArrayUpdateCount = intArrayUpdateCount;
	}

	public int[] getVID15IntArray() {
		return vID15IntArray;
	}

	public void setVID15IntArray(int[] intArray) {
		vID15IntArray = intArray;
	}

	public int getVID15IntArrayUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			vID15IntArrayUpdateCount++;
		}
		return vID15IntArrayUpdateCount;
	}

	public void setVID15IntArrayUpdateCount(int intArrayUpdateCount) {
		vID15IntArrayUpdateCount = intArrayUpdateCount;
	}

	public int[] getVID16IntArray() {
		return vID16IntArray;
	}

	public void setVID16IntArray(int[] intArray) {
		vID16IntArray = intArray;
	}

	public int getVID16IntArrayUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			vID16IntArrayUpdateCount++;
		}
		return vID16IntArrayUpdateCount;
	}

	public void setVID16IntArrayUpdateCount(int intArrayUpdateCount) {
		vID16IntArrayUpdateCount = intArrayUpdateCount;
	}

	public byte getVID17() {
		return vID17;
	}

	public byte[] setVID17(byte vid17, boolean bUpdate) throws IOException{
		vID17 = vid17;
		if (bUpdate) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_MSCO, (byte)7, (short)1, (short)17, this, vID17);
		} else {
			return null;
		}
	}

	public float getVID18() {
		return vID18;
	}

	public byte[] setVID18(float vid18, boolean bUpdate) throws IOException{
		vID18 = vid18;
		if (bUpdate) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_MSCO, (byte)7, (short)1, (short)18, this, vID18);
		} else {
			return null;
		}
		
	}

	public int[] getVID19IntArray() {
		return vID19IntArray;
	}

	public void setVID19IntArray(int[] intArray) {
		vID19IntArray = intArray;
	}

	public int getVID19IntArrayUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			vID19IntArrayUpdateCount++;
		}
		return vID19IntArrayUpdateCount;
	}

	public void setVID19IntArrayUpdateCount(int intArrayUpdateCount) {
		vID19IntArrayUpdateCount = intArrayUpdateCount;
	}

	public byte getVID20() {
		return vID20;
	}

	public byte[] setVID20(byte vid20, boolean bUpdate) throws IOException {
		vID20 = vid20;
		if (bUpdate) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_MSCO, (byte)7, (short)1, (short)0x14, this, vID20);
		} else {
			return null;
		}
	}

	public ManufacturingSchematic(ItemTemplate template) {
		setSTFFileName(stfFileName);
		setSTFFileIdentifier(stfFileIdentifier);
		try {
			setCustomName(template.getIFFFileName(), false);
		} catch (IOException e) {
			// Can't happen here -- We're not actually building the packet.
		}
		setIFFFileName(template.getIFFFileName());
		vSchematicAttributes = new Vector<ManufacturingSchematicAttribute>();
	}
	
	protected float getBaseCraftingComplexity() {
		return fBaseCraftingComplexity;
	}
	protected void setBaseCraftingComplexity(float baseCraftingComplexity) {
		fBaseCraftingComplexity = baseCraftingComplexity;
	}
	protected float getCurrentCraftingComplexity() {
		return fCurrentCraftingComplexity;
	}
	protected void setCurrentCraftingComplexity(float currentCraftingComplexity) {
		fCurrentCraftingComplexity = currentCraftingComplexity;
	}
	protected String getCrafterName() {
		return sCrafterName;
	}
	protected void setCrafterName(String crafterName) {
		sCrafterName = crafterName;
	}

	protected void setCraftingSchematic(CraftingSchematic schem) {
		cSchematic = schem;
		Vector<CraftingSchematicComponent> vComponentList = schem.getComponents();
		CraftingSchematicComponent[] vComponentArray = null;
		if (vComponentList != null) {
			vComponentArray = new CraftingSchematicComponent[vComponentList.size()];
			for (int i = 0; i < vComponentList.size(); i++) {
				vComponentArray[i] = vComponentList.elementAt(i);
			}
		}
		setSchematicComponentData(vComponentArray);
		setExperimentalAttributes(schem.getAttributes());
		// Temporary code used to fill out the experimental data.
		vID13StringArray = null;
		vID14IntArray = null;
		vID15IntArray = null;
		vID16IntArray = null;
		vID17 = 0;
		vID18 = 0;
		vID19IntArray = null;
		vID20 = 1;
	}
	
	protected CraftingSchematic getCraftingSchematic() {
		return cSchematic;
	}

	public byte getIngredientUpdateCounter(boolean bUpdate) {
		if (bUpdate) {
			iIngredientUpdateCounter++;
		}
		return iIngredientUpdateCounter;
	}

	public byte[] setIngredientUpdateCounter(byte newValue, boolean bUpdate) throws IOException {
		iIngredientUpdateCounter = newValue;
		if (bUpdate) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_MSCO, (byte)6, (short)1, (short)5, this, iIngredientUpdateCounter);
		}
		return null;
	}

	public void setHasSentFirstBaseline7AddIngredientDelta(boolean bHasSentFirstBaseline7Delta) {
		this.bHasSentFirstBaseline7DeltaStageAddIngredient = bHasSentFirstBaseline7Delta;
	}


	public boolean getHasSentFirstBaseline7AddIngredientDelta() {
		return bHasSentFirstBaseline7DeltaStageAddIngredient;
	}

	public void setSchematicComponentData(CraftingSchematicComponent[] schematicComponentData) {
		int arrayLengths = 0;
		if (schematicComponentData != null) {
			arrayLengths = schematicComponentData.length;
		}
		this.schematicComponentData = schematicComponentData;
		vID1IntArray = new int[arrayLengths];
		vObjectIDBySlot = new long[arrayLengths][];
		lComponentSerials = new long[arrayLengths];
		vSceneObjectIDBySlot = new long[arrayLengths][];
		vSlotResourceQuantityInserted = new int[arrayLengths];
		vID4IntArray = new int[arrayLengths];
		vID5IntArray = new int[arrayLengths];
		vID6IntArray = new int[arrayLengths];
		
		for (int i = 0; i < arrayLengths; i++) {
			vObjectIDBySlot[i] = new long[10];
			vSceneObjectIDBySlot[i] = new long[10];
			vID5IntArray[i] = -1;
			vID6IntArray[i] = i;
		}
	}

	public CraftingSchematicComponent[] getSchematicComponentData() {
		return schematicComponentData;
	}

	public void setSchematicComponentDataUpdateCount(
			int iSchematicComponentDataUpdateCount) {
		this.iSchematicComponentDataUpdateCount = iSchematicComponentDataUpdateCount;
	}

	public int getSchematicComponentDataUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			iSchematicComponentDataUpdateCount++;
		}
		return iSchematicComponentDataUpdateCount;
	}
	
	public byte[] updateVID1ValueAtIndex(int index, int newStatus, boolean bUpdate) throws IOException{
		vID1IntArray[index] = newStatus;
		if (bUpdate) {
			return PacketFactory.buildDeltasMessageMSCO7IntArray((short)1, (short)1, this, (short)index, newStatus, Constants.DELTA_UPDATING_ITEM);
		} 
		return null;
		
	}
	
	// TODO:  Update code to use decrementing for loop a la WaterStealers
	public byte[] addIngredientToSlotIngredientIDList(int slot, SOEObject object, boolean bUpdate) throws IOException {
		boolean bFound = false;
		for (int i = 0; i < vObjectIDBySlot[slot].length && !bFound; i++) {
			if (vObjectIDBySlot[slot][i] == 0) {
				vObjectIDBySlot[slot][i] = object.getID();
				bFound = true;
			}
		}
		if (object instanceof ResourceContainer) {
			System.out.println("Add resource container to crafting page -- insert it's resource spawn ID, NOT it's object ID.");
			ResourceContainer container = (ResourceContainer)object;
			lComponentSerials[slot] = container.getResourceSpawnID();
			System.out.println("Resource container ID: " + container.getID() +", spawn ID: " + container.getResourceSpawnID());
		} else {
			lComponentSerials[slot] = object.getSerialNumber();
		}
		if (bUpdate) {
			 
			return PacketFactory.buildDeltasMSCO7ListObjectIDInSlot(this, slot, vObjectIDBySlot[slot]);
		}
		return null;
		
	}
	
	public void addIngredientToSceneIngredientIDList(int slot, long objectID) {
		boolean bFound = false;
		for (int i = 0; i < vObjectIDBySlot[slot].length && !bFound; i++) {
			if (vSceneObjectIDBySlot[slot][i] == 0) {
				vSceneObjectIDBySlot[slot][i] = objectID;
				bFound = true;
			}
		}
	}

	public long[] getAllSceneObjectIDsInSlot(int slotID) {
		return vSceneObjectIDBySlot[slotID];
	}
	
	// TODO:  Update code to use decrementing for loop a la WaterStealers
	public byte[] removeIngredientsFromSlotIngredientIDList(int slot, boolean bUpdate) throws IOException {
		vObjectIDBySlot[slot] = new long[10];
		if (bUpdate) {
			return PacketFactory.buildDeltasMSCO7ListObjectIDInSlot(this, slot, vObjectIDBySlot[slot]);
		}
		return null;

	}
	
	public void removeIngredientFromSceneIngredientIDList(int slot) throws IOException {
		vSceneObjectIDBySlot[slot] = new long[10];
	}
	
	
	public void setItemBeingCrafted(TangibleItem itemBeingCrafted) {
		this.itemBeingCrafted = itemBeingCrafted;
	}

	public TangibleItem getItemBeingCrafted() {
		return itemBeingCrafted;
	}
	
	public byte[] updateItemQuantityInIngredientSlot(int slotID, int newQuantity, boolean bUpdate) throws IOException{
		vSlotResourceQuantityInserted[slotID] = newQuantity;
		if (bUpdate) {
			return PacketFactory.buildDeltasMSCO7IngredientQuantityInSlot(this, (short)slotID, newQuantity, (byte)2);
			//return PacketFactory.buildDeltasMessageMSCO7IntArray((short)1, (short)3, this, (short)slotID, newQuantity, (byte)2);
		} else {
			return null;
		}
	}
	
	public byte[] updateVID5ValueAtIndex(int index, int newStatus, boolean bUpdate) throws IOException{
		vID5IntArray[index] = newStatus;
		if (bUpdate) {
			return PacketFactory.buildDeltasMessageMSCO7IntArray((short)1, (short)5, this, (short)index, newStatus, Constants.DELTA_UPDATING_ITEM);
		} else {
			return null;
		}
	}

	// For every experimental attribute we have on this schematic, we need to go through and get the maximum experimental value. -- Done
	// To do this, we go through every attribute and find it's experiemtal weights. -- Done
	// We multiply the appropriate quality, on every resource used, by this weight.  So, OQ 500 with a weight of 50% gives us a value of 0.25 as the max. 
	// We then multiply that value by the coefficient of the number of units of "this" resource over the number of units of resources in the schematic.  
	// Add all those up, and you get your max experimentation value for this attribute.
	
	protected int setExperimentalValues(ZoneClient client) {
		// For each experiemental value, find the weight it has by percentage.
		Vector<Integer> requisiteSkills = cSchematic.getRequiredSkillID();
		boolean bHasAnyRequiredSkill = false;
		Player player = client.getPlayer(); 
		Skills theSkill = null;
		Vector<SkillMods> allSkillModsThisSkill = null;
		if (requisiteSkills.isEmpty()) {
			// No required skill
			System.out.println("No skill required to craft this object: " + itemBeingCrafted.getIFFFileName());
		} else {
			for (int i = 0; i < requisiteSkills.size() && !bHasAnyRequiredSkill; i++) {
				int iSkillID = requisiteSkills.elementAt(i);
				if (player.hasSkill(iSkillID)) {
					theSkill = client.getServer().getSkillFromIndex(iSkillID);
					Skills noviceSkill = client.getServer().getSkillFromIndex(theSkill.getNoviceSkillID());
					allSkillModsThisSkill = noviceSkill.getAllSkillMods();
					bHasAnyRequiredSkill = true; // Short-circuit the loop.
				}
			}
		}
		if (!bHasAnyRequiredSkill) {
			System.out.println("setExperimentalValues -- possible error -- crafting Player does not have requisite skill to craft this object, or requisite skill ID = -1");
		}
		SkillMods requiredSkillMod = null;
		for (int i = 0; i < allSkillModsThisSkill.size() && requiredSkillMod == null; i++) {
			SkillMods tempMod = allSkillModsThisSkill.elementAt(i);
			if (tempMod.getName().contains("assembly")) {
				//System.out.println("Found skill mod " + tempMod.sName);
				requiredSkillMod = tempMod;
			}
		}
		
		try {
		
			if (requiredSkillMod == null) {
				//System.out.println("No existant skill mod for this assembly attempt -- cannot craft this item.  We should never be here.");
				client.insertPacket(PacketFactory.buildChatSystemMessage("An error has occured in the server while processing your assembly attempt."));
				client.insertPacket(PacketFactory.buildChatSystemMessage("Please cancel your crafting session and try again."));
				return Constants.CRAFTING_ASSEMBLY_INTERNAL_FAILURE;
			}
			
			// Find this skill mod in the Player, and get the Player's current level for it.
			SkillMods playerMod = client.getPlayer().getSkillMod(requiredSkillMod.getName());
			//System.out.println("Found skill mod " + playerMod.sName + " with current value " + playerMod.iSkillModModdedValue);
			
			int successRatingRoll = SWGGui.getRandomInt(0, 150); // TODO -- This should be weighted by player skill.
			//System.out.println("Success rating rolled was " + successRatingRoll);
			int successRating;
			
			if (successRatingRoll == 0) {
				successRating = Constants.CRAFTING_ASSEMBLY_AMAZING_SUCCESS;
			} else if (successRatingRoll == 150) {
				successRating = Constants.CRAFTING_ASSEMBLY_CRITICAL_FAILURE;
			} else {
				// Get a range of values based on the assembly skill mod.
				// We have 148 possible values.
				// Anything rolled between 1 and the skill mod will be a Great Success.
				// Anything else will not be -- calculate a range in the remaining space for less than greats.
				// Find where the roll falls in that range, and set it appropriately.
				if (successRatingRoll <= requiredSkillMod.getSkillModModdedValue()) {
					successRating = Constants.CRAFTING_ASSEMBLY_GREAT_SUCCESS;
				} else {
					// Calculate the ranges.
					int range = (148 - playerMod.getSkillModModdedValue()) / 6;
					int timesDivided = successRatingRoll / range;
					successRating = Math.max(Constants.CRAFTING_ASSEMBLY_GOOD_SUCCESS, timesDivided);
					successRating = Math.min(successRating, Constants.CRAFTING_ASSEMBLY_BARE_SUCCESS);
				}
			}
			//System.out.println("Assembly roll -- success rating = " + successRating);
			// At this stage successRating must be initialized.

			if (successRating == Constants.CRAFTING_ASSEMBLY_CRITICAL_FAILURE) {
				return successRating;
			}
			ResourceManager manager = client.getServer().getResourceManager();
			// What's the total amount of resources we're putting in here?
			int totalQuantityAllSlots = 0;
			for (int k = 0; k < schematicComponentData.length; k++) {
				totalQuantityAllSlots += schematicComponentData[k].getComponentQuantity();
			}
			
			vID12MaxExperimentationValue = new float[vExperimentalAttributes.length];
			// For each attribute
			
			for (int i = 0; i < vExperimentalAttributes.length; i++) { 
				
				//float maximumPossibleExperimentationValuePercent = 0;
				
				CraftingExperimentationAttribute component = vExperimentalAttributes[i];
				
				byte[] experimentalWeightsThisComponent = component.getWeightAndTypeBitmask();
				int[] weightTypes = new int[experimentalWeightsThisComponent.length];
			
				// For each weight
				for (int j = 0; j < experimentalWeightsThisComponent.length; j++) {
					weightTypes[j] = (experimentalWeightsThisComponent[j] & 0xF0);
					float[] effectivePercentages = findEffectivePercentages(experimentalWeightsThisComponent);
					if (effectivePercentages != null) {
						
						// Gets the acutal max that the attribute can be experimented up to, and sticks it into the vID12 float array.
		
						// Now, go through each resource inserted, and find it's quantity and quality.
						for (int k = 0; k < vObjectIDBySlot.length; k++) {
							float effectivePercentageThisSlot = 0;
							int quantity = vSlotResourceQuantityInserted[k];
							float quantityModifier = (float)quantity / (float)totalQuantityAllSlots;
		
							SpawnedResourceData theResource = manager.getResourceByID(vObjectIDBySlot[k][0]);
							if (theResource != null) {
								//float maxExperimentationThisObject = 0;
								int resourceQuality = 0;
								switch(weightTypes[j]) {
								// We'll use you as entangle resistance.
									case Constants.RESOURCE_WEIGHT_POTENCY: {
										resourceQuality = theResource.getEntangleResistance();
										break;
									}
									case Constants.RESOURCE_WEIGHT_BULK: {
										// We won't use you.
										break;
									}
									case Constants.RESOURCE_WEIGHT_COLD_RESISTANCE: {
										resourceQuality = theResource.getColdResistance();
										break;
									}
									case Constants.RESOURCE_WEIGHT_CONDUCTIVITY: {
										resourceQuality = theResource.getConductivity();
										break;
									}
									case Constants.RESOURCE_WEIGHT_DECAY_RESISTANCE: {
										resourceQuality = theResource.getDecayResistance();
										break;
									}
									case Constants.RESOURCE_WEIGHT_FLAVOR: {
										resourceQuality = theResource.getFlavor();
										break;
									}
									case Constants.RESOURCE_WEIGHT_HEAT_RESISTANCE: {
										resourceQuality = theResource.getHeatResistance();
										break;
									}
									case Constants.RESOURCE_WEIGHT_MALLEABILITY: {
										resourceQuality = theResource.getMalleability();
										break;
									}
									case Constants.RESOURCE_WEIGHT_OVERALL_QUALITY: {
										resourceQuality = theResource.getOverallQuality();
										break;
									}
									case Constants.RESOURCE_WEIGHT_POTENTIAL_ENERGY: {
										resourceQuality = theResource.getPotentialEnergy();
										break;
									}
									case Constants.RESOURCE_WEIGHT_SHOCK_RESISTANCE: {
										resourceQuality = theResource.getShockResistance();
										break;
									}
									case Constants.RESOURCE_WEIGHT_UNIT_TOUGHNESS: {
										resourceQuality = theResource.getUnitToughness();
										break;
									}
									default: {
										//System.out.println("Unknown resource weight type: " + weightTypes[k]);
										
									}
								
								}
								// We have it's quality -- multiply that by the percentage effect, and by the quantity of this resource / the total quantity of resources.
								// That will give us the experimental effect, and the impact, of this resource on the item being crafted.
								// Quality of resource = 362, effective percentage = 0.5, quantity = 0.5
								// ADD to experimental value:  362 * .25
								effectivePercentageThisSlot = resourceQuality * effectivePercentages[j] * quantityModifier;
							}
							vID12MaxExperimentationValue[i] += effectivePercentageThisSlot;
							// When all is said and done, this value must be a number between 0 and 1.
						}
					}  else {
						vID12MaxExperimentationValue[i] = 0;
					}
				}
			}
			float multiplier = 0;
			switch (successRating) {
				case Constants.CRAFTING_ASSEMBLY_AMAZING_SUCCESS: {
					multiplier = 0.4f;
					break;
				}
				case Constants.CRAFTING_ASSEMBLY_GREAT_SUCCESS: {
					multiplier = 0.3f;
					break;
				}
				case Constants.CRAFTING_ASSEMBLY_GOOD_SUCCESS: {
					multiplier = 0.2f;
					break;
				}
				case Constants.CRAFTING_ASSEMBLY_MODERATE_SUCCESS: {
					multiplier = 0.1f;
					break;
				}
				case Constants.CRAFTING_ASSEMBLY_SUCCESS: {
					multiplier = 0.08f;
					break;
				}
				case Constants.CRAFTING_ASSEMBLY_MARGINAL_SUCCESS: {
					multiplier = 0.06f;
					break;
				}
				case Constants.CRAFTING_ASSEMBLY_OK: {
					multiplier = 0.04f;
					break;
				}
				case Constants.CRAFTING_ASSEMBLY_BARE_SUCCESS: {
					multiplier = 0.02f;
					break;
				}
				default: {
					multiplier = 0;
				}
			}
			
			for (int i = 0; i < vID12MaxExperimentationValue.length; i++) {
				// Divide by 1000, to get our number between 0 and 1.
				
				vID12MaxExperimentationValue[i] = vID12MaxExperimentationValue[i] * 0.001f; // Divide by 100, to make this a "percentage"

				vID9CurrentExperimentationValueFloatArray[i] = vID12MaxExperimentationValue[i] * multiplier; // Current one is based on assembly success rating.  
			}
			return successRating;
		} catch (Exception e) {
			return Constants.CRAFTING_ASSEMBLY_INTERNAL_FAILURE;
		}
	}
				
	private float[] findEffectivePercentages(byte[] weights) {
		float[] iPercentagesToReturn = null;
		if (weights != null) {
			int length = weights.length;
			iPercentagesToReturn = new float[length];
			switch(length) {
				case 0: {
					// This should never happen.
					break;
				}
				case 1: {
					if ((weights[0] & 0x0F) > 0) {
						iPercentagesToReturn[0] = 1.0f;
					} else {
						iPercentagesToReturn[0] = 0;
					}
					break;
				}
				case 2: {
					// 50/50
					// How can this be out of bounds if the length is 2???
					if (((weights[0] & 0xF) == 1) && ((weights[1] & 0xF) == 1)) {
						iPercentagesToReturn[0] = 0.50f;
						iPercentagesToReturn[1] = 0.50f;
					}
					// 66/33
					else if (((weights[0] & 0xF) == 2) && ((weights[1] & 0xF) == 1))  {
						iPercentagesToReturn[0] = 0.67f;
						iPercentagesToReturn[1] = 0.33f;
					}
					// 33/66
					else if (((weights[1] & 0xF) == 2) && ((weights[0] & 0xF) == 1)) {
						iPercentagesToReturn[0] = 0.33f;
						iPercentagesToReturn[1] = 0.67f;
					}
					// 75/25
					else if (((weights[0] & 0xF) == 3) && ((weights[1] & 0xF) == 1)) {
						iPercentagesToReturn[0] = 0.75f;
						iPercentagesToReturn[1] = 0.25f;
					}
					// 25/75
					else if (((weights[1] & 0xF) == 3) && ((weights[0] & 0xF) == 1)) {
						iPercentagesToReturn[1] = 0.75f;
						iPercentagesToReturn[0] = 0.25f;
					}
					// 80/20
					else if (((weights[0] & 0xF) == 4) && ((weights[1] & 0xF) == 1)) {
						iPercentagesToReturn[0] = 0.80f;
						iPercentagesToReturn[1] = 0.20f;
					}
					// 20/80
					else if (((weights[1] & 0xF) == 4) && ((weights[0] & 0xF) == 1)) {
						iPercentagesToReturn[0] = 0.20f;
						iPercentagesToReturn[1] = 0.80f;
					} else {
						// Whacky.
						
						iPercentagesToReturn[0] = 1.0f;
						iPercentagesToReturn[1] = 0;
					}
					break;
				}
				case 3: {
					// 33/33/33
					if (((weights[0] & 0xF) == 1) && ((weights[1] & 0xF) == 1) && ((weights[2] & 0xF) == 1)) {
						iPercentagesToReturn[0] = 0.34f;
						iPercentagesToReturn[1] = 0.33f;
						iPercentagesToReturn[2] = 0.33f;
					}
					// 50/25/25
					else if (((weights[0] & 0xF) == 2) && ((weights[1] & 0xF) == 1) && ((weights[2] & 0xF) == 1)) {
						iPercentagesToReturn[0] = 0.50f;
						iPercentagesToReturn[1] = 0.25f;
						iPercentagesToReturn[2] = 0.25f;
					}
					// 25/50/25
					else if (((weights[0] & 0xF) == 1) && ((weights[1] & 0xF) == 2) && ((weights[2] & 0xF) == 1)) {
						iPercentagesToReturn[0] = 0.25f;
						iPercentagesToReturn[1] = 0.50f;
						iPercentagesToReturn[2] = 0.25f;
					}
					// 25/25/50
					else if (((weights[0] & 0xF) == 1) && ((weights[1] & 0xF) == 1) && ((weights[2] & 0xF) == 2)) {
						iPercentagesToReturn[0] = 0.25f;
						iPercentagesToReturn[1] = 0.25f;
						iPercentagesToReturn[2] = 0.50f;
					}
					// Whacky.
					else {
						
						iPercentagesToReturn[0] = 1.0f;
						iPercentagesToReturn[1] = 0;
						iPercentagesToReturn[2] = 0;
					}
					break;
				}
				default: {
					//System.out.println("Unusually large number of resource weights: " + length);
				}
			}
		}
		return iPercentagesToReturn;
	}
	
	public byte[] setCustomName(String sName, boolean bUpdate) throws IOException {
		System.out.println("ManufacturingSchematic:  SetCustomName.  New name: " + sName);
		super.setCustomName(sName, false);
		if (bUpdate) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_MSCO,(byte)3, (short)1, (short)2, this, sName, true);
		}
		return null;
	}

	public void setToolUsedToCraft(CraftingTool toolUsedToCraft) {
		this.toolUsedToCraft = toolUsedToCraft;
	}

	public CraftingTool getToolUsedToCraft() {
		return toolUsedToCraft;
	}

	public void setNumberOfFactoryItems(int iNumberOfFactoryItems) {
		this.iNumberOfFactoryItems = iNumberOfFactoryItems;
	}

	public int getNumberOfFactoryItems() {
		return iNumberOfFactoryItems;
	}

	
	public void setSerialNumber(long lSerialNumber) {
		this.lSerialNumber = lSerialNumber;
		addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_SERIAL_NUMBER, Long.toHexString(lSerialNumber)));
	}

	public long getSerialNumber() {
		return lSerialNumber;
	}

	public void setCanRecoverInstalledItems(boolean bCanRecoverInstalledItems) {
		this.bCanRecoverInstalledItems = bCanRecoverInstalledItems;
	}

	public boolean getCanRecoverInstalledItems() {
		return bCanRecoverInstalledItems;
	}

	public long[] getComponentSerials() {
		return lComponentSerials;
	}
	
	public void setComponentSerial(long serial, int index) {
		lComponentSerials[index] = serial;
	}
}
