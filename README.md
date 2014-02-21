HTTPClient
==========

This is a simple implementation of HTTP 1.1 that abides by the RFC 2616 memorandum. Header fields are written and sent manually through a low level socket connection. Redirect codes 301 and 307 are properly handled. A persistent connection was used for obtaining objects, but not for redirects. Object at URL is downloaded into a file at the specified location.

Usage
javac HTTPClient.java
java HTTPClient "URL" "save to file name"

URL must include protocol such as http://
