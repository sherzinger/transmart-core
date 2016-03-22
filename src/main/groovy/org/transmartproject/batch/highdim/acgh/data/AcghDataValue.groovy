package org.transmartproject.batch.highdim.acgh.data

import groovy.transform.Canonical
import groovy.transform.ToString
import org.transmartproject.batch.highdim.datastd.PatientInjectionSupport
import org.transmartproject.batch.patient.Patient

/**
 * Acgh data bean.
 */
@Canonical
@ToString(includeNames=true)
class AcghDataValue implements PatientInjectionSupport {

    String regionName
    String sampleCode

    Integer flag

    Double chip
    Double segmented

    Double probHomLoss
    Double probLoss
    Double probNorm
    Double probGain
    Double probAmp

    Patient patient
}
