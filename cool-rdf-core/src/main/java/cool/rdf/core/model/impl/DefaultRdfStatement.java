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

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Alt;
import org.apache.jena.rdf.model.Bag;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Seq;
import org.apache.jena.rdf.model.Statement;

import cool.rdf.core.model.RdfNode;
import cool.rdf.core.model.RdfProperty;
import cool.rdf.core.model.RdfResource;
import cool.rdf.core.model.RdfStatement;

public class DefaultRdfStatement implements RdfStatement {
   private final Statement statement;

   public DefaultRdfStatement( final Statement statement ) {
      this.statement = statement;
   }

   @Override
   public RdfResource subject() {
      final Resource resource = getResource();
      return resource instanceof final RdfResource rdfResource ? rdfResource : new DefaultRdfResource( resource );
   }

   @Override
   public RdfProperty predicate() {
      final Property property = getPredicate();
      return property instanceof final RdfProperty rdfProperty ? rdfProperty : new DefaultRdfProperty( property );
   }

   @Override
   public RdfNode object() {
      final RDFNode object = getObject();
      return object instanceof final RdfNode rdfNode ? rdfNode : new DefaultRdfNode( object );
   }

   @Override
   public Resource getSubject() {
      return statement.getSubject();
   }

   @Override
   public Property getPredicate() {
      return statement.getPredicate();
   }

   @Override
   public RDFNode getObject() {
      return statement.getObject();
   }

   @Override
   public Statement getProperty( final Property p ) {
      return statement.getProperty( p );
   }

   @Override
   public Statement getStatementProperty( final Property p ) {
      return statement.getStatementProperty( p );
   }

   @Override
   public Resource getResource() {
      return statement.getResource();
   }

   @Override
   public Literal getLiteral() {
      return statement.getLiteral();
   }

   @Override
   public boolean getBoolean() {
      return statement.getBoolean();
   }

   @Override
   public byte getByte() {
      return statement.getByte();
   }

   @Override
   public short getShort() {
      return statement.getShort();
   }

   @Override
   public int getInt() {
      return statement.getInt();
   }

   @Override
   public long getLong() {
      return statement.getLong();
   }

   @Override
   public char getChar() {
      return statement.getChar();
   }

   @Override
   public float getFloat() {
      return statement.getFloat();
   }

   @Override
   public double getDouble() {
      return statement.getDouble();
   }

   @Override
   public String getString() {
      return statement.getString();
   }

   @Override
   public Bag getBag() {
      return statement.getBag();
   }

   @Override
   public Alt getAlt() {
      return statement.getAlt();
   }

   @Override
   public Seq getSeq() {
      return statement.getSeq();
   }

   @Override
   public RDFList getList() {
      return statement.getList();
   }

   @Override
   public String getLanguage() {
      return statement.getLanguage();
   }

   @Override
   public Statement changeLiteralObject( final boolean o ) {
      return statement.changeLiteralObject( o );
   }

   @Override
   public Statement changeLiteralObject( final long o ) {
      return statement.changeLiteralObject( o );
   }

   @Override
   public Statement changeLiteralObject( final int o ) {
      return statement.changeLiteralObject( o );
   }

   @Override
   public Statement changeLiteralObject( final char o ) {
      return statement.changeLiteralObject( o );
   }

   @Override
   public Statement changeLiteralObject( final float o ) {
      return statement.changeLiteralObject( o );
   }

   @Override
   public Statement changeLiteralObject( final double o ) {
      return statement.changeLiteralObject( o );
   }

   @Override
   public Statement changeObject( final String o ) {
      return statement.changeObject( o );
   }

   @Override
   public Statement changeObject( final String o, final String l ) {
      return statement.changeObject( o, l );
   }

   @Override
   public Statement changeObject( final RDFNode o ) {
      return statement.changeObject( o );
   }

   @Override
   public Statement remove() {
      return statement.remove();
   }

   @Override
   public Model getModel() {
      return statement.getModel();
   }

   @Override
   public Triple asTriple() {
      return statement.asTriple();
   }
}
