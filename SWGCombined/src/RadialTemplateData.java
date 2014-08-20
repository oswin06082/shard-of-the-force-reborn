
public class RadialTemplateData {
	private String sCaption;
	private String sCommandName;
	private float fUseableRange;
	private boolean bUseItemAttachedTo;
	private int objectID;
	
	public RadialTemplateData() {
		
	}

	protected boolean isUseItemAttachedTo() {
		return bUseItemAttachedTo;
	}

	protected void setUseItemAttachedTo(boolean useItemAttachedTo) {
		bUseItemAttachedTo = useItemAttachedTo;
	}

	protected float getUseableRange() {
		return fUseableRange;
	}

	protected void setUseableRange(float useableRange) {
		fUseableRange = useableRange;
	}

	protected String getCaption() {
		return sCaption;
	}

	protected void setCaption(String caption) {
		sCaption = caption;
	}

	protected String getCommandName() {
		return sCommandName;
	}

	protected void setCommandName(String commandName) {
		sCommandName = commandName;
	}

	protected void setID(int id) {
		objectID = id;
	}
	protected int getID() {
		return objectID;
	}
}
