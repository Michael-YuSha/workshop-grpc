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
		return SECURE_SERVER;
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
		return ServerBuilder.forPort(PORT).addService(new TranslateServiceImpl()).build();
	}

	private Server buildSecureServer() throws IOException, CertificateException  {
		return GrpcUtils.serverBuilder(PORT, "/server1.pem", "/server1.key", "/ca.pem", SslProvider.OPENSSL)
				.addService(new TranslateServiceImpl())
				.build();
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
