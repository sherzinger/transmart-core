package org.transmartproject.rest

import com.recomdata.transmart.domain.i2b2.AsyncJob
import grails.converters.JSON
import grails.transaction.Transactional
import org.grails.web.json.JSONObject
import org.quartz.JobBuilder
import org.quartz.JobDataMap
import org.quartz.JobDetail
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.transmartproject.db.user.User
import org.transmartproject.rest.dataExport.DataExportService
import org.transmartproject.rest.dataExport.ExportAsyncJobService
import org.transmartproject.rest.dataExport.JobResultsService
import org.transmartproject.rest.dataExport.JobStatus
import org.transmartproject.rest.dataExport.ExportJobExecutor
import sun.reflect.generics.reflectiveObjects.NotImplementedException

@Transactional
class RestExportService {
    
    Scheduler quartzScheduler
    JobResultsService jobResultsService
    ExportAsyncJobService exportAsyncJobService
    DataExportService dataExportService
    
    
    // TODO add support for other file formats
    static enum FileFormat {
        TSV('TSV'),
        
        final String value
    
        FileFormat(String value) { this.value = value }
    
        String toString() { value }
        String getKey() { name() }
    }
    
    static enum SupportedSetTypes {
        OBSERVATION('observation'),
        PATIENT('patient')
        
        final String value
        
        SupportedSetTypes(String value) { this.value = value }
        
        String toString() { value }
        String getKey() { name() }
    }
    
    static supportedFileFormats = FileFormat.values().collect {it.toString()}
    static supportedSetTypes =  SupportedSetTypes.values().collect {it.toString()}
    
    def createExportJob(params, userName) {
        def analysis = 'DataExport'
        def jobStatus = JobStatus.STARTED
    
        def newJob = new AsyncJob(lastRunOn: new Date())
        newJob.jobType = analysis
        newJob.jobStatus = jobStatus.value
        newJob.save()
    
        String jobName = userName + "-" + analysis + "-" + newJob.id
        newJob.jobName = jobName
        newJob.altViewerURL = 'Test'
        newJob.save()
    
        jobResultsService[jobName] = [:]
        def querySummary = params.querySummary
        exportAsyncJobService.updateStatus(jobName, jobStatus, null, querySummary, null)
    
        log.debug("Sending ${newJob.jobName} back to the client")
        JSONObject result = new JSONObject()
        result.put("jobName", jobName)
        result.put("jobStatus", jobStatus.value)
    
        result
    }
    
    def exportData(jobName, params, userName) {
      
        // update job status
        // exportAsyncJobService.updateStatus(jobName, JobStatus.STARTED)
    
        // validate input parameters and parse to checkboxList
        // exportAsyncJobService.updateStatus(jobName, JobStatus.VALIDATING_COHORT_INFO)
        
        // check if job not cancelled
        if (exportAsyncJobService.cancelJob(jobName)) return
        
        // create export data job
        executeExportJob(jobName, params, userName)
        
    }
    
    
    def export(jobName, params, userName){
        def jobDataMap = createJobDataMap(jobName, params, userName)
        dataExportService.exportData(jobDataMap)
    }
    
    
    def downloadFile(jobName) {
        def job = AsyncJob.findByJobName(jobName)
        def exportJobExecutor = new ExportJobExecutor()

        return exportJobExecutor.getExportJobFileStream(job.viewerURL)
    }
    
    def private executeExportJob(jobName, params, userName) {
        
        def jobDataMap = new JobDataMap(createJobDataMap(jobName, params, userName))
        
        JobDetail jobDetail = JobBuilder.newJob(ExportJobExecutor.class)
                .withIdentity(jobName, 'DataExport')
                .setJobData(jobDataMap)
                .build()
        
        // update job status
        exportAsyncJobService.updateStatus(jobName, JobStatus.TRIGGERING_JOB)
        
        def randomDelay = Math.random()*10 as int
        def startTime = new Date(new Date().time + randomDelay)
        def trigger = TriggerBuilder.newTrigger()
                .startAt(startTime)
                .withIdentity('DataExport')
                .build()
        quartzScheduler.scheduleJob(jobDetail, trigger)
    }
    
    def getDataFormats(setType, id) {
        throw NotImplementedException()
    }
    
    Map createJobDataMap(jobName, params, User user) {
                
        List dataTypeAndFormatList = JSON.parse(params.types)
        List<Integer> ids = JSON.parse(params.ids)
        
        return [
                analysis                      : "DataExport",
                user                          : user,
                jobName                       : jobName,
                ids                           : ids,
                dataTypeAndFormatList         : dataTypeAndFormatList,
                highDimDataTypes              : '',//getHighDimDataTypesAndFormats(checkboxList),
                subsetSelectedPlatformsByFiles: '',//getsubsetSelectedPlatformsByFiles(checkboxList),
                subsetSelectedFilesMap        : '',//getSubsetSelectedFilesMap(checkedFileTypes),
                resultType                    : "DataExport",
        ]
    }
}
