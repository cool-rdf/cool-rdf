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

package cool.rdf.cli;

import java.util.stream.Stream;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

/**
 * Provides the names of all the .ttl resources for parameterized tests (without .ttl extension)
 */
public class ResourceArgumentsProvider implements ArgumentsProvider {
   @Override
   @NullMarked
   public Stream<? extends Arguments> provideArguments( final ParameterDeclarations parameterDeclarations,
         final ExtensionContext context ) {
      try ( final ScanResult scanResult = new ClassGraph().scan() ) {
         return scanResult.getResourcesWithExtension( ".ttl" ).getPaths().stream()
               .map( filename -> filename.replace( ".ttl", "" ) )
               .filter( filename -> !filename.contains( "/" ) )
               .sorted()
               .map( FilenameArguments::new );
      }
   }

   public static class FilenameArguments implements Arguments {
      final String filename;

      public FilenameArguments( final String filename ) {
         this.filename = filename;
      }

      @Override
      @NullMarked
      public Object[] get() {
         return new Object[] { filename };
      }
   }
}
