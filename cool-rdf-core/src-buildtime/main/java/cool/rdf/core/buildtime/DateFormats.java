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

package cool.rdf.core.buildtime;

import java.time.format.DateTimeFormatter;

/**
 * Date format constants for build time code generation.
 */
public class DateFormats {
   public static final DateTimeFormatter ISO_8601_FORMAT = DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH:mm:ss.SSSX" );

   public static final DateTimeFormatter SIMPLE_DATE_FORMAT = DateTimeFormatter.ofPattern( "yyyy-MM-dd" );

   public static final DateTimeFormatter YEAR_FORMAT = DateTimeFormatter.ofPattern( "yyyy" );
}
