package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class NetworkFileReader {

	private String path;
	private String file;
	private BufferedReader bufferReader;
	private String separator;

	/**
	 * 
	 * @param path
	 * @param file
	 * @param separator
	 * @param skipFirstLine
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public NetworkFileReader(String path, String file, String separator,
			boolean skipFirstLine) throws IOException, FileNotFoundException {
		this.path = path;
		this.file = file;
		this.bufferReader = new BufferedReader(new FileReader(this.path
				+ this.file));

		if (skipFirstLine) {
			this.bufferReader.readLine();
		}
	}

	/**
	 * 
	 * @return null if line is empty and on exceptions.
	 */
	public String[] getNextLine() {
		String line;
		try {
			line = this.bufferReader.readLine();
		} catch (IOException e) {
			return null;
		}

		if (line != null) {
			return line.split(this.separator);
		} else {
			return null;
		}
	}
}