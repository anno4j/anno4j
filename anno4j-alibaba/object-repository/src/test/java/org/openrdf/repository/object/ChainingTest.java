package org.openrdf.repository.object;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;

public class ChainingTest extends ObjectRepositoryTestCase {

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(ChainingTest.class);
	}

	@Override
	public void setUp() throws Exception {
		config.addConcept(Command.class);
		config.addBehaviour(Command1.class);
		config.addBehaviour(Command2.class);
		super.setUp();
	}

	public static int command = 0;

	@Iri("urn:command")
	public interface Command {
		String doCommand();
	}
	
	public static class Command1 implements Command {
		public String doCommand() {
			if (command == 1)
				return "Command 1";
			return null;
		}
	}
	
	public static class Command2 implements Command {
		public String doCommand() {
			if (command == 2)
				return "Command 2";
			return null;
		}
	}
	
	public void testChainCommand() throws RepositoryException {
		Command cmd = con.addDesignation(con.getObjectFactory().createObject(), Command.class);
		command = 0;
		assertNull(cmd.doCommand());
		command = 1;
		assertEquals("Command 1", cmd.doCommand());
		command = 2;
		assertEquals("Command 2", cmd.doCommand());
	}
}
