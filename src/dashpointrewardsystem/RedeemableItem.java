package dashpointrewardsystem;

public class RedeemableItem extends Reward {

    private String itemCode;
    private String itemCategory;
    private int stockAmt;
    private int redeemedAmt;
    private boolean availability;
    private String minimumTier;

    //Constructor
    public RedeemableItem() {
        super();
    }

    public RedeemableItem(String itemCode, String rewardName, String itemCategory, int ptRequired, int stockAmt, int redeemedAmt, boolean availability, String minimumTier) {
        super(rewardName, ptRequired);
        this.itemCode = itemCode;
        this.itemCategory = itemCategory;
        this.stockAmt = stockAmt;
        this.redeemedAmt = redeemedAmt;
        this.availability = availability;
        this.minimumTier = minimumTier;
    }

    //Getter
    public String getItemCode() {
        return itemCode;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public int getRedeemedAmt() {
        return redeemedAmt;
    }

    public int getStockAmt() {
        return stockAmt;
    }

    public boolean getAvailability() {
        return availability;
    }

    public String getMinimumTier() {
        return minimumTier;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    //Setter
    public void setMinimumTier(String minimumTier) {
        this.minimumTier = minimumTier;
    }

    public RedeemableItem setRedeemedAmt(int redeemedAmt) {
        this.redeemedAmt = redeemedAmt;
        return this;
    }

    public RedeemableItem setStockAmt(int stockAmt) {
        this.stockAmt = stockAmt;
        return this;
    }

    @Override
    public String toString() {
        return "\nItem Code: " + itemCode + "\nItem Category: " + itemCategory + super.toString() + "\nStock Amount: " + stockAmt + "\nRedeemed Amount: " + redeemedAmt + "Availability: " + availability + "\nMinimum Tier: " + minimumTier;
    }

}
