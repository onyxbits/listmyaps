package de.onyxbits.listmyapps;


/**
 * Access to the formats table.
 * @author patrick
 *
 */
class FormatsData {
	
	protected long id;
	protected String formatName;
	protected String header;
	protected String item;
	protected String footer;
	
	public String toString() {
		return formatName;
	}
}
