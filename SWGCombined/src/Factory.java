import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public class Factory extends Structure {
	public final static long serialVersionUID = 1l;
	private Vector<Long> vHopperAccessList;
	private ManufacturingSchematic currentSchematic;
	private TangibleItem tInputHopper;
	private TangibleItem tOutputHopper;
	private boolean bIsRunning;
	private long lTimeToNextObjectCreationMS;
	private FactoryCrate lastItemCreated;
	private Hashtable<Character, RadialMenuItem> vRadialsForAdmin;
	private Hashtable<Character, RadialMenuItem> vRadialsForNonAdmin;
	private byte iFactoryType = -1;

	public Factory() {
		// TODO Auto-generated constructor stub
		super();
	}

	public Factory(String name, long deedID) {
		super(name, deedID);
		// TODO Auto-generated constructor stub
	}

	public Factory(int deedTemplateID, long deedID, Deed d, float x, float y,
			float z, int planetid, float ow, float os, int facingDirection,
			String ownerName, long structureOwnerID, ZoneServer server) throws IOException {
		super(deedTemplateID, deedID, d, x, y, z, planetid, ow, os,
				facingDirection, ownerName, structureOwnerID, server);
		// TODO Auto-generated constructor stub
		setServer(server);
		initializeHoppers();
	}

	protected void initializeHoppers() {
		try {
			tInputHopper = new TangibleItem();
			tOutputHopper = new TangibleItem();
			ItemTemplate inputHopperTemplate = DatabaseInterface
					.getTemplateDataByCRC(0xFE4DA94B);
			ItemTemplate outputHopperTemplate = DatabaseInterface
					.getTemplateDataByCRC(0xE06B113D);
			tInputHopper.setSTFFileName(inputHopperTemplate
					.getSTFFileName());
			tInputHopper.setTemplateID(inputHopperTemplate.getTemplateID());
			tInputHopper.setIFFFileName(inputHopperTemplate.getIFFFileName());
			tInputHopper
					.setSTFFileIdentifier(inputHopperTemplate
							.getSTFFileIdentifier());
			tInputHopper.setName("Input Hopper", false);
			tInputHopper.setCustomizationData(null);
			// long playerID = p.getID();
			tInputHopper.setOwnerID(getID());
			tInputHopper.setEquipped(this, 4);
			tInputHopper.setID(getServer().getNextObjectID());
			tInputHopper.setConditionDamage(0, false);
			tInputHopper.setMaxCondition(1, false);
			getServer().addObjectToAllObjects(tInputHopper, false, false);
			tInputHopper.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);

			tOutputHopper.setSTFFileName(outputHopperTemplate
					.getSTFFileName());
			tOutputHopper.setTemplateID(outputHopperTemplate.getTemplateID());
			tOutputHopper.setIFFFileName(outputHopperTemplate.getIFFFileName());
			tOutputHopper
					.setSTFFileIdentifier(outputHopperTemplate
							.getSTFFileIdentifier());
			tOutputHopper.setName("Output Hopper", false);
			tOutputHopper.setCustomizationData(null);
			// long playerID = p.getID();
			tOutputHopper.setOwnerID(getID());
			tOutputHopper.setEquipped(this, 4);
			tOutputHopper.setID(getServer().getNextObjectID());
			tOutputHopper.setConditionDamage(0, false);
			tOutputHopper.setMaxCondition(1, false);
			getServer().addObjectToAllObjects(tOutputHopper, false, false);
			tOutputHopper.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);
			vHopperAccessList = new Vector<Long>();
		} catch (Exception e) {
			System.out
					.println("Error initializing input / output hoppers for factory: "
							+ e.toString());
			e.printStackTrace();
		}

	}

	public void update(long lElapsedTimeMS, ZoneServer server) {
		super.update(lElapsedTimeMS, server);
		if (bIsRunning) {
			lTimeToNextObjectCreationMS -= lElapsedTimeMS;
			if (lTimeToNextObjectCreationMS <= 0) {
				manufactureObject();
			}
		}
	}

	/**
	 * Gets the default Radial Menu Items for this object. These radials have to
	 * account for different conditions. i/e equipped, not used , used, dead,
	 * incapped, opened , closed, etc, etc...
	 * 
	 * @return The Radial Items.
	 */
	public Hashtable<Character, RadialMenuItem> getRadialMenus(ZoneClient c) {
		Player player = c.getPlayer();
		if (vRadialsForAdmin == null) {
			vRadialsForAdmin = new Hashtable<Character, RadialMenuItem>();
			vRadialsForNonAdmin = new Hashtable<Character, RadialMenuItem>();
			Vector<RadialMenuItem> vRadial = c.getServer().getRadialMenusByCRC(
					getCRC());
			for (int i = 0; i < vRadial.size(); i++) {
				RadialMenuItem item = vRadial.elementAt(i);
				if (item.getActionLocation() == 1) {
					vRadialsForNonAdmin.put(item.getCommandID(), item);
				}
				boolean bUsingCommand = true;
				Character commandID = item.getCommandID();
				if (bIsRunning) {
					bUsingCommand = (commandID != 195 && commandID != 197);
					if (commandID == 77) {
						item.setButtonText("@manf_station:deactivate");
					}
				} else {
					if (commandID == 77) {
						if (currentSchematic != null) {
							item.setButtonText("@manf_station:activate");
						} else {
							bUsingCommand = false;
						}
					}
				}
				if (bUsingCommand) {
					vRadialsForAdmin.put(item.getCommandID(), item);
				}

			}
		}
		if (isAdmin(player.getID())) {
			return vRadialsForAdmin;
		}
		return vRadialsForNonAdmin;
	}

	protected void useItemByCommandID(ZoneClient client, byte commandID) {

		try {
			if (iFactoryType == -1) {
				client
						.insertPacket(PacketFactory
								.buildChatSystemMessage("Error:  Unknown factory type!"));
				return;
			}
			Player player = client.getPlayer();
			Vector<TangibleItem> vInputHopper = tInputHopper.getLinkedObjects();
			Vector<TangibleItem> vOutputHopper = tOutputHopper
					.getLinkedObjects();
			switch (commandID) {
			case Constants.RADIAL_MENU_EXAMINE: {
				client.insertPacket(PacketFactory.buildAttributeListMessage(
						client, this)); // Just in case.
				break;
			}
			case Constants.RADIAL_MENU_MANAGE_STRUCTURE: {
				// This is a "root" menu and is not selectable.
				break;
			}
			case Constants.RADIAL_MENU_ADMIN_SERVER_MENU3: {// Manage power.
				if (isAdmin(player.getID())) {
					int iPowerOnHand = 0;
					Vector<ResourceContainer> vRCList = new Vector<ResourceContainer>();
					for (int i = 0; i < client.getPlayer().getInventoryItems()
							.size(); i++) {
						TangibleItem o = client.getPlayer().getInventoryItems()
								.get(i);
						if (o instanceof ResourceContainer) {
							vRCList.add((ResourceContainer) o);
						}
					}
					int iPowerCount = 0;
					if (vRCList.isEmpty()) {
						client
								.insertPacket(PacketFactory
										.buildChatSystemMessage("You do not have any power in your inventory."));
					} else {

						for (int i = 0; i < vRCList.size(); i++) {
							ResourceContainer r = vRCList.get(i);
							SpawnedResourceData rd = getServer()
									.getResourceManager().getResourceByID(
											r.getResourceSpawnID());
							// System.out.println("Resource in inventory Type: "
							// + rd.getResourceType());
							if (rd.getResourceType().contains("Radioactive")) {
								if (rd.getPotentialEnergy() >= 650) {
									iPowerOnHand += (r.getStackQuantity() * 5) / 2;
								} else {
									iPowerOnHand += (r.getStackQuantity() * 195) / 100;
								}
								iPowerCount++;
							} else if (rd.getResourceType().contains("Solar")) {
								if (rd.getPotentialEnergy() >= 950) {
									iPowerOnHand += (r.getStackQuantity() * 3) / 2;
								} else {
									iPowerOnHand += r.getStackQuantity();
								}
								iPowerCount++;
							} else if (rd.getResourceType().contains("Wind")) {
								iPowerOnHand += r.getStackQuantity();
								iPowerCount++;
							}
						}
					}
					// client.insertPacket(PacketFactory.buildChatSystemMessage("You have "
					// + iPowerCount + " power source in your inventory."));
					SUIWindow w = new SUIWindow(player);
					w.setWindowType(Constants.SUI_STRUCTURE_PAY_POWER);
					w.setOriginatingObject(this);
					String sTransferBoxPrompt = "Select the amount of power to deposit"; // .\r\n\r\nCurrent
																							// Power
																							// Reserves
																							// at:
																							// "
																							// +
																							// this.getIPowerPool();
					String sFromLabel = "Total Power";
					String sToLabel = "To Deposit";
					int iFromAmount = iPowerOnHand;
					int iToAmount = 0;// <--always 0 because we do not allow for
										// power to be retrieved.
					int iConversionRatioFrom = 1;
					int iConversionRatioTo = 1;
					client.insertPacket(w.SUIScriptTransferBox(client,
							sToLabel, sTransferBoxPrompt, sTransferBoxPrompt,
							sFromLabel, sToLabel, iFromAmount, iToAmount,
							iConversionRatioFrom, iConversionRatioTo));

				} else {
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"player_structure", "not_admin"));
				}
				break;
			}
			case Constants.RADIAL_MENU_SET_ITEM_NAME: {
				// Rename the item.
				if (getStructureOwnerID() == player.getID()) {
					SUIWindow w = new SUIWindow(player);
					w.setWindowType(Constants.SUI_RENAME_STRUCTURE);
					w.setOriginatingObject(this);
					String sCurrentName = this.getStructureName();
					client.insertPacket(w.SUIScriptTextInputBox(client,
							"handleFilterInput",
							"@player_structure:structure_name_prompt",
							"@player_structure:management_name_structure",
							true, "Enabled", "Visible", 127, sCurrentName));
				} else {
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"player_structure", "not_admin"));

				}

				break;
			}
			case (byte) Constants.RADIAL_MENU_PAY_MAINTENANCE: {
				// Pay maintenance.
				if (isAdmin(player.getID())) {
					SUIWindow w = new SUIWindow(player);
					w.setWindowType(Constants.SUI_STRUCTURE_PAY_MAINTENANCE);
					w.setOriginatingObject(this);
					String sTransferBoxPrompt = "@player_structure:give_maintenance";
					String sFromLabel = "@player_structure:total_funds";
					String sToLabel = "@player_structure:to_pay";
					int iFromAmount = client.getPlayer().getCashOnHand();
					int iToAmount = 0; // sent in as 0 cause you cannot withdraw
										// from a structure.
					int iConversionRatioFrom = 1;
					int iConversionRatioTo = 1;
					client.insertPacket(w.SUIScriptTransferBox(client,
							sToLabel, sTransferBoxPrompt, sTransferBoxPrompt,
							sFromLabel, sToLabel, iFromAmount, iToAmount,
							iConversionRatioFrom, iConversionRatioTo));
				} else {
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"player_structure", "not_admin"));
				}

				break;
			}
			case Constants.RADIAL_MENU_OPEN_STATUS_WINDOW: {
				// Open the status window.
				Player owner = (Player) client.getServer()
						.getObjectFromAllObjects(this.getStructureOwnerID());
				if (this.isAdmin(client.getPlayer().getID())) {
					SUIWindow w = new SUIWindow(player);
					w.setWindowType(Constants.SUI_STRUCTURE_SHOW_STATUS);
					w.setOriginatingObject(this);
					String WindowTypeString = "handleSUI";
					String DataListTitle = "@player_structure:structure_status_t";
					String DataListPrompt = "@player_structure:structure_name_prompt "
							+ this.getStructureName();
					String sList[] = new String[7];
					sList[0] = "@player_structure:owner_prompt "
							+ owner.getFullName(); // owner
					sList[1] = "@player_structure:structure_status_t "
							+ this.getCurrentStructureStatusString(); // status
																		// private
																		// /
																		// public
					sList[2] = "@player_structure:condition_prompt "
							+ this.getCurrentConditionString();// condition
					sList[3] = "@player_structure:current_maint_pool "
							+ this.getCurrentStructureMaintenancePoolString(); // //maint
																				// pool
																				// int
																				// /days
																				// hours
					sList[4] = "@player_structure:power_reserve_prompt "
							+ this.getPowerPool() + " units";
					sList[5] = "@player_structure:maintenance_rate_prompt "
							+ this.getIMaintenanceRate() + " cr/hr";// main rate
					sList[6] = null;
					if (bIsRunning) {
						sList[6] = "@manf_station:activated";
					} else {
						sList[6] = "@manf_station:deactivated";
					}
					client.insertPacket(w.SUIScriptListBox(client,
							WindowTypeString, DataListTitle, DataListPrompt,
							sList, null, 0, 0));
				} else {
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"player_structure", "not_admin"));
				}
				break;
			}
			case (byte) Constants.RADIAL_MENU_DESTROY_STRUCTURE: {
				// Destroy and (possibly) redeed.
				if (getStructureOwnerID() == player.getID()) {
					if (vInputHopper.isEmpty() && vOutputHopper.isEmpty()
							&& !isInstallationActive()) {
						System.out.println("Structure Destruction Requested.");
						SUIWindow w = new SUIWindow(player);
						w.setWindowType(Constants.SUI_STRUCTURE_CONFIRM_REDEED);
						w.setOriginatingObject(this);
						String WindowTypeString = "handleSUI";
						String DataListTitle = this.getStructureName()
								+ "         Confirm Structure Destruction";
						String DataListPrompt = "@player_structure:confirm_destruction_d1 \r\n @player_structure:confirm_destruction_d2 \r\n @player_structure:confirm_destruction_d3a";
						DataListPrompt += " \\#00BB00 @player_structure:confirm_destruction_d3b";
						DataListPrompt += " \r\n\r\n \\#EEEEEE @player_structure:confirm_destruction_d4";

						String sCanRedeed = " \\#FF0000 @player_structure:can_redeed_no_suffix ";
						// String sColor = " \\#FF0000 ";//red
						String sWhite = " \\#EEEEEE "; // NO -- True white is
														// #FFFFFF
						if ((getConditionDamage() == 0) && (getMaintenancePool() >= 800)) {
							sCanRedeed = " \\#00BB00 @player_structure:can_redeed_yes_suffix ";
							// sColor = " \\#00BB00 ";//green
						}
						String sConditionColor = " \\#FF0000 ";
						if (getConditionDamage() == 0) {
							sConditionColor = " \\#00BB00 ";// green
						}
						String sMaintColor = " \\#FF0000 ";
						Deed d = (Deed) getServer().getObjectFromAllObjects(
								getDeedID());
						if (this.getMaintenancePool() >= d.getRedeedfee()) {
							sMaintColor = " \\#00BB00 ";
						}

						String sList[] = new String[6];
						sList[0] = "@player_structure:can_redeed_alert"
								+ sCanRedeed + sWhite;
						sList[1] = "@player_structure:redeed_condition "
								+ sConditionColor
								+ (this.getMaxCondition() - this
										.getConditionDamage())
								+ sWhite
								+ "/"
								+ sConditionColor
								+ (this.getMaxCondition());
						sList[2] = "@player_structure:redeed_maintenance "
								+ sMaintColor + this.getMaintenancePool()
								+ sWhite + "/" + sMaintColor + d.getRedeedfee()
								+ sWhite;// condition
						sList[3] = "--- Select an Option to Proceed ---";
						sList[4] = "OK To Proceed With Redeed";
						sList[5] = "Cancel To Abort Redeed";
						client.insertPacket(w.SUIScriptListBox(client,
								WindowTypeString, DataListTitle,
								DataListPrompt, sList, null, 0, 0));
					} else if (this.isInstallationActive()) {
						client.insertPacket(PacketFactory
								.buildChatSystemMessage("player_structure",
										"deactivate_factory_for_delete"));
					} else if (!vInputHopper.isEmpty()) {
						client.insertPacket(PacketFactory
								.buildChatSystemMessage("player_structure",
										"clear_input_hopper_for_delete"));
					} else if (!vOutputHopper.isEmpty()) {
						client.insertPacket(PacketFactory
								.buildChatSystemMessage("player_structure",
										"clear_output_hopper_for_delete"));
					}
				} else {
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"player_structure", "destroy_must_be_owner"));
				}

				break;
			}
			case Constants.RADIAL_MENU_STRUCTURE_SET_PERMISSIONS: {
				// Top level menu -- not selectable.
				break;
			}
			case Constants.RADIAL_MENU_MANAGE_ADMIN_RIGHTS: {
				// Manage admin.
				if (isAdmin(client.getPlayer().getID())) {
					SUIWindow w = new SUIWindow(player);
					w.setWindowType(Constants.SUI_STRUCTURE_SHOW_ADMIN_LIST);
					w.setOriginatingObject(this);
					String WindowTypeString = "handleSUI";
					String DataListTitle = "ADMIN LIST";
					String DataListPrompt = "This is a list of Admins for this Structure";
					String sList[] = new String[this.getVAdminList().size()];
					for (int i = 0; i < this.getVAdminList().size(); i++) {
						Player p = client.getServer().getPlayer(
								this.getVAdminList().get(i));
						sList[i] = p.getFullName();
					}
					client.insertPacket(w.SUIScriptListBox(client,
							WindowTypeString, DataListTitle, DataListPrompt,
							sList, null, 0, 0));
				} else {
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"player_structure", "must_be_admin"));
				}

				break;
			}
			case (byte) Constants.RADIAL_MENU_SERVER_ITEM_OPTIONS: {
				// Top level menu -- not selectable.
				break;
			}
			case (byte) Constants.RADIAL_MENU_FACTORY_OPEN_INPUT_HOPPER: {
				// Open the input hopper, spawn all items contained therein.
				// Cannot open input hopper when factory is active.
				if (bIsRunning) {
					client.insertPacket(PacketFactory.buildChatSystemMessage("Unable to open input hopper while the factory is active."));
				} else {
					if (!vInputHopper.isEmpty()) {
						for (int i = 0; i < vInputHopper.size(); i++) {
							player.spawnItem(vInputHopper.elementAt(i));
						}
					}
					client.insertPacket(PacketFactory.buildOpenContainerMessage(
							tInputHopper, -1));
				}
				break;
			}
			case (byte) Constants.RADIAL_MENU_FACTORY_OPEN_OUTPUT_HOPPER: {
				// Open the output hopper, spawn all items contained therein.
				if (bIsRunning) {
					client.insertPacket(PacketFactory.buildChatSystemMessage("Unable to open input hopper while the factory is active."));
				} else {
					if (!vOutputHopper.isEmpty()) {
						for (int i = 0; i < vOutputHopper.size(); i++) {
							player.spawnItem(vOutputHopper.elementAt(i));
						}
					}
					client.insertPacket(PacketFactory.buildOpenContainerMessage(
							tOutputHopper, -1));
				}
				break;
			}
			case (byte) Constants.RADIAL_MENU_FACTORY_MANAGE_SCHEMATIC: {
				// Add / remove schematics.
				// If there is a schematic inserted, ask user if they want to
				// remove it.
				// If there is no schematic inserted, display an SUI List box
				// containing all VALID schematics to install.
				// SUIWindow schematicWindow = new SUIWindow();

				SUIWindow w = new SUIWindow(player);
				w.setWindowType(Constants.SUI_FACTORY_UPDATE_INSTALLED_SCHEMATIC);
				w.setOriginatingObject(this);
				String WindowTypeString = "handleUpdateSchematic";
				String DataListTitle = Constants.BASE_PLAYER_STRING;
				String DataListPrompt = null;
				if (currentSchematic == null) {
					DataListPrompt = "No schematic installed";
				} else {
					DataListPrompt = "Current schematic installed: "
							+ currentSchematic.getCraftedName();
				}
				Vector<ManufacturingSchematic> vSchematics = player.getSchematicsForFactory(iFactoryType);
				int listSize = vSchematics.size();
				if (currentSchematic != null) {
					listSize += 1;
				}
				String sList[] = new String[listSize];
				if (!vSchematics.isEmpty()) {
					for (int i = 0; i < vSchematics.size(); i++) {
						sList[i] = vSchematics.elementAt(i).getCraftedName();
					}
				}
				if (currentSchematic != null) {
					sList[sList.length - 1] = "Remove schematic "
							+ currentSchematic.getCraftedName();
				}
				client.insertPacket(w.SUIScriptListBox(client,
						WindowTypeString, DataListTitle, DataListPrompt, sList,
						null, 0, 0));
				break;
			}
			case Constants.RADIAL_MENU_ADMIN_SERVER_MENU10: {
				// Activate the factory.
				// First -- loop through everything in the input hopper and
				// match them to the resource / item requirements of the
				// schematic.
				if (checkCanManufactureObject()) {
					// Started manufacturing items.
					client.insertPacket(PacketFactory.buildChatSystemMessage("Station activated"));
					bIsRunning = true;
				} else {
					// Failed to start.
					client.insertPacket(PacketFactory.buildChatSystemMessage("system_msg", "manf_error"));
					bIsRunning = false;
				}
				break;
			}
			case Constants.RADIAL_MENU_ADMIN_SERVER_MENU9: {
				// List ingredients needed to run the factory with the installed
				// schematic.
				if (currentSchematic == null) {
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"manf_station", "no_schematic_examine_prompt"));
				} else {
					// Build an SUI Message Box containing the list of resource
					// names, resource types, and quantites.
					// Also add to the message box the TangibleItem names,
					// quantities, and serial numbers (if applicable)
				}
				break;
			}
			default: {
				client.insertPacket(PacketFactory.buildChatSystemMessage("Unknown radial menu item chosen for this factory: " + commandID));
				break;
			}
			}
		} catch (Exception e) {
			System.out.println("Error handling radial menu action in Factory: "
					+ e.toString());
			e.printStackTrace();
		}
	}

	protected void setFactoryType(byte factoryType) {
		this.iFactoryType = factoryType;
	}

	protected byte getFactoryType() {
		return iFactoryType;
	}

	protected TangibleItem getInputHopper() {
		return tInputHopper;
	}

	protected TangibleItem getOutputHopper() {
		return tOutputHopper;
	}

	protected ManufacturingSchematic getCurrentSchematic() {
		return currentSchematic;
	}

	protected void setCurrentSchematic(ManufacturingSchematic schematic) {
		currentSchematic = schematic;
	}
	
	private boolean checkCanManufactureObject() {
		// Number of slots, number of objects inserted. Most of these
		// will be an array of [n][1] size, but tangible items will be
		// bigger.
		// Is the slot a resource slot, a similar tangible item slot, or
		// an identical tangible item slot?
		System.out.println("checkCanManufactureObject");
		
		if (currentSchematic == null) {
			System.out.println("No installed schematic -- cannot manufacture nothing.");
			return false;
		}
		if (getMaintenancePool() > 0) {
			if (getPowerPool() > getPowerRate()) {
				
		
				long[] vSerialsOfComponents = currentSchematic.getComponentSerials();
				CraftingSchematic schematic= currentSchematic.getCraftingSchematic();
				schematic.clearFactoryItemForCrafting();
				Vector<CraftingSchematicComponent> vComponents = schematic.getComponents();
				Vector<TangibleItem> vInputHopper= tInputHopper.getLinkedObjects();
				// For each component in the list:  Is it an optional component, a required component, or a resource?
				// If it's an optional component, must they all be identical, or similar?
				// If it's an identical component, is it in the factory, and must they all be identical, or similar?
				// If it's a resource, is it in the factory?
				
				// TODO:  Sort stuff in the ingredient hopper by serial number.
				for (int i = 0; i < vSerialsOfComponents.length; i++) {
					long lSerial = vSerialsOfComponents[i];
					CraftingSchematicComponent component = vComponents.elementAt(i);
					int quantityNeeded = component.getComponentQuantity();
					System.out.println("Search for serial " + lSerial + " for component " + i + ", quantity needed: " + quantityNeeded);
					if (component != null) {
						// We have a component, we should have a serial number.
						if (lSerial != 0) {
							if (component.getIsResource()) {
								// Go searching through all our input hopper objects, find all the ResourceContainers, and see if their resourceSpawnID matches this serial.
								int quantityFound = 0;
								boolean bFoundResource = false;
								for (int j = 0; j < vInputHopper.size(); j++) {
									TangibleItem item = vInputHopper.elementAt(i);
									if (item instanceof ResourceContainer) {
										ResourceContainer container = (ResourceContainer)item;
										long lResourceSpawnID = container.getResourceSpawnID();
										if (lResourceSpawnID == lSerial) {
											// Found it, we can check the next item.
											quantityFound += container.getStackQuantity();
											schematic.addFactoryItemForCrafting(container, i);
											if (quantityFound >= quantityNeeded) {
												System.out.println("Found resource!");
												bFoundResource = true;
											}
										} else {
											// Keep looking.
										}
									}
								}
								// If we never found the requisite resource in the list, we cannot manufacture this item.
								if (!bFoundResource) {
									System.out.println("Missing requisite resource with resource spawn ID " + lSerial);
									return false;
								}
							} else{
								int numFound = 0;
								byte componentRequirementType = component.getComponentRequirementType();
								if (componentRequirementType == Constants.CRAFTING_COMPONENT_MANDATORY_RESOURCE) {
									// Shouldn't be here.
									System.out.println("Possible factory error:  Expected tangible item in crafting schematic, got resource.");
									return false;
								}else if (componentRequirementType == Constants.CRAFTING_COMPONENT_MANDATORY_IDENTICAL_ITEM) {
									// Find the item or items with this serial number in the input hopper, find out if we have enough to make the next item for the current factory crate.
									// Short-circuit as soon as we have found "enough" items.
									for (int j = 0; j < vInputHopper.size() && (numFound < quantityNeeded); j++) {
										TangibleItem item = vInputHopper.elementAt(i);
										if (item instanceof FactoryCrate) {
											FactoryCrate crate = (FactoryCrate) item;
											int crateQuantity = crate.getQuantity();
											long lCrateSerial = crate.getSerialNumber();
											if (lCrateSerial == lSerial) {
												// Found it!
												System.out.println("Found a factory crate with " + crateQuantity + " of the necessary item.");
												schematic.addFactoryItemForCrafting(crate, i);
												numFound += crateQuantity;
											} else {
												// Keep looking
											}
											
										} else {
											if (!(item instanceof ResourceContainer)) {
												long lResourceSpawnID = item.getSerialNumber();
												if (lResourceSpawnID == lSerial) {
													// Found it, we can check the next item.
													System.out.println("Found a tangible item with matching serial number."  );
													schematic.addFactoryItemForCrafting(item, i);
													numFound++;
												} else {
													// Keep looking.
												}
											}
										}
									}
									
								} else if (componentRequirementType == Constants.CRAFTING_COMPONENT_MANDATORY_SIMILAR_ITEM) {
									// Find any items which have the same .iff filename in the input hopper, find out if we have enough to make the next item for the current factory crate.
									// Don't forget to search in factory crates, as well.
									String stfFileIdentifier = component.getSTFFileIdentifier();
									
									for (int j = 0; j < vInputHopper.size() && (numFound < quantityNeeded); j++) {
										TangibleItem item = vInputHopper.elementAt(i);
										if (item instanceof FactoryCrate) {
											FactoryCrate crate = (FactoryCrate) item;
											int crateQuantity = crate.getQuantity();
											if (stfFileIdentifier.equals(crate.getSTFFileIdentifier())) {
												// Found it!
												System.out.println("Found a factory crate with " + crateQuantity + " of the necessary stf type.");
												schematic.addFactoryItemForCrafting(item, i);
												numFound += crateQuantity;
											} else {
												// Keep looking
											}
											
										} else {
											if (!(item instanceof ResourceContainer)) {
												if (stfFileIdentifier.equals(item.getSTFFileIdentifier())) {
													// Found it, we can check the next item.
													System.out.println("Found a tangible item of the necessary stf type.");
													schematic.addFactoryItemForCrafting(item, i);
													numFound++;
												} else {
													// Keep looking.
												}
											}
										}
									}
								
								} else {
									// Should never happen.
								}
								if (numFound < quantityNeeded) {
									System.out.println("Insufficient tangible item with serial " + lSerial + " -- cannot manufacture.");
									return false;
								}
							}
						} else {
							if (component.isOptionalComponent()) {
								// Everything's cool -- go ahead an manufacture anyway.
							} else {
								// This should never be able to happen.
								throw new NullPointerException("Schematic is missing data for a required component at index " + i);
							}
						}
					}
				}
				System.out.println("All resources and tangible items used in schematic are present in factory, we can manufacture!");
				lTimeToNextObjectCreationMS = ((((int)currentSchematic.getCurrentCraftingComplexity() * 5) / 2) * 1000);
				return true;
			} else {
				System.out.println("Insufficient power available for factory -- cannot manufacture.");
			}
		} else {
			System.out.println("Insufficient maintenance funds available for factory -- cannot manufacture.");
		}
		return false;
	}
	
	private void manufactureObject(){
		// Essentially, what we want to do here is create a "prototype" item like we do in the crafting process, set all it's data, customization data, etc. etc. from
		// the crafting schematic, and add it to the last used factory crate.
		// If the last used factory crate is full, then we must start up a new one.
		CraftingSchematic schematic = currentSchematic.getCraftingSchematic();
		
		ItemTemplate template = DatabaseInterface.getTemplateDataByFilename(currentSchematic.getCraftingSchematic().getCraftedItemIFFFilename());
		TangibleItem itemToManufacture = null;
		if (schematic instanceof WeaponCraftingSchematic) {
			itemToManufacture = new Weapon(currentSchematic.getCustomName());
			// Set it's values, and it's attributes.
		} else if (schematic instanceof FoodCraftingSchematic) {
			itemToManufacture = new FoodItem();
			itemToManufacture.setCraftedName(currentSchematic.getCustomName());
			// Again, set it's values and attributes.
		} else {
			itemToManufacture = new TangibleItem();
			itemToManufacture.setCraftedName(currentSchematic.getCustomName());
			// Set values, attributes, etc.
		}
		// Set template data here.
		itemToManufacture.setIFFFileName(template.getIFFFileName());
		itemToManufacture.setSTFFileName(template.getSTFFileName());
		itemToManufacture.setSTFFileIdentifier(template.getSTFFileIdentifier());
		itemToManufacture.setTemplateID(template.getTemplateID());
		// Subtract raw materials used in manufacture of the item here.
		
		if (lastItemCreated == null) {
			lastItemCreated = new FactoryCrate();
			lastItemCreated.addToCrate(itemToManufacture);
			tOutputHopper.addLinkedObject(lastItemCreated);
		} else {
			boolean bNeedNewCrate = lastItemCreated.addToCrate(itemToManufacture);
			if (bNeedNewCrate) {
				lastItemCreated = new FactoryCrate();
				lastItemCreated.addToCrate(itemToManufacture);
				tOutputHopper.addLinkedObject(lastItemCreated);
			}
		}
		if (checkCanManufactureObject()) {
			lTimeToNextObjectCreationMS = ((((int)currentSchematic.getCurrentCraftingComplexity() * 5) / 2) * 1000);
			System.out.println("Item " + template.getIFFFileName() + " added to factory crate.  Time to next manufacture: " + lTimeToNextObjectCreationMS + " ms");
		} else {
			System.out.println("Item " + template.getIFFFileName() + " added to factory crate, but cannot manufacture more due to lack of raw materials.  Shutting down factory."); 
			bIsRunning = false;
		}
	}
	
	protected void addToHopperAccess(Player p) {
		vHopperAccessList.add(p.getID());
	}
	
	protected void removeFromHopperAccess(Player p) {
		vHopperAccessList.remove(p.getID());
	}
	
	protected boolean hasHopperAccess(Player p) {
		return vHopperAccessList.contains(p.getID());
	}
	
}
