package net.teamio.taam.machines;

/**
 * Used to communicate from a machine instance back to the wrapping block.
 * This was necessary to have both TileEntity and Multipart with the same logic (via IMachine).
 */
public interface IMachineWrapper {
	void sendPacket();

	void markAsDirty();
}
