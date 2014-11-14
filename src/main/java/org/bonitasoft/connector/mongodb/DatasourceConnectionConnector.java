package org.bonitasoft.connector.mongodb;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.bonitasoft.engine.connector.AbstractConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

import com.mongodb.CommandResult;
import com.mongodb.DB;

public class DatasourceConnectionConnector extends AbstractConnector {

	private static final Logger LOGGER = Logger.getLogger(DatasourceConnectionConnector.class.getName());
	
	// Connector input parameters
	private final static String DATASOURCE_INPUT_PARAMETER	= "datasource";
	private final static String COMMAND_INPUT_PARAMETER		= "command";
	// Connector output parameters
	private final String COMMANDRESULT_OUTPUT_PARAMETER		= "commandResult";
	
	
	// Java server JNDI context
	private static Context context;
	// Database connection
	private DB db;
	
	
	@Override
	public void validateInputParameters() throws ConnectorValidationException {
		try {
			getDatasource();
		} catch (ClassCastException cce) {
			throw new ConnectorValidationException("datasource type is invalid");
		}
		try {
			getCommand();
		} catch (ClassCastException cce) {
			throw new ConnectorValidationException("command type is invalid");
		}
	}
	
	@Override
    public void connect() throws ConnectorException {
		// Dump connection settings
		if (LOGGER.isLoggable(Level.FINE))
			LOGGER.fine("Settings: {datasource="+ getDatasource() +"}");
		
		// Initalize context for first use
		if (context == null)
		{
			try {
				context = new InitialContext();
			} catch (NamingException e) {
				throw new ConnectorException("Failed to initalize context", e);
			}
		}
		// Retrieve connection
		try {
			db = (DB) context.lookup(getDatasource());
		} catch (NamingException e) {
			throw new ConnectorException("Failed to retrieve connection from datasource "+ getDatasource(), e);
		}
		// Start request
		db.requestStart();
		db.requestEnsureConnection();
	}

	@Override
	protected void executeBusinessLogic() throws ConnectorException {
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
    public void disconnect() throws ConnectorException {
		if (db != null)
			db.requestDone();
	}
	
	// PARAMETER GETTERS & SETTERS
	
	private final String getDatasource() {
		return (String) getInputParameter(DATASOURCE_INPUT_PARAMETER);
	}

	private final String getCommand() {
		return (String) getInputParameter(COMMAND_INPUT_PARAMETER);
	}

	private final void setCommandResult(CommandResult commandResult) {
		setOutputParameter(COMMANDRESULT_OUTPUT_PARAMETER, commandResult);
	}
}
