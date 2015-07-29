package org.openrdf.repository.object;

import junit.framework.Test;

import org.openrdf.annotations.Matching;
import org.openrdf.annotations.Precedes;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;

public class PrecedesTest extends ObjectRepositoryTestCase {

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(PrecedesTest.class);
	}

	@Override
	public void setUp() throws Exception {
		config.addConcept(Command1.class);
		config.addConcept(Command2.class);
		config.addConcept(Command3.class);
		config.addBehaviour(Command1Impl.class);
		config.addBehaviour(Command2Impl.class);
		config.addBehaviour(Command3Impl.class);
		super.setUp();
	}

	public static int command = 0;

	public interface Command {
		String doCommand();
	}

	@Matching("/command/*")
	public interface Command1 extends Command {

	}

	@Matching("/command/2/*")
	public interface Command2 extends Command {

	}

	@Matching("http://localhost/*")
	public interface Command3 extends Command {

	}

	public static class Command1Impl implements Command1 {
		public String doCommand() {
			return "Command 1";
		}
	}

	@Precedes(Command1Impl.class)
	public static class Command2Impl implements Command2 {
		public String doCommand() {
			return "Command 2";
		}
	}

	@Precedes(Command2Impl.class)
	public static class Command3Impl implements Command3 {
		public String doCommand() {
			return "Command 3";
		}
	}

	public void testSingleCommand() throws RepositoryException {
		Command cmd = (Command) con.getObject("http://localhost:8080/command/1/cmd");
		assertEquals("Command 1", cmd.doCommand());
	}

	public void testPrecedesCommand() throws RepositoryException {
		Command cmd = (Command) con.getObject("http://localhost:8080/command/2/cmd");
		assertEquals("Command 2", cmd.doCommand());
	}

	public void testPrecedesDepthCommand() throws RepositoryException {
		Command cmd = (Command) con.getObject("http://localhost/command/2/cmd");
		assertEquals("Command 3", cmd.doCommand());
	}

	public void testPrecedesIndirectlyCommand() throws RepositoryException {
		Command cmd = (Command) con.getObject("http://localhost/command/3/cmd");
		assertEquals("Command 3", cmd.doCommand());
	}
}
