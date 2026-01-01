/*
 * Copyright 2024 Andreas Textor
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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serial;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenient test runner to execute a command line interface either via main class, executable jar or native binary.
 * Arguments and stdin can be provided, return code, stderr and stdout are captured. This class uses Mockito to capture
 * calls to {@link Runtime#exit(int)}: It relies on the tested class having a non-final field "java.lang.Runtime
 * runtime" which is used to calling exit().
 */
public class CliRunner {
    private static final Logger LOG = LoggerFactory.getLogger( CliRunner.class );

    /**
     * Represents the content of a stream (stdin/stdout/stderr)
     *
     * @param raw the raw byte content
     */
    public record StreamContent( byte[] raw ) {
        /**
         * Returns the stream content as UTF-8 string
         *
         * @return the stream content as string
         */
        public String asString() {
            return new String( raw, StandardCharsets.UTF_8 );
        }

        /**
         * Returns the stream content as string with all non-printable non-ASCII characters removed
         *
         * @return the stream without non-printable non-ASCII characters
         */
        public String cleaned() {
            return asString().replaceAll( "\\P{Print}", "" );
        }
    }

    public record ExecArguments( List<String> arguments, StreamContent stdin, File workingDirectory ) {
        public ExecArguments( final List<String> arguments, final StreamContent stdin ) {
            this( arguments, stdin, new File( "." ).getAbsoluteFile() );
        }

        public ExecArguments( final List<String> arguments ) {
            this( arguments, new StreamContent( new byte[0] ) );
        }

        public ExecArguments( final String... arguments ) {
            this( List.of( arguments ) );
        }
    }

    public record Result( int exitStatus, StreamContent stdOut, StreamContent stdErr ) {
    }

    private static class SystemExitCapturedException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = 6080552325336609875L;

        private final int returnCode;

        private SystemExitCapturedException( final int returnCode ) {
            this.returnCode = returnCode;
        }
    }

    private record StreamCollector( InputStream in ) implements Callable<ByteArrayOutputStream> {
        @Override
        public ByteArrayOutputStream call() throws Exception {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            in.transferTo( buffer );
            return buffer;
        }
    }

    /**
     * Executes a command line tool via its main class. This also captures any System.exit() calls that might be done.
     *
     * @param clazz the class
     * @param execArguments the arguments
     * @return the execution result
     */
    public static Result runMainClass( final Class<?> clazz, final ExecArguments execArguments ) {
        final PrintStream originalStdout = System.out;
        final PrintStream originalStderr = System.err;
        final InputStream originalStdin = System.in;
        final String originalUserDir = System.getProperty( "user.dir" );

        try (
            final ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
            final ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();
            final PrintStream outStream = new PrintStream( stdoutBuffer );
            final PrintStream errStream = new PrintStream( stderrBuffer ) ) {
            System.setOut( outStream );
            System.setErr( errStream );

            if ( execArguments.stdin().raw().length > 0 ) {
                System.setIn( new ByteArrayInputStream( execArguments.stdin().raw() ) );
            }

            System.setProperty( "user.dir", execArguments.workingDirectory().getAbsolutePath() );
            final Method method = clazz.getMethod( "main", String[].class );

            // Mock runtime for the tested class, in order to capture
            // calls to System.exit()
            try {
                final Runtime spyRuntime = spy( Runtime.getRuntime() );
                final Field runtime = clazz.getDeclaredField( "runtime" );
                runtime.setAccessible( true );
                runtime.set( null, spyRuntime );
                doThrow( new SystemExitCapturedException( -1 ) ).when( spyRuntime ).exit( anyInt() );
                doThrow( new SystemExitCapturedException( 1 ) ).when( spyRuntime ).exit( 1 );
                doThrow( new SystemExitCapturedException( 0 ) ).when( spyRuntime ).exit( 0 );
            } catch ( final NoSuchFieldException exception ) {
                throw new RuntimeException( exception );
            }

            int returnCode = 0;
            try {
                method.invoke( null, (Object) execArguments.arguments().toArray( new String[0] ) );
            } catch ( final InvocationTargetException exception ) {
                // Ignore System.exit, throw everything else
                if ( exception.getCause() instanceof SystemExitCapturedException systemExitCapturedException ) {
                    returnCode = systemExitCapturedException.returnCode;
                } else {
                    throw new RuntimeException( exception );
                }
            }
            return new Result( returnCode,
                new StreamContent( stdoutBuffer.toByteArray() ),
                new StreamContent( stderrBuffer.toByteArray() ) );
        } catch ( final NoSuchMethodException | IllegalAccessException | IOException exception ) {
            throw new RuntimeException( exception );
        } finally {
            System.setOut( originalStdout );
            System.setErr( originalStderr );
            if ( execArguments.stdin().raw().length > 0 ) {
                System.setIn( originalStdin );
            }
            System.setProperty( "user.dir", originalUserDir );
        }
    }

    /**
     * Executes a command line interface via its executable jar
     *
     * @param jarFile the jar file
     * @param execArguments the arguments
     * @param jvmArguments additional arguments that should be passed to the Java call
     * @return the execution result
     */
    public static Result runJar( final File jarFile, final ExecArguments execArguments, final List<String> jvmArguments ) {
        final File javaBinary = new File( ProcessHandle.current().info().command().orElse( "java" ) );

        final List<String> javaExecArguments = Stream.of(
            jvmArguments.stream(),
            Stream.of( "-jar", jarFile.toString() ),
            execArguments.arguments().stream() ).flatMap( Function.identity() ).toList();

        return runBinary( javaBinary, new ExecArguments( javaExecArguments, execArguments.stdin(), execArguments.workingDirectory() ) );
    }

    /**
     * Executes a command line interface via its native executable
     *
     * @param binary the executable file
     * @param executionArgument the arguments
     * @return the execution result
     */
    public static Result runBinary( final File binary, final ExecArguments executionArgument ) {
        LOG.debug( "Executing command (in {}): \"{}\" {}",
            executionArgument.workingDirectory(), binary, executionArgument.arguments().stream()
                .map( argument -> '"' + argument + '"' )
                .collect( Collectors.joining( " " ) ) );
        try {
            final List<String> commandWithAllArguments = Stream.concat( Stream.of( binary.toString() ), executionArgument.arguments()
                .stream() ).toList();
            final Process process = Runtime.getRuntime()
                .exec( commandWithAllArguments.toArray( new String[0] ), null, executionArgument.workingDirectory() );
            if ( executionArgument.stdin().raw().length > 0 ) {
                process.getOutputStream().write( executionArgument.stdin().raw() );
            }
            process.getOutputStream().close();
            final ExecutorService executor = Executors.newFixedThreadPool( 2 );
            final Future<ByteArrayOutputStream> stdout = executor.submit( new StreamCollector( process.getInputStream() ) );
            final Future<ByteArrayOutputStream> stderr = executor.submit( new StreamCollector( process.getErrorStream() ) );
            process.waitFor();

            return new Result( process.exitValue(), new StreamContent( stdout.get().toByteArray() ),
                new StreamContent( stderr.get().toByteArray() ) );
        } catch ( final IOException | InterruptedException | ExecutionException exception ) {
            throw new RuntimeException( exception );
        }
    }
}
