/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * This is the Travel Ticket Class.
 * Having this class allows for easy identification of a travel ticket for dispatch of properties
 * to be displayed when the item is being examined.
 * @author Tomas Cruz
 */
public final class TravelTicket extends TangibleItem {
	public final static long serialVersionUID = 1l;
    private TravelDestination DepartureInformation;
    private TravelDestination ArrivalInformation;
    private int TravelTicketPrice;

	
	public TravelTicket(){
        
    }
    
    /**
     * Sets the Deaprture information for this item and makes it a travel ticket
     * @param T - TravelDestination
     */
    protected void setDepartureInformation(TravelDestination T){
        DepartureInformation = T;
    }
    
    
    /**
     * Gets the Travel Departure Data for this ticket.
     * @return TravelDestination
     */
    protected TravelDestination getDepartureInformation(){
        return  DepartureInformation;
    }
    
    protected void setArrivalInformation(TravelDestination T){
        ArrivalInformation = T;
    }
    
    
    /**
     * Gets the Travel Arrival Data for this ticket.
     * @return TravelDestination
     */
    protected TravelDestination getArrivalInformation(){
        return  ArrivalInformation;
    }

    protected void setTravelTicketPrice(int price){
        TravelTicketPrice = price;
    }
    
    protected int getTravelTicketPrice(){
        return TravelTicketPrice;
    }

	
	
	public int experiment(long iExperimentalIndex, int numExperimentationPointsUsed, Player thePlayer) {
		// Can't experiment on a TravelTicket -- just return.
		return 0;
	}
	

}
