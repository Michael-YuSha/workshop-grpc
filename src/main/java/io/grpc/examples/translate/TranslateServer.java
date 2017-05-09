package io.grpc.examples.translate;

import java.io.IOException;
import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.examples.routeguide.RouteGuideServer;

public class TranslateServer {
	
	public static final int PORT = 9234;
	
	public static void main(final String args[]) throws Exception {
		final TranslateServer translateServer = new TranslateServer(PORT);
		translateServer.start();
		translateServer.blockUntilShutdown();
	}

	private static final Logger logger = Logger.getLogger(RouteGuideServer.class.getName());

	private final int port;
	private final Server server;

	public TranslateServer(final int port) {
		this.port = port;
		this.server = ServerBuilder.forPort(port).addService(new TranslateServiceImpl()).build();
	}

	/** Start serving requests. */
	public void start() throws IOException {
		server.start();
		logger.info("Server started, listening on " + port);
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
