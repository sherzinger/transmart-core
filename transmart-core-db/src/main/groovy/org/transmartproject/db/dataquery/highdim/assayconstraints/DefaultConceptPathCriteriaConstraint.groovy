/*
 * Copyright © 2013-2014 The Hyve B.V.
 *
 * This file is part of transmart-core-db.
 *
 * Transmart-core-db is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * transmart-core-db.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.transmartproject.db.dataquery.highdim.assayconstraints

import grails.gorm.DetachedCriteria
import groovy.transform.Canonical
import org.grails.datastore.mapping.query.api.Criteria
import org.transmartproject.core.ontology.OntologyTerm
import org.transmartproject.db.i2b2data.ConceptDimension

@Canonical
class DefaultConceptPathCriteriaConstraint implements AssayCriteriaConstraint {

    String conceptPath

    @Override
    void addToCriteria(Criteria criteria) {
        def subCriteria = new DetachedCriteria(ConceptDimension)
                .property('conceptCode')
                .eq('conceptPath', conceptPath)
        criteria.in('conceptCode', subCriteria)
    }

}
