package il.ac.bgu.cs.fvm.ltl;

public class Not<L> extends LTL<L> {
	private LTL<L> inner;

	public Not(LTL<L> inner) {
		this.setInner(inner);
	}

	public LTL<L> getInner() {
		return inner;
	}

	public void setInner(LTL<L> inner) {
		this.inner = inner;
	}

	@Override
	public String toString() {
		return "!" + inner;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inner == null) ? 0 : inner.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Not))
			return false;
		Not<?> other = (Not<?>) obj;
		if (inner == null) {
			if (other.inner != null)
				return false;
		} else if (!inner.equals(other.inner))
			return false;
		return true;
	}

}
