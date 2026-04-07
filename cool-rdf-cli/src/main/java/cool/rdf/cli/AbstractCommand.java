/*
 * Copyright Andreas Textor
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

package cool.rdf.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;

import io.vavr.control.Try;
import picocli.CommandLine;

/**
 * Base class for commands that bundles common functionality
 */
public abstract class AbstractCommand {
   protected final Runtime runtime;

   protected AbstractCommand( final Runtime runtime ) {
      this.runtime = runtime;
   }

   /**
    * Will exit the program with status code 1
    */
   protected void commandFailed() {
      runtime.exit( 1 );
   }

   /**
    * Try to open an output stream
    *
    * @param input the original input: file path or "-" for stdin
    * @param output the designated output: an absolute or relative path or "-" for stdout. If empty,
    *        determine the
    *        output from the input
    * @param targetFileExtension the target file extension to use, if the output is empty
    * @return if successful, the output stream
    */
   protected Try<OutputStream> openOutput( final @Nonnull String input, final Optional<String> output, final String targetFileExtension ) {
      if ( output.isPresent() ) {
         // Output is given as - --> write to stdout
         if ( output.get().equals( "-" ) ) {
            return Try.success( System.out );
         }

         // Output is given as something else --> open as file
         try {
            return Try.success( new FileOutputStream( output.get() ) );
         } catch ( final FileNotFoundException exception ) {
            return Try.failure( exception );
         }
      }

      if ( input.equals( "-" ) ) {
         // Input is stdin, output is not given -> write to stdout
         return Try.success( System.out );
      }

      // Input is something else, output is not given -> interpret input as filename,
      // change input's file extension to target format and use as output file name
      final String outputFilename = input.replaceFirst( "[.][^.]+$",
            "." + targetFileExtension.toLowerCase() );
      if ( outputFilename.equals( input ) ) {
         return Try.failure( new ErrorMessage( "Can't determine an output filename" ) );
      }

      try {
         return Try.success( new FileOutputStream( outputFilename ) );
      } catch ( final FileNotFoundException exception ) {
         return Try.failure( exception );
      }
   }

   /**
    * Try to open a given input as input stream, either an absolute or relative file system path or "-"
    * meaning stdin
    *
    * @param input the input
    * @return an input stream on success
    */
   protected Try<InputStream> openInput( final String input ) {
      if ( input.equals( "-" ) ) {
         return Try.success( System.in );
      }
      try {
         final File inputFile = new File( input );
         if ( !inputFile.exists() ) {
            return Try.failure( new FileNotFoundException( input ) );
         }
         return Try.success( new FileInputStream( inputFile ) );
      } catch ( final FileNotFoundException exception ) {
         return Try.failure( exception );
      }
   }

   /**
    * Loads an ontology from an input stream
    *
    * @param inputStream the input stream
    * @return {@link Try.Success} with the {@link OWLOntology} on success, and a {@link Try.Failure}
    *         with the
    *         {@link OWLOntologyCreationException} otherwise.
    */
   public Try<OWLOntology> loadOntology( final InputStream inputStream ) {
      final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
      final OWLOntology ontology;
      try {
         ontology = manager.loadOntologyFromOntologyDocument( inputStream );
         return Try.success( ontology );
      } catch ( final OWLOntologyCreationException exception ) {
         return Try.failure( exception );
      }
   }

   /**
    * Bail out after a command failed with a throwable
    *
    * @param logger the logger to print a message
    * @param loggingMixin the logging mixin of the respective command, to determine verbosity
    * @param throwable the cause
    */
   protected void exitWithErrorMessage( final Logger logger, final LoggingMixin loggingMixin, final Throwable throwable ) {
      if ( loggingMixin.getVerbosity().length == 0 ) {
         System.err.println( "Error: " + throwable.getMessage() );
      } else if ( loggingMixin.getVerbosity().length == 1 ) {
         logger.warn( "Error: " + throwable.getMessage() );
      } else {
         throwable.printStackTrace();
      }
      commandFailed();
   }

   /**
    * Provide a hook for subclasses to register custom {@link CommandLine.ITypeConverter}s
    *
    * @param commandLine the command line
    */
   public void registerTypeConverters( final CommandLine commandLine ) {
      // empty by default
   }

   /**
    * Return the name of this command
    *
    * @return the command name
    */
   public abstract String commandName();
}
