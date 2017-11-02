package com.xhystc.wheel.connection;

public class ConnectionUtil
{
	static private ThreadLocal<Boolean> env = new ThreadLocal<>();
	static public void inWorkEnv(){
		env.set(true);
	}
	static public boolean isInWorkEnv(){
		if(env.get()==null || !env.get()){
			return false;
		}
		return true;
	}
}
