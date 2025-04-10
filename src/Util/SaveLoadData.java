package Util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import dashpointrewardsystem.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

public class SaveLoadData {

    private static final String url = "jdbc:mysql://pointssystem.clcw44g4knvm.ap-southeast-2.rds.amazonaws.com:3306/fastfood";
    private static final String username = "admin";
    private static final String password = "dCcfrNmdpYeoxhwaO6TV";
    private static final String driver = "com.mysql.cj.jdbc.Driver";

    private static ComboPooledDataSource dataSource;

    public static void startConnection() {
        try {
            dataSource = new ComboPooledDataSource();
            dataSource.setDriverClass(driver);
            dataSource.setJdbcUrl(url);
            dataSource.setUser(username);
            dataSource.setPassword(password);

            dataSource.setMinPoolSize(3);
            dataSource.setMaxPoolSize(8);
            dataSource.setAcquireIncrement(1);
        } catch (Exception e) {
        }
    }

    private static ArrayList<ArrayList<String>> runSelectSQL(String sql) {
        try {
            Connection connection = dataSource.getConnection();

            Statement statement = connection.createStatement();

            //Run sql
            ResultSet resultSet = statement.executeQuery(sql);

            ArrayList<ArrayList<String>> data = new ArrayList<>();

            while (resultSet.next()) {
                ArrayList<String> currentResult = new ArrayList<>();

                for (int i = 1; i < resultSet.getMetaData().getColumnCount() + 1; i++) {
                    currentResult.add(resultSet.getString(i));
                }

                data.add(currentResult);
            }

            connection.close();

            return data;
        } catch (Exception e) {
            System.err.println(e);
        }

        return new ArrayList<>();
    }

    private static void runInsertUpdateSQL(String sql) {
        try {
            Connection connection = dataSource.getConnection();

            Statement statement = connection.createStatement();

            //Run sql
            statement.executeUpdate(sql);

            connection.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static class SLDLoginRegister {

        /**
         * Check ID and password, match any data at Customer and Staff database,
         *
         * @return Customer --> match Customer / Staff --> match Staff / false
         * --> no match
         */
        public static String loginValidation(String id, String password) throws SQLException {
            ArrayList<ArrayList<String>> resultUser = runSelectSQL("SELECT * FROM CUSTOMER WHERE CustomerID=\"" + id + "\" AND CPassword=\"" + password + "\"");

            if (!resultUser.isEmpty()) {
                return "Customer";
            }

            ArrayList<ArrayList<String>> resultStaff = runSelectSQL("SELECT * FROM STAFF WHERE StaffID=\"" + id + "\" AND SPassword=\"" + password + "\"");

            if (!resultStaff.isEmpty()) {
                return "Staff";
            }

            //check if staff is top management
            return "false";
        }

        /**
         * Check whether an email has been use
         *
         * @return true if used, or false if not used
         */
        public static boolean registerEmailDuplication(String email) {
            ArrayList<ArrayList<String>> resultUser = runSelectSQL("SELECT * FROM CUSTOMER WHERE Email=\"" + email + "\"");

            if (!resultUser.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }

        private static String uniqueCustomerIDGenerator() {
            String CID;

            ArrayList<ArrayList<String>> result = runSelectSQL("SELECT CustomerID FROM CUSTOMER");

            Random rand = new Random();

            while (true) {
                CID = "C" + String.format("%05d", rand.nextInt(99999));

                if (!result.get(0).contains(CID)) {
                    return CID;
                }
            }
        }

        /**
         * Insert new customer registration info to database
         */
        public static String registerEntry(String name, String contact, String email, String password) {
            String sql = String.format("INSERT INTO CUSTOMER VALUES (\"%s\", \"%s\", \"%s\", \"%s\", DEFAULT, \"%s\", %d, \"%s\",DEFAULT,DEFAULT)", uniqueCustomerIDGenerator(), name, email, contact, "Member", 0, password);
            runInsertUpdateSQL(sql);

            ArrayList<ArrayList<String>> resultUser = runSelectSQL("SELECT * FROM CUSTOMER WHERE Email=\"" + email + "\"");

            return resultUser.get(0).get(0);
        }
    }

    public static class SLDProfile {

        /**
         * Need to use "get(indexPosition)" to get column data
         *
         * @return the specific customer ArrayList
         */
        public static ArrayList<String> getCustomerProfile(String customerID) {
            return runSelectSQL("SELECT * FROM CUSTOMER WHERE CustomerID=\"" + customerID + "\"").get(0);
        }

        public static void changeCustomerName(String customerID, String name) {
            runInsertUpdateSQL("UPDATE CUSTOMER SET CustomerName = \"" + name + "\" WHERE CustomerID = \"" + customerID + "\"");
        }

        public static void changeCustomerEmail(String customerID, String email) {
            runInsertUpdateSQL("UPDATE CUSTOMER SET Email = \"" + email + "\" WHERE CustomerID = \"" + customerID + "\"");
        }

        public static void changeCustomerContact(String customerID, String contact) {
            runInsertUpdateSQL("UPDATE CUSTOMER SET PhoneNumber = \"" + contact + "\" WHERE CustomerID = \"" + customerID + "\"");
        }

        public static void changeCustomerPassword(String customerID, String password) {
            runInsertUpdateSQL("UPDATE CUSTOMER SET CPassword = \"" + password + "\" WHERE CustomerID = \"" + customerID + "\"");
        }

        public static void deleteCustomerAccount(String customerID) {
            runInsertUpdateSQL("DELETE FROM CUSTOMER WHERE CustomerID = \"" + customerID + "\"");
        }
    }

    public static class SLDStaff {

        /**
         * Need to use "get(indexPosition)" to get column data
         *
         * @return the specific staff ArrayList
         */
        public static ArrayList<String> getStaffProfile(String staffID) {
            ArrayList<ArrayList<String>> data = runSelectSQL("SELECT * FROM STAFF WHERE StaffID=\"" + staffID + "\"");

            try{
                return data.get(0);
            }catch (Exception e){
                return null;
            }
        }

        /**
         * @return All normal staff list excluding top management
         */
        public static ArrayList<Staff> getStaffList() {
            ArrayList<ArrayList<String>> result = runSelectSQL("SELECT * FROM STAFF WHERE AccessLevel = \"Normal\"");
            ArrayList<Staff> staffList = new ArrayList<>();

            for (ArrayList<String> data : result) {
                staffList.add(new Staff(data.get(0), data.get(1), data.get(2), data.get(3), LocalDate.parse(data.get(4), DateTimeFormatter.ofPattern("yyyy-MM-dd")), data.get(5)));
            }

            return staffList;
        }

        /**
         * @return searched staff list excluding top management
         */
        public static ArrayList<ArrayList<String>> searchStaffIDAndName(String searchString) {
            return runSelectSQL("SELECT * FROM STAFF WHERE AccessLevel = \"Normal\" AND ( StaffID LIKE \"%" + searchString + "%\" OR StaffName LIKE \"%" + searchString + "%\" )");
        }

        private static String uniqueStaffIDGenerator() {
            String SID;

            ArrayList<ArrayList<String>> result = runSelectSQL("SELECT StaffID FROM STAFF");

            Random rand = new Random();

            while (true) {
                SID = "S" + String.format("%05d", rand.nextInt(99999));

                if (!result.get(0).contains(SID)) {
                    return SID;
                }
            }
        }

        /**
         * Insert new staff registration info to database
         */
        public static void createStaff(String name, String accessLevel, String contact, String password) {
            String sql = String.format("INSERT INTO STAFF VALUES (\"%s\", \"%s\", \"%s\", \"%s\", DEFAULT, \"%s\")", uniqueStaffIDGenerator(), name, accessLevel, contact, password);
            runInsertUpdateSQL(sql);
        }

        /**
         * delete staff excluding top management
         */
        public static void deleteStaffAccount(String staffID) {
            runInsertUpdateSQL("DELETE FROM STAFF WHERE StaffID = \"" + staffID + "\"");
        }

        public static void changeStaffPassword(String staffID, String password) {
            runInsertUpdateSQL("UPDATE STAFF SET SPassword = \"" + password + "\" WHERE StaffID = \"" + staffID + "\"");
        }
    }

    public static class SLDEventRoom {

        public static ArrayList<EventRoom> getAllRoom() {
            ArrayList<ArrayList<String>> data = runSelectSQL("SELECT * FROM EVENTROOM");
            ArrayList<EventRoom> eventRoom = new ArrayList<>();

            for (ArrayList<String> row : data) {
                eventRoom.add(new EventRoom(row.get(0), row.get(1), row.get(2), Integer.valueOf(row.get(3)), Integer.valueOf(row.get(4))));
            }

            return eventRoom;
        }

        public static EventRoom getOnlyOne(String roomNum) {
            ArrayList<ArrayList<String>> data = runSelectSQL("SELECT * FROM EVENTROOM WHERE EVT_RM_NUM=\"" + roomNum + "\"");
            ArrayList<EventRoom> eventRoom = new ArrayList<>();

            for (ArrayList<String> row : data) {
                eventRoom.add(new EventRoom(row.get(0), row.get(1), row.get(2), Integer.valueOf(row.get(3)), Integer.valueOf(row.get(4))));
            }

            return eventRoom.get(0);
        }

        public static void customerReserveRoom(String roomNum, String res_Date, String cust_ID, int numOfExtraPax) {
            runInsertUpdateSQL(String.format("INSERT INTO EVENT_RESERVATION VALUES (NULL, \"%s\", \"%s\", DEFAULT,\"%s\", %d)", roomNum, res_Date, cust_ID, numOfExtraPax));
            runInsertUpdateSQL(""
                    + "UPDATE CUSTOMER "
                    + "SET BalancePoint = BalancePoint - ((SELECT EVT_PT_REQUIRED FROM EVENTROOM WHERE EVT_RM_NUM = \"" + roomNum + "\")+60*" + numOfExtraPax + ") WHERE CustomerID = \"" + cust_ID + "\"");
        }
    }

    public static class SLDPointAndItem {

        /**
         * Valid = true, invalid = false
         */
        public static boolean checkPointsValidity(String customerID) {
            ArrayList<ArrayList<String>> oldData = runSelectSQL("SELECT BalancePoint FROM CUSTOMER WHERE CustomerID=\"" + customerID + "\"");

            runInsertUpdateSQL(
                    "UPDATE CUSTOMER SET BalancePoint=0 WHERE DATEDIFF(CURRENT_DATE(),"
                    + "(SELECT * FROM ("
                    + " SELECT MAX(PointEarnDate) FROM CUSTOMER C"
                    + " INNER JOIN POINT_EARNING PE ON C.CustomerID=PE.CustomerID"
                    + " WHERE C.CustomerID=\"" + customerID + "\""
                    + " ORDER BY PointEarnDate DESC"
                    + ") as Sub)) > 30"
            );

            ArrayList<ArrayList<String>> newData = runSelectSQL("SELECT BalancePoint FROM CUSTOMER WHERE CustomerID=\"" + customerID + "\"");

            return oldData.get(0).get(0).equals(newData.get(0).get(0));
        }

        public static String checkTierValidity(Customer customer) {
            String getTierPointSQL = "SELECT TierPoint FROM CUSTOMER WHERE CustomerID=\"" + customer.getUserID() + "\"";

            LocalDate refreshDate = LocalDate.parse(runSelectSQL("SELECT TierRefreshDate FROM CUSTOMER WHERE CustomerID=\"" + customer.getUserID() + "\"").get(0).get(0), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            if (refreshDate.isAfter(LocalDate.now())) {
                return "";
            }

            if (customer.getLoyaltyTier().equals("Gold")) {
                runInsertUpdateSQL(
                        "UPDATE CUSTOMER SET TierPoint=(SELECT * FROM (\n"
                        + "SELECT COALESCE(SUM(PtEarned), 0) FROM POINT_EARNING \n"
                        + "WHERE (PointEarnDate BETWEEN DATE_SUB(NOW(), INTERVAL 60 DAY) AND NOW()) \n"
                        + "AND CustomerID=\"" + customer.getUserID() + "\"\n"
                        + ") AS Sub) WHERE CustomerID=\"" + customer.getUserID() + "\""
                );

                int tierPoint = Integer.parseInt(runSelectSQL(getTierPointSQL).get(0).get(0));

                if (tierPoint < 250) {
                    runInsertUpdateSQL("UPDATE CUSTOMER SET TierRefreshDate=TierRefreshDate + INTERVAL 40 DAY, LoyaltyTier=\"Silver\" WHERE CustomerID=\"" + customer.getUserID() + "\"");
                    customer.setLoyaltyTier("Silver");
                    customer.setTierPoint(tierPoint);

                    return GeneralFunction.color.ANSI_RED + "Demoted to Silver" + GeneralFunction.color.ANSI_RESET;
                } else {
                    runInsertUpdateSQL("UPDATE CUSTOMER SET TierRefreshDate=TierRefreshDate + INTERVAL 60 DAY WHERE CustomerID=\"" + customer.getUserID() + "\"");

                    return "";
                }
            } else if (customer.getLoyaltyTier().equals("Silver")) {
                runInsertUpdateSQL(
                        "UPDATE CUSTOMER SET TierPoint=(SELECT * FROM (\n"
                        + "SELECT COALESCE(SUM(PtEarned), 0) FROM POINT_EARNING \n"
                        + "WHERE (PointEarnDate BETWEEN DATE_SUB(NOW(), INTERVAL 40 DAY) AND NOW()) \n"
                        + "AND CustomerID=\"" + customer.getUserID() + "\"\n"
                        + ") AS Sub) WHERE CustomerID=\"" + customer.getUserID() + "\""
                );

                int tierPoint = Integer.parseInt(runSelectSQL(getTierPointSQL).get(0).get(0));

                if (tierPoint < 100) {
                    runInsertUpdateSQL("UPDATE CUSTOMER SET TierRefreshDate=TierRefreshDate + INTERVAL 30 DAY, LoyaltyTier=\"Bronze\" WHERE CustomerID=\"" + customer.getUserID() + "\"");
                    customer.setLoyaltyTier("Bronze");
                    customer.setTierPoint(tierPoint);

                    return GeneralFunction.color.ANSI_RED + "Demoted to Bronze" + GeneralFunction.color.ANSI_RESET;
                } else if (tierPoint >= 888) {
                    runInsertUpdateSQL("UPDATE CUSTOMER SET TierRefreshDate=TierRefreshDate + INTERVAL 60 DAY, LoyaltyTier=\"Gold\" WHERE CustomerID=\"" + customer.getUserID() + "\"");
                    customer.setLoyaltyTier("Gold");
                    customer.setTierPoint(tierPoint);

                    return GeneralFunction.color.ANSI_GREEN + "Promoted to Gold" + GeneralFunction.color.ANSI_RESET;
                } else {
                    runInsertUpdateSQL("UPDATE CUSTOMER SET TierRefreshDate=TierRefreshDate + INTERVAL 40 DAY WHERE CustomerID=\"" + customer.getUserID() + "\"");

                    return "";
                }
            } else if (customer.getLoyaltyTier().equals("Bronze")) {
                runInsertUpdateSQL(
                        "UPDATE CUSTOMER SET TierPoint=(SELECT * FROM (\n"
                        + "SELECT COALESCE(SUM(PtEarned), 0) FROM POINT_EARNING \n"
                        + "WHERE (PointEarnDate BETWEEN DATE_SUB(NOW(), INTERVAL 30 DAY) AND NOW()) \n"
                        + "AND CustomerID=\"" + customer.getUserID() + "\"\n"
                        + ") AS Sub) WHERE CustomerID=\"" + customer.getUserID() + "\""
                );

                int tierPoint = Integer.parseInt(runSelectSQL(getTierPointSQL).get(0).get(0));

                if (tierPoint < 20) {
                    runInsertUpdateSQL("UPDATE CUSTOMER SET TierRefreshDate=TierRefreshDate + INTERVAL 30 DAY, LoyaltyTier=\"Member\" WHERE CustomerID=\"" + customer.getUserID() + "\"");
                    customer.setLoyaltyTier("Member");
                    customer.setTierPoint(tierPoint);

                    return GeneralFunction.color.ANSI_RED + "Demoted to Bronze" + GeneralFunction.color.ANSI_RESET;
                } else if (tierPoint >= 666) {
                    runInsertUpdateSQL("UPDATE CUSTOMER SET TierRefreshDate=TierRefreshDate + INTERVAL 40 DAY, LoyaltyTier=\"Silver\" WHERE CustomerID=\"" + customer.getUserID() + "\"");
                    customer.setLoyaltyTier("Silver");
                    customer.setTierPoint(tierPoint);

                    return GeneralFunction.color.ANSI_GREEN + "Promoted to Silver" + GeneralFunction.color.ANSI_RESET;
                } else {
                    runInsertUpdateSQL("UPDATE CUSTOMER SET TierRefreshDate=TierRefreshDate + INTERVAL 30 DAY WHERE CustomerID=\"" + customer.getUserID() + "\"");

                    return "";
                }
            } else {
                runInsertUpdateSQL(
                        "UPDATE CUSTOMER SET TierPoint=(SELECT * FROM (\n"
                        + "SELECT COALESCE(SUM(PtEarned), 0) FROM POINT_EARNING \n"
                        + "WHERE (PointEarnDate BETWEEN DATE_SUB(NOW(), INTERVAL 30 DAY) AND NOW()) \n"
                        + "AND CustomerID=\"" + customer.getUserID() + "\"\n"
                        + ") AS Sub) WHERE CustomerID=\"" + customer.getUserID() + "\""
                );

                int tierPoint = Integer.parseInt(runSelectSQL(getTierPointSQL).get(0).get(0));

                if (tierPoint >= 20) {
                    runInsertUpdateSQL("UPDATE CUSTOMER SET TierRefreshDate=TierRefreshDate + INTERVAL 30 DAY, LoyaltyTier=\"Bronze\" WHERE CustomerID=\"" + customer.getUserID() + "\"");
                    customer.setLoyaltyTier("Bronze");
                    customer.setTierPoint(tierPoint);

                    return GeneralFunction.color.ANSI_GREEN + "Promoted to Bronze" + GeneralFunction.color.ANSI_RESET;
                } else {
                    runInsertUpdateSQL("UPDATE CUSTOMER SET TierRefreshDate=TierRefreshDate + INTERVAL 30 DAY WHERE CustomerID=\"" + customer.getUserID() + "\"");

                    return "";
                }
            }
        }

        /**
         * Conversion not included, add point only
         */
        public static void addPoint(String customerID, String staffID, int point) {
            runInsertUpdateSQL("UPDATE CUSTOMER SET BalancePoint = BalancePoint + \"" + point + "\" WHERE CustomerID = \"" + customerID + "\"");
            runInsertUpdateSQL("UPDATE CUSTOMER SET TierPoint = TierPoint + \"" + point + "\" WHERE CustomerID = \"" + customerID + "\"");
            runInsertUpdateSQL(String.format("INSERT INTO POINT_EARNING VALUES (NULL, DEFAULT, \"%s\", \"%s\", %d)", customerID, staffID, point));
        }

        /**
         * All item included
         *
         * @return ArrayList<RedeemableItem>
         */
        public static ArrayList<RedeemableItem> getAllItem() {
            ArrayList<ArrayList<String>> data = runSelectSQL("SELECT * FROM ITEM");
            ArrayList<RedeemableItem> redeemableItemList = new ArrayList<>();

            for (ArrayList<String> row : data) {
                redeemableItemList.add(new RedeemableItem(row.get(0), row.get(1), row.get(2), Integer.parseInt(row.get(3)), Integer.parseInt(row.get(4)), Integer.parseInt(row.get(7)), Integer.parseInt(row.get(5)) == 1, row.get(6)));
            }

            return redeemableItemList;
        }

        /**
         * All item included
         *
         * @return ArrayList<RedeemableItem>
         */
        public static ArrayList<RedeemableItem> getSpecificItem(String itemCode) {
            ArrayList<ArrayList<String>> data = runSelectSQL("SELECT * FROM ITEM WHERE ItemCode=\"" + itemCode + "\"");
            ArrayList<RedeemableItem> redeemableItemList = new ArrayList<>();

            for (ArrayList<String> row : data) {
                redeemableItemList.add(new RedeemableItem(row.get(0), row.get(1), row.get(2), Integer.parseInt(row.get(3)), Integer.parseInt(row.get(4)), Integer.parseInt(row.get(7)), Integer.parseInt(row.get(5)) == 1, row.get(6)));
            }

            return redeemableItemList;
        }

        public static RedeemableItem getOnlyOneItem(String itemCode) {
            ArrayList<ArrayList<String>> data = runSelectSQL("SELECT * FROM ITEM WHERE ItemCode=\"" + itemCode + "\"");
            ArrayList<RedeemableItem> redeemableItemList = new ArrayList<>();

            for (ArrayList<String> row : data) {
                redeemableItemList.add(new RedeemableItem(row.get(0), row.get(1), row.get(2), Integer.parseInt(row.get(3)), Integer.parseInt(row.get(4)), Integer.parseInt(row.get(7)), Integer.parseInt(row.get(5)) == 1, row.get(6)));
            }

            return redeemableItemList.get(0);
        }

        /**
         * All item included and sort by off the self, tier and category
         *
         * @return ArrayList<RedeemableItem>
         */
        public static ArrayList<RedeemableItem> getAllItemSortStatusTierCategory() {
            ArrayList<ArrayList<String>> data = runSelectSQL("SELECT * FROM ITEM ORDER BY Availability DESC, MinimumTier, ItemCategory");
            ArrayList<RedeemableItem> redeemableItemList = new ArrayList<>();

            for (ArrayList<String> row : data) {
                redeemableItemList.add(new RedeemableItem(row.get(0), row.get(1), row.get(2), Integer.parseInt(row.get(3)), Integer.parseInt(row.get(4)), Integer.parseInt(row.get(7)), Integer.parseInt(row.get(5)) == 1, row.get(6)));
            }

            return redeemableItemList;
        }

        /**
         * All item included, include not enough points item and not fulfill
         * minimum tier requirement, exclude Off the shelf item and stock = 0
         *
         * @return ArrayList<RedeemableItem>
         */
        public static ArrayList<RedeemableItem> getAllItemExcludeDisable() {
            ArrayList<ArrayList<String>> data = runSelectSQL("SELECT * FROM ITEM WHERE Availability = true AND StockAmt > 0");
            ArrayList<RedeemableItem> redeemableItemList = new ArrayList<>();

            for (ArrayList<String> row : data) {
                redeemableItemList.add(new RedeemableItem(row.get(0), row.get(1), row.get(2), Integer.parseInt(row.get(3)), Integer.parseInt(row.get(4)), Integer.parseInt(row.get(7)), Integer.parseInt(row.get(5)) == 1, row.get(6)));
            }

            return redeemableItemList;
        }

        /**
         * Get item that customer can redeem only, need to fulfill points and
         * tier requirements
         *
         * @return ArrayList<RedeemableItem>
         */
        public static ArrayList<RedeemableItem> getRedeemableItem(int activePoints, String tier) {
            ArrayList<ArrayList<String>> data = runSelectSQL("SELECT * FROM ITEM WHERE PtRequired <= \"" + activePoints + "\" AND MinimumTier = \"" + tier + "\"");
            ArrayList<RedeemableItem> redeemableItemList = new ArrayList<>();

            for (ArrayList<String> row : data) {
                redeemableItemList.add(new RedeemableItem(row.get(0), row.get(1), row.get(2), Integer.parseInt(row.get(3)), Integer.parseInt(row.get(4)), Integer.parseInt(row.get(7)), Integer.parseInt(row.get(5)) == 1, row.get(6)));
            }

            return redeemableItemList;
        }

        public static void CustomerRedeemItem(String customerID, String itemCode, int amount) {
            runInsertUpdateSQL(String.format("INSERT INTO POINT_REDEMPTION VALUES (NULL, DEFAULT, \"%s\", \"%s\", %d)", customerID, itemCode, amount));
            runInsertUpdateSQL("UPDATE ITEM SET StockAmt = StockAmt - " + amount + ",RedeemedAmt = RedeemedAmt +" + amount + " WHERE ItemCode = \"" + itemCode + "\"");
            runInsertUpdateSQL(""
                    + "UPDATE CUSTOMER "
                    + "SET BalancePoint = BalancePoint - (SELECT PtRequired FROM ITEM WHERE ItemCode = \"" + itemCode + "\") * " + amount
                    + " WHERE CustomerID = \"" + customerID + "\"");
        }

        private static String uniqueItemCodeGenerator() {
            String IID;

            ArrayList<ArrayList<String>> result = runSelectSQL("SELECT ItemCode FROM ITEM");

            Random rand = new Random();

            while (true) {
                IID = "I" + String.format("%05d", rand.nextInt(99999));

                if (result.isEmpty() || !result.get(0).contains(IID)) {
                    return IID;
                }
            }
        }

        public static void addRedeemItem(String itemName, String itemCategory, int pointsRequired, int stockAmount, String minimumTier) {
            runInsertUpdateSQL(String.format("INSERT INTO ITEM VALUES (\"%s\", \"%s\", \"%s\", %d, %d, TRUE, \"%s\", 0)", uniqueItemCodeGenerator(), itemName, itemCategory, pointsRequired, stockAmount, minimumTier));
        }

        public static void modifyRedeemItem(String itemCode, String itemName, String itemCategory, int pointsRequired, String minimumTier) {
            runInsertUpdateSQL("UPDATE ITEM "
                    + " SET ItemName=\"" + itemName + "\""
                    + " , ItemCategory=\"" + itemCategory + "\""
                    + " , PtRequired=\"" + pointsRequired + "\""
                    + " , MinimumTier=\"" + minimumTier + "\""
                    + " WHERE itemCode = \"" + itemCode + "\"");
        }

        public static void addRedeemStock(String itemCode, int quantity) {
            runInsertUpdateSQL("UPDATE ITEM SET StockAmt = StockAmt + " + quantity + " WHERE ItemCode = \"" + itemCode + "\"");
        }

        public static void disableRedeemItem(String itemCode, Boolean status) {
            runInsertUpdateSQL("UPDATE ITEM SET Availability = " + status + " WHERE ItemCode = \"" + itemCode + "\"");
        }
    }

    public static class SLDPolicy {

        /**
         * All item included
         *
         * @return ArrayList<Policy>
         */
        public static ArrayList<Policy> getAllItem() {
            ArrayList<ArrayList<String>> data = runSelectSQL("SELECT * FROM POLICY");
            ArrayList<Policy> pointsPolicy = new ArrayList<>();

            for (ArrayList<String> row : data) {
                pointsPolicy.add(new Policy(row.get(0), row.get(1), row.get(2)));
            }

            return pointsPolicy;
        }

        public static void changePolicyTitle(String policyIndex, String policyTitle) {
            runInsertUpdateSQL("UPDATE POLICY SET PolicyTitle = \"" + policyTitle + "\" WHERE PolicyIndex = \"" + policyIndex + "\"");
        }

        public static void changePolicyContent(String policyIndex, String policyContent) {
            runInsertUpdateSQL("UPDATE POLICY SET PolicyContent = \"" + policyContent + "\" WHERE PolicyIndex = \"" + policyIndex + "\"");
        }

        public static void addPointsPolicy(String policyIndex, String policyTitle, String policyContent, String staffID) {
            String sql = String.format("INSERT INTO POLICY VALUES (\"%s\", \"%s\", \"%s\", \"%s\")", policyIndex, policyTitle, policyContent, staffID);
            runInsertUpdateSQL(sql);
        }

        public static void deletePointsPolicy(String policyIndex) {
            runInsertUpdateSQL("DELETE FROM POLICY WHERE PolicyIndex = \"" + policyIndex + "\"");
        }

    }

    public static class SLDContact {

        /**
         * All item included
         *
         * @return ArrayList<Policy>
         */
        public static ArrayList<CompanyDetails> getAllItem() {
            ArrayList<ArrayList<String>> data = runSelectSQL("SELECT * FROM COMPANY_DETAILS");
            ArrayList<CompanyDetails> contactDetails = new ArrayList<>();

            for (ArrayList<String> row : data) {
                contactDetails.add(new CompanyDetails(row.get(0), row.get(1), row.get(2)));
            }

            return contactDetails;
        }

        public static void changeCompanyDetailsTitle(String companyDetailsIndex, String companyDetailsTitle) {
            runInsertUpdateSQL("UPDATE COMPANY_DETAILS SET CompanyDetailsTitle = \"" + companyDetailsTitle + "\" WHERE CompanyDetailsIndex = \"" + companyDetailsIndex + "\"");
        }

        public static void changeCompanyDetailsContent(String companyDetailsIndex, String companyDetailsContent) {
            runInsertUpdateSQL("UPDATE COMPANY_DETAILS SET CompanyDetailsContent = \"" + companyDetailsContent + "\" WHERE CompanyDetailsIndex = \"" + companyDetailsIndex + "\"");
        }

        public static void addCompanyDetails(String companyDetailsIndex, String companyDetailsTitle, String companyDetailsContent, String staffID) {
            String sql = String.format("INSERT INTO COMPANY_DETAILS VALUES (\"%s\", \"%s\", \"%s\", \"%s\")", companyDetailsIndex, companyDetailsTitle, companyDetailsContent, staffID);
            runInsertUpdateSQL(sql);
        }

        public static void deleteCompanyDetails(String companyDetailsIndex) {
            runInsertUpdateSQL("DELETE FROM COMPANY_DETAILS WHERE CompanyDetailsIndex = \"" + companyDetailsIndex + "\"");
        }
    }

    public static class SLDRecord {

        public static ArrayList<ItemRedemption> getAllRedemptionRecord() {
            ArrayList<ArrayList<String>> data = runSelectSQL("SELECT * FROM POINT_REDEMPTION");
            ArrayList<ItemRedemption> redemptionRecordList = new ArrayList<>();

            for (ArrayList<String> row : data) {
                ArrayList<String> customerData = SaveLoadData.SLDProfile.getCustomerProfile(row.get(2));
                Customer customer = new Customer(customerData.get(0), customerData.get(1), customerData.get(2), customerData.get(3), LocalDate.parse(customerData.get(4), DateTimeFormatter.ofPattern("yyyy-MM-dd")), customerData.get(5), Integer.parseInt(customerData.get(6)), customerData.get(7), Integer.parseInt(customerData.get(8)), LocalDate.parse(customerData.get(9), DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                RedeemableItem item = SaveLoadData.SLDPointAndItem.getOnlyOneItem(row.get(3));
                redemptionRecordList.add(new ItemRedemption(row.get(0), LocalDateTime.parse(row.get(1), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), customer, item, Integer.valueOf(row.get(4))));
            }

            return redemptionRecordList;
        }

        public static ArrayList<RewardPoint> getAllRewardPointRecord() {
            ArrayList<ArrayList<String>> data = runSelectSQL("SELECT * FROM POINT_EARNING");
            ArrayList<RewardPoint> rewardPointRecordList = new ArrayList<>();

            for (ArrayList<String> row : data) {
                ArrayList<String> customerData = SaveLoadData.SLDProfile.getCustomerProfile(row.get(2));

                Customer customer = new Customer(customerData.get(0), customerData.get(1), customerData.get(2), customerData.get(3), LocalDate.parse(customerData.get(4), DateTimeFormatter.ofPattern("yyyy-MM-dd")), customerData.get(5), Integer.parseInt(customerData.get(6)), customerData.get(7), Integer.parseInt(customerData.get(8)), LocalDate.parse(customerData.get(9), DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                ArrayList<String> staffData = SaveLoadData.SLDStaff.getStaffProfile(row.get(3));
                if(staffData == null){
                    continue;
                }

                Staff staff = new Staff(staffData.get(0), staffData.get(1), staffData.get(2), staffData.get(3), LocalDate.parse(staffData.get(4), DateTimeFormatter.ofPattern("yyyy-MM-dd")), staffData.get(5));

                rewardPointRecordList.add(new RewardPoint(row.get(0), LocalDateTime.parse(row.get(1), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), customer, staff, Integer.valueOf(row.get(4))));
            }

            return rewardPointRecordList;
        }

        public static ArrayList<EventRoomReservation> getAllEventRoomResRecord() {
            ArrayList<ArrayList<String>> data = runSelectSQL("SELECT * FROM EVENT_RESERVATION");
            ArrayList<EventRoomReservation> eventRoomRes = new ArrayList<>();

            for (ArrayList<String> row : data) {
                ArrayList<String> customerData = SaveLoadData.SLDProfile.getCustomerProfile(row.get(4));
                Customer customer = new Customer(customerData.get(0), customerData.get(1), customerData.get(2), customerData.get(3), LocalDate.parse(customerData.get(4), DateTimeFormatter.ofPattern("yyyy-MM-dd")), customerData.get(5), Integer.parseInt(customerData.get(6)), customerData.get(7), Integer.parseInt(customerData.get(8)), LocalDate.parse(customerData.get(9), DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                EventRoom eventroom = SaveLoadData.SLDEventRoom.getOnlyOne(row.get(1));
                eventRoomRes.add(new EventRoomReservation(row.get(0), eventroom, LocalDate.parse(row.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd")), row.get(3), customer, Integer.valueOf(row.get(5))));
            }

            return eventRoomRes;
        }

        public static PointRedemption[] getOverallRedemptionRecord() {
            ArrayList<ArrayList<String>> data1 = runSelectSQL("SELECT * FROM POINT_REDEMPTION");
            ArrayList<ArrayList<String>> data2 = runSelectSQL("SELECT * FROM EVENT_RESERVATION");
            PointRedemption[] eventRoomRes = new PointRedemption[100];
            int i = 0;
            for (ArrayList<String> row : data1) {
                ArrayList<String> customerData = SaveLoadData.SLDProfile.getCustomerProfile(row.get(2));
                Customer customer = new Customer(customerData.get(0), customerData.get(1), customerData.get(2), customerData.get(3), LocalDate.parse(customerData.get(4), DateTimeFormatter.ofPattern("yyyy-MM-dd")), customerData.get(5), Integer.parseInt(customerData.get(6)), customerData.get(7), Integer.parseInt(customerData.get(8)), LocalDate.parse(customerData.get(9), DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                RedeemableItem item = SaveLoadData.SLDPointAndItem.getOnlyOneItem(row.get(3));
                eventRoomRes[i] = new ItemRedemption(row.get(0), LocalDateTime.parse(row.get(1), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), customer, item, Integer.valueOf(row.get(4)));
                i++;
            }
            for (ArrayList<String> row : data2) {
                ArrayList<String> customerData = SaveLoadData.SLDProfile.getCustomerProfile(row.get(4));
                Customer customer = new Customer(customerData.get(0), customerData.get(1), customerData.get(2), customerData.get(3), LocalDate.parse(customerData.get(4), DateTimeFormatter.ofPattern("yyyy-MM-dd")), customerData.get(5), Integer.parseInt(customerData.get(6)), customerData.get(7), Integer.parseInt(customerData.get(8)), LocalDate.parse(customerData.get(9), DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                EventRoom eventroom = SaveLoadData.SLDEventRoom.getOnlyOne(row.get(1));
                eventRoomRes[i] = new EventRoomReservation(row.get(0), eventroom, LocalDate.parse(row.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd")), row.get(3), customer, Integer.valueOf(row.get(5)));
                i++;
            }

            return eventRoomRes;
        }

        public static ArrayList<EventRoomReservation> getEventRoomResForACustRecord(String custID) {
            ArrayList<ArrayList<String>> data = runSelectSQL("SELECT * FROM EVENT_RESERVATION WHERE CUST_ID=\"" + custID + "\";");
            ArrayList<EventRoomReservation> eventRoomRes = new ArrayList<>();

            for (ArrayList<String> row : data) {
                ArrayList<String> customerData = SaveLoadData.SLDProfile.getCustomerProfile(row.get(4));
                Customer customer = new Customer(customerData.get(0), customerData.get(1), customerData.get(2), customerData.get(3), LocalDate.parse(customerData.get(4), DateTimeFormatter.ofPattern("yyyy-MM-dd")), customerData.get(5), Integer.parseInt(customerData.get(6)), customerData.get(7), Integer.parseInt(customerData.get(8)), LocalDate.parse(customerData.get(9), DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                EventRoom eventroom = SaveLoadData.SLDEventRoom.getOnlyOne(row.get(1));
                eventRoomRes.add(new EventRoomReservation(row.get(0), eventroom, LocalDate.parse(row.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd")), row.get(3), customer, Integer.valueOf(row.get(5))));
            }

            return eventRoomRes;
        }

        public static ArrayList<EventRoomReservation> getEventRoomResToday() {
            ArrayList<ArrayList<String>> data = runSelectSQL("SELECT * FROM EVENT_RESERVATION WHERE RES_DATE=\"" + LocalDate.now() + "\";");
            ArrayList<EventRoomReservation> eventRoomRes = new ArrayList<>();

            for (ArrayList<String> row : data) {
                ArrayList<String> customerData = SaveLoadData.SLDProfile.getCustomerProfile(row.get(4));
                Customer customer = new Customer(customerData.get(0), customerData.get(1), customerData.get(2), customerData.get(3), LocalDate.parse(customerData.get(4), DateTimeFormatter.ofPattern("yyyy-MM-dd")), customerData.get(5), Integer.parseInt(customerData.get(6)), customerData.get(7), Integer.parseInt(customerData.get(8)), LocalDate.parse(customerData.get(9), DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                EventRoom eventroom = SaveLoadData.SLDEventRoom.getOnlyOne(row.get(1));
                eventRoomRes.add(new EventRoomReservation(row.get(0), eventroom, LocalDate.parse(row.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd")), row.get(3), customer, Integer.valueOf(row.get(5))));
            }

            return eventRoomRes;
        }

        public static void delRedemptionRecordSQL(String RecordID) {
            runInsertUpdateSQL("DELETE FROM POINT_REDEMPTION WHERE PR_RecordID = \"" + RecordID + "\"");
        }

        public static void delRewardPointRecordSQL(String RecordID) {
            runInsertUpdateSQL("DELETE FROM POINT_EARNING WHERE PE_RecordID = \"" + RecordID + "\"");
        }

        public static void delEventRoomResRecordSQL(String RecordID) {
            runInsertUpdateSQL("DELETE FROM EVENT_RESERVATION WHERE ERES_ID = \"" + RecordID + "\"");
        }

        public static void cancelEventRoomResRecordSQL(String RecordID) {
            runInsertUpdateSQL("UPDATE EVENT_RESERVATION SET RES_STATUS = \"Cancelled\" WHERE ERES_ID = \"" + RecordID + "\"");
        }

        public static void expiredEventRoomResRecordSQL(String RecordID) {
            runInsertUpdateSQL("UPDATE EVENT_RESERVATION SET RES_STATUS = \"Expired\" WHERE ERES_ID = \"" + RecordID + "\"");
        }

        public static void completedEventRoomResRecordSQL(String RecordID) {
            runInsertUpdateSQL("UPDATE EVENT_RESERVATION SET RES_STATUS = \"Completed\" WHERE ERES_ID = \"" + RecordID + "\"");
        }
    }

    public static class SLDReport {

        /**
         * Get yearly redemption data, SUM BY Redeemed Amount, SORT DESC BY
         * Redeemed Amount
         *
         * @return ArrayList
         */
        public static ArrayList<RedeemableItem> getYearlyPointRedemptionData() {
            ArrayList<ArrayList<String>> data = runSelectSQL(""
                    + "SELECT I.* "
                    + "FROM POINT_REDEMPTION PR JOIN ITEM I ON PR.ItemCode = I.ItemCode "
                    + "WHERE RedemptionDate >= DATE_SUB(NOW(),INTERVAL 1 YEAR) "
                    + "GROUP BY ItemCode "
                    + "ORDER BY RedeemedAmt DESC");
            ArrayList<RedeemableItem> redeemableItemList = new ArrayList<>();

            for (ArrayList<String> row : data) {
                RedeemableItem rItem = new RedeemableItem(row.get(0), row.get(1), row.get(2), Integer.parseInt(row.get(3)), Integer.parseInt(row.get(4)), Integer.parseInt(row.get(7)), Integer.parseInt(row.get(5)) == 1, row.get(6));
                rItem.setRedeemedAmt(Integer.parseInt(row.get(7)));

                redeemableItemList.add(rItem);
            }

            return redeemableItemList;
        }

        /**
         * Get monthly redemption data, SUM BY Redeemed Amount, SORT DESC BY
         * Redeemed Amount
         *
         * @return ArrayList
         */
        public static ArrayList<RedeemableItem> getMonthlyPointRedemptionData() {
            ArrayList<ArrayList<String>> data = runSelectSQL(""
                    + "SELECT I.*"
                    + "FROM POINT_REDEMPTION PR JOIN ITEM I ON PR.ItemCode = I.ItemCode "
                    + "WHERE RedemptionDate >= DATE_SUB(NOW(),INTERVAL 1 MONTH) "
                    + "GROUP BY ItemCode "
                    + "ORDER BY RedeemedAmt DESC");
            ArrayList<RedeemableItem> redeemableItemList = new ArrayList<>();

            for (ArrayList<String> row : data) {
                RedeemableItem rItem = new RedeemableItem(row.get(0), row.get(1), row.get(2), Integer.parseInt(row.get(3)), Integer.parseInt(row.get(4)), Integer.parseInt(row.get(7)), Integer.parseInt(row.get(5)) == 1, row.get(6));
                rItem.setRedeemedAmt(Integer.parseInt(row.get(7)));

                redeemableItemList.add(rItem);
            }

            return redeemableItemList;
        }
    }
}
