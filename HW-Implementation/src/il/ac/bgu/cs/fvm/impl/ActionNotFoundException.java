package il.ac.bgu.cs.fvm.impl;

import il.ac.bgu.cs.fvm.exceptions.FVMException;

public class ActionNotFoundException extends FVMException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ActionNotFoundException(String string) {
		super(string);
	}

}
