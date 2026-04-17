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

package cool.rdf.infer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.http.HttpResponse;

import org.apache.jena.ontapi.OntModelFactory;
import org.apache.jena.ontapi.OntSpecification;
import org.apache.jena.ontapi.model.OntModel;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cool.rdf.core.util.HttpDownload;
import cool.rdf.formatter.FormattingStyle;
import cool.rdf.formatter.TurtleFormatter;
import io.vavr.control.Try;

/**
 *
 */
public class Inferrer {
   /**
    * The default configuration
    */
   public static final Configuration DEFAULT_CONFIGURATION = Configuration.builder().build();

   private static final Logger LOG = LoggerFactory.getLogger( Inferrer.class );

   /**
    * Writes an inferred OWL document given by an input URL, to an output stream using a
    * writing/formatting
    * configuration
    *
    * @param inputUrl the input URL
    * @param output the output stream
    * @param configuration the configuration
    * @return {@link io.vavr.control.Try.Success} if writing succeeded
    */
   public Try<Void> infer( final URL inputUrl, final OutputStream output, final Configuration configuration ) {
      if ( inputUrl.getProtocol().equalsIgnoreCase( "http" ) || inputUrl.getProtocol().equalsIgnoreCase( "https" ) ) {
         try {
            final String document = new HttpDownload().retrieve( inputUrl, HttpResponse.BodyHandlers.ofString() );
            return infer( new ByteArrayInputStream( document.getBytes() ), output, configuration );
         } catch ( final Exception exception ) {
            LOG.debug( "Failure during reading from URL: {}", inputUrl );
            return Try.failure( exception );
         }
      }

      try {
         return infer( inputUrl.openStream(), output, configuration );
      } catch ( final IOException exception ) {
         return Try.failure( exception );
      }
   }

   /**
    * Writes an inferred OWL document given by an input stream, to an output stream using a
    * writing/formatting configuration
    *
    * @param input the input stream
    * @param output the output stream
    * @param configuration the configuration
    * @return {@link io.vavr.control.Try.Success} if writing succeeded
    */
   public Try<Void> infer( final InputStream input, final OutputStream output, final Configuration configuration ) {
      final OntModel base = OntModelFactory.createModel( OntSpecification.OWL2_DL_MEM );

      try {
         base.read( input, configuration.base(), configurationFormatToJenaFormat( configuration.inputFormat() ) );
         final OntModel inf = OntModelFactory.createModel( base.getGraph(), OntSpecification.OWL2_DL_MEM_RDFS_INF );
         return writeTurtle( inf, output );
      } catch ( final Exception exception ) {
         LOG.debug( "Failure during RDF I/O", exception );
         return Try.failure( exception );
      }
   }

   /**
    * Writes an RDF model to and output stream in RDF/Turtle format
    *
    * @param model the model
    * @param output the output stream
    * @return {@link io.vavr.control.Try.Success} if writing succeeded
    */
   public Try<Void> writeTurtle( final Model model, final OutputStream output ) {
      final TurtleFormatter formatter = new TurtleFormatter( FormattingStyle.DEFAULT );
      formatter.accept( model, output );
      return Try.success( null );
   }

   /**
    * Builds an RDF format string as expected by the lang parameter of
    * {@link Model#read(InputStream, String, String)}
    *
    * @param format the format
    * @return the format identifier for the Jena parser
    */
   private String configurationFormatToJenaFormat( final Configuration.Format format ) {
      return switch ( format ) {
         case TURTLE -> "TURTLE";
         case RDFXML -> "RDF/XML";
         case NTRIPLE -> "N-TRIPLE";
         case N3 -> "N3";
      };
   }
}
