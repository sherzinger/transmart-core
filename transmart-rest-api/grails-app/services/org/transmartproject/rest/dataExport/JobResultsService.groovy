package org.transmartproject.rest.dataExport

/**
 * JobResultsService handles the management of the asynchronous job results
 *
 * Duplicates JobResultsService from Rmodules plugin (unnecessary dependency for rest-api)
 *
 */
class JobResultsService {

    @Delegate Map jobResults = [:].asSynchronized()

    boolean isJobCancelled(String jobName) {
        boolean isJobCancelled = jobResults[jobName]["Status"] == JobStatus.CANCELLED
        if (isJobCancelled) {
            log.warn("Job ${jobName} has been cancelled")
        }
        isJobCancelled
    }
}
