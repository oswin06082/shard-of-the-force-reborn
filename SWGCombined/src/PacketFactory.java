import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;

public class PacketFactory {

	protected static byte[] buildClientUIErrorMessage(String sMessageTitle,
			String sMessageBody) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.ACCOUNT_UPDATE);
		dOut.writeInt(Constants.ClientUIErrorMessage);
		dOut.writeUTF(sMessageTitle);
		dOut.writeUTF(sMessageBody);
		dOut.writeShort(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildSessionResponse(int connectionID,
			int packetSize) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_SESSION_RESPONSE);
		dOut.writeInt(connectionID);
		dOut.writeInt(0);
		// dOut.writeReversedInt(PacketUtils.getRandomSeed());
		dOut.writeByte(2);
		dOut.writeByte(1);
		dOut.writeByte(4);
		dOut.writeReversedInt(packetSize);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildAcknowledgement(short sequence)
			throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream sOut = new SOEOutputStream(bOut);
		sOut.setOpcode(Constants.SOE_ACK_A);
		sOut.setSequence(sequence);
		sOut.flush();
		return sOut.getBuffer();
	}

	protected static byte[] buildNetStatusResponse(ZoneClient client)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_NET_STATUS_RES);
		dOut.writeReversedShort(client.getLastTickCount());
		dOut.writeReversedInt((int) client.getServer().getTicks());
		dOut.writeReversedLong(client.getClientPacketsSent());
		dOut.writeReversedLong(client.getClientPacketsReceived());
		dOut.writeReversedLong(client.getServerPacketsSent());
		dOut.writeReversedLong(client.getServerPacketsReceived());
		dOut.flush();
		return dOut.getBuffer();
	}

	// This packet sets various client permissions. The first byte / boolean and
	// the last byte / boolean are unknown, but the second
	// gives the client permission to create players on this server.

	protected static byte[] buildClientPermissionsMessage(
			int iNumCharactersOwnedByAccount, int iMaxCharsPerAccount)
			throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream sOut = new SOEOutputStream(bOut);
		// These next 3 lines are generated in the C++ core after the fact, by
		// the routine that prepares this packet to actually be sent.
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0); // Will be sequence.
		sOut.setUpdateType(Constants.SERVER_UPDATE);
		// These lines are actually present in the C++ version.
		sOut.writeInt(Constants.ClientPermissionsMessage);
		sOut.writeByte((byte) 1);
		sOut.writeBoolean(iNumCharactersOwnedByAccount < iMaxCharsPerAccount);
		sOut.writeByte((byte) 0);

		return bOut.toByteArray();
	}

	// This packet provides a response to the Client's RandomNameRequest
	// message. If the name is correct, nothing happens
	// to the client. Otherwise, the client will pop up a message box indicating
	// what the problem is.
	protected static byte[] buildClientRandomNameResponse(
			String characterSpecies, String characterName,
			int nameResponseIndexPosition) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream sOut = new SOEOutputStream(bOut);
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0);
		sOut.setUpdateType(Constants.WORLD_UPDATE);
		sOut.writeInt(Constants.ClientRandomNameResponse);
		sOut.writeUTF(characterSpecies);
		sOut.writeUTF16(characterName);
		sOut.writeUTF("ui");
		sOut.writeInt(0);
		sOut.writeUTF(Constants.NameResponseCodes[nameResponseIndexPosition]);
		return bOut.toByteArray();
	}

	// Build this when the client's entered name is invalid. This will indicate
	// that the name is invalid, and a general
	// reason why.
	protected static byte[] buildClientNameDeclinedResponse(String invalidName,
			int responseCode) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream sOut = new SOEOutputStream(bOut);
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0); // Sequence -- will be replaced.
		sOut.setUpdateType(Constants.ACCOUNT_UPDATE);
		sOut.writeInt(Constants.ClientNameDeclinedResponse);
		sOut.writeUTF16(invalidName);
		sOut.writeUTF("ui");
		sOut.writeUTF(Constants.NameResponseCodes[1]);
		return bOut.toByteArray();
	}

	// Indicates to the client that the character was created successfully.
	protected static byte[] buildCreateCharacterSuccess(long characterID)
			throws IOException {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0);
		sOut.setUpdateType(Constants.WORLD_UPDATE);
		sOut.writeInt(Constants.ClientCreateCharacterSuccess);
		sOut.writeLong(characterID);
		return sOut.getBuffer();
	}

	// Indicates to the client that the character was NOT created successfully,
	// and a general reason as to why.
	// The client may or may not have already seen the reason why if they
	// generated a random name (generally
	// the only reason a character couldn't be created)
	protected static byte[] buildCreateCharacterFailed(long characterID,
			int iNameResponseIndex) throws IOException {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0);
		sOut.setUpdateType(Constants.ACCOUNT_UPDATE);
		sOut.writeInt(Constants.ClientCreateCharacterFailed);
		sOut.writeUTF16("Creation failed");
		sOut.writeUTF("ui");
		sOut.writeInt(0);
		sOut.writeUTF(Constants.NameResponseCodes[iNameResponseIndex]);
		return sOut.getBuffer();
	}

	// This one really needs a better name... unknown Zone packet?
	protected static byte[] buildChatServerStatus() throws IOException {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0);
		sOut.setUpdateType(Constants.WORLD_UPDATE);
		sOut.writeInt(Constants.ChatServerStatus);
		sOut.writeByte(1);
		return sOut.getBuffer();
	}

	// Well... it sends the Paramaters of the player. Obviously the int value of
	// 900 needs to be discovered...
	protected static byte[] buildParametersMessage() throws IOException {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0);
		sOut.setUpdateType(Constants.WORLD_UPDATE);
		sOut.writeInt(Constants.ParametersMessage);
		sOut.writeInt(900);
		return sOut.getBuffer();
	}

	// Start the scene for the player. This must be sent before we start
	// spawning items, or the client will get
	// really fracking pissed off.
	protected static byte[] buildCmdStartScene(Player player,
			long spaceCmdStartScene) throws IOException {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0);
		sOut.setUpdateType(Constants.UPDATE_NINE);
		sOut.writeInt(Constants.CmdStartScene);
		if (player.tempnobuildings == 1) {
			sOut.writeByte((byte) 1);
		} else {
			sOut.writeByte((byte) 0);
		}

		sOut.writeLong(player.getID());
		int planetID = player.getPlanetID();
		if (planetID < 0) {
			planetID = Constants.TATOOINE;
			player.setPlanetID(Constants.TATOOINE);
		}
		sOut.writeUTF(Constants.TerrainNames[player.getPlanetID()]);
		sOut.writeFloat(player.getX());
		sOut.writeFloat(player.getZ());
		sOut.writeFloat(player.getY());
		sOut.writeUTF(Constants.SharedRaceModels[player.getRaceID()]);
		sOut.writeLong(spaceCmdStartScene);
		return sOut.getBuffer();
	}

	// Sends the server's current Earth Time to the client. (Or is it the
	// client's up time?)
	protected static byte[] buildServerTimeMessage() throws IOException {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0);
		sOut.setUpdateType(Constants.WORLD_UPDATE);
		sOut.writeInt(Constants.ServerTimeMessage);
		sOut.writeLong(System.currentTimeMillis() / 1000); // Expects the
															// current time in
															// Seconds, not in
															// Milliseconds.
		return sOut.getBuffer();
	}

	// Creates an object visible to the Player. (Also, could create the Player)

	protected static byte[] buildSceneCreateObjectByCRC(SOEObject o,
			boolean bIsSharedPlayer) throws IOException {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0);
		sOut.setUpdateType(Constants.OBJECT_UPDATE);
		sOut.writeInt(Constants.SceneCreateObjectByCrc);
		sOut.writeLong(o.getID());
		sOut.writeFloat(o.getOrientationN());
		sOut.writeFloat(o.getOrientationS());
		sOut.writeFloat(o.getOrientationE());
		sOut.writeFloat(o.getOrientationW());
		// ------------------------------------------------
		long lCellId = o.getCellID();
		if (lCellId == 0) {
			sOut.writeFloat(o.getX());
			sOut.writeFloat(o.getZ());
			sOut.writeFloat(o.getY());
		} else {
			sOut.writeFloat(o.getCellX());
			sOut.writeFloat(o.getCellZ());
			sOut.writeFloat(o.getCellY());
		}
		if (bIsSharedPlayer) {
			Player p = (Player) o;
			sOut.writeInt(p.getSharedCRC());
		} else {
			sOut.writeInt(o.getCRC());
		}
		sOut.writeBoolean(false); // For JTL make it true.
		return sOut.getBuffer();
	}

	// "Destroys" the object passed in, so that it becomes despawned.
	protected static byte[] buildSceneDestroyObject(SOEObject o)
			throws IOException {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0); // Sequence
		sOut.setUpdateType(Constants.ACCOUNT_UPDATE);
		sOut.writeInt(Constants.SceneDestroyObject);
		sOut.writeLong(o.getID());
		sOut.writeBoolean(false); // Unknown. Might not even be a boolean, but
									// an actual 1 byte value as in the C++
									// version.
		// Based on the sceneCreateObjectByCRC, we probably get to make this
		// true if it's a JTL object.
		return sOut.getBuffer();
	}

	// Send this when we're done sending all the baseline messages the object
	// needs.
	protected static byte[] buildSceneEndBaselines(SOEObject o)
			throws IOException {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0); // Sequence
		sOut.setUpdateType(Constants.WORLD_UPDATE);
		sOut.writeInt(Constants.SceneEndBaselines);
		sOut.writeLong(o.getID());
		return sOut.getBuffer();
	}

	// Send this packet when all the initialization stuff is done. This tells
	// the client "O.k., your're loaded in -- start playing!"
	protected static byte[] buildCmdSceneReady() throws IOException {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0); // Sequence
		sOut.setUpdateType(Constants.CLIENT_UI_UPDATE);
		sOut.writeInt(Constants.CmdSceneReady);
		return sOut.getBuffer();
	}

	// Note: The UpdateContainmentMessage and UpdateCellContainmentMessage from
	// the C++ core are IDENTICAL, except
	// the UpdateCellContainmentMessage has the isEquipped always true.
	// As the name indicates, this updates the "containment" status of the item
	// passed within the container.
	protected static byte[] buildUpdateContainmentMessage(SOEObject item,
			SOEObject container, int equippedStatus) throws IOException {

		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0); // Sequence;
		sOut.setUpdateType(Constants.SERVER_UPDATE);
		sOut.writeInt(Constants.UpdateContainmentMessage);
		sOut.writeLong(item.getID());
		if (container != null) {
			sOut.writeLong(container.getID());
		} else {
			sOut.writeLong(0);
		}
		sOut.writeInt(equippedStatus);
		return sOut.getBuffer();
	}

	// Baseline for the TangibleItem class.
	protected static byte[] buildBaselineTANO3(TangibleItem t)
			throws IOException {
		byte[] customizationData = t.getCustomData();
		if (customizationData == null) {
			customizationData = new byte[0];
		}
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		int packetSize = 49;
		String stfFileName = t.getSTFFileName();
		if (stfFileName != null) {
			packetSize += stfFileName.length();
		}
		String stfFileIdentifier = t.getSTFFileIdentifier();
		if (stfFileIdentifier != null) {
			packetSize += stfFileIdentifier.length();
		}
		String sCustomName = t.getName();
		if (sCustomName == null) {
			// sCustomName = "default_name";
			sCustomName = "";
		}
		packetSize += (sCustomName.length() * 2);
		packetSize += customizationData.length;
		// if(t.getCRC() == 0x3D7F6F9F)
		// {
		// System.out.println("Sending Tano3 for Mission Bag");
		// System.out.print(stfFileName);
		// System.out.print(stfFileIdentifier);
		// System.out.print(sCustomName);
		// }

		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0); // Sequence.
		sOut.setUpdateType(Constants.OBJECT_UPDATE);
		sOut.writeInt(Constants.BaselinesMessage);
		sOut.writeLong(t.getID());
		sOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_TANO]);
		sOut.writeByte((byte) 3);
		sOut.writeInt(packetSize);
		sOut.writeShort(0x0B); // Operand count
		sOut.writeFloat(t.getOrientationE()); // vID 0

		// vID 1
		sOut.writeUTF(stfFileName); // For hair, this has to be "hair_name"
		sOut.writeInt(0); // Unknown
		sOut.writeUTF(stfFileIdentifier); // For hair, this has to be "hair"

		// vID 2
		sOut.writeUTF16(sCustomName); // For hair this can be null -- won't
										// really affect anything if it isn't.

		// vID 3
		sOut.writeInt(1); // Unknown;

		// vID 4
		if (customizationData != null) {
			sOut.writeShort(customizationData.length);
			sOut.write(customizationData);
		} else {
			sOut.writeShort(0);
		}
		// CHANGED BY PLASMAFLOW
		// VERIFIED BY MAACH_INE : NO
		// sOut.writeInt(0); // Unknown
		// sOut.writeInt(0); // Unknown
		// vID 5
		sOut.writeLong(0); // Unknown

		// 2 ints, actually.
		// First int is some sort of whacky bitmask.
		// Second int is the number of items in this container. 0 for
		// "don't show".
		// vID 6
		sOut.writeInt(768); // //sOut.writeLong(768); 
		// vID 7
		int iStackSize = t.getStackQuantity();
		if (iStackSize == 1) {
			sOut.writeInt(0);
		} else {
			sOut.writeInt(iStackSize);
		}

		// vID 8
		sOut.writeInt(t.getConditionDamage());
		// vID 9
		sOut.writeInt(t.getMaxCondition());
		// vID 10
		sOut.writeBoolean(true); // Is this a boolean, or an actual byte value?
		sOut.flush();

		/*
		 * byte [] buff = sOut.getBuffer(); if(t.getCRC() == 0x3D7F6F9F) {
		 * PacketUtils.printPacketToScreen(buff,buff.length,"TANO 3 PACKET"); }
		 */
		return sOut.getBuffer();
	}

	protected static byte[] buildBaselineTANO6(TangibleItem t)
			throws IOException {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		String sSTFFileName = t.getSTFDetailName();
		String sSTFFileIdentifier = t.getSTFDetailIdentifier();
		int packetSize = 23;// 14;
		if (sSTFFileName == null) {
			sSTFFileName = "";
		}
		if (sSTFFileIdentifier == null) {
			sSTFFileIdentifier = "";
		}
		packetSize += ((sSTFFileName.length()) + (sSTFFileIdentifier.length()));

		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0); // Sequence.
		sOut.setUpdateType(Constants.OBJECT_UPDATE);
		sOut.writeInt(Constants.BaselinesMessage);
		sOut.writeLong(t.getID());
		sOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_TANO]);
		sOut.writeByte((byte) 6);
		sOut.writeInt(0x0E);// (14); // Packet Size.
		// sOut.writeInt(packetSize);
		sOut.writeShort(2);// (2); // Operand count.
		sOut.writeInt(0x76);// (61); // Unknown;
		// CHANGED BY PLASMAFLOW
		// VERIFIED BY MAACH_INE : NO
		// sOut.writeUTF(sSTFFileName);//these should be strings
		// sOut.writeInt(0);
		// sOut.writeUTF(sSTFFileIdentifier);//these should be strings

		// vID 1 -- List of longs?
		sOut.writeInt(0);
		sOut.writeInt(0);
		// sOut.writeByte(0);
		sOut.flush();
		return sOut.getBuffer();
	}

	protected static byte[] buildBaselineTANO7(TangibleItem t)
			throws IOException {
		int packetSize = 0x12;
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0);
		sOut.setUpdateType(Constants.OBJECT_UPDATE);
		sOut.writeInt(Constants.BaselinesMessage);
		sOut.writeLong(t.getID());
		sOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_TANO]);
		sOut.writeByte(7);
		sOut.writeInt(packetSize);
		sOut.writeShort(2);
		// 2 unknown longs.
		sOut.writeLong(0);
		sOut.writeLong(0);
		sOut.flush();
		return sOut.getBuffer();
	}

	protected static byte[] buildBaselineTANO8(TangibleItem t)
			throws IOException {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0);
		sOut.setUpdateType(Constants.OBJECT_UPDATE);
		sOut.writeInt(Constants.BaselinesMessage);
		sOut.writeLong(t.getID());
		sOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_TANO]);
		sOut.writeByte((byte) 8);
		sOut.writeInt(2); // Packet size.
		sOut.writeShort(0); // Operand count
		sOut.flush();
		return sOut.getBuffer();
	}

	protected static byte[] buildBaselineTANO9(TangibleItem t)
			throws IOException {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0);
		sOut.setUpdateType(Constants.OBJECT_UPDATE);
		sOut.writeInt(Constants.BaselinesMessage);
		sOut.writeLong(t.getID());
		sOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_TANO]);
		sOut.writeByte((byte) 9);
		sOut.writeInt(2); // / Packet length
		sOut.writeShort(0); // Operand count
		sOut.flush();
		return sOut.getBuffer();
	}

	// Baselines messages for the Creature objects.
	protected static byte[] buildBaselineCREO1(Player player)
			throws IOException {
		int packetSize = 62;
		BitSet skills = null;
		if (!(player instanceof Vehicle)) {
			skills = player.getSkillList();
		}

		int numSetSkills = 0;
		if (skills != null) {

			for (int i = skills.nextSetBit(0); i >= 0; i = skills
					.nextSetBit(i + 1)) {
				numSetSkills++;
				// Skills skill = DatabaseInterface.getSkillFromIndex(i);
				Skills skill = player.getServer().getSkillFromIndex(i);
				packetSize += skill.getName().length() + 2;
			}
		}
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0); // Sequence
		sOut.setUpdateType(Constants.OBJECT_UPDATE);
		sOut.writeInt(Constants.BaselinesMessage);
		sOut.writeLong(player.getID());
		sOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_CREO]);
		sOut.writeByte((byte) 1);
		sOut.writeInt(packetSize);
		sOut.writeShort(4); // Operand count
		sOut.writeInt(player.getBankCredits());
		sOut.writeInt(player.getInventoryCredits());

		int[] iMaxHam = player.getMaxHam();
		sOut.writeInt(iMaxHam.length); // / Number of HAM bars.
		sOut.writeInt(player.getMaxHamUpdateCounter(false)); // Update count on
																// the HAM bars.
		for (int i = 0; i < iMaxHam.length; i++) {
			sOut.writeInt(iMaxHam[i]);
			// sOut.writeInt(0);
		}
		if (skills == null || numSetSkills == 0) {
			sOut.writeLong(0);
		} else {
			sOut.writeInt(numSetSkills);
			sOut.writeInt(player.getSkillListUpdateCount(false)); // Update
																	// counter
																	// on the
																	// skill
																	// list.

			for (int i = skills.nextSetBit(0); i >= 0; i = skills
					.nextSetBit(i + 1)) {
				// Skills skill = DatabaseInterface.getSkillFromIndex(i);
				Skills skill = player.getServer().getSkillFromIndex(i);
				sOut.writeUTF(skill.getName());
			}
		}
		sOut.flush();
		return sOut.getBuffer();
	}

	protected static byte[] buildBaselineCREO3(Player p) throws IOException {
		int[] iHamWounds = p.getHamWounds();
		byte[] customizedData = p.getCustomData();
		int PacketSize = 83 + p.getSTFFileName().length()
				+ p.getSTFFileIdentifier().length()
				+ (p.getFullName().length() * 2) + (iHamWounds.length * 4);
		if (customizedData != null) {
			PacketSize += customizedData.length;
		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(p.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_CREO]);
		dOut.writeByte(3);
		dOut.writeInt(PacketSize);
		dOut.writeShort(0x12); // Object operand count.
		// operand 0
		dOut.writeFloat(10); // 10 unknown.
		// operand 1
		dOut.writeUTF(p.getSTFFileName());
		dOut.writeInt(0);
		dOut.writeUTF(p.getSTFFileIdentifier());
		// operand 2
		dOut.writeUTF16(p.getFullName());
		// operand 3
		dOut.writeInt(0x0085E5CA);
		// operand 4
		if (customizedData == null) {
			dOut.writeShort(0);
		} else {
			dOut.writeShort(customizedData.length);
			dOut.write(customizedData);
		}
		// vID 5
		dOut.writeInt(0); // Unknown list.
		dOut.writeInt(0);

		// vID 6
		int iCreo3Bitmask = p.getCREO3Bitmask();
		dOut.writeInt(iCreo3Bitmask);
		// vID 7
		dOut.writeInt(p.getIncapTimer());
		if (p instanceof NPC) {
			NPC n = (NPC) p;
			// vID 8
			dOut.writeInt(n.getDamage()); // Vehicle Damage
			// vID 9
			dOut.writeInt(n.getHealth()); // Vehicle Max Health

		} else {
			// vID 8
			dOut.writeInt(0); // Vehicle Damage
			// vID 9
			dOut.writeInt(0); // Vehicle Max Health

		}
		// vID 10
		dOut.writeByte((byte) p.getMoodID());
		// vID 11
		dOut.writeByte(p.getStance());
		// vID 12
		dOut.writeByte(p.getFactionRank()); // FactionRankID
		// vID 13
		dOut.writeLong(0); // Owner/Link ID
		// vID 14
		dOut.writeFloat(p.getScale());
		// vID 15
		dOut.writeInt(p.getBattleFatigue()); // Confirmed
		// vID 16
		dOut.writeLong(p.getStateBitmask());
		// vID 17
		dOut.writeInt(iHamWounds.length);
		dOut.writeInt(p.getHamWoundsUpdateCount(false));
		for (int i = 0; i < iHamWounds.length; i++) {
			dOut.writeInt(iHamWounds[i]);
		}
		return dOut.getBuffer();
	}

	protected static byte[] buildBaselineCREO4(Player player)
			throws IOException {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		int packetSize = 110;
		Vector<SkillMods> vSkillMods = player.getSkillModsList();
		if (vSkillMods != null) {
			for (int i = 0; i < vSkillMods.size(); i++) {
				packetSize += (vSkillMods.elementAt(i).getName().length() + 11); // Length
																					// of
																					// the
																					// name
																					// string,
																					// plus
																					// 2
																					// ints
																					// and
																					// a
																					// boolean.
			}
		}
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0); // Sequence
		sOut.setUpdateType(Constants.OBJECT_UPDATE);
		sOut.writeInt(Constants.BaselinesMessage);
		sOut.writeLong(player.getID());
		sOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_CREO]);
		sOut.writeByte((byte) 4);
		sOut.writeInt(packetSize);
		sOut.writeShort(14); // Operand Count
		// operand 0
		sOut.writeFloat(1f); // Unknown
		// operand 1
		sOut.writeFloat(1f); // Unknown
		// operand 2
		int[] iHamEncumberance = player.getHamEncumberances();
		sOut.writeInt(iHamEncumberance.length);
		// operand 3
		sOut.writeInt(player.getHamEncumberanceUpdateCount(false));
		for (int i = 0; i < iHamEncumberance.length; i++) {
			sOut.writeInt(iHamEncumberance[i]);
		}
		if (vSkillMods == null) {
			sOut.writeLong(0);
		} else {
			sOut.writeInt(vSkillMods.size());
			sOut.writeInt(player.getSkillModsUpdateCounter(false));
			for (int i = 0; i < vSkillMods.size(); i++) {
				SkillMods s = vSkillMods.elementAt(i);
				sOut.writeByte(Constants.DELTA_CREATING_ITEM);
				sOut.writeUTF(s.getName());
				sOut.writeInt(s.getSkillModModdedValue());
				sOut.writeInt(0); // Probably always 0?
			}
		}
		// operand 4
		sOut.writeFloat(1f); // Unknown
		// operand 5
		sOut.writeFloat(1f); // Acceleration
		// Operand 6
		sOut.writeLong(player.getListeningToID()); // This is the Listen To
													// Target.
		// op 7
		float fMaxVelocity = player.getMaxVelocity();
		if (!(player instanceof NPC)) {
			System.out.println("Writing max velocity of " + fMaxVelocity
					+ " metres per second for player " + player.getFirstName());
		}
		sOut.writeFloat(player.getMaxVelocity()); // Run speed.
		// op 8
		sOut.writeFloat(1.00625f); // Unknown
		// op 9
		sOut.writeFloat(0); // Terrain Negotiation
		// op 10
		sOut.writeFloat(player.getTurnRadius()); // Turn radius
		// op 11
		sOut.writeFloat(player.getAcceleration());
		// op 12
		sOut.writeFloat(0.125f); // Unknown
		/*
		 * sOut.writeInt(unknownVector.size());
		 * sOut.writeInt(unknownVectorUpdateCount);
		 */
		// op 13
		sOut.writeInt(0);
		// op 14
		sOut.writeInt(0);
		return sOut.getBuffer();
	}

	protected static byte[] buildBaselineCREO6(Player player)
			throws IOException {

		int[] iCurrentHam = player.getCurrentHam();
		int[] iMaxHam = player.getMaxHam();
		int[] hamMods = player.getHamModifiers();
		if (hamMods == null) {
			hamMods = new int[iMaxHam.length];
			for (int i = 0; i < hamMods.length; i++) {
				hamMods[i] = 0;
			}
			player.setHamModifiers(hamMods);
		}
		String sMoodString = player.getMoodString();
		String sPerformanceString = player.getPerformanceString();

		int PacketSize = 100 + iCurrentHam.length * 4 + iMaxHam.length * 4;

		if (sMoodString != null) {
			PacketSize += sMoodString.length();
		}
		if (sPerformanceString != null) {
			PacketSize += sPerformanceString.length();
		}

		Vector<TangibleItem> vEquippedItems = player.getEquippedItems();
		if (vEquippedItems != null) {
			for (int i = 0; i < vEquippedItems.size(); i++) {
				TangibleItem t = vEquippedItems.elementAt(i);

				if (t == null) {
					vEquippedItems.remove(i);
				} else {
					byte[] customData = t.getCustomData();
					if (customData != null) {
						PacketSize += customData.length;
					}
					PacketSize += 18;
				}
			}
		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(player.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_CREO]);
		dOut.writeByte(6);
		dOut.writeInt(PacketSize);
		dOut.writeShort(0x16); // Operand count
		// Note: The vID count in the Creo6 baseline is wrong, since there's
		// only 0x11 variables, yet we tell it there's 0x16.
		// vID 0
		dOut.writeInt(0x3D); // Unknown -- Core 3 == 0x3D

		// TODO: Implement Defender List!!!
		// opreand 1 0x01
		dOut.writeInt(0); // Defender list. Probably just a list of longs?
		dOut.writeInt(0);

		// operand 2 0x02
		if (player instanceof Player && !(player instanceof NPC)) {
			dOut.writeShort(100);
		} else {
			dOut.writeShort(player.getConLevel());
		}
		// operand 3 0x03
		dOut.writeUTF(player.getPerformanceString()); // This should be null if
														// not performing any
														// music/song.
		// operand 4 0x04
		dOut.writeUTF(player.getMoodString()); // This should be "none" if not
												// having a mood.
		// operand 5 0x05
		Weapon w = player.getWeapon();
		if (w != null) {
			dOut.writeLong(w.getID());
		} else {
			dOut.writeLong(0);
		}
		// operand 5 0x05
		dOut.writeLong(player.getGroupID()); // group id
		// operand 6 0x06
		dOut.writeLong(player.getGroupHost()); // Invite Sender's ID.
		// operand 7 0x07
		dOut.writeLong(player.getGroupInviteCounter(false)); // New group invite
																// counter.
		// operand 8 0x08
		dOut.writeInt(0); // Guild ID.
		// operand 9 0x09
		dOut.writeLong(player.getTargetID()); // Target ID.
		// operand 10 0x0A
		dOut.writeByte(player.getMoodID());
		// operand 11 0x0B
		dOut.writeInt(player.getIPerformanceID()); // Performance ID
		// operand 12 0x0C
		dOut.writeInt(player.getSongID()); // song ID
		// operand 13 0x0D
		dOut.writeInt(iCurrentHam.length);
		dOut.writeInt(player.getCurrentHamUpdateCounter(false));
		for (int i = 0; i < iCurrentHam.length; i++) {
			dOut.writeInt(iCurrentHam[i]);
		}
		// operand 14 0x0E
		dOut.writeInt(iMaxHam.length);
		dOut.writeInt(player.getHamModifiersUpdateCount(false));
		for (int i = 0; i < iMaxHam.length; i++) {
			dOut.writeInt(iMaxHam[i] + hamMods[i]);
		} // 0x0E

		// operand 15 0x0F
		if (vEquippedItems == null) {
			dOut.writeLong(0);
		} else {
			dOut.writeInt(vEquippedItems.size());
			dOut.writeInt(player.getEquippedItemUpdateCount(false));
			// EQUIPMENT LIST

			for (int i = 0; i < vEquippedItems.size(); i++) {
				TangibleItem item = vEquippedItems.elementAt(i);
				byte[] customizationData = item.getCustomData();
				if (customizationData != null) {
					dOut.writeShort(customizationData.length);
					dOut.write(customizationData);
				} else {
					dOut.writeShort(0);
				}
				dOut.writeInt(item.getEquippedStatus());
				dOut.writeLong(item.getID());
				dOut.writeInt(item.getCRC());
			}
		}
		// operand 16 0x10
		dOut.writeUTF(""); // ModModelIFF
		// operand 17 0x11
		dOut.writeBoolean(player.getC60X11()); // is performing bool ????
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasCREO6DefenderList(Player player,
			long lDefenderID, short iDefenderIndex) throws IOException {
		SOEOutputStream buff = new SOEOutputStream(new ByteArrayOutputStream());
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(player.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_CREO]);
		dOut.writeByte(6);

		// Packet size

		buff.writeShort(1); // Number of updates this Deltas
		buff.writeShort(1); // vID to update.
		buff.writeInt(1); // Number of updates this vID list.
		buff.writeInt(player.getDefenderListUpdateCounter(true));
		if (lDefenderID == 0) {
			buff.writeByte(0);
			buff.writeShort(iDefenderIndex);
		} else {
			buff.writeByte(1);
			buff.writeShort(iDefenderIndex);
			buff.writeLong(lDefenderID);
		}
		buff.flush();
		buff.close();
		byte[] tailBytes = buff.getBuffer();
		dOut.writeInt(tailBytes.length);
		dOut.write(tailBytes);
		dOut.flush();
		dOut.close();
		return dOut.getBuffer();
	}

	// Baselines messages for the Player object.

	protected static byte[] buildBaselinePLAY3(PlayerItem player)
			throws IOException {
		/**
		 * 05 00 0C 5F A7 68 21 00 00 00 93 22 00 00 59 41 4C 50 03 //
		 * ..YALP.v........ 76 00 00 00 0B 00 00 00 80 3F 0F 00 53 74 72 69 6E
		 * 67 5F 69 64 5F 74 61 62 6C 65 /// ?..String_id_table.............. 00
		 * 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 04 00 00 00 00 00
		 * 00 80 //State Bitmask 00 00 00 00 ................ 00 00 00 00 00 00
		 * 00 00 04 00 00 00 00 00 00 00 ................ 00 00 00 00 00 00 00
		 * 00 00 00 00 00 17 00 63 72 61 66 74 69 6E 67 5F 61 72 74 69 73 61 6E
		 * 5F 6E 6F 76 69 63 65 //crafting_artisan_ovice....b...#.. C2 06 00 00
		 * 62 DC 00 00 23 00 00 00
		 */
		int PacketSize = 74 + player.getTitle().length() + 2
				+ Constants.STRING_ID_TABLE.length() + 2;
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(player.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_PLAY]);
		dOut.writeByte(3);
		dOut.writeInt(PacketSize);
		dOut.writeShort(11);
		dOut.writeFloat(1.0f);
		dOut.writeUTF(Constants.STRING_ID_TABLE);
		dOut.writeInt(0);
		dOut.writeUTF(""); // STF File Identifier
		dOut.writeInt(0); // Customized item name.
		dOut.writeInt(0);
		dOut.writeInt(0);
		dOut.writeInt(4);
		dOut.writeInt(player.getStatusBitmask()); // dOut.writeInt(0x80000080);
		dOut.writeInt(0);
		dOut.writeInt(0);
		dOut.writeInt(0);
		dOut.writeInt(4);
		dOut.writeInt(0);
		dOut.writeInt(0);
		dOut.writeInt(0);
		dOut.writeInt(0);
		dOut.writeUTF(player.getTitle()); // vID 7
		dOut.writeInt(0x6C2); // vID 8
		dOut.writeInt(0xD6C2); // vID 9
		dOut.writeInt(0x23); // vID 10
		return dOut.getBuffer();

	}

	protected static byte[] buildBaselinePLAY6(PlayerItem player) {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		int packetSize = 7;
		try {
			sOut.setOpcode(Constants.SOE_CHL_DATA_A);
			sOut.setSequence(0);
			sOut.setUpdateType(Constants.OBJECT_UPDATE);
			sOut.writeInt(Constants.BaselinesMessage);
			sOut.writeLong(player.getID());
			sOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_PLAY]);
			sOut.writeByte((byte) 6);
			sOut.writeInt(packetSize);
			sOut.writeShort(2);
			sOut.writeInt(0);
			sOut.writeByte(player.getCsrOrDeveloperFlag()); // Developer / CSR
															// flag 0 = normal,
															// 1 = CSR, 2 =
															// Developer, 3+ =
															// ignored.
			return sOut.getBuffer();
		} catch (Exception e) {
			return null;
		}
	}

	protected static byte[] buildBaselinePLAY8(PlayerItem player)
			throws IOException {
		// Calculate Packet Size
		int PacketSize = 58; // Size without xp or waypoints
		Vector<Waypoint> vWaypoints = player.getWaypoints();
		for (int i = 0; i < vWaypoints.size(); i++) {
			PacketSize += ((vWaypoints.elementAt(i).getName().length() * 2) + 51);
		}
		int iExperienceCount = 0;
		Hashtable<Integer, PlayerExperience> vExperienceList = player
				.getExperienceList();
		Enumeration<PlayerExperience> vExperienceEnum = vExperienceList
				.elements();
		while (vExperienceEnum.hasMoreElements()) {
			PlayerExperience exp = vExperienceEnum.nextElement();
			if (exp.getCurrentExperience() > 0) {
				PacketSize += (exp.getExperienceName().length() + 7);
				iExperienceCount++;
			}
		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(player.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_PLAY]);
		dOut.writeByte(8);
		dOut.writeInt(PacketSize);
		dOut.writeShort(7); // Operand count
		// Experience
		dOut.writeInt(iExperienceCount);
		dOut.writeInt(player.getExperienceListUpdateCount(false));
		vExperienceEnum = vExperienceList.elements();
		while (vExperienceEnum.hasMoreElements()) {
			PlayerExperience experience = vExperienceEnum.nextElement();
			if (experience.getCurrentExperience() > 0) {
				dOut.writeByte(Constants.DELTA_CREATING_ITEM);
				dOut.writeUTF(experience.getExperienceName());
				dOut.writeInt(experience.getCurrentExperience());
			}
		}

		dOut.writeInt(vWaypoints.size());
		dOut.writeInt(player.getWaypointUpdateCount(false));
		for (int i = 0; i < vWaypoints.size(); i++) {
			Waypoint w = vWaypoints.elementAt(i);
			dOut.writeByte(Constants.DELTA_CREATING_ITEM);
			dOut.writeLong(w.getID());
			dOut.writeInt(0);
			dOut.writeFloat(w.getX());
			dOut.writeFloat(w.getZ());
			dOut.writeFloat(w.getY());
			dOut.writeInt(0); // Unknown
			dOut.writeInt(0); // Unknown
			dOut.writeInt(w.getPlanetCRC());
			dOut.writeUTF16(w.getName());
			dOut.writeLong(w.getID());
			dOut.writeByte(w.getWaypointType());
			dOut.writeBoolean(w.getIsActivated());
		}

		dOut.writeInt(player.getCurrentForce());
		dOut.writeInt(player.getMaxForce());

		dOut.writeInt(0); // Padawan Quests
		dOut.writeInt(0); // Padawan Quest Update Counter

		dOut.writeInt(0); // Force Sensitive Quests
		dOut.writeInt(0); // Force Sensitive Quest Update Counter

		dOut.writeInt(0); // Normal Quests
		dOut.writeInt(0); // Normal Quest Update Counter

		dOut.writeInt(0); // Unknown 1
		dOut.writeInt(0); // Unknown 2

		return dOut.getBuffer();
	}

	protected static byte[] buildBaselinePLAY9(PlayerItem player)
			throws IOException {

		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		int packetSize = 98; // Correct
		Vector<PlayerFriends> vFriendsList = player.getFriendsList();
		Vector<PlayerFriends> vIgnoreList = player.getIgnoreList();
		BitSet skillBits = player.getSkillBits();
		BitSet schematics = player.getSchematics();
		int schematicCount = 0;
		Vector<String> vCertifications = new Vector<String>();
		for (int i = skillBits.nextSetBit(0); i >= 0; i = skillBits
				.nextSetBit(i + 1)) {
			Skills skill = player.getMyPlayer().getServer()
					.getSkillFromIndex(i);
			Vector<String> vCertificationsThisSkill = skill
					.getCertificationList();
			for (int j = 0; j < vCertificationsThisSkill.size(); j++) {
				String cert = vCertificationsThisSkill.elementAt(j);
				packetSize += (cert.length() + 2);
			}
			vCertifications.addAll(vCertificationsThisSkill);

		}

		for (int i = schematics.nextSetBit(0); i >= 0; i = schematics
				.nextSetBit(i + 1)) {
			schematicCount++;
		}
		packetSize += (8 * schematicCount);

		for (int i = 0; i < vFriendsList.size(); i++) {
			packetSize += (vFriendsList.elementAt(i).getName().length() + 2);
		}

		for (int i = 0; i < vIgnoreList.size(); i++) {
			packetSize += (vIgnoreList.elementAt(i).getName().length() + 2);
		}
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0); // Sequence
		sOut.setUpdateType(Constants.OBJECT_UPDATE);
		sOut.writeInt(Constants.BaselinesMessage);
		sOut.writeLong(player.getID());
		sOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_PLAY]);
		sOut.writeByte((byte) 9);
		sOut.writeInt(packetSize);
		sOut.writeShort(0x13); // Operand count
		sOut.writeInt(vCertifications.size()); // vID 0
		sOut.writeInt(player.getCertificationsListUpdateCount(false));
		for (int i = 0; i < vCertifications.size(); i++) {
			String str = vCertifications.elementAt(i);
			sOut.writeUTF(str);
		}
		sOut.writeInt(0); // vID 1 -- Experimentation / manufacturing schematic
							// flag.
		sOut.writeInt(player.getCraftingStage()); // vID 2 -- Crafting stage
		sOut.writeLong(0); // vID 3 -- Crafting station ID?

		sOut.writeInt(schematicCount); // vID 4 -- Crafting Schematic List
		int counter = player.getSchematicUpdateCount(false);
		// System.out.println("Play9 packet -- schematic update counter = " +
		// counter);
		// sOut.writeInt(player.getSchematicUpdateCount());
		sOut.writeInt(counter);
		CraftingSchematic cs;

		for (int i = schematics.nextSetBit(0); i >= 0; i = schematics
				.nextSetBit(i + 1)) {
			// System.out.println("Index["+i+"]");
			cs = DatabaseInterface.getSchematicByIndex(i);
			sOut.writeInt(cs.getCRC());// (cs.getCRC()); // Could also be the
										// CRC?
			sOut.writeInt(cs.getCRC()); // Could also be number of
										// experimentation points...
		}

		sOut.writeInt(0); // vID 5 -- Number of available experimentation points
		sOut.writeInt(0); // vID 6 Unknown. Species data???

		// vID 7 -- Friends List
		sOut.writeInt(vFriendsList.size());
		sOut.writeInt(player.getFriendsListUpdateCount(false));
		PlayerFriends f;
		for (int i = 0; i < vFriendsList.size(); i++) {
			f = vFriendsList.elementAt(i);
			sOut.writeUTF(f.getName());
		}

		// vID 8 -- Ignore list
		sOut.writeInt(vIgnoreList.size());
		sOut.writeInt(player.getIgnoreListUpdateCount(false));
		for (int i = 0; i < vIgnoreList.size(); i++) {
			f = vFriendsList.elementAt(i);
			sOut.writeUTF(f.getName());
		}

		// vID 9 -- Language ID
		sOut.writeInt(player.getCurrentLanguageID());

		// vID 10 -- Current Food Fullness
		sOut.writeInt(player.getCurrentFoodFullness());
		// vID 11 -- Max Food Fullness
		sOut.writeInt(player.getMaxFoodFullness());
		// vID 12 -- Current Drink Fullness
		sOut.writeInt(player.getCurrentDrinkFullness());
		// vID 13 -- Max Drink Fullness
		sOut.writeInt(player.getMaxDrinkFullness());

		// vID 14 -- Unknown list
		sOut.writeInt(0);
		sOut.writeInt(0);

		// vID 15 -- Unknown list
		sOut.writeInt(0);
		sOut.writeInt(0);

		// vID 16 -- Player Jedi Flag.
		if (player.getMyPlayer().getClient().getUpdateThread().getIsDeveloper()) {
			sOut.writeInt(8);
		} else {
			sOut.writeInt(player.getJediFlag());
		}
		sOut.flush();
		return sOut.getBuffer();
	}

	// Baselines for Manufacturing Schematics -- used in crafting process.

	protected static byte[] buildBaselineMSCO3(
			ManufacturingSchematic schematic, Player player) throws IOException {
		// int packetSize = 0;
		String sFileName = schematic.getSTFFileName();
		String sFileIdentifier = schematic.getSTFFileIdentifier();
		String sItemName = schematic.getCustomName();
		String crafting = "crafting";
		String complexity = "complexity";
		// packetSize = 59;
		// if (sFileName != null) {
		// packetSize += sFileName.length();
		// }
		// if (sFileIdentifier != null) {
		// packetSize += sFileIdentifier.length();
		// }
		// if (sItemName != null) {
		// packetSize += sItemName.length() * 2;
		// }

		// packetSize += crafting.length() + complexity.length();

		// String sFullName = player.getFullName();
		String sFullName = schematic.getCraftingSchematic()
				.getCraftedItemIFFFilename();
		// packetSize += (sFullName.length() * 2);
		SOEOutputStream buff = new SOEOutputStream(new ByteArrayOutputStream());
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(schematic.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MSCO]);
		dOut.writeByte(3);

		// dOut.writeInt(packetSize);
		buff.writeShort(9); // num operands;
		buff.writeFloat(schematic.getBaseCraftingComplexity()); // vID 0
		buff.writeUTF(sFileName); // vID 1
		buff.writeInt(0);
		buff.writeUTF(sFileIdentifier);
		buff.writeUTF16(sItemName); // vID 2
		buff.writeInt(0); // vID 3
		buff.writeInt(10);// seems to be constant // vID 4

		// vID 5 -- Experimental properties list.
		buff.writeInt(1);// seems to be constant
		buff.writeInt(1);// seems to be constant
		buff.writeByte(Constants.DELTA_CREATING_ITEM);
		buff.writeUTF(crafting);
		buff.writeInt(0);
		buff.writeUTF(complexity);
		buff.writeFloat(schematic.getBaseCraftingComplexity());

		buff.writeUTF16(sFullName); // vID 6
		buff.writeInt(0x19); // 0x19 = 25 why 25 what is the significance of
								// 25????? // vID 7
		buff.writeFloat(8); // vID 8
		buff.flush();
		byte[] body = buff.getBuffer();
		dOut.writeInt(body.length);
		dOut.write(body);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildBaselineMSCO6(ManufacturingSchematic schematic)
			throws IOException {
		int packetSize = 16;
		// int packetSize = 17;
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(schematic.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MSCO]);
		dOut.writeByte(6);
		dOut.writeInt(packetSize);
		dOut.writeShort(7);// Said 7 works with 6
		dOut.writeShort(0x76);// Oper0 //dec 118 //2 bytes
		dOut.writeUTF(null);// possible UTF//Oper1 //2 bytes
		dOut.writeInt(0);// Oper2 //4 bytes
		// dOut.writeUTF(null);//Oper3 //2 bytes
		dOut.writeInt(schematic.getCraftingSchematic().getCRC()); // Oper4 //4
																	// bytes
		dOut.writeShort(schematic.getIngredientUpdateCounter(false));// Oper5
																		// //2
																		// bytes
		// 16 bytes total
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildBaselineMSCO7(ManufacturingSchematic schematic)
			throws IOException {
		/*
		 * System.out.println("MSCO7 baseline building."); StackTraceElement[]
		 * stackTrace = Thread.currentThread().getStackTrace(); for (int i = 0;
		 * i < stackTrace.length; i++) { System.out.println(stackTrace[i]); }
		 */
		int packetSize = 145;
		// vID 0
		CraftingSchematicComponent[] components = schematic
				.getSchematicComponentData();
		if (components != null) {
			for (int i = 0; i < components.length; i++) {
				CraftingSchematicComponent thisComponent = components[i];
				String sSTFFileName = thisComponent.getSTFFileName();
				String sSTFFileIdentifier = thisComponent
						.getSTFFileIdentifier();
				packetSize += sSTFFileName.length() + 2;
				packetSize += sSTFFileIdentifier.length() + 2;
				packetSize += 4;
			}
		}

		// vID 1
		int[] vID1 = schematic.getVID1IntArray();
		if (vID1 != null) {
			packetSize += (4 * vID1.length);
		}

		// vID 2
		long[][] vObjectIDListBySlotID = schematic.getAllObjectIDsInSlots();
		// We need to track not only if any slots have object IDs but which ones
		// have how many, at transmission of tihs baseline.
		int[] numObjectIDsBySlot = new int[vObjectIDListBySlotID.length];
		int numSlotsHaveObjectIDs = 0;
		int numObjectIDsInSlots = 0;
		packetSize += (4 * vObjectIDListBySlotID.length);
		if (vObjectIDListBySlotID != null) {
			// Need to first see if ANY ID in here is valid
			// packetSize += (4 * vObjectIDListBySlotID.length);
			// If it's not empty, there must be 1 value in it.
			for (int i = 0; i < vObjectIDListBySlotID.length; i++) {
				if (vObjectIDListBySlotID[i] != null) {
					for (int j = 0; j < vObjectIDListBySlotID[i].length; j++) {
						if (vObjectIDListBySlotID[i][j] != 0) {
							numObjectIDsInSlots++;

						}
					}
				}
				if (numObjectIDsInSlots > 0) {
					numSlotsHaveObjectIDs++;
					numObjectIDsBySlot[i] = numObjectIDsInSlots;
					packetSize += (8 * numObjectIDsInSlots);
				}
				numObjectIDsInSlots = 0;
			}
		}

		// vID 3
		int[] vIngredientQuantitiesInSlots = schematic
				.getSlotResourceQuantityInserted();
		if (vIngredientQuantitiesInSlots != null) {
			packetSize += (4 * vIngredientQuantitiesInSlots.length);
		}

		// vID 4
		int[] vID4 = schematic.getVID4IntArray();
		if (vID4 != null) {
			packetSize += (4 * vID4.length);
		}

		// vID 5
		int[] vID5 = schematic.getVID5IntArray();
		if (vID5 != null) {
			packetSize += (4 * vID5.length);
		}

		// vID 6
		int[] vID6 = schematic.getVID6IntArray();
		if (vID6 != null) {
			packetSize += (4 * vID6.length);
		}

		// vID 7
		byte vID7 = schematic.getVID7();

		// vID 8
		CraftingExperimentationAttribute[] vAttributes = schematic
				.getExperimentalAttributes();
		final String crafting = "crafting";
		int numActualAttributes = 0;
		if (vAttributes != null) {
			for (int i = 0; i < vAttributes.length; i++) {
				if (vAttributes[i] != null) {
					String stfFileIdentifier = vAttributes[i]
							.getStfFileIdentifier();
					if (stfFileIdentifier != null) {
						numActualAttributes++;
						packetSize += crafting.length() + 2;
						packetSize += vAttributes[i].getStfFileIdentifier()
								.length() + 2;
						packetSize += 4;
					}
				}
			}
		}

		// vID 9
		float[] vID9 = schematic.getVID9CurrentExperimentalValueArray();
		if (vID9 != null) {
			packetSize += (4 * vID9.length);
		}

		// vID 10
		float[] vID10 = schematic.getVID10FloatArray();
		if (vID10 != null) {
			packetSize += (4 * vID10.length);
		}

		// vID 11
		float[] vID11 = schematic.getVID11FloatArray();
		if (vID11 != null) {
			packetSize += (4 * vID11.length);
		}

		// vID 12
		float[] vID12 = schematic.getVID12CurrentExperimentationArray();
		if (vID12 != null) {
			packetSize += (4 * vID12.length);
		}

		// vID 13
		String[] vID13 = schematic.getVID13StringArray();
		if (vID13 != null) {
			for (int i = 0; i < vID13.length; i++) {
				packetSize += (5 + vID13[i].length());
			}

		}

		// vID 14
		int[] vID14 = schematic.getVID14IntArray();
		if (vID14 != null) {
			packetSize += (4 * vID14.length);
		}

		// vID 15
		int[] vID15 = schematic.getVID15IntArray();
		if (vID15 != null) {
			packetSize += (4 * vID15.length);
		}

		// vID 16
		int[] vID16 = schematic.getVID16IntArray();
		if (vID16 != null) {
			packetSize += (4 * vID16.length);
		}

		// vID17
		byte vID17 = schematic.getVID17();

		// vID 18
		float vID18 = schematic.getVID18();

		// vID 19
		int[] vID19 = schematic.getVID19IntArray();
		if (vID19 != null) {
			packetSize += (4 * vID19.length);
		}

		// vID 20
		byte vID20 = schematic.getVID20();

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(schematic.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MSCO]);
		dOut.writeByte(7);

		// dOut.writeInt(packetSize);

		SOEOutputStream buff = new SOEOutputStream(new ByteArrayOutputStream());

		buff.writeShort(0x15); // num Operands

		// vID 0
		if (components != null) {
			buff.writeInt(components.length);
			buff
					.writeInt(schematic
							.getSchematicComponentDataUpdateCount(false));
			for (int i = 0; i < components.length; i++) {
				CraftingSchematicComponent dsi = components[i];
				buff.writeUTF(dsi.getSTFFileName());
				buff.writeInt(0);
				buff.writeUTF(dsi.getSTFFileIdentifier());
			}
		} else {
			buff.writeLong(0);
		}
		// vID 1
		if (vID1 != null) {
			buff.writeInt(vID1.length);
			buff.writeInt(schematic.getVID1IntArrayUpdateCount(false));
			for (int i = 0; i < vID1.length; i++) {
				buff.writeInt(vID1[i]);
			}
		} else {
			buff.writeLong(0);
		}

		// vID 2 -- Object ID list
		if (vObjectIDListBySlotID != null) {
			buff.writeInt(vObjectIDListBySlotID.length);
			buff.writeInt(schematic.getObjectIdBySlotUpdateCount(false));
			for (int i = 0; i < vObjectIDListBySlotID.length; i++) {
				if (vObjectIDListBySlotID[i] != null) {
					buff.writeInt(numObjectIDsBySlot[i]);
					if (numObjectIDsBySlot[i] > 0) {
						for (int j = 0; j < vObjectIDListBySlotID[i].length; j++) {
							if (vObjectIDListBySlotID[i][j] != 0) {
								buff.writeLong(vObjectIDListBySlotID[i][j]);
							}
						}
					}
				} else {
					buff.writeInt(0);
				}
			}
		} else {
			buff.writeLong(0);
		}

		// vID 3
		if (vIngredientQuantitiesInSlots != null) {
			buff.writeInt(vIngredientQuantitiesInSlots.length);
			buff.writeInt(schematic.getSlotResourceQuantityUpdateCount(false));
			for (int i = 0; i < vIngredientQuantitiesInSlots.length; i++) {
				buff.writeInt(vIngredientQuantitiesInSlots[i]);
			}
		} else {
			buff.writeLong(0);
		}

		// vID 4
		if (vID4 != null) {
			buff.writeInt(vID4.length);
			buff.writeInt(schematic.getVID4IntArrayUpdateCount(false));
			for (int i = 0; i < vID4.length; i++) {
				buff.writeInt(vID4[i]);
			}
		} else {
			buff.writeLong(0);
		}

		// vID 5
		if (vID5 != null) {
			buff.writeInt(vID5.length);
			buff.writeInt(schematic.getVID5IntArrayUpdateCount(false));
			for (int i = 0; i < vID5.length; i++) {
				buff.writeInt(vID5[i]);
			}
		} else {
			buff.writeLong(0);
		}

		// vID 6
		if (vID6 != null) {
			buff.writeInt(vID6.length);
			buff.writeInt(schematic.getVID6IntArrayUpdateCount(false));
			for (int i = 0; i < vID6.length; i++) {
				buff.writeInt(vID6[i]);
			}
		} else {
			buff.writeLong(0);
		}

		buff.writeByte(vID7); // no idea vID 7

		// vID 8
		buff.writeInt(numActualAttributes);
		buff.writeInt(schematic.getExperimentalAttributesUpdateCount(false));
		for (int i = 0; i < vAttributes.length; i++) {
			if (vAttributes[i] != null) {
				String title = vAttributes[i].getStfFileIdentifier();
				if (title != null) {

					buff.writeUTF(crafting); // I think this is always
												// "crafting"
					buff.writeInt(0);
					buff.writeUTF(title);
				}
			}
		}

		// vID 9
		if (vID4 != null) {
			buff.writeInt(vID9.length);
			buff.writeInt(schematic
					.getVID9CurrentExperimentalValueUpdateCount(false));
			for (int i = 0; i < vID9.length; i++) {
				buff.writeFloat(vID9[i]);
			}
		} else {
			buff.writeLong(0);
		}

		// vID 10
		if (vID10 != null) {
			buff.writeInt(vID10.length);
			buff.writeInt(schematic.getVID10FloatArrayUpdateCount(false));
			for (int i = 0; i < vID10.length; i++) {
				buff.writeFloat(vID10[i]);
			}
		} else {
			buff.writeLong(0);
		}

		// vID 11
		if (vID11 != null) {
			buff.writeInt(vID11.length);
			buff.writeInt(schematic.getVID11FloatArrayUpdateCount(false));
			for (int i = 0; i < vID11.length; i++) {
				buff.writeFloat(vID11[i]);
			}
		} else {
			buff.writeLong(0);
		}
		// vID 12
		if (vID12 != null) {
			buff.writeInt(vID12.length);
			buff.writeInt(schematic
					.getVID12MaxExperimentationArrayUpdateCount(false));
			for (int i = 0; i < vID12.length; i++) {
				buff.writeFloat(vID12[i]);
			}
		} else {
			buff.writeLong(0);
		}

		// vID 13
		if (vID13 != null) {
			buff.writeInt(vID13.length);
			buff.writeInt(schematic.getVID13StringArrayUpdateCount(false));
			for (int i = 0; i < vID13.length; i++) {
				buff.writeUTF(vID13[i]);
			}
		} else {
			buff.writeLong(0);
		}

		// vID 14
		if (vID14 != null) {
			buff.writeInt(vID14.length);
			buff.writeInt(schematic.getVID14IntArrayUpdateCount(false));
			for (int i = 0; i < vID14.length; i++) {
				buff.writeInt(vID14[i]);
			}
		} else {
			buff.writeLong(0);
		}

		// vID 15
		if (vID15 != null) {
			buff.writeInt(vID15.length);
			buff.writeInt(schematic.getVID15IntArrayUpdateCount(false));
			for (int i = 0; i < vID15.length; i++) {
				buff.writeInt(vID15[i]);
			}
		} else {
			buff.writeLong(0);
		}

		// vID 16
		if (vID16 != null) {
			buff.writeInt(vID16.length);
			buff.writeInt(schematic.getVID16IntArrayUpdateCount(false));
			for (int i = 0; i < vID16.length; i++) {
				buff.writeInt(vID16[i]);
			}
		} else {
			buff.writeLong(0);
		}

		buff.writeByte(vID17);
		buff.writeFloat(vID18);
		// vID 19
		if (vID19 != null) {
			buff.writeInt(vID19.length);
			buff.writeInt(schematic.getVID19IntArrayUpdateCount(false));
			for (int i = 0; i < vID19.length; i++) {
				buff.writeInt(vID19[i]);
			}
		} else {
			buff.writeLong(0);
		}

		buff.writeByte(vID20);
		buff.flush();
		byte[] body = buff.getBuffer();
		dOut.writeInt(body.length);
		dOut.write(body);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildBaselineMSCO8(ManufacturingSchematic t)
			throws IOException {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0);
		sOut.setUpdateType(Constants.OBJECT_UPDATE);
		sOut.writeInt(Constants.BaselinesMessage);
		sOut.writeLong(t.getID());
		sOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MSCO]);
		sOut.writeByte((byte) 8);
		sOut.writeInt(2); // Packet size.
		sOut.writeShort(0); // Operand count
		sOut.flush();
		return sOut.getBuffer();
	}

	protected static byte[] buildBaselineMSCO9(ManufacturingSchematic t)
			throws IOException {
		SOEOutputStream sOut = new SOEOutputStream(new ByteArrayOutputStream());
		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0);
		sOut.setUpdateType(Constants.OBJECT_UPDATE);
		sOut.writeInt(Constants.BaselinesMessage);
		sOut.writeLong(t.getID());
		sOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MSCO]);
		sOut.writeByte((byte) 9);
		sOut.writeInt(2); // / Packet length
		sOut.writeShort(0); // Operand count
		sOut.flush();
		return sOut.getBuffer();
	}

	// Baselines for the Weapon class.
	protected static byte[] buildBaselineWEAO3(Weapon weapon)
			throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		String sSTFFilename = weapon.getSTFFileName();
		String sSTFFileIdentifier = weapon.getSTFFileIdentifier();
		String sName = weapon.getName();

		int packetSize = 85;
		if (sSTFFileIdentifier != null) {
			packetSize += sSTFFileIdentifier.length();
		}
		if (sSTFFilename != null) {
			packetSize += sSTFFilename.length();
		}
		if (sName != null) {
			packetSize += (sName.length() * 2);
		}
		byte[] customizationData = weapon.getCustomData();
		if (customizationData != null) {
			packetSize += customizationData.length;
		}
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(weapon.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_WEAO]);
		dOut.writeByte(3);
		dOut.writeInt(packetSize);
		dOut.writeShort(17); // Operand count
		dOut.writeFloat(0); // unknown -- operand 0.

		dOut.writeUTF(sSTFFilename); // Operand 1
		dOut.writeInt(0);
		dOut.writeUTF(sSTFFileIdentifier);

		dOut.writeUTF16(sName); // Operand 2
		//dOut.writeInt(weapon.getSkillRequirement()); // Operand 3
		dOut.writeInt(weapon.getNumberOfInventorySlotsTaken()); // Number of inventory spaces taken.  Operand 3.
		if (customizationData != null) {
			dOut.writeShort(customizationData.length); // Operand 4
			dOut.write(customizationData);
		} else {
			dOut.writeShort(0);
		}

		dOut.writeLong(0); // Operand 5 

		dOut.writeInt(0); // Operand 6 // Bitmask
		dOut.writeInt(weapon.getStackSize()); // Most often this is 1, but for
												// grenades it can be higher.
												// Operand 7

		dOut.writeInt(weapon.getConditionDamage()); // Operand 8
		dOut.writeInt(weapon.getMaxCondition()); // Operand 9
		dOut.writeByte(0); // Operand 10
		// These 2 ints are 1 operand? /me shrugs
		dOut.writeLong(0x3E8000000l); // Unknown value // Operand 11
		//dOut.writeInt(3); // ??? // Operand 12
		dOut.writeLong(0); // Operand 13
		dOut.writeFloat(weapon.getRefireDelay()); // Operand 14
		dOut.writeInt(weapon.getSkillRequirement()); // Operand 15
		//dOut.writeInt(1);
		dOut.writeLong(0); // Operand 16
		dOut.writeInt(0); // Operand 17
		dOut.flush();
		return bOut.toByteArray();
	}

	protected static byte[] buildBaselineWEAO6(Weapon weapon)
			throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(weapon.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_WEAO]);
		dOut.writeByte(6);
		dOut.writeInt(14); // 14 bytes
		dOut.writeShort(2); // 2 operands
		dOut.writeInt(61); // TODO -- Discover what this is / is supposed to be.
		dOut.writeInt(0);
		dOut.writeInt(0);
		dOut.flush();
		return bOut.toByteArray();
	}

	protected static byte[] buildBaselineWEAO8(Weapon weapon)
			throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(weapon.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_WEAO]);
		dOut.writeByte(8);
		dOut.writeInt(2);
		dOut.writeShort(0);
		dOut.flush();
		return bOut.toByteArray();
	}

	protected static byte[] buildBaselineWEAO9(Weapon weapon)
			throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(weapon.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_WEAO]);
		dOut.writeByte(9);
		dOut.writeInt(2);
		dOut.writeShort(0);
		dOut.flush();
		return bOut.toByteArray();
	}

	// Baselines message for Static Objects
	protected static byte[] buildBaselineSTAO3(StaticItem o) throws IOException {
		int packetSize = 22;
		String sSTFName = o.getSTFFileName();
		String sSTFIdentifier = o.getSTFFileIdentifier();
		String sName = null; // o.getName();

		if (sSTFName != null) {
			packetSize += sSTFName.length();
		}
		if (sSTFIdentifier != null) {
			packetSize += sSTFIdentifier.length();
		}
		if (sName != null) {
			packetSize += sName.length();
		}
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(o.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_STAO]);
		dOut.writeByte(3);
		dOut.writeInt(packetSize);
		dOut.writeShort(4);
		dOut.writeInt(0);
		dOut.writeUTF(sSTFName);
		dOut.writeInt(0);
		dOut.writeUTF(sSTFIdentifier);
		dOut.writeUTF16(sName); // This will eventually be the static object's
								// custom name. ATM ALL WE GET IS CRASHY CRASHY
		dOut.writeInt(0xFF); // Odd number to write...
		dOut.flush();
		return bOut.toByteArray();
	}

	protected static byte[] buildBaselineSTAO6(StaticItem o) throws IOException {
		int packetSize = 29;
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(o.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_STAO]);
		dOut.writeByte(6);
		dOut.writeInt(packetSize);
		dOut.writeShort(2);
		dOut.writeInt(74); // Unknown
		dOut.writeUTF("string_id_table");
		dOut.writeInt(0);
		dOut.writeUTF("");// object name
		dOut.flush();
		return bOut.toByteArray();
	}

	// Intangible Baselines.
	// Items which get put into the Datapad go here, apparently. Such as swoop
	// deeds, ship deeds, creature deeds.
	protected static byte[] buildBaselineITNO3(IntangibleObject item)
			throws IOException {
		int packetSize = 26;
		String stfName = item.getSTFFileName();
		String objectName = item.getSTFFileIdentifier();
		String customName = item.getCustomName();
		if (stfName != null) {
			packetSize += stfName.length();
		} else {
			stfName = "monster_name";
		}
		if (objectName != null) {
			packetSize += objectName.length();
		} else {
			objectName = "speederbike_swoop";
		}
		if (customName != null) {
			packetSize += customName.length() * 2;
		}
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(item.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_ITNO]);
		dOut.writeByte(3);
		dOut.writeInt(packetSize);
		dOut.writeShort(5); // Operand count
		dOut.writeFloat(1.0f);
		dOut.writeUTF(stfName);
		dOut.writeInt(0);
		dOut.writeUTF(objectName);
		dOut.writeUTF16(customName);
		// dOut.writeInt(0);
		dOut.writeInt(1); // Is this some sort of spawned / not spawned
							// variable? Why update this if it's already a 1?
		dOut.writeInt(0);
		dOut.flush();
		return bOut.toByteArray();
	}

	protected static byte[] buildBaselineITNO6(IntangibleObject item)
			throws IOException {
		// int packetSize = 14 + item.getNameParamaterInSTF().length() +
		// item.getObjectName().length();
		int packetSize = 14;
		String stfName = item.getSTFDetailName();
		String objectName = item.getSTFDetailIdentifier();
		if (stfName != null) {
			packetSize += stfName.length();
		} else {
			stfName = "monster_name";
		}
		if (objectName != null) {
			packetSize += objectName.length();
		} else {
			objectName = "speederbike_swoop";
		}

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(item.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_ITNO]);
		dOut.writeByte(6);
		dOut.writeInt(packetSize);
		dOut.writeShort(2); // Operand count
		dOut.writeInt(74); // TODO -- Find what this does.
		// dOut.writeUTF(item.getNameParamaterInSTF());
		dOut.writeUTF(stfName);
		dOut.writeInt(0);
		dOut.writeUTF(objectName);
		dOut.flush();
		return bOut.toByteArray();
	}

	protected static byte[] buildBaselineITNO8(IntangibleObject item)
			throws IOException {
		int packetSize = 2;
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(item.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_ITNO]);
		dOut.writeByte(8);
		dOut.writeInt(packetSize);
		dOut.writeShort(0);
		dOut.flush();
		return bOut.toByteArray();
	}

	protected static byte[] buildBaselineITNO9(IntangibleObject item)
			throws IOException {
		int packetSize = 2;
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(item.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_ITNO]);
		dOut.writeByte(9);
		dOut.writeInt(packetSize);
		dOut.writeShort(0);
		dOut.flush();
		return bOut.toByteArray();
	}

	protected static byte[] buildBaselineSCLT3(Cell cell) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream sOut = new SOEOutputStream(bOut);

		int packetSize = 28;
		String stfName = cell.getSTFFileName();
		if (stfName != null) {
			packetSize += stfName.length();
		}
		String stfIdentifier = cell.getSTFFileIdentifier();
		if (stfIdentifier != null) {
			packetSize += stfIdentifier.length();
		}

		sOut.setOpcode(Constants.SOE_CHL_DATA_A);
		sOut.setSequence(0); // Sequence.
		sOut.setUpdateType(Constants.OBJECT_UPDATE);
		sOut.writeInt(Constants.BaselinesMessage);
		sOut.writeLong(cell.getID());
		sOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_SCLT]);
		sOut.writeByte((byte) 3);

		sOut.writeInt(packetSize);
		sOut.writeShort(5); // Operand count
		sOut.writeInt(0x00000000);// op0

		sOut.writeUTF(stfName); // For hair, this has to be "hair_name"//op1
		sOut.writeInt(0); // Unknown
		sOut.writeUTF(stfIdentifier); // For hair, this has to be "hair"
		// op2
		sOut.writeUTF16(""); // For hair this can be null -- won't really affect
								// anything if it isn't.//op2
		// op 3 and 4???
		sOut.writeInt(0); // Unknown;//op3

		sOut.writeInt(cell.getCellNum());// op5

		/*
		 * sOut.writeInt(0x1B); sOut.writeShort(6); //6 Operands //op0
		 * sOut.writeInt(0); //op1 sOut.writeShort(0); sOut.writeInt(0);
		 * sOut.writeShort(0); //op2 sOut.writeInt(0); //op3 sOut.writeShort(0);
		 * //op4 sOut.writeShort(0); //op 5 sOut.writeByte(1); //permissions
		 * bool? //op6 sOut.writeInt(cell.getCellNum());
		 */
		sOut.flush();
		return sOut.getBuffer();
	}

	protected static byte[] buildBaselineSCLT6(Cell c) throws IOException {
		int packetSize = 6;
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0); // Sequence.
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(c.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_SCLT]);
		dOut.writeByte((byte) 6);

		dOut.writeInt(packetSize);
		dOut.writeShort(1);// opcnt
		dOut.writeInt(0x42); // 66DEC Unknown value. 0x42 ???

		/*
		 * dOut.writeInt(0x0E); dOut.writeShort(2);//opcnt dOut.writeInt(0x7D);
		 * dOut.writeInt(0); dOut.writeInt(0);
		 */
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildBaselineRCNO3(ResourceContainer r)
			throws IOException {
		int packetSize = 61;

		String sSTFName = r.getSTFFileName();
		String sSTFIdentifier = r.getSTFFileIdentifier(); // .getIFFFileName();
		String sName = r.getName();
		if (sSTFName != null) {
			packetSize += sSTFName.length();
		}
		if (sSTFIdentifier != null) {
			packetSize += sSTFIdentifier.length();
		}
		if (sName != null) {
			packetSize += (sName.length() * 2);
		}
		// System.out.println("Resource Container RCNO 3 Strings");
		// System.out.println("sSTFName: " + sSTFName);
		// System.out.println("sSTFIdentifier: " + sSTFIdentifier);
		// System.out.println("sName: " + sName);
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(r.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_RCNO]);
		dOut.writeByte(3);
		dOut.writeInt(packetSize);
		dOut.writeShort(0x0F); // Operand count
		// vID 0
		dOut.writeFloat(1.0f);
		// vID 1
		dOut.writeUTF(sSTFName);// sSTFName: resource_container_n
		dOut.writeInt(0);
		dOut.writeUTF(sSTFIdentifier);// sSTFIdentifier: wind_energy_yavin4
		// vID 2
		dOut.writeUTF16(sName);// sName: Wind Energy
		// vID 3
		dOut.writeInt(1); // Unknown
		// vID 4
		dOut.writeShort(0);
		// vID 5
		dOut.writeInt(0);
		// vID 6
		dOut.writeInt(0);
		// vID 7
		dOut.writeInt(1);
		// vID 8
		dOut.writeInt(4);
		// vID 9
		dOut.writeInt(r.getConditionDamage());
		// vID 10
		dOut.writeInt(r.getMaxCondition());
		// vID 11
		dOut.writeBoolean(true);
		// vID 12
		dOut.writeInt(r.getStackQuantity());
		// vID 13
		dOut.writeLong(r.getResourceSpawnID()); // Unknown

		dOut.flush();
		// byte[] buffer = dOut.getBuffer();
		// PacketUtils.printPacketToScreen(buffer,
		// "Resource Container 3 Baseline");
		return dOut.getBuffer();
	}

	protected static byte[] buildBaselineRCNO6(ResourceContainer r)
			throws IOException {
		int packetSize = 24;
		String sName = r.getName();
		if (sName != null) {
			packetSize += (sName.length() * 2);
		}
		String sIFFFileName = r.getResourceType(); // r.getIFFFileName();
		if (sIFFFileName != null) {
			packetSize += sIFFFileName.length();
		}
		// System.out.println("Resource Container RCNO 6 Strings");
		// System.out.println("sIFFFileName: " + sIFFFileName);
		// System.out.println("sName: " + sName);
		// System.out.println("sType: " + r.getResourceType());
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(r.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_RCNO]);
		dOut.writeByte(6);
		dOut.writeInt(packetSize);
		dOut.writeShort(5);
		dOut.writeUTF("");
		dOut.writeInt(0);
		dOut.writeUTF("");
		dOut.writeUTF16("");
		dOut.writeInt(Constants.MAX_STACK_SIZE);
		dOut.writeUTF(sIFFFileName);// sIFFFileName:
									// object/resource_container/shared_resource_container_energy_liquid.iff
		dOut.writeUTF16(sName);// sName: Wind Energy
		dOut.flush();
		// byte[] buffer = dOut.getBuffer();
		// PacketUtils.printPacketToScreen(buffer,
		// "Resource Container 6 Baseline");
		return dOut.getBuffer();
	}

	// -----------------Mission related baselines---------------------
	protected static byte[] buildBaselineMISO3(MissionObject m)
			throws IOException {
		int iPacketsize = 158;// base packet size
		if (m.getSTFDetailIdentifier() != null
				|| m.getSTFDetailIdentifier().isEmpty()) {
			iPacketsize += m.getSTFDetailIdentifier().length();
		}
		if (m.getSTFDetailName() != null || m.getSTFDetailName().isEmpty()) {
			iPacketsize += m.getSTFDetailName().length();
		}
		if (m.getSMissionGiverName() != null) {
			iPacketsize += (m.getSMissionGiverName().length() * 2);
		} else {
			m.setSMissionGiverName("");
		}
		if (m.getSMissionSTFString() != null) {
			iPacketsize += (m.getSMissionSTFString().length() * 2);
		} else {
			m.setSMissionSTFString("");
		}
		if (m.getSMissionSTFDetailIdentifier() != null) {
			iPacketsize += m.getSMissionSTFDetailIdentifier().length();
		} else {
			m.setSMissionSTFDetailIdentifier("");
		}
		if (m.getSMissionSTFTextIdentifier() != null) {
			iPacketsize += m.getSMissionSTFTextIdentifier().length();
		} else {
			m.setSMissionSTFTextIdentifier("");
		}
		if (m.getMissionTargetDisplayString() != null) {
			iPacketsize += m.getMissionTargetDisplayString().length();
		} else {
			m.setMissionTargetDisplayString("");
		}
		// boolean debug = true;

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(m.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MISO]);
		dOut.writeByte(3);
		dOut.writeInt(iPacketsize);// (0xC3);

		dOut.writeShort(17);// 2
		dOut.writeFloat(1);// 4//orientation this object will show up in.
							// MUAHAHAHAHAHAHAHAHA
		dOut.writeUTF(m.getSTFDetailName());// 2
		dOut.writeInt(0);// 4
		dOut.writeUTF(m.getSTFDetailIdentifier());// 2
		dOut.writeInt(0);// 4

		// if(!debug)
		// {
		// dOut.write(new byte[130]);//constant empty when mission objects are
		// unused always sent empty and then updated via deltasmessage
		// }
		// else
		{

			dOut.writeInt(0);// 4
			dOut.writeInt(0);// 4
			// --------------------------
			dOut.writeInt(m.getIDiffcultyLevel());// 4 // 02 00 00 00 seen as
													// int 2 seen also as 5A 00
													// 00 00 // 0x0000005A = 90
													// // difficulty level
			// --------------------------
			// pickup coordinates
			if (m.getIMissionType() == Constants.MISSION_TYPE_DELIVER) {
				dOut.writeFloat(m.getPickupX());// 4 // seen as 7B E5 C4 44 //
												// 0x44C4E57B
				dOut.writeFloat(m.getPickupZ());// 4
				dOut.writeFloat(m.getPickupY());// 4 // seen as 13 E2 C7 C5 //
												// 0xC5C7E213
				dOut.writeFloat(0);// 4 space??
				dOut.writeFloat(0);// 4 space??????
				if (m.getIPickupPlanetID() == -1) {
					dOut.writeInt(0);// 4
				} else {
					dOut.writeInt(Constants.PlanetCRCForWaypoints[m
							.getIPickupPlanetID()]);// 4
				}
			} else {
				dOut.writeFloat(0);// 4 // seen as 7B E5 C4 44 // 0x44C4E57B
				dOut.writeFloat(0);// 4
				dOut.writeFloat(0);// 4 // seen as 13 E2 C7 C5 // 0xC5C7E213
				dOut.writeFloat(0);// 4 space??
				dOut.writeFloat(0);// 4 space??????
				dOut.writeInt(0);// 4 planet crc pickup
			}

			// --------------------------
			dOut.writeUTF16(m.getSMissionGiverName());// 4 + length * 2
			// --------------------------
			dOut.writeInt(m.getIMissionPayout());// 4 // payout
			// --------------------------
			// delivery and or mission coordinates
			dOut.writeFloat(m.getMissionX());// 4// 29 79 4B 45 //x
			dOut.writeFloat(m.getMissionZ());// 4// 00 00 00 00 //z
			dOut.writeFloat(m.getMissionY());// 4// 7B BA 59 C5 //y
			dOut.writeFloat(0);// 4// 00 00 00 00 //space? coord?
			dOut.writeFloat(0);// 4// 00 00 00 00 //space? coord?
			if (m.getMissionPlanetID() == -1) {
				dOut.writeInt(0);// 4 // 19 CA 4E A9 //planet crc
			} else {
				dOut.writeInt(Constants.PlanetCRCForWaypoints[m
						.getMissionPlanetID()]);// 4 // 19 CA 4E A9 //planet crc
			}
			// --------------------------
			dOut.writeInt(m.getIDisplayObjectCRC());// 4
			dOut.writeUTF(m.getSMissionSTFString());// 2 + length
			dOut.writeInt(0);// 4
			dOut.writeUTF(m.getSMissionSTFDetailIdentifier());// 2 + length
			dOut.writeUTF(m.getSMissionSTFString());// 2 + length
			dOut.writeInt(0);// 4
			dOut.writeUTF(m.getSMissionSTFTextIdentifier());// 2 + length
			dOut.writeInt(5);// 4 // seen as int 5 seems constant
			dOut.writeInt(m.getIMissionTypeCRC());// 4 // seen as C6 7E C2 E5 //
													// 0xE5C27EC6
			// --------------------------
			dOut.writeUTF(m.getMissionTargetDisplayString());// 2 + length
			// --------------------------
			dOut.writeInt(0);// 4
			dOut.writeInt(0);// 4
			dOut.writeInt(0);// 4
			dOut.writeInt(0);// 4
			dOut.writeInt(0);// 4
			dOut.writeInt(0);// 4
			dOut.writeInt(0);// 4
			dOut.writeInt(0);// 4
			// --------------------------
			// TOTAL 158 + Lengths
		}
		TangibleItem t = m.getTParentObject();
		Vector<MissionObject> vML = t.getVMissionList();
		int iMissionKey = vML.indexOf(m);
		if (iMissionKey < 0
				|| iMissionKey >= (Constants.MAX_MISSION_BAG_ITEMS - 1)) {
			dOut.writeLong(m.getLLastMissionID());
		} else {
			iMissionKey++;
			dOut.writeLong(vML.get(iMissionKey).getID()); // This throws
															// ArrayIndexOutOfBoundsException
		}

		dOut.writeShort(1);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildBaselineMISO6(MissionObject m)
			throws IOException {

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(m.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MISO]);
		dOut.writeByte(6);
		dOut.writeInt(14);
		dOut.writeShort(2);
		dOut.writeInt(129); // TODO -- Discover what this is / is supposed to
							// be. 122 when mission is taken????
		dOut.writeInt(0);
		dOut.writeInt(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildBaselineMISO8(MissionObject m)
			throws IOException {

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(m.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MISO]);
		dOut.writeByte(8);
		dOut.writeInt(2);
		dOut.writeShort(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildBaselineMISO9(MissionObject m)
			throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(m.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MISO]);
		dOut.writeByte(9);
		dOut.writeInt(2);
		dOut.writeShort(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	// --------------------MISSION BASELINES END------------------------
	/*
	 * RCNO8 and 9 are not needed -- also null. protected static byte[]
	 * buildBaselineRCNO8(ResourceContainer r) throws IOException {
	 * SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
	 * dOut.setOpcode(Constants.SOE_CHL_DATA_A); dOut.setSequence(0);
	 * dOut.setUpdateType(Constants.OBJECT_UPDATE);
	 * dOut.writeInt(Constants.BaselinesMessage); dOut.writeLong(r.getID());
	 * dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_RCNO]);
	 * dOut.writeByte(8); dOut.writeInt(2); dOut.writeShort(0); dOut.flush();
	 * return dOut.getBuffer(); }
	 * 
	 * protected static byte[] buildBaselineRCNO9(ResourceContainer r) throws
	 * IOException { SOEOutputStream dOut = new SOEOutputStream(new
	 * ByteArrayOutputStream()); dOut.setOpcode(Constants.SOE_CHL_DATA_A);
	 * dOut.setSequence(0); dOut.setUpdateType(Constants.OBJECT_UPDATE);
	 * dOut.writeInt(Constants.BaselinesMessage); dOut.writeLong(r.getID());
	 * dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_RCNO]);
	 * dOut.writeByte(9); dOut.writeInt(2); dOut.writeShort(0); dOut.flush();
	 * return dOut.getBuffer(); }
	 */

	// Cell permission message. Assuming we send a boolean of true if the user
	// is allowed to enter the building,
	// and false if they are not.
	protected static byte[] buildUpdateCellPermissionMessage(Cell c,
			Structure s, Player p) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.ACCOUNT_UPDATE);
		dOut.writeInt(Constants.UpdateCellPermissionMessage);

		if (c.getIsStaticObject()) {
			/**
			 * @todo this needs a list of checks to see what type of building it
			 *       is. if the building is like a startport or hospital we have
			 *       to allow access. if the building is a house like the ones
			 *       in cities we need to restrict permission to the cells
			 *       depending on the purpose of the house. If the house or
			 *       structure is for a quest, only people having the quest and
			 *       having completed the appropriate steps need to gain access.
			 */
			Structure building = c.getBuilding();
			long lBuildingID = building.getID();
			if (DatabaseInterface.isRestricted(lBuildingID) && !(p.isGM() || p.isDev())) {
				dOut.writeBoolean(false);
			} else if (p.isGM() || p.isDev()) {
				dOut.writeBoolean(true);
			} else {
				dOut.writeBoolean(c.getCanEnter());
			}

		} else {
			if (s.getStructureStatus() == Constants.STRUCTURE_PERMISSIONS_PUBLIC) {
				if (s.isBanned(p.getID())) {
					dOut.writeBoolean(false);
				} else {
					dOut.writeBoolean(true);
				}
			} else {
				if (s.hasEntry(p.getID())) {
					dOut.writeBoolean(true);
				} else {
					dOut.writeBoolean(false);
				}
			}
		}
		dOut.writeLong(c.getID());
		dOut.flush();
		return bOut.toByteArray();
	}

	protected static byte[] buildUpdateCellPermissionMessage(Cell c,
			TutorialObject s, Player p) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.ACCOUNT_UPDATE);
		dOut.writeInt(Constants.UpdateCellPermissionMessage);

		if (c.getIsStaticObject()) {
			/**
			 * @todo this needs a list of checks to see what type of building it
			 *       is. if the building is like a startport or hospital we have
			 *       to allow access. if the building is a house like the ones
			 *       in cities we need to restrict permission to the cells
			 *       depending on the purpose of the house. If the house or
			 *       structure is for a quest, only people having the quest and
			 *       having completed the appropriate steps need to gain access.
			 */
			Structure building = c.getBuilding();
			long lBuildingID = building.getID();
			if (DatabaseInterface.isRestricted(lBuildingID) && !(p.isGM() || p.isDev())) {
				dOut.writeBoolean(false);
			} else if (p.isGM() || p.isDev()) {
				dOut.writeBoolean(true);
			} else {
				dOut.writeBoolean(c.getCanEnter());
			}

		} else {
			if (s.getStructureStatus() == Constants.STRUCTURE_PERMISSIONS_PUBLIC) {
				if (s.isBanned(p.getID())) {
					dOut.writeBoolean(false);
				} else {
					dOut.writeBoolean(true);
				}
			} else {
				if (s.hasEntry(p.getID())) {
					dOut.writeBoolean(true);
				} else {
					dOut.writeBoolean(false);
				}
			}
		}
		dOut.writeLong(c.getID()); // TODO -- How is this determined?
		dOut.flush();
		return bOut.toByteArray();
	}

	// Play me some music!
	protected static byte[] buildPlayMusicMessage(String musicString)
			throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.PlayMusicMessage);
		dOut.writeUTF(musicString);
		dOut.writeLong(0); // Probably the person playing the music? Or a group
							// ID?
		dOut.writeInt(0); // Probably a spacer? Or a timer?
		dOut.writeByte(0); // writeBoolean(false)?? Original source not sure on
							// what these 3 commands were for.
		dOut.flush();
		return dOut.getBuffer();
	}

	// 
	protected static byte[] buildUpdatePvPStatusMessage(SOEObject p)
			throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.SERVER_UPDATE);
		dOut.writeInt(Constants.UpdatePvpStatusMessage);
		// int iPVPStatus = p.getPVPStatus();
		dOut.writeInt(p.getPVPStatus()); // TODO: Implement PVP status on
											// players. This is a bitmask, is it
											// not?
		// dOut.writeInt(Constants.PVP_STATUS_IS_PLAYER |
		// Constants.PVP_STATUS_TEF | Constants.PVP_STATUS_AGGRESSINVE |
		// Constants.PVP_STATUS_ATTACKABLE);
		dOut.writeInt(p.getFactionCRC()); // TODO: Implement faction status on
											// NPC's. Again, this is a bitmask,
											// is it not?
		dOut.writeLong(p.getID());
		dOut.flush();
		return dOut.getBuffer();
	}

	/**
	 * This version of hte buildUpdatePVPStatusMessage allows us to specify an "override" pvp status to be seen only by one client.
	 * For example, if Player A attacks a nuna, the nuna should turn red, but only to that Player.  Player B, who is watching the combat take place, will
	 * still see the Nuna as having a Yellow name.
	 * @param p The object to update.
	 * @param iTempBitmask The new bitmask to be seen
	 * @return The packet containing the update
	 * @throws IOException
	 */
	protected static byte[] buildUpdatePvPStatusMessage(SOEObject p, int iTempBitmask)	throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.SERVER_UPDATE);
		dOut.writeInt(Constants.UpdatePvpStatusMessage);
		// int iPVPStatus = p.getPVPStatus();
		dOut.writeInt(iTempBitmask); // TODO: Implement PVP status on
											// players. This is a bitmask, is it
											// not?
		// dOut.writeInt(Constants.PVP_STATUS_IS_PLAYER |
		// Constants.PVP_STATUS_TEF | Constants.PVP_STATUS_AGGRESSINVE |
		// Constants.PVP_STATUS_ATTACKABLE);
		dOut.writeInt(p.getFactionCRC()); // TODO: Implement faction status on
											// NPC's. Again, this is a bitmask,
											// is it not?
		dOut.writeLong(p.getID());
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildConnectPlayerResponseMessage()
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.WORLD_UPDATE);
		dOut.writeInt(Constants.ConnectPlayerResponseMessage);
		dOut.writeInt(5); // Unknown
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildAttributeListMessage(ZoneClient c, SOEObject o)
			throws IOException {

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.SERVER_UPDATE);
		dOut.writeInt(Constants.AttributesList);
		dOut.writeLong(o.getID());
		Hashtable<Integer, Attribute> vList = o.getAttributeList(c);
		// Vector<Attribute> vList = o.getAttributeList(c);
		dOut.writeInt(vList.size());
		Enumeration<Attribute> vItr = vList.elements();
		while (vItr.hasMoreElements()) {
			Attribute a = vItr.nextElement();
			int attributeIndex = a.getAttributeIndex();

			if (attributeIndex < Constants.NUM_OBJECT_ATTRIBUTES
					&& attributeIndex >= 0) {
				dOut.writeUTF(Constants.OBJECT_ATTRIBUTES[attributeIndex]);
			} else {
				dOut.writeUTF(a.getAttributeName());
			}
			dOut.writeUTF16(a.getAttributeValue());
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildAttributeListMessage(
			SpawnedResourceData theResource) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.SERVER_UPDATE);
		dOut.writeInt(Constants.AttributesList);
		dOut.writeLong(theResource.getID());
		Vector<Attribute> vList = theResource.getAttributes();
		dOut.writeInt(vList.size());
		for (int i = 0; i < vList.size(); i++) {
			Attribute a = vList.elementAt(i);
			dOut.writeUTF(a.getAttributeName());
			dOut.writeUTF16(a.getAttributeValue());
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildNPCUpdateTransformMessage(SOEObject n)
			throws IOException {
		float currentX = ((n.getX() * 4) + 0.5f);
		float currentZ = ((n.getZ() * 4) + 0.5f);
		float currentY = ((n.getY() * 4) + 0.5f);
		short shortX = (short) currentX;
		short shortZ = (short) currentZ;
		short shortY = (short) currentY;
		float movAngle = (n.getMovementAngle() * 16);
		byte moveAngleByte = (byte) movAngle;
		int movementCounter = n.getMoveUpdateCount();
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.UPDATE_TEN);
		dOut.writeInt(Constants.UpdateTransformMessage);
		dOut.writeLong(n.getID());
		dOut.writeShort(shortX);
		dOut.writeShort(shortZ);
		dOut.writeShort(shortY);
		dOut.writeInt(movementCounter);
		dOut.writeByte(0);
		dOut.writeByte(moveAngleByte);
		dOut.writeShort(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildUpdateTransformMessage(Player p)
			throws IOException {
		// if (true) return null;
		float currentX = ((p.getX() * 4) + 0.5f);
		float currentZ = ((p.getZ() * 4) + 0.5f);
		float currentY = ((p.getY() * 4) + 0.5f);
		short shortX = (short) currentX;
		short shortZ = (short) currentZ;
		short shortY = (short) currentY;
		float movAngle = (p.getMovementAngle() * 16);
		byte moveAngleByte = (byte) movAngle;
		int movementCounter = p.getMoveUpdateCount();

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.SCENE_UPDATE);
		dOut.writeInt(Constants.UpdateTransformMessage);
		dOut.writeLong(p.getID());
		// System.out.println("UpdateTransformMessage for Player " +
		// p.getFirstName() +
		// ":  x="+shortX+", y="+shortY+", z="+shortZ+", angle="+moveAngleByte+", counter="+movementCounter);
		dOut.writeShort(shortX);
		dOut.writeShort(shortZ);
		dOut.writeShort(shortY);
		dOut.writeInt(movementCounter);
		dOut.writeByte(0);
		dOut.writeByte(moveAngleByte);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildUpdateCellTransformMessage(Player p,
			SOEObject parent) throws IOException {
		if (parent == null) {
			return buildUpdateTransformMessage(p);
		} else {
			SOEOutputStream dOut = new SOEOutputStream(
					new ByteArrayOutputStream());
			dOut.setOpcode(Constants.SOE_CHL_DATA_A);
			dOut.setSequence(0);
			dOut.setUpdateType(Constants.SCENE_UPDATE);
			dOut.writeInt(Constants.UpdateTransformParentMessage);
			dOut.writeLong(parent.getID()); // This is the Cell Object we are
											// in.
			dOut.writeLong(p.getID());
			dOut.writeShort((short) p.getCellX() * 8);
			dOut.writeShort((short) p.getCellZ() * 8);
			dOut.writeShort((short) p.getCellY() * 8);
			dOut.writeInt(p.getMoveUpdateCount());
			dOut.writeByte(0);
			float movAngle = p.getMovementAngle() / 0.0625f;
			byte moveAngleByte = (byte) movAngle;
			dOut.writeByte(moveAngleByte);
			dOut.flush();
			return dOut.getBuffer();
		}
	}

	protected static byte[] buildNPCUpdateCellTransformMessage(SOEObject n,
			SOEObject parent) throws IOException {
		if (parent == null) {
			return buildNPCUpdateTransformMessage(n);
		} else {
			SOEOutputStream dOut = new SOEOutputStream(
					new ByteArrayOutputStream());
			dOut.setOpcode(Constants.SOE_CHL_DATA_A);
			dOut.setSequence(0);
			dOut.setUpdateType(Constants.UPDATE_ELEVEN);
			dOut.writeInt(Constants.UpdateTransformParentMessage);
			dOut.writeLong(parent.getID()); // This is the Cell Object we are
											// in.
			dOut.writeLong(n.getID());
			dOut.writeShort((short) n.getCellX() * 8);
			dOut.writeShort((short) n.getCellZ() * 8);
			dOut.writeShort((short) n.getCellY() * 8);
			dOut.writeInt(n.getMoveUpdateCount());
			dOut.writeByte(0);
			float movAngle = n.getMovementAngle() / 0.0625f;
			byte moveAngleByte = (byte) movAngle;
			dOut.writeByte(moveAngleByte);
			dOut.writeShort(0);
			dOut.flush();
			return dOut.getBuffer();
		}
	}

	protected static byte[] buildObjectControllerMessageSpatial(Player source,
			Player target, Player recipient, short languageID,
			String sSTFFileName, String sSTFFileIdentifier,

			long lTUObjectID, String sTUSTFFileName, // UTF
			String sTUSTFFileIdentifier, // UTF
			String sTUSTFOverride, // UTF16

			long lTTObjectID, String sTTSTFFileName, // UTF
			String sTTSTFFileIdentifier, // UTF
			String sTTSTFOverride, // UTF16

			long lTOObjectID, String sTOSTFFileName, // UTF
			String sTOSTFFileIdentifier, // UTF
			String sTOSTFOverride, // UTF16

			int iDIValue, float fDFValue, boolean bFlytext) throws IOException {

		// Temporarially disabled -- not quite working right.
		// if (true) return null;

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.SpatialChatMessage);
		dOut.writeLong(recipient.getID());
		dOut.writeInt(0);
		dOut.writeLong(source.getID());
		if (target != null) {
			dOut.writeLong(target.getID());
		} else {
			dOut.writeLong(0);
		}
		dOut.writeUTF16(null);
		dOut.writeShort(0x32); // Is this the howl modifier?
		if (target != null) {
			dOut.writeShort(target.getMoodID()); // / Guessing on these until I
													// get the C++ version of
													// eclipse set up properly.
		} else {
			dOut.writeShort(0);
		}
		dOut.writeShort(source.getMoodID());
		dOut.writeByte(0);
		dOut.writeByte(languageID);
		// Calculate the size of the rest of the packet.

		int size = 86;
		if (sSTFFileName != null) {
			size += sSTFFileName.length();
		}
		if (sSTFFileIdentifier != null) {
			size += sSTFFileIdentifier.length();
		}
		if (sTUSTFFileName != null) {
			size += sTUSTFFileName.length();
		}
		if (sTUSTFFileIdentifier != null) {
			size += sTUSTFFileIdentifier.length();
		}
		if (sTUSTFOverride != null) {
			size += (sTUSTFOverride.length() * 2);
		}

		if (sTOSTFFileName != null) {
			size += sTOSTFFileName.length();
		}
		if (sTOSTFFileIdentifier != null) {
			size += sTOSTFFileIdentifier.length();
		}
		if (sTOSTFOverride != null) {
			size += (sTOSTFOverride.length() * 2);
		}
		if (sTTSTFFileName != null) {
			size += sTTSTFFileName.length();
		}
		if (sTTSTFFileIdentifier != null) {
			size += sTTSTFFileIdentifier.length();
		}
		if (sTTSTFOverride != null) {
			size += (sTTSTFOverride.length() * 2);
		}

		// Odd number?
		boolean odd = ((size & 1) == 1);
		if (odd) {
			size += 1;
		}
		dOut.writeInt(size / 2);

		// 15
		dOut.writeBoolean(bFlytext); // This variable indicates if this message
										// is a flytext message or not.
		dOut.writeBoolean(false);
		dOut.writeBoolean(true);
		dOut.writeInt(-1);
		dOut.writeUTF(sSTFFileName);
		dOut.writeInt(0);
		dOut.writeUTF(sSTFFileIdentifier);

		// 20
		dOut.writeLong(lTUObjectID);
		dOut.writeUTF(sTUSTFFileName);
		dOut.writeInt(0);
		dOut.writeUTF(sTUSTFFileIdentifier);
		dOut.writeUTF16(sTUSTFOverride);
		// 20
		dOut.writeLong(lTTObjectID);
		dOut.writeUTF(sTTSTFFileName);
		dOut.writeInt(0);
		dOut.writeUTF(sTTSTFFileIdentifier);
		dOut.writeUTF16(sTTSTFOverride);
		// 20
		dOut.writeLong(lTOObjectID);
		dOut.writeUTF(sTOSTFFileName);
		dOut.writeInt(0);
		dOut.writeUTF(sTOSTFFileIdentifier);
		dOut.writeUTF16(sTOSTFOverride);
		// 8
		dOut.writeInt(iDIValue);
		dOut.writeFloat(fDFValue);

		// 3
		dOut.writeByte(0);
		dOut.writeShort(0);
		// 1, maybe
		if (odd) {
			dOut.writeByte(0);
		}

		dOut.flush();
		return dOut.getBuffer();
	}

	/**
	 * Note: This function handles spatial chat both between players, AND
	 * between NPC's and Players. Just typecast the NPC as a player when
	 * performing the function call.
	 * 
	 * @param source
	 * @param target
	 * @param recipient
	 * @param sMessage
	 * @param languageID
	 * @param howlModifier
	 * @return
	 * @throws java.io.IOException
	 */
	protected static byte[] buildObjectControllerMessageSpatial(Player source,
			Player target, Player recipient, String sMessage, short languageID,
			short howlModifier) throws IOException {
		if (recipient == null || source == null) {
			throw new NullPointerException(
					"Ack!  The player actually seeing this message, or the player who sent this message, doesn't exist???");
		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.SpatialChatMessage);
		dOut.writeLong(recipient.getID());
		dOut.writeInt(0);
		dOut.writeLong(source.getID());
		if (target != null) {
			dOut.writeLong(target.getID());
		} else {
			dOut.writeLong(0);
		}
		dOut.writeUTF16(sMessage);
		dOut.writeShort(0x32); // Is this the howl modifier?
		dOut.writeShort(howlModifier);
		dOut.writeShort(source.getMoodID());
		dOut.writeByte(0);
		dOut.writeByte(languageID);
		dOut.writeShort(0);
		dOut.writeLong(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	// Again -- If the emoting / emoted object is an NPC or Creature, typecast
	// it as a player when performing this call.
	protected static byte[] buildObjectControllerMessage_PlayerEmote(
			Player source, Player target, Player player) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11); // WTF shall we call this??
		dOut.writeInt(Constants.PlayerEmote);
		dOut.writeLong(player.getID());
		dOut.writeInt(0);
		dOut.writeLong(source.getID());
		if (target != null) {
			dOut.writeLong(target.getID());
		} else {
			dOut.writeLong(0);
		}
		dOut.writeShort(source.getPerformedEmoteID());
		dOut.writeByte(0);
		dOut.writeShort(0x0300);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildChatSystemMessage(String sMessage)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ChatSystemMessage);
		dOut.writeByte(0);
		dOut.writeUTF16(sMessage);
		dOut.writeInt(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildChatSystemMessage(String sSTFFileName,
			String sSTFFileIdentifier) throws IOException {
		return buildChatSystemMessage(sSTFFileName, sSTFFileIdentifier, 0, "",
				"", "", 0, "", "", "", 0, "", "", "", 0, 0, false);
	}

	/**
	 * Builds a chat system message
	 * 
	 * @param sSTFFileName
	 *            -- The STF File in which this message exists.
	 * @param sSTFFileIdentifier
	 *            -- The string name of the STF String in the file.
	 * @param lTUObjectID
	 *            -- The object ID of the person seeing this message, if this
	 *            message contains that person's name.
	 * @param sTUSTFFileName
	 *            -- The STF File name of the person's name who is seeing this
	 *            message, if any.
	 * @param sTUSTFFileIdentifier
	 *            -- The string name of the STF String of the person seeing this
	 *            message, if any.
	 * @param sTUSTFOverride
	 *            -- The override name of the person seeing this message, if
	 *            any.
	 * @param lTTObjectID
	 *            -- The object ID of the "target" of this message.
	 * @param sTTSTFFileName
	 *            -- The STF File in which the name of this target of this
	 *            message resides.
	 * @param sTTSTFFileIdentifier
	 *            -- The STF string name of the target of this message.
	 * @param sTTSTFOverride
	 *            -- The override name of the target of this message.
	 * @param lTOObjectID
	 *            -- The object ID of the observer of this message.
	 * @param sTOSTFFileName
	 *            -- The STF FIle in which the name of the observer of this
	 *            message resides.
	 * @param sTOSTFFileIdentifier
	 *            -- The string name of the STF String of the observer of this
	 *            message.
	 * @param sTOSTFOverride
	 *            -- The override name of the observer of this message.
	 * @param iDIValue
	 *            -- The integer number appended to this message.
	 * @param fDFValue
	 *            -- The floating point number appended to this message.
	 * @param bFlytext
	 *            -- Indicates if this is a flytext message or a regular system
	 *            message.
	 * @return The packet.
	 * @throws IOException
	 *             if an error occured during the writing of data to the output
	 *             stream.
	 */
	protected static byte[] buildChatSystemMessage(String sSTFFileName, // UTF
			String sSTFFileIdentifier, // UTF

			long lTUObjectID, String sTUSTFFileName, // UTF
			String sTUSTFFileIdentifier, // UTF
			String sTUSTFOverride, // UTF16

			long lTTObjectID, String sTTSTFFileName, // UTF
			String sTTSTFFileIdentifier, // UTF
			String sTTSTFOverride, // UTF16

			long lTOObjectID, String sTOSTFFileName, // UTF
			String sTOSTFFileIdentifier, // UTF
			String sTOSTFOverride, // UTF16

			int iDIValue, float fDFValue, boolean bFlytext) throws IOException {

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ChatSystemMessage);
		dOut.writeByte(0);
		dOut.writeUTF16("");

		// Null checks.

		int size = 0x56;
		if (sSTFFileName != null) {
			size += sSTFFileName.length();
		}
		if (sSTFFileIdentifier != null) {
			size += sSTFFileIdentifier.length();
		}
		if (sTUSTFFileName != null) {
			size += sTUSTFFileName.length();
		}
		if (sTUSTFFileIdentifier != null) {
			size += sTUSTFFileIdentifier.length();
		}
		if (sTUSTFOverride != null) {
			size += (sTUSTFOverride.length() * 2);
		}

		if (sTOSTFFileName != null) {
			size += sTOSTFFileName.length();
		}
		if (sTOSTFFileIdentifier != null) {
			size += sTOSTFFileIdentifier.length();
		}
		if (sTOSTFOverride != null) {
			size += (sTOSTFOverride.length() * 2);
		}
		if (sTTSTFFileName != null) {
			size += sTTSTFFileName.length();
		}
		if (sTTSTFFileIdentifier != null) {
			size += sTTSTFFileIdentifier.length();
		}
		if (sTTSTFOverride != null) {
			size += (sTTSTFOverride.length() * 2);
		}

		// Odd number?
		boolean odd = ((size & 1) == 1);
		if (odd) {
			size += 1;
		}
		dOut.writeInt(size / 2);

		dOut.writeBoolean(bFlytext); // This variable indicates if this message
										// is a flytext message or not.
		dOut.writeBoolean(false);
		dOut.writeBoolean(true);
		dOut.writeInt(-1);
		dOut.writeUTF(sSTFFileName);
		dOut.writeInt(0);
		dOut.writeUTF(sSTFFileIdentifier);

		// 20
		dOut.writeLong(lTUObjectID);
		dOut.writeUTF(sTUSTFFileName);
		dOut.writeInt(0);
		dOut.writeUTF(sTUSTFFileIdentifier);
		dOut.writeUTF16(sTUSTFOverride);

		dOut.writeLong(lTTObjectID);
		dOut.writeUTF(sTTSTFFileName);
		dOut.writeInt(0);
		dOut.writeUTF(sTTSTFFileIdentifier);
		dOut.writeUTF16(sTTSTFOverride);

		dOut.writeLong(lTOObjectID);
		dOut.writeUTF(sTOSTFFileName);
		dOut.writeInt(0);
		dOut.writeUTF(sTOSTFFileIdentifier);
		dOut.writeUTF16(sTOSTFOverride);

		dOut.writeInt(iDIValue);
		dOut.writeFloat(fDFValue);

		dOut.writeByte(0);
		dOut.writeShort(0);
		dOut.writeInt(0);
		if (odd) {
			dOut.writeByte(0);
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	/*
	 * protected static byte[] buildChatSystemMessage(String sSTFFile, String
	 * sSTFString, String sValueSTFFile, String sValueSTFString, int iValue)
	 * throws IOException { SOEOutputStream dOut = new SOEOutputStream(new
	 * ByteArrayOutputStream()); dOut.setOpcode(Constants.SOE_CHL_DATA_A);
	 * dOut.setSequence(0); dOut.setUpdateType(Constants.OBJECT_UPDATE);
	 * dOut.writeInt(Constants.ChatSystemMessage); dOut.writeByte(0);
	 * dOut.writeUTF16(""); dOut.writeInt(0x47); dOut.writeShort(1);
	 * dOut.writeByte(1); dOut.writeInt(-1); dOut.writeUTF(sSTFFile);
	 * dOut.writeInt(0); dOut.writeUTF(sSTFString); dOut.write(new byte[48]); //
	 * Fugly, I know, but it'll do what's needed. dOut.writeUTF(sValueSTFFile);
	 * dOut.writeInt(0); dOut.writeUTF(sValueSTFString); dOut.writeInt(0);
	 * dOut.writeInt(iValue); dOut.writeInt(0); dOut.writeShort(0);
	 * dOut.flush(); return dOut.getBuffer(); }
	 * 
	 * protected static byte[] buildChatSystemMessage(String sSTFFile, String
	 * sSTFString) throws IOException { SOEOutputStream dOut = new
	 * SOEOutputStream(new ByteArrayOutputStream());
	 * dOut.setOpcode(Constants.SOE_CHL_DATA_A); dOut.setSequence(0);
	 * dOut.setUpdateType(Constants.OBJECT_UPDATE);
	 * dOut.writeInt(Constants.ChatSystemMessage); dOut.writeByte(0);
	 * dOut.writeUTF16(""); dOut.writeInt(0x32); dOut.writeShort(1);
	 * dOut.writeByte(1); dOut.writeInt(-1); dOut.writeUTF(sSTFFile);
	 * dOut.writeInt(0); dOut.writeUTF(sSTFString); dOut.writeInt(0x74);
	 * dOut.write(new byte[67]); dOut.flush(); return dOut.getBuffer(); }
	 */

	// / This packet was formerly known as the ObjectMenuResponse --
	// RadialsResponse seems more accurate, as
	// this packet attaches the Radial menu items to an object.
	protected static byte[] buildObjectControllerMessage_RadialsResponse(
			Player player, SOEObject target, byte rCounter) throws IOException {

		Collection<RadialMenuItem> vRadials = target.getRadialMenus(
				player.getClient()).values();
		Iterator<RadialMenuItem> vRadialItr = vRadials.iterator();
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.ObjectMenuResponse);
		dOut.writeLong(player.getID()); // Me.
		dOut.writeInt(0);
		dOut.writeLong(target.getID()); // The originator.
		dOut.writeLong(player.getID()); // The target of the message. In this
										// case, me. The client would probably
										// either ignore,
		// or have a fit about, a packet it received of this type which isn't
		// actually telling "me" about the Radials of an object.
		dOut.writeInt(vRadials.size());
		while (vRadialItr.hasNext()) {
			RadialMenuItem r = vRadialItr.next();
			dOut.writeByte(r.getButtonNumber());
			dOut.writeByte(r.getParentButton());
			dOut.writeByte(r.getCommandID());
			dOut.writeByte(r.getActionLocation());
			dOut.writeUTF16(r.getButtonText()); // If getCustomLabel returns "",
												// this writes a 0-value int.
												// Otherwise, it writes the
												// needed label length + label.
		}
		dOut.writeByte(rCounter); // This is the menu Request id sent by the
									// client with the original radial request.
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildObjectControllerMessage_RadialsResponse(
			Player player, SOEObject target, Vector<RadialMenuItem> vRadials,
			byte rCounter) throws IOException {

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.ObjectMenuResponse);
		dOut.writeLong(player.getID()); // Me.
		dOut.writeInt(0);
		dOut.writeLong(target.getID()); // The originator.
		dOut.writeLong(player.getID()); // The target of the message. In this
										// case, me. The client would probably
										// either ignore,
		// or have a fit about, a packet it received of this type which isn't
		// actually telling "me" about the Radials of an object.
		dOut.writeInt(vRadials.size());
		for (byte i = 0; i < vRadials.size(); i++) {
			RadialMenuItem r = vRadials.elementAt(i);
			dOut.writeByte(r.getButtonNumber());
			dOut.writeByte(r.getParentButton());
			dOut.writeByte(r.getCommandID());
			dOut.writeByte(r.getActionLocation());
			dOut.writeUTF16(r.getButtonText()); // If getCustomLabel returns "",
												// this writes a 0-value int.
												// Otherwise, it writes the
												// needed label length + label.
		}
		dOut.writeByte(rCounter); // WTF is this?
		dOut.flush();

		return dOut.getBuffer();
	}

	protected static byte[] buildObjectControllerMessage_RadialsResponse(
			Player player, long targetID, Vector<RadialMenuItem> vRadials,
			byte rCounter) throws IOException {

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.ObjectMenuResponse);
		dOut.writeLong(player.getID()); // Me.
		dOut.writeInt(0);
		dOut.writeLong(targetID); // The originator.
		dOut.writeLong(player.getID()); // The target of the message. In this
										// case, me. The client would probably
										// either ignore,
		// or have a fit about, a packet it received of this type which isn't
		// actually telling "me" about the Radials of an object.
		dOut.writeInt(vRadials.size());
		for (int i = 0; i < vRadials.size(); i++) {
			RadialMenuItem r = vRadials.elementAt(i);
			dOut.writeByte(r.getButtonNumber());
			dOut.writeByte(r.getParentButton());
			dOut.writeByte(r.getCommandID());
			dOut.writeByte(r.getActionLocation());
			dOut.writeUTF16(r.getButtonText()); // If getCustomLabel returns "",
												// this writes a 0-value int.
												// Otherwise, it writes the
												// needed label length + label.
		}
		dOut.writeByte(rCounter);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildObjectControllerMessage_RadialsResponse(
			Player player, long targetID, Collection<RadialMenuItem> vRadials,
			byte rCounter) throws IOException {

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.ObjectMenuResponse);
		dOut.writeLong(player.getID()); // Me.
		dOut.writeInt(0);
		dOut.writeLong(targetID); // The originator.
		dOut.writeLong(player.getID()); // The target of the message. In this
										// case, me. The client would probably
										// either ignore,
		// or have a fit about, a packet it received of this type which isn't
		// actually telling "me" about the Radials of an object.
		dOut.writeInt(vRadials.size());
		Iterator<RadialMenuItem> radialsIterator = vRadials.iterator();
		while (radialsIterator.hasNext()) {
			RadialMenuItem r = radialsIterator.next();
			dOut.writeByte(r.getButtonNumber());
			dOut.writeByte(r.getParentButton());
			dOut.writeByte(r.getCommandID());
			dOut.writeByte(r.getActionLocation());
			dOut.writeUTF16(r.getButtonText()); // If getCustomLabel returns "",
												// this writes a 0-value int.
												// Otherwise, it writes the
												// needed label length + label.
		}
		dOut.writeByte(rCounter);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] stopClientEffectObjectByLabelMessage(NPC mountObject)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.SERVER_UPDATE);
		dOut.writeInt(Constants.StopClientEffectObjectByLabelMessage);
		dOut.writeLong(mountObject.getID());
		dOut.writeUTF(mountObject.getVehicleEffectString());
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasMessage(int iBaselineIndex,
			byte iBaselineType, short updateCount, short updateOperand,
			SOEObject o, boolean newValue) throws IOException {
		return buildDeltasMessage(iBaselineIndex, iBaselineType, updateCount,
				updateOperand, o, (byte) (newValue ? 0 : 1));
	}

	protected static byte[] buildDeltasMessage(int iBaselineIndex,
			byte iBaselineType, short updateCount, short updateOperand,
			SOEObject o, byte newValue) throws IOException {
		// System.out.println("Building BYTE DeltasMessage.  Values -- Baseline "
		// + Integer.toHexString(Constants.BaselinesTypes[iBaselineIndex]) +
		// ", subBaseline: " + iBaselineType + ", Updates: " + updateCount +
		// ", vID: " + updateOperand + ", new Value: " + newValue +
		// " , for object " + Long.toHexString(p.getID()));
		int packetSize = 5;
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(o.getID());
		dOut.writeInt(Constants.BaselinesTypes[iBaselineIndex]);
		dOut.writeByte(iBaselineType);
		dOut.writeInt(packetSize);
		dOut.writeShort(updateCount); // / This should always be 1 here. This is
										// the number of overall updates we are
										// doing.
		dOut.writeShort(updateOperand);
		// If this is a list: write out the update count for this list, then
		// write out the number of sub-updates being performed.
		dOut.writeByte(newValue);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasMessage(int iBaselineIndex,
			byte iBaselineType, short updateCount, short updateOperand,
			SOEObject o, short newValue) throws IOException {
		// System.out.println("Building SHORT DeltasMessage.  Values -- Baseline "
		// + Integer.toHexString(Constants.BaselinesTypes[iBaselineIndex]) +
		// ", subBaseline: " + iBaselineType + ", Updates: " + updateCount +
		// ", vID: " + updateOperand + ", new Value: " + newValue +
		// " , for object " + Long.toHexString(p.getID()));
		int packetSize = 6;
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(o.getID());
		dOut.writeInt(Constants.BaselinesTypes[iBaselineIndex]);
		dOut.writeByte(iBaselineType);
		dOut.writeInt(packetSize);
		dOut.writeShort(updateCount); // / This should always be 1 here. This is
										// the number of overall updates we are
										// doing.
		dOut.writeShort(updateOperand);
		// If this is a list: write out the update count for this list, then
		// write out the number of sub-updates being performed.
		dOut.writeShort(newValue);
		dOut.flush();
		return dOut.getBuffer();

	}

	/**
	 * Used to send Customization Data Updates to objects
	 * 
	 * @param iBaselineIndex
	 * @param iBaselineType
	 * @param updateCount
	 * @param updateOperand
	 * @param o
	 * @param customizationData
	 * @return
	 * @throws java.io.IOException
	 */
	protected static byte[] buildDeltasMessage(int iBaselineIndex,
			byte iBaselineType, short updateCount, short updateOperand,
			SOEObject o, byte[] customizationData) throws IOException {
		// System.out.println("Building SHORT DeltasMessage.  Values -- Baseline "
		// + Integer.toHexString(Constants.BaselinesTypes[iBaselineIndex]) +
		// ", subBaseline: " + iBaselineType + ", Updates: " + updateCount +
		// ", vID: " + updateOperand + ", new Value: " + newValue +
		// " , for object " + Long.toHexString(p.getID()));
		int packetSize = 6;
		int iDataLength = 0;
		if (customizationData != null) {
			iDataLength = customizationData.length;
		}
		packetSize += iDataLength;
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(o.getID());
		dOut.writeInt(Constants.BaselinesTypes[iBaselineIndex]);
		dOut.writeByte(iBaselineType);
		dOut.writeInt(packetSize);
		dOut.writeShort(updateCount); // / This should always be 1 here. This is
										// the number of overall updates we are
										// doing.
		dOut.writeShort(updateOperand);
		// If this is a list: write out the update count for this list, then
		// write out the number of sub-updates being performed.
		if (iDataLength == 0) {
			dOut.writeShort(iDataLength);
		} else {
			dOut.writeShort(iDataLength);
			dOut.write(customizationData);
		}

		dOut.flush();
		return dOut.getBuffer();

	}

	protected static byte[] buildDeltasMessage(int iBaselineIndex,
			byte iBaselineType, short updateCount, short updateOperand,
			SOEObject o, int newValue) throws IOException {
		// System.out.println("Building INT DeltasMessage.  Values -- Baseline "
		// + Integer.toHexString(Constants.BaselinesTypes[iBaselineIndex]) +
		// ", subBaseline: " + iBaselineType + ", Updates: " + updateCount +
		// ", vID: " + updateOperand + ", new Value: " + newValue +
		// " , for object " + Long.toHexString(p.getID()));

		int packetSize = 8; // + the size of the object;
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(o.getID());
		dOut.writeInt(Constants.BaselinesTypes[iBaselineIndex]);
		dOut.writeByte(iBaselineType);
		dOut.writeInt(packetSize);
		dOut.writeShort(updateCount); // / This should always be 1 here. This is
										// the number of overall updates we are
										// doing.
		dOut.writeShort(updateOperand);
		// If this is a list: write out the update count for this list, then
		// write out the number of sub-updates being performed.
		dOut.writeInt(newValue);
		dOut.flush();
		return dOut.getBuffer();

	}

	protected static byte[] buildDeltasMessage(int iBaselineIndex,
			byte iBaselineType, short updateCount, short updateOperand,
			SOEObject o, long newValue) throws IOException {
		// System.out.println("Building LONG DeltasMessage.  Values -- Baseline "
		// + Integer.toHexString(Constants.BaselinesTypes[iBaselineIndex]) +
		// ", subBaseline: " + iBaselineType + ", Updates: " + updateCount +
		// ", vID: " + updateOperand + ", new Value: " + newValue +
		// " , for object " + Long.toHexString(p.getID()));

		int packetSize = 12; // + the size of the object;
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(o.getID());
		dOut.writeInt(Constants.BaselinesTypes[iBaselineIndex]);
		dOut.writeByte(iBaselineType);
		dOut.writeInt(packetSize);
		dOut.writeShort(updateCount); // / This should always be 1 here. This is
										// the number of overall updates we are
										// doing.
		dOut.writeShort(updateOperand);
		// If this is a list: write out the update count for this list, then
		// write out the number of sub-updates being performed.
		dOut.writeLong(newValue);
		dOut.flush();
		return dOut.getBuffer();
	}

	/**
	 * Group Baselines
	 */
	protected static byte[] buildBaselineGRUP3(Group g) throws IOException {

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(g.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_GRUP]);
		dOut.writeByte(3);
		dOut.writeInt(37);
		dOut.writeShort(4);
		dOut.writeFloat(1);
		dOut.writeUTF("string_id_table");
		dOut.writeInt(0);
		dOut.writeShort(0);
		dOut.writeInt(0);
		dOut.writeInt(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildBaselineGRUP6(Group g, Player player)
			throws IOException {

		int iPacketSize = 42;
		for (int i = 0; i < g.getGroupMembers().size(); i++) {
			iPacketSize += 10;
			if (g.getGroupMembers().get(i) instanceof Player) {
				Player p = (Player) g.getGroupMembers().get(i);
				iPacketSize += p.getFullName().length();
			}
		}

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(g.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_GRUP]);
		dOut.writeByte(6);
		dOut.writeInt(iPacketSize);
		dOut.writeShort(6);
		dOut.writeFloat(128);
		dOut.writeInt(g.getGroupMembers().size());
		dOut.writeInt(player.getGroupUpdateCounter(false)); // (g.getGroupUpdateCounter());//<--GroupUpd
															// Counter???
		for (int i = 0; i < g.getGroupMembers().size(); i++) {

			if (g.getGroupMembers().get(i) instanceof Player) {
				Player p = (Player) g.getGroupMembers().get(i);
				dOut.writeLong(p.getID());
				dOut.writeUTF(p.getFullName());
			}
		}
		dOut.writeInt(0);
		dOut.writeInt(0);
		dOut.writeShort(0);
		dOut.writeShort(0);
		dOut.writeInt(0);
		dOut.writeLong(0);
		dOut.writeInt(0);

		dOut.flush();
		return dOut.getBuffer();
	}

	/**
	 * Group Deltas
	 */

	protected static byte[] buildDeltasMessageGRUP(int iBaselineIndex,
			byte iBaselineType, short updateCount, short updateOperand,
			byte subTypeOperand, Group g, SOEObject updatedMember,
			int iGroupUpdateCounter, Player player, short deleteIndex)
			throws IOException {

		int packetSize = 13; // + the size of the object;

		switch (subTypeOperand) {
		case Constants.Group_DeleteMember:// 0
		{
			packetSize += 2;
			break;
		}
		case Constants.Group_AddMember:// 1
		{
			packetSize += 12;
			if (updatedMember instanceof Player) {
				Player p = (Player) updatedMember;
				packetSize += p.getFullName().length();
			}
			break;
		}
		case Constants.Group_GroupLeader:// 2
		{
			packetSize += 12;
			if (updatedMember instanceof Player) {
				Player p = (Player) updatedMember;
				packetSize += p.getFullName().length();
			}
			break;
		}
		case Constants.Group_ResetGroup:// 3
		{
			packetSize += 2;
			Vector<SOEObject> vML = g.getGroupMembers();
			for (int i = 0; i < vML.size(); i++) {
				SOEObject o = vML.get(i);
				if (o instanceof Player) {
					Player p = (Player) o;
					packetSize += 10;
					packetSize += p.getFullName().length();
				}
			}

			break;
		}
		case Constants.Group_ClearList: {
			break;
		}
		}

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(g.getID());
		dOut.writeInt(Constants.BaselinesTypes[iBaselineIndex]);
		dOut.writeByte(iBaselineType);
		dOut.writeInt(packetSize);
		dOut.writeShort(updateCount);
		dOut.writeShort(updateOperand);
		dOut.writeInt(1);
		dOut.writeInt(player.getGroupUpdateCounter(true));
		dOut.writeByte(subTypeOperand);
		switch (subTypeOperand) {
		case Constants.Group_DeleteMember:// 0
		{

			dOut.writeShort(deleteIndex);
			break;
		}
		case Constants.Group_AddMember:// 1
		{

			dOut.writeShort(g.getNextMemberIndex());
			dOut.writeLong(updatedMember.getID());
			if (updatedMember instanceof Player) {
				Player p = (Player) updatedMember;
				dOut.writeUTF(p.getFullName());
			}
			break;
		}
		case Constants.Group_GroupLeader:// 2
		{
			dOut.writeShort(g.getMemberIndex(updatedMember));
			dOut.writeLong(updatedMember.getID());
			if (updatedMember instanceof Player) {
				Player p = (Player) updatedMember;
				dOut.writeUTF(p.getFullName());
			}
			break;
		}
		case Constants.Group_ResetGroup:// 3
		{
			dOut.writeShort(g.getGroupMembers().size());

			for (int i = 0; i < g.getGroupMembers().size(); i++) {
				dOut.writeLong(g.getGroupMembers().get(i).getID());
				if (g.getGroupMembers().get(i) instanceof Player) {
					Player p = (Player) g.getGroupMembers().get(i);
					dOut.writeUTF(p.getFullName());
				}
			}
			break;
		}
		case Constants.Group_ClearList: {
			break;
		}
		}

		dOut.flush();
		return dOut.getBuffer();
	}

	/**
	 * Invitation to group baseline
	 * 
	 * @param iBaselineIndex
	 * @param iBaselineType
	 * @param updateCount
	 * @param updateOperand
	 * @param o
	 * @param inviter
	 * @return
	 * @throws java.io.IOException
	 */
	protected static byte[] buildDeltasMessageGroupInvite(int iBaselineIndex,
			byte iBaselineType, short updateCount, short updateOperand,
			Player p, Player host) throws IOException {
		// System.out.println("Building LONG DeltasMessage.  Values -- Baseline "
		// + Integer.toHexString(Constants.BaselinesTypes[iBaselineIndex]) +
		// ", subBaseline: " + iBaselineType + ", Updates: " + updateCount +
		// ", vID: " + updateOperand + ", new Value: " + newValue +
		// " , for object " + Long.toHexString(p.getID()));

		int packetSize = 20; // + the size of the object;
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(p.getID());
		dOut.writeInt(Constants.BaselinesTypes[iBaselineIndex]);
		dOut.writeByte(iBaselineType);
		dOut.writeInt(packetSize);
		dOut.writeShort(updateCount);
		dOut.writeShort(updateOperand);

		dOut.writeLong(p.getGroupHost());
		dOut.writeLong(p.getGroupInviteCounter(true));
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasMessage(int iBaselineIndex,
			byte iBaselineType, short updateCount, short[] updateOperand,
			SOEObject o, float[] newValue) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(o.getID());
		dOut.writeInt(Constants.BaselinesTypes[iBaselineIndex]);
		dOut.writeByte(iBaselineType);
		int PacketSize = (updateOperand.length * 2);
		PacketSize += (newValue.length * 4);
		PacketSize += 2;
		dOut.writeInt(PacketSize);
		dOut.writeShort(updateCount); // update count
		for (int i = 0; i < updateCount; i++) {
			dOut.writeShort(updateOperand[i]);
			dOut.writeFloat(newValue[i]);
		}
		dOut.flush();
		return dOut.getBuffer();

		//
	}

	protected static byte[] buildDeltasMessage(int iBaselineIndex,
			byte iBaselineType, short updateCount, short updateOperand,
			SOEObject o, float newValue) throws IOException {
		// System.out.println("Building FLOAT DeltasMessage.  Values -- Baseline "
		// + Integer.toHexString(Constants.BaselinesTypes[iBaselineIndex]) +
		// ", subBaseline: " + iBaselineType + ", Updates: " + updateCount +
		// ", vID: " + updateOperand + ", new Value: " + newValue +
		// " , for object " + Long.toHexString(p.getID()));

		int packetSize = 8; // + the size of the object;
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(o.getID());
		dOut.writeInt(Constants.BaselinesTypes[iBaselineIndex]);
		dOut.writeByte(iBaselineType);
		dOut.writeInt(packetSize);
		dOut.writeShort(updateCount); // / This should always be 1 here. This is
										// the number of overall updates we are
										// doing.
		dOut.writeShort(updateOperand);
		// If this is a list: write out the update count for this list, then
		// write out the number of sub-updates being performed.
		dOut.writeFloat(newValue);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasMessage(int iBaselineIndex,
			byte iBaselineType, short updateCount, short updateOperand,
			SOEObject o, double newValue) throws IOException {
		// System.out.println("Building DOUBLE DeltasMessage.  Values -- Baseline "
		// + Integer.toHexString(Constants.BaselinesTypes[iBaselineIndex]) +
		// ", subBaseline: " + iBaselineType + ", Updates: " + updateCount +
		// ", vID: " + updateOperand + ", new Value: " + newValue +
		// " , for object " + Long.toHexString(p.getID()));

		int packetSize = 12; // + the size of the object;
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(o.getID());
		dOut.writeInt(Constants.BaselinesTypes[iBaselineIndex]);
		dOut.writeByte(iBaselineType);
		dOut.writeInt(packetSize);
		dOut.writeShort(updateCount); // / This should always be 1 here. This is
										// the number of overall updates we are
										// doing.
		dOut.writeShort(updateOperand);
		// If this is a list: write out the update count for this list, then
		// write out the number of sub-updates being performed.
		dOut.writeDouble(newValue);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasMessage(int iBaselineIndex,
			byte iBaselineType, short updateCount, short updateOperand,
			SOEObject o, String newValue, boolean bUTF16) throws IOException {
		// System.out.println("Building DOUBLE DeltasMessage.  Values -- Baseline "
		// + Integer.toHexString(Constants.BaselinesTypes[iBaselineIndex]) +
		// ", subBaseline: " + iBaselineType + ", Updates: " + updateCount +
		// ", vID: " + updateOperand + ", new Value: " + newValue +
		// " , for object " + Long.toHexString(p.getID()));
		int packetSize = 0;
		if (bUTF16) {
			packetSize = 8 + (newValue.length() * 2);
		} else {
			packetSize = 6 + newValue.length(); // + the size of the object;
		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(o.getID());
		dOut.writeInt(Constants.BaselinesTypes[iBaselineIndex]);
		dOut.writeByte(iBaselineType);
		dOut.writeInt(packetSize);
		dOut.writeShort(updateCount); // / This should always be 1 here. This is
										// the number of overall updates we are
										// doing.
		dOut.writeShort(updateOperand);
		// If this is a list: write out the update count for this list, then
		// write out the number of sub-updates being performed.
		if (bUTF16) {
			dOut.writeUTF16(newValue);
		} else {
			dOut.writeUTF(newValue);
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	/**
	 * This deltas message is to be used ONLY to update the STF File data for an
	 * object.
	 * 
	 * @param The
	 *            baseline "name" of this object. Immutable.
	 * @param iBaselineType
	 *            -- The baseline ID of the object -- is always 3 here.
	 * @param updateCount
	 *            -- The number of updates to the baseline -- is always 1 here.
	 * @param updateOperand
	 *            -- The update operand -- Is always 1 here.
	 * @param o
	 *            -- The object being updated.
	 * @param newSTFFileName
	 *            -- The new STF Filename.
	 * @param newSTFFileIdentifier
	 *            -- The new STF File Identifier.
	 * @return -- The deltas message generated.
	 * @throws IOException
	 *             -- If an error occured writing to the output stream.
	 */
	protected static byte[] buildDeltasMessage(int iBaselineIndex,
			byte iBaselineType, short updateCount, short updateOperand,
			SOEObject o, String newSTFFileName, String newSTFFileIdentifier)
			throws IOException {
		// System.out.println("Building DOUBLE DeltasMessage.  Values -- Baseline "
		// + Integer.toHexString(Constants.BaselinesTypes[iBaselineIndex]) +
		// ", subBaseline: " + iBaselineType + ", Updates: " + updateCount +
		// ", vID: " + updateOperand + ", new Value: " + newValue +
		// " , for object " + Long.toHexString(p.getID()));
		int packetSize = 12;
		if (newSTFFileName != null) {
			packetSize += newSTFFileName.length();
		}
		if (newSTFFileIdentifier != null) {
			packetSize += newSTFFileIdentifier.length();
		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(o.getID());
		dOut.writeInt(Constants.BaselinesTypes[iBaselineIndex]);
		dOut.writeByte(iBaselineType);
		dOut.writeInt(packetSize);
		dOut.writeShort(updateCount); // / This should always be 1 here. This is
										// the number of overall updates we are
										// doing.
		dOut.writeShort(updateOperand);
		// If this is a list: write out the update count for this list, then
		// write out the number of sub-updates being performed.
		dOut.writeUTF(newSTFFileName);
		dOut.writeInt(0);
		dOut.writeUTF(newSTFFileIdentifier);
		dOut.flush();
		return dOut.getBuffer();
	}

	/*
	 * protected static byte[] buildDeltasMessageList(int iBaselineIndex, byte
	 * iBaselineType, short updateCount, short updateOperand, Vector updateData,
	 * SOEObject p) throws IOException { int packetSize = 4; for (int i = 0; i <
	 * updateData.size(); i++) { Object object = updateData.elementAt(i); if
	 * (object instanceof Experience) { Experience e = (Experience)object;
	 * packetSize += e.sExperienceName.length() + 2 + 5; } }
	 * 
	 * SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
	 * dOut.setOpcode(Constants.SOE_CHL_DATA_A); dOut.setSequence(0);
	 * dOut.setUpdateType(Constants.OBJECT_UPDATE);
	 * dOut.writeInt(Constants.DeltasMessage); dOut.writeLong(p.getID());
	 * dOut.writeInt(Constants.BaselinesTypes[iBaselineIndex]);
	 * dOut.writeByte(iBaselineType); dOut.writeInt(packetSize); for (int i = 0;
	 * i < updateData.size(); i++) { Object object = updateData.elementAt(i); if
	 * (object instanceof PlayerExperience) { if (i == 0) { if (!(p instanceof
	 * PlayerItem)) { throw new
	 * ClassCastException("Cannot cast the passed SOEObject to a PlayerItem"); }
	 * PlayerItem p = (PlayerItem)p; dOut.writeInt(updateData.size());
	 * dOut.writeInt(p.getExperienceListUpdateCount()); }
	 * insertExperienceData((PlayerExperience)object, dOut); } } dOut.flush();
	 * return dOut.getBuffer(); }
	 */

	// private static void insertExperienceData(PlayerExperience e,
	// SOEOutputStream dOut) throws IOException {
	// dOut.writeBoolean(e.iCurrentExperienceValue == 0);
	// dOut.writeUTF(e.sExperienceName);
	// dOut.writeInt(e.iCurrentExperienceValue);
	// }

	protected static byte[] buildObjectController_FriendListUpdate(
			Player originator, Player friend) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.FriendListRequestResponse);
		dOut.writeLong(originator.getID());
		dOut.writeInt(0);
		dOut.writeLong(friend.getID());
		Vector<PlayerFriends> v = originator.getFriendsList();
		String s = friend.getFirstName();
		boolean bFound = false;
		PlayerFriends f;
		for (int i = 0; i < v.size() && !bFound; i++) {
			f = v.elementAt(i);
			bFound = f.getName().equalsIgnoreCase(s);
		}
		dOut.writeBoolean(bFound);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildEnterStructurePlacementMode(Player p,
			String sStructureIFFFileName) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.ACCOUNT_UPDATE);
		dOut.writeInt(Constants.EnterStructurePlacementMode);
		dOut.writeLong(p.getID());
		dOut.writeUTF(sStructureIFFFileName);
		dOut.writeShort(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildEnterTicketPurchaseMode(Player p,
			Terminal ourTerminal) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.SERVER_UPDATE);
		dOut.writeInt(Constants.EnterTicketPurchaseModeMessage);
		dOut.writeUTF(Constants.PlanetNames[p.getPlanetID()].toLowerCase());

		// this is the name of the place this terminal is at. I/E Naboo, So
		// on....
		dOut.writeUTF(p.getServer().getTravelTerminalLocationName(
				ourTerminal.getTicketID()));

		dOut.writeShort(0); // always seems to be 0
		dOut.flush();
		return dOut.getBuffer();
	}

	/**
	 * 
	 * This function sends the list of available travel destinations
	 * 
	 **/
	protected static byte[] buildTravelPointListResponse(Player player,
			int _PlanetID) throws IOException {

		Vector<TravelDestination> vTd = player.getServer()
				.getTravelDestinationsForPlanet(player, _PlanetID);
		// this allows us to send 0 size lists for any planet
		int ListSize = 0;
		if (vTd != null) {
			ListSize = vTd.size();
		}

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.UPDATE_SIX);
		dOut.writeInt(Constants.PlanetTravelPointListResponse);
		dOut.writeUTF(Constants.TravelPlanetNames[_PlanetID].toLowerCase());

		// Ports Names List begins Here
		// Port List Size int
		dOut.writeInt(ListSize);
		// Port List Names UTF Strings
		for (int i = 0; i < ListSize; i++) {
			dOut.writeUTF(vTd.get(i).getDestinationName());
		}

		// Port Coordinate List begins here
		// Port Coordinate Count Int
		dOut.writeInt(ListSize);
		// Port Coordinates List X,Y,Z as Floats
		for (int i = 0; i < ListSize; i++) {
			dOut.writeFloat(vTd.get(i).getX());
			dOut.writeFloat(vTd.get(i).getZ());
			dOut.writeFloat(vTd.get(i).getY());
		}

		// Ticket Surcharge List Begins here
		// Ticket Surcharge List Count Int
		dOut.writeInt(ListSize);
		// Ticket surchage List of Ints,
		for (int i = 0; i < ListSize; i++) {

			// for some reason prices have to set at 0 or they get doubled from
			// whats in the client table.
			if (vTd.get(i).getIsPlayerCityShuttle()) {
				dOut.writeInt(vTd.get(i).getCost());
			} else {
				if (vTd.get(i).getDestinationPlanet() == player.getPlanetID()) {
					dOut.writeInt(0);
					// System.out.println("Ticket Price Written at 0");
				} else {
					// dOut.writeInt(player.getServer().ServerTicketPriceList.getTicketPrice(player.getPlanetID(),
					// vTd.get(i).getDestinationPlanet()));
					dOut.writeInt(0);
					// System.out.println("Ticket Price Written");
				}
			}
		}

		// Starport Identification List Begins Here
		// write if its a starport or shuttle port
		// Write a list of bytes 0 is a shuttelport and 1 is a starport
		// Port ID Count Int
		dOut.writeInt(ListSize);
		// Port ID Byte List
		for (int i = 0; i < ListSize; i++) {
			dOut.writeBoolean(vTd.get(i).getIsStarPort());
		}

		dOut.flush();
		return dOut.getBuffer();

	}

	// When ready, alter the paramater to be a Container, not an SOEObject
	protected static byte[] buildOpenContainerMessage(SOEObject o, int iStatus)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.WORLD_UPDATE);
		dOut.writeInt(Constants.OpenedContainerMessage);
		dOut.writeInt(iStatus); // ??? called iStatus for now since i suspect
								// its a status value.
		dOut.writeLong(o.getID());
		dOut.writeShort(0);
		// dOut.writeBoolean(false); not used on nge? do we need this?
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildFlourishResponse(Player player, Player target)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		if (player.equals(target)) {
			dOut.writeInt(0x0B);
		} else {
			dOut.writeInt(0x1B); // This must have some meaning. It's most often
									// 0x0A when modifying the player directly?
		}
		dOut.writeInt(Constants.MobileObjectAnimate);
		dOut.writeLong(player.getID());
		dOut.writeInt(0);
		dOut.writeInt(player.getFlourishID());
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildCommandQueueDequeue(Player player,
			CommandQueueItem command) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.CommandQueueDequeue);
		dOut.writeLong(player.getID());
		dOut.writeInt(0); // Tick count
		dOut.writeInt(command.getCommandID()); // Should be action count.
		dOut.writeFloat(command.getTimer());
		dOut.writeInt(command.getErrorMessageID()); // Unknown (Core 1 calls it
													// string error code.)
		dOut.writeInt(command.getInvokedState()); // Unknown (The state that
													// this command invoked?)
		dOut.flush();
		return dOut.getBuffer();
	}


	protected static byte[] buildChatRoomMessage(Player player,
			String sChatMessage, int chatRoomID) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ChatRoomMessage);
		dOut.writeUTF("SWG");
		dOut.writeUTF(player.getFirstName());
		dOut.writeInt(0);
		dOut.writeInt(chatRoomID);
		dOut.writeUTF16(sChatMessage);
		dOut.writeInt(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildServerWeatherMessage(int weatherType)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ServerWeatherMessage);
		dOut.writeInt(weatherType); // Clear weather.
		dOut.writeFloat(0);
		dOut.writeFloat(0);
		dOut.writeFloat(0);
		dOut.writeFloat(5);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildEmailHeader(SWGEmail email, Player sender,
			Player receiver) throws IOException {
		// System.out.println("Build Email Header.");

		/*
		 * 00 09 05 CC 02 00 //World Update 17 5E 48 08 // 0X08485E17 Email CRC
		 * SOE OP COMMAND 0F 00 69 64 61 72 65 79 61 20 62 65 61 6D 27 61 6E
		 * //astring recipient 03 00 53 57 47 //astring game name 04 00 42 72 69
		 * 61 //astring server name/galaxy 'Bria' 91 BC DA 2F //message id INT
		 * 01 00 00 00 // 00 // bool always 0 0D 00 00 00 74 00 65 00 73 00 74
		 * 00 20 00 6D 00 65 00 73 00 73 00 61 00 67 00 65 00 27 00 //Ustring
		 * Subject // seesm in rev 14 this is the message body 00 00 00 00 //int
		 * 0 Spacer 4E //email status ascII character 52 = R for read 4E = N for
		 * New 24 14 95 46 //int time stamp time(0)//seems as of rev 14 this is
		 * a long 31 27 //crc
		 * 
		 * 
		 * 02 00 17 5E 48 08 //0x08485E17 ServerSendEmailMessage 03 00 72 65 6F
		 * //sender 03 00 53 57 47 //swg 00 00 //cluster name goes here 97 61 00
		 * 00 //email id 01 00 00 00 00 06 00 00 00 74 00 65 00 73 00 74 00 20
		 * 00 31 00 //header / subject
		 * 
		 * 00 00 00 00 //Separator
		 * 
		 * 4E AA 3B 63 48 00 00 00 00 //long????
		 * 
		 * 00 7D E3 ...N.;cH.....}.
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.WORLD_UPDATE);
		dOut.writeInt(Constants.ServerSendEmailMessage);
		dOut.writeUTF(sender.getFirstName()); // 03 00 72 65 6F //sender
		dOut.writeUTF("SWG"); // This is an SWG email.
		dOut.writeUTF(sender.getPlayerCluster());// cluster
		dOut.writeInt(email.getEmailID());// email id
		dOut.writeInt(1);// int 1
		dOut.writeBoolean(false); // bool 0
		dOut.writeUTF16(email.getHeader()); // subject
		dOut.writeInt(0);// separator
		if (email.isRead() == false) {
			if (email.getIsNew()) {
				dOut.writeByte('N'); // ('N');
				email.setIsNew(false);
			} else {
				dOut.writeByte('U');
			}
		} else {
			dOut.writeByte('R'); // ('R');
		}
		long Tl = email.getMessageTime() / 1000;
		int T = (int) Tl;
		dOut.writeInt(T);
		// dOut.writeInt(1);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildEmailContent(SWGEmail message, Player sender)
			throws IOException {
		// System.out.println("Build Email Content.");
		/*
		 * 00 09 05 CE 02 00 //world update 17 5E 48 08 // 0X08485E17 Email CRC
		 * SOE OP COMMAND ChatPersistentMessageToClient??
		 * 
		 * 0F 00 69 64 61 72 65 79 61 20 62 65 61 6D 27 61 6E //astring
		 * recipient 00 00 00 00 //INT
		 * 
		 * 91 BC DA 2F //email id INT
		 * 
		 * 00 // Byte Value always 0
		 * 
		 * 0D 00 00 00 74 00 65 00 73 00 74 00 20 00 73 00 75 00 62 00 6A 00 65
		 * 00 63 00 74 00 27 00 //Ustring Message 0D 00 00 00 74 00 65 00 73 00
		 * 74 00 20 00 6D 00 65 00 73 00 73 00 61 00 67 00 65 00 27 00 //Ustring
		 * Subject
		 * 
		 * 00 00 00 00 //INT Attachment list size //attachment list goes here
		 * 
		 * 4E //email status ascII character 52 = R for read 4E = N for New
		 * 
		 * 23 14 95 46 //int time stamp time(0)
		 * 
		 * 07 7D //CRC
		 * 
		 * 
		 * 02 00 17 5E 48 08 03 00 72 65 6F //recipient 03 00 53 57 47 //game
		 * name swg 00 00 //cluster name 97 61 00 00 00 11 00 00 00 74 00 68 00
		 * 69 00 73 00 20 00 69 00 73 00 20 00 61 00 20 00 74 00 65 00 73 00 74
		 * 00 20 00 31 00 0A 00 06 00 00 00 74 00 65 00 73 00 74 00 20 00 31 00
		 * 00 00 00 00 4E AD 3B 63 48 00 00 00 00 00 2D 8D
		 */

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.WORLD_UPDATE);
		dOut.writeInt(Constants.ServerSendEmailMessage);
		dOut.writeUTF(sender.getFirstName());
		dOut.writeInt(0);
		dOut.writeInt(message.getEmailID());
		dOut.writeBoolean(false);
		dOut.writeUTF16(message.getBody());
		dOut.writeUTF16(message.getHeader());
		Vector<Waypoint> v = message.getAttachments();
		if (v != null) {
			int attachmentCount = 0;
			for (int i = 0; i < v.size(); i++) {
				Waypoint w = v.elementAt(i);
				attachmentCount += 25 + w.getName().length();
			}
			dOut.writeInt(attachmentCount);
			for (int i = 0; i < v.size(); i++) {
				Waypoint w = v.elementAt(i);
				dOut.writeShort(1);
				dOut.writeByte(4);
				dOut.writeInt(0xFFFFFFFD);
				dOut.writeInt(0);
				dOut.writeFloat(w.getX());
				dOut.writeFloat(w.getZ());
				dOut.writeFloat(w.getY());
				dOut.writeInt(0);
				dOut.writeInt(0);
				dOut.writeInt(w.getPlanetCRC());
				dOut.writeUTF16(w.getName());
				dOut.writeInt(0);
				dOut.writeInt(0);
				dOut.writeShort(1);
				dOut.writeByte(0);
			}
		} else {
			dOut.writeInt(0);
		}
		if (message.isRead()) {
			dOut.writeByte(0x52);// 'R');
		} else {
			dOut.writeByte(0x4E); // 'N');
		}
		long Tl = message.getMessageTime() / 1000;
		int T = (int) Tl;
		dOut.writeInt(T);
		dOut.flush();
		return dOut.getBuffer();
	}

	// I'm assuming that this simply plays the new email sound and causes the
	// email icon to blink.
	protected static byte[] buildNewEmailNotificaion() throws IOException {
		// System.out.println("Build Email Notification.");
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.CLIENT_UI_UPDATE);
		dOut.writeInt(Constants.NewEmailNotification);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildErrorMessage(String errorTitle,
			String errorBody) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.CLIENT_UI_UPDATE);
		dOut.writeInt(Constants.ClientUIErrorMessage);
		dOut.writeUTF(errorTitle);
		dOut.writeUTF(errorBody);
		dOut.writeInt(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildChatOnSendInstantMessage(Player player,
			int iChatCode) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.ACCOUNT_UPDATE);
		dOut.writeInt(Constants.ChatOnSendInstantMessage);
		dOut.writeInt(iChatCode);
		dOut.writeInt(player.getTellCount());
		dOut.flush();
		// System.out.println("ChatOnSendInstantMessage.  Response code: " +
		// iChatCode + ", tell count: " + player.getTellCount());
		return dOut.getBuffer();

	}

	protected static byte[] buildChatInstantMessageToClient(String senderName,
			String messageData, String serverName) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.SERVER_UPDATE);
		dOut.writeInt(Constants.ChatInstantMessageToClient);
		dOut.writeUTF("SWG");
		dOut.writeUTF(serverName);
		dOut.writeUTF(senderName);
		dOut.writeUTF16(messageData);
		// System.out.println("ChatInstantMessageToClient.  Server Name: " +
		// serverName + ", senderName: " + senderName + ".\nMessage: " +
		// messageData);
		// Are these really needed?
		dOut.writeInt(0); // Spacer
		dOut.flush();
		return dOut.getBuffer();
	}

	// Note: The SOEObject here will eventually become a TangibleItem
	protected static byte[] buildResourceListForSurveyMessage(TangibleItem o,
			Vector<SpawnedResourceData> vResources) throws IOException {
		int templateID = o.getTemplateID();
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.SERVER_UPDATE);
		dOut.writeInt(Constants.ResourceListForSurveyMessage);
		dOut.writeInt(vResources.size());
		SpawnedResourceData resource = null;

		for (int i = 0; i < vResources.size(); i++) {
			resource = vResources.elementAt(i);
			String sName = resource.getName();
			String sIFFFileName = resource.getIffFileName();

			dOut.writeUTF(resource.getName());

			dOut.writeLong(resource.getID());
			dOut.writeUTF(resource.getIffFileName());

		}
		if (resource != null) {
			dOut.writeUTF(resource.getGenericResourceType());
		} else {
			int resourceTypeIndex = -1;
			switch (templateID) {
			case 14037: {
				resourceTypeIndex = 3;
				break;
			}
			case 14038: {

			}
			case 14039: {
				resourceTypeIndex = 1;
			}
			case 14040: {
				resourceTypeIndex = 2;
			}
			case 14041: {
				resourceTypeIndex = 4;
			}
			case 14042: {
				resourceTypeIndex = 5;
			}
			case 14043: {
				//
			}
			case 14044: {
				resourceTypeIndex = 0;
			}
			case 14045: {
				resourceTypeIndex = 6;
			}
			}
			if (resourceTypeIndex >= 0) {
				dOut
						.writeUTF(SpawnedResourceData.GENERIC_RESOURCE_TYPES[resourceTypeIndex]);
			} else {
				dOut.writeShort(0);
			}
		}
		dOut.writeLong(o.getID());
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildClientEffectAtLocation(String sClientEffect,
			Player player) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.SCENE_UPDATE);
		dOut.writeInt(Constants.PlayClientEffectLocMessage);
		dOut.writeUTF(sClientEffect);
		dOut
				.writeUTF(Constants.PlanetNames[player.getPlanetID()]
						.toLowerCase());
		dOut.writeFloat(player.getX());
		dOut.writeFloat(player.getZ());
		dOut.writeFloat(player.getY());
		dOut.writeInt(0);
		dOut.writeInt(0);
		dOut.writeInt(0); // TODO: Discover meaning, if any, of these 3 ints.
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildChatAccountUpdate(Player target)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.ACCOUNT_UPDATE);
		dOut.writeInt(Constants.ChatAccountUpdate);
		dOut.writeLong(target.getID()); // Just a guess. Only long that makes
										// sense.
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildFriendOfflineStatusUpdate(Player player,
			Player friend) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.UPDATE_SIX);
		dOut.writeInt(Constants.ChatFriendOfflineUpdate);
		dOut.writeLong(player.getID());
		dOut.writeUTF("SWG");
		dOut.writeUTF(friend.getPlayerCluster());
		dOut.writeUTF(friend.getFirstName());
		dOut.writeInt(0);
		dOut.writeBoolean(false);
		dOut.writeInt(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildFriendOnlineStatusUpdate(Player player,
			Player friend) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.ACCOUNT_UPDATE);
		dOut.writeInt(Constants.ChatFriendOnlineUpdate);
		dOut.writeUTF("SWG");
		dOut.writeUTF(friend.getPlayerCluster());
		dOut.writeUTF(friend.getFirstName());
		dOut.writeBoolean(true);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildFriendOfflineStatusUpdate(Player player,
			String sClusterName, String sFriendName) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.UPDATE_SIX);
		dOut.writeInt(Constants.ChatFriendOfflineUpdate);
		dOut.writeLong(player.getID());
		dOut.writeUTF("SWG");
		dOut.writeUTF(sClusterName);
		dOut.writeUTF(sFriendName);
		dOut.writeInt(0);
		dOut.writeBoolean(false);
		dOut.writeInt(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildFriendOnlineStatusUpdate(Player player,
			String sClusterName, String sFriendName) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.ACCOUNT_UPDATE);
		dOut.writeInt(Constants.ChatFriendOnlineUpdate);
		dOut.writeUTF("SWG");
		dOut.writeUTF(sClusterName);
		dOut.writeUTF(sFriendName);
		dOut.writeBoolean(true);
		dOut.flush();
		return dOut.getBuffer();
	}

	// Are we going to support cross-server friends lists? If so, then the
	// structure of the friends list object
	// needs to be reworked. Right now, it's simply a String array.
	protected static byte[] buildFriendsListResponse(Player player)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.ACCOUNT_UPDATE);
		dOut.writeInt(Constants.FriendListRequestResponse);
		dOut.writeLong(player.getID());
		Vector<PlayerFriends> vFriendsList = player.getFriendsList();
		dOut.writeInt(vFriendsList.size());
		Iterator<PlayerFriends> itr = vFriendsList.iterator();
		while (itr.hasNext()) {
			PlayerFriends f = itr.next();
			dOut.writeUTF(Constants.GAME_NAME);
			dOut.writeUTF(f.getServerName());
			dOut.writeUTF(f.getName());
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	/**
	 * This is the NPC Conversation Packet. This will begin a dialog and prompt
	 * with the appropriate STF File name or a custom string.
	 * 
	 * @param player
	 *            - Player Object Receiving the Dialog
	 * @param sConversationSTFFile
	 *            - If using STF Strings this is the file location I/E
	 *            'skill_teacher' do not add the @ sign nor the :
	 * @param sConversationStringInSTF
	 *            - If using STF Strings this is the string name to be sent I/E
	 *            'msg1_1'
	 * @param SpokenNumericArgument
	 *            - If the message to prompr includes a cost or numeric argument
	 *            to be spoken it goes here normally its 0 if there is no cost
	 *            or if its not going to be spoken
	 * @param sSpokenString
	 *            - If using this option do not use the STF Strings since this
	 *            is the only text the npc will speak
	 * @param sSpokenArgumentSTFFile
	 *            - If using STF Strings this is the STF location of any
	 *            argument that the first string used needs I/E 'skl_n' do not
	 *            add the @ sign nor the :
	 * @param aSpokenArgumentSTFName
	 *            - If using STF Strings this is the name of the STF String to
	 *            be used I/E 'crafting_artisan'
	 * @param bShowDialog
	 *            - This makes the dialog dissapear, looks like SOE wanted a way
	 *            to turn a dialog off wihtout changin the way it was sent.
	 * @return
	 * @throws java.io.IOException
	 */
	protected static byte[] buildNPCConversationMessage(Player player,
			String sConversationSTFFile, String sConversationStringInSTF,
			int SpokenNumericArgument, String sSpokenString,
			String sSpokenArgumentSTFFile, String aSpokenArgumentSTFName,
			boolean bShowDialog) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.NpcConversationMessage);
		dOut.writeLong(player.getID());
		dOut.writeInt(0);
		if (sSpokenString.length() != 0) {
			dOut.writeUTF16(sSpokenString);
			dOut.writeInt(0);
		} else {
			dOut.writeInt(sConversationSTFFile.length()
					+ sConversationStringInSTF.length()
					+ sSpokenArgumentSTFFile.length()
					+ aSpokenArgumentSTFName.length() + 48); // /((0x56 +
																// sConversationSTFFile.length()
																// +
																// sConversationStringInSTF.length()
																// +
																// (bShowDialog
																// ? 1 : 0)) /
																// 2);
			dOut.writeInt(0);
			dOut.writeBoolean(bShowDialog);
			dOut.writeInt(-1); // 0xFFFFFFFF
			dOut.writeUTF(sConversationSTFFile);
			dOut.writeInt(0);
			dOut.writeUTF(sConversationStringInSTF);
			dOut.write(new byte[48]);
			dOut.writeUTF(sSpokenArgumentSTFFile);
			dOut.writeInt(0);
			dOut.writeUTF(aSpokenArgumentSTFName);
			dOut.writeInt(0);
			dOut.writeInt(SpokenNumericArgument); // BC 02 00 00 /
			dOut.writeInt(0);
			dOut.writeShort(0);

		}
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildNPCConversationOptions(Player player,
			Vector<DialogOption> vOptions) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.NPCConversationOptions);
		dOut.writeLong(player.getID());
		dOut.writeInt(0);
		// There can be only 5 options sent out to the client at any one time.
		// The code will need to account for this,
		// and send options appropriately, especially when one of the options is
		// "More..."
		int actualOptions = vOptions.size(); // Math.min(5, vOptions.size());
		// System.out.println("Options Count: " + vOptions.size());
		dOut.writeByte(actualOptions);
		// DialogOption p = null;
		boolean bShowDialog = false;
		String sConversationSTFFile = null;
		String sConversationStringInSTF = null;
		// String sSpokenArgumentSTFFile = ""; //not in use atm
		// String aSpokenArgumentSTFName = ""; //not in use atm
		// int SpokenNumericArgument = 0; // not in use atm

		for (int i = 0; i < actualOptions; i++) {
			// System.out.println("Putting Option " + i);
			DialogOption o = vOptions.get(i); // .elementAt(i);
			if (o == null) {
				System.out.println("Null Option " + i);
				o = new DialogOption(true, "ui", "error");
			}
			bShowDialog = o.getShowDialog();
			sConversationSTFFile = o.getSTFFile();
			sConversationStringInSTF = o.getStringSTF();
			// Constant 86 + the Length of Both Strings.
			int size = ((sConversationSTFFile.length()
					+ sConversationStringInSTF.length() + 86));
			boolean TackBool = false;
			// if the length of the count is odd add 1 to it and add one byte at
			// the end.
			if ((size & 1) > 0) {
				TackBool = true;
				size++;
			}
			// divide the size between two as to why dunno but this gives us the
			// right size.
			size = size / 2;
			dOut.writeInt(size);
			dOut.writeInt(0);
			dOut.writeBoolean(bShowDialog);
			dOut.writeInt(-1);
			dOut.writeUTF(sConversationSTFFile);
			dOut.writeInt(0);
			dOut.writeUTF(sConversationStringInSTF);
			dOut.writeInt(0);
			dOut.write(new byte[64]); // inside here seems to be additional
										// string locations
			dOut.writeByte(0);
			if (TackBool) {
				dOut.writeByte(0);
			}

		}

		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildStartNPCConversation(Player player,
			Player target) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.StartNpcConversation);
		dOut.writeLong(player.getID());
		dOut.writeInt(0);
		dOut.writeLong(target.getID());
		dOut.writeInt(0);
		dOut.writeShort(0);
		dOut.writeByte(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	/**
	 * This will build a client animation i/e a "skill_action_" + flourishID
	 * animation will come from the getPerformedAnimation method
	 * 
	 * @param player
	 *            - Player executing the move, and same player to receive the
	 *            move.
	 * @return
	 * @throws java.io.IOException
	 * @note : This packet does not work when sent to chat range since its meant
	 *       for the player executing the effect only. To execute from a third
	 *       party's point of view use ??? Sending this packet to chatrange is a
	 *       waste of bandwidth.
	 */
	protected static byte[] buildCharacterAnimation(Player player)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.MobileAnimation);
		dOut.writeLong(player.getID());
		dOut.writeInt(0);
		dOut.writeUTF(player.getPerformedAnimation());
		dOut.flush();
		return dOut.getBuffer();
	}

	/**
	 * This will build a client animation i/e "skill_action_" + flourishID
	 * 
	 * @param player
	 *            - Player executing the move, and same player to receive the
	 *            move.
	 * @param spectator
	 *            - The player receiving the animation
	 * @param sAnimation
	 *            - client animation to play
	 * @return
	 * @throws java.io.IOException
	 * @note : This packet does not work when sent to chat range since its meant
	 *       for the player executing the animation only. To execute from a
	 *       third party's point of view use :??? Sending this packet to
	 *       chatrange is a waste of bandwidth.
	 */

	protected static byte[] buildCharacterAnimation(Player player,
			Player spectator, String sAnimation) throws IOException {
		/*
		 * 05 00 46 5E CE 80 1B 00 00 00 // 0B 00 00 00 <---THIS CONTROLS WHO
		 * RECEIVES THE ANIMATION IS PLAYED ON, 0B = SELF, 1B OTHER F2 00 00 00
		 * FE 5C C9 21 2F 00 00 00 00 00 00 00 0E 00 73 6B 69 6C 6C 5F 61 63 74
		 * 69 6F 6E 5F 34 //skill_action_4 *
		 */

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		if (player.equals(spectator)) {
			dOut.writeInt(0x0B);// 0x0B
		} else {
			dOut.writeInt(0x1B);// 0x0B
		}
		dOut.writeInt(Constants.MobileAnimation);
		dOut.writeLong(player.getID());
		dOut.writeInt(0);
		dOut.writeUTF(sAnimation);
		dOut.flush();
		return dOut.getBuffer();
	}

	// Note: This is a testing packet.
	protected static byte[] buildClientEffectObject(Player player,
			SOEObject target) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.UPDATE_SIX);
		dOut.writeUTF(player.getCurrentEffectOnOtherObjects()); // Is this an
																// effect made
																// by another
																// player? Must
																// be...
		dOut.writeShort(0);
		dOut.writeLong(target.getID());
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildClientEffectOnObject(Player player,
			SOEObject target, String sClientEffect) throws IOException {

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE); // operand this was set to
														// update 6, yet its
														// seen as obj update
		dOut.writeInt(Constants.PlayClientEffectObjectMessage); // opcode
		dOut.writeUTF(sClientEffect); // Effect file to play
		dOut.writeShort(0); // NULL bytes
		dOut.writeLong(player.getID()); // ID recieving effect.
		dOut.writeShort(0); // NULL bytes possibly not needed
		dOut.flush();
		return dOut.getBuffer();
	}

	// Do I want to pass the int in as a CRC, or do I want to pass in the
	// CombatAction itself?
	protected static byte[] buildObjectController_CombatAction(
			Player originator, Player[] target, Player observer, CombatAction a)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(Constants.ObjectActionEnqueue);
		dOut.writeInt(Constants.CombatAction);
		dOut.writeLong(originator.getID());
		dOut.writeInt(0);
		dOut.writeInt(a.getCRC());
		dOut.writeLong(originator.getID());
		Weapon w = originator.getWeapon();
		if (w != null) {
			dOut.writeLong(w.getID());
		} else {
			dOut.writeLong(0);
		}
		dOut.writeByte(originator.getStance());
		if (a.getTrails()) {
			dOut.writeShort(0xFF);
		} else {
			dOut.writeShort(0);
		}
		dOut.writeShort(target.length);
		for (int i = 0; i < target.length; i++) {
			dOut.writeLong(target[i].getID());
			dOut.writeInt(0);
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildObjectController_CombatAction(
			Player originator, Player target, int actionCRC, boolean bTrails)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(Constants.ObjectActionEnqueue);
		dOut.writeInt(Constants.CombatAction);
		dOut.writeLong(originator.getID());
		dOut.writeInt(0);
		dOut.writeInt(actionCRC);
		dOut.writeLong(originator.getID());
		Weapon w = originator.getWeapon();
		if (w != null) {
			dOut.writeLong(w.getID());
		} else {
			dOut.writeLong(0);
		}
		dOut.writeByte(originator.getStance());
		if (bTrails) {
			dOut.writeShort(0xFF);
		} else {
			dOut.writeShort(0);
		}
		dOut.writeShort(1);
		dOut.writeLong(target.getID());
		dOut.writeByte(target.getStance());
		dOut.writeShort(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildChatroomList(Vector<ChatServer> vServers)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.WORLD_UPDATE);
		dOut.writeInt(vServers.size());
		ChatServer s;
		Vector<Chatroom> vRooms;
		Chatroom r;
		for (int i = 0; i < vServers.size(); i++) {
			s = vServers.elementAt(i);
			dOut.writeInt(s.getID());
			vRooms = s.getRooms();
			dOut.writeInt(vRooms.size());
			dOut.writeByte(1); // Unknown.
			for (int j = 0; j < vRooms.size(); j++) {
				r = vRooms.elementAt(i);
				dOut.writeUTF(r.getRoomName());
				dOut.writeUTF(Constants.GAME_NAME);
				dOut.writeUTF(r.getCreator());
				dOut.writeUTF(r.getDescription());
				// I'm assuming that the list of moderators and list of people
				// in the room should be written here.
				// If it isn't, then these 2 loops would go outside the "j"
				// loop... or even outside the "i" loop.
				Vector<String> vModerators = r.getModeratorList();
				Vector<String> vUsers = r.getPlayersInRoom();
				dOut.writeInt(1);
				for (int k = 0; k < vModerators.size(); k++) {
					dOut.writeUTF(vModerators.elementAt(k));
				}
				dOut.writeInt(0);
				for (int k = 0; k < vUsers.size(); k++) {
					dOut.writeUTF(vUsers.elementAt(i));
				}
			}
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildChatRoomEntered(Player player, Chatroom r)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ChatOnEnteredRoom);
		dOut.writeUTF(Constants.GAME_NAME);
		dOut.writeUTF(r.getRoomName());
		dOut.writeUTF(player.getFirstName());
		dOut.writeInt(0);
		dOut.writeInt(r.getServer().getID());
		dOut.writeInt(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildFriendsListUpdateDelta(Player p) {

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		try {
			int ByteCount;
			ByteCount = 15; // count without names

			Vector<PlayerFriends> vPlayerFriendsList = p.getFriendsList();
			Iterator<PlayerFriends> itr = vPlayerFriendsList.iterator();

			while (itr.hasNext()) {
				PlayerFriends f = itr.next();
				ByteCount = ByteCount + (f.getName().length() + 2); // +2 for
																	// the
																	// astring
																	// length
																	// short
			}

			dOut.writeShort(Constants.OBJECT_UPDATE); // 05 00 //OBJECT_UPDATE
			dOut.writeInt(Constants.DeltasMessage); // 53 21 86 12
													// //DeltasMessage =
													// 0x12862153,
			dOut.writeLong(p.getFriendsListID()); // 33 12 5C 14 06 00 00 00
													// //player ID + (11
													// FRIEND_LIST_OFFSET)//A6
													// 05 49 05 054905a6 =
													// 88671654 , B0 05 49 05
													// 054905b0 = 88671664
			dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_PLAY]); // 59
																				// 41
																				// 4C
																				// 50
																				// //YALP
																				// //54905AD
																				// AD
																				// 05
																				// 49
																				// 05
			dOut.writeByte(9); // 09 //9
			dOut.writeInt(ByteCount); // F1 01 00 00 //Byte Count after this int
			dOut.writeShort(1); // 01 00 //short operand count
			dOut.writeShort(7); // 07 00 //Short Friends list ?
			dOut.writeInt(vPlayerFriendsList.size() + 1); // 3A 00 00 00 //int
															// list items count:
															// 3A HEX = 58 DEC
			dOut.writeInt(p.getFriendsListUpdateCounter(true)); // 24 01 00 00
																// update
																// counter
			dOut.writeByte(3); // 03 unknown
			dOut.writeShort((short) vPlayerFriendsList.size()); // 39 00 //Short
																// Name Count
																// dec 57 names

			Iterator<PlayerFriends> itrb = vPlayerFriendsList.iterator();
			while (itrb.hasNext()) {
				dOut.writeUTF(itrb.next().getName()); // astring friend name
														// //06 00 73 61 6C 6C
														// 75 73
			}
			dOut.flush();
			return bOut.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	protected static byte[] buildWaypointDelta(Player p, Waypoint w,
			byte changeStatus) {
		// System.out.println("Building waypoint delta for waypoint with name "
		// + w.getDestinationName() + ", player " + p.getFullName());
		int len = 63;
		if (w == null) {
			System.out.println("Null Waypoint in buildWaypointDelta");
		}
		if (w.getName() == null) {
			w.setName("");
		}
		len += (w.getName().length() * 2);

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		try {
			dOut.setOpcode(Constants.SOE_CHL_DATA_A);
			dOut.setSequence(0);
			dOut.setUpdateType(Constants.OBJECT_UPDATE);
			dOut.writeInt(Constants.DeltasMessage);
			dOut.writeLong(p.getPlayObjectID());
			dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_PLAY]);
			dOut.writeByte(8);
			dOut.writeInt(len);
			dOut.writeShort(1); // Number of updates this packet.
			dOut.writeShort(1); // vID being updated
			dOut.writeInt(1); // // Number of updates this vID this packet
			dOut.writeInt(p.getPlayData().getWaypointUpdateCount(true)); // number
																			// of
																			// updates
																			// this
																			// vID
																			// lifetime.
			dOut.writeByte(changeStatus); // Correct
			dOut.writeLong(w.getID()); // Correct
			dOut.writeInt(0); // 25
			dOut.writeFloat(w.getX()); // 29
			dOut.writeFloat(w.getZ()); // 33
			dOut.writeFloat(w.getY()); // 37
			dOut.writeLong(0); // 45
			dOut.writeInt(w.getPlanetCRC()); // 49
			dOut.writeUTF16(w.getName()); // 53 + name length
			dOut.writeLong(w.getID()); // 61
			dOut.writeByte(w.getWaypointType()); // 62
			dOut.writeBoolean(w.getIsActivated()); // 63
			dOut.flush();
			return bOut.toByteArray();
		} catch (Exception e) {
			System.out.println("Error creating waypoint deltas message: "
					+ e.toString());
			e.printStackTrace();
		}
		return null;
	}

	protected static byte[] buildExperienceDelta(PlayerItem p,
			PlayerExperience exp, byte updateType) {
		// System.out.println("Building experience delta for experience type " +
		// exp.getExperienceName());
		int len = 19 + exp.getExperienceName().length();
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		try {
			dOut.setOpcode(Constants.SOE_CHL_DATA_A);
			dOut.setSequence(0);
			dOut.setUpdateType(Constants.OBJECT_UPDATE);
			dOut.writeInt(Constants.DeltasMessage);
			dOut.writeLong(p.getID());
			dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_PLAY]);
			dOut.writeByte(8);
			dOut.writeInt(len);
			dOut.writeShort(1); // Number of updates this packet.
			dOut.writeShort(0); // vID being updated
			dOut.writeInt(1); // // Number of updates this vID this packet
			dOut.writeInt(p.getExperienceListUpdateCount(true)); // number of
																	// updates
																	// to this
																	// vID since
																	// we sent
																	// the
																	// entire
																	// Play 8
																	// packet
																	// last
																	// time.
			dOut.writeByte(updateType); // UpdateType -- 0 = creating, 1 =
										// deleting, 2 = updating.
			dOut.writeUTF(exp.getExperienceName());
			dOut.writeInt(exp.getCurrentExperience());
			dOut.flush();
			return dOut.getBuffer();
		} catch (Exception e) {
			System.out.println("Error writing Experience delta to packet: "
					+ e.toString());
			e.printStackTrace();
		}
		return null;
	}

	protected static byte[] buildOutOfOrderPacket(short sequence)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_OUT_ORDER_PKT_A);
		dOut.setSequence(sequence);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildObjectControllerMessage_UpdatePosture(Player o)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0); // Will be replaced.
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(Constants.ObjectActionEnqueue);
		dOut.writeInt(Constants.PlayerPostureChange);
		dOut.writeLong(o.getID());
		dOut.writeInt(0);
		dOut.writeByte(o.getStance());
		dOut.writeBoolean(true);
		dOut.flush();
		return dOut.getBuffer();

	}

	protected static byte[] buildUpdatePostureMessage(Player p)
			throws IOException {
		/**
		 * 03 00 41 6B DE 0B //0BDE6B41 UpdatePosture 00 30 0A F5 04 2D 00 00 00
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0); // Will be replaced.
		dOut.setUpdateType(Constants.ACCOUNT_UPDATE);
		dOut.writeInt(Constants.UpdatePosture);
		dOut.writeByte(p.getStance());
		dOut.writeLong(p.getID());
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildSUISetSurveyToolRange(int iNumOptions,
			long tangibleItemID, long playerID) throws IOException {
		int iUpdates = 8 + (2 * iNumOptions);
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.WORLD_UPDATE);
		dOut.writeInt(Constants.SuiCreatePageMessage);
		int lastSurveyWindow = (int) System.currentTimeMillis();
		dOut.writeInt(lastSurveyWindow); // Window ID. This can be anything,
											// according to Meanmon.
		dOut.writeUTF("Script.listBox"); // This is the type of script we're
											// running. listBox, messageBox,
											// etc. etc.
		dOut.writeInt(iUpdates); // Number of updates we are performing to this
									// box.
		// --------------------------------
		dOut.writeByte(5); // Update type. 5 = modify?
		dOut.writeInt(0); // Number of variables we are updating.
		dOut.writeInt(7); // List size.
		{
			dOut.writeShort(0x00);
			dOut.writeByte(0x01);
			dOut.writeByte(0x00);
			dOut.writeByte(0x09);
		} // Unknown data.
		dOut.writeUTF("handleSetRange"); // String 1.
		dOut.writeUTF("List.lstList"); // String 2
		dOut.writeUTF("SelectedRow"); // String 3
		dOut.writeUTF("bg.caption.lblTitle"); // String 4
		dOut.writeUTF("Text"); // String 5
		// --------------------------------
		dOut.writeByte(0x05);
		dOut.writeInt(0);
		dOut.writeInt(7);
		{
			dOut.writeShort(0);
			dOut.writeByte(0x01);
			dOut.writeByte(0);
			dOut.writeByte(0x0a);
		}
		dOut.writeUTF("handleSetRange");
		dOut.writeUTF("List.lstList");
		dOut.writeUTF("SelectedRow");
		dOut.writeUTF("bg.caption.lblTitle");
		dOut.writeUTF("Text");
		// --------------------------------

		dOut.writeByte(0x03); // Update type 3 = Create object?
		dOut.writeInt(1); // Component count
		dOut.writeUTF16("@base_player:swg"); // Component name?
		dOut.writeInt(2); // Subcomponent count
		dOut.writeUTF("bg.caption.lblTitle"); // Component 1
		dOut.writeUTF("Text"); // Component 2

		// --------------------------------
		dOut.writeByte(0x03);
		dOut.writeInt(1);
		dOut.writeUTF16("@survey:select_range");
		dOut.writeInt(2);
		dOut.writeUTF("Prompt.lblPrompt");
		dOut.writeUTF("Text");
		// --------------------------------
		dOut.writeByte(0x03);
		dOut.writeInt(1);
		dOut.writeUTF16("False");
		dOut.writeInt(2);
		dOut.writeUTF("btnCancel");
		dOut.writeUTF("Enabled");
		// --------------------------------
		dOut.writeByte(0x03);
		dOut.writeInt(1);
		dOut.writeUTF16("False");
		dOut.writeInt(2);
		dOut.writeUTF("btnCancel");
		dOut.writeUTF("Visible");
		// --------------------------------
		dOut.writeByte(0x03);
		dOut.writeInt(1);
		dOut.writeUTF16("@ok");
		dOut.writeInt(2);
		dOut.writeUTF("btnOk");
		dOut.writeUTF("Text");
		// --------------------------------
		dOut.writeInt(1);
		dOut.writeByte(0x00);
		dOut.writeInt(1);
		dOut.writeUTF("List.dataList");
		// --------------------------------
		// Here begins the actual list that we are putting into the box we just
		// made.
		for (int i = 0; i < iNumOptions; i++) {
			dOut.writeByte(0x04); // Add object to the data list.
			dOut.writeByte(1); // Main component count?
			dOut.writeUTF16(Integer.toString(i));
			dOut.writeInt(2); // Subcomponent count?
			dOut.writeUTF("List.dataList");
			dOut.writeUTF("Name");
			// --------------------------------
			dOut.writeByte(0x03); // Modify an object.
			dOut.writeInt(1); // Component count.
			switch (i) {
			case 0:
				dOut.writeUTF16("64m x 3pts"); // Main component.
				dOut.writeInt(2); // Subcomponent count
				dOut.writeUTF("List.dataList.0"); // Subcomponent 1
				break;
			case 1:
				dOut.writeUTF16("128m x 4pts");
				dOut.writeInt(2);
				dOut.writeUTF("List.dataList.1");
				break;
			case 2:
				dOut.writeUTF16("192m x 4pts");
				dOut.writeInt(2);
				dOut.writeUTF("List.dataList.2");
				break;
			case 3:
				dOut.writeUTF16("256m x 5pts");
				dOut.writeInt(2);
				dOut.writeUTF("List.dataList.3");
				break;
			case 4:
				dOut.writeUTF16("320m x 5pts");
				dOut.writeInt(2);
				dOut.writeUTF("List.dataList.4");
				break;
			}
			dOut.writeUTF("Text"); // Subcomponent 2.
		}
		// --------------------------------
		dOut.writeLong(0); // Object ID of the item generating the dialog box
							// (if any).
		dOut.writeInt(0); // Spacer?
		dOut.writeLong(0); // Object ID of the user, if necessary.
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildSurveyMessage(int iSurveyToolRangeOption,
			SpawnedResourceData theResource, Player player) throws IOException {
		System.out.println("buildSurveyMessage called");
		int iPlanetID = player.getPlanetID();
		float fToolRadius = 0;
		Vector<Float> vDensitiesAndLocations = null; // Note: Floats are in this
														// vector in order by
														// of: X coordinate, Y
														// coordinate, density
														// value.
		int numberOfPoints = 0;
		int iDivider = 0;

		if (iSurveyToolRangeOption == 0) {
			iDivider = 2;
			fToolRadius = 64.0f;
			numberOfPoints = 9;
		} else if (iSurveyToolRangeOption == 1) {
			iDivider = 3;
			fToolRadius = 128.0f;
			numberOfPoints = 16;
		} else if (iSurveyToolRangeOption == 2) {
			iDivider = 3;
			fToolRadius = 192.0f;
			numberOfPoints = 16;
		} else if (iSurveyToolRangeOption == 3) {
			iDivider = 4;
			fToolRadius = 256.0f;
			numberOfPoints = 25;
		} else if (iSurveyToolRangeOption == 4) {
			iDivider = 4;
			fToolRadius = 320.0f;
			numberOfPoints = 25;
		} else {
			// Admin command
			iDivider = 5;
			fToolRadius = 3072.0f;
			numberOfPoints = 25;
		}

		float distanceIncrement = fToolRadius / (float) iDivider;
		vDensitiesAndLocations = theResource.getDensitiesForSurveyToolUsage(
				player.getX(), player.getY(), fToolRadius, distanceIncrement,
				numberOfPoints);

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.WORLD_UPDATE);
		dOut.writeInt(Constants.SurveyMessage);
		dOut.writeInt(numberOfPoints);
		float bestDensity = 0;
		int j = 0;
		float bestX = 0;
		float bestY = 0;
		for (int i = 0; i < numberOfPoints; i++) {
			j = i * 3;
			float x = vDensitiesAndLocations.elementAt(j);
			float y = vDensitiesAndLocations.elementAt(j + 1);
			float density = vDensitiesAndLocations.elementAt(j + 2) / 100f;
			dOut.writeFloat(x);
			dOut.writeFloat(0);
			dOut.writeFloat(y);
			dOut.writeFloat(density);
			if (density > bestDensity) {
				bestDensity = density;
				bestX = x;
				bestY = y;
			}
		}
		dOut.flush();
		if (bestDensity > 0.10f) {
			Waypoint w = player.getSurveyWaypoint();
			if (w == null) {
				w = new Waypoint();
			}
			w.setName("Survey location");
			w.setPlanetID(iPlanetID);
			w.setPlanetCRC(Constants.PlanetCRCForWaypoints[iPlanetID]);
			w.setX(bestX);
			w.setY(bestY);
			w.setZ(0);
			w.setIsActivated(true);
			w.setWaypointType(Constants.WAYPOINT_TYPE_PLAYER_CREATED);
			player.addSurveyWaypoint(w);

			player.getClient().insertPacket(
					buildChatSystemMessage("survey", "survey_waypoint", 0l, "",
							"", "", 0l, "", "", "", 0l, "", "", "", 0, 0f,
							false));

		}
		return dOut.getBuffer();
	}

	protected static byte[] buildGetMapLocationsResponseMessage(
			String sPlanetName, Vector<MapLocationData> vStaticLocations,
			Vector<MapLocationData> vPlayerMadeLocations) throws IOException {

		// vStaticLocations.clear();
		// vPlayerMadeLocations.clear();
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.MapLocationsResponseMessage);
		dOut.writeUTF(sPlanetName);

		if (vStaticLocations != null) {
			dOut.writeInt(vStaticLocations.size());
			for (int i = 0; i < vStaticLocations.size(); i++) {
				MapLocationData data = vStaticLocations.elementAt(i);
				dOut.writeLong(data.getObjectID());
				dOut.writeUTF16(data.getName());
				dOut.writeFloat(data.getCurrentX());
				dOut.writeFloat(data.getCurrentY());
				dOut.writeByte(data.getObjectType());
				dOut.writeByte(data.getObjectSubType());
				dOut.writeByte(0); // Probably only need the 2 types -- 3 types
									// probably exist for simplicity sake --
									// they could use the same structure here as
									// in the Bazaar listings.
			}
		} else {
			dOut.writeInt(0);
		}

		if (vPlayerMadeLocations != null) {
			dOut.writeInt(vPlayerMadeLocations.size());
			for (int i = 0; i < vPlayerMadeLocations.size(); i++) {
				MapLocationData data = vPlayerMadeLocations.elementAt(i);
				dOut.writeLong(data.getObjectID());
				dOut.writeUTF16(data.getName());
				dOut.writeFloat(data.getCurrentX());
				dOut.writeFloat(data.getCurrentY());
				dOut.writeByte(data.getObjectType());
				dOut.writeByte(data.getObjectSubType());
				dOut.writeByte(0); // Probably only need the 2 types -- 3 types
									// probably exist for simplicity sake --
									// they could use the same structure here as
									// in the Bazaar listings.

			}
		} else {
			dOut.writeInt(0);
		}
		dOut.writeInt(0); // A third list goes here.

		// Footer goes here -- 12 bytes of 0.
		dOut.writeLong(0);
		dOut.writeInt(0);
		dOut.flush();
		return dOut.getBuffer();

	}

	/*
	 * // Packets that are incomplete / broken below.
	 * ----------------------------
	 * ----------------------------------------------
	 * ------------------------------------
	 * 
	 * ByteBuffer* ZClient::BUILD_SendUICommand(){ ByteBuffer *SUIC = new
	 * ByteBuffer(); SUIC->writeSHORT(2); SUIC->writeASTRING("ui");
	 * SUIC->writeINT(sizeof(uiCommandCodes[ZClient::uiCommand]));
	 * SUIC->writeASTRING(uiCommandCodes[ZClient::uiCommand]);
	 * printf("SUIC:%s",uiCommandCodes[ZClient::uiCommand]); return SUIC; }
	 * 
	 * // Packets that are waiting on more functions for the player below.
	 * ------
	 * --------------------------------------------------------------------
	 * ------------------------------------
	 * 
	 * 
	 * // Waiting on SUIManager class and, possibly, a SUIWindow class.
	 * 
	 * ByteBuffer* ZClient::BUILD_SUI_PurchaseComplete(){
	 * printf("Building SuiPurchaseTicketComplete.\n"); ByteBuffer *STPC = new
	 * ByteBuffer(); //int iBytesWritten = 0;
	 * //SSTR->writeSHORT(SOE_CHL_DATA_A); //SSTR->writeSHORT(server_sequence);
	 * // Packet size. Can be 1 of 5 values. Each list item is 94 bytes long. //
	 * If 5 menu options, packet size = 977 bytes. // If 4 menu options, packet
	 * size = 883 bytes. // If 3 menu options, packet size = 789 bytes. // If 2
	 * menu options, packet size = 695 bytes. Should be 695. // If only 1 menu
	 * option, packet size = 601 bytes. //SSTR->writeINT(977);
	 * 
	 * /* 00 09 04 7D 02 00 //WORLD_UPDATE 59 72 4B D4 //SuiCreatePageMessage =
	 * 0xD44B7259, 4C 8E 04 00 //window ID 11 00 //astring length 53 63 72 69 70
	 * 74 2E 6D 65 73 73 61 67 65 42 6F 78 // Script.messageBox //astring 08 00
	 * 00 00 //int num of updates 05 // byte 5 // 00 00 00 00 //int 0 03 00 00
	 * 00 //list size 00 00 //short 0 01 //byte 1 00 //byte 0 09 //byte 9 09 00
	 * //astring length 68 61 6E 64 6C 65 53 55 49 //handleSUI //astring 05
	 * //byte 5 00 00 00 00 //int 0 03 00 00 00 //int 3 00 00 //short 0 01
	 * //byte 1 00 //byte 0 0A //byte 0A 09 00 //astring length 68 61 6E 64 6C
	 * 65 53 55 49 //handleSUI //astring 03 01 00 00 00 //item in list 20 00 00
	 * 00 //ustring length 40 00 74 00 72 00 61 00 76 00 65 00 6C 00 3A 00
	 * @.t.r.a.v.e.l.:. 74 00 69 00 63 00 6B 00 65 00 74 00 5F 00 70 00
	 * t.i.c.k.e.t._.p. 75 00 72 00 63 00 68 00 61 00 73 00 65 00 5F 00
	 * u.r.c.h.a.s.e._. 63 00 6F 00 6D 00 70 00 6C 00 65 00 74 00 65 00
	 * c.p.m.p.l.e.t.e.
	 * 
	 * 02 00 00 00 //int 2 10 00 //astring length 50 72 6F 6D 70 74 2E 6C 62 6C
	 * 50 72 6F 6D 70 74 //Prompt.lblPrompt // astring
	 * 
	 * 04 00 //astring length 54 65 78 74 //text astring 03 //byte 3 01 //byte 1
	 * 00 00 //short 0 00 //byte 0 10 00 00 00 //ustring length 40 00 62 00 61
	 * 00 73 00 65 00 5F 00 70 00 6C 00 61 00 79 00 65 00 72 00 3A 00 73 00 77
	 * 00 67 00 //@.b.a.s.e._.p.l.a.y.e.r.:.s.w.g.
	 * 
	 * 02 00 00 00 //item in list 13 00 62 67 2E 63 61 70 74 69 6F 6E 2E 6C 62
	 * 6C 54 69 74 6C 65 //bg.caption.lblTitle.. 04 00 //astring length 54 65 78
	 * 74 //Text//astring 03 01 00 00 00 05 00 00 00 46 00 61 00 6C 00 73 00 65
	 * 00 //F.a.l.s.e. 02 00 00 00 09 00 62 74 6E 43 61 6E 63 65 6C //btnCancel
	 * 07 00 45 6E 61 62 6C 65 64 //Enabled 03 01 00 00 00 05 00 00 00 46 00 61
	 * 00 6C 00 73 00 65 00 //F.a.l.s.e. 02 00 00 00 09 00 62 74 6E 43 61 6E 63
	 * 65 6C //btnCancel 07 00 56 69 73 69 62 6C 65 //Visible 03 01 00 00 00 05
	 * 00 00 00 46 00 61 00 6C 00 73 00 65 00 //F.a.l.s.e. 02 00 00 00 09 00 62
	 * 74 6E 52 65 76 65 72 74 //btnRevert 07 00 45 6E 61 62 6C 65 64 //Enabled
	 * 03 01 00 00 00 05 00 00 00 46 00 61 00 6C 00 73 00 65 00 //F.a.l.s.e 02
	 * 00 00 00 09 00 62 74 6E 52 65 76 65 72 74 //btnRevert 07 00 56 69 73 69
	 * 62 6C 65 //Visible 00 00 00 00 00 00 00 00 //// Object ID of the item
	 * generating the dialog box (if any). 00 00 00 00 //spacer 00 00 00 00 00
	 * 00 00 00 //players id 37 21 //crc
	 * 
	 * 
	 * STPC->uType(WORLD_UPDATE); STPC->opcode2(SuiCreatePageMessage);
	 * STPC->writeINT(lastTicketWindow); // Window ID. This can be anything,
	 * according to Meanmon. STPC->writeASTRING("Script.messageBox"); // This is
	 * the type of script we're running. listBox, messageBox, etc. etc.
	 * STPC->writeINT(0x08);//int num of updates STPC->writeBYTE(0x05); // byte
	 * 5 // STPC->writeINT(00); //int 0 STPC->writeINT(0x03); //list size
	 * STPC->writeSHORT(0x00); //short 0 STPC->writeBYTE(0x01); //byte 1
	 * STPC->writeBYTE(0x00); //byte 0 STPC->writeBYTE(0x09); //byte 9
	 * STPC->writeASTRING("handleSUI");//09 00 //astring length 68 61 6E 64 6C
	 * 65 53 55 49 //handleSUI //astring STPC->writeBYTE(0x05); //byte 5
	 * STPC->writeINT(0x00); //int 0 STPC->writeINT(0x03); //int 3
	 * STPC->writeSHORT(0x00); //short 0 STPC->writeBYTE(0x01); //byte 1
	 * STPC->writeBYTE(0x00); //byte 0 STPC->writeBYTE(0x0A); //byte 0A
	 * STPC->writeASTRING("handleSUI");//09 00 //astring length 68 61 6E 64 6C
	 * 65 53 55 49 //handleSUI //astring STPC->writeBYTE(0x03);
	 * STPC->writeINT(0x01); //item in list //20 00 00 00 //ustring length //40
	 * 00 74 00 72 00 61 00 76 00 65 00 6C 00 3A 00 @.t.r.a.v.e.l.:. //74 00 69
	 * 00 63 00 6B 00 65 00 74 00 5F 00 70 00 t.i.c.k.e.t._.p. //75 00 72 00 63
	 * 00 68 00 61 00 73 00 65 00 5F 00 u.r.c.h.a.s.e._. //63 00 6F 00 6D 00 70
	 * 00 6C 00 65 00 74 00 65 00 c.p.m.p.l.e.t.e.
	 * STPC->writeUSTRING("@travel:ticket_purchase_complete");
	 * STPC->writeINT(0x02); //int 2 STPC->writeASTRING("Prompt.lblPrompt");//10
	 * 00 //astring length 50 72 6F 6D 70 74 2E 6C 62 6C 50 72 6F 6D 70 74
	 * //Prompt.lblPrompt // astring STPC->writeASTRING("Text"); // 04 00
	 * //astring length 54 65 78 74 //text astring STPC->writeBYTE(0x03); //byte
	 * 3 STPC->writeBYTE(0x01); //byte 1 STPC->writeSHORT(0x00); //short 0
	 * STPC->writeBYTE(0x00);//byte 0 //10 00 00 00 //ustring length 40 00 62 00
	 * 61 00 73 00 65 00 5F 00 70 00 6C 00 61 00 79 00 65 00 72 00 3A 00 73 00
	 * 77 00 67 00 //@.b.a.s.e._.p.l.a.y.e.r.:.s.w.g.
	 * STPC->writeUSTRING("@base_player:swg"); STPC->writeINT(0x02);//item in
	 * list STPC->writeASTRING("bg.caption.lblTitle"); //13 00 62 67 2E 63 61 70
	 * 74 69 6F 6E 2E 6C 62 6C 54 69 74 6C 65 //bg.caption.lblTitle..
	 * STPC->writeASTRING("Text"); // 04 00 //astring length 54 65 78 74 //text
	 * astring STPC->writeBYTE(0x03); //byte 3 STPC->writeINT(0x01);//int 1
	 * STPC->writeUSTRING("False");//05 00 00 00 46 00 61 00 6C 00 73 00 65 00
	 * //F.a.l.s.e. STPC->writeINT(0x02);//int 2
	 * STPC->writeASTRING("btnCancel");// 09 00 62 74 6E 43 61 6E 63 65 6C
	 * //btnCancel STPC->writeASTRING("Enabled");//07 00 45 6E 61 62 6C 65 64
	 * //Enabled STPC->writeBYTE(0x03); //byte 3 STPC->writeINT(0x01);//int 1 01
	 * 00 00 00 STPC->writeUSTRING("False");//05 00 00 00 46 00 61 00 6C 00 73
	 * 00 65 00 //F.a.l.s.e. STPC->writeINT(0x02);//02 00 00 00
	 * STPC->writeASTRING("btnCancel");//09 00 62 74 6E 43 61 6E 63 65 6C
	 * //btnCancel STPC->writeASTRING("Visible");//07 00 56 69 73 69 62 6C 65
	 * //Visible STPC->writeBYTE(0x03); //03 STPC->writeINT(0x01);//int 1 01 00
	 * 00 00 STPC->writeUSTRING("False");//05 00 00 00 46 00 61 00 6C 00 73 00
	 * 65 00 //F.a.l.s.e. STPC->writeINT(0x02);// 02 00 00 00
	 * STPC->writeASTRING("btnRevert");//09 00 62 74 6E 52 65 76 65 72 74
	 * //btnRevert STPC->writeASTRING("Enabled");//07 00 45 6E 61 62 6C 65 64
	 * //Enabled STPC->writeBYTE(0x03);//03 STPC->writeINT(0x01);//int 1 01 00
	 * 00 00 STPC->writeUSTRING("False");//05 00 00 00 46 00 61 00 6C 00 73 00
	 * 65 00 //F.a.l.s.e STPC->writeINT(0x02); //02 00 00 00
	 * STPC->writeASTRING("btnRevert");//09 00 62 74 6E 52 65 76 65 72 74
	 * //btnRevert STPC->writeASTRING("Visible");//07 00 56 69 73 69 62 6C 65
	 * //Visible STPC->writeLONG(0x00); // 00 00 00 00 00 00 00 00 //// Object
	 * ID of the item generating the dialog box (if any). STPC->writeINT(0x0);
	 * //00 00 00 00 //spacer STPC->writeLONG(0x00); //00 00 00 00 00 00 00 00
	 * //players id return STPC; } ByteBuffer* ZClient::BUILD_SUI_MessageBox(){
	 * printf("Building BUILD_SUI_MessageBox.\n"); ByteBuffer *STPC = new
	 * ByteBuffer();
	 * 
	 * /* 00 09 04 7D 02 00 //WORLD_UPDATE 59 72 4B D4 //SuiCreatePageMessage =
	 * 0xD44B7259, 4C 8E 04 00 //window ID 11 00 //astring length 53 63 72 69 70
	 * 74 2E 6D 65 73 73 61 67 65 42 6F 78 // Script.messageBox //astring 08 00
	 * 00 00 //int num of updates 05 // byte 5 // 00 00 00 00 //int 0 03 00 00
	 * 00 //list size 00 00 //short 0 01 //byte 1 00 //byte 0 09 //byte 9 09 00
	 * //astring length 68 61 6E 64 6C 65 53 55 49 //handleSUI //astring 05
	 * //byte 5 00 00 00 00 //int 0 03 00 00 00 //int 3 00 00 //short 0 01
	 * //byte 1 00 //byte 0 0A //byte 0A 09 00 //astring length 68 61 6E 64 6C
	 * 65 53 55 49 //handleSUI //astring 03 01 00 00 00 //item in list 20 00 00
	 * 00 //ustring length 40 00 74 00 72 00 61 00 76 00 65 00 6C 00 3A 00
	 * @.t.r.a.v.e.l.:. 74 00 69 00 63 00 6B 00 65 00 74 00 5F 00 70 00
	 * t.i.c.k.e.t._.p. 75 00 72 00 63 00 68 00 61 00 73 00 65 00 5F 00
	 * u.r.c.h.a.s.e._. 63 00 6F 00 6D 00 70 00 6C 00 65 00 74 00 65 00
	 * c.p.m.p.l.e.t.e.
	 * 
	 * 02 00 00 00 //int 2 10 00 //astring length 50 72 6F 6D 70 74 2E 6C 62 6C
	 * 50 72 6F 6D 70 74 //Prompt.lblPrompt // astring
	 * 
	 * 04 00 //astring length 54 65 78 74 //text astring 03 //byte 3 01 //byte 1
	 * 00 00 //short 0 00 //byte 0 10 00 00 00 //ustring length 40 00 62 00 61
	 * 00 73 00 65 00 5F 00 70 00 6C 00 61 00 79 00 65 00 72 00 3A 00 73 00 77
	 * 00 67 00 //@.b.a.s.e._.p.l.a.y.e.r.:.s.w.g.
	 * 
	 * 02 00 00 00 //item in list 13 00 62 67 2E 63 61 70 74 69 6F 6E 2E 6C 62
	 * 6C 54 69 74 6C 65 //bg.caption.lblTitle.. 04 00 //astring length 54 65 78
	 * 74 //Text//astring 03 01 00 00 00 05 00 00 00 46 00 61 00 6C 00 73 00 65
	 * 00 //F.a.l.s.e. 02 00 00 00 09 00 62 74 6E 43 61 6E 63 65 6C //btnCancel
	 * 07 00 45 6E 61 62 6C 65 64 //Enabled 03 01 00 00 00 05 00 00 00 46 00 61
	 * 00 6C 00 73 00 65 00 //F.a.l.s.e. 02 00 00 00 09 00 62 74 6E 43 61 6E 63
	 * 65 6C //btnCancel 07 00 56 69 73 69 62 6C 65 //Visible 03 01 00 00 00 05
	 * 00 00 00 46 00 61 00 6C 00 73 00 65 00 //F.a.l.s.e. 02 00 00 00 09 00 62
	 * 74 6E 52 65 76 65 72 74 //btnRevert 07 00 45 6E 61 62 6C 65 64 //Enabled
	 * 03 01 00 00 00 05 00 00 00 46 00 61 00 6C 00 73 00 65 00 //F.a.l.s.e 02
	 * 00 00 00 09 00 62 74 6E 52 65 76 65 72 74 //btnRevert 07 00 56 69 73 69
	 * 62 6C 65 //Visible 00 00 00 00 00 00 00 00 //// Object ID of the item
	 * generating the dialog box (if any). 00 00 00 00 //spacer 00 00 00 00 00
	 * 00 00 00 //players id 37 21 //crc
	 * 
	 * / STPC->uType(WORLD_UPDATE); STPC->opcode2(SuiCreatePageMessage);
	 * STPC->writeINT(lastTicketWindow); // Window ID. This can be anything,
	 * according to Meanmon. STPC->writeASTRING("Script.messageBox"); // This is
	 * the type of script we're running. listBox, messageBox, etc. etc.
	 * STPC->writeINT(0x08);//int num of updates STPC->writeBYTE(0x05); // byte
	 * 5 // STPC->writeINT(00); //int 0 STPC->writeINT(0x03); //list size
	 * STPC->writeSHORT(0x00); //short 0 STPC->writeBYTE(0x01); //byte 1
	 * STPC->writeBYTE(0x00); //byte 0 STPC->writeBYTE(0x09); //byte 9
	 * STPC->writeASTRING("handleSUI");//09 00 //astring length 68 61 6E 64 6C
	 * 65 53 55 49 //handleSUI //astring STPC->writeBYTE(0x05); //byte 5
	 * STPC->writeINT(0x00); //int 0 STPC->writeINT(0x03); //int 3
	 * STPC->writeSHORT(0x00); //short 0 STPC->writeBYTE(0x01); //byte 1
	 * STPC->writeBYTE(0x00); //byte 0 STPC->writeBYTE(0x0A); //byte 0A
	 * STPC->writeASTRING("handleSUI");//09 00 //astring length 68 61 6E 64 6C
	 * 65 53 55 49 //handleSUI //astring STPC->writeBYTE(0x03);
	 * STPC->writeINT(0x01); //item in list //20 00 00 00 //ustring length //40
	 * 00 74 00 72 00 61 00 76 00 65 00 6C 00 3A 00 @.t.r.a.v.e.l.:. //74 00 69
	 * 00 63 00 6B 00 65 00 74 00 5F 00 70 00 t.i.c.k.e.t._.p. //75 00 72 00 63
	 * 00 68 00 61 00 73 00 65 00 5F 00 u.r.c.h.a.s.e._. //63 00 6F 00 6D 00 70
	 * 00 6C 00 65 00 74 00 65 00 c.p.m.p.l.e.t.e.
	 * STPC->writeUSTRING("@travel:ticket_purchase_complete");
	 * STPC->writeINT(0x02); //int 2 STPC->writeASTRING("Prompt.lblPrompt");//10
	 * 00 //astring length 50 72 6F 6D 70 74 2E 6C 62 6C 50 72 6F 6D 70 74
	 * //Prompt.lblPrompt // astring STPC->writeASTRING("Text"); // 04 00
	 * //astring length 54 65 78 74 //text astring STPC->writeBYTE(0x03); //byte
	 * 3 STPC->writeBYTE(0x01); //byte 1 STPC->writeSHORT(0x00); //short 0
	 * STPC->writeBYTE(0x00);//byte 0 //10 00 00 00 //ustring length 40 00 62 00
	 * 61 00 73 00 65 00 5F 00 70 00 6C 00 61 00 79 00 65 00 72 00 3A 00 73 00
	 * 77 00 67 00 //@.b.a.s.e._.p.l.a.y.e.r.:.s.w.g.
	 * STPC->writeUSTRING("@base_player:swg"); STPC->writeINT(0x02);//item in
	 * list STPC->writeASTRING("bg.caption.lblTitle"); //13 00 62 67 2E 63 61 70
	 * 74 69 6F 6E 2E 6C 62 6C 54 69 74 6C 65 //bg.caption.lblTitle..
	 * STPC->writeASTRING("Text"); // 04 00 //astring length 54 65 78 74 //text
	 * astring STPC->writeBYTE(0x03); //byte 3 STPC->writeINT(0x01);//int 1
	 * STPC->writeUSTRING("False");//05 00 00 00 46 00 61 00 6C 00 73 00 65 00
	 * //F.a.l.s.e. STPC->writeINT(0x02);//int 2
	 * STPC->writeASTRING("btnCancel");// 09 00 62 74 6E 43 61 6E 63 65 6C
	 * //btnCancel STPC->writeASTRING("Enabled");//07 00 45 6E 61 62 6C 65 64
	 * //Enabled STPC->writeBYTE(0x03); //byte 3 STPC->writeINT(0x01);//int 1 01
	 * 00 00 00 STPC->writeUSTRING("False");//05 00 00 00 46 00 61 00 6C 00 73
	 * 00 65 00 //F.a.l.s.e. STPC->writeINT(0x02);//02 00 00 00
	 * STPC->writeASTRING("btnCancel");//09 00 62 74 6E 43 61 6E 63 65 6C
	 * //btnCancel STPC->writeASTRING("Visible");//07 00 56 69 73 69 62 6C 65
	 * //Visible STPC->writeBYTE(0x03); //03 STPC->writeINT(0x01);//int 1 01 00
	 * 00 00 STPC->writeUSTRING("False");//05 00 00 00 46 00 61 00 6C 00 73 00
	 * 65 00 //F.a.l.s.e. STPC->writeINT(0x02);// 02 00 00 00
	 * STPC->writeASTRING("btnRevert");//09 00 62 74 6E 52 65 76 65 72 74
	 * //btnRevert STPC->writeASTRING("Enabled");//07 00 45 6E 61 62 6C 65 64
	 * //Enabled STPC->writeBYTE(0x03);//03 STPC->writeINT(0x01);//int 1 01 00
	 * 00 00 STPC->writeUSTRING("False");//05 00 00 00 46 00 61 00 6C 00 73 00
	 * 65 00 //F.a.l.s.e STPC->writeINT(0x02); //02 00 00 00
	 * STPC->writeASTRING("btnRevert");//09 00 62 74 6E 52 65 76 65 72 74
	 * //btnRevert STPC->writeASTRING("Visible");//07 00 56 69 73 69 62 6C 65
	 * //Visible STPC->writeLONG(0x00); // 00 00 00 00 00 00 00 00 //// Object
	 * ID of the item generating the dialog box (if any). STPC->writeINT(0x0);
	 * //00 00 00 00 //spacer STPC->writeLONG(0x00); //00 00 00 00 00 00 00 00
	 * //players id return STPC; }
	 * 
	 * // Converted, but don't have enough data on what it needs.
	 */
	protected static byte[] buildBadgeResponseMessage(long targetID,
			int[] badgeBits) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.ACCOUNT_UPDATE);
		dOut.writeInt(Constants.badgesResponseMessage);
		dOut.writeLong(targetID);
		dOut.writeInt(badgeBits.length);
		for (int i = 0; i < badgeBits.length; i++) {
			dOut.writeInt(badgeBits[i]);
		}
		dOut.flush();
		return dOut.getBuffer();

	}

	protected static byte[] buildBiographyResponse(long recipientID,
			long targetID, String sBiography) throws IOException {

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.BiographyUpdate);
		dOut.writeLong(recipientID);
		dOut.writeInt(0);
		dOut.writeLong(targetID);
		dOut.writeUTF16(sBiography);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildCharacterSheetResponseMessage(Player player)
			throws IOException {
		ZoneServer server = player.getClient().getServer();
		float[] fStartingCoordinates = player.getStartingCoordinates();
		String sStartingLocation = player.getBirthplace();
		int iBankPlanetID = player.getBankPlanetID();
		float[] fBankCoordinates = player.getBankCoordinates();
		float[] fHouseCoordinates = player.getHouseCoordinates();
		int iHousePlanetID = player.getHousePlanetID();
		Player spouse = server.getPlayer(player.getPlayData()
				.getMarriedPlayerID());
		String sMarriedPlayerName = null;
		if (spouse != null) {
			sMarriedPlayerName = spouse.getFullName();
		}

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.UPDATE_CHAR_CREATE);
		dOut.writeInt(Constants.characterSheetResponseMessage);
		dOut.writeLong(0);
		for (int i = 0; i < fStartingCoordinates.length; i++) {
			dOut.writeFloat(fStartingCoordinates[i]);
		}
		dOut.writeUTF(sStartingLocation);
		if (iBankPlanetID != -1) {
			for (int i = 0; i < fBankCoordinates.length; i++) {
				dOut.writeFloat(fBankCoordinates[i]);
			}
			dOut.writeUTF(Constants.PlanetNames[iBankPlanetID]);
		} else {
			dOut.writeDouble(0);
			dOut.writeFloat(0);
			dOut.writeUTF("");
		}

		if (iHousePlanetID != -1) {
			for (int i = 0; i < fHouseCoordinates.length; i++) {
				dOut.writeFloat(fHouseCoordinates[i]);
			}
			dOut.writeUTF(Constants.PlanetNames[iHousePlanetID]);
		} else {
			dOut.writeDouble(0);
			dOut.writeFloat(0);
			dOut.writeUTF("");
		}
		if (sMarriedPlayerName != null) {
			dOut.writeUTF16(sMarriedPlayerName);
		} else {
			dOut.writeUTF16("");
		}
		dOut.writeInt(player.getFreeLots());
		dOut.writeInt(player.getFactionCRC());
		dOut.writeInt(player.getTotalLots());
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildStatMigrationDataResponse(Player player)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.UPDATE_NINE);
		dOut.writeInt(Constants.statMigrationResponseMessage);
		int[] iHamTargets = player.getHamMigrationTargets();
		int iMigrationPointsLeft = player.getHamMigrationPointsAvailable();
		for (int i = 0; i < iHamTargets.length; i++) {
			dOut.writeInt(iHamTargets[i]);
		}
		dOut.writeInt(iMigrationPointsLeft);
		dOut.flush();
		return dOut.getBuffer();

	}

	protected static byte[] buildFactionResponseMessage(Player p)
			throws IOException {
		// if (true) return null;
		Vector<PlayerFactions> vFactionList = p.getFactionList();
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.UPDATE_SEVEN);
		dOut.writeInt(Constants.FactionResponseMessage);
		byte factionID = p.getFactionRank();
		if (factionID == -1) {
			dOut.writeUTF("civilian");
		} else {
			dOut.writeUTF(Constants.FactionRanks[factionID]);
		}
		dOut.writeInt(p.getRebelFactionPoints());
		dOut.writeInt(p.getImperialFactionPoints());
		dOut.writeInt(0); // Hutt list.
		if (vFactionList.isEmpty()) {
			dOut.writeLong(0);
		} else {
			for (int i = 0; i < vFactionList.size(); i++) {
				dOut.writeInt(i + 1);
				dOut.writeUTF(vFactionList.elementAt(i).getFactionName());
			}
			for (int i = 0; i < vFactionList.size(); i++) {
				dOut.writeInt(i + 1);
				dOut.writeFloat(vFactionList.elementAt(i).getFactionValue());
			}
		}
		dOut.flush();
		// byte[] thePacket = dOut.getBuffer();
		// PacketUtils.printPacketToScreen(thePacket, thePacket.length, null);
		return dOut.getBuffer();

	}

	/*
	 * protected static byte[] buildFlytextMessageTicketPurchase(Player p, int
	 * PurchasePrice) throws IOException{
	 * 
	 * SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
	 * dOut.setOpcode(Constants.SOE_CHL_DATA_A); dOut.setSequence(0);
	 * dOut.setUpdateType(Constants.SERVER_UPDATE);
	 * dOut.writeInt(Constants.ChatSystemMessage); dOut.writeByte(0);
	 * dOut.writeInt(0); dOut.writeInt(71);//113//stf size dOut.writeShort(1);
	 * //2 dOut.writeByte(1);//1 dOut.writeInt(0xFFFFFFFF);//4
	 * dOut.writeUTF("base_player"); //13 62 61 73 65 5F 70 6C 61 79 65 72 //
	 * base_player dOut.writeInt(0); //4
	 * dOut.writeUTF("prose_pay_acct_success");//24 //70 72 6F 73 65 5F 70 61 79
	 * 5F 61 63 63 74 5F 73 75 63 63 65 73 73 // "prose_pay_acct_success"
	 * dOut.writeLong(0);//8 dOut.writeLong(0);//8 dOut.writeLong(0);//8
	 * dOut.writeLong(0);//8 dOut.writeLong(0);//8 dOut.writeLong(0);//8
	 * dOut.writeUTF("money/acct_n"); //14 //6D 6F 6E 65 79 2F 61 63 63 74 5F 6E
	 * // "money/acct_n" dOut.writeInt(0);//4 dOut.writeUTF("travelsystem");//14
	 * //74 72 61 76 65 6C 73 79 73 74 65 6D // "travelsystem" dOut.writeInt(0);
	 * //4 dOut.writeInt(PurchasePrice);//4 //BC 02 00 00 //ticket price int
	 * dOut.writeInt(0); //4 dOut.writeShort(0);//2 dOut.flush(); return
	 * dOut.getBuffer();
	 * 
	 * 
	 * }
	 */

	protected static byte[] buildSUITicketPurchase(Player p) throws IOException {

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.WORLD_UPDATE);
		dOut.writeInt(Constants.SuiCreatePageMessage);
		dOut.writeInt(p.getLastSUIBox()); // Window ID. Each player has a sui
											// window counter

		p.setLastSuiWindowTypeInt(Constants.SUI_WINDOW_TYPE_ScriptmessageBox);
		p.setLastSuiWindowTypeString("@travel:ticket_purchase_complete");

		dOut.writeUTF("Script.messageBox"); // This is the type of script we're
											// running. listBox, messageBox,
											// etc. etc.
		dOut.writeInt(0x08);// int num of updates
		// update 1
		dOut.writeByte(0x05); // byte 5 //
		dOut.writeInt(00); // int 0
		dOut.writeInt(0x03); // list size
		dOut.writeShort(0x00); // short 0
		dOut.writeByte(0x01); // byte 1
		dOut.writeByte(0x00); // byte 0
		dOut.writeByte(0x09); // byte 9
		dOut.writeUTF("handleSUI");// 09 00 //astring length 68 61 6E 64 6C 65
									// 53 55 49 //handleSUI //astring
		dOut.writeByte(0x05); // byte 5
		dOut.writeInt(0x00); // int 0
		dOut.writeInt(0x03); // int 3
		dOut.writeShort(0x00); // short 0
		dOut.writeByte(0x01); // byte 1
		dOut.writeByte(0x00); // byte 0
		dOut.writeByte(0x0A); // byte 0A
		dOut.writeUTF("handleSUI");// 09 00 //astring length 68 61 6E 64 6C 65
									// 53 55 49 //handleSUI //astring
		// update 2
		dOut.writeByte(0x03);
		dOut.writeInt(0x01); // item in list //20 00 00 00 //ustring length //40
								// 00 74 00 72 00 61 00 76 00 65 00 6C 00 3A 00
								// @.t.r.a.v.e.l.:. //74 00 69 00 63 00 6B 00 65
								// 00 74 00 5F 00 70 00 t.i.c.k.e.t._.p. //75 00
								// 72 00 63 00 68 00 61 00 73 00 65 00 5F 00
								// u.r.c.h.a.s.e._. //63 00 6F 00 6D 00 70 00 6C
								// 00 65 00 74 00 65 00 c.p.m.p.l.e.t.e.
		dOut.writeUTF16("@travel:ticket_purchase_complete");
		dOut.writeInt(0x02); // int 2
		dOut.writeUTF("Prompt.lblPrompt");// 10 00 //astring length 50 72 6F 6D
											// 70 74 2E 6C 62 6C 50 72 6F 6D 70
											// 74 //Prompt.lblPrompt // astring
		dOut.writeUTF("Text"); // 04 00 //astring length 54 65 78 74 //text
								// astring
		// update 3
		dOut.writeByte(0x03); // byte 3
		dOut.writeByte(0x01); // byte 1
		dOut.writeShort(0x00); // short 0
		dOut.writeByte(0x00);// byte 0 //10 00 00 00 //ustring length 40 00 62
								// 00 61 00 73 00 65 00 5F 00 70 00 6C 00 61 00
								// 79 00 65 00 72 00 3A 00 73 00 77 00 67 00
								// //@.b.a.s.e._.p.l.a.y.e.r.:.s.w.g.
		dOut.writeUTF16("@base_player:swg");
		dOut.writeInt(0x02);// item in list
		dOut.writeUTF("bg.caption.lblTitle"); // 13 00 62 67 2E 63 61 70 74 69
												// 6F 6E 2E 6C 62 6C 54 69 74 6C
												// 65 //bg.caption.lblTitle..
		dOut.writeUTF("Text"); // 04 00 //astring length 54 65 78 74 //text
								// astring
		// update 4
		dOut.writeByte(0x03); // byte 3
		dOut.writeInt(0x01);// int 1
		dOut.writeUTF16("False");// 05 00 00 00 46 00 61 00 6C 00 73 00 65 00
									// //F.a.l.s.e.
		dOut.writeInt(0x02);// int 2
		dOut.writeUTF("btnCancel");// 09 00 62 74 6E 43 61 6E 63 65 6C
									// //btnCancel
		dOut.writeUTF("Enabled");// 07 00 45 6E 61 62 6C 65 64 //Enabled
		// update 5
		dOut.writeByte(0x03); // byte 3
		dOut.writeInt(0x01);// int 1 01 00 00 00
		dOut.writeUTF16("False");// 05 00 00 00 46 00 61 00 6C 00 73 00 65 00
									// //F.a.l.s.e.
		dOut.writeInt(0x02);// 02 00 00 00
		dOut.writeUTF("btnCancel");// 09 00 62 74 6E 43 61 6E 63 65 6C
									// //btnCancel
		dOut.writeUTF("Visible");// 07 00 56 69 73 69 62 6C 65 //Visible
		// update 6
		dOut.writeByte(0x03); // 03
		dOut.writeInt(0x01);// int 1 01 00 00 00
		dOut.writeUTF16("False");// 05 00 00 00 46 00 61 00 6C 00 73 00 65 00
									// //F.a.l.s.e.
		dOut.writeInt(0x02);// 02 00 00 00
		dOut.writeUTF("btnRevert");// 09 00 62 74 6E 52 65 76 65 72 74
									// //btnRevert
		dOut.writeUTF("Enabled");// 07 00 45 6E 61 62 6C 65 64 //Enabled
		// update 7
		dOut.writeByte(0x03);// 03
		dOut.writeInt(0x01);// int 1 01 00 00 00
		dOut.writeUTF16("False");// 05 00 00 00 46 00 61 00 6C 00 73 00 65 00
									// //F.a.l.s.e
		dOut.writeInt(0x02); // 02 00 00 00
		dOut.writeUTF("btnRevert");// 09 00 62 74 6E 52 65 76 65 72 74
									// //btnRevert
		dOut.writeUTF("Visible");// 07 00 56 69 73 69 62 6C 65 //Visible
		// update 8
		dOut.writeLong(0x00); // 00 00 00 00 00 00 00 00 //// Object ID of the
								// item generating the dialog box (if any).
		dOut.writeInt(0x0); // 00 00 00 00 //spacer
		dOut.writeLong(0x00); // 00 00 00 00 00 00 00 00 //players id

		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildSUIUseTravelTicketList(Player p,
			Vector<TravelTicket> ticketList) throws IOException {

		/*
		 * @travel/travel: 1 no_shuttle_for_location There is no shuttle nearby
		 * for that ticket's departure location. 2 no_shuttle_nearby There is no
		 * shuttle nearby. 3 not_in_combat You cannot travel while in combat. 4
		 * on_pet_or_vehicle You cannot board the shuttle when you are riding on
		 * a pet or in a vehicle. 5 ready_to_board The next shuttle is ready to
		 * board. 6 shuttle_begin_boarding The next shuttle is about to begin
		 * boarding. 7 shuttle_board_delay The next shuttle will be ready to
		 * board in %DI seconds. 8 sui_select_destination_header Select
		 * Destination 9 sui_select_destination_loc %TT -- %TO 10
		 * sui_select_destination_title Select Destination 11
		 * sui_ticket_arrival_point Arrival: %TT -- %TO 12
		 * sui_ticket_departure_point Departure: %TT -- %TO 13
		 * sui_ticket_information Ticket Information 14 ticket_invalid This
		 * ticket is invalid. 15 ticket_purchase_complete Ticket purchase
		 * complete.
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.WORLD_UPDATE);
		dOut.writeInt(Constants.SuiCreatePageMessage);

		p.clearSUIListWindowObjectList();// this is to prevent overlapping
											// windows

		dOut.writeInt(p.getLastSUIBox()); // Window ID. Each Player has a window
											// counter
		int NumUpdates = 7;
		int TicketCount = ticketList.size();
		NumUpdates += (TicketCount * 2);
		// NumUpdates = 0x11;
		// -------------------------------
		p.setLastSuiWindowTypeString("Script.listBox");
		p.setLastSuiWindowTypeInt(Constants.SUI_WINDOW_TYPE_ScriptlistBox);

		dOut.writeUTF("Script.listBox"); // 0E 00 53 63 72 69 70 74 2E 6C 69 73
											// 74 42 6F 78 //Script.listBox
		dOut.writeInt(NumUpdates);// 11 00 00 00 DEC17 - number of updates
									// Constant 7 + 2 Per Ticket Count

		// ----------------------------------
		// update 1
		dOut.writeByte(5);// 05
		dOut.writeInt(0);// 00 00 00 00
		dOut.writeInt(7);// 07 00 00 00
		// -----------------

		dOut.writeShort(0);// 00 00
		dOut.writeByte(1);// 01
		dOut.writeByte(0);// 00
		dOut.writeByte(9);// 09
		// -----------------

		dOut.writeUTF("msgBoardShuttle");// 0B 00 6D 73 67 53 65 6C 65 63 74 65
											// 64 //msgSelected
		dOut.writeUTF("List.lstList");// 0C 00 4C 69 73 74 2E 6C 73 74 4C 69 73
										// 74 //List.lstList
		dOut.writeUTF("SelectedRow");// 0B 00 53 65 6C 65 63 74 65 64 52 6F 77
										// //SelectedRow
		dOut.writeUTF("bg.caption.lblTitle");// 13 00 62 67 2E 63 61 70 74 69 6F
												// 6E 2E 6C 62 6C 54 69 74 6C 65
												// //bg.caption.lblTitle
		dOut.writeUTF("Text");// 04 00 54 65 78 74 //Text
		// ----------------------------
		// update 2
		dOut.writeByte(5);// 05
		dOut.writeInt(0);// 00 00 00 00
		dOut.writeInt(7);// 07 00 00 00
		// -----------------------------

		dOut.writeShort(0);// 00 00
		dOut.writeByte(1);// 01
		dOut.writeByte(0);// 00
		dOut.writeByte(0x0A);// 0A = 10
		// --------------------------------

		dOut.writeUTF("msgBoardShuttle");// 0B 00 6D 73 67 53 65 6C 65 63 74 65
											// 64 //msgSelected
		dOut.writeUTF("List.lstList");// 0C 00 4C 69 73 74 2E 6C 73 74 4C 69 73
										// 74 //List.lstList
		dOut.writeUTF("SelectedRow");// 0B 00 53 65 6C 65 63 74 65 64 52 6F 77
										// //SelectedRow
		dOut.writeUTF("bg.caption.lblTitle");// 13 00 62 67 2E 63 61 70 74 69 6F
												// 6E 2E 6C 62 6C 54 69 74 6C 65
												// //bg.caption.lblTitle
		dOut.writeUTF("Text");// 04 00 54 65 78 74 //Text
		// --------------------------------
		// update 3
		dOut.writeByte(3);// 03
		dOut.writeInt(1);// 01 00 00 00
		dOut.writeUTF16("@travel:ticket_collector_name");// 1D 00 00 00 40 00 74
															// 00 72 00 61 00 76
															// 00 65 00 6C 00 3A
															// 00 74 00 69 00 63
															// 00 6B 00 65 00 74
															// 00 5F 00 63 00 6F
															// 00 6C 00 6C 00 65
															// 00 63 00 74 00 6F
															// 00 72 00 5F 00 6E
															// 00 61 00 6D 00 65
															// 00
															// //@.t.r.a.v.e.l.:.t.i.c.k.e.t._.c.p.l.l.e.c.t.p.r._.n.a.m.e
		dOut.writeInt(2);// 02 00 00 00
		dOut.writeUTF("bg.caption.lblTitle");// 13 00 62 67 2E 63 61 70 74 69 6F
												// 6E 2E 6C 62 6C 54 69 74 6C 65
												// bg.caption.lblTitle
		dOut.writeUTF("Text");// 04 00 54 65 78 74 //Text
		// -------------------------------

		dOut.writeByte(3);// 03
		dOut.writeInt(1);// 01 00 00 00
		dOut.writeUTF16("@travel:boarding_ticket_selection");// 21 00 00 00 40
																// 00 74 00 72
																// 00 61 00 76
																// 00 65 00 6C
																// 00 3A 00 62
																// 00 6F 00 61
																// 00 72 00 64
																// 00 69 00 6E
																// 00 67 00 5F
																// 00 74 00 69
																// 00 63 00 6B
																// 00 65 00 74
																// 00 5F 00 73
																// 00 65 00 6C
																// 00 65 00 63
																// 00 74 00 69
																// 00 6F 00 6E
																// 00
																// //@.t.r.a.v.e.l.:.b.p.a.r.d.i.n.g._.t.i.c.k.e.t._.s.e.l.e.c.t.i.p.n
		dOut.writeInt(2);// 02 00 00 00
		dOut.writeUTF("Prompt.lblPrompt");// 10 00 50 72 6F 6D 70 74 2E 6C 62 6C
											// 50 72 6F 6D 70 74
											// //Prompt.lblPrompt
		dOut.writeUTF("Text");// 04 00 54 65 78 74 //Text
		// -------------------------
		// Update 4 add a button to the window
		dOut.writeByte(3);// 03 //add button
		dOut.writeInt(1);// 01 00 00 00
		dOut.writeUTF16("@cancel");// 07 00 00 00 40 00 63 00 61 00 6E 00 63 00
									// 65 00 6C 00 //@.c.a.n.c.e.l
		dOut.writeInt(2);// 02 00 00 00
		dOut.writeUTF("btnCancel");// 09 00 62 74 6E 43 61 6E 63 65 6C
									// //btnCancel
		dOut.writeUTF("Text");// 04 00 54 65 78 74 //Text
		// -------------------------
		// Update 5 add a button
		dOut.writeByte(3);// 03
		dOut.writeInt(1);// 01 00 00 00
		dOut.writeUTF16("@ok");// 03 00 00 00 40 00 6F 00 6B 00 //@.p.k
		dOut.writeInt(2);// 02 00 00 00 ...........
		dOut.writeUTF("btnOk");// 05 00 62 74 6E 4F 6B //btnOk..
		dOut.writeUTF("Text");// 04 00 54 65 78 74 //Text...
		// ---------------------------------
		// Update 6 add a data list to the sui window
		dOut.writeByte(1);// 01 //add list
		dOut.writeInt(0);// 00 00 00 00
		dOut.writeInt(1);// 01 00 00 00
		dOut.writeUTF("List.dataList");// 0D 00 4C 69 73 74 2E 64 61 74 61 4C 69
										// 73 74 //List.dataList
		// here is where we add items

		// ---------------------------------
		// Update 7 add list item
		/*
		 * dOut.writeByte(0x04);//04 dOut.writeInt(1);//01 00 00 00
		 * dOut.writeUTF16("0");//01 00 00 00 30 00 //0 dOut.writeInt(2);//02 00
		 * 00 00 dOut.writeUTF("List.dataList");//0D 00 4C 69 73 74 2E 64 61 74
		 * 61 4C 69 73 74 //List.dataList.. dOut.writeUTF("Name");//04 00 4E 61
		 * 6D 65 //Name upt 8 dOut.writeByte(0x03);//03 dOut.writeInt(1);//01 00
		 * 00 00 dOut.writeUTF16("corellia - Vreni Island");//17 00 00 00 63 00
		 * 6F 00 72 00 65 00 6C 00 6C 00 69 00 61 00 20 00 2D 00 20 00 56 00 72
		 * 00 65 00 6E 00 69 00 20 00 49 00 73 00 6C 00 61 00 6E 00 64 00
		 * //corellia.-.Vreni.Island dOut.writeInt(2);//02 00 00 00
		 * dOut.writeUTF("List.dataList.0"); //0F 00 4C 69 73 74 2E 64 61 74 61
		 * 4C 69 73 74 2E 30 //List.dataList.0.. dOut.writeUTF("Text");//04 00
		 * 54 65 78 74 //Text
		 */

		for (int i = 0; i < TicketCount; i++) {
			// this is one update
			dOut.writeByte(0x04);// 04
			dOut.writeInt(1);// 01 00 00 00
			dOut.writeUTF16(Integer.toString(i));// 01 00 00 00 30 00 //0
			dOut.writeInt(2);// 02 00 00 00
			dOut.writeUTF("List.dataList");// 0D 00 4C 69 73 74 2E 64 61 74 61
											// 4C 69 73 74 //List.dataList..
			dOut.writeUTF("Name");// 04 00 4E 61 6D 65 //Name
			// this is a sencond update
			dOut.writeByte(0x03);// 03
			dOut.writeInt(1);// 01 00 00 00
			dOut.writeUTF16(Constants.PlanetNames[ticketList.get(i)
					.getArrivalInformation().getDestinationPlanet()]
					+ " - "
					+ ticketList.get(i).getArrivalInformation()
							.getDestinationName());// 17 00 00 00 63 00 6F 00 72
													// 00 65 00 6C 00 6C 00 69
													// 00 61 00 20 00 2D 00 20
													// 00 56 00 72 00 65 00 6E
													// 00 69 00 20 00 49 00 73
													// 00 6C 00 61 00 6E 00 64
													// 00
													// //corellia.-.Vreni.Island
			dOut.writeInt(2);// 02 00 00 00
			dOut.writeUTF("List.dataList." + Integer.toString(i)); // 0F 00 4C
																	// 69 73 74
																	// 2E 64 61
																	// 74 61 4C
																	// 69 73 74
																	// 2E 30
																	// //List.dataList.0..
			dOut.writeUTF("Text");// 04 00 54 65 78 74 //Text
			p.addSUIListWindowObjectList(i, ticketList.get(i));
		}

		// ----------------------------------
		// Update 9
		/*
		 * dOut.writeByte(0x04);//04 dOut.writeInt(1);//01 00 00 00
		 * dOut.writeUTF16("1");//01 00 00 00 31 00 //1 dOut.writeInt(2);//02 00
		 * 00 00 dOut.writeUTF("List.dataList");//0D 00 4C 69 73 74 2E 64 61 74
		 * 61 4C 69 73 74 //List.dataList dOut.writeUTF("Name");//04 00 4E 61 6D
		 * 65 //Name upt 10 dOut.writeByte(0x03);//03 dOut.writeInt(1);//01 00
		 * 00 00 dOut.writeUTF16("naboo - Lake Retreat Shuttleport");//20 00 00
		 * 00 6E 00 61 00 62 00 6F 00 6F 00 20 00 2D 00 20 00 4C 00 61 00 6B 00
		 * 65 00 20 00 52 00 65 00 74 00 72 00 65 00 61 00 74 00 20 00 53 00 68
		 * 00 75 00 74 00 74 00 6C 00 65 00 70 00 6F 00 72 00 74 00
		 * //n.a.b.p.p...-...L.a.k.e...R.e.t.r.e.a.t...S.h.u.t.t.l.e.p.p.r.t
		 * dOut.writeInt(2);//02 00 00 00 dOut.writeUTF("List.dataList.1");//0F
		 * 00 4C 69 73 74 2E 64 61 74 61 4C 69 73 74 2E 31 //List.dataList.1
		 * dOut.writeUTF("Text");//04 00 54 65 78 74 //Text
		 */
		// ----------------------------------
		// Update 11
		/*
		 * dOut.writeByte(0x04);//04 dOut.writeInt(1);//01 00 00 00
		 * dOut.writeUTF16("2");//01 00 00 00 32 00 //2 dOut.writeInt(2);//02 00
		 * 00 00 dOut.writeUTF("List.dataList");//0D 00 4C 69 73 74 2E 64 61 74
		 * 61 4C 69 73 74 //List.dataList dOut.writeUTF("Name");//04 00 4E 61 6D
		 * 65 //Name //upd 12 dOut.writeByte(0x03);//03 dOut.writeInt(1);//01 00
		 * 00 00 dOut.writeUTF16("naboo - Lake Retreat Shuttleport");//20 00 00
		 * 00 6E 00 61 00 62 00 6F 00 6F 00 20 00 2D 00 20 00 4C 00 61 00 6B 00
		 * 65 00 20 00 52 00 65 00 74 00 72 00 65 00 61 00 74 00 20 00 53 00 68
		 * 00 75 00 74 00 74 00 6C 00 65 00 70 00 6F 00 72 00 74 00
		 * //n.a.b.p.p...-...L.a.k.e...R.e.t.r.e.a.t...S.h.u.t.t.l.e.p.p.r.t
		 * dOut.writeInt(2);//02 00 00 00 dOut.writeUTF("List.dataList.2");//0F
		 * 00 4C 69 73 74 2E 64 61 74 61 4C 69 73 74 2E 32 //List.dataList.2
		 * dOut.writeUTF("Text");//04 00 54 65 78 74 //Text
		 */
		// ----------------------------------
		// Update 13
		/*
		 * dOut.writeByte(0x04);//04 dOut.writeInt(1);//01 00 00 00
		 * dOut.writeUTF16("3");//01 00 00 00 33 00 //3 dOut.writeInt(2);//02 00
		 * 00 00 dOut.writeUTF("List.dataList");//0D 00 4C 69 73 74 2E 64 61 74
		 * 61 4C 69 73 74 //List.dataList dOut.writeUTF("Name");//04 00 4E 61 6D
		 * 65 //Name //upd 14 dOut.writeByte(0x03);//03 dOut.writeInt(1);//01 00
		 * 00 00 dOut.writeUTF16("naboo - Moenia Shuttleport");//1A 00 00 00 6E
		 * 00 61 00 62 00 6F 00 6F 00 20 00 2D 00 20 00 4D 00 6F 00 65 00 6E 00
		 * 69 00 61 00 20 00 53 00 68 00 75 00 74 00 74 00 6C 00 65 00 70 00 6F
		 * 00 72 00 74 00
		 * //n.a.b.p.p...-...M.p.e.n.i.a...S.h.u.t.t.l.e.p..T.p.r.t.......
		 * dOut.writeInt(2);//02 00 00 00 dOut.writeUTF("List.dataList.3");//0F
		 * 00 4C 69 73 74 2E 64 61 74 61 4C 69 73 74 2E 33 //List.dataList.3.
		 * dOut.writeUTF("Text");//04 00 54 65 78 74 //Text
		 */
		// ---------------------------------
		// Update 15
		/*
		 * dOut.writeByte(0x04);//04 dOut.writeInt(1);//01 00 00 00
		 * dOut.writeUTF16("4");//01 00 00 00 34 00 //4 dOut.writeInt(2);//02 00
		 * 00 00 dOut.writeUTF("List.dataList");//0D 00 4C 69 73 74 2E 64 61 74
		 * 61 4C 69 73 74 //List.dataList dOut.writeUTF("Name");//04 00 4E 61 6D
		 * 65 //Name //UPD 16 dOut.writeByte(0x03);//03 dOut.writeInt(1);//01 00
		 * 00 00 dOut.writeUTF16("naboo - Deeja Peak Shuttleport");//1E 00 00 00
		 * 6E 00 61 00 62 00 6F 00 6F 00 20 00 2D 00 20 00 44 00 65 00 65 00 6A
		 * 00 61 00 20 00 50 00 65 00 61 00 6B 00 20 00 53 00 68 00 75 00 74 00
		 * 74 00 6C 00 65 00 70 00 6F 00 72 00 74 00
		 * //n.a.b.p.p...-...D.e.e.j.a...P.e.a.k...S.h.u.t.t.l.e.p.p.r.t.
		 * dOut.writeInt(2);//02 00 00 00 dOut.writeUTF("List.dataList.4");//0F
		 * 00 4C 69 73 74 2E 64 61 74 61 4C 69 73 74 2E 34 //List.dataList.4
		 * dOut.writeUTF("Text");//04 00 54 65 78 74 //Text
		 */
		// bhere is where the loop ends for adding items
		// ------------------------------
		// Update 17
		dOut.writeLong(0);// 00 00 00 00 00 00 00 00
		dOut.writeLong(0);// 00 00 00 00 00 00 00 00
		dOut.writeInt(0);// 00 00 00 00

		// ------------------------------
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildPlayerAnimation(Player N, String sAnimation)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0x1B);
		dOut.writeInt(Constants.MobileAnimation);
		dOut.writeLong(N.getID());
		dOut.writeInt(0);
		dOut.writeUTF(sAnimation);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildNPCAnimation(NPC N, String sAnimation)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0x1B);
		dOut.writeInt(Constants.MobileAnimation);
		dOut.writeLong(N.getID());
		dOut.writeInt(0);
		dOut.writeUTF(sAnimation);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildNPCSpeak(Player P, NPC N, Player observer,
			String sSpatialText, short Mood1, short Mood2) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.SpatialChatMessage);
		dOut.writeLong(P.getID());
		dOut.writeInt(0);
		dOut.writeLong(N.getID());
		if (observer != null) {
			dOut.writeLong(observer.getID());
		} else {
			dOut.writeLong(0);
		}
		dOut.writeUTF16(sSpatialText);
		dOut.writeShort(50);
		dOut.writeShort(Mood1);// mood id 1
		dOut.writeShort(Mood2);// mood id 2
		dOut.writeInt(256);// sub mood id 3
		dOut.writeLong(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildObjectSpeak(Player P, SOEObject N,
			Player observer, String sSpatialText, short Mood1, short Mood2)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.SpatialChatMessage);
		dOut.writeLong(P.getID());
		dOut.writeInt(0);
		dOut.writeLong(N.getID());
		if (observer != null) {
			dOut.writeLong(observer.getID());
		} else {
			dOut.writeLong(0);
		}
		dOut.writeUTF16(sSpatialText);
		dOut.writeShort(50);
		dOut.writeShort(Mood1);// mood id 1
		dOut.writeShort(Mood2);// mood id 2
		dOut.writeInt(256);// sub mood id 3
		dOut.writeLong(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	/*
	 * protected static byte[] buildFlyTextSTFMessage(Player p, String
	 * sSTFMessageFile, String sSTFMessageStringName, String sSTFArgumentFile,
	 * String sSTFArgumentStringName , int ArgumentInt ) throws IOException{
	 * SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
	 * dOut.setOpcode(Constants.SOE_CHL_DATA_A); dOut.setSequence(0);
	 * dOut.setUpdateType(Constants.SERVER_UPDATE);
	 * dOut.writeInt(Constants.ChatSystemMessage); dOut.writeByte(0);
	 * dOut.writeInt(0); dOut.writeInt(48 + sSTFMessageFile.length() +
	 * sSTFMessageStringName.length() + sSTFArgumentFile.length() +
	 * sSTFArgumentStringName.length());//113//stf size dOut.writeShort(1); //2
	 * dOut.writeByte(1);//1 dOut.writeInt(0xFFFFFFFF);//4
	 * dOut.writeUTF(sSTFMessageFile); //13 62 61 73 65 5F 70 6C 61 79 65 72 //
	 * base_player dOut.writeInt(0); //4
	 * dOut.writeUTF(sSTFMessageStringName);//24 //70 72 6F 73 65 5F 70 61 79 5F
	 * 61 63 63 74 5F 73 75 63 63 65 73 73 // "prose_pay_acct_success"
	 * dOut.write(new byte [48]); dOut.writeUTF(sSTFArgumentFile); //14 //6D 6F
	 * 6E 65 79 2F 61 63 63 74 5F 6E // "money/acct_n" dOut.writeInt(0);//4
	 * dOut.writeUTF(sSTFArgumentStringName);//14 //74 72 61 76 65 6C 73 79 73
	 * 74 65 6D // "travelsystem" dOut.writeInt(0); //4
	 * dOut.writeInt(ArgumentInt);//4 //BC 02 00 00 //ticket price int
	 * dOut.writeInt(0); //4 dOut.writeShort(0);//2 dOut.flush(); return
	 * dOut.getBuffer();
	 * 
	 * 
	 * }
	 */

	protected static byte[] buildPlayClientEffectObjectMessage(Player p,
			String sEffectSTFName) throws IOException {
		/**
		 * AS SEEN ON NGE 05 00 4A 43 55 88 21 00 63 6C 69 65 6E 74 65 66 66 65
		 * 63 74 2F 65 6C 65 76 61 74 6F 72 5F 64 65 73 63 65 6E 64 2E 63 65 66
		 * //clienteffect/elevator_descend.cef 00 00 A6 FF 7C 4D 09 00 00 00 00
		 * 00
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE); // operand this was set to
														// update 6, yet its
														// seen as obj update
		dOut.writeInt(Constants.PlayClientEffectObjectMessage); // opcode
		dOut.writeUTF(sEffectSTFName); // Effect file to play
		dOut.writeShort(0); // NULL bytes
		dOut.writeLong(p.getID()); // ID recieving effect.
		dOut.writeShort(0); // NULL bytes possibly not needed
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildPlaySoundFileMessage(long objectID,
			String sMusicSTFName, int iTimesToPlay, byte byteUnknown)
			throws IOException {
		/**
		 * 05 00 8A 0D 27 04 //04270D8A PlayMusicMessage = 0x04270D8A; 8A0D2704
		 * 04 27 0D 8A s o u n d / m u s i c _ a c q _ m i n e r . s n d 19 00
		 * 73 6F 75 6E 64 2F 6D 75 73 69 63 5F 61 63 71 5F 6D 69 6E 65 72 2E 73
		 * 6E 64 00 00 00 00 00 00 00 00 01 00 00 00 00
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.PlayMusicMessage);
		dOut.writeUTF(sMusicSTFName);
		dOut.writeLong(objectID);
		// dOut.writeLong(0); // Not an Object ID?
		dOut.writeInt(iTimesToPlay); // 0 = Indefinite. 0 is used for combat
										// type music that will loop on the
										// client side. Also for player
										// entertainer music.
		dOut.writeByte(byteUnknown);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildOpenChatterWindow(Player p,
			String sSoundSTFName, String sNPCSTFName, String sNPCSTFString,
			short iUnkShort, byte byteUnknown, int iNPCCRC,
			float chatterWindowCounter) throws IOException {

		/**
		 * 02 00 58 D2 4A 59 //594AD258 //OpenChatterWindow 74 C3 01 98 07 00 00
		 * 00 36 00 00 00 //54 bytes??? 00 00 01 // FF FF FF FF 0C 00 6E 70 65
		 * 5F 68 61 6E 67 61 72 5F 31 //npe_hangar_1..... 00 00 00 00 0C 00 64
		 * 72 6F 69 64 5F 78 70 5F 62 61 72 //droid_xp_bar 00 00 00 00 00 00 00
		 * 00 //1 00 00 00 00 00 00 00 00 //2 00 00 00 00 00 00 00 00 //3 00 00
		 * 00 00 00 00 00 00 //4 00 00 00 00 00 00 00 00 //5 00 00 00 00 00 00
		 * 00 00 //6 00 00 00 00 00 00 00 00 //7 00 00 00 00 00 00 00 00 //8 00
		 * 00 00 00 //9 00 //10 <---this could be a byte or a short depending if
		 * the packet is odd or even sized F0 29 94 F1 //crc for the npc
		 * appearing in the chatter window 14 00 73 6F 75 6E 64 2F 76 6F 5F 63
		 * 33 70 6F 5F 39 61 2E 73 6E 64 //sound/vo_c3po_9a.snd 00 00 20 41
		 * //looks like a float value, window location maybe?
		 * 
		 * 
		 * ////CRC FOR MON MOTHMA NPC : 0x2AC0AED8 // sNPCSTFString = mon_mothma
		 * , sNPCSTFName = theme_park_name
		 * 
		 */
		int iPacketSize = 6 + sSoundSTFName.length() + sNPCSTFName.length()
				+ sNPCSTFString.length();
		boolean odd = ((iPacketSize & 1) == 1);
		if (odd) {
			iPacketSize += 1;
		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.WORLD_UPDATE);
		dOut.writeInt(Constants.OpenChatterWindow);
		dOut.writeLong(p.getID());
		dOut.writeInt(iPacketSize);
		dOut.writeShort(iUnkShort);
		dOut.writeByte(byteUnknown);
		dOut.writeInt(0xFFFFFFFF);
		dOut.writeUTF(sNPCSTFName);
		dOut.writeInt(0);
		dOut.writeUTF(sNPCSTFString);
		dOut.write(new byte[68]);
		if (odd) {
			dOut.writeShort(0);
		} else {
			dOut.writeByte(0);
		}
		dOut.writeInt(iNPCCRC);
		dOut.writeUTF(sSoundSTFName);
		dOut.writeFloat(chatterWindowCounter); // for some reason this is a
												// sequential value for each
												// window. And yes it is a
												// float!!!!
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasPlayMusicMessage(Player p)
			throws IOException {
		/**
		 * This message apparently does nothing atm, the client does not crash
		 * but it does not update anything i can see. 05 00 53 21 86 12 F1 4B 1E
		 * C2 23 00 00 00 .-..S!...K..#... 59 41 4C 50 03 16 00 00 00 //22
		 * bytes? 01 00 10 00 0A 00 00 00 4C 00 00 00 00 00 00 00 00 00 00 00 00
		 * 08
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(p.getAccountID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_PLAY]);
		dOut.writeByte(3);
		dOut.writeInt(0x16);
		dOut.writeShort(1);
		dOut.writeShort(0x10);
		dOut.writeInt(0x0A);
		dOut.writeInt(0x4C);
		dOut.writeLong(0);
		dOut.writeReversedShort((short) 8);
		dOut.flush();
		return dOut.getBuffer();
	}

	/**
	 * 
	 * @param p
	 * @param mod
	 * @param deltaType
	 * @return
	 * @throws java.io.IOException
	 */

	protected static byte[] buildSkillModsDelta(Player p, SkillMods mod,
			byte deltaType) throws IOException {
		if (mod == null) {
			return null;
		}
		int packetSize = 23 + mod.getName().length();
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(p.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_CREO]);
		dOut.writeByte(4);
		dOut.writeInt(packetSize);
		dOut.writeShort(1); // 2
		dOut.writeShort(3); // 4 -- vID
		dOut.writeInt(1); // 8
		dOut.writeInt(p.getSkillModsUpdateCounter(true)); // 12
		dOut.writeByte(deltaType); // 13
		dOut.writeUTF(mod.getName()); // 15
		dOut.writeInt(mod.getSkillModModdedValue()); // 19
		dOut.writeInt(0); // 23
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildSkillModsDelta(Player p,
			Vector<SkillMods> mods, byte deltaType) throws IOException {
		if (mods == null || mods.isEmpty()) {
			return null;
		}
		int packetSize = 13;
		for (int i = 0; i < mods.size(); i++) {
			packetSize += 11 + mods.elementAt(i).getName().length();
		}

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(p.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_CREO]);
		dOut.writeByte(4);
		dOut.writeInt(packetSize);
		dOut.writeShort(1); // 2
		dOut.writeShort(3); // 4 -- vID

		dOut.writeInt(mods.size()); // 8
		int updateCounter = p.getSkillModsUpdateCounter(true);
		updateCounter += mods.size() - 1;
		p.setSkillModsUpdateCounter(updateCounter);
		dOut.writeInt(updateCounter); // 12
		for (int i = 0; i < mods.size(); i++) {
			SkillMods mod = mods.elementAt(i);
			dOut.writeByte(deltaType); // 13
			dOut.writeUTF(mod.getName()); // 15
			dOut.writeInt(mod.getSkillModModdedValue()); // 19
			dOut.writeInt(0); // 23
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildSkillsDelta(Player p, String skill,
			byte deltaType) throws IOException {
		if (skill == null) {
			return null;
		}
		int packetSize = 15 + skill.length();
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(p.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_CREO]);
		dOut.writeByte(1);
		dOut.writeInt(packetSize);
		dOut.writeShort(1);
		dOut.writeShort(Constants.DELTAS_CREO1_SKILLS_LIST);
		dOut.writeInt(1);
		dOut.writeInt(p.getSkillListUpdateCount(true));
		dOut.writeByte(deltaType);
		dOut.writeUTF(skill);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildCertificationsDelta(PlayerItem p,
			String certification, byte updateType) throws IOException {
		if (certification == null) {
			return null;
		}
		int packetSize = 17 + certification.length();
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(p.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_PLAY]);
		dOut.writeByte(9);
		dOut.writeInt(packetSize);
		dOut.writeShort(1);
		dOut.writeShort(Constants.DELTAS_PLAY9_CERTIFICATIONS_LIST);
		dOut.writeInt(1);
		dOut.writeInt(p.getCertificationsListUpdateCount(true));
		dOut.writeByte(updateType); // Is a 1 in the packet cap from Core 3?
		dOut.writeShort(0); // Hmm? Why are you here?
		dOut.writeUTF(certification);
		dOut.flush();
		return dOut.getBuffer();

	}

	protected static byte[] buildCertificationsDelta(PlayerItem p,
			Vector<String> certification, byte updateType) throws IOException {
		if (certification == null || certification.isEmpty()) {
			return null;
		}

		int packetSize = 12;
		for (int i = 0; i < certification.size(); i++) {
			packetSize += (5 + certification.elementAt(i).length());
		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(p.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_PLAY]);
		dOut.writeByte(9);
		dOut.writeInt(packetSize);
		dOut.writeShort(1);
		dOut.writeShort(Constants.DELTAS_PLAY9_CERTIFICATIONS_LIST);
		dOut.writeInt(certification.size());
		int updateCount = p.getCertificationsListUpdateCount(true)
				+ certification.size() - 1;
		p.setCertificationsListUpdateCount(updateCount);
		dOut.writeInt(updateCount);
		for (int i = 0; i < certification.size(); i++) {
			dOut.writeByte(updateType); // Is a 1 in the packet cap from Core 3?
			dOut.writeShort(0); // Hmm? Why are you here?
			dOut.writeUTF(certification.elementAt(i));
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildCertificationsDelta(PlayerItem p,
			Vector<String> certification) throws IOException {
		if (certification == null || certification.isEmpty()) {
			return null;
		}

		int packetSize = 15;
		for (int i = 0; i < certification.size(); i++) {
			packetSize += (2 + certification.elementAt(i).length());
		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(p.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_PLAY]);
		dOut.writeByte(9);
		dOut.writeInt(packetSize);
		dOut.writeShort(1);
		dOut.writeShort(Constants.DELTAS_PLAY9_CERTIFICATIONS_LIST);
		dOut.writeInt(certification.size());
		int updateCount = p.getCertificationsListUpdateCount(true)
				+ certification.size() - 1;
		p.setCertificationsListUpdateCount(updateCount);
		dOut.writeInt(updateCount);
		dOut.writeByte(3);
		dOut.writeShort(certification.size());
		for (int i = 0; i < certification.size(); i++) {
			dOut.writeUTF(certification.elementAt(i));
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDraftSchematicsDelta(PlayerItem p,
			Vector<CraftingSchematic> vSchematics, int updateType,
			boolean bResetList) throws IOException {
		if (vSchematics == null || (vSchematics.isEmpty() && !bResetList)) {
			return null;
		}
		int schematicUpdateCounter = p.getSchematicUpdateCount(true);
		int schematicListSize = vSchematics.size();
		int updateCount = schematicUpdateCounter + schematicListSize;
		int packetSize = 12 + (11 * vSchematics.size());
		if (bResetList) {
			packetSize += 3;
			schematicListSize += 1;
		} else {
			updateCount -= 1;
			p.setSchematicUpdateCount(updateCount);
		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(p.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_PLAY]);
		dOut.writeByte(9);
		dOut.writeInt(packetSize);
		dOut.writeShort(1); // Num Updates
		dOut.writeShort(Constants.DELTAS_PLAY9_SCHEMATICS_LIST);
		dOut.writeInt(schematicListSize); // + 1);
		dOut.writeInt(updateCount);
		if (bResetList) {
			dOut.writeByte(3);
			dOut.writeShort(0);
		}
		for (short i = 0; i < vSchematics.size(); i++) {
			CraftingSchematic schematic = vSchematics.get(i);
			dOut.writeByte(updateType);
			dOut.writeShort(schematic.getIndex()); // Array index?
			dOut.writeInt(schematic.getCRC()); // This is actually the object
												// ID, but the crc value can
												// double for that.
			dOut.writeInt(schematic.getCRC());
			// dOut.writeInt(1);
		}
		dOut.flush();
		byte[] buff = dOut.getBuffer();
		// PacketUtils.printPacketToScreen(buff,
		// "Draft Schematics List Delta Message");
		return buff;
	}

	protected static byte[] buildDraftSchematicsDelta(PlayerItem p,
			CraftingSchematic schematic, int updateType) throws IOException {
		if (schematic == null) {
			return null;
		}
		int packetSize = 15;// 23;

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(p.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_PLAY]);
		dOut.writeByte(9);
		dOut.writeInt(packetSize);
		dOut.writeShort(1); // Num Updates
		dOut.writeShort(Constants.DELTAS_PLAY9_SCHEMATICS_LIST);
		// dOut.writeInt(1);
		// int counter = p.getSchematicUpdateCount();
		// System.out.println("Draft Schematics Delta -- Single schematic.  Update count: "
		// + counter);
		// dOut.writeInt(counter);
		dOut.writeByte(updateType);
		dOut.writeShort(schematic.getIndex()); // Not sure on this.
		dOut.writeInt(schematic.getCRC());
		dOut.writeInt(schematic.getCRC());
		dOut.flush();
		byte[] buff = dOut.getBuffer();
		// PacketUtils.printPacketToScreen(buff,
		// "Draft Schematics Delta Message");
		return buff;
	}

	protected static byte[] buildAttackFlyText(SOEObject Source,
			SOEObject Recipient, String sAttackSTFFileName,
			String sAttackSTFStringName, int r, int g, int b)
			throws IOException {

		/**
		 * Color codes: My guess is that mixing the color codes will produce
		 * different color text. Red = 252; Green = 249; Blue = 6;
		 * sAttackSTFFileName is for the folling VVV STF File : "combat_effects"
		 * - sAttackSTFStringName for the following SAMPLE ATTACK NAMES
		 * "hit_head" "hit_body" "hit_rleg" "hit_lleg" "hit_rarm" "hit_larm"
		 * "miss"
		 * */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(Constants.ObjectActionEnqueue);
		dOut.writeInt(Constants.ShowFlyText);
		dOut.writeLong(Source.getID());
		dOut.writeInt(0); // spacer
		dOut.writeLong(Recipient.getID());
		dOut.writeUTF(sAttackSTFFileName);
		dOut.writeInt(0); // spacer
		dOut.writeUTF(sAttackSTFStringName);
		dOut.writeInt(0); // spacer
		dOut.writeByte(r);
		dOut.writeByte(g);
		dOut.writeByte(b);
		dOut.writeByte(5);// seems to be a constant need more info -- setting
							// this value to 10 adds the sAttackSTFStringName to
							// the combat chat tab.
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildCombatTextSpam(SOEObject Source,
			SOEObject Recipient, SOEObject Victim,
			String sCombatSpamSTFFileName, String sCombatSpamSTFStringName,
			int iDamageDealt) throws IOException {

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(Constants.ObjectActionEnqueue);
		dOut.writeInt(Constants.CombatTextSpam);
		dOut.writeLong(Recipient.getID());// who receives the message i/e
											// spectator
		dOut.writeInt(0); // spacer
		dOut.writeLong(Source.getID());// who originates the message i/e
										// combatant
		dOut.writeLong(Victim.getID()); // who the message is applied to i/e
										// victim
		dOut.writeInt(0); // spacer
		dOut.writeInt(0); // unknown???//some kind of counter?
		dOut.writeInt(iDamageDealt);
		dOut.writeUTF(sCombatSpamSTFFileName);
		dOut.writeInt(0); // spacer
		dOut.writeUTF(sCombatSpamSTFStringName);
		dOut.writeInt(0); // spacer
		dOut.flush();
		return dOut.getBuffer();

	}

	protected static byte[] buildMissionObjectDelta(MissionObject m,
			boolean debug) throws IOException {

		int iTotalLength = 2;
		short iNumUpdates = (short) m.getUpdatesList().size();

		for (int i = 0; i < m.getUpdatesList().size(); i++) {

			short iUpdateID = m.getUpdatesList().get(i);
			switch (iUpdateID) {
			case 0x0B:// 1
			{
				iTotalLength += 10;
				iTotalLength += m.getSMissionSTFString().length();
				iTotalLength += m.getSMissionSTFDetailIdentifier().length();
				break;
			}
			case 0x0E:// 2 mission type crc
			case 0x08:// 3
			case 0x05:// 4
			case 0x0A:// 5
			case 0x0D:// 6
			{
				iTotalLength += 6;
				break;
			}
			case 0x06:// 7
			case 0x09:// 8
			{
				iTotalLength += 26;
				break;
			}
			case 0x07:// 9
			{
				iTotalLength += 6;
				iTotalLength += (m.getSMissionGiverName().length() * 2);
				break;
			}
			case 0x0C:// 10
			{
				iTotalLength += 10;
				iTotalLength += m.getSMissionSTFString().length();
				iTotalLength += m.getSMissionSTFTextIdentifier().length();
				break;
			}
			case 0x0F:// 11
			{
				iTotalLength += 4;
				iTotalLength += m.getMissionTargetDisplayString().length();
				break;
			}
			}
		}
		if (iNumUpdates > 10) {
			iNumUpdates = 10;
		}
		// int missionType = m.getIMissionType();

		// -------------------------------------------------------------------------
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(m.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MISO]);
		dOut.writeByte(3);
		dOut.writeInt(iTotalLength);
		dOut.writeShort(iNumUpdates); // This is a variable that must be altered
										// if there is no destination waypoint.
		// ---------------------------------------------------------------------------
		if (m.getUpdatesList().contains((short) 0x0B))//
		{

			dOut.writeShort(0x0B);
			dOut.writeUTF(m.getSMissionSTFString());
			dOut.writeInt(0);
			dOut.writeUTF(m.getSMissionSTFDetailIdentifier());
		}
		// ---------------------------------------------------------------------
		if (m.getUpdatesList().contains((short) 0x05))//
		{

			dOut.writeShort(5);
			dOut.writeInt(m.getIDiffcultyLevel()); // Mission Level
		}
		// -------------------------------------------------------------------------
		if (m.getUpdatesList().contains((short) 0x06))//
		{

			dOut.writeShort(6); // origination wp begin set by the mission
								// object based on players location
			dOut.writeFloat(m.getPickupX()); // x
			dOut.writeFloat(m.getPickupZ()); // z sent as 0 because in all
												// reality height does not
												// matter here yet
			dOut.writeFloat(m.getPickupY()); // y
			dOut.writeFloat(0);// probably for space
			dOut.writeFloat(0);// probably for space
			dOut.writeInt(Constants.PlanetCRCForWaypoints[m
					.getIPickupPlanetID()]);
		}
		// -----------------------------------------------------------------------------
		if (m.getUpdatesList().contains((short) 0x07))//
		{
			dOut.writeShort(7);// mission originator name
			dOut.writeUTF16(m.getSMissionGiverName());
		}
		// ----------------------------------------------------------------------------
		if (m.getUpdatesList().contains((short) 0x0E))//
		{
			dOut.writeShort(0x0E);// Mission Type CRC
			dOut.writeInt(m.getIMissionTypeCRC());// Mission Type CRC
													// Constants.MISSION_TYPES_CRC_DELIVER
													// /
													// Constants.MISSION_TYPES_CRC_DEESTROY
		}
		// ------------------------------------------------------------------------
		// mission payout
		if (m.getUpdatesList().contains((short) 0x08))//
		{
			dOut.writeShort(8);// mission payout
			dOut.writeInt(m.getIMissionPayout());// payout amount
		}
		// ---------------------------------------------------------------------
		if (m.getUpdatesList().contains((short) 0x09))//
		{
			dOut.writeShort(9);// lair coords to be set by the mission object
			dOut.writeFloat(m.getMissionX()); // x
			dOut.writeFloat(m.getMissionZ()); // z sent as 0 because in all
												// reality height does not
												// matter here yet
			dOut.writeFloat(m.getMissionY()); // y
			dOut.writeFloat(0);// probably for space
			dOut.writeFloat(0);// probably for space
			dOut.writeInt(Constants.PlanetCRCForWaypoints[m
					.getMissionPlanetID()]);
		}
		// ------------------------------------------------------------------
		// display object
		if (m.getUpdatesList().contains((short) 0x0A))//
		{
			dOut.writeShort(0x0A);
			dOut.writeInt(m.getIDisplayObjectCRC());// display object CRC
													// 0xE191DBAB --
													// object/tangible/mission/shared_mission_datadisk.iff
													// -- display object crc
		}
		// ---------------------------------------------------------------------
		if (m.getUpdatesList().contains((short) 0x0C))//
		{
			dOut.writeShort(0x0C);
			dOut.writeUTF(m.getSMissionSTFString());
			dOut.writeInt(0);
			dOut.writeUTF(m.getSMissionSTFTextIdentifier());
		}
		// ---------------------------------------------------------------------
		if (m.getUpdatesList().contains((short) 0x0D))//
		{
			dOut.writeShort(0x0D);// ??update counter
			dOut.writeInt(m.getUpdateCounter(true));
		}
		// -------------------------------------------------------------------
		if (m.getUpdatesList().contains((short) 0x0F))//
		{
			dOut.writeShort(0x0F);
			dOut.writeUTF(m.getMissionTargetDisplayString());
		}
		// ----------------------------------------------------------------
		dOut.flush();
		byte[] buff = dOut.getBuffer();
		return buff;
	}

	protected static byte[] buildBaselineINSO3(Structure s) throws IOException {

		int iPacketSize = 66; // at 66 is base size
		// add string sizes
		iPacketSize += s.getSTFFileIdentifier().length();
		iPacketSize += s.getSTFFileName().length();
		iPacketSize += (s.getStructureName().length() * 2);

		short iUpdateCount = 16;

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(s.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_INSO]);
		dOut.writeByte(3);
		dOut.writeInt(iPacketSize); // 65 00 00 00 //byte count 101 // all bytes
									// counted below this
		dOut.writeShort(iUpdateCount);// 2 - 10 00 //upd Count
		dOut.writeFloat(1);// 4 - 00 00 80 3F //west facing 1 float 1 //operand 0
		
		// Operand 1 -- Structure STF data.
		dOut.writeUTF(s.getSTFFileName());// 2 - 10 00 70 6C 61 79 65 72 5F 73
											// 74 72 75 63 74 75 72 65 //
											// player_structure //16 - 2
											// //operand1
		dOut.writeInt(0);// 4 - 00 00 00 00 -
		dOut.writeUTF(s.getSTFFileIdentifier());// 2 - 13 00 74 65 6D 70 6F 72
												// 61 72 79 5F 73 74 72 75 63 74
												// 75 72 65
												// //temporary_structure //19 -
											// 3
		
		// Operand 2 -- Structure custom name.
		dOut.writeUTF16(s.getStructureName());// 4 - 00 00 00 00 - 4// its
												// possible some of these values
												// modify the terrain.//operand
												// 2
		dOut.writeInt(1);// int 1 // oper 3
		dOut.writeShort(0);// 4 - 00 00 00 00 - 4 -- Customization data
		
		dOut.writeInt(0);// 4 - 00 00 00 00 - 4 -- Whacky bitmask
		dOut.writeInt(0);// 4 - 00 00 00 00 - 5 -- Stack size should ALWAYS be 0 for a Structure
		dOut.writeLong(0); // Unknown value... maybe owner ID?
		//dOut.writeInt(0);// 4 - 00 00 00 00 - 6
		//dOut.writeInt(0);// 4 - 00 00 00 00 - 7
		dOut.writeInt(s.getPVPStatus());// 4 - 00 00 00 00 - 8
		dOut.writeInt(0);// 4 - 00 00 00 00 - 9
		dOut.writeInt(s.getConditionDamage());// 4 - 00 00 00 00 - 10
		dOut.writeInt(s.getMaxCondition());// 4 - 00 00 00 00 - 11
		dOut.writeByte(1);// 12
		dOut.writeByte(0);// 13
		dOut.writeFloat(s.getPowerPool());// 4 - 00 00 00 00 - 14 //power reserves -- almost.  Unquestionably related to power reserves... 
		dOut.writeFloat(s.getPowerRate());// 4 - 00 00 00 00 - 15 //power rate per hour 

		dOut.flush();
		return dOut.getBuffer();

	}

	protected static byte[] buildBaselineINSO6(Structure s) throws IOException {

		int iPacketSize = 23; // at 23 is base size
		short iUpdateCount = 4;

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(s.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_INSO]);
		dOut.writeByte(6);
		dOut.writeInt(iPacketSize);// 17 00 00 00 //byte count 23 //all bytes
									// counted below this
		dOut.writeShort(iUpdateCount);// 04 00 //upd size
		dOut.writeInt(0x7D);// 7D 00 00 00 //125???? 125 what?
		dOut.writeShort(0);// 00 00 utf
		dOut.writeInt(0);// 00 00 00 00
		dOut.writeShort(0);// 00 00 //utf
		dOut.writeInt(0);// 00 00 00 00
		dOut.writeInt(0);// 00 00 00 00
		dOut.writeByte(0);// 00

		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildBaselineHINO3(Harvester s) throws IOException {

		int iPacketSize = 66; // at 66 is base size
		// add string sizes
		iPacketSize += s.getSTFFileIdentifier().length();
		iPacketSize += s.getSTFFileName().length();
		iPacketSize += (s.getStructureName().length() * 2);

		short iUpdateCount = 16;

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(s.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_HINO]);
		dOut.writeByte(3);
		dOut.writeInt(iPacketSize); // 65 00 00 00 //byte count 101 // all bytes
									// counted below this
		dOut.writeShort(iUpdateCount);// 2 - 10 00 //upd Count
		dOut.writeFloat(1);// 4 - 00 00 80 3F //west facing 1 float 1 //operand
							// 0 0x00
		// operand 1 VVV 0x01
		dOut.writeUTF(s.getSTFFileName());// 2 - 10 00 70 6C 61 79 65 72 5F 73
											// 74 72 75 63 74 75 72 65 //
											// player_structure //16 - 2
											// //operand1
		dOut.writeInt(0);// 4 - 00 00 00 00 -
		dOut.writeUTF(s.getSTFFileIdentifier());// 2 - 13 00 74 65 6D 70 6F 72
												// 61 72 79 5F 73 74 72 75 63 74
												// 75 72 65
												// //temporary_structure //19 -
												// 3
		dOut.writeUTF16(s.getStructureName());// 4 - 00 00 00 00 - 4// its
												// possible some of these values
												// modify the terrain.//operand
												// 2 0x02
		dOut.writeInt(1);// short 1 / operand 3 0x03
		dOut.writeShort(0);// 4 - 00 00 00 00 - oper 4 0x04
		dOut.writeInt(0);// 4 - 00 00 00 00 - oper 5 0x05
		dOut.writeInt(s.getIAnimationBitmask());// 4 - 00 00 00 00 - oper 6
												// 0x06<--Animation Bitmask 256
												// off 257 on
		//dOut.writeInt(0);// 4 - 00 00 00 00 - oper 7 0x07
		//dOut.writeInt(0);// 4 - 00 00 00 00 - oper 8 0x08
		dOut.writeLong(0); // Operand 7 
		dOut.writeInt(s.getPVPStatus());// 4 - 00 00 00 00 - oper 8 0x09
		dOut.writeInt(0);// 4 - 00 00 00 00 - oper 9 0x0A
		dOut.writeInt(s.getConditionDamage());// 4 - 00 00 00 00 - oper 10 0x0B
		dOut.writeInt(s.getMaxCondition());// 4 - 00 00 00 00 - oper 11 0x0C
		dOut.writeByte(1);// oper 12 //0x0D
		dOut.writeByte(0); // Operand 13
		dOut.writeFloat(s.getPowerPool());// 4 - 00 00 00 00 - 14 0x0E //power
											// reserves
		dOut.writeFloat(s.getPowerRate());// 4 - 00 00 00 00 - 15 //power
											// consumption per hour 0x0F

		dOut.flush();
		return dOut.getBuffer();

	}

	protected static byte[] buildBaselineHINO6(Harvester s) throws IOException {

		int iPacketSize = 27; // at 27 is base size
		short iUpdateCount = 5;

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(s.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_HINO]);
		dOut.writeByte(6);
		dOut.writeInt(iPacketSize);// 17 00 00 00 //byte count 23 //all bytes
									// counted below this
		dOut.writeShort(iUpdateCount);// 04 00 //upd size
		dOut.writeInt(0x88);// 88 00 00 00
		dOut.writeShort(0);// 00 00 utf
		dOut.writeInt(0);// 00 00 00 00
		dOut.writeShort(0);// 00 00 //utf
		dOut.writeInt(0);// 00 00 00 00
		dOut.writeLong(0);// 00 00 00 00 00 00 00 00
		dOut.writeByte(0);// 00

		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildBaselineHINO7(Harvester s,
			Vector<SpawnedResourceData> vSRD) throws IOException {

		int iPacketSize = 74;
		int iResourceCount = vSRD.size();
		Hashtable<Long, SOEObject> vOutputHopper = s.getOutputHopper();
		int iHopperContents = vOutputHopper.size();
		float fTotalHopperContents = 0;

		/*
		 * int iHopperList = 0; if(iHopperContents>=1) { iHopperList = 1; }
		 */

		iPacketSize += (iResourceCount * 4);// count the utf shorts
		iPacketSize += (iResourceCount * 16);// count the resource id longs

		iPacketSize += (iHopperContents * 12);// count the resource id longs
		Enumeration<SOEObject> vOutputHopperEnum = vOutputHopper.elements();

		for (int i = 0; i < iResourceCount; i++) {
			iPacketSize += vSRD.get(i).getName().length(); // add up the name
															// length
			iPacketSize += vSRD.get(i).getIffFileName().length();// addup the
																	// iff
																	// filename
																	// length
		}

		while (vOutputHopperEnum.hasMoreElements()) {
			ResourceContainer r = (ResourceContainer) vOutputHopperEnum
					.nextElement();
			fTotalHopperContents += r.getStackQuantity();
		}

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(s.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_HINO]);
		dOut.writeByte(7);
		dOut.writeInt(iPacketSize); // 86 02 00 00 //Packet Size???? 646
									// bytes!!!! WOW THIS IS A BIG MOMMA~
		dOut.writeShort((0x0F)); // 0F 00 // 15? Decimal??? 15 Updates??? --
									// Yes, 15 operands.
		dOut.writeByte(1);// 01 //oper 0 0x00

		dOut.writeInt(iResourceCount);// 0D 00 00 00
		dOut.writeInt(iResourceCount);// 0D 00 00 00 // Actually, this is the
										// resource list update counter.
		/*
		 * 58 82 F2 D9 35 00 00 00 B4 D7 E8 FA 35 00 00 00 CC 1B 9B DE 35 00 00
		 * 00 1B 3A 9A DE 35 00 00 00 94 DE AF 0F 36 00 00 00 CF F8 D8 EC 35 00
		 * 00 00 08 E5 F4 D9 35 00 00 00 5D 8D 99 DE 35 00 00 00 5E 8D 9B DE 35
		 * 00 00 00 95 DE AF 0F 36 00 00 00 7C FF D8 EC 35 00 00 00 35 B5 9A DE
		 * 35 00 00 00 4D 78 9B DE 35 00 00 00
		 */
		for (int i = 0; i < iResourceCount; i++) {
			dOut.writeLong(vSRD.get(i).getID());// write the resource ids
		}
		// oper 1 0x01
		dOut.writeInt(iResourceCount);// 0D 00 00 00 //4

		dOut.writeInt(iResourceCount);// 0D 00 00 00 // Again, resource list
										// update counter
		/*
		 * 58 82 F2 D9 35 00 00 00 B4 D7 E8 FA 35 00 00 00 CC 1B 9B DE 35 00 00
		 * 00 1B 3A 9A DE 35 00 00 00 94 DE AF 0F 36 00 00 00 CF F8 D8 EC 35 00
		 * 00 00 08 E5 F4 D9 35 00 00 00 5D 8D 99 DE 35 00 00 00 5E 8D 9B DE 35
		 * 00 00 00 95 DE AF 0F 36 00 00 00 7C FF D8 EC 35 00 00 00 35 B5 9A DE
		 * 35 00 00 00 4D 78 9B DE 35 00 00 00
		 */
		for (int i = 0; i < iResourceCount; i++) {
			dOut.writeLong(vSRD.get(i).getID());// write the resource ids
		}
		// oper 2 0x02
		dOut.writeInt(iResourceCount);// 0D 00 00 00

		dOut.writeInt(iResourceCount);// 0D 00 00 00
		/*
		 * 05 00 45 61 64 6F 70 //Eadop 0B 00 43 61 72 62 69 66 65 6B 69 61 6E
		 * //Carbifekian.. 06 00 57 6F 64 69 6F 77 //Wodiow 07 00 56 6F 74 65 69
		 * 73 6D //..Voteism. 0A 00 45 75 73 65 66 61 67 69 74 65 //Eusefagite
		 * 05 00 42 61 68 69 6B //Bahik 0D 00 41 75 6B 65 63 61 75 6E 69 72 69
		 * 6E 65 //..Aukecaunirine 07 00 4F 69 64 69 65 77 69 //Oidiewi 07 00 45
		 * 61 6A 65 61 72 6F //..Eajearo 07 00 42 65 69 61 73 69 64 //..Beiasid
		 * 0D 00 43 61 72 62 61 6C 6B 61 72 65 69 75 6D //..Carbalkareium 0F 00
		 * 4F 6D 6E 69 63 69 70 61 64 6C 65 68 72 69 73 //..Omnicipadlehris 08
		 * 00 49 6E 65 6D 65 69 73 6D //..Inemeism
		 */
		for (int i = 0; i < iResourceCount; i++) {
			dOut.writeUTF(vSRD.get(i).getName());// write the resource name
		}
		// oper 3 0x03
		dOut.writeInt(iResourceCount);// 0D 00 00 00
		dOut.writeInt(iResourceCount);// 0D 00 00 00
		/*
		 * 11 00 72 61 64 69 6F 61 63 74 69 76 65 5F 74 79 70 65 37
		 * //radioactive_type7 0D 00 73 74 65 65 6C 5F 72 68 6F 64 69 75 6D
		 * //..steel_rhodium 0D 00 73 74 65 65 6C 5F 6B 69 69 72 69 75 6D
		 * //.steel_kiirium. 0F 00 73 74 65 65 6C 5F 63 61 72 62 6F 6E 69 74 65
		 * //.steel_carbonite 0C 00 69 72 6F 6E 5F 61 78 69 64 69 74 65
		 * //..iron_axidite 0D 00 69 72 6F 6E 5F 64 6F 6C 6F 76 69 74 65
		 * //..iron_dolovite 0C 00 69 72 6F 6E 5F 6B 61 6D 6D 72 69 73
		 * //..iron_kammris 11 00 61 6C 75 6D 69 6E 75 6D 5F 63 68 72 6F 6D 69
		 * 75 6D //..aluminum_chromium 11 00 61 6C 75 6D 69 6E 75 6D 5F 63 68 72
		 * 6F 6D 69 75 6D //..aluminum_chromium 0D 00 63 6F 70 70 65 72 5F 63 6F
		 * 64 6F 61 6E //..copper_codoan 10 00 63 6F 70 70 65 72 5F 70 6F 6C 79
		 * 73 74 65 65 6C //..copper_polysteel 19 00 6F 72 65 5F 63 61 72 62 6F
		 * 6E 61 74 65 5F 62 61 72 74 68 69 65 72 69 75 6D
		 * //..ore_carbonate_barthierium 0F 00 61 72 6D 6F 70 68 6F 75 73 5F 72
		 * 75 64 69 63 //..armophous_rudic
		 */
		for (int i = 0; i < iResourceCount; i++) {
			dOut.writeUTF(vSRD.get(i).getIffFileName());// write the resource
														// type from the iff
														// name
		}
		// -----confusing part

		/*
		 * 
		 * 00 00 00 00 00 00 00 00 01 <---Harvester on or off Boolean 2C 00 00
		 * 00 -ext rate 00 00 30 42 -ext rate 83 71 B1 41 -actual ext rate 00 F5
		 * FC 46 - hopper contents 78 13 06 00 -hopper size 02 //unk 01 00 00 00
		 * --res in hopper cnt 01 00 00 00 --res in hopper //list 35 B5 9A DE 35
		 * 00 00 00 --res id 00 F5 FC 46 <--res quantity in hopper for this one
		 * 
		 * 64 --harv cond
		 */
		// this is operand 5 it is the curent resource were harvesting 0x05
		if (s.getCurrentHarvestResource() != null) {
			dOut.writeLong(s.getCurrentHarvestResource().getID());
		} else {
			dOut.writeLong(0);
		}

		dOut.writeBoolean(s.isInstallationActive());// oper 6 0x06 00
													// <---HARVESTER ON OR OFF
													// STATUS
		dOut.writeInt(s.getBaseExtractionRate());// Oper 7 0x07| 2C 00 00 00
													// <--seen on next packet
													// //ext rate
		dOut.writeFloat(s.getBaseExtractionRate());// Op8 0x08 00 00 30 42
														// <--seen on next
														// packet
		dOut.writeFloat(s.getIActualExtractionRate());// op 9 0x09 00 00 00
														// 00//actual extraction
														// rate
		dOut.writeFloat(fTotalHopperContents);// op10 0x0A 00 00 00 00//total
												// hopper contents all resource
												// quantities added up
		dOut.writeInt(s.getIOutputHopperSize());// op11 0x0B 78 13 06 00 <--seen
												// on next packet 398200 /
												// hopper size
		dOut.writeByte(s.getHarvesterUpdateCounter());// 0x0C op 12 00
														// //HarvesterUpdateCounter
														// //???
		dOut.writeInt(vOutputHopper.size());// oper 13 List 0x0D 00 00 00 00
											// //resources in hopper list count
											// 0 if none 1 if res exist
		dOut.writeInt(s.getIHarvesterResourceUpdateCounter());// 00 00 00 00 //
																// HarvesterResourceUpdateCounter
		vOutputHopperEnum = vOutputHopper.elements();
		while (vOutputHopperEnum.hasMoreElements()) {
			ResourceContainer r = (ResourceContainer) vOutputHopperEnum
					.nextElement();
			dOut.writeLong(r.getResourceSpawnID()); // 35 B5 9A DE 35 00 00 00
													// --res id
			dOut.writeFloat(r.getStackQuantity()); // 00 F5 FC 46 <--res
													// quantity in hopper for
													// this one
		}
		dOut.writeByte(s.getCurrentHarvesterCondition());// Operand 14 0x0E 64
															// <--Seen on next
															// packet//condition//seems
															// to always be last
															// byte
		//
		dOut.flush();
		byte[] b = dOut.getBuffer();
		// PacketUtils.printPacketToScreen(b, b.length,"HINO7BASELINE");
		return b;
	}

	protected static byte[] buildDeltasMessageHINO7(Harvester s,
			Vector<SpawnedResourceData> vSRD) throws IOException {

		int iPacketSize = 66;
		int iResourceCount = vSRD.size();

		iPacketSize += (iResourceCount * 16);// add up the resource id longs
		iPacketSize += (iResourceCount * 4);// addup the available bools
		iPacketSize += (iResourceCount * 8);// addup the available res index
		iPacketSize += (iResourceCount * 4); // add up the utf name shorts and
												// iff name shorts

		for (int i = 0; i < iResourceCount; i++) {
			iPacketSize += vSRD.get(i).getName().length();// addup the name
															// lengths
			iPacketSize += vSRD.get(i).getIffFileName().length();// addup the
																	// iffname
																	// lengths
		}

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(s.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_HINO]);
		dOut.writeByte(7);

		dOut.writeInt(iPacketSize);// 1A 03 00 00 //794 bytes
		dOut.writeShort(9);// 09 00 //upd count???
		dOut.writeShort(0);// 00 00 //upd 0
		dOut.writeByte(1);// 01

		dOut.writeShort(1); // 01 00 //upd 1 resource list
		dOut.writeInt(iResourceCount); // 0D 00 00 00 //resource list items
										// count
		dOut.writeInt(iResourceCount); // 0D 00 00 00 //resource count again???
		for (int i = 0; i < iResourceCount; i++) {
			dOut.writeBoolean(vSRD.get(i).isSpawned());
			dOut.writeShort(i);
			dOut.writeLong(vSRD.get(i).getID());
		}
		/*
		 * 01 Available flag 00 00 //unk byte 58 82 F2 D9 35 00 00 00 //res id
		 * 
		 * 01 //available bool????? 01 00 //resource index B4 D7 E8 FA 35 00 00
		 * 00
		 * 
		 * 01 02 00 CC 1B 9B DE 35 00 00 00
		 * 
		 * 01 5..........5.... 03 00 1B 3A 9A DE 35 00 00 00
		 * 
		 * 01 04 00 94 DE AF 0F 36 00 00 00
		 * 
		 * 01 05 00 CF F8 D8 EC 35 00 00 00 .6..........5...
		 * 
		 * 01 06 00 08 E5 F4 D9 35 00 00 00
		 * 
		 * 01 07 00 5D 8D 99 DE 35 00 00 00
		 * 
		 * 01 08 00 5E 8D 9B DE 35 00 00 00
		 * 
		 * 01 09 00 95 DE AF 0F 36 00 00 00
		 * 
		 * 01 0A 00 7C FF D8 EC 35 00 00 00
		 * 
		 * 01 0B 00 35 B5 9A DE 35 00 00 00
		 * 
		 * 01 0C 00 4D 78 9B DE 35 00 00 00
		 */

		dOut.writeShort(2); // 02 00 //update 2
		dOut.writeInt(iResourceCount); // 0D 00 00 00
		dOut.writeInt(iResourceCount); // 0D 00 00 00
		for (int i = 0; i < iResourceCount; i++) {
			dOut.writeBoolean(vSRD.get(i).isSpawned());
			dOut.writeShort(i);
			dOut.writeLong(vSRD.get(i).getID());
		}
		/*
		 * 01 00 00 58 82 F2 D9 35 00 00 00 01 01 00 B4 D7 E8 FA 35 00 00 00 01
		 * 02 00 .........5...... CC 1B 9B DE 35 00 00 00 01 03 00 1B 3A 9A DE
		 * 35 00 00 00 01 04 00 94 DE AF 0F 36 00 00 00 01 05 00 CF F8 D8 EC 35
		 * 00 00 00 01 06 00 08 E5 F4 D9 35 00 00 00 01 07 00 5D 8D 99 DE 35 00
		 * 00 00 01 5......]...5.... 08 00 5E 8D 9B DE 35 00 00 00 01 09 00 95
		 * DE AF 0F 36 00 00 00 01 0A 00 7C FF D8 EC 35 00 00 00
		 * .6......|...5... 01 0B 00 35 B5 9A DE 35 00 00 00 01 0C 00 4D 78 9B
		 * DE 35 00 00 00
		 */
		dOut.writeShort(3); // 03 00 //upd 3
		dOut.writeInt(iResourceCount); // 0D 00 00 00
		dOut.writeInt(iResourceCount); // 0D 00 00 00 ..5.............
		for (int i = 0; i < iResourceCount; i++) {
			dOut.writeBoolean(vSRD.get(i).isSpawned());
			dOut.writeShort(i);
			dOut.writeUTF(vSRD.get(i).getName());
		}
		/*
		 * 01 00 00 05 00 45 61 64 6F 70 //Eadop..... 01 01 00 0B 00 43 61 72 62
		 * 69 66 65 6B 69 61 6E //Carbifekian..... 01 02 00 06 00 57 6F 64 69 6F
		 * 77 //Wodiow 01 03 00 07 00 56 6F 74 65 69 73 6D //..Voteism 01 04 00
		 * 0A 00 45 75 73 65 66 61 67 69 74 65 //..Eusefagite 01 05 00 05 00 42
		 * 61 68 69 6B //..Bahik 01 06 00 0D 00 41 75 6B 65 63 61 75 6E 69 72 69
		 * 6E 65 //..Aukecaunirine.... 01 07 00 07 00 4F 69 64 69 65 77 69 //
		 * .Oidiewi 01 08 00 07 00 45 61 6A 65 61 72 6F //..Eajearo 01 09 00 07
		 * 00 42 65 69 61 73 69 64 //.Beiasid 01 0A 00 0D 00 43 61 72 62 61 6C
		 * 6B 61 72 65 69 75 6D //.Carbalkareium 01 0B 00 0F 00 4F 6D 6E 69 63
		 * 69 70 61 64 6C 65 68 72 69 73 //..Omnicipadlehris... 01 0C 00 08 00
		 * 49 6E 65 6D 65 69 73 6D //..Inemeism
		 */
		dOut.writeShort(4); // 04 00 //upd 4
		dOut.writeInt(iResourceCount); // 0D 00 00 00 ......
		dOut.writeInt(iResourceCount); // 0D 00 00 00
		for (int i = 0; i < iResourceCount; i++) {
			dOut.writeBoolean(vSRD.get(i).isSpawned());
			dOut.writeShort(i);
			dOut.writeUTF(vSRD.get(i).getIffFileName());
		}
		/*
		 * 01 00 00 11 00 72 61 64 69 6F 61 63 74 69 76 65 5F 74 79 70 65 37
		 * //.radioactive_type7 01 01 00 0D 00 73 74 65 65 6C 5F 72 68 6F 64 69
		 * 75 6D //.steel_rhodium.... 01 02 00 0D 00 73 74 65 65 6C 5F 6B 69 69
		 * 72 69 75 6D //.steel_kiirium.. 01 03 00 0F 00 73 74 65 65 6C 5F 63 61
		 * 72 62 6F 6E 69 74 65 //..steel_carbonite 01 04 00 0C 00 69 72 6F 6E
		 * 5F 61 78 69 64 69 74 65 //.iron_axidite 01 05 00 0D 00 69 72 6F 6E 5F
		 * 64 6F 6C 6F 76 69 74 65 //..iron_dolovite 01 06 00 0C 00 69 72 6F 6E
		 * 5F 6B 61 6D 6D 72 69 73 //iron_kammris 01 07 00 11 00 61 6C 75 6D 69
		 * 6E 75 6D 5F 63 68 72 6F 6D 69 75 6D //.aluminum_chromium.... 01 08 00
		 * 11 00 61 6C 75 6D 69 6E 75 6D 5F 63 68 72 6F 6D 69 75 6D
		 * //.aluminum_chromium 01 09 00 0D 00 63 6F 70 70 65 72 5F 63 6F 64 6F
		 * 61 6E //.copper_codoan 01 0A 00 10 00 63 6F 70 70 65 72 5F 70 6F 6C
		 * 79 73 74 65 65 6C //.copper_polysteel 01 0B 00 19 00 6F 72 65 5F 63
		 * 61 72 62 6F 6E 61 74 65 5F 62 61 72 74 68 69 65 72 69 75 6D
		 * //..ore_carbonate_barthierium 01 0C 00 0F 00 61 72 6D 6F 70 68 6F 75
		 * 73 5F 72 75 64 69 63 //.armophous_rudic..,..
		 */

		dOut.writeShort(7); // 07 00 upd 5
		dOut.writeInt(s.getBaseExtractionRate());// 2C 00 00 00 <--extraction
													// rate as to why send it as
													// int and float who knows
		dOut.writeShort(8); // 08 00 //upd6
		dOut.writeFloat(s.getBaseExtractionRate());// 00 00 30 42 //
														// <--Extraction rate
		dOut.writeShort(0x0B); // 0B 00 upd 7
		dOut.writeInt(s.getIOutputHopperSize());// 78 13 06 00 //<--output
												// hopper size
		dOut.writeShort(0x0E); // 0E 00 //upd 8
		dOut.writeByte(s.getCurrentHarvesterCondition());// 64 // count 9 from
															// 0<--Harvester
															// Condition

		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasMessageHINO7_ResourceHopper(Harvester s)
			throws IOException {

		int iHopperList = 0;

		if (s.getOutputHopper().size() >= 1) {
			iHopperList = 1;
		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(s.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_HINO]);
		dOut.writeByte(7);
		dOut.writeInt(36);// this packet should always be 36 bytes WHY…………
		dOut.writeShort(3);// upd count 2
		dOut.writeShort(0x0C);// 4
		dOut.writeByte(s.getHarvesterUpdateCounter());// 5
		dOut.writeShort(0x0D);// 0D 00 //opr 13 <--hopper contents upd 4//7
		dOut.writeInt(iHopperList);// 01 00 00 00//11
		// currently being harvested resource
		dOut.writeInt(s.getIHarvesterResourceUpdateCounter());// 02 00 00 00//15
		// boolean resourcesent = false;
		Enumeration<SOEObject> vOutputHopperEnum = s.getOutputHopper()
				.elements();
		boolean bFound = false;
		int i = 0;
		while (vOutputHopperEnum.hasMoreElements() && !bFound) {
			ResourceContainer r = (ResourceContainer) vOutputHopperEnum
					.nextElement();
			if (r.getResourceSpawnID() == s.getCurrentHarvestResource().getID()) {
				dOut.writeByte(2);// 02//16
				dOut.writeShort((short) i);// // 00//18
				dOut.writeLong(r.getResourceSpawnID());// 35 B5 9A DE 35 00 00
														// 00//26
				dOut.writeFloat(r.getStackQuantity());// 7B 60 6E 47//30
				// resourcesent = true;
			}
			i++;
		}
		dOut.writeShort(0x0A);// 0A 00 //opr 10 //total hopper contents upd
								// 5//32
		dOut.writeFloat(s.getTotalHopperQuantity());// 7B 60 6E 47////36
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasMessageHINO7_EmptyHopper(Harvester s)
			throws IOException {

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(s.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_HINO]);
		dOut.writeByte(7);
		dOut.writeInt(21);//
		dOut.writeShort(3);// upd count 2
		dOut.writeShort(0x0C);// 4
		dOut.writeByte(s.getHarvesterUpdateCounter());// 5
		dOut.writeShort(0x0D);// 0D 00 //opr 13 <--hopper contents upd 4//7
		dOut.writeInt(0);// 01 00 00 00//11
		dOut.writeInt(s.getIHarvesterResourceUpdateCounter());// 02 00 00 00//15
		dOut.writeShort(0x0A);// 0A 00 //opr 10 //total hopper contents upd
								// 5//17
		dOut.writeFloat(s.getTotalHopperQuantity());// 7B 60 6E 47////21
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasMessageHINO3_Activate_Installation(
			Harvester s) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(s.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_HINO]);
		dOut.writeByte(3);
		dOut.writeInt(11);// 0B 00 00 00 Packet Size//
		dOut.writeShort(2);// upd count //it says 2 but there are 3 operands,
							// looks like two of em is actually one
		dOut.writeShort(8);
		dOut.writeInt(257);// operand 8 08 00 <active or not UPD 1
		dOut.writeShort(0x0D);// Update 2 0D 00 //oper 13
		dOut.writeByte(1);
		dOut.flush();
		byte[] b = dOut.getBuffer();
		return b;
	}

	protected static byte[] buildDeltasMessageHINO7_Activate_Installation(
			Harvester s) throws IOException {

		int iHopperList = 0;
		if (!s.getOutputHopper().isEmpty()) {
			iHopperList = 1;
		}
		int iHopperUpdateCounter = s.getIHarvesterResourceUpdateCounter();

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(s.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_HINO]);
		dOut.writeByte(7);
		SOEOutputStream buff = new SOEOutputStream(new ByteArrayOutputStream());
		// dOut.writeInt(0x2D);//2D 00 00 00 packet size
		buff.writeShort(5);// upd count 05 00 .6...-.....
		buff.writeShort(6);// oper 6 06 00 //oper 6 <---HARVESTER ON OR OFF
							// STATUS update 1
		buff.writeBoolean(s.isInstallationActive());// 01 <bool
		buff.writeShort(6);// actual extraction rate upd 2
		buff.writeFloat(s.getIActualExtractionRate());// 83 71 B1 41
		buff.writeShort(0x0C);// 0C 00 //opr 12 upd 3
		buff.writeByte(s.getHarvesterUpdateCounter());// 03
		buff.writeShort(0x0D);// 0D 00 //opr 13 <--hopper contents upd 4
		buff.writeInt(iHopperList);// 01 00 00 00
		buff.writeInt(iHopperUpdateCounter);// 02 00 00 00
		int iHopperTotalContents = 0;
		Enumeration<SOEObject> vOutputHopperEnum = s.getOutputHopper()
				.elements();
		byte i = 0;
		while (vOutputHopperEnum.hasMoreElements()) {
			ResourceContainer r = (ResourceContainer) vOutputHopperEnum
					.nextElement();
			buff.writeShort((short) iHopperUpdateCounter);// 02 00
			buff.writeByte(i);// 00
			buff.writeLong(r.getResourceSpawnID());// 35 B5 9A DE 35 00 00 00
			buff.writeInt(r.getStackQuantity());// 7B 60 6E 47
			iHopperTotalContents += r.getStackQuantity();
			i++;
		}
		buff.writeShort(0x0A);// 0A 00 //opr 10 //total hopper contents upd 5
		buff.writeInt(iHopperTotalContents);// 7B 60 6E 47
		buff.flush();
		byte[] footer = buff.getBuffer();
		buff.close();
		dOut.writeInt(footer.length);
		dOut.write(footer);
		dOut.flush();

		return dOut.getBuffer();
	}

	protected static byte[] buildBaselineBUIO3(Structure s) throws IOException {

		int iPacketSize = 57; // at 66 is base size
		// add string sizes
		iPacketSize += s.getSTFFileIdentifier().length();
		iPacketSize += s.getSTFFileName().length();
		iPacketSize += s.getStructureName().length() * 2;
		short iUpdateCount = 13;

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(s.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_BUIO]);
		dOut.writeByte(3);
		dOut.writeInt(iPacketSize); // 65 00 00 00 //byte count 101 // all bytes
									// counted below this
		dOut.writeShort(iUpdateCount);// 2 - 10 00 //upd Count
		// op0
		dOut.writeFloat(1);// 4 - 00 00 80 3F //west facing 1 float 1//upd 0
		// op1
		dOut.writeUTF(s.getSTFFileName());// 2 - 10 00 70 6C 61 79 65 72 5F 73
											// 74 72 75 63 74 75 72 65 //
											// player_structure //16 - 2//upd 1
		dOut.writeInt(0);// 4 - 00 00 00 00 -
		dOut.writeUTF(s.getSTFFileIdentifier());// 2 - 13 00 74 65 6D 70 6F 72
												// 61 72 79 5F 73 74 72 75 63 74
												// 75 72 65
												// //temporary_structure //19 -
												// 3
		// op2
		dOut.writeUTF16(s.getStructureName());// upd 2
		// op3
		dOut.writeShort(0xFF);
		
		// Corrected by Maach to line up baseline IDs correctly.  0x0D variables = 13 variables, numbered 0 to 12.
		//dOut.writeInt(0);// 4
		//dOut.writeInt(0);// 5
		dOut.writeLong(0); // 4
		dOut.writeInt(0);// 5
		dOut.writeInt(0); // 6
		dOut.writeInt(0x01000000); // 7
		dOut.writeInt(s.getPVPStatus());// 8
		dOut.writeInt(0);// 9
		dOut.writeInt(0);// 10
		dOut.writeInt(s.getMaxCondition());// condition //11
		dOut.writeByte(1);// 12

		dOut.flush();
		return dOut.getBuffer();

	}

	protected static byte[] buildBaselineBUIO6(Structure s) throws IOException {
		int iPacketSize = 23; // at 23 is base size
		// iPacketSize += s.getSTFDetailName().length();
		// iPacketSize += s.getSTFDetailIdentifier().length();

		short iUpdateCount = 4;

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(s.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_BUIO]);
		dOut.writeByte(6);
		dOut.writeInt(iPacketSize);// 17 00 00 00 //byte count 23 //all bytes
									// counted below this
		dOut.writeShort(iUpdateCount);// 04 00 //upd size
		dOut.writeInt(0x7D);// 7D 00 00 00 //125???? 125 what?
		dOut.writeShort(0); // dOut.writeUTF(s.getSTFDetailName());//0F 00 62 75
							// 69 6C 64 69 6E 67 5F 64 65 74 61 69 6C
							// //building_detail
		dOut.writeInt(0);// 00 00 00 00
		dOut.writeShort(0); // dOut.writeUTF(s.getSTFDetailIdentifier());//1D 00
							// 68 6F 75 73 69 6E 67 5F 67 65 6E 65 72 61 6C 5F
							// 73 6D 61 6C 6C 5F 73 74 79 6C 65 5F 31
							// //housing_general_small_style_1
		dOut.writeInt(0);// 00 00 00 00
		dOut.writeInt(0);// 00 00 00 00
		dOut.writeByte(0);// 00

		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasMessageSCLT3(Cell c, ZoneClient client)
			throws IOException {

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.BaselinesMessage);
		dOut.writeLong(c.getID());// EE 24 AF EB 34 00 00 00
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_SCLT]);
		dOut.writeByte(3);// 54 4C 43 53 03 //TCLS 3 DELTA
		dOut.writeInt(8);// 08 00 00 00 //8bytes
		dOut.writeShort(0x01);// 01 00 //operand count
		dOut.writeShort(0x05);// 05 00 //op5 is n ot known......must be 4
		dOut.writeInt(c.getCellNum());// 01 00 00 00
		dOut.flush();
		return dOut.getBuffer();
	}

	protected final static byte[] buildObjectController_CraftingSchematicList(
			Player player, TangibleItem item,
			Vector<CraftingSchematic> schematics,
			TangibleItem nearbyCraftingStation) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0x0B);
		dOut.writeInt(Constants.CraftingDraftSchematics);
		dOut.writeLong(player.getID());
		dOut.writeInt(0);
		dOut.writeLong(item.getID());
		if (nearbyCraftingStation != null) {
			System.out.println("Nearby crafting station found with ID "
					+ nearbyCraftingStation.getID());
			dOut.writeLong(nearbyCraftingStation.getID());
		} else {
			System.out.println("No nearby crafting station.");
			dOut.writeLong(0);
		}
		if (schematics != null) {
			if (!schematics.isEmpty()) {
				dOut.writeInt(schematics.size());
				CraftingSchematic cs;
				for (int i = 0; i < schematics.size(); i++) {
					cs = schematics.elementAt(i);
					// System.out.println("Schematic index " + i + " -- CRC: " +
					// Integer.toHexString(cs.getCRC()));
					dOut.writeInt(cs.getCRC()); // Could also be the CRC?
					dOut.writeInt(cs.getCRC()); // Could also be number of
												// experimentation points...
					dOut.writeInt(cs.getIToolTabBitmask()); // int 2 is armor
				}
			} else {
				dOut.writeInt(0);
			}
		} else {
			dOut.writeInt(0);
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	protected final static byte[] buildObjectControllerPlayerDataTransformToClient(
			Player p, int iOperation) throws IOException {
		/**
		 * This is an object controller update transform message from the server
		 * to the client It has been seen while placing structures? Why dunno
		 * Maybe it flattens terrain or makes it ready for something. It may be
		 * what makes it snap to terrain.Probably its exactly that. 05 00 = 46
		 * 5E CE 80 = 53 00 00 00 = 71 00 00 00 //DataTransform= 37 1B 79 1F 35
		 * 00 00 00 //objid= 00 00 00 00 = 00 00 00 00 = 02 00 00 00 //mov
		 * update counter 00 00 00 00 //N F3 04 35 3F //S 0.707107 00 00 00 00
		 * //E F3 04 35 3F //W 0.707107 B8 BE 52 C5 //X -3371.92 4C 23 A8 41 //Z
		 * 21.0172 3D 0A BA C3 //Y -372.08 00 00 00 00 00 00 00 00 00
		 */
		/*
		 * if(p instanceof Player) {System.out.println(
		 * "Player in buildObjControllerPlayerDataTransformToClient");
		 * System.out.println("Player X:" + p.getX() + " Y:" + p.getY() + " Z:"
		 * + p.getZ() + " Pl:" + p.getPlanetID() + " C:" + p.getCellID() +
		 * " oI:" + p.getOrientationN() + " oJ:" + p .getOrientationS() + " oK:"
		 * + p .getOrientationE() + " oW:" + p .getOrientationW()); }
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(iOperation);// operation? Seen as 0x53 on nge but this is
									// normally 0x21 on Precu
		dOut.writeInt(Constants.DataTransform);
		dOut.writeLong(p.getID());
		dOut.writeInt(0);
		// dOut.writeInt(0);
		dOut.writeInt(p.getMoveUpdateCount());
		dOut.writeFloat(p.getOrientationN());
		dOut.writeFloat(p.getOrientationS());
		dOut.writeFloat(p.getOrientationE());
		dOut.writeFloat(p.getOrientationW());
		dOut.writeFloat(p.getX());
		dOut.writeFloat(p.getZ());
		dOut.writeFloat(p.getY());
		dOut.writeLong(0);
		dOut.writeByte(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected final static byte[] buildObjectControllerPlayerDataTransformWithParentToClient(
			Player p, int iOperation) throws IOException {
		/**
		 * This is an object controller update transform message from the server
		 * to the client It has been seen while placing structures? Why dunno
		 * Maybe it flattens terrain or makes it ready for something. It may be
		 * what makes it snap to terrain.Probably its exactly that. 05 00 46 5E
		 * CE 80 0B 00 00 00 F1 00 00 00 A6 FF 7C 4D 09 00 00 00 00 00 00 00 00
		 * 00 00 00 98 01 00 00 77 17 F1 F5 2C 00 00 00 <--CELL ID 00 00 00 00
		 * //n 14 7F 26 3F //s 00 00 00 00 //e ED 75 42 3F //w 00 40 1B BE //x
		 * -0.151611 0C D7 07 C1 //z -8.49 D0 FA 48 41 //y 12.5612 00 00 00 00
		 * 00 00 00 00 00
		 * 
		 * B8 A7 31 01 00 00 00 00 //long //character id 7D 55 00 00 //upd
		 * counter 1E 00 00 00 //Mov Counter C5 D1 19 00 00 00 00 00 //cell id
		 */
		/*
		 * if(p instanceof Player) {System.out.println(
		 * "Player in buildObjControllerPlayerDataTransformWithParentToClient");
		 * System.out.println("Player X:" + p.getCellX() + " Y:" + p.getCellY()
		 * + " Z:" + p.getCellZ() + " Pl:" + p.getPlanetID() + " C:" +
		 * p.getCellID()); }
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(iOperation);// seen as 0x53 nge, precu 0x21 ???WIT?
									// POSTURE????? Seen Values as 0x53,0x21,
									// 0x0B
		dOut.writeInt(Constants.DataTransformWithParent);
		dOut.writeLong(p.getID());

		dOut.writeInt(p.getIObjectUpdateCounter(true));
		// dOut.writeInt(0);

		dOut.writeInt(p.getMoveUpdateCount());
		dOut.writeLong(p.getCellID());
		dOut.writeFloat(p.getOrientationN());
		dOut.writeFloat(p.getOrientationS());
		dOut.writeFloat(p.getOrientationE());
		dOut.writeFloat(p.getOrientationW());
		dOut.writeFloat(p.getCellX());
		dOut.writeFloat(p.getCellZ());
		dOut.writeFloat(p.getCellY());
		// dOut.writeInt(0);
		dOut.writeLong(0);
		dOut.writeByte(0);
		dOut.flush();
		// byte [] buff =
		// PacketUtils.printPacketToScreen(buff,buff.length,
		// "Update Player Pos OBJ Controller IN CELL");
		return dOut.getBuffer();// buff;
	}

	protected final static byte[] buildObjectControllerDataTransformObjectToClient(
			SOEObject o, int iOperation) throws IOException {
		/**
		 * This is an object controller update transform message from the server
		 * to the client It has been seen while placing structures? Why dunno
		 * Maybe it flattens terrain or makes it ready for something. It may be
		 * what makes it snap to terrain.Probably its exactly that. 05 00 = 46
		 * 5E CE 80 = 53 00 00 00 = 71 00 00 00 //DataTransform= 37 1B 79 1F 35
		 * 00 00 00 //objid= 00 00 00 00 = 00 00 00 00 = 02 00 00 00 //mov
		 * update counter 00 00 00 00 //N F3 04 35 3F //S 0.707107 00 00 00 00
		 * //E F3 04 35 3F //W 0.707107 B8 BE 52 C5 //X -3371.92 4C 23 A8 41 //Z
		 * 21.0172 3D 0A BA C3 //Y -372.08 00 00 00 00 00 00 00 00 00
		 */
		/*
		 * if(o instanceof Player) {System.out.println(
		 * "Player in buildObjControllerDataTransformObjectToClient");
		 * System.out.println("Player X:" + o.getX() + " Y:" + o.getY() + " Z:"
		 * + o.getZ() + " Pl:" + o.getPlanetID() + " C:" + o.getCellID()); }
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(iOperation);// operation? Seen as 0x53 on nge but this is
									// normally 0x21 on Precu
		dOut.writeInt(Constants.DataTransform);
		dOut.writeLong(o.getID());
		dOut.writeInt(0);
		dOut.writeInt(0);
		dOut.writeInt(o.getMoveUpdateCount());
		dOut.writeFloat(o.getOrientationN());
		dOut.writeFloat(o.getOrientationS());
		dOut.writeFloat(o.getOrientationE());
		dOut.writeFloat(o.getOrientationW());
		dOut.writeFloat(o.getX());
		dOut.writeFloat(o.getZ());
		dOut.writeFloat(o.getY());
		dOut.writeLong(0);
		dOut.writeByte(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected final static byte[] buildObjectControllerDataTransformWithParentObjectToClient(
			SOEObject o, int iOperation) throws IOException {
		/**
		 * This is an object controller update transform message from the server
		 * to the client It has been seen while placing structures? Why dunno
		 * Maybe it flattens terrain or makes it ready for something. It may be
		 * what makes it snap to terrain.Probably its exactly that. 05 00 46 5E
		 * CE 80 53 00 00 00 <---0x53 in nge 0x21 in PRECU F1 00 00 00
		 * >---DataTransform .O..F^..S....... 53 03 EB 34 33 00 00 00 <---Object
		 * ID 00 00 00 00 <--- 00 00 00 00 <--- S..43........... 01 00 00 00
		 * <---Mov Counter 5F 1B 79 1F 35 00 00 00 <---CELLID 00 00 00 00 //n
		 * ...._.y.5....... 00 00 00 00 //s 00 00 00 00 //e 00 00 80 3F //w 7C
		 * 7B 71 40 //x ...........?|{q@ A4 70 3D 3F //z FF 97 13 C0 //y 00 00
		 * 00 00 00 00 00 00 .p=?............ 00
		 */
		/*
		 * if(o instanceof Player) {System.out.println(
		 * "Player in buildObjControllerDataTransformWithParentObjectToClient");
		 * System.out.println("Player X:" + o.getX() + " Y:" + o.getY() + " Z:"
		 * + o.getZ() + " Pl:" + o.getPlanetID() + " C:" + o.getCellID()); }
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(iOperation);// seen as 0x53 nge, precu 0x21 ???WIT?
									// POSTURE????? Seen Values as 0x53,0x21,
									// 0x0B
		dOut.writeInt(Constants.DataTransformWithParent);
		dOut.writeLong(o.getID());
		dOut.writeInt(0);
		dOut.writeInt(0);
		dOut.writeInt(o.getMoveUpdateCount());
		dOut.writeLong(o.getCellID());
		dOut.writeFloat(o.getOrientationN());
		dOut.writeFloat(o.getOrientationS());
		dOut.writeFloat(o.getOrientationE());
		dOut.writeFloat(o.getOrientationW());
		dOut.writeFloat(o.getCellX());
		dOut.writeFloat(o.getCellZ());
		dOut.writeFloat(o.getCellY());
		dOut.writeLong(0);
		dOut.writeByte(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected final static byte[] buildObjectControllerDequeueItemTransfer(
			SOEObject o, int commandID, int commandCRC) throws IOException {
		/**
		 * 05 00 = 46 5E CE 80 = 0B 00 00 00 = 48 04 00 00 = A6 FF 7C 4D 09 00
		 * 00 00 <--Player ID 00 00 00 00 00 20 00 00 40 //command id 77 59 F7
		 * 82 //transferitemmisc //command crc
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0x0B);
		dOut.writeInt(Constants.TransferItemDequeue);
		dOut.writeLong(o.getID());
		dOut.writeInt(0);
		dOut.writeByte(0);
		dOut.writeInt(commandID);
		dOut.writeInt(commandCRC);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildHAMDelta(Player player, byte baselineID,
			short operand, int hamIndex, int hamValue, boolean bFlytext)
			throws IOException {
		// packetSize = 19 bytes?
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(player.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_CREO]);
		dOut.writeByte(baselineID);
		dOut.writeInt(19); // Packet size
		dOut.writeShort(1);
		// Num updates.
		// update operand:
		dOut.writeShort(operand);
		dOut.writeInt(1);
		int updateCount = 0;
		switch (operand) {
		case 2: { // Max HAM, OR Ham Encumberances
			if (baselineID == 1) {
				// MAX HAM
				updateCount = player.getMaxHamUpdateCounter(true);
				// player.getClient().insertPacket(buildChatSystemMessage("Max Ham update counter "
				// + updateCount));
			} else if (baselineID == 4) {
				// Encumberances
				updateCount = player.getHamEncumberanceUpdateCount(true);
				// player.getClient().insertPacket(buildChatSystemMessage("Ham Encumberance update counter "
				// + updateCount));
			}
			break;
		}
		case 0x11: { // HAM Wounds
			updateCount = player.getHamWoundsUpdateCount(true);
			// player.getClient().insertPacket(buildChatSystemMessage("Ham wounds update counter "
			// + updateCount));
			break;
		}
		case 0x0D: { // Current Ham
			updateCount = player.getCurrentHamUpdateCounter(true);
			// player.getClient().insertPacket(buildChatSystemMessage("Current Ham update counter "
			// + updateCount));
			// System.out.println("Current Ham update counter: " + updateCount);
			break;
		}
		case 0x0E: { // HAM Modifiers
			updateCount = player.getHamModifiersUpdateCount(true);
			// player.getClient().insertPacket(buildChatSystemMessage("Ham modifiers update counter "
			// + updateCount));
			break;
		}
		default: {
			// System.out.println("Ham delta for unknown value -- returning null.");
			return null;
		}
		}
		// Debug

		dOut.writeInt(updateCount);
		if (bFlytext) {
			dOut.writeByte(2);
		} else {
			dOut.writeByte(1);
		}
		dOut.writeShort(hamIndex);
		dOut.writeInt(hamValue);
		dOut.flush();
		// byte[] packet = dOut.getBuffer();
		// PacketUtils.printPacketToScreen(packet, "HAM Delta CREO " +
		// baselineID + " operand " + operand);
		return dOut.getBuffer();
	}

	protected static byte[] buildHAMDelta(Player player, byte baselineID,
			short operand, int[] hamValue) throws IOException {

		int packetSize = 12 + (7 * hamValue.length);
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(player.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_CREO]);
		dOut.writeByte(baselineID);
		// Packet size...
		dOut.writeInt(packetSize);
		dOut.writeShort(1); // NumUpdates
		dOut.writeShort(operand);

		dOut.writeInt(hamValue.length);
		int updateCount = 0;
		switch (operand) {
		case 2: { // Max HAM, OR Ham Encumberances
			if (baselineID == 1) {
				// MAX HAM
				updateCount = player.getMaxHamUpdateCounter(true);
			} else if (baselineID == 4) {
				// Encumberances
				updateCount = player.getHamEncumberanceUpdateCount(true);
			}
			break;
		}
		case 0x11: { // HAM Wounds
			updateCount = player.getHamWoundsUpdateCount(true);
			break;
		}
		case 0x0D: { // Current Ham
			updateCount = player.getCurrentHamUpdateCounter(true);
			break;
		}
		case 0x0E: { // HAM Modifiers
			updateCount = player.getHamModifiersUpdateCount(true);
			break;
		}
		default: {
			return null;
		}
		}
		dOut.writeInt(updateCount);
		for (int i = 0; i < hamValue.length; i++) {
			dOut.writeByte(1);
			dOut.writeShort(i);
			dOut.writeInt(hamValue[i]);
		}

		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildLogoutClient() throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.CLIENT_UI_UPDATE);
		dOut.writeInt(Constants.ClientLogout);
		dOut.flush();
		return dOut.getBuffer();

	}

	protected static byte[] buildDisconnectClient(ZoneClient client,
			short iReason) throws IOException {

		/**
		 * 00 05 42 30 7E B7 //session id 00 06 00 6E 0E
		 * */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.SOE_DISCONNECT);
		dOut.writeInt(client.getSessionID());
		dOut.writeShort(iReason);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildUnCheckAcceptTrade() throws IOException {
		// 01 00
		// 82 43 1E E8 //E81E4382 unaccept transaction message
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.CLIENT_UI_UPDATE);
		dOut.writeInt(Constants.Trade_UnCheckTradeWindowAccept);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildCheckAcceptTrade() throws IOException {
		// 01 00
		// 82 43 1E E8 //E81E4382 unaccept transaction message
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.CLIENT_UI_UPDATE);
		dOut.writeInt(Constants.Trade_CheckTradeWindowAccept);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildUpdateTradeWindowCredits(int amount)
			throws IOException {
		/**
		 * 02 00 E8 7E 52 D1 //give money 39 30 00 00
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.WORLD_UPDATE);
		dOut.writeInt(Constants.Trade_UpdateTradeWindowCredits);
		dOut.writeInt(amount);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildObjectControllerTradeRequestToPlayer(
			TradeObject t) throws IOException {
		/*
		 * 05 00 46 5E CE 80 0B 00 00 00 15 01 00 00 28 12 5C 14 06 00 00 00
		 * ////zelmak receiving 00 00 00 00 01 00 00 00 <----REQUEST CRC 64 EB
		 * 81 0C 26 00 00 00 //estiwe 163418598244 originating 28 12 5C 14 06 00
		 * 00 00 //zel receiving
		 */

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0x0B);
		dOut.writeInt(Constants.StartSecureTrade);
		dOut.writeLong(t.getRecipient().getID());
		dOut.writeInt(0);
		dOut.writeInt(t.getITradeRequestID());
		dOut.writeLong(t.getOriginator().getID());
		dOut.writeLong(t.getRecipient().getID());
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildBeginTradeMessage(Player p) throws IOException {

		/**
		 * 02 00 D8 32 59 32 //325932D8 Begin Trade Message 64 EB 81 0C 26 00 00
		 * 00 //estiwe
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.WORLD_UPDATE);
		dOut.writeInt(Constants.Trade_BeginTradeMessage);
		dOut.writeLong(p.getID());
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildAbortTradeMessage() throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.CLIENT_UI_UPDATE);
		dOut.writeInt(Constants.Trade_AbortTradeMessage);
		dOut.flush();
		return dOut.getBuffer();

	}

	protected static byte[] buildVerifyTradeMessage() throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.CLIENT_UI_UPDATE);
		dOut.writeInt(Constants.Trade_VerifyTradeMessage);
		dOut.flush();
		return dOut.getBuffer();

	}

	protected static byte[] buildTradeCompleteMessage() throws IOException {
		/**
		 * 
		 01 00 8B 03 42 C5 //Trade Complete
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.CLIENT_UI_UPDATE);
		dOut.writeInt(Constants.Trade_TradeCompleteMessage);
		dOut.flush();
		return dOut.getBuffer();

	}

	protected static byte[] buildAddItemToTradeWindow(long lObjectID)
			throws IOException {
		// 02 00
		// 56 13 8D 1E
		// DB 72 7B 38 37 00 00 00
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.WORLD_UPDATE);
		dOut.writeInt(Constants.Trade_ClientAddItemToTradeWindow);
		dOut.writeLong(lObjectID);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDisconnectClient(LoginClient client,
			short iReason) throws IOException {

		/**
		 * 00 05 42 30 7E B7 //session id 00 06 00 6E 0E
		 * */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.SOE_DISCONNECT);
		dOut.writeInt((int) client.getConnectionID());
		dOut.writeShort(iReason);
		dOut.flush();
		return dOut.getBuffer();
	}

	// This must be in a loop, for each CraftingSchematic available to the tool.
	protected static byte[] buildObjectController_DraftSchematicComponentMessage(
			Player player, CraftingSchematic schematic) throws IOException {
		if (schematic == null) {
			return null;
		}
		Vector<CraftingSchematicComponent> vComponents = schematic
				.getComponents();
		if (vComponents == null) {
			return null;
		} else if (vComponents.isEmpty()) {
			return null;
		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.DraftSchematicComponentMessage);
		dOut.writeLong(player.getID());
		dOut.writeInt(0);
		dOut.writeInt(schematic.getCRC());
		dOut.writeInt(schematic.getCRC());
		dOut.writeInt(schematic.getComplexity());
		dOut.writeInt(1); // Size of schematic???
		dOut.writeByte(1); // Unknown.

		if (vComponents != null) {
			if (!vComponents.isEmpty()) {
				dOut.writeInt(vComponents.size());
				for (int i = 0; i < vComponents.size(); i++) {
					CraftingSchematicComponent component = vComponents
							.elementAt(i);
					dOut.writeUTF(component.getSTFFileName());
					dOut.writeInt(0);
					dOut.writeUTF(component.getSTFFileIdentifier());
					dOut.writeBoolean(component.isOptionalComponent());
					// Components only ever take 1 resource or item type...
					dOut.writeInt(1);
					// However, if they took more, than this would be a loop
					// here.
					dOut.writeUTF(component.getSTFFileName());
					dOut.writeInt(0);
					dOut.writeUTF(component.getSTFFileIdentifier());
					dOut.writeUTF16(component.getName());
					dOut.writeByte(component.getComponentRequirementType());
					dOut.writeInt(component.getComponentQuantity());
				}
			} else {
				dOut.writeInt(0);
			}
		} else {
			dOut.writeInt(0);
		}

		// Not entirely sure this is needed.
		dOut.writeShort(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildObjectController_DraftSchematicComponentMessage(
			Player player, CraftingSchematic schematic,
			Vector<CraftingSchematicComponent> vComponents) throws IOException {
		if (schematic == null) {
			return null;
		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.DraftSchematicComponentMessage);
		dOut.writeLong(player.getID());
		dOut.writeInt(0);
		dOut.writeInt(schematic.getCRC());
		dOut.writeInt(schematic.getCRC());
		dOut.writeInt(schematic.getComplexity());
		dOut.writeInt(1); // Size of schematic???
		dOut.writeByte(1); // Unknown.
		if (vComponents != null) {
			if (!vComponents.isEmpty()) {
				dOut.writeInt(vComponents.size());
				for (int i = 0; i < vComponents.size(); i++) {
					CraftingSchematicComponent component = vComponents
							.elementAt(i);
					dOut.writeUTF(component.getSTFFileName());
					dOut.writeInt(0);
					dOut.writeUTF(component.getSTFFileIdentifier());
					dOut.writeBoolean(component.isOptionalComponent());
					// dOut.writeBoolean(true);
					// Components only ever take 1 resource or item type...
					dOut.writeInt(1);
					// However, if they took more, than this would be a loop
					// here.
					dOut.writeUTF(component.getSTFFileName());
					dOut.writeInt(0);
					dOut.writeUTF(component.getSTFFileIdentifier());
					dOut.writeUTF16(component.getName());
					dOut.writeByte(component.getComponentRequirementType());

					dOut.writeInt(component.getComponentQuantity());
				}
			} else {
				dOut.writeInt(0);
				System.out.println("Empty component list!");
			}
		} else {
			dOut.writeInt(0);
			System.out.println("Null component list!");
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildObjectController_CraftingSchematicComponentMessage(
			Player player, ManufacturingSchematic schematic,
			Vector<CraftingSchematicComponent> vComponents,
			TangibleItem craftingTool, TangibleItem itemBeingCrafted,
			boolean bCanMakeFactorySchematic) throws IOException {
		if (schematic == null || craftingTool == null
				|| itemBeingCrafted == null) {
			return null;
		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(11);
		dOut.writeInt(Constants.CraftingSchematicComponenetMessage);
		dOut.writeLong(player.getID());
		dOut.writeInt(0);
		dOut.writeLong(craftingTool.getID());
		dOut.writeLong(schematic.getID());
		dOut.writeLong(itemBeingCrafted.getID());
		dOut.writeInt(1); // Size of schematic???
		dOut.writeBoolean(bCanMakeFactorySchematic); // Unknown.
		if (vComponents != null) {
			if (!vComponents.isEmpty()) {
				dOut.writeInt(vComponents.size());
				for (int i = 0; i < vComponents.size(); i++) {
					CraftingSchematicComponent component = vComponents
							.elementAt(i);
					dOut.writeUTF(component.getSTFFileName());
					dOut.writeInt(0);
					dOut.writeUTF(component.getSTFFileIdentifier());
					dOut.writeBoolean(component.isOptionalComponent());
					// Components only ever take 1 resource or item type...
					dOut.writeInt(1);
					// However, if they took more, than this would be a loop
					// here.
					dOut.writeUTF(component.getSTFFileName());
					dOut.writeInt(0);
					dOut.writeUTF(component.getSTFFileIdentifier());
					dOut.writeUTF16(component.getName());
					dOut.writeByte(component.getComponentRequirementType());
					dOut.writeInt(component.getComponentQuantity());
				}
			} else {
				dOut.writeInt(0);
			}
		} else {
			dOut.writeInt(0);
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	// Did this even exist in PreCU? -<<< yes >>>-
	protected static byte[] buildHeartbeat() throws IOException {
		/**
		 * 01 00 DD 19 FD 42 //42Fd19DD
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.CLIENT_UI_UPDATE);
		dOut.writeInt(0xA16CF9AF);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildObjectController_ResourceWeightsMessage(
			Player player, CraftingSchematic schematic) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0x0B);
		dOut.writeInt(Constants.ResourceWeightMessage);
		dOut.writeLong(player.getID());
		dOut.writeInt(0);
		dOut.writeInt(schematic.getCRC()); // ID
		dOut.writeInt(schematic.getCRC()); // CRC
		CraftingExperimentationAttribute[] expAttributes = schematic
				.getAttributes();
		dOut.writeByte(expAttributes.length);
		// byte oqWeight = 0;
		// byte cdWeight = 0;
		// byte peWeight = 0;
		for (int i = 0; i < expAttributes.length; i++) {
			CraftingExperimentationAttribute attrib = expAttributes[i];
			byte[] expWeight = attrib.getWeightAndTypeBitmask();
			if (expWeight != null) {
				// Hard coded weight playing around with
				/*
				 * oqWeight = (byte)((Constants.RESOURCE_WEIGHT_OVERALL_QUALITY
				 * | 0x1) & 0xFF); cdWeight =
				 * (byte)((Constants.RESOURCE_WEIGHT_CONDUCTIVITY | 0x1) &
				 * 0xFF); peWeight =
				 * (byte)((Constants.RESOURCE_WEIGHT_POTENTIAL_ENERGY | 0x2) &
				 * 0xFF); dOut.writeByte(3); dOut.writeByte(peWeight);
				 * dOut.writeByte(oqWeight); dOut.writeByte(cdWeight);
				 */
				dOut.writeByte(expWeight.length);
				dOut.write(expWeight);
			} else {
				dOut.writeByte(0);
			}
		}

		dOut.writeByte(expAttributes.length);
		for (int i = 0; i < expAttributes.length; i++) {
			CraftingExperimentationAttribute attrib = expAttributes[i];
			byte[] expWeight = attrib.getWeightAndTypeBitmask();
			if (expWeight != null) {
				/*
				 * oqWeight = (byte)((Constants.RESOURCE_WEIGHT_OVERALL_QUALITY
				 * | 0x1) & 0xFF); cdWeight =
				 * (byte)((Constants.RESOURCE_WEIGHT_CONDUCTIVITY | 0x1) &
				 * 0xFF); peWeight =
				 * (byte)((Constants.RESOURCE_WEIGHT_POTENTIAL_ENERGY | 0x2) &
				 * 0xFF); dOut.writeByte(3); dOut.writeByte(peWeight);
				 * dOut.writeByte(oqWeight); dOut.writeByte(cdWeight);
				 */
				dOut.writeByte(expWeight.length);
				dOut.write(expWeight);
			} else {
				dOut.writeByte(0);
			}
		}

		return dOut.getBuffer();
	}

	protected static byte[] buildObjectControllerStartingLocationsWindow(
			Player player, Vector<StartingLocation> vSL) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0x1B);
		dOut.writeInt(Constants.OpenStartingLocationsWindow);
		dOut.writeLong(player.getID());
		dOut.writeInt(0);
		dOut.writeInt(vSL.size());
		for (int i = 0; i < vSL.size(); i++) {
			StartingLocation L = vSL.get(i);
			dOut.writeUTF(L.getCityName());
			dOut.writeUTF(Constants.PlanetNames[L.getPlanetID()].toLowerCase());
			dOut.writeLong(0);
			dOut.writeShort(0);
			dOut.writeUTF(L.getStyleSTF());
			dOut.writeShort(0);
			dOut.writeBoolean(L.isAvailable());
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasMessage_EquippedItem(Player player,
			TangibleItem item, byte updateType, short slotID)
			throws IOException {
		int packetSize = 15;
		byte[] customData = item.getCustomData();
		if (updateType != 0) {
			packetSize += 18;
			if (customData != null) {
				packetSize += customData.length;
			}
		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(player.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_CREO]);
		dOut.writeByte(6);
		dOut.writeInt(packetSize);
		dOut.writeShort(0x01);
		dOut.writeShort(0x0F);
		dOut.writeInt(1);
		dOut.writeInt(player.getEquippedItemUpdateCount(true));

		// If there were more than 1 element to update, this would be in a loop.
		{
			dOut.writeByte(updateType);
			dOut.writeShort(slotID);
			if (updateType != 0) {
				// Need the array index.
				// Is there an array index here?
				if (customData != null) {
					dOut.writeShort(customData.length);
					dOut.write(customData);
				} else {
					dOut.writeShort(0);
				}
				dOut.writeInt(4); // Is equipped
				dOut.writeLong(item.getID());
				dOut.writeInt(item.getCRC());
			}
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildWorldUpdateOpenHarvesterOperatorWindow(
			Structure s) throws IOException {
		/**
		 * 02 00 79 C6 18 BD //BD18C679 30 3E ED 15 36 00 00 00
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.WORLD_UPDATE);
		dOut.writeInt(Constants.ClientOpenHarvesterOperate);
		dOut.writeLong(s.getID());
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildObjectControllerOpenHarvesterOperatorWindow(
			Structure s) throws IOException {
		/**
		 * 05 00 46 5E CE 80 1B 00 00 00 .6......F^...... 2B 02 00 00 //0000022B
		 * 30 3E ED 15 36 00 00 00 00 00 00 00
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0x1B);
		dOut.writeInt(Constants.HarvesterOpenOperatorWindow);
		dOut.writeLong(s.getID());
		dOut.writeInt(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildObjectControllerHarvesterResourceData(
			Player p, Structure s, Vector<SpawnedResourceData> vSRD)
			throws IOException {

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0x0B);
		dOut.writeInt(Constants.HarvesterResourceList);
		dOut.writeLong(p.getID());
		dOut.writeInt(0);
		dOut.writeLong(s.getID());
		if (vSRD == null || vSRD.isEmpty()) {
			dOut.writeInt(0);
		} else {
			dOut.writeInt(vSRD.size());// 0D 00 00 00 //Resource count in this
										// list
			for (int i = 0; i < vSRD.size(); i++) {
				dOut.writeLong(vSRD.get(i).getID());// 58 82 F2 D9 35 00 00 00
													// //resource id
				dOut.writeUTF(vSRD.get(i).getName());// 05 00 45 61 64 6F 70
														// //Eadop //resource
														// name
				dOut.writeUTF(vSRD.get(i).getIffFileName());// 11 00 72 61 64 69
															// 6F 61 63 74 69 76
															// 65 5F 74 79 70 65
															// 37
															// //radioactive_type7
															// // resource type
				dOut.writeByte((byte) (vSRD.get(i).getBestDensityAtLocation(s
						.getX(), s.getY())));// 09 //concentration
			}
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildObjectControllerDequeueRetrieveHarvesterResource(
			Player p, boolean success, byte reqID, byte err) throws IOException {

		/**
		 * 05 00 46 5E CE 80 <--ObjControllerMessage 0B 00 00 00 <-- EE 00 00 00
		 * <--dequeueRetrieveHarvesterResource A6 FF 7C 4D 09 00 00 00 <--Player
		 * ID 00 00 00 00 ED 00 00 00 <--enqueueRetrieveHarvesterResource 01
		 * <--Retrieved Bool??? 01 <--REQ ID
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0x0B);
		dOut.writeInt(Constants.dequeueRetrieveHarvesterResource);
		dOut.writeLong(p.getID());
		dOut.writeInt(0);
		dOut.writeInt(Constants.enqueueRetrieveHarvesterResource);
		dOut.writeBoolean(success);
		dOut.writeByte(reqID);
		dOut.flush();
		byte[] b = dOut.getBuffer();
		return b;
	}

	protected static byte[] buildWorldUpdateMessage(int iUpdateID,
			int iUpdateValue) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.WORLD_UPDATE);
		dOut.writeInt(iUpdateID);
		dOut.writeInt(iUpdateValue);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected final static byte[] buildObjectControllerSitOnObject(SOEObject o)
			throws IOException {

		/**
		 * 05 00 46 5E CE 80 1B 00 00 00 3B 01 00 00 ....F^......;... 17 46 ED
		 * 7F 11 00 00 00 <---PlayerID 00 00 00 00 98 76 1C 00 00 00 00 00
		 * <--CELL ID 90 A0 7E C1 <--X CD CC CC 3F <--Z E3 C7 93 C1 <--Y
		 * ......~....?....
		 */
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0X1B);
		dOut.writeInt(Constants.SitOnObject);
		dOut.writeLong(o.getID());
		dOut.writeInt(0);
		dOut.writeLong(o.getCellID());
		dOut.writeFloat(o.getCellX());
		dOut.writeFloat(o.getCellZ());
		dOut.writeFloat(o.getCellY());
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasMessageMSCO7IntArray(short updateCount,
			short updateOperand, ManufacturingSchematic o, short element,
			int newValue, byte updateStatus) throws IOException {
		int vIDUpdateCounter = 0;
		switch (updateOperand) {
		case 1: {
			vIDUpdateCounter = o.getVID1IntArrayUpdateCount(true);
			break;
		}
		case 3: {
			vIDUpdateCounter = o.getSlotResourceQuantityUpdateCount(true);
			break;
		}

		case 4: {
			vIDUpdateCounter = o.getVID4IntArrayUpdateCount(true);
			break;
		}
		case 5: {
			vIDUpdateCounter = o.getVID5IntArrayUpdateCount(true);
			break;
		}
		case 6: {
			vIDUpdateCounter = o.getVID6IntArrayUpdateCount(true);
			break;
		}

		case 9: {
			vIDUpdateCounter = o
					.getVID9CurrentExperimentalValueUpdateCount(true);
			break;
		}
		case 10: {
			vIDUpdateCounter = o.getVID10FloatArrayUpdateCount(true);
			break;
		}
		case 11: {
			vIDUpdateCounter = o.getVID11FloatArrayUpdateCount(true);
			break;
		}
		case 12: {
			vIDUpdateCounter = o
					.getVID12MaxExperimentationArrayUpdateCount(true);
			break;
		}
		case 13: {
			vIDUpdateCounter = o.getVID13StringArrayUpdateCount(true);
			break;
		}
		case 14: {
			vIDUpdateCounter = o.getVID14IntArrayUpdateCount(true);
			break;
		}
		case 15: {
			vIDUpdateCounter = o.getVID15IntArrayUpdateCount(true);
			break;
		}
		case 16: {
			vIDUpdateCounter = o.getVID16IntArrayUpdateCount(true);

			break;
		}
		case 19: {
			vIDUpdateCounter = o.getVID19IntArrayUpdateCount(true);
			break;
		}

		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		int packetSize = 19;
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(o.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MSCO]);
		dOut.writeByte(7);
		dOut.writeInt(packetSize);
		dOut.writeShort(updateCount);
		dOut.writeShort(updateOperand);
		dOut.writeInt(1);
		dOut.writeInt(vIDUpdateCounter);
		dOut.writeByte(updateStatus);
		dOut.writeShort(element);
		dOut.writeInt(newValue);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasMessageMSCO7FloatArray(
			short updateCount, short updateOperand, ManufacturingSchematic o,
			short element, float newValue, byte updateStatus)
			throws IOException {
		int vIDUpdateCounter = 0;
		switch (updateOperand) {
		case 1: {
			vIDUpdateCounter = o.getVID1IntArrayUpdateCount(true);
			break;
		}
		case 3: {
			vIDUpdateCounter = o.getSlotResourceQuantityUpdateCount(true);
			break;
		}

		case 4: {
			vIDUpdateCounter = o.getVID4IntArrayUpdateCount(true);
			break;
		}
		case 5: {
			vIDUpdateCounter = o.getVID5IntArrayUpdateCount(true);
			break;
		}
		case 6: {
			vIDUpdateCounter = o.getVID6IntArrayUpdateCount(true);
			break;
		}

		case 9: {
			vIDUpdateCounter = o
					.getVID9CurrentExperimentalValueUpdateCount(true);
			break;
		}
		case 10: {
			vIDUpdateCounter = o.getVID10FloatArrayUpdateCount(true);
			break;
		}
		case 11: {
			vIDUpdateCounter = o.getVID11FloatArrayUpdateCount(true);
			break;
		}
		case 12: {
			vIDUpdateCounter = o
					.getVID12MaxExperimentationArrayUpdateCount(true);
			break;
		}
		case 13: {
			vIDUpdateCounter = o.getVID13StringArrayUpdateCount(true);
			break;
		}
		case 14: {
			vIDUpdateCounter = o.getVID14IntArrayUpdateCount(true);
			break;
		}
		case 15: {
			vIDUpdateCounter = o.getVID15IntArrayUpdateCount(true);
			break;
		}
		case 16: {
			vIDUpdateCounter = o.getVID16IntArrayUpdateCount(true);

			break;
		}
		case 19: {
			vIDUpdateCounter = o.getVID19IntArrayUpdateCount(true);
			break;
		}

		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		int packetSize = 19;
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(o.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MSCO]);
		dOut.writeByte(7);
		dOut.writeInt(packetSize);
		dOut.writeShort(updateCount);
		dOut.writeShort(updateOperand);
		dOut.writeInt(1);
		dOut.writeInt(vIDUpdateCounter);
		dOut.writeByte(updateStatus);
		dOut.writeShort(element);
		dOut.writeFloat(newValue);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasMessageMSCO7StringArray(
			short updateCount, short updateOperand, ManufacturingSchematic o,
			short element, String newValue, byte updateStatus)
			throws IOException {
		int vIDUpdateCounter = o.getVID13StringArrayUpdateCount(true);
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		int packetSize = 17 + newValue.length();
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(o.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MSCO]);
		dOut.writeByte(7);
		dOut.writeInt(packetSize);
		dOut.writeShort(updateCount);
		dOut.writeShort(updateOperand);
		dOut.writeInt(1);
		dOut.writeInt(vIDUpdateCounter);
		dOut.writeByte(updateStatus);
		dOut.writeShort(element);
		dOut.writeUTF(newValue);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasMSCO7IngredientQuantityInSlot(
			ManufacturingSchematic schematic, short slotID, int quantity,
			byte updateType) throws IOException {
		int packetSize = 19;
		if (quantity > 0) {
			packetSize += 4;
		}
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(schematic.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MSCO]);
		dOut.writeByte(7);
		dOut.writeInt(packetSize);
		dOut.writeShort(1);
		dOut.writeShort(3);
		dOut.writeInt(1);
		dOut.writeInt(schematic.getSlotResourceQuantityUpdateCount(true));
		dOut.writeByte(updateType);
		dOut.writeShort(slotID);
		if (quantity > 0) {
			dOut.writeInt(1); // Num ingredients in this slot
			dOut.writeInt(quantity);
		} else {
			dOut.writeInt(0);
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasMSCO7ListObjectIDInSlot(
			ManufacturingSchematic schematic, int arrayIndex, long[] vObjectIDs)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		int numObjectIDsActuallyExist = 0;
		boolean bEnded = false;
		for (int i = 0; i < vObjectIDs.length && !bEnded; i++) {
			if (vObjectIDs[i] == 0) {
				numObjectIDsActuallyExist = i;
				bEnded = true;
			}
		}
		if (!bEnded) {
			numObjectIDsActuallyExist = vObjectIDs.length;
		}
		int packetSize = 19 + (numObjectIDsActuallyExist * 8);
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(schematic.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MSCO]);
		dOut.writeByte(7);
		dOut.writeInt(packetSize);
		dOut.writeShort(1); // Num updates
		dOut.writeShort(2); // Update variable

		dOut.writeInt(1); // Num updates to this variable;
		dOut.writeInt(schematic.getObjectIdBySlotUpdateCount(true));

		dOut.writeByte(2);
		dOut.writeShort(arrayIndex);
		dOut.writeInt(numObjectIDsActuallyExist);
		for (int i = 0; i < numObjectIDsActuallyExist; i++) {
			dOut.writeLong(vObjectIDs[i]);
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	/*protected static byte[] buildDeltasMSCO7FirstTimeAddIngredient(
			ManufacturingSchematic schematic, short slotID) throws IOException {
		short vIDUpates = 9;

		// vID 0
		CraftingSchematicComponent[] components = schematic
				.getSchematicComponentData();

		// vID 1
		int[] vID1 = schematic.getVID1IntArray();

		// vID 2
		long[][] vObjectIDListBySlotID = schematic.getAllObjectIDsInSlots();
		// We need to track not only if any slots have object IDs but which ones
		// have how many, at transmission of tihs baseline.
		int[] numObjectIDsBySlot = new int[vObjectIDListBySlotID.length];
		int numSlotsHaveObjectIDs = 0;
		int numObjectIDsInSlots = 0;
		if (vObjectIDListBySlotID != null) {
			// Need to first see if ANY ID in here is valid
			// packetSize += (4 * vObjectIDListBySlotID.length);
			// If it's not empty, there must be 1 value in it.
			for (int i = 0; i < vObjectIDListBySlotID.length; i++) {
				if (vObjectIDListBySlotID[i] != null) {
					for (int j = 0; j < vObjectIDListBySlotID[i].length; j++) {
						if (vObjectIDListBySlotID[i][j] != 0) {
							numObjectIDsInSlots++;

						}
					}
				}
				if (numObjectIDsInSlots > 0) {
					numSlotsHaveObjectIDs++;
					numObjectIDsBySlot[i] = numObjectIDsInSlots;
				}
				numObjectIDsInSlots = 0;
			}
		}

		// vID 3
		int[] vIngredientQuantitiesInSlots = schematic
				.getSlotResourceQuantityInserted();
		// vID 4
		int[] vID4 = schematic.getVID4IntArray();
		// vID 5
		int[] vID5 = schematic.getVID5IntArray();
		// vID 6
		int[] vID6 = schematic.getVID6IntArray();

		// vID 7
		byte vID7 = schematic.getVID7();

		// vID 8
		CraftingExperimentationAttribute[] vAttributes = schematic
				.getExperimentalAttributes();
		final String crafting = "crafting";
		int numActualAttributes = 0;

		if (vAttributes != null) {
			for (int i = 0; i < vAttributes.length; i++) {
				if (vAttributes[i] != null) {
					String stfFileIdentifier = vAttributes[i]
							.getStfFileIdentifier();
					if (stfFileIdentifier != null) {
						numActualAttributes++;
					}
				}
			}
		}
		if (true)
			numActualAttributes = 0;

		if (numActualAttributes > 0) {
			vIDUpates = 14;
		}

		// vID 9
		float[] vID9 = schematic.getVID9CurrentExperimentalValueArray();

		// vID 10
		float[] vID10 = schematic.getVID10FloatArray();
		// vID 11
		float[] vID11 = schematic.getVID11FloatArray();

		// vID 12
		float[] vID12 = schematic.getVID12CurrentExperimentationArray();

		// vID 20
		byte vID20 = schematic.getVID20();

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(schematic.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MSCO]);
		dOut.writeByte(7);

		SOEOutputStream buff = new SOEOutputStream(new ByteArrayOutputStream());

		// dOut.writeInt(packetSize);

		buff.writeShort(vIDUpates); // num Updates
		int numUpdateThisVID = 0;
		int updateCount = 0;
		// vID 0
		buff.writeShort(0);
		if (components != null) {
			for (int i = 0; i < components.length && components[i] != null; i++) {
				numUpdateThisVID++;
			}

			buff.writeInt(components.length);
			buff.writeInt(numUpdateThisVID);
			schematic.setSchematicComponentDataUpdateCount(numUpdateThisVID);
			for (short i = 0; i < components.length; i++) {
				buff.writeByte(1);
				buff.writeShort(i);
				buff.writeUTF(components[i].getSTFFileName());
				buff.writeInt(0);
				buff.writeUTF(components[i].getSTFFileIdentifier());
			}
		} else {
			buff.writeLong(0);
		}
		numUpdateThisVID = 0;
		// vID 1
		buff.writeShort(1);
		if (vID1 != null) {
			for (int i = 0; i < vID1.length && vID1[i] != 0; i++) {
				numUpdateThisVID++;
			}
			updateCount = vID1.length + numUpdateThisVID;
			buff.writeInt(updateCount);
			schematic.setVID1IntArrayUpdateCount(updateCount);
			buff.writeInt(updateCount);
			for (short i = 0; i < vID1.length; i++) {
				buff.writeByte(1);
				buff.writeShort(i);
				buff.writeInt(0);
			}
			for (short i = 0; i < numUpdateThisVID; i++) {
				buff.writeByte(2);
				buff.writeShort(i);
				buff.writeInt(vID1[i]);
			}
		} else {
			buff.writeLong(0);
		}

		// vID 2 -- Object ID list
		// System.out.println("MSCO 3 Delta First Time Message OPERAND 2 UPDATE");
		buff.writeShort(2);
		if (vObjectIDListBySlotID != null) {
			// numSlotsHaveObjectIDs + vObjectIDListBySlotID.length is our
			// number of updates here.
			numUpdateThisVID = vObjectIDListBySlotID.length
					+ numSlotsHaveObjectIDs;
			schematic.setObjectIdBySlotUpdateCount(numUpdateThisVID);
			buff.writeInt(numUpdateThisVID);
			buff.writeInt(numUpdateThisVID);

			for (int i = 0; i < vObjectIDListBySlotID.length; i++) {
				buff.writeByte(1);
				buff.writeShort(i);
				buff.writeInt(0);

			}
			for (int i = 0; i < numObjectIDsBySlot.length; i++) {
				if (numObjectIDsBySlot[i] != 0) {
					buff.writeByte(2);
					buff.writeShort(i);
					buff.writeInt(numObjectIDsBySlot[i]);
					for (int j = 0; j < numObjectIDsBySlot[i]; j++) {
						// System.out.println("Object IDS in the slot: " +
						// vObjectIDListBySlotID[i][j]);
						buff.writeLong(vObjectIDListBySlotID[i][j]);
					}
				}
			}
		} else {
			// this is really two ints set to 0 but i guess one long would be
			// same
			// System.out.println("Slot Count and updates: 0");
			buff.writeLong(0);
		}
		// System.out.println("-------DONE MSCO 3 Delta First Time ID2 Update-------------");
		// vID 3
		buff.writeShort(3);
		if (vIngredientQuantitiesInSlots != null) {
			// numSlotsHaveObjectIDs + vObjectIDListBySlotID.length is our
			// number of updates here.
			numUpdateThisVID = vIngredientQuantitiesInSlots.length
					+ numSlotsHaveObjectIDs;
			schematic.setSlotResourceQuantityUpdateCount(numUpdateThisVID);
			buff.writeInt(numUpdateThisVID);
			buff.writeInt(numUpdateThisVID);

			for (int i = 0; i < vObjectIDListBySlotID.length; i++) {
				buff.writeByte(1);
				buff.writeShort(i);
				buff.writeInt(0);
			}
			for (int i = 0; i < numObjectIDsBySlot.length; i++) {
				if (numObjectIDsBySlot[i] != 0) {
					buff.writeByte(2);
					buff.writeShort(i);
					buff.writeInt(numObjectIDsBySlot[i]);
					for (int j = 0; j < numObjectIDsBySlot[i]; j++) {
						// System.out.println("Object IDS in the slot: " +
						// vObjectIDListBySlotID[i][j]);
						buff.writeInt(vIngredientQuantitiesInSlots[i]);
					}
				}
			}
		} else {
			// this is really two ints set to 0 but i guess one long would be
			// same
			// System.out.println("Slot Count and updates: 0");
			buff.writeLong(0);
		}

		// vID 4
		buff.writeShort(4);
		numUpdateThisVID = 0;
		if (vID4 != null) {
			for (int i = 0; i < vID4.length && vID4[i] != 0; i++) {
				numUpdateThisVID++;
			}
			updateCount = vID4.length + numUpdateThisVID;
			buff.writeInt(updateCount);
			schematic.setVID4IntArrayUpdateCount(updateCount);
			buff.writeInt(updateCount);
			for (short i = 0; i < vID4.length; i++) {
				buff.writeByte(1);
				buff.writeShort(i);
				buff.writeInt(0);
			}
			for (short i = 0; i < numUpdateThisVID; i++) {
				buff.writeByte(2);
				buff.writeShort(i);
				buff.writeInt(vID4[i]);
			}
		} else {
			buff.writeLong(0);
		}

		// vID 5
		buff.writeShort(5);
		numUpdateThisVID = 0;
		if (vID5 != null) {

			updateCount = vID5.length + 1;
			buff.writeInt(updateCount);
			schematic.setVID5IntArrayUpdateCount(updateCount);
			buff.writeInt(updateCount);
			for (short i = 0; i < vID5.length; i++) {
				buff.writeByte(1);
				buff.writeShort(i);
				buff.writeInt(-1);
			}
			buff.writeByte(2);
			buff.writeShort(slotID);
			buff.writeInt(vID5[slotID]);
		} else {
			buff.writeLong(0);
		}

		// vID 6
		buff.writeShort(6);
		numUpdateThisVID = vID6.length * 2;
		if (vID6 != null) {
			buff.writeInt(numUpdateThisVID);
			buff.writeInt(numUpdateThisVID);
			schematic.setVID6IntArrayUpdateCount(numUpdateThisVID);
			for (int i = 0; i < vID6.length; i++) {
				buff.writeByte(1);
				buff.writeShort(i);
				buff.writeInt(-1);
				buff.writeByte(2);
				buff.writeShort(i);
				buff.writeInt(i);
			}
		} else {
			buff.writeLong(0);
		}

		buff.writeShort(7);
		buff.writeByte(vID7); // no idea vID 7

		// vID 8
		if (numActualAttributes > 0) {

			buff.writeShort(8);
			buff.writeInt(numActualAttributes);
			buff.writeInt(numActualAttributes);
			schematic.setExperimentalAttributesUpdateCount(numActualAttributes);
			for (int i = 0; i < vAttributes.length; i++) {
				if (vAttributes[i] != null) {
					String title = vAttributes[i].getStfFileIdentifier();
					if (title != null) {
						System.out.println("FirstTimeAddIngredient -- writing "
								+ crafting + ":" + title + " at index " + i);
						buff.writeByte(1);
						buff.writeShort(i);// <--Components begin at 1
						buff.writeUTF(crafting); // I think this is always
													// "crafting"
						buff.writeInt(0);
						buff.writeUTF(title);
					}
				}
			}

			// vID 9
			buff.writeShort(9);
			if (vID9 != null) {
				buff.writeInt(vID9.length);
				buff.writeInt(vID9.length);
				schematic
						.setVID9CurrentExperimentalValueUpdateCount(vID9.length);
				for (int i = 0; i < vID9.length; i++) {
					buff.writeByte(1);
					buff.writeShort(i);
					buff.writeFloat(vID9[i]);
				}
			} else {
				buff.writeLong(0);
			}

			// vID 10
			buff.writeShort(10);
			if (vID10 != null) {
				buff.writeInt(vID10.length);
				buff.writeInt(schematic.getVID10FloatArrayUpdateCount(true));
				schematic.setVID10FloatArrayUpdateCount(vID10.length);
				for (int i = 0; i < vID10.length; i++) {
					buff.writeByte(1);
					buff.writeShort(i);
					buff.writeFloat(vID10[i]);
				}
			} else {
				buff.writeLong(0);
			}

			// vID 11
			buff.writeShort(11);
			if (vID11 != null) {
				buff.writeInt(vID11.length);
				buff.writeInt(schematic.getVID11FloatArrayUpdateCount(true));
				schematic.setVID11FloatArrayUpdateCount(vID11.length);
				for (int i = 0; i < vID11.length; i++) {
					buff.writeByte(1);
					buff.writeShort(i);
					buff.writeFloat(vID11[i]);
				}
			} else {
				buff.writeLong(0);
			}
			// vID 12
			buff.writeShort(12);
			if (vID12 != null) {
				buff.writeInt(vID12.length);
				buff.writeInt(schematic
						.getVID12MaxExperimentationArrayUpdateCount(true));
				schematic
						.setVID12CurrentExperimentationArrayUpdateCount(vID12.length);
				for (int i = 0; i < vID12.length; i++) {
					buff.writeByte(1);
					buff.writeShort(i);
					buff.writeFloat(vID12[i]);
				}
			} else {
				buff.writeLong(0);
			}

		}

		buff.writeShort(20);// 14 DOH!
		buff.writeByte(vID20);

		buff.flush();

		byte[] packet = buff.getBuffer();
		dOut.writeInt(packet.length);
		dOut.write(packet);
		dOut.flush();
		return dOut.getBuffer();

	}*/

	/*protected static byte[] buildDeltasMSCO7FirstTimeSetExperimentation(
			ManufacturingSchematic schematic) throws IOException {
		// These arrays must all be the same length.
		CraftingExperimentationAttribute[] vExperimentalAttributes = schematic
				.getExperimentalAttributes();
		float[] vID9CurrentExperimentationArray = schematic
				.getVID9CurrentExperimentalValueArray();
		float[] vID10FloatArray = schematic.getVID10FloatArray(); // This is an
																	// unknown
																	// array.
		float[] vID11FloatArray = schematic.getVID11FloatArray(); // This also
																	// is an
																	// unknown
																	// float
																	// array.
		float[] vID12MaxExperimentalValues = schematic
				.getVID12CurrentExperimentationArray();
		// TODO: Customization options.
		// Currently, we will tell the system that there are no customization
		// options available.

		// These arrays probably must all be the same length???
		String[] vID13CustomizationStrings = new String[1];
		vID13CustomizationStrings[0] = null;
		int[] vID14CustomizationCursorPosition = new int[1];
		int[] vID15CustomizationUnknownValue = new int[1];
		int[] vID16NumberOfPaletteColors = new int[1];

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		SOEOutputStream buff = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(schematic.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MSCO]);
		dOut.writeByte(7);

		// Packet size

		// Number of updates.
		// buff.writeShort(11);
		if (false) {
			buff.writeShort(11);
		} else {
			buff.writeShort(10);
		}

		// vID 8 -- Experimental Attributes
		if (false) {
			int iExperimentalAttributeUpdateCount = schematic
					.getExperimentalAttributesUpdateCount(false)
					+ vExperimentalAttributes.length;

			schematic
					.setExperimentalAttributesUpdateCount(iExperimentalAttributeUpdateCount);
			buff.writeShort(8);
			buff.writeInt(vExperimentalAttributes.length);
			buff.writeInt(iExperimentalAttributeUpdateCount);
			// System.out.println("MSCO7 set experimentation:  attributes length "
			// + vExperimentalAttributes.length + ", update counter " +
			// iExperimentalAttributeUpdateCount);
			for (int i = 0; i < vExperimentalAttributes.length; i++) {
				buff.writeByte(1);
				buff.writeShort(i);
				CraftingExperimentationAttribute thisAttrib = vExperimentalAttributes[i];
				System.out.println("Writing experimental attribute " + i
						+ " -- it is @" + thisAttrib.getStfFileName() + ":"
						+ thisAttrib.getStfFileIdentifier());
				buff.writeUTF(thisAttrib.getStfFileName());
				buff.writeInt(0);
				buff.writeUTF(thisAttrib.getStfFileIdentifier());
			}
		}

		// vID 9 -- Current Experimental Values.
		{
			int iCurrentExperimentationUpdateCount = schematic
					.getVID9CurrentExperimentalValueUpdateCount(false)
					+ (vID9CurrentExperimentationArray.length * 2);
			schematic
					.setVID9CurrentExperimentalValueUpdateCount(iCurrentExperimentationUpdateCount);
			buff.writeShort(9);
			buff.writeInt(vID9CurrentExperimentationArray.length * 2);
			buff.writeInt(iCurrentExperimentationUpdateCount);

			for (int i = 0; i < vID9CurrentExperimentationArray.length; i++) {
				buff.writeByte(1);
				buff.writeShort(i);
				buff.writeFloat(0);
			}
			for (int i = 0; i < vID9CurrentExperimentationArray.length; i++) {
				buff.writeByte(2);
				buff.writeShort(i);
				buff.writeFloat(vID9CurrentExperimentationArray[i]);
			}
		}

		// vID 10 -- Unknown array which appears to be simply initialized to 0.
		{
			int vID10UpdateCount = schematic
					.getVID10FloatArrayUpdateCount(false)
					+ vID10FloatArray.length;
			schematic.setVID10FloatArrayUpdateCount(vID10UpdateCount);
			buff.writeShort(10);
			buff.writeInt(vID10FloatArray.length);
			buff.writeInt(vID10UpdateCount);
			for (int i = 0; i < vID10FloatArray.length; i++) {
				buff.writeByte(1);
				buff.writeShort(i);
				buff.writeFloat(0);
			}
		}

		// vID 11 -- Unknown array which is initialized to 0, then set to 1.
		{
			int vID11UpdateCount = schematic
					.getVID11FloatArrayUpdateCount(false)
					+ (vID11FloatArray.length * 2);
			schematic.setVID11FloatArrayUpdateCount(vID11UpdateCount);
			buff.writeShort(11);
			buff.writeInt(vID11FloatArray.length * 2);
			buff.writeInt(vID11UpdateCount);
			for (int i = 0; i < vID11FloatArray.length; i++) {
				buff.writeByte(1);
				buff.writeShort(i);
				buff.writeFloat(0);
			}
			for (int i = 0; i < vID11FloatArray.length; i++) {
				buff.writeByte(2);
				buff.writeShort(i);
				// float valueToWrite = SWGGui.getRandomInt(5, 10) / 10.0f;
				// buff.writeFloat(valueToWrite);
				buff.writeFloat(1.0f); // WTF is this variable
			}
		}

		// vID 12 -- Max experimentation values for each attribute.
		{
			int vID12MaxExperimentationUpdateCount = schematic
					.getVID12MaxExperimentationArrayUpdateCount(false)
					+ (vID12MaxExperimentalValues.length * 2);
			schematic
					.setVID12CurrentExperimentationArrayUpdateCount(vID12MaxExperimentationUpdateCount);
			buff.writeShort(12);
			buff.writeInt(vID12MaxExperimentalValues.length * 2);
			buff.writeInt(vID12MaxExperimentationUpdateCount);
			for (int i = 0; i < vID12MaxExperimentalValues.length; i++) {
				buff.writeByte(1);
				buff.writeShort(i);
				buff.writeFloat(0);
			}

			for (int i = 0; i < vID12MaxExperimentalValues.length; i++) {
				buff.writeByte(2);
				buff.writeShort(i);
				// float valueToWrite = SWGGui.getRandomInt(5, 10) / 10.0f;
				// buff.writeFloat(valueToWrite); // Is it this one?
				System.out.println("Writing max experimentation value of "
						+ vID12MaxExperimentalValues[i] + " for index " + i);
				buff.writeFloat(vID12MaxExperimentalValues[i]);
			}
		}

		// vID 13 -- Experimentation string values.
		{
			int vID13CustomizationOptionUpdateCount = schematic
					.getVID13StringArrayUpdateCount(false)
					+ vID13CustomizationStrings.length;
			schematic
					.setVID13StringArrayUpdateCount(vID13CustomizationOptionUpdateCount);
			buff.writeShort(13);
			buff.writeInt(vID13CustomizationStrings.length);
			buff.writeInt(vID13CustomizationOptionUpdateCount);
			for (int i = 0; i < vID13CustomizationStrings.length; i++) {
				buff.writeByte(1);
				buff.writeShort(i);
				buff.writeUTF(null);
			}
		}

		// vID 14 -- Color palette cursor position?
		{
			int vID14UpdateCount = schematic.getVID14IntArrayUpdateCount(false)
					+ vID14CustomizationCursorPosition.length;
			schematic.setVID14IntArrayUpdateCount(vID14UpdateCount);
			buff.writeShort(14);
			buff.writeInt(vID14CustomizationCursorPosition.length);
			buff.writeInt(vID14UpdateCount);
			for (int i = 0; i < vID14CustomizationCursorPosition.length; i++) {
				buff.writeByte(1);
				buff.writeShort(i);
				buff.writeInt(0);
			}
		}

		// vID 15 -- Unknown.
		{
			int vID15UpdateCount = schematic.getVID15IntArrayUpdateCount(false)
					+ vID15CustomizationUnknownValue.length;
			schematic.setVID15IntArrayUpdateCount(vID15UpdateCount);
			buff.writeShort(15);
			buff.writeInt(vID15CustomizationUnknownValue.length);
			buff.writeInt(vID15UpdateCount);
			for (int i = 0; i < vID15CustomizationUnknownValue.length; i++) {
				buff.writeByte(1);
				buff.writeShort(i);
				buff.writeInt(0);
			}
		}

		// vID 16 -- Number of palette colours?
		{
			int vID16PaletteColorUpdateCount = schematic
					.getVID16IntArrayUpdateCount(false)
					+ vID16NumberOfPaletteColors.length;
			schematic.setVID16IntArrayUpdateCount(vID16PaletteColorUpdateCount);
			buff.writeShort(16);
			buff.writeInt(vID16NumberOfPaletteColors.length);
			buff.writeInt(vID16PaletteColorUpdateCount);
			for (int i = 0; i < vID16NumberOfPaletteColors.length; i++) {
				buff.writeByte(1);
				buff.writeShort(i);
				buff.writeInt(0);
			}
		}

		// vID 17 -- Unknown
		schematic.setVID17((byte) 1, false);
		buff.writeShort(17);
		buff.writeByte(schematic.getVID17());

		// schematic.setVID18(0.27275002f, false); -- Moderate success
		schematic.setVID18(1.0f, false); // Or is it this one?
		buff.writeShort(18);
		buff.writeFloat(schematic.getVID18());

		buff.flush();
		byte[] tail = buff.getBuffer();
		dOut.writeInt(tail.length);
		dOut.write(tail);
		dOut.flush();
		return dOut.getBuffer();
	}*/

	protected static byte[] buildDequeueGenericObjectControllerMessage010C(
			long playerID, int objControllerType, int unknownVar,
			byte updateCount) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0x0B);
		dOut.writeInt(Constants.DequeueGenericObjController010C);
		dOut.writeLong(playerID);
		dOut.writeInt(0);
		dOut.writeInt(objControllerType);
		dOut.writeInt(unknownVar);
		dOut.writeByte(updateCount);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildCraftingAssemblySuccessRating(long playerID,
			int objControllerType, int successRating, byte updateCount)
			throws IOException {

		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0x0B);
		dOut.writeInt(Constants.DequeueCraftingAssemblySuccessRating);
		dOut.writeLong(playerID);
		dOut.writeInt(0);
		dOut.writeInt(objControllerType);
		dOut.writeInt(successRating);
		dOut.writeByte(updateCount);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDequeueExperimentationMessage(long playerID,
			int successRating, byte updateCount) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0x0B);
		dOut.writeInt(Constants.DequeueExperimentationMessage);
		dOut.writeLong(playerID);
		dOut.writeInt(0);
		dOut.writeInt(Constants.CraftingExperimentationMessage);
		dOut.writeInt(successRating);
		dOut.writeByte(updateCount);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDequeueCreatePrototype(long playerID)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0x0B);
		dOut.writeInt(Constants.DequeueCreatePrototype);
		dOut.writeLong(playerID);
		dOut.writeInt(0);
		dOut.writeByte(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildObjectControllerMessageImageDesignStart(
			Player designer, Player customer) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0x0B);
		dOut.writeInt(Constants.ImageDesignStart);
		dOut.writeLong(designer.getID());
		dOut.writeInt(0);
		dOut.writeLong(designer.getID());// Possible Designer
		dOut.writeLong(customer.getID());// possible customer
		dOut.writeLong(0);
		dOut.writeShort(0);
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildObjectControllerMessageBuffStat(Player player,
			int iBuffCRC, float fPeriod) throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.ObjControllerMessage);
		dOut.writeInt(0x1B);
		dOut.writeInt(Constants.BuffStats);
		dOut.writeLong(player.getID());
		dOut.writeInt(0);
		dOut.writeInt(iBuffCRC);// buff CRC
		dOut.writeFloat(fPeriod);// duration
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeleteCharacterResponse(boolean bDeleted)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.WORLD_UPDATE);
		dOut.writeInt(Constants.ClientDeleteCharacterResponse);
		if (bDeleted) {
			dOut.writeInt(0);
		} else {
			dOut.writeInt(1);
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	protected static byte[] buildDeltasMSCO3SchematicAttribute(
			ManufacturingSchematic schematic, int numUpdates, byte updateType)
			throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
		dOut.setOpcode(Constants.SOE_CHL_DATA_A);
		dOut.setSequence(0);
		dOut.setUpdateType(Constants.OBJECT_UPDATE);
		dOut.writeInt(Constants.DeltasMessage);
		dOut.writeLong(schematic.getID());
		dOut.writeInt(Constants.BaselinesTypes[Constants.BASELINES_MSCO]);
		dOut.writeByte(3);

		// Packet size.

		SOEOutputStream buff = new SOEOutputStream(new ByteArrayOutputStream());
		buff.writeShort(1);
		buff.writeShort(5);
		buff.writeInt(numUpdates);
		int updateCount = schematic.getSchematicAttributeUpdateCount(true)
				+ numUpdates - 1;
		buff.writeInt(updateCount);
		schematic.setSchematicAttributeUpdateCount(updateCount);
		Vector<ManufacturingSchematicAttribute> vAttribs = schematic
				.getSchematicAttributes();

		for (int i = 0; i < numUpdates; i++) {
			ManufacturingSchematicAttribute attrib = vAttribs.elementAt(i);
			buff.writeByte(updateType);
			buff.writeUTF(attrib.getAttributeName());
			buff.writeInt(0);
			buff.writeUTF(attrib.getAttributeType());
			buff.writeFloat(attrib.getAttributeValue());
		}
		buff.flush();
		byte[] tail = buff.getBuffer();
		dOut.writeInt(tail.length);
		dOut.write(tail);
		dOut.flush();
		return dOut.getBuffer();
	}

}
