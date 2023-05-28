package sample;

/**
 * Contains fields from Part class an addition to a field and methods for a part's Machine ID
 *
 * @author Long Tran
 */
public class InHouse extends Part {
    /**
     * The machine ID of in-house part
     * */
    private int machineId;

    /**
     * The parts produced in-house
     *
     * @param id the id of the part
     * @param name the name of the part
     * @param price the price of the part
     * @param stock the amount of the part available in inventory
     * @param min the minimum amount of the part available in inventory
     * @param max the maximum amount of the part available in inventory
     * @param machineId the machine id of the part
     * */
        public InHouse(int id, String name, double price, int stock, int min, int max, int machineId){
        super(id, name, price, stock, min, max);
        setMachineId(machineId);
    }

    /**
     * Adds or changes the machine ID of in-house part
     *
     * @param machineId the machine ID of in-house part */
    public void setMachineId(int machineId){
        this.machineId = machineId;
    }

    /**
     * Obtains the machine ID of in-house part
     *
     * @return the machine ID*/
    public int getMachineId(){
        return machineId;
    }
}