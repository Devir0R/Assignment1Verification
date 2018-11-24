package il.ac.bgu.cs.fvm.impl;

import il.ac.bgu.cs.fvm.FvmFacade;
import il.ac.bgu.cs.fvm.automata.Automaton;
import il.ac.bgu.cs.fvm.automata.MultiColorAutomaton;
import il.ac.bgu.cs.fvm.channelsystem.ChannelSystem;
import il.ac.bgu.cs.fvm.circuits.Circuit;
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
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement isExecution
	}

	@Override
	public <S, A, P> boolean isExecutionFragment(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement isExecutionFragment
	}

	@Override
	public <S, A, P> boolean isInitialExecutionFragment(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement isInitialExecutionFragment
	}

	@Override
	public <S, A, P> boolean isMaximalExecutionFragment(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement isMaximalExecutionFragment
	}

	@Override
	public <S, A> boolean isStateTerminal(TransitionSystem<S, A, ?> ts, S s) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement isStateTerminal
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
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement interleave
	}

	@Override
	public <S1, S2, A, P> TransitionSystem<Pair<S1, S2>, A, P> interleave(TransitionSystem<S1, A, P> ts1, TransitionSystem<S2, A, P> ts2, Set<A> handShakingActions) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement interleave
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
