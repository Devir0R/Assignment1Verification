package il.ac.bgu.cs.fvm.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import il.ac.bgu.cs.fvm.exceptions.DeletionOfAttachedActionException;
import il.ac.bgu.cs.fvm.exceptions.DeletionOfAttachedAtomicPropositionException;
import il.ac.bgu.cs.fvm.exceptions.DeletionOfAttachedStateException;
import il.ac.bgu.cs.fvm.exceptions.FVMException;
import il.ac.bgu.cs.fvm.exceptions.InvalidTransitionException;
import il.ac.bgu.cs.fvm.exceptions.StateNotFoundException;
import il.ac.bgu.cs.fvm.exceptions.TransitionSystemPart;
import il.ac.bgu.cs.fvm.impl.exc.AtomicPropositionNotFoundException;
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
		if(states.contains(aState)) {
			if(isInitial) {
				initStates.add(aState);
			}
			else {
				initStates.remove(aState);
			}
		}
		else {
			throw new StateNotFoundException("the following state cannot be initial because it is not present in the Transition System: " + aState);
		}
	}

	@Override
	public void addState(STATE state) {
		states.add(state);
	}

	@Override
	public void addTransition(Transition<STATE, ACTION> t) throws FVMException {
		if(states.contains(t.getFrom())&&states.contains(t.getTo())&&actions.contains(t.getAction())) {
					transitions.add(t);
		}
		else {
			throw new InvalidTransitionException(t);
		}
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
		if(!ap.contains(l)) {
			throw new AtomicPropositionNotFoundException("the action " + s + "could not be labeled by " + l +" because it is not an atomic proposition in the transition system");
		}
		if(labelingFunction.get(s)==null) {
			labelingFunction.put(s, new HashSet<>());
		}
		labelingFunction.get(s).add(l);
	}

	@Override
	public Set<ATOMIC_PROPOSITION> getLabel(STATE s) {
		if(!states.contains(s)) {
			throw new StateNotFoundException("could not get state's labeling, because the following state is not present in the Transition System states: " + s);
		}
		if(labelingFunction.get(s)==null) {
			labelingFunction.put(s, new HashSet<>());
		}
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
		for(Transition<STATE, ACTION> t : transitions) {
			if(t.getAction().equals(action)) {
				throw new DeletionOfAttachedActionException(action,TransitionSystemPart.TRANSITIONS);
			}
		}
		if(actions.contains(action)) {
			actions.remove(action);
		}
	}

	@Override
	public void removeAtomicProposition(ATOMIC_PROPOSITION p) throws FVMException {
		for(STATE s : states) {
			if(labelingFunction.get(s).contains(p)) {
				throw new DeletionOfAttachedAtomicPropositionException(p,TransitionSystemPart.STATES);
			}
		}
		if(ap.contains(p)) {
			ap.remove(p);
		}
	}

	@Override
	public void removeLabel(STATE s, ATOMIC_PROPOSITION l) {
		Set<ATOMIC_PROPOSITION> state_ap = labelingFunction.get(s);
		if(state_ap!=null && state_ap.contains(l)) {
			state_ap.remove(l);
		}
	}

	@Override
	public void removeState(STATE state) throws FVMException {
		for(Transition<STATE, ACTION> t : transitions) {
			if(t.getFrom().equals(state) || t.getTo().equals(state)) {
				throw new DeletionOfAttachedStateException(state,TransitionSystemPart.TRANSITIONS);
			}
		}
		if(labelingFunction.get(state)!=null && labelingFunction.get(state).size()>0)
			throw new DeletionOfAttachedStateException(state,TransitionSystemPart.LABELING_FUNCTION);
		if(initStates.contains(state))
			throw new DeletionOfAttachedStateException(state,TransitionSystemPart.INITIAL_STATES);
		states.remove(state);

	}

	@Override
	public void removeTransition(Transition<STATE, ACTION> t) {
		transitions.remove(t);
	}

}

