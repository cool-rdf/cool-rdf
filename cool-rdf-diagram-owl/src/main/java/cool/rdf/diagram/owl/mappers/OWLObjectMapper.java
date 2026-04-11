/*
 * Copyright Cool RDF project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cool.rdf.diagram.owl.mappers;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

import cool.rdf.diagram.owl.graph.Graph;

/**
 * Dispatcher of multiple types of OWL objects; this is called in some generic mapping operations.
 * Maps
 * {@link org.semanticweb.owlapi.model.OWLObject}s to {@link Graph}s.
 */
public class OWLObjectMapper implements OWLObjectVisitorEx<Graph> {
   private final MappingConfiguration mappingConfig;

   /**
    * Creates a new object mapper from a given mapping config
    *
    * @param mappingConfig the config
    */
   public OWLObjectMapper( final MappingConfiguration mappingConfig ) {
      this.mappingConfig = mappingConfig;
   }

   @Override
   public Graph visit( final OWLObjectIntersectionOf classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLObjectUnionOf classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLObjectComplementOf classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLObjectSomeValuesFrom classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLObjectAllValuesFrom classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLObjectHasValue classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLObjectMinCardinality classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLObjectExactCardinality classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLObjectMaxCardinality classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLObjectHasSelf classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLObjectOneOf classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLDataSomeValuesFrom classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLDataAllValuesFrom classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLDataHasValue classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLDataMinCardinality classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLDataExactCardinality classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLDataMaxCardinality classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLObjectInverseOf property ) {
      return mappingConfig.getOwlPropertyExpressionMapper().visit( property );
   }

   @Override
   public Graph visit( final OWLObjectProperty property ) {
      return mappingConfig.getOwlPropertyExpressionMapper().visit( property );
   }

   @Override
   public Graph visit( final OWLDataProperty property ) {
      return mappingConfig.getOwlPropertyExpressionMapper().visit( property );
   }

   @Override
   public Graph visit( final OWLAnnotationProperty property ) {
      return mappingConfig.getOwlPropertyExpressionMapper().visit( property );
   }

   @Override
   public Graph visit( final OWLClass classExpression ) {
      return mappingConfig.getOwlClassExpressionMapper().visit( classExpression );
   }

   @Override
   public Graph visit( final OWLNamedIndividual individual ) {
      return mappingConfig.getOwlIndividualMapper().visit( individual );
   }

   @Override
   public Graph visit( final OWLLiteral literal ) {
      return mappingConfig.getOwlDataMapper().visit( literal );
   }

   @Override
   public Graph visit( final OWLDatatype datatype ) {
      return mappingConfig.getOwlDataMapper().visit( datatype );
   }

   @Override
   public Graph visit( final OWLDataComplementOf complement ) {
      return mappingConfig.getOwlDataMapper().visit( complement );
   }

   @Override
   public Graph visit( final OWLDataOneOf oneOf ) {
      return mappingConfig.getOwlDataMapper().visit( oneOf );
   }

   @Override
   public Graph visit( final OWLDataIntersectionOf intersection ) {
      return mappingConfig.getOwlDataMapper().visit( intersection );
   }

   @Override
   public Graph visit( final OWLDataUnionOf union ) {
      return mappingConfig.getOwlDataMapper().visit( union );
   }

   @Override
   public Graph visit( final OWLDatatypeRestriction restriction ) {
      return mappingConfig.getOwlDataMapper().visit( restriction );
   }

   @Override
   public Graph visit( final OWLFacetRestriction restriction ) {
      return mappingConfig.getOwlDataMapper().visit( restriction );
   }
}
