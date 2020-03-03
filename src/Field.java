import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Represent a rectangular grid of field positions. Each position is able to
 * store a single animal.
 * 
 * @author David J. Barnes and Michael Kolling. Modified by David Dobervich
 *         2007-2013
 * @version 2006.03.30
 */
public class Field implements Serializable {

	private static final Random rand = new Random();

	// The height and width of the field.
	private int height, width;

	// Storage for the items on the board.
	private Object[][] board;

	private HashMap<Class, ArrayList<Location>> animals;

	private int numberOfRows;
	private int numberOfColumns;

	// define some class constants to represent directions
	/** Represents the direction NORTH */
	static final int N = 0;
	/** Represents the direction NORTHEAST */
	static final int NE = 1;
	/** Represents the direction EAST */
	static final int E = 2;
	/** Represents the direction SOUTHEAST */
	static final int SE = 3;
	/** Represents the direction SOUTH */
	static final int S = 4;
	/** Represents the direction SOUTHWEST */
	static final int SW = 5;
	/** Represents the direction WEST */
	static final int W = 6;
	/** Represents the direction NORTHWEST */
	static final int NW = 7;
	/** Represents the direction "right here" */
	static final int STAY = 8;
	/** The smallest int representing a direction */
	static final int MIN_DIRECTION = 0;
	/** The largest int representing a direction */
	static final int MAX_DIRECTION = 7;

	/**
	 * Represent a field of the given dimensions.
	 * 
//	 * @param depth
	 *            The depth of the field.
	 * @param width
	 *            The width of the field.
	 */
	public Field(int width, int height) {
		this.height = height;
		this.width = width;
		this.numberOfColumns = width;
		this.numberOfRows = height;
		board = new Object[width][height];
		animals = new HashMap<Class, ArrayList<Location>>();
	}

	/**
	 * Empty the field.
	 */
	public void clear() {
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				board[col][row] = null;
			}
		}
	}

	/**
	 * Place an animal at the given location. If there is already an animal at
	 * the location it will be lost.
	 * 
//	 * @param animal
	 *            The animal to be placed.
	 * @param row
	 *            Row coordinate of the location.
	 * @param col
	 *            Column coordinate of the location.
	 */
	public void put(Object obj, int col, int row) {
		put(obj, new Location(col, row));
	}

	public boolean isInGrid(Location loc) {
		return isInGrid(loc.getRow(), loc.getCol());
	}
	
	public boolean isInGrid(int row, int col) {
		return ((row >= 0) && (row < this.width) &&
				(col >= 0) && (col < this.height));
	}
	
	/**
	 * Place an animal at the given location. If there is already an animal at
	 * the location it will be lost.
	 *
//	 * @param animal
	 *            The animal to be placed.
	 * @param location
	 *            Where to place the animal.
	 */
	public void put(Object obj, Location location) {
		board[location.getCol()][location.getRow()] = obj;
	}

	/**
	 * Return the animal at the given location, if any.
	 * 
	 * @param location
	 *            Where in the field.
	 * @return The animal at the given location, or null if there is none.
	 */
	public Object getObjectAt(Location location) {
		return getObjectAt(location.getCol(), location.getRow());
	}

	/**
	 * Return the animal at the given location, if any.
	 * 
	 * @param row
	 *            The desired row.
	 * @param col
	 *            The desired column.
	 * @return The animal at the given location, or null if there is none.
	 */
	public Object getObjectAt(int col, int row) {
		return board[col][row];
	}

	/**
	 * Generate a random location that is adjacent to the given location, or is
	 * the same location. The returned location will be within the valid bounds
	 * of the field.
	 * 
	 * @param location
	 *            The location from which to generate an adjacency.
	 * @return A valid location within the grid area. This may be the same
	 *         object as the location parameter.
	 */
	public Location randomAdjacentLocation(Location location) {
		int row = location.getRow();
		int col = location.getCol();
		// Generate an offset of -1, 0, or +1 for both the current row and col.
		int nextRow = row + rand.nextInt(3) - 1;
		int nextCol = col + rand.nextInt(3) - 1;
		// Check in case the new location is outside the bounds.
		if (nextRow < 0 || nextRow >= height || nextCol < 0 || nextCol >= width) {
			return location;
		} else if (nextRow != row || nextCol != col) {
			return new Location(nextCol, nextRow);
		} else {
			return location;
		}
	}

	/**
	 * Try to find a free location that is adjacent to the given location. If
	 * there is none, then return the current location if it is free. If not,
	 * return null. The returned location will be within the valid bounds of the
	 * field.
	 * 
	 * @param location
	 *            The location from which to generate an adjacency.
	 * @return A valid location within the grid area. This may be the same
	 *         object as the location parameter, or null if all locations around
	 *         are full.
	 */
	public Location freeAdjacentLocation(Location location) {
		List<Location> adjacent = adjacentLocations(location);
		for (Location next : adjacent) {
			if (board[next.getCol()][next.getRow()] == null) {
				return next;
			}
		}
		// check whether current location is free
		if (board[location.getCol()][location.getRow()] == null) {
			return location;
		} else {
			return null;
		}
	}

	public Location freeAdjacentLocation(int x, int y) {
		return freeAdjacentLocation(new Location(x, y));
	}

	/**
	 * Generate an iterator over a shuffled list of locations adjacent to the
	 * given one. The list will not include the location itself. All locations
	 * will lie within the grid.
	 * 
	 * @param location
	 *            The location from which to generate adjacencies.
	 * @return An iterator over locations adjacent to that given.
	 */
	public List<Location> adjacentLocations(Location location) {
		int row = location.getRow();
		int col = location.getCol();
		List<Location> locations = new LinkedList<Location>();
		for (int roffset = -1; roffset <= 1; roffset++) {
			int nextRow = row + roffset;
			if (nextRow >= 0 && nextRow < height) {
				for (int coffset = -1; coffset <= 1; coffset++) {
					int nextCol = col + coffset;
					// Exclude invalid locations and the original location.
					if (nextCol >= 0 && nextCol < width
							&& (roffset != 0 || coffset != 0)) {
						locations.add(new Location(nextCol, nextRow));
					}
				}
			}
		}
		Collections.shuffle(locations, rand);
		return locations;
	}

	public List<Location> adjacentLocations(int x, int y) {
		return adjacentLocations(new Location(x, y));
	}

	/**
	 * Return the depth of the field.
	 * 
	 * @return The depth of the field.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Return the width of the field.
	 * 
	 * @return The width of the field.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Determines how moving in the given direction affects the row number.
	 * 
	 * @param direction
	 *            the direction in which to move
	 * @return the amount by which the row number will change
	 */
	static int rowChange(int direction) {
		int change = 0;
		switch (direction) {
		case N:
		case NE:
		case NW:
			change = -1;
			break;
		case S:
		case SE:
		case SW:
			change = +1;
			break;
		}
		return change;
	}

	/**
	 * Determines how moving in the given direction affects the column number.
	 * 
	 * @param direction
	 *            the direction in which to move
	 * @return the amount by which the column number will change
	 */
	static int columnChange(int direction) {
		int change = 0;
		switch (direction) {
		case W:
		case NW:
		case SW:
			change = -1;
			break;
		case E:
		case NE:
		case SE:
			change = +1;
		}
		return change;
	}

	/**
	 * Determines whether the given row and column numbers represent a legal
	 * location in the field.
	 * 
	 * @param row
	 *            the row number
	 * @param column
	 *            the column number
	 */
	boolean isLegalLocation(int column, int row) {
		return row >= 0 && row < getHeight() && column >= 0
				&& column < getWidth();
	}

	boolean isLegalLocation(Location l) {
		return isLegalLocation(l.getCol(), l.getRow());
	}

	boolean isEmpty(int col, int row) {
		return this.board[col][row] == null;
	}

	boolean isEmpty(Location l) {
		return isEmpty(l.getRow(), l.getCol());
	}

	/**
	 * Determines what can be seen from a given location, looking in a given
	 * direction.
	 * 
	 * @param row
	 *            the row of the object doing the looking
	 * @param column
	 *            the column of the object doing the looking
	 * @param direction
	 *            the direction of the look
	 * @return the object seen, or null if nothing seen.
	 */
	private Object look(int column, int row, int direction) {
		// decode direction into its x-y components
		int rowDelta = rowChange(direction);
		int columnDelta = columnChange(direction);

		// check in that direction until you see something
		// (if nothing else, you will eventually see the edge of the
		// array, thus the loop <i>will</i> terminate)
		while (true) {
			row = row + rowDelta;
			column = column + columnDelta;
			if (!isLegalLocation(row, column))
				return null;
			if (board[column][row] != null)
				return board[column][row];
		}
	}

	public Object getObjectInDirection(Location l, int d) {
		return look(l.getCol(), l.getRow(), d);
	}

	/**
	 * Determines the distance to the nearest thing, or to the edge of the
	 * field, looking in a given direction.
	 * 
	 * @param row
	 *            the row of the object doing the looking
	 * @param column
	 *            the column of the object doing the looking
	 * @param direction
	 *            the direction of the look
	 * @return the distance
	 */
	private int distance(int row, int column, int direction) {
		// decode direction into its x-y components
		int rowDelta = rowChange(direction);
		int columnDelta = columnChange(direction);

		// check in that direction until you see something
		// (if nothing else, you will eventually see the edge of the
		// array, thus the loop <i>will</i> terminate)
		int steps = 0;
		while (true) {
			row = row + rowDelta;
			column = column + columnDelta;
			steps++;
			if (!isLegalLocation(row, column) || board[column][row] != null) {
				return steps;
			}
		}
	}

	public int distanceToObject(Location l, int d) {
		return distance(l.getRow(), l.getCol(), d);
	}

	/**
	 * Given a direction and a number of times to make 1/8 turn clockwise,
	 * return the resultant direction.
	 * 
	 * @param direction
	 *            the initial direction
	 * @param number
	 *            of 45 degree turns clockwise
	 * @return the resultant direction
	 */
	static int calculateNewDirection(int direction, int number) {
		int mod = (direction + number) % (MAX_DIRECTION - MIN_DIRECTION + 1);
		if (mod >= MIN_DIRECTION)
			return mod;
		else
			return 8 + mod;
	}
}