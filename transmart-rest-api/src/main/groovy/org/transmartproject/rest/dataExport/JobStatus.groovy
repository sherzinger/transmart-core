package org.transmartproject.rest.dataExport

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@CompileStatic
@Slf4j
enum JobStatus {
   
    STARTED ("Started"),
    VALIDATING_COHORT_INFO ("Validating Cohort Information"),
    TRIGGERING_JOB ("Triggering Data-Export Job"),
    GATHERING_DATA ("Gathering Data"),
    RUN_CONVERSION ("Running Conversions"),
    RUN_ANALYSIS ("Running Analysis"),
    RENDER_OUTPUT ("Rendering Output"),
    CANCELLED ("Cancelled"),
    COMPLETED ("Completed"),
    ERROR ("Error")
    
    final String value
    
    JobStatus(String value) { this.value = value }
    
    String toString() { value }
    String getKey() { name() }
}
