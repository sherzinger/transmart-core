package org.transmartproject.rest.dataExport

import grails.transaction.Transactional
import grails.util.Holders
import org.apache.commons.lang.StringUtils
import org.transmartproject.core.exceptions.InvalidRequestException
import org.transmartproject.core.exceptions.LegacyStudyException
import org.transmartproject.core.multidimquery.Hypercube
import org.transmartproject.core.multidimquery.MultiDimConstraint
import org.transmartproject.core.users.User
import org.transmartproject.db.multidimquery.query.Constraint
import org.transmartproject.db.multidimquery.query.OrConstraint
import org.transmartproject.db.multidimquery.query.PatientSetConstraint
import org.transmartproject.rest.MultidimensionalDataService
import org.transmartproject.rest.MultidimensionalDataService.Format as Format
import org.transmartproject.rest.RestExportService


@Transactional
class DataExportService {
    
    def grailsApplication
    MultidimensionalDataService multidimensionalDataService
    final String tempFolderDirectory = Holders.config.com.recomdata.plugins.tempFolderDirectory

    def exportData(jobDataMap) {
        
        def dataTypeAndFormatList = jobDataMap.dataTypeAndFormatList.flatten()
        def user = jobDataMap.user
        Constraint constraint = new OrConstraint(args:  jobDataMap.ids.collect { new PatientSetConstraint(patientSetId: it) } )
        dataTypeAndFormatList.each {
            if (it.dataType == 'clinical') {
                try {
                    String jobTmpDirectory = ''
                    if (StringUtils.isEmpty(tempFolderDirectory)) {
                          //  throw new Exception('Job temp directory needs to be specified') TODO
                        jobTmpDirectory = '/var/tmp/jobs/'
                    } else {
                        jobTmpDirectory = tempFolderDirectory
                    }

                    File jobDirectory = new File(jobTmpDirectory, jobDataMap.jobName)
                    jobDirectory.mkdir()
                    Map args = [
                            directory : jobDirectory,
                            dataType : it.dataType
                    ]
                    multidimensionalDataService.writeClinical(args, Format[it.format], constraint, user, null)
                } catch (LegacyStudyException e) {
                    throw new InvalidRequestException("This endpoint does not support legacy studies.", e)
                }
            } else {
                // TODO highDim
            }
        }
    }
    
    /**
     * Write clinical data to the output stream
     *
     * @param format
     * @param constraint
     * @param user The user accessing the data
     * @param out
     */
    void writeClinical(RestExportService.FileFormat format, MultiDimConstraint constraint, User user, OutputStream out) {
        
        Hypercube result = multiDimService.retrieveClinicalData(constraint, user)
        
        try {
            log.info "Writing to format: ${format}"
            serialise(result, format, out)
        } finally {
            result.close()
        }
    }
}

