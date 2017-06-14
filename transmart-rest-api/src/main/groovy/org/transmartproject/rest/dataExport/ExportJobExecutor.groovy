package org.transmartproject.rest.dataExport

import grails.util.Holders

import groovy.util.logging.Slf4j
import org.apache.commons.lang.StringUtils
import org.apache.tomcat.util.http.fileupload.FileUtils
import org.quartz.Job
import org.quartz.JobExecutionContext

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * This class will encompass the job scheduled by Quartz. When the execute method is called we will travel down a list of predefined methods to prep data
 */
@Slf4j
class ExportJobExecutor implements Job {

//    def ctx = Holders.grailsApplication.mainContext
//    def springSecurityService = ctx.springSecurityService
//    def jobResultsService = ctx.jobResultsService
//    def i2b2HelperService = ctx.i2b2HelperService
        DataExportService dataExportService
//    def asyncJobService = ctx.asyncJobService
//
//    QuartzSpringScope quartzSpringScope = ctx.quartzSpringScope
//
    final String tempFolderDirectory = Holders.config.com.recomdata.plugins.tempFolderDirectory
    final String workingDirectoryFolderName = "workingDirectory"

    public void execute(JobExecutionContext jobExecutionContext) {
    
        // put the user in context
//        quartzSpringScope."${CurrentUserBeanProxyFactory.SUB_BEAN_QUARTZ}" =
//                userInContext
        // init ()

        //Gather the jobs info.
        String jobName = jobExecutionContext.jobDetail.getName()
        Map jobDataMap = jobExecutionContext.jobDetail.getJobDataMap()
       
        //create directories and folder structure
       // createFilesAndDirectories(jobName, jobDataMap)
        
        
        try {
            // updateStatus: Gathering Data
            // if (isJobCancelled(jobName)) return
            getData(jobDataMap)
            
            // renderOutput
            
        } catch(Exception e){
            // catch different exception
        
    } finally {
//        if (jobResultsService[jobName]["Exception"] != null) {
//            asyncJobService.updateStatus(jobName, "Error", null, null, jobResultsService[jobName]["Exception"])
//        }
    }
    
    //Marking the status as complete
    //updateStatus(jobName, "Completed")
}
    
    void getData(Map jobDataMap) {
        try {
            dataExportService.exportData(jobDataMap)
        } catch (Exception e){
            
        } finaly
        createZipFile()
    }
    
    private void createFilesAndDirectories(String jobName, Map jobDataMap){
        String jobTmpDirectory = tempFolderDirectory + File.separator + "${jobName}" + File.separator
        jobTmpDirectory = jobTmpDirectory.replace("\\", "\\\\")
        if (new File(jobTmpDirectory).exists()) {
            log.warn("The job folder ${jobTmpDirectory} already exists. It's going to be overwritten.")
            FileUtils.deleteDirectory(new File(jobTmpDirectory))
        }
        String jobTmpWorkingDirectory = jobTmpDirectory + workingDirectoryFolderName
        jobDataMap.put('jobTmpDirectory', jobTmpDirectory)
        
        //Try to make the working directory.
        File jobTmpFile = new File(jobTmpWorkingDirectory)
        jobTmpFile.mkdirs()
    
        //Create a file that will have all the job parameters.
        File jobInfoFile = new File(jobTmpWorkingDirectory + File.separator + 'jobInfo.txt')
    
        //Write our parameters to the file.
        jobInfoFile.write("Parameters" + System.getProperty("line.separator"))
        jobDataMap.getKeys().each { _key ->
            jobInfoFile.append("\t${_key} -> ${jobDataMap[_key]}" + System.getProperty("line.separator"))
        }
    }
        
    // TODO improve this method: create a zip file from in memory stream
    def createZipFile(String inputFileName, String zipFileName) {
        
        String inputFilePath = tempFolderDirectory + File.separator + inputFileName
        ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(zipFileName))
        
        new File(inputFilePath).eachFile() { file ->
            zipFile.putNextEntry(new ZipEntry(file.getName()))
            def buffer = new byte[1024]
            file.withInputStream { i ->
                def l = i.read(buffer)
                // check whether the file is empty
                if (l > 0) {
                    zipFile.write(buffer, 0, l)
                }
            }
            zipFile.closeEntry()
        }
        zipFile.close()
    }
    
    public InputStream getExportJobFileStream(String fileName) {
        
        if (StringUtils.isEmpty(fileName)) return null
        
        InputStream inputStream = null

        try {
            String filePath = tempFolderDirectory + File.separator + fileName;
            File jobZipFile = new File(filePath);
            if (jobZipFile.isFile()) {
                inputStream = new FileInputStream(jobZipFile);
            }
        } catch (Exception e) {
            log.error("Failed to get the ZIP file")
        }
        
        return inputStream
    }
}
