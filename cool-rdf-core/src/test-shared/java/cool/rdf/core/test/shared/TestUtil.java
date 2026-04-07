/*
 * Copyright (c) 2026 Bosch Software Innovations GmbH. All rights reserved.
 */

package cool.rdf.core.test.shared;

public class TestUtil {
   public static boolean systemUsesProxy() {
      return System.getenv( "http_proxy" ) != null
            || System.getenv( "HTTP_PROXY" ) != null
            || System.getProperty( "http.proxyHost" ) != null;
   }
}
