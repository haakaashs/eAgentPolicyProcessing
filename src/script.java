import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class script {
    public static void main(String[] args) {
       
       try {

            log("enter into script");
            Properties config = loadConfig("./config.env");
            
            if (config ==null){
                log("Error loading config");
                return;
            }

            log("Config loaded successfully");
            String ids = readInputFile(config.getProperty("input_file"));
            String filePath = config.getProperty("file_path");
            String updateQuery = "UPDATE info SET status = 0 WHERE id NOT IN (" + ids + ")";
        
            if (!updateDB(config,updateQuery)) {
                 return;
            }

            int countA = 0;
            int countB = 0;
            
            if (runJob(config.getProperty("job_1").trim())){
                for (String temp : ids.split(",")) {
                    countA++;
                    String id = temp.replace("'","");     
                    String xmlFileName = "[A-Za-z0-9]+\\.[A-Za-z0-9]+\\."+ id.trim() + ".xml";
                    String pdfFileName = "[A-Za-z0-9]+\\.[A-Za-z0-9]+\\."+ id.trim() + ".pdf";                    
                    if (!fileExists(filePath, xmlFileName) || !fileExists(filePath, pdfFileName)) {
                        countB++;
                        log("Error: XML or PDF file not found for ID " + id);
                    }
                }

                if (countA!=countB) {  
                    log("job1 completed successfully");
                    if (runJob(config.getProperty("job_2").trim())) {
                        String xmlFileName = "[A-Za-z0-9]+\\.[A-Za-z0-9]+\\.control.xml";
                        if (fileExists(filePath, xmlFileName)){
                            log("job2 completed successfully");
                            if (runJob(config.getProperty("job_3").trim())) {
                                log("job3 completed successfully");
                                log("All jobs completed successfully");
                            } else {
                                log("Error running job3");
                            }
                        }else{
                            log("Error: control file not exist job2 failed");
                        }
                    } else {
                        log("Error running job2");
                    }
                } else {
                    log("Error: XML or PDF file not exist job1 failed");
                }
            } else {
                log("Error running job1");
            }
        log("exit script");
    } catch (IOException | SQLException | ClassNotFoundException | InterruptedException e) {
            try {
                log("Error log: "+e.getMessage());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private static void log(String message) throws IOException{
        FileWriter writer = new FileWriter("./log.txt", true);
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateFormat.format(now);
        writer.write("[" + formattedDateTime + "] " + message+"\n");
        writer.close();
    }

    private static Properties loadConfig(String configFile) throws IOException {
        log("enter into loadConfig");   
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(configFile);
        properties.load(input);
        log("exit loadConfig"); 
        return properties;
    }

    private static String readInputFile(String filePath)throws IOException{
        log("enter into readInputFile");   
        String line;
        ArrayList<String> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder idsBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            list.add(line.trim());
            idsBuilder.append("'").append(line.trim()).append("',");
        }
        reader.close();
        String ids = idsBuilder.toString().replaceAll(",$", "");
        log("exit readInputFile"); 
        return ids;
    }
    
    private static boolean updateDB(Properties config,String updateQuery)throws SQLException, IOException ,ClassNotFoundException{
        log("enter into updateDB");          
        Class.forName(config.getProperty("db_driver"));
        Connection connection = DriverManager.getConnection(config.getProperty("db_url"),config.getProperty("db_username"),config.getProperty("db_password"));
        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
        int rowsnullAffected =  updateStatement.executeUpdate();
        connection.close();
        if (rowsAffected > 0) {
            log("Database update successfull");
            log("exit updateDB");          
            return true;
        } 
        log("Error updating database ");
        log("exit updateDB");          
        return false;
    }
    
    private static boolean fileExists(String directoryPath, String fileNamePattern) throws IOException{
        log("enter into fileExists");    
        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> name.matches(fileNamePattern));
        log("exit fileExists");
        return files != null && files.length > 0;
    }

    private static boolean runJob(String exe) throws IOException,InterruptedException {
        log("enter into runJob for "+exe);          
        ProcessBuilder processBuilder = new ProcessBuilder(exe);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode==0){
            log("exit runJob for "+exe);          
            return true;
        }
        log("exit runJob for "+exe);          
        return false;
    }
}
