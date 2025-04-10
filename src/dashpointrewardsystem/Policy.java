package dashpointrewardsystem;

public class Policy {

    private String policyIndex;
    private String policyTitle;
    private String policyContent;

    //Constructor
    public Policy() {

    }

    public Policy(String policyIndex, String policyTitle, String policyContent) {
        this.policyIndex = policyIndex;
        this.policyTitle = policyTitle;
        this.policyContent = policyContent;
    }

    //Getter
    public String getPolicyIndex() {
        return policyIndex;
    }

    public String getPolicyTitle() {
        return policyTitle;
    }

    public String getPolicyContent() {
        return policyContent;
    }

    //Setter
    public void setPolicyIndex(String policyIndex) {
        this.policyIndex = policyIndex;
    }

    public void setPolicyTitle(String policyTitle) {
        this.policyTitle = policyTitle;
    }

    public void setPolicyContent(String policyContent) {
        this.policyContent = policyContent;
    }

    @Override
    public String toString() {
        return policyTitle + ": " + policyContent + "\n";
    }
}
