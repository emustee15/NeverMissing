package edu.jcu.emustee15.nevermissing;

public class ObjectLocation
{
	private String description;
	private String name;
	private double latitude;
	private double longitude;
	private String imagePath;
	private long id;
	private int type;
	
	public final static int TYPE_SMALL_OBJECT = 0;
	public final static int TYPE_LARGE_OBJECT = 1;
	
	// This is a container class that contains all the properties about large on small objects. 
	
	// Constructor for large objects
	public ObjectLocation(String name, long id, double latitude, double longitude, String imagePath)
	{
		this.name = name;
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.imagePath = imagePath;
		this.type = TYPE_LARGE_OBJECT;
	}
	
	// Constructor for small objects
	public ObjectLocation(String name, long id, String description, String imagePath)
	{
		this.name = name;
		this.id = id;
		this.description = description;
		this.imagePath = imagePath;
		this.type = TYPE_SMALL_OBJECT;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude()
	{
		return latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude()
	{
		return longitude;
	}

	/**
	 * @return the imagePath
	 */
	public String getImagePath()
	{
		return imagePath;
	}

	/**
	 * @return the id
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * @return the type
	 */
	public int getType()
	{
		return type;
	}
	
	// This method is used for the listAdapter
	@Override
	public String toString()
	{
		return name;
	}
	
	
	// This is used when viewing subobjects, so descriptions appear instead of names.
	public void setNameToDescription()
	{
		name = description;
	}
	
	
}
