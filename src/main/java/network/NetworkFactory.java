package network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import network.Link.LinkSource;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import util.NetworkFileReader;
import core.DatabaseException;
import core.DatabaseReader;
import core.Monitor;

public class NetworkFactory {

	//TODO: Make this class return a Network, should hold all the initial calls to DB/Filereader in order to create the network returned.
	
	public static Network makeNetwork(int nid) throws NetworkInstantiationException {
		
		ArrayList<Link> links;
		PrecisionModel pm = new PrecisionModel();
		GeometryFactory geometryFactory = new GeometryFactory(pm);
		WKTReader wktReader = new WKTReader(geometryFactory);
		DatabaseReader dbr;
		
		try {
			dbr = new DatabaseReader();
			//ToDo
			links = null;
			
			return new Network(links, dbr);
		} catch (DatabaseException ex) {
			
			Monitor.info("No database connection, trying a filereader instead.");
			
			try {
				//TODO: Hardcoded values.
				NetworkFileReader fileReader = new NetworkFileReader("/Users/Magnus/Desktop/", "testnetwork.csv", ",", true);
				links = NetworkFactory.readLinksFromFile(fileReader, wktReader, geometryFactory);

				return new Network(links, null);
			} catch (FileNotFoundException e) {
				throw new NetworkInstantiationException("No such file.");
			} catch (IOException e) {
				throw new NetworkInstantiationException(
						"Something went wrong in the file stream");
			} catch (ParseException e) {
				throw new NetworkInstantiationException("Something went wrong parsing input");
			} catch (NullPointerException e) {
				throw new NetworkInstantiationException("Could not parse input (null).");
			} catch (NumberFormatException e) {
				throw new NetworkInstantiationException("Could not parse String to number.");
			}
		}
	}

	/**
	 * 
	 * @param fileReader
	 * @param wktReader
	 * @return
	 * @throws ParseException if wkt failed.
	 */
	private static ArrayList<Link> readLinksFromFile(
			NetworkFileReader fileReader, WKTReader wktReader,
			GeometryFactory factory) throws ParseException, NullPointerException, NumberFormatException {
		// TODO: Assumes first row has headings.
		fileReader.getNextLine();

		ArrayList<Link> links = new ArrayList<Link>();

		while (true) {
			String[] linkStrings = fileReader.getNextLine();

			if (linkStrings == null) {
				break;
			}

			LineString geom = (LineString) wktReader
					.read(linkStrings[8].trim());

			Node start = new Node(null, Long.parseLong(linkStrings[2]),
					geom.getCoordinates()[0].x, geom.getCoordinates()[0].y);
			Node end = new Node(null, Long.parseLong(linkStrings[3]),
					geom.getCoordinates()[geom.getCoordinates().length - 1].x,
					geom.getCoordinates()[geom.getCoordinates().length - 1].y);

			String directionStr = linkStrings[4];
			directionStr.trim().toLowerCase();

			Link.Directions direction;

			if (directionStr.equals("forward")
					|| directionStr.equals("forwards")) {
				direction = Link.Directions.FORWARD;
			} else if (directionStr.equals("backward")
					|| directionStr.equals("backwards")) {
				direction = Link.Directions.BACKWARD;
			} else {
				direction = Link.Directions.BOTH;
			}

			links.add(new Link(linkStrings[0], Long.parseLong(linkStrings[1]),
					Link.LinkSource.UNKNOWN, start, end, direction, Double
							.parseDouble(linkStrings[5]), Short
							.parseShort(linkStrings[6]), Boolean
							.parseBoolean(linkStrings[7]), geom, factory));
		}

		return links;
	}
}