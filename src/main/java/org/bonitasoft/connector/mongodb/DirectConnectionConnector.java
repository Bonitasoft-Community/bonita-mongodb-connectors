package org.bonitasoft.connector.mongodb;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.engine.connector.AbstractConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class DirectConnectionConnector extends AbstractConnector {

	private static final Logger LOGGER = Logger.getLogger(DirectConnectionConnector.class.getName());
	
	// Connector input parameters
	protected final static String HOST_INPUT_PARAMETER = "host";
	protected final static String PORT_INPUT_PARAMETER = "port";
	protected final static String DATABASE_INPUT_PARAMETER = "database";
	protected final static String USERNAME_INPUT_PARAMETER = "username";
	protected final static String PASSWORD_INPUT_PARAMETER = "password";
	protected final static String COMMAND_INPUT_PARAMETER = "command";
	// Connector output parameters
	protected final String COMMANDRESULT_OUTPUT_PARAMETER = "commandResult";
	
	// Default parameter values
	private final static String DEFAULT_HOST	= "localhost";
	private final static int DEFAULT_PORT		= 27017;

	// Connector attributes
	private MongoClient mongoClient;
	private DB db;
	
	
	@Override
	public void validateInputParameters() throws ConnectorValidationException {
		try {
			getHost();
		} catch (ClassCastException cce) {
			throw new ConnectorValidationException("host type is invalid");
		}
		try {
			getPort();
		} catch (ClassCastException cce) {
			throw new ConnectorValidationException("port type is invalid");
		}
		try {
			getDatabase();
		} catch (ClassCastException cce) {
			throw new ConnectorValidationException("database type is invalid");
		}
		try {
			getUsername();
		} catch (ClassCastException cce) {
			throw new ConnectorValidationException("username type is invalid");
		}
		try {
			getPassword();
		} catch (ClassCastException cce) {
			throw new ConnectorValidationException("password type is invalid");
		}
		try {
			getCommand();
		} catch (ClassCastException cce) {
			throw new ConnectorValidationException("command type is invalid");
		}
		
		// Check for password if user name is specified
		if (getUsername() != null && !getUsername().trim().isEmpty()
				&& (getPassword() == null || getPassword().trim().isEmpty()))
			throw new ConnectorValidationException("Cannot have an empty password if user name is specified.");
	}
	
	@Override
	public void connect() throws ConnectorException {
		// Setup server address
		String host = getHost();
		if (host == null || host.trim().isEmpty())
			host = DEFAULT_HOST;
		Integer port = getPort();
		if (port == null || port == 0)
			port = DEFAULT_PORT;
		final ServerAddress serverAddress;
		try {
			serverAddress = new ServerAddress(host, port);
		} catch (UnknownHostException e) {
			throw new ConnectorException(e);
		}

		// Dump connection settings
		if (LOGGER.isLoggable(Level.FINE))
			LOGGER.fine("Settings: {host="+ host +", port="+ port +", database="+ getDatabase() +", username="+ getUsername() +", password="+ getPassword() +"}");
		
		// Setup connection options: disable pooling
		final MongoClientOptions.Builder optionBuilder = MongoClientOptions.builder();
		optionBuilder.description("MongoDirectConnectionConnector");
		optionBuilder.minConnectionsPerHost(1);
		optionBuilder.connectionsPerHost(1);
		final MongoClientOptions options = optionBuilder.build();

		// Create Mongo client instance
		if (getUsername() != null && !getUsername().trim().isEmpty()) {
			// Perform authenticated connection
			if (LOGGER.isLoggable(Level.FINE))
				LOGGER.fine("Attempting to create authenticated connection...");
			final MongoCredential credential = MongoCredential.createMongoCRCredential(getUsername(), getDatabase(), getPassword().toCharArray());
			mongoClient = new MongoClient(serverAddress, Arrays.asList(new MongoCredential[] { credential }), options);
		}
		else
		{
			// Perform anonymous connection
			if (LOGGER.isLoggable(Level.FINE))
				LOGGER.fine("Attempting to create anonymous connection...");
			mongoClient = new MongoClient(serverAddress, options);
		}

		// Create a connection
		db = mongoClient.getDB(getDatabase());
		db.requestStart();
		db.requestEnsureConnection();
	}

	@Override
	protected void executeBusinessLogic() throws ConnectorException{
		// Run database command and set it in connector output
		if (LOGGER.isLoggable(Level.FINE))
			LOGGER.fine("Running command: "+ getCommand());		
		CommandResult result = db.command(getCommand());
		setCommandResult(result);
		
		if (LOGGER.isLoggable(Level.FINE))
		{
			LOGGER.fine("Command status: "+ (result.ok() ? "suceeded" : "failed") );
			LOGGER.fine("Command result: "+ result.toString());
		}
	}
	
	@Override
	public void disconnect() throws ConnectorException{
		if (db != null)
			db.requestDone();
		if (mongoClient != null)
			mongoClient.close();
	}
	
	// PARAMETER GETTERS & SETTERS
	
	
	private final java.lang.String getHost() {
		return (String) getInputParameter(HOST_INPUT_PARAMETER);
	}

	private final Integer getPort() {
		return (Integer) getInputParameter(PORT_INPUT_PARAMETER);
	}

	private final String getDatabase() {
		return (String) getInputParameter(DATABASE_INPUT_PARAMETER);
	}

	private final String getUsername() {
		return (String) getInputParameter(USERNAME_INPUT_PARAMETER);
	}

	private final String getPassword() {
		return (String) getInputParameter(PASSWORD_INPUT_PARAMETER);
	}

	private final String getCommand() {
		return (String) getInputParameter(COMMAND_INPUT_PARAMETER);
	}

	private final void setCommandResult(CommandResult commandResult) {
		setOutputParameter(COMMANDRESULT_OUTPUT_PARAMETER, commandResult);
	}
}
