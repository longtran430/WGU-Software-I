package sample;

/**
 * Contains fields from Part class an addition to a field and methods for a part's Company Name
 *
 * @author Long Tran
 */
public class Outsourced extends Part{
    /**
     *  The name of company of outsourced part
     * */
    private String companyName;

    /**
     * The parts outsourced from another company
     *
     * @param id the id of the part
     * @param name the name of the part
     * @param price the price of the part
     * @param stock the amount of the part available in inventory
     * @param min the minimum amount of the part available in inventory
     * @param max the maximum amount of the part available in inventory
     * @param companyName the name of the company of the outsourced part
     * */
    public Outsourced(int id, String name, double price, int stock, int min, int max, String companyName){
        super(id, name, price, stock, min, max);
        setCompanyName(companyName);
    }

    /**
     * Adds or changes the company name of the Outsourced object
     *
     * @param companyName the name of the company of the outsourced part*/
    public void setCompanyName(String companyName){
        this.companyName = companyName;
    }

    /**
     * Obtains the name of the company of the Outsourced object
     *
     * @return the company name*/
    public String getCompanyName(){
        return companyName;
    }
}
