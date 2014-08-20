/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * This is the ticket Price Matrix.
 * @author Tomas Cruz
 */
public class TicketPriceMatrix {

    private int[][] Price = new int[100][500];
    
    public TicketPriceMatrix(){
        
    }
    
    public int getTicketPrice(int DeparturePlanet, int DestinationPlanet){
       // System.out.println("TicketPriceMatrix.getTicketPrice() Returning Ticket Price: " + Price[DeparturePlanet][DestinationPlanet] + " from " + Constants.PlanetNames[DeparturePlanet] + " " + DeparturePlanet +" to " + Constants.PlanetNames[DestinationPlanet] + " " + DestinationPlanet);
        return Price[DeparturePlanet][DestinationPlanet];
        
    }
    public void setTicketPrice(int DeparturePlanet, int DestinationPlanet,int TicketPrice){
        Price[DeparturePlanet][DestinationPlanet] = TicketPrice;
    }
}
