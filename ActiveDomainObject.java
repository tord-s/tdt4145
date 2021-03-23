import java.sql.*;

/**
 * Represents an entry in a database table
 * 
 * @author Bj�rge, Martinus
 * @author S�fteland, Tord �stensen
 * @author Torsvik, Jakob Martin
 *
 */
public abstract class ActiveDomainObject {
	/**
	 * Retrieves entity-information from the database
	 * 
	 * @param conn Connection to the database
	 */
	public abstract void initialize(Connection conn);

	/**
	 * Saves the object to the database as an entity. Overwrites if duplicate primary key exists
	 * 
	 * @param conn Connection to the database
	 */
	public abstract void save(Connection conn);
}
