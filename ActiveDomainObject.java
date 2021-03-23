import java.sql.*;

public abstract class ActiveDomainObject {
	/**
	 * Retrieves entity-information from the database
	 * 
	 * @param conn Connection to the database
	 */
	public abstract void initialize(Connection conn);

	public void refresh(Connection conn) {
		initialize(conn);
	}

	/**
	 * Saves the object to the database as an entity
	 * 
	 * @param conn Connection to the database
	 */
	public abstract void save(Connection conn);
}
