package org.transmartproject.rest

import grails.converters.JSON
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.transmartproject.core.exceptions.AccessDeniedException
import org.transmartproject.core.exceptions.InvalidArgumentsException
import org.transmartproject.core.users.UsersResource
import org.transmartproject.db.user.User
import org.transmartproject.rest.misc.CurrentUser
import sun.reflect.generics.reflectiveObjects.NotImplementedException

import javax.transaction.NotSupportedException
import java.util.regex.Matcher
import java.util.regex.Pattern

class RestExportController {
    
    @Autowired
    RestExportService restExportService
    def springSecurityService
    @Autowired
    CurrentUser currentUser
    
    @Autowired
    UsersResource usersResource
    
    private static final String ROLE_ADMIN = 'ROLE_ADMIN'
    
    /**
     * Data export endpoint:
     * Create the new asynchronous job and download the data
     * <code>/v2/export?set_type=${set_type}&ids=${ids}&data_type=${data_type}&format=&{format}&name=&{name}</code>
     *
     * A <code>name</code> parameter is optional.
     * Current methodology for naming is username-jobtype-ID from sequence generator
     *
     *
     */
    def create() {
        def result = restExportService.createExportJob(params, currentUser.username)
        
        response.setContentType("text/json")
        response.outputStream << result.toString()
    }
    
    /**
     * Run a data export is called asynchronously from the datasetexplorer -> Data Export tab
     */
    def run(jobName) {
        checkRightsToExport(params.ids)
        
        def jsonResult = restExportService.exportData(jobName, params, currentUser.username)
        
        response.setContentType("text/json")
        response.outputStream << jsonResult.toString()
    }
    
    def download(jobName) {
        checkJobAccess jobName
        
        def InputStream inputStream = restExportService.downloadFile(jobName);
        def result = [:]
        
        if (inputStream) {
            result.fileStatus = true
            inputStream.close()
        } else {
            result.fileStatus = false
            result.message = "Download failed as file could not be found on the server"
        }
        
        return result as JSON
    }
//    def downloadFile() {
//        checkJobAccess params.jobname
//
//        def InputStream inputStream = restExportService.downloadFile(params)
//
//        def fileName = params.jobname + ".zip"
//        response.setContentType 'application/zip'
//        response.setHeader "Content-disposition", "attachment;filename=${fileName}"
//        response.outputStream << inputStream
//        response.outputStream.flush()
//        inputStream.close();
//        return true;
//    }
    
   def status(jobName) {
       throw NotImplementedException()
   }
    
    def list() {
        throw new NotImplementedException()
    }
    
    def export(){
        User user = (User) usersResource.getUserFromUsername(currentUser.username)
        restExportService.export('test', params, user)
    }
    
    // TODO
    def metaData() {
        //checkRightsToExport(resultInstanceIds)
        
        //render exportMetadataService.getMetaData as JSON
        throw NotImplementedException()
    }
    
    //TODO GBD-101
    def dataFormats() {
        validateDataFormatParams(params)
        restExportService.getDataFormats(params.type, params.id)
    }
      
    def fileFormats() {
        return restExportService.supportedFileFormats
    }

    /********************************************************************
    ** HELPER METHODS
    ********************************************************************/
    
    private void checkRightsToExport(List<Long> resultInstanceIds) {
        if (!resultInstanceIds) {
            throw new InvalidArgumentsException("No result instance id provided")
        }
        // TODO
//        if (!dataExportService.isUserAllowedToExport(currentUserBean, resultInstanceIds)) {
//            throw new AccessDeniedException("User ${currentUserBean.username} has no EXPORT permission" +
//                    " on one of the result sets: ${resultInstanceIds}")
//        }
    }
    
    private checkJobAccess(String jobName) {
        if (isAdmin()) {
            return
        }
        
        String loggedInUsername = springSecurityService.principal.username
        String jobUsername = extractUserFromJobName(jobName)
        
        if (jobUsername != loggedInUsername) {
            log.warn("Denying access to job $jobName because the " +
                    "corresponding username ($jobUsername) does not match " +
                    "that of the current user")
            throw new AccessDeniedException("Job $jobName was not started by " +
                    "this user")
        }
    }
    
    private boolean isAdmin() {
        springSecurityService.principal.authorities.any {
            it.authority == ROLE_ADMIN
        }
    }
    
    static private String extractUserFromJobName(String jobName) {
        Pattern pattern = ~/(.+)-[a-zA-Z]+-\d+/
        Matcher matcher = pattern.matcher(jobName)
        
        if (!matcher.matches()) {
            throw new IllegalStateException('Invalid job name')
        }
        
        matcher.group(1)
    }
    
    private void validateDataFormatParams(params) {
        if (!params.id){
            throw new InvalidArgumentsException('Empty id parameter.')
        }
        if (!params.set_type?.trim()) {
            throw new InvalidArgumentsException('Empty set_type parameter.')
        } else {
            if (params.set_type.trim() in restExportService.supportedSetTypes) {
                throw new InvalidArgumentsException("Type not supported: ${type}.")
            }
        }
    }
}
