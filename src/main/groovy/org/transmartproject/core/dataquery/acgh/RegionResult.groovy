package org.transmartproject.core.dataquery.acgh

import org.transmartproject.core.dataquery.assay.Assay
import org.transmartproject.core.dataquery.DataQueryResult

/**
 * The {@link DataQueryResult} type used for aCHG region queries.
 * @deprecate To be removed when aCGH is ported to the new interface.
 */
@Deprecated
public interface RegionResult extends DataQueryResult<Assay, RegionRow> {}
