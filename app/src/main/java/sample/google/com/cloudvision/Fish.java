package sample.google.com.cloudvision;

public class Fish implements Comparable<Fish> {
	private String name;
	private long id;
	
	public Fish(long id, String name) {
		this.name = name;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public long getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "(" + name + ")";
	}

	@Override
	public int compareTo(Fish o) {
		if (o == null)
			throw new NullPointerException("[Student] Can not comprate to null");
		
		return name.compareTo(o.name);
	}
}
