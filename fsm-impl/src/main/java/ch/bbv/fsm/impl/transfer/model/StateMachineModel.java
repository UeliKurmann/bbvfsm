package ch.bbv.fsm.impl.transfer.model;

public class StateMachineModel {

	private final String guid;

	private final String name;

	/**
	 * Initialize the object.
	 *
	 * @param guid The GUID of the model element.
	 * @param name The name of the model element.
	 */
	public StateMachineModel(final String guid, final String name) {
		this.guid = guid;
		this.name = name;
	}

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		str.append("StateMachine: ").append(guid).append(" / ").append(name).append('\n');
		return str.toString();
	}

}
