/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *  //int objectID, long sender, long receiver, String header, String body, Vector<Waypoint> attachments,boolean readFlag
    //'base_player','escrow_withdraw_failed','%TO attempted to transfer %DI credits to you via bank wire.  The transfer from escrow to your bank account failed.  %TO has been refunded.')
    //'base_player','failed_escrow_refund','Your attempt to transfer %DI credits to %TO via a bank wire failed.  The attempt to refund the money to you from an escrow account also failed.  Please contact a CSR for a manual refund.')
    /**
     *  base_player','prose_tip_abort','Aborting last attempted /tip to %TT...')
        'base_player','prose_tip_invalid_amt','/TIP: invalid amount ("%DI").')
        base_player','prose_tip_invalid_param','/TIP: invalid amount ("%TO") parameter.')
        'base_player','prose_tip_nsf_bank','You lack the bank funds to wire %DI credits to %TT.')
        'base_player','prose_tip_nsf_cash','You lack the cash funds to tip %DI credits to %TT.')
        'base_player','prose_tip_nsf_wire','You do not have %DI credits (surcharge included) to tip the desired amount to %TT.')
        'base_player','prose_tip_pass_self','You successfully tip %DI credits to %TT.')
        'base_player','prose_tip_pass_target','%TT tips you %DI credits.')
        'base_player','prose_tip_range','You are too far away to tip %TT with cash.  You can send a wire transfer instead.')
        'base_player','prose_wire_mail_from','On behalf of %TO')
        'base_player','prose_wire_mail_self','%TO has received %DI credits from you, via bank wire transfer.')
        'base_player','prose_wire_mail_target','%DI credits from %TO have been successfully delivered from escrow to your bank account.')
        'base_player','prose_wire_pass_self','You have successfully sent %DI bank credits to %TO.')
        'base_player','prose_wire_pass_target','You have successfully received %DI bank credits from %TO.')
        'base_player','prose_withdraw_success','You successfully withdraw %DI credits from your account.')
        'base_player','received_escrow_refund','Your attempt to transfer %DI credits to %TO via a bank wire failed.  The money has been refunded to you (minus transfer fee) from the escrow account.')

     */

 
import java.util.Vector;
/**
 * Tip Object is used for making bank tips.
 * It is passed to the SUI Window for information for the sui window.
 * @author Tomas Cruz
 */
public class TipObject extends SOEObject{ // This shouldn't extend SOEObject as it is not a client object.

    public final static long serialVersionUID = 1l;
    private Player recipient;
    private Player player;
    private int iTipAmount;
    private transient ZoneServer server;
    
    public TipObject(ZoneServer server){
        this.server = server;
    }
    
    public void sendTipEmails(){
        try{
            if(player.debitCredits((int)((iTipAmount * .05) + iTipAmount)))
            {
                recipient.creditBankCredits(iTipAmount);
            }
            String sHeader = "On behalf of " + player.getFullName();
            String sBody = iTipAmount + " credits from " + player.getFullName() + " have been successfully delivered from escrow to your bank account.";
            Vector<Waypoint> attachments = new Vector<Waypoint>();
            SWGEmail e = new SWGEmail(server.getNextEmailID(),EmailServer.BM.getID(),recipient.getID(),sHeader,sBody,attachments,false);
            server.queueEmailNewClientMessage(e);
            sHeader = "On behalf of " + player.getFullName();
            sBody = recipient.getFullName() + " has received " + iTipAmount + " credits from you, via bank wire transfer.";                                        
            SWGEmail ee = new SWGEmail(server.getNextEmailID(),EmailServer.BM.getID(),player.getID(),sHeader,sBody,attachments,false);
            server.queueEmailNewClientMessage(ee);
            player.getClient().insertPacket(PacketFactory.buildChatSystemMessage("You have successfully sent " + iTipAmount + " bank credits to " + recipient.getFirstName()));
        }catch(Exception e){
            System.out.println("Exception caught in TipObject.sendTipEmails " + e);
            e.printStackTrace();
        }
    }

    public int getITipAmount() {
        return iTipAmount;
    }

    public void setITipAmount(int iTipAmount) {
        this.iTipAmount = iTipAmount;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getRecipient() {
        return recipient;
    }

    public void setRecipient(Player recipient) {
        this.recipient = recipient;
    }    
    
}
