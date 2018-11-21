package il.ac.bgu.cs.fvm.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import il.ac.bgu.cs.fvm.exceptions.FVMException;
import il.ac.bgu.cs.fvm.exceptions.StateNotFoundException;
import il.ac.bgu.cs.fvm.transitionsystem.Transition;
import il.ac.bgu.cs.fvm.transitionsystem.TransitionSystem;

public class TSImple<STATE, ACTION, ATOMIC_PROPOSITION> implements TransitionSystem<STATE, ACTION, ATOMIC_PROPOSITION> {

	String name;
	HashSet<STATE> states = new HashSet<>();
	HashSet<ACTION> actions = new HashSet<>();
	HashSet<STATE> initStates = new HashSet<>();
	HashSet<Transition<STATE, ACTION> > transitions = new HashSet<>();
	HashSet<ATOMIC_PROPOSITION> ap = new HashSet<>();
	Map<STATE, Set<ATOMIC_PROPOSITION>> labelingFunction = new HashMap<STATE, Set<ATOMIC_PROPOSITION>>();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actions == null) ? 0 : actions.hashCode());
		result = prime * result + ((ap == null) ? 0 : ap.hashCode());
		result = prime * result + ((initStates == null) ? 0 : initStates.hashCode());
		result = prime * result + ((labelingFunction == null) ? 0 : labelingFunction.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((states == null) ? 0 : states.hashCode());
		result = prime * result + ((transitions == null) ? 0 : transitions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {//TODO check that it compares correctly like the comment in the interface
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		TSImple other = (TSImple) obj;
		if (actions == null) {
			if (other.actions != null)
				return false;
		} else if (!actions.equals(other.actions))
			return false;
		if (ap == null) {
			if (other.ap != null)
				return false;
		} else if (!ap.equals(other.ap))
			return false;
		if (initStates == null) {
			if (other.initStates != null)
				return false;
		} else if (!initStates.equals(other.initStates))
			return false;
		if (labelingFunction == null) {
			if (other.labelingFunction != null)
				return false;
		} else if (!labelingFunction.equals(other.labelingFunction))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (states == null) {
			if (other.states != null)
				return false;
		} else if (!states.equals(other.states))
			return false;
		if (transitions == null) {
			if (other.transitions != null)
				return false;
		} else if (!transitions.equals(other.transitions))
			return false;
		return true;
	}

	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void addAction(ACTION anAction) {
		actions.add(anAction);
	}

	@Override
	public void setInitial(STATE aState, boolean isInitial) throws StateNotFoundException {
		initStates.add(aState);
	}

	@Override
	public void addState(STATE state) {
		states.add(state);
	}

	@Override
	public void addTransition(Transition<STATE, ACTION> t) throws FVMException {
		transitions.add(t);
	}

	@Override
	public Set<ACTION> getActions() {
		return actions;
	}

	@Override
	public void addAtomicProposition(ATOMIC_PROPOSITION p) {
		ap.add(p);
	}

	@Override
	public Set<ATOMIC_PROPOSITION> getAtomicPropositions() {
		return ap;
	}

	@Override
	public void addToLabel(STATE s, ATOMIC_PROPOSITION l) throws FVMException {
		if(labelingFunction.get(s)==null) {
			labelingFunction.put(s, new HashSet<>());
		}
		labelingFunction.get(s).add(l);
	}

	@Override
	public Set<ATOMIC_PROPOSITION> getLabel(STATE s) {
		return labelingFunction.get(s);
	}

	@Override
	public Set<STATE> getInitialStates() {
		return initStates;
	}

	@Override
	public Map<STATE, Set<ATOMIC_PROPOSITION>> getLabelingFunction() {
		return labelingFunction;
	}

	@Override
	public Set<STATE> getStates() {
		return states;
	}

	@Override
	public Set<Transition<STATE, ACTION>> getTransitions() {
		return transitions;
	}

	@Override
	public void removeAction(ACTION action) throws FVMException {
		LinkedList<Transition<STATE, ACTION>> toRemove = new LinkedList<>();
		for(Transition<STATE, ACTION> t : transitions) {
			if(t.getAction().equals(t)) {
				toRemove.add(t);
			}
		}
		for(Transition<STATE, ACTION> t : toRemove) {
			transitions.remove(t);
		}
		if(actions.contains(action)) {
			actions.remove(action);
		}
		else {
			throw new ActionNotFoundException("the following action is not present in the Transition System: " + action.toString());
		}
	}

	@Override
	public void removeAtomicProposition(ATOMIC_PROPOSITION p) throws FVMException {
		for(STATE s : states) {
			if(labelingFunction.get(s).contains(p)) {
				labelingFunction.get(s).remove(p);
			}
		}
		if(ap.contains(p)) {
			actions.remove(p);
		}
		else {
			throw new AtomicPropositionNotFoundException("the following atomic preposition is not present in the Transition System: " + p.toString());
		}
	}

	@Override
	public void removeLabel(STATE s, ATOMIC_PROPOSITION l) {
		if(labelingFunction.get(s).contains(l)) {
			labelingFunction.get(s).remove(l);
		}
	}

	@Override
	public void removeState(STATE state) throws FVMException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTransition(Transition<STATE, ACTION> t) {
		transitions.remove(t);
	}

}

