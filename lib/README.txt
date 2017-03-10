It is important that xercesImpl-2.6.2.jar stays before j2ee.jar in the .classpath.
The reason for this is that certain XML (JAXP) classes can be loaded either from
j2ee.jar (org.apache.crimson) or xerces. If j2ee.jar is first, the crimson classes are
taken. The problem is that certain validators are present only in xerces and some of the JUnit 
tests fail.