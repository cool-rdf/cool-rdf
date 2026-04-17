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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Alt;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Bag;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelChangedListener;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.NsIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.RDFReaderI;
import org.apache.jena.rdf.model.RDFWriterI;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Seq;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StatementTerm;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shared.Lock;
import org.apache.jena.shared.PrefixMapping;

import cool.rdf.core.model.RdfModel;
import cool.rdf.core.model.RdfProperty;
import cool.rdf.core.model.RdfResource;
import cool.rdf.core.model.RdfStatement;

public class DefaultRdfModel implements RdfModel {
   private final Model model;

   public DefaultRdfModel( final Model model ) {
      this.model = model;
   }

   private RdfResource mapResource( final Resource resource ) {
      return new DefaultRdfResource( resource );
   }

   private RdfProperty mapProperty( final Property property ) {
      return new DefaultRdfProperty( property );
   }

   private RDFNode mapResourceRdfNode( final RDFNode node ) {
      return node.isResource() ? mapResource( node.asResource() ) : node;
   }

   private RdfStatement mapResourcesInStatement( final Statement statement ) {
      return new DefaultRdfStatement( createStatement( mapResource( statement.getSubject() ), mapProperty( statement.getPredicate() ),
            mapResourceRdfNode( statement.getObject() ) ) );
   }

   @Override
   public Stream<RdfResource> subjects() {
      return stream( listSubjects() ).map( this::mapResource );
   }

   @Override
   public Stream<String> namespaces() {
      return stream( listNameSpaces() );
   }

   @Override
   public Stream<RdfResource> resourcesWithProperty( final Property property ) {
      return stream( listResourcesWithProperty( property ) ).map( this::mapResource );
   }

   @Override
   public Stream<RdfResource> resourcesWithProperty( final Property property, final RDFNode object ) {
      return stream( listResourcesWithProperty( property, object ) ).map( this::mapResource );
   }

   @Override
   public Stream<RDFNode> objects() {
      return stream( listObjects() ).map( this::mapResourceRdfNode );
   }

   @Override
   public Stream<RDFNode> objectsOfProperty( final Property property ) {
      return stream( listObjectsOfProperty( property ) ).map( this::mapResourceRdfNode );
   }

   @Override
   public Stream<RDFNode> objectsOfProperty( final Resource subject, final Property property ) {
      return stream( listObjectsOfProperty( subject, property ) ).map( this::mapResourceRdfNode );
   }

   @Override
   public Stream<RdfStatement> statements() {
      return stream( listStatements() ).map( this::mapResourcesInStatement );
   }

   @Override
   public Stream<RdfStatement> statements( final Resource subject, final Property predicate, final RDFNode object ) {
      return stream( listStatements( subject, predicate, object ) ).map( this::mapResourcesInStatement );
   }

   @Override
   public Stream<RdfStatement> literalStatements( final Resource subject, final Property predicate, final boolean object ) {
      return stream( listLiteralStatements( subject, predicate, object ) ).map( this::mapResourcesInStatement );
   }

   @Override
   public Stream<RdfStatement> literalStatements( final Resource subject, final Property predicate, final char object ) {
      return stream( listLiteralStatements( subject, predicate, object ) ).map( this::mapResourcesInStatement );
   }

   @Override
   public Stream<RdfStatement> literalStatements( final Resource subject, final Property predicate, final long object ) {
      return stream( listLiteralStatements( subject, predicate, object ) ).map( this::mapResourcesInStatement );
   }

   @Override
   public Stream<RdfStatement> literalStatements( final Resource subject, final Property predicate, final int object ) {
      return stream( listLiteralStatements( subject, predicate, object ) ).map( this::mapResourcesInStatement );
   }

   @Override
   public Stream<RdfStatement> literalStatements( final Resource subject, final Property predicate, final float object ) {
      return stream( listLiteralStatements( subject, predicate, object ) ).map( this::mapResourcesInStatement );
   }

   @Override
   public Stream<RdfStatement> literalStatements( final Resource subject, final Property predicate, final double object ) {
      return stream( listLiteralStatements( subject, predicate, object ) ).map( this::mapResourcesInStatement );
   }

   @Override
   public Stream<RdfStatement> literalStatements( final Resource subject, final Property predicate, final String object ) {
      return stream( listStatements( subject, predicate, object ) ).map( this::mapResourcesInStatement );
   }

   @Override
   public Stream<RdfStatement> literalStatements( final Resource subject, final Property predicate, final String object,
         final String language ) {
      return stream( listStatements( subject, predicate, object, language ) ).map( this::mapResourcesInStatement );
   }

   @Override
   public Stream<RdfResource> resourcesWithProperty( final Property p, final boolean o ) {
      return stream( listResourcesWithProperty( p, o ) ).map( this::mapResource );
   }

   @Override
   public Stream<RdfResource> resourcesWithProperty( final Property p, final long o ) {
      return stream( listResourcesWithProperty( p, o ) ).map( this::mapResource );
   }

   @Override
   public Stream<RdfResource> resourcesWithProperty( final Property p, final char o ) {
      return stream( listResourcesWithProperty( p, o ) ).map( this::mapResource );
   }

   @Override
   public Stream<RdfResource> resourcesWithProperty( final Property p, final float o ) {
      return stream( listResourcesWithProperty( p, o ) ).map( this::mapResource );
   }

   @Override
   public Stream<RdfResource> resourcesWithProperty( final Property p, final double o ) {
      return stream( listResourcesWithProperty( p, o ) ).map( this::mapResource );
   }

   @Override
   public Stream<RdfResource> resourcesWithProperty( final Property p, final Object o ) {
      return stream( listResourcesWithProperty( p, o ) ).map( this::mapResource );
   }

   @Override
   public Stream<RdfResource> subjectsWithProperty( final Property p, final String o ) {
      return stream( listSubjectsWithProperty( p, o ) ).map( this::mapResource );
   }

   @Override
   public Stream<RdfResource> resourcesWithProperty( final Property p, final String o, final String l ) {
      return stream( listSubjectsWithProperty( p, o, l ) ).map( this::mapResource );
   }

   @Override
   public long size() {
      return model.size();
   }

   @Override
   public boolean isEmpty() {
      return model.isEmpty();
   }

   @Override
   public ResIterator listSubjects() {
      return model.listSubjects();
   }

   @Override
   public NsIterator listNameSpaces() {
      return model.listNameSpaces();
   }

   @Override
   public Resource getResource( final String uri ) {
      return model.getResource( uri );
   }

   @Override
   public Resource getResource( final AnonId id ) {
      return model.getResource( id );
   }

   @Override
   public Property getProperty( final String nameSpace, final String localName ) {
      return model.getProperty( nameSpace, localName );
   }

   @Override
   public Resource createResource() {
      return model.createResource();
   }

   @Override
   public Resource createResource( final AnonId id ) {
      return model.createResource( id );
   }

   @Override
   public Resource createResource( final String uri ) {
      return model.createResource( uri );
   }

   @Override
   public StatementTerm createStatementTerm( final Statement statement ) {
      return model.createStatementTerm( statement );
   }

   @Override
   public Resource createReifier( final Statement statement ) {
      return model.createReifier( statement );
   }

   @Override
   public Resource createReifier( final Resource reifier, final Statement statement ) {
      return model.createReifier( reifier, statement );
   }

   @Override
   public Property createProperty( final String nameSpace, final String localName ) {
      return model.createProperty( nameSpace, localName );
   }

   @Override
   public Literal createLiteral( final String v, final String language ) {
      return model.createLiteral( v, language );
   }

   @Override
   public Literal createLiteral( final String v, final String language, final String baseDirection ) {
      return model.createLiteral( v, language, baseDirection );
   }

   @Override
   public Literal createTypedLiteral( final String lex, final RDFDatatype dtype ) {
      return model.createTypedLiteral( lex, dtype );
   }

   @Override
   public Literal createTypedLiteral( final Object value, final RDFDatatype dtype ) {
      return model.createTypedLiteral( value, dtype );
   }

   @Override
   public Literal createTypedLiteral( final Object value ) {
      return model.createTypedLiteral( value );
   }

   @Override
   public Statement createStatement( final Resource s, final Property p, final RDFNode o ) {
      return model.createStatement( s, p, o );
   }

   @Override
   public RDFList createList() {
      return model.createList();
   }

   @Override
   public RDFList createList( final Iterator<? extends RDFNode> members ) {
      return model.createList( members );
   }

   @Override
   public RDFList createList( final RDFNode... members ) {
      return model.createList( members );
   }

   @Override
   public Model add( final Statement s ) {
      return model.add( s );
   }

   @Override
   public Model add( final Statement[] statements ) {
      return model.add( statements );
   }

   @Override
   public Model remove( final Statement[] statements ) {
      return model.remove( statements );
   }

   @Override
   public Model add( final List<Statement> statements ) {
      return model.add( statements );
   }

   @Override
   public Model remove( final List<Statement> statements ) {
      return model.remove( statements );
   }

   @Override
   public Model add( final StmtIterator iter ) {
      return model.add( iter );
   }

   @Override
   public Model add( final Model m ) {
      return model.add( m );
   }

   @Override
   public Model read( final String url ) {
      return model.read( url );
   }

   @Override
   public Model read( final InputStream in, final String base ) {
      return model.read( in, base );
   }

   @Override
   public Model read( final InputStream in, final String base, final String lang ) {
      return model.read( in, base, lang );
   }

   @Override
   public Model read( final Reader reader, final String base ) {
      return model.read( reader, base );
   }

   @Override
   public Model read( final String url, final String lang ) {
      return model.read( url, lang );
   }

   @Override
   public Model read( final Reader reader, final String base, final String lang ) {
      return model.read( reader, base, lang );
   }

   @Override
   public Model read( final String url, final String base, final String lang ) {
      return model.read( url, base, lang );
   }

   @Override
   public Model write( final Writer writer ) {
      return model.write( writer, "turtle" );
   }

   @Override
   public Model write( final Writer writer, final String lang ) {
      return model.write( writer, lang );
   }

   @Override
   public Model write( final Writer writer, final String lang, final String base ) {
      return model.write( writer, lang, base );
   }

   @Override
   public Model write( final OutputStream out ) {
      return model.write( out );
   }

   @Override
   public Model write( final OutputStream out, final String lang ) {
      return model.write( out, lang );
   }

   @Override
   public Model write( final OutputStream out, final String lang, final String base ) {
      return model.write( out, lang, base );
   }

   @Override
   public RDFReaderI getReader( final String lang ) {
      return model.getReader( lang );
   }

   @Override
   public RDFWriterI getWriter( final String lang ) {
      return model.getWriter( lang );
   }

   @Override
   public Model remove( final Statement s ) {
      return model.remove( s );
   }

   @Override
   public Statement getRequiredProperty( final Resource s, final Property p ) {
      return model.getRequiredProperty( s, p );
   }

   @Override
   public Statement getRequiredProperty( final Resource s, final Property p, final String lang ) {
      return model.getRequiredProperty( s, p, lang );
   }

   @Override
   public Statement getProperty( final Resource s, final Property p ) {
      return model.getProperty( s, p );
   }

   @Override
   public Statement getProperty( final Resource s, final Property p, final String lang ) {
      return model.getProperty( s, p, lang );
   }

   @Override
   public ResIterator listSubjectsWithProperty( final Property p ) {
      return model.listSubjectsWithProperty( p );
   }

   @Override
   public ResIterator listResourcesWithProperty( final Property p ) {
      return model.listResourcesWithProperty( p );
   }

   @Override
   public ResIterator listSubjectsWithProperty( final Property p, final RDFNode o ) {
      return model.listSubjectsWithProperty( p, o );
   }

   @Override
   public ResIterator listResourcesWithProperty( final Property p, final RDFNode o ) {
      return model.listResourcesWithProperty( p, o );
   }

   @Override
   public NodeIterator listObjects() {
      return model.listObjects();
   }

   @Override
   public NodeIterator listObjectsOfProperty( final Property p ) {
      return model.listObjectsOfProperty( p );
   }

   @Override
   public NodeIterator listObjectsOfProperty( final Resource s, final Property p ) {
      return model.listObjectsOfProperty( s, p );
   }

   @Override
   public boolean contains( final Resource s, final Property p ) {
      return model.contains( s, p );
   }

   @Override
   public boolean containsResource( final RDFNode r ) {
      return model.containsResource( r );
   }

   @Override
   public boolean contains( final Resource s, final Property p, final RDFNode o ) {
      return model.contains( s, p, o );
   }

   @Override
   public boolean contains( final Statement s ) {
      return model.contains( s );
   }

   @Override
   public boolean containsAny( final StmtIterator iter ) {
      return model.containsAny( iter );
   }

   @Override
   public boolean containsAll( final StmtIterator iter ) {
      return model.containsAll( iter );
   }

   @Override
   public boolean containsAny( final Model model ) {
      return this.model.containsAny( model );
   }

   @Override
   public boolean containsAll( final Model model ) {
      return this.model.containsAll( model );
   }

   @Override
   public StmtIterator listStatements() {
      return model.listStatements();
   }

   @Override
   public StmtIterator listStatements( final Resource s, final Property p, final RDFNode o ) {
      return model.listStatements( s, p, o );
   }

   @Override
   public Model union( final Model model ) {
      return this.model.union( model );
   }

   @Override
   public Model intersection( final Model model ) {
      return this.model.intersection( model );
   }

   @Override
   public Model difference( final Model model ) {
      return this.model.difference( model );
   }

   @Override
   public Model begin() {
      return model.begin();
   }

   @Override
   public Model abort() {
      return model.abort();
   }

   @Override
   public Model commit() {
      return model.commit();
   }

   @Override
   public void executeInTxn( final Runnable action ) {
      model.executeInTxn( action );
   }

   @Override
   public <T> T calculateInTxn( final Supplier<T> action ) {
      return model.calculateInTxn( action );
   }

   @Override
   public boolean independent() {
      return model.independent();
   }

   @Override
   public boolean supportsTransactions() {
      return model.supportsTransactions();
   }

   @Override
   public boolean supportsSetOperations() {
      return model.supportsSetOperations();
   }

   @Override
   public boolean isIsomorphicWith( final Model g ) {
      return model.isIsomorphicWith( g );
   }

   @Override
   public void close() {
      model.close();
   }

   @Override
   public Lock getLock() {
      return model.getLock();
   }

   @Override
   public Model register( final ModelChangedListener listener ) {
      return model.register( listener );
   }

   @Override
   public Model unregister( final ModelChangedListener listener ) {
      return model.unregister( listener );
   }

   @Override
   public Model notifyEvent( final Object e ) {
      return model.notifyEvent( e );
   }

   @Override
   public Model removeAll() {
      return model.removeAll();
   }

   @Override
   public Model removeAll( final Resource s, final Property p, final RDFNode r ) {
      return model.removeAll( s, p, r );
   }

   @Override
   public boolean isClosed() {
      return model.isClosed();
   }

   @Override
   public Model setNsPrefix( final String prefix, final String uri ) {
      return model.setNsPrefix( prefix, uri );
   }

   @Override
   public Model removeNsPrefix( final String prefix ) {
      return model.removeNsPrefix( prefix );
   }

   @Override
   public Model clearNsPrefixMap() {
      return model.clearNsPrefixMap();
   }

   @Override
   public Model setNsPrefixes( final PrefixMapping other ) {
      return model.setNsPrefixes( other );
   }

   @Override
   public Model setNsPrefixes( final Map<String, String> map ) {
      return model.setNsPrefixes( map );
   }

   @Override
   public Model withDefaultMappings( final PrefixMapping map ) {
      return model.withDefaultMappings( map );
   }

   @Override
   public Property getProperty( final String uri ) {
      return model.getProperty( uri );
   }

   @Override
   public Bag getBag( final String uri ) {
      return model.getBag( uri );
   }

   @Override
   public Bag getBag( final Resource r ) {
      return model.getBag( r );
   }

   @Override
   public Alt getAlt( final String uri ) {
      return model.getAlt( uri );
   }

   @Override
   public Alt getAlt( final Resource r ) {
      return model.getAlt( r );
   }

   @Override
   public Seq getSeq( final String uri ) {
      return model.getSeq( uri );
   }

   @Override
   public Seq getSeq( final Resource r ) {
      return model.getSeq( r );
   }

   @Override
   public RDFList getList( final String uri ) {
      return model.getList( uri );
   }

   @Override
   public RDFList getList( final Resource r ) {
      return model.getList( r );
   }

   @Override
   public Resource createResource( final Resource type ) {
      return model.createResource( type );
   }

   @Override
   public RDFNode getRDFNode( final Node n ) {
      return model.getRDFNode( n );
   }

   @Override
   public Resource createResource( final String uri, final Resource type ) {
      return model.createResource( uri, type );
   }

   @Override
   public Property createProperty( final String uri ) {
      return model.createProperty( uri );
   }

   @Override
   public Literal createLiteral( final String v ) {
      return model.createLiteral( v );
   }

   @Override
   public Literal createTypedLiteral( final boolean v ) {
      return model.createTypedLiteral( v );
   }

   @Override
   public Literal createTypedLiteral( final int v ) {
      return model.createTypedLiteral( v );
   }

   @Override
   public Literal createTypedLiteral( final long v ) {
      return model.createTypedLiteral( v );
   }

   @Override
   public Literal createTypedLiteral( final Calendar d ) {
      return model.createTypedLiteral( d );
   }

   @Override
   public Literal createTypedLiteral( final char v ) {
      return model.createTypedLiteral( v );
   }

   @Override
   public Literal createTypedLiteral( final float v ) {
      return model.createTypedLiteral( v );
   }

   @Override
   public Literal createTypedLiteral( final double v ) {
      return model.createTypedLiteral( v );
   }

   @Override
   public Literal createTypedLiteral( final String v ) {
      return model.createTypedLiteral( v );
   }

   @Override
   public Literal createTypedLiteral( final String lex, final String typeURI ) {
      return model.createTypedLiteral( lex, typeURI );
   }

   @Override
   public Literal createTypedLiteral( final Object value, final String typeURI ) {
      return model.createTypedLiteral( value, typeURI );
   }

   @Override
   public Statement createLiteralStatement( final Resource s, final Property p, final boolean o ) {
      return model.createLiteralStatement( s, p, o );
   }

   @Override
   public Statement createLiteralStatement( final Resource s, final Property p, final float o ) {
      return model.createLiteralStatement( s, p, o );
   }

   @Override
   public Statement createLiteralStatement( final Resource s, final Property p, final double o ) {
      return model.createLiteralStatement( s, p, o );
   }

   @Override
   public Statement createLiteralStatement( final Resource s, final Property p, final long o ) {
      return model.createLiteralStatement( s, p, o );
   }

   @Override
   public Statement createLiteralStatement( final Resource s, final Property p, final int o ) {
      return model.createLiteralStatement( s, p, o );
   }

   @Override
   public Statement createLiteralStatement( final Resource s, final Property p, final char o ) {
      return model.createLiteralStatement( s, p, o );
   }

   @Override
   public Statement createLiteralStatement( final Resource s, final Property p, final Object o ) {
      return model.createLiteralStatement( s, p, o );
   }

   @Override
   public Statement createStatement( final Resource s, final Property p, final String o ) {
      return model.createStatement( s, p, o );
   }

   @Override
   public Statement createStatement( final Resource s, final Property p, final String o, final String l ) {
      return model.createStatement( s, p, o, l );
   }

   @Override
   public Bag createBag() {
      return model.createBag();
   }

   @Override
   public Bag createBag( final String uri ) {
      return model.createBag( uri );
   }

   @Override
   public Alt createAlt() {
      return model.createAlt();
   }

   @Override
   public Alt createAlt( final String uri ) {
      return model.createAlt( uri );
   }

   @Override
   public Seq createSeq() {
      return model.createSeq();
   }

   @Override
   public Seq createSeq( final String uri ) {
      return model.createSeq( uri );
   }

   @Override
   public Model add( final Resource s, final Property p, final RDFNode o ) {
      return model.add( s, p, o );
   }

   @Override
   public Model addLiteral( final Resource s, final Property p, final boolean o ) {
      return model.addLiteral( s, p, o );
   }

   @Override
   public Model addLiteral( final Resource s, final Property p, final long o ) {
      return model.addLiteral( s, p, o );
   }

   @Override
   public Model addLiteral( final Resource s, final Property p, final int o ) {
      return model.addLiteral( s, p, o );
   }

   @Override
   public Model addLiteral( final Resource s, final Property p, final char o ) {
      return model.addLiteral( s, p, o );
   }

   @Override
   public Model addLiteral( final Resource s, final Property p, final float o ) {
      return model.addLiteral( s, p, o );
   }

   @Override
   public Model addLiteral( final Resource s, final Property p, final double o ) {
      return model.addLiteral( s, p, o );
   }

   @Override
   public Model addLiteral( final Resource s, final Property p, final Literal o ) {
      return model.addLiteral( s, p, o );
   }

   @Override
   public Model add( final Resource s, final Property p, final String o ) {
      return model.add( s, p, o );
   }

   @Override
   public Model add( final Resource s, final Property p, final String lex, final RDFDatatype datatype ) {
      return model.add( s, p, lex, datatype );
   }

   @Override
   public Model add( final Resource s, final Property p, final String lex, final String lang ) {
      return model.add( s, p, lex, lang );
   }

   @Override
   public Model add( final Resource s, final Property p, final String lex, final String lang, final String direction ) {
      return model.add( s, p, lex, lang, direction );
   }

   @Override
   public Model remove( final Resource s, final Property p, final RDFNode o ) {
      return model.remove( s, p, o );
   }

   @Override
   public Model remove( final StmtIterator iter ) {
      return model.remove( iter );
   }

   @Override
   public Model remove( final Model m ) {
      return model.remove( m );
   }

   @Override
   public StmtIterator listLiteralStatements( final Resource subject, final Property predicate, final boolean object ) {
      return model.listLiteralStatements( subject, predicate, object );
   }

   @Override
   public StmtIterator listLiteralStatements( final Resource subject, final Property predicate, final char object ) {
      return model.listLiteralStatements( subject, predicate, object );
   }

   @Override
   public StmtIterator listLiteralStatements( final Resource subject, final Property predicate, final long object ) {
      return model.listLiteralStatements( subject, predicate, object );
   }

   @Override
   public StmtIterator listLiteralStatements( final Resource subject, final Property predicate, final int object ) {
      return model.listLiteralStatements( subject, predicate, object );
   }

   @Override
   public StmtIterator listLiteralStatements( final Resource subject, final Property predicate, final float object ) {
      return model.listLiteralStatements( subject, predicate, object );
   }

   @Override
   public StmtIterator listLiteralStatements( final Resource subject, final Property predicate, final double object ) {
      return model.listLiteralStatements( subject, predicate, object );
   }

   @Override
   public StmtIterator listStatements( final Resource subject, final Property predicate, final String object ) {
      return model.listStatements( subject, predicate, object );
   }

   @Override
   public StmtIterator listStatements( final Resource subject, final Property predicate, final String object, final String lang ) {
      return model.listStatements( subject, predicate, object, lang );
   }

   @Override
   public StmtIterator listStatements( final Resource subject, final Property predicate, final String object, final String lang,
         final String direction ) {
      return model.listStatements( subject, predicate, object, lang, direction );
   }

   @Override
   public ResIterator listResourcesWithProperty( final Property p, final boolean o ) {
      return model.listResourcesWithProperty( p, o );
   }

   @Override
   public ResIterator listResourcesWithProperty( final Property p, final long o ) {
      return model.listResourcesWithProperty( p, o );
   }

   @Override
   public ResIterator listResourcesWithProperty( final Property p, final char o ) {
      return model.listResourcesWithProperty( p, o );
   }

   @Override
   public ResIterator listResourcesWithProperty( final Property p, final float o ) {
      return model.listResourcesWithProperty( p, o );
   }

   @Override
   public ResIterator listResourcesWithProperty( final Property p, final double o ) {
      return model.listResourcesWithProperty( p, o );
   }

   @Override
   public ResIterator listResourcesWithProperty( final Property p, final Object o ) {
      return model.listResourcesWithProperty( p, o );
   }

   @Override
   public ResIterator listSubjectsWithProperty( final Property p, final String str ) {
      return model.listSubjectsWithProperty( p, str );
   }

   @Override
   public ResIterator listSubjectsWithProperty( final Property p, final String str, final String lang ) {
      return model.listSubjectsWithProperty( p, str, lang );
   }

   @Override
   public ResIterator listSubjectsWithProperty( final Property p, final String str, final String lang, final String dir ) {
      return model.listSubjectsWithProperty( p, str, lang, dir );
   }

   @Override
   public boolean containsLiteral( final Resource s, final Property p, final boolean o ) {
      return model.containsLiteral( s, p, o );
   }

   @Override
   public boolean containsLiteral( final Resource s, final Property p, final long o ) {
      return model.containsLiteral( s, p, o );
   }

   @Override
   public boolean containsLiteral( final Resource s, final Property p, final int o ) {
      return model.containsLiteral( s, p, o );
   }

   @Override
   public boolean containsLiteral( final Resource s, final Property p, final char o ) {
      return model.containsLiteral( s, p, o );
   }

   @Override
   public boolean containsLiteral( final Resource s, final Property p, final float o ) {
      return model.containsLiteral( s, p, o );
   }

   @Override
   public boolean containsLiteral( final Resource s, final Property p, final double o ) {
      return model.containsLiteral( s, p, o );
   }

   @Override
   public boolean containsLiteral( final Resource s, final Property p, final Object o ) {
      return model.containsLiteral( s, p, o );
   }

   @Override
   public boolean contains( final Resource s, final Property p, final String lex ) {
      return model.contains( s, p, lex );
   }

   @Override
   public boolean contains( final Resource s, final Property p, final String lex, final String lang ) {
      return model.contains( s, p, lex, lang );
   }

   @Override
   public boolean contains( final Resource s, final Property p, final String lex, final String lang, final String dir ) {
      return model.contains( s, p, lex, lang, dir );
   }

   @Override
   public Statement asStatement( final Triple t ) {
      return model.asStatement( t );
   }

   @Override
   public Graph getGraph() {
      return model.getGraph();
   }

   @Override
   public RDFNode asRDFNode( final Node n ) {
      return model.asRDFNode( n );
   }

   @Override
   public Resource wrapAsResource( final Node n ) {
      return model.wrapAsResource( n );
   }

   @Override
   public String getNsPrefixURI( final String prefix ) {
      return model.getNsPrefixURI( prefix );
   }

   @Override
   public String getNsURIPrefix( final String uri ) {
      return model.getNsURIPrefix( uri );
   }

   @Override
   public Map<String, String> getNsPrefixMap() {
      return model.getNsPrefixMap();
   }

   @Override
   public String expandPrefix( final String prefixed ) {
      return model.expandPrefix( prefixed );
   }

   @Override
   public String shortForm( final String uri ) {
      return model.shortForm( uri );
   }

   @Override
   public String qnameFor( final String uri ) {
      return model.qnameFor( uri );
   }

   @Override
   public PrefixMapping lock() {
      return model.lock();
   }

   @Override
   public boolean hasNoMappings() {
      return model.hasNoMappings();
   }

   @Override
   public int numPrefixes() {
      return model.numPrefixes();
   }

   @Override
   public boolean samePrefixMappingAs( final PrefixMapping other ) {
      return model.samePrefixMappingAs( other );
   }

   @Override
   public void enterCriticalSection( final boolean readLockRequested ) {
      model.enterCriticalSection( readLockRequested );
   }

   @Override
   public void leaveCriticalSection() {
      model.leaveCriticalSection();
   }
}
