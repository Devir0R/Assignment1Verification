package il.ac.bgu.cs.fvm.impl;

import il.ac.bgu.cs.fvm.FvmFacade;
import il.ac.bgu.cs.fvm.automata.Automaton;
import il.ac.bgu.cs.fvm.automata.MultiColorAutomaton;
import il.ac.bgu.cs.fvm.channelsystem.ChannelSystem;
import il.ac.bgu.cs.fvm.circuits.Circuit;
import il.ac.bgu.cs.fvm.exceptions.ActionNotFoundException;
import il.ac.bgu.cs.fvm.exceptions.StateNotFoundException;
import il.ac.bgu.cs.fvm.ltl.LTL;
import il.ac.bgu.cs.fvm.programgraph.ActionDef;
import il.ac.bgu.cs.fvm.programgraph.ConditionDef;
import il.ac.bgu.cs.fvm.programgraph.ProgramGraph;
import il.ac.bgu.cs.fvm.transitionsystem.AlternatingSequence;
import il.ac.bgu.cs.fvm.transitionsystem.Transition;
import il.ac.bgu.cs.fvm.transitionsystem.TransitionSystem;
import il.ac.bgu.cs.fvm.util.Pair;
import il.ac.bgu.cs.fvm.verification.VerificationResult;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.svvrl.goal.core.util.HashSet;

/**
 * Implement the methods in this class. You may add additional classes as you
 * want, as long as they live in the {@code impl} package, or one of its 
 * sub-packages.
 */
public class FvmFacadeImpl implements FvmFacade {

	@Override
	public <S, A, P> TransitionSystem<S, A, P> createTransitionSystem() {
		return new TSImple<>();
	}

	@Override
	public <S, A, P> boolean isActionDeterministic(TransitionSystem<S, A, P> ts) {
		boolean isInitStatesCountLessThanOne =  ts.getInitialStates().size()<=1;
		return isInitStatesCountLessThanOne && isEveryPostCountLessThanOne(ts);
	}

	@Override
	public <S, A, P> boolean isAPDeterministic(TransitionSystem<S, A, P> ts) {
		boolean isInitStatesCountLessThanOne =  ts.getInitialStates().size()<=1;
		return isInitStatesCountLessThanOne && isEveryLabelPostCountLessThanOne(ts);
	}

	private <S, A, P> boolean isEveryLabelPostCountLessThanOne(TransitionSystem<S, A, P> ts) {
		for(S state : ts.getStates()) {
			Set<Set<P>> labelings = new HashSet<>();
			for(S postState : post(ts, state)) {
				Set<P> stateLabeling = ts.getLabel(postState);
				if(labelings.contains(stateLabeling)) {
					return false;
				}
				else {
					labelings.add(stateLabeling);
				}

			}
		}
		return true;
	}

	private <S, A, P> boolean isEveryPostCountLessThanOne(TransitionSystem<S, A, P> ts) {
		HashMap<Pair<S,A>,Integer> stateActionPairs_state = new HashMap<>();
		for(Transition<S, A> t : ts.getTransitions()) {
			if(stateActionPairs_state.get(new Pair<S,A>(t.getFrom(),t.getAction()))==null) {
				stateActionPairs_state.put(new Pair<S,A>(t.getFrom(),t.getAction()), 1);
			}
			else {
				return false;
			}
		}
		return true;
	}

	@Override
	public <S, A, P> boolean isExecution(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
		return isStateTerminal(ts, e.last()) && ts.getInitialStates().contains(e.head()) && isExecutionFragment(ts, e);
	}

	@Override
	public <S, A, P> boolean isExecutionFragment(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
		S from,to;
		A action;
		AlternatingSequence<S, A> itStates = e;
		AlternatingSequence<A, S> itActions;
		while(itStates.size()>1) {
			from = itStates.head();
			itActions = itStates.tail();
			action = itActions.head();
			itStates = itActions.tail();
			to = itStates.head();
			if(!ts.getStates().contains(from)) {
				throw new StateNotFoundException(from);
			}
			if(!ts.getActions().contains(action)) {
				throw new ActionNotFoundException(action);
			}
			if(!ts.getStates().contains(to)) {
				throw new StateNotFoundException(from);
			}
			if(!ts.getTransitions().contains(new Transition<S, A>(from, action, to))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public <S, A, P> boolean isInitialExecutionFragment(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
		return ts.getInitialStates().contains(e.head()) && isExecutionFragment(ts, e);
	}

	@Override
	public <S, A, P> boolean isMaximalExecutionFragment(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
		return isStateTerminal(ts, e.last()) && isExecutionFragment(ts, e);
	}

	@Override
	public <S, A> boolean isStateTerminal(TransitionSystem<S, A, ?> ts, S s) {
		if(!ts.getStates().contains(s)) {
			throw new StateNotFoundException(s);
		}
		return post(ts,s).size()==0;
	}

	@Override
	public <S> Set<S> post(TransitionSystem<S, ?, ?> ts, S s) {
		HashSet<S> post_s = new HashSet<>();
		for(Transition<S, ?> t : ts.getTransitions()) {
			if(t.getFrom().equals(s)) {
				post_s.add(t.getTo());
			}
		}
		return post_s;
	}

	@Override
	public <S> Set<S> post(TransitionSystem<S, ?, ?> ts, Set<S> c) {
		HashSet<S> post_states = new HashSet<>();
		for(Transition<S, ?> t : ts.getTransitions()) {
			if(c.contains(t.getFrom())) {
				post_states.add(t.getTo());
			}
		}
		return post_states;
	}

	@Override
	public <S, A> Set<S> post(TransitionSystem<S, A, ?> ts, S s, A a) {
		HashSet<S> post_s_a = new HashSet<>();
		for(Transition<S, A> t : ts.getTransitions()) {
			if(t.getFrom().equals(s)&& t.getAction().equals(a)) {
				post_s_a.add(t.getTo());
			}
		}
		return post_s_a;
	}

	@Override
	public <S, A> Set<S> post(TransitionSystem<S, A, ?> ts, Set<S> c, A a) {
		HashSet<S> post_states_a = new HashSet<>();
		for(Transition<S, ?> t : ts.getTransitions()) {
			if(t.getAction().equals(a)&& c.contains(t.getFrom())) {
				post_states_a.add(t.getTo());
			}
		}
		return post_states_a;
	}

	@Override
	public <S> Set<S> pre(TransitionSystem<S, ?, ?> ts, S s) {
		HashSet<S> pre_s = new HashSet<>();
		for(Transition<S, ?> t : ts.getTransitions()) {
			if(t.getTo().equals(s)) {
				pre_s.add(t.getFrom());
			}
		}
		return pre_s;
	}

	@Override
	public <S> Set<S> pre(TransitionSystem<S, ?, ?> ts, Set<S> c) {
		HashSet<S> pre_states = new HashSet<>();
		for(Transition<S, ?> t : ts.getTransitions()) {
			if(c.contains(t.getTo())) {
				pre_states.add(t.getFrom());
			}
		}
		return pre_states;
	}

	@Override
	public <S, A> Set<S> pre(TransitionSystem<S, A, ?> ts, S s, A a) {
		HashSet<S> pre_s_a = new HashSet<>();
		for(Transition<S, A> t : ts.getTransitions()) {
			if(t.getTo().equals(s)&& t.getAction().equals(a)) {
				pre_s_a.add(t.getFrom());
			}
		}
		return pre_s_a;
	}

	@Override
	public <S, A> Set<S> pre(TransitionSystem<S, A, ?> ts, Set<S> c, A a) {
		HashSet<S> pre_states_a = new HashSet<>();
		for(Transition<S, ?> t : ts.getTransitions()) {
			if(t.getAction().equals(a)&& c.contains(t.getTo())) {
				pre_states_a.add(t.getFrom());
			}
		}
		return pre_states_a;
	}

	@Override
	public <S, A> Set<S> reach(TransitionSystem<S, A, ?> ts) {
		HashSet<S> reachable = new HashSet<>();
		bfs(ts,(s)->reachable.add(s));
		return reachable;
	}

	private <S, A> HashMap<S, Boolean> bfs(TransitionSystem<S, A, ?> ts,Consumer<S> stateFunction) {
		HashMap<S, Boolean> visited = new  HashMap<>();
		// Mark all the vertices as not visited(By default 
		// set as false) 
		for(S state : ts.getStates()) {
			visited.put(state, false);
		}
		// Create a queue for BFS 
		LinkedList<S> queue = new LinkedList<>(); 

		// Mark the current node as visited and enqueue it
		for(S initState : ts.getInitialStates()) {
			visited.replace(initState, false, true);
			queue.add(initState);
			stateFunction.accept(initState);
		}

		S s;
		while (queue.size() != 0) 
		{ 
			// Dequeue a vertex from queue and print it 
			s = queue.poll(); 

			// Get all adjacent vertices of the dequeued vertex s 
			// If a adjacent has not been visited, then mark it 
			// visited and enqueue it 

			Iterator<S> currStateNeighbors = getNeighbors(s,ts).iterator(); 
			while (currStateNeighbors.hasNext()) 
			{ 
				S aNeighbor = currStateNeighbors.next(); 
				if (!visited.get(aNeighbor)) 
				{ 
					visited.replace(aNeighbor, false, true);
					queue.add(aNeighbor); 
					stateFunction.accept(aNeighbor);
				} 
			} 
		}
		return visited;
	}


	private <S,A> Set<S> getNeighbors(S s, TransitionSystem<S, A, ?> ts) {
		HashSet<S> neighbors = new HashSet<>();
		for(Transition<S, A> t : ts.getTransitions()) {
			if(t.getFrom().equals(s)) {
				neighbors.add(t.getTo());
			}
		}
		return neighbors;
	}

	@Override
	public <S1, S2, A, P> TransitionSystem<Pair<S1, S2>, A, P> interleave(TransitionSystem<S1, A, P> ts1, TransitionSystem<S2, A, P> ts2) {
		TransitionSystem<Pair<S1,S2>, A, P> interleaved = createTransitionSystem();
		addUnionAction(ts1, ts2, interleaved);
		addUnionAP(ts1, ts2, interleaved);
		addCartesianProductStates(ts1, ts2, interleaved);
		addTransitionsByTS1Actions(ts1, ts2, interleaved);
		addTransitionsByTS2Actions(ts1, ts2, interleaved);
		return interleaved;

	}

	private <S2, S1, A, P> void addTransitionsByTS2Actions(TransitionSystem<S1, A, P> ts1,
			TransitionSystem<S2, A, P> ts2, TransitionSystem<Pair<S1, S2>, A, P> interleaved) {
		for(Transition<S2, A> t : ts2.getTransitions()) {
			for(S1 state_from_s1 : ts1.getStates()) {
				interleaved.addTransition(
						new Transition<Pair<S1,S2>, A>(
								new Pair<S1, S2>(state_from_s1,t.getFrom()),
								t.getAction(),
								new Pair<S1, S2>(state_from_s1,t.getTo())));
			}
		}
	}

	private <S1, S2, A, P> void addTransitionsByTS1Actions(TransitionSystem<S1, A, P> ts1,
			TransitionSystem<S2, A, P> ts2, TransitionSystem<Pair<S1, S2>, A, P> interleaved) {
		for(Transition<S1, A> t : ts1.getTransitions()) {
			for(S2 state_from_s2 : ts2.getStates()) {
				interleaved.addTransition(
						new Transition<Pair<S1,S2>, A>(
								new Pair<S1, S2>(t.getFrom(),state_from_s2),
								t.getAction(),
								new Pair<S1, S2>(t.getTo(),state_from_s2)));
			}
		}
	}

	private <S2, S1, A, P> void addCartesianProductStates(TransitionSystem<S1, A, P> ts1,
			TransitionSystem<S2, A, P> ts2, TransitionSystem<Pair<S1, S2>, A, P> interleaved) {
		for(S1 stae_from_ts1 : ts1.getStates()) {
			for(S2 stae_from_ts2 : ts2.getStates()) {
				Pair<S1, S2> s1Xs2 = new Pair<S1, S2>(stae_from_ts1, stae_from_ts2);
				interleaved.addState(s1Xs2);
				if(ts1.getInitialStates().contains(stae_from_ts1) && ts2.getInitialStates().contains(stae_from_ts2)) {
					interleaved.setInitial(s1Xs2,true);
				}
				for(P prop : ts1.getLabel(stae_from_ts1)) {
					interleaved.addToLabel(s1Xs2, prop);
				}
				for(P prop : ts2.getLabel(stae_from_ts2)) {
					interleaved.addToLabel(s1Xs2, prop);
				}
			}	
		}
	}

	private <S1, A, P, S2> void addUnionAP(TransitionSystem<S1, A, P> ts1, TransitionSystem<S2, A, P> ts2,
			TransitionSystem<Pair<S1, S2>, A, P> interleaved) {
		interleaved.addAllAtomicPropositions(ts1.getAtomicPropositions());
		interleaved.addAllAtomicPropositions(ts2.getAtomicPropositions());
	}

	private <S1, A, P, S2> void addUnionAction(TransitionSystem<S1, A, P> ts1, TransitionSystem<S2, A, P> ts2,
			TransitionSystem<Pair<S1, S2>, A, P> interleaved) {
		interleaved.addAllActions(ts1.getActions());
		interleaved.addAllActions(ts2.getActions());
	}

	@Override
	public <S1, S2, A, P> TransitionSystem<Pair<S1, S2>, A, P> interleave(TransitionSystem<S1, A, P> ts1, TransitionSystem<S2, A, P> ts2, Set<A> handShakingActions) {
		TransitionSystem<Pair<S1,S2>, A, P> interleaved = createTransitionSystem();
		addUnionAction(ts1, ts2, interleaved);
		addUnionAP(ts1, ts2, interleaved);
		addCartesianProductStates(ts1, ts2, interleaved);
		if(handShakingActions.size()==0) {
			addTransitionsByTS1Actions(ts1, ts2, interleaved);
			addTransitionsByTS2Actions(ts1, ts2, interleaved);
		}
		else {
			HashMap<A,Set<Transition<S1, A>>> a_to_transitionsWithA_in_ts1 = new HashMap<>();
			HashMap<A,Set<Transition<S2, A>>> a_to_transitionsWithA_in_ts2 = new HashMap<>();
			for(A actionInHandshake : handShakingActions) {
				a_to_transitionsWithA_in_ts1.put(actionInHandshake, new HashSet<>());
				a_to_transitionsWithA_in_ts2.put(actionInHandshake, new HashSet<>());
			}
			for(Transition<S2, A> t : ts2.getTransitions()) {
				addTransitionExcludingHandshakeActionsTS1(ts1, handShakingActions, interleaved,
						a_to_transitionsWithA_in_ts2, t);
			}
			for(Transition<S1, A> t : ts1.getTransitions()) {
				addTransitionExcludingHandshakeActionsTS2(ts2, handShakingActions, interleaved,
						a_to_transitionsWithA_in_ts1, t);
			}
			handshake(handShakingActions, interleaved, a_to_transitionsWithA_in_ts1, a_to_transitionsWithA_in_ts2);
		}
		

		return removeUnreachableStates(interleaved);
	}

	private <S, A, P> TransitionSystem<S, A, P> removeUnreachableStates(
			TransitionSystem<S, A, P> ts) {
		TransitionSystem<S, A, P> ans = createTransitionSystem();
		ans.addAllStates(reach(ts));
		ans.addAllActions(ts.getActions());
		ans.addAllAtomicPropositions(ts.getAtomicPropositions());
		for(S s : ts.getInitialStates()) {
			ans.setInitial(s, true);
		}
		
		for(S s : ans.getStates()) {
			for(P ap : ts.getLabel(s)) {
				ans.addToLabel(s, ap);
			}
		}
		for(Transition<S, A> t : ts.getTransitions()) {
			if(ans.getStates().contains(t.getFrom())&&ans.getStates().contains(t.getTo())) {
				ans.addTransition(t);
			}
		}
		
		return ans;
	}

	private <S1, S2, A, P> void handshake(Set<A> handShakingActions, TransitionSystem<Pair<S1, S2>, A, P> interleaved,
			HashMap<A, Set<Transition<S1, A>>> a_to_transitionsWithA_in_ts1,
			HashMap<A, Set<Transition<S2, A>>> a_to_transitionsWithA_in_ts2) {
		for(A actionInHandshake : handShakingActions) {
			for(Transition<S1, A> transition_from_s1 : a_to_transitionsWithA_in_ts1.get(actionInHandshake)) {
				for(Transition<S2, A> transition_from_s2 : a_to_transitionsWithA_in_ts2.get(actionInHandshake)) {
					interleaved.addTransition(
							new Transition<Pair<S1,S2>, A>(
									new Pair<S1, S2>(transition_from_s1.getFrom(), transition_from_s2.getFrom()),
									actionInHandshake,
									new Pair<S1, S2>(transition_from_s1.getTo(), transition_from_s2.getTo())));
				}	
			}
		}
	}

	private <S2, S1, A, P> void addTransitionExcludingHandshakeActionsTS2(TransitionSystem<S2, A, P> ts2,
			Set<A> handShakingActions, TransitionSystem<Pair<S1, S2>, A, P> interleaved,
			HashMap<A, Set<Transition<S1, A>>> a_to_transitionsWithA_in_ts1, Transition<S1, A> t) {
		if(handShakingActions.contains(t.getAction())) {
			a_to_transitionsWithA_in_ts1.get(t.getAction()).add(t);
		}
		else {
			for(S2 state_from_s2 : ts2.getStates()) {
				interleaved.addTransition(
						new Transition<Pair<S1,S2>, A>(
								new Pair<S1, S2>(t.getFrom(),state_from_s2),
								t.getAction(),
								new Pair<S1, S2>(t.getTo(),state_from_s2)));
			}
		}
	}

	private <S1, A, S2, P> void addTransitionExcludingHandshakeActionsTS1(TransitionSystem<S1, A, P> ts1,
			Set<A> handShakingActions, TransitionSystem<Pair<S1, S2>, A, P> interleaved,
			HashMap<A, Set<Transition<S2, A>>> a_to_transitionsWithA_in_ts2, Transition<S2, A> t) {
		if(handShakingActions.contains(t.getAction())) {
			a_to_transitionsWithA_in_ts2.get(t.getAction()).add(t);
		}
		else {
			for(S1 state_from_s1 : ts1.getStates()) {
				interleaved.addTransition(
						new Transition<Pair<S1,S2>, A>(
								new Pair<S1, S2>(state_from_s1,t.getFrom()),
								t.getAction(),
								new Pair<S1, S2>(state_from_s1,t.getTo())));
			}
		}
	}

	@Override
	public <L, A> ProgramGraph<L, A> createProgramGraph() {
		return new PGImple<>();
	}

	@Override
	public <L1, L2, A> ProgramGraph<Pair<L1, L2>, A> interleave(ProgramGraph<L1, A> pg1, ProgramGraph<L2, A> pg2) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement interleave
	}

	@Override
	public TransitionSystem<Pair<Map<String, Boolean>, Map<String, Boolean>>, Map<String, Boolean>, Object> transitionSystemFromCircuit(Circuit c) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement transitionSystemFromCircuit
	}

	@Override
	public <L, A> TransitionSystem<Pair<L, Map<String, Object>>, A, String> transitionSystemFromProgramGraph(ProgramGraph<L, A> pg, Set<ActionDef> actionDefs, Set<ConditionDef> conditionDefs) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement transitionSystemFromProgramGraph
	}

	@Override
	public <L, A> TransitionSystem<Pair<List<L>, Map<String, Object>>, A, String> transitionSystemFromChannelSystem(ChannelSystem<L, A> cs) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement transitionSystemFromChannelSystem
	}

	@Override
	public <Sts, Saut, A, P> TransitionSystem<Pair<Sts, Saut>, A, Saut> product(TransitionSystem<Sts, A, P> ts, Automaton<Saut, P> aut) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement product
	}

	@Override
	public ProgramGraph<String, String> programGraphFromNanoPromela(String filename) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement programGraphFromNanoPromela
	}
	/***until here first Assignment***************/

























	@Override
	public ProgramGraph<String, String> programGraphFromNanoPromelaString(String nanopromela) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement programGraphFromNanoPromelaString
	}

	@Override
	public ProgramGraph<String, String> programGraphFromNanoPromela(InputStream inputStream) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement programGraphFromNanoPromela
	}

	@Override
	public <S, A, P, Saut> VerificationResult<S> verifyAnOmegaRegularProperty(TransitionSystem<S, A, P> ts, Automaton<Saut, P> aut) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement verifyAnOmegaRegularProperty
	}

	@Override
	public <L> Automaton<?, L> LTL2NBA(LTL<L> ltl) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement LTL2NBA
	}

	@Override
	public <L> Automaton<?, L> GNBA2NBA(MultiColorAutomaton<?, L> mulAut) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement GNBA2NBA
	}

}
