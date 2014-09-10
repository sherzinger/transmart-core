package org.transmartproject.batch.clinical

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.transmartproject.batch.model.Row
import org.transmartproject.batch.reader.GenericRowReader
import org.transmartproject.batch.support.MappingHelper

/**
 *
 */
class DataRowReader extends GenericRowReader<Row> {

    @Autowired
    ClinicalJobContext jobContext

    @Value("#{jobParameters['dataLocation']}")
    String dataLocation

    @Override
    List<Resource> getResourcesToProcess() {
        if (dataLocation == null) {
            throw new IllegalArgumentException('Data location not defined')
        }

        //obtains the list of data filenames
        Set<String> filenames = jobContext.variables.collect { it.filename } as Set
        List<Resource> resources = []
        filenames.each {
            File file = new File(dataLocation, it)
            if (!file.exists()) {
                throw new IllegalArgumentException("Data file not found: $file.absolutePath")
            }
            resources.add(new FileSystemResource(file))
        }
        resources
    }

    @Override
    Row mapLine(String line, int lineNumber) throws Exception {
        Row row
        if (line) {
            row = new Row(
                    filename: getCurrentResource().filename,
                    index: lineNumber,
                    values: MappingHelper.parseValues(line)
            )
        }
        row
    }
}
