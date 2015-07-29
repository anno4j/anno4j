package org.openrdf.repository.object.helpers;

import java.io.PrintStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;

public class ConnectionRecorder implements InvocationHandler {
	public static RepositoryConnection wrap(RepositoryConnection conn) {
		InvocationHandler handler = new ConnectionRecorder(conn);
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Class<?>[] faces = new Class<?>[]{RepositoryConnection.class};
		return (RepositoryConnection) Proxy.newProxyInstance(cl, faces, handler);
	}

	private RepositoryConnection conn;

	private PrintStream out = System.out;

	public ConnectionRecorder(RepositoryConnection conn) {
		this.conn = conn;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String name = method.getName();
		if (!name.startsWith("is") && !name.startsWith("get")) {
		out.print("conn.");
		out.print(name);
		out.print("(");
		if (args != null) {
		for (int i=0; i<args.length; i++) {
			if (i > 0) {
				out.print(",");
			}
			print(args[i]);
		}
		}
		out.println(");");
		}
		return method.invoke(conn, args);
	}

	private void print(Object object) {
		if (object instanceof Object[]) {
			out.print("new ");
			out.print(object.getClass().getComponentType().getSimpleName());
			out.print("[]{");
			Object[] ar = (Object[]) object;
			for (int i=0; i<ar.length; i++) {
				if (i > 0) {
					out.print(",");
				}
				print(ar[i]);
			}
			out.print("}");
		} else if (object instanceof URI) {
			out.print("new URIImpl(\"");
			out.print(object);
			out.print("\")");
		} else if (object instanceof BNode) {
			BNode bnode = (BNode) object;
			out.print("new BNodeImpl(\"");
			out.print(bnode.getID());
			out.print("\")");
		} else if (object instanceof Literal) {
			Literal lit = (Literal) object;
			out.print("new LiteralImpl(");
			out.print(lit.toString());
			out.print(")");
		} else {
			out.print(object);
		}
	}

}
