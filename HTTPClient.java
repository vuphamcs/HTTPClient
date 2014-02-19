import java.io.*;
import java.net.Socket;
import java.net.URL;

public class HTTPClient {

	public static void main(String[] args) throws IOException {

		URL url = new URL(args[0]);

		Socket socket = new Socket(url.getHost(), (url.getPort() == -1 ? 80
				: url.getPort()));

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream(), "UTF8"));
		BufferedReader in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		BufferedWriter fw = new BufferedWriter(
				new FileWriter(new File(args[1])));

		// create original request
		String request = "GET " + url.getPath() + " HTTP/1.1\r\nHost: "
				+ url.getHost() + "\r\n\r\n";

		// send request to server
		System.out.print(request);
		out.write(request);

		out.flush();

		String line;
		int length = 0;
		int statusCode = 0;

		// read header out and collect header information
		while (!(line = in.readLine()).equals("")) {

			if (line.startsWith("HTTP/1.1")) {
				statusCode = Integer.parseInt(line.substring(9, 12));
			}

			if (line.startsWith("Content-Length")) {
				length = Integer.parseInt(line.substring(16));
			}

			if (line.startsWith("Location")) {
				url = new URL(line.substring(10));
			}

			System.out.println(line);
		}

		System.out.println("");

		// if a redirect code occurs handle it until a different status code is
		// given (200 preferably)
		while (statusCode == 301 || statusCode == 307) {

			socket.close();

			socket = new Socket(url.getHost(), (url.getPort() == -1 ? 80
					: url.getPort()));

			out.close();
			in.close();

			out = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream(), "UTF8"));
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			request = "GET " + url.getPath() + " HTTP/1.1\r\nHost: "
					+ url.getHost() + "\r\n\r\n";
			// send the new request
			System.out.print(request);
			out.write(request);
			
			out.flush();

			// gather and print header information
			line = null;
			while (!(line = in.readLine()).equals("")) {
				if (line.startsWith("HTTP/1.1")) {
					statusCode = Integer.parseInt(line.substring(9, 12));
				}

				if (line.startsWith("Content-Length")) {
					length = Integer.parseInt(line.substring(16));
				}

				if (line.startsWith("Location")) {
					url = new URL(line.substring(9));
				}

				System.out.println(line);
			}
		}

		// finally once complete, write to file
		for (int x = 0; x < length; x++) {
			fw.append((char) in.read());
		}

		out.close();
		in.close();
		fw.close();
	}
}