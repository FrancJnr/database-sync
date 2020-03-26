package org.assignment;

import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
  
class DbSync {  
    public static void main(String[] args) throws InterruptedException {
        String userName = "root";
        String password = "giftedbrain";
        String hostUrlDB1 = "jdbc:mysql://localhost:3306/techsava_sanofipos";
        String hostUrlDB2 = "jdbc:mysql://localhost:3306/techsava_sanofipos_copy";
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        Runnable syncRecords = () -> {
            DbSync sync = new DbSync();
            Connection db1Conn = sync.connectDB(userName, password, hostUrlDB1);
            Connection db2Conn = sync.connectDB(userName, password, hostUrlDB2);
            //check if both databases are linked link
            if(db1Conn != null && db2Conn != null){
                
                System.out.println("Link Established");
                //add default columns if they dont exist in the table verify in each execution
                sync.addDefaultColumns("sma_sales", db1Conn, "created_at");
                sync.addDefaultColumns("sma_sales", db2Conn, "created_at");
                sync.addDefaultColumns("sma_sales", db1Conn, "updated_at");
                sync.addDefaultColumns("sma_sales", db2Conn, "updated_at");

                sync.addDefaultColumns("sma_sale_items", db1Conn, "created_at");
                sync.addDefaultColumns("sma_sale_items", db2Conn, "created_at");
                sync.addDefaultColumns("sma_sale_items", db1Conn, "updated_at");
                sync.addDefaultColumns("sma_sale_items", db2Conn, "updated_at");

                sync.addDefaultColumns("sma_purchases", db1Conn, "created_at");
                sync.addDefaultColumns("sma_purchases", db2Conn, "created_at");
                sync.addDefaultColumns("sma_purchases", db1Conn, "updated_at");
                sync.addDefaultColumns("sma_purchases", db2Conn, "updated_at");

                sync.addDefaultColumns("sma_purchase_items", db1Conn, "created_at");
                sync.addDefaultColumns("sma_purchase_items", db2Conn, "created_at");
                sync.addDefaultColumns("sma_purchase_items", db1Conn, "updated_at");
                sync.addDefaultColumns("sma_purchase_items", db2Conn, "updated_at");

                sync.addDefaultColumns("sma_budget", db1Conn, "created_at");
                sync.addDefaultColumns("sma_budget", db2Conn, "created_at");
                sync.addDefaultColumns("sma_budget", db1Conn, "updated_at");
                sync.addDefaultColumns("sma_budget", db2Conn, "updated_at");
                //check for updates or inserted records. Synchronize the records on both tables
                sync.syncTableRecords("sma_sales",  db1Conn,  db2Conn, 100000, 1);
                sync.syncTableRecords("sma_sales",  db2Conn,  db1Conn, 100000, 0);

                sync.syncTableRecords("sma_sale_items",  db1Conn,  db2Conn, 100000, 1);
                sync.syncTableRecords("sma_sale_items",  db2Conn,  db1Conn, 100000, 0);

                sync.syncTableRecords("sma_purchases",  db1Conn,  db2Conn, 100000, 1);
                sync.syncTableRecords("sma_purchases",  db2Conn,  db1Conn, 100000, 0);

                sync.syncTableRecords("sma_purchase_items",  db1Conn,  db2Conn, 100000, 1);
                sync.syncTableRecords("sma_purchase_items",  db2Conn,  db1Conn, 10, 0);

                sync.syncTableRecords("sma_budget",  db1Conn,  db2Conn, 100000, 1);
                sync.syncTableRecords("sma_budget",  db2Conn,  db1Conn, 100000, 0);

            };

        };
         //Set up a cron job to execute the syncRecords method after every 1000 seconds. 
        ScheduledFuture<?> scheduledFuture = ses.scheduleAtFixedRate(syncRecords, 5, 1, TimeUnit.SECONDS);

        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public Connection connectDB(String dbUser, String dbPassword, String dbUrl){
        Connection conn = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn  = DriverManager.getConnection( dbUrl, dbUser, dbPassword);
        }catch(Exception ex) {
            System.out.println(ex);
        }
        return conn;
    }
    public String syncTableRecords(String tableName,  Connection connDb1, Connection connDb2, int lastExecutionTime, int action){
        StringBuffer columnsPlaceHolder = new StringBuffer(); //declare a string to hold query parameters
        columnsPlaceHolder.append("?");
        String responseMessage = "";
        //Query to execute recent updates since the last update
        String updateDb2Query = "SELECT *FROM "+tableName + " WHERE UNIX_TIMESTAMP('updated_at')+"+lastExecutionTime +">=UNIX_TIMESTAMP(current_timestamp())";
        String insertDb2Query = "SELECT *FROM "+tableName + " WHERE UNIX_TIMESTAMP('created_at')+"+lastExecutionTime +">=UNIX_TIMESTAMP(current_timestamp())";
        PreparedStatement statement1 = null;
        PreparedStatement statement2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSetMetaData rsmd = null;
        try {
            if(action==0){statement2 = connDb2.prepareStatement(updateDb2Query);}
            else if(action == 1){statement2 = connDb2.prepareStatement(insertDb2Query);}
            rs2 = statement2.executeQuery();
            rsmd = rs2.getMetaData();
            //Append ? place holder for the query dynamically
            int columnCount = rsmd.getColumnCount();
            for (int i = 0; i<columnCount-1; i++) {
                columnsPlaceHolder.append(",?");
            }
            //Perform batch updates to all columns by using REPLACE INTO.
            String updateFromDb2Query = "REPLACE INTO "+tableName+" SELECT "+ columnsPlaceHolder.toString();
            String insertFromDb2Query = "INSERT INTO "+tableName+ " SELECT "+columnsPlaceHolder.toString();
            while (rs2.next()){
                if(action==0){statement1 = connDb1.prepareStatement(updateFromDb2Query); System.out.println("Checking For updates on "+tableName);}
                else if(action == 1){statement1 = connDb1.prepareStatement(insertFromDb2Query);System.out.println("Checking For new rows in "+tableName);}
                for(int i = 1; i<=columnCount; i++) {
                    //loop through the columns and get the column type. This is for reusability accross all tables
                    int columnType = rsmd.getColumnType(i);
                    if((columnType == 4)|| (columnType == -6)){
                        statement1.setInt(i, rs2.getInt(i));
                    }else if(columnType == 12){
                        statement1.setString(i, rs2.getString(i));
                    }else if(columnType ==1){
                        statement1.setCharacterStream(i, rs2.getCharacterStream(i));
                    }else if(columnType == 93){
                        statement1.setTimestamp(i, rs2.getTimestamp(i));
                    }else if(columnType ==3){
                        statement1.setBigDecimal(i, rs2.getBigDecimal(i));
                    }else if(columnType == 91) {
                        statement1.setDate(i, rs2.getDate(i));
                    }else if(columnType == 16){
                        statement1.setBoolean(i,rs2.getBoolean(i));
                    }else if(columnType == -7) {
                        statement1.setBinaryStream(i, rs2.getBinaryStream(i));
                    }else{
                        System.out.println("Printng column type "+columnType);
                    }
                }
                statement1.executeUpdate();
                System.out.println("Synchronizing "+tableName);

            }

        } catch (Exception e) {
            responseMessage = e.toString();
            System.out.println("Synchronizing "+ e);

        }
        return responseMessage;
    }
    public void addDefaultColumns(String tableName, Connection conn, String columnName){
        String query = "SELECT COUNT(*) AS count " +
                "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? " +
                "AND COLUMN_NAME = ?";
        try{
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1,tableName);
            statement.setString(2,columnName);
            ResultSet rs = statement.executeQuery();
            Statement statement2 = null;
            while (rs.next()){
                if(rs.getInt("count")==0){
                    System.out.println("ADDING COLUMN "+columnName+" TO "+ tableName);
                }

            }

        }catch (Exception ex){
            System.out.println(ex);
        }


    }
}
