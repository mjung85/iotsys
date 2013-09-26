/*
    Calimero - A library for KNX network access
    Copyright (C) 2006-2008 W. Kastner

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package at.ac.tuwien.auto.calimero.log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Global Manager for {@link LogService}s and {@link LogWriter}s.
 * <p>
 * There is only one instance of this manager in the library, obtained with
 * {@link #getManager()}.<br>
 * A log service can be queried and removed. A log writer can be added (i.e. registered)
 * and removed, either to a particular log service or as a global log writer. A global log
 * writer will receive all logging output from all registered log services.
 * 
 * @author B. Malinowsky
 * @see LogWriter
 * @see LogService
 */
public final class LogManager
{
	// TODO method to provide an easy, clean way to shutdown logging at the end of an
	// application, i.e. remove all log-svcs, wait for dispatcher idle, return writers

	private final Map loggers;
	private final List writers;

	private LogManager()
	{
		loggers = Collections.synchronizedMap(new HashMap());
		writers = new Vector();
	}

	/**
	 * Returns the only instance of the log manager.
	 * <p>
	 * 
	 * @return the log manager object
	 */
	public static LogManager getManager()
	{
		return ManagerHolder.mgr;
	}

	/**
	 * Checks whether a log service with <code>name</code> exists in the manager.
	 * <p>
	 * A log service is only listed in the manager, if it was initially queried using
	 * {@link #getLogService(String)}.
	 * 
	 * @param name name of log service
	 * @return <code>true</code> if log service exists, <code>false</code> otherwise
	 */
	public boolean hasLogService(String name)
	{
		synchronized (loggers) {
			return loggers.get(name) != null;
		}
	}

	/**
	 * Queries for a log service with the specified <code>name</code>.
	 * <p>
	 * If the log service with this name already exists in the manager, it will be
	 * returned, otherwise a new log service with this name will be created and added to
	 * the log services listed in the manager.
	 * 
	 * @param name name of log service, the empty string is not allowed
	 * @return the LogService object
	 */
	public LogService getLogService(String name)
	{
		synchronized (loggers) {
			LogService l = (LogService) loggers.get(name);
			if (l == null) {
				l = new LogService(name);
				loggers.put(name, l);
				for (final Iterator i = writers.iterator(); i.hasNext();)
					l.addWriter((LogWriter) i.next());
			}
			return l;
		}
	}

	/**
	 * Removes a log service from the manager.
	 * <p>
	 * If no log service with the specified name is found, no action is performed.
	 * 
	 * @param name name of log service
	 */
	public void removeLogService(String name)
	{
		loggers.remove(name);
	}

	/**
	 * Returns the names of all registered log services.
	 * <p>
	 * 
	 * @return array of type String with log service names
	 */
	public String[] getAllLogServices()
	{
		return (String[]) loggers.keySet().toArray(new String[loggers.size()]);
	}

	/**
	 * Adds a log writer, either global or to a particular log service.
	 * <p>
	 * Note that the writer is added to the log service(s) regardless if it was already
	 * added before.<br>
	 * If the writer is added global, it will receive logging information from all log
	 * services that are already registered or will be registered in the future.
	 * 
	 * @param logService name of a log service; to add the writer global, use an empty
	 *        string or <code>null</code>
	 * @param writer log writer to add
	 * @return true if the writer was added successfully,<br>
	 *         false a specified log service name was not found
	 * @see LogService#addWriter(LogWriter)
	 */
	public boolean addWriter(String logService, LogWriter writer)
	{
		if (logService != null && logService.length() > 0) {
			final LogService l = (LogService) loggers.get(logService);
			if (l != null)
				l.addWriter(writer);
			return l != null;
		}
		synchronized (loggers) {
			writers.add(writer);
			for (final Iterator i = loggers.values().iterator(); i.hasNext();)
				((LogService) i.next()).addWriter(writer);
			return true;
		}
	}

	/**
	 * Removes a log writer, either global or from a particular <code>logService</code>.
	 * <p>
	 * Note that for a writer to be removed global, it had to be added global before.
	 * 
	 * @param logService name of the log service of which the writer will be removed; to
	 *        remove the writer global, use an empty string or <code>null</code>
	 * @param writer log writer to remove
	 * @see LogService#removeWriter(LogWriter)
	 */
	public void removeWriter(String logService, LogWriter writer)
	{
		if (logService != null && logService.length() > 0) {
			final LogService l = (LogService) loggers.get(logService);
			if (l != null)
				l.removeWriter(writer);
		}
		else
			synchronized (loggers) {
				if (writers.remove(writer))
					for (final Iterator i = loggers.values().iterator(); i.hasNext();)
						((LogService) i.next()).removeWriter(writer);
			}
	}

	/**
	 * Returns all registered global log writer.
	 * <p>
	 * Global are all log writers which were not registered at a particular log service.
	 * 
	 * @return array with global log writers
	 */
	public LogWriter[] getAllGlobalWriter()
	{
		return (LogWriter[]) writers.toArray(new LogWriter[writers.size()]);
	}

	private static final class ManagerHolder
	{
		static final LogManager mgr = new LogManager();

		private ManagerHolder()
		{}
	}
}
