package jp.gr.area51.sakesake;

import java.util.List;

public class HeartRailsExpress {
	Response response;
	
	public HeartRailsExpress(Response response) {
		this.response = response;
	}
	
	public String toString() {
		return String.format("---- HeartRailsExpress ----\n response: %s\n",
			this.response
		);
	}

}

class Response {
	List<Station> station;
	
	public Response(List<Station> station) {
		this.station = station;
	}
	
	public String toString() {
		return String.format("---- Response ----\n station: %s\n",
			this.station
		);
	}
	
}

class Station {
	double x;
	String next;
	String prev;
	String distance;
	double y;
	String line;
	String postal;
	String name;
	String prefecture;
	
	public Station(
		double x,
		String next,
		String prev,
		String distance,
		double y,
		String line,
		String postal,
		String name,
		String prefecture
	) {
		this.x =             x;
		this.next =          next;
		this.prev =          prev;
		this.distance =      distance;
		this.y =             y;
		this.line =          line;
		this.postal =        postal;
		this.name =          name;
		this.prefecture =    prefecture;
	}

	public String toString() {
		return String.format("---- Station ----\n x: %g\n next: %s\n prev: %s\n distance: %s\n y: %g\n line: %s\n postal: %s\n name %s\n prefecture: %s\n",
			this.x,
			this.next,
			this.prev,
			this.distance,
			this.y,
			this.line,
			this.postal,
			this.name,
			this.prefecture
		);
	}
}




