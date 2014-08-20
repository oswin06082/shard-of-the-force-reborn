import java.io.IOException;
import java.util.Vector;
import java.io.ByteArrayOutputStream;

/**
 * This is the SUI Window Class. This class will build SUI Windows based on the
 * different constructors. Only the Constructors and the Window Build Functions
 * for each window type.
 * 
 * @author Tomas Cruz
 */
public class SUIWindow {

	private int iWindowID;
	// private TreeSet<String> vOptions;
	private ZoneClient client;
	private SOEObject OriginatingObject;
	// private SOEObject InputTextObject;
	private Player pOwnerOfWindow;
	private boolean bWindowTypeSet = false;
	private boolean bWindowHeaderSet = false;
	private boolean bWindowIDSet = false;
	private boolean bWindowTypeStringSet = false;
	private boolean bWindowPromptSet = false;
	private boolean bWindowHasCancelButton = false;
	private boolean bWindowHasOkButton = false;
	private boolean bWindowHasRevertButton = false;
	private boolean bWindowHasFooter = false;
	private boolean bWindowHasDataListHeader = false;
	private boolean bWindowHasDataListPrompt = false;
	private boolean bWindowHasDataList = false;
	private boolean bWindowHasDataListItems = false;

	private boolean bTransferBoxHasHeader = false;
	private boolean bTransferBoxHasContent = false;

	private boolean bWindowHasInputBoxHeader = false;
	private boolean bWindowHasInputFields = false;
	private byte iWindowType;

	private Vector<byte[]> PacketComponents;

	private int iNumberOfUpdates = 0;

	private String[] sParams;

	public String[] getListContents() {
		return sParams;
	}

	private String sWindowTitle;

	public String getWindowTitle() {
		return sWindowTitle;
	}

	/**
	 * This makes our base Window Object. Once the Window Object is made use one
	 * of the Window Making functions like 'SUIScriptMessageBox'
	 */
	public SUIWindow(Player player) {
		pOwnerOfWindow = player;
		iWindowID = pOwnerOfWindow.getLastSUIBox();
		client = pOwnerOfWindow.getClient();
		PacketComponents = new Vector<byte[]>();
		
		pOwnerOfWindow.addPendingSUIWindow(this);
	}

	/**
	 * This Command Returns a Message Box Window Packet that can be queued to
	 * the client. Message Boxes are used when anything needs to be prompted to
	 * a player but a response is not required or expected.
	 * 
	 * @param client
	 *            - The Client to which this message box will be prompted.
	 * @param WindowTypeString
	 *            - Default is 'handleSUI' - there are many types you must
	 *            select one and know what it is. This defines what the window
	 *            is used for.
	 * @param WindowTitle
	 *            - This is the Title across the top of the window being popped
	 *            up. This can be any text or an STF String like
	 *            '@travel:ticket_purchase_complete'
	 * @param WindowPromptContent
	 *            - This is the actual text that the window will contain as an
	 *            explanation to the player that the window pops up to. this can
	 *            be any text you desire or it can be an STF String like
	 *            '@travel:ticket_collector_name'
	 * @param bEnableCancel
	 *            - Enable Cancel Button on Window?
	 * @param bEnableRevert
	 *            - Enable Revert Button on Window?
	 * @param ObjectID
	 *            - This is the optional id of the object making the window and
	 *            only some windows will require it. Most of the time this will
	 *            be 0.
	 * @param PlayerID
	 *            - This is the optional id of the player making or receiving
	 *            the window and only some windows will require it. Most of the
	 *            time this will be 0.
	 * @return
	 */
	public byte[] SUIScriptMessageBox(ZoneClient client,
			String WindowTypeString, String WindowTitle,
			String WindowPromptContent, boolean bEnableCancel,
			boolean bEnableRevert, long ObjectID, long PlayerID) {
		sWindowTitle = WindowTitle;
		try {
			this.client = client;
			if (buildSUIWindowHeader()) {
				if (setSUIWindowID()) {
					if (setWindowType(Constants.SUI_WINDOW_TYPE_ScriptmessageBox)) {
						if (setWindowTypeData(WindowTypeString)) {
							if (setWindowPrompt(WindowPromptContent,
									WindowTitle)) {
								if (addCancelButton(bEnableCancel)) {
									if (addRevertButton(bEnableRevert)) {
										if (addWindowFooter(ObjectID, PlayerID)) {
											return this.getSUIWindowPacket();
										} else {
											return null;
										}
									} else {
										return null;
									}
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else {
							return null;
						}
					} else {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			System.out
					.println("Exception Caught while Building SUIScriptMessageBox Window: "
							+ e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This Constructor makes a new Script Data List window. This is used for
	 * when a player needs a multiple choice selection. Returns a Packet
	 * containing the window that can be queued to the client.
	 * 
	 * @param client
	 *            - Client Initiating the dialog
	 * @param WindowTypeString
	 *            - there are many types you must select one and know what it
	 *            is. This defines what the window is used for.
	 * @param DataListTitle
	 *            - The title that the list will bear
	 * @param DataListPrompt
	 *            - The prompt containing instructions for the user.
	 * @param sList
	 *            - List of strings that will be placed in the data list
	 *            selections 0 indexed.
	 * @param ObjectList
	 *            - List of objects for the client to store so we can later
	 *            reference the selection to an object. I/E travel tickets or
	 *            rewards.
	 * @param ObjectID
	 *            - This is the optional id of the object making the window and
	 *            only some windows will require it. Most of the time this will
	 *            be 0.
	 * @param PlayerID
	 *            - This is the optional id of the player making or receiving
	 *            the window and only some windows will require it. Most of the
	 *            time this will be 0.
	 * @return
	 */
	public byte[] SUIScriptListBox(ZoneClient client, String WindowTypeString,
			String DataListTitle, String DataListPrompt, String sList[],
			Vector<SOEObject> ObjectList, long ObjectID, long PlayerID) {
		sParams = sList;
		sWindowTitle = DataListTitle;
		try {
			this.client = client;
			if (buildSUIWindowHeader()) {
				if (setSUIWindowID()) {
					if (setWindowType(Constants.SUI_WINDOW_TYPE_ScriptlistBox)) {
						if (setWindowDataListHeader(WindowTypeString)) {
							if (setWindowDataListPrompts(DataListTitle,
									DataListPrompt)) {
								if (addDataListCancelButton()) {
									if (addDataListOkButton()) {
										if (addDataListlst()) {
											if (addDataListItems(sList,
													ObjectList, client)) {
												if (this.addWindowFooter(
														ObjectID, PlayerID)) {
													return this
															.getSUIWindowPacket();
												} else {
													return null;
												}
											} else {
												return null;
											}
										} else {
											return null;
										}
									} else {
										return null;
									}
								} else {
									return null;
								}
							} else {
								return null;
							}
						} else {
							return null;
						}
					} else {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			System.out
					.println("Exception Caught while Building SUIScriptListBox Window: "
							+ e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This constructor builds a new Tranfer Box for transfer of credits or
	 * anything else that can be counted in units like power.
	 * 
	 * @param client
	 *            - Client Originating the request.
	 * @param sWindowTypeString
	 *            - there are many types you must select one and know what it
	 *            is. This defines what the window is used for.
	 * @param sTransferBoxTitle
	 *            - The title at the top of the transfer box
	 * @param sTransferBoxPrompt
	 *            - The prompt with instructions for this box
	 * @param sFromLabel
	 *            - the label for the From Box
	 * @param sToLabel
	 *            - the label for the to box
	 * @param iFromAmount
	 *            - Current Amount for the from box
	 * @param iToAmount
	 *            - current amount for the to box
	 * @param iConversionRatioFrom
	 *            - from box conversion ratio - if one unit of 'from' costs 2
	 *            units of 'to' then send this as 2 other wise 1
	 * @param iConversionRatioTo
	 *            - to box conversion ratio - if one unit of 'to' costs 2 units
	 *            of 'from' then send this as 2 other wise 1
	 * @return
	 */
	public byte[] SUIScriptTransferBox(ZoneClient client,
			String sWindowTypeString, String sTransferBoxTitle,
			String sTransferBoxPrompt, String sFromLabel, String sToLabel,
			int iFromAmount, int iToAmount, int iConversionRatioFrom,
			int iConversionRatioTo) {
		sWindowTitle = sTransferBoxTitle;
		try {
			this.client = client;
			if (buildSUIWindowHeader()) {
				if (setSUIWindowID()) {
					if (setWindowType(Constants.SUI_WINDOW_TYPE_ScripttransferBox)) {
						if (setTransferBoxHeader(sWindowTypeString)) {
							if (this.setTransferBoxContent(sTransferBoxTitle,
									sTransferBoxPrompt, sFromLabel, sToLabel,
									iFromAmount, iToAmount,
									iConversionRatioFrom, iConversionRatioTo)) {
								return this.getSUIWindowPacket();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.out
					.println("Exception Caught while Building SUIScriptTransferBox Window: "
							+ e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This constructor makes a new SUI Input Text box
	 * 
	 * @param client
	 *            - Client Originating the Window being created
	 * @param sWindowTypeString
	 *            - there are many types you must select one and know what it
	 *            is. This defines what the window is used for.
	 * @param sWindowPrompt
	 *            - This is the instructions that the user will receive within
	 *            the window
	 * @param sWindowTitle
	 *            - This is the title on the top of the window and what we will
	 *            use to identify the response with.
	 * @param bState
	 *            - State of the prompts as true or false normally true
	 * @param sState
	 *            - State of the buttons as 'Enabled' or 'Disabled' Case
	 *            Sensitive
	 * @param sVisible
	 *            - State of the buttons and lables as 'Visible' or 'Invisible'
	 *            - Normally seen as 'Visible'
	 * @param iMaxInputLength
	 *            - This designates the maximum length of the input box text. if
	 *            it exceeds it it will be truncated
	 * @param sCurrentTextString
	 *            - This is the current text that the input box will display can
	 *            be empty if we need an input. This will be checked for length
	 *            and truncated to iMaxInputLength
	 * @param oInputTextObject
	 *            - Object that will receive the text entered in the box.
	 * @return
	 */
	public byte[] SUIScriptTextInputBox(ZoneClient client,
			String sWindowTypeString, String sWindowPrompt,
			String sWindowTitle, boolean bState, String sState,
			String sVisible, int iMaxInputLength, String sCurrentTextString/*
																			 * ,
																			 * SOEObject
																			 * oInputTextObject
																			 */) {
		this.sWindowTitle = sWindowTitle;
		try {
			// System.out.println("1");
			this.client = client;
			// System.out.println("2");
			// this.InputTextObject = oInputTextObject;
			if (buildSUIWindowHeader()) {
				// System.out.println("3");
				if (setSUIWindowID()) {
					// System.out.println("4");
					if (this
							.setWindowType(Constants.SUI_WINDOW_TYPE_ScriptinputBox)) {
						// System.out.println("5");
						if (this.setWindowInputBoxHeader(sWindowTypeString)) {
							// System.out.println("6");
							if (setInputWindowPrompt(sWindowPrompt,
									sWindowTitle)) {
								// System.out.println("7");
								if (addDataListCancelButton()) {
									// System.out.println("8");
									if (addDataListOkButton()) {
										// System.out.println("9");
										if (this.setInputWindowInputFields(
												bState, sState, sVisible,
												iMaxInputLength,
												sCurrentTextString)) {
											// System.out.println("Returning Input Box Packet.");
											return this.getSUIWindowPacket();
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.out
					.println("Exception Caught while Building SUIScriptTransferBox Window: "
							+ e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * this builds the beginnning of the SUIWindow Packet.
	 * 
	 * @return
	 */
	private boolean buildSUIWindowHeader() {

		if (!bWindowHeaderSet) {
			SOEOutputStream ComponentWindowHeader = new SOEOutputStream(
					new ByteArrayOutputStream());
			try {
				ComponentWindowHeader.setOpcode(Constants.SOE_CHL_DATA_A);
				ComponentWindowHeader.setSequence(0);
				ComponentWindowHeader.setUpdateType(Constants.WORLD_UPDATE);
				ComponentWindowHeader.writeInt(Constants.SuiCreatePageMessage);
				ComponentWindowHeader.flush();
				bWindowHeaderSet = true;
				this.PacketComponents.add(ComponentWindowHeader.getBuffer());// 0
			} catch (Exception e) {
				System.out
						.println("Exception Caught in SUIWindow() BuildHeader "
								+ e);
				e.printStackTrace();
				bWindowHeaderSet = false;
			}
		}
		return bWindowHeaderSet;
	}

	/**
	 * This function sets the window identifier for the clients request. With
	 * the identifier we can tell what the window was for and find it later on
	 * when we receive a response from the client once a selection has been
	 * made.
	 * 
	 * @param ID
	 * @return
	 */
	private boolean setSUIWindowID() {
		if (!bWindowIDSet && bWindowHeaderSet) {
			bWindowIDSet = true;
			try {
				SOEOutputStream ComponentWindowID = new SOEOutputStream(
						new ByteArrayOutputStream());
				ComponentWindowID.writeInt(iWindowID);
				ComponentWindowID.flush();
				this.PacketComponents.add(ComponentWindowID.getBuffer());// 1
			} catch (Exception e) {
				System.out
						.println("Exception Caught in SUIWindow().setSUIWindowID() while writing the Window ID to the packet "
								+ e);
				e.printStackTrace();
				iWindowID = -1;
				bWindowIDSet = false;
			}
		}
		return bWindowIDSet;
	}

	/**
	 * Returns the client object that created this SUIWindow Object.
	 * 
	 * @return
	 */
	protected ZoneClient getClient() {
		return client;
	}

	/**
	 * Returns the window id for this window object for reference back to the
	 * client SUIWindow List.
	 * 
	 * @return
	 */
	protected int getWindowID() {
		return this.iWindowID;
	}

	protected void setOriginatingObject(SOEObject o) {
		OriginatingObject = o;
	}

	protected SOEObject getOriginatingObject() {
		return OriginatingObject;
	}

	/**
	 * This begins our window settings process. This Function sets the type of
	 * window.
	 * 
	 * @param WindowType
	 * @return
	 */
	private boolean setWindowType(int WindowType) {
		try {
			if (!bWindowTypeSet && bWindowHeaderSet && bWindowIDSet) {
				switch (WindowType) {
				case Constants.SUI_WINDOW_TYPE_ScriptlistBox: {
					bWindowTypeSet = true;
					SOEOutputStream ComponentWindowType = new SOEOutputStream(
							new ByteArrayOutputStream());
					ComponentWindowType.writeUTF("Script.listBox");
					ComponentWindowType.flush();
					this.PacketComponents.add(ComponentWindowType.getBuffer());// 2
					iNumberOfUpdates = 0;
					break;
				}
				case Constants.SUI_WINDOW_TYPE_ScriptmessageBox: {
					bWindowTypeSet = true;
					SOEOutputStream ComponentWindowType = new SOEOutputStream(
							new ByteArrayOutputStream());
					ComponentWindowType.writeUTF("Script.messageBox");
					ComponentWindowType.flush();
					this.PacketComponents.add(ComponentWindowType.getBuffer());// 2
					iNumberOfUpdates = 0;
					client.getPlayer().setLastSuiWindowTypeString(
							"Script.messageBox");
					client.getPlayer().setLastSuiWindowTypeInt(
							Constants.SUI_WINDOW_TYPE_ScriptmessageBox);
					break;
				}
				case Constants.SUI_WINDOW_TYPE_ScripttransferBox: {
					bWindowTypeSet = true;
					SOEOutputStream ComponentWindowType = new SOEOutputStream(
							new ByteArrayOutputStream());
					ComponentWindowType.writeUTF("Script.transfer");
					ComponentWindowType.flush();
					this.PacketComponents.add(ComponentWindowType.getBuffer());// 2
					iNumberOfUpdates = 0;
					client.getPlayer().setLastSuiWindowTypeString(
							"Script.transfer");
					client.getPlayer().setLastSuiWindowTypeInt(
							Constants.SUI_WINDOW_TYPE_ScripttransferBox);
					break;
				}
				case Constants.SUI_WINDOW_TYPE_ScriptinputBox: {
					bWindowTypeSet = true;
					SOEOutputStream ComponentWindowType = new SOEOutputStream(
							new ByteArrayOutputStream());
					ComponentWindowType.writeUTF("Script.inputBox");
					ComponentWindowType.flush();
					this.PacketComponents.add(ComponentWindowType.getBuffer());// 2
					iNumberOfUpdates = 0;
					client.getPlayer().setLastSuiWindowTypeString(
							"Script.inputBox");
					client.getPlayer().setLastSuiWindowTypeInt(
							Constants.SUI_WINDOW_TYPE_ScriptinputBox);
					break;
				}
				}
			}
		} catch (Exception e) {
			System.out
					.println("Exception Caught in SUIWindow().setWindowType().SUI_WINDOW_TYPE_ScriptlistBox "
							+ e);
			e.printStackTrace();
			bWindowTypeSet = false;
		}
		return bWindowTypeSet;
	}

	/**
	 * This is what defines our window Information and base Sizing information.
	 * the window type defines what this window is for. The generic type is
	 * 'handleSUI'
	 * 
	 * @param WindowTypeString
	 * @return
	 */
	private boolean setWindowTypeData(String WindowTypeString) {
		if (WindowTypeString.isEmpty()) {
			WindowTypeString = "handleSUI";
		}
		if (!bWindowTypeStringSet && bWindowTypeSet && bWindowHeaderSet
				&& bWindowIDSet) {
			try {
				SOEOutputStream ComponentWindowTypeData = new SOEOutputStream(
						new ByteArrayOutputStream());
				ComponentWindowTypeData.writeByte(0x05); // byte 5 //
				ComponentWindowTypeData.writeInt(00); // int 0
				ComponentWindowTypeData.writeInt(0x03); // list size
				ComponentWindowTypeData.writeShort(0x00); // short 0
				ComponentWindowTypeData.writeByte(0x01); // byte 1
				ComponentWindowTypeData.writeByte(0x00); // byte 0
				ComponentWindowTypeData.writeByte(0x09); // byte 9
				ComponentWindowTypeData.writeUTF(WindowTypeString);// 09 00
																	// //astring
																	// length 68
																	// 61 6E 64
																	// 6C 65 53
																	// 55 49
																	// //handleSUI
																	// //astring
				ComponentWindowTypeData.writeByte(0x05); // byte 5
				ComponentWindowTypeData.writeInt(0x00); // int 0
				ComponentWindowTypeData.writeInt(0x03); // int 3
				ComponentWindowTypeData.writeShort(0x00); // short 0
				ComponentWindowTypeData.writeByte(0x01); // byte 1
				ComponentWindowTypeData.writeByte(0x00); // byte 0
				ComponentWindowTypeData.writeByte(0x0A); // byte 0A
				ComponentWindowTypeData.writeUTF(WindowTypeString);// 09 00
																	// //astring
																	// length 68
																	// 61 6E 64
																	// 6C 65 53
																	// 55 49
																	// //handleSUI
																	// //astring
				ComponentWindowTypeData.flush();
				this.PacketComponents.add(ComponentWindowTypeData.getBuffer());
				this.iNumberOfUpdates++;
				bWindowTypeStringSet = true;
			} catch (Exception e) {
				System.out
						.println("Exception Caught in SUIWindow().setWindowTypeData() "
								+ e);
				e.printStackTrace();
			}
		}
		return bWindowTypeStringSet;
	}

	/**
	 * This adds a new prompt to our window. I/E This is the text in a small box
	 * below the window title. These promtps are achieved using the
	 * @Location:String Stf File Paths. But can also get any generic message you
	 * wish.
	 * 
	 * @param WindowPrompt
	 * @param sWindowTitle
	 * @return
	 */
	private boolean setWindowPrompt(String sWindowPrompt, String sWindowTitle) {
		if (!bWindowPromptSet && bWindowTypeStringSet && bWindowTypeSet
				&& bWindowHeaderSet && bWindowIDSet) {
			try {
				SOEOutputStream ComponentWindowPrompt = new SOEOutputStream(
						new ByteArrayOutputStream());
				// update 2
				ComponentWindowPrompt.writeByte(0x03);
				ComponentWindowPrompt.writeInt(0x01); // item in list //20 00 00
														// 00 //ustring length
														// //40 00 74 00 72 00
														// 61 00 76 00 65 00 6C
														// 00 3A 00
														// @.t.r.a.v.e.l.:. //74
														// 00 69 00 63 00 6B 00
														// 65 00 74 00 5F 00 70
														// 00 t.i.c.k.e.t._.p.
														// //75 00 72 00 63 00
														// 68 00 61 00 73 00 65
														// 00 5F 00
														// u.r.c.h.a.s.e._. //63
														// 00 6F 00 6D 00 70 00
														// 6C 00 65 00 74 00 65
														// 00 c.o.m.p.l.e.t.e.
				ComponentWindowPrompt.writeUTF16(sWindowPrompt);
				ComponentWindowPrompt.writeInt(0x02); // int 2
				ComponentWindowPrompt.writeUTF("Prompt.lblPrompt");// 10 00
																	// //astring
																	// length 50
																	// 72 6F 6D
																	// 70 74 2E
																	// 6C 62 6C
																	// 50 72 6F
																	// 6D 70 74
																	// //Prompt.lblPrompt
																	// //
																	// astring
				ComponentWindowPrompt.writeUTF("Text"); // 04 00 //astring
														// length 54 65 78 74
														// //text astring
				this.iNumberOfUpdates++;
				// update 3
				ComponentWindowPrompt.writeByte(0x03); // byte 3
				ComponentWindowPrompt.writeByte(0x01); // byte 1
				ComponentWindowPrompt.writeShort(0x00); // short 0
				ComponentWindowPrompt.writeByte(0x00);// byte 0 //10 00 00 00
														// //ustring length 40
														// 00 62 00 61 00 73 00
														// 65 00 5F 00 70 00 6C
														// 00 61 00 79 00 65 00
														// 72 00 3A 00 73 00 77
														// 00 67 00
														// //@.b.a.s.e._.p.l.a.y.e.r.:.s.w.g.
				ComponentWindowPrompt.writeUTF16(sWindowTitle);
				ComponentWindowPrompt.writeInt(0x02);// item in list
				ComponentWindowPrompt.writeUTF("bg.caption.lblTitle"); // 13 00
																		// 62 67
																		// 2E 63
																		// 61 70
																		// 74 69
																		// 6F 6E
																		// 2E 6C
																		// 62 6C
																		// 54 69
																		// 74 6C
																		// 65
																		// //bg.caption.lblTitle..
				ComponentWindowPrompt.writeUTF("Text"); // 04 00 //astring
														// length 54 65 78 74
														// //text astring
				ComponentWindowPrompt.flush();
				this.PacketComponents.add(ComponentWindowPrompt.getBuffer());
				this.iNumberOfUpdates++;
				bWindowPromptSet = true;

			} catch (Exception e) {
				System.out
						.println("Exception Caught in SUIWindow().setWindowPrompt() "
								+ e);
				e.printStackTrace();
				bWindowPromptSet = false;
			}
		}
		return bWindowPromptSet;
	}

	/**
	 * Adds a cancel button to our window.
	 * 
	 * @return
	 */
	private boolean addCancelButton(boolean bEnableCancel) {

		try {
			SOEOutputStream ComponentWindowCanceButton = new SOEOutputStream(
					new ByteArrayOutputStream());
			// update 4
			ComponentWindowCanceButton.writeByte(0x03); // byte 3
			ComponentWindowCanceButton.writeInt(0x01);// int 1
			ComponentWindowCanceButton.writeUTF16(Boolean
					.toString(bEnableCancel));// 05 00 00 00 46 00 61 00 6C 00
												// 73 00 65 00 //F.a.l.s.e.
			ComponentWindowCanceButton.writeInt(0x02);// int 2
			ComponentWindowCanceButton.writeUTF("btnCancel");// 09 00 62 74 6E
																// 43 61 6E 63
																// 65 6C
																// //btnCancel
			ComponentWindowCanceButton.writeUTF("Enabled");// 07 00 45 6E 61 62
															// 6C 65 64
															// //Enabled
			this.iNumberOfUpdates++;
			// update 5
			ComponentWindowCanceButton.writeByte(0x03); // byte 3
			ComponentWindowCanceButton.writeInt(0x01);// int 1 01 00 00 00
			ComponentWindowCanceButton.writeUTF16(Boolean
					.toString(bEnableCancel));// 05 00 00 00 46 00 61 00 6C 00
												// 73 00 65 00 //F.a.l.s.e.
			ComponentWindowCanceButton.writeInt(0x02);// 02 00 00 00
			ComponentWindowCanceButton.writeUTF("btnCancel");// 09 00 62 74 6E
																// 43 61 6E 63
																// 65 6C
																// //btnCancel
			ComponentWindowCanceButton.writeUTF("Visible");// 07 00 56 69 73 69
															// 62 6C 65
															// //Visible
			ComponentWindowCanceButton.flush();
			this.PacketComponents.add(ComponentWindowCanceButton.getBuffer());
			this.iNumberOfUpdates++;
			bWindowHasCancelButton = true;

		} catch (Exception e) {
			System.out
					.println("Exception Caught in SUIWindow().addCancelButton() "
							+ e);
			e.printStackTrace();
			bWindowHasCancelButton = false;
		}
		return bWindowHasCancelButton;
	}

	private boolean addDataListCancelButton() {

		try {
			SOEOutputStream ComponentWindowCanceButton = new SOEOutputStream(
					new ByteArrayOutputStream());
			// update 4
			ComponentWindowCanceButton.writeByte(3);// 03 //add button
			ComponentWindowCanceButton.writeInt(1);// 01 00 00 00
			ComponentWindowCanceButton.writeUTF16("@cancel");// 07 00 00 00 40
																// 00 63 00 61
																// 00 6E 00 63
																// 00 65 00 6C
																// 00
																// //@.c.a.n.c.e.l
			ComponentWindowCanceButton.writeInt(2);// 02 00 00 00
			ComponentWindowCanceButton.writeUTF("btnCancel");// 09 00 62 74 6E
																// 43 61 6E 63
																// 65 6C
																// //btnCancel
			ComponentWindowCanceButton.writeUTF("Text");// 04 00 54 65 78 74
														// //Text
			this.PacketComponents.add(ComponentWindowCanceButton.getBuffer());
			this.iNumberOfUpdates++;
			bWindowHasCancelButton = true;

		} catch (Exception e) {
			System.out
					.println("Exception Caught in SUIWindow().adddataListCancelButton() "
							+ e);
			e.printStackTrace();
			bWindowHasCancelButton = false;
		}
		return bWindowHasCancelButton;
	}

	private boolean addDataListOkButton() {

		try {
			SOEOutputStream ComponentWindowOkButton = new SOEOutputStream(
					new ByteArrayOutputStream());

			// Update 5 add a button
			ComponentWindowOkButton.writeByte(3);// 03
			ComponentWindowOkButton.writeInt(1);// 01 00 00 00
			ComponentWindowOkButton.writeUTF16("@ok");// 03 00 00 00 40 00 6F 00
														// 6B 00 //@.o.k
			ComponentWindowOkButton.writeInt(2);// 02 00 00 00 ...........
			ComponentWindowOkButton.writeUTF("btnOk");// 05 00 62 74 6E 4F 6B
														// //btnOk..
			ComponentWindowOkButton.writeUTF("Text");// 04 00 54 65 78 74
														// //Text...
			// ---------------------------------
			this.PacketComponents.add(ComponentWindowOkButton.getBuffer());
			this.iNumberOfUpdates++;
			bWindowHasOkButton = true;

		} catch (Exception e) {
			System.out
					.println("Exception Caught in SUIWindow().adddataListCancelButton() "
							+ e);
			e.printStackTrace();
			bWindowHasOkButton = false;
		}
		return bWindowHasOkButton;
	}

	private boolean addDataListlst() {

		try {
			SOEOutputStream ComponentWindowDataList = new SOEOutputStream(
					new ByteArrayOutputStream());
			// Update 6 add a data list to the sui window
			ComponentWindowDataList.writeByte(1);// 01 //add list
			ComponentWindowDataList.writeInt(0);// 00 00 00 00
			ComponentWindowDataList.writeInt(1);// 01 00 00 00
			ComponentWindowDataList.writeUTF("List.dataList");// 0D 00 4C 69 73
																// 74 2E 64 61
																// 74 61 4C 69
																// 73 74
																// //List.dataList
			this.PacketComponents.add(ComponentWindowDataList.getBuffer());
			this.iNumberOfUpdates++;
			bWindowHasDataList = true;

		} catch (Exception e) {
			System.out
					.println("Exception Caught in SUIWindow().addDataListlst() "
							+ e);
			e.printStackTrace();
			bWindowHasDataList = false;
		}
		return bWindowHasDataList;
	}

	private boolean addDataListItems(String sList[],
			Vector<SOEObject> oObjectList, ZoneClient c) {

		try {
			SOEOutputStream ComponentWindowDataListItems = new SOEOutputStream(
					new ByteArrayOutputStream());
			for (int i = 0; i < sList.length; i++) {
				// this is one update
				ComponentWindowDataListItems.writeByte(0x04);// 04
				ComponentWindowDataListItems.writeInt(1);// 01 00 00 00
				ComponentWindowDataListItems.writeUTF16(Integer.toString(i));// 01
																				// 00
																				// 00
																				// 00
																				// 30
																				// 00
																				// //0
				ComponentWindowDataListItems.writeInt(2);// 02 00 00 00
				ComponentWindowDataListItems.writeUTF("List.dataList");// 0D 00
																		// 4C 69
																		// 73 74
																		// 2E 64
																		// 61 74
																		// 61 4C
																		// 69 73
																		// 74
																		// //List.dataList..
				ComponentWindowDataListItems.writeUTF("Name");// 04 00 4E 61 6D
																// 65 //Name
				this.iNumberOfUpdates++;
				// this is a sencond update
				ComponentWindowDataListItems.writeByte(0x03);// 03
				ComponentWindowDataListItems.writeInt(1);// 01 00 00 00
				ComponentWindowDataListItems.writeUTF16(sList[i]);// 17 00 00 00
																	// 63 00 6F
																	// 00 72 00
																	// 65 00 6C
																	// 00 6C 00
																	// 69 00 61
																	// 00 20 00
																	// 2D 00 20
																	// 00 56 00
																	// 72 00 65
																	// 00 6E 00
																	// 69 00 20
																	// 00 49 00
																	// 73 00 6C
																	// 00 61 00
																	// 6E 00 64
																	// 00
																	// //corellia.-.Vreni.Island
				ComponentWindowDataListItems.writeInt(2);// 02 00 00 00
				ComponentWindowDataListItems.writeUTF("List.dataList."
						+ Integer.toString(i)); // 0F 00 4C 69 73 74 2E 64 61 74
												// 61 4C 69 73 74 2E 30
												// //List.dataList.0..
				ComponentWindowDataListItems.writeUTF("Text");// 04 00 54 65 78
																// 74 //Text
				if (oObjectList != null) {
					c.getPlayer().addSUIListWindowObjectList(i,
							oObjectList.get(i));
				}

				this.iNumberOfUpdates++;
			}
			this.PacketComponents.add(ComponentWindowDataListItems.getBuffer());

			bWindowHasDataListItems = true;

		} catch (Exception e) {
			System.out
					.println("Exception Caught in SUIWindow().addDataListlst() "
							+ e);
			e.printStackTrace();
			bWindowHasDataListItems = false;
		}
		return bWindowHasDataListItems;
	}

	/**
	 * This adds a revert button to our window. Apparently the OK Button is the
	 * Revert Button. dont ask qhy SOE is full of it.
	 * 
	 * @return
	 */
	private boolean addRevertButton(boolean bEnableRevert) {
		try {
			SOEOutputStream ComponentWindowRevertButton = new SOEOutputStream(
					new ByteArrayOutputStream());
			// update 6
			ComponentWindowRevertButton.writeByte(0x03); // 03
			ComponentWindowRevertButton.writeInt(0x01);// int 1 01 00 00 00
			ComponentWindowRevertButton.writeUTF16(Boolean
					.toString(bEnableRevert));// 05 00 00 00 46 00 61 00 6C 00
												// 73 00 65 00 //F.a.l.s.e.
			ComponentWindowRevertButton.writeInt(0x02);// 02 00 00 00
			ComponentWindowRevertButton.writeUTF("btnRevert");// 09 00 62 74 6E
																// 52 65 76 65
																// 72 74
																// //btnRevert
			ComponentWindowRevertButton.writeUTF("Enabled");// 07 00 45 6E 61 62
															// 6C 65 64
															// //Enabled
			this.iNumberOfUpdates++;
			// update 7
			ComponentWindowRevertButton.writeByte(0x03);// 03
			ComponentWindowRevertButton.writeInt(0x01);// int 1 01 00 00 00
			ComponentWindowRevertButton.writeUTF16(Boolean
					.toString(bEnableRevert));// 05 00 00 00 46 00 61 00 6C 00
												// 73 00 65 00 //F.a.l.s.e
			ComponentWindowRevertButton.writeInt(0x02); // 02 00 00 00
			ComponentWindowRevertButton.writeUTF("btnRevert");// 09 00 62 74 6E
																// 52 65 76 65
																// 72 74
																// //btnRevert
			ComponentWindowRevertButton.writeUTF("Visible");// 07 00 56 69 73 69
															// 62 6C 65
															// //Visible
			ComponentWindowRevertButton.flush();
			this.PacketComponents.add(ComponentWindowRevertButton.getBuffer());
			this.iNumberOfUpdates++;
			bWindowHasRevertButton = true;

		} catch (Exception e) {
			System.out
					.println("Exception Caught in SUIWindow().addRevertButton() "
							+ e);
			e.printStackTrace();
			bWindowHasRevertButton = false;
		}
		return bWindowHasRevertButton;
	}

	/**
	 * This adds the footer to the SUI Window. If you do not know if the Object
	 * ID and Player ID are needed set them to 0 it is what its needed in most
	 * cases.
	 * 
	 * @param _ObjectID
	 * @param _PlayerID
	 * @return
	 */
	private boolean addWindowFooter(long _ObjectID, long _PlayerID) {
		if (!bWindowHasFooter) {
			try {
				SOEOutputStream ComponentWindowFooter = new SOEOutputStream(
						new ByteArrayOutputStream());
				ComponentWindowFooter.writeLong(_ObjectID); // 00 00 00 00 00 00
															// 00 00 //// Object
															// ID of the item
															// generating the
															// dialog box (if
															// any).
				ComponentWindowFooter.writeInt(0x0); // 00 00 00 00 //spacer
				ComponentWindowFooter.writeLong(_PlayerID); // 00 00 00 00 00 00
															// 00 00 //players
															// id
				ComponentWindowFooter.flush();
				this.PacketComponents.add(ComponentWindowFooter.getBuffer());

				this.iNumberOfUpdates++;
				bWindowHasFooter = true;
			} catch (Exception e) {
				System.out
						.println("Exception Caught in SUIWindow().addWindowFooter() "
								+ e);
				e.printStackTrace();
				bWindowHasFooter = false;
			}
		}
		return bWindowHasFooter;
	}

	private boolean setWindowDataListHeader(String WindowTypeString) {
		if (!bWindowHasDataListHeader) {
			try {
				SOEOutputStream ComponentWindowListHeader = new SOEOutputStream(
						new ByteArrayOutputStream());
				// update 1
				ComponentWindowListHeader.writeByte(5);// 05
				ComponentWindowListHeader.writeInt(0);// 00 00 00 00
				ComponentWindowListHeader.writeInt(7);// 07 00 00 00
				// -----------------

				ComponentWindowListHeader.writeShort(0);// 00 00
				ComponentWindowListHeader.writeByte(1);// 01
				ComponentWindowListHeader.writeByte(0);// 00
				ComponentWindowListHeader.writeByte(9);// 09
				// -----------------

				ComponentWindowListHeader.writeUTF(WindowTypeString);// 0B 00 6D
																		// 73 67
																		// 53 65
																		// 6C 65
																		// 63 74
																		// 65 64
																		// //msgSelected
				ComponentWindowListHeader.writeUTF("List.lstList");// 0C 00 4C
																	// 69 73 74
																	// 2E 6C 73
																	// 74 4C 69
																	// 73 74
																	// //List.lstList
				ComponentWindowListHeader.writeUTF("SelectedRow");// 0B 00 53 65
																	// 6C 65 63
																	// 74 65 64
																	// 52 6F 77
																	// //SelectedRow
				ComponentWindowListHeader.writeUTF("bg.caption.lblTitle");// 13
																			// 00
																			// 62
																			// 67
																			// 2E
																			// 63
																			// 61
																			// 70
																			// 74
																			// 69
																			// 6F
																			// 6E
																			// 2E
																			// 6C
																			// 62
																			// 6C
																			// 54
																			// 69
																			// 74
																			// 6C
																			// 65
																			// //bg.caption.lblTitle
				ComponentWindowListHeader.writeUTF("Text");// 04 00 54 65 78 74
															// //Text
				this.iNumberOfUpdates++;
				// ----------------------------
				// update 2
				ComponentWindowListHeader.writeByte(5);// 05
				ComponentWindowListHeader.writeInt(0);// 00 00 00 00
				ComponentWindowListHeader.writeInt(7);// 07 00 00 00
				// -----------------------------

				ComponentWindowListHeader.writeShort(0);// 00 00
				ComponentWindowListHeader.writeByte(1);// 01
				ComponentWindowListHeader.writeByte(0);// 00
				ComponentWindowListHeader.writeByte(0x0A);// 0A = 10
				// --------------------------------

				ComponentWindowListHeader.writeUTF(WindowTypeString);// 0B 00 6D
																		// 73 67
																		// 53 65
																		// 6C 65
																		// 63 74
																		// 65 64
																		// //msgSelected
				ComponentWindowListHeader.writeUTF("List.lstList");// 0C 00 4C
																	// 69 73 74
																	// 2E 6C 73
																	// 74 4C 69
																	// 73 74
																	// //List.lstList
				ComponentWindowListHeader.writeUTF("SelectedRow");// 0B 00 53 65
																	// 6C 65 63
																	// 74 65 64
																	// 52 6F 77
																	// //SelectedRow
				ComponentWindowListHeader.writeUTF("bg.caption.lblTitle");// 13
																			// 00
																			// 62
																			// 67
																			// 2E
																			// 63
																			// 61
																			// 70
																			// 74
																			// 69
																			// 6F
																			// 6E
																			// 2E
																			// 6C
																			// 62
																			// 6C
																			// 54
																			// 69
																			// 74
																			// 6C
																			// 65
																			// //bg.caption.lblTitle
				ComponentWindowListHeader.writeUTF("Text");// 04 00 54 65 78 74
															// //Text
				this.iNumberOfUpdates++;
				this.PacketComponents
						.add(ComponentWindowListHeader.getBuffer());
				bWindowHasDataListHeader = true;
			} catch (Exception e) {
				System.out
						.println("Exception Caught in SUIWindow().addWindowFooter() "
								+ e);
				e.printStackTrace();
				bWindowHasDataListHeader = false;
			}
		}

		return bWindowHasDataListHeader;
	}

	private boolean setWindowDataListPrompts(String DataListTitle,
			String DataListPrompt) {
		if (!bWindowHasDataListPrompt) {
			try {
				SOEOutputStream ComponentWindowDataListPrompt = new SOEOutputStream(
						new ByteArrayOutputStream());

				// update 3
				ComponentWindowDataListPrompt.writeByte(3);// 03
				ComponentWindowDataListPrompt.writeInt(1);// 01 00 00 00
				ComponentWindowDataListPrompt.writeUTF16(DataListTitle);// 1D 00
																		// 00 00
																		// 40 00
																		// 74 00
																		// 72 00
																		// 61 00
																		// 76 00
																		// 65 00
																		// 6C 00
																		// 3A 00
																		// 74 00
																		// 69 00
																		// 63 00
																		// 6B 00
																		// 65 00
																		// 74 00
																		// 5F 00
																		// 63 00
																		// 6F 00
																		// 6C 00
																		// 6C 00
																		// 65 00
																		// 63 00
																		// 74 00
																		// 6F 00
																		// 72 00
																		// 5F 00
																		// 6E 00
																		// 61 00
																		// 6D 00
																		// 65 00
																		// //@.t.r.a.v.e.l.:.t.i.c.k.e.t._.c.o.l.l.e.c.t.o.r._.n.a.m.e
				ComponentWindowDataListPrompt.writeInt(2);// 02 00 00 00
				ComponentWindowDataListPrompt.writeUTF("bg.caption.lblTitle");// 13
																				// 00
																				// 62
																				// 67
																				// 2E
																				// 63
																				// 61
																				// 70
																				// 74
																				// 69
																				// 6F
																				// 6E
																				// 2E
																				// 6C
																				// 62
																				// 6C
																				// 54
																				// 69
																				// 74
																				// 6C
																				// 65
																				// bg.caption.lblTitle
				ComponentWindowDataListPrompt.writeUTF("Text");// 04 00 54 65 78
																// 74 //Text
				// -------------------------------

				ComponentWindowDataListPrompt.writeByte(3);// 03
				ComponentWindowDataListPrompt.writeInt(1);// 01 00 00 00
				ComponentWindowDataListPrompt.writeUTF16(DataListPrompt);// 21
																			// 00
																			// 00
																			// 00
																			// 40
																			// 00
																			// 74
																			// 00
																			// 72
																			// 00
																			// 61
																			// 00
																			// 76
																			// 00
																			// 65
																			// 00
																			// 6C
																			// 00
																			// 3A
																			// 00
																			// 62
																			// 00
																			// 6F
																			// 00
																			// 61
																			// 00
																			// 72
																			// 00
																			// 64
																			// 00
																			// 69
																			// 00
																			// 6E
																			// 00
																			// 67
																			// 00
																			// 5F
																			// 00
																			// 74
																			// 00
																			// 69
																			// 00
																			// 63
																			// 00
																			// 6B
																			// 00
																			// 65
																			// 00
																			// 74
																			// 00
																			// 5F
																			// 00
																			// 73
																			// 00
																			// 65
																			// 00
																			// 6C
																			// 00
																			// 65
																			// 00
																			// 63
																			// 00
																			// 74
																			// 00
																			// 69
																			// 00
																			// 6F
																			// 00
																			// 6E
																			// 00
																			// //@.t.r.a.v.e.l.:.b.o.a.r.d.i.n.g._.t.i.c.k.e.t._.s.e.l.e.c.t.i.o.n
				ComponentWindowDataListPrompt.writeInt(2);// 02 00 00 00
				ComponentWindowDataListPrompt.writeUTF("Prompt.lblPrompt");// 10
																			// 00
																			// 50
																			// 72
																			// 6F
																			// 6D
																			// 70
																			// 74
																			// 2E
																			// 6C
																			// 62
																			// 6C
																			// 50
																			// 72
																			// 6F
																			// 6D
																			// 70
																			// 74
																			// //Prompt.lblPrompt
				ComponentWindowDataListPrompt.writeUTF("Text");// 04 00 54 65 78
																// 74 //Text
				// -------------------------

				this.iNumberOfUpdates++;
				this.PacketComponents.add(ComponentWindowDataListPrompt
						.getBuffer());
				bWindowHasDataListPrompt = true;
			} catch (Exception e) {
				System.out
						.println("Exception Caught in SUIWindow().addWindowFooter() "
								+ e);
				e.printStackTrace();
				bWindowHasDataListPrompt = false;
			}
		}

		return bWindowHasDataListPrompt;
	}

	private boolean setTransferBoxHeader(String WindowTypeString) {
		if (!bTransferBoxHasHeader) {
			try {
				SOEOutputStream ComponentTransferBoxHeader = new SOEOutputStream(
						new ByteArrayOutputStream());
				// update 1
				ComponentTransferBoxHeader.writeByte(5);// 05
				ComponentTransferBoxHeader.writeInt(0);// 00 00 00 00
				ComponentTransferBoxHeader.writeInt(7);// 07 00 00 00
				// -----------------

				ComponentTransferBoxHeader.writeShort(0);// 00 00
				ComponentTransferBoxHeader.writeByte(1);// 01
				ComponentTransferBoxHeader.writeByte(0);// 00
				ComponentTransferBoxHeader.writeByte(9);// 09
				// -----------------

				ComponentTransferBoxHeader.writeUTF(WindowTypeString);
				ComponentTransferBoxHeader.writeUTF("transaction.txtInputFrom");
				ComponentTransferBoxHeader.writeUTF("Text");// 04 00 54 65 78 74
															// //Text
				ComponentTransferBoxHeader.writeUTF("transaction.txtInputTo");
				ComponentTransferBoxHeader.writeUTF("Text");// 04 00 54 65 78 74
															// //Text

				this.iNumberOfUpdates++;
				// ----------------------------
				// update 2
				ComponentTransferBoxHeader.writeByte(5);// 05
				ComponentTransferBoxHeader.writeInt(0);// 00 00 00 00
				ComponentTransferBoxHeader.writeInt(7);// 07 00 00 00
				// -----------------------------

				ComponentTransferBoxHeader.writeShort(0);// 00 00
				ComponentTransferBoxHeader.writeByte(1);// 01
				ComponentTransferBoxHeader.writeByte(0);// 00
				ComponentTransferBoxHeader.writeByte(0x0A);// 0A = 10
				// --------------------------------

				ComponentTransferBoxHeader.writeUTF(WindowTypeString);
				ComponentTransferBoxHeader.writeUTF("transaction.txtInputFrom");
				ComponentTransferBoxHeader.writeUTF("Text");
				ComponentTransferBoxHeader.writeUTF("transaction.txtInputTo");
				ComponentTransferBoxHeader.writeUTF("Text");// 04 00 54 65 78 74
															// //Text
				this.iNumberOfUpdates++;
				this.PacketComponents.add(ComponentTransferBoxHeader
						.getBuffer());
				bTransferBoxHasHeader = true;
			} catch (Exception e) {
				System.out
						.println("Exception Caught in SUIWindow().setTransferBoxHeader() "
								+ e);
				e.printStackTrace();
				bTransferBoxHasHeader = false;
			}
		}

		return bTransferBoxHasHeader;
	}

	private boolean setTransferBoxContent(String sTransferBoxTitle,
			String sTransferBoxPrompt, String sFromLabel, String sToLabel,
			int iFromAmount, int iToAmount, int iConversionRatioFrom,
			int iConversionRatioTo) {
		if (!bTransferBoxHasContent) {
			try {
				client.getPlayer()
						.setLastSuiWindowTypeString(sTransferBoxTitle);
				SOEOutputStream ComponentTransferBoxContent = new SOEOutputStream(
						new ByteArrayOutputStream());
				// 3
				ComponentTransferBoxContent.writeByte(3);// 03
				ComponentTransferBoxContent.writeInt(1);// 01 00 00 00
				ComponentTransferBoxContent.writeUTF16(sTransferBoxTitle);// 17
																			// 00
																			// 00
																			// 00
																			// 40
																			// 00
																			// 62
																			// 00
																			// 61
																			// 00
																			// 73
																			// 00
																			// 65
																			// 00
																			// 5F
																			// 00
																			// 70
																			// 00
																			// 6C
																			// 00
																			// 61
																			// 00
																			// 79
																			// 00
																			// 65
																			// 00
																			// 72
																			// 00
																			// 3A
																			// 00
																			// 62
																			// 00
																			// 61
																			// 00
																			// 6E
																			// 00
																			// 6B
																			// 00
																			// 5F
																			// 00
																			// 74
																			// 00
																			// 69
																			// 00
																			// 74
																			// 00
																			// 6C
																			// 00
																			// 65
																			// 00
																			// //@.b.a.s.e._.p.l.a.y.e.r.:.b.a.n.k._.t.i.t.l.e.
				ComponentTransferBoxContent.writeInt(2);// 02 00 00 00
				ComponentTransferBoxContent.writeUTF("bg.caption.lblTitle");// 13
																			// 00
																			// 62
																			// 67
																			// 2E
																			// 63
																			// 61
																			// 70
																			// 74
																			// 69
																			// 6F
																			// 6E
																			// 2E
																			// 6C
																			// 62
																			// 6C
																			// 54
																			// 69
																			// 74
																			// 6C
																			// 65
																			// //bg.caption.lblTitle..
				ComponentTransferBoxContent.writeUTF("Text");// 04 00 54 65 78
																// 74 //Text.
				this.iNumberOfUpdates++;
				// 4
				ComponentTransferBoxContent.writeByte(3);// 03
				ComponentTransferBoxContent.writeInt(1);// 01 00 00 00
				ComponentTransferBoxContent.writeUTF16(sTransferBoxPrompt);// 18
																			// 00
																			// 00
																			// 00
																			// 40
																			// 00
																			// 62
																			// 00
																			// 61
																			// 00
																			// 73
																			// 00
																			// 65
																			// 00
																			// 5F
																			// 00
																			// 70
																			// 00
																			// 6C
																			// 00
																			// 61
																			// 00
																			// 79
																			// 00
																			// 65
																			// 00
																			// 72
																			// 00
																			// 3A
																			// 00
																			// 62
																			// 00
																			// 61
																			// 00
																			// 6E
																			// 00
																			// 6B
																			// 00
																			// 5F
																			// 00
																			// 70
																			// 00
																			// 72
																			// 00
																			// 6F
																			// 00
																			// 6D
																			// 00
																			// 70
																			// 00
																			// 74
																			// 00
																			// //@.b.a.s.e._.p.l.a.y.e.r.:.b.a.n.k._.p.r.o.m.p.t
				ComponentTransferBoxContent.writeInt(2);// 02 00 00 00
				ComponentTransferBoxContent.writeUTF("Prompt.lblPrompt");// 10
																			// 00
																			// 50
																			// 72
																			// 6F
																			// 6D
																			// 70
																			// 74
																			// 2E
																			// 6C
																			// 62
																			// 6C
																			// 50
																			// 72
																			// 6F
																			// 6D
																			// 70
																			// 74
																			// //Prompt.lblPrompt..
				ComponentTransferBoxContent.writeUTF("Text");// 04 00 54 65 78
																// 74
																// //Text........
				this.iNumberOfUpdates++;
				// 5
				ComponentTransferBoxContent.writeByte(3);// 03
				ComponentTransferBoxContent.writeInt(1);// 01 00 00 00
				ComponentTransferBoxContent.writeUTF16(sFromLabel);// 04 00 00
																	// 00 43 00
																	// 61 00 73
																	// 00 68 00
																	// .C.a.s.h.......
				ComponentTransferBoxContent.writeInt(2);// 02 00 00 00
				ComponentTransferBoxContent.writeUTF("transaction.lblFrom");// 13
																			// 00
																			// 74
																			// 72
																			// 61
																			// 6E
																			// 73
																			// 61
																			// 63
																			// 74
																			// 69
																			// 6F
																			// 6E
																			// 2E
																			// 6C
																			// 62
																			// 6C
																			// 46
																			// 72
																			// 6F
																			// 6D
																			// //transaction.lblFrom..
				ComponentTransferBoxContent.writeUTF("Text");// 04 00 54 65 78
																// 74
																// //Text........
				this.iNumberOfUpdates++;
				// 6
				ComponentTransferBoxContent.writeByte(3);// 03
				ComponentTransferBoxContent.writeInt(1);// 01 00 00 00
				ComponentTransferBoxContent.writeUTF16(sToLabel);// 04 00 00 00
																	// 42 00 61
																	// 00 6E 00
																	// 6B 00
																	// //.B.a.n.k.......
				ComponentTransferBoxContent.writeInt(2);// 02 00 00 00
				ComponentTransferBoxContent.writeUTF("transaction.lblTo");// 11
																			// 00
																			// 74
																			// 72
																			// 61
																			// 6E
																			// 73
																			// 61
																			// 63
																			// 74
																			// 69
																			// 6F
																			// 6E
																			// 2E
																			// 6C
																			// 62
																			// 6C
																			// 54
																			// 6F
																			// //transaction.lblTo..
				ComponentTransferBoxContent.writeUTF("Text");// 04 00 54 65 78
																// 74 //Text...
				this.iNumberOfUpdates++;
				// 7
				ComponentTransferBoxContent.writeByte(3);// 03
				ComponentTransferBoxContent.writeInt(1);// 01 00 00 00
				ComponentTransferBoxContent.writeUTF16(Integer
						.toString(iFromAmount));// 06 00 00 00 31 00 35 00 39 00
												// 36 00 33 00 32 00
												// //1.5.9.6.3.2 // LOL AMOUNT
												// IS A STRING .......
				ComponentTransferBoxContent.writeInt(2);// 02 00 00 00
				ComponentTransferBoxContent
						.writeUTF("transaction.lblStartingFrom");// 1B 00 74 72
																	// 61 6E 73
																	// 61 63 74
																	// 69 6F 6E
																	// 2E 6C 62
																	// 6C 53 74
																	// 61 72 74
																	// 69 6E 67
																	// 46 72 6F
																	// 6D
																	// //transaction.lblStartingFrom
				ComponentTransferBoxContent.writeUTF("Text");// 04 00 54 65 78
																// 74
																// //Text.......
				this.iNumberOfUpdates++;
				// 8
				ComponentTransferBoxContent.writeByte(3);// 03
				ComponentTransferBoxContent.writeInt(1);// 01 00 00 00
				ComponentTransferBoxContent.writeUTF16(Integer
						.toString(iToAmount));// 07 00 00 00 35 00 31 00 31 00
												// 36 00 38 00 39 00 32 00
												// //5.1.1.6.8.9.2.
				ComponentTransferBoxContent.writeInt(2);// 02 00 00 00
				ComponentTransferBoxContent
						.writeUTF("transaction.lblStartingTo");// 19 00 74 72 61
																// 6E 73 61 63
																// 74 69 6F 6E
																// 2E 6C 62 6C
																// 53 74 61 72
																// 74 69 6E 67
																// 54 6F
																// //transaction.lblStartingTo.
				ComponentTransferBoxContent.writeUTF("Text");// 04 00 54 65 78
																// 74
																// //Text.........
				this.iNumberOfUpdates++;
				// 9
				ComponentTransferBoxContent.writeByte(3);// 03
				ComponentTransferBoxContent.writeInt(1);// 01 00 00 00
				ComponentTransferBoxContent.writeUTF16(Integer
						.toString(iFromAmount));// 06 00 00 00 31 00 35 00 39 00
												// 36 00 33 00 32 00
												// //1.5.9.6.3.2.......
				ComponentTransferBoxContent.writeInt(2);// 02 00 00 00
				ComponentTransferBoxContent
						.writeUTF("transaction.txtInputFrom");// 18 00 74 72 61
																// 6E 73 61 63
																// 74 69 6F 6E
																// 2E 74 78 74
																// 49 6E 70 75
																// 74 46 72 6F
																// 6D
																// //transaction.txtInputFrom..
				ComponentTransferBoxContent.writeUTF("Text");// 04 00 54 65 78
																// 74 //Text..
				this.iNumberOfUpdates++;
				// 10
				ComponentTransferBoxContent.writeByte(3);// 03
				ComponentTransferBoxContent.writeInt(1);// 01 00 00 00
				ComponentTransferBoxContent.writeUTF16(Integer
						.toString(iToAmount));// 07 00 00 00 35 00 31 00 31 00
												// 36 00 38 00 39 00 32 00
												// //5.1.1.6.8.9.2
				ComponentTransferBoxContent.writeInt(2);// 02 00 00 00
				ComponentTransferBoxContent.writeUTF("transaction.txtInputTo");// 16
																				// 00
																				// 74
																				// 72
																				// 61
																				// 6E
																				// 73
																				// 61
																				// 63
																				// 74
																				// 69
																				// 6F
																				// 6E
																				// 2E
																				// 74
																				// 78
																				// 74
																				// 49
																				// 6E
																				// 70
																				// 75
																				// 74
																				// 54
																				// 6F
																				// //transaction.txtInputTo
				ComponentTransferBoxContent.writeUTF("Text");// 04 00 54 65 78
																// 74
																// //Text.........
				this.iNumberOfUpdates++;
				// 11
				// ---------------- the bank transaction window is meant to be a
				// transfer ratio window,
				// it is used not only to transfer credits but also power and
				// any other tradeable item,
				// it has a parameters for the conversion ratio from one value
				// to another
				// like power is 2 to 1 or credits being 1 to 1
				ComponentTransferBoxContent.writeByte(3);// 03
				ComponentTransferBoxContent.writeInt(1);// 01 00 00 00
				ComponentTransferBoxContent.writeUTF16(Integer
						.toString(iConversionRatioFrom));// 01 00 00 00 31 00
															// //1
				ComponentTransferBoxContent.writeInt(2);// 02 00 00 00
				ComponentTransferBoxContent.writeUTF("transaction");// 0B 00 74
																	// 72 61 6E
																	// 73 61 63
																	// 74 69 6F
																	// 6E
																	// //transaction
				ComponentTransferBoxContent.writeUTF("ConversionRatioFrom");// 13
																			// 00
																			// 43
																			// 6F
																			// 6E
																			// 76
																			// 65
																			// 72
																			// 73
																			// 69
																			// 6F
																			// 6E
																			// 52
																			// 61
																			// 74
																			// 69
																			// 6F
																			// 46
																			// 72
																			// 6F
																			// 6D
																			// //..ConversionRatioFrom........
				this.iNumberOfUpdates++;
				// 12
				ComponentTransferBoxContent.writeByte(3);// 03
				ComponentTransferBoxContent.writeInt(1);// 01 00 00 00
				ComponentTransferBoxContent.writeUTF16(Integer
						.toString(iConversionRatioTo));// 01 00 00 00 31 00 //1
				ComponentTransferBoxContent.writeInt(2);// 02 00 00 00
				ComponentTransferBoxContent.writeUTF("transaction");// 0B 00 74
																	// 72 61 6E
																	// 73 61 63
																	// 74 69 6F
																	// 6E
																	// //transaction
				ComponentTransferBoxContent.writeUTF("RatioTo");// 11 00 43 6F
																// 6E 76 65 72
																// 73 69 6F 6E
																// 52 61 74 69
																// 6F 54
																// 6F//RatioTo.........
				this.iNumberOfUpdates++;
				// ------------
				ComponentTransferBoxContent.writeLong(0);// 00 00 00 00 00 00 00
															// 00
				ComponentTransferBoxContent.writeInt(0x7F7FFFFF);// FF FF 7F 7F
				ComponentTransferBoxContent.writeInt(0x7F7FFFFF);// FF FF 7F 7F
				ComponentTransferBoxContent.writeInt(0x7F7FFFFF);// FF FF 7F 7F
				ComponentTransferBoxContent.writeInt(0);// 00 00 00 00
				this.PacketComponents.add(ComponentTransferBoxContent
						.getBuffer());
				bTransferBoxHasContent = true;
			} catch (Exception e) {
				System.out.println("Exception caught in setTransferBoxContent "
						+ e);
				e.printStackTrace();
				bTransferBoxHasContent = false;
			}
		}
		return bTransferBoxHasContent;
	}

	private boolean setWindowInputBoxHeader(String WindowTypeString) {
		if (!bWindowHasInputBoxHeader) {
			try {
				SOEOutputStream ComponentWindowInputHeader = new SOEOutputStream(
						new ByteArrayOutputStream());
				// update 1
				ComponentWindowInputHeader.writeByte(5);// 05
				ComponentWindowInputHeader.writeInt(0);// 00 00 00 00
				ComponentWindowInputHeader.writeInt(0x0B);
				// -----------------

				ComponentWindowInputHeader.writeShort(0);// 00 00
				ComponentWindowInputHeader.writeByte(1);// 01
				ComponentWindowInputHeader.writeByte(0);// 00
				ComponentWindowInputHeader.writeByte(9);// 09
				// -----------------

				ComponentWindowInputHeader.writeUTF(WindowTypeString);// 0B 00
																		// 6D 73
																		// 67 53
																		// 65 6C
																		// 65 63
																		// 74 65
																		// 64
																		// //msgSelected
				ComponentWindowInputHeader.writeUTF("Prompt.lblPrompt");// 0C 00
																		// 4C 69
																		// 73 74
																		// 2E 6C
																		// 73 74
																		// 4C 69
																		// 73 74
																		// //List.lstList
				ComponentWindowInputHeader.writeUTF("Text");// 0B 00 53 65 6C 65
															// 63 74 65 64 52 6F
															// 77 //SelectedRow
				ComponentWindowInputHeader.writeUTF("bg.caption.lblTitle");// 13
																			// 00
																			// 62
																			// 67
																			// 2E
																			// 63
																			// 61
																			// 70
																			// 74
																			// 69
																			// 6F
																			// 6E
																			// 2E
																			// 6C
																			// 62
																			// 6C
																			// 54
																			// 69
																			// 74
																			// 6C
																			// 65
																			// //bg.caption.lblTitle
				ComponentWindowInputHeader.writeUTF("Text");// 04 00 54 65 78 74
															// //Text
				ComponentWindowInputHeader.writeUTF("txtInput");// 08 00 74 78
																// 74 49 6E 70
																// 75 74
																// //txtInput
				ComponentWindowInputHeader.writeUTF("MaxLength");// 09 00 4D 61
																	// 78 4C 65
																	// 6E 67 74
																	// 68
																	// //MaxLength
				ComponentWindowInputHeader.writeUTF("txtInput");// 08 00 74 78
																// 74 49 6E 70
																// 75 74
																// //txtInput
				ComponentWindowInputHeader.writeUTF("LocalText");// 09 00 4C 6F
																	// 63 61 6C
																	// 54 65 78
																	// 74
																	// //LocalText...........
				this.iNumberOfUpdates++;
				// ----------------------------
				// update 2
				ComponentWindowInputHeader.writeByte(5);// 05
				ComponentWindowInputHeader.writeInt(0);// 00 00 00 00
				ComponentWindowInputHeader.writeInt(0x0B);// 07 00 00 00
				// -----------------------------

				ComponentWindowInputHeader.writeShort(0);// 00 00
				ComponentWindowInputHeader.writeByte(1);// 01
				ComponentWindowInputHeader.writeByte(0);// 00
				ComponentWindowInputHeader.writeByte(0x0A);// 0A = 10
				// --------------------------------

				ComponentWindowInputHeader.writeUTF(WindowTypeString);// 0B 00
																		// 6D 73
																		// 67 53
																		// 65 6C
																		// 65 63
																		// 74 65
																		// 64
																		// //msgSelected
				ComponentWindowInputHeader.writeUTF("Prompt.lblPrompt");// 0C 00
																		// 4C 69
																		// 73 74
																		// 2E 6C
																		// 73 74
																		// 4C 69
																		// 73 74
																		// //List.lstList
				ComponentWindowInputHeader.writeUTF("Text");// 0B 00 53 65 6C 65
															// 63 74 65 64 52 6F
															// 77 //SelectedRow
				ComponentWindowInputHeader.writeUTF("bg.caption.lblTitle");// 13
																			// 00
																			// 62
																			// 67
																			// 2E
																			// 63
																			// 61
																			// 70
																			// 74
																			// 69
																			// 6F
																			// 6E
																			// 2E
																			// 6C
																			// 62
																			// 6C
																			// 54
																			// 69
																			// 74
																			// 6C
																			// 65
																			// //bg.caption.lblTitle
				ComponentWindowInputHeader.writeUTF("Text");// 04 00 54 65 78 74
															// //Text
				ComponentWindowInputHeader.writeUTF("txtInput");// 08 00 74 78
																// 74 49 6E 70
																// 75 74
																// //txtInput
				ComponentWindowInputHeader.writeUTF("MaxLength");// 09 00 4D 61
																	// 78 4C 65
																	// 6E 67 74
																	// 68
																	// //MaxLength
				ComponentWindowInputHeader.writeUTF("txtInput");// 08 00 74 78
																// 74 49 6E 70
																// 75 74
																// //txtInput
				ComponentWindowInputHeader.writeUTF("LocalText");// 09 00 4C 6F
																	// 63 61 6C
																	// 54 65 78
																	// 74
																	// //LocalText...........
				this.iNumberOfUpdates++;
				this.PacketComponents.add(ComponentWindowInputHeader
						.getBuffer());
				bWindowHasInputBoxHeader = true;
			} catch (Exception e) {
				System.out
						.println("Exception Caught in SUIWindow().setWindowInputBoxHeader() "
								+ e);
				e.printStackTrace();
				bWindowHasInputBoxHeader = false;
			}
		}

		return bWindowHasInputBoxHeader;
	}

	private boolean setInputWindowPrompt(String sWindowPrompt,
			String sWindowTitle) {

		if (!bWindowPromptSet) {
			try {

				SOEOutputStream ComponentWindowPrompt = new SOEOutputStream(
						new ByteArrayOutputStream());
				// update 2
				ComponentWindowPrompt.writeByte(0x03);
				ComponentWindowPrompt.writeInt(0x01);
				ComponentWindowPrompt.writeUTF16(sWindowPrompt); // 14 00 00 00
																	// 40 00 73
																	// 00 75 00
																	// 69 00 3A
																	// 00 73 00
																	// 65 00 74
																	// 00 5F 00
																	// 6E 00 61
																	// 00 6D 00
																	// 65 00 5F
																	// 00 70 00
																	// 72 00 6F
																	// 00 6D 00
																	// 70 00 74
																	// 00
																	// //@.s.u.i.:.s.e.t._.n.a.m.e._.p.r.o.m.p.t.......
				ComponentWindowPrompt.writeInt(0x02); // int 2
				ComponentWindowPrompt.writeUTF("Prompt.lblPrompt");
				ComponentWindowPrompt.writeUTF("Text");
				this.iNumberOfUpdates++;
				// update 3
				ComponentWindowPrompt.writeByte(0x03); // byte 3
				ComponentWindowPrompt.writeInt(0x01); // byte 1
				ComponentWindowPrompt.writeUTF16(sWindowTitle);// 13 00 00 00 40
																// 00 73 00 75
																// 00 69 00 3A
																// 00 73 00 65
																// 00 74 00 5F
																// 00 6E 00 61
																// 00 6D 00 65
																// 00 5F 00 74
																// 00 69 00 74
																// 00 6C 00 65
																// 00
																// //@.s.u.i.:.s.e.t._.n.a.m.e._.t.i.t.l.e..
				ComponentWindowPrompt.writeInt(0x02);// item in list
				ComponentWindowPrompt.writeUTF("bg.caption.lblTitle"); // 13 00
																		// 62 67
																		// 2E 63
																		// 61 70
																		// 74 69
																		// 6F 6E
																		// 2E 6C
																		// 62 6C
																		// 54 69
																		// 74 6C
																		// 65
																		// //bg.caption.lblTitle..
				ComponentWindowPrompt.writeUTF("Text"); // 04 00 //astring
														// length 54 65 78 74
														// //text astring
				ComponentWindowPrompt.flush();
				this.iNumberOfUpdates++;
				this.PacketComponents.add(ComponentWindowPrompt.getBuffer());
				bWindowPromptSet = true;

			} catch (Exception e) {
				System.out
						.println("Exception Caught in SUIWindow().setInputWindowPrompt() "
								+ e);
				e.printStackTrace();
				bWindowPromptSet = false;
			}
		}
		return bWindowPromptSet;
	}

	private boolean setInputWindowInputFields(boolean bState, String sState,
			String sVisible, int iMaxInputLength, String sCurrentTextString) {
		if (!bWindowHasInputFields) {
			try {
				if (sCurrentTextString.length() >= iMaxInputLength) {
					sCurrentTextString = sCurrentTextString.substring(0,
							(iMaxInputLength - 1));// this way we never exceed
													// the max length
				}

				SOEOutputStream ComponentWindowInputFields = new SOEOutputStream(
						new ByteArrayOutputStream());
				// 7
				ComponentWindowInputFields.writeByte(0x03);// 03
				ComponentWindowInputFields.writeInt(0x01); // 01 00 00 00
				ComponentWindowInputFields.writeUTF16(Boolean.toString(bState));// 04
																				// 00
																				// 00
																				// 00
																				// 74
																				// 00
																				// 72
																				// 00
																				// 75
																				// 00
																				// 65
																				// 00
																				// //t.r.u.e
				ComponentWindowInputFields.writeInt(0x02); // int 202 00 00 00
				ComponentWindowInputFields.writeUTF("txtInput");// 08 00 74 78
																// 74 49 6E 70
																// 75 74
																// //txtInput
				ComponentWindowInputFields.writeUTF(sState);// 07 00 45 6E 61 62
															// 6C 65 64
															// //Enabled.......
				this.iNumberOfUpdates++;
				// 8
				ComponentWindowInputFields.writeByte(0x03); // byte 03
				ComponentWindowInputFields.writeInt(0x01); // 01 00 00 00
				ComponentWindowInputFields.writeUTF16(Boolean.toString(bState));// 04
																				// 00
																				// 00
																				// 00
																				// 74
																				// 00
																				// 72
																				// 00
																				// 75
																				// 00
																				// 65
																				// 00
																				// //t.r.u.e.......
				ComponentWindowInputFields.writeInt(0x02);// item in list 02 00
															// 00 00
				ComponentWindowInputFields.writeUTF("txtInput");// 08 00 74 78
																// 74 49 6E 70
																// 75 74
																// //txtInput
				ComponentWindowInputFields.writeUTF(sVisible); // 07 00 56 69 73
																// 69 62 6C 65
																// //Visible
				this.iNumberOfUpdates++;
				// 9
				ComponentWindowInputFields.writeByte(0x03); // 03
				ComponentWindowInputFields.writeInt(0x01); // 01 00 00 00
				ComponentWindowInputFields.writeUTF16("false");// 05 00 00 00 66
																// 00 61 00 6C
																// 00 73 00 65
																// 00
																// //f.a.l.s.e
				ComponentWindowInputFields.writeInt(0x02);// 02 00 00 00
				ComponentWindowInputFields.writeUTF("cmbInput");// 08 00 63 6D
																// 62 49 6E 70
																// 75 74
																// //cmbInput
				ComponentWindowInputFields.writeUTF(sState);// 07 00 45 6E 61 62
															// 6C 65 64
															// //Enabled.....
				this.iNumberOfUpdates++;
				// 10
				ComponentWindowInputFields.writeByte(0x03); // 03
				ComponentWindowInputFields.writeInt(0x01); // 01 00 00 00
				ComponentWindowInputFields.writeUTF16("false");// 05 00 00 00 66
																// 00 61 00 6C
																// 00 73 00 65
																// 00
																// //f.a.l.s.e...
				ComponentWindowInputFields.writeInt(0x02);// 02 00 00 00
				ComponentWindowInputFields.writeUTF("cmbInput");// 08 00 63 6D
																// 62 49 6E 70
																// 75 74
																// //cmbInput..
				ComponentWindowInputFields.writeUTF(sVisible); // 07 00 56 69 73
																// 69 62 6C 65
																// //Visible
				this.iNumberOfUpdates++;
				// 11
				ComponentWindowInputFields.writeByte(0x03); // 03
				ComponentWindowInputFields.writeInt(0x01); // 01 00 00 00
				ComponentWindowInputFields.writeUTF16(Integer
						.toString(iMaxInputLength));// 03 00 00 00 31 00 32 00
													// 37 00 ////1.2.7.
				ComponentWindowInputFields.writeInt(0x02);// 02 00 00 00
				ComponentWindowInputFields.writeUTF("txtInput");// 08 00 74 78
																// 74 49 6E 70
																// 75 74
																// //txtInput
				ComponentWindowInputFields.writeUTF("MaxLength");// 09 00 4D 61
																	// 78 4C 65
																	// 6E 67 74
																	// 68
																	// //MaxLength...
				this.iNumberOfUpdates++;
				// 12
				ComponentWindowInputFields.writeByte(0x03); // 03
				ComponentWindowInputFields.writeInt(0x01); // 01 00 00 00
				ComponentWindowInputFields.writeUTF16(sCurrentTextString);// 0C
																			// 00
																			// 00
																			// 00
																			// 57
																			// 00
																			// 49
																			// 00
																			// 4C
																			// 00
																			// 4C
																			// 00
																			// 49
																			// 00
																			// 57
																			// 00
																			// 20
																			// 00
																			// 57
																			// 00
																			// 4F
																			// 00
																			// 4E
																			// 00
																			// 4B
																			// 00
																			// 41
																			// 00
																			// //W.I.L.L.I.W...W.O.N.K.A...
				ComponentWindowInputFields.writeInt(0x02);// 02 00 00 00
				ComponentWindowInputFields.writeUTF("txtInput");// 08 00 74 78
																// 74 49 6E 70
																// 75 74
																// //txtInput..
				ComponentWindowInputFields.writeUTF("Text");// 04 00 54 65 78 74
															// //Text
				this.iNumberOfUpdates++;

				ComponentWindowInputFields.writeLong(0);// 00 00 00 00 00 00 00
														// 00
				ComponentWindowInputFields.writeInt(0x7F7FFFFF);// FF FF 7F 7F
				ComponentWindowInputFields.writeInt(0x7F7FFFFF);// FF FF 7F 7F
				ComponentWindowInputFields.writeInt(0x7F7FFFFF);// FF FF 7F 7F
				ComponentWindowInputFields.writeInt(0);// 00 00 00 00

				ComponentWindowInputFields.flush();
				this.PacketComponents.add(ComponentWindowInputFields
						.getBuffer());

				bWindowHasInputFields = true;

			} catch (Exception e) {
				System.out
						.println("Exception Caught in SUIWindow().setInputWindowPrompt() "
								+ e);
				e.printStackTrace();
				bWindowHasInputFields = false;
			}
		}
		return bWindowHasInputFields;
	}

	/**
	 * This returns our SUIWindow Packet to the calling function.
	 * 
	 * @return
	 * @throws java.io.IOException
	 */
	private byte[] getSUIWindowPacket() throws IOException {
		SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());

		for (int i = 0; i < this.PacketComponents.size(); i++) {
			dOut.write(this.PacketComponents.get(i));
			if (i == 2) {
				dOut.writeInt(this.iNumberOfUpdates);
			}
		}
		dOut.flush();
		return dOut.getBuffer();
	}

	public void setWindowType(byte iWindowType) {
		this.iWindowType = iWindowType;
	}

	public byte getWindowType() {
		return iWindowType;
	}
}
