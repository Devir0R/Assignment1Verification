package il.ac.bgu.cs.fvm.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import il.ac.bgu.cs.fvm.programgraph.PGTransition;
import il.ac.bgu.cs.fvm.programgraph.ProgramGraph;

public class PGImple<L,A> implements ProgramGraph<L, A> {

	String graphName;
	HashSet<L> locs = new HashSet<>();
	HashSet<A> actions = new HashSet<>();
	HashSet<L> initLocs = new HashSet<>();
	HashSet<PGTransition<L, A>> transitions = new HashSet<PGTransition<L, A>>();
	HashSet<HashSet<String>> valuesInits = new HashSet<>();
	
	@Override
	public void addInitalization(List<String> init) {
		valuesInits.add(new HashSet<String>(init));
	}

	@Override
	public void setInitial(L location, boolean isInitial) {
		if(isInitial) {
			initLocs.add(location);
		}
		else {
			initLocs.remove(location);
		}
	}

	@Override
	public void addLocation(L l) {
		locs.add(l);
	}

	@Override
	public void addTransition(PGTransition<L, A> t) {
		transitions.add(t);
	}

	@Override
	public Set<List<String>> getInitalizations() {
		HashSet<List<String>> inits = new HashSet<>();
		for(HashSet<String> oneInit : valuesInits) {
			inits.add(new LinkedList<>(oneInit));
		}
		return inits;
	}

	@Override
	public Set<L> getInitialLocations() {
		return initLocs;
	}

	@Override
	public Set<L> getLocations() {
		return locs;
	}

	@Override
	public String getName() {
		return graphName;
	}

	@Override
	public Set<PGTransition<L, A>> getTransitions() {
		return transitions;
	}

	@Override
	public void removeLocation(L l) {
		LinkedList<PGTransition<L, A>> toRemove = new LinkedList<>();
		for(PGTransition<L,A> pgt : transitions) {
			if(pgt.getFrom().equals(l)||pgt.getTo().equals(l)) {
				toRemove.add(pgt);
			}
		}
		for(PGTransition<L,A> pgt : toRemove) {
			transitions.remove(pgt);
		}
		initLocs.remove(l);
		locs.remove(l);
	}

	@Override
	public void removeTransition(PGTransition<L, A> t) {
		transitions.remove(t);
	}

	@Override
	public void setName(String name) {
		graphName = name;
	}

}
