package com.mygene2.postgreslistener.service;

import org.apache.velocity.app.VelocityEngine;

public class MyGeneVelocityEngine extends VelocityEngine {
	public MyGeneVelocityEngine() {
		super();
		this.setProperty("resource.loader", "class");
		this.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
	}
}
