package il.ac.bgu.cs.fvm.impl;

import il.ac.bgu.cs.fvm.exceptions.FVMException;

public class StateNotFoundException extends FVMException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StateNotFoundException(String string) {
		super(string);
	}

}
