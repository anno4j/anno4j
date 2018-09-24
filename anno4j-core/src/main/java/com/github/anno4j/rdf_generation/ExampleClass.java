package com.github.anno4j.rdf_generation;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JFileChooser;

public class ExampleClass {

	public static void main(String[] args)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, MalformedURLException {
		JFileChooser chooser = new JFileChooser();
		File myFolder = null;
		chooser.setCurrentDirectory(null);
		int retrival = chooser.showSaveDialog(null);
		if (retrival == JFileChooser.APPROVE_OPTION) {
			myFolder = new File(chooser.getSelectedFile().getAbsolutePath());
		}
		URLClassLoader classLoader = new URLClassLoader(new URL[] { myFolder.toURI().toURL() },
				Thread.currentThread().getContextClassLoader());
//		Class<?> myClass = Class.forName(myFolder.getName(), true, classLoader);
		Class<?> myClass = Class.forName(myFolder.getName());

		Class<?> obj = (Class<?>) myClass.newInstance();
		System.out.println(obj.getName());

	}
}