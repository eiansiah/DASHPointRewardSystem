package dashpointrewardsystem;

public class CompanyDetails {

    private String companyDetailsIndex;
    private String companyDetailsTitle;
    private String companyDetailsContent;

    public CompanyDetails() {

    }

    public CompanyDetails(String companyDetailsIndex, String companyDetailsTitle, String companyDetailsContent) {
        this.companyDetailsIndex = companyDetailsIndex;
        this.companyDetailsTitle = companyDetailsTitle;
        this.companyDetailsContent = companyDetailsContent;

    }

    //Getter
    public String getCompanyDetailsIndex() {
        return companyDetailsIndex;
    }

    public String getCompanyDetailsTitle() {
        return companyDetailsTitle;
    }

    public String getCompanyDetailsContent() {
        return companyDetailsContent;
    }

    //Setter
    public void setCompanyDetailsIndex(String companyDetailsIndex) {
        this.companyDetailsIndex = companyDetailsIndex;
    }

    public void setCompanyDetailsTitle(String companyDetailsTitle) {
        this.companyDetailsTitle = companyDetailsTitle;
    }

    public void setCompanyDetailsContent(String companyDetailsContent) {
        this.companyDetailsContent = companyDetailsContent;
    }

    @Override
    public String toString() {
        return companyDetailsTitle + ": " + companyDetailsContent + "\n";
    }
}
