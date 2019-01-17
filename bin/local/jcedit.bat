set CLASSPATH=..\..\..\build\common\jar\*
java -Xms200m -Xmx500m  -Dsun.java2d.pmoffscreen=false  -Dorg.xml.sax.parser="com.sun.org.apache.xerces.internal.parsers.SAXParser" \
-Djavax.xml.parsers.DocumentBuilderFactory="com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl" \
-Djavax.xml.parsers.SAXParserFactory="com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl" \
org.jlab.coda.cedit.cooldesktop.CDesktop

#java org.jlab.coda.cedit.cooldesktop.CDesktop
