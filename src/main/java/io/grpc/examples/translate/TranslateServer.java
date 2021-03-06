package io.grpc.examples.translate;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.examples.routeguide.RouteGuideServer;
import io.netty.handler.ssl.SslProvider;

public class TranslateServer {
	
	public static final String HOST = "localhost";
	public static final int PORT = 9234;
	public static final boolean SIMPLE_SERVER = true;
	public static final boolean SECURE_SERVER = false;
	
	public static void main(final String args[]) throws Exception {
		final TranslateServer translateServer = new TranslateServer(useSimpleServer());
		translateServer.start();
		translateServer.blockUntilShutdown();
	}

	public static boolean useSimpleServer() {
		//TOOD-10 return SECURE_SERVER; 
		return SIMPLE_SERVER;
	}
	
	private static final Logger logger = Logger.getLogger(RouteGuideServer.class.getName());

	private final Server server;

	public TranslateServer(final boolean simpleServer) throws CertificateException, IOException {
		if (simpleServer) {
			this.server = buildSimpleServer();
		} else {
			this.server = buildSecureServer();
		}
		
	}

	private Server buildSimpleServer() {
		//TODO-4 : return een server zonder extra security. hint ServerBuilder en de impl class in deze package.
		return null;
	}

	private Server buildSecureServer() throws IOException, CertificateException  {
		//TODO-10 : return een server met extra security. hint GrpcUtils 
		//met: chainFile:"/server1.pem",privateKey:"/server1.key",trustedCa:"/ca.pem", SslProvider.OPENSSL
		return null;
	}

	/** Start serving requests. */
	public void start() throws IOException {
		server.start();
		logger.info("Server started, listening on " + PORT);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// Use stderr here since the logger may has been reset by its
				// JVM shutdown hook.
				System.err.println("*** shutting down gRPC server since JVM is shutting down");
				TranslateServer.this.stop();
				System.err.println("*** server shut down");
			}
		});
	}

	/** Stop serving requests and shutdown resources. */
	public void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	/**
	 * Await termination on the main thread since the grpc library uses daemon
	 * threads.
	 */
	private void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}

}
