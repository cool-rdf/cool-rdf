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

package cool.rdf.core.model.impl;

import static cool.rdf.core.util.IteratorStream.stream;

import java.util.List;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.RDFVisitor;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StatementTerm;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shared.PropertyNotFoundException;
import org.apache.jena.vocabulary.RDF;

import cool.rdf.core.model.RdfResource;

public class DefaultRdfResource extends DefaultRdfNode implements RdfResource {
   private final Resource resource;

   public DefaultRdfResource( final Resource resource ) {
      super( resource );
      this.resource = resource;
   }

   public Statement property( final Property property ) {
      final List<Statement> statements = stream( listProperties( property ) ).toList();
      if ( statements.size() != 1 ) {
         throw new PropertyNotFoundException( property );
      }
      return statements.getFirst();
   }

   @Override
   public Literal literalValue( final Property property ) {
      final RDFNode object = property( property ).getObject();
      if ( !object.isLiteral() ) {
         throw new PropertyNotFoundException( property );
      }
      return object.asLiteral();
   }

   @Override
   public boolean isList() {
      return resource.equals( RDF.Nodes.nil ) || ( resource.hasProperty( RDF.rest ) && resource.hasProperty( RDF.first ) );
   }

   @Override
   public AnonId getId() {
      return resource.getId();
   }

   @Override
   public Resource inModel( final Model m ) {
      return resource.inModel( m );
   }

   @Override
   public boolean hasURI( final String uri ) {
      return resource.hasURI( uri );
   }

   @Override
   public String getURI() {
      return resource.getURI();
   }

   @Override
   public String getNameSpace() {
      return resource.getNameSpace();
   }

   @Override
   public String getLocalName() {
      return resource.getLocalName();
   }

   @Override
   public Statement getRequiredProperty( final Property p ) {
      return resource.getRequiredProperty( p );
   }

   @Override
   public Statement getRequiredProperty( final Property p, final String lang ) {
      return resource.getRequiredProperty( p, lang );
   }

   @Override
   public Statement getProperty( final Property p ) {
      return resource.getProperty( p );
   }

   @Override
   public Statement getProperty( final Property p, final String lang ) {
      return resource.getProperty( p, lang );
   }

   @Override
   public StmtIterator listProperties( final Property p ) {
      return resource.listProperties( p );
   }

   @Override
   public StmtIterator listProperties( final Property p, final String lang ) {
      return resource.listProperties( p, lang );
   }

   @Override
   public StmtIterator listProperties() {
      return resource.listProperties();
   }

   @Override
   public Resource addLiteral( final Property p, final boolean o ) {
      return resource.addLiteral( p, o );
   }

   @Override
   public Resource addLiteral( final Property p, final long o ) {
      return resource.addLiteral( p, o );
   }

   @Override
   public Resource addLiteral( final Property p, final char o ) {
      return resource.addLiteral( p, o );
   }

   @Override
   public Resource addLiteral( final Property value, final double d ) {
      return resource.addLiteral( value, d );
   }

   @Override
   public Resource addLiteral( final Property value, final float d ) {
      return resource.addLiteral( value, d );
   }

   @Override
   public Resource addLiteral( final Property p, final Object o ) {
      return resource.addLiteral( p, o );
   }

   @Override
   public Resource addLiteral( final Property p, final Literal o ) {
      return resource.addLiteral( p, o );
   }

   @Override
   public Resource addProperty( final Property p, final String o ) {
      return resource.addProperty( p, o );
   }

   @Override
   public Resource addProperty( final Property p, final String o, final String l ) {
      return resource.addProperty( p, o, l );
   }

   @Override
   public Resource addProperty( final Property p, final String lexicalForm, final RDFDatatype datatype ) {
      return resource.addProperty( p, lexicalForm, datatype );
   }

   @Override
   public Resource addProperty( final Property p, final RDFNode o ) {
      return resource.addProperty( p, o );
   }

   @Override
   public boolean hasProperty( final Property p ) {
      return resource.hasProperty( p );
   }

   @Override
   public boolean hasLiteral( final Property p, final boolean o ) {
      return resource.hasLiteral( p, o );
   }

   @Override
   public boolean hasLiteral( final Property p, final long o ) {
      return resource.hasLiteral( p, o );
   }

   @Override
   public boolean hasLiteral( final Property p, final char o ) {
      return resource.hasLiteral( p, o );
   }

   @Override
   public boolean hasLiteral( final Property p, final double o ) {
      return resource.hasLiteral( p, o );
   }

   @Override
   public boolean hasLiteral( final Property p, final float o ) {
      return resource.hasLiteral( p, o );
   }

   @Override
   public boolean hasLiteral( final Property p, final Object o ) {
      return resource.hasLiteral( p, o );
   }

   @Override
   public boolean hasProperty( final Property p, final String o ) {
      return resource.hasProperty( p, o );
   }

   @Override
   public boolean hasProperty( final Property p, final String o, final String l ) {
      return resource.hasProperty( p, o, l );
   }

   @Override
   public boolean hasProperty( final Property p, final RDFNode o ) {
      return resource.hasProperty( p, o );
   }

   @Override
   public Resource removeProperties() {
      return resource.removeProperties();
   }

   @Override
   public Resource removeAll( final Property p ) {
      return resource.removeAll( p );
   }

   @Override
   public Resource begin() {
      return resource.begin();
   }

   @Override
   public Resource abort() {
      return resource.abort();
   }

   @Override
   public Resource commit() {
      return resource.commit();
   }

   @Override
   public Resource getPropertyResourceValue( final Property p ) {
      return resource.getPropertyResourceValue( p );
   }

   @Override
   public boolean isAnon() {
      return resource.isAnon();
   }

   @Override
   public boolean isLiteral() {
      return resource.isLiteral();
   }

   @Override
   public boolean isURIResource() {
      return resource.isURIResource();
   }

   @Override
   public boolean isResource() {
      return resource.isResource();
   }

   @Override
   public boolean isStatementTerm() {
      return resource.isStatementTerm();
   }

   @Override
   public <T extends RDFNode> T as( final Class<T> view ) {
      return resource.as( view );
   }

   @Override
   public <T extends RDFNode> boolean canAs( final Class<T> view ) {
      return resource.canAs( view );
   }

   @Override
   public Model getModel() {
      return resource.getModel();
   }

   @Override
   public Object visitWith( final RDFVisitor rv ) {
      return resource.visitWith( rv );
   }

   @Override
   public Resource asResource() {
      return resource.asResource();
   }

   @Override
   public Literal asLiteral() {
      return resource.asLiteral();
   }

   @Override
   public StatementTerm asStatementTerm() {
      return resource.asStatementTerm();
   }

   @Override
   public Node asNode() {
      return resource.asNode();
   }
}
