package org.transmartproject.rest.dataExport

import com.recomdata.transmart.domain.i2b2.AsyncJob
import grails.transaction.Transactional
import org.apache.commons.lang.StringUtils
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.quartz.JobKey
import org.quartz.Scheduler

@Transactional
class ExportAsyncJobService {
    
    Scheduler quartzScheduler
    def springSecurityService
    JobResultsService jobResultsService

    /**
     * Get the list of jobs to show in the jobs tab
     */
    def getJobList() {
        JSONObject result = new JSONObject()
        JSONArray rows = new JSONArray()

        def userName = springSecurityService.getPrincipal().username
        def jobResults = AsyncJob.createCriteria {
            like("jobName", "${userName}%")
            or {
                ne("jobType", "DataExport")
                isNull("jobType")
            }
            ge("lastRunOn", new Date() - 7)
            order("id", "desc")
        }

        for (jobResult in jobResults) {
            def m = [:]
            m["name"] = jobResult.jobName
            m["type"] = jobResult.jobType
            m["status"] = jobResult.jobStatus
            m["runTime"] = jobResult.jobStatusTime
            m["startDate"] = jobResult.lastRunOn
            m["viewerURL"] = jobResult.viewerURL
            m["altViewerURL"] = jobResult.altViewerURL
            rows.put(m)
        }
        
        result.put("success", true)
        result.put("totalCount", jobResults.size())
        result.put("jobs", rows)

        return result
    }

    /**
     * Create the new asynchronous job
     * Default name is username-jobtype-ID from sequence generator (for compliance with transmartApp)
     */
//    def createNewJob(params) {
//        def userName = springSecurityService.getPrincipal().username
//        def jobStatus = JobStatus.STARTED
//
//        def newJob = new AsyncJob(lastRunOn: new Date())
//        newJob.save()
//
//        def jobName = params?.jobName
//        //if (StringUtils.isEmpty(jobName)) { TODO for now always default name
//            def jobNameBuf = new StringBuffer(userName)
//            jobNameBuf.append('-')
//            if (StringUtils.isNotEmpty(params.jobType)) jobNameBuf.append(params.jobType)
//            jobNameBuf.append('-').append(newJob.id)
//            jobName = jobNameBuf.toString()
//        //}
//        newJob.jobName = jobName
//        newJob.jobType = "DataExport"
//        newJob.jobStatus = jobStatus.value
//        newJob.jobInputsJson = new JSONObject(params).toString()
//        newJob.save()
//
//        jobResultsService[jobName] = [:]
//        updateStatus(newJob.id, jobStatus)
//
//        log.debug("Sending ${jobName} back to the client")
//        JSONObject result = new JSONObject()
//        result.put("jobName", jobName)
//        result.put("jobStatus", jobStatus.value)
//
//        return result;
//    }
    
    /**
     * Cancel a running job
     */
    def cancelJob(jobName, group = null) {
        def jobStatus = JobStatus.CANCELLED
        log.debug("Attempting to delete ${jobName} from the Quartz scheduler")
        def result = quartzScheduler.deleteJob(new JobKey(jobName, group))
        log.debug("Deletion attempt successful? ${result}")
        
        updateStatus(jobName, jobStatus)
        
        JSONObject jsonResult = new JSONObject()
        jsonResult.put("jobName", jobName)
        return jsonResult
    }
    
    /**
     * Repeatedly called by datasetExplorer.js to get the job status and results
     */
    def checkJobStatus(jobName) {
        JSONObject result = new JSONObject()
        def jobType = "DataExport"
        def jobStatus = jobResultsService[jobName]["Status"]
        def statusIndex = null
        
        if (jobResultsService[jobName]["StatusList"] != null) {
            statusIndex = jobResultsService[jobName]["StatusList"].indexOf(jobStatus)
        }
        def jobException = jobResultsService[jobName]["Exception"]
        def viewerURL = jobResultsService[jobName]["ViewerURL"]
        def altViewerURL = jobResultsService[jobName]["AltViewerURL"]
        def jobResults = jobResultsService[jobName]["Results"]
        def errorType = ""
        if (viewerURL != null) {
            def jobResultType = jobResultsService[jobName]["resultType"]
            if (jobResultType != null) result.put("resultType", jobResultType)
            log.debug("${viewerURL} is being sent to the client")
            result.put("jobViewerURL", viewerURL)
            if (altViewerURL != null) {
                log.debug("${altViewerURL} for Comparative Marker Selection")
                result.put("jobAltViewerURL", altViewerURL)
            }
            jobStatus = JobStatus.COMPLETED
        } else if (jobResults != null) {
            result.put("jobResults", jobResults)
            result.put("resultType", jobType)
            jobStatus = JobStatus.COMPLETED
        } else if (jobException != null) {
            log.warn("An exception was thrown, passing this back to the user")
            log.warn(jobException)
            result.put("jobException", jobException)
            jobStatus = JobStatus.ERROR
            errorType = "data"
        }
        if (statusIndex != null) {
            result.put('statusIndexExists', true)
            result.put("statusIndex", statusIndex)
        } else {
            result.put('statusIndexExists', false)
        }
        
        updateStatus(jobName, jobStatus, viewerURL, altViewerURL, jobResults)
        
        result.put("jobStatus", jobStatus.value)
        result.put("errorType", errorType)
        result.put("jobName", jobName)
        
        return result
    }
    
    /**
     * Helper to update the status of the job and log it
     *
     * @return true if the job was cancelled
     */
    private def updateStatus(String jobName, JobStatus status, viewerURL = null, altViewerURL = null, results = null) {
        
        if (jobResultsService.isJobCancelled(jobName)) return true
        
        def asyncJob = AsyncJob.findByJobName(jobName)
        jobResultsService[jobName]["Status"] = status
        asyncJob.jobStatus = status.value
        if (viewerURL && viewerURL != '') asyncJob.viewerURL = viewerURL
        if (altViewerURL && altViewerURL != '' && asyncJob.altViewerURL != null) asyncJob.altViewerURL = altViewerURL
        if (results && results != '') asyncJob.results = results
        jobResultsService[jobName]["ViewerURL"] = viewerURL
        
        asyncJob.save(flush: true)
        return false
    }
}
