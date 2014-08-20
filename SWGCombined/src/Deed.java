import java.util.concurrent.ConcurrentHashMap;
import java.util.*;
/**
 *
 * @author Tomas Cruz
 */
public class Deed extends TangibleItem {
        
    public final static long serialVersionUID = 1;
    private int deedTemplateID;    
    private boolean isPlaced;
    private int iSurplusMaintenance = 0;
    private int iSurplusPower = 0;
    
    private int iExtractionRate = 4;
    private int iEnergyMaintenanceRate = 4;
    private long lCurrentStructureID;
    private int iInputHopperSize;
    private int iOutputHopperSize;
    private int iLotSize;
    private transient ZoneServer server;
    private int redeedfee;
    private int [] pethams;


    public Deed(int deedTemplateID,int itemTemplateID , ZoneServer server){
        this.deedTemplateID = deedTemplateID;
        this.setTemplateID(itemTemplateID);
        this.server = server;
        this.isPlaced = false;
        iSurplusMaintenance = 0;
        iSurplusPower = 0;        
        
        DeedTemplate dT = DatabaseInterface.getDeedTemplateByID(deedTemplateID);
        iLotSize = dT.getLotsused();
        ItemTemplate t = getServer().getTemplateData(itemTemplateID);
        if(t!=null)
        {
            this.setTemplateID(t.getTemplateID());
            this.setFactionID(0);
            this.setHasSockets(false);
            this.setID(getServer().getNextObjectID());
            try {
            	this.setMaxCondition(1000, false);
            } catch (Exception e) {
            	// Can't happen -- not building the packet.
            }
            this.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
            this.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());           
        } 
        iInputHopperSize = dT.getIBaseHoppersize();
        iOutputHopperSize = dT.getIBaseHoppersize();
        iExtractionRate = dT.getIBaseExtractionRate();
        iEnergyMaintenanceRate = dT.getPower_per_hour();
        redeedfee = dT.getRedeedfee();
    }

    public ZoneServer getServer() {
        return server;
    }

    public void setServer(ZoneServer server) {
        this.server = server;
    }   
    
    public boolean isPlaced() {
        return isPlaced;
    }

    public void setIsPlaced(boolean isPlaced) {
        this.isPlaced = isPlaced;
    }

    public int getDeedTemplateID() {
        return deedTemplateID;
    }
        
    @Override
    protected void useItemByCommandID(ZoneClient client, byte commandID) {
        
        try{
                
                switch(getTemplateID())
                {
                    case 8953: //'object/tangible/deed/city_deed/shared_bank_corellia_deed.iff'
                    case 8954: //'object/tangible/deed/city_deed/shared_bank_naboo_deed.iff'
                    case 8955: //'object/tangible/deed/city_deed/shared_bank_tatooine_deed.iff'
                    case 8956: //'object/tangible/deed/city_deed/shared_cantina_corellia_deed.iff'
                    case 8957: //'object/tangible/deed/city_deed/shared_cantina_naboo_deed.iff'
                    case 8958: //'object/tangible/deed/city_deed/shared_cantina_tatooine_deed.iff'
                    case 8959: //'object/tangible/deed/city_deed/shared_cityhall_corellia_deed.iff'
                    case 8960: //'object/tangible/deed/city_deed/shared_cityhall_naboo_deed.iff'
                    case 8961: //'object/tangible/deed/city_deed/shared_cityhall_tatooine_deed.iff'
                    case 8962: //'object/tangible/deed/city_deed/shared_cloning_corellia_deed.iff'
                    case 8963: //'object/tangible/deed/city_deed/shared_cloning_naboo_deed.iff'
                    case 8964: //'object/tangible/deed/city_deed/shared_cloning_tatooine_deed.iff'
                    case 8965: //'object/tangible/deed/city_deed/shared_garage_corellia_deed.iff'
                    case 8966: //'object/tangible/deed/city_deed/shared_garage_naboo_deed.iff'
                    case 8967: //'object/tangible/deed/city_deed/shared_garage_tatooine_deed.iff'
                    case 8968: //'object/tangible/deed/city_deed/shared_garden_corellia_lrg_01_deed.iff'
                    case 8969: //'object/tangible/deed/city_deed/shared_garden_corellia_lrg_02_deed.iff'
                    case 8970: //'object/tangible/deed/city_deed/shared_garden_corellia_lrg_03_deed.iff'
                    case 8971: //'object/tangible/deed/city_deed/shared_garden_corellia_lrg_04_deed.iff'
                    case 8972: //'object/tangible/deed/city_deed/shared_garden_corellia_lrg_05_deed.iff'
                    case 8973: //'object/tangible/deed/city_deed/shared_garden_corellia_med_01_deed.iff'
                    case 8974: //'object/tangible/deed/city_deed/shared_garden_corellia_med_02_deed.iff'
                    case 8975: //'object/tangible/deed/city_deed/shared_garden_corellia_med_03_deed.iff'
                    case 8976: //'object/tangible/deed/city_deed/shared_garden_corellia_med_04_deed.iff'
                    case 8977: //'object/tangible/deed/city_deed/shared_garden_corellia_med_05_deed.iff'
                    case 8978: //'object/tangible/deed/city_deed/shared_garden_corellia_sml_01_deed.iff'
                    case 8979: //'object/tangible/deed/city_deed/shared_garden_corellia_sml_02_deed.iff'
                    case 8980: //'object/tangible/deed/city_deed/shared_garden_corellia_sml_03_deed.iff'
                    case 8981: //'object/tangible/deed/city_deed/shared_garden_corellia_sml_04_deed.iff'
                    case 8982: //'object/tangible/deed/city_deed/shared_garden_corellia_sml_05_deed.iff'
                    case 8983: //'object/tangible/deed/city_deed/shared_garden_dantooine_lrg_01_deed.iff'
                    case 8984: //'object/tangible/deed/city_deed/shared_garden_dantooine_med_01_deed.iff'
                    case 8985: //'object/tangible/deed/city_deed/shared_garden_dantooine_sml_01_deed.iff'
                    case 8986: //'object/tangible/deed/city_deed/shared_garden_dathomir_lrg_01_deed.iff'
                    case 8987: //'object/tangible/deed/city_deed/shared_garden_dathomir_med_01_deed.iff'
                    case 8988: //'object/tangible/deed/city_deed/shared_garden_dathomir_sml_01_deed.iff'
                    case 8989: //'object/tangible/deed/city_deed/shared_garden_endor_lrg_01_deed.iff'
                    case 8990: //'object/tangible/deed/city_deed/shared_garden_endor_med_01_deed.iff'
                    case 8991: //'object/tangible/deed/city_deed/shared_garden_endor_sml_01_deed.iff'
                    case 8992: //'object/tangible/deed/city_deed/shared_garden_naboo_lrg_01_deed.iff'
                    case 8993: //'object/tangible/deed/city_deed/shared_garden_naboo_lrg_02_deed.iff'
                    case 8994: //'object/tangible/deed/city_deed/shared_garden_naboo_lrg_03_deed.iff'
                    case 8995: //'object/tangible/deed/city_deed/shared_garden_naboo_lrg_04_deed.iff'
                    case 8996: //'object/tangible/deed/city_deed/shared_garden_naboo_lrg_05_deed.iff'
                    case 8997: //'object/tangible/deed/city_deed/shared_garden_naboo_med_01_deed.iff'
                    case 8998: //'object/tangible/deed/city_deed/shared_garden_naboo_med_02_deed.iff'
                    case 8999: //'object/tangible/deed/city_deed/shared_garden_naboo_med_03_deed.iff'
                    case 9000: //'object/tangible/deed/city_deed/shared_garden_naboo_med_04_deed.iff'
                    case 9001: //'object/tangible/deed/city_deed/shared_garden_naboo_med_05_deed.iff'
                    case 9002: //'object/tangible/deed/city_deed/shared_garden_naboo_sml_01_deed.iff'
                    case 9003: //'object/tangible/deed/city_deed/shared_garden_naboo_sml_02_deed.iff'
                    case 9004: //'object/tangible/deed/city_deed/shared_garden_naboo_sml_03_deed.iff'
                    case 9005: //'object/tangible/deed/city_deed/shared_garden_naboo_sml_04_deed.iff'
                    case 9006: //'object/tangible/deed/city_deed/shared_garden_naboo_sml_05_deed.iff'
                    case 9007: //'object/tangible/deed/city_deed/shared_garden_tatooine_lrg_01_deed.iff'
                    case 9008: //'object/tangible/deed/city_deed/shared_garden_tatooine_lrg_02_deed.iff'
                    case 9009: //'object/tangible/deed/city_deed/shared_garden_tatooine_lrg_03_deed.iff'
                    case 9010: //'object/tangible/deed/city_deed/shared_garden_tatooine_lrg_04_deed.iff'
                    case 9011: //'object/tangible/deed/city_deed/shared_garden_tatooine_lrg_05_deed.iff'
                    case 9012: //'object/tangible/deed/city_deed/shared_garden_tatooine_med_01_deed.iff'
                    case 9013: //'object/tangible/deed/city_deed/shared_garden_tatooine_med_02_deed.iff'
                    case 9014: //'object/tangible/deed/city_deed/shared_garden_tatooine_med_03_deed.iff'
                    case 9015: //'object/tangible/deed/city_deed/shared_garden_tatooine_med_04_deed.iff'
                    case 9016: //'object/tangible/deed/city_deed/shared_garden_tatooine_med_05_deed.iff'
                    case 9017: //'object/tangible/deed/city_deed/shared_garden_tatooine_sml_01_deed.iff'
                    case 9018: //'object/tangible/deed/city_deed/shared_garden_tatooine_sml_02_deed.iff'
                    case 9019: //'object/tangible/deed/city_deed/shared_garden_tatooine_sml_03_deed.iff'
                    case 9020: //'object/tangible/deed/city_deed/shared_garden_tatooine_sml_04_deed.iff'
                    case 9021: //'object/tangible/deed/city_deed/shared_garden_tatooine_sml_05_deed.iff'
                    case 9022: //'object/tangible/deed/city_deed/shared_hospital_corellia_deed.iff'
                    case 9023: //'object/tangible/deed/city_deed/shared_hospital_naboo_deed.iff'
                    case 9024: //'object/tangible/deed/city_deed/shared_hospital_tatooine_deed.iff'
                    case 9025: //'object/tangible/deed/city_deed/shared_shuttleport_corellia_deed.iff'
                    case 9026: //'object/tangible/deed/city_deed/shared_shuttleport_naboo_deed.iff'
                    case 9027: //'object/tangible/deed/city_deed/shared_shuttleport_tatooine_deed.iff'
                    case 9028: //'object/tangible/deed/city_deed/shared_theater_corellia_deed.iff'
                    case 9029: //'object/tangible/deed/city_deed/shared_theater_naboo_deed.iff'
                    case 9030: //'object/tangible/deed/city_deed/shared_theater_tatooine_deed.iff'
                    case 9134: //'object/tangible/deed/factory_deed/shared_factory_clothing_deed.iff'
                    case 9135: //'object/tangible/deed/factory_deed/shared_factory_food_deed.iff'
                    case 9136: //'object/tangible/deed/factory_deed/shared_factory_item_deed.iff'
                    case 9137: //'object/tangible/deed/factory_deed/shared_factory_structure_deed.iff'
                    case 9138: //'object/tangible/deed/generator_deed/shared_generator_fusion_deed.iff'
                    case 9139: //'object/tangible/deed/generator_deed/shared_generator_photo_bio_deed.iff'
                    case 9140: //'object/tangible/deed/generator_deed/shared_generator_solar_deed.iff'
                    case 9141: //'object/tangible/deed/generator_deed/shared_generator_wind_deed.iff'
                    case 9142: //'object/tangible/deed/guild_deed/shared_corellia_guild_deed.iff'
                    case 9143: //'object/tangible/deed/guild_deed/shared_generic_guild_deed.iff'
                    case 9144: //'object/tangible/deed/guild_deed/shared_naboo_guild_deed.iff'
                    case 9145: //'object/tangible/deed/guild_deed/shared_tatooine_guild_deed.iff'
                    case 9146: //'object/tangible/deed/guild_deed/shared_tatooine_guild_style_02_deed.iff'
                    case 9148: //'object/tangible/deed/harvester_deed/shared_harvester_flora_deed.iff'
                    case 9149: //'object/tangible/deed/harvester_deed/shared_harvester_flora_deed_heavy.iff'
                    case 9150: //'object/tangible/deed/harvester_deed/shared_harvester_flora_deed_medium.iff'
                    case 9151: //'object/tangible/deed/harvester_deed/shared_harvester_gas_deed.iff'
                    case 9152: //'object/tangible/deed/harvester_deed/shared_harvester_gas_deed_heavy.iff'
                    case 9153: //'object/tangible/deed/harvester_deed/shared_harvester_gas_deed_medium.iff'
                    case 9154: //'object/tangible/deed/harvester_deed/shared_harvester_liquid_deed.iff'
                    case 9155: //'object/tangible/deed/harvester_deed/shared_harvester_liquid_deed_heavy.iff'
                    case 9156: //'object/tangible/deed/harvester_deed/shared_harvester_liquid_deed_medium.iff'
                    case 9157: //'object/tangible/deed/harvester_deed/shared_harvester_moisture_deed_heavy.iff'
                    case 9158: //'object/tangible/deed/harvester_deed/shared_harvester_moisture_deed_medium.iff'
                    case 9159: //'object/tangible/deed/harvester_deed/shared_harvester_moisture_deed.iff'
                    case 9160: //'object/tangible/deed/harvester_deed/shared_harvester_ore_heavy_deed.iff'
                    case 9161: //'object/tangible/deed/harvester_deed/shared_harvester_ore_s1_deed.iff'
                    case 9162: //'object/tangible/deed/harvester_deed/shared_harvester_ore_s2_deed.iff'
                    case 9265: //'object/tangible/deed/player_house_deed/shared_corellia_house_large_deed.iff'
                    case 9266: //'object/tangible/deed/player_house_deed/shared_corellia_house_large_style_02_deed.iff'
                    case 9267: //'object/tangible/deed/player_house_deed/shared_corellia_house_medium_deed.iff'
                    case 9268: //'object/tangible/deed/player_house_deed/shared_corellia_house_medium_style_02_deed.iff'
                    case 9269: //'object/tangible/deed/player_house_deed/shared_corellia_house_small_style_02_deed.iff'
                    case 9271: //'object/tangible/deed/player_house_deed/shared_corellia_house_small_deed.iff'
                    case 9273: //'object/tangible/deed/player_house_deed/shared_generic_house_large_deed.iff'
                    case 9274: //'object/tangible/deed/player_house_deed/shared_generic_house_large_style_02_deed.iff'
                    case 9275: //'object/tangible/deed/player_house_deed/shared_generic_house_medium_deed.iff'
                    case 9276: //'object/tangible/deed/player_house_deed/shared_generic_house_medium_style_02_deed.iff'
                    case 9277: //'object/tangible/deed/player_house_deed/shared_generic_house_small_deed.iff'
                    case 9279: //'object/tangible/deed/player_house_deed/shared_generic_house_small_style_02_deed.iff'
                    case 9281: //'object/tangible/deed/player_house_deed/shared_merchant_tent_style_02_deed.iff'
                    case 9282: //'object/tangible/deed/player_house_deed/shared_merchant_tent_style_01_deed.iff'
                    case 9283: //'object/tangible/deed/player_house_deed/shared_merchant_tent_style_03_deed.iff'
                    case 9284: //'object/tangible/deed/player_house_deed/shared_naboo_house_large_deed.iff'
                    case 9285: //'object/tangible/deed/player_house_deed/shared_naboo_house_medium_deed.iff'
                    case 9286: //'object/tangible/deed/player_house_deed/shared_naboo_house_medium_style_02_deed.iff'
                    case 9287: //'object/tangible/deed/player_house_deed/shared_naboo_house_small_deed.iff'
                    case 9288: //'object/tangible/deed/player_house_deed/shared_naboo_house_small_style_02_deed.iff'
                    case 9290: //'object/tangible/deed/player_house_deed/shared_tatooine_house_medium_deed.iff'
                    case 9289: //'object/tangible/deed/player_house_deed/shared_tatooine_house_large_deed.iff'
                    case 9291: //'object/tangible/deed/player_house_deed/shared_tatooine_house_small_deed.iff'
                    case 9278: //small gen style 1 fp 02
                    case 9280: //small gen style 2 fp 02
                    case 9270: //small corellia st 2
                    case 9272: // samll corellia st 2 fp 2    
                    case 9292: //'object/tangible/deed/player_house_deed/shared_tatooine_house_small_style_02_deed.iff'
                    {
                        switch(commandID)
                        {
                            case 20:
                            {
                                this.enterStructurePlacementMode(client);
                                break;
                            }
                        }
                        break;
                    }
                    case 9297: //'object/tangible/deed/vehicle_deed/shared_jetpack_deed.iff'
                    case 9298: //'object/tangible/deed/vehicle_deed/shared_landspeeder_av21_deed.iff'
                    case 9299: //'object/tangible/deed/vehicle_deed/shared_landspeeder_x31_deed.iff'
                    case 9300: //'object/tangible/deed/vehicle_deed/shared_landspeeder_x34_deed.iff'
                    case 9301: //'object/tangible/deed/vehicle_deed/shared_speederbike_deed.iff'
                    case 9302: //'object/tangible/deed/vehicle_deed/shared_speederbike_flash_deed.iff'
                    case 9303: //'object/tangible/deed/vehicle_deed/shared_speederbike_swoop_deed.iff'
                    {
                        switch(commandID)
                        {
                            case 20:
                            {
                                //use deed
                                this.generateVehicleFromDeed(client);
                                break;
                            }
                        }                        
                        break;
                    }
                    case 12035:
                    case 12036:
                    case 12037:
                    case 12038:
                    case 12039:
                    case 12040:
                    {
                        switch(commandID)
                        {
                            case 20:
                            {
                                this.placeCamp(client);
                            }
                        }
                        break;
                    }
                    //pet deeds
                    case 9167: //object/tangible/deed/pet_deed/shared_angler_deed.iff
                    case 9168: //object/tangible/deed/pet_deed/shared_bageraset_deed.iff
                    case 9170: //object/tangible/deed/pet_deed/shared_bearded_jax_deed.iff
                    case 9171: //object/tangible/deed/pet_deed/shared_blurrg_deed.iff
                    case 9172: //object/tangible/deed/pet_deed/shared_boar_wolf_deed.iff
                    case 9173: //object/tangible/deed/pet_deed/shared_bocatt_deed.iff
                    case 9174: //object/tangible/deed/pet_deed/shared_bol_deed.iff
                    case 9175: //object/tangible/deed/pet_deed/shared_bolle_bol_deed.iff
                    case 9176: //object/tangible/deed/pet_deed/shared_bolma_deed.iff
                    case 9177: //object/tangible/deed/pet_deed/shared_bordok_deed.iff
                    case 9178: //object/tangible/deed/pet_deed/shared_brackaset_deed.iff
                    case 9179: //object/tangible/deed/pet_deed/shared_carrion_spat_deed.iff
                    case 9180: //object/tangible/deed/pet_deed/shared_choku_deed.iff
                    case 9181: //object/tangible/deed/pet_deed/shared_cu_pa_deed.iff
                    case 9182: //object/tangible/deed/pet_deed/shared_dalyrake_deed.iff
                    case 9210: //object/tangible/deed/pet_deed/shared_dewback_deed.iff
                    case 9211: //object/tangible/deed/pet_deed/shared_dune_lizard_deed.iff
                    case 9212: //object/tangible/deed/pet_deed/shared_durni_deed.iff
                    case 9213: //object/tangible/deed/pet_deed/shared_eopie_deed.iff
                    case 9214: //object/tangible/deed/pet_deed/shared_falumpaset_deed.iff
                    case 9215: //object/tangible/deed/pet_deed/shared_fambaa_deed.iff
                    case 9216: //object/tangible/deed/pet_deed/shared_gnort_deed.iff
                    case 9217: //object/tangible/deed/pet_deed/shared_graul_deed.iff
                    case 9218: //object/tangible/deed/pet_deed/shared_gronda_deed.iff
                    case 9219: //object/tangible/deed/pet_deed/shared_gualama_deed.iff
                    case 9220: //object/tangible/deed/pet_deed/shared_guf_drolg_deed.iff
                    case 9221: //object/tangible/deed/pet_deed/shared_gurnaset_deed.iff
                    case 9223: //object/tangible/deed/pet_deed/shared_gurreck_deed.iff
                    case 9224: //object/tangible/deed/pet_deed/shared_hermit_spider_deed.iff
                    case 9225: //object/tangible/deed/pet_deed/shared_huf_dun_deed.iff
                    case 9226: //object/tangible/deed/pet_deed/shared_huurton_deed.iff
                    case 9227: //object/tangible/deed/pet_deed/shared_ikopi_deed.iff
                    case 9228: //object/tangible/deed/pet_deed/shared_kaadu_deed.iff
                    case 9230: //object/tangible/deed/pet_deed/shared_kima_deed.iff
                    case 9231: //object/tangible/deed/pet_deed/shared_kimogila_deed.iff
                    case 9232: //object/tangible/deed/pet_deed/shared_kliknik_deed.iff
                    case 9233: //object/tangible/deed/pet_deed/shared_krahbu_deed.iff
                    case 9234: //object/tangible/deed/pet_deed/shared_kusak_deed.iff
                    case 9235: //object/tangible/deed/pet_deed/shared_kwi_deed.iff
                    case 9236: //object/tangible/deed/pet_deed/shared_langlatch_deed.iff
                    case 9237: //object/tangible/deed/pet_deed/shared_malkloc_deed.iff
                    case 9238: //object/tangible/deed/pet_deed/shared_mawgax_deed.iff
                    case 9239: //object/tangible/deed/pet_deed/shared_merek_deed.iff
                    case 9240: //object/tangible/deed/pet_deed/shared_mott_deed.iff
                    case 9241: //object/tangible/deed/pet_deed/shared_narglatch_deed.iff
                    case 9242: //object/tangible/deed/pet_deed/shared_piket_deed.iff
                    case 9243: //object/tangible/deed/pet_deed/shared_pugoriss_deed.iff
                    case 9244: //object/tangible/deed/pet_deed/shared_rancor_deed.iff
                    case 9245: //object/tangible/deed/pet_deed/shared_roba_deed.iff
                    case 9246: //object/tangible/deed/pet_deed/shared_ronto_deed.iff
                    case 9248: //object/tangible/deed/pet_deed/shared_sharnaff_deed.iff
                    case 9249: //object/tangible/deed/pet_deed/shared_shear_mite_deed.iff
                    case 9251: //object/tangible/deed/pet_deed/shared_snorbal_deed.iff
                    case 9252: //object/tangible/deed/pet_deed/shared_squall_deed.iff
                    case 9253: //object/tangible/deed/pet_deed/shared_swirl_prong_deed.iff
                    case 9254: //object/tangible/deed/pet_deed/shared_thune_deed.iff
                    case 9255: //object/tangible/deed/pet_deed/shared_torton_deed.iff
                    case 9256: //object/tangible/deed/pet_deed/shared_tybis_deed.iff
                    case 9257: //object/tangible/deed/pet_deed/shared_veermok_deed.iff
                    case 9260: //object/tangible/deed/pet_deed/shared_verne_deed.iff
                    case 9261: //object/tangible/deed/pet_deed/shared_vesp_deed.iff
                    case 9262: //object/tangible/deed/pet_deed/shared_vir_vur_deed.iff
                    case 9263: //object/tangible/deed/pet_deed/shared_woolamander_deed.iff
                    case 9264: //object/tangible/deed/pet_deed/shared_zucca_boar_deed.iff
                    {
                        switch(commandID)
                        {
                            case 20:
                            {
                                this.generatePetFromDeed(client);
                            }
                        }
                        break;
                    }
                    default:
                    {
                        //System.out.println("Unhandled Deed in Deed Object.useitem TemplateID " + this.getTemplateID() + " IFF:" + this.getIFFFileName());
                    }
                }
                
        }catch(Exception e){
            System.out.println("Exception caught in Deed.useItemByCommandID() " + e );
            e.printStackTrace();
        }
    }
    
    private void generateVehicleFromDeed(ZoneClient client){
        try{
            //client.insertPacket(PacketFactory.buildChatSystemMessage("Vehicle Generation not implemented yet."));
            client.insertPacket(PacketFactory.buildChatSystemMessage("Generating Vehicle"));
            DeedTemplate dT = DatabaseInterface.getDeedTemplateByID(this.getDeedTemplateID());
            // Make the Datapad intangible Item for this new Vehicle
            IntangibleObject vehicleDPIcon = new IntangibleObject();            
            vehicleDPIcon.setTemplateID(dT.getObject_base_template_id());
            vehicleDPIcon.setID(client.getServer().getNextObjectID());
            client.getServer().addObjectToAllObjects(vehicleDPIcon, false,false);
            vehicleDPIcon.setCustomizationData(null);
            client.getPlayer().getDatapad().addIntangibleObject(vehicleDPIcon);
            vehicleDPIcon.setContainer(client.getPlayer().getDatapad(), -1, false);
            client.getPlayer().spawnItem(vehicleDPIcon);
            
            // make the tangible item and place it inside the datapad control icon
            Vehicle vehicleItem = new Vehicle(3000, vehicleDPIcon); // This NPC constructor is for creating vehicles.
            vehicleItem.setTemplateID(dT.getObject_iff_template_id());
            vehicleItem.setCellID(0);
            vehicleItem.setIsVehicle();            
            vehicleItem.setMasterID(client.getPlayer().getID());
            vehicleItem.setServer(client.getServer());
            vehicleItem.setScale(1.0f, false);            
            vehicleItem.setID(client.getServer().getNextObjectID());
            vehicleItem.setPVPStatus(client.getPlayer().getPVPStatus());
            vehicleItem.setFactionID(client.getPlayer().getFactionID());
            vehicleItem.setStance(null, Constants.STANCE_STANDING, true);
            vehicleItem.clearAllStates(false);
            vehicleItem.setLinkID(0);
            vehicleItem.setDamage(0);
            vehicleItem.setHealth(3000);
            int[] hams = new int[1];
            hams[0] = 3000;
            vehicleItem.setHam(hams);
            vehicleItem.setCREO3Bitmask(Constants.BITMASK_CREO3_VEHICLE);
            vehicleItem.setMasterID(client.getPlayer().getID());
            vehicleDPIcon.setAssociatedCreature(vehicleItem);
            client.getServer().addObjectToAllObjects(vehicleItem, false,false);
            //delete the Deed form the players inventory
            client.getPlayer().getInventory().removeLinkedObject(this);
            client.getPlayer().removeItemFromInventory(this);
            client.getServer().removeObjectFromAllObjects(this, false);
            client.getPlayer().despawnItem(this);
            client.insertPacket(PacketFactory.buildChatSystemMessage("A Control Device has been added to your Datapad."));

        }catch(Exception e){
            System.out.println("Exception caught in Deed.generateVehicleFromDeed() " + e );
            e.printStackTrace();
        }
    }
    
    private void enterStructurePlacementMode(ZoneClient client){
        try{
            
                DeedTemplate dT = DatabaseInterface.getDeedTemplateByID(this.getDeedTemplateID());
                if(dT.getLotsused() > client.getPlayer().getFreeLots())
                {
                    int lotReq = dT.getLotsused();
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"not_enough_lots",
                    		0l, 
                    		"",
                    		"",
                    		"",
                    		0l, 
                    		"",
                    		"",
                    		"",
                    		0l,
                    		"",
                    		"",
                    		"",
                    		lotReq,
                    		0f, false));

                    return;
                }
                if(client.getPlayer().getCellID() == 0 && !client.getPlayer().isMounted())
                {
                    //String sReason = "";
                    if(this.checkAreaForPlacement(client) == 1)
                    {
                        
                        //DeedTemplate dT = getServer().getGUI().getDB().getDeedTemplateByID(deedTemplateID);
                        int [] apl = dT.getAllowedplanetslist();
                        boolean allowedtoplace = false;
                        for(int i = 0; i < apl.length; i++)
                        {
                            if(apl[i] == client.getPlayer().getPlanetID()  || apl[i] == -1)
                            {
                                allowedtoplace = true;
                            }
                        }
                        if(dT!=null && allowedtoplace)
                        {                            
                            ItemTemplate t = client.getServer().getTemplateData(dT.getObject_iff_template_id());                
                            if(t!=null)
                            {
                                
                                client.insertPacket(PacketFactory.buildEnterStructurePlacementMode(client.getPlayer(),t.getIFFFileName()));
                                client.getPlayer().setLCurrentDeedInPlacementMode(this.getID());
                            }
                            else
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("Structure Template Error, Contact a CSR. Code:" + deedTemplateID));
                            }
                        }
                        else if(!allowedtoplace)
                        {
                            client.insertPacket(PacketFactory.buildChatSystemMessage(
                            		"player_structure",
                            		"cannot_use_deed_here",
                            		0l, 
                            		"",
                            		"",
                            		"",
                            		0l, 
                            		"",
                            		"",
                            		"",
                            		0l,
                            		"",
                            		"",
                            		"",
                            		0,
                            		0f, false));

                        }
                        else
                        {
                            client.insertPacket(PacketFactory.buildChatSystemMessage("Structure Template Error, Contact a CSR. Code:" + deedTemplateID));
                        }
                    }
                    else
                    {
                        client.insertPacket(PacketFactory.buildChatSystemMessage(
                        		"player_structure",
                        		"no_rights",
                        		0l, 
                        		"",
                        		"",
                        		"",
                        		0l, 
                        		"",
                        		"",
                        		"",
                        		0l,
                        		"",
                        		"",
                        		"",
                        		0,
                        		0f, false));

                    }     
                }
                else if(client.getPlayer().isMounted())
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"cant_place_mounted",
                    		0l, 
                    		"",
                    		"",
                    		"",
                    		0l, 
                    		"",
                    		"",
                    		"",
                    		0l,
                    		"",
                    		"",
                    		"",
                    		0,
                    		0f, false));
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"player_structure",
                    		"not_inside",
                    		0l, 
                    		"",
                    		"",
                    		"",
                    		0l, 
                    		"",
                    		"",
                    		"",
                    		0l,
                    		"",
                    		"",
                    		"",
                    		0,
                    		0f, false));

                }
            }catch(Exception e){
            System.out.println("Exception caught in Deed.enterStructurePlacementMode() " + e );
            e.printStackTrace();
        }
    }
    
    
    // TODO:  No-build zones!!!
    private int checkAreaForPlacement(ZoneClient client){
        
            float iRange = 32;
            ConcurrentHashMap<Long, SOEObject> vOAO = getServer().getAllObjects();
            Enumeration <SOEObject> oEnum = vOAO.elements();       
            int retcode = 1;
            
            while(oEnum.hasMoreElements())
            {
                SOEObject o = oEnum.nextElement();
                float iActualRange = ZoneServer.getRangeBetweenObjects(client.getPlayer(), o);                
                if(iActualRange <= iRange && o.getPlanetID() == client.getPlayer().getPlanetID() && o.getIsStaticObject() && !getServer().checkObjectBypassNoBuildZone(o))
                {                                            
                    retcode = 0;
                }           
            }        
        return retcode;
    }

    public int getISurplusMaintenance() {
        return iSurplusMaintenance;
    }

    public void setISurplusMaintenance(int iSurplusMaintenance) {
        this.iSurplusMaintenance = iSurplusMaintenance;
    }

    public int getISurplusPower() {
        return iSurplusPower;
    }

    public void setISurplusPower(int iSurplusPower) {
        this.iSurplusPower = iSurplusPower;
    }

    public int getIExtractionRate() {
        return iExtractionRate;
    }

    public void setIExtractionRate(int iExtractionRate) {
        this.iExtractionRate = iExtractionRate;
    }

    public int getIEnergyMaintenanceRate() {
        return iEnergyMaintenanceRate;
    }

    public void setIEnergyMaintenanceRate(int iEnergyMaintenanceRate) {
        this.iEnergyMaintenanceRate = iEnergyMaintenanceRate;
    }

    public long getLCurrentStructureID() {
        return lCurrentStructureID;
    }

    public void setLCurrentStructureID(long lCurrentStructureID) {
        this.lCurrentStructureID = lCurrentStructureID;
    }
    
    protected void destroyStructure(Structure theStructure, ZoneClient client) {
        try {
    		Player player = client.getPlayer();
    		long lPlayerCell = player.getCellID();
    		Cell cell = (Cell)server.getObjectFromAllObjects(lPlayerCell);
    		if (cell != null) {
    			if (theStructure.getCellsInBuilding().containsKey(lPlayerCell)) {
    				player.setCellID(0);
    				client.insertPacket(PacketFactory.buildUpdateContainmentMessage(player, null, -1));
    				cell.removeCellObject(player);
    				client.insertPacket(PacketFactory.buildUpdateTransformMessage(player));
    			}
    		}
            if(theStructure.getStructureSign()!=null)
            {
               server.removeObjectFromAllObjects(theStructure.getStructureSign(),true);
            }
            if(theStructure.getAdminTerminal()!=null)
            {
                server.removeObjectFromAllObjects(theStructure.getAdminTerminal(),true);
            }
            if(theStructure.getStructureBase()!=null)
            {
                server.removeObjectFromAllObjects(theStructure.getStructureBase(),true);
            }
            server.removeObjectFromAllObjects(theStructure,true);                
            server.removePlayerStructureFromAllStructures(theStructure.getID());
            Player sO = (Player)server.getObjectFromAllObjects(theStructure.getStructureOwnerID());   
            if(sO!=null)
            {
               sO.removePlayerStructure(theStructure);
               server.getGUI().getDB().updatePlayer(sO, false,false);
               client.insertPacket(PacketFactory.buildChatSystemMessage("player_structure", "structure_destroyed"));
               sO.deleteWaypoint(theStructure.getStructureWaypoint(),true); // But what happens if the                         
               sO.removeItemFromInventory(this);
               server.removeObjectFromAllObjects(this, false);
            }
    	} catch (Exception e) {
    		System.out.println("Error destroying structure/deed: " + e.toString());
    		e.printStackTrace();
    	}
    }
    
    protected byte[] redeedStructure(Structure theStructure, ZoneServer server) {
        if (lCurrentStructureID != theStructure.getID()) {
        	lCurrentStructureID = theStructure.getID();
        }
    	try{
                // All right, why is s null?
                Deed d = (Deed)server.getObjectFromAllObjects(theStructure.getDeedID());
                d.setISurplusMaintenance(theStructure.getMaintenancePool() - d.getRedeedfee());
                if(theStructure.usesPower())
                {
                    d.setISurplusPower(theStructure.getPowerPool());
                }
                if(theStructure instanceof Harvester)
                {
                	Harvester h = (Harvester)theStructure;
                    d.setIExtractionRate(h.getBaseExtractionRate());
                    d.setIOutputHopperSize(h.getIOutputHopperSize() / 100);
                }
                d.setIsPlaced(false);                
                if(theStructure.getStructureSign()!=null)
                {
                   server.removeObjectFromAllObjects(theStructure.getStructureSign(),true);
                }
                if(theStructure.getAdminTerminal()!=null)
                {
                    server.removeObjectFromAllObjects(theStructure.getAdminTerminal(),true);
                }
                if(theStructure.getStructureBase()!=null)
                {
                    server.removeObjectFromAllObjects(theStructure.getStructureBase(),true);
                }
                server.removeObjectFromAllObjects(theStructure,true);                
                server.removePlayerStructureFromAllStructures(theStructure.getID());
                Player sO = (Player)server.getObjectFromAllObjects(theStructure.getStructureOwnerID());   
                if(sO!=null)
                {
                	sO.removePlayerStructure(theStructure);
                	server.getGUI().getDB().updatePlayer(sO, false,false);
                                                                         
                	sO.spawnItem(d);
                	sO.deleteWaypoint(theStructure.getStructureWaypoint(),true);                                                             
                	return PacketFactory.buildChatSystemMessage("player_structure", "deed_reclaimed");
                }
        }catch(Exception e){
            System.out.println("Exception caught in Deed.redeedStructure " + e);
            e.printStackTrace();
        }     
        return null;
    	
    }
    protected void redeedStructure(Structure theStructure, ZoneClient client){
    	byte[] sysMsg = redeedStructure(theStructure, client.getServer());
    	if (sysMsg != null) {
    		client.insertPacket(sysMsg);
    	}
    }

    public int getIInputHopperSize() {
        return iInputHopperSize;
    }

    public void setIInputHopperSize(int iInputHopperSize) {
        this.iInputHopperSize = iInputHopperSize;
    }

    public int getIOutputHopperSize() {
        return iOutputHopperSize;
    }

    public void setIOutputHopperSize(int iOutputHopperSize) {
        this.iOutputHopperSize = iOutputHopperSize;
    }

    public int getILotSize() {
        return iLotSize;
    }

    public void setILotSize(int iLotSize) {
        this.iLotSize = iLotSize;
    }

    public int getRedeedfee() {
        return redeedfee;
    }
    

    private void placeCamp(ZoneClient client){
        try{
            //System.out.println("Camp Deed Template ID: " + this.getTemplateID());
            //System.out.println("Camp Placement, Required Skill: " + DatabaseInterface.getTemplateDataByID(this.getTemplateID()).getRequiredSkillID());
            if(client.getPlayer().getCellID() == 0)
            {
                if(client.getPlayer().getCurrentCampObject()==null)
                {
                    if(client.getPlayer().hasSkill(DatabaseInterface.getTemplateDataByID(this.getTemplateID()).getRequiredSkillID()))
                    {
                        Vector<SOEObject> vNearbyObjects = server.getWorldObjectsAroundObject(client.getPlayer());
                        boolean hasClearance = true;
                        for(int i = 0; i < vNearbyObjects.size();i++)
                        {
                            SOEObject o = vNearbyObjects.get(i);
                            if((o instanceof Camp) || (o instanceof Structure) || (o instanceof Lair) || o.getIsStaticObject())
                            {
                                if(ZoneServer.isInRange(client.getPlayer(), o, 32))
                                {
                                    hasClearance = false;
                                }
                            }
                            else if(o instanceof Terminal)
                            {
                                Terminal tt = (Terminal)o;
                                int iTermType = tt.getTerminalType();
                                switch(iTermType)
                                {
                                    case Constants.TERMINAL_TYPES_NOBUILD_ZONE_64:
                                    {
                                        if(ZoneServer.isInRange(client.getPlayer(), o, 64))
                                        {
                                            hasClearance = false;
                                        }
                                        break;
                                    }
                                    case Constants.TERMINAL_TYPES_NOBUILD_ZONE_128:
                                    {
                                        if(ZoneServer.isInRange(client.getPlayer(), o, 128))
                                        {
                                            hasClearance = false;
                                        }
                                        break;
                                    }
                                    case Constants.TERMINAL_TYPES_NOBUILD_ZONE_768:
                                    {
                                        if(ZoneServer.isInRange(client.getPlayer(), o, 768)) 
                                        {
                                            hasClearance = false;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        if(hasClearance)
                        {
                            client.insertPacket(PacketFactory.buildChatSystemMessage("camp", "sys_deploy"));
                            DeedTemplate dT = DatabaseInterface.getDeedTemplateByID(this.getDeedTemplateID());
                            Camp c = new Camp(dT.getObject_iff_template_id(),(long)dT.getObject_base_template_id());
                            c.setCampOwner(client.getPlayer());
                            c.setID(server.getNextObjectID());
                            c.setX(client.getPlayer().getX());
                            c.setY(client.getPlayer().getY());
                            c.setZ(client.getPlayer().getZ());
                            c.setOrientationN(0);//(client.getPlayer().getOrientationN());
                            c.setOrientationS(0);//(client.getPlayer().getOrientationS());
                            c.setOrientationE(0);//(client.getPlayer().getOrientationE());
                            c.setOrientationW(1);//(client.getPlayer().getOrientationW());
                            c.setPlanetID(client.getPlayer().getPlanetID());
                            c.setDeedTemplate(dT);
                            c.setICampXPMultiplier(dT.getIXPMultiplier());
                            c.makeTerminal(server.getNextObjectID(), dT,server);
                            c.setName(client.getPlayer().getFirstName() + "'s Camp", false);                            
                            client.getPlayer().setCurrentCampObject(c);
                            server.addObjectToAllObjects(c, true, false);                            
                            client.getPlayer().getInventory().removeLinkedObject(this);
                            client.getPlayer().removeItemFromInventory(this);
                            client.getPlayer().despawnItem(this);
                            server.removeObjectFromAllObjects(this, true);
                            client.getPlayer().spawnItem(c);
                        }
                        else
                        {
                            client.insertPacket(PacketFactory.buildChatSystemMessage("You are too close to other structures please move elsewhere."));
                        }
                    }
                    else
                    {
                        client.insertPacket(PacketFactory.buildChatSystemMessage("You lack the skill to deploy this camp kit."));
                    }
                }
                else
                {
                    client.insertPacket(PacketFactory.buildChatSystemMessage("You already have a camp, disband the first one to place another."));
                }
            }
            else
            {
                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot place a camp while indoors."));
            }
        }catch(Exception e){
            DataLog.logException("Exception while placing a Camp", "Deed", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
    }

    private void generatePetFromDeed(ZoneClient client){
        try{
            boolean generatepet = true;



            if(generatepet)
            {
                client.insertPacket(PacketFactory.buildChatSystemMessage("Generating Pet"));
                DeedTemplate dT = DatabaseInterface.getDeedTemplateByID(this.getDeedTemplateID());

                // Make the Datapad intangible Item for this new Pet
                IntangibleObject petDPIcon = new IntangibleObject();
                petDPIcon.setTemplateID(dT.getObject_base_template_id());
                petDPIcon.setID(client.getServer().getNextObjectID());
                petDPIcon.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
                client.getServer().addObjectToAllObjects(petDPIcon, false,false);
                petDPIcon.setCustomizationData(null);
                client.getPlayer().getDatapad().addIntangibleObject(petDPIcon);
                petDPIcon.setContainer(client.getPlayer().getDatapad(), -1, false);
                client.getPlayer().spawnItem(petDPIcon);

                CreaturePet pet = new CreaturePet();
                pet.setID(client.getServer().getNextObjectID());
                pet.setPVPStatus(Constants.PVP_STATUS_IS_NORMAL_NON_ATTACKABLE);
                pet.setCREO3Bitmask(Constants.BITMASK_CREO3_NPC);
                pet.setFactionID(client.getPlayer().getFactionID());
                pet.setTemplateID(dT.getObject_iff_template_id());
                pet.setMaster(client.getPlayer());
                pet.setCanGrow(false);
                pet.setGrowthLevel(1.0f);
                pet.setFullGrown(true);
                if(this.getPethams() == null)
                {
                    pet.setHam(dT.getPethams());
                }
                else
                {
                    pet.setHam(this.getPethams());
                }
                //pet.getVTrainedCommands().add(11);//store only trained.
                if(dT.isBase_required())
                {
                    pet.setIsAnimal(true);
                }
                pet.setDatapadControlIcon(petDPIcon);
                petDPIcon.setAssociatedCreature(pet);
                client.insertPacket(PacketFactory.buildChatSystemMessage("A Control Device has been added to your DataPad"));
                client.getPlayer().getInventory().removeLinkedObject(this);
                client.getPlayer().removeItemFromInventory(this);
                client.getPlayer().despawnItem(this);
                client.getServer().removeObjectFromAllObjects(this, true);
            }

        }catch(Exception e){
            DataLog.logException("Exception in generatePetFromDeed", "CreaturePet", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
    }

    public int[] getPethams() {
        return pethams;
    }

    public void setPethams(int[] pethams) {
        this.pethams = pethams;
    }

	public int experiment(long iExperimentalIndex, int numExperimentationPointsUsed, Player thePlayer) {
		// Depends on what it is.
		return 0;
	}


}
