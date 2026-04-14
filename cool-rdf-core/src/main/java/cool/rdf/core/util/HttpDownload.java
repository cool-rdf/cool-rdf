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

package cool.rdf.core.util;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cool.rdf.core.exception.DownloadFailedException;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Convenience class that allows download a file via HTTP/HTTPS, while following redirects, and which honors the
 * system proxy configuration configuration (http_proxy/https_proxy environment variables).
 */
public class HttpDownload {
   private static final Logger LOG = LoggerFactory.getLogger( HttpDownload.class );
   private final Config config;

   @RecordBuilder
   public record Config(
         HttpClient.Version httpVersion,
         HttpClient.Redirect redirectPolicy,
         Duration timeout,
         Optional<Authenticator> authenticator
   ) {}


   public HttpDownload( final Config config ) {
      this.config = config;
   }

   public HttpDownload() {
      this( HttpDownloadConfigBuilder.builder()
            .httpVersion( HttpClient.Version.HTTP_1_1 )
            .redirectPolicy( HttpClient.Redirect.ALWAYS )
            .timeout( Duration.ofSeconds( 10 ) )
            .build() );
   }

   public enum ProxiedProtocol {
      HTTP, HTTPS
   }

   public static Optional<ProxySelector> detectProxySettings( final ProxiedProtocol protocol ) {
      final String environmentConfigVariable = protocol.toString().toLowerCase() + "_proxy";
      final String systemPropertyForProxyHost = protocol.toString().toLowerCase() + ".proxyHost";
      final String systemPropertyForProxyPort = protocol.toString().toLowerCase() + ".proxyPort";
      final String envProxy = System.getenv( environmentConfigVariable );
      if ( envProxy != null && System.getProperty( systemPropertyForProxyHost ) == null ) {
         final Pattern proxyPattern = Pattern.compile( "http://([^:]*):(\\d+)/?" );
         final Matcher matcher = proxyPattern.matcher( envProxy );
         if ( matcher.matches() ) {
            final String host = matcher.group( 1 );
            final String port = matcher.group( 2 );
            System.setProperty( systemPropertyForProxyHost, host );
            System.setProperty( systemPropertyForProxyPort, port );
         } else {
            LOG.debug( "The value of the '{}' environment variable is malformed, ignoring: {}", environmentConfigVariable, envProxy );
         }
      }

      final String host = System.getProperty( systemPropertyForProxyHost );
      final String port = System.getProperty( systemPropertyForProxyPort );
      if ( host != null && port != null ) {
         return Optional.of( ProxySelector.of( new InetSocketAddress( host, Integer.parseInt( port ) ) ) );
      } else if ( protocol == ProxiedProtocol.HTTPS ) {
         // Fallback for HTTPS to make use of the proxy configuration for http
         return detectProxySettings( ProxiedProtocol.HTTP );
      }
      return Optional.empty();
   }

   public <T> T retrieve( final URL inputUrl, final HttpResponse.BodyHandler<T> bodyHandler ) {
      LOG.debug( "Trying to download {}", inputUrl );
      final HttpClient.Builder builder = HttpClient.newBuilder()
            .version( config.httpVersion() )
            .followRedirects( config.redirectPolicy() )
            .connectTimeout( config.timeout() );
      if ( inputUrl.getProtocol().equalsIgnoreCase( "http" ) || inputUrl.getProtocol().equalsIgnoreCase( "https" ) ) {
         final ProxiedProtocol protocol = ProxiedProtocol.valueOf( inputUrl.getProtocol().toUpperCase() );
         detectProxySettings( protocol ).ifPresent( builder::proxy );
      }
      config.authenticator().ifPresent( builder::authenticator );

      try ( final HttpClient client = builder.build() ) {
         final HttpRequest request = HttpRequest.newBuilder()
               .uri( inputUrl.toURI() )
               .build();
         final HttpResponse<T> response = client.send( request, bodyHandler );
         if ( response.statusCode() == HttpURLConnection.HTTP_OK ) {
            return response.body();
         }
         throw new DownloadFailedException( "Could not retrieve " + inputUrl + ": Server returned " + response.statusCode() );
      } catch ( final InterruptedException | URISyntaxException | IOException exception ) {
         throw new DownloadFailedException( "Could not retrieve " + inputUrl, exception );
      }
   }
}
