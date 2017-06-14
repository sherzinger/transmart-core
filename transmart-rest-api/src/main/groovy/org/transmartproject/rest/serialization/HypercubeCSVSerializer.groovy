package org.transmartproject.rest.serialization

import au.com.bytecode.opencsv.CSVWriter
import groovy.transform.CompileStatic
import org.transmartproject.core.multidimquery.Dimension
import org.transmartproject.core.multidimquery.Hypercube
import org.transmartproject.core.multidimquery.HypercubeValue

@CompileStatic
class HypercubeCSVSerializer extends HypercubeSerializer {

    final static char COLUMN_SEPARATOR = '\t' as char
    final static String FORMAT_EXTENSION = ".tsv"
    protected Hypercube cube
    protected String dataType
    protected File directory

    protected File createObservationsFile() {
        File observationFile = new File(directory, defineTableName('observations'))
        List header = createObservationsHeader()
        
        observationFile.withWriter { Writer writer ->
            CSVWriter csvWriter = new CSVWriter(writer, COLUMN_SEPARATOR)
            csvWriter.writeNext(header as String[])
            writeObservationsColumns(csvWriter)
        }
        
        observationFile
    }

    /**
     * Creates new file foreach dense dimension
     *
     * TODO Merge all tables into one after implementing preloading of dimensions in the hypercube
     */
    protected void createDimensionsFiles() {
        for (dim in cube.dimensions.findAll { it.density.isDense }) {
            File dimensionFile = new File(directory, defineTableName(dim.name))
            List header = createDimensionHeader(dim)
            
            dimensionFile.withWriter { Writer writer ->
                CSVWriter csvWriter = new CSVWriter(writer, COLUMN_SEPARATOR)
                csvWriter.writeNext(header as String[])
                writeDimensionColumns(csvWriter, dim)
            }
        }
    }

    protected List createObservationsHeader() {
        def header = []
        header << 'value'
        for (Dimension dim : cube.dimensions) {
            header << dim.name //TODO change after adding preloading of dimensions
        }
        header
    }

    protected List createDimensionHeader(Dimension dim) {
        def header = []
        header << 'dimensionId'
        dim.elementFields ? dim.elementFields.each { header << it.value.name }
                : header << dim.name
        header
    }

    protected void writeObservationsColumns(CSVWriter writer) {
        Iterator<HypercubeValue> it = cube.iterator()
        while (it.hasNext()) {
            HypercubeValue value = it.next()
            def row = []
            row << value.value.toString()
            for (Dimension dim : cube.dimensions) {
                if (dim.density.isSparse) {
                    // Add the value element inline
                    row << value[dim] ?: "null"
                } else {
                    // Add index to footer element inline. This may be null.
                    row << value.getDimElementIndex(dim) ?: "null"
                }
            }
            writer.writeNext(row as String[])
        }
    }

    protected void writeDimensionColumns(CSVWriter csvWriter, Dimension dim) {
        cube.dimensionElements(dim).eachWithIndex { element, index ->
            def row = []
            row << index
            row.addAll(dim.elementFields.collect { it.value.get(element).toString() })
            csvWriter.writeNext(row as String[])
        }
    }

    private String defineTableName(String dimension) {
        dimension.replace(' ', '_') + "_" + dataType + FORMAT_EXTENSION
    }

    void write(Map args, Hypercube cube, OutputStream out) {
        this.directory = args.directory as File
        assert directory.exists() && directory.isDirectory()
        this.cube = cube
        this.dataType = args.dataType
        this.directory = args.directory as File

        createObservationsFile()
        createDimensionsFiles()
    }

}
