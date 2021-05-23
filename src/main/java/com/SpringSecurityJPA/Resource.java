package com.SpringSecurityJPA;


import com.SpringSecurityJPA.models.User;
import com.SpringSecurityJPA.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;


@Controller
public class Resource {

    @Autowired
    private UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(Resource.class);

    @GetMapping("/")
    public String home() {
        LOGGER.info("welcome user or admin");
        return ("<h1> Welcome </h1>");
    }

    @GetMapping("/users")
    public String user() {
        LOGGER.info("welcome user or admin for users");
        return ("<h1> Welcome users</h1>");
    }

    @GetMapping("/admin")
    public String admin() {
        LOGGER.info("welcome admin");
        return ("<h1> Welcome Admin</h1>");
    }

    /**
     * This request for download the users in the database
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/downloadCSV")
    public void downloadsCSV(HttpServletResponse response) throws IOException{

        LOGGER.info("Request is received to download the user csv");

        String csvFileName = "User_Detail.csv";
        response.setContentType("text/csv");

        // creates mock data
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                csvFileName);
        response.setHeader(headerKey, headerValue);

        List<User> users = userRepository.findAll();

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);

        String[] header = {"id", "active","password", "roles", "userName"};

        csvWriter.writeHeader(header);

        for (User aUser : users) {
            csvWriter.write(aUser, header);
        }

        csvWriter.close();
        LOGGER.info("User CSV file downloaded successfully");

    }

    // file directory path
    String filepath = "/home/devin/demo/";

    /**
     * this lists  the files in the above directory
     * @param model
     * @return
     */
    @RequestMapping("/downloads")
    public String showFiles(Model model) {
        File folder =  new File(filepath);
        File[] listOfFiles = folder.listFiles();
        model.addAttribute("files", listOfFiles);
        LOGGER.info("Direct to the file listing folder and List It");
        return "showFiles";

    }

    /**
     * this download the selected file
     * @param fileName
     * @param response
     */
    @RequestMapping("/file/{fileName}")
    @ResponseBody
    public void show(@PathVariable("fileName") String fileName, HttpServletResponse response) {

        // setting the contentType for several types of files
        if (fileName.contains(".doc")) response.setContentType("application/msword");
        if (fileName.contains(".docx")) response.setContentType("application/msword");
        if (fileName.contains(".xls")) response.setContentType("application/vnd.ms-excel");
        if (fileName.contains(".csv")) response.setContentType("application/vnd.ms-excel");
        if (fileName.contains(".ppt")) response.setContentType("application/ppt");
        if (fileName.contains(".pdf")) response.setContentType("application/pdf");
        if (fileName.contains(".zip")) response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" +fileName);
        response.setHeader("Content-Transfer-Encoding", "binary");

       File file = new File(filepath + fileName);

        try (InputStream is = new FileInputStream(file)) {
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            int len;
            byte[] buf = new byte[1024];
            while((len = is.read(buf)) > 0) {
                bos.write(buf,0,len);
            }
            response.flushBuffer();
            LOGGER.info("Download the file {} was successfully!", fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
