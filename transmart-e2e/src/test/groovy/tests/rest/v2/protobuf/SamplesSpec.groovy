package tests.rest.v2.protobuf

import base.RESTSpec
import protobuf.ObservationsMessageProto
import selectors.protobuf.ObservationSelector
import spock.lang.IgnoreIf
import spock.lang.Requires

import static config.Config.*
import static org.hamcrest.Matchers.*
import static spock.util.matcher.HamcrestSupport.that
import static tests.rest.v2.Operator.EQUALS
import static tests.rest.v2.ValueType.NUMERIC
import static tests.rest.v2.ValueType.STRING
import static tests.rest.v2.constraints.ModifierConstraint
import static tests.rest.v2.constraints.ValueConstraint

class SamplesSpec extends RESTSpec{

    /**
     *  given: "study TUMOR_NORMAL_SAMPLES is loaded"
     *  when: "I get all observations related to a modifier "Sample type" with value "Tumor""
     *  then: "3 observations are returned, all have a cellcount"
     */
    @Requires({TUMOR_NORMAL_SAMPLES_LOADED})
    def "get observations related to a modifier"(){
        given: "study TUMOR_NORMAL_SAMPLES is loaded"

        when: "I get all observations related to a modifier 'Sample type' with value 'Tumor'"
        def constraintMap = [
                type: ModifierConstraint, path:"\\Public Studies\\TUMOR_NORMAL_SAMPLES\\Sample Type\\",
                values: [type: ValueConstraint, valueType: STRING, operator: EQUALS, value: "Tumor"]
        ]

        ObservationsMessageProto responseData = getProtobuf(PATH_HYPERCUBE, toQuery(constraintMap))

        then: "3 observations are returned, all have a cellcount"
        ObservationSelector selector = new ObservationSelector(responseData)

        assert selector.cellCount == 8
        (0..<selector.cellCount).each {
            assert ['TNS:HD:EXPLUNG', 'TNS:HD:EXPBREAST', 'TNS:LAB:CELLCNT'].contains(selector.select(it, "ConceptDimension", "conceptCode", 'String'))
            assert selector.select(it) != null
        }
    }

    /**
     *  given: "study TUMOR_NORMAL_SAMPLES is loaded"
     *  when: "I get all observations related to a modifier 'Sample ID' with value 'id'"
     *  then: "3 observations are returned with concept codes: CELLCNT, .., ..."
     */
    @Requires({TUMOR_NORMAL_SAMPLES_LOADED})
    @IgnoreIf({SUPPRESS_UNIMPLEMENTED}) //no test data with multiple concepts linked to a modifier
    def "get observations related to a"(){
        given: "study TUMOR_NORMAL_SAMPLES is loaded"

        when: "I get all observations related to a modifier 'Sample ID' with value 'id'"
        def constraintMap = [
                type: ModifierConstraint, path:"\\Public Studies\\TUMOR_NORMAL_SAMPLES\\Sample ID\\",
                values: [type: ValueConstraint, valueType: NUMERIC, operator: EQUALS, value: 10]
        ]

        ObservationsMessageProto responseData = getProtobuf(PATH_HYPERCUBE, toQuery(constraintMap))

        then: "3 observations are returned, all have a cellcount"
        ObservationSelector selector = new ObservationSelector(responseData)

        assert selector.cellCount == 3
        (0..<selector.cellCount).each {
            assert (selector.select(it, "ConceptDimension", "conceptCode", 'String').equals('TNS:LAB:CELLCNT') ||
                    selector.select(it, "ConceptDimension", "conceptCode", 'String').equals('....'))
            assert selector.select(it) != null
        }
    }
}