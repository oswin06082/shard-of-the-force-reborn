import net.persistentworlds.script.ScriptConstants;

public class ScriptAPI implements ScriptConstants {
	private ZoneServer server;

	public ScriptAPI(ZoneServer z) {
		server = z;
	}

	public void printServerConsoleMessage(String s) {
		System.out.println("Script message: " + s);
	}

	public void broadcastSystemMessage(long targetPlayerID, String message) {
		
		//Get the player from the server.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//If the player is valid.
		if(targetPlayer != null) {
		
			//Send the system message.
			server.broadcastSystemMessage(targetPlayer, message);
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
	}

	public void broadcastAreaMessage(long sourceID, String message, boolean includeObject) {
		
		//Get the object and all players around the object.
		SOEObject sourceObject = server.getObjectFromAllObjects(sourceID);
		
		//If the player is valid.
		if(sourceObject != null) {
			
			//If we want to include the object in the area message:
			if(includeObject) {
				
				//If we can send the object a system message:
				if(sourceObject instanceof Player) {
					
					//Broadcast message to every player around the object, including the object.
					server.broadcastAreaMessage(sourceObject, message, true);
				} else {
					
					//Else, invalid argument.
					throw new IllegalArgumentException("Cannot include object ID " + sourceID + " in broadcastAreaMessage(), it's not a Player.");
				}
			} else {
				
				//Else, broadcast message to every player around the object.
				server.broadcastAreaMessage(sourceObject, message, false);
			}
		} else {
			
			//Invalid object.
			throw new IllegalArgumentException(String.format("There is no valid object with an ID of %s.", sourceObject));
		}
	}

	public void broadcastPlanetaryMessage(long sourceID, String message) {
		
		//Get the object and all players around the object.
		SOEObject sourceObject = server.getObjectFromAllObjects(sourceID);

		//Broadcast message to every player on the planet.
		server.broadcastPlanetaryMessage(sourceObject.getPlanetID(), message);
	}
	
	public boolean objectExists(long objectID) {
		
		//Local instance variables.
		boolean objectExists = false;
		
		//Get the object from the server.
		SOEObject targetObject = server.getObjectFromAllObjects(objectID);
		
		//If the object exists.
		if(targetObject != null) {

			//Update results.
			objectExists = true;
		}
		
		//Return results of check.
		return objectExists;		
	}
	
	public boolean isObjectPlayer(long objectID) {
		
		//Local instance variables.
		boolean isPlayer = false;
		
		//Get the object from the server.
		SOEObject targetObject = server.getObjectFromAllObjects(objectID);
		
		//If the object exists.
		if(targetObject != null) {
			
			//Check if the object is a player.
			if(targetObject instanceof Player) {
				
				//Update results.
				isPlayer = true;
			}
		}
		
		//Return results of check.
		return isPlayer;
	}
	
	public String getPlayerFullName(long targetPlayerID) {
		
		//Local instance variables.
		String fullPlayerName = null;
		
		//Get the player from the server.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//Check if the player exists.
		if(targetPlayer != null) {
			
			//Get the player name.
			fullPlayerName = targetPlayer.getFullName();
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
		
		//Return result.
		return fullPlayerName;
	}
	
	public String getPlayerFirstName(long targetPlayerID) {
		
		//Local instance variables.
		String firstPlayerName = null;
		
		//Get the player from the server.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//Check if the player exists.
		if(targetPlayer != null) {
			
			//Get the player name.
			firstPlayerName = targetPlayer.getFirstName();
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
		
		//Return result.
		return firstPlayerName;
	}
	
	public String getPlayerLastName(long targetPlayerID) {
		
		//Local instance variables.
		String lastPlayerName = null;
		
		//Get the player from the server.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//Check if the player exists.
		if(targetPlayer != null) {
			
			//Get the player name.
			lastPlayerName = targetPlayer.getLastName();
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
		
		//Return result.
		return lastPlayerName;
	}
	
	public boolean isPlayerGM(long targetPlayerID) {
		
		//Local instance variables.
		boolean isGM = false;
		
		//Get the player.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//Check if the player exists.
		if(targetPlayer != null) {
			
			//Get the player's ZoneClient.
			ZoneClient targetPlayerClient = targetPlayer.getClient();			
			
			//Check if the player has a valid session.
			if(targetPlayerClient != null && targetPlayerClient.getValidSession()) {
				
				//Get if the player is a GM.
				isGM = targetPlayerClient.getUpdateThread().getIsGM();
			} else {

				throw new IllegalArgumentException(String.format("Player ID(%s) doesn't have a valid session with the server.", targetPlayer.getID()));
			}
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
		
		//Return results of check.
		return isGM;
	}
	
	public boolean isPlayerDeveloper(long targetPlayerID) {
		
		//Local instance variables.
		boolean isDeveloper = false;
		
		//Get the player.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//Check if the player exists.
		if(targetPlayer != null) {
			
			//Get the player's ZoneClient.
			ZoneClient targetPlayerClient = targetPlayer.getClient();			
			
			//Check if the player has a valid session.
			if(targetPlayerClient != null && targetPlayerClient.getValidSession()) {
				
				//Get if the player is a developer.
				isDeveloper = targetPlayerClient.getUpdateThread().getIsDeveloper();
			} else {

				throw new IllegalArgumentException(String.format("Player ID(%s) doesn't have a valid session with the server.", targetPlayer.getID()));
			}
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
		
		//Return results of check.
		return isDeveloper;
	}
	
	public int getInventoryCreditAmmount(long targetPlayerID) {
		
		//Local instance variables.
		int inventoryCredits = 0;
		
		//Get the player.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//Check if the player exists.
		if(targetPlayer != null) {
			
			//Get inventory credits.
			inventoryCredits = targetPlayer.getInventoryCredits();
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
		
		//Return amount of inventory credits.
		return inventoryCredits;
	}
	
	public int getBankCreditAmmount(long targetPlayerID) {
		
		//Local instance variables.
		int bankCredits = 0;
		
		//Get the player.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//Check if the player exists.
		if(targetPlayer != null) {
			
			//Get bank credits.
			bankCredits = targetPlayer.getBankCredits();
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
		
		//Return amount of inventory credits.
		return bankCredits;
	}
	
	public int getTotalCreditAmmount(long targetPlayerID) {
		//Local instance variables.
		int totalCredits = 0;
		
		//Get the player.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//Check if the player exists.
		if(targetPlayer != null) {
			
			//Get total credits.
			totalCredits = targetPlayer.getCashOnHand();
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
		
		//Return amount of inventory credits.
		return totalCredits;
	}
	
	public void addInventoryCredits(long targetPlayerID, int ammount) {
		
		//Unsigned and out of bounds check.
		if(ammount > 1 && ammount <= Integer.MAX_VALUE) {
			
			//Get the player.
			Player targetPlayer = server.getPlayer(targetPlayerID);
			
			//Check if the player exists.
			if(targetPlayer != null) {
				
				//Give the player credits.
				targetPlayer.creditInventoryCredits(ammount);
			} else {
				
				//Player doesn't exist.
				throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
			}			
		} else {
			
			//Out of bounds.
			throw new IllegalArgumentException(String.format("Credit ammount must be between 1 and %s.", Integer.MAX_VALUE));
		}
	}
	
	public void removeInventoryCredits(long targetPlayerID, int ammount) {
		
		//Unsigned and out of bounds check.
		if(ammount > 1 && ammount <= Integer.MAX_VALUE) {
			
			//Get the player.
			Player targetPlayer = server.getPlayer(targetPlayerID);
			
			//Check if the player exists.
			if(targetPlayer != null) {
				
				//If the player has enough credits.
				if(ammount <= targetPlayer.getInventoryCredits()) {
				
					//Remove credits from player.
					boolean success = targetPlayer.debitInventoryCredits(ammount);
					
					//If removing credits was unsuccessful.
					if(!success) {
						
						//This shouldn't happen.
						throw new InternalError("ScriptAPI::removeInventoryCredits(long, int) encountered impossible error! Credit ammount is in range in ScriptAPI::removeInventoryCredits(long, int), but not in Player::debitInventoryCredits(int)!");
					}
				} else {
					
					//Player doesn't have enough credits.
					throw new IllegalArgumentException("Player doesn't have enough credits.");
				}
			} else {
				
				//Player doesn't exist.
				throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
			}			
		} else {
			
			//Out of bounds.
			throw new IllegalArgumentException(String.format("Credit ammount must be between 1 and %s.", Integer.MAX_VALUE));
		}
	}
	
	public void addBankCredits(long targetPlayerID, int ammount) {
		
		//Unsigned and out of bounds check.
		if(ammount > 1 && ammount <= Integer.MAX_VALUE) {
			
			//Get the player.
			Player targetPlayer = server.getPlayer(targetPlayerID);
			
			//Check if the player exists.
			if(targetPlayer != null) {
				
				//Give the player credits.
				targetPlayer.creditBankCredits(ammount);
			} else {
				
				//Player doesn't exist.
				throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
			}			
		} else {
			
			//Out of bounds.
			throw new IllegalArgumentException(String.format("Credit ammount must be between 1 and %s.", Integer.MAX_VALUE));
		}
	}
	
	public void removeBankCredits(long targetPlayerID, int ammount) {
		
		//Unsigned and out of bounds check.
		if(ammount > 1 && ammount <= Integer.MAX_VALUE) {
			
			//Get the player.
			Player targetPlayer = server.getPlayer(targetPlayerID);
			
			//Check if the player exists.
			if(targetPlayer != null) {
				
				//If the player has enough credits.
				if(ammount <= targetPlayer.getInventoryCredits()) {
				
					//Remove credits from player.
					boolean success = targetPlayer.debitBankCredits(ammount);
					
					//If removing credits was unsuccessful.
					if(!success) {
						
						//This shouldn't happen.
						throw new InternalError("ScriptAPI::removeBankCredits(long, int) encountered impossible error! Credit ammount is in range in ScriptAPI::removeBankCredits(long, int), but not in Player::debitBankCredits(int)!");
					}
				} else {
					
					//Player doesn't have enough credits.
					throw new IllegalArgumentException("Player doesn't have enough credits.");
				}
			} else {
				
				//Player doesn't exist.
				throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
			}			
		} else {
			
			//Out of bounds.
			throw new IllegalArgumentException(String.format("Credit ammount must be between 1 and %s.", Integer.MAX_VALUE));
		}
	}
	
	public void playerTravel(long targetPlayerID, float x, float y, float z, int planetID) {
		
		//If the planet is a valid planet.
		if(planetID >= 0 && planetID <= 12) {
			
			//Get the player.
			Player targetPlayer = server.getPlayer(targetPlayerID);
			
			//Force the player to travel.
			targetPlayer.playerTravel(x, y, z, planetID);
		} else {
			
			throw new IllegalArgumentException("Invalid planet ID, planets range from 0 to 12.");
		}
	}
	
	public float getPlayerX(long targetPlayerID) {
		
		//Get the player from the server.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//Store coordinate.
		float XCoordinate = -1;
		
		//If the player is valid.
		if(targetPlayer != null) {
		
			//Get the coordinate
			XCoordinate = targetPlayer.getX();
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
		
		//Return X coordinate.
		return XCoordinate;
	}
	
	public float getPlayerY(long targetPlayerID) {
		
		//Get the player from the server.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//Store coordinate.
		float YCoordinate = -1;
		
		//If the player is valid.
		if(targetPlayer != null) {
		
			//Get the coordinate
			YCoordinate = targetPlayer.getY();
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
		
		//Return Y coordinate.
		return YCoordinate;
	}
	
	public float getPlayerZ(long targetPlayerID) {
		
		//Get the player from the server.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//Store coordinate.
		float ZCoordinate = -1;
		
		//If the player is valid.
		if(targetPlayer != null) {
		
			//Get the coordinate
			ZCoordinate = targetPlayer.getZ();
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
		
		//Return Z coordinate.
		return ZCoordinate;
	}
	
	public int getPlayerPlanet(long targetPlayerID) {
		
		//Get the player from the server.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//Store planet.
		int planetID = -1;
		
		//If the player is valid.
		if(targetPlayer != null) {
		
			//Get the planet
			planetID = targetPlayer.getPlanetID();
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
		
		//Return planet.
		return planetID;
	}
	
	public boolean isPlayerAt(long targetPlayerID, float x, float y, float z) {
		
		//Get the player from the server.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//Store result.
		boolean isPlayerAt = false;
		
		//If the player is valid.
		if(targetPlayer != null) {
		
			//Check the coordinates.
			if(targetPlayer.getX() == x && targetPlayer.getY() == y && targetPlayer.getZ() == z) {
				
				//Update result.
				isPlayerAt = true;
			}
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
		
		//Return result.
		return isPlayerAt;
	}
	
	public boolean isPlayerAt(long targetPlayerID, int x, int y, int z) {
		
		//Get the player from the server.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//Store result.
		boolean isPlayerAt = false;
		
		//If the player is valid.
		if(targetPlayer != null) {
		
			//Check the coordinates.
			if((int) targetPlayer.getX() == x && (int) targetPlayer.getY() == y &&  (int) targetPlayer.getZ() == z) {
				
				//Update result.
				isPlayerAt = true;
			}
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
		
		//Return result.
		return isPlayerAt;
	}	
	
	public boolean isPlayerAt(long targetPlayerID, float x, float y, float z, int planetID) {
		
		//Get the player from the server.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//Store result.
		boolean isPlayerAt = false;
		
		//If the player is valid.
		if(targetPlayer != null) {
		
			//Check the coordinates.
			if(targetPlayer.getX() == x && targetPlayer.getY() == y && targetPlayer.getZ() == z && targetPlayer.getPlanetID() == planetID) {
				
				//Update result.
				isPlayerAt = true;
			}
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
		
		//Return result.
		return isPlayerAt;
	}
	
	public boolean isPlayerAt(long targetPlayerID, int x, int y, int z, int planetID) {
		
		//Get the player from the server.
		Player targetPlayer = server.getPlayer(targetPlayerID);
		
		//Store result.
		boolean isPlayerAt = false;
		
		//If the player is valid.
		if(targetPlayer != null) {
		
			//Check the coordinates.
			if((int) targetPlayer.getX() == x && (int) targetPlayer.getY() == y &&  (int) targetPlayer.getZ() == z && targetPlayer.getPlanetID() == planetID) {
				
				//Update result.
				isPlayerAt = true;
			}
		} else {
			
			throw new IllegalArgumentException(String.format("There is no valid player with an object ID of %s.", targetPlayerID));
		}
		
		//Return result.
		return isPlayerAt;
	}	
}