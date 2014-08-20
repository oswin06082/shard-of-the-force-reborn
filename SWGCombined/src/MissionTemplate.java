public class MissionTemplate {
	private int missionID;
	private int missionTypeCRC;
	private int missionRequiredFaction;
	private int missionDifficulty;
        private int missionDisplayObjectCRC;
	private int [] missionRequiredPlanet;
	private String missionStringFile;
        private int missionNumberOfEntries;
	private int [] missionAllowedLairTemplates;
        private int missionTerminalType;
	
	protected int getMissionID() {
		return missionID;
	}
	protected void setMissionID(int missionID) {
		this.missionID = missionID;
	}
	protected int getMissionTypeCRC() {
		return missionTypeCRC;
	}
	protected void setMissionTypeCRC(int missionTypeCRC) {
		this.missionTypeCRC = missionTypeCRC;
	}
	protected int getMissionRequiredFaction() {
		return missionRequiredFaction;
	}
	protected void setMissionRequiredFaction(int missionRequiredFaction) {
		this.missionRequiredFaction = missionRequiredFaction;
	}
	protected int getMissionDifficulty() {
		return missionDifficulty;
	}
	protected void setMissionDifficulty(int missionDifficulty) {
		this.missionDifficulty = missionDifficulty;
	}
	protected int[] getMissionRequiredPlanet() {
		return missionRequiredPlanet;
	}
	protected void setMissionRequiredPlanet(int [] missionRequiredPlanet) {
		this.missionRequiredPlanet = missionRequiredPlanet;
	}
	protected String getMissionStringFile() {
		return missionStringFile;
	}
	protected void setMissionStringFile(String missionStringFile) {
		this.missionStringFile = missionStringFile;
	}

        public int getMissionNumberOfEntries() {
            return missionNumberOfEntries;
        }

        public void setMissionNumberOfEntries(int missionNumberOfEntries) {
            this.missionNumberOfEntries = missionNumberOfEntries;
        }

        public int getMissionDisplayObjectCRC() {
            return missionDisplayObjectCRC;
        }

        public void setMissionDisplayObjectCRC(int missionDisplayObjectCRC) {
            this.missionDisplayObjectCRC = missionDisplayObjectCRC;
        }

        public int[] getMissionAllowedLairTemplates() {
            return missionAllowedLairTemplates;
        }

        public void setMissionAllowedLairTemplates(int[] missionAllowedLairTemplates) {
            this.missionAllowedLairTemplates = missionAllowedLairTemplates;
        }

        public int getMissionTerminalType() {
            return missionTerminalType;
        }

        public void setMissionTerminalType(int missionTerminalType) {
            this.missionTerminalType = missionTerminalType;
        }

        
        
        

}