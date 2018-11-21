package il.ac.bgu.cs.fvm.impl;

import java.util.List;
import java.util.Set;

import il.ac.bgu.cs.fvm.programgraph.PGTransition;
import il.ac.bgu.cs.fvm.programgraph.ProgramGraph;

public class PGImple<L,A> implements ProgramGraph<L, A> {

	@Override
	public void addInitalization(List<String> init) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInitial(L location, boolean isInitial) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addLocation(L l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTransition(PGTransition<L, A> t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<List<String>> getInitalizations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<L> getInitialLocations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<L> getLocations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<PGTransition<L, A>> getTransitions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeLocation(L l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTransition(PGTransition<L, A> t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

}
