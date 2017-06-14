package org.transmartproject.rest

import org.transmartproject.rest.dataExport.ExportAsyncJobService
import org.transmartproject.rest.dataExport.JobResultsService

class ExportAsyncJobController {
    
    ExportAsyncJobService exportAsyncJobService
    JobResultsService jobResultsService
        
    /**
     * Create the get the list of jobs to show in the jobs tab
     **/
    def getJobs = {
        def result = exportAsyncJobService.getJobList()
        
        response.setContentType("text/json")
        response.outputStream << result?.toString()
    }
    
    /**
     * Create the new asynchronous job
     **/
//    def createNewJob = {
//        def result = exportAsyncJobService.createNewJob(params)
//
//        response.setContentType("text/json")
//        response.outputStream << result?.toString()
//    }
    
    /**
     * Cancel a running job
     *
     * @param jobName
     * @param group
     *
     */
    def cancelJob = {
        def result = exportAsyncJobService.cancelJob(params.jobName, params.group)
        
        response.setContentType("text/json")
        response.outputStream << result?.toString()
    }
    
    /**
     * Repeatedly called by datasetExplorer.js to get the job status and results
     */
    def checkJobStatus = {
        def result = exportAsyncJobService.checkJobStatus(params.jobName)
        
        def statusIndexExists = result.get('statusIndexExists')
        if (statusIndexExists) {
            def statusIndex = result.get('statusIndex')
            def statusHtml = g.render(template: "/genePattern/jobStatusList", model: [jobStatuses: jobResultsService[params.jobName]["StatusList"], statusIndex: statusIndex]).toString();
            result.put('jobStatusHTML', statusHtml)
            
            result.remove('statusIndex')
            result.remove('statusIndexExists')
        }
        
        response.setContentType("text/json")
        response.outputStream << result?.toString()
    }
}
